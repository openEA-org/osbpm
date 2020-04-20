package cn.linkey.rulelib.S017;

import java.util.*;
import cn.linkey.dao.*;
import cn.linkey.util.*;
import cn.linkey.doc.*;
import cn.linkey.factory.*;
import cn.linkey.rest.RestUtil;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:查询流程实例的安全日记
 * @author  admin
 * @version: 1.0
 */
final public class R_S017_B176 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
		 String docUnid = BeanCtx.g("docUnid",true);//流程实例id
        if (Tools.isBlank(docUnid)) {return RestUtil.formartResultJson("0", "docUnid不能为空");}
	    
        String sql = "select * from BPM_AttachmentLog where DocUnid='" + docUnid + "'";
        Document[] dc=Rdb.getAllDocumentsBySql(sql);
        String jsonStr=Documents.dc2json(dc, "");
        
        return RestUtil.formartResultJson("1", "",jsonStr);

	}
    
}