package cn.linkey.rulelib.S001;

import java.util.HashMap;

import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * 读取硬盘中的js文件代码到设计中
 * 
 * @author Administrator
 *
 */
public class R_S001_B045 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        String fileName = BeanCtx.g("FileName"); //得到 js文件名
        String jsCode = BeanCtx.g("JsCode"); //js code
        if (Tools.isBlank(fileName)) {
            BeanCtx.p("{\"Status\":\"error\",\"msg\":\"Error:file name cannot be empty!\"}");
        }
        //写入文件内容
        String filePath = BeanCtx.getWebAppsPath() + "linkey/bpm/jscode/" + fileName;
        boolean w = Tools.writeStringToFile(filePath, jsCode, "utf-8", false);
        if (w) {
            BeanCtx.p("{\"Status\":\"ok\",\"msg\":\"已成功更新文件(" + filePath.replace("\\", "/") + ")\"}");
        }
        else {
            BeanCtx.p("{\"Status\":\"error\",\"msg\":\"Error:文件更新失败!\"}");
        }
        return "";
    }

}
