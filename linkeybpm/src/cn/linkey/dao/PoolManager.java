package cn.linkey.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import cn.linkey.factory.BeanCtx;
import cn.linkey.factory.FactoryEngine;
import cn.linkey.factory.ThreadContext;
import cn.linkey.util.Tools;

/**
 * 本类的主要功能是获得数据库链接对像
 * 
 * 使用synchronized关键字可以避免多线程时拿到同一个链接这样就会出错.
 * 
 * @author Administrator 本类为单例类,主要由Rdb类进行调用
 */
public final class PoolManager {

    protected static ConcurrentHashMap<String, DataSource> dataSourceMap = new ConcurrentHashMap<String, DataSource>(); // 用来缓存数据源对像

    /**
     * 根据数据源配制名称获得数据源对像
     * 
     * @param configName 配制名称在 tomcat/conf/catalina/localhost/bpm.xml 文件中指定的名称
     * @return 返回数据源对像
     */
    protected static DataSource getDataSource(String configName) {
        try {
            if (Tools.isBlank(configName)) {
                configName = "default";
            } // 默认数据源
            String defaultDataSourceid = Tools.getProperty("DefaultDataSourceid");
            if (Tools.isNotBlank(defaultDataSourceid) && !defaultDataSourceid.equals("default") && configName.equals("default")) {
                configName = defaultDataSourceid; //如果属性文件中指定了默认数据源的编号则使用指定值，如果没有指定则默认为default
            }
            DataSource dataSource = dataSourceMap.get(configName);
            if (dataSource == null) {
                // 以下创建一个新的数据源
                Context initContext = new InitialContext();
                String jndiStr = Tools.getProperty("JNDI_".concat(configName));
                if (Tools.isNotBlank(jndiStr)) {
                    dataSource = (DataSource) initContext.lookup(jndiStr);//直接使用jndi字符串进行数据源链接
                }
                else {
                    String appServerid = FactoryEngine.getAppServerid();
                    if (appServerid.equals("TOMCAT")) {
                        Context envContext = (Context) initContext.lookup("java:/comp/env");
                        dataSource = (DataSource) envContext.lookup("jdbc/" + configName);
                    }
                    else if (appServerid.equals("JBOSS")) {
                        //jboss...
                        dataSource = (DataSource) initContext.lookup("java:jboss/datasources/".concat(configName));
                    }
                    else {
                        //其他从配置文件中读取
                        dataSource = (DataSource) initContext.lookup(Tools.getProperty("DataSource").concat("/").concat(configName));
                    }
                }
                dataSourceMap.put(configName, dataSource);
            }
            return dataSource;
        }
        catch (Exception e) {
            BeanCtx.log(e, "E", "PoolManager.getDataSource()获得数据源时出错，数据库链接池配置正确!");
            return null;
        }
    }

    /**
     * 从线程变量中获得默认数据源的链接对像
     * 
     * 如果线程链接对像存在则返回线程链接 <br />
     * 如果不存在则创建一个新的链接对像并放入到线程变量中去
     * 
     * @return
     * @throws Exception
     */
    protected static Connection getConnection() throws Exception {
        //synchronized
        Connection conn = null;
        try {
            ThreadContext insThreadContext = BeanCtx.getContext();
            conn = insThreadContext.getConnection();
            // 首先从当前线程中拿链接，如果拿到了就直接返回否则就从数据库池中拿
            if (conn == null) {
                DataSource ds = getDataSource("default");
                conn = ds.getConnection(); // 从链接池中拿一个
                BeanCtx.setConnection(conn); // 把拿到的设置到当前线程中
            }
        }
        catch (SQLException e) {
            BeanCtx.log(e, "E", "PoolManager中拿数据库链接时出错");
        }
        return conn;
    }

    /**
     * 获得一个新的数据库链接对像,configName=default时也会返回一个新的链接对像
     * 
     * @param configName 数据源配置id
     * @return 返回一个新的数据库链接对像
     */
    protected static Connection getNewConnection(String configName) throws Exception {
        Connection conn = getDataSource(configName).getConnection(); // 从链接池中拿一个新的链接
        BeanCtx.out("PoolManager.getNewConnection()中拿到一个新的数据库链接对像=" + conn);
        return conn;
    }
}
