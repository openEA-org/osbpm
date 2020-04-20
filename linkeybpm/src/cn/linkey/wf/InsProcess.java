package cn.linkey.wf;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;

/**
 * 本类为单实例类，本类主要对流程过程进行启动和停止等
 * 
 * @author Administrator
 *
 */
public class InsProcess {
    /**
     * 获得当前用户所处流程的当前环节id号
     */
    public void getCurrentNodeid(String taskid) throws Exception {
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        ModNode modNode = (ModNode) BeanCtx.getBean("ModNode");
        String processid = linkeywf.getProcessid();
        String docUnid = linkeywf.getDocUnid();
        Document insUserDoc; //当前用户所处的文档对像bpm_insuserlist表中current的记录，有可能一个用户有多个current状态的环节
        if (Tools.isBlank(taskid)) {
            insUserDoc = ((InsUser) BeanCtx.getBean("InsUser")).getCurInsUserDoc(); //从bpm_insuserlist表中去找当前用户实例current的文档
        }
        else {
            insUserDoc = ((InsUser) BeanCtx.getBean("InsUser")).getInsUserDocByTaskid(taskid); //当前用户实例文档
        }
        if (!insUserDoc.isNull()) {
            //说明用户有审批权限及活动环节
            linkeywf.setCurrentInsUserDoc(insUserDoc); //缓存当前用户实例到流程引擎对像中去
            linkeywf.setCurrentNodeid(insUserDoc.g("Nodeid"));//当前用户所在环节
            linkeywf.setCurrentNodeName(insUserDoc.g("NodeName"));
            Document curModNodeDoc = modNode.getNodeDoc(processid, insUserDoc.g("Nodeid"));
            linkeywf.setCurrentModNodeDoc(curModNodeDoc); //缓存当前环节的模型文档到流程引擎对像中去
            if (BeanCtx.isMobile() && Tools.isNotBlank(curModNodeDoc.g("FormNumberForMobile"))) { //设置手机端表单
                linkeywf.setFormNumber(curModNodeDoc.g("FormNumberForMobile")); //说明当前环节中要更换手机表单
            }
            else {
                //pc端表单更换
                String formNumber = curModNodeDoc.g("FormNumber");
                if (Tools.isNotBlank(formNumber)) {
                    linkeywf.setFormNumber(formNumber); //说明当前环节中要更换表单
                }
            }
            //读取当前环节组织架构标识设置到全局变量中去，看在环节中是否需要切换组织标识
            if (Tools.isNotBlank(curModNodeDoc.g("OrgClass"))) {
                BeanCtx.setOrgClass(curModNodeDoc.g("OrgClass")); //看是否流程中强制指定了组织架构标识
            }
            linkeywf.setNewProcess(false); //说明不是新流程
        }
        else {
            //说明用户没有审批权限，看是否是新文档，如果是则需要查找开始环节作为当前用户的活动环节,否则表示此用户无权审批
            if (isNewProcess(docUnid)) {
                String startNodeid = modNode.getStartNodeid(processid);
                Document curModNodeDoc = modNode.getNodeDoc(processid, startNodeid);
                if (!curModNodeDoc.isNull()) {
                    linkeywf.setCurrentNodeid(startNodeid);
                    linkeywf.setCurrentNodeName(curModNodeDoc.g("NodeName"));
                    insUserDoc = getNewCurInsUserDoc(processid, docUnid, curModNodeDoc); //获得一个新的用户文档对像
                    linkeywf.setCurrentInsUserDoc(insUserDoc); //缓存新的文档对像
                    linkeywf.setCurrentModNodeDoc(curModNodeDoc); //缓存当前环节的模型文档到流程引擎对像中去
                    String formNumber = curModNodeDoc.g("FormNumber");
                    if (Tools.isNotBlank(formNumber)) {
                        linkeywf.setFormNumber(formNumber); //说明当前环节中要更换表单
                    }
                }
                else {
                    BeanCtx.log("E", "在流程(" + processid + ")中未找到(" + startNodeid + ")的节点配置信息!");
                }
                linkeywf.setNewProcess(true); //说明是新流程
            }
            else {
                linkeywf.setReadOnly(true); //说明是只读状态
                linkeywf.setCurrentNodeName("");
                linkeywf.setCurrentNodeid("");
                linkeywf.setNewProcess(false); //说明不是新流程
            }
        }

        //检测是否是首环节节点true表示是,false表示否
        if (Tools.isNotBlank(linkeywf.getCurrentNodeid())) {
            linkeywf.setFirstNode(modNode.isFirstNode(processid, linkeywf.getCurrentNodeid()));
        }

        //检测当前用户是否处于兼职状态
        if (linkeywf.getCurrentInsUserDoc() != null) {
            String deptid = linkeywf.getCurrentInsUserDoc().g("Deptid"); //当前审批节点中限定的部门id
            //获得当前用户活动的所在部门deptid
            String sql = "select Deptid from BPM_OrgUserDeptMap where Userid='" + BeanCtx.getUserid() + "' and CurrentFlag='1' and OrgClass='" + BeanCtx.getOrgClass() + "'";
            String currentDeptid = Rdb.getValueBySql(sql);
            //BeanCtx.out("当前用户活动的所在主部门="+currentDeptid);
            if (!deptid.equalsIgnoreCase(currentDeptid) && Tools.isNotBlank(deptid) && Tools.isNotBlank(currentDeptid)) {
                //如果用户实例中的deptid不等于mainDeptid就需要切换到当前的部门中去
                //BeanCtx.out("开始切换==从"+currentDeptid+"-切换到-"+deptid);
                BeanCtx.getLinkeyUser().changeCurrentDept(BeanCtx.getOrgClass(), BeanCtx.getUserid(), deptid); //切换到deptid的部门上去
            }
        }
    }

    /**
     * 判断当前流程是否有实例文档生成，来决定是否是一个新流程
     * 
     * @return true表示是新流程，false表示否
     */
    public boolean isNewProcess(String docUnid) {
        String sql = "select WF_OrUnid from BPM_InsNodeList where DocUnid='" + docUnid + "'";
        boolean hasDoc = Rdb.hasRecord(sql);
        if (hasDoc) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * 在新启动流程时，因为当前用户还没有在bpm_insuserlist表中产生记录，所以要用程序直接生成一个来代表用户的当前文档对像
     * 
     * @param processid 流程id
     * @param docUnid 文档unid
     * @param modNodeDoc 节点模型文档对像
     * @return 返回一个新的用户实例文档对像
     */
    public Document getNewCurInsUserDoc(String processid, String docUnid, Document modNodeDoc) {
        Document insUserDoc = BeanCtx.getDocumentBean("BPM_InsUserList");
        insUserDoc.s("WF_OrUnid", Rdb.getNewid("BPM_InsUserList"));
        insUserDoc.s("processid", processid);
        insUserDoc.s("docUnid", docUnid);
        insUserDoc.s("StartTime", DateUtil.getNow());
        String deptid = BeanCtx.getLinkeyUser().getDeptidByUserid(BeanCtx.getUserid(), false);
        insUserDoc.s("DocUnid", docUnid);
        insUserDoc.s("Processid", processid);
        insUserDoc.s("ProcessNumber", BeanCtx.getLinkeywf().getProcessNumber());
        insUserDoc.s("Nodeid", modNodeDoc.g("Nodeid"));
        insUserDoc.s("NodeName", modNodeDoc.g("NodeName"));
        insUserDoc.s("StartTime", DateUtil.getNow());
        insUserDoc.s("ExceedTime", modNodeDoc.g("ExceedTime"));
        insUserDoc.s("Userid", BeanCtx.getUserid());
        insUserDoc.s("Deptid", deptid);
        insUserDoc.s("LimitTime", "");
        insUserDoc.s("Status", "NoStart");
        insUserDoc.s("SourceOrUnid", insUserDoc.g("WF_OrUnid"));
        insUserDoc.s("ActionNum", BeanCtx.getLinkeywf().getActionNum());//从环节实例中得到节点实例号，可以区分每一次启时节点与用户之间的对应关系
        insUserDoc.s("WF_IsNewInsUserDoc", "1"); //标识此文档为一个新建立的用户实例，在sql表中并不存在
        return insUserDoc;
    }

    /**
     * 初始化过程属性中的相关变量，流程名称、流程所属应用、流程所属企业、流程过程读者等
     */
    public void initProcessVar(String taskid) throws Exception {
        Document processDoc = ((ModNode) BeanCtx.getBean("ModNode")).getNodeDoc(BeanCtx.getLinkeywf().getProcessid(), "Process");
        if (!processDoc.isNull()) {
            String appid = processDoc.g("WF_Appid");
            if (Tools.isNotBlank(appid)) {
                BeanCtx.setAppid(appid);//线程全局变量中也设置进去,这样在访问R_S003_B035,36时得到的应用编号不是S003
                BeanCtx.getLinkeywf().setAppid(appid);
            }
            else {
                BeanCtx.getLinkeywf().setAppid(BeanCtx.getAppid());
            }
            BeanCtx.getLinkeywf().setProcessModNodedoc(processDoc); //把当前流程过程属性的对像设置到流程引擎对像中去，方便读取过程属性
            BeanCtx.getLinkeywf().setProcessName(processDoc.g("NodeName"));
            BeanCtx.getLinkeywf().setProcessNumber(processDoc.g("ProcessNumber"));

            //获得扩展的业务数据存储表
            String extendTableName = processDoc.g("ExtendTableName");
            if (Tools.isBlank(extendTableName)) {
                extendTableName = "xmldata";
            }
            BeanCtx.getLinkeywf().setExtendTable(extendTableName);

            if (BeanCtx.isMobile() && Tools.isNotBlank(processDoc.g("FormNumberForMobile"))) { //设置手机端表单
                BeanCtx.getLinkeywf().setFormNumber(processDoc.g("FormNumberForMobile")); //设置当前流程过程中指定的主表单,在getCurrentNodeid()中会判断是否更换主表单
            }
            else {
                BeanCtx.getLinkeywf().setFormNumber(processDoc.g("FormNumber")); //设置当前流程过程中指定的主表单,在getCurrentNodeid()中会判断是否更换主表单
            }
            //读取组织架构标识设置到全局变量中去,针对整个流程的
            if (Tools.isNotBlank(processDoc.g("OrgClass"))) {
                BeanCtx.setOrgClass(processDoc.g("OrgClass")); //看是否流程中强制指定了组织架构标识
            }
            //看是否是流程管理员
            String processOwner = processDoc.g("ProcessOwner");
            if (Tools.isNotBlank(processOwner)) {
                if (BeanCtx.getUserid().equals("admin") || BeanCtx.getLinkeyUser().inRoles(BeanCtx.getUserid(), processOwner)) {
                    BeanCtx.getLinkeywf().setProcessOwner(true);//说明是流程管理员
                }
            }
            //设置当前环节的文档对像和当前节点信息
            getCurrentNodeid(taskid);

            //设置调试状态,通过设置DebugAllProcess来设置调试所有流程
            if (processDoc.g("Debug").equals("1") || BeanCtx.getSystemConfig("DebugAllProcess").equals("1")) {
                if (Tools.isNotBlank(BeanCtx.getLinkeywf().getCurrentNodeid())) {
                    BeanCtx.getLinkeywf().setDebug(true);
                }
            }
            //设置回滚状态，调试用
            if (processDoc.g("RollBack").equals("1")) {
                if (Tools.isNotBlank(BeanCtx.getLinkeywf().getCurrentNodeid())) {
                    BeanCtx.setRollback(true);
                    BeanCtx.getLinkeywf().setRollbackMsg(BeanCtx.getMsg("Engine", "EngineDebugMsg")); //输出调试状态的信息
                }
            }
        }
        else {
            BeanCtx.getLinkeywf().setReadOnly(true);
            BeanCtx.log("E", "InsProcess.未找到流程(" + BeanCtx.getLinkeywf().getProcessid() + ")的配置信息,请传入流程id号!");
        }
    }

    /**
     * 启动一个流程
     * 
     * @param docUnid 文档unid
     * @param processid 流程id
     * @param actionNum 动作id
     * @return 返回1表示新流程启动，0表示启动失败，2表示流程已经在启动状态
     */
    public int startProcess(String docUnid, String processid) throws Exception {

        //获得流程节点文档
        String sql = "select * from bpm_insnodelist where DocUnid='" + docUnid + "' and Processid='" + processid + "' and Nodeid='Process' and Status='Current'";
        Document processDoc = Rdb.getDocumentBySql("bpm_insnodelist", sql);
        if (processDoc.isNull()) {
            //获得流程模型节点文档
            Document modProcessDoc = ((ModNode) BeanCtx.getBean("ModNode")).getNodeDoc(processid, "Process");
            if (modProcessDoc.isNull()) {
                BeanCtx.log("E", "未找到流程(" + processid + ")的配置信息!");
                return 0;
            }
            processDoc.s("DocUnid", docUnid);
            processDoc.s("Processid", processid);
            processDoc.s("Nodeid", "Process");
            processDoc.s("NodeName", modProcessDoc.g("NodeName"));
            processDoc.s("NodeType", "Process");
            processDoc.s("ProcessNumber", modProcessDoc.g("ProcessNumber"));
            processDoc.s("ExceedTime", modProcessDoc.g("ExceedTime")); //节点持续时间
            processDoc.s("StartTime", DateUtil.getNow());
            processDoc.s("ActionUserid", BeanCtx.getUserid());
            processDoc.s("Status", "Current");
            processDoc.s("ActionNum", BeanCtx.getLinkeywf().getActionNum());
            processDoc.s("SourceOrUnid", BeanCtx.getLinkeywf().getCurrentInsUserDoc().g("WF_OrUnid"));
            changeStatus("NoStart", "Current", processid, "ProcessStatusBeforeChanged", processDoc, modProcessDoc); //状态改变之前
            processDoc.save();
            changeStatus("NoStart", "Current", processid, "ProcessStatusAfterChanged", processDoc, modProcessDoc); //通知状态发生变化，触发流程事件
            return 1; //流程启动成功
        }
        else {
            return 2; //流程已经在启动状态
        }
    }

    /**
     * 结束流程运行
     * 
     * @param docUnid
     * @param processid
     * @return
     */
    public boolean endProcess(String docUnid, String processid) throws Exception {
        //获得流程节点文档
        String sql = "select * from bpm_insnodelist where DocUnid='" + docUnid + "' and Processid='" + processid + "' and Nodeid='Process'";
        Document processDoc = Rdb.getDocumentBySql("bpm_insnodelist", sql);
        if (!processDoc.isNull()) {
            String status = processDoc.g("Status");
            processDoc.s("EndTime", DateUtil.getNow());
            processDoc.s("TotalTime", DateUtil.getDifTime(processDoc.g("StartTime"), DateUtil.getNow()));
            processDoc.s("ActionUserid", BeanCtx.getUserid());
            processDoc.s("Status", "End");

            //获得模型节点文档对像
            sql = "select * from BPM_ModProcessList where processid='" + processid + "' nodeid='Process'";
            Document modProcessDoc = Rdb.getDocumentBySql("BPM_ModProcessList", sql);
            if (modProcessDoc.isNull()) {
                BeanCtx.log("E", "未找到流程(" + processid + ")的配置信息!");
                return false;
            }
            if (!changeStatus(status, "End", processid, "ProcessStatusBeforeChanged", processDoc, modProcessDoc).equals("0")) { //状态改变之前
                processDoc.save();
                changeStatus(status, "End", processid, "ProcessStatusAfterChanged", processDoc, modProcessDoc); //通知状态发生变化，触发流程事件
                return true;
            }
        }
        return false;

    }

    /**
     * 调用startProcess()方法之前不能指定SourceOrUnid因为流程往往先于用户启动，这时还没有SourceOrUnid,只有启动子流程时才存在这种情况 更新流程的SourceOrUnid属性，用来反向查找BPM_InsUserList表中WF_OrUnid是那个用户文档启动的本流程
     * 
     * @param docUnid 文档id
     * @param processid 流程id
     * @return 返回是否更新成功，负数表示更新失败
     */
    public int updateSourceOrUnid(String docUnid, String processid, String SourceOrUnid) {
        //获得流程节点文档
        String sql = "update bpm_insnodelist set SourceOrUnid='" + SourceOrUnid + "' where DocUnid='" + docUnid + "' and Processid='" + processid + "' and Nodeid='Process'";
        return Rdb.execSql(sql);
    }

    /**
     * 暂停流程运行
     * 
     * @param docUnid
     * @param processid
     * @return
     */
    public boolean pauseProcess(String docUnid, String processid) throws Exception {
        //获得流程节点文档
        String sql = "select * from bpm_insnodelist where DocUnid='" + docUnid + "' and Processid='" + processid + "' and Nodeid='Process' and Status='Current'";
        Document processDoc = Rdb.getDocumentBySql("bpm_insnodelist", sql);
        if (!processDoc.isNull()) {
            String status = processDoc.g("Status");
            processDoc.s("ActionUserid", BeanCtx.getUserid());
            processDoc.s("Status", "Pause");
            //获得模型节点文档对像
            sql = "select * from BPM_ModProcessList where processid='" + processid + "' and nodeid='Process'";
            Document modProcessDoc = Rdb.getDocumentBySql("BPM_ModProcessList", sql);
            if (modProcessDoc.isNull()) {
                BeanCtx.log("E", "未找到流程(" + processid + ")的配置信息!");
                return false;
            }
            if (!changeStatus(status, "Pause", processid, "ProcessStatusBeforeChanged", processDoc, modProcessDoc).equals("0")) { //状态改变之前
                processDoc.save();
                changeStatus(status, "Pause", processid, "ProcessStatusAfterChanged", processDoc, modProcessDoc); //通知状态发生变化，触发流程事件
                return true;
            }
        }
        return false;

    }

    /**
     * 把流程变为等待状态
     * 
     * @param docUnid
     * @param processid
     * @return
     */
    public boolean waitProcess(String docUnid, String processid) throws Exception {
        //获得流程节点文档
        String sql = "select * from bpm_insnodelist where DocUnid='" + docUnid + "' and Processid='" + processid + "' and Nodeid='Process' and Status='Current'";
        Document processDoc = Rdb.getDocumentBySql("bpm_insnodelist", sql);
        if (!processDoc.isNull()) {
            String status = processDoc.g("Status");
            processDoc.s("ActionUserid", BeanCtx.getUserid());
            processDoc.s("Status", "Pause");
            //获得模型节点文档对像
            sql = "select * from BPM_ModProcessList where processid='" + processid + "' and nodeid='Process'";
            Document modProcessDoc = Rdb.getDocumentBySql("BPM_ModProcessList", sql);
            if (modProcessDoc.isNull()) {
                BeanCtx.log("E", "未找到流程(" + processid + ")的配置信息!");
                return false;
            }
            if (!changeStatus(status, "Pause", processid, "ProcessStatusBeforeChanged", processDoc, modProcessDoc).equals("0")) { //状态改变之前
                processDoc.save();
                changeStatus(status, "Pause", processid, "ProcessStatusAfterChanged", processDoc, modProcessDoc); //通知状态发生变化，触发流程事件 
                return true;
            }
        }
        return false;
    }

    /**
     * 状态变化通知事件引擎处理
     * 
     * @param oldStatus 旧的状态值
     * @param newStatus 新的状态值
     * @return
     */
    public String changeStatus(String oldStatus, String newStatus, String processid, String eventType, Document processdoc, Document modProcessDoc) throws Exception {
        //首先看事件是否有定义，如果不存在直接返回1表示成立
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("oldStatus", oldStatus);
        params.put("newStatus", newStatus);
        params.put("processid", processid);
        params.put("nodeid", "Process");
        params.put("insProcessDoc", processdoc); //实例节点文档
        params.put("modProcessDoc", modProcessDoc); //模型节点文档
        return BeanCtx.getEventEngine().run(processid, "Process", "NodeStatusChanged", params);
    }

}
