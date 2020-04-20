package cn.linkey.rulelib.S026;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:指定审批用户事件
 * @author admin
 * @version: 8.0
 * @Created: 2014-09-15 20:48
 */
final public class R_S026_E004 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) throws Exception {
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

    public String onFormOpen(Document doc, Document formDoc, String readOnly) throws Exception {
        //当表单打开时
        String simProcessUnid = BeanCtx.g("SimProcessUnid", true);
        String dataDocUnid = BeanCtx.g("DataDocUnid", true);
        String nodeid = BeanCtx.g("Nodeid", true);
        String processid = BeanCtx.g("Processid", true);
        String sql = "select * from BPM_SimNodeList where SimProcessUnid='" + simProcessUnid + "' and Nodeid='" + nodeid + "'";
        Document nodeDoc = Rdb.getDocumentBySql(sql);
        if (!nodeDoc.isNull()) {
            doc.s("SubSimOrUnid", nodeDoc.g("SubSimOrUnid"));
        }

        return "1"; //成功必须返回1，否则表示退出并显示返回的字符串
    }

    public String onFormSave(Document doc, Document formDoc) throws Exception {
        //当表单存盘前
        String sql = "delete from BPM_SimNodeList where SimProcessUnid='" + doc.g("SimProcessUnid") + "' and Nodeid='" + doc.g("Nodeid") + "'";
        Rdb.execSql(sql);

        doc.s("NodeStatus", "1");

        return "1"; //成功必须返回1，否则表示退出存盘
    }

}