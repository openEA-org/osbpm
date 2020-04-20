package cn.linkey.rulelib.S014;

import java.util.*;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;
import cn.linkey.wf.*;

/**
 * @RuleName:监控中结束或启动一个环节
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-15 14:47
 */
final public class R_S014_B001 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        //BeanCtx.setDebug();
        String docUnid = BeanCtx.g("wf_docunid", true);
        if (Tools.isBlank(docUnid)) {
            docUnid = BeanCtx.g("WF_DocUnid", true);
        }
        String nodeid = BeanCtx.g("WF_Nodeid", true);
        String action = BeanCtx.g("WF_Action", true);
        String processid = BeanCtx.g("WF_Processid", true);
        String msg = "";
        //		BeanCtx.out("docUnid="+docUnid);
        ProcessEngine linkeywf = new ProcessEngine();
        BeanCtx.setLinkeywf(linkeywf); //把工作流引擎对像设置为全局变量对像
        linkeywf.init(processid, docUnid, BeanCtx.getUserid(), ""); //初始化工作流引擎
        InsNode insNode = (InsNode) BeanCtx.getBean("InsNode");
        if (action.equalsIgnoreCase("End")) {
            insNode.endNode(processid, docUnid, nodeid);
            msg = "环节成功结束";
        }
        else if (action.equalsIgnoreCase("Start")) {
            insNode.startNode(processid, docUnid, nodeid);
            msg = "环节成功启动";
        }
        linkeywf.saveDocument();

        //增加操作记录
        Document nodeDoc = ((ModNode) BeanCtx.getBean("ModNode")).getNodeDoc(processid, nodeid);
        String nodeName = nodeDoc.g("NodeName");
        String remark = "在流程监控中调整节点(" + nodeName + "->" + msg + ")";
        addProcessReadLog(docUnid, processid, remark);

        //		BeanCtx.userlog(docUnid, "流程监控调整节点", "调整("+processid+"->"+nodeid+")"+msg);

        //增加操作记录
        addProcessReadLog(docUnid, processid, "阅读文档(" + linkeywf.getDocument().g("Subject") + ")");

        BeanCtx.p(msg);
        return "";
    }

    /**
     * 记录文件阅读记录
     */
    public static void addProcessReadLog(String docUnid, String processid, String remark) {
        if (BeanCtx.getSystemConfig("ProcessDocReadLog").equals("1")) {
            String ip = "";
            if (BeanCtx.getRequest() != null) {
                ip = BeanCtx.getRequest().getRemoteAddr();
            }
            remark = remark.replace("'", "''");
            String sql = "insert into BPM_AttachmentLog(WF_OrUnid,DocUnid,Userid,Processid,IP,Remark,WF_DocCreated) " + "values('" + Rdb.getNewUnid() + "','" + docUnid + "','" + BeanCtx.getUserName()
                    + "(" + BeanCtx.getUserid() + ")" + "','" + processid + "','" + ip + "','" + remark + "','" + DateUtil.getNow() + "')";
            Rdb.execSql(sql);
        }
    }

}