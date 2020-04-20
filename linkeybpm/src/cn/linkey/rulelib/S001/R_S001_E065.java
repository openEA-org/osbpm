package cn.linkey.rulelib.S001;

import java.util.HashMap;

import cn.linkey.app.AppUtil;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:流程数据流出视图事件
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-24 10:46
 */
final public class R_S001_E065 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) {
        Document gridDoc = (Document) params.get("GridDoc"); //grid配置文档
        Document doc = (Document) params.get("DataDoc"); //数据主文档
        String eventName = (String) params.get("EventName");//事件名称
        if (eventName.equals("onGridOpen")) {
            return onGridOpen(gridDoc);
        }
        else if (eventName.equals("onDocDelete")) {
            return onDocDelete(doc, gridDoc);
        }
        else if (eventName.equals("onDocCopy")) {
            return onDocCopy(doc, gridDoc);
        }
        else if (eventName.equals("onBtnClick")) {
            return onBtnClick(doc, gridDoc);
        }
        return "1";
    }

    public String onGridOpen(Document gridDoc) {
        //如果不返回1则表示退出本视图并输出返回的字符串
        //通过gridDoc.s("WF_SearchBar","自定义操作条上的搜索框HTML代码");

        return "1";
    }

    public String onDocDelete(Document doc, Document gridDoc) {
        //如果不返回1则表示退出本次删除操作，并弹出返回的字符串为提示消息

        return "1";
    }

    public String onDocCopy(Document doc, Document gridConfigDoc) {
        // 如果不返回1则表示退出本次拷贝操作，并弹出返回的字符串为提示消息
        BeanCtx.setCtxData("WF_NoEncode", "1"); // 指定存盘时不进行编码操作
        String appid = doc.g("WF_Appid");
        String sql = "select Dataid from BPM_ProcessMapData where WF_Appid='" + appid + "' order by Dataid desc";
        String newJsonid = AppUtil.getElNewNum(sql);
        if (Tools.isBlank(newJsonid)) {
            newJsonid = doc.g("Dataid") + "(Copy)";
        }
        doc.s("DataName", doc.g("DataName") + "(copy)");
        doc.s("Dataid", newJsonid);
        return "1";
    }

    public String onBtnClick(Document doc, Document gridDoc) {
        //返回操作完成后的提示信息
        //String action=BeanCtx.g("WF_Btnid"); 获得按扭编号

        return "";
    }

}