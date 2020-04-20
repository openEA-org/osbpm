package cn.linkey.rulelib.S001;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:数据库列表数据源事件
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-29 16:50
 */
final public class R_S001_E068 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        Document configdoc = (Document) params.get("ConfigDoc"); //grid配置文档
        Document doc = (Document) params.get("DataDoc"); //数据主文档
        String eventName = (String) params.get("EventName");//事件名称
        if (eventName.equals("formatRowData")) {
            formatRowData(doc);
        }
        else if (eventName.equals("formatSql")) {
            return formatSql(configdoc);
        }
        else if (eventName.equals("getTotalNum")) {
            return getTotalNum(configdoc);
        }
        else if (eventName.equals("onDataSourceOpen")) {
            onDataSourceOpen(configdoc);
        }
        return "";
    }

    public void onDataSourceOpen(Document configdoc) {
        //通过操作configdoc可以在数据源打开时动态修改配置参数
        //动态修改SqlWhere选择条件: configdoc.s("SqlWhere","id='1001'");

    }

    public void formatRowData(Document doc) {
        //doc对像对应数据库表中的记录可修改或获取所有字段
        //格式化列数据如: doc.s("Subject",doc.g("Subject")+doc.g("UserName")); 可重新格式化Subject字段
        String sql = "";

        //看数据库表是否在sql中真实存在
        String dataSourceid = doc.g("DataSourceid");
        if (Tools.isBlank(dataSourceid)) {
            dataSourceid = "default";
        }
        if (Rdb.isExistTable(dataSourceid, doc.g("TableName"))) {
            doc.s("InSQLFlag", "1");
        }
        else {
            doc.s("InSQLFlag", "0");
            doc.s("FieldFlag", "1");
        }

        //如果表真实存在则看字段是否有差异
        if (doc.g("InSQLFlag").equals("1")) {

            //获得文档总数
            sql = "select count(*) as TotalNum from " + doc.g("TableName");
            doc.s("Count", Rdb.getValueBySql(sql));

            //获得配置表中的所有字段的配置集合
            HashSet<String> fieldConfigSet = new HashSet<String>();
            String fieldConfig = doc.g("FieldConfig"); //所有字段配置
            int spos = fieldConfig.indexOf("[");
            if (spos != -1) {
                fieldConfig = fieldConfig.substring(spos, fieldConfig.lastIndexOf("]") + 1);
                //BeanCtx.out("fieldConfig="+fieldConfig);
                com.alibaba.fastjson.JSONArray jsonArr = com.alibaba.fastjson.JSON.parseArray(fieldConfig);
                for (int i = 0; i < jsonArr.size(); i++) {
                    com.alibaba.fastjson.JSONObject rowItem = (com.alibaba.fastjson.JSONObject) jsonArr.get(i);
                    String fdName = rowItem.getString("FdName"); // 字段名称
                    fieldConfigSet.add(fdName);
                }
            }
            String configFdStr = "," + Tools.join(fieldConfigSet, ",").toLowerCase() + ",";

            //sql实体表中的数据库表字段列表
            Set<String> sqlFieldListSet = Rdb.getTableColumnName(doc.g("TableName")).keySet();
            String sqlFdStr = "," + Tools.join(sqlFieldListSet, ",").toLowerCase() + ",";

            //先比较配置表中的所有字段是否在实体表中
            for (String fdName : fieldConfigSet) {
                if (sqlFdStr.indexOf("," + fdName.toLowerCase() + ",") == -1) {
                    BeanCtx.out("fdname=" + fdName);
                    doc.s("FieldFlag", "1"); //表示字段有差异
                    return;
                }
            }

            //再比较sql表中与配置表中的字段差异
            for (String fdName : sqlFieldListSet) {
                if (configFdStr.indexOf("," + fdName.toLowerCase() + ",") == -1) {
                    doc.s("FieldFlag", "1"); //表示字段有差异
                    return;
                }
            }

        }

    }

    public String formatSql(Document configdoc) {
        /*
         * configdoc表示当前json数据源的配置信息 自定义复杂的sql语句，当配置中的where条件无法满足时可以实现自定义sql String sql="select * from "+configDoc.g("SqlTableName")+" where WF_OrUnid='"+BeanCtx.g("fromurlarg")+"'"
         */
        return ""; //返回空表示使用配置值
    }

    public String getTotalNum(Document configdoc) {
        /* 与formatSql一起使用返回文档总数 */
        return "0";
    }

}