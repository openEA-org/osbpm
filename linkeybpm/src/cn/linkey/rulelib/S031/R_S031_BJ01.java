package cn.linkey.rulelib.S031;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
/**
 * 
 * 切换主题规则
 *
 * @author: alibao
 *
 * Modification History:
 * Date         Author          Version            Description
 *---------------------------------------------------------*
 * 2018年9月6日     alibao           v1.0.0               修改原因
 */
final public class R_S031_BJ01 implements LinkeyRule {
    
    private static String bwthemeid = "3a2870070fa48049cc0b11301ada1c79f8c5"; //蓝白老主题的id
//	private static String common_all_main_text_color = "#94a7b2"; //通用文字颜色、时间控件文字颜色
//	private static String common_all_main_text_color2 = "#ffffff"; //鼠标移动和点击时文字颜色、标题字体颜色
	private static String common_all_main_background = "#161921"; //主窗口背景颜色、点击时背景颜色……
//	private static String common_all_main_background2 = "#1C222C"; //通用颜色2，左侧导航树背景、视图背景、开发端菜单栏背景……
//	private static String common_all_main_background3 = "#233142"; //通用颜色3，页面头部背景、视图第一行背景……
//	private static String common_all_main_background4 = "#181d26"; //通用颜色4，tab栏背景颜色……
//	private static String common_all_main_background5 = "#2e4561"; //通用颜色5，流程中心模板背景……
	private static String common_all_main_background6 = "#fd6060"; //通用颜色6，在视图中的特殊颜色1
	private static String common_all_main_background7 = "#69a5ff"; //通用颜色7，在视图中的特殊颜色2
	
	//	private static String common_all_main_text_color_old = "#94a7b2"; //通用文字颜色、时间控件文字颜色
//	private static String common_all_main_text_color2_old = "#ffffff"; //鼠标移动和点击时文字颜色、标题字体颜色
	private static String common_all_main_background_old = "#161921"; //主窗口背景颜色、点击时背景颜色……
//	private static String common_all_main_background2_old = "#1C222C"; //通用颜色2，左侧导航树背景、视图背景、开发端菜单栏背景……
//	private static String common_all_main_background3_old = "#233142"; //通用颜色3，页面头部背景、视图第一行背景……
//	private static String common_all_main_background4_old = "#181d26"; //通用颜色4，tab栏背景颜色……
//	private static String common_all_main_background5_old = "#2e4561"; //通用颜色5，流程中心模板背景……
	private static String common_all_main_background6_old = "#fd6060"; //通用颜色6，在视图中的特殊颜色1
	private static String common_all_main_background7_old = "#69a5ff"; //通用颜色7，在视图中的特殊颜色2
    
    
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
		//20180904 改为从数据库中读取
	    //String oldThemeid = BeanCtx.g("oldThemeid");
		String sql2 = "select distinct THIMEID from S031_UNIONTHEME where userid = '" + BeanCtx.getUserid() +"'";
	    String oldThemeid = Rdb.getValueBySql(sql2);
	    if(Tools.isBlank(oldThemeid)){
	    	oldThemeid = bwthemeid; //默认蓝色主题
	    }
	    String newThemeid = BeanCtx.g("newThemeid");
	   
	    
	    if (oldThemeid.equals(newThemeid)|| "".equals(oldThemeid) || "".equals(newThemeid)) { //如果切换的主题相同，或者其中一方为空则不变动
	    	return "";
	    }
	    else{
	       // this.disUpdateSql(oldThemeid);
	       // this.updateSql(newThemeid);
	       
	    	
	       this.updatetheme(oldThemeid,newThemeid);
	    }
	    return "";
	}
	
	private Document getColor(String themeid) {
		//获取颜色
//		common_all_main_text_color = "#94a7b2"; //通用文字颜色、时间控件文字颜色
//		common_all_main_text_color2 = "#ffffff"; //鼠标移动和点击时文字颜色、标题字体颜色
//		common_all_main_background = "#161921"; //主窗口背景颜色、点击时背景颜色……
//		common_all_main_background2 = "#1C222C"; //通用颜色2，左侧导航树背景、视图背景、开发端菜单栏背景……
//		common_all_main_background3 = "#233142"; //通用颜色3，页面头部背景、视图第一行背景……
//		common_all_main_background4 = "#181d26"; //通用颜色4，tab栏背景颜色……
//		common_all_main_background5 = "#2e4561"; //通用颜色5，流程中心模板背景……
//		common_all_main_background6 = "#fd6060"; //通用颜色6，在视图中的特殊颜色1
//		common_all_main_background7 = "#69a5ff"; //通用颜色7，在视图中的特殊颜色2
		String sql = "select XmlData from S031_ThemeList where WF_OrUnid = '" + themeid+"'";
		Document doc = Rdb.getDocumentBySql(sql);
		/*common_all_main_background6 = doc.g("common_all_main_background6");
		common_all_main_background7 = doc.g("common_all_main_background7");*/
		return doc;
	}
	
	private void updatetheme(String oldThemeid, String newThemeid){
	    Document oldThemeDoc = this.getColor(oldThemeid);
	    Document newThemeDoc = this.getColor(newThemeid);
	    common_all_main_background_old = oldThemeDoc.g("common_all_main_background");
	    common_all_main_background6_old = oldThemeDoc.g("common_all_main_background6");
		common_all_main_background7_old = oldThemeDoc.g("common_all_main_background7");

		common_all_main_background = newThemeDoc.g("common_all_main_background");
		common_all_main_background6 = newThemeDoc.g("common_all_main_background6");
		common_all_main_background7 = newThemeDoc.g("common_all_main_background7");
		
		
		//BeanCtx.out(common_all_main_background6_old+"\n"+common_all_main_background7_old+"\n"+common_all_main_background6+"\n"+common_all_main_background7);
		
	/*	StringBuffer sql = new StringBuffer();
		sql.
		append("update BPM_DevDefaultCode set DefaultCode = replace(DefaultCode, '[").append(common_all_main_background6_old).append("]', '[").append(common_all_main_background6).append("]') where DefaultCode like '%[red]%';\n").
		append("update BPM_GridList set ColumnConfig = replace(ColumnConfig, '[").append(common_all_main_background6_old).append("]', '[").append(common_all_main_background6).append("]') where ColumnConfig like '%[red]%';\n").
		append("update BPM_GridList set JsHeader = REPLACE(JsHeader, '<font color=").append(common_all_main_background6_old).append(">', '<font color=").append(common_all_main_background6).append(">') where JsHeader like '%<font color=red>%';\n").
		append("update BPM_GridList set JsHeader = REPLACE(JsHeader, '<font color=").append(common_all_main_background7_old).append(">', '<font color=").append(common_all_main_background7).append(">') where JsHeader like '%<font color=blue>%';\n").
		append("update BPM_RuleList set RuleCode = REPLACE(RuleCode, '<font color=").append(common_all_main_background6_old).append(">', '<font color=").append(common_all_main_background6).append(">') where RuleCode like '%<font color=red>%';\n").
		append("update BPM_RuleList set RuleCode = REPLACE(RuleCode, '<font color=").append(common_all_main_background7_old).append(">', '<font color=").append(common_all_main_background7).append(">') where RuleCode like '%<font color=blue>%';\n").
		append("update BPM_PageList set PageBody = REPLACE(PageBody, ' color=").append(common_all_main_background6_old).append("', ' color=").append(common_all_main_background6).append("') where PageBody like '% color=red%';\n").
		append("update BPM_PageList set PageBody = REPLACE(PageBody, ' color=").append(common_all_main_background7_old).append("', ' color=").append(common_all_main_background7).append("') where PageBody like '% color=blue%';\n").
		append("update BPM_PageList set JsHeader = REPLACE(JsHeader, ' color=").append(common_all_main_background6_old).append("', ' color=").append(common_all_main_background6).append("') where JsHeader like '% color=red%';\n").
		append("update BPM_PageList set JsHeader = REPLACE(JsHeader, ' color=").append(common_all_main_background7_old).append("', ' color=").append(common_all_main_background7).append("') where JsHeader like '% color=blue%';\n");
		Rdb.execSql(sql.toString());*/
		
		String sqlc = "";
        sqlc = "update BPM_DEVDEFAULTCODE set DEFAULTCODE = replace(DEFAULTCODE, '["+common_all_main_background6_old+"]', '["+common_all_main_background6+"]') where DEFAULTCODE like '%["+common_all_main_background6_old+"]%'";
		Rdb.execSql(sqlc);
		sqlc = "update BPM_GRIDLIST set COLUMNCONFIG = replace(COLUMNCONFIG, '["+common_all_main_background6_old+"]', '["+common_all_main_background6+"]') where COLUMNCONFIG like '%["+common_all_main_background6_old+"]%'";
	    Rdb.execSql(sqlc);
		sqlc = "update BPM_GRIDLIST set JSHEADER = REPLACE(JSHEADER, '<font color="+common_all_main_background6_old+">', '<font color="+common_all_main_background6+">') where JSHEADER like '%<font color="+common_all_main_background6_old+">%'";
		Rdb.execSql(sqlc);
		sqlc = "update BPM_GRIDLIST set JSHEADER = REPLACE(JSHEADER, '<font color="+common_all_main_background7_old+">', '<font color="+common_all_main_background7+">') where JSHEADER like '%<font color="+common_all_main_background7_old+">%'";
 		Rdb.execSql(sqlc);
 		sqlc = "update BPM_RULELIST set RULECODE = REPLACE(RULECODE, '<font color="+common_all_main_background6_old+">', '<font color="+common_all_main_background6+">') where RULECODE like '%<font color="+common_all_main_background6_old+">%'";
	    Rdb.execSql(sqlc);
		sqlc = "update BPM_RULELIST set RULECODE = REPLACE(RULECODE, '<font color="+common_all_main_background7_old+">', '<font color="+common_all_main_background7+">') where RULECODE like '%<font color="+common_all_main_background7_old+">%'";
		Rdb.execSql(sqlc);
		sqlc = "update BPM_PAGELIST set PAGEBODY = REPLACE(PAGEBODY, ' color="+common_all_main_background6_old+"', ' color="+common_all_main_background6+"') where PAGEBODY like '% color="+common_all_main_background6_old+"'";
 		Rdb.execSql(sqlc);
 		sqlc = "update BPM_PAGELIST set PAGEBODY = REPLACE(PAGEBODY, ' color="+common_all_main_background7_old+"', ' color="+common_all_main_background7+"') where PAGEBODY like '% color="+common_all_main_background7_old+"'";
		Rdb.execSql(sqlc);
		sqlc = "update BPM_PAGELIST set JSHEADER = REPLACE(JSHEADER, ' color="+common_all_main_background6_old+"', ' color="+common_all_main_background6+"') where JSHEADER like '% color="+common_all_main_background6_old+"'";
 		Rdb.execSql(sqlc);
 		sqlc = "update BPM_PAGELIST set JSHEADER = REPLACE(JSHEADER, ' color="+common_all_main_background7_old+"', ' color="+common_all_main_background7+"') where JSHEADER like '% color="+common_all_main_background7_old+"'";
 		Rdb.execSql(sqlc);
 		sqlc = "update BPM_PAGELIST set PAGEBODY = REPLACE(PAGEBODY, '"+common_all_main_background_old+"', '"+common_all_main_background+"') where PAGEBODY like '%bgcolor%' and PAGEBODY like '%"+common_all_main_background_old+"%'";
        Rdb.execSql(sqlc);
		
        //20180718 更新header
        updateHeader(newThemeid);
	}
	
	/*private void updateSql(String themeid) {
	    //更新主题的细节部分
		this.getColor(themeid);
		StringBuffer sql = new StringBuffer();
		sql.
		append("update BPM_DevDefaultCode set DefaultCode = replace(DefaultCode, '[red]', '[").append(common_all_main_background6).append("]') where DefaultCode like '%[red]%';\n").
		append("update BPM_GridList set ColumnConfig = replace(ColumnConfig, '[red]', '[").append(common_all_main_background6).append("]') where ColumnConfig like '%[red]%';\n").
		append("update BPM_GridList set JsHeader = REPLACE(JsHeader, '<font color=red>', '<font color=").append(common_all_main_background6).append(">') where JsHeader like '%<font color=red>%';\n").
		append("update BPM_GridList set JsHeader = REPLACE(JsHeader, '<font color=blue>', '<font color=").append(common_all_main_background7).append(">') where JsHeader like '%<font color=blue>%';\n").
		append("update BPM_RuleList set RuleCode = REPLACE(RuleCode, '<font color=red>', '<font color=").append(common_all_main_background6).append(">') where RuleCode like '%<font color=red>%';\n").
		append("update BPM_RuleList set RuleCode = REPLACE(RuleCode, '<font color=blue>', '<font color=").append(common_all_main_background7).append(">') where RuleCode like '%<font color=blue>%';\n").
		append("update BPM_PageList set PageBody = REPLACE(PageBody, ' color=red', ' color=").append(common_all_main_background6).append("') where PageBody like '% color=red%';\n").
		append("update BPM_PageList set PageBody = REPLACE(PageBody, ' color=blue', ' color=").append(common_all_main_background7).append("') where PageBody like '% color=blue%';\n").
		append("update BPM_PageList set JsHeader = REPLACE(JsHeader, ' color=red', ' color=").append(common_all_main_background6).append("') where JsHeader like '% color=red%';\n").
		append("update BPM_PageList set JsHeader = REPLACE(JsHeader, ' color=blue', ' color=").append(common_all_main_background7).append("') where JsHeader like '% color=blue%';\n").
		
		append("update BPM_PageList set HtmlHeader = REPLACE(HtmlHeader, '<link rel=\"stylesheet\" type=\"text/css\" href=\"linkey/bpm/css/app_grid.css\">', '<!--<link rel=\"stylesheet\"  type=\"text/css\"  href=\"linkey/bpm/css/app_grid.css\">-->') where htmlHeader like '%<link rel=\"stylesheet\" type=\"text/css\" href=\"linkey/bpm/css/app_grid.css\">%';").	

		append("update BPM_PageList set PageBody = REPLACE(PageBody, 'img src=linkey/bpm/images/icons/doclist.gif', 'i class=icon-list style=vertical-align:0%') where PageBody like '%img src=linkey/bpm/images/icons/doclist.gif%';").
		append("update BPM_PageList set PageBody = REPLACE(PageBody, 'img src=linkey/bpm/images/icons/task.gif', 'i class=icon-task style=vertical-align:0%') where PageBody like '%img src=linkey/bpm/images/icons/task.gif%';").
		append("update BPM_PageList set PageBody = REPLACE(PageBody, 'img src=linkey/bpm/images/icons/user_comment.png', 'i class=icon-comment style=vertical-align:0%') where PageBody like '%img src=linkey/bpm/images/icons/user_comment.png%';");
		
		Rdb.execSql(sql.toString());
		BeanCtx.out("done");
	}
	private void disUpdateSql(String themeid) {
	    //还原更新主题的细节部分
		this.getColor(themeid);
		StringBuffer sql = new StringBuffer();
		sql.
		append("update BPM_DevDefaultCode set DefaultCode = replace(DefaultCode, '[").append(common_all_main_background6).append("]', '[red]') where DefaultCode like '%[").append(common_all_main_background6).append("]%';\n").
		append("update BPM_GridList set ColumnConfig = replace(ColumnConfig, '[").append(common_all_main_background6).append("]', '[red]') where ColumnConfig like '%[").append(common_all_main_background6).append("]%';\n").
		append("update BPM_GridList set JsHeader = REPLACE(JsHeader, '<font color=").append(common_all_main_background6).append(">', '<font color=red>') where JsHeader like '%<font color=").append(common_all_main_background6).append(">%';\n").
		append("update BPM_GridList set JsHeader = REPLACE(JsHeader, '<font color=").append(common_all_main_background7).append(">', '<font color=blue>') where JsHeader like '%<font color=").append(common_all_main_background7).append(">%';\n").
		append("update BPM_RuleList set RuleCode = REPLACE(RuleCode, '<font color=").append(common_all_main_background6).append(">', '<font color=red>') where RuleCode like '%<font color=").append(common_all_main_background6).append(">%';\n").
		append("update BPM_RuleList set RuleCode = REPLACE(RuleCode, '<font color=").append(common_all_main_background7).append(">', '<font color=blue>') where RuleCode like '%<font color=").append(common_all_main_background7).append(">%';\n");
		append("update BPM_PageList set PageBody = REPLACE(PageBody, ' color=").append(common_all_main_background6).append("', ' color=red') where PageBody like '% color=").append(common_all_main_background6).append("%';\n").
		append("update BPM_PageList set PageBody = REPLACE(PageBody, ' color=").append(common_all_main_background7).append("', ' color=blue') where PageBody like '% color=").append(common_all_main_background7).append("%';\n").
		append("update BPM_PageList set JsHeader = REPLACE(JsHeader, ' color=").append(common_all_main_background6).append("', ' color=red') where JsHeader like '% color=").append(common_all_main_background6).append("%';\n").
		append("update BPM_PageList set JsHeader = REPLACE(JsHeader, ' color=").append(common_all_main_background7).append("', ' color=blue') where JsHeader like '% color=").append(common_all_main_background7).append("%';\n").
		
		append("update BPM_PageList set HtmlHeader = REPLACE(HtmlHeader, '<!--<link rel=\"stylesheet\"  type=\"text/css\"  href=\"linkey/bpm/css/app_grid.css\">-->', '<link rel=\"stylesheet\" type=\"text/css\" href=\"linkey/bpm/css/app_grid.css\">') where htmlHeader like '%<link rel=\"stylesheet\"  type=\"text/css\"  href=\"linkey/bpm/css/app_grid.css\">%';\n").
		
		append("update BPM_PageList set PageBody = REPLACE(PageBody, 'i class=icon-list style=vertical-align:0%', 'img src=linkey/bpm/images/icons/doclist.gif') where PageBody like '%i class=icon-list style=vertical-align:0%';\n").
		append("update BPM_PageList set PageBody = REPLACE(PageBody, 'i class=icon-task style=vertical-align:0%', 'img src=linkey/bpm/images/icons/task.gif') where PageBody like '%i class=icon-task style=vertical-align:0%';\n").
		append("update BPM_PageList set PageBody = REPLACE(PageBody, 'i class=icon-comment style=vertical-align:0%', 'img src=linkey/bpm/images/icons/user_comment.png') where PageBody like '%i class=icon-comment style=vertical-align:0%';\n");

		Rdb.execSql(sql.toString());
		//BeanCtx.p("haha2");
	}*/
	
	
	/**
	 * 
	* @Description: 更新页面、表单、视图头部header到S031_UnionTheme表
	*
	* @param: filePath 主题对应的路径
	* @author: alibao
	* @date: 2018年7月18日 上午10:07:37
	 */
	private void updateHeader(String thimeid){

//	    String sql1="update BPM_SystemConfig set ConfigValue='<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><link rel=\"stylesheet\" id=\"bpm_theme\" type=\"text/css\" href=\""+filePath+"/devclient.css\"><script type=\"text/javascript\" src=\"linkey/bpm/easyui/newtheme/CreateCSSforDev.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/lang/{LANG}.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/easyui/jquery.min.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/easyui/jquery.easyui.min.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/sharefunction.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/json.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/viewgrid.js\"></script>'where Configid='AppGridHtmlHeader'";
//	    String sql2="update BPM_SystemConfig set ConfigValue='<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><link rel=\"stylesheet\" id=\"bpm_theme\" type=\"text/css\" href=\""+filePath+"/devclient.css\"><script type=\"text/javascript\" src=\"linkey/bpm/easyui/newtheme/CreateCSSforDev.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/lang/{LANG}.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/easyui/jquery.min.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/easyui/jquery.easyui.min.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/sharefunction.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/json.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/app_openform.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/uivalidate.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/app_custom.js\"></script>'where Configid='AppFormHtmlHeader'";
//	    String sql3="update BPM_SystemConfig set ConfigValue='<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><link rel=\"stylesheet\" id=\"bpm_theme\" type=\"text/css\" href=\""+filePath+"/devclient.css\"><script type=\"text/javascript\" src=\"linkey/bpm/easyui/newtheme/CreateCSSforDev.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/easyui/jquery.min.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/easyui/jquery.easyui.min.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/sharefunction.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/json.js\"></script>'where Configid='AppPageHtmlHeader'";
//	    String sql4="update BPM_SystemConfig set ConfigValue='<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><link rel=\"stylesheet\" id=\"bpmu_theme\" type=\"text/css\" href=\""+filePath+"/userclient.css\"><script type=\"text/javascript\" src=\"linkey/bpm/easyui/newtheme/CreateCSSforUser.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/easyui/jquery.min.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/easyui/jquery.easyui.min.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/sharefunction.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/json.js\"></script>'where Configid='AppPageHtmlHeader_theme'";
//	    String sql5="update BPM_SystemConfig set ConfigValue='<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><link rel=\"stylesheet\" id=\"bpm_theme\" type=\"text/css\" href=\""+filePath+"/devclient.css\"><script type=\"text/javascript\" src=\"linkey/bpm/easyui/newtheme/CreateCSSforDev.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/lang/{LANG}.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/easyui/jquery.min.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/easyui/jquery.easyui.min.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/json.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/sharefunction.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/engine_openform.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/uivalidate.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/viewgrid.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/engine_custom.js\"></script>'where Configid='ProcessFormHtmlHeader'";
//	    String sql6="update BPM_SystemConfig set ConfigValue='<link rel=\"stylesheet\" id=\"bpm_theme\" type=\"text/css\" href=\"../"+filePath+"/devclient.css\"><script type=\"text/javascript\" src=\"../linkey/bpm/easyui/newtheme/CreateCSSforDev.js\"></script>'where Configid='DesignerHtmlHeader'";
//	    
//	    Rdb.execSql(sql1.toString());
//	    Rdb.execSql(sql2.toString());
//	    Rdb.execSql(sql3.toString());
//	    Rdb.execSql(sql4.toString());
//	    Rdb.execSql(sql5.toString());
//	    Rdb.execSql(sql6.toString());
		
		
		String filePath = getFilePath(thimeid); //路径
		String userid = BeanCtx.getUserid();
		
		//AppGridHtmlHeader
		String configValue1 = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><link rel=\"stylesheet\" id=\"bpm_theme\" type=\"text/css\" href=\""+filePath+"/devclient.css\"><script type=\"text/javascript\" src=\"linkey/bpm/easyui/newtheme/CreateCSSforDev.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/lang/{LANG}.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/easyui/jquery.min.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/easyui/jquery.easyui.min.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/sharefunction.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/json.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/viewgrid.js\"></script>";
		//AppFormHtmlHeader
		String configValue2 = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><link rel=\"stylesheet\" id=\"bpm_theme\" type=\"text/css\" href=\""+filePath+"/devclient.css\"><script type=\"text/javascript\" src=\"linkey/bpm/easyui/newtheme/CreateCSSforDev.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/lang/{LANG}.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/easyui/jquery.min.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/easyui/jquery.easyui.min.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/sharefunction.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/json.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/app_openform.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/uivalidate.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/app_custom.js\"></script>";
		//AppPageHtmlHeader
		String configValue3 = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><link rel=\"stylesheet\" id=\"bpm_theme\" type=\"text/css\" href=\""+filePath+"/devclient.css\"><script type=\"text/javascript\" src=\"linkey/bpm/easyui/newtheme/CreateCSSforDev.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/easyui/jquery.min.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/easyui/jquery.easyui.min.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/sharefunction.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/json.js\"></script>";
	    //AppPageHtmlHeader_theme
		String configValue4 = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><link rel=\"stylesheet\" id=\"bpmu_theme\" type=\"text/css\" href=\""+filePath+"/userclient.css\"><script type=\"text/javascript\" src=\"linkey/bpm/easyui/newtheme/CreateCSSforUser.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/easyui/jquery.min.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/easyui/jquery.easyui.min.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/sharefunction.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/json.js\"></script>";
	    //ProcessFormHtmlHeader
		String configValue5 = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><link rel=\"stylesheet\" id=\"bpm_theme\" type=\"text/css\" href=\""+filePath+"/devclient.css\"><script type=\"text/javascript\" src=\"linkey/bpm/easyui/newtheme/CreateCSSforDev.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/lang/{LANG}.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/easyui/jquery.min.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/easyui/jquery.easyui.min.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/json.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/sharefunction.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/engine_openform.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/uivalidate.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/viewgrid.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/engine_custom.js\"></script>";
		//DesignerHtmlHeader
		String configValue6 = "<link rel=\"stylesheet\" id=\"bpm_theme\" type=\"text/css\" href=\"../"+filePath+"/devclient.css\"><script type=\"text/javascript\" src=\"../linkey/bpm/easyui/newtheme/CreateCSSforDev.js\"></script>";
		
		//UI_theme //20180920添加
		String configValue7 = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><link rel=\"stylesheet\" type=\"text/css\" href=\""+filePath+"/page.css\"><script type=\"text/javascript\" src=\"linkey/bpm/easyui/jquery.min.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/page.js\"></script>";
		
		String[] configids = {"AppGridHtmlHeader","AppFormHtmlHeader","AppPageHtmlHeader","AppPageHtmlHeader_theme","ProcessFormHtmlHeader","DesignerHtmlHeader","UI_theme"};
	    String[] configValues = {configValue1,configValue2,configValue3,configValue4,configValue5,configValue6,configValue7};
		String sql = "";
		for (int i = 0; i < configids.length; i++) {
			sql = "select * from s031_uniontheme where CONFIGID='" + configids[i] + "_" + userid + "'";
			Document doc = Rdb.getDocumentBySql(sql);
			doc.s("thimeid", thimeid);
			doc.s("userid", userid);
			doc.s("Configid", configids[i] + "_" + userid);
			doc.s("ConfigValue", configValues[i]);
			doc.save();
		}
		
		
	}
	
	/**
	 * 
	* @Description: 获取主题对应的文件路径
	*
	* @param: thimeid  主题的id
	* @return：String 主题对应的文件路径
	* @author: alibao
	* @date: 2018年7月18日 上午10:20:05
	 */
	private String getFilePath(String thimeid){
		
		String sql = "select THEME_PATH from S031_ThemeList where WF_ORUNID='" + thimeid + "'";
		String filePath = Rdb.getValueBySql(sql);
		
		return filePath;
	}
	
	
}