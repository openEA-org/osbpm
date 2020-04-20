package cn.linkey.rulelib.S001;

import java.util.HashMap;
import cn.linkey.dao.Rdb;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.doc.*;
import java.util.*;
import cn.linkey.util.Tools;

/**
 * 输出Json Data所配置的字段列表
 * 
 * @author Administrator
 */
public class R_S001_B012 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        String jsonid = BeanCtx.g("Dataid", true);
        String sql = "";
        sql = "select JsonConfig from BPM_DataSourceList where Dataid='" + jsonid + "'";
        String jsonConfig = Rdb.getValueBySql(sql);
        int spos = jsonConfig.indexOf("[");
        if (spos != -1) {
            jsonConfig = jsonConfig.substring(spos, jsonConfig.lastIndexOf("]") + 1);
        }
        else {
            jsonConfig = "[]";
        }
        HashSet<Document> dc = Documents.jsonStr2dc(jsonConfig);
        for (Document doc : dc) {
            String jsonName = doc.g("JsonName");
            if (Tools.isNotBlank(jsonName)) {
                doc.s("FdName", jsonName);
            }
        }
        jsonConfig = Documents.dc2json(dc, "");
        BeanCtx.print(jsonConfig); // 输出json字符串

        return "";
    }
}