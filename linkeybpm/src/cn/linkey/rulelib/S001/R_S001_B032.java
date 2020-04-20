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
 * 比较源文件与规则的更新时间和代码是否一至
 * 
 * @author Administrator
 *
 */
public class R_S001_B032 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        String appid = BeanCtx.g("WF_Appid", true); //应用编号
        String ruleNum = BeanCtx.g("ruleNum", true); //只同步单个规则的代码
        String sql = "select * from BPM_RuleList where RuleNum='" + ruleNum + "'";
        Document ruleDoc = Rdb.getDocumentBySql(sql);
        if (ruleDoc.isNull()) {
            BeanCtx.p("{\"Status\":\"error\",\"msg\":\"Error:rule does not exists!\"}");
        }

        String srcDirPath = "";
        String filePath = "";
        if (ruleDoc.g("RuleType").equals("7")) {
            //为javabean时
            srcDirPath = BeanCtx.getSystemConfig("ProjectJavaSrcPath") + "/" + ruleDoc.g("ClassPath").replace(".", "/");
            filePath = srcDirPath + ".java";
        }
        else {
            srcDirPath = BeanCtx.getSystemConfig("ProjectJavaSrcPath") + "/cn/linkey/rulelib/" + appid;
            filePath = srcDirPath + "/" + ruleNum + ".java";
        }
        //		BeanCtx.out("filePath="+filePath);
        File file = FileUtils.getFile(filePath);
        String msg = "";
        if (file.exists()) {
            String ruleLastModified = ruleDoc.g("WF_LastModified");
            boolean isJavaSourceNew = FileUtils.isFileNewer(file, DateUtil.string2Date(ruleLastModified, "yyyy-MM-dd hh:mm")); //看文件是否比规则的更新时间新
            if (isJavaSourceNew) {
                msg = "注意 : Java源文件时间新于当前规则的最后更新时间\\n";
            }
            else {
                msg = "注意 : Java源文件时间旧于当前规则的最后更新时间\\n";
            }
            String javaCode = Tools.readFileToString(filePath, "utf-8");
            javaCode = Tools.trim(javaCode);
            //BeanCtx.out("javaCode="+javaCode);
            String ruleCode = BeanCtx.g("ruleCode");
            ruleCode = Tools.trim(ruleCode);
            //BeanCtx.out("ruleCode="+ruleCode);
            if (javaCode.equals(ruleCode)) {
                msg += "恭喜 : Java源文件的内容从与当前规则程序是一至的!";
            }
            else {
                msg += "警告 : Java源文件的内容与当前规则代码不等,请注意核对!";
            }
        }
        else {
            msg = "Error:Java source file does not exist!";
        }

        BeanCtx.p("{\"Status\":\"ok\",\"msg\":\"" + msg + "\"}");
        return "";
    }

}
