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
 * @RuleName:文件搜索
 * @author  admin
 * @version: 8.0
 * @Created: 2014-06-30 16:55
 */
final public class R_S017_B160 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
	    //示例参数:{"PageSize":"20","pageNo":"1","Appid":"*","SearchWord":""}
	   
	    String pageSize=BeanCtx.g("pageSize");
	    String pageNo=BeanCtx.g("pageNo");
	    String appid=BeanCtx.g("appid");
	    String subject=BeanCtx.g("subject");
	    String processId=BeanCtx.g("processId");
	    String processName=BeanCtx.g("processName");
	    String serialNum=BeanCtx.g("serialNum");
	    

	    if(Tools.isBlank(pageSize)){pageSize="20";}
	    if(Tools.isBlank(pageNo)){pageNo="1";}
	    
	    String aclSqlWhere=Rdb.getInReaderSql("WF_AllReaders");
	    String sql="select Subject,WF_AddName_CN,WF_Author_CN,WF_CurrentNodeName,WF_DocNumber,WF_ProcessName,WF_Processid,WF_OrUnid,WF_DocCreated from BPM_AllDocument where "+aclSqlWhere;
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
	    Document[] dc=Rdb.getAllDocumentsBySql("BPM_AllDocument",sql,Integer.valueOf(pageNo),Integer.valueOf(pageSize));
	    String jsonStr="{\"total\":"+totalNum+",\"rows\":"+Documents.dc2json(dc,"")+"}";
	    jsonStr=RestUtil.formartResultJson("1", "", jsonStr);
	    return jsonStr;
	    
	}
}