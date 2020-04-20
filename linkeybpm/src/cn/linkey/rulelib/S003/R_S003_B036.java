package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;
import cn.linkey.wf.ProcessEngine;

/**
 * 打开流程文档并返回表单的HTML代码
 * 
 * @author Administrator
 *
 */
public class R_S003_B036 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> nodeParams) throws Exception {
		try {
			String docUnid = BeanCtx.g("wf_docunid", true); // 文档id
			String taskid = BeanCtx.g("wf_taskid", true); // 指定任务id
			// 兼容小写
			if (Tools.isBlank(docUnid)) {
				docUnid = BeanCtx.g("WF_DocUnid", true);
			}
			String processid = BeanCtx.g("wf_processid", true); // 流程id
			// 兼容小写
			if (Tools.isBlank(processid)) {
				processid = BeanCtx.g("WF_Processid", true);
			}
			if (Tools.isBlank(processid) && Tools.isNotBlank(docUnid)) {
				processid = Rdb
						.getValueBySql("select WF_Processid from BPM_MainData where WF_OrUnid='" + docUnid + "'");
			}

			// 检测非法字符串
			if (!Tools.isString(processid)) {
				String msg = BeanCtx.getMsg("Engine", "Error_EngineOpen");
				BeanCtx.showErrorMsg(msg);
				return "";
			}

			ProcessEngine linkeywf = new ProcessEngine();
			BeanCtx.setLinkeywf(linkeywf);
			linkeywf.init(processid, docUnid, BeanCtx.getUserid(), taskid);
			// 增加调试功能
			if (linkeywf.isDebug()) {
				BeanCtx.out("*******流程打开调试开始流程id:" + linkeywf.getProcessid() + "实例id为:" + linkeywf.getDocUnid()
						+ " *************");
			}

			String htmlBody = linkeywf.open();
			BeanCtx.print(htmlBody);

			// 增加调试功能
			if (linkeywf.isDebug()) {
				BeanCtx.out("*******流程打开调试信息输出结束*************");
			}

			// 增加操作记录
			addProcessReadLog(docUnid, processid, "阅读");
		} catch (Exception e) {
			String msg = BeanCtx.getMsg("Engine", "Error_EngineOpen");
			BeanCtx.log(e, "E", msg);
			BeanCtx.showErrorMsg(msg);
		}
		return "";
	}

	/**
	 * 记录文件阅读记录
	 */
	public static void addProcessReadLog(String docUnid, String processid, String remark) {
		if (BeanCtx.getSystemConfig("ProcessDocReadLog").equals("1")) {
			String ip = "";
			if (BeanCtx.getRequest() != null) {
				ip = BeanCtx.getRequest().getRemoteAddr();
			}
			remark = Rdb.formatArg(remark);
			String sql = "insert into BPM_AttachmentLog(WF_OrUnid,DocUnid,Userid,Processid,IP,Remark,WF_DocCreated) "
					+ "values('" + Rdb.getNewUnid() + "','" + docUnid + "','" + BeanCtx.getUserName() + "("
					+ BeanCtx.getUserid() + ")" + "','" + processid + "','" + ip + "','" + remark + "','"
					+ DateUtil.getNow() + "')";
			Rdb.execSql(sql);
		}
	}
}
