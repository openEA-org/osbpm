package cn.linkey.rulelib.S001;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:获取应用的附件数据
 * @author admin
 * @version: 8.0
 * @Created: 2015-10-19 09:20
 */
final public class R_S001_B074 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception {
		//params为运行本规则时所传入的参数

		// 202001 优化应用附件上传地址，调整附件上传地址配置顺序，先数据库，没有则从配置文件，再没有则使用默认地址===========
        String configid = "SysAppAttachmentPath";
        String appPath = BeanCtx.getSystemConfig(configid); 
        if (Tools.isBlank(appPath)) {
            appPath = Tools.getProperty(configid);
        }else{
        	if(appPath.indexOf(":") == -1 || "/".equals(appPath.substring(0, 1))){ // 判断是否是绝对路径(window和linux)
        		appPath = BeanCtx.getWebAppsPath() + appPath;
        	}
        }
        if(Tools.isBlank(appPath)){
        	appPath = BeanCtx.getWebAppsPath() + "linkey/bpm/appfile/";
        }
		//===============================================================

		String appid = BeanCtx.g("WF_Appid", true);
		if (Tools.isBlank(appid)) {
			BeanCtx.out("R_S001_B074.应用编号不能为空!");
			return "";
		}
		appPath += appid; //要加上应用的编号

		//如果目录不存在就创建一个
		File dir = new File(appPath);
		if (!dir.exists()) {
			dir.mkdir();
			BeanCtx.log("W", "警告:应用目录不存在系统自动创建一个" + appPath);
			return "";
		}

		HashMap<String, Integer> totalMap = new HashMap<String, Integer>();
		totalMap.put("TotalNum", 0);
		StringBuilder fileStr = new StringBuilder();
		fileStr = getAllFiles(new File(appPath), fileStr, totalMap);

		String jsonStr = "{\"total\":" + totalMap.get("TotalNum") + ",\"rows\":[" + fileStr.toString() + "]}";
		BeanCtx.p(jsonStr.replace("\\", "/"));

		return "";
	}

	public StringBuilder getAllFiles(File dir, StringBuilder fileStr, HashMap<String, Integer> totalMap) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				//这里面用了递归的算法
				getAllFiles(files[i], fileStr, totalMap);
			} else {
				totalMap.put("TotalNum", totalMap.get("TotalNum") + 1);
				if (fileStr.length() > 0) {
					fileStr.append(",");
				}
				cal.setTimeInMillis(files[i].lastModified());
				String dateStr = sdf.format(cal.getTime());
				fileStr.append("{\"FileName\":\"" + files[i].getName() + "\",\"FilePath\":\"" + files[i].getPath()
						+ "\",\"LastModified\":\"" + dateStr + "\"}");
			}
		}
		return fileStr;
	}

}