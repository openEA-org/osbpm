package cn.linkey.wf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.org.LinkeyUser;
import cn.linkey.rule.ScriptEngine;
import cn.linkey.util.Tools;

/**
 * 流程节点模型操作类 本类可以应该实现模型配置文档的缓存功能
 * 
 * @author Administrator 本类为单实例类
 */
public class ModNode {

    /**
     * 根据流程id和节点id获得节点文档对像,文档对像中不包含扩展属性,如果需要扩展属性可使用getExtAttribute()方法获得
     * 
     * @param processid 流程id
     * @param nodeid 节点id
     * @return 节点文档对像
     */
    public Document getNodeDoc(String processid, String nodeid) {
        String tableName = getNodeTableName(processid, nodeid);
        String sql = "select * from " + tableName + " where processid='" + processid + "' and nodeid='" + nodeid + "'";
        Document nodeDoc = Rdb.getDocumentBySql(tableName, sql);
        if (nodeDoc.isNull()) {
            BeanCtx.log("E", "ModNode.getNodeDoc() Error:Can't find the node document by sql(" + sql + ")");
        }
        return nodeDoc;
    }

    /**
     * 根据节点id获得节点所在数据库表名
     * 
     * @param nodeid
     * @return 数据库表名
     */
    public String getNodeTableName(String processid, String nodeid) {
        String nodeType = this.getNodeType(processid, nodeid);
        if (Tools.isBlank(nodeType)) {
            return "";
        }
        return "BPM_Mod" + nodeType + "List";
    }

    /**
     * 根据节点id获得节点的基本类型名称
     * 
     * @param nodeid
     * @return 数据库表名
     */
    public String getNodeType(String processid, String nodeid) {
        // 根据节点首字母得到，速度最快
        if (Tools.isBlank(nodeid)) {
            return "";
        }
        String nodeType = nodeid.substring(0, 1);
        if (nodeType.equals("T")) {
            nodeType = "Task"; // 任务
        }
        else if (nodeType.equals("R")) {
            nodeType = "SequenceFlow"; // 路由
        }
        else if (nodeType.equals("G")) {
            nodeType = "Gateway"; // 网关
        }
        else if (nodeType.equals("P")) {
            nodeType = "Process"; // 流程
        }
        else if (nodeType.equals("E")) {
            nodeType = "Event"; // 事件
        }
        else if (nodeType.equals("S")) {
            nodeType = "SubProcess"; // 子流程
        }
        else {
            return "";
        }
        return nodeType;

        // 从视图中去得到，速度慢一点
        /*
         * String sql="select NodeType from BPM_AllModNodeList where Processid='"+processid+"' and Nodeid='"+nodeid+"'"; String nodeType=Rdb.getValueTopOneBySql(sql); return nodeType;
         */
    }

    /**
     * 获得节点的扩展类型
     * 
     * @param processid 流程id
     * @param nodeid 节点id
     * @return 返回扩展类型的名称
     */
    public String getExtNodeType(String processid, String nodeid) {
        String sql = "select ExtNodeType from BPM_AllModNodeList where Processid='" + processid + "' and Nodeid='" + nodeid + "'";
        return Rdb.getValueBySql(sql);
    }

    /**
     * 获得任意两个节点之间所处中间节点的网关\路由线\事件 本方法不会计算路由线中配置的条件表达式，不管是否成立都会返回
     * 
     * @param processid 流程id
     * @param sourceNode 起始节点
     * @param targetNode 目的节点
     * @param prvNodeList 已知的链接点,一般传null即可
     * @return 返回Nodeid的集合
     */
    public LinkedHashSet<String> getBetweenNodeid(String processid, String sourceNodeid, String targetNodeid, ArrayList<String> prvNodeList) {
        // 首先找到此节点出去的所有线段,前提条件所有Task类型的节点编号为T开头
        LinkedHashSet<String> nodeList = new LinkedHashSet<String>();
        String sql = "select Nodeid,TargetNode from BPM_ModSequenceFlowList where Processid='" + processid + "' and SourceNode='" + sourceNodeid + "'";
        HashMap<String, String> sequenceFlow = Rdb.getMapDataBySql(sql);
        for (String sequenceNodeid : sequenceFlow.keySet()) {
            String nextNodeid = sequenceFlow.get(sequenceNodeid);
            String curNodeType = getNodeType(processid, nextNodeid);
            if (nextNodeid.equals(targetNodeid)) {
                // 说明已经找到目的节点
                if (prvNodeList != null) {
                    nodeList.addAll(prvNodeList);
                }
                nodeList.add(sequenceNodeid);
            }
            else if (curNodeType.equals("Task") || curNodeType.equals("SubProcess")) {
                // 如果目的节点已经是Task or SubProcess or endEvent类型则停止查找并返回空值

            }
            else if (curNodeType.equals("Event")) { // 如果是事件则要看是否是结束事件,如果是结束事件也要停止查找
                if (!getExtNodeType(processid, nextNodeid).equals("endEvent")) {
                    // 如果不是结束事件也要往后再查找目标节点
                    if (prvNodeList == null) {
                        prvNodeList = new ArrayList<String>();
                    }
                    prvNodeList.add(sequenceNodeid);
                    prvNodeList.add(nextNodeid);
                    nodeList.addAll(getBetweenNodeid(processid, nextNodeid, targetNodeid, prvNodeList));
                }
            }
            else {
                // 如果以上条件都不是则再往后查找,Task是路由线、网关、事件等则还要再往后查
                if (prvNodeList == null) {
                    prvNodeList = new ArrayList<String>();
                }
                prvNodeList.add(sequenceNodeid);
                prvNodeList.add(nextNodeid);
                nodeList.addAll(getBetweenNodeid(processid, nextNodeid, targetNodeid, prvNodeList));
            }
        }
        return nodeList;
    }

    /**
     * 获得任意两个节点之间的所有路由线的Nodeid,即找到两个节点之间的路径
     * 
     * @param processid 流程id
     * @param sourceNode 起始节点
     * @param targetNode 目的节点
     * @param allPath 表示是否返回全路径径，ture表示返回全部路由线段的id，false表示只返回源节点的后面的路由线段id
     * @return 返回路由线的id的集合
     */
    public HashSet<String> getNodePath(String processid, String sourceNodeid, String targetNodeid, boolean allPath) {
        // 首先找到此节点出去的所有线段
        HashSet<String> nodeList = new HashSet<String>();
        HashMap<String, String> findNode = new HashMap<String, String>();
        String sql = "select Nodeid,TargetNode from BPM_ModSequenceFlowList where Processid='" + processid + "' and SourceNode='" + sourceNodeid + "'";
        HashMap<String, String> sequenceFlow = Rdb.getMapDataBySql(sql);
        for (String sequenceFlowNodeid : sequenceFlow.keySet()) {
            String nextNodeid = sequenceFlow.get(sequenceFlowNodeid);
            if (nextNodeid.equals(targetNodeid)) {
                nodeList.add(sequenceFlowNodeid);// 第一个节点就相等，说明就找到了
            }
            else if (!getNodeType(processid, nextNodeid).equals("Task")) {
                // 如果目标节点不是Task类型则往后循环找
                findNode.put("find", "0");
                HashSet<String> set = getNodePathBySequenceFlowid(processid, nextNodeid, targetNodeid, findNode);
                BeanCtx.out("查找n=" + nextNodeid + "->" + targetNodeid + " 结果=" + findNode.get("find"));
                if (findNode.get("find").equals("1")) {
                    nodeList.add(sequenceFlowNodeid);
                    if (allPath)
                        nodeList.addAll(set);
                }
            }
        }
        return nodeList;
    }

    /**
     * 与getNodePath函数一起，完成查找两个节点之间路径的功能
     * 
     * @param processid 流程id
     * @param sourceNode 起始节点
     * @param targetNode 目的节点
     * @return 返回路由线的id的集合
     */
    public HashSet<String> getNodePathBySequenceFlowid(String processid, String sourceNodeid, String targetNodeid, HashMap<String, String> findNode) {
        // 首先找到此节点出去的所有线段
        HashSet<String> nodeList = new HashSet<String>();
        String sql = "select Nodeid,TargetNode from BPM_ModSequenceFlowList where Processid='" + processid + "' and SourceNode='" + sourceNodeid + "'";
        HashMap<String, String> sequenceFlow = Rdb.getMapDataBySql(sql);
        for (String sequenceNodeid : sequenceFlow.keySet()) {
            String nextNodeid = sequenceFlow.get(sequenceNodeid);
            nodeList.add(sequenceNodeid);
            if (nextNodeid.equals(targetNodeid)) {
                // 说明已经找到目的节点
                BeanCtx.out("找到sequenceNodeid=" + sequenceNodeid + " targetNodeid=" + targetNodeid);
                findNode.put("find", "1");
            }
            else if (getNodeType(processid, nextNodeid).equals("Task") || getExtNodeType(processid, nextNodeid).equals("endEvent")) {
                // 如果目的节点已经是Task类型则停止查找并返回空值
                BeanCtx.out("找到Task=" + nextNodeid + " 不符合要求,终止查找!");
            }
            else {
                // 如果不是Task是路由线、网关、事件等则还要再往后查
                BeanCtx.out("往后查找nextNodeid=" + nextNodeid + " targetNodeid=" + targetNodeid);
                nodeList.addAll(getNodePathBySequenceFlowid(processid, nextNodeid, targetNodeid, findNode));
            }
        }
        return nodeList;
    }

    /**
     * 获得流程的启动节点id链接的第一个task
     * 
     * @param processid 流程id
     * @return 返回开始事件链接的第一个Task节点的id号
     */
    public String getStartNodeid(String processid) throws Exception {
        String sql = "select Nodeid from BPM_ModEventList where Processid='" + processid + "' and ExtNodeType='startEvent'";
        String nodeid = Rdb.getValueBySql(sql);
        return getTargetNodeid(processid, nodeid, false);
    }

    /**
     * 获得开始事件节点的id
     * 
     * @param processid
     * @return 返回startEvent的Nodeid
     */
    public String getStartEventNodeid(String processid) {
        String sql = "select Nodeid from BPM_ModEventList where Processid='" + processid + "' and ExtNodeType='startEvent'";
        return Rdb.getValueBySql(sql);
    }

    /**
     * 根据路由线的id直接返回此路由线所链接的目的节点的文档对像
     * 
     * @param processid 流程id
     * @param sequenceFlowid 路由线id
     * @return 返回目的节点的文档对像
     */
    public Document getTargetNodeDocBySequenceFlowid(String processid, String sequenceFlowid) {
        String sql = "select TargetNode from BPM_ModSequenceFlowList where Processid='" + processid + "' and Nodeid='" + sequenceFlowid + "'";
        String targetNodeid = Rdb.getValueBySql(sql);
        return this.getNodeDoc(processid, targetNodeid);
    }

    /**
     * 获得指定流程的节点的SequenceFlow路由线所链接的目的节点的id
     * 
     * @param processid 流程id
     * @param nodeid 节点id
     * @param isCondition true表示条件表达式不符合的不返回，false表示返回全部
     * @return
     */
    public String getTargetNodeid(String processid, String sourceNodeid, boolean isCondition) throws Exception {
        if (isCondition) {
            // 需要检测条件表达式
            HashSet<String> routerList = new HashSet<String>();
            String sql = "select TargetNode,ConditionExpression from BPM_ModSequenceFlowList where Processid='" + processid + "' and SourceNode='" + sourceNodeid + "'";
            HashSet<Document> dc = Rdb.getAllDocumentsSetBySql("BPM_ModSequenceFlowList", sql);
            for (Document nodeDoc : dc) {
                if (this.checkSequenceFlow(nodeDoc)) {
                    routerList.add(nodeDoc.g("Nodeid"));
                } // 只返回符合条件的
            }
            return Tools.join(routerList, ",");
        }
        else {
            // 无需检测条件表达式
            String sql = "select TargetNode from BPM_ModSequenceFlowList where Processid='" + processid + "' and SourceNode='" + sourceNodeid + "'";
            return Rdb.getValueBySql(sql);
        }
    }

    /**
     * 直接获得本节点后面的节点id(大部分节点后面的节点都是路由线,如果本身是路由线则直接返回路由线所指向的目标节点)
     * 
     * @param processid 流程id
     * @param sourceNodeid 节点id
     * @param isCondition true表示条件表达式不符合的不返回，false表示返回全部
     * @return
     */
    public HashSet<String> getNextNodeid(String processid, String sourceNodeid, boolean isCondition) throws Exception {
        HashSet<String> routerList = new HashSet<String>();
        if (this.getNodeType(processid, sourceNodeid).equals("SequenceFlow")) {
            // 说明本身就是路由线段
            String sql = "select TargetNode from BPM_ModSequenceFlowList where Processid='" + processid + "' and Nodeid='" + sourceNodeid + "'";
            routerList.add(Rdb.getValueBySql(sql));
        }
        else {
            // 说明不是路由线段
            String sql = "select Nodeid,ConditionExpression from BPM_ModSequenceFlowList where Processid='" + processid + "' and SourceNode='" + sourceNodeid + "'";
            HashSet<Document> dc = Rdb.getAllDocumentsSetBySql("BPM_ModSequenceFlowList", sql);
            for (Document nodeDoc : dc) {
                if (isCondition) {
                    if (this.checkSequenceFlow(nodeDoc)) {
                        routerList.add(nodeDoc.g("Nodeid"));
                    } // 只返回符合条件的
                }
                else {
                    routerList.add(nodeDoc.g("Nodeid")); // 全部返回
                }
            }
        }
        return routerList;
    }

    /**
     * 获得指定节点的前置节点列表
     * 
     * @param processid 流程id
     * @param nodeid 节点id
     * @return 返回所有前置节点列表set
     */
    public HashSet<String> getPreviousNodeid(String processid, String nodeid) {
        String sql = "select SourceNode from BPM_ModSequenceFlowList where Processid='" + processid + "' and TargetNode='" + nodeid + "'";
        return Rdb.getValueSetBySql(sql);
    }

    /**
     * 获得指定环节的后置事件节点的id号
     * 
     * @param processid 流程id
     * @param sourceNodeid 节点id
     * @return 返回后置事件的nodeid如果没有后置事件则返回空值
     */
    public String getRearEventNodeid(String processid, String sourceNodeid) {
        String sql = "select TargetNode from BPM_ModSequenceFlowList where Processid='" + processid + "' and SourceNode='" + sourceNodeid + "'";
        // BeanCtx.out("getRearEventNodeid="+sql);
        HashSet<String> nodeidList = Rdb.getValueSetBySql(sql, false);
        for (String nodeid : nodeidList) {
            Document doc = getNodeDoc(processid, nodeid);
            // BeanCtx.out("ExtNodeType="+nodeid+"=="+doc.g("ExtNodeType"));
            if (doc.g("ExtNodeType").equals("rearEvent")) {
                return nodeid; // 找到后置事件节点id
            }
        }
        return "";
    }

    /**
     * 获得指定环节的前置事件节点的id号
     * 
     * @param processid 流程id
     * @param sourceNodeid 节点id
     * @return 返回后置事件的nodeid如果没有后置事件则返回空值
     */
    public String getFrontEventNodeid(String processid, String sourceNodeid) {
        String sql = "select SourceNode from BPM_ModSequenceFlowList where Processid='" + processid + "' and TargetNode='" + sourceNodeid + "'";
        HashSet<String> nodeidList = Rdb.getValueSetBySql(sql, false);
        for (String nodeid : nodeidList) {
            Document doc = getNodeDoc(processid, nodeid);
            if (doc.g("ExtNodeType").equals("frontEvent")) {
                return nodeid; // 找到前置事件节点id
            }
        }
        return "";
    }

    /**
     * 获得指定节点后面所链接的任务节点或者是结束事件或者是子流程节点，排除网关和路由线
     * 
     * @param processid 流程id
     * @param nodeid 环节id
     * @param isCondition true表示要计算路由线中的条件表达式，false表示不需要计算
     * @return 返回目的节点的id集合
     */
    public LinkedHashSet<String> getNextTaskNodeid(String processid, String sourceNodeid, boolean isCondition) throws Exception {
        // 首先找到此节点出去的所有线段,前提条件所有Task类型的节点编号为T开头
        LinkedHashSet<String> nodeList = new LinkedHashSet<String>();
        String sql = "select Nodeid,TargetNode from BPM_ModSequenceFlowList where Processid='" + processid + "' and SourceNode='" + sourceNodeid + "'";
        HashMap<String, String> sequenceFlow = Rdb.getMapDataBySql(sql);
        for (String sequenceFlowNodeid : sequenceFlow.keySet()) {

            if (isCondition) {
                if (!checkSequenceFlow(processid, sequenceFlowNodeid)) {
                    continue;
                }
            } // 条件不成立，跳过

            String targetNodeid = sequenceFlow.get(sequenceFlowNodeid);
            String nodeType = getNodeType(processid, targetNodeid);
            if (nodeType.equals("Task") || nodeType.equals("SubProcess") || getExtNodeType(processid, targetNodeid).equals("endEvent")) {
                // 如果目的节点已经是Task/endEvent类型则停止查找并返回节点id
                nodeList.add(targetNodeid);
            }
            else {
                // 如果不是Task是路由线、网关、事件等则还要再往后查
                nodeList.addAll(getNextTaskNodeid(processid, targetNodeid, isCondition));
            }

        }
        return nodeList;
    }

    /**
     * 获得任意两个节点之间的网关类型
     * 
     * @param processid 流程id
     * @param sourceNodeid 源节点id
     * @param targetNodeid 目标节点id
     * @param resultType 默认网关类型，传入1
     * @return 返回 1表示为始终网关，2表示唯一网关，3表示并行网关,4复杂条件网关
     */
    public String getGatewayType(String processid, String sourceNodeid, String targetNodeid, String resultType) {
        // 首先找到此节点出去的所有线段
        // BeanCtx.out("\n\n需要查找sourceNodeid="+sourceNodeid+"与targetNodeid="+targetNodeid+"之间的网关节点");
        String sql = "select Nodeid,TargetNode from BPM_ModSequenceFlowList where Processid='" + processid + "' and SourceNode='" + sourceNodeid + "'";
        HashMap<String, String> sequenceFlowMap = Rdb.getMapDataBySql(sql);
        // BeanCtx.out("找到sourceNodeid节点后面的所有路由线="+sequenceFlowMap.toString());
        int i = 0;
        for (String sequenceFlowNodeid : sequenceFlowMap.keySet()) {
            if (i > 5) {
                break;
            }
            String nextNodeid = sequenceFlowMap.get(sequenceFlowNodeid);
            // BeanCtx.out("开始查找:::路由线的目标节点为nextNodeid="+nextNodeid);
            // 再看找到的是不是网关节点，如果是则记录网关类型
            Document nextNodeDoc = getNodeDoc(processid, nextNodeid);
            String extNodeType = nextNodeDoc.g("ExtNodeType");
            String nodeType = nextNodeDoc.g("NodeType");
            if (nodeType.equals("Gateway")) { // 找到网关就以最后一个网关的类型为准（如果有多个网关接起来的情况）,如果网关后面的路由线又定义了则以线为准
                // BeanCtx.out("找到了网关节点:"+nextNodeid);
                if (extNodeType.equals("exclusiveGateway")) {
                    resultType = "2"; // 唯一网关
                }
                else if (extNodeType.equals("parallelGateway")) {
                    resultType = "3"; // 并行网关
                }
                else if (extNodeType.equals("complexGateway")) {
                    resultType = "4"; // 复杂条件网关
                }
                else {
                    resultType = "1"; // 始终网关
                }
            }

            // 再看是否找到了目标节点
            if (nextNodeid.equals(targetNodeid)) {
                // BeanCtx.out("路由线("+sequenceFlowNodeid+")的目标节点与targetNodeid相等:"+nextNodeid+"等于"+targetNodeid);
                return resultType;
            }
            else if (nodeType.equals("Task") || extNodeType.equals("SubProcess")) {
                // BeanCtx.out("路由线("+sequenceFlowNodeid+")的目标节点已经是Task节点类型:"+nextNodeid+"<>"+targetNodeid);
                continue;
            }
            else {
                // 不是目标节点再往后查
                // BeanCtx.out("路由线的目标节点与targetNodeid不相等,再往后查找:"+nextNodeid+"与目标节点之间的网关"+targetNodeid);
                resultType = getGatewayType(processid, nextNodeid, targetNodeid, resultType);
            }
            i++;
        }
        return resultType;
    }

    /**
     * 获得当前用户是否允许转他人处理标记
     * 
     * @return true表示可以，false表示禁止
     */
    public boolean getReassignmentFlag() {
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        if (linkeywf.getIsNewProcess()) {
            return false;
        } // 如果是新流程则禁止
        if (linkeywf.getCurrentModNodeDoc().g("NoReassFlag").equals("1")) {
            return false;
        } // 禁止转他人处理

        // 看当前用户是否已经是被转发的用户ReassignmentFlag为0表示未被转发过，如果禁止再次转交则返回false，否则还是可以再转交
        if (!linkeywf.getCurrentInsUserDoc().g("ReassignmentFlag").equals("0") && linkeywf.getCurrentModNodeDoc().g("NoMoreReassFlag").equals("1")) {
            // 已经是被转交一次的用户，禁止多次转交
            return false;
        }

        return true;
    }

    /**
     * 获得节点的名称
     * 
     * @param processid 流程id
     * @param nodeid 节点id
     * @param replace 是否使用节点前面的路由线的名称替换节点的名称true表示是,false表示否
     * @return 返回节点名称
     */
    public String getNodeName(String processid, String nodeid, boolean replace) {
        if (replace) {
            // 以节点前面的路由线的名称为准
            // String sql="select SourceNode from BPM_ModSequenceFlowList where Processid='"+processid+"' and TargetNode='"+sourceNodeid+"'";

        }
        else {
            String sql = "select NodeName from BPM_AllModNodeList where Processid='" + processid + "' and Nodeid='" + nodeid + "'";
            return Rdb.getValueBySql(sql);
        }
        return nodeid;
    }

    /**
     * 检测指定路由线的条件是否成立，如果运算后成立返回true,否则返回false
     * 
     * @param processid 流程id
     * @param nodeid 路由线的nodeid
     * @return 返回true,false
     */
    public boolean checkSequenceFlow(String processid, String nodeid) throws Exception {
        Document nodeDoc = getNodeDoc(processid, nodeid);
        return checkSequenceFlow(nodeDoc);
    }

    /**
     * 检测指定路由线的条件是否成立，如果运算后成立返回true,否则返回false
     * 
     * @param nodedoc路由线的文档对像
     * @return 返回true,false
     */
    public boolean checkSequenceFlow(Document nodeDoc) throws Exception {
        String conditionRuleNum = nodeDoc.g("ConditionRuleNum").trim(); // 绑定规则
        String conditionExpression = nodeDoc.g("ConditionExpression").trim(); // 条件表达式
        if (Tools.isBlank(conditionExpression) && Tools.isBlank(conditionRuleNum)) {
            return true;
        }
        // System.out.println("ConditionExpression="+ConditionExpression);

        if (Tools.isNotBlank(conditionRuleNum)) {
            // 绑定规则进行执行，并由规则返回0表示不成立，1表示成立
            String result = BeanCtx.getExecuteEngine().run(conditionRuleNum);
            if (result.equals("0")) {
                return false;
            }
            else {
                return true;
            }
        }
        else {
            // 直接运算条件
            return ((ScriptEngine) BeanCtx.getBean("ScriptEngine")).evalRouterCondition(conditionExpression);
        }
    }

    /**
     * 获得任务节点的参与者
     * 
     * @param processid 流程id
     * @param nodeid 任务节点id
     * @return 返回此环节的参与者的字符串
     */
    public String getNodePotentialOwner(String processid, String nodeid) throws Exception {
        Document modNodeDoc = this.getNodeDoc(processid, nodeid);
        String potentialOwner = modNodeDoc.g("PotentialOwner");
        if (Tools.isBlank(potentialOwner) && modNodeDoc.g("UsePostOwner").equals("1")) {
            potentialOwner = modNodeDoc.g("ApprovalFormOwner");// 如果没有找到活动参与者就使用选择范围为活动参与者，这样提高兼容性
        }
        return potentialOwner; // 分析节点用户
    }

    /**
     * 获得任务节点的参与者和节点的组合集合 key为节点id value为用逗号分隔的用户值
     * 
     * @param processid 流程id
     * @param nodeidList 任务节点id多个可以用逗号进行分隔
     * @return 返回节点和节点参与者的map集合
     */
    public HashMap<String, String> getTaskUser(String processid, HashSet<String> nodeidList) throws Exception {
        HashMap<String, String> nodeAndUserMap = new HashMap<String, String>();
        for (String nodeid : nodeidList) {
            HashSet<String> potentialOwner = new HashSet<String>();
            Document modNodeDoc = this.getNodeDoc(processid, nodeid);
            potentialOwner.addAll(Tools.splitAsSet(modNodeDoc.g("PotentialOwner"), ","));
            potentialOwner = ((LinkeyUser) BeanCtx.getBean("LinkeyUser")).parserNodeMembers(potentialOwner);
            nodeAndUserMap.put(nodeid, Tools.join(potentialOwner, ","));
        }
        return nodeAndUserMap;
    }

    /**
     * 判断当前环节是否是首环节
     * 
     * @param processid 流程id
     * @param nodeid 节点id
     * @return 返回true表示是，false表示不是
     */
    public boolean isFirstNode(String processid, String nodeid) throws Exception {
        if (getStartNodeid(processid).equals(nodeid)) {
            return true;
        }
        else {
            return false;
        }
    }
}
