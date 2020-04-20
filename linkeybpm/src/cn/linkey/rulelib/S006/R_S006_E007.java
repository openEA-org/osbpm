package cn.linkey.rulelib.S006;

import java.util.HashMap;

import cn.linkey.app.AppUtil;
import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:注册岗位事件
 * @author admin
 * @version: 8.0
 * @Created: 2014-04-23 14:48
 */
final public class R_S006_E007 implements LinkeyRule {

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

            String sql = "select RoleNumber from BPM_OrgRoleList where RoleType='2' order by RoleNumber desc";
            String newNum = AppUtil.getElNewNum(sql);
            if (Tools.isBlank(newNum)) {
                newNum = "RG001";
            }
            doc.s("RoleNumber", newNum);

        }

        return "1"; // 成功必须返回1，否则表退出并显示返回的字符串
    }

    public String onFormSave(Document doc, Document formDoc) {
        // 当表单存盘前
        doc.s("WF_Appid", "default");

        return "1"; // 成功必须返回1，否则表示退出存盘
    }

}