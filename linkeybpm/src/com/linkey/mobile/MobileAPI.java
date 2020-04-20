package com.linkey.mobile;

import java.io.IOException;
import java.util.LinkedHashSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;

import cn.linkey.dao.Rdb;
import cn.linkey.dao.RdbCache;
import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.factory.BeanCtx;
import cn.linkey.org.LinkeyUser;
import cn.linkey.servlet.DESUserid;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;
import cn.linkey.util.Vml2Svg;

@WebServlet("/mobile")
public class MobileAPI extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String RETURN_MSG = "msg";
    private static final String STATUS_CODE = "statusCode";
    private JSONObject returnJson;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        BeanCtx.init("Anonymous", req, res);
        String act = BeanCtx.g("act", true);
        returnJson = new JSONObject();
        Object obj = req.getSession().getAttribute("mobileSessionByUserId");
        if (Tools.isBlank(act) || "login".equals(act)) {
            login(req, res);
        }
        else if (obj == null) {
            returnJson.put(RETURN_MSG, "用户未登录或登录超时");
            returnJson.put(STATUS_CODE, "204");
        }
        else {
            String userid = (String) obj;
            BeanCtx.init(userid, req, res);
            switch (act) {
                case "logout": //退出
                    logout(req);
                    break;
                case "applist": //用户中心
                    getAppList();
                    break;
                case "flowlist": //流程中心
                    getFlowlist();
                    break;
                case "flow": //流程图
                    getProcessidSvg();
                    break;
                case "flowlog": //流转记录
                    getFlowLog();
                    break;
                case "todolist": //待办
                    getToDo();
                    break;
                case "didlist": //已办
                    getDid();
                    break;
                case "readlist": //待阅
                    getRead();
                    break;
                case "applylist": //我的申请
                    getApplylist();
                    break;
                case "alllist": //所有在批
                    getAll();
                    break;
                case "archivelist": //已归档列表
                    getArchive();
                    break;
                default:
                    break;
            }
        }
        res.setContentType("text/html;charset=utf-8");
        res.getWriter().append(returnJson.toJSONString());
    }

    /**
     * 登录
     */
    private void login(HttpServletRequest req, HttpServletResponse res) {
        String userid = BeanCtx.g("userName", true);
        String password = BeanCtx.g("password", true);
        if (Tools.isBlank(userid)) {
            returnJson.put(RETURN_MSG, "请填写用户名");
            returnJson.put(STATUS_CODE, "202");
        }
        else if (Tools.isBlank(password)) {
            returnJson.put("msg", "请填写登录密码");
            returnJson.put("status", "203");
        }
        else {
            Document userDc = getUserByUserId(userid, password);
            if (userDc.isNull()) {
                returnJson.put(RETURN_MSG, "用户名或密码错误");
                returnJson.put(STATUS_CODE, "201");
            }
            else {
                setLoginUserid(req, res, userid);
                saveUserLoginLog(req, userid);
                DESUserid.setLoginUserid(req, res, userid);

                returnJson.put(RETURN_MSG, "登录成功");
                returnJson.put(STATUS_CODE, "200");
            }
        }
        BeanCtx.close();
    }

    public static Document getUserByUserId(String userid, String pwd) {
        if (pwd.length() < 32) {
            pwd = Tools.md5(pwd);
        }
        String sql = "select WF_OrUnid from BPM_OrgUserList where Status='1' and Userid='" + Rdb.formatArg(userid) + "' and Password='" + Rdb.formatArg(pwd) + "'";
        return Rdb.getDocumentBySql(sql);
    }

    /**
     * 退出
     */
    private void logout(HttpServletRequest req) {
        String isCluster = Tools.getProperty("Cluster");
        String cookieName = "BPMUserid";
        if (isCluster.equals("1")) {
            Cookie cook = new Cookie(cookieName, null);
            cook.setDomain(Tools.getProperty("Domain"));
            cook.setMaxAge(0);
            cook.setPath(Tools.getProperty("DomainPath"));
            BeanCtx.getResponse().addCookie(cook);
        }

        req.getSession().removeAttribute("mobileSessionByUserId");
        BeanCtx.getRequest().getSession().removeAttribute("mobileSessionByUserId");
        BeanCtx.getRequest().getSession().removeAttribute("LoginUserid");

        RdbCache.remove("UserCacheStrategy", BeanCtx.getUserid());

        returnJson.put(RETURN_MSG, "退出成功");
        returnJson.put(STATUS_CODE, "200");
    }

    /**
     * 所有在批
     */
    private void getAll() {
        String sqlWhere = "";
        String userid = BeanCtx.getUserid();
        if (Rdb.getDbType().equals("MSSQL")) {
            sqlWhere = " where WF_Status='Current' and ','+WF_AllReaders+',' like '%," + userid + ",%'";
        }
        else if (Rdb.getDbType().equals("MYSQL")) {
            sqlWhere = " where WF_Status='Current' and concat(',',WF_AllReaders,',') like '%," + userid + ",%'";
        }
        else {
            sqlWhere = " where WF_Status='Current' and ','||WF_AllReaders||',' like '%," + userid + ",%'";
        }
        String sql = "select WF_OrUnid, Subject,WF_DocCreated as StartTime,WF_AddName_CN, WF_ProcessName,WF_Processid from BPM_MainData " + sqlWhere;
        fillData(sql);
    }

    /**
     * 已归档
     */
    private void getArchive() {
        String sqlWhere = "";
        String userid = BeanCtx.getUserid();
        if (Rdb.getDbType().equals("MSSQL")) {
            sqlWhere = " where ','+WF_AllReaders+',' like '%," + userid + ",%'";
        }
        else if (Rdb.getDbType().equals("MYSQL")) {
            sqlWhere = " where concat(',',WF_AllReaders,',') like '%," + userid + ",%'";
        }
        else {
            sqlWhere = " where ','||WF_AllReaders||',' like '%," + userid + ",%'";
        }
        String sql = "select WF_OrUnid, Subject,WF_DocCreated as StartTime,WF_EndTime,WF_AddName_CN, WF_ProcessName,WF_Processid from BPM_ArchivedData " + sqlWhere;
        fillData(sql);
    }

    /**
     * 待阅
     */
    private void getRead() {
        String sql = "select WF_OrUnid, Subject,WF_DocCreated as StartTime,WF_AddName_CN, WF_ProcessName,WF_Processid from BPM_UserReadDoc where Userid='" + BeanCtx.getUserid() + "'";
        fillData(sql);
    }

    /**
     * 我申请的
     */
    private void getApplylist() {
        String sql = "select WF_OrUnid, Subject,WF_DocCreated as StartTime,WF_AddName_CN, WF_ProcessName,WF_Processid from BPM_AllDocument where WF_AddName='" + BeanCtx.getUserid()
                + "' and WF_Status<>'Draft'";
        fillData(sql);
    }

    /**
     * 已办
     */
    private void getDid() {
        String sqlWhere = "";
        String userid = BeanCtx.getUserid();
        if (Rdb.getDbType().equals("MSSQL")) {
            sqlWhere = " where WF_Status='Current' and ','+WF_EndUser+',' like '%," + userid + ",%'";
        }
        else if (Rdb.getDbType().equals("MYSQL")) {
            sqlWhere = " where WF_Status='Current' and concat(',',WF_EndUser,',') like '%," + userid + ",%'";
        }
        else {
            sqlWhere = " where WF_Status='Current' and ','||WF_EndUser||',' like '%," + userid + ",%'";
        }
        String sql = "select WF_OrUnid, Subject,WF_DocCreated as StartTime,WF_AddName_CN, WF_ProcessName,WF_Processid from BPM_MainData " + sqlWhere;
        fillData(sql);
    }

    /**
     * 待办
     */
    private void getToDo() {
        String sql = "select Subject,WF_AddName_CN,StartTime,WF_DocCreated,WF_ProcessName,WF_Processid,WF_OrUnid from BPM_UserToDo where Userid='" + BeanCtx.getUserid() + "'";
        fillData(sql);
    }

    /**
     * 封闭列表数据
     */
    private void fillData(String sql) {
        String pageSize = BeanCtx.g("pageSize", true);
        String pageNum = BeanCtx.g("pageNum", true);
        String appid = BeanCtx.g("appid", true);
        if (Tools.isBlank(pageSize)) {
            pageSize = "20";
        }
        if (Tools.isBlank(pageNum)) {
            pageNum = "1";
        }
        if (StringUtils.isNotEmpty(appid) && !appid.equals("*")) {
            sql += " and WF_Appid='" + appid + "'";
        }
        sql += " order by WF_DocCreated desc";

        String totalNum = String.valueOf(Rdb.getCountBySql(sql));
        Document[] dc = Rdb.getAllDocumentsBySql(sql, Integer.valueOf(pageNum), Integer.valueOf(pageSize));
        for (Document doc : dc) {
            String startTime = doc.g("StartTime");
            String difTime = DateUtil.getAllDifTime(startTime, DateUtil.getNow());
            int min = Integer.valueOf(difTime);
            if (min > 60) {
                difTime = String.valueOf(min / 60) + "(小时)";
            }
            else {
                difTime = min + "(分钟)";
            }
            doc.s("TotalTime", difTime);
        }
        returnJson.put("pageSize", pageSize);
        returnJson.put("pageNum", pageNum);
        returnJson.put("total", totalNum);
        returnJson.put("rows", Documents.dc2json(dc, ""));
        returnJson.put(STATUS_CODE, "200");
    }

    /**
     * 应用列表
     */
    private void getAppList() {
        Document[] dc = Rdb.getAllDocumentsBySql("select WF_OrUnid, WF_Appid, AppName, Icon from BPM_AppList");
        returnJson.put("rows", Documents.dc2json(dc, ""));
        returnJson.put(STATUS_CODE, "200");
    }

    /**
     * 流程中心
     */
    private void getFlowlist() {
        String appid = BeanCtx.g("appid", true);
        String userid = BeanCtx.getUserid();
        String sqlWhere = " where ProcessStarter is null or ProcessStarter = ''  or ";
        if (Rdb.getDbType().equals("MSSQL")) {
            sqlWhere += " ','+ProcessStarter+',' like '%," + userid + ",%'";
        }
        else if (Rdb.getDbType().equals("MYSQL")) {
            sqlWhere += " concat(',',ProcessStarter,',') like '%," + userid + ",%'";
        }
        else {
            sqlWhere += " ','||ProcessStarter||',' like '%," + userid + ",%'";
        }
        String sql = "select NodeName as ProcessName,Processid,icons from BPM_ModProcessList " + sqlWhere;
        if (StringUtils.isNotEmpty(appid) && !appid.equals("*")) {
            sql += " where WF_Appid='" + appid + "'";
        }
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        returnJson.put("rows", Documents.dc2json(dc, ""));
        returnJson.put(STATUS_CODE, "200");
    }

    private static void setLoginUserid(HttpServletRequest request, HttpServletResponse response, String userid) {
        request.getSession().setAttribute("mobileSessionByUserId", userid);
        request.getSession().setMaxInactiveInterval(30 * 60);
    }

    /**
     * 记录用户的登录日记信息
     */
    private static void saveUserLoginLog(HttpServletRequest request, String userid) {
        //记录用户的登录息
        String sql = "select WF_OrUnid,Userid,LastLoginIP,LastLoginTime from BPM_UserProfile where Userid='" + userid + "'";
        Document doc = Rdb.getDocumentBySql(sql);
        doc.s("Userid", userid);
        doc.s("LastLoginIP", request.getParameter("mobileID")); //手机唯一标识
        doc.s("LastLoginTime", DateUtil.getNow()); //最后登录时间
        doc.save();
    }

    /**
     * 获取流程的审批记录或阅读记录
     * 
     * @return
     */
    private void getFlowLog() {
        //params为运行本规则时所传入的参数
        LinkeyUser linkeyUser = BeanCtx.getLinkeyUser();
        String nodeid = BeanCtx.g("nodeid", true);
        String docUnid = BeanCtx.g("docUnid", true);
        String isRead = BeanCtx.g("isRead", true);
        if (Tools.isBlank(isRead)) {
            isRead = "0";
        }
        String sql = "select * from BPM_AllRemarkList where docUnid='" + docUnid + "' and IsReadFlag='" + isRead + "' order by EndTime";
        if (Tools.isNotBlank(nodeid)) {
            sql = "select * from BPM_AllRemarkList where docUnid='" + docUnid + "' and IsReadFlag='" + isRead + "' and Nodeid='" + nodeid + "' order by EndTime";
        }
        LinkedHashSet<Document> dc = Rdb.getAllDocumentsSetBySql(sql);

        //追加正在处理的用户
        if (isRead.equals("0")) {
            sql = "select Userid, (SELECT CNNAME FROM BPM_ORGUSERLIST WHERE USERID = I.USERID ) UserName, StartTime, FirstReadTime, ExceedTime, LimitTime from BPM_InsUserList i where DocUnid='"
                    + docUnid + "' and Status='Current'";
            if (Tools.isNotBlank(nodeid)) {
                sql += " and Nodeid='" + nodeid + "'";
            }
            LinkedHashSet<Document> curdc = Rdb.getAllDocumentsSetBySql(sql);
            for (Document doc : curdc) {
                doc.s("OverTimeFlag", "0");
                doc.s("ActionName", "处理中");
                doc.s("DeptName", linkeyUser.getDeptNameByUserid(doc.g("Userid"), false));
                if (Tools.isNotBlank(doc.g("LimitTime"))) {
                    doc.s("ExceedTime", doc.g("LimitTime")); //时限
                }
                if (doc.g("ExceedTime").equals("0")) {
                    doc.s("ExceedTime", "-无-");
                }
                if (Tools.isNotBlank(doc.g("FirstReadTime"))) {
                    doc.s("Remark", "已阅(" + doc.g("FirstReadTime") + ")");
                }
            }
            dc.addAll(curdc);
        }

        if (Rdb.getDbType().equals("ORACLE")) {
            returnJson.put("rows", Documents.dc2json(dc, "rows", true));
        }
        else {
            returnJson.put("rows", Documents.dc2json(dc, "rows"));
        }
        returnJson.put(STATUS_CODE, "200");
    }

    /**
     * 查看流程图
     * @return 输出流程图的svg代码
     */
    private void getProcessidSvg() {
        String processid = BeanCtx.g("processid", true);
        String sql = "select GraphicBody from BPM_ModGraphicList where Processid='" + processid + "'";
        String xmlBody = Rdb.getValueBySql(sql);
        xmlBody = Rdb.deCode(xmlBody, false);
        xmlBody = Vml2Svg.getSvgXml(xmlBody);

        BeanCtx.getResponse().setCharacterEncoding("UTF-8");
        BeanCtx.print(xmlBody);
    }
}
