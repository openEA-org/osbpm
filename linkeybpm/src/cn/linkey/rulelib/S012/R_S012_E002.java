package cn.linkey.rulelib.S012;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:修改或追加流程权限
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-11 15:40
 */
final public class R_S012_E002 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) {
        //获取事件运行参数
        Document formDoc = (Document) params.get("FormDoc"); //表单配置文档
        Document doc = (Document) params.get("DataDoc"); //数据主文档
        String eventName = (String) params.get("EventName");//事件名称
        if (eventName.equals("onFormOpen")) {
            String readOnly = (String) params.get("ReadOnly"); //1表示只读，0表示编辑
            return onFormOpen(doc, formDoc, readOnly);
        }
        else if (eventName.equals("onFormSave")) {
            return onFormSave(doc, formDoc);
        }
        return "1";
    }

    public String onFormOpen(Document doc, Document formDoc, String readOnly) {
        //当表单打开时
        if (readOnly.equals("1")) {
            return "1";
        } //如果是阅读状态则可不执行
        if (doc.isNewDoc()) {
            //可以对表单字段进行初始化如:doc.s("fdname",fdvalue),可以获取字段值 doc.g("fdname")

        }
        return "1"; //成功必须返回1，否则表示退出并显示返回的字符串
    }

    public String onFormSave(Document doc, Document formDoc) {
        //当表单存盘前
        int i = 0, j = 0;
        String dataType = doc.g("DataType"); //3表示归档数据，2表示流转中的数据，1表示全部数据
        String aclType = doc.g("AclType");// 1表示追加,2表示删除
        String processid = doc.g("Processid");
        String userid = doc.g("Userid");
        String newUserid = doc.g("NewUserid");

        String sqlWhere = "";
        //		BeanCtx.out("DataType="+dataType+" aclType="+aclType+" procesdid="+doc.g("Processid"));
        if (processid.equals("001") || Tools.isBlank(processid)) {
            //表示全部流程
            sqlWhere = "";
        }
        else {
            String[] processidArray = Tools.split(processid);
            for (String pid : processidArray) {
                if (Tools.isNotBlank(sqlWhere)) {
                    sqlWhere += " or ";
                }
                sqlWhere += "WF_Processid='" + pid + "'";
            }
        }
        if (Tools.isNotBlank(sqlWhere)) {
            sqlWhere = " where (" + sqlWhere + ")";
        }

        if (aclType.equals("1")) {//追加权限
            i = addUserAcl(processid, userid, dataType, sqlWhere);
        }
        else if (aclType.equals("2")) { //删除权限
            i = deleteUserAcl(processid, userid, dataType, sqlWhere);
        }
        else if (aclType.equals("3")) {//替换查看权限
            i = replaceUserAcl(processid, userid, newUserid, dataType, sqlWhere);
        }
        else if (aclType.equals("4")) { //替换审批权限
            i = changeUseridToDo(processid, userid, newUserid);
        }

        return "共成功修改(" + i + ")个文档的权限!";
    }

    /**
     * 替换用户的查看权限
     * 
     * @param processid
     * @param userid
     * @param dataType
     * @param sqlWhere
     * @return
     */
    private int replaceUserAcl(String processid, String userid, String newUserid, String dataType, String sqlWhere) {
        int i = 0;
        String dbType = Rdb.getDbType();
        String sql = "", msql = "", tableName = "";
        if (dataType.equals("1")) { //全部数据
            tableName = "BPM_MainData";
            if (dbType.equalsIgnoreCase("MSSQL")) {
                msql = "update BPM_ArchivedData set WF_AllReaders=replace(','+WF_AllReaders+',','," + userid + ",','," + newUserid + ",')  " + sqlWhere;
            }
            else if (dbType.equalsIgnoreCase("MYSQL")) {
                msql = "update BPM_ArchivedData set WF_AllReaders=replace(concat(',',WF_AllReaders,','),'," + userid + ",','," + newUserid + ",')  " + sqlWhere;
            }
            else {
                msql = "update BPM_ArchivedData set WF_AllReaders=replace(','||WF_AllReaders||',','," + userid + ",','," + newUserid + ",')  " + sqlWhere;
            }
        }
        else if (dataType.equals("2")) { //流转中的数据
            tableName = "BPM_MainData";
        }
        else if (dataType.equals("3")) { //归档的数据
            tableName = "BPM_ArchivedData";
        }
        if (dbType.equalsIgnoreCase("MSSQL")) {
            sql = "update " + tableName + " set WF_AllReaders=replace(','+WF_AllReaders+',','," + userid + ",','," + newUserid + ",')  " + sqlWhere;
        }
        else if (dbType.equalsIgnoreCase("MYSQL")) {
            sql = "update " + tableName + " set WF_AllReaders=replace(concat(',',WF_AllReaders,','),'," + userid + ",','," + newUserid + ",')  " + sqlWhere;
        }
        else {
            sql = "update " + tableName + " set WF_AllReaders=replace(','||WF_AllReaders||',','," + userid + ",','," + newUserid + ",')  " + sqlWhere;
        }
        if (Tools.isNotBlank(sql)) {
            i += Rdb.execSql(sql);
        }
        if (Tools.isNotBlank(msql)) {
            i += Rdb.execSql(msql);
        }
        return i;
    }

    /**
     * 删除用户的查看权限
     * 
     * @param processid
     * @param userid
     * @param dataType
     * @param sqlWhere
     * @return
     */
    private int deleteUserAcl(String processid, String userid, String dataType, String sqlWhere) {
        int i = 0;
        String dbType = Rdb.getDbType();
        String sql = "", msql = "", tableName = "";
        if (dataType.equals("1")) { //全部数据
            tableName = "BPM_MainData";
            if (dbType.equalsIgnoreCase("MSSQL")) {
                msql = "update BPM_ArchivedData set WF_AllReaders=replace(','+WF_AllReaders+',','," + userid + ",',',')  " + sqlWhere;
            }
            else if (dbType.equalsIgnoreCase("MYSQL")) {
                msql = "update BPM_ArchivedData set WF_AllReaders=replace(concat(',',WF_AllReaders,','),'," + userid + ",',',')  " + sqlWhere;
            }
            else {
                msql = "update BPM_ArchivedData set WF_AllReaders=replace(','||WF_AllReaders||',','," + userid + ",',',')  " + sqlWhere;
            }
        }
        else if (dataType.equals("2")) { //流转中的数据
            tableName = "BPM_MainData";
        }
        else if (dataType.equals("3")) { //归档的数据
            tableName = "BPM_ArchivedData";
        }
        if (dbType.equalsIgnoreCase("MSSQL")) {
            sql = "update " + tableName + " set WF_AllReaders=replace(','+WF_AllReaders+',','," + userid + ",',',')  " + sqlWhere;
        }
        else if (dbType.equalsIgnoreCase("MYSQL")) {
            sql = "update " + tableName + " set WF_AllReaders=replace(concat(',',WF_AllReaders,','),'," + userid + ",',',')  " + sqlWhere;
        }
        else {
            sql = "update " + tableName + " set WF_AllReaders=replace(','||WF_AllReaders||',','," + userid + ",',',')  " + sqlWhere;
        }
        if (Tools.isNotBlank(sql)) {
            i += Rdb.execSql(sql);
        }
        if (Tools.isNotBlank(msql)) {
            i += Rdb.execSql(msql);
        }
        return i;
    }

    /**
     * 追加用户的查看权限
     * 
     * @param processid
     * @param userid
     * @param dataType
     * @param sqlWhere
     * @return
     */
    private int addUserAcl(String processid, String userid, String dataType, String sqlWhere) {
        int i = 0;
        String dbType = Rdb.getDbType();
        String sql = "", msql = "", tableName = "";
        if (dataType.equals("1")) { //全部数据
            tableName = "BPM_MainData";
            if (dbType.equalsIgnoreCase("MSSQL")) {
                msql = "update BPM_ArchivedData set WF_AllReaders=WF_AllReaders+'," + userid + "' " + sqlWhere;
            }
            else if (dbType.equalsIgnoreCase("MYSQL")) {
                msql = "update BPM_ArchivedData set WF_AllReaders=concat(WF_AllReaders,'," + userid + "') " + sqlWhere;
            }
            else {
                msql = "update BPM_ArchivedData set WF_AllReaders=WF_AllReaders||'," + userid + "' " + sqlWhere;
            }
        }
        else if (dataType.equals("2")) { //流转中的数据
            tableName = "BPM_MainData";
        }
        else if (dataType.equals("3")) { //归档的数据
            tableName = "BPM_ArchivedData";
        }
        if (dbType.equalsIgnoreCase("MSSQL")) {
            sql = "update " + tableName + " set WF_AllReaders=WF_AllReaders+'," + userid + "' " + sqlWhere;
        }
        else if (dbType.equalsIgnoreCase("MYSQL")) {
            sql = "update " + tableName + " set WF_AllReaders=concat(WF_AllReaders,'," + userid + "') " + sqlWhere;
        }
        else {
            sql = "update " + tableName + " set WF_AllReaders=WF_AllReaders||'," + userid + "' " + sqlWhere;
        }
        if (Tools.isNotBlank(sql)) {
            i += Rdb.execSql(sql);
        }
        if (Tools.isNotBlank(msql)) {
            i += Rdb.execSql(msql);
        }
        return i;
    }

    /**
     * 批量替换待办
     * 
     * @param processid
     * @param oldUserid
     * @param newUserid
     */
    private int changeUseridToDo(String processid, String oldUserid, String newUserid) {
        String sqlWhere = "";

        //1.组合sqlwhere
        if (processid.equals("001") || Tools.isBlank(processid)) {
            //表示全部流程
            sqlWhere = "";
        }
        else {
            String[] processidArray = Tools.split(processid);
            for (String pid : processidArray) {
                if (Tools.isNotBlank(sqlWhere)) {
                    sqlWhere += " or ";
                }
                sqlWhere += "Processid='" + pid + "'";
            }
        }
        if (Tools.isNotBlank(sqlWhere)) {
            sqlWhere = " where " + sqlWhere + " and Userid='" + oldUserid + "' and Status='Current'";
        }
        else {
            sqlWhere = "Userid='" + oldUserid + "' and Status='Current'";
        }

        //获得所有当前用户活动的实例记录
        String oldUserName = BeanCtx.getLinkeyUser().getCnName(oldUserid);
        String newUserName = BeanCtx.getLinkeyUser().getCnName(newUserid);
        int i = 0;
        String sql = "select DocUnid from BPM_InsUserList " + sqlWhere;
        LinkedHashSet<String> docUnidList = Rdb.getValueLinkedSetBySql(sql);
        for (String mainDocUind : docUnidList) {
            //更新主表单中的WF_Author字段
            i++;
            sql = "select WF_Author,WF_Author_CN from BPM_MainData where WF_OrUnid='" + mainDocUind + "'";
            Document doc = Rdb.getDocumentBySql(sql);
            if (!doc.isNull()) {
                //更新文档的作者字段
                HashSet<String> author = Tools.splitAsSet(doc.g("WF_Author"));
                author.remove(oldUserid);
                author.add(newUserid);
                HashSet<String> authorName = Tools.splitAsSet(doc.g("WF_Author_CN"));
                authorName.remove(oldUserName);
                authorName.add(newUserName);
                sql = "update BPM_MainData set WF_Author='" + Tools.join(author, ",") + "',WF_Author_CN='" + Tools.join(authorName, ",") + "' where WF_OrUnid='" + mainDocUind + "'";
                Rdb.execSql(sql);
            }
        }

        //2.替换BPM_InsUserList中的记录
        sql = "update  BPM_InsUserList set Userid='" + newUserid + "' " + sqlWhere;
        Rdb.execSql(sql);

        return i;
    }

}