package cn.linkey.rulelib.S016;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.dao.SqlType;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:流程及环节平均耗时
 * @author admin
 * @version: 8.0
 * @Created: 2014-10-21 10:11
 */
final public class R_S016_E026 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        Document viewDoc = (Document) params.get("GridDoc"); //grid配置文档
        String eventName = (String) params.get("EventName");//事件名称
        String processid = BeanCtx.g("processid");
        //processid="f217a2fe03e52048d10ba0b0950eec57";
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
        //String sql = "select avg(convert(int,diftime))as time,nodename,COUNT(nodename)as num from BPM_InsRemarkList where Processid='" + processid + "' group by NodeName";
        //String DBType = Tools.getProperty("DBType");
    	//20190328 改为自动获取当前数据库类型
    	String DBType = Rdb.getDbType();
        String sql = SqlType.createSql(DBType, "R_S016_E026_SQL1", processid);
        
        String processsql = "select nodename from bpm_modprocesslist where processid='" + processid + "'";
        String processname = Rdb.getDocumentBySql(processsql).g("nodename");
        Document[] document = Rdb.getAllDocumentsBySql(sql);
        if (Rdb.getCountBySql(sql) > 0) {
            for (Document doc : document) {
                if (doc.g("nodename").length() == 0)
                    doc.s("nodename", "环节为空");
                nodename = nodename + "'" + doc.g("nodename") + "',";
                value = value + "{value:" + doc.g("time") + ",name:'" + doc.g("nodename") + "'},";
            }
            if (nodename.length() > 1) {
                nodename = nodename.substring(0, nodename.length() - 1);
                value = value.substring(0, value.length() - 1);
            }
            nodename = nodename + "]";
            value = value + "]";
        }
        else {
            nodename = "['']";
            value = "['']";
        }
        processname = "'" + processname + "'";
        viewDoc.s("process", processname);
        viewDoc.s("nodename", nodename);
        viewDoc.s("value", value);
        //System.out.println("process:"+processname);
        //System.out.println("nodename"+nodename);
        //System.out.println("value"+value);
        return "1";
    }

}