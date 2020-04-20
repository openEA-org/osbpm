package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:用户关闭页面解锁文件
 * @author admin
 * @version: 8.0
 * @Created: 2017-05-12 15:38:11
 */
final public class R_S003_B081 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String docUnid = BeanCtx.g("WF_DocUnid", true);
        String userId = BeanCtx.getUserid();
        Rdb.execSql("delete from BPM_LockDocList where Userid='" + userId + "' and DocUnid='" + docUnid + "'");
        return "1";
    }
}
