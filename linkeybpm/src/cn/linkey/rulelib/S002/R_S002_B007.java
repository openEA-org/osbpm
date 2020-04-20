package cn.linkey.rulelib.S002;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.wf.ModNode;

/**
 * @RuleName:获得扩展属性列表
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-02 23:25
 */
final public class R_S002_B007 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        ModNode modNode = (ModNode) BeanCtx.getBean("ModNode");
        String processid = BeanCtx.g("Processid");
        String nodeType = BeanCtx.g("NodeType");
        String nodeid = BeanCtx.g("Nodeid");

        //获得数据文档
        String nodeTableName = modNode.getNodeTableName(processid, nodeid); //节点所在数据库表
        String sql = "select * from " + nodeTableName + " where Processid='" + processid + "' and Nodeid='" + nodeid + "'";
        Document nodeDoc = Rdb.getDocumentBySql(sql);

        //获得扩展属性的json
        if (Rdb.getDbType().equals("MSSQL")) {
            sql = "select * from BPM_ModAttributeConfig where ','+AttributeType+',' like '%," + nodeType + ",%' and Status='1' order by SortNum";
        }
        else if (Rdb.getDbType().equals("MYSQL")) {
            sql = "select * from BPM_ModAttributeConfig where concat(',',AttributeType,',') like '%," + nodeType + ",%' and Status='1' order by SortNum";
        }
        else {
            sql = "select * from BPM_ModAttributeConfig where ','||AttributeType||',' like '%," + nodeType + ",%' and Status='1' order by SortNum";
        }

        //	    BeanCtx.out(sql);
        StringBuilder jsonStr = new StringBuilder();
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        jsonStr.append("{\"total\":" + dc.length + ",\"rows\":[");
        int i = 0;
        for (Document doc : dc) {
            if (i == 0) {
                i = 1;
            }
            else {
                jsonStr.append(",");
            }
            String editor = doc.g("EditorConfig");
            if (Tools.isBlank(editor)) {
                editor = "\"editor\":\"text\"";
            }
            jsonStr.append("{\"id\":\"" + doc.g("Attributeid") + "\",\"name\":\"" + doc.g("AttributeName") + "\",\"value\":\"" + nodeDoc.g(doc.g("Attributeid")) + "\",\"group\":\""
                    + doc.g("GroupName") + "\"," + editor + "}");
        }
        jsonStr.append("]}");
        //	    BeanCtx.out(jsonStr.toString());
        BeanCtx.p(jsonStr.toString());
        return "";
    }
}