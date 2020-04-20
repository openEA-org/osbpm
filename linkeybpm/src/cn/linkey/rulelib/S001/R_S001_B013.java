package cn.linkey.rulelib.S001;

import java.sql.Connection;
import java.util.*;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * 获得数据库表字段(表单自定义用)
 * 
 * @author Administrator 编号:R_S001_B013
 */
public class R_S001_B013 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        BeanCtx.getResponse().setContentType("text/json;charset=utf-8");

        String docunid = BeanCtx.g("wf_docunid", true); // 表单的unid值
        String sql = "select SqlTableName,DataSourceid,FormType,WF_Appid from BPM_FormList where WF_OrUnid='" + docunid + "'";
        Document formDoc = Rdb.getDocumentBySql(sql);
        String sqlTableName = formDoc.g("SqlTableName");
        if (formDoc.g("FormType").equals("3")) {
            sqlTableName = "BPM_SubFormData"; //子表单所在表
        }
        else if (formDoc.g("FormType").equals("2")) {
            sqlTableName = "BPM_MainData"; //子表单所在表
        }
        String dataSourceid = formDoc.g("DataSourceid");
        String title = Tools.isBlank(sqlTableName) ? "No Table" : sqlTableName;
        StringBuilder json = new StringBuilder();
        json.append("[{\"id\":1,\"text\":\"" + title + "\",\"children\":[");
        if (Tools.isNotBlank(sqlTableName)) {
            // 找到配置的数据库表
            sql = "select * from " + sqlTableName + " where WF_OrUnid='" + docunid + "'";
            Set<String> fdList = null;
            if (Tools.isBlank(dataSourceid) || dataSourceid.equals("default")) {
                fdList = Rdb.getTableColumnName(sqlTableName).keySet();
            }
            else {
                Connection conn = null;
                try {
                    conn = Rdb.getNewConnection(dataSourceid);
                    fdList = Rdb.getTableColumnName(conn, sqlTableName).keySet();
                }
                catch (Exception e) {
                    BeanCtx.log(e, "E", "R_S001_B013.获得数据库表字段时出错");
                }
                finally {
                    Rdb.close(conn);
                }
            }
            TreeSet<String> treeSet = new TreeSet<String>(fdList);
            treeSet.comparator();

            // 获得所有字段列表
            int i = 0;
            for (String fdName : treeSet) {
                String itemStr = "{\"id\":\"" + fdName + "\",\"text\":\"" + fdName + "\",\"iconCls\":\"icon-field\"}";
                if (i == 1) {
                    json.append(",");
                }
                else {
                    i = 1;
                }
                json.append(itemStr);
            }
        }
        json.append("]}]");
        BeanCtx.print(json.toString());

        return "";
    }

}
