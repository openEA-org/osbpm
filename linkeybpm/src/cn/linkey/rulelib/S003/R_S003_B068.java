package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.util.Vml2Svg;
import cn.linkey.wf.NodeUser;

/**
 * @RuleName:查看流程图中间页SVG(ShowCenter)
 * @author admin
 * @version: 8.0
 * @Created: 2014-07-05 13:44
 */
final public class R_S003_B068 implements LinkeyRule {
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
        String docUnid = BeanCtx.g("DocUnid", true);
		String chooseSql = "select FlowType from bpm_modgraphiclist where Processid = '" + processid + "'";
		String flowType = Rdb.getValueBySql(chooseSql);
		String sql = "";
		if ("2".equals(flowType)) { //添加新版jsPlumb兼容
			sql = "select DefaultCode from BPM_DevDefaultCode where  CodeType='ProcessFrameShowJSPlumb'";
		} else {
			sql = "select DefaultCode from BPM_DevDefaultCode where  CodeType='ProcessModCenterShowSvg'";
		}
        String htmlCode = Rdb.getValueBySql(sql);

        //得到文档当前的状态
        String status = "Current";
        if (Tools.isNotBlank(docUnid)) {
            sql = "select WF_Status from BPM_AllDocument where WF_OrUnid='" + docUnid + "'";
            status = Rdb.getValueBySql(sql);
        }

        //看是否读取归档表中的流程图
        String xmlBody = "";
        if (BeanCtx.getSystemConfig("ArchivedGraphic").equals("1") && status.equals("ARC")) {
            //配置了才可以
            sql = "select GraphicBody from BPM_ArchivedGraphicList where Processid='" + processid + "'"; //从归档表中拿
            xmlBody = Rdb.getValueBySql(sql);
        }

        //看xmlbody是否为空，如果为空则直接从模型中拿
        if (Tools.isBlank(xmlBody)) {
            sql = "select GraphicBody from BPM_ModGraphicList where Processid='" + processid + "'"; //直接从模型中拿
            xmlBody = Rdb.getValueBySql(sql);
        }
		
		//如果是新版流程图
		if ("2".equals(flowType)) {
			htmlCode = htmlCode.replace("{JsonBody}", xmlBody);
		}

        //对xmlbody进行解码
        xmlBody = Rdb.deCode(xmlBody, false);

        //xmlBody = Vml2Svg.getSvgXml(xmlBody); //把vml转为svg 
        // 20180109 修复升级后查看流程图svg无法显示问题。
     	if (xmlBody.contains("<v:Oval")&&xmlBody.contains("</v:Oval>")) {
     		xmlBody = Vml2Svg.getSvgXml(xmlBody);
     	}else{
     		xmlBody = Vml2Svg.getSvgXml2(xmlBody);
     	}

        htmlCode = htmlCode.replace("{XmlBody}", xmlBody);

        //文档unid为空时直接返回
        if (Tools.isBlank(docUnid)) {
            htmlCode = htmlCode.replace("{CurrentNodeid}", "");
            htmlCode = htmlCode.replace("{EndNodeList}", "");
            BeanCtx.p(htmlCode);
            return;
        }

        //获得活动的节点
        String currentNodeid = nodeUser.getCurrentNodeid(docUnid);

        //获得已结束的节点,看文档是否已经归档
        String endNodeid = "";
        if (status.equals("ARC")) {
            sql = "select Nodeid from BPM_ReportNodeList where docUnid='" + docUnid + "' and Status='End'  and NodeType<>'Process' order by StartTime";
        }
        else {
            sql = "select Nodeid from BPM_InsNodeList where docUnid='" + docUnid + "' and Status='End'  and NodeType<>'Process' order by StartTime";
        }
        endNodeid = Rdb.getValueBySql(sql);

        htmlCode = htmlCode.replace("{CurrentNodeid}", currentNodeid);
        htmlCode = htmlCode.replace("{EndNodeList}", endNodeid);
        Document userDoc = BeanCtx.getLinkeyUser().getUserDoc(BeanCtx.getUserid());
        htmlCode = htmlCode.replace("{Country}", userDoc.g("LANG").replace(",", "_"));
        BeanCtx.p(htmlCode);
    }
}