package cn.linkey.rulelib.S009;

/**
 * 打包选中表单到硬盘中
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
import cn.linkey.util.Tools;
import cn.linkey.util.XmlParser;

public class R_S009_B003 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {

        // 全部转换为xml字符串
        int x = 0;
        String formName = "";
        String[] docArray = Tools.split(BeanCtx.g("WF_OrUnid"));
        StringBuilder xmlStr = new StringBuilder();
        xmlStr.append("<documents>");
        for (String docUnid : docArray) {
            Document doc = Rdb.getDocumentBySql("select * from BPM_FormList where WF_OrUnid='" + docUnid + "'");
            if (Tools.isBlank(formName)) {
                formName = doc.g("FormName");
            }
            doc.s("WF_OrTableName", "BPM_FormList"); //文档所在数据库的表名
            xmlStr.append(doc.toXmlStr(true));
            doc.clear();
            x++;
        }
        xmlStr.append("</documents>");
        formName += "(" + x + ")";
        // 导出到硬盘中
        String fullfilepath = AppUtil.getPackagePath();
        String fileName = formName + ".xml";
        fullfilepath = fullfilepath + fileName;
        org.dom4j.Document xmldoc = XmlParser.string2XmlDoc(xmlStr.toString());
        XmlParser.doc2XmlFile(xmldoc, fullfilepath, "utf-8"); // 导出到硬盘的outfile目录下
        BeanCtx.print("{\"Status\":\"ok\",\"fileName\":\"" + fileName + "\"}");
        return "";

    }
}
