package cn.linkey.ws.client;

/**
 * 调用WS启动一个新的流程服务
 * 
 * @author Administrator
 *
 */
public class Test_StartNewProcessFromWS {
    public static void main(String[] args) {
        WFRunProcessService service = new WFRunProcessService(); //获得服务对像
        WFRunProcess portType = service.getWFRunProcessPort(); //获取服务的端口

        String docXml = "<Items><WFItem name=\"Subject\">从WS重动一个流程test</WFItem></Items>";
        String processid = "f217a2fe03e52048d10ba0b0950eec57";
        String docUnid = ""; //空表示启动一个新流程
        String actionid = "EndUserTask";
        String nextNodeid = "T10021";//目标节点
        String nextUserList = "admin"; //如果有多个环节则需要使用admin$T10021,user$T10022格式
        String userid = "admin";
        String remark = "请尽快处理!";
        String sysid = "bpm";
        String syspwd = "pass";
        String jsonStr = portType.runProcess(docXml, actionid, processid, docUnid, nextNodeid, nextUserList, userid, remark, sysid, syspwd);
        System.out.println(jsonStr);
    }
}
