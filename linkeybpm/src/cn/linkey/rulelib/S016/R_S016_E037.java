package cn.linkey.rulelib.S016;

import java.util.Calendar;
import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;

/**
 * @RuleName:同比流程办结趋势
 * @author admin
 * @version: 8.0
 * @Created: 2014-10-23 09:54
 */
final public class R_S016_E037 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        Document viewDoc = (Document) params.get("GridDoc"); // grid配置文档
        String eventName = (String) params.get("EventName");// 事件名称
        String year = BeanCtx.g("year");
        // year = "2014";
        
        // 20190108 修改写死的年份为获取当前系统的年份
        year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        // 20190108 END
        
        if (eventName.equals("onViewOpen")) {
            return onViewOpen(viewDoc, year);
        }
        return "1";
    }

    public String onViewOpen(Document viewDoc, String year) throws Exception {
        // 如果不返回1则表示退出本视图并输出返回的字符串
        // 图表JsHeader中用[X]字段名[/X]标识,viewDoc.s("字段名","10,20,30")来动态输出计算好的数据,
        String now = DateUtil.getNow("yyyy-MM-dd");
        String nowyear = now.split("-")[0];
        String nowmonth = now.split("-")[1];
        String starttime = "";
        String endtime = "";
        String starttime1 = "";
        String endtime1 = "";
        String nodename = "[";
        String value = "[";
        // System.out.println(now);
        String sql1 = "";
        String num = "";
        String num1 = "";
        String sql = "select COUNT(wf_processid) as num from BPM_ArchivedData where WF_LastModified between '" + starttime + "' and '" + endtime + "' ";
        for (int i = 1; i <= 12; i++) {
            if (i < 10) {
                starttime = Integer.parseInt(year) - 1 + "-" + "0" + i + "-01";
                endtime = Integer.parseInt(year) - 1 + "-" + "0" + i + "-31";
                starttime1 = year + "-" + "0" + i + "-01";
                endtime1 = year + "-" + "0" + i + "-31";
            }
            else {
                starttime = Integer.parseInt(year) - 1 + "-" + i + "-01";
                endtime = Integer.parseInt(year) - 1 + "-" + i + "-31";
                starttime1 = year + "-" + i + "-01";
                endtime1 = year + "-" + i + "-31";
            }
            sql = "select COUNT(wf_processid) as num from BPM_ArchivedData where WF_LastModified between '" + starttime + "' and '" + endtime + "' ";
            sql1 = "select COUNT(wf_processid) as num from BPM_ArchivedData where WF_LastModified between '" + starttime1 + "' and '" + endtime1 + "'";
            // System.out.println("sql:"+sql);
            // System.out.println("sql1:"+sql1);
            nodename = nodename + "'" + starttime1.split("-")[0] + "-" + starttime1.split("-")[1] + "',";
            num = Rdb.getDocumentBySql(sql).g("num");
            num1 = Rdb.getDocumentBySql(sql1).g("num");
            // if(num.length()==0)num="0";
            // if(num1.length()==0)num1="0";
            if (!num.equals("0") && !num1.equals("0")) {
                value = value + "{value:" + (Integer.parseInt(num1) - Integer.parseInt(num)) / Integer.parseInt(num) * 100 + ",name:'" + starttime1.split("-")[0] + "-" + starttime1.split("-")[1]
                        + "'},";
            }
            else if (num.equals("0") && !num1.equals("0")) {
                value = value + "{value:" + "100" + ",name:'" + starttime1.split("-")[0] + "-" + starttime1.split("-")[1] + "'},";
            }
            else if (!num.equals("0") && num1.equals("0")) {
                value = value + "{value:" + "-100" + ",name:'" + starttime1.split("-")[0] + "-" + starttime1.split("-")[1] + "'},";
            }
            else
                value = value + "{value:" + "0" + ",name:'" + starttime1.split("-")[0] + "-" + starttime1.split("-")[1] + "'},";
        }
        if (nodename.length() > 1) {
            nodename = nodename.substring(0, nodename.length() - 1);
            value = value.substring(0, value.length() - 1);
        }
        nodename = nodename + "]";
        value = value + "]";
        viewDoc.s("nodename", nodename);
        viewDoc.s("value", value);
        // System.out.println("nodename"+nodename);
        // System.out.println("value"+value);
        return "1";
    }

}