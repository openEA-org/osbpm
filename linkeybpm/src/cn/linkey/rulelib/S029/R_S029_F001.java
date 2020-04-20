package cn.linkey.rulelib.S029;

import java.util.HashMap;

import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:获得用户的Userid
 * @author admin
 * @version: 8.0
 * @Created: 2014-04-12 21:12
 */
final public class R_S029_F001 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        Document doc = (Document) params.get("Document");
        String fdName = (String) params.get("FieldName");
        doc.s(fdName, BeanCtx.getUserid());
        doc.s(fdName + "_show", BeanCtx.getUserName());
        return "";
    }
}