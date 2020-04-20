package cn.linkey.rulelib.S001;

import java.util.*;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.app.*;

/**
 * @RuleName:过滤器规则视图拷贝事件
 * @author admin
 * @version: 8.0
 * @Created: 2014-04-01 18:04
 */
public class R_S001_E049 implements LinkeyRule {

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

    public String onDocCopy(Document doc, Document gridDoc) {
        //如果不返回1则表示退出本次拷贝操作，并弹出返回的字符串为提示消息
        BeanCtx.setCtxData("WF_NoEncode", "1"); // 指定存盘时不进行编码操作

        String appid = doc.g("WF_Appid");
        String sql = "select RuleNum from BPM_RuleList where WF_Appid='" + appid + "' and RuleType='6' order by RuleNum desc";
        String newNum = AppUtil.getElNewNum(sql);
        if (Tools.isBlank(newNum)) {
            newNum = doc.g("RuleNum") + "(Copy)";
        }
        String ruleCode = doc.g("RuleCode");
        ruleCode = ruleCode.replace(doc.g("RuleNum"), newNum); //替换规则中的类名称
        doc.s("RuleCode", ruleCode);
        doc.s("RuleName", doc.g("RuleName") + "(copy)");
        doc.s("RuleNum", newNum);
        return "1";
    }

    public String onBtnClick(Document doc, Document gridDoc) {
        //返回成功的提示信息

        return "";
    }

}