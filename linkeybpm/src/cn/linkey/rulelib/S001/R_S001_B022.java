package cn.linkey.rulelib.S001;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * 同步应用或单个文件的源文件代码到规则中去
 * 
 * @author Administrator
 *
 */
public class R_S001_B022 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        String appid = BeanCtx.g("WF_Appid", true); //应用编号
        String ruleNum = BeanCtx.g("ruleNum", true); //如果传入规则编号则只同步单个规则，否则同步整个应用
        //BeanCtx.out("appid="+appid+" rulNum="+ruleNum);

        if (Tools.isBlank(appid) && Tools.isBlank(ruleNum)) {
            BeanCtx.p("{\"Status\":\"error\",\"msg\":\"Error:Application no or rulenum cannot be empty!\"}");
        }

        Document ruleDoc = Rdb.getDocumentBySql("select RuleType,ClassPath from BPM_RuleList where RuleNum='" + ruleNum + "'");
        String ruleType = ruleDoc.g("RuleType");
        String srcDirPath = "";
        String filePath = "";
        String srcPath = Tools.getProperty("ProjectJavaSrcPath");
        if (Tools.isBlank(srcPath)) {
            srcPath = BeanCtx.getSystemConfig("ProjectJavaSrcPath");
        }
        if (ruleType.equals("7")) {
            //javabean时
            srcDirPath = srcPath + "/" + ruleDoc.g("ClassPath").replace(".", "/");
            filePath = srcDirPath + ".java";
        }
        else {
            //非javabean时
            srcDirPath = srcPath + "/cn/linkey/rulelib/" + appid;
            filePath = srcDirPath + "/" + ruleNum + ".java";
        }

        //		BeanCtx.out("filePath="+filePath);
        if (Tools.isNotBlank(ruleNum)) {
            //同步单个规则
            //			BeanCtx.out("filePath="+filePath);
            File file = FileUtils.getFile(filePath);
            if (file.exists()) {
                String javaCode = Tools.readFileToString(filePath, "utf-8");
                javaCode = Tools.encodeJson(javaCode);
                BeanCtx.p("{\"Status\":\"ok\",\"code\":\"" + javaCode + "\"}");
                return "";
            }
            else {
                BeanCtx.p("{\"Status\":\"error\",\"msg\":\"Error:Java source file does not exist!\"}");
                return "";
            }

        }
        else if (Tools.isNotBlank(appid)) {
            //同步整个应用,此功能暂时不实现
            Collection<File> javaFileSet = FileUtils.listFiles(new File(srcDirPath), new String[] { "java" }, true);
            for (File fileItem : javaFileSet) {
                BeanCtx.out("fileName=" + fileItem.getName());
            }
        }
        BeanCtx.p("{\"Status\":\"ok\",\"msg\":\"同步成功!\"}");
        return "";
    }

}
