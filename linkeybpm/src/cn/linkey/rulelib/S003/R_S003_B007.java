package cn.linkey.rulelib.S003;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;
import cn.linkey.wf.InsUser;
import cn.linkey.wf.ProcessEngine;
import cn.linkey.wf.Remark;

/**
 * 本规则为撤销 WF_RunActionid=Undo
 * 
 * <pre>
 * 暂不支持转交后的收回操作
 * 因为转交后的SourceOrUnid是上一环节的SourceOrUnid而不是转交人的
 * 如果要支持则需要特别的判断ReassignmentUserid用户标记来实现而不是SourceOrUnid
 * </pre>
 * 
 * 参数说明:本方法所有参数均在后台分析获得，无需传入任何参数
 * 
 * @author Administrator
 */
public class R_S003_B007 implements LinkeyRule {
    /**
     * GoTo->R_S003_B007 动作
     */
    @Override
    public String run(HashMap<String, Object> params) throws Exception {

        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        String processid = linkeywf.getProcessid();
        String docUnid = linkeywf.getDocUnid();
        String returnMsg = "";

        //1.首先获得用户的ActionNum和他所在的环节id以及部门Deptid
        String sql = "select Nodeid,WF_OrUnid,Deptid,SOURCEORUNID from BPM_InsUserList where DocUnid='" + docUnid + "' and Userid='" + BeanCtx.getUserid() + "' and Status='End' order by EndTime desc";
        Document userdoc = Rdb.getDocumentBySql(sql);
        if (userdoc.isNull()) {
            //如果获取不到就直接退出
            returnMsg = BeanCtx.getMsg("Engine", "UndoError");
            return returnMsg;
        }
        String actionNodeid = userdoc.g("Nodeid");
        String sourceOrUnid = userdoc.g("WF_OrUnid");
        String deptid = userdoc.g("Deptid");

        //2.看是否有权限进行收回操作,如果用户已处理则不允许收回,要排除自已(因为新启动的流程时SourceOrUnid为自已的OrUnid)
        sql = "select WF_OrUnid from BPM_InsUserList where DocUnid='" + docUnid + "' and Userid<>'" + BeanCtx.getUserid() + "' and Status='End' and SourceOrUnid='" + sourceOrUnid + "'";
        if (Tools.isBlank(sourceOrUnid) || Rdb.hasRecord(sql)) {
            returnMsg = BeanCtx.getMsg("Engine", "UndoError");
            return returnMsg;
        }

        //3.删除所有sourceOrUnid所影响的环节及用户记录
        //不能删除自已的这条记录(新发起的流程就是这个情况)
        //修改2017-5-12 添加退回记录的删除
        sql = "delete from BPM_InsUserList where DocUnid='" + docUnid + "' and ((SourceOrUnid='" + sourceOrUnid + "' and  WF_OrUnid<>'" + sourceOrUnid + "') or (IsBackFlag = '1' and ActionUserid = '"
                + BeanCtx.getUserid() + "' and Status = 'Current'))";
        Rdb.execSql(sql);
        sql = "delete from BPM_InsCopyUserList where DocUnid='" + docUnid + "' and SourceOrUnid='" + sourceOrUnid + "'";
        Rdb.execSql(sql);
        sql = "delete from BPM_InsNodeList where DocUnid='" + docUnid + "' and SourceOrUnid='" + sourceOrUnid + "'";
        Rdb.execSql(sql);

        //4.准备重新启动本用户的任务的参数
        InsUser insUser = (InsUser) BeanCtx.getBean("InsUser");
        HashMap<String, String> userDept = new HashMap<String, String>();
        userDept.put(BeanCtx.getUserid(), deptid);
        if (StringUtils.isEmpty(BeanCtx.getLinkeywf().getSourceOrUnid())) {
            Document currentInsUserDoc = BeanCtx.getDocumentBean("BPM_InsUserList"); //创建一个空文档,防止后面获得字段时出错
            currentInsUserDoc.s("StartTime", DateUtil.getNow());
            currentInsUserDoc.s("WF_OrUnid", userdoc.g("SOURCEORUNID"));
            BeanCtx.getLinkeywf().setCurrentInsUserDoc(currentInsUserDoc);
        }
        //4.1启动用户任务
        insUser.startUser(processid, docUnid, actionNodeid, BeanCtx.getUserid(), userDept);

        //5.增加流转记录
        Remark remark = (Remark) BeanCtx.getBean("Remark");
        remark.AddRemark("Undo", (String) params.get("WF_Remark"), "1");

        //6.获得提示消息
        returnMsg = BeanCtx.getMsg("Engine", "Undo");

        linkeywf.setRunStatus(true);//表示运行成功
        return returnMsg;
    }

}
