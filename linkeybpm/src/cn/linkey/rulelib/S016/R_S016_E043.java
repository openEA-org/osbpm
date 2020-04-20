package cn.linkey.rulelib.S016;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:保存业务数据分析
 * @author admin
 * @version: 8.0
 * @Created: 2014-10-29 16:57
 */
final public class R_S016_E043 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //获取事件运行参数
        Document formDoc = (Document) params.get("FormDoc"); //表单配置文档
        Document doc = (Document) params.get("DataDoc"); //数据主文档
        String eventName = (String) params.get("EventName");//事件名称

        //	System.out.println("wf_docunid:"+BeanCtx.g("wf_docunid"));

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
        //	System.out.println(doc.g("Process"));

        //float f=Float.parseFloat(doc.g("Process"));
        String sql = "select nodename from bpm_modprocesslist where processid='" + doc.g("Process") + "'";
        //  System.out.println(sql);
        if (Rdb.getCountBySql(sql) > 0)
            doc.s("Process", Rdb.getDocumentBySql(sql).g("nodename"));

        else
            doc.s("Process", doc.g("Process"));
        //doc.save("App_DataAnalyse");

        System.out.println("表单存盘前");
        System.out.println(doc.g("dg"));
        //System.out.println(doc.g("Process"));
        return "1"; //成功必须返回1，否则表示退出存盘
    }

}