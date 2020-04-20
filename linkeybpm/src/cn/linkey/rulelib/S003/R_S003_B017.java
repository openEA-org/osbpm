package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.wf.*;

/**
 * 本规则负责启动路由线段
 * 
 * @author Administrator
 *
 */
public class R_S003_B017 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        String processid = BeanCtx.getLinkeywf().getProcessid();
        String runNodeid = (String) params.get("WF_RunNodeid"); //获得要运行的节点id

        InsNode insNode = (InsNode) BeanCtx.getBean("InsNode");

        //1.首先启动本节点
        insNode.startNode(processid, BeanCtx.getLinkeywf().getDocUnid(), runNodeid);

        //1.1发送路由邮件
        ModNode insModNode = (ModNode) BeanCtx.getBean("ModNode");
        Document nodeDoc = insModNode.getNodeDoc(processid, runNodeid); //节点配置文档
        if (nodeDoc.g("SendMailFlag").equals("1")) {
            BeanCtx.getLinkeywf().getMaildc().add(nodeDoc); //把路由文档加入到全局集合中去，ProcessEngine.run()方法中会统一发送路由邮件
        }

        //2.因为是路由线所以直接结束本线段,运路由线的结束规则
        BeanCtx.getLinkeywf().runNode(processid, runNodeid, "EndRuleNum", params);

        return "";
    }
}
