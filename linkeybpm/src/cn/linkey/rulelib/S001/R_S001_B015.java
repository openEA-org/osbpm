package cn.linkey.rulelib.S001;

import java.util.HashMap;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * 获得字段规则列表
 * 
 * @author Administrator
 */
public class R_S001_B015 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        String sql = "select RuleNum,RuleName from BPM_RuleList where RuleType='4'";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        // BeanCtx.out(Tools.dc2json(dc,""));
        String jsonStr = Documents.dc2json(dc, "", true);
        BeanCtx.print(jsonStr); // 输出json字符串

        return "";
    }
}
