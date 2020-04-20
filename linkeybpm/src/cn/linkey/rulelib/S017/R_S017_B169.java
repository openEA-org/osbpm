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
 * @RuleName:查询可申请的流程列表
 * @author  admin
 * @version: 1.0
 */
final public class R_S017_B169 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
	    
	    String appId=BeanCtx.g("appId");
	    String processName=BeanCtx.g("processName");
	    
	    
	    Document[] dc=getProcessList(appId,processName);
	    String jsonStr="{\"total\":"+dc.length+",\"rows\":"+Documents.dc2json(dc,"")+"}";
	    jsonStr=RestUtil.formartResultJson("1", "", jsonStr);
	    return jsonStr;
	    
	}
	
	public Document[] getProcessList(String appId,String processName){
		//获得所有已发布的流程列表
		String sql="";
		if(Tools.isBlank(appId)){
			sql="select NodeName,Processid from BPM_ModProcessList where Status='1'";
		}else{
			sql="select NodeName,Processid from BPM_ModProcessList where WF_Appid='"+appId+"' and Status='1'";
		}
		if(Tools.isNotBlank(processName)){
			sql+=" and NodeName like '%"+processName+"%'";
		}
		
		Document[] dc=Rdb.getAllDocumentsBySql(sql);
		for(Document doc:dc){
			if(BeanCtx.getLinkeyUser().inRoles(BeanCtx.getUserid(), doc.g("ProcessStarter"))){
				//获得本流程的待办总数
			    String num_sql="select * from BPM_UserTodo where WF_Processid='"+doc.g("Processid")+"'and Userid='"+BeanCtx.getUserid()+"'";
			    int count=Rdb.getCountBySql(num_sql);
			    doc.s("todoCount", count);
			    
			}
		}
		return dc;
	}
	
}