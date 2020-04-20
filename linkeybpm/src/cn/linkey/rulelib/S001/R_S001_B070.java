package cn.linkey.rulelib.S001;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:获得国际标签的SON
 * @author admin
 * @version: 8.0
 * @Created: 2014-12-20 16:15
 */
final public class R_S001_B070 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String appid = BeanCtx.g("WF_Appid", true);

        //首先获得公用标签
        String sql = "select * from BPM_Internation where WF_Appid='S029' order by zhCN";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        StringBuilder jsonStr = new StringBuilder();
        jsonStr.append("[{\"id\":\"S029\",\"text\":\"公共标签\",\"state\":\"closed\",\"children\":[");
        int i = 0;
        for (Document doc : dc) {
            if (i == 1) {
                jsonStr.append(",");
            }
            jsonStr.append("{\"id\":\"" + doc.g("Langid") + "\",\"text\":\"" + doc.g("zhCN") + "\"}");
            i = 1;
        }
        jsonStr.append("]}");

        //追加当前用户的标签
        String appName = Rdb.getValueBySql("select AppName from BPM_AppList where WF_Appid='" + appid + "'");
        sql = "select * from BPM_Internation where WF_Appid='" + appid + "' order by zhCN";
        dc = Rdb.getAllDocumentsBySql(sql);
        jsonStr.append(",{\"id\":\"" + appid + "\",\"text\":\"" + appName + "\",\"state\":\"closed\",\"children\":[");
        i = 0;
        for (Document doc : dc) {
            if (i == 1) {
                jsonStr.append(",");
            }
            jsonStr.append("{\"id\":\"" + doc.g("Langid") + "\",\"text\":\"" + doc.g("zhCN") + "\"}");
            i = 1;
        }
        jsonStr.append("]}]");
        BeanCtx.p(jsonStr);

        return "";
    }
}