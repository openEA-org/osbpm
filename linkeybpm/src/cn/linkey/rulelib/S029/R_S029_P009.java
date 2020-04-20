package cn.linkey.rulelib.S029;

import java.util.HashMap;

import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:上一活动参与者的秘书
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-17 11:14
 */
final public class R_S029_P009 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        Document userdoc = BeanCtx.getLinkeyUser().getUserDoc(BeanCtx.getUserid());
        return userdoc.g("secretary");
    }
}