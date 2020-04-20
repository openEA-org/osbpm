package cn.linkey.rulelib.S026;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:把文档输出为propertygrid json
 * @author admin
 * @version: 8.0
 * @Created: 2014-09-18 08:29
 */
final public class R_S026_B008 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        StringBuilder jsonStr = new StringBuilder();
        String dataUnid = BeanCtx.g("DataUnid", true);
        String sql = "select * from BPM_SimFormList where WF_OrUnid='" + dataUnid + "'";
        Document doc = Rdb.getDocumentBySql(sql);
        if (!doc.isNull()) {
            HashMap<String, String> fieldMap = doc.getAllItems();
            for (String fieldName : fieldMap.keySet()) {
                String fieldValue = Tools.encodeJson(fieldMap.get(fieldName));
                if (jsonStr.length() > 0) {
                    jsonStr.append(",");
                }
                jsonStr.append("{\"name\":\"" + fieldName + "\",\"value\":\"" + fieldValue + "\",\"editor\":\"text\"}");
            }
            BeanCtx.p("{\"total\":" + fieldMap.size() + ",\"rows\":[");
            BeanCtx.p(jsonStr.toString());
            BeanCtx.p("]}");
        }
        return "";
    }
}