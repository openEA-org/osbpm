package cn.linkey.rulelib.S001;

/**
 * 打包选导航树并下载
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

public class R_S001_B052 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {

        // 全部转换为xml字符串
        String[] docArray = Tools.split(BeanCtx.g("WF_OrUnid", true));
        StringBuilder xmlStr = new StringBuilder();
        xmlStr.append("<documents>");
        for (String docUnid : docArray) {
            Document doc = Rdb.getDocumentBySql("select * from BPM_NavTreeList where WF_OrUnid='" + docUnid + "'");
            doc.s("WF_OrTableName", "BPM_NavTreeList"); //文档所在数据库的表名
            xmlStr.append(doc.toXmlStr(true));
            addNavTreeNode(doc.g("Treeid"), xmlStr);
            doc.clear();
        }
        xmlStr.append("</documents>");

        // 导出到硬盘中
        String fullfilepath = AppUtil.getPackagePath();
        String fileName = "导航树(" + DateUtil.getDateTimeNum() + ").xml";
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

}
