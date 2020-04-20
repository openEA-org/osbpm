package cn.linkey.rulelib.S013;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:选择字号
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-09 16:24
 */
final public class R_S013_B004 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        String type = BeanCtx.g("Type", true);
        String sql = "";
        if (Tools.isBlank(type)) {
            sql = "select WF_OrUnid as id,Subject as text from BPM_ZhiHao";
        }
        else {
            sql = "select WF_OrUnid as id,Subject as text from BPM_ZhiHao where Type='" + type + "'";
        }
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        BeanCtx.print(Documents.dc2json(dc, "")); // 输出json字符串

        return "";
    }
}