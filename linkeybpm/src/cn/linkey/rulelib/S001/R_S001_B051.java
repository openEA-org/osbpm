package cn.linkey.rulelib.S001;

/**
 * 打包选中设计到硬盘中
 * 
 * @author Administrator
 *
 */
import java.util.HashMap;
import java.util.LinkedHashSet;

import cn.linkey.app.AppUtil;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;
import cn.linkey.util.XmlParser;

public class R_S001_B051 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {

        // 全部转换为xml字符串
        String[] docArray = Tools.split(BeanCtx.g("WF_OrUnid", true));
        StringBuilder xmlStr = new StringBuilder();
        xmlStr.append("<documents>");
        for (String docUnid : docArray) {
            String sql = "select TableName from BPM_AllElementList where WF_OrUnid='" + docUnid + "'";
            String tableName = Rdb.getValueBySql(sql);
            sql = "select * from " + tableName + " where WF_OrUnid='" + docUnid + "'";
            Document eldoc = Rdb.getDocumentBySql(sql);
            eldoc.s("WF_OrTableName", tableName); //文档所在数据库的表名
            xmlStr.append(eldoc.toXmlStr(true));

            //如果是流程则需要追加所有的流程文档
            if (tableName.equalsIgnoreCase("BPM_ModProcessList")) {
                addProcessDoc(eldoc.g("Processid"), xmlStr);
            }

            //如果是导航树也需要打包导航树中的
            if (tableName.equalsIgnoreCase("BPM_NavTreeList")) {
                addNavTreeNode(eldoc.g("Treeid"), xmlStr);
            }

            eldoc.clear();

        }
        xmlStr.append("</documents>");

        // 导出到硬盘中
        String fullfilepath = AppUtil.getPackagePath();
        String fileName = "所有设计元素(" + DateUtil.getDateTimeNum() + ").xml";
        fullfilepath = fullfilepath + fileName;
        org.dom4j.Document xmldoc = XmlParser.string2XmlDoc(xmlStr.toString());
        XmlParser.doc2XmlFile(xmldoc, fullfilepath, "utf-8"); // 导出到硬盘的outfile目录下
        BeanCtx.print("{\"Status\":\"ok\",\"fileName\":\"" + fileName + "\"}");
        return "";

    }

    /**
     * 打包导航树的所有配置节点
     * 
     * @param treeid
     * @param xmlStr
     */
    public void addNavTreeNode(String treeid, StringBuilder xmlStr) {
        String sql = "select * from BPM_NavTreeNode where treeid='" + treeid + "'";
        LinkedHashSet<Document> subdc = Rdb.getAllDocumentsSetBySql(sql);
        for (Document doc : subdc) {
            doc.s("WF_OrTableName", "BPM_NavTreeNode"); //文档所在数据库的表名
            xmlStr.append(doc.toXmlStr(true));
        }
    }

    /**
     * 打包流程节点文档
     * 
     * @param processid
     * @param xmlStr
     * @return
     */
    public void addProcessDoc(String processid, StringBuilder xmlStr) {

        // 获得流程模型的所有数据库表清
        String tableList = BeanCtx.getSystemConfig("ProcessModTableList");
        String[] tableArray = Tools.split(tableList);

        // 全部转换为xml字符串
        for (String tableName : tableArray) {
            String sql = "select * from " + tableName + " where Processid='" + processid + "'";
            LinkedHashSet<Document> subdc = Rdb.getAllDocumentsSetBySql(sql);
            for (Document doc : subdc) {
                doc.s("WF_OrTableName", tableName); //文档所在数据库的表名
                xmlStr.append(doc.toXmlStr(true));
            }
        }

    }

}
