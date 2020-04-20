package cn.linkey.rulelib.S016;

import java.util.Calendar;
import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:环比环节超时趋势
 * @author admin
 * @version: 8.0
 * @Created: 2014-11-07 11:13
 */
final public class R_S016_E044 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        Document viewDoc = (Document) params.get("GridDoc"); //grid配置文档
        String eventName = (String) params.get("EventName");//事件名称
        // String year = "2014";
        
        // 20190108 修改写死的年份为获取当前系统的年份
        String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        // 20190108 END
        
        String processid = BeanCtx.g("processid");
        // processid="f217a2fe03e52048d10ba0b0950eec57";
        if (eventName.equals("onViewOpen")) {
            return onViewOpen(viewDoc, year, processid);
        }
        return "1";
    }

    public String onViewOpen(Document viewDoc, String year, String processid) throws Exception {
        //如果不返回1则表示退出本视图并输出返回的字符串
        //图表JsHeader中用[X]字段名[/X]标识,viewDoc.s("字段名","10,20,30")来动态输出计算好的数据,
        String starttime = "";
        String endtime = "";
        String starttime1 = "";
        String endtime1 = "";
        String processnodename = "[";
        String nodename = "[";
        String value = "[";
        //	System.out.println(now);
        String timeoutsql = "";
        String numsql = "";
        String numsql1 = "";
        String timeoutsql1 = "";
        boolean full = false;
        String processnamesql = "select nodename from bpm_modprocesslist where processid='" + processid + "'";
        String processname = Rdb.getDocumentBySql(processnamesql).g("nodename");
        String nodenamesql = "select distinct nodename from BPM_InsRemarkList where Processid='" + processid + "'";
        for (Document nodenamedoc : Rdb.getAllDocumentsBySql(nodenamesql)) {
            if (nodenamedoc.g("nodename").length() == 0) {
                processnodename = processnodename + "'流程环节名为空',";
            }
            else
                processnodename = processnodename + "'" + nodenamedoc.g("nodename") + "',";
            String data = "";
            String num = "";
            String timeoutnum = "";
            String num1 = "";
            String timeoutnum1 = "";

            for (int i = 1; i <= 12; i++) {
                if (2 < i && i < 10) {
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
                numsql = "select distinct nodename,COUNT(*) as num from BPM_InsRemarkList where nodename='" + nodenamedoc.g("nodename") + "' and processid='" + processid + "' and starttime between '"
                        + starttime + "' and '" + endtime + "' group by nodename ";
                timeoutsql = "select distinct nodename,COUNT(*) as num from BPM_InsRemarkList where nodename='" + nodenamedoc.g("nodename") + "' and processid='" + processid
                        + "' and overtimeflag <>0  and starttime between '" + starttime + "' and '" + endtime + "' group by nodename";
                numsql1 = "select distinct nodename,COUNT(*) as num from BPM_InsRemarkList where nodename='" + nodenamedoc.g("nodename") + "' and processid='" + processid + "' and starttime between '"
                        + starttime1 + "' and '" + endtime1 + "' group by nodename ";
                timeoutsql1 = "select distinct nodename,COUNT(*) as num from BPM_InsRemarkList where nodename='" + nodenamedoc.g("nodename") + "' and  processid='" + processid
                        + "' and overtimeflag <>0  and starttime between '" + starttime1 + "' and '" + endtime1 + "' group by nodename";
                // System.out.println("numsql:"+numsql);
                // System.out.println("timeoutsql:"+timeoutsql);
                if (!full) {
                    nodename = nodename + "'" + year + "-" + i + "',";
                    if (i == 12)
                        full = true;
                }
                num = Rdb.getDocumentBySql(numsql).g("num");
                timeoutnum = Rdb.getDocumentBySql(timeoutsql).g("num");
                num1 = Rdb.getDocumentBySql(numsql1).g("num");
                timeoutnum1 = Rdb.getDocumentBySql(timeoutsql1).g("num");

                if (Tools.isBlank(num)) {
                    num = "0";
                }
                if (Tools.isBlank(num1)) {
                    num1 = "0";
                }
                if (Tools.isBlank(timeoutnum)) {
                    timeoutnum = "0";
                }
                if (Tools.isBlank(timeoutnum1)) {
                    timeoutnum1 = "0";
                }

                try {
                    float temp = Float.parseFloat(timeoutnum) / Float.parseFloat(num);
                    if (num1.equals("0")) {
                        data = data + "100,";
                        continue;
                    }
                    else {
                        float temp1 = Float.parseFloat(timeoutnum1) / Float.parseFloat(num1);
                        String t = (temp1 - temp) / temp * 100 + "";
                        if (t.equals("NaN") || t.equals("Infinity"))
                            data = data + "0,";
                        else {
                            data = data + (temp1 - temp) / temp * 100 + ",";
                        }
                    }

                }
                catch (Exception e) {
                    data = data + "0,";
                }
            }
            if (nodenamedoc.g("nodename").length() == 0) {
                value = value + "{name:'" + "流程环节名为空" + "',type:'line',stack:'总量',data:[" + data.substring(0, data.length()) + "]},";
            }
            else
                value = value + "{name:'" + nodenamedoc.g("nodename") + "',type:'line',stack:'总量',data:[" + data.substring(0, data.length()) + "]},";

        }
        if (nodename.length() > 1) {
            processnodename = processnodename.substring(0, processnodename.length() - 1);
            nodename = nodename.substring(0, nodename.length() - 1);
            //data=data.substring(0,data.length()-1);
            value = value.substring(0, value.length() - 1);
            processnodename = processnodename + "]";
            nodename = nodename + "]";
            value = value + "]";
        }
        else {
            processnodename = "['']";
            nodename = "['']";
            value = "[{name:'',type:'line',stack:'总量',data:[]}]";
        }
        processname = "['环比环节超时趋势(" + processname + ")']";
        viewDoc.s("nodename", nodename);
        viewDoc.s("value", value);
        viewDoc.s("processnodename", processnodename);
        viewDoc.s("processname1", processname);

        return "1";
    }

}