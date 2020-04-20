package cn.linkey.doc;

import java.sql.Connection;

/**
 * 存盘文档接口，所有需要存盘文档的类需要继承本接口
 * 
 * @author Administrator
 * 
 */
public interface SaveDoc {
    public int save(Connection conn, Document doc, String tableName) throws Exception;

    public int save(Connection conn, Document doc, String tableName, String extendTableName) throws Exception;
}
