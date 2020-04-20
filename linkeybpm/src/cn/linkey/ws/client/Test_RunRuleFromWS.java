package cn.linkey.ws.client;

import java.net.URL;

/**
 * 调用运行规则服务,获得返回的字符串
 * 
 * @author Administrator
 *
 */
public class Test_RunRuleFromWS {
    public static void main(String[] args) throws Exception {

        String srvUrl = "http://localhost:8080/bpm/WF_RunRulePort?wsdl";
        URL url = new URL(srvUrl);

        WFRunRuleService service = new WFRunRuleService(url); //获得服务对像
        WFRunRule portType = service.getWFRunRulePort(); //获取服务的端口

        String rulenum = "R_S017_B013";
        String params = "{\"Folderid\":\"001001\"}"; //传入json格式的字符串
        String userid = "admin";
        String sysid = "bpm";
        String syspwd = "pass";
        String jsonStr = portType.runRule(rulenum, params, userid, sysid, syspwd);
        System.out.println(jsonStr);
    }
}
