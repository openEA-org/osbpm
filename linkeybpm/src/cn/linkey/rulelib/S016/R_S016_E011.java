package cn.linkey.rulelib.S016;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:流程效率前10名
 * @author admin
 * @version: 8.0
 * @Created: 2014-10-17 14:35
 */
final public class R_S016_E011 implements LinkeyRule {

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
        // processid="af0ad0d1061e104ab80a1360382e3551a738";
        String sql = "select distinct processid from bpm_modProcesslist ";
        String countsql;
        String processsql;
        String now;
        String diftime;
        String processname = "[";
        String value = "[";
        int i = 0;
        Map<String, Integer> numbers = new HashMap<String, Integer>();
        Document[] document = Rdb.getAllDocumentsBySql(sql);
        int length = Rdb.getCountBySql(sql);
        long[] array = new long[10];
        for (Document doc : document) {
            countsql = "select * from BPM_InsRemarkList where processid='" + doc.g("processid") + "'";
            processsql = "select nodename from bpm_modprocesslist where processid='" + doc.g("processid") + "'";
            //  System.out.println("processsql:"+processsql);
            Document[] docu = Rdb.getAllDocumentsBySql(countsql);
            int totaltime = 0;
            if (Rdb.getCountBySql(countsql) > 0) {
                for (Document doc1 : docu) {
                    totaltime = Integer.parseInt(doc1.g("diftime")) + totaltime;
                }

                numbers.put(Rdb.getDocumentBySql(processsql).g("nodename"), totaltime / Rdb.getCountBySql(countsql));
            }

        }
        Iterator it = numbers.keySet().iterator();
        while (it.hasNext()) {
            Object key = it.next();
            //             System.out.println("key:"+key);  
            processname = processname + "'" + key + "',";
            //             System.out.println("value:"+numbers.get(key));  
            value = value + numbers.get(key) + ",";
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