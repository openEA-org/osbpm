package cn.linkey.rulelib.S016;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.dao.SqlType;
import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:运行周期后10名
 * @author admin
 * @version: 8.0
 * @Created: 2014-10-20 11:40
 */
final public class R_S016_E017 implements LinkeyRule {

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
        
    	//20181019修改支持多数据库sql语句
    	//String sql = "select top 10 WF_processid,cast(avg(numb) as numeric(10,4)) as tmp from (SELECT wf_processid,convert(int,WF_TotalTime) as numb FROM BPM_ArchivedData where wf_totaltime is not null) as f group by Wf_processid  order by tmp desc";
        //String DBType = Tools.getProperty("DBType");
    	//20190328 改为自动获取当前数据库类型
    	String DBType = Rdb.getDbType();
    	String sql = SqlType.createSql(DBType, "R_S016_E017_SQL1");
    	
    	String countsql;
        String processname = "[";
        String value = "[";
        Document[] document = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : document) {

            countsql = "select nodename from bpm_modprocesslist where processid='" + doc.g("wf_processid") + "'";
            Document d = Rdb.getDocumentBySql(countsql);
            if (!d.isNull()) {
                processname = processname + "'" + d.g("nodename") + "',";
                value = value + doc.g("tmp") + ",";
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