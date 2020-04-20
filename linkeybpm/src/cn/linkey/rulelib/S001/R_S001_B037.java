package cn.linkey.rulelib.S001;

import java.util.*;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;

/**
 * 混淆应用的全部代码
 * 
 * @author Administrator
 *
 */
public class R_S001_B037 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        BeanCtx.setCtxData("WF_NoEncode", "1"); // 指定存盘时不进行编码操作
        String appid = BeanCtx.g("WF_Appid", true); //应用编号列表
        if (Tools.isBlank(appid)) {
            BeanCtx.p("{\"Status\":\"error\",\"msg\":\"Error:appid can't be empty!\"}");
        }

        String sql = "select * from BPM_RuleList where WF_Appid='" + appid + "'";
        int i = 0, j = 0;
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : dc) {
            String ruleCode = doc.g("RuleCode");
            ruleCode = Tools.mixJavaCode(ruleCode);
            doc.s("RuleCode", ruleCode);
            doc.save();
            i++;
        }

        BeanCtx.p("{\"Status\":\"ok\",\"msg\":\"1.共成功混淆(" + i + ")个规则\"}");
        return "";
    }

}