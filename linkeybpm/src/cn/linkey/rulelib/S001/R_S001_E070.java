package cn.linkey.rulelib.S001;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import cn.linkey.app.AppUtil;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.FileUtil;
import cn.linkey.util.Tools;

/**
 * @RuleName:安装或更新一个新应用
 * @author admin
 * @version: 8.0 
 * @Created: 2014-04-01 08:16
 */
public class R_S001_E070 implements LinkeyRule {

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

	@Override
	public String run(HashMap<String, Object> params) {
		//获取事件运行参数
		Document formDoc = (Document) params.get("FormDoc"); //表单配置文档
		Document doc = (Document) params.get("DataDoc"); //数据主文档
		String eventName = (String) params.get("EventName");//事件名称
		if (eventName.equals("onFormOpen")) {
			String readOnly = (String) params.get("ReadOnly"); //1表示只读，0表示编辑
			return onFormOpen(doc, formDoc, readOnly);
		} else if (eventName.equals("onFormSave")) {
			return onFormSave(doc, formDoc);
		}
		return "1";
	}

	public String onFormOpen(Document doc, Document formDoc, String readOnly) {
		//当表单打开时
		if (readOnly.equals("1")) {
			return "1";
		} //如果是阅读状态则可不执行
		if (doc.isNewDoc()) {
			//可以对表单字段进行初始化如:doc.s("fdname",fdvalue),可以获取字段值 doc.g("fdname")

		}
		return "1"; //成功必须返回1，否则表退出并显示返回的字符串
	}

	/** 201912 by OldHui 重构应用安装表单保存的方法，扩展zip包方式的应用安装  **/
	public String onFormSave(Document doc, Document formDoc) {
		//当表单存盘前
		String msg = "";
		String replaceFlag = doc.g("replaceFlag"); //是否覆盖标记
		LinkedHashSet<String> fileList = (LinkedHashSet<String>) doc.getAttachmentsNameAndPath();
		if (fileList.size() == 0) {
			return "请上传一个对应格式的应用包!";
		}
		String formatFlag = doc.g("formatFlag");
		if ("1".equals(formatFlag)) {
			for (String fileName : fileList) {
				msg += AppUtil.installApp(fileName, replaceFlag) + " ";
			}
			doc.removeAllAttachments(true); //删除上传的临时文件
		} else {//zip包格式先将zip包解压到对应的文件夹中，再从zip包中取出xml文件安装
			for (String fileName : fileList) {
				String zipPath = BeanCtx.getAppPath() + fileName;
				File zipFile = FileUtils.getFile(zipPath);
				if (zipFile.exists()) {
					try {
						//解压至的目录
						String unZipTarget = zipPath.substring(0, zipPath.lastIndexOf(separatorOS) + 1);
						AppUtil.unZipFiles(zipFile, unZipTarget);
						//解压完后将临时zip包删除
						FileUtil.delFile(zipFile);
						//解压完后获取appid
						String unZipPath = zipPath.substring(0, zipPath.indexOf(".zip"));
						File unZipFile = new File(unZipPath);
						if (!unZipFile.exists()) {
							unZipFile = new File(unZipPath + ".zip");
						}
						String appid = "";
						File versionDirFile = FileUtils
								.getFile(unZipFile.listFiles()[0].getPath() + "" + separatorOS + "版本");
						if (versionDirFile.exists() && versionDirFile.listFiles().length > 0) {
							String versionFileName = "";
							for (int i = 0; i < versionDirFile.listFiles().length; i++) {
								String versionFileNameTemp = versionDirFile.listFiles()[i].getName();
								if (versionFileNameTemp.indexOf("_") != -1
										&& versionFileNameTemp.indexOf(".xml") != -1) {
									versionFileName = versionFileNameTemp;
								}
							}
							appid = versionFileName.substring(0, versionFileName.indexOf("_"));
						} else {
							appid = unZipFile.listFiles()[0].getName();
						}
						//先判断是否之前已经存在该解压目录，如果不存在则直接解压到该目录，如果已经存在则要判断是跳过还是覆盖，覆盖则将之前的删除再解压，跳过则要对比目录是否要替换
						String dirPath = AppUtil.getPackagePath() + appid;
						//如果没有应用目录则先创建
						File appDir = new File(dirPath);
						if (!appDir.exists()) {
							appDir.mkdir();
						}
						File dir = new File(dirPath + separatorOS + appid);
						if (dir.isDirectory()) {//如果目录存在，则删除，再copy过来
							FileUtil.delFile(new File(dirPath + separatorOS + appid));
						}
						//将解压后的二级目录复制到目标目录
						FileUtil.copyFileOrDir(unZipPath + separatorOS + unZipFile.listFiles()[0].getName(),
								dirPath + separatorOS + appid);
						//复制完毕将临时解压的文件夹删除
						FileUtil.delFile(new File(unZipPath));
						//从目录中取出版本xml文件进行安装
						String versionXmlDirPath = dirPath + separatorOS + appid + "" + separatorOS + "版本" + separatorOS
								+ "";
						File versionXmlDirFile = new File(versionXmlDirPath);
						if (versionXmlDirFile.isDirectory()) {
							//========取版本文件夹下修改时间最新的xml来安装=============
							String[] versionXmls = versionXmlDirFile.list();
							String versionXmlFilePath = "";
							Map<Object, String> map = new HashMap<Object, String>();
							for (String versionXml : versionXmls) {
								String versionXmlPath = versionXmlDirPath + versionXml;
								File f = new File(versionXmlPath);
								map.put(f.lastModified(), versionXmlPath);
							}
							Object[] obj = map.keySet().toArray();
							Arrays.sort(obj);
							versionXmlFilePath = map.get(obj[obj.length - 1]);
							//==============安装最新的版本xml文件===================
							File versionXmlFile = new File(versionXmlFilePath);
							if (versionXmlFile.exists()) {
								
								//将最新版目录的应用文件里的文件拷贝到appfile下（除了目录不需要）
								// 202001 add by alibao  ===========================================   start
								// 优化应用附件上传地址，调整附件上传地址配置顺序，先数据库，没有则从配置文件，再没有则使用默认地址
								String configid = "SysAppAttachmentPath";
								String appTargetPath = BeanCtx.getSystemConfig(configid);
								if (Tools.isBlank(appTargetPath)) {
									appTargetPath = Tools.getProperty(configid);
								} else {
									if (appTargetPath.indexOf(":") == -1 || "/".equals(appTargetPath.substring(0, 1))) { // 判断是否是绝对路径(window和linux)
										appTargetPath = BeanCtx.getWebAppsPath() + appTargetPath;
									}
								}
								if (Tools.isBlank(appTargetPath)) {
									appTargetPath = BeanCtx.getWebAppsPath() + "linkey/bpm/appfile/";
								}
								// 202001 add by alibao  ===========================================   end

								appTargetPath += appid; //要加上应用的编号
								//如果目录不存在就创建一个
								File appTargetDir = new File(appTargetPath);
								if (!appTargetDir.exists()) {
									appTargetDir.mkdir();
								}
								String appfilePath = dirPath + separatorOS + appid + "" + separatorOS + "资源"
										+ separatorOS + "应用文件" + separatorOS + "";
								File appfiles = new File(appfilePath);
								File[] appfileArr = appfiles.listFiles();
								if (appfileArr != null && appfileArr.length > 0) {
									for (File appfile : appfileArr) {
										//如果不是文件夹，则将文件拷贝到appfile下
										if (!appfile.isDirectory()) {
											FileUtil.copyFile(appfile.getPath(),
													appTargetPath + separatorOS + appfile.getName());
										}
									}
								}
								msg += AppUtil.installZipApp(versionXmlFilePath, replaceFlag) + " ";
								//=======安装完之后检查该应用是否能正常显示，如果不能则修改该应用的父目录为root=================
								boolean checkFlag = true;
								String app_id = appid;
								while (checkFlag) {
									Document document = Rdb.getDocumentBySql(
											"select * from bpm_applist where WF_Appid='" + app_id + "'");
									if (!document.isNull()) {
										String parent = document.g("ParentFolderid");
										if (!"root".equals(parent)) {
											app_id = parent;
										} else {
											checkFlag = false;
										}
									} else {
										//修改数据库
										Rdb.execSql("update bpm_applist set ParentFolderid = 'root' where WF_Appid='"
												+ appid + "'");
										checkFlag = false;
									}
								}
								//=================检查完毕==================================	
							}
						} else {
							msg = "请上传符合规格的zip文件";
							//删除问题文件目录(由于复制过来的是问题目录，所以取到的appid即为目录的名称)
							FileUtil.delFile(new File(dirPath));
						}
					} catch (IOException e) {
						msg = "安装失败";
						e.printStackTrace();
					}
				} else {
					msg = "安装失败";
				}
			}
		}
		return msg; //成功必须返回1，否则表示退出存盘
	}

}