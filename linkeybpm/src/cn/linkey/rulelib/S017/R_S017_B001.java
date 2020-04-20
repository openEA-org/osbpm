package cn.linkey.rulelib.S017;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashSet;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;

import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * 只传soapbody部分内容即可
 * 
 * @RuleName:动态调用WebService服务
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-30 16:07
 */
final public class R_S017_B001 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String userid = "admin"; //当前登录用户id
        String sysid = "bpm";//系统id
        String syspwd = "pass";//系统密码
        String ruleNum = "R_S017_B002"; //要调用的规则编号
        String wsParams = "{\"PageSize\":\"20\",\"PageNum\":\"1\",\"Appid\":\"*\"}"; //传入服务的参数
        String targetNamespace = "http://server.ws.linkey.cn/";//命名空间
        String wsdlUrl = "http://localhost:8080/bpm/WF_RunRulePort?wsdl";
        String serviceName = "WF_RunRuleService"; //服务名称
        String portName = "WF_RunRulePort"; //端口名称
        String soapBody = "<ns2:runRule xmlns:ns2=\"" + targetNamespace + "\" ><rulenum>" + ruleNum + "</rulenum><params>" + wsParams + "</params><userid>" + userid + "</userid><sysid>" + sysid
                + "</sysid><syspwd>" + syspwd + "</syspwd></ns2:runRule>";

        /**
         * wsdl中的代码 <service name="WF_RunRuleService"> 这里的name就是serviceName <port name="WF_RunRulePort" binding="tns:WF_RunRulePortBinding"> 这里的name就是portName
         * <soap:address location="http://localhost:8080/bpm/WF_RunRulePort"></soap:address> </port> </service>
         */

        //创建 Service服务对像  
        StreamSource xmlSource = new StreamSource(new StringReader(soapBody));
        URL wsdlURL = new URL(wsdlUrl);
        QName serviceQName = new QName(targetNamespace, serviceName);
        Service service = Service.create(wsdlURL, serviceQName);

        //创建port端口及 Dispatch<Source>对像  
        QName portQName = new QName(targetNamespace, portName);
        Dispatch<Source> dispatch = service.createDispatch(portQName, Source.class, Service.Mode.PAYLOAD);
        Source orderSource = dispatch.invoke(xmlSource);

        //处理reponse.  
        StreamResult result = new StreamResult(new ByteArrayOutputStream());
        Transformer trans = TransformerFactory.newInstance().newTransformer();
        trans.transform(orderSource, result);
        ByteArrayOutputStream baos = (ByteArrayOutputStream) result.getOutputStream();

        // 输出WS返回的内容并获得返回的JSON字符串
        String responseContent = new String(baos.toByteArray(), "utf-8");
        int spos = responseContent.indexOf("<return>");
        int epos = responseContent.indexOf("</return>");
        String jsonStr = responseContent.substring(spos + 8, epos);

        //json转doc object
        if (Tools.isNotBlank(jsonStr)) {
            spos = jsonStr.indexOf("[");
            jsonStr = jsonStr.substring(spos, jsonStr.length() - 1);
            LinkedHashSet<Document> dc = Documents.jsonStr2dc(jsonStr);
            for (Document doc : dc) {
                BeanCtx.p(doc.toJson());
            }
        }

        //BeanCtx.p(jsonStr);  
        return "";

    }

    /**
     * 传入整个soap的整体进行调用
     */
    public static void callWsByAllSoap() {

        String soapBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://server.ws.linkey.cn/\">" + "<soapenv:Header/>" + "<soapenv:Body>"
                + "<ser:runRule>" + "<rulenum>R_S017_B002</rulenum>" + "<params>{\"PageSize\":\"20\",\"PageNum\":\"1\",\"Appid\":\"*\"}</params>" + "<userid>admin</userid>" + "<sysid>bpm</sysid>"
                + "<syspwd>pass</syspwd>" + "</ser:runRule>" + "</soapenv:Body>" + "</soapenv:Envelope>";

        try {
            StreamSource xmlSource1 = new StreamSource(new StringReader(soapBody));

            // create Service
            URL wsdlURL = new URL("http://localhost:8080/bpm/WF_RunRulePort?wsdl");
            QName serviceQName = new QName("http://server.ws.linkey.cn/", "WF_RunRuleService");
            Service service = Service.create(wsdlURL, serviceQName);

            // create Dispatch<Source>
            QName portQName = new QName("http://server.ws.linkey.cn/", "WF_RunRulePort");
            Dispatch<SOAPMessage> dispatch = service.createDispatch(portQName, SOAPMessage.class, Service.Mode.MESSAGE);

            MessageFactory factory = MessageFactory.newInstance();
            SOAPMessage message = factory.createMessage();
            message.getSOAPPart().setContent(xmlSource1);
            message.saveChanges();
            SOAPMessage response = dispatch.invoke(message);
            SOAPPart sp = response.getSOAPPart();
            Source resp = sp.getContent();

            // Process the response.
            StreamResult result = new StreamResult(new ByteArrayOutputStream());
            Transformer trans = TransformerFactory.newInstance().newTransformer();
            trans.transform(resp, result);
            ByteArrayOutputStream baos = (ByteArrayOutputStream) result.getOutputStream();

            String responseContent = new String(baos.toByteArray());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 调用Domino的WebService服务,传入整个soap的整体
     */
    public static void callDominoWs() {

        String soapBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:DefaultNamespace\">"
                        + "<soapenv:Header/>"
                        + "<soapenv:Body>"
                        + "<urn:IN_USERID>admin</urn:IN_USERID>"
                        + "<urn:IN_TOPNUM>10</urn:IN_TOPNUM>"
                        + "<urn:SYSID>bpm</urn:SYSID>"
                        + "<urn:SYSPWD>pass</urn:SYSPWD>"
                        + "</soapenv:Body>"
                + "</soapenv:Envelope>";

        try {
            StreamSource xmlSource1 = new StreamSource(new StringReader(soapBody));

            // create Service
            URL wsdlURL = new URL("http://140.207.90.118/bpm/interface.nsf/WF_GetToDoSrv?wsdl");
            QName serviceQName = new QName("urn:DefaultNamespace", "GetToDoSrvService");
            Service service = Service.create(wsdlURL, serviceQName);

            // create Dispatch<Source>
            QName portQName = new QName("urn:DefaultNamespace", "Domino");
            Dispatch<SOAPMessage> dispatch = service.createDispatch(portQName, SOAPMessage.class, Service.Mode.MESSAGE);

            MessageFactory factory = MessageFactory.newInstance();
            SOAPMessage message = factory.createMessage();
            message.getSOAPPart().setContent(xmlSource1);
            message.saveChanges();
            SOAPMessage response = dispatch.invoke(message);
            SOAPPart sp = response.getSOAPPart();
            Source resp = sp.getContent();

            // Process the response.
            StreamResult result = new StreamResult(new ByteArrayOutputStream());
            Transformer trans = TransformerFactory.newInstance().newTransformer();
            trans.transform(resp, result);
            ByteArrayOutputStream baos = (ByteArrayOutputStream) result.getOutputStream();

            // Write out the response content.
            String responseContent = new String(baos.toByteArray());
            //			System.out.println(responseContent);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}