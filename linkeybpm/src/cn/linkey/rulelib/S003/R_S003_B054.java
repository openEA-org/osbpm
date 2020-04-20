package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.wf.ProcessEngine;

/**
 * 参数说明:本动作无需传入参数
 * 
 * @RuleName:暂停流程
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-20 17:51
 */
final public class R_S003_B054 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        // params为运行本规则时所传入的参数
        // BeanCtx.setDebug();
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        String docUnid = linkeywf.getDocUnid();

        if (Rdb.hasRecord("select WF_OrUnid from BPM_ArchivedData where WF_OrUnid='" + docUnid + "'")) {
            return "错误:本文档已结束审批,不能再暂停本流程!";
        }

        if (!linkeywf.getDocument().g("WF_AddName").equals(BeanCtx.getUserid())) {
            return "错误:本流程不是由您发起的，无权暂停本流程!";
        }

        // 1.把所有活动的用户实例标识为暂停
        String sql = "update BPM_InsUserList set Status='Pause' where Status='Current' and DocUnid='" + docUnid + "'";
        Rdb.execSql(sql);

        // 2.把所有活动的环节标识为暂停
        sql = "update BPM_InsNodeList set Status='Pause' where Status='Current' and DocUnid='" + docUnid + "'";
        Rdb.execSql(sql);

        // 3.把主文档的状态改为Pause
        linkeywf.getDocument().s("WF_Status", "Pause");

        linkeywf.setRunStatus(true);// 表示运行成功
        return "流程已成功暂停并已放入您的草稿箱中!";
    }
}