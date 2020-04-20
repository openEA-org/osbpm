package cn.linkey.rulelib.S006;

import java.util.HashMap;
import java.util.LinkedHashSet;

import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.org.UserModel;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:批量注册用户
 * @author admin
 * @version: 8.0
 * @Created: 2014-11-20 11:24
 */
final public class R_S006_E020 implements LinkeyRule {

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
        //从excel中批量注册用户
        BeanCtx.setCtxData("WF_NoEncode", "1"); // 指定存盘时不进行编码操作
        LinkedHashSet<String> fileList = doc.getAttachmentsNameAndPath();
        //BeanCtx.out(fileList.toString());
        if (fileList.size() == 0) {
            return "Please upload a excel file!";
        }
        String filePath = BeanCtx.getAppPath();
        String msg = "";
        UserModel um = new UserModel();
        for (String fileName : fileList) {
            String fullFilePath = filePath + fileName; //获得excel的文件名
            msg += um.batchReg(fullFilePath, doc.g("Actionid")); //指量注册
        }
        //BeanCtx.out("删除所有上传的附件...");
        doc.removeAllAttachments(true); //删除上传的临时文件

        return msg;

    }

}