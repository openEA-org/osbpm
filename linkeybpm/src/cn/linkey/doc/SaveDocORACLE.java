package cn.linkey.doc;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import cn.linkey.dao.Rdb;
import cn.linkey.factory.BeanCtx;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;

/**
 * 存盘文档对像,针对MS SQL对像，表结构中需要有WF_OrUnid关键字段 如果有自增长列则必须在key参数中指定
 * 
 * @author Administrator
 */
public class SaveDocORACLE implements SaveDoc {

    /**
     * 存盘文档对像，多余字段存储在xmldata字段中
     * 
     * @param doc 要存盘的文档
     * @param conn 指定数据库链接对像，如果为当前链接则可以传null空值
     * @return 返回非负数表示存盘成功
     */
    public int save(Connection conn, Document doc, String tableName) throws Exception {
        return save(conn, doc, tableName, "xmldata");
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
    public int save(Connection conn, Document doc, String tableName, String extendTableName) throws Exception {
        boolean isNewDoc = true; // 是否新文档标记
        String docUnid = doc.getDocUnid();
        String sql = "select " + doc.getKeyid() + " from " + tableName + " where " + doc.getKeyid() + "='" + docUnid + "'";
        if (Rdb.hasRecord(conn, sql)) {
            isNewDoc = false;
        }
        // BeanCtx.out("doc="+doc.toString());
        HashMap<String, String> fdList = new HashMap<String, String>();
        if (tableName.equalsIgnoreCase(doc.getTableName())) {
            // 如果表名和文档是相等的情况下直接从文档对像中拿
            fdList.putAll(doc.getColumnList()); // 这里要保证一定能拿到文档所在数所库表的清单，否则报错,这里不能直接操作getColumnList()返回的hashSet对像，只以复制一份
        }
        else {
            // 如果表名和文档对像中的表名不一至，说明是存扩展表，扩展表是没有缓存的
            fdList.putAll(Rdb.getTableColumnName(conn, tableName)); // 这里要保证一定能拿到文档所在数所库表的清单，否则报错,这里不能直接操作getColumnList()返回的hashSet对像，只以复制一份
        }
        fdList.remove(doc.getAutoKeyid()); // 把表的主键去掉，不然自增长列插不进去
        // BeanCtx.out("fdList="+fdList.toString());
        // BeanCtx.out("oracle存入doc="+doc);
        if (isNewDoc) {
            // 如果是新文档则要设置默认字段
            if (Tools.isBlank(doc.g("WF_OrUnid"))) {
                doc.s("WF_OrUnid", docUnid);
            }
            if (doc.hasTableItem("WF_DocCreated") && Tools.isBlank(doc.g("WF_DocCreated"))) {
                doc.s("WF_DocCreated", DateUtil.getNow());
            }
            if (doc.hasTableItem("WF_AddName") && Tools.isBlank(doc.g("WF_AddName"))) {
                doc.s("WF_AddName", BeanCtx.getUserid());
            }
            if (doc.hasTableItem("WF_AddName_CN") && Tools.isBlank(doc.g("WF_AddName_CN"))) {
                doc.s("WF_AddName_CN", BeanCtx.getUserName());
            }
        }
        // 设置最后更新时间
        if (doc.hasTableItem("WF_LastModified")) {
            doc.s("WF_LastModified", DateUtil.getNow());
        }
        // 设置最后更新者
        if (doc.hasTableItem("WF_LastUpdateUser")) {
            doc.s("WF_LastUpdateUser", BeanCtx.getUserid());
        }
        // 检测是否有多个WF_Appid只取第一个
        String appid = doc.g("WF_Appid");
        if (appid.indexOf(",") != -1) {
            doc.s("WF_Appid", appid.substring(0, appid.indexOf(",")));
        }
        String fdValue;
        int i = 0;
        StringBuilder insertSql = new StringBuilder();
        StringBuilder updateSql = new StringBuilder();
        HashSet<String> removeFdSet = new HashSet<String>(); // doc中没有需要删除的字段
        LinkedHashMap<String, String> clobFdList = new LinkedHashMap<String, String>(); // clob字段
        for (String fdName : fdList.keySet()) {
            // 如果字段在doc中不存在则跳过更新
            if (!doc.hasItem(fdName) && !fdName.equalsIgnoreCase("XmlData")) {
                removeFdSet.add(fdName);
                continue;
            }

            // 获得字段值
            if (extendTableName.equals("xmldata") && fdName.equalsIgnoreCase("xmldata")) {
                fdValue = getXmlData(doc, fdList.keySet());
                fdValue = fdValue.replace("&amp;", "&");
            }
            else {
                fdValue = doc.g(fdName);
            }

            // 开始判断是否是clob字段
            String fieldType = fdList.get(fdName);// 字段类型
            String separator = getFieldSeparator(fdList.get(fdName)); // 根据字段类型获得分隔符

            if (fieldType.equals("CLOB")) {
                // clob字段要特殊处理
                clobFdList.put(fdName, fdValue);
                fdValue = "empty_clob()";
                separator = "";
            }
            else if (fieldType.equals("date")) {
                // 存盘的时候，oracle需要对时间格式的字段要进行转换，显示时也需要转换不然会显示为2015-09-01 00:00 多出一个00:00的尾巴
                fdValue = "to_date('" + fdValue + "','yyyy-mm-dd')";
                separator = "";
            }
            else {
                // oracle的clob字段不需要替换单引号
                fdValue = fdValue.replace("'", "''"); // 替换单引号为两个单引号
                if (!fdName.equalsIgnoreCase("xmldata")) {
                    // 看存盘时是否需要进行<>的编码,xmldata字段不能这样替换
                    if (BeanCtx.getEnCodeStatus()) {
                        fdValue = fdValue.replace("<", "&lt;").replace(">", "&gt;");
                    }
                }
            }

            // 拼接sql语句
            fdValue = fdValue.replace("©", "&copy;").replace("®", "&reg;").replace("™", "&trade;");
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
            // 拼接结束

        }

        // 删除文档中没有的字段
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
            strSql.append("update ").append(tableName).append(" set ").append(updateSql).append(" where " + doc.getKeyid() + "='" + docUnid + "'");
        }
        strSql.trimToSize();
        try {
            // BeanCtx.out("准备存盘文档doc="+doc);
            // BeanCtx.out("SaveDocOracle sql="+strSql.toString());
            // 执行insert和update字段非 clob字段类型的
            int r = 0;
            if (conn == null) {
                r = Rdb.execSql(strSql.toString()); // 执行sql语句保存文档
            }
            else {
                r = Rdb.execSql(conn, strSql.toString()); // 执行sql语句保存文档
            }

            // 开始更新clob字段
            // BeanCtx.out("clob字段列表="+clobFdList);
            if (clobFdList.size() > 0) {
                // 这里一定要加for update否则不能进行更新操作
                sql = "select " + Tools.join(clobFdList.keySet(), ",") + " from " + tableName + " where WF_OrUnid = '" + docUnid + "' for update";
                // BeanCtx.out("准备执行sql更新clob字段="+sql);
                Statement stm = null;
                ResultSet rs = null;
                try {
                    if (conn == null) {
                        stm = Rdb.getConnection().createStatement();
                    }
                    else {
                        stm = conn.createStatement();
                    }
                    rs = stm.executeQuery(sql);
                    if (rs.next()) {
                        // 更新所有clob字段的内容
                        for (String fdName : clobFdList.keySet()) {
                            // BeanCtx.out("更新clob="+fdName);
                            Clob c = rs.getClob(fdName);
                            c.truncate(0);// clear
                            c.setString(1, clobFdList.get(fdName));
                            // BeanCtx.out("写入值到clob中="+clobFdList.get(fdName));
                        }
                    }
                }
                catch (Exception e) {
                    BeanCtx.log(e, "E", "");
                }
                finally {
                    Rdb.closers(rs);
                }

            }

            // 如果扩展表名不是xmldata的情况下还要再调用一次来把数据存到扩展表中去
            if (!extendTableName.equalsIgnoreCase("xmldata")) {
                save(conn, doc, extendTableName, "xmldata");
            }

            return r;
        }
        catch (Exception e) {
            BeanCtx.log(e, "E", "SQL=" + strSql.toString());
            return -1;
        }
    }

    /**
     * 返回当前文档对像的XmlData所应该存的字符串,字段名称中不能包含wf_字样，不然就存不进xmldata数据中
     * 
     * @param fdList为数据库表字段清单列表
     * @param doc 数据文档
     * @return 返回xml字符串
     */
    private String getXmlData(Document doc, Set<String> fdList) {
        boolean f = false;
        StringBuilder XmlData = new StringBuilder();
        XmlData.append("<Items>");
        Set<String> allItems = doc.getAllItemsName(); // 得到所有字段列表
        for (String fdName : allItems) {
            // (表字段中不包含本字段 且 字段名称不能WF_开头)或者 字段名称以_show节尾
            String upFdName = fdName.toUpperCase(); // 全部转换为大写，因为oracle中fdList中的所有字段都为大写
            //if ((!fdList.contains(upFdName) && !upFdName.startsWith("WF_")) || fdName.endsWith("_SHOW")) {
        	//20181009 添加setContainsItem(fdName, fdList)方法，替代!fdList.contains(fdName)，忽略字段大小写
            if ((!setContainsItem(fdName, fdList) && !upFdName.startsWith("WF_")) || fdName.endsWith("_SHOW")) {
                // 字段不在数据库表中则全部放入到XmlData中去
                String fdValue = doc.g(fdName);
                if (Tools.isNotBlank(fdValue)) {
                    fdValue = "<![CDATA[" + fdValue + "]]>";// oracle此处不进行单引号替换，因为如果xmldata为clob字段时就会有问题,统一到fdValue中去替换
                }
                XmlData.append("<WFItem name=\"" + fdName + "\">" + fdValue + "</WFItem>");
                f = true;
            }
        }
        XmlData.append("</Items>");
        // System.out.println(XmlData.toString());
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
    private String getFieldSeparator(String typeName) {
        String separator = "'";
        String typeList = Tools.getProperty("FieldTypeList").toLowerCase(); // 从配置值中获取
        if (typeList.indexOf(typeName.toLowerCase() + ",") != -1) {
            separator = "";
        }
        return separator;
    }
    
    
    /**
     * 
     * @Description 判断Set<String> 集合是否包含某个fdName，忽略大小写[BPM 12.0新增]
     *
     * @param fdName 需要匹配的fName
     * @param fdList 需要在此Set集合中查找
     * @return true 存在 false 不存在
     */
    private boolean setContainsItem(String fdName, Set<String> fdList){
    	
    	boolean flag = false;
    	
    	for(String key : fdList){
    		if(fdName.equalsIgnoreCase(key)){
    			flag = true;
    			break;
    		}
    	}	
    	return flag;
    }
}
