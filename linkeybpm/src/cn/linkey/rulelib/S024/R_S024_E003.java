package cn.linkey.rulelib.S024;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.wf.ModNode;

/**
 * @RuleName:Word流程表单打印
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-10 15:29
 */
final public class R_S024_E003 implements LinkeyRule {

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
        //获取Word打印模板路径
        String WordDocUrl = "";
        String processid = BeanCtx.g("Processid", true); //表单的编号
        String docUnid = BeanCtx.g("DocUnid", true); //文档的编号
        ModNode modNode = (ModNode) BeanCtx.getBean("ModNode");
        Document nodeDoc = modNode.getNodeDoc(processid, "Process");
        String printTemplate = nodeDoc.g("PrintTemplate"); //获得流程中设置的打印模板
        if (Tools.isNotBlank(printTemplate)) {
            //说明设置了打印模板,需要获得打印模板的url地址
            String sql = "select * from BPM_AttachmentsList where DocUnid='" + printTemplate + "'";
            Document fdoc = Rdb.getDocumentBySql(sql);
            String FileName = fdoc.g("FileName");
            WordDocUrl = fdoc.g("FilePath") + fdoc.g("WF_OrUnid") + FileName.substring(FileName.indexOf("."));
        }

        doc.s("WordDocUrl", WordDocUrl);

        return "1"; //成功必须返回1，否则表示退出并显示返回的字符串
    }

    public String onFormSave(Document doc, Document formDoc) {
        //当表单存盘前

        return "1"; //成功必须返回1，否则表示退出存盘
    }

}