package cn.linkey.servlet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;

import com.alibaba.fastjson.JSON;

import cn.linkey.factory.BeanCtx;
import cn.linkey.util.Tools;

public class MyServletRequest extends HttpServletRequestWrapper {
	// 我们要装饰的对象
	HttpServletRequest myrequest;
	private static Policy policy = null;
	private static AntiSamy antiSamy;

	public MyServletRequest(HttpServletRequest request) {
		super(request);
		this.myrequest = request;
	}

	// 我们要增强的功能方法
	@Override
	public String getParameter(String name) {
		// TODO Auto-generated method stub
		// 使用被装饰的成员，获取数据
		String value1 = super.getParameter(name);
		// 20180201这里添加重定向过滤
		if ("gourl".equals(name) && !whiteList(myrequest, value1)) {
			value1 = null;
		}
		if (value1 == null)
			return null;
		// AntiSamy
		// value1 =xssClean(value1);
		if (myrequest.getParameter("wf_num") != null && myrequest.getParameter("wf_num").indexOf("V_") != -1
				&& myrequest.getParameter("wf_num").indexOf("_E") != -1) {
			//
			if (value1.length() > 2 && value1.indexOf("[") != -1 && value1.indexOf("]") != -1) {
				JSONArray myJsonArray = null;
				try {
					myJsonArray = new JSONArray(value1);
					JSONArray newJsonArray = new JSONArray();
					for (int i = 0; i < myJsonArray.length(); i++) {
						JSONObject myjObject = myJsonArray.getJSONObject(i);
						Iterator iterator = (Iterator) myjObject.keys();
						Map<String, String> map = new HashMap<>();
						String key = "";
						String value = "";
						while (iterator.hasNext()) {
							key = (String) iterator.next();
							value = myjObject.getString(key);
							map.put(key, xssClean(value));
						}
						newJsonArray.put(map);
					}
					value1 = newJsonArray.toString();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		// 将数据转码后返回

		return value1;

	}

	public String[] getParameterValues(String paramString) {
		String[] arrayOfString1 = super.getParameterValues(paramString);
		if (arrayOfString1 == null)
			return null;
		int i = arrayOfString1.length;
		String[] arrayOfString2 = new String[i];
		for (int j = 0; j < i; j++)
			arrayOfString2[j] = xssClean(arrayOfString1[j]);
		return arrayOfString2;
	}

	public String getHeader(String paramString) {
		String str = super.getHeader(paramString);
		if (str == null)
			return null;
		str = xssClean(str);
		return str;
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		Map<String, String[]> request_map = super.getParameterMap();
		String wf_param = myrequest.getParameter("wf_num");
		if (myrequest.getParameter("wf_num").indexOf("F_S0") != -1) {
			// 20180523添加过来Tab键
			if (request_map == null)
				return null;
			else {
				Iterator iterator = request_map.entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry me = (Map.Entry) iterator.next();
					String[] values = (String[]) me.getValue();
					for (int i = 0; i < values.length; i++) {
						values[i] = values[i].replaceAll("\\t", "  "); // 20180523添加过来Tab键
					}
				}
			}
			return request_map;
		} else if ("R_S003_B035".equals(wf_param) || wf_param.indexOf("F_") != -1) {
			// if
			// ("R_S003_B035".equals(wf_param)
			// ||
			// wf_param.indexOf("F_")
			// !=
			// -1)
			if (request_map == null)
				return null;
			Iterator iterator = request_map.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry me = (Map.Entry) iterator.next();
				// System.out.println(me.getKey()+":");
				String[] values = (String[]) me.getValue();
				for (int i = 0; i < values.length; i++) {
					values[i] = values[i].replaceAll("\\t", "  "); // 20180523添加过来Tab键
					values[i] = xssClean(values[i]);
				}
			}
			return request_map;
		} else {
			return super.getParameterMap();
		}

	}

	/** 扫描处理方法 */
	public String xssClean(String value) {
		try {
			String path1 = this.getClass().getResource("").getPath() + "antisamy-anythinggoes.xml";
			// path1 = path1.substring(1);
			// path1="/home/yzkf/www/bpm/bpm/WEB-INF/classes/cn/linkey/servlet/antisamy-anythinggoes.xml";
			value = new AntiSamy().scan(value, Policy.getInstance(path1)).getCleanHTML();
		} catch (ScanException e) {
			e.printStackTrace();
		} catch (PolicyException e) {
			e.printStackTrace();
		}
		// 将数据转码后返回
		return value;
	}

	// 20180201白名单判断
	private boolean whiteList(HttpServletRequest request, String value1) {
		
		if( Tools.isBlank(value1) )
			return false;
		
		//20180525  修改白名单从系统公用配置里面选择
		String[] whiteLsit = null; //存放白名单的list
		whiteLsit = BeanCtx.getSystemConfig("securityWhiteList").split(",");
		
		if(whiteLsit == null){
			whiteLsit = new String[] { "r?wf_num=S005", "r?wf_num=S013" };
		}
		
		for (String wname : whiteLsit) {
			//System.out.println("value1: " + value1 + "\nwname: " + wname);
			if (Tools.isNotBlank(wname) && value1.contains(wname)) {
				return true;
			}
		}
		
//		List<String> whiteName = Arrays.asList(new String[] { "r?wf_num=S005" });
//		for (String wname : whiteName) {
//			if (Tools.isNotBlank(value1) && value1.contains(wname)) {
//				return true;
//			}
//		}
		return false;
	}

}
