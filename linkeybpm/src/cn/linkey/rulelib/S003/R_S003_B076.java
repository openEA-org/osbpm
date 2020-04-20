package cn.linkey.rulelib.S003;

import java.util.*;

import cn.linkey.dao.*;
import cn.linkey.util.*;
import cn.linkey.doc.*;
import cn.linkey.factory.*;
import cn.linkey.wf.*;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:查看流程图，系统集成时专用,兼容IE和非IE均可
 * @author admin
 * @version: 8.0
 * @Created: 2014-07-05 13:35
 */
final public class R_S003_B076 implements LinkeyRule {
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
        String configid = "ProcessModCenterShowSvg";
        boolean isIE = false;
        String userAgent = BeanCtx.getRequest().getHeader("user-agent");
        if (userAgent.indexOf("MSIE") != -1 || userAgent.indexOf("Trident") != -1) {
            configid = "ProcessModCenterShow";
            isIE = true;
        }
        String sql = "select DefaultCode from BPM_DevDefaultCode where  CodeType='" + configid + "'";
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

        //对xmlbody进行解码
        xmlBody = Rdb.deCode(xmlBody, false);
        if (!isIE) {
            xmlBody = Vml2Svg.getSvgXml(xmlBody); //把vml转为svg
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
        if (status.equals("ARC")) {
            sql = "select Nodeid from BPM_ReportNodeList where docUnid='" + docUnid + "' and Status='End'  and NodeType<>'Process' order by StartTime";
        }
        else {
            sql = "select Nodeid from BPM_InsNodeList where docUnid='" + docUnid + "' and Status='End'  and NodeType<>'Process' order by StartTime";
        }
        String endNodeid = Rdb.getValueBySql(sql);

        htmlCode = htmlCode.replace("{CurrentNodeid}", currentNodeid);
        htmlCode = htmlCode.replace("{EndNodeList}", endNodeid);
        Document userDoc = BeanCtx.getLinkeyUser().getUserDoc(BeanCtx.getUserid());
        htmlCode = htmlCode.replace("{Country}", userDoc.g("LANG").replace(",", "_"));
        if (isIE) {
            htmlCode = htmlCode.replace("ecflow_show.js", "ecflow_show_sys.js"); //集成专用
        }
        else {
            htmlCode = htmlCode.replace("ecflow_showsvg.js", "ecflow_showsvg_sys.js"); //集成专用
        }
        BeanCtx.p(htmlCode);
    }
}