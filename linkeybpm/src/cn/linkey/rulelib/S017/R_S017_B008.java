package cn.linkey.rulelib.S017;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:修改流程文档字段服务
 * @author admin
 * @version: 8.0
 * @Created: 2014-07-01 22:27
 */
final public class R_S017_B008 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {

        //示例参数{"WF_DocUnid":"9018d1430a418047a10897b01a8ffa0cced0","WF_DocXml":"<Items><WFItem name=\"Subject\">标题</WFItem></Items>"}

        String docUnid = (String) params.get("WF_DocUnid");
        String xmlStr = (String) params.get("WF_DocXml");
        String sql = "select * from BPM_MainData where WF_OrUnid='" + docUnid + "'";
        Document doc = Rdb.getDocumentBySql(sql);
        doc.appendFromXml(xmlStr);
        int i = doc.save();
        return String.valueOf(i);
    }
}