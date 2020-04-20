package cn.linkey.rulelib.S007;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:流程节点列表
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-04 14:51
 */
final public class R_S007_B010 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        StringBuilder jsonStr = new StringBuilder();
        jsonStr.append("{\"rows\":[");
        String sql = "select Nodeid,NodeName from BPM_ModTaskList where Processid='" + BeanCtx.g("Processid", true) + "'";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        int i = 0;
        for (Document doc : dc) {
            if (i == 0) {
                i = 1;
            }
            else {
                jsonStr.append(",");
            }
            jsonStr.append("{\"nodeid\":\"" + doc.g("Nodeid") + "\",\"nodename\":\"" + doc.g("NodeName") + "(参与者)\"}");
        }

        jsonStr.append("]}");
        BeanCtx.p(jsonStr.toString());
        return "";
    }
}