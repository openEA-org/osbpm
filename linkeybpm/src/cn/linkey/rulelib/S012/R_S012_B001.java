package cn.linkey.rulelib.S012;

import java.util.HashMap;
import java.util.HashSet;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:从SQL中导入所有数据库表结构
 * @author admin
 * @version: 8.0
 * @Created: 2014-09-20 09:19
 */
final public class R_S012_B001 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        int i = 0, u = 0;
        HashSet<String> tableSet = Rdb.getValueSetBySql(getTableSql());
        for (String tableName : tableSet) {
            String sql = "select * from BPM_TableConfig where TableName='" + tableName + "'";
            Document doc = Rdb.getDocumentBySql(sql);
            if (doc.isNull()) {
                //表不存在创建一个
                doc.s("WF_Appid", "S001");
                doc.s("TableRemark", tableName);
                doc.s("TableName", tableName);
                doc.s("TableType", "1");
                doc.s("InitData", "0");
                doc.s("WF_Version", "8.0");
                i++;
            }
            else {
                u++;
            }

            //看是否是视图
            sql = "SELECT * FROM sys.views where name='" + tableName + "'";
            if (Rdb.hasRecord(sql)) {
                doc.s("TableType", "2");
            }

            //获得字段配置的详细信息
            String fdConfig = getFieldConfig(tableName);
            doc.s("FieldConfig", fdConfig);

            //保存数据库表
            doc.save();

        }
        BeanCtx.p("共成功导入(" + i + ")个数据库表,更新(" + u + ")个数据库表结构!");
        return "";
    }

    /**
     * 获得数据库表字段信息
     * 
     * @param tableName
     * @return
     */
    private String getFieldConfig(String tableName) {
        String sql = BeanCtx.getDefaultCode("GetSqlTableFieldInfo");
        sql = sql.replace("{TableName}", tableName);
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : dc) {
            //处理缺省值
            String fdVal = doc.g("FdVal");
            if (fdVal.equals("('')")) {
                doc.s("FdVal", "''");
            }
            else {
                if (fdVal.startsWith("('")) {
                    fdVal = fdVal.substring(2, fdVal.length() - 2);
                    doc.s("FdVal", fdVal);
                }
            }
            //处理xmldata字段
            if (doc.g("FdName").equalsIgnoreCase("XmlData")) {
                doc.s("FdLen", "");
            }
        }
        String jsonStr = Documents.dc2json(dc, "");
        jsonStr = "{\"total\":" + dc.length + ",\"rows\":" + jsonStr + "}";
        return jsonStr;
    }

    //根据数据库类型获取不同的sql语句
    private String getTableSql() throws Exception {
        String sql = "";
        String dbType = Rdb.getDbType();
        if (dbType.equals("MSSQL")) {
            sql = "select table_name from information_schema.tables";
        }
        else if (dbType.equals("MYSQL")) {
            sql = "SELECT  table_name FROM  information_schema.tables WHERE  table_schema = '" + Rdb.getConnection().getCatalog() + "'";
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