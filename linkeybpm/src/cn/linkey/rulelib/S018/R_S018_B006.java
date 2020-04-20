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
 * @RuleName:Rest_MOD获得流程清单
 * @author  admin
 * @version: 8.0
 * @Created: 2014-07-01 22:40
 */
final public class R_S018_B006 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
	    //示例参数: {"Folderid":"001002"}
	    String folderid=BeanCtx.g("folderId");
	    String searchWord=BeanCtx.g("keyWord");
	    String pageSize=BeanCtx.g("pageSize");
	    String pageNo=BeanCtx.g("pageNo");
	    String appid=BeanCtx.g("appId");
	    String status=BeanCtx.g("status");//状态
	    
	    if(Tools.isBlank(pageSize)){pageSize="20";}
	    if(Tools.isBlank(pageNo)){pageNo="1";}
	    

	    String sql="select * from BPM_ModProcessList";
	    
	    //按status显示
	    if(Tools.isNotBlank(status)){
	    	if(sql.indexOf("where")==-1){
	    		sql+=" where Status='"+status+"'";
	    	}else{
	    		sql+=" and Status='"+status+"'";
	    	}
	    }
	    
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
	    		sql+=" where NodeName like '%"+searchWord+"%'";
	    	}else{
	    		sql+=" and NodeName like '%"+searchWord+"%'";
	    	}
	    }
	    
	    //增加排序功能
	    sql+=" order by WF_DocCreated desc";
	    
	    int total=Rdb.getCountBySql(sql);
        Document[] dc=Rdb.getAllDocumentsBySql("BPM_ModProcessList",sql,Integer.valueOf(pageNo),Integer.valueOf(pageSize));
        
        
	    String jsonStr=Documents.dc2json(dc,"");
	    
	    jsonStr="{\"total\":"+total+",\"rows\":"+jsonStr+"}";
	    jsonStr=RestUtil.formartResultJson("1", "", jsonStr);
	    
	    return jsonStr;
	}
}