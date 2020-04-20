package cn.linkey.rulelib.S001;

/**
 * 打包选中文档到硬盘中
 * 
 * @author Administrator
 *
 */
import java.util.HashMap;

import cn.linkey.app.AppUtil;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;
import cn.linkey.util.XmlParser;

public class R_S001_B019 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        BeanCtx.getResponse().setContentType("text/json;charset=utf-8");

        // 获得视图的文档并得到数据源所在的数据库表名
        String gridNum = BeanCtx.g("GridNum", true); // grid编号
        if (Tools.isBlank(gridNum)) {
            BeanCtx.print("{\"msg\":\"Error:There is no GridNum parameters in the post data!\"");
            return "";
        }
        String sql = "select DataSource,GridNum,GridName from bpm_gridlist where GridNum='" + gridNum + "'";
        Document gridDoc = Rdb.getDocumentBySql(sql);
        if (gridDoc.isNull()) {
            BeanCtx.print("{\"Status\":\"error\",\"msg\":\"Error:The view does not exist!\"");
            return "";
        }
        String gridName = gridDoc.g("GridName");

        // 获得json数据源的数据库表名
        sql = "select SqlTableName from BPM_DataSourceList where Dataid='" + gridDoc.g("DataSource") + "'";
        String sqlTableName = Rdb.getValueBySql(sql);

        // 全部转换为xml字符串
        String[] docArray = Tools.split(BeanCtx.g("WF_OrUnid", true));
        StringBuilder xmlStr = new StringBuilder();
        xmlStr.append("<documents>");
        for (String docUnid : docArray) {
            Document doc = Rdb.getDocumentBySql("select * from " + sqlTableName + " where WF_OrUnid='" + docUnid + "'");
            doc.s("WF_OrTableName", sqlTableName); //文档所在数据库的表名
            xmlStr.append(doc.toXmlStr(true));
            doc.clear();
        }
        xmlStr.append("</documents>");

        // 导出到硬盘中
        String fullfilepath = AppUtil.getPackagePath();
        String fileName = gridName + "(" + DateUtil.getDateTimeNum() + ").xml";
        fullfilepath = fullfilepath + "/" + fileName;
        org.dom4j.Document xmldoc = XmlParser.string2XmlDoc(xmlStr.toString());
        XmlParser.doc2XmlFile(xmldoc, fullfilepath, "utf-8"); // 导出到硬盘的打包目录下
        BeanCtx.print("{\"Status\":\"ok\",\"fileName\":\"" + Tools.encode(fileName) + "\"}");
        return "";

    }
}
