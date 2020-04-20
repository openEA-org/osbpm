package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.wf.InsNode;

/**
 * @RuleName:启动startEvent节点
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-10 20:50
 */
final public class R_S003_B049 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        String processid = BeanCtx.getLinkeywf().getProcessid();
        String runNodeid = (String) params.get("WF_RunNodeid"); //获得要运行的节点id

        //1.首先启动本节点
        InsNode insNode = (InsNode) BeanCtx.getBean("InsNode");
        insNode.startNode(processid, BeanCtx.getLinkeywf().getDocUnid(), runNodeid);

        //运行开始节点的结束规则
        BeanCtx.getLinkeywf().runNode(processid, runNodeid, "EndRuleNum", params);

        return "";
    }
}