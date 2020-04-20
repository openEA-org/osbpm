package cn.linkey.rulelib.S017;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;
import cn.linkey.wf.InsNode;
import cn.linkey.wf.ProcessEngine;
import cn.linkey.wf.ProcessUtil;

/**
 * @RuleName:Engine_启动一个新的子流程
 * @author admin
 * @version: 8.0
 * @Created: 2015-04-27 11:50
 */
final public class R_S017_B031 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        //示例参数:{"MainDocUnid":"027dad530861c042f60b6dc0d56028d440d1","ParentSubNodeid":"S10003","SubProcessid":"a8bb004c00173041060b5d502bada1435268","SubNodeid":"T10054","TargetUser":"admin","CopyData":"Y","CopyAttach":"N"}

        String copyData = (String) params.get("CopyData"); //拷贝数据到子流程中
        String copyAttach = (String) params.get("CopyAttach"); //拷贝附件到子流程中
        String parentNodeid = (String) params.get("ParentSubNodeid"); //主流程中的启动子流程节点id
        String mainDocUnid = (String) params.get("MainDocUnid"); //主流程实例的docunid
        String subProcessid = (String) params.get("SubProcessid"); //要启动的子流程的id号
        String targetNodeid = (String) params.get("SubNodeid");
        ; //要启动的目标节点为的id	    		
        String targetUser = (String) params.get("TargetUser"); //要启动的子流程的用户id

        //1.先找到主流程文档
        String sql = "select * from bpm_maindata where WF_OrUnid='" + mainDocUnid + "'";
        Document parentDocument = Rdb.getDocumentBySql(sql);

        //2.看主流程的节点是否已经启动，如果不有则启动起来
        String parentProcessid = parentDocument.g("WF_Processid");
        ProcessEngine processEngine = new ProcessEngine();
        BeanCtx.setLinkeywf(processEngine); //切换全局引擎变量到当前实例
        processEngine.init(parentProcessid, mainDocUnid, BeanCtx.getUserid(), "");
        ((InsNode) BeanCtx.getBean("InsNode")).startNode(parentProcessid, mainDocUnid, parentNodeid);

        //3.启动子流程
        String docUnid = Rdb.getNewUnid();
        Document subDoc = BeanCtx.getDocumentBean("BPM_MainData");
        if (copyData.equals("Y"))
            parentDocument.copyAllItems(subDoc); //拷贝数据
        subDoc.s("WF_OrUnid", docUnid);
        if (copyAttach.equals("Y"))
            parentDocument.copyAttachment(subDoc); //拷贝附件
        subDoc.s("WF_OrUnid", docUnid); //需要重新设置一次
        subDoc.s("WF_MainDocUnid", parentDocument.getDocUnid()); //设置子流程文档的主文档UNID,子流程返回时有用
        subDoc.s("WF_MainNodeid", parentNodeid); //记录启动子流程的节点id,子流程返回主流程时有用
        subDoc.s("WF_DocNumber", Rdb.getNewSerialNo("ENGINEDOCUMENT")); //设置子流程的文档编号
        subDoc.s("WF_DocCreated", DateUtil.getNow()); //设置启动时间
        subDoc.s("WF_AddName", BeanCtx.getUserid());
        subDoc.s("WF_AddName_CN", BeanCtx.getUserName());

        String msg = ProcessUtil.startNewProcess(subDoc, subProcessid, docUnid, targetNodeid, targetUser, "", "同意");

        return "{\"status\":\"1\",\"msg\":\"" + msg + "\"}";

    }
}