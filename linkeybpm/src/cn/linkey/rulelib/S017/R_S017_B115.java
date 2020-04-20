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
 * @RuleName:Rest_Engine强制结束节点
 * @author  admin
 * @version: 8.0
 * @Created: 2017-09-08 08:38:10
 */
final public class R_S017_B115 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
        String docUnid = BeanCtx.g("docUnid"); //实例id
        String nextNodeid=BeanCtx.g("endNodeId"); //要结束的节点id
        String remark=BeanCtx.g("remark"); //办理意见
        
        if(Tools.isBlank(docUnid)){return RestUtil.formartResultJson("0", "docUnid不能为空");}
        if(Tools.isBlank(nextNodeid)){return RestUtil.formartResultJson("0", "endNodeId不能为空");}
        
        //自动获取流程id
        String sql="select WF_ProcessId from bpm_maindata where WF_OrUnid='"+docUnid+"'";
        String processid=Rdb.getValueBySql(sql);
        
        ProcessEngine linkeywf = new ProcessEngine();
        BeanCtx.setLinkeywf(linkeywf); //指定全局引擎变量到线程中
        linkeywf.init(processid, docUnid, BeanCtx.getUserid(), "");
        linkeywf.getDocument().appendFromRequest(); //加入其他的键值对参数
        
        //准备运行参数
        HashMap<String, Object> runParams = new HashMap<String, Object>();
        runParams.put("WF_NextNodeid", nextNodeid);
        runParams.put("WF_Remark", remark) ;//办理意见
        
        //提交工作流引擎运行
        String msg = linkeywf.run("EndNode", runParams);
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