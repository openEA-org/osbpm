package cn.linkey.rulelib.S003;

import java.util.HashMap;
import java.util.HashSet;

import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.wf.ProcessEngine;
import cn.linkey.wf.ModNode;
import cn.linkey.wf.Remark;

/**
 * 本规则为跳转到任意环节 WF_RunActionid=GoToAnyNode
 * 
 * 参数说明:需要传入WF_NextNodeid节点id,WF_NextUserList用户id,WF_Remark办理意见
 * 
 * @author Administrator
 */
public class R_S003_B003 implements LinkeyRule {
    /**
     * GoTo->GoToAnyNode 动作
     */
    @SuppressWarnings("unchecked")
    @Override
    public String run(HashMap<String, Object> params) throws Exception {

        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        String processid = linkeywf.getProcessid();
        String runNodeid = BeanCtx.getLinkeywf().getCurrentNodeid();
        ModNode insModNode = (ModNode) BeanCtx.getBean("ModNode");

        //1.看本环节是否有后置事件节点来决本环节结束后在什么位置停止推进
        String rearEventNodeid = insModNode.getRearEventNodeid(processid, linkeywf.getCurrentNodeid());
        if (Tools.isNotBlank(rearEventNodeid)) {
            params.put("WF_StopNodeType", "rearEvent");//说明有后置事件节点,运行到后置事件节点时停止推进
        }
        else {
            params.put("WF_StopNodeType", "userTask");//没有配置后置事件,直接运行userTask类型的结束规则就停止推进
        }

        //2.结束本环节，运行本环节的结束规则即可
        linkeywf.runNode(processid, runNodeid, "EndRuleNum", params);

        //3.启动指定的环节由WF_NextNodeid传入进来
        HashSet<String> nextNodeidSet = (HashSet<String>) params.get("WF_NextNodeid");
        if (nextNodeidSet == null) {
            BeanCtx.log("E", "未找到要跳转的Nodeid请确认是否传入了WF_NextNodeid参数!");
        }

        for (String targetNodeid : nextNodeidSet) {
            String frontNodeid = insModNode.getFrontEventNodeid(processid, targetNodeid);
            if (Tools.isBlank(frontNodeid)) {
                //没有配置前置事件节点,直接启动目标环节即可
                linkeywf.runNode(processid, targetNodeid, "StartRuleNum", params);
            }
            else {
                //有配置前置事件节点,运行前置事件节点即可,前置事件节点会自动推进入到目标节点
                linkeywf.runNode(processid, frontNodeid, "StartRuleNum", params);
            }
        }

        //4.增加流转记录
        Remark remark = (Remark) BeanCtx.getBean("Remark");
        remark.AddRemark("GoToAnyNode", (String) params.get("WF_Remark"), "1");
        linkeywf.setRunStatus(true);//表示运行成功
        return linkeywf.getRunMsg();
    }

}
