package cn.linkey.rulelib.S031;

import java.util.HashMap;

import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;
import cn.linkey.wf.InsNode;
import cn.linkey.wf.InsUser;
import cn.linkey.wf.ModNode;

/**
 * @RuleName:扩展InsNode节点结束事件
 * @author admin
 * @version: 8.0
 * @Created: 2015-12-03 18:04
 */
final public class R_S031_B002 extends InsNode implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数

        return "";
    }

    /**
     * 结束指定流程的节点
     * 
     * @param processid 流程id
     * @param docUnid 文档id
     * @param nodeid 要启动的节点id
     * @return true表示结束成功，false表示结束失败
     */
    @Override
    public void endNode(String processid, String docUnid, String nodeid) throws Exception {
        Document insNodeDoc = getInsNodeDoc(processid, nodeid, docUnid);
        Document modNodeDoc = ((ModNode) BeanCtx.getBean("ModNode")).getNodeDoc(processid, nodeid);
        if (insNodeDoc.isNull()) {
            //说明当前节点没有活动的节点文档，需要直接创建一个已结束的节点文档即可
            insNodeDoc.s("DocUnid", docUnid);
            insNodeDoc.s("Processid", processid);
            insNodeDoc.s("Nodeid", nodeid);
            insNodeDoc.s("NodeName", modNodeDoc.g("NodeName"));
            insNodeDoc.s("NodeType", modNodeDoc.g("NodeType"));
            insNodeDoc.s("ProcessNumber", BeanCtx.getLinkeywf().getProcessNumber());
            insNodeDoc.s("StartTime", DateUtil.getNow());
            if (BeanCtx.getLinkeywf().getCurrentInsUserDoc() != null) {
                insNodeDoc.s("SourceOrUnid", BeanCtx.getLinkeywf().getCurrentInsUserDoc().g("WF_OrUnid"));
            }
            insNodeDoc.s("ActionNum", BeanCtx.getLinkeywf().getActionNum());
        }

        //不管是否存在均进行以下操作
        insNodeDoc.s("EndTime", DateUtil.getNow());
        insNodeDoc.s("TotalTime", DateUtil.getDifTime(insNodeDoc.g("StartTime"), DateUtil.getNow()));
        insNodeDoc.s("ActionUserid", BeanCtx.getUserid());
        insNodeDoc.s("Status", "End");

        this.changeStatus(processid, nodeid, docUnid, insNodeDoc, modNodeDoc, "InsNodeDocBeforeEnd"); /* 状态改变前事件 */

        insNodeDoc.save();

        //		BeanCtx.log("D","节点名称="+modNodeDoc.g("NodeName")+" 节点id="+modNodeDoc.g("Nodeid")+" 的环节结束成功!");

        //这里同时要结束本节点所有其他的审批用户
        InsUser insUser = (InsUser) BeanCtx.getBean("InsUser");
        insUser.endUserByNodeid(processid, docUnid, nodeid);

        //这里自定义结束节点后的事件
        runEndNodeEvent(insNodeDoc);

    }

    /**
     * 运行节点结束后的代码 nodeDoc为当前结束的节点文档对像
     */

    public void runEndNodeEvent(Document insNodeDoc) {
        Document modNodeDoc = ((ModNode) BeanCtx.getBean("ModNode")).getNodeDoc(BeanCtx.getLinkeywf().getProcessid(), insNodeDoc.g("Nodeid"));//节点模型配置文档
        Document engineDoc = BeanCtx.getLinkeywf().getDocument();//流程主文档对像
        String subject = engineDoc.g("Subject"); //事务标题
        String transactionid = engineDoc.g("transactionid");
        String emergency = engineDoc.g("Emergency"); //紧急程度
        String startTime = insNodeDoc.g("StartTime"); //节点开始时间
        String endTime = DateUtil.getNow();//节点结束时间

        BeanCtx.out("节点结束成功=" + insNodeDoc);
    }

}