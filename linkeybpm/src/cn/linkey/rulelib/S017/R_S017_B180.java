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
 * @RuleName:查询已归档流程
 * @author  admin
 * @version: 1.0
 */
final public class R_S017_B180 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
	    String pageSize=BeanCtx.g("pageSize");
	    String pageNo=BeanCtx.g("pageNo");
	    String appid=BeanCtx.g("appid");
	    String keyWord=BeanCtx.g("keyWord");
	    String processId=BeanCtx.g("processId");
	    String processName=BeanCtx.g("processName");
	    String folderId=BeanCtx.g("folderId");
	    
	    if(Tools.isBlank(pageSize)){pageSize="20";}
	    if(Tools.isBlank(pageNo)){pageNo="1";}
	    
	    String sqlWhere=Rdb.getInReaderSql("WF_AllReaders");
	    String sql="select WF_DocNumber,Subject,WF_Author_CN,WF_AddName_CN,WF_DocCreated,WF_ProcessName,WF_CurrentNodeName,WF_OrUnid,WF_Folderid from BPM_ArchivedData where "+sqlWhere;

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
	    //增加搜索条件folderId
	    if(Tools.isNotBlank(folderId)){
	        sql+=" and WF_Folderid = '%"+folderId+"%'";
	    }
	    //增加排序功能
	    sql+=" order by WF_DocCreated desc";
	    
	    String totalNum=String.valueOf(Rdb.getCountBySql(sql));
	    Document[] dc=Rdb.getAllDocumentsBySql("BPM_ArchivedData",sql,Integer.valueOf(pageNo),Integer.valueOf(pageSize));

	    String jsonStr="{\"total\":"+totalNum+",\"rows\":"+Documents.dc2json(dc,"")+"}";
	    jsonStr=RestUtil.formartResultJson("1", "", jsonStr);
	    
	    return jsonStr;
	    
	}
}