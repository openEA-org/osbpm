package cn.linkey.rulelib.S001;

import java.io.File;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * 同步整个应用的源码到规则中
 * 
 * @author Administrator
 *
 */
public class R_S001_B035 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        BeanCtx.setCtxData("WF_NoEncode", "1"); // 指定存盘时不进行编码操作
        String appid = BeanCtx.g("WF_Appid", true); //应用编号列表
        if (Tools.isBlank(appid)) {
            BeanCtx.p("{\"Status\":\"error\",\"msg\":\"Error:appid can't be empty!\"}");
        }
        String srcDirPath = BeanCtx.getSystemConfig("ProjectJavaSrcPath") + "/cn/linkey/rulelib/" + appid;
        String sql = "select * from BPM_RuleList where WF_Appid='" + appid + "'";
        int i = 0, j = 0, e = 0;
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : dc) {
            if (doc.g("CompileFlag").equals("1")) {
                String filePath = srcDirPath + "/" + doc.g("RuleNum") + ".java";
                File file = FileUtils.getFile(filePath);
                if (file.exists()) {
                    String javaCode = Tools.readFileToString(filePath, "utf-8");
                    if (Tools.isNotBlank(javaCode)) {
                        doc.s("RuleCode", javaCode);
                        doc.save();
                        i++; //同步成功的
                    }
                }
                else {
                    e++; //源文件不存在跳过的
                }
            }
            else {
                j++; //实时运行不允许同步的
            }
        }
        BeanCtx.p("{\"Status\":\"ok\",\"msg\":\"1.共成功同步(" + i + ")个规则\\n2.因源文件不存在的跳过(" + e + ")个\\n3.跳过实时运行的规则(" + j + ")个\"}");
        return "";
    }

}