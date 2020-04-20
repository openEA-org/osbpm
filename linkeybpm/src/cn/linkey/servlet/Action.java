package cn.linkey.servlet;

import java.io.IOException;
import java.util.HashSet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.linkey.app.AppElement;
import cn.linkey.app.AppUtil;
import cn.linkey.dao.Rdb;
import cn.linkey.dao.RdbCache;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.util.Tools;

@SuppressWarnings("serial")
public class Action extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        run(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        run(request, response);
    }

    /**
     * 通用运行设计元素
     * 
     * @param request
     * @param response
     */
    public void run(HttpServletRequest request, HttpServletResponse response) {
        String wf_num = "", appid = "";
        try {

            //long ts = System.currentTimeMillis();
            BeanCtx.init("", request, response); //首先初始化
            request.setCharacterEncoding("utf-8"); // 设置编码为utf-8
            response.setContentType("text/html;charset=utf-8");

            //看是否移动终端访问
            String userAgent = request.getHeader("user-agent");
            if (userAgent.indexOf("Mobile") != -1 || BeanCtx.g("mobile").equals("1")) {
                BeanCtx.setMobile(true);
                if(userAgent.indexOf("Mac") != -1 && userAgent.indexOf("Version") == -1 && userAgent.indexOf("Language") == -1) {
                    BeanCtx.setMobileType("2");
                }
                else if(userAgent.indexOf("Android") == -1 && userAgent.indexOf("wv") != -1 && userAgent.indexOf("Language") == -1) {
                    BeanCtx.setMobileType("1");
                }
            }

            //1.检测wf_num是否符合要求
            wf_num = request.getParameter("wf_num");
            if (!Tools.isString(wf_num)) { //防止sql注入
                wf_num = wf_num.replace("<", "&lt;").replace(">", "&gt;");
                BeanCtx.log("W", "wf_num传入了空值或者非法字符串(" + wf_num + ")");
                response.getWriter().println("Error:Wf_num " + wf_num + " can't be empty or incoming illegal string!");
                return;
            }

            //2.检测wf_docunid是否符合要求
            String wf_docunid = request.getParameter("wf_docunid");
            if (!Tools.isString(wf_docunid)) {//防止sql注入
                wf_num = wf_num.replace("<", "&lt;").replace(">", "&gt;");
                BeanCtx.log("W", "wf_docunid传入了非法字符串(" + wf_num + ")");
                response.getWriter().println("Error:wf_docunid " + wf_num + " can't be empty or incoming illegal string!");
                return;
            }

            //3.看是否是打开应用首页如果编号中不含_则表示要打开应用首页
            if (wf_num.indexOf("_") == -1) {
                //说明只传入了应用编号,需要查找应用的首页page的编号作为wf_num
                String sql = "select PageNum from BPM_PageList where WF_Appid='" + wf_num + "' and IsHomePage='1'";
                wf_num = Rdb.getValueTopOneBySql(sql);
                if (Tools.isBlank(wf_num)) {
                    BeanCtx.log("W", "根据wf_num无法找到应用的首页,请确认应用设置了首页!");
                    response.getWriter().println("Error:Could not find the application's home page, verify that the application settings home page!");
                    return;
                }
            }

            //4.从url中分析获得元素类型和应用编号
            String qry = request.getRequestURL().toString();
            int spos = qry.indexOf("?");
            if (spos != -1) {
                qry = qry.substring(0, spos);
            }
            spos = qry.lastIndexOf("/");
            String elementType = qry.substring(spos + 1);
            if (elementType.equals("r")) {
                elementType = AppUtil.getAppBeanidByNum(wf_num);//根据元素编号获取类型
                if (Tools.isBlank(elementType)) {
                    wf_num = wf_num.replace("<", "&lt;").replace(">", "&gt;");
                    BeanCtx.log("W", "根据wf_num(" + wf_num + ")无法找到设计类型,请检查编号是否正确(区分大小写)!");
                    response.getWriter().println("Error:错误的设计元素编号(" + wf_num + "),请检查编号是否正确或是否有小写!");
                    return;
                }
            }
            appid = Tools.getAppidFromElNum(wf_num);
            if (Tools.isBlank(wf_num)) {
                BeanCtx.out("wf_num是空值" + request.getRequestURL());
                response.getWriter().println("Error:错误的设计元素编号(" + wf_num + "),请检查编号是否正确或是否有小写!");
                return;
            }
            BeanCtx.setAppid(appid);//当前应用的编号设置到线程变量中去
            BeanCtx.setWfnum(wf_num); //把当前设计的编号设置到线程变量中去

            //4.获得应用文档检测应用是否允许匿名访问
            String userid = "";
            Document appdoc = AppUtil.getDocByid("BPM_AppList", "WF_Appid", appid, true);
            if (appdoc.isNull() || appdoc.g("Status").equals("0")) {
                //应用不存在,或者应用未启用禁止访问
                BeanCtx.showErrorMsg(BeanCtx.getMsg("Common", "AppNotFindOrStop", ""));
                BeanCtx.log("D", appid + "=" + BeanCtx.getMsg("Common", "AppNotFindOrStop", ""));
                return;
            }
            else {
                userid = DESUserid.getLoginUserid(request, response);//从session中获取用户名
                if (appdoc.g("Anonymous").equals("1")) {
                    //说明允许匿名访问
                    if (Tools.isBlank(userid)) {
                        userid = "Anonymous";
                    }
                }
                else {
                    // 2.1.看用户是否已登录
                    if (Tools.isBlank(userid)) { // 用户未登录
                        String gourl = request.getRequestURL() + "?" + request.getQueryString();
                        response.sendRedirect("login?gourl=" + Tools.encode(gourl));
                        return;
                    }
                }
            }

            //5.设置已登录用户的userid,并重新初始BeanCtx
            BeanCtx.init(userid, request, response);

            //6.检测用户对针本应用是否有访问权限
            String roles = appdoc.g("Roles");
            if (Tools.isNotBlank(roles) && !appdoc.g("Anonymous").equals("1")) {
                //说明应用指定了访问权限
                if (!BeanCtx.getLinkeyUser().inRoles(userid, roles)) {
                    //说明用户没有访问该应用的权限
                    BeanCtx.showErrorMsg(BeanCtx.getMsg("Common", "AppUnAuthorization", ""));
                    return;
                }
            }

            //7.执行过虑器插件,进行权限，等其他检测，只有检测通过才可以继续执行
            HashSet<String> filterRule = (HashSet<String>) RdbCache.get("FilterRuleList");
            if (filterRule == null) {
                String sql = "select RuleNum from BPM_RuleList where RuleType='6' order by SortNum asc";
                filterRule = Rdb.getValueSetBySql(sql);
                RdbCache.put("FilterRuleList", filterRule);
            }
            for (String ruleNum : filterRule) {
                String result = BeanCtx.getExecuteEngine().run(ruleNum); //执行过虑器规则
                if (result.equals("0")) {
                    return;
                }
            }

            //8.根据设计元素执行不同动作
            if (elementType.equals("rule")) {
                BeanCtx.getExecuteEngine().run(wf_num); // 运行规则
            }
            else {
                AppElement insAppElement = (AppElement) BeanCtx.getBean(elementType);
                insAppElement.run(wf_num); //根据元素类型运行设计解析引擎
            }
        }
        catch (Exception e) {
            BeanCtx.log(e, "W", "");
            BeanCtx.setRollback(true); //设置为需要回滚如果没有开启事务则设置了回滚系统也不会回滚数据
            if (wf_num.startsWith("P_") || wf_num.startsWith("F_") || wf_num.startsWith("D_")) {
                BeanCtx.showErrorMsg(BeanCtx.getMsg("Engine", "ActionException"));
            }
        }
        finally {
            BeanCtx.close(); // 这句一定要执行，要收回资源
        }
    }

}
