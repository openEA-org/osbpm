package cn.linkey.rulelib.S026;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:保存模似数据
 * @author admin
 * @version: 8.0
 * @Created: 2014-09-11 11:17
 */
final public class R_S026_B002 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String docUnid = BeanCtx.g("wf_docunid");
        String sql = "select * from BPM_SimFormList where WF_OrUnid='" + docUnid + "'";
        Document doc = Rdb.getDocumentBySql(sql);
        doc.appendFromRequest(BeanCtx.getRequest());
        doc.save();
        BeanCtx.p(Tools.jmsg("ok", "数据保存成功!"));
        return "";
    }
}