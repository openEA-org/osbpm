package cn.linkey.rulelib.S001;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentHashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.dao.RdbCache;
import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:设计元素性能分析数据
 * @author admin
 * @version: 8.0
 * @Created: 2014-04-15 23:25
 */
final public class R_S001_B038 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        LinkedHashSet<Document> dc = new LinkedHashSet<Document>();
        ConcurrentHashMap<String, HashMap<String, String>> performanceCache = (ConcurrentHashMap<String, HashMap<String, String>>) RdbCache.get("PerformanceCache");
        if (performanceCache != null) {
            for (String wf_num : performanceCache.keySet()) {
                HashMap<String, String> elmap = performanceCache.get(wf_num);
                Document doc = BeanCtx.getDocumentBean("");
                doc.appendFromMap(elmap);
                doc.s("ElementName", Rdb.getValueBySql("select eleName from BPM_AllElementList where eleid='" + wf_num + "'"));
                doc.s("AverageTime", formatNum(doc.g("AverageTime")));
                doc.s("MaxTime", formatNum(doc.g("MaxTime")));
                doc.s("MinTime", formatNum(doc.g("MinTime")));
                doc.s("TotalAccess", formatNum(doc.g("TotalAccess")));
                dc.add(doc);
            }
            String jsonStr = Documents.dc2json(dc, "");
            jsonStr = "{\"total\":" + performanceCache.size() + ",\"rows\":" + jsonStr + "}";
            BeanCtx.p(jsonStr);
        }
        return "";
    }

    public String formatNum(String num) {
        if (num.length() < 5) {
            num = "000000" + num;
            num = num.substring(num.length() - 5);
        }
        return num;
    }

}