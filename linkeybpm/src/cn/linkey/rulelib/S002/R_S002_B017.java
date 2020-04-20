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

public class R_S002_B017 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {

        // 全部转换为xml字符串
        String processName = "";
        int p = 0;
        String[] processidArray = Tools.split(BeanCtx.g("Processid"));
        //先获得第一个流程的名称
        if (processidArray.length > 0) {
            String sql = "select NodeName from BPM_ModProcessList where Processid='" + processidArray[0] + "'";
            processName = Rdb.getValueBySql(sql);
        }
        LinkedHashSet<Document> dc = new LinkedHashSet<Document>();
        for (String processid : processidArray) {
            dc.addAll(AppUtil.getAllModDocByProcessid(processid));
            p++;
        }
        processName += "(" + p + ")"; //包含流程数
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
