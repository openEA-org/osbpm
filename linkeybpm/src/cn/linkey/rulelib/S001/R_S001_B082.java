package cn.linkey.rulelib.S001;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;

import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:获取指定日期的平台日志
 * @author  admin
 * @version: 8.0
 * @Created: 2018-12-26 16:43:43
 */
final public class R_S001_B082 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception {

		String date = BeanCtx.g("date").replace("-", "");
		String filePath = BeanCtx.getWebAppsPath() + "log/" + date + ".log";

		File file = new File(filePath);
		StringBuilder logStr = new StringBuilder();
		try {
			List<String> contents = FileUtils.readLines(file);
			int start = 0, max = 2000, size = contents.size();
			if (size > max) {
				start = size - max;
			}
			for (String line : contents) {
				if (start < size) {
					line = line.replace("	", "        ");
					logStr.append("<br>").append(line);
				}
				start++;
			}
			BeanCtx.p(logStr);
		} catch (Exception e) {
			BeanCtx.p("未找到日期为 " + date + " 的系统日记文件...");
		}

		return "";
	}
}