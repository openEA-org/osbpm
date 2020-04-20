package cn.linkey.rulelib.S002;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.wf.ModNode;

/**
 * @RuleName:流程节点属性
 * @author admin
 * @version: 8.0
 * @Created: 2014-04-30 11:15
 */
final public class R_S002_B002 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数

        //获得环节的名称以及unid号
        ModNode modNode = (ModNode) BeanCtx.getBean("ModNode");
        String processid = BeanCtx.g("Processid", true);
        String nodeid = BeanCtx.g("Nodeid", true);
        String extNodeType = BeanCtx.g("ExtNodeType", true);
        String appid = BeanCtx.g("WF_Appid", true);
        if (Tools.isNotBlank(appid)) {
            appid = "&WF_Appid=" + appid;
        }
        String nodeTableName = modNode.getNodeTableName(processid, nodeid); //节点所在数据库表
        String sql = "select * from " + nodeTableName + " where Processid='" + processid + "' and Nodeid='" + nodeid + "'";
        Document nodeDoc = Rdb.getDocumentBySql(sql);
        String docUnid = "";
        String nodeName = "节点属性";
        if (!nodeDoc.isNull()) {
            docUnid = nodeDoc.getDocUnid();
            nodeName = nodeDoc.g("NodeName");
        }

        //获得环节配置的插件列表
        if (Rdb.getDbType().equals("MSSQL")) {
            sql = "select * from BPM_EngineNodeAttrPluginConfig where (','+NodeType+',' like '%," + extNodeType + ",%' or NodeType='*') and Status='1' order by SortNum";
        }
        else if (Rdb.getDbType().equals("MYSQL")) {
            sql = "select * from BPM_EngineNodeAttrPluginConfig where (concat(',',NodeType,',') like '%," + extNodeType + ",%' or NodeType='*') and Status='1' order by SortNum";
        }
        else {
            sql = "select * from BPM_EngineNodeAttrPluginConfig where (','||NodeType||',' like '%," + extNodeType + ",%' or NodeType='*') and Status='1' order by SortNum";
        }
        Document[] dc = Rdb.getAllDocumentsBySql(sql);

        StringBuilder tabStr = new StringBuilder();
        for (Document doc : dc) {
            String pluginid = doc.g("Pluginid");
            String pluginName = doc.g("PluginName");
            String pluginUrl = doc.g("url");
            if (pluginUrl.indexOf("?") == -1) {
                pluginUrl += "?";
            }
            pluginUrl += "&WF_Action=edit&WF_DocUnid=" + docUnid + "&Processid=" + processid + "&Nodeid=" + nodeid + "&NodeType=" + extNodeType + appid;
            tabStr.append("<div title=\"" + pluginName + "\" url=\"" + pluginUrl + "\" id=\"" + pluginid + "\" iconCls=\"" + doc.g("iconCls")
                    + "\"  style=\"padding:0px;overflow:hidden\"><iframe src=\"about:blank\" id=\"iframe_" + pluginid + "\" frameborder='0' style=\"width:100%;height:100%\"></iframe></div>");
        }

        //获得缺省html代码
        sql = "select DefaultCode from BPM_DevDefaultCode where  CodeType='ProcessNodeAtrrEditor'";
        String htmlCode = Rdb.getValueBySql(sql);
        htmlCode = htmlCode.replace("{NodeName}", nodeName);
        htmlCode = htmlCode.replace("{processid}", processid);
        htmlCode = htmlCode.replace("{Nodeid}", nodeid);
        htmlCode = htmlCode.replace("{ExtNodeType}", extNodeType);
        htmlCode = htmlCode.replace("{TabHtml}", tabStr.toString());

        BeanCtx.p(htmlCode);

        return "";
    }
}