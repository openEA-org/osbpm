package cn.linkey.rulelib.S001;

import java.util.*;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:搜索所有设计元素
 * @author admin
 * @version: 8.0
 * @Created: 2014-03-31 14:32
 */
public class R_S001_E009 implements LinkeyRule {

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
        String sql = "select AppName,WF_Appid from BPM_AppList where " + Rdb.getInReaderSql("Owner");
        String applist = Rdb.getValueForSelectTagBySql(sql, "");
        applist = "--所有应用--|," + applist;
        doc.s("Appid", applist);
        return "1"; // 成功必须返回1，否则表退出并显示返回的字符串
    }

    public String onFormSave(Document doc, Document formDoc) {
        //当表单存盘前

        return "1"; //成功必须返回1，否则表示退出存盘
    }

}