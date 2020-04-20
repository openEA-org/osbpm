package cn.linkey.rulelib.S001;

import java.util.HashMap;
import java.util.LinkedHashSet;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * 搜索所有设计元素
 * 
 * @author Administrator
 *
 */
public class R_S001_B023 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        BeanCtx.getResponse().setContentType("text/json;charset=utf-8");
        String keyword = Tools.decodeUrl(BeanCtx.g("keyword", true));
        String tableList = BeanCtx.g("tableList", true);
        String qryappid = BeanCtx.g("WF_Appid", true);

        String[] tableArray = Tools.split(tableList);
        LinkedHashSet<Document> resultDc = new LinkedHashSet<Document>();
        for (String tableName : tableArray) {
            //针对每一张表要进行全文搜索，转成json对像后进行比较
            String sql = "select * from " + tableName;
            if (Tools.isNotBlank(qryappid)) {
                sql = sql + " where WF_Appid='" + qryappid + "'";
            }
            Document[] dc = Rdb.getAllDocumentsBySql(sql);
            for (Document doc : dc) {
                String jsonStr = doc.toJson();
                if (jsonStr.indexOf(keyword) != -1) {
                    //说明找到了文档
                    String elementType = "";
                    String elappid = doc.g("WF_Appid");
                    String elementName = "", elementid = "";
                    String eletime = doc.g("WF_LastModified");
                    String eleUserName = doc.g("WF_AddName_CN");
                    String eleVersion = doc.g("WF_Version");
                    String docUnid = doc.g("WF_OrUnid");
                    String devType = "";
                    if (tableName.equals("BPM_GridList")) {
                        elementName = doc.g("GridName");
                        elementid = doc.g("GridNum");
                        elementType = "view";
                        devType = "3";
                    }
                    else if (tableName.equals("BPM_FormList")) {
                        elementName = doc.g("FormName");
                        elementid = doc.g("FormNumber");
                        elementType = "form";
                        devType = "1";
                    }
                    else if (tableName.equals("BPM_DataSourceList")) {
                        elementName = doc.g("DataName");
                        elementid = doc.g("Dataid");
                        elementType = "json";
                        devType = "2";
                    }
                    else if (tableName.equals("BPM_RuleList")) {
                        elementName = doc.g("RuleName");
                        elementid = doc.g("RuleNum");
                        elementType = "rule";
                        devType = "4";
                    }
                    else if (tableName.equals("BPM_PageList")) {
                        elementName = doc.g("PageName");
                        elementid = doc.g("PageNum");
                        elementType = "page";
                        devType = "5";
                    }
                    doc.clear();
                    doc.s("ElementName", elementName);
                    doc.s("Elementid", elementid);
                    doc.s("WF_Appid", elappid);
                    doc.s("ElementType", elementType);
                    doc.s("WF_LastModified", eletime);
                    doc.s("WF_AddName_CN", eleUserName);
                    doc.s("WF_Version", eleVersion);
                    doc.s("WF_OrUnid", docUnid);
                    doc.s("WF_DType", devType);
                    resultDc.add(doc);
                }
            }
        }
        String jsonStr = Documents.dc2json(resultDc, "rows");
        BeanCtx.p(jsonStr);
        resultDc.clear();
        return "";
    }

}