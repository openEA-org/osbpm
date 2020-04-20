package cn.linkey.rulelib.S012;

import java.util.HashMap;
import java.util.HashSet;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:导出所有数据库表
 * @author admin
 * @version: 8.0
 * @Created: 2014-12-17 20:26
 */
final public class R_S012_E003 implements LinkeyRule {

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
        if (readOnly.equals("1")) {
            return "1";
        } //如果是阅读状态则可不执行
        if (doc.isNewDoc()) {
            //可以对表单字段进行初始化如:doc.s("fdname",fdvalue),可以获取字段值 doc.g("fdname")

        }
        return "1"; //成功必须返回1，否则表示退出并显示返回的字符串
    }

    public String onFormSave(Document doc, Document formDoc) throws Exception {
        //当表单存盘前
        String filePath = doc.g("FilePath");
        String tableList = doc.g("TableList");
        HashSet<String> tableSet = Tools.splitAsLinkedSet(tableList);
        int i = 0;
        for (String tableName : tableSet) {
            String sql = "select * from " + tableName;
            Document[] dc = Rdb.getAllDocumentsBySql(sql);
            for (Document outdoc : dc) {
                outdoc.s("WF_OrTableName", tableName);
            }
            String fileName = filePath + tableName + ".xml";
            if (!Documents.dc2Xmlfile(dc, fileName, true)) {
                break;
            }
            i++;
        }
        return "共导出(" + i + ")个表的数据到" + filePath + "中!"; //成功必须返回1，否则表示退出存盘
    }

}