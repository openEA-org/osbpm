package cn.linkey.rulelib.S001;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.research.ws.wadl.Doc;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * 同步代码提示配置
 * @author alibao
 *
 */
final public class R_S001_B084 implements LinkeyRule {

	// 202003 add by alibao  ================================start
	// 兼容linux路径，分隔符为 /
	private static final String OS = System.getProperty("os.name").toLowerCase();
	private static String separatorOS = "\\";

	static {
		if (OS.indexOf("linux") >= 0) {
			separatorOS = "//";
		}
	}
	// 202003 add by alibao  ================================end

	private String basePath = BeanCtx.getWebAppsPath() + "linkey" + separatorOS + "bpm" + separatorOS + "editor"
			+ separatorOS + "ace";

	@Override
	public String run(HashMap<String, Object> params) throws Exception {
		//params为运行本规则时所传入的参数

		JSONObject retrunJson = new JSONObject();

		// 同步Java 提示配置文件
		syncJavaConfig();

		// 同步JS 提示配置文件
		syncJSConfig();

		retrunJson.put("Status", "1");
		retrunJson.put("msg", "更新成功！");
		retrunJson.put("linkeyData", "[]");

		BeanCtx.p(retrunJson.toJSONString());

		return "";
	}

	/**
	 * 同步JS 提示配置文件
	 */
	private void syncJSConfig() {
		String fileName = "linkeyJS.json";
		String filePath = basePath + separatorOS + fileName;

		JSONArray jsonArray = new JSONArray();

		// 同步自定义输出
		syncCustomHint(jsonArray, "2");

		Tools.writeStringToFile(filePath, jsonArray.toJSONString(), "utf-8", false);

	}

	/**
	 * 同步Java 提示配置文件
	 */
	private void syncJavaConfig() {

		String fileName = "linkeyJava.json";
		String filePath = basePath + separatorOS + fileName;

		JSONArray jsonArray = new JSONArray();
		// 同步Java类配置
		synchJava(jsonArray);

		// 同步自定义输出
		syncCustomHint(jsonArray, "1");

		// 同步自定义class或jar
		syncClass(jsonArray);
// 		BeanCtx.p(jsonArray.toJSONString());

		// 同步jar包
		syncJar(jsonArray);

		Tools.writeStringToFile(filePath, jsonArray.toJSONString(), "utf-8", false);
	}

	/**
	 * 同步jar包的方法
	 *
	 * @param 配置的json数组
	 */
	private void syncJar(JSONArray jsonArray) {
		// 同步jar的class的方法
		String sql5 = "select * from BPM_CodeHintingConfig where fileType = 2 and CodeType =1";
		Document[] docs5 = Rdb.getAllDocumentsBySql(sql5);
		for (Document doc : docs5) {
			String packageName = doc.g("filePath");
			List<String> classNames = getClassName(packageName, true);
			if (classNames != null) {
				for (String className : classNames) {
					Document docTemp = new Document("");
					docTemp.s("filePath", className);
					docTemp.s("meta", doc.g("meta"));
					docTemp.s("score", doc.g("score"));
					
					syncClass(jsonArray, docTemp);
				}
			}
		}
	}

	/**
	 * 
	 * 同步自定义输出语句
	 *
	 * @param 配置的json数组
	 */
	private void syncCustomHint(JSONArray jsonArray, String codeType) {

		// 同步自定义输出语句
		String sql3 = "select * from BPM_CodeHintingConfig where fileType = 3 and CodeType = " + codeType + "";
		Document[] docs3 = Rdb.getAllDocumentsBySql(sql3);
		for (Document doc : docs3) {
			String meta = doc.g("meta");
			String caption = doc.g("caption");
			String value = doc.g("value");
			String score = doc.g("score");

			JSONObject jsonObj = new JSONObject();
			jsonObj.put("meta", meta);
			jsonObj.put("caption", caption);
			jsonObj.put("value", value);
			jsonObj.put("score", score);

			jsonArray.add(jsonObj);
		}
	}

	/**
	 * 用于同步自定义Class
	 * @param jsonArray 配置的json数组
	 */
	private void syncClass(JSONArray jsonArray) {

		// 同步class的方法
		String sql4 = "select * from BPM_CodeHintingConfig where fileType = 1 and CodeType = 1";
		Document[] docs4 = Rdb.getAllDocumentsBySql(sql4);

		for (Document doc : docs4) {
			syncClass(jsonArray, doc);
		}
	}

	/**
	 * 
	 * 同步 class的代码提示
	 *
	 * @param jsonArray 配置的json数组
	 * @param doc  class的doc对象
	 */
	private void syncClass(JSONArray jsonArray, Document doc) {
		String classPath = doc.g("filePath");
		String meta = doc.g("meta");
		String score = doc.g("score");
		String className = classPath.substring(classPath.lastIndexOf(".") + 1, classPath.length());
		
		JSONObject jsonClass = new JSONObject();
		jsonClass.put("meta", meta);
		jsonClass.put("caption", classPath);
		jsonClass.put("value", classPath);
		jsonClass.put("score", score);

		jsonArray.add(jsonClass);
		
		try {

			Class<?> localObj = Class.forName(classPath);
			
			// 获取类方法
			Method[] localMethods = localObj.getMethods();
			for (Method method : localMethods) {

				StringBuilder methodStrB = new StringBuilder(method.getName());
				String methodStr = "";
				// 获取类参数
				Parameter[] parmsNames = method.getParameters();
				if (parmsNames.length > 0) {
					methodStrB.append("(");
					for (Parameter param : parmsNames) {
						methodStrB.append(param.getType().getSimpleName() + " " + param.getName() + ",");
					}
					methodStr = methodStrB.substring(0, methodStrB.length() - 1) + ")";
				} else {
					methodStr = methodStrB.append("()").toString();
				}

				JSONObject jsonObj = new JSONObject();
				jsonObj.put("meta", className);
				jsonObj.put("caption", methodStr);
				jsonObj.put("value", methodStr);
				jsonObj.put("score", score);

				jsonArray.add(jsonObj);
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 同步Java类配置
	 * @param jsonArray 
	 */
	private void synchJava(JSONArray jsonArray) {

		// 同步规则帮助树配置中方法
		String sql1 = "select (select B.FolderName from bpm_systemhelp B where A.ParentFolderid = B.Folderid and B.FolderType=1) className, A.FolderName from BPM_SystemHelp A where A.FolderType = 2 order by className";
		Document[] docs = Rdb.getAllDocumentsBySql(sql1);
		for (Document doc : docs) {
			String className = doc.g("className");
			String methodName = doc.g("FolderName");
			className = className.substring(className.lastIndexOf(".") + 1, className.length());
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("meta", className);
			jsonObj.put("caption", methodName);
			jsonObj.put("value", methodName);
			jsonObj.put("score", 1);

			jsonArray.add(jsonObj);
		}

		// 同步规则帮助树中class
		String sql2 = "select DISTINCT FolderName from bpm_systemhelp where FolderType=1";
		Document[] docs2 = Rdb.getAllDocumentsBySql(sql2);
		for (Document doc : docs2) {
			String methodName = doc.g("FolderName");
			//			className = className.substring(className.lastIndexOf(".") + 1, className.length());
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("meta", "class");
			jsonObj.put("caption", methodName);
			jsonObj.put("value", methodName);
			jsonObj.put("score", 6);

			jsonArray.add(jsonObj);
		}

	}

	/**
	 * 获取某包下（包括该包的所有子包）所有类
	 * 
	 * @param packageName
	 *            包名
	 * @return 类的完整名称
	 */
	public static List<String> getClassName(String packageName) {
		return getClassName(packageName, true);
	}

	/**
	 * 获取某包下所有类
	 * 
	 * @param packageName
	 *            包名
	 * @param childPackage
	 *            是否遍历子包
	 * @return 类的完整名称
	 */
	public static List<String> getClassName(String packageName, boolean childPackage) {
		List<String> fileNames = null;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		String packagePath = packageName.replace(".", "/");
		URL url = loader.getResource(packagePath);
		if (url != null) {
			String type = url.getProtocol();
			if (type.equals("file")) {
				fileNames = getClassNameByFile(url.getPath(), null, childPackage);
			} else if (type.equals("jar")) {
				fileNames = getClassNameByJar(url.getPath(), childPackage);
			}
		} else {
			fileNames = getClassNameByJars(((URLClassLoader) loader).getURLs(), packagePath, childPackage);
		}
		return fileNames;
	}

	/**
	 * 从项目文件获取某包下所有类
	 * 
	 * @param filePath
	 *            文件路径
	 * @param className
	 *            类名集合
	 * @param childPackage
	 *            是否遍历子包
	 * @return 类的完整名称
	 */
	private static List<String> getClassNameByFile(String filePath, List<String> className, boolean childPackage) {
		List<String> myClassName = new ArrayList<>();
		File file = new File(filePath);
		File[] childFiles = file.listFiles();
		for (File childFile : childFiles) {
			if (childFile.isDirectory()) {
				if (childPackage) {
					myClassName.addAll(getClassNameByFile(childFile.getPath(), myClassName, childPackage));
				}
			} else {
				String childFilePath = childFile.getPath();
				if (childFilePath.endsWith(".class")) {
					childFilePath = childFilePath.substring(childFilePath.indexOf("\\classes") + 9,
							childFilePath.lastIndexOf("."));
					childFilePath = childFilePath.replace("\\", ".");
					myClassName.add(childFilePath);
				}
			}
		}

		return myClassName;
	}

	/**
	 * 从jar获取某包下所有类
	 * 
	 * @param jarPath
	 *            jar文件路径
	 * @param childPackage
	 *            是否遍历子包
	 * @return 类的完整名称
	 */
	private static List<String> getClassNameByJar(String jarPath, boolean childPackage) {
		List<String> myClassName = new ArrayList<>();
		String[] jarInfo = jarPath.split("!");
		String jarFilePath = jarInfo[0].substring(jarInfo[0].indexOf("/"));
		String packagePath = jarInfo[1].substring(1);
		try {
			JarFile jarFile = new JarFile(jarFilePath);
			Enumeration<JarEntry> entrys = jarFile.entries();
			while (entrys.hasMoreElements()) {
				JarEntry jarEntry = entrys.nextElement();
				String entryName = jarEntry.getName();
				if (entryName.endsWith(".class")) {
					if (childPackage) {
						if (entryName.startsWith(packagePath)) {
							entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
							myClassName.add(entryName);
						}
					} else {
						int index = entryName.lastIndexOf("/");
						String myPackagePath;
						if (index != -1) {
							myPackagePath = entryName.substring(0, index);
						} else {
							myPackagePath = entryName;
						}
						if (myPackagePath.equals(packagePath)) {
							entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
							myClassName.add(entryName);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return myClassName;
	}

	/**
	 * 从所有jar中搜索该包，并获取该包下所有类
	 * 
	 * @param urls
	 *            URL集合
	 * @param packagePath
	 *            包路径
	 * @param childPackage
	 *            是否遍历子包
	 * @return 类的完整名称
	 */
	private static List<String> getClassNameByJars(URL[] urls, String packagePath, boolean childPackage) {
		List<String> myClassName = new ArrayList<>();
		if (urls != null) {
			for (int i = 0; i < urls.length; i++) {
				URL url = urls[i];
				String urlPath = url.getPath();
				// 不必搜索classes文件夹
				if (urlPath.endsWith("classes/")) {
					continue;
				}
				String jarPath = urlPath + "!/" + packagePath;
				myClassName.addAll(getClassNameByJar(jarPath, childPackage));
			}
		}
		return myClassName;
	}
}
