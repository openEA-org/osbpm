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
 * @RuleName:Rest_Engine提交下一会串行用户
 * @author  admin
 * @version: 1.0
 */
final public class R_S017_B128 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
        String docUnid = BeanCtx.g("docUnid",true); //实例id
        String remark=BeanCtx.g("remark",true);
        String formData=BeanCtx.g("formData"); //要修改的表单数据
        
        if(Tools.isBlank(docUnid)){return RestUtil.formartResultJson("0", "docUnid不能为空");}
        
        String sql="select WF_ProcessId from bpm_maindata where WF_OrUnid='"+docUnid+"'";
        String processid=Rdb.getValueBySql(sql);
        
        ProcessEngine linkeywf = new ProcessEngine();
        BeanCtx.setLinkeywf(linkeywf); //指定全局引擎变量到线程中
        linkeywf.init(processid, docUnid, BeanCtx.getUserid(), "");
        if(Tools.isNotBlank(formData)){
        	linkeywf.getDocument().appendFromJsonStr(formData);
        }
        
        //准备运行参数
        HashMap<String, Object> runParams = new HashMap<String, Object>();
        runParams.put("WF_Remark", remark) ;//办理意见
        
        //提交工作流引擎运行
        String msg = linkeywf.run("GoToNextSerialUser", runParams);
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