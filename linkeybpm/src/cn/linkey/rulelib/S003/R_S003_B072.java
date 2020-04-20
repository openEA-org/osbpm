package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.factory.BeanCtx;
import cn.linkey.org.LinkeyUser;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.wf.InsUser;
import cn.linkey.wf.ProcessEngine;
import cn.linkey.wf.Remark;

/**
 * @RuleName:强制启动用户任务 参数说明: 1.需要在Params中传入WF_NextNodeid节点Nodeid参数只能传一个 3.需要在Params中传入WF_NextUserList作为目的节点要启动的用户列表 4.需要在Params中传入WF_Remark作为审批意见
 * @author admin
 * @version: 8.0
 * @Created: 2014-11-22 22:07
 */
final public class R_S003_B072 implements LinkeyRule {
    @Override
    @SuppressWarnings("unchecked")
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        String processid = linkeywf.getProcessid();
        String docUnid = linkeywf.getDocUnid();

        //2.启动要强制启动的用户,启动用户任务时如果节点没有启动，系统会自动启动节点
        InsUser insUser = (InsUser) BeanCtx.getBean("InsUser");
        String nodeid = (String) params.get("WF_NextNodeid"); //获得要启动的节点id，只能传一个节点id
        String userList = ((HashMap<String, String>) params.get("WF_NextUserList")).get("ALL"); //获得要启动的用户列表，多个用逗号分隔

        //用户与部门的组合字符串map对像
        HashMap<String, String> deptSet = ((HashMap<String, String>) params.get("WF_NextUserDept"));

        insUser.startUser(processid, docUnid, nodeid, userList, deptSet); //启动所有用户

        //4.获得提示语并返回
        LinkeyUser linkeyUser = (LinkeyUser) BeanCtx.getBean("LinkeyUser");
        String returnMsg = BeanCtx.getMsg("Engine", "StartUser", linkeyUser.getCnName(userList));

        //5.增加流转记录
        Remark remark = (Remark) BeanCtx.getBean("Remark");
        remark.AddRemark("StartUser", (String) params.get("WF_Remark"), "1");

        linkeywf.setRunStatus(true);//表示运行成功
        return returnMsg;
    }
}