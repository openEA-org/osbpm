package cn.linkey.wf;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.org.LinkeyUser;
import cn.linkey.util.Tools;
import cn.linkey.util.DateUtil;

/**
 * 本类单实例类
 * 
 * 本类负责用户实例的启动、暂停、等待、停止状态的维护，并抛出相应事件
 * 
 * @author Administrator
 *
 */
public class InsUser {
    public Document startUser(String processid, String docUnid, String nodeid, String userList, HashMap<String, String> userDept) throws Exception {
        return startUser(processid, docUnid, nodeid, userList, userDept, null);
    }
    /**
     * 启动一个或多个指定流程的用户的审批权限，启动用户时系统会自动检测对应环节有没有启动，如果没有环节会自动启动
     * 
     * @param processid 流程id
     * @param docUnid 主文档id
     * @param nodeid 用户所在节点id
     * @param userList 要启动的用户列表多个用逗号分隔
     * @param userDept 强制让用户以某个部门的身份来处理此任务,如果没有可以传null空值表示用户以默认所在部门的身份进行处理
     * @return 返回最后一个用户的实例文档对像
     */
    public Document startUser(String processid, String docUnid, String nodeid, String userList, HashMap<String, String> userDept, String backFlag) throws Exception {
        //所有用户启动前都要首先看节点是否已经启动，如果没有启动先启动节点
        LinkeyUser linkeyUser = (LinkeyUser) BeanCtx.getBean("LinkeyUser");
        InsNode insNode = (InsNode) BeanCtx.getBean("InsNode");
        insNode.startNode(processid, docUnid, nodeid);
        if (Tools.isBlank(userList)) {
            return null;
        }
        /* 说明没有用户可以启动，直接返回null */
        Document modNodeDoc = ((ModNode) BeanCtx.getBean("ModNode")).getNodeDoc(processid, nodeid);
        String isSequential = modNodeDoc.g("IsSequential"); //是否串行审批
        String nodeName = modNodeDoc.g("NodeName"); //节点名称
        String exceedTime = modNodeDoc.g("ExceedTime"); //节点持继时间
        if (Tools.isBlank(exceedTime)) {
            exceedTime = "0";
        }
        LinkedHashSet<String> userArray = Tools.splitAsLinkedSet(userList, ","); //会去掉重复值
        int s = 100;
        Document insUserDoc = null;
        for (String userid : userArray) {
            String deptid = "";
            if (userDept != null) {
                deptid = userDept.get(userid);
            }
            if (Tools.isBlank(deptid)) {
                deptid = linkeyUser.getDeptidByUserid(userid, false); //没有指定用户的部门时从系统中读取最小部门的deptid
            }
            insUserDoc = getInsUserDoc(processid, nodeid, docUnid, userid, "Current,Wait"); //活动的和等待的都算
            insUserDoc.s("DocUnid", docUnid);
            insUserDoc.s("Processid", processid);
            insUserDoc.s("ProcessNumber", BeanCtx.getLinkeywf().getProcessNumber());
            insUserDoc.s("Nodeid", nodeid);
            insUserDoc.s("NodeName", nodeName);
            insUserDoc.s("StartTime", DateUtil.getNow());
            insUserDoc.s("ExceedTime", exceedTime);
            String firstTriggerTime = modNodeDoc.g("FirstTriggerTime");
            if (Tools.isBlank(firstTriggerTime)) {
                firstTriggerTime = "0";
            }
            insUserDoc.s("FirstTriggerTime", firstTriggerTime); //第一次提前触发时间
            String repeatTime = modNodeDoc.g("RepeatTime");
            if (Tools.isBlank(repeatTime)) {
                repeatTime = "0";
            }
            if (Tools.isNotBlank(backFlag)) {
                insUserDoc.s("IsBackFlag", backFlag);
            }
            insUserDoc.s("RepeatTime", repeatTime); //超时后的重复间隔
            insUserDoc.s("Userid", userid);
            insUserDoc.s("Deptid", deptid);
            insUserDoc.s("ActionUserid", BeanCtx.getUserid());
            insUserDoc.s("SourceOrUnid", BeanCtx.getLinkeywf().getSourceOrUnid());
            insUserDoc.s("ActionNum", BeanCtx.getLinkeywf().getActionNum());
            insUserDoc.s("SerialIndexNum", String.valueOf(s)); //串行排序号
            String oldStatus = insUserDoc.g("Status");
            if (Tools.isBlank(oldStatus)) {
                oldStatus = "NoStart";
            }
            insUserDoc.s("LimitTime", BeanCtx.getLinkeywf().getDocument().g("WF_LimitTime_" + nodeid)); //看是否给本环节设置了时限

            //看是否设置了委托
            if (checkEntrust(processid, docUnid, nodeid, userid)) {
                //说明已启动了委托用户的处理权限,把当前用户从Current改为End
                insUserDoc.s("Status", "End");
                insUserDoc.save();
                s++;
            }
            else {
                //没有设置委托的情况下
                String status = "Current";
                if (isSequential.equals("1")) {
                    if (s > 100) {
                        status = "Wait";
                    }
                } //如果是串行审批则后面的用户都处于Wait状态
                insUserDoc.s("Status", status);

                //如果事件中返回0则表示阻止此用户的启动
                if (!this.changeStatus(userid, processid, nodeid, insUserDoc, modNodeDoc, "UserBeforeStarted").equals("0")) { //用户任务启动前
                    insUserDoc.save();
                    s++;
                }
            }
        }
        return insUserDoc;
    }

    /**
     * 检测用户是否有设置委托办理
     * 
     * @param processid 流程id
     * @param userid 用户id
     * @return 返回true表示有，返回false表示无
     */
    public boolean checkEntrust(String processid, String docUnid, String nodeid, String userid) throws Exception {
        String sql = "select EntrustUserid,Processid from BPM_EntrustList where WF_AddName='" + userid + "' and Status='1'";
        //sql = "select EntrustUserid,Processid from BPM_EntrustList where WF_AddName='" + userid + "' and (status = 1 or (Status=2 and current_timestamp() >= StartTime and current_timestamp() <= EndTime ))";
        LinkedHashSet<Document> dc = Rdb.getAllDocumentsSetBySql("BPM_EntrustList", sql);
        for (Document doc : dc) {
            String processNumberStr = doc.g("Processid");
            HashSet<String> processNumberSet = Tools.splitAsSet(processNumberStr, ",");
            if (processNumberSet.contains(processid) || Tools.isBlank(processNumberStr)) {
                String toUserid = doc.g("EntrustUserid");//要委托的用户
                HashMap<String, String> deptSet = BeanCtx.getDeptidByMulStr(toUserid);
                toUserid = BeanCtx.getUseridByMulStr(toUserid);
                if (toUserid.equals(userid)) {
                    //不能委托给自已不然进入死循环
                    return false;
                }
                else {
                    //委托给其他人处理，有可能是A->B->A 进行死循环
                    Document newUserDoc = startUser(processid, docUnid, nodeid, toUserid, deptSet); //启动目标办理用户
                    newUserDoc.s("EntrustUserid", userid); //设置委托者的用户id
                    newUserDoc.save();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 结束指定流程的用户的审批权限，如果结束当前用户的权限可以使用endCurrentUser()方法
     * 
     * @param processid 流程id
     * @param docUnid 文档id
     * @param nodeid 用户所处环节id
     * @param userList 要结束的用列表，多个用逗号分隔
     * @return 成功结束的用户个数
     */
    public int endUser(String processid, String docUnid, String nodeid, String userList) throws Exception {
        if (Tools.isBlank(userList)) {
            return 0;
        }
        String[] userArray = Tools.split(userList, ",");
        int x = 0;
        Document modNodeDoc = ((ModNode) BeanCtx.getBean("ModNode")).getNodeDoc(processid, nodeid);
        for (String userid : userArray) {
            Document insUserDoc;
            //如果已存在时只执行下面数据更新部分即可
            if (userid.equals(BeanCtx.getUserid()) && nodeid.equals(BeanCtx.getLinkeywf().getCurrentNodeid())) {
                //说明是结束当前用户所处的环节
                insUserDoc = BeanCtx.getLinkeywf().getCurrentInsUserDoc(); //从缓存中拿当前用户的实例文档
                insUserDoc.s("EndTime", DateUtil.getNow());
                insUserDoc.s("TotalTime", DateUtil.getDifTime(insUserDoc.g("StartTime"), DateUtil.getNow()));
            }
            else if (userid.equals(BeanCtx.getUserid())) {
                //说明当前审批人和节点的处理人同一个也算他的处理时间，但从缓存中拿不到节点的实例文档对像
                insUserDoc = getInsUserDoc(processid, nodeid, docUnid, userid, "Current");//从sql表中拿
                insUserDoc.s("EndTime", DateUtil.getNow());
                String startTime = insUserDoc.g("StartTime");
                //如果是流程启动环节，开始时间是空，默认为当前时间
                startTime = Tools.isBlank(startTime) ? DateUtil.getNow() : startTime;
                insUserDoc.s("TotalTime", DateUtil.getDifTime(startTime, DateUtil.getNow()));
            }
            else {
                //如果流程不是他自已处理的则不能算他的完成时间，否在在流程绩效时会有偏差
                insUserDoc = getInsUserDoc(processid, nodeid, docUnid, userid, "Current"); //从sql表中拿
                insUserDoc.s("EndTime", "-");
                insUserDoc.s("TotalTime", "0");
            }

            //如果是新文档则执行本操作
            if (insUserDoc.isNewDoc()) {
                insUserDoc.s("Userid", BeanCtx.getUserid());
                insUserDoc.s("DocUnid", docUnid);
                insUserDoc.s("Processid", processid);
                insUserDoc.s("Nodeid", nodeid);
                insUserDoc.s("NodeName", modNodeDoc.g("NodeName"));
            }

            insUserDoc.s("ActionUserid", BeanCtx.getUserid());
            insUserDoc.s("Status", "End");
            insUserDoc.s("IsAgree", BeanCtx.g("WF_IsAgree", true)); //会签时是否同意选项

            //如果事件中返回0表示阻止此用户结束
            if (!this.changeStatus(userid, processid, nodeid, insUserDoc, modNodeDoc, "UserBeforeEnd").equals("0")) { /* 用户任务结束前 */
                insUserDoc.save();
                x++;
            }
        }
        return x;
    }

    /**
     * 结束指定流程指定环节所有Current状态的用户的审批权限
     * 
     * @param processid 流程id
     * @param docUnid 文档id
     * @param nodeid 用户所处环节id
     * @return 成功结束的用户个数
     */
    public int endUserByNodeid(String processid, String docUnid, String nodeid) throws Exception {
//        String nodeType = ((ModNode) BeanCtx.getBean("ModNode")).getNodeType(processid, nodeid);
        //只有Task,outProcess节点才需要进行用户的结束操作，其他类型的节点没有用户实例无需运行
        String sql = "select userid from bpm_InsUserList where DocUnid='" + docUnid + "' and Processid='" + processid + "' and Nodeid='" + nodeid + "' and Status='Current'";
        String endUserList = Rdb.getValueBySql(sql);
        if(Tools.isBlank(endUserList)){
        	return 0;
        }else{
        	return this.endUser(processid, docUnid, nodeid, endUserList);
        }
    }

    /**
     * 暂停用户的审批权限
     * 
     * @param processid 流程id
     * @param docUnid 文档id
     * @param nodeid 用户所处环节id
     * @param userList 多个用户使用逗号分隔
     * @return 返回成功暂停的用户数
     */
    public int pauseUser(String nodeid, String userList) throws Exception {
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        String processid = linkeywf.getProcessid();
        String docUnid = linkeywf.getDocUnid();
        String[] userArray = Tools.split(userList, ",");
        int userNum = userArray.length;
        int i, x = 0;
        for (i = 0; i < userNum; i++) {
            String userid = userArray[i];
            Document insUserDoc = getInsUserDoc(processid, nodeid, docUnid, userid, "Current");
            insUserDoc.s("Status", "Pause");
            insUserDoc.save();
            x++;
        }
        return x;
    }

    /**
     * 把用户状态改变为等待状态
     * 
     * @return 返回成功改为等待状态的用户数
     */
    public int waitUser(String nodeid, String userList) throws Exception {
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        String processid = linkeywf.getProcessid();
        String docUnid = linkeywf.getDocUnid();
        String[] userArray = Tools.split(userList, ",");
        int userNum = userArray.length;
        int i, x = 0;
        for (i = 0; i < userNum; i++) {
            String userid = userArray[i];
            Document insUserDoc = getInsUserDoc(processid, nodeid, docUnid, userid, "Current");
            insUserDoc.s("Status", "Wait");
            insUserDoc.save();
            x++;
        }
        return x;
    }

    /**
     * 传阅给指定用户
     * 
     * @param processid 流程id
     * @param nodeid 传阅用户所在节点
     * @param userList 要传阅的用户列表，多个用逗号分隔
     * @return 返回传阅成功的用户数
     */
    public int copyToUser(String processid, String nodeid, String userList) throws Exception {
        //所有用户启动前都要首先看节点是否已经启动，如果没有启动先启动节点
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        String docUnid = linkeywf.getDocUnid();
        LinkeyUser linkeyuser = ((LinkeyUser) BeanCtx.getBean("LinkeyUser"));
        HashSet<String> userArray = Tools.splitAsSet(userList, ","); //可以去掉重复值
        int x = 0;
        Document modNodeDoc = ((ModNode) BeanCtx.getBean("ModNode")).getNodeDoc(processid, nodeid);
        for (String userid : userArray) {
            if (Tools.isBlank(userid)) {
                continue;
            } //如果是空值则跳过
            String sql = "select WF_OrUnid from BPM_InsUserList where DocUnid='" + docUnid + "' and Userid='" + userid + "' and Status='Current'";
            if (!Rdb.hasRecord(sql)) {
                sql = "select WF_OrUnid from BPM_InsCopyUserList where DocUnid='" + docUnid + "' and Userid='" + userid + "' and Status='Current'";
                if (!Rdb.hasRecord(sql)) {
                    //如果记录不存在则创建一个传阅文档
                    Document insUserDoc = BeanCtx.getDocumentBean("BPM_InsCopyUserList");
                    insUserDoc.s("DocUnid", docUnid);
                    insUserDoc.s("Processid", processid);
                    insUserDoc.s("ProcessNumber", BeanCtx.getLinkeywf().getProcessNumber());
                    insUserDoc.s("Nodeid", nodeid);
                    insUserDoc.s("NodeName", modNodeDoc.g("NodeName"));
                    insUserDoc.s("StartTime", DateUtil.getNow());
                    insUserDoc.s("Userid", userid);
                    insUserDoc.s("ActionUserid", BeanCtx.getUserid());
                    insUserDoc.s("Deptid", linkeyuser.getDeptidByUserid(userid, false));
                    insUserDoc.s("SourceOrUnid", linkeywf.getSourceOrUnid());
                    insUserDoc.s("ActionNum", linkeywf.getActionNum());
                    insUserDoc.s("Status", "Current");
                    insUserDoc.save();
                    x++;
                }
            }
        }
        return x;
    }

    /**
     * 把用户标记为已阅
     * 
     * @param userList 要标记为已阅的用户列表多个用逗号分隔
     * @return 返回最后一个用户的文档对像
     */
    public Document endCopyUser(String userList) throws Exception {
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        String docUnid = linkeywf.getDocUnid();
        HashSet<String> userArray = Tools.splitAsSet(userList, ",");
        Document insUserDoc = null;
        for (String userid : userArray) {
            String sql = "select * from BPM_InsCopyUserList where Userid='" + userid + "' and  DocUnid='" + docUnid + "' and Status='Current'";
            insUserDoc = Rdb.getDocumentBySql("BPM_InsCopyUserList", sql);
            if (!insUserDoc.isNull()) {
                //只有用户已经有传阅文档的情况下才可以标识为已阅
                insUserDoc.s("EndTime", DateUtil.getNow());
                insUserDoc.s("TotalTime", DateUtil.getDifTime(insUserDoc.g("StartTime"), DateUtil.getNow()));
                insUserDoc.s("ActionUserid", BeanCtx.getUserid());
                insUserDoc.s("Status", "End");
                // changeStatus(userid,processid,insUserDoc.g("Nodeid"),insUserDoc,null,"UserBeforeEndRead"); //用户已阅前
                insUserDoc.save();
            }
        }
        return insUserDoc;
    }

    /**
     * 删除用户的审批权限
     * 
     * @param processid 流程id
     * @param docUnid 文档id
     * @param nodeid 用户所处环节id
     * @param userList 多个用逗号分隔
     * @return 返回成功删除的用户数
     */
    public int deleteUser(String processid, String docUnid, String nodeid, String userList) throws Exception {
        String[] userArray = userList.split(",");
        int userNum = userArray.length;
        int i, x = 0;
        for (i = 0; i < userNum; i++) {
            String userid = userArray[i];
            String sql = "delete from bpm_insUserList where Userid='" + userid + "' and  DocUnid='" + docUnid + "' and  Processid='" + processid + "' and Nodeid='" + nodeid + "' and Status='Current'";
            Rdb.execSql(sql);
            x++;
        }
        return x;
    }

    /**
     * 返回用户最近处理的用户实例文档,即最新一次处理的用户实例文档对像
     * 
     * @param userid 用户id
     * @param status 状态Current,End 等，当为end时有可能有多个end状态的，则只返回第一个
     * @return 只返回最新的处理任务文档
     */
    public Document getInsUserDoc(String processid, String nodeid, String docUnid, String userid, String status) {
        String sql;
        if (status.indexOf(",") == -1) {
            sql = "select * from bpm_insUserList where Userid='" + userid + "' and  DocUnid='" + docUnid + "' and  Processid='" + processid + "' and Nodeid='" + nodeid + "' and Status='" + status + "' order by EndTime DESC";
        }
        else {
            int spos = status.indexOf(",");
            String firstStatus = status.substring(0, spos);
            String endStatus = status.substring(spos + 1, status.length());
            sql = "select * from bpm_insUserList where Userid='" + userid + "' and  DocUnid='" + docUnid + "' and  Processid='" + processid + "' and Nodeid='" + nodeid + "' and (Status='"
                    + firstStatus + "' or Status='" + endStatus + "') order by EndTime DESC";
        }
        return Rdb.getDocumentBySql("bpm_insUserList", sql);
    }

    /**
     * 获得当前用户所处环节的实例文档对像
     * 
     * @return 当bpm_insuserlist表中的文档对像
     */
    public Document getCurInsUserDoc() {

        if (BeanCtx.getLinkeywf().getCurrentInsUserDoc() == null || BeanCtx.getLinkeywf().getCurrentInsUserDoc().isNull()) {
            //long ts = System.currentTimeMillis();

            //先得到所有活动的环节,Process过程节点不计算在内
            String sql = "select Nodeid from BPM_InsNodeList where DocUnid='" + BeanCtx.getLinkeywf().getDocUnid() + "' and Status='Current' and Nodeid<>'Process'";
            HashSet<String> nodeSet = Rdb.getValueSetBySql(sql);
            if (nodeSet.size() == 0) {
                Document doc = BeanCtx.getDocumentBean("BPM_InsUserList");
                doc.setIsNull();
                return doc;
            }
            StringBuilder sqlWhere = new StringBuilder();
            int i = 0;
            for (String nodeid : nodeSet) {
                if (i == 0) {
                    sqlWhere.append("Nodeid='" + nodeid + "'");
                    i = 1;
                }
                else {
                    sqlWhere.append(" or Nodeid='" + nodeid + "'");
                }
            }

            //再找这些活动的节点中有没有此用户的活动记录
            sql = "select * from BPM_InsUserList where DocUnid='" + BeanCtx.getLinkeywf().getDocUnid() + "' and Userid='" + BeanCtx.getUserid() + "' and Status='Current' and (" + sqlWhere.toString() + ")";
            return Rdb.getDocumentBySql("BPM_InsUserList", sql);
        }
        else {
            return BeanCtx.getLinkeywf().getCurrentInsUserDoc();
        }
    }

    /**
     * 根据任务id返回实例文档对像
     * 
     * @param taskid 即bpm_insuserlist表中的WF_OrUnid记录
     * @return
     */
    public Document getInsUserDocByTaskid(String taskid) {
        //在指定任务id的情况下，必须核对任务id是否是当前用户的任务，所以在加上userid=''条件
        return Rdb.getDocumentBySql("BPM_InsUserList", "select * from BPM_InsUserList where WF_OrUnid='" + taskid + "' and Status='Current' and Userid='" + BeanCtx.getUserid() + "'"); //当前用户实例文档
    }

    /**
     * 状态变化通知事件引擎处理,此事件暂时取消，由节点启动前和节点启动后事件来实现
     * 
     * @param processid 流程id
     * @param nodeid 节点id
     * @param oldStatus 旧的状态值
     * @param newStatus 新的状态值
     * @return 事件中返回0表示停止操作
     */
    private String changeStatus(String userid, String processid, String nodeid, Document insUserDoc, Document modNodeDoc, String eventType) throws Exception {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("processid", processid);
        params.put("nodeid", nodeid);
        params.put("userid", userid);
        params.put("insUserDoc", insUserDoc); //实例节点文档
        params.put("modNodeDoc", modNodeDoc); //模型节点文档
        return BeanCtx.getEventEngine().run(processid, nodeid, eventType, params); //触发事件
    }
}
