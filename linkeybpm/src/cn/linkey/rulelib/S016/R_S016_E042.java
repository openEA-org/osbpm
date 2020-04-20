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
 * @RuleName:环比流程超时趋势
 * @author admin
 * @version: 8.0
 * @Created: 2014-10-24 15:21
 */
final public class R_S016_E042 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        Document viewDoc = (Document) params.get("GridDoc"); //grid配置文档
        String eventName = (String) params.get("EventName");//事件名称
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
        //如果不返回1则表示退出本视图并输出返回的字符串
        //图表JsHeader中用[X]字段名[/X]标识,viewDoc.s("字段名","10,20,30")来动态输出计算好的数据,
        String starttime = "";
        String endtime = "";
        String starttime1 = "";
        String endtime1 = "";
        String nodename = "[";
        String value = "[";
        String value1 = "[";
        String mainsql1 = "";
        String archsql1 = "";
        String title = "";
        String lable1 = "";
        String lable2 = "";
        String processsql = "";
        String mainsql = "";
        String archsql = "";
        String s1 = "";
        String s2 = "";
        for (int i = 1; i <= 12; i++) {
            int num = 0;
            int timeoutnum = 0;
            int num1 = 0;
            int timeoutnum1 = 0;
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
            
            //20181019修改支持多数据库sql语句
            //mainsql = "select wf_processid,WF_DocCreated,DATEDIFF(HOUR,CONVERT(datetime,wf_doccreated),getdate()) as difftime from BPM_MainData where WF_DocCreated between '" + starttime + "' and '"
            //        + endtime + "'";
            //archsql = "select wf_processid,WF_DocCreated,DATEDIFF(HOUR,CONVERT(datetime,wf_doccreated),getdate()) as difftime from BPM_ArchivedData where WF_DocCreated between '" + starttime
            //        + "' and '" + endtime + "'";
            //mainsql1 = "select wf_processid,WF_DocCreated,DATEDIFF(HOUR,CONVERT(datetime,wf_doccreated),getdate()) as difftime from BPM_MainData where WF_DocCreated between '" + starttime1 + "' and '"
            //        + endtime1 + "'";
            //archsql1 = "select wf_processid,WF_DocCreated,DATEDIFF(HOUR,CONVERT(datetime,wf_doccreated),getdate()) as difftime from BPM_ArchivedData where WF_DocCreated between '" + starttime1
            //        + "' and '" + endtime1 + "'";
            //String DBType = Tools.getProperty("DBType");
        	//20190328 改为自动获取当前数据库类型
        	String DBType = Rdb.getDbType();
        	mainsql = SqlType.createSql(DBType, "R_S016_E042_mainsql", starttime, endtime);
        	archsql = SqlType.createSql(DBType, "R_S016_E042_archsql", starttime, endtime);
        	mainsql1 = SqlType.createSql(DBType, "R_S016_E042_mainsql1", starttime1, endtime1);
        	archsql1 = SqlType.createSql(DBType, "R_S016_E042_archsql1", starttime1, endtime1);
            
            //System.out.println("sql:"+sql);
            //  System.out.println("sql1:"+sql1);
            num = (Rdb.getCountBySql(mainsql) + Rdb.getCountBySql(archsql));
            num1 = (Rdb.getCountBySql(mainsql1) + Rdb.getCountBySql(archsql1));
            timeoutnum = findnum(mainsql) + findnum(archsql);
            timeoutnum1 = findnum(mainsql1) + findnum(archsql1);
            //System.out.println(starttime1.split("-")[0]+"-"+starttime1.split("-")[1]+":总的"+num1+"超时的："+timeoutnum1);
            nodename = nodename + "'" + year + "-" + i + "',";
            value = value + "{value:" + calculate(timeoutnum + "", num + "") + ",name:'" + starttime.substring(0, 7) + "'},";
            //s1=s1+","+calculate(timeoutnum+"",num+"");
            //s2=s2+","+calculate(timeoutnum1+"",num1+"");
            value1 = value1 + "{value:" + calculate(calculate(timeoutnum1 + "", num1 + "") - calculate(timeoutnum + "", num + "") + "", calculate(timeoutnum + "", num + "") + "") + ",name:'"
                    + starttime1.substring(0, 7) + "'},";
        }
        if (nodename.length() > 1) {
            nodename = nodename.substring(0, nodename.length() - 1);
            value1 = value1.substring(0, value1.length() - 1);
            value = value.substring(0, value.length() - 1);
        }
        nodename = nodename + "]";
        value = value + "]";
        value1 = value1 + "]";
        viewDoc.s("nodename", nodename);
        viewDoc.s("value1", value1);
        //	System.out.println("nodename:"+nodename);
        //	System.out.println("value:"+value);
        //	System.out.println("value1:"+value1);
        //	System.out.println("s1:"+s1);
        //	System.out.println("s2:"+s2);
        return "1";
    }

    public int findnum(String sql) {
        int timeoutnum = 0;

        for (Document doc : Rdb.getAllDocumentsBySql(sql)) {
            if (Rdb.getCountBySql(sql) > 0) {
                String processsql = "select exceedtime from bpm_modprocesslist where processid='" + doc.g("Wf_processid") + "'";
                String exceedtime = Rdb.getDocumentBySql(processsql).g("exceedtime");
                // System.out.println("exceedtime:"+exceedtime);

                if (!exceedtime.equals("0") && Rdb.getCountBySql(processsql) > 0 && Integer.parseInt(doc.g("difftime")) > Integer.parseInt(exceedtime)) {
                    //System.out.println("sql"+sql);   System.out.println("processsql"+processsql); 
                    //System.out.println("exceedtime:"+exceedtime);
                    //System.out.println("超时");
                    timeoutnum++;
                }
            }
        }
        return timeoutnum;
    }

    public float calculate(String num, String num1) {
        num = num + "";
        num1 = num1 + "";
        float value;
        if (Float.parseFloat(num) != 0 && Float.parseFloat(num1) != 0) {
            value = (Float.parseFloat(num)) / Float.parseFloat(num1) * 100;
        }
        else if (Float.parseFloat(num) == 0 && Float.parseFloat(num1) != 0) {
            value = 0;
        }
        else if (Float.parseFloat(num) != 0 && Float.parseFloat(num1) == 0) {
            value = -100;
        }
        else
            value = 0;

        return value;
    }

}