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
 * @RuleName:Rest_Engine强制启动用户和节点
 * @author  admin
 * @version: 1.0
 */
final public class R_S017_B109 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
        String processid =BeanCtx.g("processId"); //流程id
        String docUnid = BeanCtx.g("docUnid"); //实例id
        String nextNodeid=BeanCtx.g("nodeId"); //下一节点id
        String nextUserList=BeanCtx.g("userId"); //下一节点用户多个用逗号分隔
        String remark=BeanCtx.g("remark"); //办理意见
        
        if(Tools.isBlank(processid)){return RestUtil.formartResultJson("0", "processId不能为空");}
        if(Tools.isBlank(docUnid)){return RestUtil.formartResultJson("0", "docUnid不能为空");}
        if(Tools.isBlank(nextNodeid)){return RestUtil.formartResultJson("0", "nodeId不能为空");}
        if(Tools.isBlank(nextUserList)){return RestUtil.formartResultJson("0", "userId不能为空");}
        
        ProcessEngine linkeywf = new ProcessEngine();
        BeanCtx.setLinkeywf(linkeywf); //指定全局引擎变量到线程中
        linkeywf.init(processid, docUnid, BeanCtx.getUserid(), "");
        linkeywf.getDocument().appendFromRequest(); //加入其他的键值对参数
        
        //准备运行参数
        HashMap<String, Object> runParams = new HashMap<String, Object>();
        runParams.put("WF_NextNodeid", nextNodeid); //指定要结束的节点
        runParams.put("WF_Remark", remark) ;//办理意见
        if(Tools.isNotBlank(nextUserList) ){
        	HashMap<String,String> nextUserListMap=new HashMap<String,String>();
        	nextUserListMap.put("ALL", nextUserList);
        	runParams.put("WF_NextUserList", nextUserListMap);
        }
        
        //提交工作流引擎运行
        String msg = linkeywf.run("StartUser", runParams);
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