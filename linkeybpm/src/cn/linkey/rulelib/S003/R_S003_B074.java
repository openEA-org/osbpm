package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.wf.InsNode;
import cn.linkey.wf.ModNode;
import cn.linkey.wf.ProcessEngine;
import cn.linkey.wf.Remark;

/**
 * @RuleName:强制结束节点
 * @author admin
 * @version: 8.0
 * @Created: 2014-11-23 10:08 参数说明:需要传入WF_NextNodeid节点id参数,WF_Remark办理意见
 */
final public class R_S003_B074 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        String processid = linkeywf.getProcessid();
        String docUnid = linkeywf.getDocUnid();

        String nodeid = (String) params.get("WF_NextNodeid"); //获得要结束的节点id
        InsNode insNode = (InsNode) BeanCtx.getBean("InsNode");
        Document nodeDoc = ((ModNode) BeanCtx.getBean("ModNode")).getNodeDoc(processid, nodeid); //这句在要前面执行
        insNode.endNode(processid, docUnid, nodeid);

        //4.获得提示语并返回
        String returnMsg = BeanCtx.getMsg("Engine", "EndNode", nodeDoc.g("NodeName"));

        //5.增加流转记录
        Remark remark = (Remark) BeanCtx.getBean("Remark");
        remark.AddRemark("EndUser", (String) params.get("WF_Remark"), "1");

        linkeywf.setRunStatus(true);//表示运行成功
        return returnMsg;
    }
}