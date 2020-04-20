package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:(参与者)流程启动者
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-06 11:27
 */
final public class R_S003_B038 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        //从文档中获得流程启动者
        String userid = BeanCtx.getLinkeywf().getDocument().g("WF_AddName");
        return userid;
    }
}