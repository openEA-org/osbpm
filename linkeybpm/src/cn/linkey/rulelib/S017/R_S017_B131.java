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
 * @RuleName:Rest_Engine获得委托列表
 * @author  admin
 * @version: 1.0
 */
final public class R_S017_B131 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
	    String sql="select * from BPM_EntrustList where WF_AddName='"+BeanCtx.getUserid()+"'";
	    Document[] dc=Rdb.getAllDocumentsBySql(sql);
	    
	    String jsonStr="{\"total\":"+dc.length+",\"rows\":"+Documents.dc2json(dc, "")+"}";
	    jsonStr=RestUtil.formartResultJson("1", "", jsonStr);
	    
	    return jsonStr;
	}
}