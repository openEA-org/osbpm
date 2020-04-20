package cn.linkey.rulelib.S001;

import java.util.*;
import java.sql.Connection;
import cn.linkey.dao.Rdb;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:测试数据源链接
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-22 20:55
 */
final public class R_S001_B047 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        Connection conn = null;
        try {
            conn = Rdb.getNewConnection(BeanCtx.g("DataSourceid"));
            //BeanCtx.p(conn.toString());
            if (Rdb.getDbType(conn).equals("ORACLE")) {
                String r = Rdb.getValueBySql(conn, "select * from v$version");
            }
            else {
                String r = Rdb.getValueBySql(conn, "select 1");
            }
            BeanCtx.p("数据源链接成功....");
        }
        catch (Exception e) {
            BeanCtx.p("<font color=red>错误:数据库链接出错....</font>");
            e.printStackTrace();
        }
        finally {
            Rdb.close(conn);
        }

        return "";
    }
}