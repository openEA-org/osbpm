package cn.linkey.rulelib.S006;

import java.util.HashMap;

import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:多用户选择返回部门
 * @author admin
 * @version: 8.0
 * @Created: 2016-08-17 22:29:31
 */
final public class R_S006_B022 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {

        String dept_string = "";
        String userid[] = BeanCtx.g("userid").split(",");
        String deptId = "", deptName = "";
        for (int i = 0; i < userid.length; i++) {
            String tmpId = BeanCtx.getLinkeyUser().getDeptidByUserid(userid[i], false);
            String tmpName = BeanCtx.getLinkeyUser().getFullDeptNameByDeptid(tmpId, false);
            if (i == 0) {
                deptId = tmpId;
                deptName = tmpName;
            }
            else {
                deptId += "," + tmpId;
                deptName += "," + tmpName;
            }
        }
        dept_string += "{\"deptId\":\"" + deptId + "\",\"deptName\":\"" + deptName + "\"}";
        BeanCtx.p(dept_string);
        return "";
    }

}