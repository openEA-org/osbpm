package cn.linkey.wf;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.org.LinkeyUser;
import cn.linkey.util.Tools;

/**
 * 
 * 流程相关工具类
 *
 * @author: alibao
 *
 * Modification History:
 * Date         Author          Version            Description
 *---------------------------------------------------------*
 * 2018年9月3日     alibao           v1.0.0               修改原因
 */
public class ProcessUtil {

    /**
     * 本方法适用于独立运行的规则，不适用于在流程事件中进行调用
     * 
     * <pre>
     * 运行流程引擎Action动作方法
     * 代码示例:
     * String actionid="GoToAnyNode";
     * String processid="a8bb004c00173041060b5d502bada1435268";
     * String docUnid="9414483206e9504da1082e0035b2de626a70";
     * String targetNodeid="T10008";
     * String targetUserid="admin,zhubo";
     * String copyUserid="";
     * String remark="同意";
     * String msg=ProcessUtil.runEngineAction(actionid,processid,docUnid,targetNodeid,targetUserid,copyUserid,false,"0",remark);
     * BeanCtx.p(msg);
     * </pre>
     * 
     * @param actionid 必须参数:动作id详见平台设置-流程相关设置-流程引擎动作配置
     * @param processid 必须参数:流程id
     * @param docUnid 如果是新流程可以为空:流程实例id
     * @param targetNodeid 目标节点多个用逗号分隔,如果Action动作不需要可以传入空值
     * @param targetUserid 目标节点需要启动的用户多个用逗号分隔如Action动作不需要可以传入空值,格式支持:user01,user01 or user01$Nodeid,user02$Nodeid or user01#DP1002$Nodeid,user02#DP1001$Nodeid
     * @param copyUserid 要抄送的用户,可以传入空值
     * @param sendSmsFlag true表示发送手机短信，false表示否
     * @param isBackFlag 在执行回退动作时标识是否直接返回回退者1表示否,2表示需要，其他请传空值
     * @param remark 办理意见
     * @return 返回流程引擎运行成功后的消息
     * @throws Exception 运行出错
     */
    public static String runEngineAction(String actionid, String processid, String docUnid, String targetNodeid, String targetUserid, String copyUserid, boolean sendSmsFlag, String isBackFlag,
            String remark) throws Exception {

        //1.开启事务并初始化工作流引擎
        Rdb.setAutoCommit(false); //开启事务
        ProcessEngine linkeywf = new ProcessEngine();
        BeanCtx.setLinkeywf(linkeywf); //把工作流引擎对像设置为全局变量对像
        linkeywf.init(processid, docUnid, BeanCtx.getUserid(), ""); //初始化工作流引擎

        //2.准备节点参数
        String nextNodeList = targetNodeid;
        HashSet<String> nextNodeSet;//提交的后继节点列表
        if (Tools.isNotBlank(nextNodeList)) {
            nextNodeSet = Tools.splitAsSet(nextNodeList); //把节点变为set集合
        }
        else {
            nextNodeSet = new HashSet<String>(); //给一个空值
        }

        //3.准备用户参数
        NodeUser insNodeUser = (NodeUser) BeanCtx.getBean("NodeUser");
        HashMap<String, String> nextUserMap = new HashMap<String, String>(); //提交后继环节的用户列表
        HashMap<String, HashMap<String, String>> nextUserDeptMap = new HashMap<String, HashMap<String, String>>(); //key为节点id,提交后继环节的用户和部门列表,user01#Deptid,user02#Deptid

        //用户格式为8.0版本user01,user02或者user01$T10003,user02T10002
        //8.1版本增加了如下格式支持 user01$T10003#DP1002,user02T10002#DP1003 后面可以支持强制指定用户所处的部门id
        String nextUserList = targetUserid;
        //		BeanCtx.out("nextUserList="+nextUserList);
        if (Tools.isNotBlank(nextUserList)) {
            if (Tools.isNotBlank(nextNodeList)) { //有节点有用户
                for (String nodeid : nextNodeSet) {
                    nextUserMap.put(nodeid, insNodeUser.getNodeUser(nextUserList, nodeid));
                    nextUserDeptMap.put(nodeid, insNodeUser.getNodeUserAndDept(nextUserList, nodeid));//获得要提交的用户的部门id,如果有部门指定时有用，没有也可以为空
                }
            }
            else {
                nextUserMap.put("ALL", nextUserList); //没有节点只有用户设置为全部
                nextUserDeptMap.put("ALL", insNodeUser.getNodeUserAndDept(nextUserList, "*"));//获得要提交的用户的部门id,如果有部门指定时有用，没有也可以为空
            }
            linkeywf.getDocument().s("WF_NextNodeUser", insNodeUser.getNodeUser(nextUserList, "*")); //所有提交的用户
        }

        //4.准备传阅用户
        if (Tools.isNotBlank(copyUserid)) {
            nextUserMap.put("COPYUSER", copyUserid); //要抄送的用户
        }

        //准备启动流程的节点和用户参数
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("WF_NextNodeid", nextNodeSet); //要提交的节点类型为HashSet<String> 值为nodeid多个逗号分隔
        params.put("WF_NextUserList", nextUserMap); //要提交的用户类型为HashMap<String,String> key为nodeid,value为userid多个逗号分隔,不包含部门id信息
        params.put("WF_NextUserDept", nextUserDeptMap);//要提交的用户和部门的组合，用来强制指定用户以什么部门身份来审批文档,兼职时有用 key为节点nodeid,value为用户与部门的map对像

        //5.1设置发送手机短信标记
        if (sendSmsFlag) {
            params.put("WF_SendSms", "1");//发送手机短信标记
        }

        //5.2设置回退后返回标记，如果为2表示回退后需要直接返回给回退者
        if (Tools.isNotBlank(isBackFlag)) {
            params.put("WF_IsBackFlag", isBackFlag);
        }

        //5.4设置审批意见
        params.put("WF_Remark", remark);

        if (linkeywf.isDebug()) {
            BeanCtx.out("Debug:运行Actionid==" + actionid);
            BeanCtx.out("Debug:提交流程引擎的运行参数为==" + params.toString());
        }
        //************************************************************准备结束

        //6.提交工作流引擎运行
        String msg = linkeywf.run(actionid, params);
        if (linkeywf.isDebug()) {
            BeanCtx.out("Debug:" + msg);
        }

        //7.如果出错则数据需要回滚
        if (BeanCtx.isRollBack()) {
            //获得回滚后的提示信息
            if (Tools.isBlank(linkeywf.getRollbackMsg())) {
                msg = BeanCtx.getMsg("Engine", "Error_EngineRun");
            }
            else {
                msg = linkeywf.getRollbackMsg();
            }
            params.put("ErrorType", "RollBack");
            BeanCtx.getEventEngine().run(processid, "Process", "EngineRunError", params); //注册流程运行出错后的事件
        }

        return msg;

    }

    /**
     * 本方法可以在独立运行的规则或流程事件中调用
     * 
     * 启动一个新的流程,兼容旧的版本
     * 
     * @param doc 要启动流程的文档对像
     * @param processid 流程id
     * @param docUnid 目的文档的unid应与doc的WF_OrUnid字段一至
     * @param targetNodeid 要启动的节点
     * @param targetUser 要启动的用户
     * @param targetCopyUser 要抄送的用户
     * @param remark 办理意见
     * @return 返回成功后的提示消息
     * @throws Exception 启动出错
     */
    public static String startNewProcess(Document doc, String processid, String docUnid, String targetNodeid, String targetUser, String targetCopyUser, String remark) throws Exception {
        HashMap<String, HashMap<String, String>> userDeptMap = new HashMap<String, HashMap<String, String>>();
        return startNewProcess(doc, processid, docUnid, targetNodeid, targetUser, targetCopyUser, remark, userDeptMap);
    }

    /**
     * 本方法可以在独立运行的规则或流程事件中调用
     * 
     * 启动一个新的流程,支持指定用户以什么部门的身份进行审批功能
     * 
     * @param doc 要启动流程的文档对像
     * @param processid 流程id
     * @param docUnid 目的文档的unid应与doc的WF_OrUnid字段一至
     * @param targetNodeid 要启动的节点
     * @param targetUser 要启动的用户,如果使用环节中配置的用户则传空值即可
     * @param targetCopyUser 要抄送的用户
     * @param remark 办理意见
     * @param userDeptMap 可以用来指定用户以什么部门的身份进行审批，兼职时有用,如果无需指定则传null值即可
     * @return 返回成功后的提示消息
     * @throws Exception 启动出错
     */
    public static String startNewProcess(Document doc, String processid, String docUnid, String targetNodeid, String targetUser, String targetCopyUser, String remark,
            HashMap<String, HashMap<String, String>> userDeptMap) throws Exception {
        ProcessEngine linkeywf = BeanCtx.getLinkeywf(); //先得到已有的linkeywf引擎对像

        //1.创建一个新的流程引擎
        ProcessEngine processEngine = new ProcessEngine();
        BeanCtx.setLinkeywf(processEngine); //切换全局引擎变量到当前实例
        processEngine.init(processid, docUnid, BeanCtx.getUserid(), "");
        processEngine.setDocument(doc);
        HashMap<String, Object> params = new HashMap<String, Object>();
        HashSet<String> nextNodeSet = Tools.splitAsSet(targetNodeid);
        params.put("WF_NextNodeid", nextNodeSet);

        //2.准备用户参数
        NodeUser insNodeUser = (NodeUser) BeanCtx.getBean("NodeUser");
        HashMap<String, String> nextUserMap = new HashMap<String, String>();
        if (Tools.isNotBlank(targetUser)) {
            if (Tools.isNotBlank(targetNodeid)) { //有节点有用户
                for (String nodeid : nextNodeSet) {
                    nextUserMap.put(nodeid, insNodeUser.getNodeUser(targetUser, nodeid));
                }
            }
            else {
                nextUserMap.put("ALL", targetUser); //没有节点只有用户设置为全部
            }
            params.put("WF_UsePostOwner", "1"); //使用Post中的用户参与者作为用户
        }
        else {
            params.put("WF_UsePostOwner", "0"); //使用环节中配置参与者作为用户
        }

        //3.准备传阅用户参数
        if (Tools.isNotBlank(targetCopyUser)) {
            nextUserMap.put("COPYUSER", targetCopyUser); //要抄送的用户
        }

        params.put("WF_NextNodeid", nextNodeSet); //要提交的节点类型为HashSet<String>
        params.put("WF_NextUserList", nextUserMap); //要提交的用户类型为HashMap<String,String>
        params.put("WF_NextUserDept", userDeptMap);//要提交的用户和部门的组合，用来强制指定用户以什么部门身份来审批文档,兼职时有用 key为节点nodeid,value为用户与部门的map对像
        params.put("WF_Remark", remark);//审批意见

        String msg = processEngine.run("GoToAnyNode", params);

        processEngine = null; //清空新的流程引擎对像

        if (linkeywf != null) {
            BeanCtx.setLinkeywf(linkeywf); //切换回原来的引擎变量
        }

        return msg;
    }

    /**
     * 本方法适用于独立运行的规则,不能在流程事件中进行调用
     * 
     * 删除一个流程实例文档的全部数据, 删除正在流转中的流程实例数据， 如果已经归档则不能用此方法删除
     * 
     * @param docUnid 流程实例的id
     * 
     * @return 返回是否remove成功
     */
    public static boolean removeProcessDocument(String docUnid) {
        //1.先删除所有流程实例数据
        MainDoc maindoc = (MainDoc) BeanCtx.getBean("MainDoc");
        maindoc.deleteAllInsData(docUnid);

        //2.删除主数据到回收站中去
        String sql = "select * from bpm_maindata where WF_OrUnid='" + docUnid + "'";
        Document doc = Rdb.getDocumentBySql(sql);
        int i = doc.remove(true);

        if (i > 0) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 本方法适用于独立运行的规则, 不能在流程事件中进行调用
     * 
     * 删除一个流程实例，是否归档都可以删除，删除后不能恢复
     * 
     * @param docUnid 流程实例的id
     */
    public static void deleteMainDocument(String docUnid) {
        //删除所有可能的实例数据
        String sql = "";

        //首先尝试删除扩展表中的业务数据表
        sql = "select WF_Processid from BPM_MainData where WF_OrUnid='" + docUnid + "'";
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

        sql = "Delete From BPM_InsRemarkList Where DocUNID='" + docUnid + "'"; //流转记录实例
        Rdb.execSql(sql);
        sql = "Delete From BPM_ArchivedRemarkList Where DocUNID='" + docUnid + "'"; //归档的流转记录
        Rdb.execSql(sql);
        sql = "Delete From BPM_ArchivedGraphicList Where DocUNID='" + docUnid + "'"; //归档后的流程图数据
        Rdb.execSql(sql);
        sql = "Delete From BPM_InsUserList Where DocUNID='" + docUnid + "'"; //节点实例
        Rdb.execSql(sql);
        sql = "Delete From BPM_InsNodeList Where DocUNID='" + docUnid + "'"; //节点实例
        Rdb.execSql(sql);
        sql = "Delete From BPM_InsCopyUserList Where DocUNID='" + docUnid + "'"; //待阅数据
        Rdb.execSql(sql);
        sql = "Delete From BPM_InsStayData Where DocUNID='" + docUnid + "'"; //流程驻留数据
        Rdb.execSql(sql);
        sql = "Delete From BPM_LockDocList Where DocUNID='" + docUnid + "'"; //锁定表
        Rdb.execSql(sql);
        sql = "Delete From BPM_TempRemarkList Where DocUNID='" + docUnid + "'"; //临时意见
        Rdb.execSql(sql);
        sql = "Delete from BPM_ToDoBox Where DocUNID='" + docUnid + "'"; //待办数据
        Rdb.execSql(sql);
        sql = "delete from BPM_DataSnapshot  Where DS_DocUnid='" + docUnid + "'"; //数据快照
        Rdb.execSql(sql);
        sql = "delete from  BPM_SubFormData where DocUnid='" + docUnid + "'"; //子表单数据
        Rdb.execSql(sql);
        sql = "delete from  BPM_MailBox where DocUnid='" + docUnid + "'"; //邮件队例
        Rdb.execSql(sql);
        sql = "delete from  BPM_SmsBox where DocUnid='" + docUnid + "'"; //短信队例
        Rdb.execSql(sql);
        sql = "delete from  BPM_ReportNodeList where DocUnid='" + docUnid + "'"; //归档的报表数据
        Rdb.execSql(sql);
        sql = "delete from  BPM_ReportUserList where DocUnid='" + docUnid + "'"; //归档的报表数据
        Rdb.execSql(sql);
        sql = "delete from  BPM_ArchivedData where WF_OrUnid='" + docUnid + "'"; //尝试从归档表中删除主数据
        Rdb.execSql(sql);
        sql = "delete from  BPM_MainData where WF_OrUnid='" + docUnid + "'"; //尝试从流转中的实例表中删除主数据
        Rdb.execSql(sql);

    }

    /**
     * 本方法适用流程事件,如果要在独立运行的规则中调用必须先调用ProcessEngine.init()方法进行初始化
     * 
     * 检测流程节点是否还有活动的节点,默认为当前流程的DocUnid
     * 
     * @param nodeid 节点id多个节点用逗号分隔,传入*号表示等所有任务Task节点结束才能启动
     * @return true表示还有活动环节，false表示没有了
     */
    public static boolean isCurrentNode(String nodeid) {
        return isCurrentNode(BeanCtx.getLinkeywf().getDocUnid(), nodeid);
    }

    /**
     * 本方法适用流程事件或独立运行的规则
     * 
     * 检测流程节点是否还有活动的节点
     * 
     * @param nodeid 节点id多个节点用逗号分隔传,入*号表示等所有任务节点结束才能启动
     * @param docUnid 要判断的文档id
     * @return true 表示还有活动环节，false 表示没有了
     */
    public static boolean isCurrentNode(String docUnid, String nodeid) {
        String sql = "";
        if (nodeid.equals("*")) {
            //所有节点
            sql = "select Nodeid from BPM_InsNodeList where DocUnid='" + docUnid + "' and (NodeType='Task' or NodeType='SubProcess' or NodeType='OutProcess') and Status='Current'";
        }
        else {
            //指定节点
            String[] nodeList = Tools.split(nodeid);
            String sqlWhere = "";
            for (String nodeItem : nodeList) {
                if (Tools.isNotBlank(sqlWhere)) {
                    sqlWhere += " or ";
                }
                sqlWhere += "Nodeid='" + nodeItem + "'";
            }
            sql = "select Nodeid from BPM_InsNodeList where DocUnid='" + docUnid + "' and Status='Current' and (" + sqlWhere + ")";
        }

        return Rdb.hasRecord(sql);
    }

    /**
     * 本方法适用于独立运行的规则，但在调用本方法之前必须先对ProcessEngine实例进行初始化调用ProcessEngine.init()方法
     * 
     * 获得指定环节的后继所有符合条件的节点的文档配置集合
     * 
     * @param processid 流程id
     * @param nodeid 节点id
     * @param nextNodeDc 传入一个空的LinkedHashSet&gt;Document&lt;()对像，调用后再取里面的文档即可
     * @throws Exception 获取下一个节点文档对象出错
     */
    public static void getNextNodeDoc(String processid, String nodeid, LinkedHashSet<Document> nextNodeDc) throws Exception {
        ModNode insModNode = (ModNode) BeanCtx.getBean("ModNode");
        LinkeyUser linkeyUser = (LinkeyUser) BeanCtx.getBean("LinkeyUser");
        NodeUser insNodeUser = (NodeUser) BeanCtx.getBean("NodeUser");
        String docUnid = BeanCtx.getLinkeywf().getDocUnid();

        //获得此节点出去的所有路由线段的配置信息
        String sql = "select Nodeid,TargetNode from BPM_ModSequenceFlowList where Processid='" + processid + "' and SourceNode='" + nodeid + "'";
        HashMap<String, String> sequenceFlowMap = Rdb.getMapDataBySql(sql);
        for (String sequenceFlowNodeid : sequenceFlowMap.keySet()) {
            //循环所有路由线生成节点和人员选项
            Document routerNodeDoc = insModNode.getNodeDoc(processid, sequenceFlowNodeid); //路由线文档对像
            if (insModNode.checkSequenceFlow(routerNodeDoc)) {//如果路由条件成立
                String targetNodeid = sequenceFlowMap.get(sequenceFlowNodeid); //找到路由的目标节点
                Document nextNodeDoc = insModNode.getNodeDoc(processid, targetNodeid);//目标节点文档对像
                String nodeType = nextNodeDoc.g("NodeType");//基本属性
                String nextExtNodeType = nextNodeDoc.g("ExtNodeType"); //扩展属性
                if (nodeType.equalsIgnoreCase("Task") || nextExtNodeType.equalsIgnoreCase("endEvent") || nodeType.equalsIgnoreCase("SubProcess")) {

                    //生成节点选项 1表示为始终网关，2表示唯一网关，3表示并行网关,4复杂条件网关
                    String gatewayType = insModNode.getGatewayType(processid, BeanCtx.getLinkeywf().getCurrentNodeid(), targetNodeid, "0"); //获得当前节点与目标节点之间网关节点的类型，如果没有就以最后的路由线中的为准
                    if (gatewayType.equals("0")) {
                        gatewayType = routerNodeDoc.g("GatewayType");
                    } //说明两个节点之间没有网关节点以最后路由线中的类型为准
                    String nodeName = nextNodeDoc.g("NodeName"); //节点名称
                    String routerNodeName = routerNodeDoc.g("NodeName");//由路由替换的节点名称
                    if (Tools.isBlank(routerNodeName)) {
                        routerNodeName = nodeName;
                    }
                    String defaultSelected = routerNodeDoc.g("DefaultSelected"); //是否缺省选中

                    String inputType = gatewayType.equals("2") ? "radio" : "checkbox"; //只有唯一网关是radio模式
                    String disabledStr = (gatewayType.equals("1") || gatewayType.equals("4")) ? "true" : "false"; //始终和复杂条件网关时不可选
                    String checked = (disabledStr.equals("disabled") || defaultSelected.equals("1")) ? "true" : "false"; //是否默认选中

                    nextNodeDoc.s("GatewayType", inputType); //网关类型
                    nextNodeDoc.s("GatewayType_DefaultChecked", checked); //缺省选中
                    nextNodeDoc.s("GatewayType_Disabled", disabledStr); //是否不可选
                    nextNodeDoc.s("NodeName_Router", routerNodeName);

                    //生成人员选项
                    if (nextExtNodeType.equals("userTask") && nextNodeDoc.g("UsePostOwner").equals("1")) {
                        //如果目的节点是userTask类型且环节中选择是是接受提交的用户作为参与者时则生成节点选项和人员选项

                        //看路由线是否是回退线，如果是回退线则把流程实例用户作为选择用户
                        LinkedHashSet<String> nodeOwnerSet = new LinkedHashSet<String>();
                        if (routerNodeDoc.g("ReturnFlag").equals("1")) {
                            if (routerNodeDoc.g("BackToPrvUser").equals("1")) {
                                //直接回退给上一提交者
                                String sourceOrUnid = BeanCtx.getLinkeywf().getCurrentInsUserDoc().g("SourceOrUnid");
                                sql = "select Userid from BPM_InsUserList where WF_OrUnid='" + sourceOrUnid + "'";
                                String prvUserid = Rdb.getValueBySql(sql);
                                nodeOwnerSet.add(prvUserid);
                            }
                            else {
                                nodeOwnerSet = insNodeUser.getInsNodeUser(docUnid, targetNodeid); //获得已存在的实例用户作为参与者
                            }
                        }
                        else {
                            nodeOwnerSet = Tools.splitAsLinkedSet(nextNodeDoc.g("ApprovalFormOwner"), ","); //使用节点中配置的人员选择范围
                        }

                        //获得人员列表的HTML
                        LinkedHashSet<String> ownerSet = BeanCtx.getLinkeyUser().parserNodeMembers(nodeOwnerSet);
                        String userSelected = (ownerSet.size() == 1 || nextNodeDoc.g("OwnerSelectType").equals("2") || nextNodeDoc.g("OwnerSelectType").equals("3")) ? "true" : "false"; //是否默认选中

                        String userid = Tools.join(ownerSet, ",");
                        nextNodeDoc.s("SelectUseridList", userid);
                        nextNodeDoc.s("SelectUserNameList", linkeyUser.getCnNameAndDeptName(userid, false));
                        nextNodeDoc.s("SelectUserDefaultSelected", userSelected);
                    }
                    nextNodeDc.add(nextNodeDoc);

                }
                else {
                    //如果不是Task或事件则再往后查
                    getNextNodeDoc(processid, targetNodeid, nextNodeDc);
                }
            }
        }
    }

    /**
     * 获得当前审批用户的上一提交的节点的Document对像
     * 
     * @param processid 流程id
     * @param docUnid 流程实例id
     * @param userid 用户id
     * @return 上一个节点文档对象
     * @throws Exception 获取上一个节点文档对象出错
     */
    public static Document getPrvNodeDoc(String processid, String docUnid, String userid) throws Exception {
        ProcessEngine linkeywf = BeanCtx.getDefaultEngine();
        linkeywf.init(processid, docUnid, userid, "");
        if (linkeywf.getCurrentInsUserDoc() != null) {
            String sql = "select * from BPM_InsUserList where WF_OrUnid='" + linkeywf.getCurrentInsUserDoc().g("SourceOrUnid") + "'";
            Document nodeDoc = Rdb.getDocumentBySql(sql);
            if (!nodeDoc.isNull()) {
                return nodeDoc;
            }
        }
        return null;
    }

    /**
     * 根据流程编号返回发布状态的流程id
     * 
     * @param processNumber 流程编号
     * @return 返回流程发布状态
     */
    public static String getProcessidByProcessNumber(String processNumber) {
        String sql = "select Processid from BPM_ModProcessList where ProcessNumber='" + processNumber + "' and Status='1'";
        return Rdb.getValueBySql(sql);
    }

    /**
     * 本方法适用于独立运行的规则，不能在流程事件中进行调用
     * 
     * 把文档标记为已阅,条件是：用户在文档的WF_CopyUser字段中
     * 
     * @param doc 为流程文档可以是正在流转中的也可以是已归档的可通过Rdb.getDocumentBySql();获取
     * @return 返回非负数表示标记成功
     * @throws  Exception  读取文档出错
     */
    public static int readDocument(Document doc) throws Exception {
        int i = 0;
        String processid = doc.g("WF_Processid");
        String docUnid = doc.g("WF_OrUnid");
        ProcessEngine linkeywf = BeanCtx.getDefaultEngine();
        linkeywf.init(processid, docUnid, BeanCtx.getUserid(), "");
        linkeywf.setDocument(doc);
        linkeywf.setReadOnly(true);
        String copyUserList = doc.g("WF_CopyUser");
        if (Tools.isNotBlank(copyUserList)) {
            copyUserList = "," + copyUserList + ",";
            if (copyUserList.indexOf("," + BeanCtx.getUserid() + ",") != -1) {
                //1.把当前用户标记为已阅
                InsUser insUser = (InsUser) BeanCtx.getBean("InsUser");
                Document insUserDoc = insUser.endCopyUser(BeanCtx.getUserid());

                //2.增加已阅记录
                Remark remark = (Remark) BeanCtx.getBean("Remark");
                remark.AddReadRemark(insUserDoc.g("Nodeid"), insUserDoc.g("NodeName"), insUserDoc.g("StartTime"));

                //更新主文档的WF_CopyUser字段
                copyUserList = Tools.join(((NodeUser) BeanCtx.getBean("NodeUser")).getCopyUser(docUnid), ",");
                String sql = "update " + doc.getTableName() + " set WF_CopyUser='" + copyUserList + "' where WF_OrUnid='" + docUnid + "'";
                i = Rdb.execSql(sql);
                if (doc.g("WF_Status").equals("ARC")) {
                    //已归档的文件则需要清除待阅记录
                    if (Tools.isNotBlank(copyUserList)) {
                        Rdb.execSql("delete from BPM_InsCopyUserList where Userid='" + BeanCtx.getUserid() + "' and WF_OrUnid='" + docUnid + "'");
                    }
                    else {
                        Rdb.execSql("delete from BPM_InsCopyUserList where WF_OrUnid='" + docUnid + "'"); //没有已阅的用户清除全部的记录
                    }
                }
            }
        }
        return i;
    }

    /**
     * 强制归档流程实例,本方法适合于独立运行的规则或流程事件中触发的规则
     * 
     * <pre>
     * 示例代码： String processid="fe3bb2d403e8d04f9a09c84092dd79b1b07b";
     * String docUnid="0af0357f0c2c30432a0a3fb01cdb3ac56157";
     * String msg=ProcessUtil.archivedProcess(processid,docUnid);
     * BeanCtx.p(msg);
     * </pre>
     * 
     * @param processid 流程id
     * @param docUnid 文档id
     * @return 返回提交后的成功消息
     * @throws Exception 运行出错
     */
    public static String archivedProcess(String processid, String docUnid) throws Exception {
        String sql = "select WF_OrUnid from BPM_MainData where WF_OrUnid='" + docUnid + "'";
        if (!Rdb.hasRecord(sql)) {
            return "docUnid实例不存在!";
        }

        //1.创建一个新的流程引擎
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        if (linkeywf != null && docUnid.equals(linkeywf.getDocUnid())) {
            //说明是同一个实例下面结束
            ((MainDoc) BeanCtx.getBean("MainDoc")).archiveDocument(); //运行归档逻辑程序
            String msg = BeanCtx.getMsg("Engine", "GoToArchived", "");
            return msg;
        }
        else {
            //不同的流程实例
            ProcessEngine processEngine = new ProcessEngine();
            BeanCtx.setLinkeywf(processEngine); //指定全局引擎变量到线程中
            processEngine.init(processid, docUnid, BeanCtx.getUserid(), "");
            ((MainDoc) BeanCtx.getBean("MainDoc")).archiveDocument(); //运行归档逻辑程序
            String msg = BeanCtx.getMsg("Engine", "GoToArchived", "");
            if (linkeywf != null) {
                processEngine.clear();
                BeanCtx.setLinkeywf(linkeywf); //切换回原来的引擎变量
            }
            return msg;
        }
    }

    /**
     * 本方法只适用于流程事件或独立运行的规则中
     * 
     * <pre>
     * 结束指定节点和用户的审批权限
     * 示例代码：
     * String processid="fe3bb2d403e8d04f9a09c84092dd79b1b07b";
     * String docUnid="f068053b0a4f204e4c0ba31050144ba185b4";
     * String nodeid="T10003";
     * String endUserList="admin";
     * String msg=ProcessUtil.endUser(processid,docUnid,nodeid,endUserList);
     * BeanCtx.p(msg);
     * </pre>
     * 
     * @param processid 流程id
     * @param docUnid 文档id
     * @param nodeid 节点id
     * @param endUserList 要结束的用户列表多个用逗号分隔
     * @return 返回提交后的成功消息
     * @throws Exception 结束指定节点和用户的审批权限出错
     */
    public static String endUser(String processid, String docUnid, String nodeid, String endUserList) throws Exception {
        String sql = "select WF_OrUnid from BPM_MainData where WF_OrUnid='" + docUnid + "'";
        if (!Rdb.hasRecord(sql)) {
            return "docUnid实例不存在!";
        }

        //1.创建一个新的流程引擎
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        if (linkeywf != null && docUnid.equals(linkeywf.getDocUnid())) {
            //说明是同一个实例下面结束
            InsUser insUser = (InsUser) BeanCtx.getBean("InsUser");
            insUser.endUser(processid, docUnid, nodeid, endUserList); //结束所有用户

            //4.获得提示语并返回
            LinkeyUser linkeyUser = (LinkeyUser) BeanCtx.getBean("LinkeyUser");
            String msg = BeanCtx.getMsg("Engine", "EndUser", linkeyUser.getCnName(endUserList));
            return msg;
        }
        else {
            //不同的流程实例
            ProcessEngine processEngine = new ProcessEngine();
            BeanCtx.setLinkeywf(processEngine); //指定全局引擎变量到线程中
            processEngine.init(processid, docUnid, BeanCtx.getUserid(), "");
            HashMap<String, Object> runParams = new HashMap<String, Object>();
            HashMap<String, String> userMap = new HashMap<String, String>();
            userMap.put("ALL", endUserList);
            runParams.put("WF_NextUserList", userMap); //指定要结束的用户
            runParams.put("WF_NextNodeid", nodeid); //指定要结束的节点
            String msg = processEngine.run("EndUser", runParams);
            if (linkeywf != null) {
                processEngine.clear();
                BeanCtx.setLinkeywf(linkeywf); //切换回原来的引擎变量
            }
            return msg;
        }
    }

    /**
     * 本方法只适用于流程事件和独立运行的规则
     * 
     * <pre>
     * 结束指定节点同时结束此节点上所有用户的审批权限
     * 示例代码:
     * String processid="fe3bb2d403e8d04f9a09c84092dd79b1b07b";
     * String docUnid="f068053b0a4f204e4c0ba31050144ba185b4";
     * String nodeid="T10003";
     * String msg=ProcessUtil.endNode(processid,docUnid,nodeid);
     * </pre>
     * 
     * @param processid 流程id
     * @param docUnid 文档id
     * @param nodeid 节点id
     * @return 返回提交后的成功消息
     * @throws Exception 结束出错
     */
    public static String endNode(String processid, String docUnid, String nodeid) throws Exception {
        String sql = "select WF_OrUnid from BPM_MainData where WF_OrUnid='" + docUnid + "'";
        if (!Rdb.hasRecord(sql)) {
            return "docUnid实例不存在!";
        }

        //1.创建一个新的流程引擎
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        if (linkeywf != null && docUnid.equals(linkeywf.getDocUnid())) {
            //说明是同一个实例下面结束
            InsNode insNode = (InsNode) BeanCtx.getBean("InsNode");
            insNode.endNode(processid, docUnid, nodeid);
            Document nodeDoc = insNode.getInsNodeDoc(processid, nodeid, docUnid);

            //获得提示语并返回
            String msg = BeanCtx.getMsg("Engine", "EndNode", nodeDoc.g("NodeName"));
            return msg;
        }
        else {
            //不同的流程实例,必须要启动一个新的流程引擎对像
            ProcessEngine processEngine = new ProcessEngine();
            BeanCtx.setLinkeywf(processEngine); //指定全局引擎变量到线程中
            processEngine.init(processid, docUnid, BeanCtx.getUserid(), "");
            HashMap<String, Object> runParams = new HashMap<String, Object>();
            runParams.put("WF_NextNodeid", nodeid); //指定要结束的节点
            String msg = processEngine.run("EndNode", runParams);
            if (linkeywf != null) {
                processEngine.clear();
                BeanCtx.setLinkeywf(linkeywf); //切换回原来的引擎变量
            }
            return msg;
        }

    }

    /**
     * 本方法只适用于流程规则事件或独立运行的规则中调用
     * 
     * <pre>
     * 后台强制启动指定节点和用户的审批任务,如果节点没有启动时系统会自动启动相应的节点,docUnid必须是已经启动的流程实例
     * 兼容8.0版本的方法
     * 示例代码：
     * String processid="fe3bb2d403e8d04f9a09c84092dd79b1b07b";
     * String docUnid="f068053b0a4f204e4c0ba31050144ba185b4";
     * String msg=ProcessUtil.startUser(processid,docUnid,"T10003","zhubo");
     * </pre>
     * 
     * @param processid 流程id
     * @param docUnid 文档id
     * @param nodeid 节点id
     * @param startUserList 要启动的用户 列表多个用逗号分隔
     * @return 返回提交后的成功消息
     * @throws Exception 运行出错
     */
    public static String startUser(String processid, String docUnid, String nodeid, String startUserList) throws Exception {
        return startUser(processid, docUnid, nodeid, startUserList, new HashMap<String, String>());
    }

    /**
     * 本方法只适用于流程规则事件或独立运行的规则中调用
     * 
     * <pre>
     * 后台强制启动指定节点和用户的审批任务,如果节点没有启动时系统会自动启动相应的节点,docUnid必须是已经启动的流程实例
     * 示例代码：
     * String processid="fe3bb2d403e8d04f9a09c84092dd79b1b07b";
     * String docUnid="f068053b0a4f204e4c0ba31050144ba185b4";
     * HashMap&gt;String,String&lt; userDept=new HashMap&gt;String,String&lt;();
     * userDept.put("zhubo","DP1002");
     * String msg=ProcessUtil.startUser(processid,docUnid,"T10003","zhubo",userDept);
     * </pre>
     * 
     * @param processid 流程id
     * @param docUnid 文档id
     * @param nodeid 节点id
     * @param startUserList 要启动的用户 列表多个用逗号分隔
     * @param userDept 强制让用户以某个部门的身份来处理此任务,如果没有可以传null空值表示用户以默认所在部门的身份进行处理
     * @return 返回提交后的成功消息
     * @throws Exception 运行出错
     */
    public static String startUser(String processid, String docUnid, String nodeid, String startUserList, HashMap<String, String> userDept) throws Exception {
        String sql = "select WF_OrUnid from BPM_MainData where WF_OrUnid='" + docUnid + "'";
        if (!Rdb.hasRecord(sql)) {
            return "docUnid实例不存在,如果要启动新流程请使用ProcessUtil.startNewProcess()方法!";
        }

        //1.创建一个新的流程引擎
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        if (linkeywf != null && docUnid.equals(linkeywf.getDocUnid())) {
            //说明是同一个实例下面启动
            InsUser insUser = (InsUser) BeanCtx.getBean("InsUser");
            insUser.startUser(processid, docUnid, nodeid, startUserList, userDept); //启动所有用户

            //4.获得提示语并返回
            LinkeyUser linkeyUser = (LinkeyUser) BeanCtx.getBean("LinkeyUser");
            String msg = BeanCtx.getMsg("Engine", "StartUser", linkeyUser.getCnName(startUserList));
            return msg;
        }
        else {
            //不同的流程实例
            ProcessEngine processEngine = new ProcessEngine();
            BeanCtx.setLinkeywf(processEngine); //指定全局引擎变量到线程中
            processEngine.init(processid, docUnid, BeanCtx.getUserid(), "");
            HashMap<String, Object> runParams = new HashMap<String, Object>();
            HashMap<String, String> userMap = new HashMap<String, String>();
            userMap.put("ALL", startUserList);
            runParams.put("WF_NextUserList", userMap); //指定要启动的用户
            runParams.put("WF_NextNodeid", nodeid); //指定要启动的节点
            runParams.put("WF_NextUserDept", userDept); //指定用户与部门的map关系
            String msg = processEngine.run("StartUser", runParams);
            if (linkeywf != null) {
                processEngine.clear();
                BeanCtx.setLinkeywf(linkeywf); //切换回原来的引擎变量
            }
            return msg;
        }
    }

    /**
     * 本方法适用于独立运行的规则或流程事件中
     * 
     * 获得用户当前所处环节中同意的用户数，不同意的用户数，未处理的用户数
     * 
     * @return 返回map对像，YES为同意数，NO为不同意数，ALL为还未处理数
     */
    public static HashMap<String, Integer> getAgreePer() {
        int y = 0, n = 0, x = 0;
        String actionNum = BeanCtx.getLinkeywf().getCurrentInsUserDoc().g("actionNum");
        String sql = "select Userid,IsAgree from BPM_InsUserList where DocUnid='" + BeanCtx.getLinkeywf().getDocUnid() + "' and Status='End' and ActionNum='" + actionNum + "'";
        HashMap<String, String> map = Rdb.getMapDataBySql(sql);
        for (String key : map.keySet()) {
            String v = map.get(key);
            if (v.equals("Y")) {
                y++;
            }
            else if (v.equals("N")) {
                n++;
            }
            x++;
        }
        HashMap<String, Integer> agreeMap = new HashMap<String, Integer>();
        agreeMap.put("YES", y);
        agreeMap.put("NO", n);
        agreeMap.put("ALL", x);

        return agreeMap;
    }

}
