package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.wf.InsNode;
import cn.linkey.wf.ModNode;
import cn.linkey.wf.ProcessEngine;

/**
 * 具有自动推进功能 本规则负责结束路由线段 路由线段结束后会自动推进到路由线后面的节点
 * 
 * @author Administrator
 *
 */
public class R_S003_B018 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        String processid = linkeywf.getProcessid();
        String docUnid = linkeywf.getDocUnid();
        String runNodeid = (String) params.get("WF_RunNodeid"); //获得要运行的节点id

        //1.首先结束本路由线节点
        InsNode insNode = (InsNode) BeanCtx.getBean("InsNode");
        insNode.endNode(processid, docUnid, runNodeid);

        //2.本路由结束时是否强制结束本环节或者其他结束其他节点
        ModNode modNode = (ModNode) BeanCtx.getBean("ModNode");
        Document routerNodeDoc = modNode.getNodeDoc(processid, runNodeid);
        if (routerNodeDoc.g("EndCurrentNode").equals("1")) {
            //需要强制结束当前环节
            insNode.endNode(processid, docUnid, linkeywf.getCurrentNodeid());
        }

        //3.强制结束其他环节
        String endOtherNode = routerNodeDoc.g("EndOtherNode");
        if (Tools.isNotBlank(endOtherNode)) {
            //需要强制结束其他节点
            String[] nodeArray = Tools.split(endOtherNode);
            for (String nodeid : nodeArray) {
                insNode.endNode(processid, docUnid, nodeid);
            }
        }

        //4.本路由结束，继续推进到本路由的后继节点
        linkeywf.goToNextNode(runNodeid, params);

        return "";
    }
}
