package cn.linkey.servlet;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.linkey.factory.BeanCtx;
import cn.linkey.util.Tools;

public class DesignerFilter implements Filter {
    @SuppressWarnings("unused")
    private FilterConfig filterConfig;
    private static String cookieName = "BPMUserid";
    private String iscluster = Tools.getProperty("Cluster");

    public void init(FilterConfig config) throws ServletException {
        this.filterConfig = config;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        try {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpSession session = req.getSession();
            HttpServletResponse res = ((HttpServletResponse) response);

            String uri = req.getRequestURI().substring(req.getContextPath().length());
            if (noFilterPath(uri)) {
                chain.doFilter(request, response);
            }
            else {
                String appPath = req.getContextPath();
                String userid = getUserId(req, session);
                if (Tools.isBlank(userid)) {
                    String gourl = req.getRequestURL() + "?" + req.getQueryString();
                    gourl = Tools.encode(gourl);
                    res.sendRedirect(appPath + "/login?gourl=" + gourl);
                    return;
                }

                BeanCtx.init(userid, req, res);

                //检测有没有Designer角色
                if (BeanCtx.getLinkeyUser().isSystemAdmin() || BeanCtx.getLinkeyUser().isDesigner()) {
                    // 把处理发送到下一个过滤器
                    chain.doFilter(request, response);
                }
                else {
                    BeanCtx.showErrorMsg(BeanCtx.getMsg("Designer", "NoDesignerPermission"));
                    BeanCtx.close();
                    return;
                }
            }
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
            BeanCtx.close();
        }
        catch (ServletException se) {
            se.printStackTrace();
            BeanCtx.close();
        }
    }

    /**
     * 是否不需要过滤资源
     * 
     * @return 不需要过滤返回true，否则返回false。
     */

    private boolean noFilterPath(String uri) {
        if (Tools.isBlank(uri)) {
            return true;
        }
        int index = uri.lastIndexOf(".");
        if (index < 0) {
            return false;
        }
        String suffix = uri.substring(uri.lastIndexOf(".")).toLowerCase();
        Set<String> list = new HashSet<String>();
        list.add("js");
        list.add("css");
        list.add("png");
        list.add("jpg");
        return list.contains(suffix);
    }

    private String getUserId(HttpServletRequest req, HttpSession session) {
        String userid = "";
        if (iscluster.equals("1")) {
            //集群时使用cookie进行验证
            Cookie cookie[] = req.getCookies();
            if (cookie != null) {
                for (int i = 0; i < cookie.length; i++) {
                    if (cookie[i].getName().equals(cookieName)) {
                        userid = cookie[i].getValue();
                        userid = DESUserid.decryptUserid(userid);//解密用户名
                        break;
                    }
                }
            }
        }
        else {
            //使用session进行验证
            userid = (String) session.getAttribute("LoginUserid");
        }
        return userid;
    }

    public void destroy() {
        this.filterConfig = null;
    }
}