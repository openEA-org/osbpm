package cn.linkey.rulelib.S026;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.wf.ModNode;
import cn.linkey.wf.NodeUser;
import cn.linkey.wf.ProcessEngine;

/**
 * @RuleName:流程模拟运行
 * @author admin
 * @version: 8.0
 * @Created: 2014-09-16 17:31
 */
final public class R_S026_B007 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String orSimProcessUnid = BeanCtx.g("SimProcessUnid"); //仿真流程配置参数的Unid，打开窗口中的id原始的
        String processid = BeanCtx.g("Processid"); //真正的流程模型的Unid
        String docUnid = BeanCtx.g("DocUnid");
        if (Tools.isBlank(docUnid)) {
            docUnid = Rdb.getNewUnid(); //说明是新的仿真开始，新给一个文档id
        }
        //	    BeanCtx.out("原SimProcessUnid="+orSimProcessUnid);

        //根据流程id去获得仿真策略的id，这样的问题就是一个流程只能建一种仿真策略，好处时可以支持子流程的仿真策略
        String sql = "select WF_OrUnid from BPM_SimProcessList where Processid='" + processid + "'";
        String simProcessUnid = Rdb.getValueBySql(sql);

        //	    BeanCtx.out("新SimProcessUnid="+simProcessUnid);
        //	    BeanCtx.out("processid="+processid);
        //	    BeanCtx.out("docUnid="+docUnid);

        Document simDataDoc;
        sql = "select * from BPM_SimProcessList where WF_OrUnid='" + simProcessUnid + "'";
        Document simProcessDoc = Rdb.getDocumentBySql(sql);
        if (simProcessDoc.isNull()) {
            BeanCtx.p(Tools.jmsg("Error", "错误:本流程的仿真策略不存在!"));
            return "";
        }
        else {
            String simFormUnid = simProcessDoc.g("SimFormUnid");
            simDataDoc = Rdb.getDocumentBySql("select XmlData from BPM_SimFormList where WF_OrUnid='" + simFormUnid + "'"); //获得仿真数据文档
        }

        if (Tools.isBlank(docUnid) || Tools.isBlank(processid)) {
            BeanCtx.print("{\"msg\":\"Error:DocUNID or Processid is null!\",\"Status\":\"Error\"}");
            return "";
        }

        try {
            ModNode modNode = (ModNode) BeanCtx.getBean("ModNode");

            //1.获得当前模拟用户
            sql = "select * from BPM_AllDocument where WF_OrUnid='" + docUnid + "'";
            Document mainDoc = Rdb.getDocumentBySql(sql);
            if (mainDoc.isNull()) {
                //说明是一个新的流程启动,看是否有针对起草环节设定的模拟用户
                String startNodeid = modNode.getStartNodeid(processid);
                sql = "select NodeOwner from BPM_SimNodeList where SimProcessUnid='" + simProcessUnid + "' and Nodeid='" + startNodeid + "'";
                String startUserid = Rdb.getValueBySql(sql);
                BeanCtx.setUserid(startUserid); //切换到启动者身份上去
            }
            else {
                //说明流程已经启动了,要运行到后继环节中去
                if (mainDoc.g("WF_Status").equals("Current")) {
                    //说明在运行中，需要从WF_Author字段中获得第一个用户作为当前审批人,并自动获得后继节点id和用户userid
                    if (Tools.isNotBlank(mainDoc.g("WF_Author"))) {
                        String[] authorArray = Tools.split(mainDoc.g("WF_Author"));
                        String author = authorArray[0]; //获得第一个审批人为当前处理人
                        BeanCtx.setUserid(author); //切换到当前审批人的身份上去
                    }
                    else {
                        //当前处理人为空有可能是产生了子流程
                        sql = "select WF_OrUnid,WF_Processid,WF_ProcessName from BPM_MainData where WF_MainDocUnid='" + docUnid + "'";
                        Document subDoc = Rdb.getDocumentBySql(sql);
                        if (subDoc.isNull()) {
                            BeanCtx.p(Tools.jmsg("Error", "错误:当前运行节点的处理人为空,仿真运行停止!"));
                        }
                        else {
                            //产生了子流程,返回子流程参数进行运行
                            String jsonStr = "{\"msg\":\"启动子流程:" + subDoc.g("WF_ProcessName") + "\",\"Status\":\"ok\",\"DocUnid\":\"" + subDoc.g("WF_OrUnid") + "\",\"Processid\":\""
                                    + subDoc.g("WF_Processid") + "\",\"CurrentNodeList\":\"\",\"EndNodeList\":\"\"}";
                            //							BeanCtx.out(jsonStr);
                            BeanCtx.print(jsonStr);
                        }
                        return "";
                    }
                }
                else {
                    //说明已结结束了如果是子流程要检测主流程是否已经结束了如果没有则要启动主流程
                    if (Tools.isNotBlank(mainDoc.g("WF_MainDocUnid"))) {
                        //说明当前是子流程
                        sql = "select WF_OrUnid,WF_Processid,WF_ProcessName from BPM_MainData where WF_OrUnid='" + mainDoc.g("WF_MainDocUnid") + "'";
                        Document parentDoc = Rdb.getDocumentBySql(sql);
                        NodeUser nodeUser = (NodeUser) BeanCtx.getBean("NodeUser");
                        String currentNodeid = nodeUser.getCurrentNodeid(parentDoc.getDocUnid());
                        String endNodeid = nodeUser.getEndNodeid(parentDoc.getDocUnid());
                        String jsonStr = "{\"msg\":\"返回主流程(" + parentDoc.g("WF_ProcessName") + ")\",\"Status\":\"ok\",\"DocUnid\":\"" + parentDoc.g("WF_OrUnid") + "\",\"Processid\":\""
                                + parentDoc.g("WF_Processid") + "\",\"CurrentNodeList\":\"" + currentNodeid + "\",\"EndNodeList\":\"" + endNodeid + "\"}";
                        BeanCtx.print(jsonStr);
                    }
                    else {
                        BeanCtx.p(Tools.jmsg("End", "流程仿真运行结束!"));
                    }
                    return "";
                }
            }

            //2.初始化工作流引擎
            ProcessEngine linkeywf = new ProcessEngine();
            BeanCtx.setLinkeywf(linkeywf); //把工作流引擎对像设置为全局变量对像
            linkeywf.init(processid, docUnid, BeanCtx.getUserid(), ""); //初始化工作流引擎

            //只有仿真unid和通过流程获得的相等才拷贝，在启动子流程和主流程时都不需要了
            if (orSimProcessUnid.equals(simProcessUnid) && linkeywf.getIsNewProcess()) {
                simDataDoc.copyAllItems(linkeywf.getDocument()); //把仿真数据文档中的字段拷贝到文档中来
            }

            //删除不必要的字段
            linkeywf.getDocument().removeItem("Processid");
            linkeywf.getDocument().removeItem("SimProcessUnid");
            linkeywf.getDocument().removeItem("DocUnid");
            linkeywf.getDocument().removeItem("FormNumber_show");
            linkeywf.getDocument().removeItem("_dc");
            linkeywf.getDocument().s("WF_BusinessNum", "1"); //表示是仿真的结果数据

            //3.准备节点参数
            HashSet<String> nextNodeSet = getSimNextNodeid(simProcessUnid, processid, linkeywf.getCurrentNodeid(), docUnid); //自动获得后继节点id
            if (nextNodeSet.size() == 0) {
                BeanCtx.p(Tools.jmsg("Error", "错误:未找到条件成立的后继节点,仿真运行停止!"));
                return "";
            }

            //3.准备用户参数
            HashMap<String, HashMap<String, String>> nextUserDeptMap = new HashMap<String, HashMap<String, String>>(); //key为节点id,提交后继环节的用户和部门列表,user01#Deptid,user02#Deptid
            HashMap<String, String> nextUserMap = getSimNextNodeUser(nextNodeSet, processid, simProcessUnid, nextUserDeptMap);//获得后继节点的用户
            BeanCtx.out("nextUserMap=" + nextUserMap);

            HashMap<String, Object> enginParams = new HashMap<String, Object>();
            enginParams.put("WF_NextNodeid", nextNodeSet); //要提交的节点类型为HashSet<String>
            enginParams.put("WF_NextUserList", nextUserMap); //要提交的用户类型为HashMap<String,String>
            enginParams.put("WF_NextUserDept", nextUserDeptMap);//要提交的人员和部门的组合map对像

            //5.设置审批意见
            enginParams.put("WF_Remark", "-仿真运行-");

            String actionid = linkeywf.getCurrentActionid(); //自动获取Actionid参数
            //			linkeywf.setDebug(true);
            if (linkeywf.isDebug()) {
                BeanCtx.out("Debug:运行Actionid==" + actionid);
                BeanCtx.out("Debug:提交流程引擎的运行参数为==" + enginParams.toString());
            }
            //************************************************************准备结束

            //6.提交工作流引擎运行
            String msg = linkeywf.run(actionid, enginParams);

            //7.如果出错则数据需要回滚
            if (BeanCtx.isRollBack()) {
                //获得回滚后的提示信息
                if (Tools.isBlank(linkeywf.getRollbackMsg())) {
                    msg = BeanCtx.getMsg("Engine", "Error_EngineRun");
                }
                else {
                    msg += linkeywf.getRollbackMsg();
                }
            }

            NodeUser nodeUser = (NodeUser) BeanCtx.getBean("NodeUser");
            String currentNodeid = nodeUser.getCurrentNodeid(linkeywf.getDocUnid());
            String endNodeid = nodeUser.getEndNodeid(linkeywf.getDocUnid());

            String jsonStr = "{\"msg\":\"" + msg + "\",\"Status\":\"ok\",\"DocUnid\":\"" + linkeywf.getDocUnid() + "\",\"Processid\":\"" + linkeywf.getProcessid() + "\",\"CurrentNodeList\":\""
                    + currentNodeid + "\",\"EndNodeList\":\"" + endNodeid + "\"}";
            //BeanCtx.out(jsonStr);
            BeanCtx.print(jsonStr);

        }
        catch (Exception e) {
            e.printStackTrace();
            BeanCtx.setRollback(true); //设置需要回滚
            String msg = BeanCtx.getMsg("Engine", "Error_EngineRun");
            BeanCtx.p("{\"msg\":\"" + msg + "\",\"Status\":\"Error\"}");
            BeanCtx.log(e, "E", msg);
        }

        return "";
    }

    /**
     * 获得后继节点列表
     * 
     * @param simProcessUnid
     * @param processid
     * @param sourceNodeid
     * @return
     * @throws Exception
     */
    public HashSet<String> getSimNextNodeid(String simProcessUnid, String processid, String sourceNodeid, String wf_docUnid) throws Exception {
        LinkedHashSet nodeSet = getNextTaskNodeid(simProcessUnid, processid, sourceNodeid, wf_docUnid);
        return nodeSet;
    }

    /**
     * 根据后继节点id获得节点的默认参与人员
     * 
     * @param nodeidList
     * @return
     */
    public HashMap<String, String> getSimNextNodeUser(HashSet<String> nodeidList, String processid, String simProcessUnid, HashMap<String, HashMap<String, String>> nextUserDeptMap) throws Exception {
        ModNode modNode = (ModNode) BeanCtx.getBean("ModNode");
        HashMap<String, String> nodeAndUserMap = new HashMap<String, String>();
        for (String nodeid : nodeidList) {
            String startUserList = "";
            LinkedHashSet<String> potentialOwner = new LinkedHashSet<String>();
            //1.首先看仿真设置中是否有指定处理用户
            String sql = "select NodeOwner from BPM_SimNodeList where SimProcessUnid='" + simProcessUnid + "' and Nodeid='" + nodeid + "'";
            BeanCtx.out("sql=" + sql);
            String simNodeUser = Rdb.getValueBySql(sql);
            if (Tools.isNotBlank(simNodeUser)) {
                //使用仿真中设置的用户
                HashMap<String, String> userDeptSet = new HashMap<String, String>();//用户与部门id的组合map
                userDeptSet = BeanCtx.getDeptidByMulStr(simNodeUser); //部门与用户的组合
                nextUserDeptMap.put(nodeid, userDeptSet); //节点，用户，部门的map对像
                startUserList = BeanCtx.getUseridByMulStr(simNodeUser); //去掉#号后面的用户id
            }
            else {
                //使用节点中设置的用户
                Document modNodeDoc = modNode.getNodeDoc(processid, nodeid);
                if (!modNodeDoc.isNull()) {
                    HashMap<String, String> userDeptSet = new HashMap<String, String>();//用户与部门id的组合map
                    startUserList = ((ModNode) BeanCtx.getBean("ModNode")).getNodePotentialOwner(processid, nodeid);
                    userDeptSet = BeanCtx.getDeptidByMulStr(startUserList); //部门与用户的组合
                    nextUserDeptMap.put(nodeid, userDeptSet); //节点，用户，部门的map对像
                    startUserList = BeanCtx.getUseridByMulStr(startUserList); //去掉#号后面的用户id
                    startUserList = Tools.join(BeanCtx.getLinkeyUser().parserNodeMembers(Tools.splitAsLinkedSet(startUserList)), ",");//分析用户字符串

                }
            }
            nodeAndUserMap.put(nodeid, startUserList);
        }
        return nodeAndUserMap;
    }

    /**
     * 获得指定节点后面所链接的任务节点或者是结束事件或者是子流程节点，排除网关和路由线
     * 
     * @param processid 流程id
     * @param nodeid 环节id
     * @param isCondition true表示要计算路由线中的条件表达式，false表示不需要计算
     * @param wf_docUnid主文档的unid
     * @return 返回目的节点的id集合
     */
    public LinkedHashSet<String> getNextTaskNodeid(String simProcessUnid, String processid, String sourceNodeid, String wf_docUnid) throws Exception {
        //首先找到此节点出去的所有线段,前提条件所有Task类型的节点编号为T开头
        ModNode modNode = (ModNode) BeanCtx.getBean("ModNode");
        LinkedHashSet<String> nodeList = new LinkedHashSet<String>();
        String sql = "select Nodeid,TargetNode from BPM_ModSequenceFlowList where Processid='" + processid + "' and SourceNode='" + sourceNodeid + "'";
        HashMap<String, String> sequenceFlow = Rdb.getMapDataBySql(sql);
        for (String sequenceFlowNodeid : sequenceFlow.keySet()) {

            sql = "select NodeStatus from BPM_SimNodeList where SimProcessUnid='" + simProcessUnid + "' and Nodeid='" + sequenceFlowNodeid + "'";
            String nodeStatus = Rdb.getValueBySql(sql);
            if (nodeStatus.equals("1")) {
                //强制路由

            }
            else if (nodeStatus.equals("0")) {
                //禁止路由
                continue;
            }
            else {
                //没有仿真的设置则根据流程模型中的条件进行计算是否成立
                if (!modNode.checkSequenceFlow(processid, sequenceFlowNodeid)) {
                    //条件不成立，跳过
                    continue;
                }
            }

            String targetNodeid = sequenceFlow.get(sequenceFlowNodeid);
            String nodeType = modNode.getNodeType(processid, targetNodeid);
            if (nodeType.equals("Task") || nodeType.equals("SubProcess") || modNode.getExtNodeType(processid, targetNodeid).equals("endEvent")) {
                //如果目的节点已经是Task/endEvent类型则停止查找并返回节点id
                nodeList.add(targetNodeid);
            }
            else {
                //如果不是Task是路由线、网关、事件等则还要再往后查
                nodeList.addAll(getNextTaskNodeid(simProcessUnid, processid, targetNodeid, wf_docUnid));
            }

        }
        return nodeList;
    }

}