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
 * @RuleName:Rest_Remark获得常用办理意见
 * @author  admin
 * @version: 8.0
 * @Created: 2014-07-01 23:04
 */
final public class R_S017_B129 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
	    //无需传参数
	   	String sql="select WF_MyRemark from BPM_UserProfile where Userid='"+BeanCtx.getUserid()+"'";
	    String remark=Rdb.getValueBySql(sql);
	    remark=remark.replace("\n",",");
	    String systemRemark=BeanCtx.getSystemConfig("CommonRemarkList");
	    systemRemark=systemRemark.replace("\n",",");
	    if(Tools.isNotBlank(remark)){
	        remark+=","+systemRemark;
	    }else{
	        remark=systemRemark;
	    }
	    String jsonStr="{\"remark\":\""+RestUtil.encodeJson(remark)+"\"}";
	    jsonStr=RestUtil.formartResultJson("1", "", jsonStr);
	    return jsonStr;
	}
}