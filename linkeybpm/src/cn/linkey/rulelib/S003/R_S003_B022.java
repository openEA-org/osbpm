package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.wf.InsNode;

/**
 * 结束网关的运行
 * 
 * @author Administrator
 *
 */
public class R_S003_B022 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        String processid = BeanCtx.getLinkeywf().getProcessid();
        String runNodeid = (String) params.get("WF_RunNodeid"); //获得要运行的节点id

        //2.结束当前网关
        InsNode insNode = (InsNode) BeanCtx.getBean("InsNode");
        insNode.endNode(processid, BeanCtx.getLinkeywf().getDocUnid(), runNodeid);

        //3.本环节结束，继续推进到本网关的后继节点
        BeanCtx.getLinkeywf().goToNextNode(runNodeid, params);

        return "";

    }
}
