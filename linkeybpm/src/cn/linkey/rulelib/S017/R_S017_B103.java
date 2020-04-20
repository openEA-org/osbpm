package cn.linkey.rulelib.S017;

import java.util.*;

import cn.linkey.dao.*;
import cn.linkey.util.*;
import cn.linkey.doc.*;
import cn.linkey.factory.*;
import cn.linkey.wf.*;
import cn.linkey.rest.RestUtil;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.org.LinkeyUser;
/**
 * @RuleName:Rest_Engine结束当前环节流转到下一节点
 * @author  admin
 * @version: 8.0
 * @Created: 2017-09-07 16:26:19
 */
final public class R_S017_B103 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
        String docUnid = BeanCtx.g("docUnid"); //实例id
        String nextNodeid=BeanCtx.g("nextNodeId"); //下一节点id
        String nextUserList=BeanCtx.g("nextUserList"); //下一节点用户多个用逗号分隔
        String currentNodeid=BeanCtx.g("currentNodeId"); //当前节点,如果不指定则系统自动根据当前用户计算当前用户所在的审批节点
        String remark=BeanCtx.g("remark"); //办理意见
        String copyUser=BeanCtx.g("copyUserId");//要传阅的用户
        String formData=BeanCtx.g("formData"); //要修改的表单数据
        
        if(Tools.isBlank(docUnid)){return RestUtil.formartResultJson("0", "docUnid不能为空");}
        if(Tools.isBlank(nextNodeid)){return RestUtil.formartResultJson("0", "nextNodeId不能为空");}
        
        String sql="select WF_ProcessId from bpm_maindata where WF_OrUnid='"+docUnid+"'";
        String processid=Rdb.getValueBySql(sql);
        
        ProcessEngine linkeywf = new ProcessEngine();
        BeanCtx.setLinkeywf(linkeywf); //指定全局引擎变量到线程中
        linkeywf.init(processid, docUnid, BeanCtx.getUserid(), "");
        if(Tools.isNotBlank(formData)){
        	linkeywf.getDocument().appendFromJsonStr(formData);
        }
        if(Tools.isBlank(linkeywf.getCurrentNodeid())){
        	 return RestUtil.formartResultJson("1", "当前文档不在用户"+BeanCtx.getUserid()+"的待办中，没有处理权限!");
        }
        
        //准备运行参数
        HashMap<String, Object> runParams = new HashMap<String, Object>();
        runParams.put("WF_NextNodeid", nextNodeid); //指定要结束的节点
        runParams.put("WF_CurrentNodeid", currentNodeid); //指定当前节点
        runParams.put("WF_Remark", remark) ;//办理意见
        if(Tools.isNotBlank(nextUserList) || Tools.isNotBlank(copyUser)){
        	HashMap<String,String> nextUserListMap=new HashMap<String,String>();
        	if(Tools.isNotBlank(nextUserList)){
        		nextUserListMap.put(nextNodeid, nextUserList); //指定要提交的用户
        	}
            if (Tools.isNotBlank(copyUser)) {
            	nextUserListMap.put("COPYUSER", copyUser); //要抄送的用户
            }
        	runParams.put("WF_NextUserList", nextUserListMap);
        }
        
        BeanCtx.out("提交运行参数runParams="+runParams);
        
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
        
        return RestUtil.formartResultJson("1", msg);
	}
}