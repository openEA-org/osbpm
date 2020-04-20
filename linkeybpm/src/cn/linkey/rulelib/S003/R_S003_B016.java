package cn.linkey.rulelib.S003;

import java.util.HashMap;
import java.util.HashSet;

import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.wf.InsNode;
import cn.linkey.wf.ModNode;
import cn.linkey.wf.ProcessEngine;

/**
 * 具有自动推进功能
 * 
 * 结束后置事件，后置事件结束时要连带结束所有进入到本后置节点的userTask节点
 * 
 * @author Administrator
 *
 */
public class R_S003_B016 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        String docUnid = linkeywf.getDocUnid();
        String processid = linkeywf.getProcessid();
        String runNodeid = (String) params.get("WF_RunNodeid"); //获得要运行的节点id
        if (Tools.isBlank(runNodeid)) {
            BeanCtx.log("E", "未传入要运行的节点id=" + runNodeid);
            return "";
        }
        BeanCtx.out("R_S003_B016要结束的节点=" + runNodeid);

        //1.找到所有进入本节点的userTask节点并强制结束
        ModNode insModNode = (ModNode) BeanCtx.getBean("ModNode");
        HashSet<String> previousNodeSet = insModNode.getPreviousNodeid(processid, runNodeid);
        for (String taskNodeid : previousNodeSet) {

            BeanCtx.out("结束userTask节点=" + taskNodeid);

            //首先结束userTask节点
            InsNode insNode = (InsNode) BeanCtx.getBean("InsNode");
            insNode.endNode(processid, docUnid, taskNodeid);

            //再强制结束userTask后面的路由线,这里暂不进行路由线的结束操作，因为task在推向后置节点时路由线有可能已经执行过了

        }

        //2.结束当前后置事件环节
        InsNode insNode = (InsNode) BeanCtx.getBean("InsNode");
        insNode.endNode(processid, BeanCtx.getLinkeywf().getDocUnid(), runNodeid);

        //3.本环节结束，继续推进到本环节的后继节点
        String nodeType = (String) params.get("WF_StopNodeType"); //检测是否需要停止推进
        if (nodeType == null || !nodeType.equals("rearEvent")) {
            linkeywf.goToNextNode(runNodeid, params);
        }
        return "";

    }
}
