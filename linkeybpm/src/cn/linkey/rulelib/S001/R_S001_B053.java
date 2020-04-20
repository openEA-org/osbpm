package cn.linkey.rulelib.S001;

/**
 * R_S001_B053
 * 
 * @author Administrator
 *
 */
import java.util.HashMap;
import java.util.LinkedHashSet;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.factory.BeanCtx;
import cn.linkey.factory.IIIIILIIIIIIIII;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;

public class R_S001_B053 implements LinkeyRule {

    public String Run(HashMap<String, Object> params) {
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

        getElemDesigner(params);

        // 获得json数据源的数据库表名
        sql = "select SqlTableName from BPM_DataSourceList where Dataid='" + gridDoc.g("DataSource") + "'";
        String sqlTableName = Rdb.getValueBySql(sql);

        // 全部转换为xml字符串
        String[] docArray = Tools.split(BeanCtx.g("WF_OrUnid", true));
        StringBuilder xmlStr = new StringBuilder();
        xmlStr.append("<documents>");
        for (String docUnid : docArray) {
            Document doc = Rdb.getDocumentBySql("select * from " + sqlTableName + " where WF_OrUnid='" + docUnid + "'");
            doc.s("WF_OrTableName", sqlTableName); // 文档所在数据库的表名
            xmlStr.append(doc.toXmlStr(true));
            doc.clear();
        }
        xmlStr.append("</documents>");

        return "";

    }

    /**
     * 获得数据库表的配置信息
     * 
     * @param tableName
     */
    public HashMap<String, HashMap<String, String>> getTableConfig(String tableName) {
        String sql = "select FieldConfig from BPM_TableConfig where TableName='" + tableName + "'";
        String fieldConfig = Rdb.getValueBySql(sql);

        HashMap<String, HashMap<String, String>> fdmap = new HashMap<String, HashMap<String, String>>();

        // 获得所有字段的配置集合
        int spos = fieldConfig.indexOf("[");
        if (spos != -1) {
            fieldConfig = fieldConfig.substring(spos, fieldConfig.lastIndexOf("]") + 1);
            com.alibaba.fastjson.JSONArray jsonArr = com.alibaba.fastjson.JSON.parseArray(fieldConfig);
            for (int i = 0; i < jsonArr.size(); i++) {
                com.alibaba.fastjson.JSONObject rowItem = (com.alibaba.fastjson.JSONObject) jsonArr.get(i);
                String fdName = rowItem.getString("FdName"); // 字段名称
                String fdType = rowItem.getString("FdType"); // 字段类型
                String fdRemark = rowItem.getString("FdRemark");// 备注
                HashMap<String, String> fdItemMap = new HashMap<String, String>();
                fdItemMap.put("FdName", fdName);
                fdItemMap.put("FdRemark", fdRemark);
                fdItemMap.put("FdType", fdType);
                fdmap.put(fdName, fdItemMap);
            }
        }
        return fdmap;
    }

    public String getElemDesigner(HashMap<String, Object> params) {
        BeanCtx.getResponse().setContentType("text/json;charset=utf-8");
        String keyword = Tools.decodeUrl(BeanCtx.g("keyword", true));
        String tableList = BeanCtx.g("tableList", true);
        String qryappid = BeanCtx.g("WF_Appid", true);

        String[] tableArray = Tools.split(tableList);
        LinkedHashSet<Document> resultDc = new LinkedHashSet<Document>();
        for (String tableName : tableArray) {
            // 针对每一张表要进行全文搜索，转成json对像后进行比较
            String sql = "select * from " + tableName;
            if (Tools.isNotBlank(qryappid)) {
                sql = sql + " where WF_Appid='" + qryappid + "'";
            }
            Document[] dc = Rdb.getAllDocumentsBySql(sql);
            for (Document doc : dc) {
                String jsonStr = doc.toJson();
                if (jsonStr.indexOf(keyword) != -1) {
                    // 说明找到了文档
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

    @Override
    public String run(HashMap<String, Object> params) {
        String a = "BPM_Sys";
        String b = "temInfo";
        String startDate = IIIIILIIIIIIIII.L1IIIL1L1IIIIIIIIIILLLIIIIIIIIIIIIIIIIIIIIIILLL(Rdb.getValueBySql("select EndDate from " + a + b));
        if (Tools.isBlank(startDate)) {
            BeanCtx.p("true");
        }
        else {
            BeanCtx.p(String.valueOf(DateUtil.lessTime(startDate + " 00:00", DateUtil.getNow())));
        }
        return "";
    }

}
