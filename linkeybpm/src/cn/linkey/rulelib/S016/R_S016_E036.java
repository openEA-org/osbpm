package cn.linkey.rulelib.S016;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.dao.SqlType;
import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:流程运行耗时统计
 * @author admin
 * @version: 8.0
 * @Created: 2014-10-22 16:57
 */
final public class R_S016_E036 implements LinkeyRule {

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
    	//String sql = "SELECT wf_processid,avg(datediff(hour,Convert(datetime,WF_LastModified),getdate())) as time FROM BPM_MainData group by WF_Processid  order by time desc";
    	//String DBType = Tools.getProperty("DBType");
    	//20190328 改为自动获取当前数据库类型
    	String DBType = Rdb.getDbType();
    	String sql = SqlType.createSql(DBType, "R_S016_E036_SQL1");
    	
    	String value = "[";
        String processname = "[";
        String namesql = "";
        Document[] document = Rdb.getAllDocumentsBySql(sql);
        if (Rdb.getCountBySql(sql) > 0) {
            for (Document doc : document) {
                namesql = "select nodename from bpm_modprocesslist where processid='" + doc.g("Wf_processid") + "'";
                if (Rdb.getDocumentBySql(namesql).g("nodename").length() > 0) {
                    processname = processname + "'" + Rdb.getDocumentBySql(namesql).g("nodename") + "',";
                    value = value + "{value:" + doc.g("time") + ",name:'" + Rdb.getDocumentBySql(namesql).g("nodename") + "'},";

                }
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
        viewDoc.s("processname", processname);
        viewDoc.s("value", value);
        return "1";
    }

}