package cn.linkey.rulelib.S001;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * 同步整个应用的规则到java文件中
 * 
 * @author Administrator
 *
 */
public class R_S001_B034 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        String appid = BeanCtx.g("WF_Appid", true); //应用编号列表
        if (Tools.isBlank(appid)) {
            BeanCtx.p("{\"Status\":\"error\",\"msg\":\"Error:appid can't be empty!\"}");
        }
        String srcDirPath = BeanCtx.getSystemConfig("ProjectJavaSrcPath") + "/cn/linkey/rulelib/" + appid;
        String sql = "select * from BPM_RuleList where WF_Appid='" + appid + "'";
        int i = 0, j = 0;
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : dc) {
            String filePath = srcDirPath + "/" + doc.g("RuleNum") + ".java";
            String ruleCode = doc.g("RuleCode");
            //  BeanCtx.out("ruleCode="+ruleCode);
            if (Tools.isNotBlank(ruleCode)) {
                Tools.writeStringToFile(filePath, ruleCode, BeanCtx.getSystemConfig("ProjectJavaSrcCharset"), false);
                i++;
            }
            else {
                j++;
            }
        }
        BeanCtx.p("{\"Status\":\"ok\",\"msg\":\"1.共成功同步(" + i + ")个规则到Java源文件中\\n2.因规则代码为空的跳过(" + j + ")个\"}");
        return "";
    }

}