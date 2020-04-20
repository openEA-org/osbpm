package cn.linkey.rulelib.S016;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:流程运行数量统计
 * @author admin
 * @version: 8.0
 * @Created: 2014-10-22 11:04
 */
final public class R_S016_E033 implements LinkeyRule {

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
        String sql = "select distinct WF_Processid,count(wf_processid) as num from BPM_MainData group by wf_processid order by num desc";
        String value = "[";
        String nodename = "[";
        String namesql = "";

        Document[] document = Rdb.getAllDocumentsBySql(sql);
        if (Rdb.getCountBySql(sql) > 0) {
            for (Document doc : document) {
                namesql = "select nodename from bpm_modprocesslist where processid='" + doc.g("Wf_processid") + "'";
                if (Rdb.getDocumentBySql(namesql).g("nodename").length() > 0) {
                    nodename = nodename + "'" + Rdb.getDocumentBySql(namesql).g("nodename") + "',";
                    value = value + "{value:" + doc.g("num") + ",name:'" + Rdb.getDocumentBySql(namesql).g("nodename") + "'},";

                }
            }
            nodename = nodename.substring(0, nodename.length() - 1);
            value = value.substring(0, value.length() - 1);
            nodename = nodename + "]";
            value = value + "]";
        }
        else {
            nodename = "['']";
            value = "['']";
        }
        viewDoc.s("nodename", nodename);
        viewDoc.s("value", value);
        return "1";
    }

}