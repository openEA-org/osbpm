package cn.linkey.doc;

import java.io.File;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.dom4j.Element;

import cn.linkey.dao.Rdb;
import cn.linkey.dao.RdbCache;
import cn.linkey.factory.BeanCtx;
import cn.linkey.util.DateUtil;
import cn.linkey.util.Tools;
import cn.linkey.util.XmlParser;

/**
 * 
 * Document 文档主类操作类
 * 
 * <p>Document 为文档对象的主要操作类，其中文档数组、Set集合文档可借助 {@link Documents}}
 *
 * @author: alibao
 *
 * Modification History:
 * Date         Author          Version            Description
 *---------------------------------------------------------*
 * 2018年8月28日     alibao           v1.0.0               修改原因
 */
public class Document implements Serializable {
    private static final long serialVersionUID = 1L;
    private HashMap<String, String> fdmap;
    private String tableName;
    private boolean isNull = false; //是否空文档 true表示是,false表示否
    private HashMap<String, String> tableFdConfig;//用来存数据库表的所有字段名称列表
    private String keyid = "WF_OrUnid"; //设置关键字段的名称,默认为WF_OrUnid
    private String autoKeyid = ""; //如果数据库表有自增长列，必须指明,否则无法存盘
    private String dbType = ""; //当前文档所对应的数据库链接类型MSSQL,MYSQL,ORACLE三种类型

    /**
     * 
     * @see java.lang.Object#toString()  
     * 重写toString方法
     *
     * @return 格式化JSON对象
     */
    @Override
    public String toString() {
        String fdValue = "";
        int i = 0;
        StringBuilder jsonStr = new StringBuilder();
        for (String fdName : fdmap.keySet()) {
            fdValue = Tools.encodeJson(g(fdName)); //进行json编码
            if (i == 0) {
                jsonStr.append("{\"" + fdName + "\":\"" + fdValue + "\"");
                i = 1;
            }
            else {
                jsonStr.append(",\"" + fdName + "\":\"" + fdValue + "\"");
            }
        }
        if (Tools.isNotBlank(jsonStr.toString())) {
            jsonStr.append("}");
        }
        return jsonStr.toString();
    }

    /**
     * 把文档根据指定的表单转换为HTML代码
     * 
     * @param formNumber 指定的表单编号
     * @param isedit ture表示可编辑状态，false表示只读状态
     * @return 返回html字符串
     */
    public String toHtml(String formNumber, boolean isedit) {
        return "";
    }

    /**
     * 根据数据库表名创建一个Document对像
     * 
     * @param tableName 数据库表名
     */
    public Document(String tableName) {
        this.tableName = tableName;
        this.fdmap = new HashMap<String, String>();
        if (Tools.isNotBlank(tableName)) {
            this.getColumnList(); //获得数据库表的所有字段名称
        }
    }

    /**
     * 根据数据库表名和链接对像创建一个Document对像
     * 
     * @param tableName 数据库表名
     * @param conn 数据库链接对像
     */
    public Document(String tableName, Connection conn) {
        this.tableName = tableName;
        this.fdmap = new HashMap<String, String>();
        if (Tools.isNotBlank(tableName)) {
            this.getColumnList(conn); //获得数据库表的所有字段名称
        }
    }

    /**
     * 文档转Json格式的字符串，使用数据库表中的字段类型
     * 
     * @return Json字符串
     */
    public String toJson() {
        return toString();
    }

    /**
     * 文档转Json格式的字符串<br>
     * 使用本系统中的配置表的字段名称进行输出，如果没有则按数据库表中的字段名进行输出
     * 
     * @param useTableConfig true 表示使用,false 表示使用数据库中的字段名
     * @return Json字符串
     */
    public String toJson(boolean useTableConfig) {
        if (useTableConfig) {
            //使用配置表中的类型
            HashSet<String> allFdList = new HashSet<String>(); //所有配置的字段名称列表
            String sql = "select FieldConfig from BPM_TableConfig where TableName='" + tableName + "'";
            String fieldConfig = Rdb.getValueBySql(sql);
            if (Tools.isNotBlank(fieldConfig)) {
                //从配置表中取字段类型
                int spos = fieldConfig.indexOf("[");
                int epos = fieldConfig.lastIndexOf("]") + 1;
                fieldConfig = fieldConfig.substring(spos, epos);
                com.alibaba.fastjson.JSONArray jsonArr = com.alibaba.fastjson.JSON.parseArray(fieldConfig);
                for (int i = 0; i < jsonArr.size(); i++) {
                    com.alibaba.fastjson.JSONObject rowItem = (com.alibaba.fastjson.JSONObject) jsonArr.get(i);
                    String fdName = rowItem.getString("FdName"); // 字段名称
                    allFdList.add(fdName);
                }
            }
            else {
                return toJson(); //如果没有配置值则返数据库中的配置值
            }
            //取文档中的所有字段组合成 json字符串
            String fdValue = "";
            int i = 0;
            StringBuilder jsonStr = new StringBuilder();
            for (String fdName : fdmap.keySet()) {
                fdValue = Tools.encodeJson(g(fdName)); //进行json编码
                for (String configFdName : allFdList) {
                    if (fdName.equalsIgnoreCase(configFdName)) {
                        fdName = configFdName;
                        allFdList.remove(configFdName);
                        break;
                    }
                }
                if (i == 0) {
                    jsonStr.append("{\"" + fdName + "\":\"" + fdValue + "\"");
                    i = 1;
                }
                else {
                    jsonStr.append(",\"" + fdName + "\":\"" + fdValue + "\"");
                }
            }
            if (Tools.isNotBlank(jsonStr.toString())) {
                jsonStr.append("}");
            }
            return jsonStr.toString();
        }
        else {
            return toString();
        }
    }

    /**
     * 把xml格式中的数据转换存储为doc中的数据
     * 
     * <p>如果一次性要转入多个的xml文件请使用Documents.xmlfile2dc()方法
     * 
     * @param xml 字符串格式为&gt;Items&lt;&gt;WFItem name="字段名"&lt;字段值&gt;/WFItem&lt;&gt;/Items&lt;
     */
    //<Items><WFItem name="字段名">字段值</WFItem></Items>
    @SuppressWarnings("unchecked")
    public void appendFromXml(String xml) {
        if (Tools.isBlank(xml)) {
            return;
        }

        String fdName, fdValue;
        org.dom4j.Document doc = XmlParser.string2XmlDoc(xml);
        List<Element> list = doc.selectNodes("/Items/WFItem");
        for (Element item : list) {
            fdName = item.attribute("name").getValue();
            fdValue = item.getText(); //无需进行格式化，因为导出时使用的是cddata[]模式
            s(fdName, fdValue);
        }
    }

    /**
     * json字符串转为文档对像的字段
     * 
     * @param jsonStr 字符串格式为{"fieldName1":"字段值","fdName2":"value2"}
     */
    public void appendFromJsonStr(String jsonStr) {
        if (Tools.isBlank(jsonStr)) {
            return;
        }

        com.alibaba.fastjson.JSONObject jsonobj = com.alibaba.fastjson.JSON.parseObject(jsonStr);
        for (String fdName : jsonobj.keySet()) {
            fdmap.put(fdName, jsonobj.getString(fdName));
        }
    }

    /**
     * 文档转换成为xml字符串
     * 
     * @param isCDATA true表示使用cdata进行转义，否则将对字段值进行编码,并可以用Rdb.decode()进行解码
     * 
     * @return 返回解码后的XML字符串
     */
    public String toXmlStr(boolean isCDATA) {
        StringBuilder XmlData = new StringBuilder();
        XmlData.append("<Items>");
        Set<String> allItems = this.getAllItemsName(); //得到所有字段列表
        for (String fdName : allItems) {
            String fdValue = this.g(fdName);
            if (Tools.isNotBlank(fdValue) && isCDATA) {
                if (fdValue.indexOf("\n") != -1 || fdValue.indexOf("\r") != -1 || fdValue.indexOf("<") != -1 || fdValue.indexOf("&") != -1) {
                    if (fdValue.indexOf("<![CDATA[") != -1) {
                        fdValue = fdValue.replace("<", "&lt;").replace(">", "&gt;"); //如果cddata中又包含cddata必须要进行转义才可以
                    }
                    fdValue = "<![CDATA[" + fdValue + "]]>";
                }
            }
            else {
                fdValue = fdValue.replace("<", "&lt;").replace(">", "&gt;");
            }
            XmlData.append("<WFItem name=\"" + fdName + "\">" + fdValue + "</WFItem>");
        }
        XmlData.append("</Items>");
        return XmlData.toString();
    }

    /**
     * 文档对像转换成为xmlfile文件
     * 
     * @param filePath 要存盘的文件全路径
     * @param encType 默认utf-8编码输出，可以指定为gb2312
     * @return 返回true表示成功，false表示失败
     */
    public boolean saveToXmlfile(String filePath, String encType) {
        this.s("WF_SourceTableName", this.tableName);
        String xmlStr = this.toXmlStr(true);
        return XmlParser.string2XmlFile(xmlStr, filePath, encType);
    }

    /**
     * 按照数据流出配置信息把文档数据写入到外部数据源中去
     * 
     * @param configid 数据流程配置编号
     * @param formatRuleNum 对流出的数据进行二次格式化
     * @return 返回1表示流出成功
     * 
     * @throws Exception 连接外部数据源出错
     */
    public int saveToOutData(String configid, String formatRuleNum) throws Exception {
        Document configDoc = Rdb.getDocumentBySql("select * from BPM_ProcessMapData where Dataid='" + configid + "'");
        if (configDoc.isNull()) {
            return 0;
        }

        //获得配置信息
        String dataSourceid = configDoc.g("DataSourceid");
        String fieldConfig = configDoc.g("FieldConfig");
        String keyFdName = configDoc.g("keyFdName");
        String sqlTableName = configDoc.g("SqlTableName");

        //创建一个目标文档对像
        Document destDoc;
        java.sql.Connection conn = null;
        try {
            if (!dataSourceid.equals("default") && Tools.isNotBlank(dataSourceid)) {
                //外部数据源
                conn = Rdb.getNewConnection(dataSourceid);
            }

            destDoc = new Document(sqlTableName, conn);

            //开始拷贝所有字段数据到目标文档中去
            int spos = fieldConfig.indexOf("[");
            if (spos == -1) {
                return 0;
            }
            HashMap<String, String> fdTypeCfg = new HashMap<String, String>();
            fieldConfig = fieldConfig.substring(spos, fieldConfig.lastIndexOf("]") + 1);
            com.alibaba.fastjson.JSONArray jsonArr = com.alibaba.fastjson.JSON.parseArray(fieldConfig);
            for (int i = 0; i < jsonArr.size(); i++) {
                com.alibaba.fastjson.JSONObject rowItem = (com.alibaba.fastjson.JSONObject) jsonArr.get(i);
                String destFdName = rowItem.getString("DestFdName"); // 目标字段名称
                String srcFdName = rowItem.getString("SrcFdName"); // 文档字段名称
                String fdType = rowItem.getString("FdType");
                fdTypeCfg.put(destFdName, fdType); //存字段类型
                destDoc.s(destFdName, this.g(srcFdName));
            }

            //对目标文档执行数据转换规则
            if (Tools.isNotBlank(formatRuleNum)) {
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("TargetDoc", destDoc);
                params.put("SourceDoc", this);
                BeanCtx.getExecuteEngine().run(formatRuleNum, params); //运行规则进行数据的再次格式化
            }

            //根据字段类型生成sql语句把数据写入到外部数据源中去
            String sql = "";
            boolean isExist = false;
            if (destDoc.hasItem(keyFdName)) { //如果目标文档已存在关键字的则需要判断目标数据是否已存在
                sql = "select " + keyFdName + " from " + sqlTableName + "  where " + keyFdName + "='" + destDoc.g(keyFdName) + "'";
                if (Rdb.hasRecord(conn, sql)) {
                    isExist = true;
                }
            }

            //开始组合sql语句
            int i = 0;
            StringBuilder insertSql = new StringBuilder();
            StringBuilder updateSql = new StringBuilder();
            for (String fdName : destDoc.getAllItemsName()) {
                String fdValue = destDoc.g(fdName); //字段值

                fdValue = fdValue.replace("'", "''"); // 替换单引号为两个单引号
                // 看存盘时是否需要进行<>的编码
                if (BeanCtx.getEnCodeStatus()) {
                    fdValue = fdValue.replace("<", "&lt;").replace(">", "&gt;");
                }
                if ((!fdTypeCfg.get(fdName).equals("varchar")) && fdValue.equals("")) {
                    fdValue = "NULL";
                }

                if (!isExist) {
                    // 组成insert 语句
                    if (i == 0) {
                        i = 1;
                    }
                    else {
                        insertSql.append(",");
                    }
                    if (fdTypeCfg.get(fdName).equals("varchar")) {
                        insertSql.append("'").append(fdValue).append("'"); //字符串类型
                    }
                    else {
                        insertSql.append(fdValue); //数字或日期
                    }
                }
                else {
                    // 组成update语句
                    if (i == 0) {
                        i = 1;
                    }
                    else {
                        updateSql.append(",");
                    }
                    if (fdTypeCfg.get(fdName).equals("varchar")) {
                        updateSql.append(fdName).append("='").append(fdValue).append("'");
                    }
                    else {
                        updateSql.append(fdName).append("=").append(fdValue);
                    }
                }
            }

            // 再组成最后的SQL语句保存文档数据
            StringBuilder strSql;
            if (!isExist) {
                strSql = new StringBuilder(insertSql.length() + 200);
                strSql.append("insert into ").append(sqlTableName).append("(" + Tools.join(destDoc.getAllItemsName(), ",") + ") values(").append(insertSql).append(")");
            }
            else {
                strSql = new StringBuilder(updateSql.length() + 200);
                strSql.append("update ").append(sqlTableName).append(" set ").append(updateSql).append(" where " + keyFdName + "='" + destDoc.g(keyFdName) + "'");
            }
            strSql.trimToSize();
            if (conn == null) {
                i = Rdb.execSql(strSql.toString()); //执行sql输出数据
            }
            else {
                i = Rdb.execSql(conn, strSql.toString()); //执行sql输出数据
            }
            return i;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            Rdb.close(conn);
        }
        return 0;

    }

    /**
     * 根据文档unid,tableName，初始化字段到Document对像中
     * 
     * @param docUnid 32位文档unid对应数据库表中的WF_OrUNID字段
     */
    public void initByDocUnid(String docUnid) {
        String sql = "select * from " + tableName + " where WF_OrUnid='" + docUnid + "'";
        Rdb.appendDataFromSql(this, sql);
    }

    /**
     * 把sql语句中的字段数据初始化到当前文档中
     * 
     * @param sql 要执行的sql语句
     */
    public void initBySql(String sql) {
        Rdb.appendDataFromSql(this, sql);
    }

    /**
     * 把Request中的参数初始化到文档对像中去
     * 
     * @param request HttpServletRequest请求对像
     * @param encode true 表示对request中的请求参数进行编码&lt;&gt;替换为转义字符; false 表示不需要进行编码,编码后可以防止脚本注入到计算域中而被执行
     */
    public void appendFromRequest(HttpServletRequest request, boolean encode) {
        String fdValue;
        if (request == null) {
            return;
        }
        Map<String, String[]> reqmap = request.getParameterMap();
        for (String key : reqmap.keySet()) {
            fdValue = Tools.join(reqmap.get(key), ",");//把数组转换成为字符串进行存储
            if (encode) {
                //看是否定了需要编码操作
                fdValue = fdValue.replace("<", "&lt;");
                fdValue = fdValue.replace(">", "&gt;");
            }
            this.s(key, fdValue);
        }
    }

    /**
     * 把Request中的参数初始化到文档对像中去
     * 
     * @param request HttpServletRequest请求对像
     */
    public void appendFromRequest(HttpServletRequest request) {
        appendFromRequest(request, false);
    }

    /**
     * 把Request中的参数初始化到文档对像中去
     */
    public void appendFromRequest() {
        appendFromRequest(BeanCtx.getRequest(), false);
    }

    /**
     * 把新的map对像中的值一次性初始化到文档中
     * 
     * @param mapData 需要初始化的map对象数据
     */
    public void appendFromMap(HashMap<String, String> mapData) {
        this.fdmap.putAll(mapData);
    }

    /**
     * 追加新的值到已有字段中去<br>
     * <p>旧值和新值都需要是用逗号进行分隔的值才可以，会自动去掉重复值和空值
     * 
     * @param fdName 字段名称
     * @param textList 要添加的新值多个用逗号分隔
     */
    public void appendTextList(String fdName, String textList) {
        LinkedHashSet<String> fdSet = new LinkedHashSet<String>();
        fdSet.addAll(Tools.splitAsLinkedSet(g(fdName) + "," + textList, ","));
        s(fdName, Tools.join(fdSet, ","));
    }

    /**
     * 追加新的值到已有字段中去<br>
     * <p>旧值和新值都需要是用逗号进行分隔的值才可以，会自动去掉重复值和空值
     * 
     * @param fdName 字段名称
     * @param textList 要添加的新值为set集合
     */
    public void appendTextList(String fdName, Set<String> textList) {
        LinkedHashSet<String> fdSet = Tools.splitAsLinkedSet(g(fdName), ",");
        fdSet.addAll(textList);
        s(fdName, Tools.join(fdSet, ","));
    }

    /**
     * 附加ResultSet的数据到文档中
     * 
     * @param rs 输入rs对像把rs中的数据初始化到文档的map中去
     */
    public void appendFromResultSet(ResultSet rs) {
        try {
            ResultSetMetaData rsMeta = rs.getMetaData();
            int m = rsMeta.getColumnCount();
            for (int i = 0; i < m; i++) {
                String fdName = rsMeta.getColumnLabel(i + 1); //取fdname as a 取得的是a的名称
                String fdValue = rs.getString(fdName);
                if (fdName.equalsIgnoreCase("xmldata")) {
                    if (fdValue != null && fdValue.length() > 20) { //如果xmldata的值大于20才是有意义的xml数据,不然就不用分析消耗性能
                        fdmap.putAll(Tools.xmlStr2Map(fdValue));
                    }
                }
                else {
                    fdmap.put(fdName, fdValue);
                }
            }
        }
        catch (Exception e) {
            BeanCtx.log(e, "E", "运行doc.appendFromResultSet()函数出错！");
        }
    }

    /**
     * 判断文档是否处于锁定状态,未锁定返回空值,已锁定返回锁定者的用户名
     * 
     * @param userid 要判断的用户名，需要判断的主文档UNID
     * 
     * @return 未锁定返回空值,已锁定返回锁定者的用户名
     */
    public String getLockStatus(String userid) {
        String sql = "select Userid from BPM_LockDocList where DocUnid='" + this.getDocUnid() + "'";
        String lockUserid = Rdb.getValueBySql(sql);
        if (lockUserid.equalsIgnoreCase(userid) || Tools.isBlank(lockUserid)) {
            //自已锁定的，返回空值
            return "";
        }
        else {
            //已锁定返回锁定者的用户名
            return lockUserid;
        }
    }

    /**
     * 锁定当前文档,本方法需要加上同步锁，否则在多用户并方同时访问一个文档时会出错
     * 
     * @param docUnid 文档的唯一标识符
     * 
     * @return 返回true表示锁定成功，false表示被其他人锁定的，锁定失败
     */
    public static synchronized boolean lock(String docUnid) {
        String sql = "select Userid from BPM_LockDocList where DocUnid='" + docUnid + "'";
        String lockUserid = Rdb.getValueBySql(sql);
        if (lockUserid.equals(BeanCtx.getUserid())) {
            return true;//已经由本用户锁定了
        }
        else if (Tools.isBlank(lockUserid)) {
            //还没有被锁定,加锁
            String isql = "insert into BPM_LockDocList(WF_OrUnid,Userid,DocUnid,WF_DocCreated) values('" + Rdb.getNewUnid() + "','" + BeanCtx.getUserid() + "','" + docUnid + "','" + DateUtil.getNow()
                    + "')";
            int i = Rdb.execSql(isql);
            if (i > 0) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            //被其他用户锁定了
            return false;
        }
    }

    /**
     * 解锁当前文档
     * 
     * @return 返回true解锁成功,false解锁失败
     */
    public boolean unlock() {
        int i = Rdb.execSql("delete from BPM_LockDocList where DocUnid='" + this.getDocUnid() + "'");
        if (i > 0) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 判断当前文档是否是一个新文档
     * 
     * @return 返回ture表示是新文档,false表示否
     */
    public boolean isNewDoc() {
        String docUnid = g("WF_OrUnid");
        if (Tools.isBlank(docUnid)) {
            return true;
        }
        return isNull; //直接使用空文档功能替代
    }

    /**
     * 判断当前文档是否为空文档对像
     * 
     * @return true 表示是空文档， false 表示不是空文档
     */
    public boolean isNull() {
        return this.isNull;
    }

    /**
     * 判断当前文档是否为空文档对像
     * 
     */
    public void setIsNull() {
        this.isNull = true;
    }

    /**
     * 获得数据库表名
     * 
     * @return 返回数据表表名
     */
    public String getTableName() {
        return this.tableName;
    }

    /**
     * 设置文档所在数据库表名
     * 
     * <p>表名可以不存在，如果存在请使用setTableName()方法 只有数据库表名不存在时才使用本方法
     * 
     * @param tableName 任意名称
     */
    public void setTableNameOnly(String tableName) {
        this.tableName = tableName;
        this.tableFdConfig = null;
    }

    /**
     * 设置文档所在数据库表
     * 
     * <p>设置后系统会自动计算数据库表的所有字段到文档的tableFdConfig属性中
     * 
     * @param tableName 数据库表名，数据库表一定要真实存在，否则报错 如果表确实不存在可以使用setTableNameOnly()方法
     */
    public void setTableName(String tableName) {
        setTableName(null, tableName);
    }

    /**
     * 设置文档所在数据库表
     * 
     * <p>设置后系统会自动计算数据库表的所有字段到文档的tableFdConfig属性中
     * 
     * @param conn 数据库链接对像
     * @param tableName 数据库表名，数据库表一定要真实存在，否则报错 如果表确实不存在可以使用setTableNameOnly()方法
     */
    public void setTableName(Connection conn, String tableName) {
        this.tableName = tableName;
        if (this.tableFdConfig != null) {
            this.tableFdConfig.clear(); //首先要清除之前的记录再计算
            this.tableFdConfig = null; //设置为空才可以，不然在getColumnList()中不会重新计算表字段
        }
        this.getColumnList(conn);//重新计算本文档所在数据库表的字段列表
        this.dbType = Rdb.getDbType(conn);
    }

    /**
     * @return 获得文档的创建时间,如果是新文档则返回当前时间
     */
    public String getCreated() {
        String created = g("WF_DocCreated");
        if (Tools.isBlank(created)) {
            created = DateUtil.getNow();
        }
        return created;
    }

    /**
     * 获得当前文档的32位唯一id号
     * 
     * <p>若当前文档没有唯一的docunid，那么底层将通过Rdb.getNewid()获取一个新的32位唯一id
     * 
     * @return 返回文档的32位id号
     */
    public String getDocUnid() {
        String docunid = g("WF_OrUnid");
        if (Tools.isBlank(docunid)) {
            docunid = Rdb.getNewid(tableName);
        }
        return Rdb.formatArg(docunid);
    }

    /**
     * 获得文档所的字段列表
     * 
     * @return 返回Set对像
     */
    public Set<String> getAllItemsName() {
        return fdmap.keySet();
    }

    /**
     * 获得所有字段的map对像
     * 
     * <p>key为字段名 value为字段值 示例:HashMap&gt;String,String&lt; fieldMap=doc.getAllItems();
     * 
     * @return 返回map对像
     */
    //示例:HashMap<String,String> fieldMap=doc.getAllItems();
    public HashMap<String, String> getAllItems() {
        return this.fdmap;
    }

    /**
     * 清空文档
     */
    public void clear() {
        if (this.fdmap != null) {
            this.fdmap.clear();
        }
        if (this.tableFdConfig != null) {
            this.tableFdConfig.clear();
        }
    }

    /**
     * 拷贝一个文档的到指定的表中去
     * 
     * @param tableName 目的数据库表名
     * @return 返回新的目标文档对像
     */
    public Document copyTo(String tableName) {
        return copyTo(null, tableName);
    }

    /**
     * 拷贝一个文档的到指定的表中去
     * 
     * @param conn 目的数据库链接对像
     * @param tableName 目的数据库表名
     * @return 返回新的目标文档对像
     */
    public Document copyTo(Connection conn, String tableName) {
        Document newdoc = BeanCtx.getDocumentBean(tableName);
        this.copyAllItems(newdoc);
        newdoc.s("WF_OrUnid", Rdb.getNewUnid());
        newdoc.save(conn);
        return newdoc;
    }

    /**
     * 拷贝一个文档的所有字段到另一个文档中去
     * 
     * @param targetDocument 目的文档对像
     * @return 返回新的目标文档对像
     */
    public Document copyAllItems(Document targetDocument) {
        targetDocument.fdmap.putAll(this.fdmap);
        return targetDocument;
    }

    /**
     * 拷贝一个文档的所有字段到另一个文档中去,不包含WF_开头的字段内容
     * 
     * @param targetDocument 目的文档对象
     * 
     * @param noSystemField true表示不拷贝WF_开头的系统字段，false表示全部
     * 
     * @return 返回新的目标文档对像
     */
    public Document copyAllItems(Document targetDocument, boolean noSystemField) {
        if (noSystemField) {
            for (String fdName : this.getAllItemsName()) {
                if (!fdName.startsWith("WF_")) {
                    targetDocument.s(fdName, this.g(fdName));
                }
            }
        }
        else {
            targetDocument.fdmap.putAll(this.fdmap);
        }
        return targetDocument;
    }

    /**
     * 设置文档的字段值，有则覆盖，没有则新增,不区分大小写
     * 
     * @param fdName 字段名
     * 
     * @param fdValue 字段值
     */
    public void s(String fdName, Object fdValue) {
        //s时不进行字段名称的转换，s()什么就是什么
        if (fdValue == null) {
            fdValue = "";
        }
        else {
            fdValue = fdValue.toString();
        }
        fdName = getExistFdNameInDoc(fdName);
        this.fdmap.put(fdName, fdValue.toString());
    }
    
    /**
     * 移除文档的字段值
     * 
     * @param fdName 字段名
     */
    public void r(String fdName) {
        this.fdmap.remove(fdName);
    }

    /**
     * 设置文档的字段值，有则覆盖，没有则新增
     * 
     * @param fdName 字段名
     * 
     * @param fdValue set集合值会自动转换为用逗号分隔的字符串存储到fdName字段中去
     */
    public void s(String fdName, Set<String> fdValue) {
        if (fdValue != null) {
            fdName = getExistFdNameInDoc(fdName);
            this.fdmap.put(fdName, Tools.join(fdValue, ","));
        }
    }

    /**
     * 获取字段值，如果字段不存在则返回 "" 空值，不区分大小写
     * 
     * @param fdName 要获取的字段名
     * 
     * @return 存在返回对应的值，不存在返回为空
     */
    public String g(String fdName) {
        if (fdName.equalsIgnoreCase("WF_AttachmentName")) {
            return Tools.join(this.getAttachmentsName(false), ","); //直接返回附件名称
        }
        //到文档中查找已存在的字段，不区分大小写
        fdName = getExistFdNameInDoc(fdName);
        String fdValue = this.fdmap.get(fdName);
        if (fdValue == null) {
            fdValue = "";
        }
        return fdValue;
    }

    /**
     * 在当前文档中查找已经存在的字段名称，不区分大小写
     * 
     * @param fdName 需要查找的字段名
     * 
     * @return 返回对应的值，不存在返回null
     */
    private String getExistFdNameInDoc(String fdName) {
        //到文档中查找已存在的字段，不区分大小写
        for (String itemName : fdmap.keySet()) {
            if (itemName.equalsIgnoreCase(fdName)) {
                return itemName;
            }
        }
        return fdName;
    }

    /**
     * 获得本文档的附件名称的set集合,不含路径信息仅文件名
     * 
     * @param diskName true表示返回附件在硬盘中的直实名称，false表示返回上传时使用的文件名称
     * 
     * @return 附件名称的set集合 LinkedHashSet
     */
    public LinkedHashSet<String> getAttachmentsName(boolean diskName) {
        if (diskName) {
            LinkedHashSet<String> fileList = new LinkedHashSet<String>();
            Document[] dc = Rdb.getAllDocumentsBySql("select FileName,WF_OrUnid from BPM_AttachmentsList where DocUnid='" + this.getDocUnid() + "'");
            for (Document doc : dc) {
                String fileName = doc.g("FileName");
                fileName = doc.g("WF_OrUnid") + fileName.substring(fileName.lastIndexOf("."));
                fileList.add(fileName);
            }
            return fileList;
        }
        else {
            String sql = "select FileName from BPM_AttachmentsList where DocUnid='" + this.getDocUnid() + "'";
            return Rdb.getValueLinkedSetBySql(sql);
        }
    }

    /**
     * 获得本文档的附件名称和文件在硬盘中的路径的set集合
     * 
     * <p>包含路径信息和文件名 返回路径为: attachment/201403/132sdfdsdfds00999.gif
     * 
     * @return 返回所有附件的路径集合 LinkedHashSet
     */
    public LinkedHashSet<String> getAttachmentsNameAndPath() {
        LinkedHashSet<String> fileList = new LinkedHashSet<String>();
        Document[] dc = Rdb.getAllDocumentsBySql("select FileName,WF_OrUnid,FilePath from BPM_AttachmentsList where DocUnid='" + this.getDocUnid() + "'");
        for (Document doc : dc) {
            String fileName = doc.g("FileName");
            fileName = doc.g("FilePath") + doc.g("WF_OrUnid") + fileName.substring(fileName.lastIndexOf("."));
            fileList.add(fileName);
        }
        return fileList;
    }

    /**
     * 获得本文档的所有附件文档对像的数组
     * 
     * @return 返回附件文档数组
     */
    public Document[] getAttachmentsDoc() {
        return Rdb.getAllDocumentsBySql("select * from BPM_AttachmentsList where DocUnid='" + this.getDocUnid() + "'");
    }

    /**
     * 删除文档中的附件根据附件文档的unid
     * 
     * @param docUnid 附件的WF_OrUnid
     * 
     * @param realRemove true表示真实从硬盘中删除掉，false表示只进行删除标记，附件不会被真正删除
     * 
     * @return 返回正数表示删除成功
     */
    public int removeAttachmentByFileUnid(String docUnid, boolean realRemove) {
        String sql = "select * from BPM_AttachmentsList where WF_OrUnid='" + docUnid + "'";
        Document fileDoc = Rdb.getDocumentBySql(sql);
        if (!fileDoc.isNull()) {
            if (realRemove) {
                removeAttachmentByFileDoc(fileDoc);
                sql = "delete from BPM_AttachmentsList where WF_OrUnid='" + docUnid + "'";
            }
            else {
                sql = "update BPM_AttachmentsList set DeleteFlag='1' where WF_OrUnid='" + docUnid + "'";
            }
            return Rdb.execSql(sql);
        }
        return 0;
    }

    /**
     * 删除文档中的附件根据附件名称(附件上传时的名称)
     * 
     * @param fileName 附件名称
     * 
     * @param realRemove true表示真实从硬盘中删除掉，false表示只进行删除标记，附件不会被真正删除
     * 
     * @return 返回正数表示删除成功
     */
    public int removeAttachmentByName(String fileName, boolean realRemove) {
        String sql = "select * from BPM_AttachmentsList where DocUnid='" + this.getDocUnid() + "' and FileName='" + fileName + "'";
        Document fileDoc = Rdb.getDocumentBySql(sql);
        if (!fileDoc.isNull()) {
            if (realRemove) {
                removeAttachmentByFileDoc(fileDoc);
                sql = "delete from BPM_AttachmentsList where DocUnid='" + this.getDocUnid() + "' and FileName='" + fileName + "'";
            }
            else {
                sql = "update BPM_AttachmentsList set DeleteFlag='1' where DocUnid='" + this.getDocUnid() + "' and FileName='" + fileName + "'";
            }
            return Rdb.execSql(sql);
        }
        return 0;
    }

    /**
     * 删除文档中的所有附件
     * 
     * @param realRemove true表示真实从硬盘中删除掉，false表示只进行删除标记，附件不会被真正删除
     * 
     * @return 返回正数表示删除成功
     */
    public int removeAllAttachments(boolean realRemove) {
        String sql = "";
        if (realRemove) {
            sql = "select * from BPM_AttachmentsList where DocUnid='" + this.getDocUnid() + "'";
            Document[] dc = Rdb.getAllDocumentsBySql(sql);
            for (Document fileDoc : dc) {
                removeAttachmentByFileDoc(fileDoc);
            }
            sql = "delete from BPM_AttachmentsList where DocUnid='" + this.getDocUnid() + "'"; //删除附件记录
        }
        else {
            sql = "update BPM_AttachmentsList set DeleteFlag='1' where DocUnid='" + this.getDocUnid() + "'";
        }
        return Rdb.execSql(sql);
    }

    /**
     * 根据附件文档从硬盘中删除附件文件
     * 
     * @param fileDoc 附件文档对像
     * 
     * @return 返回1表示删除成功，0表示删除失败
     */
    public int removeAttachmentByFileDoc(Document fileDoc) {
        String fileFolder = fileDoc.g("FilePath"); //附件所在文件夹名称
        String fileName = fileDoc.g("FileName"); //附件名称
        String extName = fileName.substring(fileName.lastIndexOf(".")); //文件扩展名称
        String diskFileName = fileDoc.g("WF_OrUnid") + extName; //得到文件扩展名称加上文件编号,硬盘中的文件名称
        String diskFilePath = BeanCtx.getAppPath() + fileFolder + diskFileName; //硬盘中的全路径
        File file = new File(diskFilePath);
        if (file.exists()) {
            file.delete();
            return 1;
        }
        return 0;
    }

    /**
     * 拷贝当前文档的附件到另一个文档中去
     * 
     * @param targetDoc 目标文档对像
     * 
     * @return 0表示没有拷贝附件,大于0表示拷贝成功的附件数
     */
    public int copyAttachment(Document targetDoc) {
        try {
            int i = 0;
            String sql = "select * from BPM_AttachmentsList where DocUnid='" + this.getDocUnid() + "'";
            Document[] dc = Rdb.getAllDocumentsBySql(sql);
            for (Document doc : dc) {

                //先获得源文件的路径
                String fileName = doc.g("FileName"); //文件名
                String folder = doc.g("FilePath"); //文件路径
                String extName = fileName.substring(fileName.lastIndexOf(".")); //文件扩展名
                String srcDiskFileName = folder + doc.g("WF_OrUnid") + extName;
                String srcFilePath = BeanCtx.getAppPath() + srcDiskFileName; //源文件硬盘路径

                //获得目标文件的路径
                doc.s("DocUnid", targetDoc.getDocUnid()); //把附件标记为目标文档的附件
                doc.s("WF_OrUnid", Rdb.getNewUnid()); //创建一个新文档
                String destFilePath = BeanCtx.getAppPath() + BeanCtx.getAttachmentFolder() + "/" + DateUtil.getDateNum() + "/" + doc.g("WF_OrUnid") + extName; //目标文件硬盘路径
                //				BeanCtx.out(srcFilePath);
                //				BeanCtx.out(destFilePath);

                //开始拷贝文件
                File srcFile = FileUtils.getFile(srcFilePath);
                File destFile = FileUtils.getFile(destFilePath);
                FileUtils.copyFile(srcFile, destFile);

                //存盘新的附件文档对像
                doc.save();

                i++;
            }
            return i;
        }
        catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 给文件附加一个附件
     * 
     * @param filePath 文件在服务器硬盘中的全路径,服务器会自动拷贝一个文件到attachment目录下<br>
     *                 如果文件已经在attachment目录下请直接在sql表中插入一条记录即可，无需使用此方法
     *                 
     * @param fileFieldName 附加在表单上传控件的字段名称
     * @return 0
     */
    public int addAttachment(String filePath, String fileFieldName) {
        //creat a attachment document
        java.text.DateFormat insDateFormat = new java.text.SimpleDateFormat("yyyyMM");
        String newFileFolder = (String) insDateFormat.format(new java.util.Date());

        if (Tools.isBlank(fileFieldName)) {
            fileFieldName = "file1";
        }
        int spos = filePath.replace("\\", "/").lastIndexOf("/") + 1;
        String fileName = filePath.substring(spos); //文件的名称
        File srcfile = FileUtils.getFile(filePath);
        if (srcfile.exists()) {
            //如果附件存在
            Document fileDoc = BeanCtx.getDocumentBean("BPM_AttachmentsList");
            fileDoc.s("DocUnid", this.getDocUnid());
            fileDoc.s("FileName", fileName);
            fileDoc.s("FdName", fileFieldName);
            fileDoc.s("DeleteFlag", "0");
            fileDoc.s("FilePath", BeanCtx.getAttachmentFolder() + "/" + newFileFolder + "/");
            fileDoc.s("WF_OrUnid", Rdb.getNewUnid());
            String destpath = BeanCtx.getAppPath() + fileDoc.g("FilePath"); //获取文件附件的路径
            String extName = filePath.substring(filePath.lastIndexOf(".")); //文件扩展名称
            String destfileName = fileDoc.g("WF_OrUnid") + extName;
            File destfile = FileUtils.getFile(destpath + destfileName); //目标附件的硬盘路径
            try {
                FileUtils.copyFile(srcfile, destfile); //拷贝到附件目录中去
                BeanCtx.out("copy file " + srcfile.getAbsolutePath() + " to " + destfile.getAbsolutePath());
            }
            catch (Exception e) {
                BeanCtx.log(e, "E", "doc.addAttachment附件文件拷贝错误");
            }
            fileDoc.s("FileSize", String.valueOf(FileUtils.sizeOf(srcfile) / 1024) + "k");
            fileDoc.save();
        }

        return 0;
    }

    /**
     *
     * 获得此文档所在数据库表的所有字段名称列表集合,使用默认数据源
     * 
     * @return 返回数据库表的所有字段Set集合
     */
    public HashMap<String, String> getColumnList() {
        return getColumnList(null);
    }

    /**
     * 获得此文档所在数据库表的所有字段名称集合
     * 
     * @param conn 指定数据库链接对像
     * @return 返回数据库表的所有字段Set集合
     */
    @SuppressWarnings("unchecked")
    private HashMap<String, String> getColumnList(Connection conn) {
        if (Tools.isBlank(this.tableName)) {
            return this.tableFdConfig;
        }
        if (this.tableFdConfig == null) {
            //先从系统缓存中查找是否已缓存了本数据库表的所有字段
            if (conn == null) {
                this.dbType = Rdb.getDbType(); //获得数据库链接类型
                if (tableName.startsWith("BPM_") && BeanCtx.getSystemConfig("CacheSystemTableConfig").equals("1")) {
                    //只有在默认数据源且表名是以BPM_开头的表情况下才进行缓存,避免在不同的数据源下使用相同的数据库表名时出问题
                    this.tableFdConfig = null;
                    HashMap<String, String> tableColCache = (HashMap<String, String>) RdbCache.get(tableName + "_col");
                    if (tableColCache != null) {
                        this.tableFdConfig = new HashMap<String, String>(); //这里必须返回一个新的hashMap对像，不然在SaveDocMSSQL中对fdList进行删除操作时将影响缓存中的数据
                        this.tableFdConfig.putAll(tableColCache);
                    }
                    if (this.tableFdConfig == null) {
                        //重新到数据库中去读取,在多线程运行时有可能重复执行本put方法，但是不影响使用，所以此处暂不进行多线程锁

                        this.tableFdConfig = Rdb.getTableColumnName(tableName);
                        HashMap<String, String> fdConfigMapForCache = new HashMap<String, String>(); //这里必须新建一个map对像用来作缓存，否则doc对像中与缓存是相互引用关系，将出问题
                        fdConfigMapForCache.putAll(tableFdConfig);
                        RdbCache.put(tableName + "_col", fdConfigMapForCache);
                    }
                }
                else {
                    //不进行缓存
                    this.tableFdConfig = Rdb.getTableColumnName(tableName);
                }
            }
            else {
                //不是默认链接的情况下不进行数据库表的缓存
                this.dbType = Rdb.getDbType(conn); //获得数据库链接类型
                this.tableFdConfig = Rdb.getTableColumnName(conn, tableName);
            }
        }
        return this.tableFdConfig;
    }

    /**
     * 删除文档中的一个字段,不区分大小写
     * 
     * @param fdName 要删除的字段名称
     */
    public void removeItem(String fdName) {
        fdName = getExistFdNameInDoc(fdName); //不区分大小写
        this.fdmap.remove(fdName);
    }

    /**
     * 看文档中是否存在此字段,区分大小写
     * 
     * @param itemName 表字段名
     * @return true表示有，false表示没有
     */
    public boolean hasItem(String itemName) {
        for (String fdname : fdmap.keySet()) {
            if (fdname.equalsIgnoreCase(itemName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断数据库表中是否存在此字段，不区分大小写
     * 
     * @param fdName 表字段名
     * 
     * @return true表示有，false表示没有
     */
    public boolean hasTableItem(String fdName) {
        for (String itemName : tableFdConfig.keySet()) {
            if (itemName.equalsIgnoreCase(fdName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获得文档设定的自增长列名称，默认为空值，不允许数据库表有自增长列
     * 
     * @return 有则返回字段名称
     */
    protected String getAutoKeyid() {
        return autoKeyid;
    }

    /**
     * 设定文档的自增长列名称
     * 
     * <p>默认为空值 如果有自增长列则必须用此方法指定，否则存盘时因无法更新自增长列而报错
     * 
     * @return
     */
    protected void setAutoKeyid(String autoKeyid) {
        this.autoKeyid = autoKeyid;
    }

    /**
     * 获得本文档所在数据库表的关键字段名称
     * 
     * @return 返回keyid，默认为WF_OrUnid
     */
    protected String getKeyid() {
        return keyid;
    }

    /**
     * 设置本文档所在数据库表的关键字段名称,默认为WF_OrUnid
     * 
     * @param keyid 默认为WF_OrUnid
     */
    public void setKeyid(String keyid) {
        this.keyid = keyid;
    }

    /**
     * 设置本文档所在数据库表的关键字段名称,默认为WF_OrUnid
     * 
     * @param keyid 关键字段，默认为WF_OrUnid
     * 
     * @param keyVaue 字段的值
     * 
     */
    public void setKeyid(String keyid, String keyVaue) {
        this.keyid = keyid;
        this.s("WF_OrUnid", keyVaue);
    }

    /**
     * 存盘到当前数据源中
     * 
     * @return 返回非负数表示存盘成功，否则存盘失败
     */
    public int save() {
        return save(null, getTableName(), "xmldata");
    }

    /**
     * 存盘到当前数据源指定的表中
     * 
     * @param tableName 数据库表名
     * 
     * @return 返回非负数表示存盘成功，否则存盘失败
     */
    public int save(String tableName) {
        this.setTableName(tableName);
        return save(null, tableName, "xmldata");
    }

    /**
     * 指定数据库链接对像和表名把文档存储到指定的表中去
     * 
     * @param conn Connection 连接对象
     * 
     * @return 返回非负数表示存盘成功，否则存盘失败
     */
    public int save(Connection conn) {
        return save(conn, getTableName());
    }

    /**
     * 指定数据库链接对像和表名把文档存储到指定的表中去
     * 
     * @param conn Connection 连接对象
     * 
     * @param tableName 表名
     * 
     * @return 返回非负数表示存盘成功，否则存盘失败
     */
    public int save(Connection conn, String tableName) {
        return save(conn, tableName, "xmldata");
    }

    /**
     * 指定数据库链接对像和表名把文档存储到指定的表中去
     * 
     * @param tableName 表名
     * 
     * @param extendTableName 扩展表名必须是xmldata时才需要进行存储
     * 
     * @return 返回非负数表示存盘成功，否则存盘失败
     */
    public int saveToExtendTable(String tableName, String extendTableName) {
        return save(null, tableName, extendTableName);
    }

    /**
     * 指定数据库链接对像和表名把文档存储到指定的表中去
     * 
     * @param conn Connection 连接对象
     * 
     * @param tableName 表名
     * 
     * @param extendTableName 扩展表名必须是xmldata时才需要进行存储
     * 
     * @return 返回非负数表示存盘成功，否则存盘失败
     */
    public int save(Connection conn, String tableName, String extendTableName) {

        if (Tools.isBlank(tableName)) {
            BeanCtx.log("E", "doc.save()存盘时出错 :文档对像中没有指定数据库表名，可以用doc.setTableName('表名')进行设置!");
            return -1;
        }

        //要保证在文档存盘时一定要有数所库所在表的字段名称,如果没有就需要重新计算
        if (conn == null) {
            if (this.tableFdConfig == null || this.tableFdConfig.size() == 0) {
                //表名不为空但字段列表为空时，需要重新计算一次表的字段列表
                this.tableFdConfig = Rdb.getTableColumnName(conn, tableName); //重新计算
            }
        }
        else {
            //如果是新的链接对像则要重新计算链接对像的数据库类型和字段配置类型
            this.setDbType(Rdb.getDbType(conn)); //设置为oracle数据库类型
            this.tableFdConfig = Rdb.getTableColumnName(conn, tableName);//重新计算
        }

        //开始调用实现类存盘
        SaveDoc insSaveDoc = (SaveDoc) BeanCtx.getBean("SaveDoc" + this.dbType);
        try {
            return insSaveDoc.save(conn, this, tableName, extendTableName);
        }
        catch (Exception e) {
            BeanCtx.log(e, "E", "文档存盘时出错!");
            return 0;
        }
    }

    /**
     * 清除已有的动态表格字段的值
     * 
     * @param fdList 多个字段用逗号分隔，字段名称不需要带_1数字编号
     * 
     * @return 返回删除的字段个数
     */
    public int removeEditorField(String fdList) {
        int j = 0;
        if (Tools.isNotBlank(fdList)) { //删除主文档中的动态表格字段
            String[] fdListArray = Tools.split(fdList);
            for (int i = 1; i < 1000; i++) { //假定最大为1000行数据
                for (String fdName : fdListArray) {
                    if (this.hasItem(fdName + "_" + i)) {
                        this.removeItem(fdName + "_" + i);
                        j++;
                    }
                    else {
                        i = 100000;
                        break;
                    }
                }
            }
        }
        return j;
    }

    /**
     * 把文档移动一份到回收站中去 注意：本方法不会拷贝附件到回收站中去
     * 
     * @return i大于0 表示处理成功 i小于0 表示异常
     */
    public int removeToTrash() {
        return removeToTrash(null);
    }

    /**
     * 把文档移动一份到回收站中去
     * 
     * @param conn 数据库链接 注意：本方法不会拷贝附件到回收站中去
     * 
     * @return i大于0 表示处理成功 i小于0 表示异常
     */
    public int removeToTrash(Connection conn) {
        //把文档存到回收站中去
        if (conn == null) {
            try {
                conn = Rdb.getConnection();
            }
            catch (Exception e) {
                BeanCtx.log(e, "E", "Doc.removeToTrash()获得数据库链接出错!");
            }
        }
        StringBuilder sql = new StringBuilder();
        int i = 0;
        if (Rdb.getDbType().equals("ORACLE")) {
            //oracle有empty_clob()字段，需要特别处理
            String newOrUnid = Rdb.getNewUnid();
            sql.append("insert into BPM_DocTrash (WF_OrUnid,SourceOrUnid,SourceTableName,WF_DocCreated,WF_AddName,WF_AddName_CN,XmlData) values(");
            sql.append("'" + newOrUnid + "'");
            sql.append(",'" + this.getDocUnid() + "'");
            sql.append(",'" + this.tableName + "'");
            sql.append(",'" + DateUtil.getNow() + "'");
            sql.append(",'" + BeanCtx.getUserid() + "'");
            sql.append(",'" + BeanCtx.getUserName() + "'");
            sql.append(",empty_clob()");
            sql.append(")");
            i = Rdb.execSql(conn, sql.toString());
            //更新clob字段
            Rdb.saveClobField(conn, newOrUnid, "BPM_DocTrash", "XMLDATA", toXmlStr(true));

        }
        else {
            sql.append("insert into BPM_DocTrash (WF_OrUnid,SourceOrUnid,SourceTableName,WF_DocCreated,WF_AddName,WF_AddName_CN,XmlData) values(");
            sql.append("'" + Rdb.getNewid("BPM_DocumentTrash") + "'");
            sql.append(",'" + this.getDocUnid() + "'");
            sql.append(",'" + this.tableName + "'");
            sql.append(",'" + DateUtil.getNow() + "'");
            sql.append(",'" + BeanCtx.getUserid() + "'");
            sql.append(",'" + BeanCtx.getUserName() + "'");
            sql.append(",'" + toXmlStr(true).replace("'", "''") + "'");
            sql.append(")");
            i = Rdb.execSql(conn, sql.toString());
        }
        return i;
    }

    /**
     * 删除当前文档,返回非负数表示删除成功，否则删除失败
     * 
     * @param trash true表示删除时copy一份到回收站中，false表示强制删除不可恢复
     * 
     * @return 返回受影响的行数
     */
    public int remove(boolean trash) {
        return remove(null, trash);
    }

    /**
     * 删除当前文档,返回非负数表示删除成功，否则删除失败
     * 
     * @param conn 数据库链接对像
     * 
     * @param trash true 表示删除时copy一份到回收站中，false 示强制删除不可恢复
     * 
     * @return 返回受影响的行数
     */
    public int remove(Connection conn, boolean trash) {
        //删除文档
        if (conn == null) {
            try {
                conn = Rdb.getConnection();
            }
            catch (Exception e) {
                BeanCtx.log(e, "E", "Doc.removeToTrash()获得数据库链接出错!");
            }
        }
        int i = 2;
        if (trash) {
            i = removeToTrash(); //自动copy一份到回收站中去
            if (i < 1) {
                return i;
            } //如果回收站拷贝不成功，删除不执行后面的操作，禁止删除原数据
            this.removeAllAttachments(false); //把附件标记为已删除的
        }
        else {
            this.removeAllAttachments(true); //从sql记录和硬盘中同时删除附件文件
        }

        //增加日记
        BeanCtx.log("I", "删除了一个文档所在表(" + this.getTableName() + ")WF_OrUnid(" + this.getDocUnid() + ")");

        //执行删除文档操作
        String sql = "delete from " + tableName + " where WF_OrUnid='" + g("WF_OrUnid") + "'";
        return Rdb.execSql(conn, sql);
    }

    /**
     * 获得当前文档所对应实体表的字段配置信息包含字段名和字段类型的map对像
     * 
     * @return key为字段名,value为字段类型的map对像
     */
    public HashMap<String, String> getTableFdConfig() {
        return tableFdConfig;
    }

    /**
     * 设置文档对应实体表的字段配置信息包含字段名称和字段类型的map对像
     * 
     * @param tableFdConfig 一个包含文档信息的map对象，
     */
    public void setTableFdConfig(HashMap<String, String> tableFdConfig) {
        this.tableFdConfig = tableFdConfig;
    }

    /**
     * 获得当前文档来源的数据库链接的类型(MSSQL\MYSQL\ORACLE)
     * 
     * @return 返回数据库类型
     */
    public String getDbType() {
        return dbType;
    }

    /**
     * 当doc.save(conn)指定链接存盘时，系统会自动重新计算新链接的数据库类型
     * 
     * @param dbType 数据库类型
     */
    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

}
