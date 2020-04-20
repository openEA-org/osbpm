package cn.linkey.rulelib.S014;

import java.util.*;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:文档信息获得锁定者
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-14 23:17
 */
final public class R_S014_E003 implements LinkeyRule {

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
        //当表单打开时
        //if(readOnly.equals("1")){return "1";} //如果是阅读状态则可不执行
        if (doc.isNewDoc()) {
            //可以对表单字段进行初始化如:doc.s("fdname",fdvalue),可以获取字段值 doc.g("fdname")

        }
        String sql = "select * from BPM_LockDocList where DocUnid='" + doc.getDocUnid() + "'";
        //		BeanCtx.out(sql);
        Document ldoc = Rdb.getDocumentBySql(sql);
        if (!ldoc.isNull()) {
            doc.s("LockUserid", ldoc.g("Userid"));
            doc.s("LockTime", ldoc.g("WF_DocCreated"));
        }
        return "1"; //成功必须返回1，否则表示退出并显示返回的字符串
    }

    public String onFormSave(Document doc, Document formDoc) {
        //当表单存盘前
        String sql = "delete from BPM_LockDocList where DocUnid='" + BeanCtx.g("WF_DocUnid", true) + "'";
        Rdb.execSql(sql);
        return "成功解除锁定"; //成功必须返回1，否则表示退出存盘
    }

}