package cn.linkey.app;

import java.sql.Connection;
import java.util.HashMap;
import java.util.HashSet;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.doc.Documents;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * 本类主要负责存盘编辑类型Grid的数据
 * 
 * @author Administrator
 *
 */
public class AppEditorGridAction implements AppElement {

    @Override
    public void run(String wf_num) throws Exception {
        if (BeanCtx.getRequest().getMethod().equalsIgnoreCase("get")) {
            BeanCtx.p("Error:禁止执行GET操作!");
            return;
        }

        saveEditorData(wf_num);
    }

    /**
     * 无返回值
     */
    public String getElementBody(String wf_num, boolean readOnly) {
        return "";
    }

    /**
     * 无返回值
     */
    public String getElementHtml(String wf_num) {
        return "";
    }

    /**
     * 保存EditorGrid修改的数据
     */
    public void saveEditorData(String gridNum) throws Exception {
        Document gridDoc = AppUtil.getDocByid("BPM_GridList", "GridNum", gridNum, true);
        if (gridDoc.isNull()) {
            BeanCtx.print(Tools.jmsg("error", "Error:The view does not exist!"));
            return;
        }

        // 获得json数据源的数据库表名
        String sql = "select SqlTableName,DataSourceid from BPM_DataSourceList where Dataid='" + gridDoc.g("DataSource") + "' and Status='1'";
        Document sdoc = Rdb.getDocumentBySql(sql);
        String dataSourceid = sdoc.g("DataSourceid"); //数据源
        String sqlTableName = sdoc.g("SqlTableName"); //数据库表

        //链接数据库
        Connection conn = null;
        if (!dataSourceid.equals("default")) {
            try {
                conn = Rdb.getNewConnection(dataSourceid);
            }
            catch (Exception e) {
                e.printStackTrace();
                BeanCtx.out("获得数据库链接出错!");
            }
        }

        // 看是否开启事务
        if (gridDoc.g("isRollBack").equals("1")) {
            Rdb.setAutoCommit(false); // 开启事务
        }

        String newRow = BeanCtx.g("newRow", true);
        String editRow = BeanCtx.g("editRow", true);
        String delRow = BeanCtx.g("delRow", true);
        String parentDocUnid = BeanCtx.g("WF_ParentDocUnid", true);

        //run save editor event
        LinkeyRule insLinkeyRule = null;
        HashMap<String, Object> params = new HashMap<String, Object>();
        String ruleNum = gridDoc.g("EventRuleNum");
        if (Tools.isNotBlank(ruleNum)) {
            params.put("GridDoc", gridDoc);
            insLinkeyRule = (LinkeyRule) BeanCtx.getBeanByRuleNum(ruleNum); // Grid事件执行对像
        }

        int s = 0;
        int e = 0; //错误的存盘数
        int edit = 0; //修改次数
        int n = 0;
        HashMap<String, String> tableFdConfig = null;
        String dbType = "";

        //开始存新的文档
        HashSet<Document> newdc = Documents.jsonStr2dc(newRow, "");
        for (Document doc : newdc) {

            doc.s("WF_ParentDocUnid", parentDocUnid);
            doc.s("WF_GridNum", gridDoc.g("GridNum"));

            if (insLinkeyRule != null) {
                params.put("EventName", "onDocSave");
                params.put("DataDoc", doc);
                insLinkeyRule.run(params); // run grid save doc event
            }

            //BeanCtx.out("准备新增文档="+doc);
            if (tableFdConfig != null) {
                doc.setTableNameOnly(sqlTableName);
                doc.setTableFdConfig(tableFdConfig);
                doc.setDbType(dbType);
            }
            else {
                doc.setTableName(conn, sqlTableName);
                tableFdConfig = doc.getTableFdConfig();
                dbType = doc.getDbType();
            }
            s = doc.save(conn);
            if (s > 0) {
                n++;
            }
            else {
                e++;
            }
        }

        //存盘更新的文档
        tableFdConfig = null;
        HashSet<Document> editdc = Documents.jsonStr2dc(editRow, "");
        for (Document doc : editdc) {

            doc.s("WF_ParentDocUnid", parentDocUnid);
            doc.s("WF_GridNum", gridDoc.g("GridNum"));

            if (insLinkeyRule != null) {
                params.put("EventName", "onDocSave");
                params.put("DataDoc", doc);
                insLinkeyRule.run(params); // run grid save doc event
            }

            //BeanCtx.out("准备更新文档="+doc);
            if (tableFdConfig != null) {
                doc.setTableNameOnly(sqlTableName);
                doc.setTableFdConfig(tableFdConfig);
                doc.setDbType(dbType);
            }
            else {
                doc.setTableName(conn, sqlTableName);
                tableFdConfig = doc.getTableFdConfig();
                dbType = doc.getDbType();
            }
            s = doc.save(conn);
            if (s < 1) {
                e++;
            }else{
            	edit++; //修改次数
            }
        }

        //删除delRow中的文档
        int d = 0;
        tableFdConfig = null;
        HashSet<Document> deldc = Documents.jsonStr2dc(delRow, "");
        for (Document doc : deldc) {

            if (insLinkeyRule != null) {
                params.put("EventName", "onDocDelete");
                params.put("DataDoc", doc);
                insLinkeyRule.run(params); // run grid save doc event
            }

            //BeanCtx.out("准备删除文档="+doc);
            if (tableFdConfig != null) {
                doc.setTableNameOnly(sqlTableName);
                doc.setTableFdConfig(tableFdConfig);
                doc.setDbType(dbType);
            }
            else {
                doc.setTableName(conn, sqlTableName);
                tableFdConfig = doc.getTableFdConfig();
                dbType = doc.getDbType();
            }
            s = doc.remove(conn, true);
            if (s > 0) {
                d++;
            }
            else {
                e++;
            }
        }

        String msg = "";
        if (e > 0) {
            BeanCtx.setRollback(true);
            msg = BeanCtx.getMsg("Designer", "SaveEditorGridDataError");
            BeanCtx.p(Tools.jmsg("error", msg));
        }
        else {
            msg = BeanCtx.getMsg("Designer", "SaveEditorGridData", n, edit, d);
            BeanCtx.p(Tools.jmsg("ok", msg));
        }
        if (conn != null) {
            Rdb.close(conn); //如果数据库链接不为空则需要主动关闭
        }
        BeanCtx.userlog("---", "修改数据", "在EditorGrid视图(" + gridNum + ")中修改数据(" + msg + ")");

    }

}
