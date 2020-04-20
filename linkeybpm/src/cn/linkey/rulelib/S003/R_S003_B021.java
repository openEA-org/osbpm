package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.wf.InsNode;

/**
 * 本规则负责启动网关
 * 
 * @author Administrator
 *
 */
public class R_S003_B021 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        String processid = BeanCtx.getLinkeywf().getProcessid();
        String runNodeid = (String) params.get("WF_RunNodeid"); //获得要运行的节点id

        //1.首先启动本节点
        InsNode insNode = (InsNode) BeanCtx.getBean("InsNode");
        insNode.startNode(processid, BeanCtx.getLinkeywf().getDocUnid(), runNodeid);

        //2.抛出本节点状态变化事件，如果有事件返回0则表示中止并把数据驻留在本节点上，否则直接结束本网关并推向下一下节点
        String eventResult = BeanCtx.getEventEngine().run(processid, runNodeid, "BeforeGoToNextNode", params);
        if (eventResult.equals("0")) {
            //说明事件阻止网关节点的推进，中间驻留数据到网关数据库中
            BeanCtx.getLinkeywf().saveStayData(processid, runNodeid, BeanCtx.getLinkeywf().getDocUnid(), params);
        }
        else {
            //事件运行通过,结束本环节,只需要运行后置节点所对应的结束规则即可
            BeanCtx.getLinkeywf().runNode(processid, runNodeid, "EndRuleNum", params);
        }
        return "";
    }
}
