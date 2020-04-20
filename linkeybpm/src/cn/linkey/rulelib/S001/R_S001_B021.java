package cn.linkey.rulelib.S001;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * 获得本应用的所有事件规则的json
 * 
 * @author Administrator
 * 
 */
public class R_S001_B021 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        String appid = BeanCtx.g("appid", true);
        String eventType = BeanCtx.g("EventType", true); // 事件类型
        String sql = "";
        if (Tools.isBlank(appid)) {
            sql = "select RuleName,RuleNum,WF_Appid from BPM_RuleList where EventType='" + eventType + "' order by RuleNum";
        }
        else {
            sql = "select RuleName,RuleNum,WF_Appid from BPM_RuleList where WF_Appid='" + appid + "' and EventType='" + eventType + "' order by RuleNum";
        }
        Document[] dc = Rdb.getAllDocumentsBySql("BPM_RuleList", sql);
        Document newdoc = BeanCtx.getDocumentBean("BPM_RuleList");
        newdoc.s("RuleName", "-No Event-");
        newdoc.s("RuleNum", "");
        dc = Documents.addDoc(dc, newdoc);
        String jsonStr = Documents.dc2json(dc, "", true);
        BeanCtx.print(jsonStr); // 输出json字符串

        return "";
    }
}
