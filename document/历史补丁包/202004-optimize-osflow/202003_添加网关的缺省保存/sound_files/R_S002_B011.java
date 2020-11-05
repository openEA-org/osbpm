package cn.linkey.rulelib.S002;

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
final public class R_S002_B011 implements LinkeyRule {
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
        ModNode modNode = (ModNode) BeanCtx.getBean("ModNode");
        String nodeid = BeanCtx.g("Nodeid", true);
        String tableName = modNode.getNodeTableName(processid, nodeid);
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
            String sql = "select * from BPM_AllModNodeList where Processid='" + processid + "' and Nodeid='" + nodeid + "'";
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
        String sql = "select * from BPM_AllModNodeList where Processid='" + processid + "' and Nodeid='" + nodeid + "'";
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
                doc.setTableName("BPM_ModSequenceFlowList");
            }
            else if (nodeType.equals("EndNode")) {
                //结束节点
                doc.s("NodeName", "结束");
                doc.s("ExtNodeType", "endEvent");
                doc.s("Terminate", "1");
                doc.s("EndBusinessName", "已结束");
                doc.s("EndBusinessid", "1");
                doc.setTableName("BPM_ModEventList");
            }
            else if (nodeType.equals("StartNode")) {
                //开始节点
                doc.s("NodeName", "开始");
                doc.s("ExtNodeType", "startEvent");
                doc.setTableName("BPM_ModEventList");
            }
            else if (nodeType.equals("Event")) {
                //事件节点
                doc.s("NodeName", "");
                doc.setTableName("BPM_ModEventList");
            }
            
            // 202003 add by alibao ===================start
            // 添加缺省保存网关  
            else if(nodeType.equals("Edge")) {
            	doc.s("NodeName", "是否同意?");
                doc.s("ExtNodeType", "exclusiveGateway");
                doc.setTableName("BPM_ModGatewayList");
            }
           // 202003 add by alibao ====================end
            
            //BeanCtx.setDebug();
            int i = doc.save();
        }
        BeanCtx.p(Tools.jmsg("ok", "ok"));
    }

}