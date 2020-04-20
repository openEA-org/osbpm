package cn.linkey.rulelib.S026;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.VmlToSvg;
import cn.linkey.wf.NodeUser;

/**
 * @RuleName:仿真流程图形
 * @author admin
 * @version: 8.0
 * @Created: 2014-07-05 13:57
 */
final public class R_S026_B003 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        open();
        return "";
    }

    /**
     * 打开流程图
     */
    public void open() {
        NodeUser nodeUser = (NodeUser) BeanCtx.getBean("NodeUser");
        String processid = BeanCtx.g("Processid", true);
		
		//2018.09.04 修改对新版流程设计器仿真功能的支持
		String chooseSql = "select FlowType from bpm_modgraphiclist where Processid = '" + processid + "'";
		String flowType = Rdb.getValueBySql(chooseSql);
		String sql = "";
		if ("2".equals(flowType)) {
			sql = "select DefaultCode from BPM_DevDefaultCode where  CodeType='ProcessModSimCenterJsPlumb'";
		} else {
			sql = "select DefaultCode from BPM_DevDefaultCode where  CodeType='ProcessModSimCenter'";
		}
		String htmlCode = Rdb.getValueBySql(sql);
		
        //获得xmlbody
        sql = "select GraphicBody from BPM_ModGraphicList where Processid='" + processid + "'";
        String xmlBody = Rdb.getValueBySql(sql);
		
		//2018.08.31 修改对新版流程设计器仿真功能的支持
		if ("2".equals(flowType)) {
			htmlCode = htmlCode.replace("{JsonBody}", xmlBody);
		}
		
        xmlBody = Rdb.deCode(xmlBody, false);
        // 20180109 修复升级后查看流程图svg无法显示问题。
     	if (xmlBody.contains("<v:Oval")&&xmlBody.contains("</v:Oval>")) {
     		xmlBody = VmlToSvg.getSvgXml(xmlBody);
//     		System.out.println(xmlBody);
     	}
        
        htmlCode = htmlCode.replace("{XmlBody}", xmlBody);
        BeanCtx.p(htmlCode);
    }
}