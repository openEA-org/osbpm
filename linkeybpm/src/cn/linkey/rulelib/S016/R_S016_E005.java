package cn.linkey.rulelib.S016;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:办结流程总数统计
 * @author admin
 * @version: 8.0
 * @Created: 2014-10-16 17:34
 */
final public class R_S016_E005 implements LinkeyRule {

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
        //图表JsHeader中用[X]字段名[/X]标识,viewDoc.s("字段名","10,20,30")来动态输出计算好的数据,
        String processsql = "select distinct WF_ProcessName from BPM_ArchivedData";
        String countsql = "";
        String processname = "[";
        String count = "[";
        Document[] document = Rdb.getAllDocumentsBySql(processsql);
        for (Document doc : document) {
            countsql = "select WF_ProcessName from BPM_ArchivedData where WF_ProcessName='" + doc.g("WF_ProcessName") + "'";
            count = count + "{value:" + Rdb.getCountBySql(countsql) + ",name:'" + doc.g("WF_Processname") + "'},";
            processname = processname + "'" + doc.g("WF_Processname") + "',";
        }
        count = count.substring(0, count.length() - 1);
        processname = processname.substring(0, processname.length() - 1);
        count = count + "]";
        processname = processname + "]";
        viewDoc.s("processname", processname);
        viewDoc.s("count", count);
        return "1";
    }

}