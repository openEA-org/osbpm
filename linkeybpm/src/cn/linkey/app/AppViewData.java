package cn.linkey.app;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

import org.apache.commons.lang3.StringEscapeUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * 本类主要负责实现json数据源输出，根据json配置表输出json格式的数据
 * 
 * @author Administrator
 * 
 */
public class AppViewData implements AppElement {

    @Override
    public void run(String jsonid) throws Exception {
        BeanCtx.print(getElementHtml(jsonid));
    }

    public String getElementBody(String jsonid, boolean readOnly) throws Exception {
        return getElementHtml(jsonid);
    }

    public String getElementHtml(String jsonid) throws Exception {
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
            if (jsonColumnList.toString().toLowerCase().indexOf("wf_orunid") == -1) {
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
            return "";
        }

        java.sql.Connection conn = null;
        String formatJson = "";
        try {
            String dataSourceid = eldoc.g("DataSourceid"); //数据源id
            if (Tools.isNotBlank(dataSourceid) && !dataSourceid.equals("default")) {
                conn = Rdb.getNewConnection(dataSourceid); // get the outer connection
            }

            // 输出调试sql语句
            if (eldoc.g("OutSql").equals("1")) {
                BeanCtx.setDebug();
            }

            // 组合sql语句
            String sql, totalNum = "0";
            String remoteSortDir = BeanCtx.g("order", true); // 运程排序方向
            String remoteSortFdName = BeanCtx.g("sort", true); // 远程排序字段
            String searchStr = BeanCtx.g("searchStr", true); //搜索字符串
            String pageSize = BeanCtx.g("rows", true); // 每页显示数
            String pageNum = BeanCtx.g("page", true); // 当前页
            if (Tools.isBlank(pageSize) || pageSize.equals("NaN")) {
                pageSize = eldoc.g("PageSize");
            }
            Integer size = 25;
            Integer num = 1;
            try {
                size = Integer.parseInt(pageSize);
            } catch (NumberFormatException e) {
                size = 25;
            }
            try {
                num = Integer.parseInt(pageNum);
            } catch (NumberFormatException e) {
                num = 1;
            }
            

            LinkedHashSet<Document> dc = null;
            String orderStr = "", selectColList = "", sqlWhere = "";
            if (Tools.isBlank(formatSql)) {
                // 自动组合sql语句，分析并获得sqlwhere条件
                String orderFieldName = "", sqlDirection = "";
                sqlWhere = eldoc.g("SqlWhere");
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
                if (Tools.isNotBlank(orderFieldName)) {
                    if (orderFieldName.indexOf(",") == -1) { // 仅有一个字段排序
                        orderStr = " order by " + orderFieldName + " " + sqlDirection;
                    }
                    else { // 同时有多个字段排序
                        String[] fdArray = Tools.split(orderFieldName);
                        String[] dirArray = Tools.split(sqlDirection);
                        int i = 0;
                        for (String fdName : fdArray) {
                            String dire = "";
                            if (dirArray.length > i) {
                                dire = dirArray[i];
                            }
                            if (Tools.isBlank(orderStr)) {
                                orderStr = "order by " + fdName + " " + dire;
                            }
                            else {
                                orderStr += "," + fdName + " " + dire;
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
                //BeanCtx.out("sqlWhere="+sqlWhere);
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
                //20180223添加对sql反转义，修复HTML的&lt; &gt;&amp;&quot;分别是<，>，&，"的转义字符
                roleAclSql = StringEscapeUtils.unescapeHtml4(roleAclSql);
                if (Tools.isNotBlank(roleAclSql)) {
                    if (Tools.isBlank(sqlWhere)) {
//                      sqlWhere = roleAclSql;
                    	sqlWhere = " where" + roleAclSql; //20180425 当sqlWhere没值时，添加where关键字
                    }
                    else {
                        sqlWhere += " and " + roleAclSql;
                    }
                }

                //			BeanCtx.out("sqlwhere="+sqlWhere);
                if (eldoc.g("UseJsonColumnFlag").equals("1")) {
                    selectColList = jsonColumnList.toString(); // 使用json中的数据列配置
                }
                else {
                    selectColList = eldoc.g("SelectColList"); // 指定显示列
                }

                if (eldoc.g("OutSql").equals("1")) {
                    BeanCtx.setDebug();
                }

                //获得文档总数
                long ts = System.currentTimeMillis();
                sql = "select count(1) as TotalNum from " + eldoc.g("SqlTableName") + " " + sqlWhere;
                totalNum = Rdb.getValueBySql(conn, sql);

                //获得文档集合对像
                dc = Rdb.getAllDocumentsSetByPage(conn, eldoc.g("SqlTableName"), selectColList, orderStr, sqlWhere, num, size);

                //调试输出总花费时间
                if (eldoc.g("OutSql").equals("1")) {
                    long te = System.currentTimeMillis();
                    BeanCtx.out("totalTime=" + (te - ts));
                }

            }
            else {
                // 使用规则中返回的sql语句
                params.put("EventName", "getTotalNum");
                insLinkeyRule = (LinkeyRule) BeanCtx.getBeanByRuleNum(ruleNum); // json事件执行对像
                totalNum = insLinkeyRule.run(params);
                dc = Rdb.getAllDocumentsSetBySql(formatSql);
            }

            int x = 0;
            StringBuilder jsonOutStr = new StringBuilder();
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
                jsonOutStr.append("{");
                int j = 0;
                for (HashMap<String, String> itemMap : colmunSet) {
                    String fdName = itemMap.get("FdName"); // 字段名称
                    String jsonName = itemMap.get("JsonName"); // 自定义名称
                    String isString = itemMap.get("IsString");// 是否字符串
                    if (j == 0) {
                        j = 1;
                    }
                    else {
                        jsonOutStr.append(",");
                    }
                    String fdValue = doc.g(fdName);
                    if (isString.equals("Y")) {
                        fdValue = Tools.encodeJson(fdValue); //进行josn编码
                        fdValue = "\"" + fdValue + "\"";
                    }
                    jsonOutStr.append("\"" + jsonName + "\":" + fdValue);
                }
                jsonOutStr.append("}");
                doc.clear();
            }

            // 对json进行格式化
            formatJson = eldoc.g("FormatJson");
            if (Tools.isNotBlank(formatJson)) {
                formatJson = formatJson.replace("#Total", totalNum);
                formatJson = formatJson.replace("#JsonData", jsonOutStr.toString());
            }
            else {
                formatJson = jsonOutStr.toString();
            }

            String callback = BeanCtx.g("callback", true);
            if (Tools.isNotBlank(callback)) {
                formatJson = callback + "(" + formatJson + ")";
            }
        }
        catch (Exception e) {
            BeanCtx.log(e, "E", "获取json数据源时出错");
        }
        finally {
            Rdb.close(conn);
        }
        return formatJson;
    }

}
