package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.wf.ProcessUtil;

/**
 * @RuleName:一键删除测试数据
 * @author admin
 * @version: 8.0
 * @Created: 2015-03-26 22:54
 */
final public class R_S003_B075 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception {
		//params为运行本规则时所传入的参数

		//String sql = "select WF_OrUnid from bpm_maindata where WF_Systemid='TEST'";

		// 20181229 修复不能删除已归档的测试数据的问题
		String sql = "select WF_OrUnid from bpm_alldocument where WF_Systemid='TEST'";
		// 20181229 END

		Document[] dc = Rdb.getAllDocumentsBySql(sql);
		int i = 0;
		for (Document doc : dc) {
			i++;
			ProcessUtil.deleteMainDocument(doc.g("WF_OrUnid"));
		}

		BeanCtx.p(Tools.jmsg("ok", "共清除(" + i + ")条测试数据"));

		return "";
	}
}