package cn.linkey.rulelib.S001;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:根据数据源编号获得数据源DocUNID
 * @author admin
 * @version: 8.0
 * @Created: 2014-04-02 10:21
 */
final public class R_S001_B026 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        String dataid = BeanCtx.g("Dataid");
        String sql = "select WF_OrUnid from BPM_DataSourceList where Dataid='" + dataid + "'";
        String docUnid = Rdb.getValueBySql(sql);
        BeanCtx.p(docUnid);
        return "";
    }
}