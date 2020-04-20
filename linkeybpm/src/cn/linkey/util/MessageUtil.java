package cn.linkey.util;

import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import com.qq.weixin.mp.aes.WeiXinUtil;

/**
 * 消息发送工具类
 * 
 * @author Administrator
 *
 */
public class MessageUtil {
    /**
     * 发送微信
     * 
     * @param userid 发送者的userid
     * @param sendTo 接收者的userid多个用逗号分隔
     * @param smsBody 内容
     * @return
     */
    public int sendWeiXin(String userid, String sendTo, String smsBody) {
        String[] userArray = sendTo.split(",");
        int i = 0;
        for (String sendToUserid : userArray) {
            String jsonStr = WeiXinUtil.getNewsJsonStr(smsBody, "来自:" + BeanCtx.getLinkeyUser().getCnName(sendToUserid), "", "");
            WeiXinUtil.sendNews(sendToUserid, "", "", "1", jsonStr);
            i++;
        }
        return i;
    }

    /**
     * 发送手机短信
     * 
     * @param userid 发送者的userid
     * @param sendTo 接收者的userid多个用逗号分隔
     * @param smsBody 内容
     * @return
     */
    public int sendSms(String userid, String sendTo, String smsBody) {
        String[] userArray = sendTo.split(",");
        int i = 0;
        for (String sendToUserid : userArray) {
            Document smsDoc = BeanCtx.getDocumentBean("BPM_SmsBox");
            smsDoc.s("SmsBody", smsBody);
            smsDoc.s("SendTo", sendToUserid);
            smsDoc.s("Userid", BeanCtx.getUserid());
            smsDoc.save();
            i++;
        }
        return i;
    }

    /**
     * 发送邮件
     * 
     * @param appid 应用id
     * @param userid
     * @param sendTo
     * @param copyTo
     * @param subject
     * @param Body
     * @return
     */
    public int sendMail(String appid, String userid, String sendTo, String copyTo, String subject, String body) {
        Document mailDoc = BeanCtx.getDocumentBean("BPM_MailBox");
        mailDoc.s("WF_Appid", appid);
        mailDoc.s("MailTitle", subject);
        mailDoc.s("MailBody", body);
        mailDoc.s("SendTo", sendTo);
        mailDoc.s("CopyTo", copyTo);
        mailDoc.s("Userid", BeanCtx.getUserid());
        return mailDoc.save();
    }
}
