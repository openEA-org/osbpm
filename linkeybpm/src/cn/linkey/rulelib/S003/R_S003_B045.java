package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.wf.*;

/**
 * 本规则负责启动businessRuleTask Node
 * 
 * @author Administrator
 *
 */
public class R_S003_B045 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        String processid = BeanCtx.getLinkeywf().getProcessid();
        String runNodeid = (String) params.get("WF_RunNodeid"); //获得要运行的节点id
        String docUnid = BeanCtx.getLinkeywf().getDocUnid();

        ModNode insModNode = (ModNode) BeanCtx.getBean("ModNode");
        Document nodeDoc = insModNode.getNodeDoc(processid, runNodeid); //节点配置文档
        if (nodeDoc.g("AllNodeEndStartFlag").equals("1")) {
            //需要所有节点结束后才能启动本节点
            String sql = "select WF_OrUnid from BPM_InsNodeList where DocUnid='" + docUnid + "' and Nodeid<>'Process' and Status='Current'";
            if (Rdb.hasRecord(sql)) { //还有环节是活动的
                return "";
            }
        }

        //1.首先启动本节点
        InsNode insNode = (InsNode) BeanCtx.getBean("InsNode");
        insNode.startNode(processid, BeanCtx.getLinkeywf().getDocUnid(), runNodeid);

        //2.数据流出
        if (Tools.isNotBlank(nodeDoc.g("OutDataConfig"))) {
            int i = BeanCtx.getLinkeywf().getDocument().saveToOutData(nodeDoc.g("OutDataConfig"), nodeDoc.g("OutDataRuleNum"));
            if (i < 1) {
                BeanCtx.log("E", nodeDoc.g("OutDataConfig") + "数据流出错误!");
                return "";
            }
        }

        //3.发送节点邮件
        if (nodeDoc.g("SendMailFlag").equals("1")) {
            BeanCtx.getLinkeywf().getMaildc().add(nodeDoc); //把路由文档加入到全局集合中去，后面发邮件时使用
            ((MessageImpl) BeanCtx.getBean("Message")).sendNodeMail(nodeDoc);
        }

        //4.发送手机短信
        if (nodeDoc.g("SendSmsFlag").equals("1")) {
            ((MessageImpl) BeanCtx.getBean("Message")).sendNodeSms(nodeDoc);
        }

        //结束本环节,只需要运行节点所对应的结束规则即可
        BeanCtx.getLinkeywf().runNode(processid, runNodeid, "EndRuleNum", params);
        return "";
    }
}
