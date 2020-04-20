package cn.linkey.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import cn.linkey.dao.Rdb;
import cn.linkey.dao.RdbCache;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;

/**
 * 工具类
 * 
 * @author Administrator
 * 
 */
public class Tools {

	/**
	 * 获得服务器的ip地址
	 * 
	 * @return 服务器ip地址，若抛出异常，返回空字符串
	 */
	public static String getServerIP() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			BeanCtx.log(e, "E", "");
			return "";
		}
	}

	/**
	 * 判断set集合中是否包含某一个元素,可以不分区大小写
	 * 
	 * @param keyStr 要判断的字符串
	 * @param setParams set集合，里面只能存字符串类型的变量
	 * @param ignoreCase true表示不区分大小写,false表示要区分大小写
	 * @return 返回true表示包含，false表示不包含
	 */
	public static boolean inSet(String keyStr, Set<String> setParams, boolean ignoreCase) {
		if (ignoreCase) {
			// 不区分大小写
			for (String keyItem : setParams) {
				if (keyItem.equalsIgnoreCase(keyStr)) {
					return true;
				}
			}
		} else {
			// 要区分大小写
			return setParams.contains(keyStr);
		}
		return false;
	}

	/**
	 * 判断一个值是否在一个数组中
	 * 
	 * @param array 数组
	 * @param key 值
	 * @return true表示在，false表示不在
	 */
	public static boolean inArray(String[] array, String key) {
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(key)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 字符串数组转字符串
	 * 
	 * @param array 数组对像
	 * @param key 分隔字符串
	 * @return 返回字符串
	 */
	public static String join(String[] array, String key) {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			if (i == 0) {
				str.append(array[i]);
			} else {
				str.append(key + array[i]);
			}
		}
		return str.toString();
	}

	/**
	 * ArrayList转字符串
	 * 
	 * @param array List对像
	 * @param key 分隔字符串
	 * @return 返回字符串
	 */
	public static String join(ArrayList<String> array, String key) {
		StringBuilder str = new StringBuilder();
		int i = 0;
		for (String item : array) {
			if (i == 0) {
				str.append(item);
				i = 1;
			} else {
				str.append(key + item);
			}
		}
		return str.toString();
	}

	/**
	 * Set转字符串同时去掉空值
	 * 
	 * @param set 对像
	 * @param key 分隔字符串
	 * @return 返回逗号分隔的字符串
	 */
	public static String join(Set<String> set, String key) {
		StringBuilder fdNameList = new StringBuilder();
		int i = 0;
		set.remove("");
		for (String item : set) {
			if (i == 0) {
				fdNameList.append(item);
				i = 1;
			} else {
				fdNameList.append(key + item);
			}
		}
		return fdNameList.toString();
	}

	/**
	 * 把字符串按逗号分解成为字符串数组
	 * 
	 * @param str 要分析的字符串
	 * @return 字符串数组
	 */
	public static String[] split(String str) {
		return split(str, ",");
	}

	/**
	 * 把字符串分解成为字符串数组
	 * 
	 * @param str 要分析的字符串
	 * @param key 关键字符串
	 * @return 字符串数组
	 */
	public static String[] split(String str, String key) {
		if (key.length() > 1) {
			return StringUtils.splitByWholeSeparator(str, key); // key为多个字符时要作为一个整体进行分隔
		} else {
			return StringUtils.split(str, key);
		}
	}

	/**
	 * 把字符串的两端空格去掉
	 * 
	 * @param str 要去掉两端空格的字符串
	 * @return 去掉两端空格后的字符串
	 */
	public static String trim(String str) {
		return StringUtils.trim(str);
	}

	/**
	 * 替换字符串
	 * 
	 * @param text 要替找的字符串
	 * @param searchString 旧字符串
	 * @param replacement 新字符串
	 * @return 替换后的字符串
	 */
	public static String replace(String text, String searchString, String replacement) {
		return StringUtils.replace(text, searchString, replacement);
	}

	/**
	 * 替换字符串一次
	 * 
	 * @param text 要替找的字符串
	 * @param searchString 旧字符串
	 * @param replacement 新字符串
	 * @return 替换后的字符串
	 */
	public static String replaceOne(String text, String searchString, String replacement) {
		return StringUtils.replaceOnce(text, searchString, replacement);
	}

	/**
	 * 把字符串切分为字List集合对像
	 * 
	 * @param str 要切分的字符串
	 * @param key 关键字
	 * @return List集合
	 */
	public static List<String> splitAsList(String str, String key) {
		if (str == null) {
			str = "";
		}
		return Arrays.asList(StringUtils.split(str, key));
	}

	/**
	 * 把字符串按逗号切分为字List集合对像
	 * 
	 * @param str 要切分的字符串
	 * @return List集合
	 */
	public static List<String> splitAsList(String str) {
		return splitAsList(str, ",");
	}

	/**
	 * 把字符串按逗号切分为字Set集合对像同时会去掉重复值,这个函数有问题
	 * 
	 * @param str 要切分的字符串
	 * @return HashSet集合
	 */
	public static HashSet<String> splitAsSet(String str) {
		return splitAsSet(str, ",");
	}

	/**
	 * 把字符串切分为字Set集合对像同时会去掉重复值, 这个函数有问题，复杂字符串时会出错
	 * 
	 * @param str 要切分的字符串
	 * @param key 关键字
	 * @return HashSet集合
	 */
	public static HashSet<String> splitAsSet(String str, String key) {
		String[] strArray = StringUtils.split(str, key);
		HashSet<String> set = new HashSet<String>(strArray.length);
		for (String item : strArray) {
			set.add(item);
		}
		return set;
	}

	/**
	 * 把字符串切分为字LinkedHashSet集合对像同时会去掉重复值, 这个函数有问题，复杂字符串时会出错
	 * 
	 * @param str 要切分的字符串
	 * @return 切分后的LinkedHashSet集合
	 */
	public static LinkedHashSet<String> splitAsLinkedSet(String str) {
		return splitAsLinkedSet(str, ",");
	}

	/**
	 * 把字符串切分为字LinkedHashSet集合对像同时会去掉重复值, 这个函数有问题，复杂字符串时会出错
	 * 
	 * @param str 要切分的字符串
	 * @param key 关键字
	 * @return LinkedHashSet集合
	 */
	public static LinkedHashSet<String> splitAsLinkedSet(String str, String key) {
		String[] strArray = StringUtils.split(str, key);
		LinkedHashSet<String> set = new LinkedHashSet<String>(strArray.length);
		for (String item : strArray) {
			set.add(item);
		}
		return set;
	}

	/**
	 * 获得一个给定长度的随机字符串
	 * 
	 * @param length 随机字符串的长度
	 * @return 随机字符串
	 */
	public static String getRandom(int length) {
		String allChar = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			sb.append(allChar.charAt(random.nextInt(allChar.length())));
		}
		return sb.toString();
	}

	/**
	 * 去除数组中的""空值
	 * 
	 * @param array 字符串数组
	 * @return 去掉数组中空字符串元素后的数组
	 */
	public static String[] fullTrim(String[] array) {
		ArrayList<String> list = (ArrayList<String>) Arrays.asList(array);
		list.remove("");
		return (String[]) list.toArray();
	}

	/**
	 * josn字符串转为hashmap对像,json字符串格式为{"fieldName1":"字段值","fdName2":"value2"}
	 * 
	 * @param jsonStr json字符串
	 * @return HashMap对象
	 */
	public static HashMap<String, String> jsonStr2Map(String jsonStr) {
		com.alibaba.fastjson.JSONObject jsonobj = com.alibaba.fastjson.JSON.parseObject(jsonStr);
		HashMap<String, String> map = new HashMap<String, String>(jsonobj.size());
		for (String fdName : jsonobj.keySet()) {
			map.put(fdName, jsonobj.getString(fdName));
		}
		return map;
	}

	/**
	 * 对字符串进行json格式的编码
	 * 
	 * @param str 要编码的json值
	 * @return 编码后的json字符串
	 */
	public static String encodeJson(String str) {
//		str = str.replace("\\", "\\\\"); // 1.替换值中的\
//		str = str.replace("\"", "\\\""); // 2.替换值中的双引号
//		// str = str.replace("\t", ""); // 3.替换值中的换行符
//		str = str.replace("\n", "\\n"); // 3.替换值中的换行符
//		str = str.replace("\r", "\\r"); // 3.替换值中的换行符
//		return str;
		
		//20181009 修改替换
		str = str.replace("\\", "\\\\");
	    str = str.replace("/", "\\/");
	    str = str.replace("\"", "\\\"");
	    str = str.replace("\n", "\\n");
	    str = str.replace("\r", "\\r");
	    str = str.replace("\b", "\\b");
	    str = str.replace("\f", "\\f");
	    str = str.replace("\t", "\\t");
	    return str;
	}

	/**
	 * 对字符串进行utf-8解码,只能解码utf-8格式的编码
	 * 
	 * @param str 要解码的字符串
	 * @return 解码后的字符串，若抛出异常，返回空字符串
	 */
	public static String decode(String str) {
		try {
			return java.net.URLDecoder.decode(str, "utf-8");
		} catch (Exception e) {
			BeanCtx.log(e, "E", "字符串解码失败(" + str + ")");
			return "";
		}
	}

	/**
	 * 兼容解码方式，先判断是不是ISO-8859-1编码，否则以utf-8进行解码
	 * @param str 要解码的字符串
	 * @return 解码后的字符串，若抛出异常，返回空字符串
	 */
	public static String decodeAll(String str) {
		//20180428 修改解码方法，兼容Oracle/MySQL数据库，使用Tools.encode(fileName)进行解码
		try {
			if (str.equals(new String(str.getBytes("ISO-8859-1"), "ISO-8859-1"))) {
				str = Tools.decodeUrl(str);
			} else {
				str = Tools.decode(str);
			}
		} catch (UnsupportedEncodingException e1) {
			BeanCtx.log(e1, "E", "字符串解码失败(" + str + ")");
			return "";
		}
		return str;
	}

	/**
	 * 对字符串进行utf-8编码
	 * 
	 * @param str 要编码的字符串
	 * @return 返回编码后的字符串
	 */
	public static String encode(String str) {
		try {
			String code = java.net.URLEncoder.encode(str, "utf-8");
			code = code.replace("+", "%20");
			return code;
		} catch (Exception e) {
			BeanCtx.log(e, "E", "字符串编码失败(" + str + ")");
			return str;
		}
	}

	/**
	 * 把字符串编码为base64格式的
	 * 
	 * @param str 要编码的字符串
	 * @return 返回编码后的字符串
	 */
	public static String base64(String str) {
		if (Tools.isBlank(str))
			return "";
		try {
			return new String(org.apache.commons.codec.binary.Base64.encodeBase64(str.getBytes()), "UTF-8");
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 解码base64格式的字符串
	 * 
	 * @param str 要解码的字符串
	 * @return 返回解码后的字符串
	 */
	public static String unBase64(String str) {
		if (Tools.isBlank(str))
			return "";
		try {
			return new String(org.apache.commons.codec.binary.Base64.decodeBase64(str.getBytes("UTF-8")));
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 判断字符串是否为null或"null"或长度为0
	 * 
	 * @param string 要判断的字符串
	 * @return 判断结果，true为空，false为非空
	 */
	private static boolean isEmpty(String string) {
		return (string == null) || (string.equals("null")) || (string.length() == 0);
	}

	/**
	 * 判断字符串是否为空值或去掉前后空格长度为0
	 * 
	 * @param string 要判断的字符串
	 * @return 判断结果，true为空，false为非空
	 */
	public static boolean isBlank(String string) {
		return (isEmpty(string)) || (string.trim().length() == 0);
	}

	/**
	 * 判断字符串是否不为空值
	 * 
	 * @param string 要判断的字符串
	 * @return 判断结果，true为非空，false为空
	 */
	public static boolean isNotBlank(String string) {
		return !isBlank(string);
	}

	/**
	 * 判断字符串是否由字母数字和下划线组成
	 * 
	 * @param str 要判断的字符串
	 * @return 返回true表示成立，false表示不成立
	 */
	public static Boolean isString(String str) {
		if (Tools.isBlank(str)) {
			return true;
		}
		Boolean bl = false;
		// 首先,使用Pattern解释要使用的正则表达式，其中^表是字符串的开始，$表示字符串的结尾。
		Pattern pt = Pattern.compile("^[0-9a-zA-Z_.]+$");
		Matcher mt = pt.matcher(str);
		if (mt.matches()) {
			bl = true;
		}
		return bl;

	}

	/**
	 * 根据url传入参数对字符串中的{}变量进行格式化
	 * 
	 * @param str 要格式化的string
	 * @param isSql 是否进行sql格式化，可用于防止sql注入在分析sql语句时请用true
	 * @return 返回格式化后的字符串
	 */
	public static String parserStrByQueryString(String str, boolean isSql) {
		if (Tools.isBlank(str))
			return "";
		str = str.replace("{Userid}", BeanCtx.getUserid());
		String startCode = "{";
		String endCode = "}";
		int spos = str.indexOf(startCode);
		if (spos == -1) {
			return str;
		} // 没有{符号直接返回
		StringBuilder newHtmlStr = new StringBuilder(str.length());
		while (spos != -1) {
			int epos = str.indexOf(endCode);
			String fdName = str.substring(spos + 1, epos);
			String lStr = str.substring(0, spos);
			str = str.substring(epos + 1, str.length());
			newHtmlStr.append(lStr);
			String fdValue = BeanCtx.g(fdName);
			if (isSql) {
				fdValue = Rdb.formatArg(fdValue); // 防止sql注入
			}

			newHtmlStr.append(fdValue);
			spos = str.indexOf(startCode);
		}
		newHtmlStr.append(str);
		return newHtmlStr.toString();
	}

	/**
	 * 根据url传入参数对字符串中的{}变量进行格式化
	 * 
	 * @param str 要格式化的string
	 * @return 返回字符串
	 */
	public static String parserStrByQueryString(String str) {
		return parserStrByQueryString(str, false);
	}

	/**
	 * 根据Document文档对像对字符串中的{}变量进行文档中的字段值替换
	 * 
	 * @param doc 数据文档
	 * @param str 带有 {字段名}的字符串
	 * @return 返回字符串
	 */
	public static String parserStrByDocument(Document doc, String str) {
		if (Tools.isBlank(str)) {
			return "";
		}
		str = str.replace("{Userid}", BeanCtx.getUserid());
		String startCode = "{";
		String endCode = "}";
		int spos = str.indexOf(startCode);
		if (spos == -1) {
			return str;
		} // 没有{符号直接返回
		StringBuilder newHtmlStr = new StringBuilder(str.length());
		while (spos != -1) {
			int epos = str.indexOf(endCode);
			String fdName = str.substring(spos + 1, epos);
			String lStr = str.substring(0, spos);
			str = str.substring(epos + 1, str.length());
			newHtmlStr.append(lStr);
			newHtmlStr.append(doc.g(fdName));
			spos = str.indexOf(startCode);
		}
		newHtmlStr.append(str);
		return newHtmlStr.toString();
	}

	/**
	 * 简单的混淆java源代码
	 * 
	 * @param javaCode java代码，字符串类型
	 * @return 混淆后的java代码
	 */
	public static String mixJavaCode(String javaCode) {
		javaCode = javaCode.replace("@Override", "");
		javaCode = javaCode.replaceAll("\\/\\/[^\\n]*|\\/\\*([^\\*^\\/]*|[\\*^\\/*]*|[^\\**\\/]*)*\\*\\/", ""); // 去掉注解
		javaCode = javaCode.replace("\n", ""); // 去掉换行
		javaCode = javaCode.replace("\u0009", ""); // 去掉制表符
		javaCode = javaCode.replaceAll("\\s{2,}", " "); // 去掉空格
		return javaCode;
	}

	/**
	 * 将字符串写入文件，支持指定写入时的编码方式
	 * 
	 * @param filePath 要生成的文件全路径含文件名
	 * @param content 要写入的字符串
	 * @param charset 写入时的编码方式 utf-8,gbk,gb2312等
	 * @param append true表示追加写入,false表示清空已有内容
	 * @return true表示写入成功，false表示写入失败
	 */
	public static boolean writeStringToFile(String filePath, String content, String charset, boolean append) {
		File file = new File(filePath);
		try {
			FileUtils.writeStringToFile(file, content, charset, append);
			return true;
		} catch (Exception e) {
			BeanCtx.log(e, "E", "文件路径错误，请确定路径是否正确filepath=" + filePath);
			return false;
		}
	}

	/**
	 * 把标准的Xml字符串转换成为hashmap对像
	 * 
	 * @param xml xml格式的字符串{@code <Items><WFItem name=\"NewField\">linkey</WFItem><WFItem name=\"MeetingAddress\">新的&amp;会议室</WFItem></Items>}
	 * @return 返回字段名和字段值的map对像
	 */
	public static HashMap<String, String> xmlStr2Map(String xml) {
		HashMap<String, String> fdMap = new HashMap<String, String>();
		int max = 0;
		String startCode = "<WFItem name=\"";
		int spos = xml.indexOf(startCode);
		if (spos == -1) {
			return fdMap;
		}
		while (spos != -1) {
			max++;
			if (max > 10000) {
				break;
			}
			spos = spos + 14;
			int epos = xml.indexOf("\"", spos);
			String fdName = xml.substring(spos, epos);
			String endStr = xml.substring(epos + 1, epos + 3);
			if (!endStr.equals("/>")) {
				spos = xml.indexOf(">", epos);
				epos = xml.indexOf("</WFItem>");
				String fdValue = xml.substring(spos + 1, epos);
				if (fdValue.startsWith("<![CDATA[")) {
					fdValue = fdValue.substring(9, fdValue.length() - 3);
				}
				xml = xml.substring(epos + 9);
				fdMap.put(fdName, fdValue);
			} else {
				fdMap.put(fdName, ""); // 加入一个空值字段,这样doc.hasitem()时才会生效，不然动态表格中判断不准确
				xml = xml.substring(epos + 3);
			}
			spos = xml.indexOf(startCode);
		}
		return fdMap;
	}

	/**
	 * 将xml格式的字符串保存到本地文件中，如果字符串格式不符合xml规则，则返回失败
	 * 
	 * @param xmlStr 需要保存的XmlStr字符串
	 * @param filename 保存的文件名需要全路径如：f:/bpm/text.xml
	 * @param encType 编码方式，为空时默认为utf-8
	 * @return true:保存成功 flase:失败
	 */
	public static boolean string2XmlFile(String xmlStr, String filename, String encType) {
		return XmlParser.string2XmlFile(xmlStr, filename, encType);
	}

	/**
	 * 用IE下载指定文件
	 * 
	 * @param fullfilepath 要下载的文件全路径
	 * @param downloadfilename 下载时提示保存的文件名称
	 * @throws Exception 输入输出流异常
	 */
	public void downloadFile(String fullfilepath, String downloadfilename) throws Exception {
		/* 读取文件 */
		File file = new File(fullfilepath);
		/* 如果文件存在 */
		if (file.exists()) {
			BeanCtx.getResponse().reset();
			BeanCtx.getResponse().addHeader("Content-Disposition", "attachment; filename=\"" + downloadfilename + "\"");
			BeanCtx.getResponse().setContentType("application/x-msdownload");
			int fileLength = (int) file.length();
			BeanCtx.getResponse().setContentLength(fileLength);
			/* 如果文件长度大于0 */
			if (fileLength != 0) {
				/* 创建输入流 */
				InputStream inStream = new FileInputStream(file);
				byte[] buf = new byte[4096];
				/* 创建输出流 */
				ServletOutputStream servletOS = BeanCtx.getResponse().getOutputStream();
				int readLength;
				while (((readLength = inStream.read(buf)) != -1)) {
					servletOS.write(buf, 0, readLength);
				}
				inStream.close();
				servletOS.flush();
				servletOS.close();
			}
		} else {
			BeanCtx.log("E", "文件下载出错,找不到文件路径" + fullfilepath);
			BeanCtx.print("Error: can't found the file " + fullfilepath);
		}
	}

	/**
	 * 将字符串写入文件中
	 * 
	 * @param filePath 要生成的文件全路径含文件名
	 * @param content 要写入的字符串
	 * @param append 是否在文件末尾追加，false不追究会覆盖文件内容
	 * @return true表示写入成功，false表示写入失败
	 */
	public static boolean writeLineToFile(String filePath, String content, boolean append) {
		File file = new File(filePath);
		try {
			// BeanCtx.out("file="+file.getAbsoluteFile());
			HashSet<String> lines = new HashSet<String>();
			lines.add(content);
			FileUtils.writeLines(file, lines, append);
			return true;
		} catch (Exception e) {
			BeanCtx.log(e, "E", "文件路径错误，请确定路径是否正确filepath=" + filePath);
			return false;
		}
	}

	/**
	 * 读取文件内容到字符串中
	 * 
	 * @param filePath 要读取的文件全路径
	 * @param charset 文件编码方式utf-8 gb2312 gbk...
	 * @return 返回字符串
	 */
	public static String readFileToString(String filePath, String charset) {
		File file = new File(filePath);
		try {
			return FileUtils.readFileToString(file, charset);
		} catch (Exception e) {
			BeanCtx.log(e, "E", "文件读取错误，请确定路径是否正确!");
			return "";
		}
	}

	/**
	 * 转换项目下所有java源文件的编码从gbk转为utf-8或从utf8转gbk
	 * 
	 * @param srcDirPath 源文件所在目录如：d:/bpm/src
	 * @param destDirPath 目标文件目录如： d:/bpm/src-utf8
	 * @param srcCharset 源文件编码如：utf-8,gbk
	 * @param destCharset 目标文件编码：gbk,utf-8
	 * @return 转换后的提示结果，成功转换了几个文件，若抛出异常则返回"0"
	 */
	public static String changeProjectCharset(String srcDirPath, String destDirPath, String srcCharset,
			String destCharset) {
		// 获取所有java文件
		Collection<File> javaFileColl = FileUtils.listFiles(new File(srcDirPath), new String[] { "java" }, true);
		try {
			int i = 0;
			for (File javaFile : javaFileColl) {
				// 目标格式文件路径
				String targetDirPath = destDirPath + javaFile.getAbsolutePath().substring(srcDirPath.length());
				// 使用源格式读取数据，然后用目标编码写入文件
				FileUtils.writeLines(new File(targetDirPath), destCharset, FileUtils.readLines(javaFile, srcCharset));
				i++;
			}
			return "Successfully transformation of " + i + " files";
		} catch (Exception e) {
			BeanCtx.log(e, "E", "文件格式转换出错");
			return "0";
		}
	}

	/**
	 * 解码url中传的中文字符串,url中的中文字符串必须要用utf-8格式进行编码 js可以用encodeURIComponent()函数，java可以用Tools.encode()方法
	 * 
	 * @param qry 要解码的url
	 * @return 返回解码后的字符串
	 */
	public static String decodeUrl(String qry) {
		try {
			qry = new String(qry.getBytes("ISO-8859-1"), "UTF-8");
		} catch (Exception e) {
			BeanCtx.log(e, "E", "Query string decode error");
		}
		return qry;
	}

	/**
	 * 格式化成标准的json响应字符串
	 * 
	 * @param status 状态：ok error ....
	 * @param msg 要返回的提示消息
	 * @return 格式化后的json字符串
	 */
	public static String jmsg(String status, String msg) {
		return "{\"Status\":\"" + status + "\",\"msg\":\"" + msg + "\"}";
	}

	/**
	 * 异常信息转为字符串
	 * 
	 * @param e 异常对像
	 * @return 返回字符串
	 */
	public static String getErrorMsgFromException(Exception e) {
		try {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			return sw.toString();
		} catch (Exception e2) {
			return "错误的异常对像";
		}
	}

	/**
	 * 获得属性文件中的配置值 config.properties
	 * 
	 * @param key 属性关键字名称
	 * @return 返回配置值
	 */
	public static String getProperty(String key) {
		String propertyValue = (String) RdbCache.get(key); //首先从缓存中读取
		if (propertyValue == null) {
			InputStream inputStream = RdbCache.class.getClassLoader().getResourceAsStream("config.properties");
			Properties properties = new Properties();
			try {
				properties.load(inputStream);
				String properValue = properties.getProperty(key);
				if (Tools.isBlank(properValue)) {
					return "";
				} else {
					RdbCache.put(key, properValue); //加入缓存中去
					return properValue;
				}
			} catch (Exception e) {
				e.printStackTrace();
				BeanCtx.out("Tools.getProperty()找不到/config.properties文件");
				return "";
			} finally {
				try {
					inputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			return propertyValue;
		}
	}

	/**
	 * 从wf_num中获得应用编号
	 * 
	 * @param wf_num 设计元素的编号
	 * @return 返回应用编号appid
	 */
	public static String getAppidFromElNum(String wf_num) {
		int spos = wf_num.indexOf("_");
		String appid = "";
		if (spos != -1) {
			appid = wf_num.substring(spos + 1); // 获得应用appid
			spos = appid.indexOf("_");
			if (spos != -1) {
				appid = appid.substring(0, spos);
			}
		}
		return appid;
	}

	/**
	 * md5加密函数
	 * 
	 * @param str 要加密的字符串
	 * @return 加密后的字符串
	 */
	public static String md5(String str) {
		String re_md5 = new String();
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			re_md5 = buf.toString();
		} catch (Exception e) {
			BeanCtx.log(e, "E", "md5加密错误(" + str + ")");
		}
		return re_md5;
	}

	/**
	 * 
	 * @Description 获取request中参数的值
	 * <p>多个值的时候只返回第一个匹配值
	 *
	 * @param request HttpServletRequest对象
	 * @param key 需要获取的参数名
	 * @return 返回参数值，没有则为空
	 */
	public static String getRequestParamValue(HttpServletRequest request, String key) {

		String value = "";

		if (request == null) {
			return "";
		}
		value = request.getHeader(key);

		return value;
	}

	/**
	 * 向url地址发出get请求并返回结果字符串
	 * 
	 * @param url http地址
	 * @param charset 返回的字符串的编码格式,传空值时默认为UTF-8
	 * @return 返回url输出的字符串
	 * @throws Exception get请求异常或文件关闭异常
	 */
	public static String httpGet(String url, String charset) throws Exception {
		if (Tools.isBlank(charset)) {
			charset = "UTF-8";
		}
		CloseableHttpResponse response;
		HttpGet httpget = new HttpGet(url);
		CloseableHttpClient httpclient = HttpClients.custom().build();
		response = httpclient.execute(httpget);
		try {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				String str = streamToString(instream, charset);
				return str;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			response.close();
		}
		return "";
	}
	
	

	/**
	 * 向url地址发出get请求并返回结果字符串【BPM12.0 新增方法】
	 * 
	 * @param url http地址
	 * @param headerMap request headers的内容
	 * @param charset 返回的字符串的编码格式,传空值时默认为UTF-8
	 * @return 返回url输出的字符串
	 * @throws Exception get请求异常或文件关闭异常
	 */
	public static String httpGet(String url, Map<String, String> headerMap, String charset) throws Exception {
		if (Tools.isBlank(charset)) {
			charset = "UTF-8";
		}
		CloseableHttpResponse response;

		HttpGet httpget = new HttpGet(url);
		for (String key : headerMap.keySet()) {
			httpget.setHeader(key, headerMap.get(key));
		}

		CloseableHttpClient httpclient = HttpClients.custom().build();
		response = httpclient.execute(httpget);
		try {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				String str = streamToString(instream, charset);
				return str;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			response.close();
		}
		return "";
	}

	/**
	 * 向url地址发出get请求并返回结果字符串【BPM12.0 新增方法】
	 * 
	 * @param url http地址
	 * @param headerMap request headers的内容
	 * @param charset 返回的字符串的编码格式,传空值时默认为UTF-8
	 * @return 返回url输出的字符串
	 * @throws Exception get请求异常或文件关闭异常
	 */
	public static String httpGet(String url, Map<String, String> headerMap, Map<String, String> param, String charset)
			throws Exception {
		if (Tools.isBlank(charset)) {
			charset = "UTF-8";
		}
		CloseableHttpResponse response;

		//======================================

		//20180928添加参数
		URIBuilder uriBuilder = new URIBuilder(url);
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		for (String key : param.keySet()) {
			list.add(new BasicNameValuePair(key, param.get(key)));
		}
		uriBuilder.setParameters(list);

		HttpGet httpget = new HttpGet(uriBuilder.build());

		for (String key : headerMap.keySet()) {
			httpget.setHeader(key, headerMap.get(key));
		}
		//=======================================

		CloseableHttpClient httpclient = HttpClients.custom().build();
		response = httpclient.execute(httpget);
		try {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				String str = streamToString(instream, charset);
				return str;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			response.close();
		}
		return "";
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 * <p> ContentType默认使用application/x-www-form-urlencoded
	 * @param url 发送请求的 URL
	 * @param params 请求参数类型为{@code map<String,String>}对像
	 * @return 返回url响应的字符串
	 * @throws Exception post请求异常
	 */
	public static String httpPost(String url, Map<String, String> headerMap, HashMap<String, String> params)
			throws Exception {
		CloseableHttpResponse response;
		HttpPost httpPost = new HttpPost(url);

		//======================================
		for (String key : headerMap.keySet()) {
			httpPost.setHeader(key, headerMap.get(key));
		}
		//=======================================

		CloseableHttpClient httpclient = HttpClients.custom().build();
		List<BasicNameValuePair> formParams = new ArrayList<BasicNameValuePair>();
		for (String keyName : params.keySet()) {
			formParams.add(new BasicNameValuePair(keyName, params.get(keyName)));
		}
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formParams, "UTF-8");
		httpPost.setEntity(entity);

		response = httpclient.execute(httpPost);
		try {
			HttpEntity rsEntity = response.getEntity();
			if (rsEntity != null) {
				InputStream instream = rsEntity.getContent();
				String str = streamToString(instream, "UTF-8");
				return str;
			}
		} catch (Exception e) {
			BeanCtx.log(e, "W", "");
			return "";
		} finally {
			response.close();
		}
		return "";
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 * <p> ContentType默认使用application/json
	 * @param url 发送请求的 URL
	 * @param params 请求参数，请求参数格式为{@code fdName1=value1&fdName2=value2}
	 * @return 返回url响应的字符串
	 * @throws Exception 构造请求数据异常或post请求异常或文件关闭异常
	 */
	public static String httpPost(String url, String params) throws Exception {
		CloseableHttpResponse response;
		HttpPost httpPost = new HttpPost(url);
		CloseableHttpClient httpclient = HttpClients.custom().build();
		StringEntity myEntity = new StringEntity(params, ContentType.APPLICATION_JSON);// 构造请求数据
		httpPost.setEntity(myEntity);// 设置请求体
		response = httpclient.execute(httpPost);
		try {
			HttpEntity rsEntity = response.getEntity();
			if (rsEntity != null) {
				InputStream instream = rsEntity.getContent();
				String str = streamToString(instream, "UTF-8");
				return str;
			}
		} catch (Exception e) {
			BeanCtx.log(e, "W", "");
			return "";
		} finally {
			response.close();
		}
		return "";
	}

	/**
	 * 向指定 URL 发送POST方法的请求[BPM 12.0新增]
	 * <p> ContentType默认使用application/json
	 * @param url 发送请求的 URL
	 * @param params 请求参数，请求参数格式为{@code fdName1=value1&fdName2=value2}
	 * @param contentType 具体请求中的媒体类型信息; {@code ContentType.APPLICATION_FORM_URLENCODED}、{@code ContentType.APPLICATION_JSON}
	 * @return 返回url响应的字符串
	 * @throws Exception 构造请求数据异常或post请求异常或文件关闭异常
	 */
	public static String httpPost(String url, String params, ContentType contentType) throws Exception {
		CloseableHttpResponse response;
		HttpPost httpPost = new HttpPost(url);
		CloseableHttpClient httpclient = HttpClients.custom().build();
		StringEntity myEntity = new StringEntity(params, contentType);// 构造请求数据
		httpPost.setEntity(myEntity);// 设置请求体
		response = httpclient.execute(httpPost);
		try {
			HttpEntity rsEntity = response.getEntity();
			if (rsEntity != null) {
				InputStream instream = rsEntity.getContent();
				String str = streamToString(instream, "UTF-8");
				return str;
			}
		} catch (Exception e) {
			BeanCtx.log(e, "W", "");
			return "";
		} finally {
			response.close();
		}
		return "";
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 * <p> ContentType默认使用application/x-www-form-urlencoded
	 * @param url 发送请求的 URL
	 * @param params 请求参数类型为{@code map<String,String>}对像
	 * @return 返回url响应的字符串
	 * @throws Exception post请求异常
	 */
	public static String httpPost(String url, HashMap<String, String> params) throws Exception {
		CloseableHttpResponse response;
		HttpPost httpPost = new HttpPost(url);
		CloseableHttpClient httpclient = HttpClients.custom().build();
		List<BasicNameValuePair> formParams = new ArrayList<BasicNameValuePair>();
		for (String keyName : params.keySet()) {
			formParams.add(new BasicNameValuePair(keyName, params.get(keyName)));
		}
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formParams, "UTF-8");
		httpPost.setEntity(entity);

		response = httpclient.execute(httpPost);
		try {
			HttpEntity rsEntity = response.getEntity();
			if (rsEntity != null) {
				InputStream instream = rsEntity.getContent();
				String str = streamToString(instream, "UTF-8");
				return str;
			}
		} catch (Exception e) {
			BeanCtx.log(e, "W", "");
			return "";
		} finally {
			response.close();
		}
		return "";
	}

	/**
	 * 把InputStream转换为字符串
	 * 
	 * @param is InputStream对像
	 * @param charset 如：UTF-8等
	 * @return 从流中得到的字符串
	 * @throws Exception 输入流异常
	 */
	public static String streamToString(InputStream is, String charset) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

}
