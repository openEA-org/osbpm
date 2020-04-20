package cn.linkey.servlet;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.linkey.factory.BeanCtx;
import cn.linkey.util.Tools;

public class DESUserid {
    private static String cookieName = "BPMUserid"; //cookie名称要修改时必须同时修改R_S005_B003中的cookie名称
    private static String isCluster = Tools.getProperty("Cluster");

    public static String getLoginUserid(HttpServletRequest request, HttpServletResponse response) {
        String userid = "";
        if (Tools.isBlank(isCluster) || isCluster.equals("0")) {
            //从session中获得用户名
            userid = (String) request.getSession().getAttribute("LoginUserid");
        }
        else {
            //从cookie中获得用户名
            try {
                Cookie cookie[] = request.getCookies();
                if (cookie != null) {
                    for (int i = 0; i < cookie.length; i++) {
                        if (cookie[i].getName().equals(cookieName)) {
                            userid = cookie[i].getValue();
                            userid = decryptUserid(userid);//解密用户名
                            break;
                        }
                    }
                }
            }
            catch (Exception e) {
                BeanCtx.log(e, "E", "从Cookie中解密用户名时出错!");
                return "";
            }
        }
        return userid;
    }

    public static void setLoginUserid(HttpServletRequest request, HttpServletResponse response, String userid) {
        //设置用户名到session中去
        if (Tools.isBlank(isCluster) || isCluster.equals("0")) {
            request.getSession().setAttribute("LoginUserid", userid);
            request.getSession().setMaxInactiveInterval(-1); //设置超时时间30分钟 30*60
        }
        else {
            //设置到cookie中去
            userid = encryptUserid(userid);//加密用户名
            Cookie theCookie = new Cookie(cookieName, userid);
            theCookie.setDomain(Tools.getProperty("Domain"));
            theCookie.setPath(Tools.getProperty("DomainPath"));
            response.addCookie(theCookie);
        }
    }

    /**
     * 加密用户名
     * 
     * @param userid
     * @return
     */
    public static String encryptUserid(String userid) {
        return userid;
    }

    /**
     * 解密用户名
     * 
     * @param userid
     * @return
     */
    public static String decryptUserid(String userid) {
        return userid;
    }
}
