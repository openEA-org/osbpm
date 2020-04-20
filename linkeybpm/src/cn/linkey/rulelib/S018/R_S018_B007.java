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
 * @RuleName:保存或更新表单
 * @author  admin
 * @version: 8.0
 * @Created: 2018-05-28 21:04:37
 */
final public class R_S018_B007 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
		String tableName="BPM_FormList";
		
		String docUnid=BeanCtx.g("docUnid");
		String appId=BeanCtx.g("WF_Appid");
		String formNumber=BeanCtx.g("FormNumber");
		String FormName=BeanCtx.g("FormName");
		
		if(Tools.isBlank(formNumber)){return RestUtil.formartResultJson("0", "FormNumber不能为空");}
		if(Tools.isBlank(appId)){return RestUtil.formartResultJson("0", "WF_Appid不能为空");}
		if(Tools.isBlank(FormName)){return RestUtil.formartResultJson("0", "FormName不能为空");}
		
		
        Document eldoc = AppUtil.getDocByUnid(tableName, docUnid);
        if (eldoc.isNull()) {
        	eldoc.appendFromRequest();
            eldoc.s("WF_OrUnid", docUnid);
        }
        eldoc.s("Title", eldoc.g("FormNumber"));
        int i=eldoc.save();
        if(i>0){
        	return RestUtil.formartResultJson("1", "规则成功保存");
        }else{
        	return RestUtil.formartResultJson("0", "规则保存失败");
        }
	}
}