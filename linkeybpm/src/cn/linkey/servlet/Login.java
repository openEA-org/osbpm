package cn.linkey.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.linkey.dao.Rdb;
import cn.linkey.dao.RdbCache;
import cn.linkey.doc.Document;
import cn.linkey.domino.LtpaLibrary;
import cn.linkey.factory.BeanCtx;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;

/**
 * 本程序负责登录
 * 
 * @author Administrator
 * 
 */
@SuppressWarnings("serial")
public class Login extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        BeanCtx.init("", request, response);
        response.setContentType("text/html;charset=utf-8");
        String userid = DESUserid.getLoginUserid(request, response);//从session中获得用户名

        if (Tools.isNotBlank(userid)) {
            //用户已经登录了，直接转向首页
            response.sendRedirect(getGoToUrl(request));
        }
        else {
            //用户还没有登录
            userid = request.getRemoteUser();
            //userid = (String)request.getSession().getAttribute(CASFilter.CAS_FILTER_USER);
            if (Tools.isNotBlank(userid)) {
                //说明CAS中已经登录
                loginForCASServer(request, response, userid);
            }
            else {
                //说明没有集成CAS服务器登录
                String loginHtml = getLoginHtml(request);
                String errormsg = (String) request.getAttribute("errormsg");
                if (errormsg == null) {
                    errormsg = "";
                }
                loginHtml = Tools.replace(loginHtml, "{gourl}", getGoToUrl(request));
                loginHtml = Tools.replace(loginHtml, "{errormsg}", errormsg);
                PrintWriter out = response.getWriter();
                out.println(loginHtml);
            }
        }
        BeanCtx.close();
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        BeanCtx.init("Anonymous", request, response);
        response.setContentType("text/html;charset=utf-8");
        // PrintWriter out=response.getWriter();
        String userid = request.getParameter("UserName");
        String password = request.getParameter("Password");
		
        //20180504 验证码判断 	
        String verifyCode=request.getParameter("verify_code");
    	//检查是否是正确的验证码
        HttpSession session = request.getSession(false); 
    	String k = (String)session.getAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY);
    	//20190430 过滤移动端校验
    	String userAgent = request.getHeader("user-agent");
        if(userAgent.indexOf("Mobile") == -1 && (k==null||!k.equals(verifyCode))) {
        	  // 用户名或密码不对
            request.setAttribute("errormsg", "验证码错误!");
            doGet(request, response);
        }
        else if (checkuserpwd(userid, password)) {
            // 登录成功
            DESUserid.setLoginUserid(request, response, userid);//把用户名写入到session中去

            //记录登录信息
            saveUserLoginLog(request, userid);

            //实现与Domino版本的单点登录
            loginForDomino(response, userid);

            //如果是移动端访问则转向到移动端应用的首页
            response.sendRedirect(getGoToUrl(request));

        }
        else {
            // 用户名或密码不对
            request.setAttribute("errormsg", "用户名或密码错误!");
            doGet(request, response);
        }
        BeanCtx.close();
    }

    /**
     * 通过CAS服务器实现单点登录
     */
    public void loginForCASServer(HttpServletRequest request, HttpServletResponse response, String userid) throws IOException {
        // 登录成功
        request.getSession().setAttribute("LoginUserid", userid);

        //记录登录信息
        saveUserLoginLog(request, userid);

        //实现与Domino版本的单点登录
        loginForDomino(response, userid);

        //如果是移动端访问则转向到移动端应用的首页
        response.sendRedirect(getGoToUrl(request));
    }

    /**
     * 获得登录的html页面代码
     * 
     * @return
     */
    public String getLoginHtml(HttpServletRequest request) {
        String codeType = "LoginHTML";
        
        //20181023 添加移动端登录页面跳转
        String userAgent = request.getHeader("user-agent");
        if (userAgent.indexOf("Mobile") != -1) {
        	codeType = "LoginHTMLForMobile"; //移动端
        }
        //==================================================
        
        String htmlCode = (String) RdbCache.get(codeType);
        if (htmlCode == null) {
            String sql = "select DefaultCode from BPM_DevDefaultCode where CodeType='" + codeType + "'";
            htmlCode = Rdb.getValueBySql(sql);
            RdbCache.put(codeType, htmlCode);
        }
        return htmlCode;
    }

    /**
     * 记录用户的登录日记信息
     * 
     * @param request
     * @param userid 用户id
     */
    public void saveUserLoginLog(HttpServletRequest request, String userid) {
        //记录用户的登录息
        String sql = "select WF_OrUnid,Userid,LastLoginIP,LastLoginTime from BPM_UserProfile where Userid='" + userid + "'";
        Document doc = Rdb.getDocumentBySql(sql);
        doc.s("Userid", userid);
        doc.s("LastLoginIP", request.getRemoteAddr()); //最后登录ip
        doc.s("LastLoginTime", DateUtil.getNow()); //最后登录时间
        doc.save();
    }

    /**
     * 实现与DominoServer的单点登录服务
     * 
     * @param response
     * @param userid
     */
    public void loginForDomino(HttpServletResponse response, String userid) {
        if (Tools.isNotBlank(BeanCtx.getSystemConfig("DominoSecret"))) {
            String dominoCookie = LtpaLibrary.getcookie(userid, 30000);
            Cookie cookie = new Cookie("LtpaToken", dominoCookie);
            cookie.setPath("/");
            response.addCookie(cookie);
        }
    }

    /**
     * 获得登录成功后的转向页面地址
     * 
     * @param request
     * @return
     */
    public String getGoToUrl(HttpServletRequest request) {
        //如果是移动端访问则转向到移动端应用的首页
        String gourl = request.getParameter("gourl");
        String userAgent = request.getHeader("user-agent");
        if (userAgent.indexOf("Mobile") != -1 && (Tools.isBlank(gourl) || ( Tools.isNotBlank(gourl) && gourl.contains("S005")))) { //20190314 添加 Tools.isBlank(gourl) 判断
            gourl = "r?wf_num=S023"; //移动端首页
        }
        else {
            if (Tools.isBlank(gourl)) {
                gourl = "r?wf_num=S005";
            } //pc端首页
        }
        return gourl;
    }

    /**
     * 检测用户名密码是否正确
     * 
     * @param userid 用户id
     * @param pwd 用户密码
     * @return
     */
    public static boolean checkuserpwd(String userid, String pwd) {
        if (pwd.length() < 32) {
            pwd = Tools.md5(pwd);
        }
        String sql = "select WF_OrUnid from BPM_OrgUserList where Status='1' and Userid='" + Rdb.formatArg(userid) + "' and Password='" + Rdb.formatArg(pwd) + "'";
        return Rdb.hasRecord(sql);
    }
}
