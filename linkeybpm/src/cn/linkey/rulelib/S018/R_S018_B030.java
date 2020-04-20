package cn.linkey.rulelib.S018;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.factory.IIIIILIIIIIIIII;
import cn.linkey.rest.RestUtil;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;

/**
 * 
* Copyright © 2018 A Little Bao. All rights reserved.
* 
* @ClassName: R_S018_B030.java
* @Description: 获取或保存流程图JSON数据
*
* @version: v1.0.0
* @author: alibao
* @date: 2018年8月9日 上午10:18:56 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2018年8月9日     alibao           v1.0.0               修改原因
 */
public class R_S018_B030 implements LinkeyRule {

	@Override
	public String run(HashMap<String, Object> params) throws Exception {

		String action = BeanCtx.g("WF_Action");
		if (action.equals("save")) {
			setFlowJSON();
		} else {
			getFlowJSON();
		}

		return "";
	}

	/**
	 * 
	* @Description: 保存流程图JSON数据
	*
	* @author: alibao
	* @date: 2018年8月9日 上午10:58:39
	 */
	private void setFlowJSON() {
		String processid = BeanCtx.g("Processid", true);
        String flowJSON = BeanCtx.g("flowJSON");
        String sql = "select Processid from BPM_ModProcessList where Processid='" + processid + "'";
        if (Rdb.hasRecord(sql)) {
            sql = "select * from BPM_ModGraphicList where Processid='" + processid + "'";
            Document doc = Rdb.getDocumentBySql(sql);
            doc.s("GraphicBody", flowJSON);
            doc.s("Processid", processid);
            doc.s("FlowType", "2");
            doc.save();
			BeanCtx.p(RestUtil.formartResultJson("1", "save", flowJSON));
        }
        else {
            BeanCtx.p(RestUtil.formartResultJson("0", "请在空白处点击键并在过程属性中指定流程的名称!"));
        }

	}

	/**
	 * 
	* @Description: 获取流程图JSON数据
	*
	* @author: alibao
	* @date: 2018年8月9日 上午10:56:25
	 */
	private void getFlowJSON() {

		String processid = BeanCtx.g("Processid", true);

		String processName = "", sql = "";
		if (Tools.isBlank(processid)) {
			processid = Rdb.getNewid("");
			processName = "新建流程";
		} else {
			sql = "select * from BPM_ModProcessList where Processid='" + processid + "'";
			Document doc = Rdb.getDocumentBySql(sql);
			if (doc.isNull()) {
				BeanCtx.p(RestUtil.formartResultJson("0", "流程(" + processid + ")不存在!"));
				return;
			} else {
				processName = doc.g("NodeName");
			}
		}

		//		        sql = "select * from BPM_SystemInfo";
		//		        Document doc = Rdb.getDocumentBySql(sql);
		//		        String ctdName = doc.g("CtdName");
		//		        ctdName = IIIIILIIIIIIIII.L1IIIL1L1IIIIIIIIIILLLIIIIIIIIIIIIIIIIIIIIIILLL(ctdName);
		//		        String startDate = IIIIILIIIIIIIII.L1IIIL1L1IIIIIIIIIILLLIIIIIIIIIIIIIIIIIIIIIILLL(doc.g("EndDate"));
		//		        if (Tools.isBlank(startDate) || Tools.isBlank(ctdName)) {
		////		            BeanCtx.p("<script>alert(\"系统已过期请联系开发商解决此问题!\");top.close();</script>");
		//		        	BeanCtx.p(RestUtil.formartResultJson("0", "系统已过期请联系开发商解决此问题!"));
		//		            return;
		//		        }
		//		        if (DateUtil.lessTime(startDate + " 00:00", DateUtil.getNow())) {
		////		            BeanCtx.p("<script>alert(\"系统已过期请联系开发商解决此问题!\");top.close();</script>");
		//		        	BeanCtx.p(RestUtil.formartResultJson("0", "系统已过期请联系开发商解决此问题!"));
		//		        }
		//		        else {
		String sql1 = "select GraphicBody from bpm_modgraphiclist where Processid='" + processid + "' and FlowType='2'";
		String flowJSON = Rdb.getValueBySql(sql1);

//		BeanCtx.p(RestUtil.formartResultJson("1", processName, flowJSON));
		BeanCtx.p("{\"Status\":\"1\",\"msg\":\"" + processName + "\",\"flowJSON\": " + (Tools.isBlank(flowJSON)?"\"\"":flowJSON) + ",\"Processid\": \"" + processid + "\" }");
		//		        }

	}

}
