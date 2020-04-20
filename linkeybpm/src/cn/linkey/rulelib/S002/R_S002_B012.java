package cn.linkey.rulelib.S002;

import java.util.HashMap;
import java.util.LinkedHashSet;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;

/**
 * @RuleName:流程左则节点树
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-05 16:31
 */
final public class R_S002_B012 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        String tempStr = "";
        String processid = BeanCtx.g("Processid");
        String nodeid = BeanCtx.g("Nodeid");
        if (nodeid.equals("root")) {
            //所有节点列表
            tempStr = GetAllNodeList(processid, nodeid);
        }
        else {
            //获得人员及事件列表
            tempStr = GetNodeUserEventList(processid, nodeid);
        }
        BeanCtx.p(tempStr);
        return "";
    }

    public String GetNodeUserEventList(String processid, String nodeid) {
        return "";
    }

    public String GetAllNodeList(String processid, String nodeid) {
        StringBuilder jsonStr = new StringBuilder();
        String sql = "select * from BPM_AllModNodeList where Processid='" + processid + "' and NodeType<>'Process' order by NodeType,NodeName";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        LinkedHashSet<Document> nodeSet = new LinkedHashSet<Document>();
        LinkedHashSet<Document> routerSet = new LinkedHashSet<Document>();
        for (Document doc : dc) {
            String nodeType = doc.g("NodeType");
            if (nodeType.equals("SequenceFlow")) {
                routerSet.add(doc);
            }
            else {
                nodeSet.add(doc);
            }
        }
        nodeSet.addAll(routerSet);

        for (Document doc : nodeSet) {
            String nodeType = doc.g("NodeType");
            String img = "";
            if (nodeType.equals("SequenceFlow")) {
                img = "linkey/bpm/ext/wf/images/line.gif";
            }
            else {
                img = "linkey/bpm/images/icons/class.gif";
            }
            if (jsonStr.length() > 0) {
                jsonStr.append(",");
            }
            jsonStr.append("{\"text\":\"" + Tools.encodeJson(doc.g("NodeName")) + "(" + doc.g("Nodeid") + ")\",\"icon\":\"" + img + "\",\"leaf\":\"true\",\"id\":\"" + doc.g("Nodeid") + "\"}");
        }
        jsonStr.insert(0, "[");
        jsonStr.append("]");
        return jsonStr.toString();
    }

}