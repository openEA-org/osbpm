package cn.linkey.rulelib.S017;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:Engine_查询流程实例的当前状态
 * @author admin
 * @version: 8.0
 * @Created: 2014-07-02 11:33
 */
final public class R_S017_B016 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        //示例参数:{"WF_DocUnid":"096f1eae032d2046f7085dc0288e6101707c"}

        String docUnid = (String) params.get("WF_DocUnid");
        String sql = "select WF_Status,WF_CurrentNodeName,WF_CurrentNodeid,WF_Author from BPM_AllDocument where WF_OrUnid='" + docUnid + "'";
        Document doc = Rdb.getDocumentBySql(sql);

        return doc.toJson();
    }
}