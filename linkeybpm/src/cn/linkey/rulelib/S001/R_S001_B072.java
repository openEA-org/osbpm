package cn.linkey.rulelib.S001;

import java.util.HashMap;

import cn.linkey.app.AppUtil;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;
import cn.linkey.util.XmlParser;

/**
 * @RuleName:通用数据WF_OrUnid打包
 * @author admin
 * @version: 8.0
 * @Created: 2015-05-21 15:57
 */
final public class R_S001_B072 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        BeanCtx.getResponse().setContentType("text/json;charset=utf-8");
        String sqlTableName = BeanCtx.g("WF_TableName");

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
        String fileName = "PackageData(" + DateUtil.getDateTimeNum() + ").xml";
        fullfilepath = fullfilepath + fileName;
        org.dom4j.Document xmldoc = XmlParser.string2XmlDoc(xmlStr.toString());
        XmlParser.doc2XmlFile(xmldoc, fullfilepath, "utf-8"); // 导出到硬盘的outfile目录下
        BeanCtx.print("{\"Status\":\"ok\",\"fileName\":\"" + Tools.encode(fileName) + "\"}");
        return "";
    }
}