package cn.linkey.rulelib.S017;

import java.util.*;

import cn.linkey.dao.*;
import cn.linkey.util.*;
import cn.linkey.doc.*;
import cn.linkey.factory.*;
import cn.linkey.wf.*;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.org.LinkeyUser;
/**
 * @RuleName::Engine_强制结束一个节点并自动推进到下一个节点
 * @author  admin
 * @version: 8.0
 * @Created: 2016-10-12 10:41:34
 * <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://server.ws.linkey.cn/">
   <soapenv:Header/>
   <soapenv:Body>
      <ser:runRule>
         <!--Optional:-->
         <rulenum>R_S017_B044</rulenum>
         <!--Optional:-->
         <params>{"Processid":"5c6dc7140233f04b6f09a5a0547a35a316a8", "DocUnid":"48fa402e078d504daf0be1303837222469a6","CurrentNodeid":"S10018","Remark":"很好","NextNodeid":"T10005","NextUserList":"admin","Subject":"修改一下标准005"}</params>
         <!--Optional:-->
         <userid>admin</userid>
         <!--Optional:-->
         <sysid>bpm</sysid>
         <!--Optional:-->
         <syspwd>pass</syspwd>
      </ser:runRule>
   </soapenv:Body>
</soapenv:Envelope>
 */
final public class R_S017_B044 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
		//示例参数:{"Processid":"5c6dc7140233f04b6f09a5a0547a35a316a8", "DocUnid":"0bed75550c97504d100a0e709d60d8488221","CurrentNodeid":"S10018","NextNodeid":"T10005","NextUserList":"","Remark":"同意","Subject":"修改字段Subject的值"}
		//如果NextNodeid,NextUserList不指定则本节点的后继节点的参与者要能直接计算出来(不能使用提交上来的用户)
		//如果CurrentNodeid不指定则自动计算当前用户的审批节点为当前要结束的节点
		
		String argList=",Processid,DocUnid,NextNodeid,NextUserList,CurrentNodeid,Remark,userid,sysid,";
        String processid = (String) params.get("Processid"); //流程id
        String docUnid = (String) params.get("DocUnid"); //实例id
        String nextNodeid=(String)params.get("NextNodeid"); //下一节点id
        String nextUserList=(String)params.get("NextUserList"); //下一节点用户多个用逗号分隔
        String currentNodeid=(String)params.get("CurrentNodeid"); //当前节点,如果不指定则系统自动根据当前用户计算当前用户所在的审批节点
        String remark=(String) params.get("Remark"); //办理意见
        
        ProcessEngine linkeywf = new ProcessEngine();
        BeanCtx.setLinkeywf(linkeywf); //指定全局引擎变量到线程中
        linkeywf.init(processid, docUnid, BeanCtx.getUserid(), "");
        //设置其他字段的值
        for(String keyName:params.keySet()){
        	if(argList.indexOf(keyName+",")==-1){
        		BeanCtx.out("keyName="+keyName+" value="+ (String)params.get(keyName));
        		linkeywf.getDocument().s(keyName, (String)params.get(keyName));
        	}
        }
        
        //准备运行参数
        HashMap<String, Object> runParams = new HashMap<String, Object>();
        runParams.put("WF_NextNodeid", nextNodeid); //指定要结束的节点
        runParams.put("WF_CurrentNodeid", currentNodeid); //指定当前节点
        runParams.put("WF_Remark", remark) ;//办理意见
        if(Tools.isNotBlank(nextUserList)){
        	HashMap<String,String> nextUserListMap=new HashMap<String,String>();
        	nextUserListMap.put(nextNodeid, nextUserList);
        	runParams.put("WF_NextUserList", nextUserListMap);
        }
        
//        BeanCtx.out("提交运行参数runParams="+runParams);
        
        //6.提交工作流引擎运行
        String msg = linkeywf.run("GoToNextNode", runParams);
        if (linkeywf.isDebug()) {
            BeanCtx.out("Debug:" + msg);
        }

        //7.如果出错则数据需要回滚
        if (BeanCtx.isRollBack()) {
            //获得回滚后的提示信息
            if (Tools.isBlank(linkeywf.getRollbackMsg())) {
                msg = BeanCtx.getMsg("Engine", "Error_EngineRun");
            }
            else {
                msg = linkeywf.getRollbackMsg();
            }
            params.put("ErrorType", "RollBack");
            BeanCtx.getEventEngine().run(processid, "Process", "EngineRunError", params); //注册流程运行出错后的事件
        }
        
        return msg;
	}
}