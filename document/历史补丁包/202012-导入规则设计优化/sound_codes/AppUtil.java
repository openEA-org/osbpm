package cn.linkey.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import cn.linkey.dao.Rdb;
import cn.linkey.dao.RdbCache;
import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.factory.BeanCtx;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;
import cn.linkey.util.XmlParser;

/**
 * 通用App函数集合
 * 
 * @author Administrator 本类为单例类
 */
public class AppUtil {

	/**
	 * 返回设计打包导出时的路径
	 * 
	 * @return
	 */
	public static String getPackagePath() {
		String path = BeanCtx.getSystemConfig("SystemPackagePath");
		if (Tools.isBlank(path)) {
			path = BeanCtx.getWebAppsPath() + "outfile" + File.separatorChar;
		}
		return path;
	}

	/**
	 * 根据wf_num的编号获得应用解析类
	 * 
	 * @param wf_num
	 * @return 返回解析类的id
	 */
	public static String getAppBeanidByNum(String wf_num) {
		if (wf_num.startsWith("P_")) {
			//页面
			return "page";
		} else if (wf_num.startsWith("T_")) {
			//导航树
			return "navtree";
		} else if (wf_num.startsWith("F_")) {
			//表单
			return "form";
		} else if (wf_num.startsWith("R_")) {
			//规则
			return "rule";
		} else if (wf_num.startsWith("D_")) {
			//数据源
			String[] tArray = Tools.split(wf_num, "_");
			if (tArray[2].startsWith("J")) {
				return "json";
			} else if (tArray[2].startsWith("T")) {
				return "treedata";
			} else if (tArray[2].startsWith("C")) {
				return "categorydata";
			} else if (tArray[2].startsWith("X")) {
				return "xmldata";
			}
		} else if (wf_num.startsWith("V_")) {
			//视图
			String[] tArray = Tools.split(wf_num, "_");
			if (tArray[2].startsWith("G")) {
				return "view";
			} else if (tArray[2].startsWith("E")) {
				return "editorgrid";
			} else if (tArray[2].startsWith("T")) {
				return "treegrid";
			} else if (tArray[2].startsWith("C")) {
				return "categorygrid";
			} else if (tArray[2].startsWith("M")) {
				return "customgrid";
			} else if (tArray[2].startsWith("R")) {
				return "viewchart";
			}
		} else if (wf_num.startsWith("S_")) {
			return "DataDictionary";
		}
		return "";
	}

	/**
	 * 检测规则代码是否符合要求
	 * 
	 * @param ruleCode 规则代码
	 * @param appid 所属应用
	 * @param ruleNum 规则编号
	 * @return 返回false表示不符合要求，true表示符合要求
	 */
	public static boolean checkRuleCode(String ruleCode, String appid, String ruleNum) {
		//检测包名
		if (ruleCode.indexOf("package cn.linkey.rulelib." + appid + ";") == -1) {
			BeanCtx.print(
					"{\"Status\":\"error\",\"msg\":\"" + BeanCtx.getMsg("Designer", "DifferentRulePackage") + "\"}");
			return false;
		}

		//检测类名
		if (ruleCode.indexOf(" " + ruleNum + " ") == -1) {
			BeanCtx.print("{\"Status\":\"error\",\"msg\":\"" + BeanCtx.getMsg("Designer", "DifferentRuleNum") + "\"}");
			return false;
		}
		
		return true;
	}

	/**
	 * 自动计算新增设计元素的编号
	 * 
	 * @param sql为获得所有设计元素按编号desc排列的集合
	 * @return
	 */
	public static String getElNewNum(String sql) {
		String maxid = Rdb.getValueTopOneBySql(sql); //获得最大的编号
		if (Tools.isBlank(maxid)) {
			//没有的情况下，第一个创建
			return "";
		} else {
			//已经有设计的情况下自动增长
			if (maxid.length() > 3) {
				try {
					int no = Integer.parseInt(maxid.substring(maxid.length() - 3)); //最最后三位的值
					no++;
					String newno = String.valueOf(no);
					if (newno.length() < 3) {
						newno = "00000" + newno;
						newno = newno.substring(newno.length() - 3);
					}
					newno = maxid.substring(0, maxid.length() - 3) + newno;
					return newno;
				} catch (Exception e) {
					return "";
				}
			}
		}
		return "";
	}

	/**
	 * 根据设计unid获取文档对像,不支持缓存功能
	 * 
	 * @param tableName 设计所在数据库表
	 * @param docUnid 设计的unid编号
	 * @return 返回设计文档
	 */
	public static Document getDocByUnid(String tableName, String docUnid) {
		if (Tools.isBlank(docUnid) || Tools.isBlank(tableName)) {
			BeanCtx.log("E", "根据文档Unid获取设计文档时出错,docUnid或tableName不能传空值!");
			return null;
		}
		//从数据库中拿
		String sql = "select * from " + tableName + " where WF_OrUnid='" + docUnid + "'";
		return Rdb.getDocumentBySql(tableName, sql);
	}

	/**
	 * 根据设计的id获取文档对像
	 * 
	 * @param tableName 设计所在数据表名
	 * @param idFdName 关键字段的名称
	 * @param id 设计元素的id号
	 * @param isCache true表示允许缓存,false表示不可以
	 * @return 返回设计文档
	 */
	public static Document getDocByid(String tableName, String idFdName, String id, boolean isCache) {
		id = RdbCache.getElementExtendsNum(id);//看是否有继承设计

		if (isCache && !BeanCtx.getSystemConfig("SysDeveloperMode").equals("1")) {
			//从缓存中查找,只有在非开发模式下才启用缓存功能
			Document doc = (Document) RdbCache.get("TempCache", id);
			if (doc == null) {
				String sql = "select * from " + tableName + " where " + idFdName + "='" + Rdb.formatArg(id)
						+ "' and Status='1'";
				doc = Rdb.getDocumentBySql(tableName, sql);
				RdbCache.put("TempCache", id, doc); //30分钟不使用就会被清除缓存
				return doc;
			} else {
				Document newDoc = BeanCtx.getDocumentBean("");
				doc.copyAllItems(newDoc);
				return newDoc;
			}
		} else {
			//每次从数据库中拿取,开发者模式下每次从数据库中拿
			String sql = "select * from " + tableName + " where " + idFdName + "='" + Rdb.formatArg(id)
					+ "' and Status='1'";
			//System.out.println("空吗："+Rdb.getDocumentBySql(tableName, sql).isNull());
			return Rdb.getDocumentBySql(tableName, sql);
		}
	}

	/**
	 * 根据流程id获得所有流程模型文档
	 * 
	 * @param processid 流程id号
	 * @return 返回流程所有节点的文档集合
	 */
	public static LinkedHashSet<Document> getAllModDocByProcessid(String processid) {
		String tableList = BeanCtx.getSystemConfig("ProcessModTableList");
		String[] tableArray = Tools.split(tableList);
		LinkedHashSet<Document> dc = new LinkedHashSet<Document>();
		for (String tableName : tableArray) {
			String sql = "select * from " + tableName + " where Processid='" + processid + "'";
			LinkedHashSet<Document> subdc = Rdb.getAllDocumentsSetBySql(sql);
			for (Document doc : subdc) {
				doc.s("WF_OrTableName", tableName); //文档所在数据库的表名
			}
			dc.addAll(subdc);
		}
		return dc;
	}

	/**
	 * 根据应用id获得此应用下的所有流程模型文档
	 * 
	 * @pramam appid 应用编号
	 * @return 返回所有此应用下的所有流程节点集合
	 */
	public static LinkedHashSet<Document> getAllModDocByAppid(String appid) {
		LinkedHashSet<Document> dc = new LinkedHashSet<Document>();
		//首先获得此应用下的所有流程processid
		String sql = "select Processid from BPM_ModProcessList where WF_Appid='" + appid + "'";
		HashSet<String> processidList = Rdb.getValueSetBySql(sql);
		for (String processid : processidList) {
			LinkedHashSet<Document> subdc = getAllModDocByProcessid(processid);
			dc.addAll(subdc);
		}
		return dc;
	}

	/**
	 * 根据应用编号删除此应用下的所有流程
	 * 
	 * @param processid
	 * @return 返回删除的文档数
	 */
	public static int removeProcessByAppid(String appid) {
		int i = 0;
		String sql = "select Processid from BPM_ModProcessList where WF_Appid='" + appid + "'";
		HashSet<String> processidList = Rdb.getValueSetBySql(sql);
		for (String processid : processidList) {
			i += removeProcess(processid);
		}
		return i;
	}

	/**
	 * 根据流程id号删除流程
	 * 
	 * @param processid
	 * @return 返回删除的文档数
	 */
	public static int removeProcess(String processid) {
		String tableList = BeanCtx.getSystemConfig("ProcessModTableList");
		String[] tableArray = Tools.split(tableList);
		int i = 0;
		for (String tableName : tableArray) {
			String sql = "delete from " + tableName + " where Processid='" + processid + "'";
			i += Rdb.execSql(sql);
		}
		return i;
	}

	/**
	 * 安装设计元素包
	 * 
	 * @param fileList 设计包的文件路径，可以传入多个设计包一次导入 格式为：attachment/201402/00923432432slilksdsf00.xml 文件一定要放在附件路径下才可以导入
	 * @param replaceFlag=2 覆盖旧的设计 =1表示设计已存在时跳过
	 */
	public static String importElement(LinkedHashSet<String> fileList, String replaceFlag) {
		BeanCtx.setCtxData("WF_NoEncode", "1"); // 指定存盘时不进行编码操作
		String tableName = "";
		if (fileList.size() == 0) {
			return "Please upload an XML file!";
		}
		int i = 0, j = 0, x = 0;
		for (String fileName : fileList) {
			String filePath = BeanCtx.getAppPath() + fileName;
			File file = FileUtils.getFile(filePath);
			if (file.exists()) {
				HashSet<Document> dc = Documents.xmlfile2dc(filePath);
				for (Document indoc : dc) {
					tableName = indoc.g("WF_OrTableName");
					String wf_orunid = indoc.g("WF_OrUnid");
					if (Tools.isBlank(tableName) || Tools.isBlank(wf_orunid)) {
					} else if (StringUtils.equalsIgnoreCase(tableName, "BPM_ModProcessList")
							&& replaceFlag.equals("2")) {
						deleteProcessList(tableName, wf_orunid);
					}
				}
			} else {
				return filePath.replace("\\", "/") + " does not exist!";
			}
		}
		for (String fileName : fileList) {
			String filePath = BeanCtx.getAppPath() + fileName;
			File file = FileUtils.getFile(filePath);
			if (file.exists()) {
				HashSet<Document> dc = Documents.xmlfile2dc(filePath);
				for (Document indoc : dc) {
					tableName = indoc.g("WF_OrTableName");
					String wf_orunid = indoc.g("WF_OrUnid");
					if (Tools.isBlank(tableName) || Tools.isBlank(wf_orunid)) {
						//BeanCtx.out("不合要求记录");
						x++;
					} else {
						String sql = "select WF_OrUnid from " + tableName + " where WF_OrUnid='" + wf_orunid + "'";
						
						if (Rdb.hasRecord(sql)) {
							//覆盖旧的文档
							if (replaceFlag.equals("2")) {
								sql = "delete from " + tableName + " where WF_OrUnid='" + wf_orunid + "'";
								Rdb.execSql(sql);
								indoc.save();
								i++;
							} else {
								//BeanCtx.out("跳过");
								j++;
							}
						} else {
							//记录不存在
							indoc.save();
							i++;
						}
					}
					// 20200616 add by feiyilin ================= start
					// 导入实时运行的规则元素时自动保存编译
					if (Tools.isNotBlank(indoc.g("RuleNum")) && Tools.isNotBlank(indoc.g("RuleCode")) && "0".equals(indoc.g("CompileFlag"))) {
						autosaveRuleCodeAndCompile(indoc.g("WF_OrUnid"));
					}
					// 20200616 add by feiyilin ================== end
				}
			} else {
				return filePath.replace("\\", "/") + " does not exist!";
			}
		}
		BeanCtx.userlog("--", "导入设计文档", "在表(" + tableName + ")中导入了设计!");
		return BeanCtx.getMsg("Designer", "ImportXmlFileMsg", i, j, x); //成功必须返回1，否则表示退出存盘
	}

	private static void deleteProcessList(String tableName, String wf_orunid) {
		String sql = "select WF_OrUnid, processid from " + tableName + " where WF_OrUnid='" + wf_orunid + "'";
		Document doc = Rdb.getDocumentBySql(sql);
		if (!doc.isNull()) {
			//覆盖旧的文档
			String processid = doc.g("processid");
			sql = "delete from BPM_ModProcessList where WF_OrUnid='" + wf_orunid + "'";
			Rdb.execSql(sql);

			sql = "delete from BPM_ModEventList where Processid = '" + processid + "'";
			Rdb.execSql(sql);

			sql = "delete from BPM_ModGatewayList where Processid = '" + processid + "'";
			Rdb.execSql(sql);

			sql = "delete from BPM_ModGraphicList where Processid = '" + processid + "'";
			Rdb.execSql(sql);

			sql = "delete from BPM_ModSequenceFlowList where Processid = '" + processid + "'";
			Rdb.execSql(sql);

			sql = "delete from BPM_ModSubProcessList where Processid = '" + processid + "'";
			Rdb.execSql(sql);

			sql = "delete from BPM_ModTaskList where Processid = '" + processid + "'";
			Rdb.execSql(sql);
		}

	}

	/**
	 * 安装一个应用
	 * 
	 * @param fileName 应用包所在文件名称,文件一定要放在附件路径下才可以导入 格式为：attachment/201401/9324234000sdfdsfds.gif
	 * @param replaceFlag=2 覆盖旧的应用设计 =1表示应用设计已存在时跳过
	 */
	public static String installApp(String fileName, String replaceFlag) {

		BeanCtx.setDocNotEncode(); // 指定存盘时不进行编码操作

		String tableName = "", appid = "";
		int i = 0, j = 0, x = 0, t = 0;
		String filePath = BeanCtx.getAppPath() + fileName;
		File file = FileUtils.getFile(filePath);
		if (file.exists()) {
			LinkedHashSet<Document> dc = Documents.xmlfile2dc(filePath);
			//20180907 add by alibao  修复应用覆盖时，流程增量问题===================================================
			for (Document indoc : dc) {
				if (StringUtils.equalsIgnoreCase(indoc.g("WF_OrTableName"), "BPM_ModProcessList")
						&& replaceFlag.equals("2")) {
					deleteProcessList(indoc.g("WF_OrTableName"), indoc.g("WF_OrUnid"));
				}
			}
			//===========================================================
			for (Document indoc : dc) {
				tableName = indoc.g("WF_OrTableName");

				// --------------------------20181203新增打包应用时指定数据库类型-------------------------------
				//indoc.setDbType(indoc.g("dbType"));
				indoc.setDbType( Rdb.getDbType());
				// --------------------------20181203 END------------------------------------------------

				if (tableName.equalsIgnoreCase("BPM_TableConfig")) {
					//说明是数据库表的配置信息,要进行实体表的创建动作
					//BeanCtx.out("准备创建表=="+indoc.g("TableName"));
					if (indoc.g("CreateRdbTable").equals("1")) {
						//只有勾上了安装时需要创建实例表时才进行创建
						String dataSourceid = indoc.g("DataSourceid");
						if (Tools.isBlank(dataSourceid)) {
							dataSourceid = "default";
						} //缺省数据源id
						if (!Rdb.isExistTable(dataSourceid, indoc.g("TableName"))) {
							Rdb.creatTable(dataSourceid, indoc.g("TableName"), indoc.g("FieldConfig"));
							t++;
						}
					}
				}
				if (Tools.isBlank(appid)) {
					appid = indoc.g("WF_Appid");
				}
				if (Tools.isBlank(tableName) || Tools.isBlank(indoc.g("WF_OrUnid"))) {
					//BeanCtx.out("不合要求记录");
					x++;
				} else {
					String sql = "select WF_OrUnid from " + indoc.g("WF_OrTableName") + " where WF_OrUnid='"
							+ indoc.g("WF_OrUnid") + "'";
					if (Rdb.hasRecord(sql)) {
						//覆盖旧的文档
						if (replaceFlag.equals("2")) {
							sql = "delete from " + indoc.g("WF_OrTableName") + " where WF_OrUnid='"
									+ indoc.g("WF_OrUnid") + "'";

							Rdb.execSql(sql);//删除已存在的旧设计元素
							indoc.save();
							i++;
						} else {
							//BeanCtx.out("跳过");
							j++;
						}
					} else {
						//记录不存在
						indoc.save();
						i++;
					}
				}
				// 20200616 add by feiyilin ================= start
				// 导入实时运行的规则元素时自动保存编译
				if (Tools.isNotBlank(indoc.g("RuleNum")) && Tools.isNotBlank(indoc.g("RuleCode")) && "0".equals(indoc.g("CompileFlag"))) {
					autosaveRuleCodeAndCompile(indoc.g("WF_OrUnid"));
				}
				// 20200616 add by feiyilin ================== end
			}
			String msg = "应用安装信息:成功导入(" + i + ")个设计,跳过(" + j + ")个设计,不符要求的记录(" + x + "),创建(" + t + ")个数据库表!";
			BeanCtx.userlog("--", "安装应用", "安装应用(" + appid + ")" + msg);
			return msg;
		} else {
			return filePath + " does not exist!";
		}

	}

	/**
	 * 删除一个应用
	 * 
	 * @param appid 应用编号
	 */
	public static void removeApp(String appid) throws Exception {

		//首先存一个临时的应用版本到硬盘中，以备恢复之用
		String fileName = BeanCtx.getUserid() + "_DEL_" + appid + "_" + DateUtil.getDateTimeNum() + ".xml";
		packageApp(appid, fileName);

		//获得应用配置信息并执行他的删除规则
		HashMap<String, Object> params = new HashMap<String, Object>();
		String sql = "select * from BPM_AppConfig order by SortNum";
		Document[] dc = Rdb.getAllDocumentsBySql(sql);
		for (Document cfgdoc : dc) {
			String tableName = cfgdoc.g("TableName");
			String ruleNum = cfgdoc.g("DeleteRuleNum");
			if (Tools.isNotBlank(ruleNum)) {
				params.put("TableName", tableName);
				params.put("WF_Appid", appid);
				BeanCtx.getExecuteEngine().run(ruleNum, params); //运行应用配置中的删除规则
			}
		}

	}

	/**
	 * 打包一个应用并存入到硬盘中
	 * 
	 * @param appid 应用编号
	 * @param fileName 文件名称，无需路径
	 * @return 返回打包后的文件路径
	 */
	public static String packageApp(String appid, String fileName, String... args) throws Exception {

		LinkedHashSet<Document> appdc = new LinkedHashSet<Document>();

		//获得所有需要打包的数据库表配置
		HashMap<String, Object> params = new HashMap<String, Object>();
		String sql = "select * from BPM_AppConfig order by SortNum";
		Document[] dc = Rdb.getAllDocumentsBySql(sql);
		for (Document cfgdoc : dc) {
			String tableName = cfgdoc.g("TableName");
			String ruleNum = cfgdoc.g("PackageRuleNum");
			if (Tools.isNotBlank(ruleNum)) {
				params.put("TableName", tableName);
				params.put("WF_Appid", appid);
				params.put("AppDc", appdc);
				BeanCtx.getExecuteEngine().run(ruleNum, params); //运行打包规则获得打包的文档加入到appdc中
			}
		}

		// --------------------------20181203新增打包应用时指定数据库类型-------------------------------
		//String dbType = args.length > 0 ? args[0] : Tools.getProperty("DBType").toUpperCase();
		String dbType = Rdb.getDbType();
		for (Document doc : appdc) {
			doc.s("dbType", dbType);
		}
		// --------------------------20181203 END------------------------------------------------

		//生成文件到硬盘中
		String appxml = Documents.dc2XmlStr(appdc, true);
		String filePath = BeanCtx.getWebAppsPath() + "outfile/" + fileName;
		XmlParser.string2XmlFile(appxml, filePath, "utf-8");

		return filePath;
	}
	
	/**
	 * 将整个应用的
	 */

	/**
	 * 复制一个应用
	 * 
	 * @param srcAppid 源应用编码
	 * @param targetAppid 目标应用编号
	 * @param oldKeyStr 旧字符串 如果没有传空值""
	 * @param newKeyStr 要替换的新字符串 如果没有传空值""
	 * @return 返回成功拷贝的文档数
	 */
	public static int copyApp(String srcAppid, String targetAppid, String oldKeyStr, String newKeyStr)
			throws Exception {
		LinkedHashSet<Document> appdc = new LinkedHashSet<Document>();

		//获得所有需要打包的数据库表配置
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("OldKeyStr", oldKeyStr); //旧字符串
		params.put("NewKeyStr", newKeyStr);//要替换的新字符串
		String sql = "select * from BPM_AppConfig order by SortNum";
		Document[] dc = Rdb.getAllDocumentsBySql(sql);
		for (Document cfgdoc : dc) {
			String tableName = cfgdoc.g("TableName");
			String ruleNum = cfgdoc.g("CopyRuleNum"); //应用设计复制规则
			if (Tools.isNotBlank(ruleNum)) {
				params.put("TableName", tableName);
				params.put("SrcAppid", srcAppid);
				params.put("TargetAppid", targetAppid);
				params.put("AppDc", appdc);
				BeanCtx.getExecuteEngine().run(ruleNum, params); //运行打包规则获得打包的文档加入到appdc中
			}
		}

		//得到了源应用的所有复制后的设计文档appdc
		int i = 0;
		BeanCtx.setDocNotEncode();
		for (Document destdoc : appdc) {
			i += destdoc.save();
		}

		return i;
	}

	/**
	 * 获得应用选择的下拉框，统一生成应用的select html代码
	 * 
	 * @param appid 已有应用编号，传入后可让此值处于选中状态
	 * @param allApp true表示显示全部应用，false表示只显示有权限的应用
	 * @return
	 */
	public static String getAppSelected(String appid, boolean allApp) {
		StringBuilder htmlStr = new StringBuilder();
		htmlStr.append("<select name=\"WF_Appid\" id=\"WF_Appid\" class=\"easyui-combobox\" style='width:388px'  >");
		boolean hasFlag = false;
		String selectAppStr = "";
		String sql = "";
		if (allApp) {
			sql = "select * from BPM_AppList where IsFolder='0' order by WF_Appid";
		} else {
			sql = "select * from BPM_AppList where IsFolder='0' and " + Rdb.getInReaderSql("Owner")
					+ " order by WF_Appid";
		}
		Document[] rc = Rdb.getAllDocumentsBySql(sql);
		for (Document appdoc : rc) {
			if (appdoc.g("WF_Appid").equals(appid)) {
				selectAppStr = "selected";
				hasFlag = true;
			} else {
				selectAppStr = "";
			}
			htmlStr.append("<option value=\"" + appdoc.g("WF_Appid") + "\" " + selectAppStr + " >" + appdoc.g("AppName")
					+ "(" + appdoc.g("WF_Appid") + ")</option>");
		}
		if (!hasFlag) {
			String appName = Rdb.getValueBySql("select AppName from BPM_AppList where WF_Appid='" + appid + "'");
			htmlStr.append("<option value=\"" + appid + "\" selected >" + appName + "(" + appid + ")</option>");
		}
		htmlStr.append("</select>");
		return htmlStr.toString();
	}

	/**
	 * 获得应用选择树的Json
	 * 
	 * @return
	 */
	public static String getAppTreeJson(String parentid) {
		String jsonStr = "", sql = "";
		if (Tools.isBlank(parentid)) {
			parentid = "root";
		}

		sql = "select AppName as text,WF_Appid as id,WF_OrUnid from BPM_AppList where ParentFolderid='" + parentid
				+ "' and " + Rdb.getInReaderSql("Owner") + " order by SortNum";
		LinkedHashSet<Document> dcSet = Rdb.getAllDocumentsSetBySql(sql);
		for (Document doc : dcSet) {
			String text = doc.g("text");
			String id = doc.g("id");
			String wfUnid = doc.g("id");

			doc.r("TEXT");
			doc.s("text", text);
			doc.r("ID");
			doc.s("id", id);
			doc.r("WF_ORUNID");
			doc.s("WF_OrUnid", wfUnid);

			doc.s("text", text + "(" + id + ")");
			//看是否还有子文件夹
			if (Rdb.hasRecord("select WF_OrUnid from BPM_AppList where ParentFolderid='" + id + "'")) {
				doc.s("state", "closed");
			} else {
				doc.s("state", "opened");
				doc.s("iconCls", "icon-task");
			}
			doc.s("isLeaf", "false");
		}
		if (Rdb.getDbType().equals("ORACLE")) {
			jsonStr = Documents.dc2json(dcSet, "", true);
		} else {
			jsonStr = Documents.dc2json(dcSet, "");
		}
		return jsonStr;
	}

	/**
	 * 根据数据库表名和选择条件，输出符合DataGrid视图所要求的JSON数据 同时在http url中可以传sort=字段名&order=DESC&rows=每页显示数&page=页数,如果url不传将会使用默认值 rows=30,pageNum=1,order=WF_DocCreated,order=DESC
	 * 
	 * @param sqlTableName 数据库表名
	 * @param selectColList 要选择的字段全选可用 *
	 * @param sqlWhere 选择条件
	 * @return 返回Json字符串
	 */
	public static String getDataGridJson(String sqlTableName, String selectColList, String sqlWhere,
			boolean useTableConfig) {

		String remoteSortFieldName = BeanCtx.g("sort", true);
		String remoteOrderDirection = BeanCtx.g("order", true);
		String pageSize = BeanCtx.g("rows", true);
		String pageNum = BeanCtx.g("page", true);
		if ((Tools.isBlank(pageSize)) || (pageSize.equals("NaN"))) {
			pageSize = "30";
		}
		if (Tools.isBlank(pageNum)) {
			pageNum = "1";
		}

		String sql;
		String totalNum;

		String orderStr = "";

		if (!Tools.isBlank(sqlWhere)) {
			if (!sqlWhere.trim().toLowerCase().substring(0, 5).equals("where")) {
				sqlWhere = " where " + sqlWhere;
			}
		}

		if (Tools.isBlank(remoteSortFieldName)) {
			remoteSortFieldName = "WF_DocCreated";
		}
		if (Tools.isBlank(remoteOrderDirection)) {
			remoteOrderDirection = "DESC";
		}
		if (remoteSortFieldName.indexOf(",") == -1) {
			orderStr = " order by " + remoteSortFieldName + " " + remoteOrderDirection;
		} else {
			String[] fdnmArray = Tools.split(remoteSortFieldName);
			String[] direArray = Tools.split(remoteOrderDirection);
			int i = 0;
			for (String fdName : fdnmArray) {
				String dire = "";
				if (direArray.length > i) {
					dire = direArray[i];
				}
				if (Tools.isBlank(orderStr))
					orderStr = " order by " + fdName + " " + dire;
				else {
					orderStr = orderStr + "," + fdName + " " + dire;
				}
				++i;
			}
		}

		sql = "select count(*) as TotalNum from " + sqlTableName + sqlWhere;
		totalNum = Rdb.getValueBySql(sql);
		if (Tools.isBlank(selectColList)) {
			selectColList = "*";
		} else if (selectColList.toLowerCase().indexOf("wf_orunid") == -1) {
			selectColList = "WF_OrUnid," + selectColList;
		}
		sql = "select " + selectColList + " from " + sqlTableName + sqlWhere + orderStr;
		LinkedHashSet<Document> dc = Rdb.getAllDocumentsSetByPage(sqlTableName, "*", orderStr, sqlWhere,
				Integer.parseInt(pageNum), Integer.parseInt(pageSize));
		String formatJson = Documents.dc2json(dc, "", useTableConfig);
		formatJson = "{\"total\":" + totalNum + ",\"rows\":" + formatJson + "}";
		return formatJson;
	}

	/**
	 * 自动匹配角色权限的sql where条件
	 * 
	 * @param eldoc
	 * @return
	 */
	public static String getDataSourceAclSql(Document eldoc) throws Exception {
		if (!eldoc.g("AutoRoleFlag").equals("1")) {
			return "";
		}
		HashSet<String> userRoles = BeanCtx.getUserRoles(BeanCtx.getUserid());
		String sqlwhere = "";
		for (String roleNumber : userRoles) {
			if (Tools.isBlank(sqlwhere)) {
				sqlwhere = "RoleNumber='" + roleNumber + "'";
			} else {
				sqlwhere += " or RoleNumber='" + roleNumber + "'";
			}
		}
		if (Tools.isNotBlank(sqlwhere)) {
			sqlwhere = "DataSourceid='" + eldoc.g("Dataid") + "' and (" + sqlwhere + ")";
		} else {
			return "";
		}
		String sql = "select RuleNum,SqlCondition from BPM_DataSourceAclList where " + sqlwhere;
		LinkedHashSet<Document> dc = Rdb.getAllDocumentsSetBySql(sql);
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("GridDoc", eldoc);
		String returnsqlwhere = "";
		for (Document doc : dc) {
			String rolesqlwhere = "";
			String ruleNum = doc.g("RuleNum");//使用规则返回
			if (Tools.isNotBlank(ruleNum)) {
				rolesqlwhere = BeanCtx.getExecuteEngine().run(ruleNum);
			} else {
				rolesqlwhere = doc.g("SqlCondition");
			}
			if (Tools.isBlank(returnsqlwhere)) {
				returnsqlwhere = rolesqlwhere;
			} else {
				returnsqlwhere += " or " + rolesqlwhere; //符合多个角色时使用or条件进行组合
			}
		}
		if (Tools.isNotBlank(returnsqlwhere)) {
			returnsqlwhere = "(" + returnsqlwhere + ")";
		}

		return returnsqlwhere;
	}
	
	public static void unZipFiles(File zipFile, String descDir) throws IOException {  
        
        ZipFile zip = new ZipFile(zipFile,Charset.forName("GBK"));//解决中文文件夹乱码  
        String name = zip.getName().substring(zip.getName().lastIndexOf('\\')+1, zip.getName().lastIndexOf('.'));  
          
        File pathFile = new File(descDir+name);  
        if (!pathFile.exists()) {  
            pathFile.mkdirs();  
        }  
          
        for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements();) {  
            ZipEntry entry = (ZipEntry) entries.nextElement();  
            String zipEntryName = entry.getName();  
            InputStream in = zip.getInputStream(entry);  
            String outPath = (descDir + name +"/"+ zipEntryName).replaceAll("\\*", "/");  
              
            // 判断路径是否存在,不存在则创建文件路径  
            File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));  
            if (!file.exists()) {  
                file.mkdirs();  
            }  
            // 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压  
            if (new File(outPath).isDirectory()) {  
                continue;  
            }  
            FileOutputStream out = new FileOutputStream(outPath);  
            byte[] buf1 = new byte[1024];  
            int len;  
            while ((len = in.read(buf1)) > 0) {  
                out.write(buf1, 0, len);  
            }  
            in.close();  
            out.close();  
        }  
        System.out.println("******************解压完毕********************");  
        return;  
    }

	/** 201912 by OldHui 由于系统的安装方法installApp的安装文件路径不能满足zip包方式的安装，故子新增此方法  **/
	/**
	 * 通过压缩文件安装应用
	 * @param xmlFilePath xml文件的完整路径
	 * @param replaceFlag=2 覆盖旧的应用设计 =1表示应用设计已存在时跳过
	 * @return
	 */
	public static String installZipApp(String xmlFilePath, String replaceFlag) {
		BeanCtx.setDocNotEncode(); // 指定存盘时不进行编码操作
		String tableName = "", appid = "";
		int i = 0, j = 0, x = 0, t = 0;
		String filePath = xmlFilePath;
		File file = FileUtils.getFile(filePath);
		if (file.exists()) {
			LinkedHashSet<Document> dc = Documents.xmlfile2dc(filePath);
			for (Document indoc : dc) {
				if (StringUtils.equalsIgnoreCase(indoc.g("WF_OrTableName"), "BPM_ModProcessList")
						&& replaceFlag.equals("2")) {
					deleteProcessList(indoc.g("WF_OrTableName"), indoc.g("WF_OrUnid"));
				}
			}
			for (Document indoc : dc) {
				tableName = indoc.g("WF_OrTableName");
				indoc.setDbType( //20181204 添加默认数据
						(Tools.isNotBlank(indoc.g("dbType"))) ? indoc.g("dbType") : Rdb.getDbType());
				if (tableName.equalsIgnoreCase("BPM_TableConfig")) {
					//说明是数据库表的配置信息,要进行实体表的创建动作
					if (indoc.g("CreateRdbTable").equals("1")) {
						//只有勾上了安装时需要创建实例表时才进行创建
						String dataSourceid = indoc.g("DataSourceid");
						if (Tools.isBlank(dataSourceid)) {
							dataSourceid = "default";
						} //缺省数据源id
						if (!Rdb.isExistTable(dataSourceid, indoc.g("TableName"))) {
							Rdb.creatTable(dataSourceid, indoc.g("TableName"), indoc.g("FieldConfig"));
							t++;
						}
					}
				}
				if (Tools.isBlank(appid)) {
					appid = indoc.g("WF_Appid");
				}
				if (Tools.isBlank(tableName) || Tools.isBlank(indoc.g("WF_OrUnid"))) {
					x++;
				} else {
					String sql = "select WF_OrUnid from " + indoc.g("WF_OrTableName") + " where WF_OrUnid='"
							+ indoc.g("WF_OrUnid") + "'";
					if (Rdb.hasRecord(sql)) {
						//覆盖旧的文档
						if (replaceFlag.equals("2")) {
							sql = "delete from " + indoc.g("WF_OrTableName") + " where WF_OrUnid='"
									+ indoc.g("WF_OrUnid") + "'";

							Rdb.execSql(sql);//删除已存在的旧设计元素
							indoc.save();
							i++;
						} else {
							//BeanCtx.out("跳过");
							j++;
						}
					} else {
						//记录不存在
						indoc.save();
						i++;
					}
				}
				// 20200616 add by feiyilin ================= start
				// 导入实时运行的规则元素时自动保存编译
				if (Tools.isNotBlank(indoc.g("RuleNum")) && Tools.isNotBlank(indoc.g("RuleCode")) && "0".equals(indoc.g("CompileFlag"))) {
					autosaveRuleCodeAndCompile(indoc.g("WF_OrUnid"));
				}
				// 20200616 add by feiyilin ================== end
			}
			String msg = "应用安装信息:成功导入(" + i + ")个设计,跳过(" + j + ")个设计,不符要求的记录(" + x + "),创建(" + t + ")个数据库表!";
			BeanCtx.userlog("--", "安装应用", "安装应用(" + appid + ")" + msg);
			return msg;
		} else {
			return filePath + " does not exist!";
		}
	}
	
	
	/** 20200616 add by feiyilin
	  *  导入规则元素自动保存编译
	 * @param docUnid
	 */
	public static void autosaveRuleCodeAndCompile(String docUnid) {
        // 更新规则库中的规则代码并进行编译
        Document ruleDoc = AppUtil.getDocByUnid("BPM_RuleList", docUnid);
        String ruleNum = ruleDoc.g("RuleNum");
        String appid = ruleDoc.g("WF_Appid");
        String ruleCode = ruleDoc.g("RuleCode");
        String classPath = "";
        if (ruleDoc.g("RuleType").equals("7")) {
            classPath = ruleDoc.g("ClassPath"); //javabean可以直接指定任何路径
        }
        else {
            classPath = "cn.linkey.rulelib." + appid + "." + ruleNum;
        }

        String ruleType = ruleDoc.g("RuleType");
        if (!ruleType.equals("7")) {
            // 检测规则代码是否符合要求,javabean不进行检测
            if (AppUtil.checkRuleCode(ruleCode, ruleDoc.g("WF_Appid"), ruleNum) == false) {
                return;
            }
        }

        ruleDoc.s("ClassPath", classPath);
        ruleDoc.s("RuleCode", ruleCode);
        int i = ruleDoc.save();
        if (i < 1) {
            return;
        }
        
        /* 开始编译 */
        try {
        	BeanCtx.jmcode(ruleCode, classPath, true);
		} catch (Exception e) {
		}
        
        //编译完了再更新一次编译时间,用来比较编译的文件与编译时间的比较,只有编译成功后才更新编译时间
        ruleDoc.s("CompileDate", DateUtil.getNow("yyyy-MM-dd HH:mm:ss")); //编译时间
        ruleDoc.save();

        //清除系统中的设计缓存
        RdbCache.remove("TempCache", ruleNum);
    }
}
