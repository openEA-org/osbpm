package cn.linkey.rulelib.S016;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.dao.SqlType;
import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:流程效率后10名
 * @author admin
 * @version: 8.0
 * @Created: 2014-10-20 10:34
 */
final public class R_S016_E013 implements LinkeyRule {

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
    	//String sql = "select top 10 WF_ProcessName,cast(sum(numb) as numeric(10,4))/(select COUNT(WF_ProcessName)  from BPM_ArchivedData where wf_processname=f.wf_processname group by WF_ProcessName) as tmp from (SELECT WF_ProcessName,datediff(hour,wf_doccreated,wf_endtime) as numb FROM BPM_ArchivedData ) as f group by WF_ProcessName order by tmp desc";
    	//String DBType = Tools.getProperty("DBType");
    	//20190328 改为自动获取当前数据库类型
    	String DBType = Rdb.getDbType();
    	String sql = SqlType.createSql(DBType, "R_S016_E013_SQL1");
    	
    	String processname = "[";
        String value = "[";
        Document[] document = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : document) {
            //countsql="select nodename from bpm_modprocesslist where processid='"+doc.g("processid")+"'";
            //Document d=Rdb.getDocumentBySql(countsql);
            if (doc.g("wf_processname").length() > 0) {
                processname = processname + "'" + doc.g("wf_processname") + "',";
                if (doc.g("tmp").indexOf("E") != -1) {
                    value = value + "0.00" + ",";
                }
                else
                    value = value + doc.g("tmp").substring(0, doc.g("tmp").indexOf(".") + 3) + ",";
            }
            //value=value+doc.g("tmp")+",";}
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