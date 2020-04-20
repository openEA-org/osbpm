package cn.linkey.rulelib.S016;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;

/**
 * @RuleName:业务数据分析
 * @author admin
 * @version: 8.0
 * @Created: 2014-10-29 17:02
 */
final public class R_S016_B019 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception {
		//params为运行本规则时所传入的参数
		String wf_orunid = BeanCtx.g("wf_orunid");
		//System.out.println(wf_orunid);
		String datasql = "select * from bpm_dataanalyse where wf_orunid='" + wf_orunid + "'";
		Document doc = Rdb.getDocumentBySql(datasql);
		String wf_addname = doc.g("wf_addname");
		String process = doc.g("process");
		String datafw = doc.g("datafw");
		String starttime = doc.g("starttime");
		String endtime = doc.g("endtime");
		String expression = doc.g("expression");
		String sortfield = doc.g("sortfield");
		String reportformname = doc.g("ReportFormName");
		String isdesc = doc.g("isdesc");
		String returnnum = doc.g("returnnum");
		String field = fieldshow(doc.g("FieldShow"));
		field = field + ",WF_OrUnid";
		String wf_addname_cn = doc.g("wf_addname_cn");
		String jsonsql;
		String ordersql = "";
		String sqlWhere = "";
		if (!Tools.isBlank(datafw)) {
			String sqldatafw = "";
			if (datafw.equals("正在流转中的")) {
				sqldatafw = "wf_status ='current'";
			} else if (datafw.equals("已结束的")) {
				sqldatafw = "wf_status= 'ARC'";
			} else if (datafw.equals("所有数据"))
				sqldatafw = "";
			if (Tools.isBlank(sqlWhere)) {
				sqlWhere = sqldatafw;
			} else {
				if (sqldatafw.length() > 1)
					sqlWhere = sqlWhere + " and " + sqldatafw;
			}
		}
		if (!Tools.isBlank(starttime) && !Tools.isBlank(endtime)) {
			if (Tools.isBlank(starttime)) {
				starttime = "2000-01-01 00:00";
			}
			if (Tools.isBlank(endtime)) {
				endtime = DateUtil.getNow("yyyy-MM-dd mm:ss");
			}
			String sSqlDate = "CONVERT(DATETIME,WF_DocCreated) between '" + starttime + "' and '" + endtime + "'";
			if (Tools.isBlank(sqlWhere)) {
				sqlWhere = sSqlDate;
			} else {
				sqlWhere = sqlWhere + " and " + sSqlDate;
			}
		}

		if (!Tools.isBlank(process)) {
			String sSqlProcess = "";
			String[] arrProcess = Tools.split(process, ",");
			for (String processid : arrProcess) {
				if (Tools.isBlank(sSqlProcess)) {
					sSqlProcess = "WF_Processname='" + processid + "'";
				} else {
					sSqlProcess = sSqlProcess + " or WF_Processname='" + processid + "'";
				}
			}
			sSqlProcess = " (" + sSqlProcess + ")";
			if (Tools.isBlank(sqlWhere)) {
				sqlWhere = sSqlProcess;
			} else {
				sqlWhere = sqlWhere + " and" + sSqlProcess;
			}
		}
		if (!Tools.isBlank(expression)) {
			if (Tools.isBlank(sqlWhere)) {
				sqlWhere = expression;
			} else {
				sqlWhere = sqlWhere + " and " + expression;
			}
		}
		if (!Tools.isBlank(sortfield)) {
			String sqlsortfield = "";
			sqlsortfield = " order by " + sortfield;

			ordersql = ordersql + sqlsortfield;

		}
		if (isdesc.equals("desc")) {
			if (!Tools.isBlank(sortfield)) {
				ordersql = ordersql + " desc";
			}

		} else if (isdesc.equals("asc")) {

			if (!Tools.isBlank(sortfield))
				ordersql = ordersql + " asc";
		}
		//String json = AppUtil.getDataGridJson("bpm_alldocument", "WF_DocNumber,Subject,WF_Status,WF_Author_CN,WF_CurrentNodeName,WF_AddName_CN,WF_DocCreated,WF_ProcessName,WF_Status", sqlWhere);
		if (!Tools.isBlank(sqlWhere)) {
			sqlWhere = " where " + sqlWhere;
		}
		if (!Tools.isBlank(returnnum)) {
			jsonsql = "select top " + returnnum + " " + field + " from bpm_alldocument  " + sqlWhere + ordersql;
		} else
			jsonsql = "select " + field + " from bpm_alldocument  " + sqlWhere + ordersql;
		//System.out.println("search sql:"+jsonsql);
		Document docs[] = Rdb.getAllDocumentsBySql(jsonsql);
		int count = Rdb.getCountBySql(jsonsql);
		printtable(docs, reportformname, wf_addname_cn, count, doc.g("FieldShow"));
		return "";
	}

	public void printtable(Document[] docs, String reportformname, String wf_addname_cn, int count, String fieldshow) {
		String itemhtml = "";
		String[] describe = Tools.split(fieldshow, "FieldDescribe\":\"");
		for (String d : describe) {
			// System.out.println("--"+d);
			if (d.indexOf("[") == -1) {
				d = d.substring(0, d.indexOf("\"}"));
				itemhtml = itemhtml + "<td style=\"text-align: center;\">" + d + "</td>";
			}

		}
		itemhtml = itemhtml + "<td style=\"text-align: center;\">查看详细</td>";
		int i = 1;
		String header = "<!DOCTYPE html><html><title>" + reportformname
				+ "</title><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">"
				+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"linkey/bpm/easyui/themes/gray/easyui.css\">"
				+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"linkey/bpm/easyui/themes/icon.css\">"
				+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"linkey/bpm/css/engine_openform.css\">"

				// 20190107 样式修复
				+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"linkey/bpm/css/app_openform.css\">"
				+ "<script type=\"text/javascript\" src=\"linkey/bpm/easyui/jquery.min.js\"></script>"
				// 20190107 END

				+ "<script type=\"text/javascript\" src=\"linkey/bpm/jscode/sharefunction.js\"></script>";
		String html = "<body><center><b style=\"font-size=10px\">" + reportformname
				+ "</b></center><table  ><tbody><tr><td style=\"padding-right:10px;font-size:15px;font-family:宋体\">报表创建者："
				+ wf_addname_cn + "    </td><td style=\"font-size:15px;font-family:宋体\">    生成时间：" + DateUtil.getNow()
				+ "</td></tr></tbody></table><table class=\"linkeytable\" data-sort=\"sortDisabled\"><tbody> <tr><td style=\"text-align: center;\">序号</td>"
				+ itemhtml;
		String lasthtml = "</tbody></table></body></html>";
		String[] fieldshowsql = Tools.split(fieldshow(fieldshow), ",");
		html = html + "</tr><tr>";
		for (Document doc33 : docs) {
			//System.out.println(doc33.getAllItemsName());
			html = html + "<td style=\"text-align: center;\">" + i + "</td>";
			i++;
			for (String s : fieldshowsql) {
				if (doc33.g(s).equals("ARC")) {
					html = html + "<td style=\"text-align: center;\">" + "已结束" + "</td>";
				} else if (doc33.g(s).equals("Current")) {
					html = html + "<td style=\"text-align: center;\">" + "正在流转中" + "</td>";
				} else
					html = html + "<td style=\"text-align: center;\">" + doc33.g(s) + "</td>";

			}
			html = html
					+ "<td style=\"text-align: center;\"><a href='' onclick=\"OpenUrl('r?wf_num=R_S003_B062&wf_docunid="
					+ doc33.g("WF_OrUnid") + "');return false;\">查看详细</a></td>";
			html = html + "</tr>";

		}

		html = html + "</tr>";
		html = html + "<tr ><td colspan=30 align=center>共统计到（" + count
				+ "）条文档 <a style=\"color:blue\" href='' onclick=\"location.href='r?wf_num=V_S016_G003';return false;\">返回</a></td></tr>";
		BeanCtx.p(header + html + lasthtml);
	}

	public String fieldshow(String json) {
		String[] string = Tools.split(json, "FieldName\":\"");
		String returnstr = "";
		for (String s : string) {
			String temp = s;
			if (temp.indexOf("[") == -1) {
				returnstr = returnstr + temp.substring(0, temp.indexOf("\"")) + ",";
			}

		}
		returnstr = returnstr.substring(0, returnstr.length() - 1);
		return returnstr;
	}
}