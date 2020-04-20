package cn.linkey.rulelib.S006;

import java.util.HashMap;

import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:根据对应用户获得所属部门
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-21 10:35
 */
final public class R_S006_B015 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String userid = BeanCtx.g("userid");
        String deptid = BeanCtx.getLinkeyUser().getDeptidByUserid(userid, false);
        String deptName = BeanCtx.getLinkeyUser().getFullDeptNameByDeptid(deptid, false);
        BeanCtx.p("{\"Deptid\":\"" + deptid + "\",\"DeptName\":\"" + deptName + "\"}");
        return "";
    }
}