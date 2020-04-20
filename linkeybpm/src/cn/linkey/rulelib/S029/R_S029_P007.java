package cn.linkey.rulelib.S029;

import java.util.HashMap;
import java.util.LinkedHashSet;

import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:上一活动参与者部门内
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-17 11:08
 */
final public class R_S029_P007 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String deptid = BeanCtx.getLinkeyUser().getDeptidByUserid(BeanCtx.getUserid(), true); //获得用户在部门id
        LinkedHashSet<String> userlist = BeanCtx.getLinkeyUser().getAllUseridByDeptid(deptid, true); //获得此部门下所有人员
        return Tools.join(userlist, ",");
    }
}