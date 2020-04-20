package cn.linkey.rulelib.S017;

import java.util.HashMap;
import java.util.LinkedHashSet;

import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.wf.ModNode;

/**
 * @RuleName:获得可选的事务子流程
 * @author admin
 * @version: 8.0
 * @Created: 2015-12-03 17:25
 */
final public class R_S017_B037 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        //示例参数:{"Processid":"流程id","Nodeid":"节点id"}
        StringBuilder jsonStr = new StringBuilder();
        String processid = (String) params.get("Processid"); //获得流程id
        String nodeid = (String) params.get("Nodeid"); //节点id
        ModNode modNode = (ModNode) BeanCtx.getBean("ModNode");
        Document nodeDoc = modNode.getNodeDoc(processid, nodeid);
        if (!nodeDoc.isNull()) {
            String subProcessid = nodeDoc.g("SubProcessid");
            String[] pArray = Tools.split(subProcessid);
            for (String pid : pArray) {
                nodeDoc = modNode.getNodeDoc(pid, "Process");
                String startNodeid = modNode.getStartNodeid(pid);//获得流程的启动节点
                //获得启动节点的用户
                String nodePotentialOwner = modNode.getNodePotentialOwner(pid, startNodeid);
                LinkedHashSet<String> ownerSet = BeanCtx.getLinkeyUser().parserNodeMembers(Tools.splitAsLinkedSet(nodePotentialOwner, ","));
                String userList = Tools.join(ownerSet, ",");
                String itemStr = "{\"Processid\":\"" + pid + "\",\"ProcessName\":\"" + nodeDoc.g("NodeName") + "\",\"StartNodeid\":\"" + startNodeid + "\",\"NodeUser\":\"" + userList + "\"}";
                if (jsonStr.length() > 0) {
                    jsonStr.append(",");
                }
                jsonStr.append(itemStr);
            }
        }
        jsonStr.insert(0, "[").append("]");

        return jsonStr.toString();
    }
}