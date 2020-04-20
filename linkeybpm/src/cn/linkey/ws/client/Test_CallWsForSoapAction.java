package cn.linkey.ws.client;

import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPBinding;

public class Test_CallWsForSoapAction {

    // 名字空间 
    public static final String targetNamespace = "http://service.billInface.boss.gmt/";
    //服务名
    public static final String serName = "BillWebServiceService";
    //端口名
    public static final String pName = "BillWebServicePort";
    //服务地址
    public static final String endpointAddress = "http://localhost:8080/BillInface/BillWebServicePort?wsdl";
    //方法名
    public static final String OPER_NAME = "getInBillingLoginInfo";
    //参数名
    public static final String INPUT_NMAE = "arg0";

    public static void main(String[] args) throws Exception {

        QName serviceName = new QName(targetNamespace, serName);

        QName portName = new QName(targetNamespace, pName);

        javax.xml.ws.Service service = Service.create(serviceName);
        service.addPort(portName, SOAPBinding.SOAP11HTTP_BINDING, endpointAddress);

        Dispatch<SOAPMessage> dispatch = service.createDispatch(portName, SOAPMessage.class, Service.Mode.MESSAGE);

        BindingProvider bp = (BindingProvider) dispatch;
        Map<String, Object> rc = bp.getRequestContext();
        rc.put(BindingProvider.SOAPACTION_USE_PROPERTY, Boolean.TRUE);
        rc.put(BindingProvider.SOAPACTION_URI_PROPERTY, OPER_NAME);
        MessageFactory factory = ((SOAPBinding) bp.getBinding()).getMessageFactory();

        SOAPMessage request = factory.createMessage();
        SOAPBody body = request.getSOAPBody();
        QName payloadName = new QName(targetNamespace, OPER_NAME, "ns1");
        SOAPBodyElement payload = body.addBodyElement(payloadName);

        SOAPElement message = payload.addChildElement(INPUT_NMAE);
        message.addTextNode("x");
        SOAPMessage reply = null;

        try {
            reply = dispatch.invoke(request);
        }
        catch (WebServiceException wse) {
            wse.printStackTrace();
        }
        SOAPBody soapBody = reply.getSOAPBody();
        SOAPBodyElement nextSoapBodyElement = (SOAPBodyElement) soapBody.getChildElements().next();
        SOAPElement soapElement = (SOAPElement) nextSoapBodyElement.getChildElements().next();

        System.out.println("获取回应信息为：" + soapElement.getValue());
    }
}