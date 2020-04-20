package cn.linkey.factory;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.linkey.org.LinkeyUser;
import cn.linkey.wf.ProcessEngine;

/**
 * 线程线别的共享对像类,本类为多例类，每一个http线程都应该持有一个本类的实例对像 本类不进行数据的初始化，初始化在BeanCtx中完成
 * 
 * @author Administrator 本类为多实例类
 */

public class ThreadContext {
    private String userid; // 用户英文id
    private String userName;// 用户中文名
    private Connection conn; // 数据库链接对像
    private HttpServletRequest request; // 请求对像
    private HttpServletResponse response; // 响应对像
    private ProcessEngine linkeywf; // 全局引擎对像
    private boolean rollback; // true表示整个线程需要回滚,false表示提交
    private HashMap<String, Object> ctxMap = new HashMap<String, Object>(); // 设置一个全局的交换变量,可以在线程级别中进行使用和跨Bean交换数据
    private String OrgClass; // 用户所属组织架构标识,从通用配置中获取相关初始化
    private boolean isMobile = false; // 是否移动终端状态,true表示是 false表示否
    private String mobileType;   //移动终端类型
    private String appid;// 当前访问设计所属的应用id,如果是访问流程引擎中的设计则为流程所属应用
    private String wfnum; // 当前设计的编号

    private HashMap<String, Map<String, String>> mainFormFieldConfig; // 主表单字段配置信息
    private HashMap<String, Map<String, String>> subFormFieldConfig; // 子表单字段配置信息

    /**
     * 获得当前工作流引擎对像
     * 
     * @return
     */
    protected ProcessEngine getLinkeywf() {
        return linkeywf;
    }

    /**
     * 设置当前工作流引擎对像
     * 
     * @param linkeywf
     */
    protected void setLinkeywf(ProcessEngine linkeywf) {
        this.linkeywf = linkeywf;
    }

    /**
     * 初始化请求和响应实例到线程变量中,主程序开始前需要调用本方法进行初始化
     * 
     * @param res
     * @param req
     */
    protected void init(String userid, HttpServletRequest res, HttpServletResponse req) {
        this.userid = userid;
        this.request = res;
        this.response = req;
    }

    /**
     * 获得线程级别的数据库链接对像
     * 
     * @return
     */
    public Connection getConnection() {
        return conn;
    }

    /**
     * 设置线程级别的数据库链接对像
     * 
     * @param conn
     */
    protected void setConnection(Connection conn) {
        this.conn = conn;
    }

    /**
     * 获得servlet响应对像
     * 
     * @return
     */
    protected HttpServletResponse getResponse() {
        return response;
    }

    /**
     * 获得servlet请求对像
     * 
     * @return
     */
    protected HttpServletRequest getRequest() {
        return request;
    }

    /**
     * 获得当前用户的userid
     * 
     * @return
     */
    protected String getUserid() {
        return userid;
    }

    protected String setUserid(String userid) {
        return this.userid = userid;
    }

    /**
     * 获得当前登录用户的中文名
     * 
     * @return
     */
    protected String getUserName() {
        if (userName == null) {
            return ((LinkeyUser) BeanCtx.getBean("LinkeyUser")).getCnName(userid);
        }
        else {
            return userName;
        }
    }

    /**
     * 获取全局变量对像，按照Object进行返回
     * 
     * @return 返回对像
     */
    protected Object getCtxData(String key) {
        return ctxMap.get(key);
    }

    /**
     * 返回全局变量对像按字符串返回
     * 
     * @param key
     * @return
     */
    protected String getCtxDataStr(String key) {
        Object obj = ctxMap.get(key);
        if (obj == null) {
            return "";
        }
        else {
            return (String) obj;
        }
    }

    /**
     * 设置全局变量对像
     * 
     * @param ctxMap
     */
    protected void setCtxData(String key, Object obj) {
        this.ctxMap.put(key, obj);
    }

    /**
     * 是否需要回滚
     * 
     * @return
     */
    protected boolean isRollBack() {
        return rollback;
    }

    /**
     * 设置回滚标记
     * 
     * @param rollBack
     */
    protected void setRollback(boolean rollBack) {
        this.rollback = rollBack;
    }

    protected String getOrgClass() {
        return OrgClass;
    }

    protected void setOrgClass(String orgClass) {
        OrgClass = orgClass;
    }

    /**
     * 判断移动终端的类型
     * @return 终端类型 1、安卓  2、IOS
     */
    protected String getMobileType() {
        return mobileType;
    }

    /**
     * 设置移动终端的类型
     * @param mobileType 1、安卓  2、IOS
     */
    protected void setMobileType(String mobileType) {
        this.mobileType = mobileType;
    }
    
    protected boolean isMobile() {
        return isMobile;
    }

    protected void setMobile(boolean isMobile) {
        this.isMobile = isMobile;
    }

    protected HashMap<String, Map<String, String>> getMainFormFieldConfig() {
        return mainFormFieldConfig;
    }

    protected void setMainFormFieldConfig(HashMap<String, Map<String, String>> formFieldConfig) {
        this.mainFormFieldConfig = formFieldConfig;
    }

    protected HashMap<String, Map<String, String>> getSubFormFieldConfig() {
        return subFormFieldConfig;
    }

    protected void setSubFormFieldConfig(HashMap<String, Map<String, String>> subFormFieldConfig) {
        this.subFormFieldConfig = subFormFieldConfig;
    }

    protected String getAppid() {
        return appid;
    }

    protected void setAppid(String appid) {
        this.appid = appid;
    }

    protected String getWfnum() {
        return wfnum;
    }

    protected void setWfnum(String wf_num) {
        this.wfnum = wf_num;
    }

    protected void close() {
        Connection conn = getConnection();
        try {
            if (conn != null && !conn.isClosed()) {
//                 BeanCtx.out("T1.0 "+BeanCtx.getWfnum()+",准备关闭数据库链接对像("+conn+") conn.isClosed="+conn.isClosed());
                 conn.close();
//                 BeanCtx.out("T1.1 数据库链接成功关闭!");
            }
            else {
                 BeanCtx.out("TE1.0 "+BeanCtx.getWfnum()+"=ThreadContext.close()链接不存在="+conn);
            }
        }
        catch (Exception e) {
             System.out.println("ThreadContext.close()数据库链接关闭出错!");
            e.printStackTrace();
             BeanCtx.log(e, "E", "Context级别的数据库链接关闭出错!");
        }
        this.request = null;
        this.response = null;
        this.conn = null;
        if (linkeywf != null) {
            this.linkeywf.clear();
        }
        if (this.ctxMap != null) {
            this.ctxMap.clear();
        }
    }
}
