package cn.linkey.rulelib.S001;

import java.io.File;
import java.util.HashMap;
import org.apache.commons.io.FileUtils;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * 读取硬盘中的js文件代码到设计中
 * 
 * @author Administrator
 *
 */
public class R_S001_B044 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        String fileName = BeanCtx.g("FileName"); //得到 js文件名
        if (Tools.isBlank(fileName)) {
            BeanCtx.p("{\"Status\":\"error\",\"msg\":\"Error:file name cannot be empty!\"}");
        }
        //读取文件内容
        String filePath = BeanCtx.getWebAppsPath() + "linkey/bpm/jscode/" + fileName;
        //BeanCtx.out("filePath="+filePath);
        File file = FileUtils.getFile(filePath);
        if (file.exists()) {
            String javaCode = Tools.readFileToString(filePath, "utf-8");
            javaCode = Tools.encodeJson(javaCode);
            BeanCtx.p("{\"Status\":\"ok\",\"code\":\"" + javaCode + "\"}");
        }
        else {
            BeanCtx.p("{\"Status\":\"error\",\"msg\":\"Error:JS file does not exist!\"}");
        }
        return "";
    }

}
