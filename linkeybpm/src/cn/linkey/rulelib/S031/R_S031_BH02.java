package cn.linkey.rulelib.S031;

import java.io.File;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * 
 * 初始化主题切换
 *
 * @author: alibao
 *
 * Modification History:
 * Date         Author          Version            Description
 *---------------------------------------------------------*
 * 2018年9月6日     alibao           v1.0.0               修改原因
 */
final public class R_S031_BH02 implements LinkeyRule {
	private static String insertStartMark = "\r\n<!--insert-->\r\n"; //插入结束标志
	private static String insertEndMark = "<!--insert end-->\r\n"; //插入开始标志
	private static String replacement = "<!--#$1#-->"; //注释html代码的格式

	private static String themePath = "linkey/bpm/easyui/newtheme/black"; //安装主题切换功能之后的默认主题路径, 可更改

	private static String regexThemes = "<link.*?/themes/.*?>"; //匹配gray文件夹的css文件引入
	private static String regexBPMCss = "<link.*?linkey/bpm/css/.*?>"; //匹配linkey/bpm/css文件夹的css文件引入
	// 	private static String regexAppGrid = "<link.*?app\\_grid\\.css.*?>"; //匹配adaption.css文件引入
	private static String regexStyle = "<style>[\\s\\S]*?</style>"; //匹配<style></style>中间的内容
	// 	private static String regexAppPageTheme = "<link.*?app\\_page\\_theme\\.css.*?>"; //匹配app_page_theme.css文件引入
	// 	private static String regex = "(" + regexThemes + "|" + regexAppGrid + "|" + regexStyle + "|" + regexAppPageTheme +")"; //正则合并
	private static String regex = "(" + regexThemes + "|" + regexStyle + "|" + regexBPMCss + ")";

	private static Pattern patternCssOrJs = Pattern.compile("<link|<script|<!--"); //匹配第一个出现的<link或<script或<!--

	/**
	 * 调用之后，
	 * 6个配置项都会注释掉：
	 * 		sytle中间的css引入代码
	 * 		themes文件夹下的css引入代码
	 * 		adaption.css文件引入
	 * 		app_page_theme.css文件的引入
	 * 6个配置项同时会加入：
	 * 		dev或user的css与CreateCSSforDev的js文件
	 * 新建一个配置项DesignerHtmlHeader，并加入：
	 * 		dev的css与CreateCSSforDev的js文件
	 * 
	 * 调用参数：无
	 * 
	 */
	@Override
	public String run(HashMap<String, Object> params) throws Exception {
		String basePath = BeanCtx.getRequest().getSession().getServletContext().getRealPath("");
		//String basePath = BeanCtx.getRequest().getRequestURL().toString().replace("rule","designer");
		basePath = basePath + "\\designer";
		ReadFileName(basePath); //扫描JSP文件，并更改一些写死的页面  //20180718 注释，不修改designer页面  //面包还是要的，改为从数据库中读取
		//params为运行本规则时所传入的参数
		String str = null, sql = null;
		Document doc = null;
		String cssDevclient = "<link rel=\"stylesheet\" id=\"bpm_theme\" type=\"text/css\" href=\"" + themePath
				+ "/devclient.css\">\r\n";
		String cssUserclient = "<link rel=\"stylesheet\" id=\"bpmu_theme\" type=\"text/css\" href=\"" + themePath
				+ "/userclient.css\">\r\n";
		String jsCreateCSSforDev = "<script type=\"text/javascript\" src=\"linkey/bpm/easyui/newtheme/CreateCSSforDev.js\"></script>\r\n";
		String jsCreateCSSforUser = "<script type=\"text/javascript\" src=\"linkey/bpm/easyui/newtheme/CreateCSSforUser.js\"></script>\r\n";
		String insertDev = insertStartMark + cssDevclient + jsCreateCSSforDev + insertEndMark;
		String insertUser = insertStartMark + cssUserclient + jsCreateCSSforUser + insertEndMark;

		//		AppPageHtmlHeader_theme + user
		//		ProcessFormHtmlHeader + dev
		//		AppPageHtmlHeader + dev
		//		AppFormHtmlHeader + dev
		//		AppGridHtmlHeader + dev
		//		DesignerHtmlHeader + dev

		//ProcessFormHtmlHeader
		//AppPageHtmlHeader
		//AppFormHtmlHeader
		//AppGridHtmlHeader
		//		String Configid [] = {"ProcessFormHtmlHeader", "AppPageHtmlHeader", "AppFormHtmlHeader", "AppGridHtmlHeader"};
		//		for (int i=0; i<Configid.length; ++i) {
		//			sql = "select * from BPM_SystemConfig where Configid = '" + Configid[i] + "'";
		//			doc = Rdb.getDocumentBySql(sql);
		//			str = doc.g("ConfigValue"); //取出对应的设置项内容
		//			if (str.indexOf(insertStartMark) == -1) {
		//				sql = "update BPM_SystemConfig set ConfigValue = '" + updateHeader(str, insertDev) + "' where Configid = '" + Configid[i] + "';";
		//				Rdb.execSql(sql);
		//			}
		//		}
		//		
		//		//AppPageHtmlHeader_theme
		//		sql = "select * from BPM_SystemConfig where Configid = 'AppPageHtmlHeader_theme'";
		//		doc = Rdb.getDocumentBySql(sql);
		//		str = doc.g("ConfigValue"); //取出对应的设置项内容
		//		if (str.indexOf(insertStartMark) == -1) {
		//			sql = "update BPM_SystemConfig set ConfigValue = '" + updateHeader(str, insertUser) + "' where Configid = 'AppPageHtmlHeader_theme';";
		//			Rdb.execSql(sql);
		//		}

		//DesignerHtmlHeader //注释正则匹配的内容
		sql = "select * from BPM_SystemConfig where Configid = 'DesignerHtmlHeader'";
		if (Rdb.getCountBySql(sql) == 0) { //不存在DesignerHtml //获取第一个出现<link或者<script或者<!--的位置，如果没有则返回字符串的结尾 //插入insert的内容Header的配置项
			sql = "INSERT INTO BPM_SystemConfig VALUES('c0f1626e0e3f40492d09467003acbd32a6cc','S001','001','DesignerHtmlHeader','<link rel=\"stylesheet\" id=\"bpm_theme\" type=\"text/css\" href=\"../"
					+ themePath
					+ "/devclient.css\"><script type=\"text/javascript\" src=\"../linkey/bpm/easyui/newtheme/CreateCSSforDev.js\"></script>','设计页面资源文件统一管理','', '', '', '1','8.0','2017-12-18 10:54:31','2017-12-31 21:33:44','admin','admin');\n";
			Rdb.execSql(sql); //新增DesignerHtmlHeade设置项
		}

		/*这Rdb.execSql()行多条语句拼接时报错*/
		/*StringBuffer sqlc = new StringBuffer();
		sqlc.
		append("update BPM_PageList set HtmlHeader = REPLACE(HtmlHeader, '<link rel=\"stylesheet\" type=\"text/css\" href=\"linkey/bpm/css/app_grid.css\">', '<!--<link rel=\"stylesheet\"  type=\"text/css\"  href=\"linkey/bpm/css/app_grid.css\">-->') where htmlHeader like '%<link rel=\"stylesheet\" type=\"text/css\" href=\"linkey/bpm/css/app_grid.css\">%';").	
		append("update BPM_PageList set PageBody = REPLACE(PageBody, 'img src=linkey/bpm/images/icons/doclist.gif', 'i class=icon-list style=vertical-align:0%') where PageBody like '%img src=linkey/bpm/images/icons/doclist.gif%';").
		append("update BPM_PageList set PageBody = REPLACE(PageBody, 'img src=linkey/bpm/images/icons/task.gif', 'i class=icon-task style=vertical-align:0%') where PageBody like '%img src=linkey/bpm/images/icons/task.gif%';").
		append("update BPM_PageList set PageBody = REPLACE(PageBody, 'img src=linkey/bpm/images/icons/user_comment.png', 'i class=icon-comment style=vertical-align:0%') where PageBody like '%img src=linkey/bpm/images/icons/user_comment.png%';");
		Rdb.execSql(sqlc.toString());//一些细节的修改 */

		String sqlc = "";
		sqlc = "update BPM_PageList set HtmlHeader = REPLACE(HtmlHeader, '<link rel=\"stylesheet\" type=\"text/css\" href=\"linkey/bpm/css/app_grid.css\">', '<!--<link rel=\"stylesheet\"  type=\"text/css\"  href=\"linkey/bpm/css/app_grid.css\">-->') where htmlHeader like '%<link rel=\"stylesheet\" type=\"text/css\" href=\"linkey/bpm/css/app_grid.css\">%';";
		Rdb.execSql(sqlc);
		sqlc = "update BPM_PageList set PageBody = REPLACE(PageBody, 'img src=linkey/bpm/images/icons/doclist.gif', 'i class=icon-list style=vertical-align:0%') where PageBody like '%img src=linkey/bpm/images/icons/doclist.gif%';";
		Rdb.execSql(sqlc);
		sqlc = "update BPM_PageList set PageBody = REPLACE(PageBody, 'img src=linkey/bpm/images/icons/task.gif', 'i class=icon-task style=vertical-align:0%') where PageBody like '%img src=linkey/bpm/images/icons/task.gif%';";
		Rdb.execSql(sqlc);
		sqlc = "update BPM_PageList set PageBody = REPLACE(PageBody, 'img src=linkey/bpm/images/icons/user_comment.png', 'i class=icon-comment style=vertical-align:0%') where PageBody like '%img src=linkey/bpm/images/icons/user_comment.png%';";
		Rdb.execSql(sqlc);

		sqlc = "update BPM_DevDefaultCode set DefaultCode = replace(DefaultCode, '[red]', '[#fd6060]') where DefaultCode like '%[red]%';";
		Rdb.execSql(sqlc);
		sqlc = "update BPM_GridList set ColumnConfig = replace(ColumnConfig, '[red]', '[#fd6060]') where ColumnConfig like '%[red]%';";
		Rdb.execSql(sqlc);
		sqlc = "update BPM_PageList set PageBody = REPLACE(PageBody, ' color=red', ' color=#fd6060') where PageBody like '% color=red%';";
		Rdb.execSql(sqlc);
		sqlc = "update BPM_PageList set PageBody = REPLACE(PageBody, ' color=blue', ' color=#69a5ff') where PageBody like '% color=blue%';";
		Rdb.execSql(sqlc);
		sqlc = "update BPM_PageList set JsHeader = REPLACE(JsHeader, ' color=red', ' color=#fd6060') where JsHeader like '% color=red%';";
		Rdb.execSql(sqlc);
		sqlc = "update BPM_PageList set JsHeader = REPLACE(JsHeader, ' color=blue', ' color=#69a5ff') where JsHeader like '% color=blue%';";
		Rdb.execSql(sqlc);
		sqlc = "update BPM_PageList set PageBody = REPLACE(PageBody, '#f4f4f4', '#161921') where PageBody like '%bgcolor%' and PageBody like '%#f4f4f4%'";
		Rdb.execSql(sqlc);

		BeanCtx.p("主题初始化完成!");
		return "";
	}

	/**
	* @Description: 扫描一个目录下的所有JSP文件名
	*
	* @param:文件目录，这里是传入designer目录
	* @return：返回结果描述
	*
	* @author: Alibao
	* @date: 2018年1月2日 下午12:57:47
	 */
	public static void ReadFileName(String path) throws Exception {
		String laster = "";
		File dir = new File(path);
		File[] files = dir.listFiles();
		for (File file : files) {
			laster = file.getName();
			if (laster.indexOf(".jsp") > 0) {
				String filePath = path + "\\" + laster;
				//BeanCtx.out(filePath);
				changeJSP(filePath, "UTF-8");
			}
		}
	}

	/**
	* 
	* @Description: 读写JSP文件
	*
	* @param:jspFile JSP文件路径；charset 字符编码
	* @author: Alibao
	* @date: 2018年1月2日 下午2:24:09
	 */
	private static void changeJSP(String jspFile, String charset) {
		String readStr = Tools.readFileToString(jspFile, charset);
		String writeStr = updateJsp(readStr);
		Tools.writeStringToFile(jspFile, writeStr, charset, false);
	}

	//注释掉字符串中一部分css引入的语句与style中的部分代码，并在适当位置插入引用数据库字段的语句
	private static String updateJsp(String str) {
		String insertStartMark = "\r\n<!--insert-->\r\n"; //插入结束标志
		String insertEndMark = "<!--insert end-->\r\n"; //插入开始标志
		String replacementHtml = "<!--#$1#-->"; //注释html代码的格式
		String replacementSytle = "/*#$1#*/"; //注释sytle中间的代码的格式
		String regexThemes = "<link.*?/themes/.*?>"; //匹配themes文件夹的css文件引入
		String regexcssapp = "<link.*?linkey/bpm/css/.*?>"; //匹配css文件夹的css文件引入
		String regex = "(" + regexThemes + "|" + regexcssapp + ")"; //正则合并
		String regexSytle = "(border:1px solid #ddd;background:#f4f4f4)"; //匹配需要注释的东西
		Pattern patternCommon = Pattern.compile("[\\t ]*?<!--#"); //匹配第一个出现的<!--#

		String insert = insertStartMark
				+ "<%String designerHtmlHeader=Rdb.getValueBySql(\"select ConfigValue from BPM_SystemConfig where Configid='DesignerHtmlHeader'\"); %>\r\n"
				+ "<%=designerHtmlHeader%>\r\n" + insertEndMark; //插入的语句
		if (str.indexOf("<!--#") == -1) {
			str = str.replaceAll(regex, replacementHtml); //注释相关css的引入
			str = str.replaceAll(regexSytle, replacementSytle); //注释style中的代码
			Matcher matcher = patternCommon.matcher(str);
			if (matcher.find()) {
				str = str.substring(0, matcher.start()) + insert + str.substring(matcher.start()); //找到第一个注释掉的<link语句并插入对应的搜索语句
			}
		}

		return str;
	}

	//该函数是注释正则匹配的内容，并在中间插入insert的内容
	private static String updateHeader(String header, String insert) {
		if (header.indexOf(header.indexOf("<!--#")) == -1)
			header = header.replaceAll(regex, replacement); //注释正则匹配的内容
		if (header.indexOf(insertStartMark) == -1) {
			Matcher matcher = patternCssOrJs.matcher(header);
			int index = matcher.find() ? matcher.start() : header.length(); //获取第一个出现<link或者<script或者<!--的位置，如果没有则返回字符串的结尾
			header = header.substring(0, index) + insert + header.substring(index, header.length()); //插入insert的内容
		}
		return header;
	}
}