package cn.linkey.rulelib.S002;

/**
 * 打包选中流程到硬盘中
 * 
 * @author Administrator
 *
 */
import java.util.HashMap;
import java.util.LinkedHashSet;

import cn.linkey.app.AppUtil;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.util.XmlParser;

public class R_S002_B024 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {

        LinkedHashSet<Document> dc = new LinkedHashSet<Document>();

        // 全部转换为xml字符串
        String processName = "";
        int p = 0;
        String[] docArray = Tools.split(BeanCtx.g("DocUnid"));
        String processid = BeanCtx.g("Processid");

        //先获得流程的名称
        if (Tools.isNotBlank(processid)) {
            String sql = "select NodeName from BPM_ModProcessList where Processid='" + processid + "'";
            processName = Rdb.getValueBySql(sql);
        }
        else {
            processName = "未找到流程";
        }

        for (String docUnid : docArray) {
            String sql = "select Processid from BPM_ModProcessList where WF_OrUnid='" + docUnid + "'";
            String tmpProcessid = Rdb.getValueBySql(sql);
            if (Tools.isNotBlank(tmpProcessid)) {
                //说明是流程
                dc.addAll(AppUtil.getAllModDocByProcessid(tmpProcessid));
            }
            else {
                //说明是关联设计
                sql = "select TableName from BPM_AllElementList where WF_OrUnid='" + docUnid + "'";
                String tableName = Rdb.getValueBySql(sql);
                if (Tools.isNotBlank(tableName)) {
                    Document eldoc = Rdb.getDocumentBySql("select * from " + tableName + " where WF_OrUnid='" + docUnid + "'");
                    eldoc.s("WF_OrTableName", tableName);
                    dc.add(eldoc);
                }
            }
        }

        String xmlStr = Documents.dc2XmlStr(dc, true);

        // 导出到硬盘中
        String fullfilepath = AppUtil.getPackagePath();
        String fileName = processName + ".xml";
        fullfilepath = fullfilepath + fileName;
        org.dom4j.Document xmldoc = XmlParser.string2XmlDoc(xmlStr);
        XmlParser.doc2XmlFile(xmldoc, fullfilepath, "utf-8"); // 导出到硬盘的outfile目录下
        BeanCtx.print("{\"Status\":\"ok\",\"fileName\":\"" + fileName + "\"}");
        return "";

    }
}