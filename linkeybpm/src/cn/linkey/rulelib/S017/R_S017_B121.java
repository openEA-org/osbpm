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
 * @RuleName:查询流程文档数据
 * @author  admin
 * @version: 1.0
 */
final public class R_S017_B121 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
		String docUnid=BeanCtx.g("docUnid");
		if(Tools.isBlank(docUnid)){return RestUtil.formartResultJson("0", "docUnid不能为空");}
		
	    String sql="select * from BPM_AllDocument where WF_OrUnid='"+docUnid+"'";
	    Document doc=Rdb.getDocumentBySql(sql);
	    if(doc.isNull()){
	    	return RestUtil.formartResultJson("0","文档不存在");
	    }else{
	    	return RestUtil.formartResultJson("1","",doc.toJson());
	    }
	    
	}
}