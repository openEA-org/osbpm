package cn.linkey.rulelib.S030;

import java.util.HashMap;
import java.util.HashSet;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.wf.ModNode;

/**
 * @RuleName:流程操作规则
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-05 14:37
 */
final public class R_S030_B006 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        //params为运行本规则时所传入的参数
        String processid = BeanCtx.g("Processid");
        String action = BeanCtx.g("Action");
        if (action.equals("CheckNodeAttr")) {
            checkAllNodeAttr(processid);
        }
        else if (action.equals("SaveAllDefaultNode")) {
            saveAllDefaultNodeAttr(processid);
        }
        else if (action.equals("DeleteNode")) {
            deleteNode(processid);
        }
        return "";
    }

    /**
     * 删除指定节点
     * 
     * @param processid
     */
    public void deleteNode(String processid) {
        String nodeid = BeanCtx.g("Nodeid", true);
        String tableName = getNodeTableName(processid, nodeid);
        if (Tools.isNotBlank(tableName)) {
            String sql = "delete from " + tableName + " where Processid='" + processid + "' and Nodeid='" + nodeid + "'";
            Rdb.execSql(sql);
        }
        BeanCtx.p(Tools.jmsg("ok", "节点成功删除!"));
    }

    /**
     * 检测所有节点是否有保存属性
     */
    public void checkAllNodeAttr(String processid) {
        HashSet<String> noAttrNode = new HashSet<String>();
        String nodeList = BeanCtx.g("NodeList", true);
        String[] nodeArray = Tools.split(nodeList, ",");
        for (String itemid : nodeArray) {
            int spos = itemid.indexOf("#");
            String nodeid = itemid.substring(0, spos);
            String objid = itemid.substring(spos + 1);
            String sql = "select * from BPG_AllModNodeList where Processid='" + processid + "' and Nodeid='" + nodeid + "'";
            if (!Rdb.hasRecord(sql)) {
                noAttrNode.add(objid);
            }
        }
        BeanCtx.p(Tools.jmsg("ok", Tools.join(noAttrNode, ",")));
    }

    /**
     * 保存节点的所有缺省属性
     */
    public void saveAllDefaultNodeAttr(String processid) {
        ModNode modNode = (ModNode) BeanCtx.getBean("ModNode");
        String nodeid = BeanCtx.g("Nodeid", true);
        String nodeType = BeanCtx.g("NodeType", true);
        String sql = "select * from BPG_AllModNodeList where Processid='" + processid + "' and Nodeid='" + nodeid + "'";
        Document doc = Rdb.getDocumentBySql(sql);
        if (doc.isNull()) {
            //说明节点没有保存过，进行保存
            doc.s("Processid", processid);
            doc.s("Nodeid", nodeid);
            doc.s("NodeType", modNode.getNodeType(processid, nodeid));
            doc.setTableName(modNode.getNodeTableName(processid, nodeid));
            if (nodeType.equals("Router")) {
                //保存路由线
                // BeanCtx.out("startnodeid="+BeanCtx.g("StartNodeid"));
                doc.s("ExtNodeType", "sequenceFlow");
                doc.s("SourceNode", BeanCtx.g("StartNodeid"));
                doc.s("TargetNode", BeanCtx.g("EndNodeid"));
                doc.s("NodeName", "");
                doc.setTableName("BPG_ModSequenceFlowList");
            }
            else if (nodeType.equals("EndNode")) {
                //结束节点
                doc.s("NodeName", "结束");
                doc.s("ExtNodeType", "endEvent");
                doc.s("Terminate", "1");
                doc.s("EndBusinessName", "已结束");
                doc.s("EndBusinessid", "1");
                doc.setTableName("BPG_ModEventList");
            }
            else if (nodeType.equals("StartNode")) {
                //开始节点
                doc.s("NodeName", "开始");
                doc.s("ExtNodeType", "startEvent");
                doc.setTableName("BPG_ModEventList");
            }
            else if (nodeType.equals("Event")) {
                //事件节点
                doc.s("NodeName", "");
                doc.setTableName("BPG_ModEventList");
            }
            //BeanCtx.setDebug();
            int i = doc.save();
        }
        BeanCtx.p(Tools.jmsg("ok", "ok"));
    }

    /**
     * 根据节点id获得节点所在数据库表名
     * 
     * @param nodeid
     * @return 数据库表名
     */
    public String getNodeTableName(String processid, String nodeid) {
        ModNode modNode = (ModNode) BeanCtx.getBean("ModNode");
        String nodeType = modNode.getNodeType(processid, nodeid);
        if (Tools.isBlank(nodeType)) {
            return "";
        }
        return "BPG_Mod" + nodeType + "List";
    }

}