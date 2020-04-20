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
 * 本类主要负责实现xml数据源输出
 * 
 * @author Administrator
 * 
 */
public class AppXmlData implements AppElement {

    @Override
    public void run(String wf_num) throws Exception {
        BeanCtx.print(getElementHtml(wf_num));
    }

    public String getElementBody(String wf_num, boolean readOnly) throws Exception {
        return getElementHtml(wf_num);
    }

    public String getElementHtml(String wf_num) throws Exception {
        BeanCtx.getResponse().setContentType("text/xml;charset=utf-8");
        String jsonid = BeanCtx.g("wf_num", true);
        Document eldoc = AppUtil.getDocByid("BPM_DataSourceList", "Dataid", jsonid, true);
        if (eldoc.isNull()) {
            return "The json " + jsonid + " does not exist!";
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
            BeanCtx.log("E", "Error:The json field column config is null!");
            return "Error:The json field column config is null!";
        }

        // 组合sql语句
        String sql, totalNum;
        String remoteSortDir = BeanCtx.g("order", true); // 运程排序方向
        String remoteSortFdName = BeanCtx.g("sort", true); // 远程排序字段
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
            if (Tools.isNotBlank(remoteSortFdName)) {
                // 远程排序字段和排序方式
                orderFieldName = remoteSortFdName;
                sqlDirection = remoteSortDir;
            }
            else {
                // 使用数据源中的配置值
                orderFieldName = eldoc.g("OrderFieldName");
                sqlDirection = eldoc.g("SqlDirection");
            }
            // 组合排序方式
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

            // 获得读者域的计算where条件
            if (eldoc.g("InReaders").equals("1")) {
                if (Tools.isBlank(sqlWhere)) {
                    sqlWhere = Rdb.getInReaderSql(eldoc.g("AclFdName"));
                }
                else {
                    sqlWhere = "(" + sqlWhere + ") and " + Rdb.getInReaderSql(eldoc.g("AclFdName"));
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

            String selectColList = "";
            if (eldoc.g("UseJsonColumnFlag").equals("1")) {
                selectColList = jsonColumnList.toString(); // 使用json中的数据列配置
            }
            else {
                selectColList = eldoc.g("SelectColList"); // 指定显示列
            }

            // 获得文档总数
            sql = "select count(*) as TotalNum from " + eldoc.g("SqlTableName") + " " + sqlWhere;
            totalNum = Rdb.getValueBySql(sql);
            if (eldoc.g("OutSql").equals("1")) {
                BeanCtx.out("TotalSql=" + sql);
            }

            // 生成最后的数据sql语句
            sql = "select " + selectColList + " from " + eldoc.g("SqlTableName") + " " + sqlWhere + orderStr;

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

        // 输出xml格式数据
        String rootTag = eldoc.g("RootTag");
        StringBuilder xmlStr = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?><" + rootTag + "><TotalResults>" + totalNum + "</TotalResults>");
        Document[] dc = Rdb.getAllDocumentsBySql(eldoc.g("SqlTableName"), sql, Integer.parseInt(pageNum), Integer.parseInt(pageSize));
        for (Document doc : dc) {
            if (insLinkeyRule != null) {
                params.put("DataDoc", doc);
                params.put("EventName", "formatRowData");
                insLinkeyRule.run(params); // 格式化数据列
            }
            xmlStr.append("<Item><DocUNID>" + doc.g("WF_OrUnid") + "</DocUNID><ItemAttributes>");
            for (HashMap<String, String> itemMap : colmunSet) {
                String fdName = itemMap.get("FdName"); // 字段名称
                String jsonName = itemMap.get("JsonName"); // 自定义名称
                String isString = itemMap.get("IsString");// 是否cdata
                String fdValue = doc.g(fdName);
                if (isString.equals("Y")) {
                    fdValue = "<![CDATA[" + fdValue + "]]>";
                }
                xmlStr.append("<" + jsonName + ">" + fdValue + "</" + jsonName + ">");
            }
            xmlStr.append("</ItemAttributes></Item>");
            doc.clear();
        }

        xmlStr.append("</" + rootTag + ">");
        return xmlStr.toString();
    }

}
