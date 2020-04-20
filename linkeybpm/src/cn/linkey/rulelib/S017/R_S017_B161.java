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
 * @RuleName:我的关注列表
 * @author  admin
 * @version: 8.0
 */
final public class R_S017_B161 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数

	    String pageSize=BeanCtx.g("pageSize");
	    String pageNo=BeanCtx.g("pageNo");
	    String appid=BeanCtx.g("appid");
	    String keyWord=BeanCtx.g("keyWord");
	    

	    if(Tools.isBlank(pageSize)){pageSize="20";}
	    if(Tools.isBlank(pageNo)){pageNo="1";}
	    
	    
	    String sql="select Subject,WF_AddName_CN,WF_Author_CN,WF_CurrentNodeName,WF_DocNumber,WF_ProcessName,WF_Processid,WF_OrUnid,WF_DocCreated from BPM_AllDocFavorites where userId='"+BeanCtx.getUserid()+"'";
	    if(Tools.isNotBlank(appid)){
	        sql+=" and WF_Appid='"+appid+"'";
	    }
	    
	    //增加搜索条件subject
	    if(Tools.isNotBlank(keyWord)){
	        sql+=" and (subject like '%"+keyWord+"%' or WF_ProcessName like '%"+keyWord+"%')";
	    }
	    //增加排序功能
	    sql+=" order by WF_DocCreated desc";
	    
	    String totalNum=String.valueOf(Rdb.getCountBySql(sql));
	    Document[] dc=Rdb.getAllDocumentsBySql("BPM_AllDocFavorites",sql,Integer.valueOf(pageNo),Integer.valueOf(pageSize));
	    String jsonStr="{\"total\":"+totalNum+",\"rows\":"+Documents.dc2json(dc,"")+"}";
	    jsonStr=RestUtil.formartResultJson("1", "", jsonStr);
	    return jsonStr;
	    
	}
}