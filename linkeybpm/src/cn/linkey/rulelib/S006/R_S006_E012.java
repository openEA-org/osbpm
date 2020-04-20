package cn.linkey.rulelib.S006;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:角色成员列表事件
 * @author admin
 * @version: 8.0
 * @Created: 2014-04-24 15:04
 */
final public class R_S006_E012 implements LinkeyRule {
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
        String sql = "select FolderName from BPM_OrgDeptList where Folderid='" + doc.g("Folderid") + "' and OrgClass='" + doc.g("OrgClass") + "'";
        //BeanCtx.out("sql="+sql);
        doc.s("FolderName", Rdb.getValueBySql(sql));
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