package cn.linkey.rulelib.S002;

import java.util.HashMap;

import cn.linkey.app.AppUtil;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:获得应用列表
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-12 22:29
 */
final public class R_S002_B020 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String parentid = BeanCtx.g("id");
        String jsonStr = AppUtil.getAppTreeJson(parentid);
        BeanCtx.p(jsonStr);
        return "";
    }
}