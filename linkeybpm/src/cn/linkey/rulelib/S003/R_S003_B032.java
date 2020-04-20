package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * 控制转他人处理的按扭显示规则
 * 
 * @author Administrator
 *
 */
public class R_S003_B032 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        if (BeanCtx.getLinkeywf().getIsNewProcess()) {
            return "0"; //如果是新流程则隐藏转他人处理按扭
        }
        if (BeanCtx.getLinkeywf().canSelectNodeAndUser() > 0) {
            return "0"; //说明在会签或串行或再次转交时隐藏此按扭
        }
        return "1";
    }

}
