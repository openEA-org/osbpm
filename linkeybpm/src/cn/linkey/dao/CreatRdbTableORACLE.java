package cn.linkey.dao;

import java.sql.Connection;

import cn.linkey.factory.BeanCtx;
import cn.linkey.util.Tools;

/**
 * MySql创建数据库表实现类
 * 
 * @author Administrator
 */
public class CreatRdbTableORACLE implements CreatRdbTable {

    @Override
    public int creatTable(String dataSourceid, String tableName, String fieldConfig) {
        tableName = tableName.toUpperCase();
        String sql = "CREATE TABLE " + tableName + "("; //数据库表名也转换为大写
        String keyFdStr = "";
        String indexStr = ""; //创建唯一索引

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
                fdName = fdName.toUpperCase(); //字段名全部转为大写

                //mysql不支持xml字段类型数据
                if (fdType.equalsIgnoreCase("xml")) {
                    fdType = "clob";
                    fdVal = "";
                }

                //oracle的为varchar2
                if (fdType.equalsIgnoreCase("varchar")) {
                    fdType = "varchar2";
                }

                //oracle的varchar字段不能太长
                if (Tools.isNotBlank(fdLen)) {
                    //把varchar(max)类型转换为text类型
                    if (fdLen.equalsIgnoreCase("-1")) {
                        fdLen = "";
                        fdType = "clob";
                        fdVal = ""; //text can not default value
                    }
                    else {
                        if (Integer.parseInt(fdLen) > 4000) {
                            fdLen = "4000";
                        }
                        fdLen = "(" + fdLen + ")";
                    }

                }

                //时间类型的不能指定长度,oracle中没有datetime类型
                if (fdType.equalsIgnoreCase("datetime")) {
                    fdType = "date";
                    fdLen = "";
                    fdVal = ""; //datetime can not default value
                }

                //整数类型没有长度
                if (fdType.equalsIgnoreCase("int")) {
                    fdLen = "";
                }

                //是否允许为空
                if (fdNull != null && fdNull.equals("Y")) { //不允许为空
                    fdNull = "NOT NULL";
                }
                else {
                    fdNull = "NULL";
                }

                if (i > 0) {
                    sql += ",";
                }
                sql += fdName + " " + fdType + " " + fdLen + " ";

                //缺省值
                if (Tools.isNotBlank(fdVal)) {
                    if (fdVal.equals("''")) {
                        fdVal = "";
                    }
                    sql += " DEFAULT '" + fdVal + "'";
                }
                else if (fdType.equalsIgnoreCase("varchar")) {
                    //如果是varchar类型则自动给空字符串为缺省值
                    sql += " DEFAULT ''";
                }

                //is null
                sql += " " + fdNull;

                //主键
                if (fdKey != null && fdKey.equals("Y")) {
                    keyFdStr = ",PRIMARY KEY (" + fdName + ")";
                    indexStr = "CREATE UNIQUE INDEX " + tableName + "_" + fdName + " ON " + tableName + " (" + fdName + ")";
                }
            }
        }
        sql += keyFdStr + " )";

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
            if (Tools.isNotBlank(indexStr)) {
                //i=Rdb.execSql(conn,indexStr); //创建唯一索引
            }
            if (i < 0) {
                i = -2;
            } //表示创建出现异常
        }
        catch (Exception e) {
            BeanCtx.log(e, "E", "创建数据库表时出错!");
            i = -2; //表示创建出现异常
        }
        finally {
            if (!dataSourceid.equals("default")) {
                Rdb.close(conn); //如果不是默认链接就需要关闭
            }
        }

        return i;
    }
}
