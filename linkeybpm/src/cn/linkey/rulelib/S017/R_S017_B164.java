package cn.linkey.rulelib.S017;

import java.util.*;

import cn.linkey.dao.*;
import cn.linkey.util.*;
import cn.linkey.doc.*;
import cn.linkey.factory.*;
import cn.linkey.wf.*;
import cn.linkey.rest.RestUtil;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.org.LinkeyUser;
/**
 * @RuleName:我委托的任务列表
 * @author  admin
 * @version: 1.0
 */
final public class R_S017_B164 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
		String tableName="BPM_MainData";
		
	    String pageSize=BeanCtx.g("pageSize");
	    String pageNo=BeanCtx.g("pageNo");
	    String appid=BeanCtx.g("appid");
	    String subject=BeanCtx.g("subject");
	    String processId=BeanCtx.g("processId");
	    String processName=BeanCtx.g("processName");
	    String serialNum=BeanCtx.g("serialNum");
	    

	    if(Tools.isBlank(pageSize)){pageSize="20";}
	    if(Tools.isBlank(pageNo)){pageNo="1";}
	    
        String sqlWhere = "";
        if (Rdb.getDbType().equals("MSSQL")) {
            sqlWhere = "','+WF_SourceEntrustUserid+',' like '%," + BeanCtx.getUserid() + ",%'";
        }
        else if (Rdb.getDbType().equals("MYSQL")) {
            sqlWhere = "concat(',',WF_SourceEntrustUserid,',') like '%," + BeanCtx.getUserid() + ",%'";
        }
        else {
            sqlWhere = "','||WF_SourceEntrustUserid||',' like '%," + BeanCtx.getUserid() + ",%'";
        }
        
	    String sql="select Subject,WF_AddName_CN,WF_Author_CN,WF_CurrentNodeName,WF_DocNumber,WF_ProcessName,WF_Processid,WF_OrUnid,WF_DocCreated from "+tableName+" where "+sqlWhere;
	    if(Tools.isNotBlank(appid)){
	        sql+=" and WF_Appid='"+appid+"'";
	    }
	    //增加搜索条件WF_ProcessId
	    if(Tools.isNotBlank(processId)){
	        sql+=" and WF_ProcessId like '%"+processId+"%'";
	    }
	    //增加搜索条件WF_ProcessName
	    if(Tools.isNotBlank(processName)){
	        sql+=" and WF_ProcessName like '%"+processName+"%'";
	    }
	    //增加搜索条件subject
	    if(Tools.isNotBlank(subject)){
	        sql+=" and subject like '%"+subject+"%'";
	    }
	    //增加搜索条件编号
	    if(Tools.isNotBlank(serialNum)){
	        sql+=" and WF_DocNumber like '%"+serialNum+"%'";
	    }
	    //增加排序功能
	    sql+=" order by WF_DocCreated desc";
	    
	    String totalNum=String.valueOf(Rdb.getCountBySql(sql));
	    Document[] dc=Rdb.getAllDocumentsBySql(tableName,sql,Integer.valueOf(pageNo),Integer.valueOf(pageSize));
	    //格式化已到达的总时间
	    for(Document doc:dc){
	        String startTime=doc.g("WF_DocCreated");
    		String difTime=DateUtil.getAllDifTime(startTime, DateUtil.getNow());
    		int min=Integer.valueOf(difTime);
    		if(min>60){
    			difTime=String.valueOf(min/60)+"(小时)";
    		}else{
    			difTime=min+"(分钟)";
    		}
    		doc.s("TotalTime",difTime);
	    }
	    
	    String jsonStr="{\"total\":"+totalNum+",\"rows\":"+Documents.dc2json(dc,"")+"}";
	    jsonStr=RestUtil.formartResultJson("1", "", jsonStr);
	    return jsonStr;
	    
	}
}