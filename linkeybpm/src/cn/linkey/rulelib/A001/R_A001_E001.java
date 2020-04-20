package cn.linkey.rulelib.A001;

import java.util.HashMap;
import java.util.LinkedHashSet;

import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.MessageUtil;
import cn.linkey.util.Tools;

/**
 * @RuleName:提交发送消息
 * @author admin
 * @version: 8.0
 * @Created: 2015-05-04 09:07
 */
final public class R_A001_E001 implements LinkeyRule {

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
        String action = BeanCtx.g("WF_Action", true);
        if (action.equalsIgnoreCase("quit")) {
            doc.s("Status", "1"); //1表示已发送

            //提交并发送
            String sendToUserid = doc.g("SendToUserid");
            LinkedHashSet<String> userSet = BeanCtx.getLinkeyUser().parserUserid(sendToUserid);//分析支持部门，角色等
            for (String userid : userSet) {
                Document rdoc = BeanCtx.getDocumentBean("APP_A001_ReceiveList");
                rdoc.s("Subject", doc.g("Subject"));
                rdoc.s("Body", doc.g("Body"));
                rdoc.s("WF_AddName", BeanCtx.getUserid());
                rdoc.s("WF_AddName_CN", BeanCtx.getUserName());
                rdoc.s("ReadFlag", "0");
                rdoc.s("ReceiveUserid", userid);
                rdoc.s("ParentDocUnid", doc.getDocUnid()); //记录发送文档的unid用来显示附件或增加阅读记录时使用
                rdoc.save();
            }

            //发送手机短信通知
            if (doc.g("SendSmsFlag").equals("1")) {
                MessageUtil messageUtil = (MessageUtil) BeanCtx.getBean("MessageUtil");
                messageUtil.sendSms(BeanCtx.getUserid(), Tools.join(userSet, ","), "您有一个新消息[" + doc.g("Subject") + "]");
            }
        }
        else {
            //保存为草稿
            doc.s("Status", "0"); //0表示草稿
        }
        return "1"; //成功必须返回1，否则表示退出存盘
    }

}