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
 * @RuleName:查询委托详情
 * @author  admin
 * @version: 1.0
 */
final public class R_S017_B170 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
		 String docUnid = (String)params.get("docunid");
        if (Tools.isBlank(docUnid)) {
            BeanCtx.p(RestUtil.formartResultJson("0", "docUnid不能为空"));
            return "";
        }
	        
	    String sql="select * from BPM_EntrustList where WF_OrUnid='"+docUnid+"'";
	    Document doc=Rdb.getDocumentBySql(sql);
	    if(doc.isNull()){
	    	 BeanCtx.p(RestUtil.formartResultJson("0", "docUnid不存在"));
	            return "";
	    }
	    
	    String jsonStr=doc.toJson();
	    jsonStr=RestUtil.formartResultJson("1", "", jsonStr);
	    
	    return jsonStr;
	}
}