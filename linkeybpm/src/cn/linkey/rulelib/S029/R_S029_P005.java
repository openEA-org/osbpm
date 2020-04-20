package cn.linkey.rulelib.S029;

import java.util.HashMap;

import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:流程启动者
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-17 11:02
 */
final public class R_S029_P005 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String userid = BeanCtx.getLinkeywf().getDocument().g("WF_AddName");
        return userid;
    }
}