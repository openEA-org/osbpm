package cn.linkey.ws.client;

/**
 * 打开流程服务返回流程的文档数据和流程当前用户所处节点以及后继节点的全部属性
 *
 * @author Administrator
 *
 */
public class Test_OpenProcessFromWS {
    public static void main(String[] args) {
        WFOpenProcessService service = new WFOpenProcessService(); //获得服务对像
        WFOpenProcess portType = service.getWFOpenProcessPort(); //获取服务的端口

        String docXml = "<Items><WFItem name=\"Subject\">从WS重动一个流程test</WFItem></Items>";
        String processid = "f217a2fe03e52048d10ba0b0950eec57";
        String docUnid = "7b612e6903a68047bb086c009f8a12d4403a";//指定文档id
        String userid = "admin";
        String sysid = "bpm";
        String syspwd = "pass";
        String jsonStr = portType.openProcess(docXml, processid, docUnid, userid, sysid, syspwd);
        System.out.println(jsonStr);
    }
}
