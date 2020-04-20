package cn.linkey.rulelib.S017;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:查询流程文档服务
 * @author admin
 * @version: 8.0
 * @Created: 2014-07-01 22:27
 */
final public class R_S017_B006 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        //示例参数:{"WF_DocUnid":"f217a2fe03e52048d10ba0b0950eec57"}
        String docUnid = (String) params.get("WF_DocUnid");
        String sql = "select * from BPM_AllDocument where WF_OrUnid='" + docUnid + "'";
        Document doc = Rdb.getDocumentBySql(sql);
        return doc.toJson();

    }
}