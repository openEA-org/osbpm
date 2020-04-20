package cn.linkey.dao;

/**
 * 本类为创建数据库表的接口
 * 
 * @author Administrator
 */
public interface CreatRdbTable {
    // 创建数据库表
    public int creatTable(String dataSourceid, String tableName, String fieldConfig);
}
