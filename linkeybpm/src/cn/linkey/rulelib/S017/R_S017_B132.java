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
 * @RuleName:Rest_Engine创建或修改流程委托设置
 * @author  admin
 * @version: 8.0
 * @Created: 2014-07-01 23:04
 */
final public class R_S017_B132 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
	    String docUnid=BeanCtx.g("docUnid",true);
	    String subject=BeanCtx.g("subject",true);
	    String status=BeanCtx.g("status",true);
	    String startTime=BeanCtx.g("startTime",true);
	    String endTime=BeanCtx.g("endTime",true);
	    String processId=BeanCtx.g("processId",true);
	    String entrustUserId=BeanCtx.g("entrustUserId",true);

	    if(Tools.isBlank(subject)){return RestUtil.formartResultJson("0", "subject不能为空");}
	    if(Tools.isBlank(status)){return RestUtil.formartResultJson("0", "status不能为空");}
	    if(Tools.isBlank(entrustUserId)){return RestUtil.formartResultJson("0", "entrustUserId不能为空");}
	    
	    String entrustUserName=BeanCtx.getLinkeyUser().getCnName(entrustUserId);
	    String sql="select * from BPM_EntrustList where WF_OrUnid='"+docUnid+"'";
	    Document doc=Rdb.getDocumentBySql(sql);
	    if(doc.isNull()){
	    	//创建一个新的
//	    	doc.appendFromRequest();
	    	doc.s("Subject", subject);
	    	doc.s("EntrustUserid", entrustUserId);
	    	doc.s("StartTime", startTime);
	    	doc.s("EndTime", endTime);
	    	doc.s("Processid", processId);
	    	doc.s("EntrustUserid_show", entrustUserName);
	    	doc.save("BPM_EntrustList");
	    	return RestUtil.formartResultJson("1", "成功创建委托设置","");
	    }else{
	    	//更新设置
//	    	doc.appendFromRequest();
	    	doc.s("Subject", subject);
	    	doc.s("EntrustUserid", entrustUserId);
	    	doc.s("StartTime", startTime);
	    	doc.s("EndTime", endTime);
	    	doc.s("Processid", processId);
	    	doc.s("EntrustUserid_show", entrustUserName);
	    	doc.save("BPM_EntrustList");
	    	return RestUtil.formartResultJson("1", "成功更新委托设置","");
	    }
	}
}