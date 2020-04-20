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
 * 本类主要负责实现Tree Data json数据源输出，根据Tree data配置表输出json格式的数据
 * 
 * @author Administrator
 * 
 */
public class AppTreeData implements AppElement {

    @Override
    public void run(String wf_num) throws Exception {
        BeanCtx.print(getElementHtml(wf_num));
    }

    public String getElementBody(String wf_num, boolean readOnly) throws Exception {
        return getElementHtml(wf_num);
    }

    /**
     * 获得数据的html
     */
    public String getElementHtml(String jsonid) throws Exception {
        BeanCtx.getResponse().setContentType("text/json;charset=utf-8");
        String parentid = BeanCtx.g("id", true);
        if (Tools.isBlank(parentid)) {
            parentid = "root";
        }

        Document eldoc = AppUtil.getDocByid("BPM_DataSourceList", "Dataid", jsonid, true);
        if (eldoc.isNull()) {
            return "The treedata " + jsonid + " does not exist!";
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
            params.put("EventName", "onDataOpen"); // 用来改变当前配置文档的参数
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
        String sql;
        String remoteSortDir = BeanCtx.g("order", true); // 运程排序方向
        String remoteSortFdName = BeanCtx.g("sort", true); // 远程排序字段
        String searchStr = BeanCtx.g("searchStr", true); //搜索字符串
        String parentFdName = eldoc.g("ParentFdName"); //上级文件夹名称
        String currentFdName = eldoc.g("CurrentFdName");//当前文件夹名称

        if (Tools.isNotBlank(formatSql)) {
            sql = formatSql; // 使用规则中返回的sql语句
        }
        else {
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

            //增加默认的ParentFolderid选择条件,用来选择子文件夹
            //BeanCtx.out("sqlWhere="+sqlWhere);
            if (Tools.isNotBlank(sqlWhere)) {
                sqlWhere = " Where " + sqlWhere + " and " + parentFdName + "='{Folderid}'";
            }
            else {
                sqlWhere = " Where " + parentFdName + "='{Folderid}'";
            }

            // 组合搜索字符串
            if (Tools.isNotBlank(searchStr)) {
                if (Tools.isNotBlank(sqlWhere)) {
                    searchStr = " AND " + searchStr;
                }
                else {
                    sqlWhere += " where " + searchStr;
                }
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

            // 生成最后的数据sql语句,orderStr一定要放在最后
            sql = "select " + selectColList + " from " + eldoc.g("SqlTableName") + " " + sqlWhere + orderStr;
        }

        if (eldoc.g("OutSql").equals("1")) {
            BeanCtx.out("DataSQL=" + sql);
        }
        //BeanCtx.out("sql="+sql);

        // 输出json格式数据
        String async = BeanCtx.g("async", true); //优先使用url中传入的值
        if (Tools.isBlank(async)) {
            async = eldoc.g("Async");
            if (async.equals("1")) {
                async = "true";
            }
            else {
                async = "false";
            }
        }
        //BeanCtx.out("async="+async);
        StringBuilder jsonOutStr = new StringBuilder();
        jsonOutStr.append("[");
        String rootsql = sql.replace("{Folderid}", parentid);
        //BeanCtx.out("rootsql="+rootsql);
        Document[] dc = Rdb.getAllDocumentsBySql(eldoc.g("SqlTableName"), rootsql);
        int x = 0;
        for (Document doc : dc) {
            if (x == 0) {
                x = 1;
            }
            else {
                jsonOutStr.append(",");
            }
            jsonOutStr.append(getAllSubFolder(sql, doc, async, insLinkeyRule, params, colmunSet, currentFdName));
            doc.clear();
        }
        //		if(x==0 && Tools.isNotBlank(eldoc.g("DefaultRootName"))){//树节点为空时默认输出的节点名称
        //			jsonOutStr.append("{\"text\":\""+eldoc.g("DefaultRootName")+"\",\"id\":\"root\",\"WF_OrUnid\":\"root\"}");
        //		}
        jsonOutStr.append("]");
        //2014.12.13增加默认显示根文件夹功能
        if (eldoc.g("ShowRootFolder").equals("true")) {
            jsonOutStr.insert(0, "[{\"text\":\"" + eldoc.g("DataName") + "\",\"id\":\"root\",\"state\":\"open\",\"children\":");
            jsonOutStr.append("}]");
        }
        //BeanCtx.out(jsonOutStr.toString());
        return jsonOutStr.toString();
    }

    /**
     * 获得所有子菜单的json
     */
    public String getAllSubFolder(String sql, Document doc, String async, LinkeyRule insLinkeyRule, HashMap<String, Object> params, HashSet<HashMap<String, String>> colmunSet, String currentFdName)
            throws Exception {

        if (insLinkeyRule != null) {
            params.put("DataDoc", doc);
            params.put("EventName", "formatRowData");
            insLinkeyRule.run(params); // 格式化数据列
        }

        StringBuilder jsonStr = new StringBuilder();
        jsonStr.append("{");

        //1.增加当前文件夹的输出列
        int j = 0;
        for (HashMap<String, String> itemMap : colmunSet) {
            String fdName = itemMap.get("FdName"); // 字段名称
            String jsonName = itemMap.get("JsonName"); // 自定义名称
            String isString = itemMap.get("IsString");// 是否字符串
            if (j == 0) {
                j = 1;
            }
            else {
                jsonStr.append(",");
            }
            String fdValue = doc.g(fdName);
            if (isString.equals("Y")) {
                fdValue = encodeJson(fdValue); //进行json编码
                fdValue = "\"" + fdValue + "\"";
            }
            jsonStr.append("\"" + jsonName + "\":" + fdValue);
        }

        //2.看此文件夹是否有子文件夹
        String subsql = sql.replace("{Folderid}", doc.g(currentFdName));
        //BeanCtx.out("subsql="+subsql);
        Document[] dc = Rdb.getAllDocumentsBySql(subsql);
        //BeanCtx.out("subsql="+subsql);
        //BeanCtx.out("dc="+dc.length);
        if (dc.length > 0) {
            //说明有子文件夹
            if (!async.equals("true")) {
                //false表示一次性全部加载输出,进行递归调用
                jsonStr.append(",\"state\":\"closed\",\"children\":[");
                int i = 0;
                for (Document subdoc : dc) {
                    if (i == 0) {
                        i = 1;
                    }
                    else {
                        jsonStr.append(",");
                    }
                    jsonStr.append(getAllSubFolder(sql, subdoc, async, insLinkeyRule, params, colmunSet, currentFdName));
                }
                jsonStr.append("]");
            }
            else {
                //表示异步加载输出
                jsonStr.append(",\"state\":\"closed\"");
            }
        }
        else {
            //没有子文件夹了
            jsonStr.append(",\"state\":\"open\"");
        }
        jsonStr.append("}");
        return jsonStr.toString();
    }

    /**
     * 对字符串进行json格式的编码
     * 
     * @param str 要编码的json值
     * @return
     */
    public String encodeJson(String str) {
        str = str.replace("\\", "\\\\"); // 1.替换值中的\
        str = str.replace("\"", "\\\""); // 2.替换值中的双引号
        str = str.replace("\t", ""); // 3.替换值中的换行符
        str = str.replace("\n", ""); // 3.替换值中的换行符
        str = str.replace("\r", ""); // 3.替换值中的换行符
        str = str.replace("<", "&lt;");
        str = str.replace(">", "&gt;");
        return str;
    }

}
