package cn.linkey.rulelib.S029;

import java.util.HashMap;

import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.org.LinkeyUser;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:获得用户部门全称
 * @author admin
 * @version:
 * @Created: 2014-04-12 21:22
 */
final public class R_S029_F007 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        LinkeyUser linkeyUser = (LinkeyUser) BeanCtx.getBean("LinkeyUser");
        Document doc = (Document) params.get("Document");
        String fdName = (String) params.get("FieldName");
        doc.s(fdName, linkeyUser.getFullDeptNameByUserid(BeanCtx.getUserid(), false));
        return "";
    }
}