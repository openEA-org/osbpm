package cn.linkey.rulelib.S001;

import java.sql.Connection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;
import cn.linkey.dao.Rdb;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * 获得数据库中的所有表名
 * 
 * @author Administrator 访问编号:R_S001_B009
 */
public class R_S001_B009 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        BeanCtx.getResponse().setContentType("text/json;charset=utf-8");
        String dataSourceid = BeanCtx.g("DataSourceid", true); //数据源id
        HashSet<String> tableSet = null;
        if (Tools.isBlank(dataSourceid) || dataSourceid.equals("default")) {
            //默认数据源
            tableSet = Rdb.getValueSetBySql(getTableSql(Rdb.getConnection(), dataSourceid));
        }
        else {
            //指定数据源
            Connection conn = null;
            try {
                conn = Rdb.getNewConnection(dataSourceid);
                tableSet = Rdb.getValueSetBySql(conn, getTableSql(conn, dataSourceid), false);
            }
            catch (Exception e) {
                e.printStackTrace();
                return "";
            }
            finally {
                Rdb.close(conn);
            }
        }
        TreeSet<String> treeSet = new TreeSet(tableSet);
        treeSet.comparator();
        StringBuilder jsonStr = new StringBuilder();
        jsonStr.append("[");
        int i = 0;
        for (String tableName : treeSet) {
            if (i == 0) {
                i = 1;
            }
            else {
                jsonStr.append(",");
            }
            jsonStr.append("{\"tableName\":\"" + tableName + "\"}");
        }
        jsonStr.append("]");
        BeanCtx.print(jsonStr.toString());
        return "";
    }

    //根据数据库类型获取不同的sql语句
    private String getTableSql(Connection conn, String dataSourceid) throws Exception {
        String sql = "";
        String dbType = Rdb.getDbType(dataSourceid);
        if (dbType.equals("MSSQL")) {
            sql = "select table_name from information_schema.tables";
        }
        else if (dbType.equals("MYSQL")) {
            sql = "SELECT  table_name FROM  information_schema.tables WHERE  table_schema = '" + conn.getCatalog() + "'";
        }
        else if (dbType.equals("ORACLE")) {
            sql = "select table_name from user_tables";
        }
        else if (dbType.equals("DB2")) {
            sql = "Select tabname as table_name From syscat.tables where owner='" + BeanCtx.getSystemConfig("DB2OWNER") + "'"; // 到系统配置中得到db2的owner参数
        }
        return sql;
    }
}
