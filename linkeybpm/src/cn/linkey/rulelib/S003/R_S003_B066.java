package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.factory.IIIIILIIIIIIIII;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;

/**
 * @RuleName:查看流程图首页(frameShow)
 * @author admin
 * @version: 8.0
 * @Created: 2014-07-05 13:35
 */
final public class R_S003_B066 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) {
		// params为运行本规则时所传入的参数

//		String userAgent = BeanCtx.getRequest().getHeader("user-agent");
		String codeType = "";
		/*if (userAgent.indexOf("MSIE") != -1 || userAgent.indexOf("Trident") != -1) {
			codeType = "ProcessFrameShow";
		} else {
			codeType = "ProcessFrameShowSvg";
		}*/
		codeType = "ProcessFrameShowSvg"; // 20180109 change  // 修复升级后查看流程图svg无法显示问题。

		if (BeanCtx.isMobile()) {
			codeType = codeType + "_Mobile";
		}
		open(codeType);
		return "";
	}

	/**
	 * 打开流程ie浏览器
	 */
	public void open(String codeType) {
		String processid = BeanCtx.g("Processid", true);
		String docUnid = BeanCtx.g("DocUnid", true);
		String processName = "", sql = "";
		if (Tools.isBlank(processid)) {
			processid = Rdb.getNewid("");
			processName = "查看流程图";
		} else {
			sql = "select * from BPM_ModProcessList where Processid='" + processid + "'";
			Document doc = Rdb.getDocumentBySql(sql);
			if (doc.isNull()) {
				BeanCtx.p("流程id(" + processid + ")不存在!");
				return;
			} else {
				processName = doc.g("NodeName");
			}
		}

		sql = "select * from BPM_SystemInfo";
		Document doc = Rdb.getDocumentBySql(sql);
		String ctdName = doc.g("CtdName");
		ctdName = IIIIILIIIIIIIII.L1IIIL1L1IIIIIIIIIILLLIIIIIIIIIIIIIIIIIIIIIILLL(ctdName);

		sql = "select DefaultCode from BPM_DevDefaultCode where  CodeType='" + codeType + "'";
		// BeanCtx.out(sql);
		String htmlCode = Rdb.getValueBySql(sql);
		htmlCode = htmlCode.replace("{logoinfo}", Tools.decode(ctdName));
		htmlCode = htmlCode.replace("{UserName}", BeanCtx.getUserName());
		htmlCode = htmlCode.replace("{Date}", DateUtil.getNow("yyyy-MM-dd"));
		htmlCode = htmlCode.replace("{ProcessName}", processName);
		htmlCode = htmlCode.replace("{Processid}", processid);
		htmlCode = htmlCode.replace("{DocUnid}", docUnid);
		Document userDoc = BeanCtx.getLinkeyUser().getUserDoc(BeanCtx.getUserid());
		htmlCode = htmlCode.replace("{Country}", userDoc.g("LANG").replace(",", "_"));
		BeanCtx.p(htmlCode);
	}
}