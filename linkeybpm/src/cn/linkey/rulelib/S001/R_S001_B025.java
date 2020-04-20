package cn.linkey.rulelib.S001;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * 本规则负责存盘Xml源码文件到设计属性中
 * 
 * @author Administrator
 *
 */
public class R_S001_B025 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        BeanCtx.getResponse().setContentType("text/json;charset=utf-8");
        BeanCtx.setCtxData("WF_NoEncode", "1"); // 指定存盘时不进行编码操作
        String docUnid = BeanCtx.g("WF_elDocUnid", true);
        String tableName = BeanCtx.g("WF_ElTableName", true);
        String xmlCode = BeanCtx.g("XmlCode");
        if (Tools.isBlank(docUnid) || Tools.isBlank(tableName) || Tools.isBlank(xmlCode)) {
            BeanCtx.print(Tools.jmsg("error", "Parameter format is not correct"));
            return "";
        }
        String sql = "select * from " + tableName + " where WF_OrUnid='" + docUnid + "'";
        //BeanCtx.out(sql);
        Document doc = Rdb.getDocumentBySql(tableName, sql);
        doc.appendFromXml(xmlCode);
        //BeanCtx.out(doc.toString());
        int i = doc.save();
        if (i > 0) {
            BeanCtx.print(Tools.jmsg("ok", BeanCtx.getMsg("Common", "AppDocumentSaved")));
        }
        else {
            BeanCtx.print(Tools.jmsg("ok", BeanCtx.getMsg("Common", "AppDocumentSaveError")));
        }

        BeanCtx.userlog(docUnid, "通过XmlCode功能修改文档数据", "文档所在表为(" + tableName + ")");

        return "";
    }
}
