package cn.linkey.rulelib.S029;

import java.util.*;
import cn.linkey.dao.*;
import cn.linkey.util.*;
import cn.linkey.doc.*;
import cn.linkey.factory.*;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.mail.*;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.*;
import javax.mail.internet.*;

/**
 * @RuleName:定时发送邮件
 * @author admin
 * @version: 8.0
 * @Created: 2014-12-22 20:44
 */

final public class R_S029_T001 implements LinkeyRule {
    private String host = "smtp.163.com"; // smtp服务器的地址
    private String from = "test@163.com"; //发件人的地址
    private String personalName = "BPM业务流程管理平台";//相当于称呼，通常显示在你的发件人栏的发件人邮箱地址前
    private String smtpUserid = "test@163.com"; //发送帐号
    private String smtpPwd = ""; //发送密码
    Session session;

    @Override
    public String run(HashMap<String, Object> params) throws Exception {

        //链接smtp服务器
        Properties props = new Properties();// 获取系统环境
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.auth", true); 
        Authenticator auth = new Email_Autherticator(smtpUserid, smtpPwd);// 进行邮件服务用户认证
        this.session = Session.getInstance(props, auth);// 设置session,和邮件服务器进行通讯

        //从邮件队例中找到所有要发送的邮件
        String sql = "select * from BPM_MailBox";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : dc) {
            String mailTitle = doc.g("MailTitle");
            String mailBody = doc.g("MailBody");
            mailBody = mailBody.replace("\r\n", "<br>");
            mailBody = Rdb.deCode(mailBody, true);

            String sendTo = BeanCtx.getLinkeyUser().getMailAddress(doc.g("SendTo"));
            String copyTo = BeanCtx.getLinkeyUser().getMailAddress(doc.g("CopyTo"));
            boolean sendFlag = sendMail(sendTo, copyTo, mailTitle, mailBody);//发送邮件
            if (sendFlag) {
                doc.remove(false);//邮件发送成功后删除邮件发送记录
                BeanCtx.out("定时发送邮件成功...");
            }
        }
        return "";
    }

    /**
     * 发送邮件
     * 
     * @param mail_to 主送多个用户用逗号分隔
     * @param copy_to 抄送多个用户用逗号分隔
     * @param mail_subject 邮件标题
     * @param mail_body 邮件内容
     * @throws SendFailedException
     */
    public boolean sendMail(String mail_to, String copy_to, String mail_subject, String mail_body) throws SendFailedException {
        try {
            MimeMessage message = new MimeMessage(session);
            Multipart multipart = new MimeMultipart();
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(mail_body, "text/html; charset=utf-8");
            multipart.addBodyPart(messageBodyPart);
            message.setContent(multipart);
            message.saveChanges();
            message.setSubject(mail_subject);// 设置邮件主题
            message.setSentDate(new Date());// 设置邮件发送时期
            Address address = new InternetAddress(from, personalName);
            message.setFrom(address);// 设置邮件发送者的地址
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mail_to));
            if (Tools.isNotBlank(copy_to)) {
                message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(copy_to));
            }
            Transport.send(message);
            return true;

        }
        catch (Exception e) {
            BeanCtx.log(e, "W", "SMTP邮件发送失败！");
            return false;
        }
    }

}