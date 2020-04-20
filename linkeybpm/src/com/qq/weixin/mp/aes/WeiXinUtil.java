package com.qq.weixin.mp.aes;

import java.io.InputStream;

import cn.linkey.factory.BeanCtx;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;

import com.alibaba.fastjson.JSONObject;
import com.qq.weixin.mp.aes.WXBizMsgCrypt;

public class WeiXinUtil {
    private static String corpsecret;//管理权限组的id
    private static String sToken;
    private static String sEncodingAESKey;
    private static String sCorpID;
    private static String signature;
    private static String echostr; //填接写接口时的响应消息
    private static String timestamp; //时间标识
    private static String nonce;
    private static WXBizMsgCrypt wxcpt; //微信加解密类
    private static String httpServer; //bpm的公网地址
    static {
        sToken = BeanCtx.getSystemConfig("WeiXin.sToken");
        sCorpID = BeanCtx.getSystemConfig("Weixin.sCorpID");
        sEncodingAESKey = BeanCtx.getSystemConfig("WeiXin.sEncodingAESKey");
        corpsecret = BeanCtx.getSystemConfig("Weixin.corpsecret");
        httpServer = BeanCtx.getSystemConfig("HttpServerUrl");
        try {
            wxcpt = new WXBizMsgCrypt(sToken, sEncodingAESKey, sCorpID);
        }
        catch (Exception e) {
            BeanCtx.log(e, "E", "创建微信加解密类时出错");
        }
    }

    /**
     * 被动响应时初始化微信工具类，在主动调用接口时不需要
     * 
     * @param sToken 微信接口填写的 Token
     * @param sCorpID 公众号设置中可以得到此id
     * @param sEncodingAESKey 微信接口填写时的43位加密密钥
     */
    public static void responseInit() {
        signature = BeanCtx.g("msg_signature", true);
        echostr = BeanCtx.g("echostr", true);
        timestamp = BeanCtx.g("timestamp", true);
        nonce = BeanCtx.g("nonce", true);
    }

    /**
     * 获得主动调用接口的AccessToken标识
     * 
     * @param corpsecret 在微信的管理组权限处获取
     * @return
     */
    public static String getAccessToken(String sCorpID, String corpsecret) {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=" + sCorpID + "&corpsecret=" + corpsecret;
        try {
            String token = Tools.httpGet(url, "UTF-8");
            JSONObject jsonObj = JSONObject.parseObject(token);
            token = jsonObj.getString("access_token");
            return token;
        }
        catch (Exception e) {
            BeanCtx.log(e, "E", "获取微信的corpsecret时出错!");
            return "";
        }
    }

    /**
     * 主动发送文本消息
     * 
     * @param touser接收者的id多个用|分隔
     * @param topart接收部门的id多个用|分隔y
     * @param totag接收标签的id多个用|分隔
     * @param agentid应用id
     * @param body text内容
     * @return
     */
    public static String sendTextMsg(String touser, String toparty, String totag, String agentid, String body) {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=" + getAccessToken(sCorpID, corpsecret);
        String postJson = "{\"touser\":\"" + touser + "\",\"toparty\": \"" + toparty + "\",\"totag\": \"" + totag + "\",\"msgtype\": \"text\",\"agentid\": \"" + agentid
                + "\",\"text\": {\"content\": \"" + body + "\"},\"safe\":\"0\"}";
        try {
            return Tools.httpPost(url, postJson);
        }
        catch (Exception e) {
            e.printStackTrace();
            return "{\"errcode\":1001,\"errmsg\":\"error\"}";
        }
    }

    /**
     * 主动发送新闻格式类的消息
     * 
     * @param touser接收者的id多个用|分隔
     * @param topart接收部门的id多个用|分隔y
     * @param totag接收标签的id多个用|分隔
     * @param agentid应用id
     * @param body json内容
     * @return
     */
    public static String sendNews(String touser, String toparty, String totag, String agentid, String jsonStr) {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=" + getAccessToken(sCorpID, corpsecret);
        String postJson = "{\"touser\":\"" + touser + "\",\"toparty\": \"" + toparty + "\",\"totag\": \"" + totag + "\",\"msgtype\": \"news\",\"agentid\": \"" + agentid
                + "\",\"news\": {\"articles\":[" + jsonStr + "]}}";
        try {
            return Tools.httpPost(url, postJson);
        }
        catch (Exception e) {
            BeanCtx.log(e, "E", "WeiXinUtil.sendNews error!");
            return "{\"errcode\":1002,\"errmsg\":\"error\"}";
        }
    }

    /**
     * 格式化为news消息类的json
     * 
     * @param title 消息的标题
     * @param description 消息描述
     * @param url 点击后的链接url
     * @param picurl 图片的url可以为空
     * @return
     */
    public static String getNewsJsonStr(String title, String description, String url, String picurl) {
        String jsonStr = "{\"title\": \"" + title + "\",\"description\": \"" + description + "\",\"url\": \"" + url + "\",\"picurl\": \"" + picurl + "\"}";
        return jsonStr;
    }

    /**
     * 验证url的合法性，初始填写接口时有用
     * 
     * @return
     */
    public static String verifyURL() {
        try {
            return wxcpt.VerifyURL(signature, timestamp, nonce, echostr);
        }
        catch (Exception e) {
            //验证URL失败，错误原因请查看异常
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 用字符串方法从微信发送来的xml数据中分析数据内容
     * 
     * @param xmlStr 要分析的xml
     * @param fieldName 字段名称
     * @return
     */
    public static String getXmlValue(String xmlStr, String fieldName) {
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
     * 获得被动响应文本消息
     * 
     * @param fromUserName发送者的id
     * @param toUserName接收者的id
     * @param msg文本消息内容
     */
    public static String getResponseTextMsg(String msg, String fromUserName, String toUserName) {
        String xmlStr = "<xml>" + "<ToUserName><![CDATA[" + fromUserName + "]]></ToUserName>" + "<FromUserName><![CDATA[" + toUserName + "]]></FromUserName>" + "<CreateTime>" + DateUtil.getNow()
                + "</CreateTime>" + "<MsgType><![CDATA[text]]></MsgType>" + "<Content><![CDATA[" + msg + "]]></Content>" + "<FuncFlag>0</FuncFlag>" + "</xml>";
        xmlStr = encryptMsg(xmlStr);
        return xmlStr;
    }

    /**
     * 获得微信post过来的数据内容
     * 
     * @return
     * @throws Exception
     */
    public static String getWeiXinXml() throws Exception {
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

        String xmlStr = new String(xmldataByte, "UTF-8");
        if (Tools.isNotBlank(xmlStr)) {
            //			BeanCtx.out("获得xml="+xmlStr);
            xmlStr = decryptMsg(xmlStr);
        }
        return xmlStr;
    }

    //解密xmlStr
    public static String decryptMsg(String xmlStr) {
        try {
            return wxcpt.DecryptMsg(signature, timestamp, nonce, xmlStr);
        }
        catch (Exception e) {
            BeanCtx.log(e, "E", "解密XML时出错" + xmlStr);
            return xmlStr;
        }
    }

    //加密xmlStr
    public static String encryptMsg(String xmlStr) {
        try {
            return wxcpt.EncryptMsg(xmlStr, timestamp, nonce);
        }
        catch (Exception e) {
            BeanCtx.log(e, "E", "加密XML时出错");
            return xmlStr;
        }
    }

    public static String getTimestamp() {
        return timestamp;
    }

    public static String getsToken() {
        return sToken;
    }

    public static String getsCorpID() {
        return sCorpID;
    }

    public static String getsEncodingAESKey() {
        return sEncodingAESKey;
    }

    public static WXBizMsgCrypt getWxcpt() {
        return wxcpt;
    }

    public static String getSignature() {
        return signature;
    }

    public static String getEchostr() {
        return echostr;
    }

    public static String getNonce() {
        return nonce;
    }

    public static String getHttpServer() {
        return httpServer;
    }

    public static String getCorpsecret() {
        return corpsecret;
    }
}
