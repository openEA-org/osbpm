package cn.linkey.rulelib.S029;

import java.util.HashMap;

import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.wf.InsNode;
import cn.linkey.wf.ProcessEngine;

/**
 * @RuleName:环节超时自动跳转下一环节
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-09 09:46
 */
final public class R_S029_P021 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        ProcessEngine linkeyWf = BeanCtx.getLinkeywf();
        linkeyWf.goToNextNode(linkeyWf.getCurrentNodeid(), params);

        String processid = linkeyWf.getProcessid();
        String runNodeid = linkeyWf.getCurrentNodeid(); //获得要运行的节点id

        //2.结束当前网关
        InsNode insNode = (InsNode) BeanCtx.getBean("InsNode");
        insNode.endNode(processid, linkeyWf.getDocUnid(), runNodeid);

        //3.本环节结束，继续推进到本网关的后继节点
        linkeyWf.goToNextNode(runNodeid, params);

        return "";
    }
}