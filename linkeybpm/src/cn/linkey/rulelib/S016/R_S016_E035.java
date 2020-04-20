package cn.linkey.rulelib.S016;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;

/**
 * @RuleName:所有已超时流程
 * @author admin
 * @version: 8.0
 * @Created: 2014-10-22 14:26
 */
final public class R_S016_E035 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        Document viewDoc = (Document) params.get("GridDoc"); //grid配置文档
        String eventName = (String) params.get("EventName");//事件名称
        if (eventName.equals("onViewOpen")) {
            return onViewOpen(viewDoc);
        }
        return "1";
    }

    public String onViewOpen(Document viewDoc) throws Exception {
        //如果不返回1则表示退出本视图并输出返回的字符串
        //图表JsHeader中用[X]字段名[/X]标识,viewDoc.s("字段名","10,20,30")来动态输出计算好的数据,
        //String sql="select distinct processid,starttime from BPM_InsNodeList where Status ='current' group by processid,starttime order by StartTime asc";
        String sql = "select WF_Processid,WF_DocCreated from BPM_MainData";
        String processname = "[";
        String value = "[";
        String temppro = "";
        String timesql = "";
        String exceedtime = "";
        String starttime = "";
        String totaltime = "";
        boolean ishas = false;
        Document[] document = Rdb.getAllDocumentsBySql(sql);
        if (Rdb.getCountBySql(sql) > 0) {

            for (Document doc : document) {
                timesql = "select exceedtime,nodename from BPM_ModProcessList where Processid='" + doc.g("wf_processid") + "'";
                //System.out.println("sql"+timesql);
                exceedtime = Rdb.getDocumentBySql(timesql).g("exceedtime");
                //  System.out.print("Processid"+doc.g("processid"));
                //  System.out.println("exceedtime.length:"+Rdb.getDocumentBySql(timesql).g("exceedtime"));
                if (Rdb.getCountBySql(timesql) > 0 && Integer.parseInt(exceedtime) > 0 && processname.indexOf(Rdb.getDocumentBySql(timesql).g("nodename")) == -1) {
                    starttime = doc.g("WF_DocCreated");
                    totaltime = DateUtil.getDifTime(starttime, DateUtil.getNow());
                    //System.out.println(totaltime);
                    //System.out.println("exceedtime:"+exceedtime);
                    if (Integer.parseInt(totaltime) > Integer.parseInt(exceedtime)) {
                        //System.out.println("chao shi");
                        processname = processname + "'" + Rdb.getDocumentBySql(timesql).g("nodename") + "',";
                        value = value + "{value:1" + ",name:'" + Rdb.getDocumentBySql(timesql).g("nodename") + "'},";
                        ishas = true;
                    }
                }
                temppro = Rdb.getDocumentBySql(timesql).g("nodename");
            }
            processname = processname.substring(0, processname.length() - 1);
            value = value.substring(0, value.length() - 1);
            processname = processname + "]";
            value = value + "]";
        }
        else {
            processname = "['']";
            value = "['']";
        }
        if (!ishas) {
            processname = "['']";
            value = "['']";
        }
        viewDoc.s("processname", processname);
        viewDoc.s("value", value);
        return "1";
    }

}