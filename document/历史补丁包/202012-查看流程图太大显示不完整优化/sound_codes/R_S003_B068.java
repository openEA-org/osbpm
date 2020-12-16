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

     	// 20200916 add by feiyilin start ----------------
     	// 获得SVG最左边和最下边的元素的坐标和该元素实际宽、高度，从而计算SVG的实际宽、高度
     	// 计算公式如：SVG标签的height = 最下面元素的纵坐标 + 该元素的height
     	xmlBody += "<script type=\"text/javascript\" src=\"linkey/bpm/easyui/jquery.min.js\"></script>\r\n" + 
     			"<style>\r\n" + 
     			"	#div1{\r\n" + 
     			"		overflow:unset !important;\r\n" + 
     			"	}\r\n" + 
     			"</style>\r\n" + 
     			"<script>\r\n" + 
     			"	$(function(){\r\n" + 
     			"		var maxHeight = 0;\r\n" + 
     			"		var maxWidth = 0;\r\n" + 
     			"		var text = $('#svg>text');\r\n" + 
     			"		text.each(function(i,d){\r\n" + 
     			"			try{\r\n" + 
     			"				var width = d.x.baseVal[0].value;\r\n" + 
     			"				var height = d.y.baseVal[0].value;\r\n" + 
     			"				if(width >= maxWidth){\r\n" + 
     			"					// 获得该元素实际宽度\r\n" + 
     			"					var dId = d.id.replace(/_text/,'');\r\n" + 
     			"					dId = d.id.replace(/_xtb/,'');\r\n" + 
     			"					var dIdObject = $('#' + dId);\r\n" + 
     			"					if(dIdObject.attr('r') != '' && typeof(dIdObject.attr('r')) != 'undefined'){\r\n" + 
     			"						maxWidth = (width - 0) + (dIdObject.attr('r') * 2 - 0);\r\n" + 
     			"					}else if(dIdObject.attr('width') != '' && typeof(dIdObject.attr('width')) != 'undefined'){\r\n" + 
     			"						maxWidth = (width - 0) + (dIdObject.attr('width') - 0);\r\n" + 
     			"					}else{\r\n" + 
     			"						maxWidth = (width - 0) + 100;\r\n" + 
     			"					}\r\n" + 
     			"				}\r\n" + 
     			"				if(height >= maxHeight){\r\n" + 
     			"					// 获得该元素实际宽度\r\n" + 
     			"					var dId = d.id.replace(/_text/,'');\r\n" + 
     			"					dId = d.id.replace(/_xtb/,'');\r\n" + 
     			"					var dIdObject = $('#' + dId);\r\n" + 
     			"					if(dIdObject.attr('r') != '' && typeof(dIdObject.attr('r')) != 'undefined'){\r\n" + 
     			"						maxHeight = (height - 0) + (dIdObject.attr('r') * 2 - 0);\r\n" + 
     			"					}else if(dIdObject.attr('height') != '' && typeof(dIdObject.attr('height')) != 'undefined'){\r\n" + 
     			"						maxHeight = (height - 0) + (dIdObject.attr('height') - 0);\r\n" + 
     			"					}else{\r\n" + 
     			"						maxHeight = (height - 0) + 100;\r\n" + 
     			"					}\r\n" + 
     			"				}\r\n" + 
     			"			}catch(e){\r\n" + 
     			"				\r\n" + 
     			"			}\r\n" + 
     			"		})\r\n" + 
     			"		$('#svg').attr('height',maxHeight + 'px')\r\n" + 
     			"		$('#svg').attr('width',maxWidth + 'px')\r\n" + 
     			"\r\n" + 
     			"	})\r\n" + 
     			"</script>";
     	// 20200916 add by feiyilin end ----------------
     	
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