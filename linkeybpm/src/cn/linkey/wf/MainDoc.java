package cn.linkey.wf;

import java.util.HashMap;
import java.util.LinkedHashSet;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.form.ModForm;
import cn.linkey.org.LinkeyUser;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;

/**
 * 流程主文档操作类主要负责流程主文档的存盘和相应的文档运算业务逻辑
 * 
 * @author Administrator 本类为单例类
 */
public class MainDoc {
    /**
     * 存盘流程主文档
     * 
     * @return 存盘存功返回1,否则返回提示字符串信息
     */
    public String saveDocument() throws Exception {
        // long ts = System.currentTimeMillis();

        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        Document document = linkeywf.getDocument();
        String processid = linkeywf.getProcessid();
        String docUnid = linkeywf.getDocUnid();
        ModNode insModNode = (ModNode) BeanCtx.getBean("ModNode");
        Document insProcessDoc = insModNode.getNodeDoc(processid, "Process");// 过程属性文档

        if (linkeywf.getIsNewProcess()) {
            // 如果是新流程则执行以下初始化字段

            String processReader = insProcessDoc.g("ProcessReader"); // 过程读者
            String processOwner = insProcessDoc.g("ProcessOwner"); // 流程所有者

            document.appendTextList("WF_AllReaders", processReader + "," + processOwner);// 增加流程读者和管理员为读者
            if (Tools.isBlank(document.g("Subject"))) {
                document.s("Subject", linkeywf.getProcessName()); // 如果标题为空则自动设置为流程名称
            }
            document.s("WF_AddDeptid", ((LinkeyUser) BeanCtx.getBean("LinkeyUser")).getDeptidByUserid(BeanCtx.getUserid(), false)); // 启动者所在部门id
            document.s("WF_ProcessNumber", linkeywf.getProcessNumber());
            document.s("WF_ProcessName", linkeywf.getProcessName());
            document.s("WF_Appid", insProcessDoc.g("WF_Appid"));
            document.s("WF_DocCreated", DateUtil.getNow()); // 设置创建时间，这样可以准备计算文档的总时间

            // 如果是测试流程则把测试流程标识为1,用来方便删除测试数据
            if (insProcessDoc.g("TestState").equals("1")) {
                document.s("WF_Systemid", "TEST");
            }
        }

        // 设置文档的相应审批，阅读，阅读权限
        NodeUser insNodeUser = (NodeUser) BeanCtx.getBean("NodeUser");
        LinkedHashSet<String> author = insNodeUser.getAuthorUser(docUnid); // 当前审批用户
        insNodeUser.getInOutUser(author); // 计算新增加和结束的用户要在WF_Author改变之前进行计算才能进行新旧值的比较
        document.s("WF_Author", author); // 所有当前的审批用户
        document.s("WF_Author_CN", ((LinkeyUser) BeanCtx.getBean("LinkeyUser")).getCnName(author)); // 审批用户中文名
        document.appendTextList("WF_AllReaders", insNodeUser.getAllReaderUser(docUnid)); // 追加所有读者域字段
        document.s("WF_EndUser", insNodeUser.getEndUser(docUnid)); // 设置所有已办用户
        document.s("WF_CopyUser", insNodeUser.getCopyUser(docUnid)); // 设置所有待阅用户
        // document.s("WF_EndCopyUser",insNodeUser.getEndCopyUser(docUnid)); //设置所有已阅用户 此功能暂时取消，影响性能

        // 设置委托人和被委托人字段
        String sql = "select Userid,EntrustUserid from BPM_InsUserList where DocUnid='" + docUnid + "' and EntrustUserid<>''";
        HashMap<String, String> entrustUserMap = Rdb.getAllMapDataBySql(sql);
        document.s("WF_SourceEntrustUserid", entrustUserMap.get("EntrustUserid"));
        document.s("WF_TargetEntrustUserid", entrustUserMap.get("Userid"));

        // 设置其他字段
        document.s("WF_CurrentNodeName", insNodeUser.getCurrentNodeName(docUnid)); // 设置当前环节名称
        document.s("WF_CurrentNodeid", insNodeUser.getCurrentNodeid(docUnid)); // 设置当前环节id
        document.s("WF_Processid", processid); // 流程编号

        // 设置文档状态为活动状态
        if (Tools.isBlank(document.g("WF_Status")) || author.size() > 0) {
            document.s("WF_Status", "Current");
        }

        // 设置文档流水号
        if (Tools.isBlank(document.g("WF_DocNumber"))) {
            document.s("WF_DocNumber", Rdb.getNewSerialNo("ENGINEDOCUMENT"));
        }

        /* 运行主表单存盘事件 */
        String returnMsg = "1";
        String ruleNum = linkeywf.getFormDoc().g("EventRuleNum");
        if (Tools.isNotBlank(ruleNum)) {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("FormDoc", linkeywf.getFormDoc());
            params.put("DataDoc", document);
            params.put("EventName", "onFormSave");
            returnMsg = BeanCtx.getExecuteEngine().run(ruleNum, params); // 运行表单存盘事件
            if (!returnMsg.equals("1")) {
                // 说明事件中要退出本次存盘
                return returnMsg;
            }
        }

        // 运行当前环节子表单的存盘事件
        String ruleResult = ((ModForm) BeanCtx.getBean("ModForm")).runSubFormEvent("onFormSave", true);
        if (!ruleResult.equals("1")) {
            // 说明事件中要退出本次表单存盘
            return ruleResult;
        }

        BeanCtx.getEventEngine().run(processid, linkeywf.getCurrentNodeid(), "EngineDocumentBeforeSave"); /* 触发存盘前事件 */
        if (linkeywf.getFormDoc().g("NoEnCode").equals("1")) {
            BeanCtx.setDocNotEncode(); // 设置为主文档不编码存盘
        }

        // 看是否存数据快照用来追查数据的改变情况
        if (insProcessDoc.g("DataSnapshotFlag").equals("1")) {
            saveDataSnapshot();
        }

        int i = document.saveToExtendTable(linkeywf.getMainTableName(), linkeywf.getExtendTable()); // 返回sql运行结果

        BeanCtx.setDocEncode(); // 重新设置回原状态

        // long te = System.currentTimeMillis();
        // BeanCtx.p("流程文档存盘总时间毫秒="+ (te - ts));
        BeanCtx.getEventEngine().run(processid, linkeywf.getCurrentNodeid(), "EngineDocumentAfterSave"); /* 触发存盘后事件 */
        if (i < 1) {
            BeanCtx.log("E", BeanCtx.getUserid() + "流程主文档存盘失败docUnid=" + docUnid);
            return BeanCtx.getMsg("Engine", "Error_SaveMainDoc");
        }
        else {
            return "1"; // 存盘成功
        }

    }

    /**
     * 归档流程主文档
     * 
     * @return 归档成功返回非负数
     */
    public int archiveDocument() throws Exception {
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        Document document = linkeywf.getDocument();
        String processid = linkeywf.getProcessid();
        String docUnid = linkeywf.getDocUnid();
        String endNodeid = linkeywf.getEndNodeid(); // 结束环节的nodeid

        // 设置归档环节的默认设置参数
        String endBusinessName = BeanCtx.getMsg("Engine", "EndBusinessName"), endBusinessid = "1";
        ModNode insModNode = (ModNode) BeanCtx.getBean("ModNode");
        if (Tools.isNotBlank(endNodeid) && !endNodeid.equals("GoToArchived")) {
            Document endModNodeDoc = insModNode.getNodeDoc(processid, endNodeid); // 结束环节的
            endBusinessName = endModNodeDoc.g("EndBusinessName"); // 结束后的状态名称
            endBusinessid = endModNodeDoc.g("EndBusinessid"); // 结束后的状态标识
        }

        // 从结束环节中获得结束后的状态说明
        Document processNodeDoc = insModNode.getNodeDoc(processid, "Process"); // 获得过程属性中的归档设置
        if (!processNodeDoc.g("AutoArchive").equals("1")) {
            return 0;
        } // 不需要自动归档
        document.s("WF_CurrentNodeName", endBusinessName);// 结束后的环节名称
        document.s("WF_EndBusinessid", endBusinessid);// 结束后的业务状态标识

        // 从过程属性中获得归档设置参数
        document.s("WF_Status", "ARC"); // 设置为已归档状态
        document.s("WF_EndTime", DateUtil.getNow());
        document.s("WF_TotalTime", DateUtil.getDifTime(document.g("WF_DocCreated"), DateUtil.getNow())); // 计算总耗时
        if (Tools.isBlank(document.g("WF_Folderid"))) {
            document.s("WF_Folderid", processNodeDoc.g("ArchiveFolderid")); // 设置归档后所处文件夹
        }
        document.s("WF_ArcFormNumber", processNodeDoc.g("ArcFormNumber"));// 结束后指定显示表单的编号

        // 设置流程权限字段
        // NodeUser insNodeUser=(NodeUser)BeanCtx.getBean("NodeUser");
        document.s("WF_Author", ""); // 所有当前的审批用户
        document.s("WF_Author_CN", ""); // 审批用户中文名
        document.appendTextList("WF_AllReaders", processNodeDoc.g("ArchiveReader")); // 追加归档后的阅读范围人员

        BeanCtx.getEventEngine().run(processid, "Process", "DocumentBeforeArchived"); // 归档前触发事件

        // 归档前保存一次主数据
        // BeanCtx.p("archiveDocument->准备归档...");
        // document.save(); //存一份到主文档中bpm_maindata表中，这样归档失败的话这个份文档也是可以手动归档的,有事务暂时不存在归档失败的可能性

        // 开始归档
        String arcTableName = processNodeDoc.g("ArchiveTableName");
        if (Tools.isNotBlank(document.g("WF_ArchiveTableName"))) {
            arcTableName = document.g("WF_ArchiveTableName"); // 主文档中指定归档数据库表
        }
        if (Tools.isBlank(arcTableName)) {
            arcTableName = "BPM_ArchivedData";
        } // 获得归档表

        archiveRemarkList(docUnid); // 归档流转记录
        archiveReportDoc(docUnid); // 归档insNodeList,insUserList表中的数据
        archiveModGraphic(processid, docUnid);// 归档流程图形

        // BeanCtx.p("archiveDocument->归档表"+arcTableName);
        if (linkeywf.getFormDoc().g("NoEnCode").equals("1")) {
            BeanCtx.setDocNotEncode(); // 设置为主文档不编码存盘
        }
        int r = document.saveToExtendTable(arcTableName, linkeywf.getExtendTable()); // 把文档存盘一份到指定的归档表中去

        if (r > 0) {
            // 归档成功
        }
        else {
            BeanCtx.log("E", "(" + document.g("Subject") + ")流程归档时出错,归档失败!");
            BeanCtx.log("D", "文档UNID=" + document.getDocUnid());
        }
        BeanCtx.setDocEncode(); // 恢复需要编码状态

        if (r > 0) {
            deleteForArc(docUnid);
        } // 如果主文档归档成功则执行删除数据操作

        BeanCtx.getEventEngine().run(processid, "Process", "DocumentAfterArchived"); // 归档后触发事件
        return r; // 返回sql的存盘结果

    }

    /**
     * 归档流转记录
     * 
     * @param docUnid为主文档unid
     * @return
     */
    protected int archiveRemarkList(String docUnid) {
        if (Rdb.hasRecord("select WF_OrUnid from BPM_InsRemarkList where DocUnid='" + docUnid + "'")) {
            // 如果流转记录存在才需要执行
            String colList = Tools.join(Rdb.getTableColumnName("BPM_ArchivedRemarkList").keySet(), ",");
            String sql = "insert into BPM_ArchivedRemarkList(" + colList + ") Select " + colList + " From BPM_InsRemarkList where DocUnid='" + docUnid + "'";
            int i = Rdb.execSql(sql);
            if (i < 1) {
                BeanCtx.log("E", "归档流转记录时出错,记录没有插入到BPM_ArchivedRemarkList中(" + sql + ")");
            }
            return i;
        }
        else {
            return 0;
        }
    }

    /**
     * 归档流程图
     * 
     * @param processid 流程的唯一id
     * @param docunid 流程实例id
     * @return
     */
    private int archiveModGraphic(String processid, String docUnid) {
        if (BeanCtx.getSystemConfig("ArchivedGraphic").equals("1")) {
            // 只有在通用配置中配了才可以归档
            String sql = "select * from BPM_ArchivedGraphicList where Processid='" + processid + "'";
            Document doc = Rdb.getDocumentBySql(sql);
            doc.s("DocUnid", docUnid);
            int i = doc.save("BPM_ArchivedGraphicList");
            return i;
        }
        else {
            // 默认的情况下不进行归档
            return 0;
        }
    }

    /**
     * 归档报表所需数据
     * 
     * @return
     */
    protected int archiveReportDoc(String docUnid) {
        // 归档所有的节点以及用户数据到报表表中去，用来统计报表用
        /*
         * //如果是工作流组件则用此方法速度比较快 String colList=Tools.join(Rdb.getTableColumnName("BPM_InsNodeList"),","); String sql="insert into BPM_ReportNodeList("+colList+") Select "+colList+
         * " From BPM_InsNodeList where DocUnid='"+docUnid+"'"; int i=Rdb.execSql(sql); if(i<1){ BeanCtx.log("E", "归档节点实例数据时出错,记录没有插入到BPM_ReportNodeList中("+sql+")"); }
         */

        // 获得所有节点实例的文档并加上节点模型上的字段一起存入到归档报表的节点记录中,这样就可以把模型中的kpi各指定存入到历史数据中去
        // 如果是bpm则用此方法能获得更多的kpi报表
        ModNode modNode = (ModNode) BeanCtx.getBean("ModNode");
        Document[] dc = Rdb.getAllDocumentsBySql("Select * From BPM_InsNodeList where DocUnid='" + docUnid + "'");
        for (Document doc : dc) {
            String nodeid = doc.g("Nodeid");
            String processid = doc.g("Processid");
            Document modDoc = modNode.getNodeDoc(processid, nodeid); // 模型文档对像
            doc.copyAllItems(modDoc);
            if (nodeid.equals("Process")) {
                modDoc.s("EndTime", DateUtil.getNow());
                modDoc.s("Status", "End");
                modDoc.s("TotalTime", DateUtil.getDifTime(doc.g("StartTime"), DateUtil.getNow())); // 流程的完成时间
            }
            modDoc.save("BPM_ReportNodeList"); // 存入归档表中
        }

        // 归档用户实例
        if (Rdb.hasRecord("select WF_OrUnid from BPM_InsUserList where DocUnid='" + docUnid + "'")) {
            String colList = Tools.join(Rdb.getTableColumnName("BPM_InsUserList").keySet(), ",");
            String sql = "insert into BPM_ReportUserList(" + colList + ") Select " + colList + " From BPM_InsUserList where DocUnid='" + docUnid + "'";
            int i = Rdb.execSql(sql);
            if (i < 1) {
                BeanCtx.log("E", "归档用户实例数据时出错,记录没有插入到BPM_ReportUserList中(" + sql + ")");
            }
            return i;
        }
        else {
            return 0;
        }
    }

    /**
     * 归档时删除主文档BPM_MainData中的主数据
     * 
     * 本方法删除数据不会进入回收站中，而是直接删除掉 
     * 
     * 删除时会删除与此主数据相关的全部实例数据 附件通过主文档doc对像删除时进行删除
     * 
     * @param docUnid文档unid
     * @return
     */
    public void deleteAllInsData(String docUnid) {
        String sql = "";

        // 首先删除扩展表中的业务数据表
        sql = "select WF_Processid from bpm_maindata where WF_OrUnid='" + docUnid + "'";
        String processid = Rdb.getValueBySql(sql);
        try {
            ProcessEngine linkeywf = BeanCtx.getDefaultEngine();
            linkeywf.init(processid, docUnid, BeanCtx.getUserid(), "");
            if (!linkeywf.getExtendTable().equalsIgnoreCase("xmldata")) {
                sql = "delete from " + linkeywf.getExtendTable() + " where WF_OrUnid='" + docUnid + "'";
                Rdb.execSql(sql);
            }
        }
        catch (Exception e) {
            BeanCtx.log(e, "E", "删除业务数据表中的数据时出错");
        }

        // 删除流程的实例数据
        sql = "Delete From BPM_InsRemarkList Where DocUNID='" + docUnid + "'"; // 流转记录实例
        Rdb.execSql(sql);
        sql = "Delete From BPM_InsUserList Where DocUNID='" + docUnid + "'"; // 节点实例
        Rdb.execSql(sql);
        sql = "Delete From BPM_InsNodeList Where DocUNID='" + docUnid + "'"; // 节点实例
        Rdb.execSql(sql);
        sql = "Delete From BPM_InsCopyUserList Where DocUNID='" + docUnid + "'"; // 待阅数据
        Rdb.execSql(sql);
        sql = "Delete From BPM_InsStayData Where DocUNID='" + docUnid + "'"; // 流程驻留数据
        Rdb.execSql(sql);
        sql = "Delete From BPM_LockDocList Where DocUNID='" + docUnid + "'"; // 锁定表
        Rdb.execSql(sql);
        sql = "Delete From BPM_TempRemarkList Where DocUNID='" + docUnid + "'"; // 临时意见
        Rdb.execSql(sql);
        sql = "update BPM_ToDoBox set Status='3' Where DocUNID='" + docUnid + "'"; // 待办数据
        Rdb.execSql(sql);
        sql = "delete from BPM_DataSnapshot  Where DS_DocUnid='" + docUnid + "'"; // 数据快照
        Rdb.execSql(sql);
        sql = "delete from  BPM_SubFormData where DocUnid='" + docUnid + "'"; // 子表单数据
        Rdb.execSql(sql);

    }

    /**
     * 删除已归档的主数据
     * 
     * @param docUnid
     */
    public void deleteArchivedData(String docUnid) {
        String sql = "";

        // 首先删除扩展表中的业务数据表
        sql = "select WF_Processid from BPM_ArchivedData where WF_OrUnid='" + docUnid + "'";
        String processid = Rdb.getValueBySql(sql);
        try {
            ProcessEngine linkeywf = BeanCtx.getDefaultEngine();
            linkeywf.init(processid, docUnid, BeanCtx.getUserid(), "");
            if (!linkeywf.getExtendTable().equalsIgnoreCase("xmldata")) {
                sql = "delete from " + linkeywf.getExtendTable() + " where WF_OrUnid='" + docUnid + "'";
                Rdb.execSql(sql);
            }
        }
        catch (Exception e) {
            BeanCtx.log(e, "E", "删除业务数据表中的数据时出错");
        }

        // 删除可能有的实例数据
        sql = "Delete From BPM_ArchivedRemarkList Where DocUNID='" + docUnid + "'"; // 流转记录实例
        Rdb.execSql(sql);
        sql = "Delete From BPM_AttachmentLog Where DocUNID='" + docUnid + "'"; // 附件日记
        Rdb.execSql(sql);
        sql = "Delete From BPM_InsNodeList Where DocUNID='" + docUnid + "'"; // 节点实例
        Rdb.execSql(sql);
        sql = "Delete From BPM_InsCopyUserList Where DocUNID='" + docUnid + "'"; // 待阅数据
        Rdb.execSql(sql);
        sql = "Delete From BPM_InsStayData Where DocUNID='" + docUnid + "'"; // 流程驻留数据
        Rdb.execSql(sql);
        sql = "Delete From BPM_LockDocList Where DocUNID='" + docUnid + "'"; // 锁定表
        Rdb.execSql(sql);
        sql = "delete from BPM_DataSnapshot  Where DS_DocUnid='" + docUnid + "'"; // 数据快照
        Rdb.execSql(sql);
        sql = "delete from  BPM_SubFormData where DocUnid='" + docUnid + "'"; // 子表单数据
        Rdb.execSql(sql);
    }

    /**
     * 归档时专用,归档后有些数据已经移动归档表中去了，所以这些实例数据需要删除 
     * 
     * 归档时删除主文档BPM_MainData中的主数据
     * 
     * @param docUnid文档unid
     * @return
     */
    private int deleteForArc(String docUnid) {

        // 删除主文档归档后一些不需要的实例数据
        String sql = "";
        sql = "Delete From BPM_InsRemarkList Where DocUNID='" + docUnid + "'";
        Rdb.execSql(sql);
        sql = "Delete From BPM_InsUserList Where DocUNID='" + docUnid + "'";
        Rdb.execSql(sql);
        sql = "Delete From BPM_InsNodeList Where DocUNID='" + docUnid + "'";
        Rdb.execSql(sql);
        sql = "Delete From BPM_InsStayData Where DocUNID='" + docUnid + "'";
        Rdb.execSql(sql);
        sql = "Delete From BPM_LockDocList Where DocUNID='" + docUnid + "'";
        Rdb.execSql(sql);
        sql = "Delete From BPM_TempRemarkList Where DocUNID='" + docUnid + "'";
        Rdb.execSql(sql);
        sql = "update BPM_ToDoBox set Status='3' Where DocUNID='" + docUnid + "'";
        Rdb.execSql(sql);

        // 删除主文档
        sql = "delete from " + BeanCtx.getLinkeywf().getMainTableName() + " where WF_OrUnid='" + docUnid + "'";
        int i = Rdb.execSql(sql);
        return i;
    }

    /**
     * 存数据快照
     * 
     * @return
     */
    protected int saveDataSnapshot() {
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        Document doc = linkeywf.getDocument();
        String xmlData = doc.toXmlStr(true);
        String nodeid = linkeywf.getCurrentNodeid();
        String nodeName = "";
        if (Tools.isNotBlank(nodeid)) {
            nodeName = linkeywf.getCurrentModNodeDoc().g("NodeName");
        }
        else {
            return -1;
        }
        int i = 0;

        // 2014.12.13修改增加了DS_FormNumber,DS_FieldEditLOG两个字段
        String fieldEditLOG = ""; // 需要比较存盘前与存盘后的字段内容差，需要每一个字段都进行比较
        String sql = "insert into BPM_DataSnapshot(WF_OrUnid,DS_DocUnid,DS_Subject,DS_Processid,DS_Nodeid,DS_NodeName,DS_userid,DS_Created,DS_ActionNum,DS_FormNumber,DS_FieldEditLOG,XmlData)values(?,?,?,?,?,?,?,?,?,?,?,?)";
        if (Rdb.getDbType().equals("ORACLE")) {
            String newDocUnid = Rdb.getNewUnid();
            i = Rdb.execSql(sql, newDocUnid, linkeywf.getDocUnid(), doc.g("Subject"), doc.g("WF_Processid"), nodeid, nodeName, BeanCtx.getUserid(), DateUtil.getNow(), linkeywf.getActionNum(),
                    linkeywf.getFormNumber(), fieldEditLOG, "empty_clob()");
            Rdb.saveClobField(newDocUnid, "BPM_DataSnapshot", "XMLDATA", xmlData);
        }
        else {
            i = Rdb.execSql(sql, Rdb.getNewUnid(), linkeywf.getDocUnid(), doc.g("Subject"), doc.g("WF_Processid"), nodeid, nodeName, BeanCtx.getUserid(), DateUtil.getNow(), linkeywf.getActionNum(),
                    linkeywf.getFormNumber(), fieldEditLOG, xmlData);
        }
        return i;

    }

    /**
     * 存盘子表单提交的数据，用来子表单显示时使用
     * 
     * @return 返回1表示成功0或负数表示失败
     */
    protected int saveSubFormData(String formNumber) {
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        String nodeid = linkeywf.getCurrentModNodeDoc().g("Nodeid");
        Document doc = null;
        if (linkeywf.getCurrentModNodeDoc().g("RemoveDuplicationSubForm").equals("1")) {
            // 去掉重复的子表单
//            String sql = "update BPM_SubFormData set ReadOnly='0' where DocUnid='" + linkeywf.getDocUnid() + "' and WF_Processid='" + linkeywf.getProcessid() + "' and Nodeid='" + nodeid + "'";
//            Rdb.execSql(sql);
        	//20180917 覆盖子表单
        	String sql2 = "select * from BPM_SubFormData where DocUnid='" + linkeywf.getDocUnid() + "' and WF_Processid='" + linkeywf.getProcessid() + "' and Nodeid='" + nodeid + "'";
        	doc = Rdb.getDocumentBySql(sql2);
        }else{
        	doc = BeanCtx.getDocumentBean("BPM_SubFormData");
        	doc.s("WF_DocCreated", DateUtil.getNow("yyyy-MM-dd HH:mm:ss"));
        }
        doc.appendFromRequest(BeanCtx.getRequest(), true);
        doc.s("Subject", linkeywf.getDocument().g("Subject"));
        doc.s("NodeName", linkeywf.getCurrentModNodeDoc().g("NodeName"));
        doc.s("Nodeid", nodeid);
        doc.s("FormNumber", formNumber);
        String formTitle = linkeywf.getCurrentModNodeDoc().g("SubFormCollapsedTitle");
        if (Tools.isBlank(formTitle)) {
            formTitle = ((ModForm) BeanCtx.getBean("ModForm")).getFormDoc(formNumber).g("FormName");
        }
        doc.s("FormTitle", formTitle);
        doc.s("WF_Processid", linkeywf.getProcessid());
        doc.s("UserName", BeanCtx.getUserName());
        doc.s("Userid", BeanCtx.getUserid());
        doc.s("DocUnid", linkeywf.getDocUnid());
        doc.s("WF_Reamrk", linkeywf.getDocument().g("WF_Remark"));
       //20180910 添加子表单HTML
        String sql2 = "select customSubFormBody from bpm_modtasklist where Processid = '" + linkeywf.getProcessid() + "' and Nodeid ='" + nodeid + "'";
        if("1".equals(Rdb.getValueBySql(sql2))){
        	doc.s("FormBody", BeanCtx.g("WF_subFormBody")); 
        }
        
        int i = doc.save();
        if (i < 1) {
            BeanCtx.log("E", "错误:saveSubFormData()保存子表单数据时失败!");
        }
        return i;
    }
}
