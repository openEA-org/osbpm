package cn.linkey.rulelib.S017;

import java.util.*;
import cn.linkey.dao.*;
import cn.linkey.util.*;
import cn.linkey.doc.*;
import cn.linkey.factory.*;
import cn.linkey.rest.RestUtil;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:获取当前登录用户的签名信息
 * @author  admin
 * @version: 1.0
 */
final public class R_S017_B177 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数

        String sql = "select * from BPM_Template where ItemId='StampSign' and Readers='" + BeanCtx.getUserid() + "'";
       Document doc=Rdb.getDocumentBySql(sql);
       doc.s("SignUrl", doc.getAttachmentsNameAndPath());
        String jsonStr=doc.toJson();
        
        return RestUtil.formartResultJson("1", "",jsonStr);

	}
    
}