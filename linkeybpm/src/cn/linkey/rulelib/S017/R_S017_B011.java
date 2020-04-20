package cn.linkey.rulelib.S017;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:Engine_创建或修改流程委托服务
 * @author admin
 * @version: 8.0
 * @Created: 2014-07-01 23:04
 */
final public class R_S017_B011 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        //示例参数{"WF_OrUnid":"9018d1430a418047a10897b01a8ffa0cced0","WF_DocXml":"<Items><WFItem name=\"BPM_EntrustList表中的字段\">字段值</WFItem></Items>"}

        String docUnid = (String) params.get("WF_OrUnid");
        String xmlStr = (String) params.get("WF_DocXml");
        String sql = "select * from BPM_EntrustList where WF_OrUnid='" + docUnid + "'";
        Document doc = Rdb.getDocumentBySql(sql);
        doc.appendFromXml(xmlStr);
        int i = doc.save();
        return String.valueOf(i);
    }
}