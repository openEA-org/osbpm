package cn.linkey.rulelib.S023;

import java.util.HashMap;

import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:根据userid获得username
 * @author admin
 * @version: 8.0
 * @Created: 2014-08-01 15:24
 */
final public class R_S023_B013 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String userid = BeanCtx.g("userid");
        String[] userArray = Tools.split(userid);
        String cnName = "";
        for (String user : userArray) {
            String username = BeanCtx.getLinkeyUser().getCnName(user);
            if (Tools.isBlank(username)) {
                username = user;
            }
            if (cnName.equals("")) {
                cnName = username;
            }
            else {
                cnName += "," + username;
            }
        }
        BeanCtx.p("{\"UserName\":\"" + cnName + "\"}");

        return "";
    }
}