package cn.linkey.app;

import java.sql.Connection;
import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * 本类负责生成view grid视图
 * 
 * @author Administrator 本类为单例类
 */
public class AppReadGridAction implements AppElement {

    @Override
    public void run(String wf_num) throws Exception {
        if (BeanCtx.getRequest().getMethod().equalsIgnoreCase("get")) {
            BeanCtx.p("Error:禁止执行GET操作!");
            return;
        }
        String action = BeanCtx.g("WF_Action", true);
        if (action.equals("del")) {
            deldoc(wf_num);
        }
        else if (action.equals("copy")) {
            copydoc(wf_num);
        }
        else if (action.equals("btnClick")) {
            btnClick(wf_num);
        }
    }

    public String getElementHtml(String gridNum) throws Exception {
        return "";
    }

    public String getElementBody(String gridNum, boolean readOnly) throws Exception {
        return "";
    }

    /**
     * 删除视图中的文档
     */
    public void deldoc(String gridNum) throws Exception {

        // 获得grid文档的配置参数
        Document gridDoc = AppUtil.getDocByid("BPM_GridList", "GridNum", gridNum, true);
        if (gridDoc.isNull()) {
            BeanCtx.print("{\"msg\":\"Error:The view does not exist!\"");
            return;
        }

        // 看是否开启事务
        if (gridDoc.g("isRollBack").equals("1")) {
            Rdb.setAutoCommit(false); // 开启事务
        }

        BeanCtx.userlog("---", "删除文档", "在视图(" + gridNum + ")中删除文档");
        String[] docArray = Tools.split(BeanCtx.g("WF_OrUnid", true));

        //看是否是ruledata定制数据源
        String dataSourceid = gridDoc.g("DataSource");
        if (dataSourceid.startsWith("R_")) {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("Actionid", "delete");
            params.put("DocUnids", docArray);
            params.put("GridNum", gridNum);
            BeanCtx.getExecuteEngine().run(dataSourceid, params);
            return;
        }

        // 获得json数据源的数据库表名
        String sql = "select SQLTABLENAME, DATASOURCEID from BPM_DataSourceList where Dataid='" + dataSourceid + "' and Status='1'";
        Document dsDoc = Rdb.getDocumentBySql(sql);
        String sqlTableName = dsDoc.g("SQLTABLENAME");
        String dataSourceId = dsDoc.g("DATASOURCEID");
        int i = 0;
        String delmsg = "1";
        LinkeyRule insLinkeyRule = null;
        HashMap<String, Object> params = new HashMap<String, Object>();
        String ruleNum = gridDoc.g("EventRuleNum");
        if (Tools.isNotBlank(ruleNum)) {
            params.put("GridDoc", gridDoc);
            params.put("EventName", "onDocDelete");
            insLinkeyRule = (LinkeyRule) BeanCtx.getBeanByRuleNum(ruleNum); // Grid事件执行对像
        }
        Connection conn = Rdb.getNewConnection(dataSourceId);
        try {
            for (String docUnid : docArray) {
                Document doc = Rdb.getDocumentBySql(conn, "select * from " + sqlTableName + " where WF_OrUnid='" + docUnid + "'");
                if (insLinkeyRule != null) {
                    params.put("DataDoc", doc);
                    delmsg = insLinkeyRule.run(params); // run grid delete doc event
                }
                if (delmsg.equals("1")) {
                    doc.remove(conn, true);
                    i++;
                }
                doc.clear();
            }
        }
        catch (Exception e) {
        }
        finally {
            conn.close();
        }
        if (delmsg.equals("1")) {
            delmsg = "{\"msg\":\"" + BeanCtx.getMsg("Common", "DeleteDocMsg", i) + "\"}";
        }
        else {
            delmsg = "{\"msg\":\"" + delmsg + "\"}";
        }
        if (BeanCtx.isRollBack()) {
            delmsg = "{\"msg\":\"" + BeanCtx.getMsg("Designer", "ViewDelDocRollBack") + "\"}"; // 出错了需要回滚数据
        }
        BeanCtx.print(delmsg);
    }

    /**
     * 拷贝视图中的文档
     */
    public void copydoc(String gridNum) throws Exception {

        // 获得grid文档的配置参数
        Document gridDoc = AppUtil.getDocByid("BPM_GridList", "GridNum", gridNum, true);
        if (gridDoc.isNull()) {
            BeanCtx.print("{\"msg\":\"Error:The view does not exist!\"");
            return;
        }

        // 看是否开启事务
        if (gridDoc.g("isRollBack").equals("1")) {
            Rdb.setAutoCommit(false); // 开启事务
        }

        String[] docArray = Tools.split(BeanCtx.g("WF_OrUnid", true));

        //看是否是ruledata定制数据源
        String dataSourceid = gridDoc.g("DataSource");
        if (dataSourceid.startsWith("R_")) {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("Actionid", "copy");
            params.put("DocUnids", docArray);
            params.put("GridNum", gridNum);
            BeanCtx.getExecuteEngine().run(dataSourceid, params);
            return;
        }

        // 获得json数据源的数据库表名
        String sql = "select SqlTableName from BPM_DataSourceList where Dataid='" + dataSourceid + "' and Status='1'";
        String sqlTableName = Rdb.getValueBySql(sql);
        int i = 0;
        String copymsg = "1";
        LinkeyRule insLinkeyRule = null;
        HashMap<String, Object> params = new HashMap<String, Object>();
        String ruleNum = gridDoc.g("EventRuleNum");
        if (Tools.isNotBlank(ruleNum)) {
            params.put("GridDoc", gridDoc);
            params.put("EventName", "onDocCopy");
            insLinkeyRule = (LinkeyRule) BeanCtx.getBeanByRuleNum(ruleNum); // Grid事件执行对像
        }
        for (String docUnid : docArray) {
            Document doc = Rdb.getDocumentBySql("select * from " + sqlTableName + " where WF_OrUnid='" + docUnid + "'");
            if (insLinkeyRule != null) {
                params.put("DataDoc", doc);
                copymsg = insLinkeyRule.run(params); // run grid copy doc event
            }
            if (copymsg.equals("1")) {
                doc.s("WF_OrUnid", Rdb.getNewid(sqlTableName));
                doc.save(sqlTableName);
                i++;
            }
            doc.clear();
        }

        if (copymsg.equals("1")) {
            copymsg = "{\"msg\":\"" + BeanCtx.getMsg("Common", "CopyDocMsg", i) + "\"}";
        }
        else {
            copymsg = "{\"msg\":\"" + copymsg + "\"}";
        }
        if (BeanCtx.isRollBack()) {
            copymsg = "{\"msg\":\"" + BeanCtx.getMsg("Designer", "ViewCopyDocRollBack") + "\"}"; // 出错了需要回滚数据
        }
        BeanCtx.print(copymsg);
    }

    /**
     * 视图中的文档执行操作
     */
    public void btnClick(String gridNum) throws Exception {
        // 获得grid文档的配置参数
        Document gridDoc = AppUtil.getDocByid("BPM_GridList", "GridNum", gridNum, true);
        if (gridDoc.isNull()) {
            BeanCtx.print("{\"msg\":\"Error:The view does not exist!\"");
            return;
        }

        // 看是否开启事务
        if (gridDoc.g("isRollBack").equals("1")) {
            Rdb.setAutoCommit(false); // 开启事务
        }

        String[] docArray = Tools.split(BeanCtx.g("WF_OrUnid", true));

        //看是否是ruledata定制数据源
        String dataSourceid = gridDoc.g("DataSource");
        if (dataSourceid.startsWith("R_")) {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("Actionid", "btnclick");
            params.put("DocUnids", docArray);
            params.put("GridNum", gridNum);
            BeanCtx.getExecuteEngine().run(dataSourceid, params);
            return;
        }

        // 获得json数据源的数据库表名
        String sql = "select SqlTableName from BPM_DataSourceList where Dataid='" + gridDoc.g("DataSource") + "' and Status='1'";
        String sqlTableName = Rdb.getValueBySql(sql);
        int i = 0;
        String ruleMsg = "";
        LinkeyRule insLinkeyRule = null;
        HashMap<String, Object> params = new HashMap<String, Object>();
        String ruleNum = gridDoc.g("EventRuleNum");
        if (Tools.isNotBlank(ruleNum)) {
            params.put("GridDoc", gridDoc);
            params.put("EventName", "onBtnClick");
            insLinkeyRule = (LinkeyRule) BeanCtx.getBeanByRuleNum(ruleNum); // Grid事件执行对像
        }
        for (String docUnid : docArray) {
            Document doc = Rdb.getDocumentBySql("select * from " + sqlTableName + " where WF_OrUnid='" + docUnid + "'");
            if (insLinkeyRule != null) {
                params.put("DataDoc", doc);
                ruleMsg = insLinkeyRule.run(params); // run grid onbtnClick doc
                                                     // event
                i++;
            }
            doc.clear();
        }
        ruleMsg = "{\"msg\":\"" + ruleMsg.replace("{i}", String.valueOf(i)) + "\"}";
        if (BeanCtx.isRollBack()) {
            ruleMsg = "{\"msg\":\"" + BeanCtx.getMsg("Designer", "ViewBtnDocRollBack") + "\"}"; // 出错了需要回滚数据
        }
        BeanCtx.print(ruleMsg);

    }

}
