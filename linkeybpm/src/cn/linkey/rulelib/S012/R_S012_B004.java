package cn.linkey.rulelib.S012;

import java.sql.Connection;
import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:创建系统数据库视图
 * @author admin
 * @version: 8.0
 * @Created: 2014-09-20 21:13
 */
final public class R_S012_B004 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String sql = BeanCtx.getDefaultCode("CreatDatabaseView");
        Connection conn = null;
        try {
            conn = Rdb.getNewConnection("mysql");
            Rdb.execSql(conn, sql);
        }
        catch (Exception e) {
            BeanCtx.p("创建失败");
        }
        finally {
            Rdb.close(conn);
        }
        BeanCtx.p("所有视图创建成功");

        return "";
    }
}