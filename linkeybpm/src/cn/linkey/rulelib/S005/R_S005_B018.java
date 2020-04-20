package cn.linkey.rulelib.S005;

import java.util.HashMap;
import java.util.LinkedHashSet;

import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.wf.ModNode;
import cn.linkey.wf.ProcessEngine;

/**
 * @RuleName:获得流程节点人员列表
 * @author admin
 * @version: 8.0
 * @Created: 2015-03-12 14:20
 */
final public class R_S005_B018 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        String curNodeid = BeanCtx.g("CurrentNodeid", true);
        String nextNodeid = BeanCtx.g("NextNodeid", true);
        String processid = BeanCtx.g("Processid", true);
        String docUnid = BeanCtx.g("DocUnid", true);

        //先初始化流程引擎
        ProcessEngine processEngine = BeanCtx.getDefaultEngine();
        processEngine.init(processid, docUnid, BeanCtx.getUserid(), "");

        //获得节点的人员列表
        ModNode insModNode = (ModNode) BeanCtx.getBean("ModNode");
        Document nextNodeDoc = insModNode.getNodeDoc(processid, nextNodeid);//目标节点文档对像
        String nodeType = nextNodeDoc.g("NodeType");//基本属性
        String nextExtNodeType = nextNodeDoc.g("ExtNodeType"); //扩展属性
        LinkedHashSet<String> ownerSet = new LinkedHashSet<String>();
        if (nextExtNodeType.equals("userTask")) {
            //如果目的节点是userTask类型则有人员
            LinkedHashSet nodeOwnerSet = Tools.splitAsLinkedSet(nextNodeDoc.g("ApprovalFormOwner"), ","); //使用节点中配置的人员选择范围
            ownerSet = BeanCtx.getLinkeyUser().parserNodeMembers(nodeOwnerSet); //解析节点中人员可能选择的各种规则
        }

        //返回选中的节点用户列表
        String jsonStr = "{\"UserList\":\"" + Tools.join(ownerSet, ",") + "\"}";
        BeanCtx.p(jsonStr);
        //	    BeanCtx.out(jsonStr);

        return "";
    }
}