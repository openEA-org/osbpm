package cn.linkey.rulelib.S006;

import java.util.HashMap;

import cn.linkey.app.AppUtil;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:注册角色事件
 * @author admin
 * @version: 8.0
 * @Created: 2014-04-23 14:48
 */
final public class R_S006_E009 implements LinkeyRule {

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

            String sql = "select RoleNumber from BPM_OrgRoleList where RoleType='1' order by RoleNumber desc";
            String newNum = AppUtil.getElNewNum(sql);
            if (Tools.isBlank(newNum)) {
                newNum = "RS001";
            }
            doc.s("RoleNumber", newNum);
        }

        //获得应用列表
        String defaultValue = doc.g("WF_Appid");
        if (defaultValue.equals("default") || doc.isNewDoc()) {
            String sql = "select AppName,WF_Appid from BPM_AppList where  IsFolder='0'";
            String applist = Rdb.getValueForSelectTagBySql(sql, "");
            doc.s("WF_Appid", applist + ",--缺省所有应用--|default|selected");
        }
        else {
            String sql = "select AppName,WF_Appid from BPM_AppList  where  IsFolder='0'";
            String applist = Rdb.getValueForSelectTagBySql(sql, defaultValue);
            doc.s("WF_Appid", applist + ",--缺省所有应用--|default");
        }

        return "1"; // 成功必须返回1，否则表退出并显示返回的字符串
    }

    public String onFormSave(Document doc, Document formDoc) {
        // 当表单存盘前
        String appid = doc.g("WF_Appid");
        int spos = appid.indexOf(",");
        if (spos != -1) {
            // 说明有多个应用编号的字段，只取第一个字段的值
            appid = appid.substring(0, spos);
            doc.s("WF_Appid", appid);
        }

        return "1"; // 成功必须返回1，否则表示退出存盘
    }

}