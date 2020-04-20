package cn.linkey.rulelib.S001;

import java.io.File;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;

import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;

/**
 * @RuleName:读取控制台日记
 * @author admin
 * @version: 8.0
 * @Created: 2014-07-17 09:37
 */
final public class R_S001_B067 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String filePath = BeanCtx.getWebAppsPath() + "log/" + DateUtil.getDateNum() + ".log";
        File file = new File(filePath);
        try {
            String logStr = FileUtils.readFileToString(file);
            logStr = logStr.replace("\n", "<br>");
            BeanCtx.p(logStr);
        }
        catch (Exception e) {
            e.printStackTrace();
            BeanCtx.p("错误:未找到系统日记文件...");
        }
        return "";
    }
}