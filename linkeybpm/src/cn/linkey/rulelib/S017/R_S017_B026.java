package cn.linkey.rulelib.S017;

import java.util.HashMap;

import cn.linkey.factory.BeanCtx;
import cn.linkey.org.UserModel;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:删除一个用户
 * @author admin
 * @version: 8.0
 * @Created: 2015-03-27 23:03
 */
final public class R_S017_B026 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        //示例Json参数:{"Userid":"admin"}
        String status = "";
        UserModel userModel = (UserModel) BeanCtx.getBean("UserModel");
        userModel.setUserid((String) params.get("Userid")); //用户id
        userModel.delete();
        String msg = "操作成功";
        return "{\"status\":\"ok\",\"msg\":\"" + msg + "\"}";
    }
}