package cn.linkey.rulelib.S018;

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
 * @RuleName:获取规则清单
 * @author  admin
 * @version: 8.0
 * @Created: 2017-09-18 09:41:28
 */
final public class R_S018_B004 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    String folderid=BeanCtx.g("folderid");
	    String searchWord=BeanCtx.g("keyWord");
	    String pageSize=BeanCtx.g("pageSize");
	    String pageNo=BeanCtx.g("pageNo");
	    String appid=BeanCtx.g("appId");
	    
	    if(Tools.isBlank(pageSize)){pageSize="20";}
	    if(Tools.isBlank(pageNo)){pageNo="1";}
	    
	    String sql="select RuleNum,WF_OrUnid,RuleName ,WF_Appid,WF_LastModified,CompileFlag,EventType,WF_AddName_CN,Status from BPM_RuleList";
	    
	    //按应用显示
	    if(Tools.isNotBlank(appid)){
	    	if(sql.indexOf("where")==-1){
	    		sql+=" where WF_Appid='"+appid+"'";
	    	}else{
	    		sql+=" and WF_Appid='"+appid+"'";
	    	}
	    }
	    
	    //按文件夹显示
	    if(Tools.isNotBlank(folderid) && !folderid.equals("*")){
	    	if(sql.indexOf("where")==-1){
	    		sql+=" where Folderid='"+appid+"'";
	    	}else{
	    		sql+=" and Folderid='"+appid+"'";
	    	}
	    }
	    
	    //增加搜索条件
	    if(Tools.isNotBlank(searchWord)){
	    	if(sql.indexOf("where")==-1){
	    		sql+=" where RuleName like '%"+searchWord+"%'";
	    	}else{
	    		sql+=" and RuleName like '%"+searchWord+"%'";
	    	}
	    }
	    
	    //增加排序功能
	    sql+=" order by WF_DocCreated desc";
//	    BeanCtx.out(sql);
	    int total=Rdb.getCountBySql(sql);
        Document[] dc=Rdb.getAllDocumentsBySql("BPM_RuleList",sql,Integer.valueOf(pageNo),Integer.valueOf(pageSize));
        
        
	    String jsonStr=Documents.dc2json(dc,"");
	    
	    jsonStr="{\"total\":"+total+",\"rows\":"+jsonStr+"}";
	    jsonStr=RestUtil.formartResultJson("1", "", jsonStr);
	    
	    return jsonStr;
	}
}