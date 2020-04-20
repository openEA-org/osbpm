package cn.linkey.rulelib.S005;

import java.util.HashMap;

import javax.servlet.http.Cookie;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:流程启动页
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-09 10:11
 */
final public class R_S005_B001 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) {
		//params为运行本规则时所传入的参数

		
		String themePath = BeanCtx.getSystemConfig("UI_theme");
		
		String ProcessTrStr = "";
		String sql = "select * from BPM_NavTreeNode where Treeid='T_S002_001'";
		Document[] dc = Rdb.getAllDocumentsBySql(sql);
		String folderStr = "";
		for (Document doc : dc) {
			ProcessTrStr = GetProcessList(doc.g("Folderid"));
			if (Tools.isNotBlank(ProcessTrStr)) {
				folderStr += "<div class=\"title\" id=\"tabs-707060\"><i id=\"title_img\"></i><ul class=\"nav nav-tabs\"><li class=\"active\"><a href=\"#panel-939343\" data-toggle=\"tab\">"
						+ doc.g("FolderName") + "</a></li></ul></div><div style=\"padding:5px\"></div>";
				folderStr += ProcessTrStr;
			}
		}
		if (Tools.isBlank(folderStr)) {
			folderStr = "未找到本应用的流程...";
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
		if (Tools.isBlank(appid)) {
			sql = "select NodeName,Processid,icons from BPM_ModProcessList where Folderid='" + folderid
					+ "' and Status='1' order by SORTNUM,WF_APPID";
		} else {
			sql = "select NodeName,Processid,icons from BPM_ModProcessList where Folderid='" + folderid
					+ "' and WF_Appid='" + appid + "' and Status='1' order by SORTNUM,WF_APPID";
		}
		Document[] dc = Rdb.getAllDocumentsBySql(sql);
		int x = 0;
		StringBuilder listStr = new StringBuilder();
		listStr.append("<div class=\"row\">");
		for (Document doc : dc) {
			if (BeanCtx.getLinkeyUser().inRoles(BeanCtx.getUserid(), doc.g("ProcessStarter"))) {
				hasProcess = true;
				x = x + 1;
				String icon = doc.g("icons");
				if (Tools.isBlank(icon)) {
					icon = "glyphicon glyphicon-file";
				}
				//获得本流程的待办总数
				String num_sql = "select count(*) from BPM_UserTodo where WF_Processid='" + doc.g("Processid")
						+ "'and Userid='" + BeanCtx.getUserid() + "'";
				String count = Rdb.getValueBySql(num_sql);
				//   listStr.append("<div class=\"col-xs-2\"><div class=\"thumbnail\">");
				listStr.append(
						"<div class=\"col-xs-2\"><div class=\"thumbnail\" onclick=\"OpenUrl('rule?wf_num=R_S003_B036&wf_processid=")
						.append(doc.g("Processid")).append("');return false;\" >");
				listStr.append(
						"<div class=\"thumbnail_bgcolor\" style=\"text-align:center;margin:0 auto;\" valign=\"middle\">");
				listStr.append("<div class=\"process_icon ").append(icon)
						.append("\" style=\"font-size:35px;color:#FFFFFF\"></div>");
				/*listStr.append("<a href='r?wf_num=V_S005_G015&Processid=").append(doc.g("Processid")).append("' ><span  style=\"background-color:#f74d4d;\" class=\"badge\">").append(count)
				        .append("</span></a></div>");*/ //20180130取消流程中心中启动流程右上角待办数量提示。
				listStr.append("</div>");
				listStr.append(
						"<div class=\"caption\" style=\"width:auto;\" align=\"center\"><a href=\"\" onclick=\"OpenUrl('rule?wf_num=R_S003_B036&wf_processid=")
						.append(doc.g("Processid")).append("');return false;\" >").append(doc.g("NodeName"))
						.append("</a></div></div></div>");
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