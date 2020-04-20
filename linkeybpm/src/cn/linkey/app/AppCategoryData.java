package cn.linkey.app;

import java.util.HashMap;
import java.util.HashSet;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 本类主要负责实现Category数据源的输出 
 * 
 * 注意：分页只针对第一列的字段有效果，后面展开的没有分页功能
 * 必须要用id和text两个字段输出 只能根据GroupField进行order by 
 * 不能用group by 之外的字段进行排序 
 * 最后行的数据不支持其他列的点击排序，不支持远程排序，只支持当前页排序功能
 * 
 * @author Administrator
 * 
 */
public class AppCategoryData implements AppElement {

    @Override
    public void run(String wf_num) throws Exception {
        BeanCtx.print(getElementHtml(wf_num));
    }

    public String getElementBody(String wf_num, boolean readOnly) throws Exception {
        return getElementHtml(wf_num);
    }

    public String getElementHtml(String wf_num) throws Exception {
        BeanCtx.getResponse().setContentType("text/json;charset=utf-8");
        String dataid = wf_num;
        String qryid = BeanCtx.g("id", true); //grid中传入进来的id参数
        String idField = qryid;
        String idValue = "";
        int ipos = qryid.indexOf("#");
        if (ipos != -1) {
            idField = qryid.substring(0, ipos);
            idValue = qryid.substring(ipos + 1);
        }
        Document eldoc = AppUtil.getDocByid("BPM_DataSourceList", "Dataid", dataid, true);
        if (eldoc.isNull()) {
            return "The datasource " + dataid + " does not exist!";
        }

        // 获得事件对像
        String formatSql = "";
        LinkeyRule insLinkeyRule = null;
        String ruleNum = eldoc.g("EventRuleNum");
        HashMap<String, Object> params = new HashMap<String, Object>();
        if (Tools.isNotBlank(ruleNum)) {
            params.put("ConfigDoc", eldoc);
            insLinkeyRule = (LinkeyRule) BeanCtx.getBeanByRuleNum(ruleNum); // json事件执行对像
            //1.先运行打开事件
            params.put("EventName", "onDataSourceOpen"); // 用来改变当前配置文档的参数
            insLinkeyRule.run(params);
            //2.再运行格式化事件
            params.put("EventName", "formatSql"); // 获得格式化的sql语句
            formatSql = insLinkeyRule.run(params);
        }

        // 组合sql语句
        boolean isLast = false;
        boolean isFirst = false;
        String groupField = ""; //分组的字段
        String totalNum = "", sql = "";
        String remoteSortDir = BeanCtx.g("order", true); // 运程排序方向
        String searchStr = BeanCtx.g("searchStr", true);
        String pageSize = BeanCtx.g("rows", true); // 每页显示数
        String pageNum = BeanCtx.g("page", true); // 当前页
        if (Tools.isBlank(pageSize)) {
            pageSize = "25";
        }
        if (Tools.isBlank(pageNum)) {
            pageNum = "1";
        }
        if (Tools.isBlank(formatSql)) {
            // 自动组合sql语句，分析并获得sqlwhere条件
            String orderFieldName = "", sqlDirection = "";
            String sqlWhere = eldoc.g("SqlWhere");
            sqlWhere = Tools.parserStrByQueryString(sqlWhere, true); // 格式化条件中的{}变量为传入的参数
            orderFieldName = eldoc.g("OrderFieldName");
            if (Tools.isNotBlank(remoteSortDir)) {
                // 远程排序字段和排序方式
                sqlDirection = remoteSortDir;
            }
            else {
                // 使用数据源中的配置值
                sqlDirection = eldoc.g("SqlDirection");
            }

            // 获得读者域的计算where条件
            if (eldoc.g("InReaders").equals("1")) {
                if (Tools.isBlank(sqlWhere)) {
                    sqlWhere = Rdb.getInReaderSql(eldoc.g("AclFdName"));
                }
                else {
                    sqlWhere = "(" + sqlWhere + ") and " + Rdb.getInReaderSql(eldoc.g("AclFdName"));
                }
            }

            //add idfield sql where
            if (Tools.isNotBlank(idField)) {
                if (Tools.isBlank(sqlWhere)) {
                    sqlWhere = idField + "='" + idValue + "'";
                }
                else {
                    sqlWhere = "(" + sqlWhere + ") and " + idField + "='" + idValue + "'";
                }
            }

            if (Tools.isNotBlank(sqlWhere)) {
                sqlWhere = " Where " + sqlWhere;
            }

            // 组合搜索字符串
            if (Tools.isNotBlank(searchStr) && Tools.isNotBlank(eldoc.g("DefaultSearchField"))) {
                searchStr = "%" + searchStr + "%";
                String defaultSearchField = "";
                if (Rdb.getDbType().equals("MSSQL")) {
                    defaultSearchField = eldoc.g("DefaultSearchField").replace(",", "+"); // sql server把,号换成+号
                }
                else if (Rdb.getDbType().equals("MYSQL")) {
                    defaultSearchField = "concat(" + eldoc.g("DefaultSearchField") + ")"; // mysql需要使用concat方法
                }
                else {
                    defaultSearchField = eldoc.g("DefaultSearchField").replace(",", "||"); // oracle把,号换成||号
                }
                if (Tools.isNotBlank(sqlWhere)) {
                    searchStr = " AND " + defaultSearchField + " like '" + searchStr + "'";
                }
                else {
                    searchStr = " where " + defaultSearchField + " like '" + searchStr + "'";
                }
                sqlWhere = sqlWhere + searchStr;
            }

            //自动匹配角色权限的sql where条件
            String roleAclSql = AppUtil.getDataSourceAclSql(eldoc);
            if (Tools.isNotBlank(roleAclSql)) {
                if (Tools.isBlank(sqlWhere)) {
                    sqlWhere = roleAclSql;
                }
                else {
                    sqlWhere += " and " + roleAclSql;
                }
            }

            //生成最后的sql语句 开始组合group by 所需的select字段
            String categoryField = eldoc.g("CategoryField");
            String[] catFieldArray = Tools.split(categoryField);
            groupField = catFieldArray[0];
            if (Tools.isBlank(idField)) {
                //说明是打开第一个字段的分类
                isFirst = true;
                sql = "select " + groupField + " as text,count(*) as TotalNum from " + eldoc.g("SqlTableName") + " " + sqlWhere + " group by " + groupField;

                // 获得文档总数
                String totalsql = "select count(*) as TotalNum from (" + sql + ") tb";
                totalNum = Rdb.getValueBySql(totalsql);
                if (eldoc.g("OutSql").equals("1")) {
                    BeanCtx.out("TotalSql=" + totalsql);
                }

                //添加排序字段
                if (Tools.isNotBlank(sqlDirection)) {
                    sql += " order by " + groupField + " " + sqlDirection;
                }
            }
            else {
                //展开第二...N个分类字段
                int maxNum = catFieldArray.length;
                for (int x = 0; x < maxNum; x++) {
                    if (catFieldArray[x].equals(idField)) {
                        if (x == maxNum - 1) {
                            //最后一个字段，需要全部展开
                            isLast = true;
                            sql = "select * from " + eldoc.g("SqlTableName") + " " + sqlWhere; //这里只能选择*号，因为没办法指定不存在的列TotalNum

                            // 加入排序方式，组合排序方式 order by 必须加在整个sql的最后
                            String orderStr = "";
                            if (Tools.isNotBlank(orderFieldName)) {
                                if (orderFieldName.indexOf(",") == -1) { // 仅有一个字段排序
                                    orderStr = " order by " + orderFieldName + " " + sqlDirection;
                                }
                                else { // 同时有多个字段排序
                                    String[] fdArray = Tools.split(orderFieldName);
                                    String[] dirArray = Tools.split(sqlDirection);
                                    int i = 0;
                                    for (String fdName : fdArray) {
                                        if (Tools.isBlank(orderStr)) {
                                            orderStr = "order by " + fdName + " " + dirArray[i];
                                        }
                                        else {
                                            orderStr += "," + fdName + " " + dirArray[i];
                                        }
                                        i++;
                                    }
                                }
                            }
                            if (Tools.isNotBlank(orderStr)) {
                                sql += " " + orderStr;
                            }
                        }
                        else {
                            //中间分类字段
                            groupField = catFieldArray[x + 1];
                            sql = "select " + groupField + " as text,count(*) as TotalNum from " + eldoc.g("SqlTableName") + " " + sqlWhere + " group by " + groupField;

                            //添加排序字段
                            if (Tools.isNotBlank(sqlDirection)) {
                                sql += " order by " + groupField + " " + sqlDirection;
                            }
                        }
                    }
                }
            }
            if (Tools.isBlank(sql)) {
                BeanCtx.log("E", "Error:Category field config error!");
            }
            if (eldoc.g("OutSql").equals("1")) {
                BeanCtx.out("DataSQL=" + sql);
            } // 输出调试sql语句

        }
        else {
            sql = formatSql; // 使用规则中返回的sql语句
            params.put("EventName", "getTotalNum");
            insLinkeyRule = (LinkeyRule) BeanCtx.getBeanByRuleNum(ruleNum); // json事件执行对像
            totalNum = insLinkeyRule.run(params);
            if (eldoc.g("OutSql").equals("1")) {
                BeanCtx.out("DataSQL=" + sql);
            }
        }

        if (isFirst) {
            return getFirstCategory(sql, pageNum, pageSize, totalNum, eldoc, insLinkeyRule, groupField);
        }
        else if (isLast) {
            return getLastRowData(sql, pageNum, pageSize, eldoc, insLinkeyRule, isLast, isFirst);
        }
        else {
            return getMidCategory(sql, pageNum, pageSize, eldoc, insLinkeyRule, groupField);
        }
    }

    /**
     * 获得第一字段分类的数据
     * 
     */
    public String getFirstCategory(String sql, String pageNum, String pageSize, String totalNum, Document eldoc, LinkeyRule insLinkeyRule, String groupField) throws Exception {

        StringBuilder jsonStr = new StringBuilder();
        jsonStr.append("{\"total\":\"" + totalNum + "\",\"rows\":[");
        Document[] dc = Rdb.getAllDocumentsBySql(eldoc.g("SqlTableName"), sql, Integer.parseInt(pageNum), Integer.parseInt(pageSize));
        int x = 0;
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("ConfigDoc", eldoc);
        for (Document doc : dc) {
            //BeanCtx.out("doc="+doc);
            if (insLinkeyRule != null) {
                params.put("DataDoc", doc);
                params.put("EventName", "formatRowData");
                insLinkeyRule.run(params); // 格式化数据列
            }
            if (x == 0) {
                x = 1;
            }
            else {
                jsonStr.append(",");
            }
            jsonStr.append("{\"id\":\"" + groupField + "#" + doc.g("text") + "\",\"text\":\"" + doc.g("text") + "\"");
            jsonStr.append(",\"TotalNum\":" + doc.g("TotalNum"));
            jsonStr.append(",\"state\":\"closed\"}");
            doc.clear();
        }
        jsonStr.append("]}");
        return jsonStr.toString();
    }

    /**
     * 获得第一字段分类的数据
     * 
     */
    public String getMidCategory(String sql, String pageNum, String pageSize, Document eldoc, LinkeyRule insLinkeyRule, String groupField) throws Exception {
        StringBuilder jsonStr = new StringBuilder();
        jsonStr.append("[");
        Document[] dc = Rdb.getAllDocumentsBySql(eldoc.g("SqlTableName"), sql, Integer.parseInt(pageNum), Integer.parseInt(pageSize));
        int x = 0;
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("ConfigDoc", eldoc);
        for (Document doc : dc) {
            if (insLinkeyRule != null) {
                params.put("DataDoc", doc);
                params.put("EventName", "formatRowData");
                insLinkeyRule.run(params); // 格式化数据列
            }
            if (x == 0) {
                x = 1;
            }
            else {
                jsonStr.append(",");
            }
            jsonStr.append("{\"id\":\"" + groupField + "#" + doc.g("text") + "\",\"text\":\"" + doc.g("text") + "\"");
            jsonStr.append(",\"TotalNum\":" + doc.g("TotalNum"));
            jsonStr.append(",\"state\":\"closed\"}");
            doc.clear();
        }
        jsonStr.append("]");
        return jsonStr.toString();
    }

    /**
     * 输出最后一个分类的全部列
     * 
     * @param sql
     * @param pageNum
     * @param pageSize
     * @param totalNum
     * @param eldoc
     * @param insLinkeyRule
     * @param params
     * @param colmunSet
     */
    public String getLastRowData(String sql, String pageNum, String pageSize, Document eldoc, LinkeyRule insLinkeyRule, boolean isLast, boolean isFirst) throws Exception {

        // 获得json数据列配置对像
        JSONArray jsonArr;
        StringBuilder jsonColumnList = new StringBuilder();
        String jsonColConfig = eldoc.g("JsonConfig"); // json列配置
        int spos = jsonColConfig.indexOf("[");
        if (spos == -1) {
            return "Error:The json field column config is null!";
        }
        jsonColConfig = jsonColConfig.substring(spos, jsonColConfig.lastIndexOf("]") + 1);
        HashSet<HashMap<String, String>> colmunSet = new HashSet<HashMap<String, String>>(); //所有列配置参数的set集合
        if (Tools.isNotBlank(jsonColConfig)) {
            jsonArr = JSON.parseArray(jsonColConfig);
            for (int i = 0; i < jsonArr.size(); i++) {
                JSONObject configItem = (JSONObject) jsonArr.get(i);
                if (i > 0) {
                    jsonColumnList.append(",");
                }
                String fdName = configItem.getString("FdName"); // 字段名称
                String jsonName = configItem.getString("JsonName"); // 自定义名称
                if (Tools.isBlank(jsonName)) {
                    jsonName = fdName;
                }
                HashMap<String, String> itemMap = new HashMap<String, String>(3);
                itemMap.put("FdName", fdName);
                itemMap.put("JsonName", jsonName);
                itemMap.put("IsString", configItem.getString("IsString")); // 是否字符串
                jsonColumnList.append(fdName);
                colmunSet.add(itemMap);
            }
            if (jsonColumnList.indexOf("WF_OrUnid") == -1) {
                //如果列中没有配置WF_OrUnid字段，则自动追加一个配置值
                HashMap<String, String> itemMap = new HashMap<String, String>(3);
                itemMap.put("FdName", "WF_OrUnid");
                itemMap.put("JsonName", "WF_OrUnid");
                itemMap.put("IsString", "Y");
                colmunSet.add(itemMap);
                jsonColumnList.append(",WF_OrUnid");
            }
        }
        else {
            return "Error:The json field column config is null!";
        }

        // 输出json格式数据
        StringBuilder jsonOutStr = new StringBuilder();
        Document[] dc = Rdb.getAllDocumentsBySql(eldoc.g("SqlTableName"), sql, Integer.parseInt(pageNum), Integer.parseInt(pageSize));
        int x = 0;
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("ConfigDoc", eldoc);
        for (Document doc : dc) {
            if (insLinkeyRule != null) {
                params.put("DataDoc", doc);
                params.put("EventName", "formatRowData");
                insLinkeyRule.run(params); // 格式化数据列
            }
            if (x == 0) {
                x = 1;
            }
            else {
                jsonOutStr.append(",");
            }
            jsonOutStr.append("{\"id\":\"" + doc.g("WF_OrUnid") + "\"");
            for (HashMap<String, String> itemMap : colmunSet) {
                String fdName = itemMap.get("FdName"); // 字段名称
                String jsonName = itemMap.get("JsonName"); // 自定义名称
                String isString = itemMap.get("IsString");// 是否字符串
                String fdValue = doc.g(fdName);
                if (isString.equals("Y")) {
                    fdValue = Tools.encodeJson(fdValue); //进行josn编码
                    fdValue = "\"" + fdValue + "\"";
                }
                jsonOutStr.append(",\"" + jsonName + "\":" + fdValue);
            }
            if (isLast) {
                jsonOutStr.append(",\"state\":\"open\"");
            }
            else {
                jsonOutStr.append(",\"state\":\"closed\"");
            }
            jsonOutStr.append("}");
            doc.clear();
        }

        String formatJson = "[" + jsonOutStr.toString() + "]";

        return formatJson;
    }

}
