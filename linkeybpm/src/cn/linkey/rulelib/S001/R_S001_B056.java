package cn.linkey.rulelib.S001;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:通用应用设计删除规则
 * @author admin
 * @version: 8.0
 * @Created: 2014-06-06 11:02
 */
final public class R_S001_B056 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        String tableName = (String) params.get("TableName"); //获得要设计所在数据库表名
        String appid = (String) params.get("WF_Appid");//获得要删除的应用编号
        String sql = "delete from " + tableName + " where WF_Appid='" + appid + "'";
        int i = Rdb.execSql(sql);
        return String.valueOf(i);
    }
}