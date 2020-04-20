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
 * @RuleName:修改流程文档字段数据
 * @author  admin
 * @version: 8.0
 * @Created: 2014-07-01 22:27
 */
final public class R_S017_B122 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    
		 String docUnid=BeanCtx.g("docUnid");
		 String formData=BeanCtx.g("formData"); //要修改的表单数据
		 if(Tools.isBlank(docUnid)){return RestUtil.formartResultJson("0", "docUnid不能为空");}
		    
	    String sql="select * from BPM_MainData where WF_OrUnid='"+docUnid+"'";
	    Document doc=Rdb.getDocumentBySql(sql);
	    if(doc.isNull()){
	    	return RestUtil.formartResultJson("0","流程文档不存在");
	    }else{
	        if(Tools.isNotBlank(formData)){
	        	doc.appendFromJsonStr(formData);
	        }
		    int i=doc.save();
	    	return RestUtil.formartResultJson("1","数据修改成功");
	    }
	}
}