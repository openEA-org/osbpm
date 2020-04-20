package cn.linkey.rulelib.S007;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:活动参与者用户限定
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-04 13:38
 */
final public class R_S007_B008 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        StringBuilder jsonStr = new StringBuilder();
        jsonStr.append("[");
        jsonStr.append("{\"userid\":\"CurrentUser\",\"text\":\"当前处理人\"},{\"userid\":\"ProcessStarter\",\"text\":\"流程启动者\"}");
        String sql = "select Nodeid,NodeName from BPM_ModTaskList where Processid='" + BeanCtx.g("Processid") + "'";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : dc) {
            jsonStr.append(",");
            jsonStr.append("{\"userid\":\"" + doc.g("Nodeid") + "\",\"text\":\"" + doc.g("NodeName") + "(参与者)\"}");
        }

        jsonStr.append("]");
        BeanCtx.p(jsonStr.toString());
        return "";
    }
}