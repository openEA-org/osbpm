package cn.linkey.rulelib.SL02;

import java.io.*;
import java.util.*;
import cn.linkey.dao.*;
import cn.linkey.util.*;
import cn.linkey.factory.*;
import cn.linkey.rule.LinkeyRule;
import com.qq.weixin.mp.aes.WXBizMsgCrypt;

/**
 * @RuleName:微信企业号入口
 * @author Mr.Yun
 * @version: 8.0
 * @Created: 2016-08-19 15:08
 */
final public class R_SL02_B001 implements LinkeyRule {
    // 签名串，对应URL参数的msg_signature
    public String signature;
    // 时间戳，对应URL参数的timestamp
    public String timestamp;
    // 随机串，对应URL参数的echostr
    public String echostr;
    // 随机串，对应URL参数的nonce
    public String nonce;
    // 微信加解密类
    WXBizMsgCrypt wxcpt;

    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        /**
         * 1、接受微信发过来的消息
         */
        this.signature = BeanCtx.g("msg_signature");
        this.echostr = BeanCtx.g("echostr");
        this.timestamp = BeanCtx.g("timestamp");
        this.nonce = BeanCtx.g("nonce");

        /**
         * 2、定义企业微信基本信息，直接读取配置信息
         */
        // 企业微信通信的签名Token，系统sToken配置
        String sToken = BeanCtx.getSystemConfig("WeiXin.sToken");
        // 企业微信号的识别ID，系统sCorpID配置
        String sCorpID = BeanCtx.getSystemConfig("Weixin.sCorpID");
        // 企业微信通信的通信密钥，系统sEncodingAESKey配置
        String sEncodingAESKey = BeanCtx.getSystemConfig("WeiXin.sEncodingAESKey");
        // 微信加解密类
        this.wxcpt = new WXBizMsgCrypt(sToken, sEncodingAESKey, sCorpID);
        
        /**
         * 3、微信公众号首次填写服务器url时使用
         */
        if (Tools.isNotBlank(echostr)) {
            try {
                String sEchoStr = wxcpt.VerifyURL(signature, timestamp, nonce, echostr);
                BeanCtx.p(sEchoStr);
            }
            catch (Exception e) {
                // 验证URL失败，错误原因请查看异常
                e.printStackTrace();
            }
            return "";
        }

        /**
         * 4、获取微信post过来的数据
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
        // 事件的keyid
        String eventKey = getXmlValue(xmlStr, "EventKey");

        /**
         * 输出微信post过来的数据
         */
        BeanCtx.out("toUserName=" + toUserName + " || fromUserName=" + fromUserName);
        BeanCtx.out("msgId=" + msgId + " || msgType=" + msgType + " || getEvent=" + getEvent);
        BeanCtx.out("content=" + content + " || eventKey=" + eventKey);

        /**
         * 5、显示我的待办 5.1 通过开启“微信消息转发”功能实现获取用户发送的内容，通过发送的内容content判断是否需要回传某些消息给客户 5.2 通过“菜单KEY值”设置菜单事件，当菜单设置值与指定的某些值相等同回传某些消息给客户
         */
        if (content.contains("待办") || eventKey.toUpperCase().equals("MyToDo".toUpperCase())) {
            getToDoList(fromUserName, toUserName);
        }

        /**
         * 6、发送欢迎消息
         */
        if (!BeanCtx.getUserid().equals("Anonymous")) {
            sendTextMsg(fromUserName, toUserName, "欢迎回来！");
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
        String xmlStr = new String(xmldataByte, "UTF-8");
        xmlStr = decryptMsg(xmlStr);
        return xmlStr;
    }

    /**
     * 用字符串方法从微信发送来的xml数据中分析数据内容
     * 
     * @param xmlStr 需要分析的数据
     * @param fieldName 字段名
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
                + "</CreateTime><MsgType><![CDATA[text]]></MsgType>" + "" + "<Content><![CDATA[" + msg + "]]></Content>" + "<FuncFlag>0</FuncFlag>" + "</xml>";
        xmlStr = encryptMsg(xmlStr);
        BeanCtx.p(xmlStr);
    }

    /**
     * 解密xmlStr
     * 
     * @param xmlStr
     * @return
     * @throws Exception
     */
    public String decryptMsg(String xmlStr) throws Exception {
        return wxcpt.DecryptMsg(signature, this.timestamp, this.nonce, xmlStr);
    }

    /**
     * 加密
     * 
     * @param xmlStr
     * @return
     */
    public String encryptMsg(String xmlStr) {
        try {
            return wxcpt.EncryptMsg(xmlStr, this.timestamp, this.nonce);
        }
        catch (Exception e) {
            BeanCtx.log(e, "E", "加密XML时出错");
            return xmlStr;
        }
    }

    /**
     * 发送待办消息
     * 
     * @param fromUserName
     * @param toUserName
     * @throws Exception
     */
    public void getToDoList(String fromUserName, String toUserName) throws Exception {
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
        xmlStr = encryptMsg(xmlStr);
        BeanCtx.p(xmlStr);
    }

    /**
     * 把字符串编码为base64格式的
     * 
     * @param str 要编码的字符串
     * @return 返回编码后的字符串
     */
    public String base64(String str) {
        if (Tools.isBlank(str)) {
            return "";
        }
        try {
            String base64str = new String(org.apache.commons.codec.binary.Base64.encodeBase64(str.getBytes()), "UTF-8");
            return base64str;
        }
        catch (Exception e) {
            return "";
        }
    }
}
