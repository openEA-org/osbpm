package cn.linkey.rulelib.S016;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:环节处理概率
 * @author admin
 * @version: 8.0
 * @Created: 2014-10-17 11:07
 */
final public class R_S016_E008 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        Document viewDoc = (Document) params.get("GridDoc"); //grid配置文档
        String eventName = (String) params.get("EventName");//事件名称
        String processid = BeanCtx.g("processid");
        if (eventName.equals("onViewOpen")) {
            return onViewOpen(viewDoc, processid);
        }
        return "1";
    }

    public String onViewOpen(Document viewDoc, String processid) throws Exception {
        //如果不返回1则表示退出本视图并输出返回的字符串
        //图表JsHeader中用[X]字段名[/X]标识,viewDoc.s("字段名","10,20,30")来动态输出计算好的数据,
        String nodename = "[";
        String value = "[";
        String countsql;
        //		System.out.println(processid);
        String sql = "select distinct nodename from BPM_InsUserList where processid='" + processid + "'";
        String processsql = "select nodename from bpm_modprocesslist where processid='" + processid + "'";

        String processname = Rdb.getDocumentBySql(processsql).g("nodename");
        Document[] document = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : document) {
            countsql = "select nodename from BPM_InsUserList where nodename='" + doc.g("nodename") + "' and processid='" + processid + "'";
            if (Rdb.getDocumentBySql(countsql).g("nodename").length() == 0) {
                doc.s("nodename", "环节名为空");
            }
            nodename = nodename + "'" + doc.g("nodename") + "',";
            value = value + "{value:" + Rdb.getCountBySql(countsql) + ",name:'" + doc.g("nodename") + "'},";
        }
        if (nodename.length() > 1) {
            nodename = nodename.substring(0, nodename.length() - 1);
            value = value.substring(0, value.length() - 1);
        }
        nodename = nodename + "]";
        value = value + "]";
        processname = "'" + processname + "'";
        viewDoc.s("process", processname);
        viewDoc.s("nodename", nodename);
        viewDoc.s("value", value);

        return "1";
    }

}