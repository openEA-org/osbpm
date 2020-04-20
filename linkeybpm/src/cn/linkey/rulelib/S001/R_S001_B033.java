package cn.linkey.rulelib.S001;

import java.util.HashMap;

import cn.linkey.app.AppUtil;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * 全部重新编译整个应用的规则
 * 
 * @author Administrator
 *
 */
public class R_S001_B033 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        BeanCtx.setCtxData("WF_NoEncode", "1"); // 指定存盘时不进行编码操作
        String appid = BeanCtx.g("WF_Appid", true); //应用编号列表
        if (Tools.isBlank(appid)) {
            BeanCtx.p("{\"Status\":\"error\",\"msg\":\"Error:appid can't be empty!\"}");
        }
        String srcDirPath = BeanCtx.getSystemConfig("ProjectJavaSrcPath") + "/cn/linkey/rulelib/" + appid;
        String sql = "select * from BPM_RuleList where WF_Appid='" + appid + "'";
        int i = 0, j = 0;
        String errorRule = "";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : dc) {
            String ruleCode = doc.g("RuleCode");
            String classPath = "cn.linkey.rulelib." + doc.g("WF_Appid") + "." + doc.g("RuleNum");
            if (AppUtil.checkRuleCode(ruleCode, doc.g("WF_Appid"), doc.g("RuleNum")) == true) {
                String resultStr = BeanCtx.jmcode(ruleCode, classPath, true);
                if (resultStr.equals("1")) {
                    i++;
                }
                else {
                    errorRule += " " + doc.g("RuleNum");
                }
            }
            else {
                errorRule += " " + doc.g("RuleNum");
            }
        }
        if (Tools.isNotBlank(errorRule)) {
            BeanCtx.p("{\"Status\":\"ok\",\"msg\":\"1.共成功编译(" + i + ")个规则，编译错误的规则有(" + errorRule + ")\"}");
        }
        else {
            BeanCtx.p("{\"Status\":\"ok\",\"msg\":\"1.共成功编译(" + i + ")个规则!\"}");
        }
        return "";
    }

}