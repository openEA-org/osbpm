package cn.linkey.rulelib.S017;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:ORG_用户名和密码认证
 * @author admin
 * @version: 8.0
 * @Created: 2014-07-09 09:15
 */
final public class R_S017_B017 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        //示例参数:{"Password":"pass123"}
        String userid = BeanCtx.getUserid();
        String pwd = (String) params.get("Password");
        pwd = Tools.md5(pwd);
        String sql = "select WF_OrUnid from BPM_OrgUserList where Userid='" + userid + "' and password='" + pwd + "'";
        String status = "false";
        String msg = "";
        if (Rdb.hasRecord(sql)) {
            //用户名和密码正确
            status = "true";
            msg = "认证通过!";
        }
        else {
            //用户名或密码错误
            msg = "用户名或密码错误!";
        }
        return "{\"status\":\"" + status + "\",\"msg\":\"" + msg + "\"}";
    }
}