package cn.linkey.rulelib.SL02;

import java.util.HashMap;
import cn.linkey.dao.Rdb;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import com.alibaba.fastjson.JSONObject;
/**
 * @RuleName:微信登陆验证方式
 * @author  Mr.Yun
 * @version: 8.0
 * @Created: 2016-08-19 11:36
 */
final public class R_SL02_B003 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
		JSONObject obj = new JSONObject();
		obj.put("Status", "err");
		obj.put("msg", "链接已超时！");
		// 获取连接中的时间戳
		String timestamp = (String) params.get("timestamp");
		if(Tools.isNotBlank(timestamp)){
			try {
				// 如有需要此处可作时间验证，取timestamp与当前时间戳相比，超出多少算连接超时
				if(System.currentTimeMillis() - Long.parseLong(timestamp) < 60*60*24*1000L){
					// 小于1天，不算超时
					obj.put("msg", "ok");
				}
			} catch (Exception e) {
			}
		}
		
		/**
		 * 不作操时处理，将应该句启动或将上面代码注销
		 */
		// obj.put("msg", "ok");
		
	    // 获取用户名
		String userid = (String) params.get("userid");
		/**
		 * 如果用户名不为空，且未超时
		 * 需要验证用户是否存在，此规则单独出来的目的：不一定所有验证都是采用本系统用户的，比如采用集成登陆或单点登陆的情况
		 */
		if(Tools.isNotBlank(userid) && obj.getString("msg").equals("ok")){
			try {
				String sql = "select * from BPM_OrgUserList where userid='" + Rdb.formatArg(userid) + "' and Status='1'";
		    	if(Rdb.hasRecord(sql)){
		    		obj.put("Status", "ok");
		    	}
			} catch (Exception e) {
			}
		}
	    return obj.toString();
	}
}