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
 * @RuleName:获取表单清单
 * @author  admin
 * @version: 8.0
 * @Created: 2017-09-18 09:41:28
 */
final public class R_S018_B002 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    String folderid=BeanCtx.g("folderid",true);
	    String searchWord=BeanCtx.g("keyWord",true);
	    String pageSize=BeanCtx.g("pageSize",true);
	    String pageNo=BeanCtx.g("pageNo",true);
	    String appid=BeanCtx.g("appid",true);
	    
	    if(Tools.isBlank(pageSize)){pageSize="20";}
	    if(Tools.isBlank(pageNo)){pageNo="1";}
	    
	    String sql="select WF_OrUnid,FormNumber,WF_LastModified,WF_Version,Folderid,FormName from BPM_FormList where FormType='2'";

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
	    		sql+=" where FormName like '%"+searchWord+"%'";
	    	}else{
	    		sql+=" and FormName like '%"+searchWord+"%'";
	    	}
	    }
	    
	    //增加排序功能
	    sql+=" order by WF_DocCreated desc";
	    
	    int total=Rdb.getCountBySql(sql);
        Document[] dc=Rdb.getAllDocumentsBySql("BPM_FormList",sql,Integer.valueOf(pageNo),Integer.valueOf(pageSize));
        
	    String jsonStr=Documents.dc2json(dc,"");
	    
	    jsonStr="{\"total\":"+total+",\"rows\":"+jsonStr+"}";
	    jsonStr=RestUtil.formartResultJson("1", "", jsonStr);
	    
	    return jsonStr;
	}
}