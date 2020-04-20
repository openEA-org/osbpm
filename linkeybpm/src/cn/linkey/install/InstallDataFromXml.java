package cn.linkey.install;

import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import cn.linkey.dao.Rdb;
import cn.linkey.factory.BeanCtx;
import cn.linkey.factory.*;
import cn.linkey.util.Tools;

public class InstallDataFromXml {

    private static String dbType = Rdb.getDbType();
    static {
        if (Tools.isBlank(dbType)) {
            dbType = "ORACLE";
        }
    }

    public static String getDbType() {
        return dbType;
    }

    public static void setDbType(String dbType) {
        InstallDataFromXml.dbType = dbType;
    }

    public static void main(String[] args) throws Exception {

        String dirPath = "F:/bpminitdata/";
        install(dirPath, true);

    }

    /**
     * 根据数据源配制名称获得数据源对像
     * 
     * @param configName 配制名称在 tomcat/conf/catalina/localhost/bpm.xml 文件中指定的名称
     * @return 返回数据源对像
     */
    public static DataSource getDataSource(String configName) {
        try {
            if (Tools.isBlank(configName)) {
                configName = "default";
            } // 默认数据源
            String defaultDataSourceid = Tools.getProperty("DefaultDataSourceid");
            if (Tools.isNotBlank(defaultDataSourceid) && !defaultDataSourceid.equals("default") && configName.equals("default")) {
                configName = defaultDataSourceid; //如果属性文件中指定了默认数据源的编号则使用指定值，如果没有指定则默认为default
            }
            DataSource dataSource = null;
            // 以下创建一个新的数据源
            Context initContext = new InitialContext();
            String appServerid = FactoryEngine.getAppServerid();
            if (appServerid.equals("TOMCAT")) {
                Context envContext = (Context) initContext.lookup("java:/comp/env");
                dataSource = (DataSource) envContext.lookup("jdbc/" + configName);
            }
            else if (appServerid.equals("JBOSS")) {
                dataSource = (DataSource) initContext.lookup("java:jboss/datasources/".concat(configName));
            }
            else {
                //其他从配置文件中读取
                dataSource = (DataSource) initContext.lookup(Tools.getProperty("DataSource").concat("/").concat(configName));
            }
            return dataSource;
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("PoolManager.getDataSource()获得数据源时出错，数据库链接池配置正确!");
            //			BeanCtx.log(e, "E","获得数据源时出错，请确认应用服务的数据源链接正确!");
            return null;
        }
    }

    /*
     * 从线程变量中获得默认数据源的链接对像 如果线程链接对像存在则返回线程链接 如果不存在则创建一个新的链接对像并放入到线程变量中去
     */
    public static Connection getConnection() throws Exception {
        Connection conn = null;
        try {
            DataSource ds = getDataSource("default");
            conn = ds.getConnection(); // 从链接池中拿一个
            BeanCtx.setConnection(conn); // 把拿到的设置到当前线程中
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * 安装
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public static String install(String dirPath, boolean deleteOldData) {

        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            int i = 0;
            File file = new File(dirPath);
            File[] fileList = file.listFiles();
            for (File fileItem : fileList) {
                i++;

                // if(!fileItem.getName().equalsIgnoreCase("bpm_pagelist.xml")){
                //  continue;
                // }

                //删除旧的数据
                if (deleteOldData) {
                    Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    stmt.execute("delete from " + fileItem.getName().substring(0, fileItem.getName().indexOf(".")));
                    stmt.close();
                }

                //开始导入数据
                HashMap<String, String> fdMap = new HashMap<String, String>();
                System.out.println("开始导入:" + fileItem.getPath());
                int j = 0;
                Document xmldoc = load(fileItem);
                List<Element> list = xmldoc.selectNodes("/documents/Items");
                for (Element docItem : list) {
                    List<Element> docList = docItem.selectNodes("WFItem");
                    for (Element item : docList) {
                        String fdName = item.attribute("name").getValue();
                        if (dbType.equals("ORACLE")) {
                            fdName = fdName.toUpperCase();
                        }
                        String fdValue = item.getText();
                        //					   BeanCtx.out(fdName+"="+item.getText());
                        fdMap.put(fdName, fdValue);
                    }

                    String tableName = fdMap.get("WF_OrTableName");
                    if (Tools.isBlank(tableName)) {
                        tableName = fdMap.get("WF_ORTABLENAME");
                    }

                    j++;

                    //数据存盘到数据库中去
                    int r = SaveMapToRdb.save(conn, fdMap, tableName);
                    BeanCtx.out("存数据行==" + j + "==结果==" + r);
                }
            }
            conn.commit();
            return "成功导入(" + i + ")个文件的数据到数据中!";
        }
        catch (Exception e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "数据导入失败!";

    }

    /**
     * load 载入一个xml文档
     * 
     * @return 成功返回Document对象，失败返回null
     * @param uri 文件的全路径
     */
    public static Document load(File file) {
        Document document = null;
        try {
            SAXReader saxReader = new SAXReader();
            document = saxReader.read(file);
        }
        catch (Exception ex) {
            System.out.println("导入xml文件时出错(" + file.getPath() + ")");
        }
        return document;
    }

}
