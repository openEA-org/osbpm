package cn.linkey.rulelib.S029;

import java.util.Date;
import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;
import cn.linkey.wf.ProcessEngine;

/**
 * @RuleName:检测前置环节条件或延时是否已满足(半小时执行一次)
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-09 10:06
 */
final public class R_S029_T006 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {

        // 1.先检测定时条件是否满足要求，如果有满足要求的就把环节推到后继节点
        String sql = "select * from BPM_InsStayData where WF_TimeRuleNum<>''";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : dc) {
            if (Tools.isNotBlank(doc.g("WF_TimeRuleNum"))) {
                String runFlag = BeanCtx.getExecuteEngine().run(doc.g("WF_TimeRuleNum"));
                if (runFlag.equals("1")) {
                    // 说明条件已经满足，运行前置节点的结束规则
                    endFrontEvent(doc);
                }
            }
        }

        // 2.检测延时的节点是否已经满足条件
        sql = "select * from BPM_InsStayData where WF_TimeDelay<>'0'";
        dc = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : dc) {
            String timeDelay = doc.g("WF_TimeDelay");
            if (Tools.isNotBlank(timeDelay)) {
                String startTime = doc.g("WF_DocCreated");
                Date startDate = DateUtil.getDate(startTime, null);
                Date endDate = DateUtil.getDate();
                double difHour = DateUtil.getDifTwoTime(endDate, startDate, "H");
                if (difHour >= Integer.valueOf(timeDelay)) {
                    // 说明延时时间已经到，运行前置节点的结束规则
                    endFrontEvent(doc);
                }
            }
        }
        return "";
    }

    /**
     * 结束前置事件节点，推进到后继环节
     * 
     * @param doc
     */
    public void endFrontEvent(Document doc) throws Exception {
        ProcessEngine linkeywf = (ProcessEngine) BeanCtx.getBean("linkeywf");
        String processid = doc.g("Processid");
        String docUnid = doc.g("DocUnid");
        String userid = doc.g("Userid");
        String nodeid = doc.g("StayNodeid"); // 前置节点的nodeid
        linkeywf.init(processid, docUnid, userid, "");

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("WF_RunNodeid", nodeid);
        params.put("WF_NextNodeid", "");
        BeanCtx.getExecuteEngine().run("R_S003_B024", params); // 结束前置事件环节
    }
}