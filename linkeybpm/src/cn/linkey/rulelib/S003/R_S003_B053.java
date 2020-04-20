package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:onunload解锁文档
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-19 21:33
 */
final public class R_S003_B053 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        String docUnid = BeanCtx.g("DocUnid");
        String sql = "delete from BPM_LockDocList where DocUnid='" + docUnid + "'";
        Rdb.execSql(sql);
        return "";
    }
}