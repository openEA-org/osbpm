package cn.linkey.rulelib.S016;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:环节超时概率
 * @author admin
 * @version: 8.0
 * @Created: 2014-10-20 17:06
 */
final public class R_S016_E025 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        Document viewDoc = (Document) params.get("GridDoc"); // grid配置文档
        String eventName = (String) params.get("EventName");// 事件名称
        String processid = BeanCtx.g("processid");
        // processid="f217a2fe03e52048d10ba0b0950eec57";
        if (eventName.equals("onViewOpen")) {
            return onViewOpen(viewDoc, processid);
        }
        return "1";
    }

    public String onViewOpen(Document viewDoc, String processid) throws Exception {
        // 如果不返回1则表示退出本视图并输出返回的字符串
        // 图表JsHeader中用[X]字段名[/X]标识,viewDoc.s("字段名","10,20,30")来动态输出计算好的数据,
        String nodenamevalue = "[";
        String value = "[";
        String countsql = "";
        String sql = "select distinct NodeName,COUNT(nodename) as nodenamesum from bpm_insremarklist where processid='" + processid + "' and overtimeflag<>0 group by nodename";
        String processsql = "select nodename from bpm_modprocesslist where processid='" + processid + "'";
        String nodesql = "select distinct nodename,count(nodename) as sum ,overtimeflag from bpm_insremarklist where processid='" + processid + "' and overtimeflag=0 group by nodename,overtimeflag";
        String nodename = "[";
        String processname = Rdb.getDocumentBySql(processsql).g("nodename");
        Document[] document = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : document) {
            // countsql="select distinct nodename,count (nodename) as sum ,overtimeflag from bpm_insremarklist where processid='"+doc.g("processid")+"' and overtimeflag<>0 group by
            // nodename,overtimeflag";
            // countsql="select distinct NodeName,COUNT(nodename) as nodenamesum from bpm_insremarklist where processid='"+doc.g("processid")+"' and overtimeflag<>0 group by nodename";
            // nodesql="select distinct nodename,count (nodename) as sum ,overtimeflag from bpm_insremarklist where processid='"+doc.g("processid")+"' and overtimeflag=0 group by
            // nodename,overtimeflag";
            // if(Rdb.getDocumentBySql(countsql).g("nodename").length()==0){doc.s("nodename","环节名为空");}
            // if(Rdb.getDocumentBySql(nodesql).g("nodename").length()==0){doc.s("nodename","环节名为空");}
            // nodename=nodename+"'"+doc.g("nodename")+"',";
            // nodenamevalue=nodenamevalue+"{value:"+Rdb.getDocumentBySql(nodesql).g("sum")+",name:'"+Rdb.getDocumentBySql(nodesql).g("nodename")+"'},";
            value = value + "{value:" + doc.g("nodenamesum") + ",name:'" + doc.g("nodename") + "'},";
        }
        Document[] document1 = Rdb.getAllDocumentsBySql(nodesql);
        for (Document doc1 : document1) {
            if (doc1.g("nodename").length() == 0)
                doc1.s("nodename", "流程名为空");
            nodename = nodename + "'" + doc1.g("nodename") + "',";

            nodenamevalue = nodenamevalue + "{value:" + doc1.g("sum") + ",name:'" + doc1.g("nodename") + "'},";
        }
        if (nodenamevalue.length() > 1) {
            nodenamevalue = nodenamevalue.substring(0, nodenamevalue.length() - 1);
            nodename = nodename.substring(0, nodename.length() - 1);
            if (value.length() > 1) {
            	value = value.substring(0, value.length() - 1);
            }
        }
        nodenamevalue = nodenamevalue + "]";
        nodename = nodename + "]";
        value = value + "]";
        processname = "'" + processname + "'";
        viewDoc.s("process", processname);
        viewDoc.s("nodename", nodename);
        viewDoc.s("nodenamevalue", nodenamevalue);
        viewDoc.s("value", value);

        return "1";
    }

}