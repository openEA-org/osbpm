package cn.linkey.rulelib.S029;

import java.util.HashMap;

import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:获得当前日期
 * @author admin
 * @version:
 * @Created: 2014-04-12 21:16
 */
final public class R_S029_F004 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        Document doc = (Document) params.get("Document");
        String fdName = (String) params.get("FieldName");
        java.text.DateFormat insDateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String dateStr = (String) insDateFormat.format(new java.util.Date());
        doc.s(fdName, dateStr);
        return "";
    }
}