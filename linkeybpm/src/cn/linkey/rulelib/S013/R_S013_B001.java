package cn.linkey.rulelib.S013;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.factory.IIIIILIIIIIIIII;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * 版权信息页
 * @author alibao
 *
 */
final public class R_S013_B001 implements LinkeyRule {

	@Override
	public String run(HashMap<String, Object> params) throws Exception {
		// params为运行本规则时所传入的参数

		String sql = "select * from BPM_SystemInfo";
		Document doc = Rdb.getDocumentBySql(sql);
		String ctdName = doc.g("CtdName");
		String copyRight = doc.g("CopyRight");
		copyRight = IIIIILIIIIIIIII.L1IIIL1L1IIIIIIIIIILLLIIIIIIIIIIIIIIIIIIIIIILLL(copyRight);
		ctdName = IIIIILIIIIIIIII.L1IIIL1L1IIIIIIIIIILLLIIIIIIIIIIIIIIIIIIIIIILLL(ctdName);
		sql = "select count(*) as TotalNum from BPM_ModProcessList";
		String processNum = Rdb.getValueBySql(sql);
		sql = "select count(*) as TotalNum from BPM_OrgUserList";
		String userNum = Rdb.getValueBySql(sql);

		// 20180323 添加oracle数据库名/实例名在首页信息中显示
		String databaseName = Rdb.getConnection().getCatalog();
		if ("ORACLE".equals(Rdb.getDbType())) {
			String sqlqName = "select instance_name from v$instance"; // 查询oracle数据库的实例名
			databaseName = Rdb.getValueBySql(sqlqName);
		}

		if (Tools.isBlank(ctdName)) {
			ctdName = "警告:系统没有注册序列号,请联系开发商!";
		}
		String htmlStr = "<!DOCTYPE html><html><head><title>CopyRight</title>"
				+ BeanCtx.getSystemConfig("AppPageHtmlHeader")
				+ "</head><body><table id='sysinfo' border='0' cellspacing='1' cellpadding='2' width='90%' height='500'><tbody>"
				+ "<tr valign='top'><td width='10%'><img border='0' style=\"margin-top: 30px;\" alt='' src='linkey/bpm/images/process/bpmadmin.png' /></td>"
				+ "<td style='PADDING-LEFT: 40px; PADDING-TOP: 40px' width='90%'>"
				+ " 系统共有<span style='FONT-WEIGHT: bold'>(" + userNum + ")</span>位注册用户 "
				+ "<span style='FONT-WEIGHT: bold'>(" + processNum + ")</span>个流程<br />"
				+ "<br/>本软件仅授权给：<span style='FONT-WEIGHT: bold'>" + Tools.decode(ctdName) + "</span><br /><br /> "
				+ "版权所有：" + Tools.decode(copyRight) + "<br /><br /><p style=\"color: red;font-weight: 800;\">为确保获得更专业的技术支持及更多的价值服务，建议致电 020-8555 8158 升级相关服务</p><br />" + "系统版本：" + doc.g("WF_Version") + "<br /><br />"
				+ "服务器IP：" + Tools.getServerIP() + "<br /><br />" + "数据库："
				//20180323 添加oracle数据库名/实例名在首页信息中显示
				+ Rdb.getConnection().getMetaData().getDatabaseProductName() + "(" + databaseName + ")" + "<br /><br />"
				+ "</div></td></tr></tbody></table></body></html>"; 

		BeanCtx.p(htmlStr);

		return "";
	}
}