package cn.linkey.rulelib.S029;

import java.util.HashMap;

import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.wf.ModNode;

/**
 * @RuleName:获得流程管理员
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-17 11:04
 */
final public class R_S029_P006 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String processid = BeanCtx.getLinkeywf().getProcessid();
        ModNode modNode = (ModNode) BeanCtx.getBean("ModNode");
        Document pdoc = modNode.getNodeDoc(processid, "Process");
        String processOwner = pdoc.g("ProcessOwner");
        return processOwner;
    }
}