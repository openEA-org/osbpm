package cn.linkey.rulelib.S016;

import java.math.BigDecimal;
import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:环节及时概率按流程
 * @author admin
 * @version: 8.0
 * @Created: 2014-10-21 14:57
 */
final public class R_S016_E028 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        Document viewDoc = (Document) params.get("GridDoc"); //grid配置文档
        String eventName = (String) params.get("EventName");//事件名称
        String processid = BeanCtx.g("processid");
        //	String process=BeanCtx.g("processname");
        //	processid="f217a2fe03e52048d10ba0b0950eec57";
        //	processname="22";
        if (eventName.equals("onViewOpen")) {
            return onViewOpen(viewDoc, processid);
        }
        return "1";
    }

    public String onViewOpen(Document viewDoc, String processid) throws Exception {
        //如果不返回1则表示退出本视图并输出返回的字符串
        //图表JsHeader中用[X]字段名[/X]标识,viewDoc.s("字段名","10,20,30")来动态输出计算好的数据,
        String sql = "select distinct NodeName,COUNT(nodename) as num from BPM_InsRemarkList where Processid='" + processid + "' and OverTimeFlag='0' group by nodename";
        String sumsql = "select distinct NodeName,COUNT(nodename) as num from BPM_InsRemarkList where Processid='" + processid + "'  group by nodename";
        String process = Rdb.getDocumentBySql("select nodename from bpm_modprocesslist where processid='" + processid + "'").g("nodename");
        String value = "[";
        String nodename = "[";
        Document[] document = Rdb.getAllDocumentsBySql(sql);
        if (Rdb.getCountBySql(sql) > 0) {
            for (Document doc : document) {

                sumsql = "select distinct NodeName,COUNT(nodename) as num from BPM_InsRemarkList where Processid='" + processid + "' and nodename ='" + doc.g("nodename") + "' group by nodename";
                Document d = Rdb.getDocumentBySql(sumsql);
                if (doc.g("nodename").length() == 0)
                    doc.s("nodename", "环节为空");
                nodename = nodename + "'" + doc.g("nodename") + "',";
                value = value + "{value:" + new BigDecimal(Float.parseFloat(doc.g("num")) / Float.parseFloat(d.g("num"))).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue() + ",name:'"
                        + doc.g("nodename") + "'},";
            }

            if (nodename.length() > 1) {
                nodename = nodename.substring(0, nodename.length() - 1);
                value = value.substring(0, value.length() - 1);
            }
            process = "'" + process + "'";
            nodename = nodename + "]";
            value = value + "]";
        }
        else {
            process = "'" + process + "'";
            nodename = "['']";
            value = "['']";
        }
        viewDoc.s("process", process);
        viewDoc.s("nodename", nodename);
        viewDoc.s("value", value);
        return "1";
    }

}