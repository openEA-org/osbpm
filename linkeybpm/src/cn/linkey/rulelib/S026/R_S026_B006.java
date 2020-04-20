package cn.linkey.rulelib.S026;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:获取已经设置过的节点id
 * @author admin
 * @version: 8.0
 * @Created: 2014-09-15 22:11
 */
final public class R_S026_B006 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String simProcessUnid = BeanCtx.g("SimProcessUnid");
        Document[] dc = Rdb.getAllDocumentsBySql("select * from BPM_SimNodeList where SimProcessUnid='" + simProcessUnid + "'");
        String nodeid = "";
        for (Document doc : dc) {
            if (Tools.isBlank(nodeid)) {
                nodeid = doc.g("Nodeid") + "#" + doc.g("NodeStatus");
            }
            else {
                nodeid = nodeid + "," + doc.g("Nodeid") + "#" + doc.g("NodeStatus");
            }
        }
        BeanCtx.p("{\"Nodeid\":\"" + nodeid + "\"}");
        return "";
    }
}