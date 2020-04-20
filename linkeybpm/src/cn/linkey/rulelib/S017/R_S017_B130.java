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
 * @RuleName:Rest_Remark追加常用办理意见
 * @author  admin
 * @version: 8.0
 * @Created: 2014-07-01 23:04
 */
final public class R_S017_B130 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
	    //参数示例:{"Remark":"同意办理"}
		String remark=BeanCtx.g("remark");
		if(Tools.isBlank(remark)){
			return RestUtil.formartResultJson("0", "意见不能为空!","");
		}
		
	    String sql="select * from BPM_UserProfile where Userid='"+BeanCtx.getUserid()+"'";
	    Document doc=Rdb.getDocumentBySql(sql);
	    if(doc.isNull()){
	    	doc.s("WF_MyRemark", remark);
	    	doc.s("Userid",BeanCtx.getUserid());
	    	doc.s("UserName",BeanCtx.getUserName());
	    }else{
	    	doc.s("WF_MyRemark", doc.g("WF_MyRemark")+"\n"+remark);
	    }
	    int i=doc.save();
	    return RestUtil.formartResultJson("1", "成功增加","");
	}
}