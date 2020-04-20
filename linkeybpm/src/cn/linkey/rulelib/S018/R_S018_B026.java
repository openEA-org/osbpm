package cn.linkey.rulelib.S018;

import java.util.*;

import cn.linkey.app.AppUtil;
import cn.linkey.dao.*;
import cn.linkey.util.*;
import cn.linkey.doc.*;
import cn.linkey.factory.*;
import cn.linkey.wf.*;
import cn.linkey.rest.RestUtil;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.org.LinkeyUser;
/**
 * @RuleName:查询流程数量
 * @author  admin
 * @version: 1.0
 */
final public class R_S018_B026 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
		String tableName="BPM_ModProcessList";
		
		String appId=BeanCtx.g("appId",true);
		String sql="";
		if(Tools.isNotBlank(appId)){
			sql="select count(*) as totalNum from "+tableName+" where WF_Appid='"+appId+"'";
		}else{
			sql="select count(*) as totalNum from "+tableName;
		}
    	String processCount=Rdb.getValueBySql(sql);
//    	BeanCtx.out("processCount="+processCount);
        return RestUtil.formartResultJson("1", "",processCount);
	}
}