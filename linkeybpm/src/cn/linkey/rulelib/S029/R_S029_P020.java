package cn.linkey.rulelib.S029;

import java.util.*;
import cn.linkey.dao.*;
import cn.linkey.util.*;
import cn.linkey.doc.*;
import cn.linkey.factory.*;
import cn.linkey.wf.*;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:环节超时发送邮件通知
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-09 09:46
 */
final public class R_S029_P020 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        Document insUserDoc = (Document) params.get("UserDoc"); //获得超时的用户实例文档
        String userid = insUserDoc.g("Userid"); //获得超时的用户id
        String docUnid = insUserDoc.g("DocUnid"); //获得超时的文档id
        String processid = insUserDoc.g("Processid");//获得超时的流程id
        Document mainDoc = Rdb.getDocumentBySql("select * from BPM_MainData where WF_OrUnid='" + docUnid + "'");

        //获得邮件配置文档
        Document mailConfigDoc = Rdb.getDocumentBySql("select * from BPM_MailConfig where Actionid='OverTimeSendMail'");

        if (mailConfigDoc != null) {
            String mailTitle = mailConfigDoc.g("MailTitle");
            String mailBody = mailConfigDoc.g("MailBody");
            String sendTo = mailConfigDoc.g("SendTo");
            String copyTo = "";
            mailTitle = Tools.parserStrByDocument(mainDoc, mailTitle);
            mailBody = mailBody.replace("{DOCLINK}", ((MessageImpl) BeanCtx.getBean("MessageImpl")).getDocLink());
            mailBody = Tools.parserStrByDocument(mainDoc, mailBody);
            sendTo = sendTo.replace("admin", userid);
            copyTo = "";

            //创建邮件文档
            Document mailDoc = BeanCtx.getDocumentBean("BPM_MailBox");
            mailDoc.s("MailTitle", mailTitle);
            mailDoc.s("MailBody", mailBody);
            mailDoc.s("SendTo", sendTo);
            mailDoc.s("CopyTo", copyTo);
            mailDoc.s("Userid", BeanCtx.getUserid());
            mailDoc.s("Processid", processid);
            mailDoc.s("DocUnid", docUnid);
            mailDoc.save();
        }

        return "";

    }
}