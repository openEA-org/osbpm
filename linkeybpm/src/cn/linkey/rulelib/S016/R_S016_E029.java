package cn.linkey.rulelib.S016;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.dao.SqlType;
import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:流程及时概率按流程
 * @author admin
 * @version: 8.0
 * @Created: 2014-10-22 09:03
 */
final public class R_S016_E029 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        Document viewDoc = (Document) params.get("GridDoc"); // grid配置文档
        String eventName = (String) params.get("EventName");// 事件名称
        if (eventName.equals("onViewOpen")) {
            return onViewOpen(viewDoc);
        }
        return "1";
    }

    public String onViewOpen(Document viewDoc) throws Exception {
        // 如果不返回1则表示退出本视图并输出返回的字符串
        // 图表JsHeader中用[X]字段名[/X]标识,viewDoc.s("字段名","10,20,30")来动态输出计算好的数据,
        // String processsql="select distinct processid from BPM_InsRemarkList";
        // String totalsql="";
        // String numsql="";
        // String findnamesql="";
        // String nodename="[";
        // String value="[";
        // Document[] document=Rdb.getAllDocumentsBySql(processsql);
        // for(Document doc:document)
        // {
        // numsql="select processid,COUNT(*) as num from BPM_InsRemarkList where Processid='"+doc.g("processid")+"' and OverTimeFlag=0 group by processid";
        // totalsql="select processid,COUNT(*) as num from BPM_InsRemarkList where Processid='"+doc.g("processid")+"' group by processid";
        // findnamesql="select nodename from bpm_modprocesslist where processid='"+doc.g("processid")+"'";
        // if(doc.g("processid").length()!=0){
        // nodename=nodename+"'"+Rdb.getDocumentBySql(findnamesql).g("nodename")+"',";
        // value=value+"{value:"+new BigDecimal(Float.parseFloat(Rdb.getDocumentBySql(numsql).g("num"))/Float.parseFloat(Rdb.getDocumentBySql(totalsql).g("num"))).setScale(2,
        // BigDecimal.ROUND_HALF_UP).floatValue()+",name:'"+Rdb.getDocumentBySql(findnamesql).g("nodename")+"'},";
        // }
        // }
        // if(nodename.length()>1){
        // nodename=nodename.substring(0,nodename.length()-1);
        // value=value.substring(0,value.length()-1);}
        // nodename=nodename+"]";
        // value=value+"]";
        // viewDoc.s("nodename",nodename);
        // viewDoc.s("value",value);
        // return "1";
        
    	//20181019修改支持多数据库sql语句
    	//String sql = "select wf_ProcessName,DATEDIFF(HOUR,CONVERT(datetime,WF_LastModified),getdate()) as difftime from BPM_ArchivedData group by WF_ProcessName,WF_LastModified";
    	//String DBType = Tools.getProperty("DBType");
    	//20190328 改为自动获取当前数据库类型
    	String DBType = Rdb.getDbType();
    	String sql = SqlType.createSql(DBType, "R_S016_E029_SQL1");
    	
    	String processname = "[";
        String countsql = "";
        String value = "[";
        String exceedtimesql = "";
        Document[] document = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : document) {
            int count = 0;
            
            //20181019修改支持多数据库sql语句
            //sql = "select wf_ProcessName,DATEDIFF(HOUR,CONVERT(datetime,WF_LastModified),getdate()) as difftime from BPM_ArchivedData where wf_processname='" + doc.g("wf_processname")
            //        + "' group by WF_ProcessName,WF_LastModified";
            sql = SqlType.createSql(DBType, "R_S016_E029_SQL2", doc.g("wf_processname"));
            
            exceedtimesql = "select nodename,exceedtime from bpm_modprocesslist where nodename='" + doc.g("wf_processname") + "'";
            Document doc2 = Rdb.getDocumentBySql(exceedtimesql);
            for (Document doc1 : Rdb.getAllDocumentsBySql(sql)) {
                if (doc2.g("exceedtime").length() > 0 && !doc2.g("exceedtime").equals("0") && Integer.parseInt(doc1.g("difftime")) > Integer.parseInt(doc2.g("exceedtime"))) {
                    count++;
                }
            }
            if (processname.indexOf(doc.g("wf_processname")) == -1) {
                processname = processname + "'" + doc.g("wf_processname") + "',";
                value = value + "{value:" + (1 - (float) count / Rdb.getCountBySql(sql)) * 100 + ",name:'" + doc.g("wf_processname") + "'},";
                // System.out.println("count:"+count);
                // System.out.println("sum:"+Rdb.getCountBySql(sql));
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