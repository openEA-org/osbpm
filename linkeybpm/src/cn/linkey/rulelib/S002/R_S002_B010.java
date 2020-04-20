package cn.linkey.rulelib.S002;

import java.util.HashMap;

import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:获得节点意见类型
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-04 10:31
 */
final public class R_S002_B010 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        BeanCtx.p(BeanCtx.getSystemConfig("NodeRemarkType"));
        return "";
    }
}