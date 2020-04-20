package cn.linkey.rulelib.S016;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.dao.SqlType;
import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:流程运行天数分布状态
 * @author admin
 * @version: 8.0
 * @Created: 2014-10-22 11:34
 */
final public class R_S016_E034 implements LinkeyRule {

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
        //String sql = "select distinct wf_processid,wf_doccreated,WF_LastModified,datediff(day,CONVERT(datetime,wf_doccreated),CONVERT(datetime,WF_LastModified)) as num from BPM_MainData order by num desc";
    	//String DBType = Tools.getProperty("DBType");
    	//20190328 改为自动获取当前数据库类型
    	String DBType = Rdb.getDbType();
    	String sql = SqlType.createSql(DBType, "R_S016_E034_SQL1");
        
        String processsql = "";
        Document[] document = Rdb.getAllDocumentsBySql(sql);
        String temppro = "";
        String tempnum = "";
        String nodename = "[";
        String value = "[";
        if (Rdb.getCountBySql(sql) > 0) {
            for (Document doc : document) {
            	processsql = "select nodename from bpm_modprocesslist where processid='" + doc.g("wf_processid") + "'";

                if (Rdb.getDocumentBySql(processsql).g("nodename").length() != 0 && (!temppro.equals(doc.g("wf_processid")) || !tempnum.equals(doc.g("num")))) {
                    //  if(!tempnum.equals(doc.g("num")))
                    //  {
                    value = value + "'" + Rdb.getDocumentBySql(processsql).g("nodename") + "',";
                    //}
                    nodename = nodename + "{value:'" + doc.g("num") + "',name:'" + Rdb.getDocumentBySql(processsql).g("nodename") + "'},";
                }
                temppro = doc.g("wf_processid");
                tempnum = doc.g("num");
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
        viewDoc.s("nodename", nodename);
        viewDoc.s("value", value);
        //System.out.println(nodename);
        //System.out.println(value);
        return "1";
    }

}