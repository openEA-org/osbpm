package cn.linkey.dao;

import java.sql.Connection;

import cn.linkey.factory.BeanCtx;
import cn.linkey.util.Tools;

/**
 * ms sql server创建数据库表实现类
 * 
 * @author Administrator
 */
public class CreatRdbTableMSSQL implements CreatRdbTable {

    @Override
    public int creatTable(String dataSourceid, String tableName, String fieldConfig) {
        String sql = "CREATE TABLE [dbo].[" + tableName + "](";
        String defaultValSql = ""; //修改字段缺省值
        String keyFdStr = "";

        //获得所有字段的配置集合
        int spos = fieldConfig.indexOf("[");
        if (spos != -1) {
            fieldConfig = fieldConfig.substring(spos, fieldConfig.lastIndexOf("]") + 1);
            com.alibaba.fastjson.JSONArray jsonArr = com.alibaba.fastjson.JSON.parseArray(fieldConfig);
            for (int i = 0; i < jsonArr.size(); i++) {
                com.alibaba.fastjson.JSONObject rowItem = (com.alibaba.fastjson.JSONObject) jsonArr.get(i);
                String fdName = rowItem.getString("FdName"); // 字段名称
                String fdType = rowItem.getString("FdType"); // 字段类型
                String fdLen = rowItem.getString("FdLen"); // 长度
                String fdNull = rowItem.getString("FdNull"); //非空
                String fdVal = rowItem.getString("FdVal");//缺省值
                String fdKey = rowItem.getString("FdKey");//关键字段

                if (fdType.equals("datetime") || fdType.equals("xml")) {
                    fdLen = "";
                }
                if (Tools.isNotBlank(fdLen)) {
                    fdLen = "(" + fdLen + ")";
                } //长度
                if (fdNull != null && fdNull.equals("Y")) { //不允许为空
                    fdNull = "NOT NULL";
                }
                else {
                    fdNull = "NULL";
                }
                if (i > 0) {
                    sql += ",";
                }
                sql += "[" + fdName + "] [" + fdType + "] " + fdLen + " " + fdNull;

                //缺省值
                if (Tools.isNotBlank(fdVal)) {
                    if (fdVal.equals("''")) {
                        fdVal = "";
                    }
                    defaultValSql += "ALTER TABLE [dbo].[" + tableName + "] ADD  CONSTRAINT [DF_" + tableName + "_" + fdName + "]  DEFAULT ('" + fdVal + "') FOR [" + fdName + "]\n";
                }

                //主键
                if (fdKey != null && fdKey.equals("Y")) {
                    keyFdStr = ",CONSTRAINT [PK_" + tableName + "] PRIMARY KEY CLUSTERED  ([" + fdName
                            + "] ASC)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON)";
                }
            }
        }
        sql += keyFdStr + " ) ON [PRIMARY]";

        int i = 0;
        Connection conn = null;
        try {
            if (dataSourceid.equals("default")) {
                conn = Rdb.getConnection();
            }
            else {
                conn = Rdb.getNewConnection(dataSourceid);
            }
            i = Rdb.execSql(conn, sql);
            Rdb.execSql(conn, defaultValSql);
        }
        catch (Exception e) {
            BeanCtx.log(e, "E", "创建数据库表时出错!");
            i = -2; //表示创建出错异常
        }
        finally {
            if (!dataSourceid.equals("default")) {
                Rdb.close(conn); //如果不是默认链接就需要关闭
            }
        }
        return i;
    }
}
