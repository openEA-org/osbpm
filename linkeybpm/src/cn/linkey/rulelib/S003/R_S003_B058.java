package cn.linkey.rulelib.S003;

import java.util.HashMap;
import java.util.LinkedHashSet;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;
import cn.linkey.wf.InsNode;
import cn.linkey.wf.ModNode;
import cn.linkey.wf.NodeUser;
import cn.linkey.wf.ProcessEngine;
import cn.linkey.wf.ProcessUtil;

/**
 * 启动子流程
 * 
 * @author Administrator
 *
 */
public class R_S003_B058 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        String processid = linkeywf.getProcessid();
        String runNodeid = (String) params.get("WF_RunNodeid"); // 获得要运行的节点id,即前置事件环节的nodeid,如果为空表示没有画

        // 1.首先启动本节点
        InsNode insNode = (InsNode) BeanCtx.getBean("InsNode");
        insNode.startNode(processid, linkeywf.getDocUnid(), runNodeid);

        // 2.如果选择了子流程则自动启动选中的子流程
        ModNode insModNode = (ModNode) BeanCtx.getBean("ModNode");
        Document subNodeDoc = insModNode.getNodeDoc(processid, runNodeid); // 子流程文档
        String subProcessid = subNodeDoc.g("SubProcessid");
        if (Tools.isNotBlank(subProcessid)) {
            startSubProcess(subProcessid, runNodeid, subNodeDoc, params);
        }

        // 3.如果选中了规则则执行规则去动态启动子流程
        String startProcessRuleNum = subNodeDoc.g("StartProcessRuleNum");
        if (Tools.isNotBlank(startProcessRuleNum)) {
            params.put("WF_NodeDoc", subNodeDoc); // 节点文档
            params.put("WF_MainDoc", linkeywf.getDocument()); // 主流程文档
            BeanCtx.getExecuteEngine().run(startProcessRuleNum, params); // 在数据转换规则通过WF_SubDoc可获得子文档对像
        }

        return "";
    }

    /**
     * 注意一次提交WF_SubNextUserList只能提交给一个子流程的节点 不支持节点指定, 也就是不支持同时画两个子流程节点然后分别指定启动者 子流程节点中可以用不使用post的用户来解决这个问题，读表单中的字段作为参与者
     * 
     * @param processid
     * @param runNodeid
     * @param subNodeDoc
     * @param params
     * @throws Exception
     */
    public void startSubProcess(String processid, String runNodeid, Document subNodeDoc, HashMap<String, Object> params) throws Exception {
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        ModNode insModNode = (ModNode) BeanCtx.getBean("ModNode");
        String subNextUserList = (String) params.get("WF_SubNextUserList"); // 要提交的子流程的目标用户

        // 2.启动一个新的子流程
        String subProcessid = subNodeDoc.g("SubProcessid");
        String subCopyData = subNodeDoc.g("SubCopyData");
        String subCopyAttach = subNodeDoc.g("subCopyAttach");
        String subRuleNum = subNodeDoc.g("SubRuleNum");
        String startSubNodeid = subNodeDoc.g("StartSubNodeid");
        String startMulInsByUserid = subNodeDoc.g("StartMulInsByUserid"); // 每用户启动一个新实例
        String targetNodeid = startSubNodeid;
        if (Tools.isBlank(targetNodeid)) {
            targetNodeid = insModNode.getStartNodeid(subProcessid); // 获得子流程的首环节的nodeid,只能启动子流程的首环节节点
        }

        // 准备启动子流程,子流程的首节点(userTask节点)需要指定为使用环节中的配置的用户作为参与者
        // 如果要在上一环节中指定子流程的的参与者，可以在主表单中弄一个字段然后把子流程的节点的参与者设定为使用字段值作为参与者
        NodeUser insNodeUser = (NodeUser) BeanCtx.getBean("NodeUser");
        String targetUser = subNextUserList; // 看是否有提交指定子流程节点的参与人员格式为admin#Deptid或admin,user01
        HashMap<String, HashMap<String, String>> nextUserDeptMap = new HashMap<String, HashMap<String, String>>(); // key为节点id,提交后继环节的用户和部门列表,user01#Deptid,user02#Deptid
        if (Tools.isNotBlank(targetUser)) {
            nextUserDeptMap.put(targetNodeid, insNodeUser.getNodeUserAndDept(targetUser, targetNodeid));// 获得要提交的用户的部门id,如果有部门指定时有用，没有也可以为空
        }

        // 开始启动子流程实例
        String msg = "";
        if (startMulInsByUserid.equals("Y")) {

            // 没有指定用户的情况下读取节点中的配置值
            if (Tools.isBlank(targetUser)) {
                targetUser = insModNode.getNodePotentialOwner(subProcessid, targetNodeid);
                LinkedHashSet<String> userSet = Tools.splitAsLinkedSet(targetUser);
                targetUser = Tools.join(BeanCtx.getLinkeyUser().parserNodeMembers(userSet), ",");
            }

            // 每用户启动一个新的流程实例
            if (linkeywf.isDebug()) {
                BeanCtx.out("每用户启动同一个子流程:节点" + targetNodeid + "->用户" + targetUser);
            }

            String[] userArray = Tools.split(targetUser);
            for (String userid : userArray) {
                Document subDoc = BeanCtx.getDocumentBean("BPM_MainData");
                String newDocUnid = Rdb.getNewUnid();
                subDoc.s("WF_OrUnid", newDocUnid);
                if (subCopyData.equals("1")) {
                    // 拷贝所有文档数据到子文档中去
                    linkeywf.getDocument().copyAllItems(subDoc, true); // 不拷贝WF_开头的字段内容过去，因为会覆盖WF_MainDocUnid等一些关键字段
                    subDoc.appendTextList("WF_AllReaders", linkeywf.getDocument().g("WF_AllReaders"));// 主流程的人可以看子流程的文档信息
                    subDoc.s("WF_AddName", linkeywf.getDocument().g("WF_AddName")); // 继承流程申请者
                    subDoc.s("WF_OrUnid", newDocUnid);// 重新设置一下DocUnid因为这个值会被覆盖
                }
                if (subCopyAttach.equals("1")) {
                    // 拷贝所有附件到子流程的文档中去
                    linkeywf.getDocument().copyAttachment(subDoc);
                }
                if (Tools.isNotBlank(subRuleNum)) {
                    // 执行数据转换规则
                    params.put("WF_NodeDoc", subNodeDoc); // 节点文档
                    params.put("WF_SubDoc", subDoc); // 子流程文档
                    params.put("WF_MainDoc", linkeywf.getDocument()); // 主流程文档
                    BeanCtx.getExecuteEngine().run(subRuleNum, params); // 在数据转换规则通过WF_SubDoc可获得子文档对像
                }
                String topDocUnid = linkeywf.getDocument().g("WF_TopDocUnid");
                if (Tools.isBlank(topDocUnid)) {
                    topDocUnid = linkeywf.getDocUnid();
                }
                subDoc.s("WF_TopDocUnid", topDocUnid);
                subDoc.s("WF_MainDocUnid", linkeywf.getDocUnid()); // 设置子流程文档的主文档UNID,子流程返回时有用
                subDoc.s("WF_MainNodeid", runNodeid); // 记录启动子流程的节点id,子流程返回主流程时有用
                subDoc.s("WF_DocNumber", Rdb.getNewSerialNo("ENGINEDOCUMENT")); // 设置子流程的文档编号
                subDoc.s("WF_DocCreated", DateUtil.getNow()); // 设置启动时间
                msg = ProcessUtil.startNewProcess(subDoc, subProcessid, subDoc.getDocUnid(), targetNodeid, userid, "", BeanCtx.g("WF_Remark"), nextUserDeptMap);
                if (linkeywf.isDebug()) {
                    BeanCtx.out("子流程启动结果:" + msg);
                }
                // 删除多出来的一条当前用户在首环节的结束信息记录,通过startNewProcess()启动后因为是当前环节提交给当前环节所以首环节会BPM_InsUserList多出一条当前操作者的End记录
                String sql = "delete from BPM_InsUserList where DocUnid='" + subDoc.getDocUnid() + "' and Status='End' and Userid='" + BeanCtx.getUserid() + "' and Nodeid='" + targetNodeid + "'";
                Rdb.execSql(sql);

            }
        }
        else {
            // 所有用户启动同一个流程实例,创建一个新文档
            if (linkeywf.isDebug()) {
                BeanCtx.out("所有用户启动同一个子流程:节点" + targetNodeid + "->用户" + targetUser);
            }

            Document subDoc = BeanCtx.getDocumentBean("BPM_MainData");
            String newDocUnid = Rdb.getNewUnid();
            subDoc.s("WF_OrUnid", newDocUnid);
            if (subCopyData.equals("1")) {
                // 拷贝所有文档数据到子文档中去
                linkeywf.getDocument().copyAllItems(subDoc, true); // 不拷贝WF_开头的字段内容过去，因为会覆盖WF_MainDocUnid等一些关键字段
                subDoc.s("WF_BusinessNum", linkeywf.getDocument().g("WF_BusinessNum")); // 仿真时需要把这个字段拷贝到子流程中去
                subDoc.appendTextList("WF_AllReaders", linkeywf.getDocument().g("WF_AllReaders"));// 主流程的人可以看子流程的文档信息
                subDoc.s("WF_AddName", linkeywf.getDocument().g("WF_AddName")); // 继承流程申请者
                subDoc.s("WF_OrUnid", Rdb.getNewUnid());
            }
            if (subCopyAttach.equals("1")) {
                // 拷贝所有附件到子流程的文档中去
                linkeywf.getDocument().copyAttachment(subDoc);
            }
            if (Tools.isNotBlank(subRuleNum)) {
                // 执行数据转换规则
                params.put("WF_NodeDoc", subNodeDoc); // 节点文档
                params.put("WF_SubDoc", subDoc); // 子流程文档
                params.put("WF_MainDoc", linkeywf.getDocument()); // 主流程文档
                BeanCtx.getExecuteEngine().run(subRuleNum, params); // 在数据转换规则通过WF_SubDoc可获得子文档对像
            }
            String topDocUnid = linkeywf.getDocument().g("WF_TopDocUnid");
            if (Tools.isBlank(topDocUnid)) {
                topDocUnid = linkeywf.getDocUnid();
            }
            subDoc.s("WF_TopDocUnid", topDocUnid);
            subDoc.s("WF_MainDocUnid", linkeywf.getDocUnid()); // 设置子流程文档的主文档UNID,子流程返回时有用
            subDoc.s("WF_MainNodeid", runNodeid); // 记录启动子流程的节点id,子流程返回主流程时有用
            subDoc.s("WF_DocNumber", Rdb.getNewSerialNo("ENGINEDOCUMENT")); // 设置子流程的文档编号
            subDoc.s("WF_DocCreated", DateUtil.getNow()); // 设置启动时间
            msg = ProcessUtil.startNewProcess(subDoc, subProcessid, subDoc.getDocUnid(), targetNodeid, targetUser, "", BeanCtx.g("WF_Remark"), nextUserDeptMap);
            if (linkeywf.isDebug()) {
                BeanCtx.out("子流程启动结果:" + msg);
            }
            // 删除多出来的一条当前用户在首环节的结束信息记录,通过startNewProcess()启动后因为是当前环节提交给当前环节所以首环节会BPM_InsUserList多出一条当前操作者的End记录
            String sql = "delete from BPM_InsUserList where DocUnid='" + subDoc.getDocUnid() + "' and Status='End' and Userid='" + BeanCtx.getUserid() + "' and Nodeid='" + targetNodeid + "'";
            Rdb.execSql(sql);

        }

        // if(linkeywf.isDebug()){
        //     BeanCtx.out("运行结果:"+msg);
        // }

    }
}
