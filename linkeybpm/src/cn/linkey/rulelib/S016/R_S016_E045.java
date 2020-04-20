package cn.linkey.rulelib.S016;

import java.util.Calendar;
import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.dao.SqlType;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:环比流程效率趋势
 * @author admin
 * @version: 8.0
 * @Created: 2014-11-10 10:36
 */
final public class R_S016_E045 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        Document viewDoc = (Document) params.get("GridDoc"); //grid配置文档
        String eventName = (String) params.get("EventName");//事件名称
        String processid = BeanCtx.g("processid");

        // String year = "2014";
        
        // 20190108 修改写死的年份为获取当前系统的年份
        String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        // 20190108 END
        
        //	processid="f217a2fe03e52048d10ba0b0950eec57";
        if (eventName.equals("onViewOpen")) {
            return onViewOpen(viewDoc, processid, year);
        }
        return "1";
    }

    public String onViewOpen(Document viewDoc, String processid, String year) throws Exception {
        //如果不返回1则表示退出本视图并输出返回的字符串
        //图表JsHeader中用[X]字段名[/X]标识,viewDoc.s("字段名","10,20,30")来动态输出计算好的数据,
        String starttime = "";
        String endtime = "";
        String starttime1 = "";
        String endtime1 = "";
        String nodename = "[";
        String value = "[";
        String time = "[";
        String timesql = "";
        String numsql = "";
        String num = "";
        String timesql1 = "";
        String numsql1 = "";
        String time1 = "";
        String num1 = "";
        String archsql = "";
        float s1 = 0;
        float s2 = 0;
        String s3 = "";
        String processsql = "select nodename from bpm_modprocesslist where processid='" + processid + "'";
        String process = Rdb.getDocumentBySql(processsql).g("nodename");

        for (int i = 1; i <= 12; i++) {
            //int num=0;
            // int timeoutnum=0;
            //int num1=0;
            //int timeoutnum1=0;
            if (2 <= i && i < 10) {
                starttime = year + "-" + "0" + (i - 1) + "-01";
                endtime = year + "-" + "0" + (i - 1) + "-31";
                starttime1 = year + "-" + "0" + i + "-01";
                endtime1 = year + "-" + "0" + i + "-31";
            }
            else if (i > 9) {
                starttime = year + "-" + (i - 1) + "-01";
                endtime = year + "-" + (i - 1) + "-31";
                starttime1 = year + "-" + i + "-01";
                endtime1 = year + "-" + i + "-31";
            }
            else if (i == 1) {
                starttime = Integer.parseInt(year) - 1 + "-12-01";
                endtime = Integer.parseInt(year) - 1 + "-12-31";
                starttime1 = year + "-01-01";
                endtime1 = year + "-01-31";
            }
            
            //timesql = "select wf_processname,SUM(DATEDIFF(hour,CONVERT(datetime,wf_doccreated),convert(datetime,wf_lastmodified))) as time from BPM_ArchivedData  where wf_processid='" + processid
            //        + "' and  WF_DocCreated between '" + starttime + "' and '" + endtime + "' group by wf_processname";
            //numsql = "select COUNT(*) as num from BPM_ArchivedData where WF_Processid='" + processid + "' and WF_DocCreated between '" + starttime + "' and '" + endtime + "'";
            //timesql1 = "select wf_processname,SUM(DATEDIFF(hour,CONVERT(datetime,wf_doccreated),convert(datetime,wf_lastmodified))) as time from BPM_ArchivedData  where wf_processid='" + processid
            //        + "' and  WF_DocCreated between '" + starttime1 + "' and '" + endtime1 + "' group by wf_processname";
            //numsql1 = "select COUNT(*) as num from BPM_ArchivedData where WF_Processid='" + processid + "' and WF_DocCreated between '" + starttime1 + "' and '" + endtime1 + "'";
            //String DBType = Tools.getProperty("DBType");
        	//20190328 改为自动获取当前数据库类型
        	String DBType = Rdb.getDbType();
            timesql = SqlType.createSql(DBType, "R_S016_E045_timesql", processid, starttime, endtime);
            numsql = SqlType.createSql(DBType, "R_S016_E045_numsql", processid, starttime, endtime);
            timesql1 = SqlType.createSql(DBType, "R_S016_E045_timesql1", processid, starttime1, endtime1);
            numsql1 = SqlType.createSql(DBType, "R_S016_E045_numsql1", processid, starttime1, endtime1);
            
            nodename = nodename + "'" + starttime1.substring(0, 7) + "',";
            //System.out.println("sub:"+starttime1.substring(0,7));
            time = Rdb.getDocumentBySql(timesql).g("time");
            num = Rdb.getDocumentBySql(numsql).g("num");

            time1 = Rdb.getDocumentBySql(timesql1).g("time");
            num1 = Rdb.getDocumentBySql(numsql1).g("num");
            if (num.equals("0")) {
                value = value + "{value:" + "0" + ",name:'" + starttime1.substring(0, 7) + "'},";
                continue;
            }
            if (num1.equals("0")) {
                value = value + "{value:" + "-100" + ",name:'" + starttime1.substring(0, 7) + "'},";
                continue;
            }
            s1 = Float.parseFloat(time) / Float.parseFloat(num);
            s2 = Float.parseFloat(time1) / Float.parseFloat(num1);
            if (s1 == 0) {
                s3 = "100";
            }
            else
                s3 = ((s2 - s1) / s1) * 100 + "";
            //System.out.println("s3:"+s3);
            value = value + "{value:" + s3 + ",name:'" + starttime1.substring(0, 7) + "'},";
        }
        nodename = nodename.substring(0, nodename.length() - 1);
        value = value.substring(0, value.length() - 1);
        process = "['环比流程效率趋势(" + process + ")']";
        nodename = nodename + "]";
        value = value + "]";
        viewDoc.s("process", process);
        viewDoc.s("nodename", nodename);
        // System.out.println("nodename:"+nodename);
        //  System.out.println("value:"+value);
        viewDoc.s("value", value);
        return "1";
    }

}