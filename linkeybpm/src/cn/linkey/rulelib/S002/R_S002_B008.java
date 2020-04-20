package cn.linkey.rulelib.S002;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.wf.ModNode;

/**
 * @RuleName:所有处理单按扭
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-03 22:48
 */
final public class R_S002_B008 implements LinkeyRule {
    
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        ModNode modNode = (ModNode) BeanCtx.getBean("ModNode");
        String processid = BeanCtx.g("Processid");
        String nodeid = BeanCtx.g("Nodeid");

        //获得节点文档和已经配置好的按扭
        String nodeTableName = modNode.getNodeTableName(processid, nodeid); //节点所在数据库表
        String sql = "select * from " + nodeTableName + " where Processid='" + processid + "' and Nodeid='" + nodeid + "'";
        Document nodeDoc = Rdb.getDocumentBySql(sql);
        String toolbarList = nodeDoc.g("ToolbarList");
        HashMap<String, String> nodeToolbarMap = new HashMap<String, String>();
        if (Tools.isNotBlank(toolbarList)) {
            String[] toolbarArray = Tools.split(toolbarList);
            for (String toolbarid : toolbarArray) {
                int spos = toolbarid.indexOf("|");
                String cusToolbarName = "";
                if (spos != -1) {
                    cusToolbarName = toolbarid.substring(spos + 1);
                    toolbarid = toolbarid.substring(0, spos);
                }
                nodeToolbarMap.put(toolbarid, cusToolbarName);
            }
        }

        //获得所有按扭配置信息并输出json
        //20180903 change by alibao 修复按钮重复
        //sql = "select ToolbarName,Toolbarid,SortNum from BPM_EngineButtonConfig where Country='CN' order by SortNum";
        sql = "select distinct ToolbarName,Toolbarid,SortNum from BPM_EngineButtonConfig where Country='CN' order by ToolBarid";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : dc) {
            String toolbarid = doc.g("Toolbarid");
            String cusToolbarName = nodeToolbarMap.get(toolbarid);
            if (cusToolbarName != null) {
                doc.s("CusToolbarName", cusToolbarName);
                doc.s("Status", "true");
            }
            if (nodeToolbarMap.size() == 0) {
                //only node not config button
                if (toolbarid.equals("BU1001") || toolbarid.equals("BU1002") || toolbarid.equals(",BU1005") || toolbarid.equals("BU1022")) {
                    doc.s("Status", "true");
                }
            }
        }

        String jsonStr = Documents.dc2json(dc, "rows", true);
        BeanCtx.p(jsonStr);

        return "";
    }
}