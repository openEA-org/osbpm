package cn.linkey.rulelib.S033;

import java.util.*;
import cn.linkey.dao.*;
import cn.linkey.util.*;
import cn.linkey.doc.*;
import cn.linkey.factory.*;
import cn.linkey.wf.*;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.org.LinkeyUser;
/**
 * @RuleName:测试REST规则
 * @author  admin
 * @version: 8.0
 * @Created: 2016-09-22 11:02:01
 */
final public class R_S033_B004 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
	    BeanCtx.out(params);
	    BeanCtx.out("post arg="+BeanCtx.g("arg")+"="+BeanCtx.getRequest().getParameter("pwd"));
	    return "post arg="+params.get("arg");
	}
}