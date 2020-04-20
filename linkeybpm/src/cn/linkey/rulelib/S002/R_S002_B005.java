package cn.linkey.rulelib.S002;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.factory.BeanCtx;
import cn.linkey.factory.IIIIILIIIIIIIII;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;
import cn.linkey.util.VmlToSvg;

/**
 * @RuleName:图形建模中间页
 * @author admin
 * @version: 8.0
 * @Created: 2014-04-28 22:54
 */
final public class R_S002_B005 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) {
		// params为运行本规则时所传入的参数
		open();
		return "";
	}

	/**
	 * 打开流程图
	 */
	public void open() {
		String processid = BeanCtx.g("Processid", true);
		String sql = "select DefaultCode from BPM_DevDefaultCode where  CodeType='ProcessModCenter'";
		String htmlCode = Rdb.getValueBySql(sql);
		String startDate = IIIIILIIIIIIIII.L1IIIL1L1IIIIIIIIIILLLIIIIIIIIIIIIIIIIIIIIIILLL(
				Rdb.getValueBySql("select EndDate from BPM_SystemInfo"));
		if (Tools.isBlank(startDate)) {
			htmlCode = htmlCode.replace("id=PolyLine1", "id=PolyLinel");
			return;
		}

		// 获得xmlbody
		sql = "select GraphicBody from BPM_ModGraphicList where Processid='" + processid + "'";
		// BeanCtx.out("sql="+sql);
		String xmlBody = Rdb.getValueBySql(sql);
		if (DateUtil.lessTime(startDate + " 00:00", DateUtil.getNow()) || Tools.isBlank(startDate)) {
			htmlCode = htmlCode.replace("id=PolyLine1", "id=PolyLinel");
		}
		xmlBody = Rdb.deCode(xmlBody, false);

		// 20171017 对旧标签的转换
		if (xmlBody.contains("<v:Oval")&&xmlBody.contains("</v:Oval>")) {
			xmlBody = VmlToSvg.getSvgXml(xmlBody);
			//System.out.println(xmlBody);
		}
		
		htmlCode = htmlCode.replace("{XmlBody}", xmlBody);
		BeanCtx.p(htmlCode);
	}

}