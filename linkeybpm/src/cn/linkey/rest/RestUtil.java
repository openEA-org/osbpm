package cn.linkey.rest;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.util.Tools;

/**
   Restful接口的工具类
 *
 * @author: alibao
 *
 * Modification History:
 * Date         Author          Version            Description
 *---------------------------------------------------------*
 * 2018年8月22日     alibao           v1.0.0               修改原因
 */
public class RestUtil {

	/**
	 * 
	 * 调用rest接口
	 *
	 * @param restid 配置的接口id
	 * @return 通过HashMap传参数到rest的调用接口中去
	 */
	public static String call(String restid) {
		Document paramsDocument = BeanCtx.getDocumentBean("");
		return call(restid, paramsDocument);
	}

	/**
	 * 
	 * 调用rest接口
	 *
	 * @param restid 配置的接口id
	 * @param mapData 一个HashMap对象
	 * @return 通过HashMap传参数到rest的调用接口中去
	 */
	public static String call(String restid, HashMap<String, String> mapData) {
		Document paramsDocument = BeanCtx.getDocumentBean("");
		paramsDocument.appendFromMap(mapData);
		return call(restid, paramsDocument);
	}

	/**
	 * 调用rest接口
	 * 
	 * @param restid
	 *            配置的接口id
	 * @param paramsDocument
	 *            在接口中的配置输入参数和头参数中允许使用{}符号来指定输入变量,再通过本参数传入参数到接口中去
	 * @return 返回信息
	 */
	public static String call(String restid, Document paramsDocument) {

		// params为运行本规则时所传入的参数
		try {
			String sql = "select * from BPM_RestInterfaceList where Interfaceid='" + Rdb.formatArg(restid) + "'";
			Document restdoc = Rdb.getDocumentBySql(sql);
			if (restdoc.isNull()) {
				return "can't found the rest config!";
			}
			String restUrl = restdoc.g("url");
			String method = restdoc.g("Method"); // get,post
			String inParams = restdoc.g("InParams");
			String headerParams = restdoc.g("HeaderParams");
			String connectTimeout = restdoc.g("ConnectTimeout");
			String readTimeout = restdoc.g("ReadTimeout");
			if (Tools.isBlank(connectTimeout)) {
				connectTimeout = "30000";
			}
			if (Tools.isBlank(readTimeout)) {
				readTimeout = "30000";
			}

			// 构建输入参数
			String postStr = "";
			JSONArray jsonArray = JSONArray.parseArray(inParams);
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				String id = jsonObject.getString("id");
				String value = jsonObject.getString("value");
				if (value.startsWith("{") && value.endsWith("}")) {
					String fdName = value.substring(1, value.length() - 1); // 得到字段名称并从参数中获得值进行替换
					value = paramsDocument.g(fdName);
				}
				if (Tools.isBlank(postStr)) {
					postStr = id + "=" + value;
				} else {
					postStr += "&" + id + "=" + value;
				}
			}
			// BeanCtx.out("method="+method);
			// BeanCtx.out("PostStr="+postStr);
			if (method.equalsIgnoreCase("GET") && Tools.isNotBlank(postStr)) {
				restUrl = restUrl + "?" + postStr;
			}
			BeanCtx.out("restUrl=" + restUrl);
			URL url = new URL(restUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // 打开restful链接
			conn.setRequestMethod(method);// POST GET PUT DELETE

			// 设置访问提交模式，表单提交
			// conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

			// 设置请求头的参数
			jsonArray = JSONArray.parseArray(headerParams);
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				String id = jsonObject.getString("id");
				String value = jsonObject.getString("value");
				if (value.startsWith("{") && value.endsWith("}")) {
					String fdName = value.substring(1, value.length() - 1); // 得到字段名称并从参数中获得值进行替换
					value = paramsDocument.g(fdName);
				}
				conn.setRequestProperty(id, value);
			}

			conn.setConnectTimeout(Integer.parseInt(connectTimeout));// 连接超时
																		// 单位毫秒
			conn.setReadTimeout(Integer.parseInt(readTimeout));// 读取超时 单位毫秒

			conn.setDoOutput(true);// 是否输入参数
			conn.setDoInput(true);

			if (method.equalsIgnoreCase("GET")) {
				conn.connect();
			} else {
				// POST方法需要传参数
				OutputStream outStrm = conn.getOutputStream();
				BeanCtx.out("postStr=" + postStr);
				byte[] bypes = postStr.getBytes("UTF-8");
				try {
					outStrm.write(bypes);// 输入参数
					outStrm.flush();
					outStrm.close();
					// BeanCtx.out("写入成功post");
				} catch (Exception e) {
					BeanCtx.log(e, "E", "");
				} finally {
					outStrm.close();
				}
			}

			// 获得返回值
			InputStream inStream = conn.getInputStream();
			String returnStr = Tools.streamToString(inStream, "UTF-8");
			return returnStr;

		} catch (Exception e) {
			BeanCtx.log(e, "E", "");
			BeanCtx.p(Tools.getErrorMsgFromException(e));
		}
		return "";
	}

	/**
	 * 格式化成标准的json响应字符串
	 * 
	 * @param status
	 *            状态：ok error ....
	 * @param msg
	 *            要返回的提示消息
	 * @return 返回标准的JSON响应字符
	 */
	public static String formartResultJson(String status, String msg) {
		return "{\"Status\":\"" + status + "\",\"msg\":\"" + msg + "\"}";
	}

	/**
	 * 
	* 返回JSON格式
	*
	* @param jsonStr 要是JSON格式的数据
	* @param status 返回状态
	* @param msg 返现提示信息
	* @return 格式化成JSON
	 */
	public static String formartResultJson(String status, String msg, String jsonStr) {

		jsonStr = (Tools.isBlank(jsonStr) ? "{}" : jsonStr);
		//	{ "Status": 1, "msg": "成功删除(1)个表单!", "data": {} }
		return "{\"Status\":\"" + status + "\",\"msg\":\"" + msg + "\",\"data\": " + jsonStr + " }";
	}

	/**
	 * 对字符串进行json格式的编码
	 * 
	 * @param str
	 *            要编码的json值
	 * @return 返回编码后数据
	 */
	public static String encodeJson(String str) {
		str = str.replace("\\", "\\\\"); // 1.替换值中的\
		str = str.replace("\"", "\\\""); // 2.替换值中的双引号
		// str = str.replace("\t", ""); // 3.替换值中的换行符
		str = str.replace("\n", "\\n"); // 3.替换值中的换行符
		str = str.replace("\r", "\\r"); // 3.替换值中的换行符
		return str;
	}
	
	
	/**
	 * 兼容解码方式，先判断是不是utf-8编码，否则以ISO-8859-1进行解码
	 * @param str 要解码的字符串
	 * @return 解码后的字符串，若抛出异常，返回空字符串
	 */
	public static String decodeAll(String str) {
		try {
			if (str.equals(new String(str.getBytes("utf-8"), "utf-8"))) {
				str = Tools.decode(str);
			} else {
				str = Tools.decodeUrl(str);
			}
		} catch (UnsupportedEncodingException e1) {
			BeanCtx.log(e1, "E", "字符串解码失败(" + str + ")");
			return "";
		}
		return str;
	}
}
