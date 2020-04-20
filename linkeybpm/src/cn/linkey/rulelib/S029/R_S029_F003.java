package cn.linkey.rulelib.S029;

import java.util.HashMap;

import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;

/**
 * @RuleName:获得当前日期
 * @author admin
 * @version:
 * @Created: 2014-04-12 21:15
 */
final public class R_S029_F003 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        Document doc = (Document) params.get("Document");
        String fdName = (String) params.get("FieldName");
        doc.s(fdName, DateUtil.getNow());
        return "";
    }
}