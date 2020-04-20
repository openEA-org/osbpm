package cn.linkey.rulelib.S001;

import java.util.HashMap;

import cn.linkey.app.AppUtil;
import cn.linkey.dao.Rdb;
import cn.linkey.dao.RdbCache;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;

/**
 * 本规则仅负责存盘规则代码
 * 
 * @author Administrator 编号 R_S001_B003
 */
public class R_S001_B003 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        BeanCtx.setCtxData("WF_NoEncode", "1"); // 指定存盘时不进行编码操作
        String btnAction = BeanCtx.g("WF_Action"); // 按扭动作
        String docUnid = BeanCtx.g("WF_elDocUnid"); // 设计元素的docunid
        if (btnAction.equals("btnSaveAndCompile")) {
            saveRuleCodeAndCompile(docUnid);
        }
        else if (btnAction.equals("btnSaveToFile")) {
            saveToJavaFile(docUnid);
        }
        else {
            saveRuleCodeOnly(docUnid);
        }
        return "";
    }

    /**
     * 保存源代码到硬盘目录中去
     * 
     * @param docUnid
     * @param ruleDoc
     * @param ruleType
     */
    public void saveToJavaFile(String docUnid) {
        String sql = "select * from BPM_RuleList where WF_OrUnid='" + docUnid + "'"; // 因为规则的docunid=wf_eldocunid,所以可以直接使用dounid来查找规则文档
        Document ruleDoc = Rdb.getDocumentBySql("BPM_RuleList", sql);
        String ruleNum = ruleDoc.g("RuleNum");
        String appid = ruleDoc.g("WF_Appid");
        String ruleCode = BeanCtx.g("RuleCode");
        String ruleType = ruleDoc.g("RuleType");
        String classPath = "";
        if (!ruleType.equals("7")) {
            // 检测规则代码是否符合要求,非javabean时
            if (AppUtil.checkRuleCode(ruleCode, ruleDoc.g("WF_Appid"), ruleNum) == false) {
                return;
            }
            classPath = "cn.linkey.rulelib." + appid + "." + ruleNum;
        }
        else {
            //javabean时
            classPath = ruleDoc.g("ClassPath");
        }
        String srcPath = Tools.getProperty("ProjectJavaSrcPath");
        if (Tools.isBlank(srcPath)) {
            srcPath = BeanCtx.getSystemConfig("ProjectJavaSrcPath");
        }
        String filePath = srcPath + "/" + classPath.replace(".", "/") + ".java";
        ruleDoc.s("ClassPath", classPath);
        ruleDoc.s("RuleCode", ruleCode);
        Tools.writeStringToFile(filePath, ruleCode, BeanCtx.getSystemConfig("ProjectJavaSrcCharset"), false);

        int i = ruleDoc.save();
        if (i < 1) {
            BeanCtx.print("{\"Status\":\"error\",\"msg\":\"Save code error!\"}");
        }
        else {
            BeanCtx.print("{\"Status\":\"ok\",\"msg\":\"" + BeanCtx.getMsg("Designer", "SaveToJavaFileMsg", filePath) + "\"}");
        }

        //清除系统中的设计缓存
        RdbCache.remove("TempCache", ruleNum);

    }

    public void saveRuleCodeOnly(String docUnid) {
        // 更新规则库中的规则代码
        Document ruleDoc = AppUtil.getDocByUnid("BPM_RuleList", docUnid);
        String ruleNum = ruleDoc.g("RuleNum");
        String appid = ruleDoc.g("WF_Appid");
        String classPath = "cn.linkey.rulelib." + appid + "." + ruleNum;
        String ruleCode = BeanCtx.g("RuleCode");
        String ruleType = ruleDoc.g("RuleType");
        if (!ruleType.equals("7")) {
            // 检测规则代码是否符合要求,javabean时不进行检测
            if (AppUtil.checkRuleCode(ruleCode, ruleDoc.g("WF_Appid"), ruleNum) == false) {
                return;
            }
        }

        ruleDoc.s("ClassPath", classPath);
        ruleDoc.s("RuleCode", ruleCode);

        int i = ruleDoc.save();
        if (i < 1) {
            BeanCtx.print("{\"Status\":\"error\",\"msg\":\"Save code error!\"}");
        }
        else {
            BeanCtx.print("{\"Status\":\"ok\"}");
        }
        //清除系统中的设计缓存
        RdbCache.remove("TempCache", ruleNum);
    }

    public void saveRuleCodeAndCompile(String docUnid) {
        // 更新规则库中的规则代码并进行编译
        Document ruleDoc = AppUtil.getDocByUnid("BPM_RuleList", docUnid);
        String ruleNum = ruleDoc.g("RuleNum");
        String appid = ruleDoc.g("WF_Appid");
        String ruleCode = BeanCtx.g("RuleCode");
        String classPath = "";
        if (ruleDoc.g("RuleType").equals("7")) {
            classPath = ruleDoc.g("ClassPath"); //javabean可以直接指定任何路径
        }
        else {
            classPath = "cn.linkey.rulelib." + appid + "." + ruleNum;
        }

        String ruleType = ruleDoc.g("RuleType");
        if (!ruleType.equals("7")) {
            // 检测规则代码是否符合要求,javabean不进行检测
            if (AppUtil.checkRuleCode(ruleCode, ruleDoc.g("WF_Appid"), ruleNum) == false) {
                return;
            }
        }

        ruleDoc.s("ClassPath", classPath);
        ruleDoc.s("RuleCode", ruleCode);
        int i = ruleDoc.save();
        if (i < 1) {
            BeanCtx.print("{\"Status\":\"error\",\"msg\":\"Save code error!\"}");
            return;
        }

        /* 开始编译 */
        String resultStr = BeanCtx.jmcode(ruleCode, classPath, true);
        if (!resultStr.equals("1")) {
            resultStr = resultStr.replace("+", " ");
            BeanCtx.print("{\"Status\":\"error\",\"msg\":\"" + resultStr + "\"}");
        }
        else {
            BeanCtx.print("{\"Status\":\"ok\"}");
        }

        //编译完了再更新一次编译时间,用来比较编译的文件与编译时间的比较,只有编译成功后才更新编译时间
        ruleDoc.s("CompileDate", DateUtil.getNow("yyyy-MM-dd HH:mm:ss")); //编译时间
        ruleDoc.save();

        //清除系统中的设计缓存
        RdbCache.remove("TempCache", ruleNum);
    }

}