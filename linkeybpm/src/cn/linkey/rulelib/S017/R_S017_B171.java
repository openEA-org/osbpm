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
 * @RuleName:删除委托
 * @author  admin
 * @version: 1.0
 */
final public class R_S017_B171 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
		 String docUnid = BeanCtx.g("docUnid");
        if (Tools.isBlank(docUnid)) {
            BeanCtx.p(RestUtil.formartResultJson("0", "docUnid不能为空"));
            return "";
        }
	    
	    String sql="delete from BPM_EntrustList where WF_OrUnid='"+docUnid+"'";
	    int i=Rdb.execSql(sql);
	    if(i>0){
	    	  BeanCtx.p(RestUtil.formartResultJson("1", "成功删除"));
	    }else{
	    	BeanCtx.p(RestUtil.formartResultJson("0", "删除失败"));
	    }
	    
	    return "";
	}
}