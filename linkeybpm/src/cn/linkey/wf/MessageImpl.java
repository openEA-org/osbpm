package cn.linkey.wf;

import java.util.HashSet;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.util.Tools;

/**
 * 本类为工作流引擎消息发类
 * 
 * @author Administrator
 *
 */
public class MessageImpl {

    /**
     * 发送节点上配置的基于Action动作的邮件
     */
    public int sendActionMail(String actionid) {
        //2.发节点上的配置邮件
        HashSet<String> mailReaders = new HashSet<String>();
        NodeUser insNodeUser = (NodeUser) BeanCtx.getBean("NodeUser");
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        if (Tools.isBlank(linkeywf.getCurrentNodeid())) {
            return 0;
        }
        String processid = linkeywf.getProcessid();
        String sql = "select * from BPM_MailConfig where Processid='" + processid + "' and Nodeid='" + linkeywf.getCurrentNodeid() + "' and (Actionid='" + actionid + "' or Actionid='*')";
        Document mailConfigDoc = Rdb.getDocumentBySql(sql);
        if (mailConfigDoc.isNull()) {
            return 0;
        }

        //created mail document
        String mailTitle = mailConfigDoc.g("MailTitle");
        String mailBody = mailConfigDoc.g("MailBody");
        String sendTo = mailConfigDoc.g("SendTo");
        String copyTo = mailConfigDoc.g("CopyTo");
        mailTitle = Tools.parserStrByDocument(linkeywf.getDocument(), mailTitle);
        mailBody = mailBody.replace("{DOCLINK}", "<a href='" + getDocLink() + "' target=_blank><u>点击打开</u></a>");
        mailBody = Tools.parserStrByDocument(linkeywf.getDocument(), mailBody);

        //解析主送和抄送中的规则编号
        HashSet<String> sendToList = Tools.splitAsSet(sendTo);
        for (String userItem : sendToList) {
            if (userItem.startsWith("R_")) {
                sendToList.remove(userItem);
                try {
                    String ruleUserList = BeanCtx.getExecuteEngine().run(userItem); //运行规则并返回字符串作为收件人
                    sendToList.add(ruleUserList);
                }
                catch (Exception e) {
                    BeanCtx.log(e, "E", "解析邮件收件人规则时出错!");
                }
            }
        }
        sendTo = Tools.join(sendToList, ",");
        if (Tools.isNotBlank(sendTo)) {
            sendTo = insNodeUser.parserStrForNodeUser(sendTo, linkeywf.getDocUnid()); //分析{Node.Nodeid}的节点用户
            sendTo = Tools.parserStrByDocument(linkeywf.getDocument(), sendTo); //分析文档字段
            HashSet<String> sendToSet = Tools.splitAsSet(sendTo);
            mailReaders.addAll(sendToSet);
            sendTo = Tools.join(sendToSet, ","); //去掉重复值
        }

        //解析抄送 
        if (Tools.isNotBlank(copyTo)) {
            HashSet<String> copyToList = Tools.splitAsSet(copyTo);
            for (String userItem : copyToList) {
                if (userItem.startsWith("R_")) {
                    copyToList.remove(userItem);
                    try {
                        String ruleUserList = BeanCtx.getExecuteEngine().run(userItem); //运行规则并返回字符串作为收件人
                        copyToList.add(ruleUserList);
                    }
                    catch (Exception e) {
                        BeanCtx.log(e, "E", "解析邮件收件人规则时出错!");
                    }
                }
            }
            copyTo = Tools.join(copyToList, ",");
            if (Tools.isNotBlank(copyTo)) {
                copyTo = insNodeUser.parserStrForNodeUser(copyTo, linkeywf.getDocUnid()); //分析{Node.Nodeid}的节点用户
                copyTo = Tools.parserStrByDocument(linkeywf.getDocument(), copyTo); //分析文档字段
                HashSet<String> copyToSet = Tools.splitAsSet(copyTo);
                mailReaders.addAll(copyToSet);
                copyTo = Tools.join(copyToSet, ","); //去掉重复值
            }
        }

        //加入主文档中的读者域中去
        linkeywf.getDocument().appendTextList("WF_AllReaders", mailReaders);

        //创建邮件文档
        if (Tools.isNotBlank(sendTo) || Tools.isNotBlank(copyTo)) {
            Document mailDoc = BeanCtx.getDocumentBean("BPM_MailBox");
            mailDoc.s("WF_Appid", linkeywf.getDocument().g("WF_Appid"));
            mailDoc.s("MailTitle", mailTitle);
            mailDoc.s("MailBody", mailBody);
            mailDoc.s("SendTo", sendTo);
            mailDoc.s("CopyTo", copyTo);
            mailDoc.s("Userid", BeanCtx.getUserid());
            mailDoc.s("Processid", linkeywf.getProcessid());
            mailDoc.s("DocUnid", linkeywf.getDocUnid());
            return mailDoc.save();
        }
        return 0;
    }

    /**
     * 发送路由节点上配置的待办邮件
     */
    public void sendRouterMail() {
        HashSet<Document> maildc = BeanCtx.getLinkeywf().getMaildc();
        for (Document routerDoc : maildc) {
            sendNodeMail(routerDoc);
        }
    }

    /**
     * 发送自动活动的邮件
     */
    public int sendNodeMail(Document nodeDoc) {
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        NodeUser insNodeUser = (NodeUser) BeanCtx.getBean("NodeUser");
        HashSet<String> mailReaders = new HashSet<String>();

        //获得邮件信息
        String mailTitle = nodeDoc.g("MailTitle");
        String mailBody = nodeDoc.g("MailBody");
        String sendTo = nodeDoc.g("SendTo");
        String copyTo = nodeDoc.g("CopyTo");
        mailTitle = Tools.parserStrByDocument(linkeywf.getDocument(), mailTitle);
        mailBody = mailBody.replace("{DOCLINK}", "<a href='" + getDocLink() + "' target=_blank><u>点击打开</u></a>");
        mailBody = Tools.parserStrByDocument(linkeywf.getDocument(), mailBody);

        //解析主送和抄送中的规则编号
        HashSet<String> sendToList = Tools.splitAsSet(sendTo);
        for (String userItem : sendToList) {
            if (userItem.startsWith("R_")) {
                sendToList.remove(userItem);
                try {
                    String ruleUserList = BeanCtx.getExecuteEngine().run(userItem); //运行规则并返回字符串作为收件人
                    sendToList.add(ruleUserList);
                }
                catch (Exception e) {
                    BeanCtx.log(e, "E", "解析邮件收件人规则时出错!");
                }
            }
        }
        sendTo = Tools.join(sendToList, ",");
        sendTo = insNodeUser.parserStrForNodeUser(sendTo, linkeywf.getDocUnid()); //分析{Node.Nodeid}的节点用户
        sendTo = Tools.parserStrByDocument(linkeywf.getDocument(), sendTo);
        HashSet<String> sendToSet = Tools.splitAsSet(sendTo);
        mailReaders.addAll(sendToSet);
        sendTo = Tools.join(sendToSet, ","); //去掉重复值

        //解析抄送 
        if (Tools.isNotBlank(copyTo)) {
            HashSet<String> copyToList = Tools.splitAsSet(copyTo);
            for (String userItem : copyToList) {
                if (userItem.startsWith("R_")) {
                    copyToList.remove(userItem);
                    try {
                        String ruleUserList = BeanCtx.getExecuteEngine().run(userItem); //运行规则并返回字符串作为收件人
                        copyToList.add(ruleUserList);
                    }
                    catch (Exception e) {
                        BeanCtx.log(e, "E", "解析邮件收件人规则时出错!");
                    }
                }
            }
            copyTo = Tools.join(copyToList, ",");
            copyTo = insNodeUser.parserStrForNodeUser(copyTo, linkeywf.getDocUnid()); //分析{Node.Nodeid}的节点用户
            copyTo = Tools.parserStrByDocument(linkeywf.getDocument(), copyTo);
            HashSet<String> copyToSet = Tools.splitAsSet(copyTo);
            mailReaders.addAll(copyToSet);
            copyTo = Tools.join(copyToSet, ","); //去掉重复值
        }

        //加入到主文档中的读者域中去
        linkeywf.getDocument().appendTextList("WF_AllReaders", mailReaders);

        //创建邮件文档
        Document mailDoc = BeanCtx.getDocumentBean("BPM_MailBox");
        mailDoc.s("WF_Appid", linkeywf.getDocument().g("WF_Appid"));
        mailDoc.s("MailTitle", mailTitle);
        mailDoc.s("MailBody", mailBody);
        mailDoc.s("SendTo", sendTo);
        mailDoc.s("CopyTo", copyTo);
        mailDoc.s("Userid", BeanCtx.getUserid());
        mailDoc.s("Processid", linkeywf.getProcessid());
        mailDoc.s("DocUnid", linkeywf.getDocUnid());
        return mailDoc.save();
    }

    public String getDocLink() {
        String url = BeanCtx.getSystemConfig("HttpServerUrl") + "/rule?wf_num=R_S003_B052&wf_docunid=" + BeanCtx.getLinkeywf().getDocUnid();
        return url;
    }

    /**
     * 发送手机短信通知
     * 
     * @param actionid 引擎动作id
     * @return 返回>0表示发送成功
     */
    public int sendSms(String actionid) {
        int i = 0;
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        String processid = linkeywf.getProcessid();
        HashSet<Document> dc = Rdb.getAllDocumentsSetBySql("select * from BPM_SmsConfig where Actionid='" + actionid + "' or Actionid='' or Actionid is null");
        //没有配置动作，直接退出
        if (dc.size() < 1) {
            return i;
        }

        //查找针对本流程有可能的配置文档
        Document smsConfigDoc = null;
        for (Document doc : dc) {
            String processidList = doc.g("Processid");
            if (processidList.indexOf(processid) != -1) {
                //找到了针对本流程的单独邮件配置文档
                smsConfigDoc = doc;
                break;
            }
            if (Tools.isBlank(processidList)) {
                smsConfigDoc = doc;
            }
        }

        if (smsConfigDoc != null) {
            String smsBody = smsConfigDoc.g("SmsBody");
            String sendTo = smsConfigDoc.g("SendTo");
            sendTo = Tools.parserStrByDocument(linkeywf.getDocument(), sendTo);
            smsBody = Tools.parserStrByDocument(linkeywf.getDocument(), smsBody);

            //创建短信文档
            String approvalFlag = "0", nodeid = "";
            if (linkeywf.getCurrentModNodeDoc() != null) {
                nodeid = linkeywf.getCurrentNodeid();
                if (linkeywf.getCurrentModNodeDoc().g("ApprovalFlag").equals("1")) {
                    approvalFlag = "1";
                }
            }
            Document smsDoc = BeanCtx.getDocumentBean("BPM_SmsBox");
            smsDoc.s("SmsBody", smsBody);
            smsDoc.s("ApprovalFlag", approvalFlag);
            smsDoc.s("SendTo", sendTo);
            smsDoc.s("Nodeid", nodeid);
            smsDoc.s("DocSubject", linkeywf.getDocument().g("Subject"));
            smsDoc.s("Userid", BeanCtx.getUserid());
            smsDoc.s("Processid", linkeywf.getProcessid());
            smsDoc.s("DocUnid", linkeywf.getDocUnid());
            return smsDoc.save();
        }
        return i;
    }

    /**
     * 发送环节手机短信通知,要求流程节点中有配置邮件的发送字段才可以
     * 
     * @param nodeDoc为环节文档
     * @return 返回>0表示发送成功
     */
    public int sendNodeSms(Document nodeDoc) {
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();

        String smsBody = nodeDoc.g("SmsBody");
        String sendTo = nodeDoc.g("SmsSendTo");
        sendTo = Tools.parserStrByDocument(linkeywf.getDocument(), sendTo);
        smsBody = Tools.parserStrByDocument(linkeywf.getDocument(), smsBody);

        //创建短信文档
        Document smsDoc = BeanCtx.getDocumentBean("BPM_SmsBox");
        smsDoc.s("SmsBody", smsBody);
        smsDoc.s("ApprovalFlag", "0");
        smsDoc.s("SendTo", sendTo);
        smsDoc.s("Nodeid", nodeDoc.g("Nodeid"));
        smsDoc.s("DocSubject", linkeywf.getDocument().g("Subject"));
        smsDoc.s("Userid", BeanCtx.getUserid());
        smsDoc.s("Processid", linkeywf.getProcessid());
        smsDoc.s("DocUnid", linkeywf.getDocUnid());
        return smsDoc.save();

    }

    public void sendToDo(String actionid, String userid, boolean isToDo) {
        //发送到数据库中间表
        String processid = BeanCtx.getLinkeywf().getProcessid();
        String docUnid = BeanCtx.getLinkeywf().getDocUnid();
        String nodeName = BeanCtx.getLinkeywf().getCurrentNodeName();
        String subject = BeanCtx.getLinkeywf().getDocument().g("Subject");
        String toDoType = isToDo ? "1" : "0";
        String[] userArray = Tools.split(userid);
        for (String userItem : userArray) {
            Document doc = BeanCtx.getDocumentBean("BPM_ToDoBox");
            doc.s("WF_Appid", BeanCtx.getLinkeywf().getDocument().g("WF_Appid"));
            doc.s("Processid", processid);
            doc.s("DocUnid", docUnid);
            doc.s("Userid", BeanCtx.getUserid());
            doc.s("SendTo", userItem);
            doc.s("NodeName", nodeName);
            doc.s("Status", "1");
            doc.s("Subject", subject);
            doc.s("ToDoType", toDoType);
            doc.save();
        }
    }

    public void cancelToDo(String docUnid, String userid) {
        //把中间表的数据标识为3表示需要取消的待办
        String[] userArray = Tools.split(userid);
        for (String userItem : userArray) {
            String sql = "update BPM_ToDoBox set Status='3' where DocUnid='" + docUnid + "' and userid='" + userItem + "'";
            Rdb.execSql(sql);
        }
    }

    /**
     * 取消文档的所有待办
     * 
     * @param docUnid 要取消文档unid
     * @param deleteAll true表示删除全部,false表示删除userid用户的待办
     */
    public void cancelToDo(String docUnid) {
        //把中间表的数据标识为3表示需要取消的待办
        String sql = "update BPM_ToDoBox set Status='3' where DocUnid='" + docUnid + "'";
        Rdb.execSql(sql);
    }

}
