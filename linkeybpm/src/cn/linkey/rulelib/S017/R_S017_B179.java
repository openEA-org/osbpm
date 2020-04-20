package cn.linkey.rulelib.S017;

import java.util.*;

import cn.linkey.dao.*;
import cn.linkey.util.*;
import cn.linkey.doc.*;
import cn.linkey.factory.*;
import cn.linkey.rest.RestUtil;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:获取当前登录用户的待办数
 * @author  admin
 * @version: 1.0
 */
final public class R_S017_B179 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数

        String appid = BeanCtx.g("appId", true);
        
        String sql = "";
        if (Tools.isBlank(appid)) {
            sql = "select count(*) as TotalNum from BPM_UserToDo where Userid='" + BeanCtx.getUserid() + "'";
        }
        else {
            sql = "select count(*) as TotalNum from BPM_UserToDo where Userid='" + BeanCtx.getUserid() + "' and WF_Appid='" + appid + "'";
        }
        String count = Rdb.getValueBySql(sql);
        
        return RestUtil.formartResultJson("1", "",count);

	}
    
}