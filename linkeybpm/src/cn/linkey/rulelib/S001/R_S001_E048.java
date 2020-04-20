package cn.linkey.rulelib.S001;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:新增JavaBean表单事件
 * @author admin
 * @version: 8.0
 * @Created: 2014-04-11 17:48
 */
public class R_S001_E048 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) {
        //获取事件运行参数
        Document formDoc = (Document) params.get("FormDoc"); //表单配置文档
        Document doc = (Document) params.get("DataDoc"); //数据主文档
        String eventName = (String) params.get("EventName");//事件名称
        if (eventName.equals("onFormOpen")) {
            String readOnly = (String) params.get("ReadOnly"); //1表示只读，0表示编辑
            return onFormOpen(doc, formDoc, readOnly);
        }
        else if (eventName.equals("onFormSave")) {
            return onFormSave(doc, formDoc);
        }
        return "1";
    }

    public String onFormOpen(Document doc, Document formDoc, String readOnly) {
        // 当表单打开时
        if (readOnly.equals("1")) {
            return "1";
        } // 如果是阅读状态则可不执行
        if (doc.isNewDoc()) {
            // 可以对表单字段进行初始化如:doc.s("fdname",fdvalue),可以获取字段值 doc.g("fdname")

        }

        String defaultValue = doc.g("WF_Appid");
        String sql = "";
        if (Rdb.getDbType().equals("MSSQL")) {
            sql = "select AppName+'('+WF_Appid+')',WF_Appid from BPM_AppList";
        }
        else if (Rdb.getDbType().equals("MYSQL")) {
            sql = "select concat(AppName,'(',WF_Appid,')'),WF_Appid from BPM_AppList";
        }
        else {
            sql = "select AppName||'('||WF_Appid||')',WF_Appid from BPM_AppList";
        }
        String applist = Rdb.getValueForSelectTagBySql(sql, defaultValue);
        doc.s("SelAppid", applist);

        return "1"; // 成功必须返回1，否则表退出并显示返回的字符串
    }

    public String onFormSave(Document doc, Document formDoc) {
        // 当表单存盘前
        doc.s("ClassPath", "cn.linkey.rulelib." + doc.g("WF_Appid") + "." + doc.g("RuleNum"));
        doc.s("CompileFlag", "1");
        doc.s("Singleton", "1");
        doc.s("Status", "1");
        doc.s("RuleType", "7"); //7表示javabean
        doc.s("EventType", "0"); //0表示是规则
        doc.s("WF_Version", "8.0");
        String sql = "select DefaultCode from BPM_DevDefaultCode where CodeType='JavaBean_Code'";
        String ruleCode = Rdb.getValueBySql(sql);
        doc.s("RuleCode", ruleCode);

        return "1"; // 成功必须返回1，否则表示退出存盘
    }

}