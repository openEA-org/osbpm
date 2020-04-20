package cn.linkey.rulelib.S018;

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
 * @RuleName:修改流转记录中的意见
 * @author  admin
 * @version: 1.0
 */
final public class R_S018_B025 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
        String docUnid =BeanCtx.g("docUnid",true); //流转记录的id
        String remark=BeanCtx.g("remark",true); 
        if(Tools.isBlank(docUnid)){return RestUtil.formartResultJson("0", "docUnid不能为空");}
   
        String sql = "update BPM_InsRemarkList set Remark='"+remark+"' where WF_OrUnid='" + docUnid + "'";
        int i=Rdb.execSql(sql);
        if(i>0){
        	return RestUtil.formartResultJson("1", "成功修改处理意见!");
        }else{
        	return RestUtil.formartResultJson("0", "修改意见失败");
        }
	    
	}
}