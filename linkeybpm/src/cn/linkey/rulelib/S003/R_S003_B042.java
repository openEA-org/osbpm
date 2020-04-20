package cn.linkey.rulelib.S003;

import java.util.HashMap;
import java.util.HashSet;

import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.wf.InsUser;
import cn.linkey.wf.ModNode;
import cn.linkey.wf.ProcessEngine;
import cn.linkey.wf.Remark;

/**
 * 回退任意环节,运行逻辑如下： 1.检测本节点是否有后置事件节点，如果有则运行本节点的后置路由线并由路由线推进到后置节点后停止，如果没有则直接结束本环节 2.不管本环节能不能结束当前用户的任务是需要结束的，并且也需要强制回退的环节 2.得到回退环节的Nodeid，然后直接启动节点和用户任务
 * 
 * 参数说明:需要传入WF_NextNodeid节点id,WF_NextUserList目标用户,WF_Remark办理意见,WF_IsBackFlag回退类型
 * 
 * @author Administrator
 *
 */
public class R_S003_B042 implements LinkeyRule {

    @Override
    @SuppressWarnings("unchecked")
    public String run(HashMap<String, Object> params) throws Exception {

        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        String processid = linkeywf.getProcessid();
        String docUnid = linkeywf.getDocUnid();
        String runNodeid = BeanCtx.getLinkeywf().getCurrentNodeid();
        ModNode insModNode = (ModNode) BeanCtx.getBean("ModNode");

        //1.看本环节是否有后置事件节点来决本环节结束后在什么位置停止推进
        String rearEventNodeid = insModNode.getRearEventNodeid(processid, linkeywf.getCurrentNodeid());
        if (Tools.isNotBlank(rearEventNodeid)) {
            params.put("WF_StopNodeType", "rearEvent"); //说明有后置事件节点,运行到后置事件节点时停止推进
        }
        else {
            params.put("WF_StopNodeType", "userTask"); //没有配置后置事件,直接运行userTask类型的结束规则就停止推进
        }

        //2.结束本环节，运行本环节的结束规则即可
        linkeywf.runNode(processid, runNodeid, "EndRuleNum", params);

        //3.得到回退环节的nodeid并进行启动
        HashSet<String> nextNodeidSet = (HashSet<String>) params.get("WF_NextNodeid");
        HashMap<String, String> nextUserMap = (HashMap<String, String>) params.get("WF_NextUserList");
        String returnNodeid = Tools.join(nextNodeidSet, ","); //获得回退环节的nodeid
        String returnUserid = nextUserMap.get(returnNodeid); //获得回退环节的用户
        HashMap<String, String> returnUserDept = BeanCtx.getDeptidByMulStr(returnUserid); //用户id与部门id的组合map
        returnUserid = BeanCtx.getUseridByMulStr(returnUserid);//重新分析出用户字符串

        InsUser insUser = (InsUser) BeanCtx.getBean("InsUser");
        String backFlag = (String) params.get("WF_IsBackFlag"); //从参数中取传入进来的回退标记，如果为2表示回退后需要直接返回给回退者
        if (Tools.isBlank(backFlag)) {
            backFlag = "1";
        }
        insUser.startUser(processid, docUnid, returnNodeid, returnUserid, returnUserDept, backFlag); //启动回退用户的任务

        BeanCtx.getEventEngine().run(processid, returnNodeid, "DocumentAfterReturned", params); //文档被退回后

        //5.增加流转记录
        Remark remark = (Remark) BeanCtx.getBean("Remark");
        remark.AddRemark((String) params.get("WF_RunActionid"), (String) params.get("WF_Remark"), "1");

        //6.获得提示消息
        String returnMsg = BeanCtx.getMsg("Engine", "ReturnToAnyNode", insModNode.getNodeName(processid, returnNodeid, false), BeanCtx.getLinkeyUser().getCnName(returnUserid));

        linkeywf.setRunStatus(true);//表示运行成功
        return returnMsg;
    }
}
