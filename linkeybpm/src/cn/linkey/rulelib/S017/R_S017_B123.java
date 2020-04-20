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
 * @RuleName:Rest_Remark获得流转记录
 * @author  admin
 * @version: 1.0
 */
final public class R_S017_B123 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
	    String sql="";
	    String remarkType=BeanCtx.g("category");
	    String docUnid=BeanCtx.g("docUnid");
	    String isReadFlag=BeanCtx.g("type"); //0表示办理意见，1表示阅读记录
	    if(Tools.isBlank(docUnid)){return RestUtil.formartResultJson("0", "docUnid不能为空");}
	    if(Tools.isBlank(isReadFlag)){return RestUtil.formartResultJson("0", "type不能为空");}
	    if(Tools.isBlank(remarkType)){remarkType="ALL";}
	   	if(remarkType.equalsIgnoreCase("ALL")){
			sql="select * from BPM_AllRemarkList where DocUnid='"+docUnid+"' and IsReadFlag='"+isReadFlag+"' order by EndTime";
		}else{
			sql="select * from BPM_AllRemarkList where DocUnid='"+docUnid+"' and IsReadFlag='"+isReadFlag+"' and RemarkType='"+remarkType+"' order by EndTime";
		}
	   	BeanCtx.out(sql);
	    Document[] dc=Rdb.getAllDocumentsBySql(sql);
	    
	    String jsonStr="{\"total\":"+dc.length+",\"rows\":"+Documents.dc2json(dc,"")+"}";
	    jsonStr=RestUtil.formartResultJson("1", "", jsonStr);
	    
	    return jsonStr;
	    
	}
}