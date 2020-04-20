package cn.linkey.rulelib.S016;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:流程返复次数最多的流程
 * @author admin
 * @version: 8.0
 * @Created: 2014-10-20 14:48
 */
final public class R_S016_E022 implements LinkeyRule {

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
        String sql = "	select processid,count from (select distinct processid,docunid,COUNT(*)as count  from   BPM_InsRemarkList group by processid ,docunid) as j order by count desc";
        String countsql;
        String processname = "[";
        String value = "[";
        String last = "";
        int i = 0;
        Document[] document = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : document) {

            countsql = "select nodename from bpm_modprocesslist where processid='" + doc.g("processid") + "'";
            Document d = Rdb.getDocumentBySql(countsql);
            if (!d.isNull() && !last.equals(doc.g("processid")) && i < 10 && processname.indexOf(d.g("nodename")) == -1) {
                processname = processname + "'" + d.g("nodename") + "',";
                value = value + doc.g("count") + ",";
                last = doc.g("processid");
                i = i + 1;
            }
        }
        processname = processname.substring(0, processname.length() - 1);
        value = value.substring(0, value.length() - 1);
        processname = processname + "]";
        value = value + "]";
        viewDoc.s("processname", processname);
        viewDoc.s("value", value);
        return "1";
    }

}