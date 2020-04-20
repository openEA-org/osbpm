package cn.linkey.rulelib.S005;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:Word打印
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-10 15:29
 */
final public class R_S005_E009 implements LinkeyRule {

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
        if (readOnly.equals("1")) {
            return "1";
        } //如果是阅读状态则可不执行
        if (doc.isNewDoc()) {
            //可以对表单字段进行初始化如:doc.s("fdname",fdvalue),可以获取字段值 doc.g("fdname")

        }
        //获取Word打印模板路径
        String WordDocUrl = "";
        String Processid = doc.g("Processid");
        String DocUnid = doc.g("DocUnid");

        if (!Processid.equals("")) {
            //获取流程设置的Word打印模版
            String sql = "select PrintTemplate from BPM_ModProcessList where Processid='" + Processid + "' and Nodeid='Process' and NodeType='Process'";
            Document pdoc = Rdb.getDocumentBySql(sql);
            if (pdoc.isNull()) {
                //BeanCtx.out(sql);
            }
            else {
                sql = "select * from BPM_AttachmentsList where DocUnid='" + pdoc.g("PrintTemplate") + "'";
                Document fdoc = Rdb.getDocumentBySql(sql);
                if (fdoc.isNull()) {
                    //BeanCtx.out(sql);
                }
                else {
                    String FileName = fdoc.g("FileName");
                    WordDocUrl = fdoc.g("FilePath") + fdoc.g("WF_OrUnid") + FileName.substring(FileName.indexOf("."));
                }
            }

        }
        doc.s("WordDocUrl", WordDocUrl);

        return "1"; //成功必须返回1，否则表示退出并显示返回的字符串
    }

    public String onFormSave(Document doc, Document formDoc) {
        //当表单存盘前

        return "1"; //成功必须返回1，否则表示退出存盘
    }

}