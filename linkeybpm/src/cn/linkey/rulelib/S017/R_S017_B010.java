package cn.linkey.rulelib.S017;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:Engine_获得委托列表服务
 * @author admin
 * @version: 8.0
 * @Created: 2014-07-01 23:04
 */
final public class R_S017_B010 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String sql = "select * from BPM_EntrustList where WF_AddName='" + BeanCtx.getUserid() + "'";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        return Documents.dc2json(dc, "rows");
    }
}