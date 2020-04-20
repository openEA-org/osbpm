package cn.linkey.wf;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.util.DateUtil;

// 本类负责任务的启动、暂停、等待、停止 状态的维护
// 当状态发生变化时统一交由状态处理程序去处理，每种状态对应一个规则
// 本类为单实例类

public class InsNode {

    /**
     * 启动指定流程的一个节点
     * 
     * @param processid 流程id
     * @param docUnid 文档id
     * @param nodeid 要启动的节点id
     * @return 返回1表示启动成功，0表示失败，2表示本环节本身已经在运行状态
     */
    public int startNode(String processid, String docUnid, String nodeid) throws Exception {
        //每次启动都插入一条新的节点记录,如果环节已经是活动的则不再执行启动任务
        Document insNodeDoc = getInsNodeDoc(processid, nodeid, docUnid);
        if (insNodeDoc.isNull()) {
            //获得模型节点的文档对像
            Document modNodeDoc = ((ModNode) BeanCtx.getBean("ModNode")).getNodeDoc(processid, nodeid);
            if (!modNodeDoc.isNull()) {
                //说明此节点活动的状态不存在，直接启动
                insNodeDoc.s("DocUnid", docUnid);
                insNodeDoc.s("Processid", processid);
                insNodeDoc.s("Nodeid", nodeid);
                insNodeDoc.s("NodeName", modNodeDoc.g("NodeName"));
                insNodeDoc.s("NodeType", modNodeDoc.g("NodeType"));
                insNodeDoc.s("ProcessNumber", BeanCtx.getLinkeywf().getProcessNumber());
                insNodeDoc.s("StartTime", DateUtil.getNow());
                insNodeDoc.s("ActionUserid", BeanCtx.getUserid());
                insNodeDoc.s("SourceOrUnid", BeanCtx.getLinkeywf().getSourceOrUnid());
                insNodeDoc.s("ActionNum", BeanCtx.getLinkeywf().getActionNum()); //生成一个随机数
                String targetStatus = "Current";
                insNodeDoc.s("Status", targetStatus);
                this.changeStatus(processid, nodeid, docUnid, insNodeDoc, modNodeDoc, "InsNodeDocBeforeStart"); /* 状态改变前 */
                insNodeDoc.save();
                return 1; /* 成功启动，表示环节成功启动 */
            }
            else {
                BeanCtx.log("E", "在流程(" + processid + ")中未找到节点(" + nodeid + ")的配置信息!");
                return 0; //节点启动出错
            }
        }
        else {
            return 2; //说明是已经启动的状态
        }
    }

    /**
     * 结束指定流程的节点
     * 
     * @param processid 流程id
     * @param docUnid 文档id
     * @param nodeid 要启动的节点id
     * @return true表示结束成功，false表示结束失败
     */
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

        //这里同时要结束本节点所有其他的审批用户
        InsUser insUser = (InsUser) BeanCtx.getBean("InsUser");
        insUser.endUserByNodeid(processid, docUnid, nodeid);

    }

    /**
     * 检查指定的环节是否可以结束
     * 
     * @param processid 流程id
     * @param docUnid 文档id
     * @param nodeid 要检测的节点id
     * @return 返回false表示不可以结束,true表示可以结束
     */
    public boolean checkEndRule(String processid, String docUnid, String nodeid) {
        Document modNodeDoc = ((ModNode) BeanCtx.getBean("ModNode")).getNodeDoc(processid, nodeid);
        String loopType = modNodeDoc.g("LoopType"); //循环类型1表示第一个，2表示所有
        if (loopType.equals("1")) {
            //表示只要有一个用户处理了就可以结束本环节
            return true;
        }
        else if (loopType.equals("2")) {
            //表示要所有用户都审批结束才可以结束环节
            String sql = "select WF_OrUnid from BPM_InsUserList where docUnid='" + docUnid + "' and processid='" + processid + "' and nodeid='" + nodeid + "' and Status='Current'";
            return Rdb.hasRecord(sql);
        }
        return false;
    }

    /**
     * 暂停一个任务节点
     * 
     * @param processid 流程id
     * @param docUnid 文档id
     * @param nodeid 要暂停的节点id
     * @return true表示成功，false表示失败
     */
    public boolean pauseNode(String processid, String docUnid, String nodeid) throws Exception {
        Document insNodeDoc = getInsNodeDoc(processid, nodeid, docUnid);
        if (insNodeDoc.isNull()) {
            insNodeDoc.s("ActionUserid", BeanCtx.getUserid());
            insNodeDoc.s("Status", "Pause");
            insNodeDoc.save();
            return true;
        }
        return false;
    }

    /**
     * 把任务节点变成等待状态，等待外部事件变化后启动
     * 
     * @return
     */
    public boolean waitNode(String processid, String docUnid, String nodeid) throws Exception {
        Document insNodeDoc = getInsNodeDoc(processid, nodeid, docUnid);
        if (insNodeDoc.isNull()) {
            insNodeDoc.s("ActionUserid", BeanCtx.getUserid());
            insNodeDoc.s("Status", "Wait");
            insNodeDoc.save();
            return true;
        }
        return false;
    }

    /**
     * 状态变化通知事件引擎处理,此事件暂时取消，由节点启动前和节点启动后事件进行实现
     * 
     * @param oldStatus 旧的状态值
     * @param newStatus 新的状态值
     * @return
     */
    protected String changeStatus(String processid, String nodeid, String docUnid, Document insNodeDoc, Document modNodeDoc, String eventType) throws Exception {
        //首先看事件是否有定义，如果不存在直接返回1表示成立
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("processid", processid);
        params.put("nodeid", nodeid);
        params.put("insNodeDoc", insNodeDoc); //实例节点文档
        params.put("modNodeDoc", modNodeDoc); //模型节点文档
        return BeanCtx.getEventEngine().run(processid, nodeid, eventType, params);
    }

    /**
     * 获得当前活动的实例节点文档,结束的节点实例一般会有多个不能通过本方法获得
     * 
     * @return
     */
    public Document getInsNodeDoc(String processid, String nodeid, String docUnid) {
        //获得实例节点文档
        String sql = "select * from bpm_insnodelist where DocUnid='" + docUnid + "' and  Processid='" + processid + "' and Nodeid='" + nodeid + "' and Status='Current'";
        return Rdb.getDocumentBySql("bpm_insnodelist", sql);
    }

}
