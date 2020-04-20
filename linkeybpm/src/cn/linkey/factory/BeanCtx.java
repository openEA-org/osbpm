package cn.linkey.factory;

import java.io.File;
import java.sql.Connection;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import cn.linkey.dao.Rdb;
import cn.linkey.dao.RdbCache;
import cn.linkey.doc.Document;
import cn.linkey.org.LinkeyUser;
import cn.linkey.rule.EventEngine;
import cn.linkey.rule.ExecuteEngine;
import cn.linkey.rule.RuleConfig;
import cn.linkey.rule.SchedulerEngine;
import cn.linkey.rule.ScriptEngine;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;
import cn.linkey.wf.ProcessEngine;

/**
 * 本类为系统的容器类，所有对像应使用本类来进行创建和获取，而不要单独使用new来创建
 * 
 * <p>本类主要功能为根据请求创建的类来判断容器中是否已经存在实例对像，<br>
 * 如果已经存在就直接返回对像实例 如果不存在则调用LinkeyObj去创建一个出来系统核心类进行缓存。
 * <p>而对于规则和用户自定义的类则不进行缓存 BeanCtx.init("admin",null,null);<br>
 * 这个需要在过虑器中进行初始化BeanCtx.close(); 本类为静态单例类。
 */
final public class BeanCtx {
	private static ThreadLocal<ThreadContext> context = new ThreadLocal<ThreadContext>(); // 线程全局对像,通过get
																							// set方式访问*/

	/**
	 * 初始化请求和响应实例到线程变量中,主程序开始前需要调用本方法进行初始化,这样在其他类中才能拿到这三个变量
	 * 调用本方法后一定要调用close()方法来关闭资源，不然会造成内存不能收回问题
	 * 
	 * @param userid
	 *            当前用户的id
	 * @param res
	 *            HttpServletRequest
	 * @param req
	 *            HttpServletResponse
	 */
	public static void init(String userid, HttpServletRequest res, HttpServletResponse req) {
		getContext().init(userid, res, req);// 先执行本操作再执行后面的
		if (getSystemConfig("SystemDebug").equals("1")) {
			BeanCtx.setDebug(); // 调试状态
		}
		setOrgClass(getSystemConfig("DefaultOrgClass")); // 设置初始化的缺省组织架构
		((LinkeyUser) BeanCtx.getBean("LinkeyUser")).initUserCache(userid); // 首先初始化当前用户的缓存对像
	}

	/**
	 * 获得用户语言环境
	 * @return  返回一个Locale对象
	 */
	public static Locale getUserLocale() {
		Locale userLocale;
		HashMap<String, Object> userCacheObj = (HashMap<String, Object>) RdbCache.get("UserCacheStrategy", getUserid());
		if (userCacheObj == null) {
			LinkeyUser linkeyUser = (LinkeyUser) BeanCtx.getBean("LinkeyUser");
			userLocale = linkeyUser.getUserLocale(getUserid());// 从sql中重新得到
		} else {
			userLocale = (Locale) userCacheObj.get("UserLocale"); // 从缓存中得到
		}
		return userLocale;
	}

	/**
	 * 获得线程级别的变量对像
	 * 
	 * @return  返回一个ThreadContext对象
	 */
	public static ThreadContext getContext() {
		// 初始化线程对像
		ThreadContext insThreadContext = context.get();
		if (insThreadContext == null) {
			insThreadContext = new ThreadContext();
			context.set(insThreadContext);
		}
		return insThreadContext;
	}

	/**
	 * 设置线程级别的变量对像
	 * 
	 * @param obj 传入一个ThreadContext对象
	 */
	public static void setContext(ThreadContext obj) {
		context.set(obj);
	}

	/**
	 * 设置全局的数据库链接对像
	 *  
	 * @param conn 传入一个Connection对象
	 */
	public static void setConnection(Connection conn) {
		getContext().setConnection(conn);
	}

	/**
	 * 获得http请求对像
	 * 
	 * @return 返回一个HttpServletRequest对象
	 */
	public static HttpServletRequest getRequest() {
		return getContext().getRequest();
	}

	/**
	 * 获得http url中的请求参数
	 * 
	 * @return 返回一个String对象
	 */
	public static String getQueryString() {
		return getContext().getRequest().getQueryString();
	}

	/**
	 * 获得用户请求的ip地址
	 * 
	 * @return 返回一个用户请求的ip地址
	 */
	public static String getRemoteAddr() {
		return BeanCtx.getRequest().getRemoteAddr();
	}

	/**
	 * 获得所有get和post过来的参数map对像
	 * 
	 * @return  返回一个Map对象,是所有get和post过来的参数map对像
	 */
	public static Map<String, String[]> getParameterMap() {
		return BeanCtx.getRequest().getParameterMap();
	}

	/**
	 * 获得请求头
	 * 
	 * @return 返回请求头
	 */
	public static HttpServletResponse getResponse() {
		return getContext().getResponse();
	}

	/**
	 * 获得当前登录的用户id
	 * @return 返回当前登录的用户id
	 */
	public static String getUserid() {
		return getContext().getUserid();
	}

	/**
	 * 从用户和部门的组合字符串中分出用户id
	 * 
	 * @param userStr 格式为userid#deptid 如果有多个请用逗号分隔
	 * @return 返回用户id
	 */
	public static String getUseridByMulStr(String userStr) {
		StringBuilder userList = new StringBuilder();
		String[] userArray = Tools.split(userStr);
		for (String userItem : userArray) {
			int spos = userItem.indexOf("#");
			String userid;
			if (spos != -1) {
				userid = userItem.substring(0, spos);
			} else {
				userid = userItem;
			}
			if (userList.length() > 0) {
				userList.append(",");
			}
			userList.append(userid);
		}
		return userList.toString();
	}

	/**
	 * 从用户和部门的组合字符串中分出部门id
	 * 
	 * @param userStr 格式为userid#deptid 如果有多个请用逗号分隔
	 * @return 返回用户与部门的map对像
	 */
	public static HashMap<String, String> getDeptidByMulStr(String userStr) {
		HashMap<String, String> deptSet = new HashMap<String, String>();
		String[] deptArray = Tools.split(userStr);
		for (String deptItem : deptArray) {
			int spos = deptItem.indexOf("#");
			String deptid, userid;
			if (spos != -1) {
				deptid = deptItem.substring(spos + 1);
				userid = deptItem.substring(0, spos);
			} else {
				deptid = "";
				userid = deptItem;
			}
			deptSet.put(userid, deptid);
		}
		return deptSet;
	}

	/**
	 * 设置当前的用户id
	 * 
	 * @param userid 用户id
	 * @return String
	 */
	public static String setUserid(String userid) {
		return getContext().setUserid(userid);
	}

	/**
	 * 获得当前登录的用户中文名称
	 * 
	 * @return 返回当前登录的用户中文名称
	 */
	public static String getUserName() {
		return getContext().getUserName();
	}

	/**
	 * 获得缺省的工作流引擎对像
	 * 
	 * @return 返回ProcessEngine对象
	 */
	public static ProcessEngine getDefaultEngine() {
		if (getContext().getLinkeywf() == null) {
			getContext().setLinkeywf(new ProcessEngine());
			return getContext().getLinkeywf();
		} else {
			return getContext().getLinkeywf();
		}
	}

	/**
	 * 设置缺省的工作流引擎对像
	 * 
	 * @param processEngine ProcessEngine工作流引擎对像
	 */
	public static void setDefaultEngine(ProcessEngine processEngine) {
		getContext().setLinkeywf(processEngine);
	}

	/**
	 * 获得当前工作流引擎对像
	 * 
	 * @return 返回当前工作流引擎对像
	 */
	public static ProcessEngine getLinkeywf() {
		return getContext().getLinkeywf();
	}

	/**
	 * 快速获得LinkeyUser用户操作类的实例
	 * 
	 * @return 返回LinkeyUser对像
	 */
	public static LinkeyUser getLinkeyUser() {
		return (LinkeyUser) getBean("LinkeyUser");
	}

	/**
	 * 设置当前工作流引擎对像
	 * 
	 * @param linkeywf 工作流引擎对像
	 */
	public static void setLinkeywf(ProcessEngine linkeywf) {
		getContext().setLinkeywf(linkeywf);
	}

	/**
	 * 获得回滚标记
	 * 
	 * @return true表示需要回滚,false表示不需要
	 */
	public static boolean isRollBack() {
		return getContext().isRollBack();
	}

	/**
	 * 设置回滚标记
	 * 
	 * @param rollBack
	 *            true表示需要回滚，false表示不需要
	 */
	public static void setRollback(boolean rollBack) {
		// log("D", "数据库链接被设置为需要回滚!");
		getContext().setRollback(rollBack);
	}

	/**
	 * 设置存盘时需要对文档的内容进行编码,默认就是编码的无需设置
	 */
	public static void setDocEncode() {
		BeanCtx.setCtxData("WF_NoEncode", "0");
	}

	/**
	 * 设置存盘时不对文档的内容进行编码
	 */
	public static void setDocNotEncode() {
		BeanCtx.setCtxData("WF_NoEncode", "1");
	}

	/**
	 * 获取当前文档是否编码的状状态
	 * 
	 * @return 返回true表示文档存盘时需要编码 返回false表示文档存盘时不进行编码
	 */
	public static boolean getEnCodeStatus() {
		String encode = (String) BeanCtx.getCtxData("WF_NoEncode");
		if (encode != null && encode.equals("1")) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 判断当前终端是否是Android设备
	 * 
	 * @return true表示是,false表示否
	 */
	public static boolean isAndroid() {
		return "1".equals(getContext().getMobileType());
	}

	/**
	 * 判断当前终端是否是IOS设备
	 * 
	 * @return true表示是,false表示否
	 */
	public static boolean isIOS() {
		return "2".equals(getContext().getMobileType());
	}

	/**
	 * 设置当前是否移动的类型
	 * 
	 * @param mobileType true表示是false表示否
	 */
	public static void setMobileType(String mobileType) {
		getContext().setMobileType(mobileType);
	}

	/**
	 * 获得当前是否移动终端状态
	 * 
	 * @return true表示是,false表示否
	 */
	public static boolean isMobile() {
		return getContext().isMobile();
	}

	/**
	 * 设置当前是否移动终端状态
	 * 
	 * @param isMobile
	 *            true表示是false表示否
	 */
	public static void setMobile(boolean isMobile) {
		getContext().setMobile(isMobile);
	}

	/**
	 * 设置为sql debug状态，系统会输出所有sql语句
	 */
	public static void setDebug() {
		BeanCtx.setCtxData("DEBUGSQL", "1");
	}

	/**
	 * 取消sql debug状态
	 */
	public static void unDebug() {
		BeanCtx.setCtxData("DEBUGSQL", "0");
	}

	/**
	 * 获取全局变量对像,返回Object对像
	 * 
	 * @param key key
	 * @return Object对像
	 */
	public static Object getCtxData(String key) {
		return getContext().getCtxData(key);
	}

	/**
	 * 获取全局变量对像，返回字符串
	 * 
	 * @param key key
	 * @return 字符串
	 */
	public static String getCtxDataStr(String key) {
		return getContext().getCtxDataStr(key);
	}

	/**
	 * 设置全局线程变量对像
	 * 
	 * @param key key
	 * @param obj obj
	 */
	public static void setCtxData(String key, Object obj) {
		getContext().setCtxData(key, obj);
	}


    /**
     * 获得Document对像专用方法
     *
     * @param tableName 数据库表名
     * @return 返回一个Document对象
     */
	public static Document getDocumentBean(String tableName) {
		return new Document(tableName);
	}

	/**
	 * 获得文档对像
	 * 
	 * @param conn
	 *            数据库链接对像
	 * @param tableName
	 *            数据库表
	 * @return Document对象
	 */
	public static Document getDocumentBean(Connection conn, String tableName) {
		return new Document(tableName, conn);
	}

	/**
	 * 获得多语言消息
	 * 
	 * @param lang
	 *            语言包的名称
	 * @param key
	 *            语言包中配置的关键字
	 * @param params
	 *            要格式化消息的字符串参数
	 * @return 返回字符串
	 */
	public static String getMsg(String lang, String key, Object... params) {
		try {
			ResourceBundle messages = ResourceBundle.getBundle("cn.linkey.lang." + lang, getUserLocale());
			return MessageFormat.format(messages.getString(key), params);
		} catch (Exception e) {
			BeanCtx.log("W", "请确认语言包(" + "cn.linkey.lang." + lang + ")和关键字(" + key + ")是否存在!");
			return key;
		}
	}

	/**
	 * 根据当前用户所处环境获得国际标签的text
	 * 
	 * @param labelid
	 *            标签的唯一id
	 * @return 字符串
	 */
	public static String getLabel(String labelid) {
		String labelText = (String) RdbCache.get("LabelCache", labelid);
		if (labelText == null) {
			String country = getUserLocale().getLanguage() + getCountry();
			String sql = "select " + country + " from BPM_Internation where Langid='" + labelid + "'";
			labelText = Rdb.getValueBySql(sql);
			if (Tools.isNotBlank(labelText)) {
				RdbCache.put("LabelCache", labelText);
			} else {
				labelText = labelid;
			}
		}
		return labelText;
	}

	/**
	 * 获得当前用户所在语言环境的国家关键字
	 * 
	 * @return 中文返回CN,英文返回US
	 */
	public static String getCountry() {
		return getUserLocale().getCountry();
	}

	/**
	 * 获得用户所有的角色列表
	 * 
	 * @param userid
	 *            用户id
	 * @return 返回角色编号的set集成
	 */
	public static HashSet<String> getUserRoles(String userid) {
		HashSet<String> rolesSet;
		HashMap<String, Object> userCacheObj = (HashMap<String, Object>) RdbCache.get("UserCacheStrategy", userid);
		if (userCacheObj == null) {
			LinkeyUser linkeyUser = (LinkeyUser) BeanCtx.getBean("LinkeyUser");
			rolesSet = linkeyUser.getRolesByUserid(userid);
		} else {
			rolesSet = (HashSet<String>) userCacheObj.get("UserRoles");
		}
		return rolesSet;
	}

	/**
	 * 从缓存中获得用户个人文档对像
	 * 
	 * @param userid
	 *            用户id
	 * @return 返回用户的个人文档
	 */
	public static Document getUserDoc(String userid) {
		Document userDoc;
		HashMap<String, Object> userCacheObj = (HashMap<String, Object>) RdbCache.get("UserCacheStrategy", userid);
		if (userCacheObj == null) {
			LinkeyUser linkeyUser = (LinkeyUser) BeanCtx.getBean("LinkeyUser");
			userDoc = linkeyUser.getUserDoc(userid); // 从数据库中得到文档
		} else {
			userDoc = (Document) userCacheObj.get("UserDoc"); // 从缓存中得到文档
		}
		return userDoc;
	}

	/**
	 * 获得事件引擎对像，用来触发和执行事件
	 * 
	 * @return 事件引擎对像
	 */
	public static EventEngine getEventEngine() {
		return (EventEngine) BeanCtx.getBean("EventEngine");
	}

	/**
	 * 获得执行引擎对像,用来执行规则
	 * 
	 * @return 执行引擎对像
	 */
	public static ExecuteEngine getExecuteEngine() {
		return (ExecuteEngine) BeanCtx.getBean("ExecuteEngine");
	}

	/**
	 * 获得脚本引擎对像,可以执行路由条件
	 * 
	 * @return ScriptEngine
	 */
	public static ScriptEngine getScriptEngine() {
		return (ScriptEngine) BeanCtx.getBean("ScriptEngine");
	}

	/**
	 * 获得定时调度引擎对像
	 * 
	 * @return SchedulerEngine
	 */
	public static SchedulerEngine getSchedulerEngine() {
		return SchedulerEngine.getIns();
	}

	/**
	 * 获得系统通用配置的参数
	 * 
	 * @param configid configid
	 * @return 返回配置值字符串
	 */
	public static String getSystemConfig(String configid) {

		String configValue = "";
		String configids = "AppFormHtmlHeader,AppGridHtmlHeader,AppPageHtmlHeader,AppPageHtmlHeader_theme,ProcessFormHtmlHeader,DesignerHtmlHeader,UI_theme";

		if (configids.contains(configid)) {

			String userConfigId = configid + "_" + BeanCtx.getUserid();
			String sql = "select userid from s031_uniontheme where Configid='" + userConfigId + "'";
			int i = Rdb.getCountBySql(sql);
			if (i > 0) {
				sql = "select ConfigValue from s031_uniontheme where Configid='" + userConfigId
						+ "' and userid<>'common_id'";
				configValue = Rdb.getValueBySql(sql);
			} else {
				sql = "select ConfigValue from s031_uniontheme where Configid like '%" + configid +  "_common" 
						+ "%' and userid='common_id'";
				configValue = Rdb.getValueBySql(sql); //若不存在自定义的主题则采用默认主题
				//System.out.println(sql + "\n" + configValue);
			}
			configValue = StringEscapeUtils.unescapeHtml4(configValue); //转义一下
			//20180904 添加为空判断，Oracle为空时返回null字符
			if(Tools.isNotBlank(getSystemConfig2(configid))){
				configValue = configValue + "\n" + getSystemConfig2(configid);
			}

		} else {
			//获取获得系统通用配置的参数实际在这里，上面是对主题切换的特殊处理
			configValue = getSystemConfig2(configid);
		}

		return configValue;
	}

	/**
	 * 
	* @Description: 获得系统通用配置的参数
	*
	* @param: configid
	* @return：返回配置值字符串
	* @author: alibao
	* @date: 2018年7月18日 下午4:15:48
	 */
	private static String getSystemConfig2(String configid) {
		String configValue = "";
		HashMap<String, String> configmap = (HashMap<String, String>) RdbCache.getSystemCache("BPM_SystemConfig",
				"ALL");
		configValue = configmap.get(configid); // 首先从缓存中得去查找
		if (configValue == null) {
			configValue = Rdb
					.getValueBySql("select ConfigValue from BPM_SystemConfig where Configid='" + configid + "'"); // 缓存找不到直接到sql表中找
			RdbCache.putSystemCache("BPM_SystemConfig", configid, configValue);
		}
		return configValue;
	}

	/**
	 * 获得应用通用配置的参数
	 * 
	 * @param appid
	 *            应用id
	 * @param configid
	 *            配置id
	 * @return 返回配置值字符串
	 */
	public static String getAppConfig(String appid, String configid) {
		HashMap<String, String> configmap = (HashMap<String, String>) RdbCache.getSystemCache("BPM_AppSystemConfig",
				"ALL");
		String configValue = configmap.get(appid + "_" + configid); // 首先从缓存中得去查找
		if (configValue == null) {
			configValue = Rdb.getValueBySql("select ConfigValue from BPM_AppSystemConfig where WF_Appid='" + appid
					+ "' and Configid='" + configid + "'"); // 缓存找不到直接到sql表中找
			synchronized (new Object()) {
				configmap.put(appid + "_" + configid, configValue);
			}
		}
		return configValue;
	}

	/**
	 * 获得缺省代码配置模板
	 * 
	 * @param id
	 *            配置的唯一编号
	 * @return 返回配置的字符串值
	 */
	public static String getDefaultCode(String id) {
		String sql = "select DefaultCode from BPM_DevDefaultCode where CodeType='" + id + "'";
		return Rdb.getValueBySql(sql);
	}

	/**
	 * 快速输出字符串到web端
	 * 
	 * @param str 字符串
	 */
	public static void p(Object str) {
		print(str.toString());
	}

	/**
	 * 快速从url参数中获得字段内容
	 * 
	 * @param fdName
	 *            post或者get方式过来的参数名
	 * @return 字符串，如果参数不存在返回空值
	 */
	public static String g(String fdName) {
		return g(fdName, false);
	}

	/**
	 * 快速从url参数中获得字段内容
	 * 
	 * @param fdName
	 *            post或者get方式过来的参数名
	 * @param format
	 *            true表示对值进行格式化,一般用来格式化作为sql拼接的参数中，可以防止sql注入,false表示不格式化
	 * @return 字符串，如果参数不存在返回空值
	 */
	public static String g(String fdName, boolean format) {
		if (getContext().getRequest() == null) {
			return "";
		}
		String vStr = getContext().getRequest().getParameter(fdName);
		if (vStr == null) {
			return "";
		} else {
			if (format) {
				vStr = Rdb.formatArg(vStr); // 格式化接收参数，防止sql注入用
			}
			return vStr;
		}
	}

	/**
	 * 获得附件所在目录返回格式为:d:\Java\tomcat7.0.47\webapps\bpm\
	 * 
	 * @return 附件所在目录，格式为: d:\Java\tomcat7.0.47\webapps\bpm\
	 */
	public static String getWebAppsPath() {
		if (getRequest() == null) {
			String appPath;
			java.net.URL url = BeanCtx.class.getResource("");
			if (url == null) {
				appPath = BeanCtx.class.getResource("/").getPath(); // 获得类所在全路径/F:/Java/tomcat7.0.47/webapps/bpm/WEB-INF/classes/cn/linkey/factory/
			} else {
				appPath = BeanCtx.class.getResource("").getPath(); // 获得类所在全路径/F:/Java/tomcat7.0.47/webapps/bpm/WEB-INF/classes/cn/linkey/factory/
			}
			int spos = appPath.indexOf("WEB-INF/");
			if (spos != -1) {
				appPath = appPath.substring(1, spos); // 计算得到
														// F:/Java/tomcat7.0.47/webapps/bpm/
			}
			return appPath;
		} else {
			return getRequest().getSession().getServletContext().getRealPath("/");
		}
	}

	/**
	 * 获得附件存储的目录，以前是获得应用的目录，从8.1改为获得附件的目录
	 * 
	 * @return 返回路径如：d:\Java\tomcat7.0.47\webapps\bpm\ 或者 f:/foldername/
	 */
	public static String getAppPath() {
		String path = BeanCtx.getSystemConfig("AttachmentFolder");
		if (Tools.isNotBlank(path)) {
			return path;
		} else {
			return getWebAppsPath();
		}
	}

	/**
	 * 附件所在文件夹名称
	 * 
	 * @return  附件所在文件夹名称 attachment
	 */
	public static String getAttachmentFolder() {
		return "attachment";
	}

	/**
	 * 获得系统日记路径
	 * 
	 * @return 返回路径字符串
	 */
	private static String getSystemlogPath() {
		return BeanCtx.getWebAppsPath() + "/linkey/bpm/easyui/themes/gray/images/";
	}

	/**
	 * 根据类型和类名返回实例对像，不需要强制类型转换
	 * @param cls
	 * @param beanid
	 * @return 
	 */
	private static <T> T getBean(Class<T> cls, String beanid) {
		HashMap<String, String> configMap = BeanConfig.getClassPath(beanid);
		String className = configMap.get("classPath");
		String singleton = configMap.get("singleton");
		T newobj;
		try {
			if (singleton.equals("0")) { // 多例模式
				Class<T> r = (Class<T>) Class.forName(className);
				newobj = (T) r.newInstance();
			} else {
				// singleton=1单例模式
				newobj = (T) RdbCache.getBeanObject(beanid);
				if (newobj == null) {
					// 说明池中还没有，创建一个新实例并放入其中
					Class<T> r = (Class<T>) Class.forName(className);
					newobj = (T) r.newInstance();
					RdbCache.putBeanObject(beanid, newobj);
				}
			}
			return newobj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 根据类名返回实例对像，需要强制类型转换
	 * @param beanid 实例ID
	 * @return Object对象
	 */
	public static Object getBean(String beanid) {
		HashMap<String, String> configMap = BeanConfig.getClassPath(beanid);
		String className = configMap.get("classPath");
		String singleton = configMap.get("singleton"); // 1表示单例模式需要从缓存中查找,0表示多实例模式，每次创建即可
		configMap = null;
		// p("beanid="+beanid+"--classname="+className+"---singleton="+singleton);
		if (className.startsWith("cn.linkey.rulelib.")) {
			// 从规则中获取对像
			int spos = className.lastIndexOf(".");
			String ruleNum = className.substring(spos + 1);
			return getBeanByRuleNum(ruleNum);
		} else {
			return getBeanByClassName(beanid, className, singleton);
		}
	}

	/**
	 * 直接根据类id,路径返、实例模式来返回对像
	 * @param beanid
	 * @param className
	 * @param singleton
	 * @return Object
	 */
	private static Object getBeanByClassName(String beanid, String className, String singleton) {
		Object obj = null;
		try {
			if (singleton.equals("0")) { // 多例模式
				Class cls = Class.forName(className);
				obj = cls.newInstance();
			} else {
				// singleton=1单例模式
				obj = RdbCache.getBeanObject(beanid);
				if (obj == null) {
					// 说明池中还没有，创建一个新实例并放入其中
					Class cls = Class.forName(className);
					obj = cls.newInstance();
					RdbCache.putBeanObject(beanid, obj);
				}
			}
			return obj;
		} catch (Exception e) {
			if (!beanid.equals("InsNode")) {
				log(e, "E", "BeanCtx获得id为" + beanid + "的bean失败,请检查(" + className + ")是否存在!");
			} else {
				log("E", "获取流程解析器时出现异常,请重新清空系统缓存后再重试,如有问题请联系系统维护人员!");
			}
			return null;
		}
	}

	/**
	 * 根据规则编号返回规则文档对像
	 * 
	 * @param ruleNum
	 *            规则编号
	 * @return 返回规则对像
	 */
	public static Object getBeanByRuleNum(String ruleNum) {
		// 2015.6.27修改
		String newRuleNum = RdbCache.getElementExtendsNum(ruleNum);// 看是否有继承设计
		HashMap<String, String> jarRuleNumCache = (HashMap<String, String>) RdbCache.getSystemCache("BPM_JARRULELIST",
				"ALL");
		if (jarRuleNumCache != null) {
			String classPath = jarRuleNumCache.get(newRuleNum); // 从jar包的缓存中取规则的路径
			if (classPath != null) {
				// 说明规则在jar包中,直接按照类返回即可
				return BeanCtx.getBeanByClassName(newRuleNum, classPath, "1");
			}
		}

		// 规则不在jar包中则读取规则配置表中的信息并返回
		RuleConfig insRuleConfig = (RuleConfig) getBean("RuleConfig");
		Document ruleDoc = insRuleConfig.getRuleDoc(ruleNum);
		if (!ruleDoc.isNull()) {
			return getBeanByRuleDoc(ruleDoc);
		} else {
			BeanCtx.log("E", "根据规则编号获取规则对像时出错,未找到编号为(" + ruleNum + ")的规则!");
			if (BeanCtx.getRequest() != null) {
				BeanCtx.showErrorMsg("Error:Did not find the rule config for (" + ruleNum + ")!");
			}
			return null;
		}
	}

	/**
	 * 根据规则文档对像获得规则对像
	 * 
	 * @param ruleDoc
	 *            规则文档对像
	 * @return 返回规则对像
	 */
	private static Object getBeanByRuleDoc(Document ruleDoc) {
		Object obj = null;
		String singleton = ruleDoc.g("Singleton"); // 是否单列模式1表示是，0表示多实例模式
		String classPath = ruleDoc.g("ClassPath"); // 规则编译生成的classpath路径
		String CompileFlag = ruleDoc.g("CompileFlag");// 是否在内存中实时编译运行0表示每次编译，1表示使用已经编译好的类文件
		String ruleNum = ruleDoc.g("RuleNum");// 规则编号
		if (CompileFlag.equals("1")) {
			// 使用已经编译好的类文件classPath
			try {
				if (getSystemConfig("SysDeveloperMode").equals("1")) {
					// 说明是开发者模式每次都重新创建类并加重新加载
					String fullClassPath = FactoryEngine.getCompilePath() + classPath.replace(".", "/") + ".class";
					File classFile = new File(fullClassPath);

					boolean reCompileFlag = false; // 需要重新编译标记
					if (classFile.exists()) {
						// class类文件已经存在的情况下
						String compileDate = ruleDoc.g("CompileDate");
						if (Tools.isNotBlank(compileDate)) {
							// 计算类的更新时间与数据库中的记录的编译时间，两个时间进行比较在2秒以内可以不用编译，大于2秒就需要重新编译
							java.util.Date comDate = DateUtil.getDate(compileDate, "yyyy-MM-dd HH:mm:ss");
							long diff = comDate.getTime() - classFile.lastModified();
							if (diff > 2000) {
								reCompileFlag = true;
							}
						}
					} else {
						reCompileFlag = true;// 类文件不存在直接编译一个新的
					}
					if (reCompileFlag == false) {
						// 不需要重新编译
						return FactoryEngine.loadClassForDevModel(classPath);
					} else {
						// 编译一个新的类文件
						String ruleCode = ruleDoc.g("RuleCode");
						return FactoryEngine.javaCodeToObject(classPath, ruleCode, true);
					}
				} else if (singleton.equals("0")) { // 多例模式或者是不加入缓存的规则都需要每次创建一个新的实例
					Class cls = Class.forName(classPath);
					obj = cls.newInstance();
					return obj;
				} else {
					// 单例模式，且需要加入缓存的规则先从缓存中查找，如果没有就创建一个并加入缓存中
					obj = RdbCache.getBeanObject(ruleNum);
					if (obj == null) {
						// 说明池中还没有，创建一个新实例并放入其中
						String fullClassPath = FactoryEngine.getCompilePath() + classPath.replace(".", "/") + ".class";
						File classFile = new File(fullClassPath);
						if (classFile.exists()) {
							// 类文件已经存在
							Class cls = Class.forName(classPath);
							obj = cls.newInstance();
						} else {
							// 编译一个类并返回对像
							String ruleCode = ruleDoc.g("RuleCode");
							obj = FactoryEngine.javaCodeToObject(classPath, ruleCode, true);
						}
						// 加入缓存中
						RdbCache.putBeanObject(ruleNum, obj);
					}
					return obj;
				}
			} catch (Exception e) {
				BeanCtx.log(e, "E", "BeanCtx.getBeanByRuleDoc->规则转换为实例对像时出错,请检查类文件是否存在!(ruleNum=" + ruleNum
						+ ",classPath=" + classPath + ")!");
				return null;
			}
		} else if (CompileFlag.equals("0")) {
			// 每次实时编译运行
			String ruleCode = ruleDoc.g("RuleCode");
			if (Tools.isBlank(ruleCode)) {
				BeanCtx.out(ruleDoc.g("RuleName") + "(" + ruleDoc.g("RuleNum") + ")规则代码为空!");
				return null;
			} else {
				return FactoryEngine.javaCodeToObject(classPath, ruleCode, false);
			}
		} else if (CompileFlag.equals("2")) {
			// 首次编译运行生成类文件并把CompileFlag标记改为1
			String ruleCode = ruleDoc.g("RuleCode");
			obj = FactoryEngine.javaCodeToObject(classPath, ruleCode, true);
			ruleDoc.s("CompileFlag", "1");
			ruleDoc.s("CompileDate", DateUtil.getNow());
			ruleDoc.save();
			return obj;
		}
		return null;
	}

	/**
	 * 编译java代码
	 * 
	 * @param javacode
	 *            源代码
	 * @param classPath
	 *            类路径与源代码中的class要一至
	 * @param creatClass
	 *            是否生成类文件true表示是，false表示否
	 * @return 返回1表示成功否则返回错误消息
	 */
	public static String jmcode(String javacode, String classPath, boolean creatClass) {
		return FactoryEngine.compileJavaCode(classPath, javacode, creatClass);
	}

	/**
	 * 直接通过JavaCode返回Java对像
	 * 
	 * @param javaCode
	 * @param classPath
	 *            指定类路径，如果路径为空则自动生成到rulelib包下面
	 */
	private static Object getBeanByCode(String javaCode, String classPath) {
		if (Tools.isBlank(classPath)) {
			// 从代码中分析出类文件名称
			int spos = javaCode.indexOf("class");
			int epos = javaCode.indexOf("{");
			String className = Tools.trim(javaCode.substring(spos + 5, epos));
			classPath = "cn.linkey.rule.rulelib." + className;
		}
		return FactoryEngine.javaCodeToObject(classPath, javaCode, false);
	}

	/**
	 * 获取当前用户所处的组织架构标识
	 * 
	 * @return 返回当前用户所处的组织架构标识
	 */
	public static String getOrgClass() {
		return getContext().getOrgClass();
	}

	/**
	 * 设置当前用户所处的组织架构标识
	 * 
	 * @param orgClass 设置当前用户所处的组织架构标识
	 */
	public static void setOrgClass(String orgClass) {
		getContext().setOrgClass(orgClass);
	}

	/**
	 * 获得主表单字段配置map对像
	 * 
	 * @return 主表单字段配置map对像
	 */
	public static HashMap<String, Map<String, String>> getMainFormFieldConfig() {
		return getContext().getMainFormFieldConfig();
	}

	/**
	 * 设置主表单字段的配置map对像
	 * 
	 * @param formFieldConfig 设置主表单字段的配置map对像
	 */
	public static void setMainFormFieldConfig(HashMap<String, Map<String, String>> formFieldConfig) {
		getContext().setMainFormFieldConfig(formFieldConfig);
	}

	/**
	 * 获得子表单字段配置map对像
	 * 
	 * @return 子表单字段配置map对像
	 */
	public static HashMap<String, Map<String, String>> getSubFormFieldConfig() {
		return getContext().getSubFormFieldConfig();
	}

	/**
	 * 设置子表单字段的配置map对像
	 * 
	 * @param formFieldConfig HashMap
	 */
	public static void setSubFormFieldConfig(HashMap<String, Map<String, String>> formFieldConfig) {
		getContext().setSubFormFieldConfig(formFieldConfig);
	}

	/**
	 * 获取应用ID
	 * @return APPID 应用ID
	 */
	public static String getAppid() {
		return getContext().getAppid();
	}

	/**
	 * 设置AppId
	 * 
	 * @param appid 应用ID
	 */
	public static void setAppid(String appid) {
		getContext().setAppid(appid);
	}

	/**
	 * @return 返回文档标识Id
	 */
	public static String getWfnum() {
		return getContext().getWfnum();
	}

	/**
	 * 
	 * @param wf_num 设置文档标识Id
	 */
	public static void setWfnum(String wf_num) {
		getContext().setWfnum(wf_num);
	}

	/**
	 * 记录用户操作日记
	 * 
	 * @param docUnid 文档ID
	 * @param action 操作
	 * @param remark 缘由
	 */
	public static void userlog(String docUnid, String action, String remark) {
		String ip = "";
		if (getRequest() != null) {
			ip = getRequest().getRemoteAddr();
		}
		remark = remark.replace("'", "''");
		String sql = "insert into BPM_UserActionLog(WF_OrUnid,DocUnid,Userid,Action,IP,Remark,WF_DocCreated) "
				+ "values('" + Rdb.getNewUnid() + "','" + docUnid + "','" + getUserid() + "','" + action + "','" + ip
				+ "','" + remark + "','" + DateUtil.getNow() + "')";
		Rdb.execSql(sql);
	}

	/**
	 * 记录系统日记,统一的log处理函数
	 * 
	 * @param e 为异常对像
	 * @param level 错误级别 level分为5个等级 D-DEBUG、I-INFO、W-WARN、E-ERROR、F-FATA
	 * @param msg 消息内容
	 */
	public static void log(Exception e, String level, String msg) {
		String errorMsg = Tools.getErrorMsgFromException(e);
		writeSystemLog(level, errorMsg + "\n" + msg);
	}

	/**
	 * 记录系统日记,统一的log处理函数
	 * 
	 * @param level 错误级别 level分为5个等级 D-DEBUG、I-INFO、W-WARN、E-ERROR、F-FATA
	 * @param msg 消息内容
	 */
	public static void log(String level, String msg) {
		writeSystemLog(level, msg);
	}

	/**
	 * 写入系统日记到数据库中去 SystemLogWriteType= 1只输出到控制台中 2输出到控制台和log文件中 3只输出到log文件中
	 * PrintDebugLog=0不输出BeanCtx.out();中debug级别的调试信息,其他表示输出
	 * 
	 * @param logLevel 错误级别 level分为5个等级 D-DEBUG、I-INFO、W-WARN、E-ERROR、F-FATA
	 * @param msg 消息内容
	 */
	private static void writeSystemLog(String logLevel, String msg) {

		if (logLevel.equals("E")) {
			setRollback(true); // 设置为数据需要回滚
		}
		// 不输出BeanCtx.out()中产生的debug信息
		if (Tools.getProperty("PrintDebugLog").equals("0") && logLevel.equalsIgnoreCase("D")) {
			return;
		}

		StringBuilder outmsg = new StringBuilder();
		outmsg.append("BPM LOG(").append(getUserid()).append(" ").append(DateUtil.getNow()).append(" ")
				.append(BeanCtx.getWfnum()).append(" ):").append(msg); // 日记格式
		String logType = "";
		if (BeanCtx.getRequest() == null) {
			System.out.println(outmsg);
			return;
		} else {
			logType = Tools.getProperty("SystemLogWriteType");
		}
		if (logLevel.equals("FI")) {
			logType = "3";
		}
		if (logType.equals("1")) {
			// 从控制台中输出
			System.out.println(outmsg);
		} else if (logLevel.equals("FI")) {
			// 写sn到log文件中去
			String filePath = BeanCtx.getSystemlogPath() + "sys.log";
			File file = new File(filePath);
			try {
				FileUtils.write(file, msg);
			} catch (Exception e) {
				System.out.println("错误:系统日记记录错误...........");
			}
		} else {
			if (logType.equals("2")) {
				System.out.println(outmsg);// 同时输出到控制台中去
			}
			// 输出到log文件中
			String filePath = BeanCtx.getWebAppsPath() + "log/" + DateUtil.getDateNum() + ".log";
			File file = new File(filePath);
			try {
				HashSet<String> lines = new HashSet<String>();
				lines.add(outmsg.toString());
				FileUtils.writeLines(file, lines, true);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("文件路径错误，请确定路径是否正确filepath=" + filePath);
			}
		}

		// 如果调试信息为流程则写入到流程的实例跟踪log文件中去
		// String wf_num = BeanCtx.getWfnum();
		// if (wf_num != null && BeanCtx.getLinkeywf() != null &&
		// (wf_num.equals("R_S003_B035") || wf_num.equals("R_S003_B036"))) {
		// String filePath = BeanCtx.getAppPath() + "log/ProcessDebugLog/" +
		// BeanCtx.getLinkeywf().getDocUnid() + ".log";
		// File file = new File(filePath);
		// try {
		// HashSet<String> lines = new HashSet<String>();
		// lines.add(outmsg.toString());
		// FileUtils.writeLines(file, lines, true);
		// }
		// catch (Exception e) {
		// e.printStackTrace();
		// System.out.println("文件路径错误，请确定路径是否正确filepath=" + filePath);
		// }
		// }
	}

	/**
	 * 输出字符串到web端
	 * 
	 * @param str 输出到web端的信息
	 */
	public static void print(String str) {
		try {
			getResponse().getWriter().println(str);
		} catch (Exception e) {
			log(e, "E", "Response 输出消息时出错!");
		}
	}

	/**
	 * 输出字符串到控制台
	 * @param msg 输出到控制台的信息
	 */
	public static void out(Object msg) {
		if (msg != null) {
			writeSystemLog("D", msg.toString());
		} else {
			System.out.println("BeanCtx.out()不能输出对像为null的日记信息!");
		}
	}

	/**
	 * 显示错误信息
	 * 
	 * @param msg 错误信息
	 */
	public static void showErrorMsg(String msg) {
		showErrorMsg(getResponse(), msg);
	}

	/**
	 * WEB端显示错误消息
	 * <p>将输入的提示错误信息，通过Response输出到前端显示
	 * 
	 * @param response HttpServletResponse对象
	 * @param msg 提示的错误信息字符串，会标红显示
	 */
	public static void showErrorMsg(HttpServletResponse response, String msg) {
		String htmlCode = getSystemConfig("ErrorHtmlCode");
		if (Tools.isBlank(htmlCode)) {
			htmlCode = "<center><font color=red>" + msg + "</color></center>";
		} else {
			htmlCode = htmlCode.replace("{msg}", msg);
		}
		try {
			if (response != null) {
				getResponse().setContentType("text/html; charset=UTF-8");
			}
			BeanCtx.p(htmlCode);
		} catch (Exception e) {
			log(e, "E", "Response 输出消息时出错!");
		}
	}

	/**
	 * 销毁线程对像以及线程级别的数据库链接对像
	 */
	public static void close() {
		// 1.如果开启了事务要进行提交或回滚
		try {
			Connection conn = Rdb.getConnection();
			if (conn != null && !conn.isClosed()) {
				if (Rdb.getAutoCommit() == false) {
					if (BeanCtx.isRollBack()) {
						// 程序运行出错或者程序主动设置需要回滚，则所有数据回滚
						Rdb.rollback();
						String url = "";
						if (BeanCtx.getRequest() != null) {
							url = BeanCtx.getRequest().getRequestURL().toString();
						}
						BeanCtx.log("W",
								DateUtil.getNow() + ":用户" + BeanCtx.getUserid() + "运行(" + url + ")时出错拉，所有数据被成功回滚!");
					} else {
						// 程序运行没有出错，或者出错也不要求回滚的情况下
						conn.commit(); // 提交事务，不需回滚提交所有数据
					}
					Rdb.setAutoCommit(true); // 恢复到非事务状态
				} else {
					if (Rdb.getDbType().equals("ORACLE")) {
						conn.commit(); // 这里必须执行commit()因为oracle数据库必须主动commit()不然会产生死锁
					}
				}
			}

			// 2.关闭数据库链接

			// 关闭数据库链接,在线程关闭程序中有关闭功能

			// 清除线程变量
			ThreadContext insThreadContext = context.get();
			if (insThreadContext != null) {
				insThreadContext.close();
				insThreadContext = null;
				// BeanCtx.log("D","BeanCtx.close=>成功关闭线程变量!");
			} else {
				if (conn != null && !conn.isClosed()) {
					// BeanCtx.out("BeanCtx.close=>准备关闭数据库链接!="+conn);
					conn.close();
				}
				// BeanCtx.log("D","BeanCtx.close=>没有需要关闭的线程变量!");
			}
			context.remove();
		} catch (Exception e) {
			log(e, "E", "BeanCtx关闭出错!");
		} finally {
			context.remove();
		}
	}
}
