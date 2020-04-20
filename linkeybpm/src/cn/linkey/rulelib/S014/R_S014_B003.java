package cn.linkey.rulelib.S014;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Vml2Svg;
import cn.linkey.util.VmlToSvg;
import cn.linkey.wf.NodeUser;

/**
 * @RuleName:流程图形监控
 * @author admin
 * @version: 8.0
 * @Created: 2014-07-05 13:57
 */
final public class R_S014_B003 implements LinkeyRule {
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

        String userAgent = BeanCtx.getRequest().getHeader("user-agent");
        //		BeanCtx.out(BeanCtx.getRequest().getHeader("user-agent"));
        String codeType = "";
//        if (userAgent.indexOf("MSIE") != -1 || userAgent.indexOf("Trident") != -1) {
//            codeType = "ProcessModMonitorCenter";
//        }
//        else {
//            codeType = "ProcessModMonitorCenterSvg";
//        }
        //20180514 使用SVG的方式显示
        codeType = "ProcessModMonitorCenterSvg";

        NodeUser nodeUser = (NodeUser) BeanCtx.getBean("NodeUser");
        String processid = BeanCtx.g("Processid", true);
        String docUnid = BeanCtx.g("DocUnid", true);
		
		//2018.08.31 修改对新版流程设计器监控功能的支持
		String chooseSql = "select FlowType from bpm_modgraphiclist where Processid = '" + processid + "'";
		String flowType = Rdb.getValueBySql(chooseSql);
		String sql = "";
		if ("2".equals(flowType)) {
			sql = "select DefaultCode from BPM_DevDefaultCode where  CodeType='ProcessModMonitorCenterJsPlumb'";
		} else {
			sql = "select DefaultCode from BPM_DevDefaultCode where  CodeType='" + codeType + "'";
		}
		
        String htmlCode = Rdb.getValueBySql(sql);
        //获得xmlbody
        sql = "select GraphicBody from BPM_ModGraphicList where Processid='" + processid + "'";
        String xmlBody = Rdb.getValueBySql(sql);
        
		//2018.08.31 修改对新版流程设计器监控功能的支持
		if ("2".equals(flowType)) {
			htmlCode = htmlCode.replace("{JsonBody}", xmlBody);
		}
		
		xmlBody = Rdb.deCode(xmlBody, false);
        //20180514 对流程图判断，兼容旧版vml
        if (xmlBody.contains("<v:Oval")&&xmlBody.contains("</v:Oval>")) {
     		xmlBody = VmlToSvg.getSvgXml(xmlBody);
     	}
        //System.out.println(xmlBody);
//        if (codeType.equals("ProcessModMonitorCenterSvg")) {
//            xmlBody = Vml2Svg.getSvgXml(xmlBody); //把vml转为svg
//        }

        htmlCode = htmlCode.replace("{XmlBody}", xmlBody);
        //获得活动的节点
        String currentNodeid = nodeUser.getCurrentNodeid(docUnid);
        //获得已结束的节点
        sql = "select Nodeid from BPM_InsNodeList where docUnid='" + docUnid + "' and Status='End'  and NodeType<>'Process' order by StartTime";
        String endNodeid = Rdb.getValueBySql(sql);

        htmlCode = htmlCode.replace("{CurrentNodeid}", currentNodeid);
        htmlCode = htmlCode.replace("{EndNodeList}", endNodeid);
        Document userDoc = BeanCtx.getLinkeyUser().getUserDoc(BeanCtx.getUserid());
        htmlCode = htmlCode.replace("{Country}", userDoc.g("LANG").replace(",", "_"));
        BeanCtx.p(htmlCode);
    }
}