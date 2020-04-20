package cn.linkey.rulelib.S017;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:ORG_修改用户密码
 * @author admin
 * @version: 8.0
 * @Created: 2014-07-09 09:15
 */
final public class R_S017_B022 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        //示例参数:{"OldPassword":"pass123","NewPassword":"pass"}
        String userid = BeanCtx.getUserid();
        String oldpwd = (String) params.get("OldPassword");
        String newpwd = (String) params.get("NewPassword");
        oldpwd = Tools.md5(oldpwd);
        newpwd = Tools.md5(newpwd);
        String sql = "select WF_OrUnid from BPM_OrgUserList where Userid='" + userid + "' and password='" + oldpwd + "'";
        String status = "false";
        String msg = "";
        if (Rdb.hasRecord(sql)) {
            //旧的用户名和密码正确
            sql = "update BPM_OrgUserList set(password)values(?) where Userid=?";
            Rdb.execSql(sql, newpwd, userid);
            status = "true";
            msg = "密码修改成功!";
        }
        else {
            //旧的用户名或密码错误
            msg = "旧密码错误!";
        }
        return "{\"status\":\"" + status + "\",\"msg\":\"" + msg + "\"}";
    }
}