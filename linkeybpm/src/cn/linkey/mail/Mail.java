package cn.linkey.mail;

import java.util.Date;
import java.util.Properties;
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
import javax.activation.*;

import cn.linkey.factory.BeanCtx;
import cn.linkey.util.Tools;
import java.io.File;

public class Mail {

    private String host = ""; // smtp服务器的地址 smtp.163.com
    private String from = ""; //发件人的地址 test@163.com
    private String personalName = "BPM业务流程管理平台";//相当于称呼，通常显示在你的发件人栏的发件人邮箱地址前
    private String smtpUserid = ""; //发送帐号 test@163.com
    private String smtpPwd = ""; //发送密码 

    /**
     * 发送邮件
     * 
     * @param mail_to 主送多个用户用逗号分隔
     * @param copy_to 抄送多个用户用逗号分隔
     * @param mail_subject 邮件标题
     * @param mail_body 邮件内容
     * @throws SendFailedException
     */
    public void sendMail(String mail_to, String copy_to, String mail_subject, String mail_body) throws SendFailedException {
        try {
            Properties props = new Properties();// 获取系统环境
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.auth", "true");
            Authenticator auth = new Email_Autherticator(smtpUserid, smtpPwd);// 进行邮件服务用户认证
            Session session = Session.getInstance(props, auth);// 设置session,和邮件服务器进行通讯
            MimeMessage message = new MimeMessage(session);

            Multipart multipart = new MimeMultipart();
            MimeBodyPart messageBodyPart = new MimeBodyPart();

            messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(transHtml(mail_body), "text/html; charset=utf-8");
            multipart.addBodyPart(messageBodyPart);

            message.setContent(multipart);
            message.saveChanges();

            message.setSubject(mail_subject);// 设置邮件主题
            // message.setHeader("Content-Type","text/html;charset=utf-8"); //这里不能写，写了后有乱码
            message.setSentDate(new Date());// 设置邮件发送时期
            Address address = new InternetAddress(from, personalName);
            message.setFrom(address);// 设置邮件发送者的地址
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mail_to));
            if (Tools.isNotBlank(copy_to)) {
                message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(copy_to));
            }
            Transport.send(message);

        }
        catch (Exception e) {
            System.out.println("SMTP邮件发送失败！");
            e.printStackTrace();
        }
    }

    /**
     * 发送邮件和附件
     * 
     * @param mail_to 主送多个用户用逗号分隔
     * @param copy_to 抄送多个用户用逗号分隔
     * @param mail_subject 邮件标题
     * @param mail_body 邮件内 容
     * @param fileList 附件全路径地址多个用逗号分隔如：d:/1.gif,d:/2.txt
     * @throws SendFailedException
     */
    public void sendMailForAttachment(String mail_to, String copy_to, String mail_subject, String mail_body, String fileList) throws SendFailedException {
        try {
            String fileUrl;
            Properties props = new Properties();// 获取系统环境
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.auth", "true");
            Authenticator auth = new Email_Autherticator();// 进行邮件服务用户认证
            Session session = Session.getInstance(props, auth);// 设置session,和邮件服务器进行通讯
            MimeMessage message = new MimeMessage(session);
            Multipart multipart = new MimeMultipart();
            MimeBodyPart messageBodyPart = new MimeBodyPart();

            if (fileList != "") {
                String[] array = fileList.split(",");
                if (array.length > 0) {
                    for (int i = 0; i < array.length; i++) {
                        fileUrl = array[i];
                        messageBodyPart = new MimeBodyPart();
                        File localFile = new File(fileUrl);
                        DataSource source = new FileDataSource(localFile);
                        messageBodyPart.setDataHandler(new DataHandler(source));
                        messageBodyPart.setFileName(MimeUtility.encodeText(localFile.getName()));
                        multipart.addBodyPart(messageBodyPart);
                    }
                }
            }

            messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(transHtml(mail_body), "text/html; charset=utf-8");
            multipart.addBodyPart(messageBodyPart);

            // Put parts in message
            message.setContent(multipart);
            message.saveChanges();
            message.setSubject(mail_subject);// 设置邮件主题
            message.setSentDate(new Date());// 设置邮件发送时期
            Address address = new InternetAddress(from, personalName);
            message.setFrom(address);// 设置邮件发送者的地址
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mail_to));//接收者
            if (Tools.isNotBlank(copy_to)) {
                message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(copy_to));
            }
            Transport.send(message);

        }
        catch (Exception e) {
            BeanCtx.log(e, "W", "邮件发送发送失败！");
        }
    }

    private String transHtml(String htmlCode) {
        htmlCode = htmlCode.replaceAll("&lt;", "<");
        htmlCode = htmlCode.replaceAll("&gt;", ">");
        htmlCode = htmlCode.replaceAll("&quot;", "\"");
        htmlCode = htmlCode.replaceAll("&amp;", "&");
        return htmlCode;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String mail_from) {
        this.from = mail_from;
    }

    public String getPersonalName() {
        return personalName;
    }

    public void setPersonalName(String personalName) {
        this.personalName = personalName;
    }

    public String getSmtpUserid() {
        return smtpUserid;
    }

    public void setSmtpUserid(String smtpUserid) {
        this.smtpUserid = smtpUserid;
    }

    public String getSmtpPwd() {
        return smtpPwd;
    }

    public void setSmtpPwd(String smtpPwd) {
        this.smtpPwd = smtpPwd;
    }

}
