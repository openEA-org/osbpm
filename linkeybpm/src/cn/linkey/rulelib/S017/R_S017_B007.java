package cn.linkey.rulelib.S017;

import java.util.HashMap;

import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.wf.ModNode;

/**
 * @RuleName:获得流程节点属性配置服务
 * @author admin
 * @version: 8.0
 * @Created: 2014-07-01 22:40
 */
final public class R_S017_B007 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        //示例参数: {"Processid":"f217a2fe03e52048d10ba0b0950eec57",Nodeid:"T10021"}
        ModNode modNode = (ModNode) BeanCtx.getBean("ModNode");
        String processid = (String) params.get("Processid");
        String nodeid = (String) params.get("Nodeid");
        Document nodeDoc = modNode.getNodeDoc(processid, nodeid);
        if (nodeDoc.isNull()) {
            return "节点不存在!";
        }
        else {
            return nodeDoc.toJson();
        }
    }
}