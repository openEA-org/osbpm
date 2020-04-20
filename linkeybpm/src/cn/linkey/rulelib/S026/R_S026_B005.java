package cn.linkey.rulelib.S026;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:删除节点设置的用户
 * @author admin
 * @version: 8.0
 * @Created: 2014-09-15 17:16
 */
final public class R_S026_B005 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String sql = "delete from BPM_SimNodeList where SimProcessUnid='" + BeanCtx.g("SimProcessUnid") + "' and Nodeid='" + BeanCtx.g("Nodeid") + "'";
        Rdb.execSql(sql);
        BeanCtx.p(Tools.jmsg("ok", ""));
        return "";
    }
}