package cn.linkey.rulelib.S014;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:文档XmlCode源码
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-15 15:15
 */
final public class R_S014_B002 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        // params为运行本规则时所传入的参数
        String docUnid = BeanCtx.g("DocUnid", true);
        String tableName = BeanCtx.g("WF_TableName", true);
        String sql = "select * from " + tableName + " where WF_OrUnid='" + docUnid + "'";
        Document doc = Rdb.getDocumentBySql(sql);
        String xmlCode = doc.toXmlStr(true);
        xmlCode = xmlCode.replace("<WFItem", "\n	<WFItem");
        xmlCode = xmlCode.replace("</Items>", "\n</Items>");
        xmlCode = xmlCode.replace("<![CDATA[function", "<![CDATA[\nfunction");
        xmlCode = xmlCode.replace("<", "&lt;");
        xmlCode = xmlCode.replace(">", "&gt;");

        sql = "select DefaultCode from BPM_DevDefaultCode where CodeType='DocXmlCodeForm'";
        String htmlCode = Rdb.getValueBySql(sql);
        htmlCode = htmlCode.replace("{DocUnid}", docUnid);
        htmlCode = htmlCode.replace("{TableName}", tableName);
        htmlCode = htmlCode.replace("{XmlBody}", xmlCode);

        BeanCtx.p(htmlCode);

        return "";
    }
}