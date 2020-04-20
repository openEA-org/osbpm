package cn.linkey.rulelib.S030;

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

public class R_S030_B009 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {

        // 全部转换为xml字符串
        String processName = "";
        int p = 0;
        String[] processidArray = Tools.split(BeanCtx.g("Processid"));
        // 先获得第一个流程的名称
        if (processidArray.length > 0) {
            String sql = "select NodeName from BPG_ModProcessList where Processid='" + processidArray[0] + "'";
            processName = Rdb.getValueBySql(sql);
        }
        LinkedHashSet<Document> dc = new LinkedHashSet<Document>();
        for (String processid : processidArray) {
            dc.addAll(getAllModDocByProcessid(processid));
            p++;
        }
        processName += "(" + p + ")"; // 包含流程数
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

    /**
     * 根据流程id获得所有流程模型文档
     * 
     * @param processid 流程id号
     * @return 返回流程所有节点的文档集合
     */
    public static LinkedHashSet<Document> getAllModDocByProcessid(String processid) {
        String tableList = "BPG_ModEventList,BPG_ModGatewayList,BPG_ModGraphicList,BPG_ModProcessList,BPG_ModSequenceFlowList,BPG_ModSubProcessList,BPG_ModTaskList";
        String[] tableArray = Tools.split(tableList);
        LinkedHashSet<Document> dc = new LinkedHashSet<Document>();
        for (String tableName : tableArray) {
            String sql = "select * from " + tableName + " where Processid='" + processid + "'";
            LinkedHashSet<Document> subdc = Rdb.getAllDocumentsSetBySql(sql);
            for (Document doc : subdc) {
                doc.s("WF_OrTableName", tableName); // 文档所在数据库的表名
            }
            dc.addAll(subdc);
        }
        return dc;
    }

}