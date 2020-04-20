package cn.linkey.install;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.UUID;

import cn.linkey.dao.Rdb;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;

public class SaveMapToRdb {

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
        SaveMapToRdb.dbType = dbType;
    }

    /**
     * 存盘文档对像
     * 
     * @param doc 要存盘的文档
     * @param conn 指定数据库链接对像，如果为当前链接则可以传null空值
     * @param key 文档所对应数据库表的主键id
     * @return 返回非负数表示存盘成功
     * @throws Exception
     */
    public static int save(Connection conn, HashMap<String, String> doc, String tableName) throws Exception {
        boolean isNewDoc = true; // 是否新文档标记
        String docUnid = "";
        if (dbType.equals("ORACLE")) {
            docUnid = doc.get("WF_ORUNID");
        }
        else {
            docUnid = doc.get("WF_OrUnid");
        }
        String sql = "select WF_OrUnid from " + tableName + " where WF_OrUnid='" + docUnid + "'";
        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next()) {
            isNewDoc = false;//说明记录已经存在
        }

        HashMap<String, String> fdList = getTableFieldConfig(conn, tableName); //所有字段名称and field type

        if (isNewDoc) {
            // 如果是新文档则要设置默认字段
            if (Tools.isBlank(doc.get("WF_OrUnid"))) {
                doc.put("WF_OrUnid", getNewUnid());
            }
            if (doc.containsKey("WF_DocCreated") && Tools.isBlank(doc.get("WF_DocCreated"))) {
                doc.put("WF_DocCreated", DateUtil.getNow());
            }
            if (doc.containsKey("WF_AddName") && Tools.isBlank(doc.get("WF_AddName"))) {
                doc.put("WF_AddName", "admin");
            }
            if (doc.containsKey("WF_AddName_CN") && Tools.isBlank(doc.get("WF_AddName_CN"))) {
                doc.put("WF_AddName_CN", "admin");
            }
        }

        String fdValue;
        int i = 0;
        StringBuilder insertSql = new StringBuilder();
        StringBuilder updateSql = new StringBuilder();
        HashSet<String> removeFdSet = new HashSet<String>(); //doc中没有需要删除的字段
        LinkedHashMap<String, String> clobFdList = new LinkedHashMap<String, String>(); //clob字段

        for (String fdName : fdList.keySet()) {
            //如果字段在doc中不存在则跳过更新
            if (!doc.containsKey(fdName) && !fdName.equalsIgnoreCase("XmlData")) {
                removeFdSet.add(fdName);
                continue;
            }

            //获得字段值
            if (fdName.equalsIgnoreCase("xmldata")) {
                fdValue = getXmlData(doc, fdList.keySet());
            }
            else {
                fdValue = doc.get(fdName);
            }

            //开始判断是否是clob字段
            String fieldType = fdList.get(fdName);//字段类型
            String separator = getFieldSeparator(fdList.get(fdName)); //根据字段类型获得分隔符

            if (fieldType.equals("CLOB")) {
                //clob字段要特殊处理
                clobFdList.put(fdName, fdValue);
                fdValue = "empty_clob()";
                separator = "";
            }
            else {
                //oracle的clob字段不需要替换单引号
                fdValue = fdValue.replace("'", "''"); //替换单引号为两个单引号
                if (dbType.equalsIgnoreCase("mysql")) {
                    fdValue = fdValue.replace("\\", "\\\\");
                } //mysql 需要转义\符号
            }

            //拼接sql语句
            if (isNewDoc) {
                // 组成insert 语句
                if (i == 0) {
                    insertSql.append(separator).append(fdValue).append(separator);
                    i = 1;
                }
                else {
                    insertSql.append(",").append(separator).append(fdValue).append(separator);
                    i = 1;
                }
            }
            else {
                // 组成update语句
                if (i == 0) {
                    updateSql.append(fdName).append("=").append(separator).append(fdValue).append(separator);
                    i = 1;
                }
                else {
                    updateSql.append(",").append(fdName).append("=").append(separator).append(fdValue).append(separator);
                }
            }
            //拼接结束
        }

        //删除文档中没有的字段
        for (String fdName : removeFdSet) {
            fdList.remove(fdName);
        }

        // 再组成最后的SQL语句保存文档数据
        StringBuilder strSql;
        if (isNewDoc) {
            strSql = new StringBuilder(insertSql.length() + 200);
            strSql.append("insert into ").append(tableName).append("(" + Tools.join(fdList.keySet(), ",") + ") values(").append(insertSql).append(")");
        }
        else {
            strSql = new StringBuilder(updateSql.length() + 200);
            strSql.append("update ").append(tableName).append(" set ").append(updateSql).append(" where WF_OrUnid='" + docUnid + "'");
        }
        strSql.trimToSize();
        try {
            //执行insert和update字段非 clob字段类型的

            int r = stmt.executeUpdate(strSql.toString()); // 执行sql语句保存文档

            //开始更新clob字段
            // BeanCtx.out("clob字段列表="+clobFdList);
            if (clobFdList.size() > 0) {
                //这里一定要加for update否则不能进行更新操作
                sql = "select " + Tools.join(clobFdList.keySet(), ",") + " from " + tableName + " where WF_OrUnid = '" + docUnid + "' for update";
                // BeanCtx.out("准备执行sql更新clob字段="+sql);
                rs = stmt.executeQuery(sql);
                if (rs.next()) {
                    //更新所有clob字段的内容
                    for (String fdName : clobFdList.keySet()) {
                        // BeanCtx.out("更新clob="+fdName);
                        Clob c = rs.getClob(fdName);
                        c.truncate(0);// clear  
                        c.setString(1, clobFdList.get(fdName));
                        // eanCtx.out("写入值到clob中="+clobFdList.get(fdName));
                    }
                }
            }

            return r;
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("SQL=" + strSql.toString());
            return -1;
        }
        finally {
            rs.close();
            stmt.close();
        }
    }

    public static String getNewUnid() {
        String unid = UUID.randomUUID().toString();
        unid = unid.replace("-", "0");//.substring(0,32);
        return unid;
    }

    /**
     * 获得数成库表的字段名称和字段类型的map对像
     * 
     * @param conn
     * @param tableName
     * @return
     */
    public static HashMap<String, String> getTableFieldConfig(Connection conn, String tableName) throws Exception {
        String sql = "";
        if (dbType.equals("MSSQL")) {
            sql = "select top 1 * from " + tableName;
        }
        else if (dbType.equals("MYSQL")) {
            sql = "select * from " + tableName + " limit 1";
        }
        else if (dbType.equals("ORACLE")) {
            sql = "select * from " + tableName + " where rownum = 1";
        }
        HashMap<String, String> columns = null;
        ResultSet rs = null;
        try {
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(sql);
            int colCount = rs.getMetaData().getColumnCount();
            columns = new HashMap<String, String>(colCount);
            //读取字段名到数组
            for (int i = 1; i <= colCount; i++) {
                columns.put(rs.getMetaData().getColumnName(i), rs.getMetaData().getColumnTypeName(i));
            }
            stmt.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("getTableFieldConfig(" + sql + ")获得数据库表的字段列表时出错!");
        }
        finally {
            if (rs != null) {
                rs.close();
            }
        }
        return columns;
    }

    /**
     * 返回当前文档对像的XmlData所应该存的字符串,字段名称中不能包含wf_字样，不然就存不进xmldata数据中
     * 
     * @param fdList为数据库表字段清单列表
     * @param doc 数据文档
     * @return 返回xml字符串
     */
    private static String getXmlData(HashMap<String, String> doc, Set<String> fdList) {
        boolean f = false;
        StringBuilder XmlData = new StringBuilder();
        XmlData.append("<Items>");
        String fdListStr = Tools.join(fdList, ",").toUpperCase();
        HashSet<String> upFdList = Tools.splitAsSet(fdListStr); //全部转换为大写的key字段，兼容oracle

        Set<String> allItems = doc.keySet(); // 得到所有字段列表
        for (String fdName : allItems) {
            //(表字段中不包含本字段 且 字段名称不能WF_开头)或者 字段名称以_show节尾
            String upFdName = fdName.toUpperCase(); //全部转换为大写，因为oracle中fdList中的所有字段都为大写
            if ((!upFdList.contains(upFdName) && !upFdName.toLowerCase().startsWith("WF_")) || fdName.endsWith("_SHOW")) {
                // 字段不在数据库表中则全部放入到XmlData中去
                String fdValue = doc.get(fdName);
                if (Tools.isNotBlank(fdValue)) {
                    fdValue = "<![CDATA[" + fdValue + "]]>";//oracle此处不进行单引号替换，因为如果xmldata为clob字段时就会有问题,统一到fdValue中去替换
                }
                XmlData.append("<WFItem name=\"" + fdName + "\">" + fdValue + "</WFItem>");
                f = true;
            }
        }
        XmlData.append("</Items>");
        if (f) {
            return XmlData.toString();
        }
        else {
            return "";
        }
    }

    /**
     * 根据字段类型获得字段的分隔符
     * 
     * @return
     */
    private static String getFieldSeparator(String typeName) {
        String separator = "'";
        String typeList = Tools.getProperty("FieldTypeList").toLowerCase(); //从配置值中获取
        if (typeList.indexOf(typeName.toLowerCase() + ",") != -1) {
            separator = "";
        }
        return separator;
    }
}
