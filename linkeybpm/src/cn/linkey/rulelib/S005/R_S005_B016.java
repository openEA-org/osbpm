package cn.linkey.rulelib.S005;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:流程启动页
 * @author  admin
 * @version: 8.0
 * @Created: 2014-05-09 10:11
 */
final public class R_S005_B016 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) {
		String themePath = BeanCtx.getSystemConfig("UI_theme");

		String ProcessTrStr = "";
		String sql = "select * from bpm_applist where isfolder='1' order by WF_APPID";
		Document[] dc = Rdb.getAllDocumentsBySql(sql);
		String folderStr = "";
		for (Document doc : dc) {
			ProcessTrStr = GetProcessList(doc.g("WF_APPID"));
			if (Tools.isNotBlank(ProcessTrStr)) {
				folderStr += "<div class=\"title\" id=\"tabs-707060\"><i id=\"title_img\"></i><ul class=\"nav nav-tabs\"><li class=\"active\"><a href=\"#panel-939343\" data-toggle=\"tab\">"
						+ doc.g("APPNAME") + "</a></li></ul></div><div style=\"padding:5px\"></div>";
				folderStr += ProcessTrStr;
			}
		}

		ProcessTrStr = GetProcessList("root");
		if (Tools.isNotBlank(ProcessTrStr)) {
			folderStr += "<div class=\"title\" id=\"tabs-707060\"><i id=\"title_img\"></i><ul class=\"nav nav-tabs\"><li class=\"active\"><a href=\"#panel-939343\" data-toggle=\"tab\">"
					+ "根目录应用</a></li></ul></div><div style=\"padding:5px\"></div>";
			folderStr += ProcessTrStr;
		}

		if (Tools.isBlank(folderStr)) {
			folderStr = "未找到应用...";
		}
		//输出页面
		BeanCtx.p("<!DOCTYPE html><html><head><title>流程启动</title>");
		BeanCtx.p(BeanCtx.getSystemConfig("BootstrapPageHeader"));
		BeanCtx.p("<script type=\"text/javascript\" src=\"linkey/bpm/jscode/sharefunction.js\"></script>");
		//BeanCtx.p("<link rel=\"stylesheet\" href=\"linkey/bpm/easyui/themes/gray/page.css\">");
		//添加主题切换
		BeanCtx.p(themePath);
		BeanCtx.p("<style>.thumbnail{min-width:60px !important;}</style>");
		BeanCtx.p("<body>");
		BeanCtx.p("<div class=\"container-fluid\"><div class=\"row clearfix\"><div class=\"col-md-12 column\">");
		BeanCtx.p(folderStr);
		BeanCtx.p("</div></div></div>");
		BeanCtx.p("</body></html>");
		return "";
	}

	public String GetProcessList(String folderid) {
		//获得所有已发布的流程列表
		boolean hasProcess = false;
		String appid = BeanCtx.g("WF_Appid", true);
		String sql = "";
		if ("root".equals(folderid)) {
			sql = "select * from bpm_applist where parentfolderid='root' and isfolder<>'1' and AppCenterFlag='1' order by SORTNUM,WF_APPID";
		} else {
			sql = "select * from bpm_applist where parentfolderid='" + folderid
					+ "' and AppCenterFlag='1' order by SORTNUM,WF_APPID";
		}

		Document[] dc = Rdb.getAllDocumentsBySql(sql);
		int x = 0;
		StringBuilder listStr = new StringBuilder();
		listStr.append("<div class=\"row\">");
		for (Document doc : dc) {
			if (BeanCtx.getLinkeyUser().inRoles(BeanCtx.getUserid(), doc.g("ProcessStarter"))) {
				hasProcess = true;
				x = x + 1;
				String icon = doc.g("InstallIcon");
				if (Tools.isBlank(icon)) {
					icon = "glyphicon glyphicon-file";
				}
				String APPNAME = doc.g("APPNAME");
				if (APPNAME.length() > 7) {
					APPNAME = APPNAME.substring(0, 7) + "...";
				}

				listStr.append("<div class=\"col-xs-2\"><div class=\"thumbnail\" onclick=\"window.open('r?wf_num="
						+ doc.g("WF_APPID") + "');return false;\" >");
				listStr.append(
						"<div class=\"thumbnail_bgcolor\" style=\"text-align:center;margin:0 auto;\" valign=\"middle\">");
				listStr.append("<div class=\"process_icon " + icon + "\"")
						.append(" style=\"font-size:35px;color:#FFFFFF;\" ></div>");

				listStr.append("</div>");
				listStr.append(
						"<div class=\"caption\" style=\"width:auto;\" align=\"center\"><a href=\"\" onclick=\"window.open('r?wf_num="
								+ doc.g("WF_APPID"))
						.append("');return false;\" >").append(APPNAME).append("</a></div></div></div>");
			}
			if (x == 6) {
				listStr.append("</div><div class=\"row\">");
				x = 0;
			}
		}
		if (hasProcess == false) {
			return "";
		} else {
			return listStr.append("</div>").toString();
		}
	}
}