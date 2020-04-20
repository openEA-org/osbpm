package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.wf.ProcessEngine;

/**
 * 尝试结束子流程节点
 * 
 * 参数说明:调用本动作无需传入参数，系统自动分析获取
 * 
 * @author Administrator WF_Action=EndSubProcessNode
 */
public class R_S003_B064 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {

        //long ts = System.currentTimeMillis();
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        String processid = linkeywf.getProcessid();
        String runNodeid = (String) params.get("MainNodeid");
        String subNextUserList = (String) params.get("WF_SubNextUserList");

        //1.直接运行流程子节点的结束规则
        if (Tools.isBlank(subNextUserList)) {
            params.put("WF_UsePostOwner", "0"); //使用环节中指定的参与者作为节点审批人
        }

        if (linkeywf.isDebug()) {
            BeanCtx.out("准备结束子流程节点=" + runNodeid + ",指定子流程节点的审批用户为=" + subNextUserList + ",设定使用环节中的参数者=" + params.get("WF_UsePostOwner"));
        }

        linkeywf.runNode(processid, runNodeid, "EndRuleNum", params); //运行SubProces的结束规则R_S003_B059

        linkeywf.setRunStatus(true);//表示运行成功
        return linkeywf.getRunMsg(); //返回提示信息
    }

}
