package cn.linkey.rulelib.S014;

import java.util.*;
import cn.linkey.dao.Rdb;
import cn.linkey.dao.RdbCache;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:流程监控列表数据源事件
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-14 19:03
 */
final public class R_S014_E001 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
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

    public void formatRowData(Document doc) throws Exception {
        //doc对像对应数据库表中的记录可修改或获取所有字段
        //格式化列数据如: doc.s("Subject",doc.g("Subject")+doc.g("UserName")); 可重新格式化Subject字段
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("Processid", doc.g("Processid"));
        String logRuleNum = (String) RdbCache.get("WF_LogRuleNum");
        if (Tools.isNotBlank(logRuleNum)) {
            BeanCtx.getExecuteEngine().run(logRuleNum, params);//先分析流程运行实例日记
        }
        String sql = "select count(*) from BPM_MainData where WF_Processid='" + doc.g("Processid") + "' and WF_Status='Current'";
        String totalNum = Rdb.getValueBySql(sql);//获得分析后的引擎的数据
        if (Tools.isBlank(totalNum)) {
            totalNum = "0";
        }
        doc.s("InsDocNum", totalNum);
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