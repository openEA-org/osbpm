package cn.linkey.rulelib.S017;

import java.util.HashMap;

import cn.linkey.factory.BeanCtx;
import cn.linkey.org.UserModel;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:ORG_注册或修改一个用户的信息，用户不存就注册，用户已存在就更新
 * @author admin
 * @version: 8.0
 * @Created: 2015-03-27 23:03
 */
final public class R_S017_B025 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        //示例Json参数:{"Userid":"admin","UserName":"管理员","Password":"pass","JobTitle":"经理","secretary":"","Phone":"136....","SortNum":"1012","Mail":"test@qq.com","Status":"1",}
        String status = "", msg = "";
        UserModel userModel = (UserModel) BeanCtx.getBean("UserModel");
        userModel.setUserid((String) params.get("Userid")); //用户id
        userModel.setCnName((String) params.get("UserName")); //用户中文名
        userModel.setPassword(Tools.md5((String) params.get("Password"))); //密码
        userModel.setJobTitle((String) params.get("JobTitle")); //职务
        userModel.setSecretary((String) params.get("secretary")); //秘书的userid
        userModel.setPhoneNumber((String) params.get("Phone")); //手机号
        userModel.setSortNumber((String) params.get("SortNum"));//排序号
        userModel.setInternetAddress((String) params.get("Mail"));//邮件地址
        userModel.setStatus((String) params.get("Status"));//状态1表示在职
        int i = userModel.save();
        if (i > 0) {
            status = "ok";
            msg = "用户操作成功!";
        }
        else {
            status = "error";
            msg = "用户操作失败";
        }

        return "{\"status\":\"" + status + "\",\"msg\":\"" + msg + "\"}";
    }
}