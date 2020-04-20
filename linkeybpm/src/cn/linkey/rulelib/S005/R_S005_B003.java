package cn.linkey.rulelib.S005;

import java.util.*;

import javax.servlet.http.Cookie;

import cn.linkey.dao.RdbCache;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:退出系统
 * @author admin
 * @version: 8.0
 * @Created: 2014-04-25 15:38
 */
final public class R_S005_B003 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        String isCluster = Tools.getProperty("Cluster");
        String cookieName = "BPMUserid";

        if (isCluster.equals("1")) {
            //清除cookie
            Cookie cook = new Cookie(cookieName, null);
            cook.setDomain(Tools.getProperty("Domain"));
            cook.setMaxAge(0);
            cook.setPath(Tools.getProperty("DomainPath"));
            BeanCtx.getResponse().addCookie(cook);
        }
        else {
            //1.清除用户的session
            BeanCtx.getRequest().getSession().removeAttribute("LoginUserid");
            //BeanCtx.out("userid="+BeanCtx.getRequest().getAttribute("LoginUserid"));
        }

        //2.清除用户的缓存信息
        RdbCache.remove("UserCacheStrategy", BeanCtx.getUserid());

        try {
            BeanCtx.getResponse().sendRedirect("login");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}