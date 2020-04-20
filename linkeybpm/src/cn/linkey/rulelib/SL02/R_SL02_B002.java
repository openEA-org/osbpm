package cn.linkey.rulelib.SL02;

import java.util.*;
import cn.linkey.dao.*;
import cn.linkey.util.*;
import cn.linkey.factory.*;
import java.io.InputStream;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:微信公众号入口
 * @author Mr.Yun
 * @version: 8.0
 * @Created: 2016-08-20 10:08
 */
final public class R_SL02_B002 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        /**
         * 1、接受微信发过来的消息
         */
        String signature = BeanCtx.g("signature");
        String echostr = BeanCtx.g("echostr");
        String timestamp = BeanCtx.g("timestamp");
        String nonce = BeanCtx.g("nonce");

        /**
         * 2、微信公众号首次填写服务器url时使用
         */
        if (Tools.isNotBlank(echostr)) {
            BeanCtx.p(echostr);
            return "";
        }

        // 验证消息的合法性
        if (!checkSignature(signature, timestamp, nonce)) {
            // 消息验证失败说明不是微信发过来的请求
            return "";
        }

        /**
         * 3、开始获取微信post过来的数据
         */
        String xmlStr = getWeiXinXml();
        // 我的公众号的OpenId
        String toUserName = getXmlValue(xmlStr, "ToUserName");
        // 发送者的OpenId
        String fromUserName = getXmlValue(xmlStr, "FromUserName");
        // 消息id号
        String msgId = getXmlValue(xmlStr, "MsgId");
        // 消息类型
        String msgType = getXmlValue(xmlStr, "MsgType");
        // 消息内容
        String content = getXmlValue(xmlStr, "Content");
        // 事件类型
        String getEvent = getXmlValue(xmlStr, "Event");

        /**
         * 4、关注本公众号时发送欢迎消息
         */
        if (getEvent.equals("subscribe")) {
            sendTextMsg(fromUserName, toUserName, "欢迎关注本公众帐号,回复任意消息可获取待办!");
            return "";
        }
        else if (getEvent.equals("unsubscribe")) {
            unsubscribe(fromUserName);
            return "";
        }

        /**
         * 5、自动注册微信号
         */
        if (content.indexOf("#") != -1) {
            if (BeanCtx.getUserid().equals("Anonymous")) {
                regWeiXinid(fromUserName, toUserName, content);
            }
            else {
                sendTextMsg(fromUserName, toUserName, BeanCtx.getUserName() + "您已经注册过了,不用重复注册!");
            }
            return "";
        }

        /**
         * 6、根据微信id获得用户的真实userid
         */
        getUseridByWeiXinid(fromUserName, toUserName);
        BeanCtx.out("userid=" + BeanCtx.getUserid());

        /**
         * 7、发送待办消息 条件：不为匿名用户，且发送的消息包含MyToDo、待办、我的待办等字段
         */
        if (!BeanCtx.getUserid().equals("Anonymous")) {
            if (content.toUpperCase().contains("MyToDo".toUpperCase()) || content.contains("待办")) {
                getToDoList(fromUserName, toUserName);
            }
        }

        return "";
    }

    /**
     * 获得微信post过来的数据内容
     * 
     * @return
     * @throws Exception
     */
    public String getWeiXinXml() throws Exception {
        InputStream is = BeanCtx.getRequest().getInputStream();
        // 取HTTP请求流长度
        int size = BeanCtx.getRequest().getContentLength();
        // 用于缓存每次读取的数据
        byte[] buffer = new byte[size];
        // 用于存放结果的数组
        byte[] xmldataByte = new byte[size];
        int count = 0;
        int rbyte = 0;
        // 循环读取
        while (count < size) {
            // 每次实际读取长度存于rbyte中
            rbyte = is.read(buffer);
            for (int i = 0; i < rbyte; i++) {
                xmldataByte[count + i] = buffer[i];
            }
            count += rbyte;
        }
        is.close();

        return new String(xmldataByte, "UTF-8");
    }

    /**
     * 用字符串方法从微信发送来的xml数据中分析数据内容
     * 
     * @param xmlStr
     * @param fieldName
     * @return
     */
    public String getXmlValue(String xmlStr, String fieldName) {
        String startCode = "<" + fieldName + ">";
        String endCode = "</" + fieldName + ">";
        String valueStr = "";
        int spos = xmlStr.indexOf(startCode);
        int epos = xmlStr.indexOf(endCode);
        if (spos != -1) {
            valueStr = xmlStr.substring(spos + startCode.length(), epos);
            if (valueStr.startsWith("<![CDATA[")) {
                valueStr = valueStr.substring(9, valueStr.length() - 3);
            }
        }
        return valueStr;
    }

    /**
     * 发送欢迎消息
     * 
     * @param fromUserName
     * @param toUserName
     * @param msg
     */
    public void sendTextMsg(String fromUserName, String toUserName, String msg) {
        String xmlStr = "<xml>" + "<ToUserName><![CDATA[" + fromUserName + "]]></ToUserName>" + "<FromUserName><![CDATA[" + toUserName + "]]></FromUserName>" + "<CreateTime>" + DateUtil.getNow()
                + "</CreateTime>" + "<MsgType><![CDATA[text]]></MsgType>" + "<Content><![CDATA[" + msg + "]]></Content>" + "<FuncFlag>0</FuncFlag>" + "</xml>";
        BeanCtx.p(xmlStr);
    }

    /**
     * 取消关注时删除用户信息
     * 
     * @param fromUserName
     */
    public void unsubscribe(String fromUserName) {
        String sql = "update BPM_OrgUserList set WeiXinid='' where WeiXinid='" + fromUserName + "'";
        Rdb.execSql(sql);
    }

    /**
     * 根据用户的微信id获得用户的直实userid
     * 
     * @param fromUserName
     * @param toUserName
     */
    public void getUseridByWeiXinid(String fromUserName, String toUserName) {
        String sql = "select Userid from BPM_OrgUserList where Weixinid='" + fromUserName + "'";
        String userid = Rdb.getValueBySql(sql);
        if (Tools.isNotBlank(userid)) {
            BeanCtx.setUserid(userid);
        }
        else {
            // 用户还没有注册
            sendTextMsg(fromUserName, toUserName, "您的微信号还没有绑定您的系统帐号!请回复用户名#密码进行绑定!");
        }
    }

    /**
     * 自动注册用户的微信id号
     * 
     * @param fromUserName
     * @param toUserName
     * @param content
     */
    public void regWeiXinid(String fromUserName, String toUserName, String content) {
        int spos = content.indexOf("#");
        String userid = content.substring(0, spos);
        String pwd = content.substring(spos + 1);
        userid = Rdb.formatArg(userid);
        userid = Rdb.formatArg(userid);
        pwd = Tools.md5(pwd);
        String sql = "select * from BPM_OrgUserList where Status='1' and Userid='" + userid + "' and Password='" + pwd + "'";
        if (Rdb.hasRecord(sql)) {
            // 用户名和密码正确,开始注册
            sql = "update BPM_OrgUserList set WeiXinid='" + fromUserName + "' where userid='" + userid + "'";
            Rdb.execSql(sql);
            BeanCtx.setUserid(userid);
            sendTextMsg(fromUserName, toUserName, BeanCtx.getUserName() + "您好!您已成功帮定微信帐号,回复任意消息可获取待办!");
        }
        else {
            sendTextMsg(fromUserName, toUserName, "用户名或密码错误!");
        }
    }

    // 暂不实现
    public boolean checkSignature(String signature, String timestamp, String nonce) {
        return true;
    }

    // 发送待办消息
    public void getToDoList(String fromUserName, String toUserName) {
        String httpServer = BeanCtx.getSystemConfig("HttpServerUrl");
        String sid = base64("WeiXin#" + fromUserName + "#" + System.currentTimeMillis());
        String xmlStr = "<xml>" + "<ToUserName><![CDATA[" + fromUserName + "]]></ToUserName>" + "<FromUserName><![CDATA[" + toUserName + "]]></FromUserName>" + "<CreateTime>" + DateUtil.getNow()
                + "</CreateTime><MsgType><![CDATA[news]]></MsgType>" + "<ArticleCount>9</ArticleCount>" + "<Articles>";

        String url = httpServer + "/r?wf_num=S023&weixinid=" + sid;
        String picUrl = "http://mmsns.qpic.cn/mmsns/QFVFbaK4sMwnuu4vYY21P61miaNJoLicb9IhlDoa53E5qTvhceEBjicMg/0";
        String count = "";
        xmlStr += "<item><Title><![CDATA[欢迎使用Linkey BPM]]></Title><Description></Description><PicUrl><![CDATA[" + picUrl + "]]></PicUrl><Url><![CDATA[" + url + "]]></Url></item>";

        url = httpServer + "/r?wf_num=P_S023_004&weixinid=" + sid;
        count = Rdb.getValueBySql("select count(*) as TotalNum from BPM_UserToDo where Userid='" + fromUserName + "'");
        xmlStr += "<item><Title><![CDATA[1.我的待办(" + count + ")]]></Title><Description></Description><PicUrl><![CDATA[]]></PicUrl><Url><![CDATA[" + url + "]]></Url></item>";

        url = httpServer + "/r?wf_num=P_S023_007&weixinid=" + sid;
        count = Rdb.getValueBySql("select count(*) as TotalNum from BPM_MainData where ','+WF_CopyUser+',' like '%," + fromUserName + ",%'");
        xmlStr += "<item><Title><![CDATA[2.我的待阅(" + count + ")]]></Title><Description></Description><PicUrl><![CDATA[]]></PicUrl><Url><![CDATA[" + url + "]]></Url></item>";

        url = httpServer + "/r?wf_num=P_S023_008&weixinid=" + sid;
        count = Rdb.getValueBySql("select count(*) as TotalNum from BPM_MainData where ','+WF_EndUser+',' like '%," + fromUserName + ",%'");
        xmlStr += "<item><Title><![CDATA[3.我的已办(" + count + ")]]></Title><Description></Description><PicUrl><![CDATA[]]></PicUrl><Url><![CDATA[" + url + "]]></Url></item>";

        url = httpServer + "/r?wf_num=P_S023_009&weixinid=" + sid;
        count = Rdb.getValueBySql("select count(*) as TotalNum from BPM_MainData where ','+WF_SourceEntrustUserid+',' like '%," + fromUserName + ",%'");
        xmlStr += "<item><Title><![CDATA[4.我委托的(" + count + ")]]></Title><Description></Description><PicUrl><![CDATA[]]></PicUrl><Url><![CDATA[" + url + "]]></Url></item>";

        url = httpServer + "/r?wf_num=P_S023_010&weixinid=" + sid;
        count = Rdb.getValueBySql("select count(*) as TotalNum from BPM_AllDocument where WF_AddName='" + fromUserName + "'");
        xmlStr += "<item><Title><![CDATA[5.我申请的(" + count + ")]]></Title><Description></Description><PicUrl><![CDATA[]]></PicUrl><Url><![CDATA[" + url + "]]></Url></item>";

        url = httpServer + "/r?wf_num=P_S023_011&weixinid=" + sid;
        xmlStr += "<item><Title><![CDATA[6.已归档的]]></Title><Description></Description><PicUrl><![CDATA[]]></PicUrl><Url><![CDATA[" + url + "]]></Url></item>";

        url = httpServer + "/r?wf_num=R_S023_B007&weixinid=" + sid;
        xmlStr += "<item><Title><![CDATA[7.流程申请]]></Title><Description></Description><PicUrl><![CDATA[]]></PicUrl><Url><![CDATA[" + url + "]]></Url></item>";

        url = httpServer + "/r?wf_num=V_S003_G003&weixinid=" + sid;
        xmlStr += "<item><Title><![CDATA[8.委托设置]]></Title><Description></Description><PicUrl><![CDATA[]]></PicUrl><Url><![CDATA[" + url + "]]></Url></item>";

        xmlStr += "</Articles><FuncFlag>1</FuncFlag></xml>";
        BeanCtx.p(xmlStr);
    }

    /**
     * 把字符串编码为base64格式的
     * 
     * @param str 要编码的字符串
     * @return 返回编码后的字符串
     */
    public String base64(String str) {
        if (Tools.isBlank(str))
            return "";
        try {
            return new String(org.apache.commons.codec.binary.Base64.encodeBase64(str.getBytes()), "UTF-8");
        }
        catch (Exception e) {
            return "";
        }
    }

}