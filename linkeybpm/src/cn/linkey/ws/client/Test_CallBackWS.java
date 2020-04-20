package cn.linkey.ws.client;

import java.net.URL;
import javax.xml.namespace.QName;

/**
 * 调用运行规则服务,获得返回的字符串
 * 
 * @author Administrator
 *
 */
public class Test_CallBackWS {
    public static void main(String[] args) throws Exception {

        String srvUrl = "http://localhost:8080/bpm/WF_CallBackPort?wsdl";
        URL url = new URL(srvUrl);
        QName qname = new QName("http://server.ws.linkey.cn/", "WF_CallBackService");

        WFCallBackService service = new WFCallBackService(url, qname); //获得服务对像
        WFCallBack portType = service.getWFCallBackPort(); //获取服务的端口

        String params = "{\"Folderid\":\"001001\"}"; //传入json格式的字符串
        String userid = "admin";
        String syspwd = "pass";
        String jsonStr = portType.callback(params, userid, syspwd);
        System.out.println(jsonStr);
    }
}
