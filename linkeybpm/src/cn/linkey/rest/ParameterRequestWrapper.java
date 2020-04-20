package cn.linkey.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * 
 * 该类用于改写request 参数的值里的值
 * 使用方法：HashMap newParam=new HashMap(request.getParameterMap());
 * ParameterRequestWrapper wrapRequest=new ParameterRequestWrapper(request,newParam);
 *
 * @author: alibao
 *
 * Modification History:
 * Date         Author          Version            Description
 *---------------------------------------------------------*
 * 2019年1月24日     alibao           v1.0.0               修改原因
 */
public class ParameterRequestWrapper extends HttpServletRequestWrapper {

	private Map<String, Object> params;

	public ParameterRequestWrapper(HttpServletRequest request, Map<String, Object> newParams) {
		super(request);
		this.params = newParams;
	}

	/**
	 * 获取getParameterMap 参数
	 */
	public Map<String, String[]> getParameterMap() {

		Map<String, String[]> returnMap = new HashMap<>();
		for (String key : params.keySet()) {
			returnMap.put(key, getParameterValues(key));
		}

		return returnMap;
	}

	/**
	 * 
	 * @see javax.servlet.ServletRequestWrapper#getParameterNames()  
	 * 重写request getParameterNames 方法
	 *
	 * @return： 返回参数结果
	 */
	public Enumeration getParameterNames() {
		Vector l = new Vector(params.keySet());
		return l.elements();
	}

	/**
	 * 
	 * @see javax.servlet.ServletRequestWrapper#getParameterValues(java.lang.String)  
	 * 重写request getParameterValues 方法
	 *
	 * @param: name 参数名
	 * @return： 返回参数结果
	 */
	public String[] getParameterValues(String name) {
		Object v = params.get(name);
		if (v == null) {
			return null;
		} else if (v instanceof String[]) {
			return (String[]) v;
		} else if (v instanceof String) {
			return new String[] { (String) v };
		} else {
			return new String[] { v.toString() };
		}
	}

	/**
	 * 
	 * @see javax.servlet.ServletRequestWrapper#getParameter(java.lang.String)  
	 * 重写request getParameter 方法
	 *
	 * @param: name 参数名
	 * @return： 返回参数结果
	 */
	public String getParameter(String name) {
		Object v = params.get(name);
		if (v == null) {
			return null;
		} else if (v instanceof String[]) {
			String[] strArr = (String[]) v;
			if (strArr.length > 0) {
				return strArr[0];
			} else {
				return null;
			}
		} else if (v instanceof String) {
			return (String) v;
		} else {
			return v.toString();
		}
	}
}
