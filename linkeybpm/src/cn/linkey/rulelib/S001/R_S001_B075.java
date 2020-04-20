package cn.linkey.rulelib.S001;

import java.io.File;
import java.util.HashMap;

import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:删除应用附件
 * @author admin
 * @version: 8.0
 * @Created: 2015-10-19 11:47
 */
final public class R_S001_B075 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String fileName = "";
        String fileList = BeanCtx.g("FileList", true);
        String[] fileArray = Tools.split(fileList, ",");
        for (String filePath : fileArray) {
            File file = new File(filePath);
            if (file.exists()) {
                fileName += " " + file.getName();
                file.delete();
            }
        }

        String jsonStr = Tools.jmsg("ok", "文件(" + fileName + ")成功删除!");
        BeanCtx.p(jsonStr);

        return "";
    }
}