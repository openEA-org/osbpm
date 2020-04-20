package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.wf.InsNode;

/**
 * 本规则负责结束buinessRuleTask节点
 * 
 * @author Administrator
 *
 */
public class R_S003_B046 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        String processid = BeanCtx.getLinkeywf().getProcessid();
        String runNodeid = (String) params.get("WF_RunNodeid"); //获得要运行的节点id

        //2.结束当前节点
        InsNode insNode = (InsNode) BeanCtx.getBean("InsNode");
        insNode.endNode(processid, BeanCtx.getLinkeywf().getDocUnid(), runNodeid);

        //3.本环节结束，继续推进到本节点的后继节点,自动活动不能放在两个任务节点之间，即自动节点不能像事件等节点一样
        //自动活动后面可以没有节点

        params.remove("WF_AllRouterPath"); //需要删除路径集合，因为buinessRuleTask节点后面的路径不在集合中，不能按已知集合进行推进
        BeanCtx.getLinkeywf().goToNextNode(runNodeid, params);

        return "";

    }
}
