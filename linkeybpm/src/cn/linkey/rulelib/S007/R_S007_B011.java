package cn.linkey.rulelib.S007;

import java.util.HashMap;

import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:获得用户详细信息
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-21 13:11
 */
final public class R_S007_B011 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String userid = BeanCtx.g("userid", true);
        Document userdoc = BeanCtx.getLinkeyUser().getUserDoc(userid);
        userdoc.s("FullDeptName", BeanCtx.getLinkeyUser().getFullDeptNameByUserid(userid, false));
        userdoc.s("Deptid", BeanCtx.getLinkeyUser().getDeptidByUserid(userid, true));
        userdoc.s("DirDeptid", BeanCtx.getLinkeyUser().getDeptidByUserid(userid, false));
        userdoc.s("DeptName", BeanCtx.getLinkeyUser().getDeptNameByDeptid(userdoc.g("Deptid")));
        userdoc.s("DirDeptName", BeanCtx.getLinkeyUser().getDeptNameByDeptid(userdoc.g("DirDeptid")));

        BeanCtx.p(userdoc.toString());

        return "";
    }
}