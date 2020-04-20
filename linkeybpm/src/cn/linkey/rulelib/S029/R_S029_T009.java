package cn.linkey.rulelib.S029;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;
/**
 * 
 * 定时启用委托设置(半小时执行一次)
 *
 * @author: alibao
 *
 * Modification History:
 * Date         Author          Version            Description
 *---------------------------------------------------------*
 * 2018年9月4日     alibao           v1.0.0               修改原因
 */
final public class R_S029_T009  implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception {
		java.util.Calendar Cal=java.util.Calendar.getInstance();    
		Cal.setTime(DateUtil.getDate());   
		Cal.add(java.util.Calendar.MINUTE,-5);//当前时间减少5分钟
		//BeanCtx.out(DateUtil.formatDate(Cal.getTime(),"yyyy-MM-dd HH:mm"));
		//把定时开始时间小于当前时间的标记为启用状态
		String sql="update BPM_EntrustList set Status='1' where StartTime<'"+DateUtil.formatDate(Cal.getTime(),"yyyy-MM-dd HH:mm")+"' and EndTime>'"+DateUtil.formatDate(Cal.getTime(),"yyyy-MM-dd HH:mm")+"' and Status='2'";
		//BeanCtx.setDebug();
		
		//20180821过时停用 
		String sql2="update BPM_EntrustList set Status='2' where EndTime <'"+DateUtil.formatDate(Cal.getTime(),"yyyy-MM-dd HH:mm")+"' and Status='1'";

		
		Rdb.execSql(sql);
		Rdb.execSql(sql2);
	    return "";
	}

}