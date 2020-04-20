package cn.linkey.rulelib.S001;

import java.util.*;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.app.AppUtil;

/**
 * 新增编辑控件表单事件
 * 
 * @author Administrator
 * 
 */
public class R_S001_E020 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) {
        // 获取事件运行参数
        Document formDoc = (Document) params.get("FormDoc"); // 表单配置文档
        Document doc = (Document) params.get("DataDoc"); // 数据主文档 dddd
        String eventName = (String) params.get("EventName");// 事件名称
        if (eventName.equals("onFormOpen")) {
            String readOnly = (String) params.get("ReadOnly"); // 1表示只读，0表示编辑
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

            String appid = BeanCtx.g("WF_Appid", true);
            String sql = "select Controlid from BPM_GridControlList where WF_Appid='" + appid + "' order by Controlid desc";
            String newGridNum = AppUtil.getElNewNum(sql);
            if (Tools.isBlank(newGridNum)) {
                newGridNum = "V_" + appid + "_C001";
            }
            doc.s("Controlid", newGridNum);
        }
        return "1"; // 成功必须返回1，否则表退出并显示返回的字符串
    }

    public String onFormSave(Document doc, Document formDoc) {
        // 当表单存盘前
        doc.s("WF_Version", "1.0");
        return "1"; // 成功必须返回1，否则表示退出存盘
    }

}