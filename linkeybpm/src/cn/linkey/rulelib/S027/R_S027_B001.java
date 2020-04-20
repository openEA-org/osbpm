package cn.linkey.rulelib.S027;

import java.sql.Connection;
import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:设计元素对比
 * @author admin
 * @version: 8.0
 * @Created: 2014-11-25 10:21
 */
final public class R_S027_B001 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        analysisAllElement("BPMV8.0");

        return "";
    }

    /**
     * 目标版本为最新版本，当前版本为需要检测的版本
     * 
     * @param targetDataSourceid
     * @throws Exception
     */
    public void analysisAllElement(String targetDataSourceid) throws Exception {
        String sql = "select * from BPM_AllElementList";
        Connection conn = Rdb.getNewConnection(targetDataSourceid);
        int i = 0;
        //目标设计元素集合
        Document[] targetdc = Rdb.getAllDocumentsBySql(conn, "BPM_AllElementList", sql);
        for (Document targetdoc : targetdc) {
            String targetTableName = targetdoc.g("TableName");
            String targetDocUnid = targetdoc.g("WF_OrUnid");
            String targetEleName = targetdoc.g("eleName");
            String targetEleid = targetdoc.g("eleid");

            //获得目标版本设计文档
            sql = "select * from " + targetTableName + " where WF_OrUnid='" + targetDocUnid + "'";
            Document targetEledoc = Rdb.getDocumentBySql(conn, targetTableName, sql);
            targetEledoc.removeItem("WF_LastModified");

            //获得当前版本的设计文档
            sql = "select * from " + targetTableName + " where WF_OrUnid='" + targetDocUnid + "'";
            Document currentEledoc = Rdb.getDocumentBySql(sql);
            currentEledoc.removeItem("WF_LastModified");
            if (currentEledoc.isNull()) {
                //设计不存在
                BeanCtx.p("<font color=red>缺少设计</font>=" + targetEleName + "(" + targetEleid + ")<br>");
            }
            else {
                //设计存在的情况下进行对比分析
                if (!targetEledoc.toString().equals(currentEledoc.toString())) {
                    BeanCtx.p("<font color=blue>设计有更新</font>=" + targetEleName + "(" + targetEleid + ")<br>");
                }
            }
            i++;
        }
        BeanCtx.p("共完成(" + i + ")个设计的对比分析！");
        conn.close();
    }
}