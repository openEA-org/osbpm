package cn.linkey.rulelib.S001;

import java.util.HashMap;

import cn.linkey.app.AppUtil;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;
import cn.linkey.util.XmlParser;

/**
 * @RuleName:打包所有系统配置信息
 * @author admin
 * @version: 8.0
 * @Created: 2015-03-26 22:14
 */
final public class R_S001_B071 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {

        // 全部转换为xml字符串
        StringBuilder xmlStr = new StringBuilder();
        xmlStr.append("<documents>");
        String sql = "select * from BPM_SystemConfig";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        for (Document eldoc : dc) {
            eldoc.s("WF_OrTableName", "BPM_SystemConfig"); //文档所在数据库的表名
            xmlStr.append(eldoc.toXmlStr(true));
        }
        xmlStr.append("</documents>");

        // 导出到硬盘中
        String fullfilepath = AppUtil.getPackagePath();
        String fileName = "所有系统配置信息(" + DateUtil.getDateTimeNum() + ").xml";
        fullfilepath = fullfilepath + fileName;
        org.dom4j.Document xmldoc = XmlParser.string2XmlDoc(xmlStr.toString());
        XmlParser.doc2XmlFile(xmldoc, fullfilepath, "utf-8"); // 导出到硬盘的outfile目录下

        BeanCtx.print("{\"Status\":\"ok\",\"fileName\":\"" + fileName + "\"}");

        return "";

    }
}