package cn.linkey.rulelib.SYSDEMO;

import java.util.*;
import cn.linkey.dao.*;
import cn.linkey.util.*;
import cn.linkey.doc.*;
import cn.linkey.factory.*;
import cn.linkey.wf.*;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.org.LinkeyUser;

/**
 * @RuleName:接口调用_获取表单所有字段配置的JSON
 * @author  admin
 * @version: 8.0
 * @Created: 2019-03-04 15:32:02
 */
final public class R_SYSDEMO_BG11 implements LinkeyRule {
	
	private static final String sysid = "SysAdmin"; //业务注册的ID
	private static final String syspwd = "pass"; //接入的密码
	private static final String userId = "admin"; //指定用户访问
	private static final String url = "http://localhost:6680/bpm12/rest/process/form/field/{processId}/{docUnid}/config";
	
	
	@Override
	public String run(HashMap<String, Object> params) throws Exception {
		//添加验证参数
		Map<String, String> headerMap = new HashMap<>();
		headerMap.put("sysid", sysid);
		headerMap.put("syspwd", syspwd);
		headerMap.put("userId", userId);

		//========================GET 第一种方式调用========================
		//添加业务逻辑参数
		String paramUrl = url;
		paramUrl = paramUrl.replace("{processId}", "292731a50828d04d8c0831b08feda690a91d");
		paramUrl = paramUrl.replace("{docUnid}", "d937a02403595040f00828f07f885037400b");

// 		使用Get方式请求数据
		String responseStr = Tools.httpGet(paramUrl, headerMap, "");
		//=================================================================

		//========================GET 第二种方式调用========================
				// 		//添加业务逻辑参数
				// 		StringBuilder param = new StringBuilder();
				// 		param.append("?processId=292731a50828d04d8c0831b08feda690a91d");
				// // 		param.append("&processName=含会签流程");
						
				// 		String paramUrl = url + param.toString();
						
				// 		//使用Get方式请求数据
				// 		String responseStr = Tools.httpGet(paramUrl, headerMap, "");
		//=================================================================

		//========================GET 第三种方式调用【需要遍历ParameterMap取数】========================
		//				//添加业务逻辑参数
		//				Map<String, String> param = new HashMap<>();
		//				//使用Get方式请求数据
		//				String responseStr = Tools.httpGet(url, headerMap, param, "");
		//=================================================================

		//打印返回的数据
		BeanCtx.p(responseStr);
		System.out.println("打印返回的数据：" + responseStr);

		return "";
	}
}