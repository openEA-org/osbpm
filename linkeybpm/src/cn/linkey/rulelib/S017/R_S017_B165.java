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
 * @RuleName:我的草稿列表
 * @author  admin
 * @version: 1.0
 */
final public class R_S017_B165 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
	    String tableName="BPM_MainData";
	    
	    String pageSize=BeanCtx.g("pageSize");
	    String pageNo=BeanCtx.g("pageNo");
	    String appid=BeanCtx.g("appId");
	    String keyWord=BeanCtx.g("keyWord");
	    String processId=BeanCtx.g("processId");
	    String processName=BeanCtx.g("processName");
	    

	    if(Tools.isBlank(pageSize)){pageSize="20";}
	    if(Tools.isBlank(pageNo)){pageNo="1";}
	    

	    String sql="select Subject,WF_AddName_CN,WF_Author_CN,WF_CurrentNodeName,WF_DocCreated,WF_DocNumber,WF_ProcessName,WF_Processid,WF_OrUnid from "+tableName+" where WF_AddName='"+BeanCtx.getUserid()+"' and (WF_Status='Draft' or WF_Status='Pause')";
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
	    if(Tools.isNotBlank(keyWord)){
	        sql+=" and subject like '%"+keyWord+"%'";
	    }
	    //增加排序功能
	    sql+=" order by WF_DocCreated desc";

	    
	    String totalNum=String.valueOf(Rdb.getCountBySql(sql));
	    Document[] dc=Rdb.getAllDocumentsBySql(tableName,sql,Integer.valueOf(pageNo),Integer.valueOf(pageSize));
	    String jsonStr="{\"total\":"+totalNum+",\"rows\":"+Documents.dc2json(dc,"")+"}";
	    jsonStr=RestUtil.formartResultJson("1", "", jsonStr);
	    return jsonStr;
	    
	}
}