package cn.linkey.rulelib.S017;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rest.RestUtil;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;
import cn.linkey.wf.InsNode;
import cn.linkey.wf.ProcessEngine;
import cn.linkey.wf.ProcessUtil;

/**
 * @RuleName:Rest_Engine启动一个新的子流程
 * @author admin
 * @version:1.0
 */
final public class R_S017_B119 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数

        String copyData = BeanCtx.g("copyData"); //拷贝数据到子流程中
        String copyAttach = BeanCtx.g("copyAttach"); //拷贝附件到子流程中
        String parentNodeid = BeanCtx.g("parentSubNodeId"); //主流程中的启动子流程节点id
        String mainDocUnid = BeanCtx.g("parentDocUnid"); //主流程实例的docunid
        String subProcessid = BeanCtx.g("subProcessId"); //要启动的子流程的id号
        String targetNodeid = BeanCtx.g("subNodeId");//要启动的目标节点为的id
        String remark = BeanCtx.g("remark");
        String targetUser = BeanCtx.g("subNodeUserId"); //要启动的子流程的用户id

        if(Tools.isBlank(parentNodeid)){return RestUtil.formartResultJson("0", "parentSubNodeId不能为空");}
        if(Tools.isBlank(mainDocUnid)){return RestUtil.formartResultJson("0", "parentDocUnid不能为空");}
        if(Tools.isBlank(subProcessid)){return RestUtil.formartResultJson("0", "subProcessId不能为空");}
        if(Tools.isBlank(targetNodeid)){return RestUtil.formartResultJson("0", "subNodeId不能为空");}
        if(Tools.isBlank(targetUser)){return RestUtil.formartResultJson("0", "subNodeUserId不能为空");}
        
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
        if (copyData.equals("Y")){
            parentDocument.copyAllItems(subDoc); //拷贝数据
        }
        subDoc.s("WF_OrUnid", docUnid);
        if (copyAttach.equals("Y")){
            parentDocument.copyAttachment(subDoc); //拷贝附件
        }
        
        subDoc.s("WF_OrUnid", docUnid); //需要重新设置一次
        subDoc.s("WF_MainDocUnid", parentDocument.getDocUnid()); //设置子流程文档的主文档UNID,子流程返回时有用
        subDoc.s("WF_MainNodeid", parentNodeid); //记录启动子流程的节点id,子流程返回主流程时有用
        subDoc.s("WF_DocNumber", Rdb.getNewSerialNo("ENGINEDOCUMENT")); //设置子流程的文档编号
        subDoc.s("WF_DocCreated", DateUtil.getNow()); //设置启动时间
        subDoc.s("WF_AddName", BeanCtx.getUserid());
        subDoc.s("WF_AddName_CN", BeanCtx.getUserName());
        subDoc.appendFromRequest(); //其他键值对参数
        
        String msg = ProcessUtil.startNewProcess(subDoc, subProcessid, docUnid, targetNodeid, targetUser, "", remark);

        String jsonStr="{\"WF_DocUnid\":\""+docUnid+"\"}";
	    return RestUtil.formartResultJson("1", msg,jsonStr);

    }
}