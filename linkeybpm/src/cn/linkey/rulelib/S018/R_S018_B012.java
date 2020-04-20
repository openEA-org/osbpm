package cn.linkey.rulelib.S018;

import java.util.*;

import cn.linkey.app.AppUtil;
import cn.linkey.dao.*;
import cn.linkey.util.*;
import cn.linkey.doc.*;
import cn.linkey.factory.*;
import cn.linkey.wf.*;
import cn.linkey.rest.RestUtil;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.org.LinkeyUser;
/**
 * @RuleName:创建或更新规则
 * @author  admin
 * @version: 1.0
 */
final public class R_S018_B012 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
		String tableName="BPM_RuleList";
		
		String docUnid=BeanCtx.g("docUnid");
		String appId=BeanCtx.g("WF_Appid");
		String RuleNum=BeanCtx.g("RuleNum");
		String RuleName=BeanCtx.g("RuleName");
		String RuleType=BeanCtx.g("RuleType");
		
//		if(Tools.isBlank(docUnid)){return RestUtil.formartResultJson("0", "docUnid不能为空");}
		if(Tools.isBlank(RuleNum)){return RestUtil.formartResultJson("0", "RuleNum不能为空");}
		if(Tools.isBlank(appId)){return RestUtil.formartResultJson("0", "WF_Appid不能为空");}
		if(Tools.isBlank(RuleName)){return RestUtil.formartResultJson("0", "RuleName不能为空");}
		if(Tools.isBlank(RuleType)){return RestUtil.formartResultJson("0", "RuleType不能为空");}
		
		
        Document eldoc = Rdb.getDocumentById(tableName, docUnid);
        if (eldoc.isNull()) {
        	eldoc.appendFromRequest();
        	eldoc.s("WF_Appid", appId);
            eldoc.s("WF_OrUnid", docUnid);
        }
        BeanCtx.out(eldoc);
        int i=eldoc.save();
        if(i>0){
        	return RestUtil.formartResultJson("1", "规则成功保存");
        }else{
        	return RestUtil.formartResultJson("0", "规则保存失败");
        }
	}
}