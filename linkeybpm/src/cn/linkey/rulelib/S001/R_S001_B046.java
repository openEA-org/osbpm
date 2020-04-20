package cn.linkey.rulelib.S001;

import java.io.File;
import java.util.HashMap;
import org.apache.commons.io.FileUtils;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.util.DateUtil;

/**
 * 比较JS源文件与设计的更新时间和代码是否一至
 * 
 * @author Administrator
 *
 */
public class R_S001_B046 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        String docUnid = BeanCtx.g("DocUnid");
        String sql = "select * from BPM_JavaScript where WF_OrUnid='" + docUnid + "'";
        Document fileDoc = Rdb.getDocumentBySql(sql);
        if (fileDoc.isNull()) {
            BeanCtx.p("{\"Status\":\"error\",\"msg\":\"Error:js file does not exists!\"}");
        }
        String filePath = BeanCtx.getWebAppsPath() + "linkey/bpm/jscode/" + fileDoc.g("FileName");
        File file = FileUtils.getFile(filePath);
        String msg = "";
        if (file.exists()) {
            String lastModified = fileDoc.g("WF_LastModified");
            boolean isJsSourceNew = FileUtils.isFileNewer(file, DateUtil.string2Date(lastModified, "yyyy-MM-dd hh:mm")); //看文件是否比设计的更新时间新
            if (isJsSourceNew) {
                msg = "注意 : JS源文件时间新于当前设计的最后更新时间\\n";
            }
            else {
                msg = "注意 : JS源文件时间旧于当前设计的最后更新时间\\n";
            }
            String javaCode = Tools.readFileToString(filePath, "utf-8");
            javaCode = Tools.trim(javaCode);
            //BeanCtx.out("javaCode="+javaCode);
            String jsCode = BeanCtx.g("JsCode").trim();
            //BeanCtx.out("ruleCode="+ruleCode);
            if (javaCode.equals(jsCode)) {
                msg += "恭喜 : JS源文件的内容从与当前设计代码是一至的!";
            }
            else {
                msg += "警告 : JS源文件的内容与当前设计代码不等,请注意核对!";
            }
        }
        else {
            msg = "Error:JS source file does not exist!";
        }

        BeanCtx.p("{\"Status\":\"ok\",\"msg\":\"" + msg + "\"}");
        return "";
    }

}
