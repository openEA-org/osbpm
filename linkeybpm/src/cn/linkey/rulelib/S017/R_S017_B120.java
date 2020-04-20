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
 * @RuleName:DOC_删除流程文档服务,只删除正在流转的流程文档
 * @author  admin
 * @version: 1.0
 */
final public class R_S017_B120 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
	    String docUnid=BeanCtx.g("docUnid");
	    if(Tools.isBlank(docUnid)){return RestUtil.formartResultJson("0", "docUnid不能为空");}
	    
	    boolean r=ProcessUtil.removeProcessDocument(docUnid);
	    if(r){
	    	return RestUtil.formartResultJson("1","删除成功");
	    }
	    else{
	    	return RestUtil.formartResultJson("0","删除失败");
	    }
	}
}