package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.wf.ProcessEngine;
import cn.linkey.wf.InsUser;
import cn.linkey.wf.ModNode;
import cn.linkey.wf.Remark;

/**
 * 回退首环节,运行逻辑如下： 
 * 1.检测本节点是否有后置事件节点，如果有则运行本节点的后置路由线并由路由线推进到后置节点后停止，如果没有则直接结束本环节 
 * 2.不管本环节能不能结束当前用户的任务是需要结束的，并且也需要强制启动首环节 
 * 3.得到首环节的Nodeid，然后直接启动首节点和流程启动者的任务
 * 4.参数说明:回退首环节时无需在WF_NextUserList中传入用户参数，也无需在WF_NextNodeid中传入节点id,全部由系统自动在后台分析获得
 * 
 * @author Administrator
 *
 */
public class R_S003_B004 implements LinkeyRule {
    @Override
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

        //3.得到首环节的nodeid并进行启动
        String firstNodeid = insModNode.getStartNodeid(processid); //获得首环节的nodeid
        String firstUserid = linkeywf.getDocument().g("WF_AddName");//获得流程的启动者

        //获得首环节用户的相关变量信息
        InsUser insUser = (InsUser) BeanCtx.getBean("InsUser");
        Document firstInsUserDoc = insUser.getInsUserDoc(processid, firstNodeid, docUnid, firstUserid, "End");//获得首环节用户的实例文档对像

        //启动首环节用户
        HashMap<String, String> userDept = new HashMap<String, String>();
        userDept.put(firstUserid, firstInsUserDoc.g("Deptid"));
        Document newUserDoc = insUser.startUser(processid, docUnid, firstNodeid, firstUserid, userDept); //启动本流程的启动者作为首环节的参与者
        String backFlag = (String) params.get("WF_IsBackFlag");//取从参数中传入进来的回退标记，如果为2表示回退后需要直接返回给回退者,不能从文档中取需要规范从params中取
        if (Tools.isBlank(backFlag)) {
            backFlag = "1";
        }
        newUserDoc.s("IsBackFlag", backFlag); //标记为回退
        newUserDoc.save();

        BeanCtx.getEventEngine().run(processid, runNodeid, "DocumentAfterReturned", params); //文档被退回后

        //5.增加流转记录
        Remark remark = (Remark) BeanCtx.getBean("Remark");
        remark.AddRemark((String) params.get("WF_RunActionid"), (String) params.get("WF_Remark"), "1");

        //6.获得提示消息
        String returnMsg = BeanCtx.getMsg("Engine", "GoToFirstNode", linkeywf.getDocument().g("WF_AddName_CN"));
        linkeywf.setRunStatus(true);//表示运行成功
        return returnMsg;
    }
}
