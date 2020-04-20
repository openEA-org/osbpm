package cn.linkey.rulelib.S030;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.wf.ModNode;

/**
 * 保存流程节点属性
 * 
 * @author Administrator
 *
 */
final public class R_S030_B004 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) {
        BeanCtx.setCtxData("WF_NoEncode", "1"); // 指定存盘时不进行编码操作

        ModNode modNode = (ModNode) BeanCtx.getBean("ModNode");
        String processid = BeanCtx.g("Processid", true);
        String nodeid = BeanCtx.g("Nodeid", true);
        String nodeType = modNode.getNodeType(processid, nodeid);
        if (nodeType.equals("Process")) {
            saveProcess(processid, nodeid, nodeType);
        }
        else {
            saveNode(processid, nodeid, nodeType);
        }
        return "";
    }

    /**
     * 存盘流程过程属性
     */
    public void saveProcess(String processid, String nodeid, String nodeType) {
        String nodeTableName = getNodeTableName(processid, nodeid); //节点所在数据库表
        String sql = "select * from " + nodeTableName + " where Processid='" + processid + "' and Nodeid='" + nodeid + "'";
        Document nodeDoc = Rdb.getDocumentBySql(sql);
        nodeDoc.appendFromRequest(BeanCtx.getRequest());
        int i = nodeDoc.save();
        if (i > 0) {
            BeanCtx.p(Tools.jmsg("ok", "保存成功"));
        }
        else {
            BeanCtx.p(Tools.jmsg("error", "保存失败"));
        }
        BeanCtx.userlog(processid, "修改流程", "修改流程(" + nodeDoc.g("NodeName") + ")");
    }

    /**
     * 存盘通用节点
     */
    public void saveNode(String processid, String nodeid, String nodeType) {
        String qryNodeType = BeanCtx.g("QryNodeType"); //url中指定的节点扩展类型
        String extNodeType = BeanCtx.g("ExtNodeType"); //扩展类型
        if (Tools.isBlank(extNodeType)) {
            extNodeType = qryNodeType;
        }
        String nodeTableName = getNodeTableName(processid, nodeid); //节点所在数据库表
        String sql = "select * from " + nodeTableName + " where Processid='" + processid + "' and Nodeid='" + nodeid + "'";
        Document nodeDoc = Rdb.getDocumentBySql(sql);
        nodeDoc.appendFromRequest(BeanCtx.getRequest());
        nodeDoc.s("ExtNodeType", extNodeType);
        nodeDoc.removeItem("QryNodeType");
        int i = nodeDoc.save();
        if (i > 0) {
            BeanCtx.p(Tools.jmsg("ok", "保存成功"));
        }
        else {
            BeanCtx.p(Tools.jmsg("error", "保存失败"));
        }
        BeanCtx.userlog(processid, "修改流程节点", "修改流程节点(" + nodeDoc.g("NodeName") + ")");
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
