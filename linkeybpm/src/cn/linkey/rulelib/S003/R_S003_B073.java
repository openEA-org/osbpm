package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.factory.BeanCtx;
import cn.linkey.org.LinkeyUser;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.wf.InsUser;
import cn.linkey.wf.ProcessEngine;
import cn.linkey.wf.Remark;

/**
 * @RuleName:强制结束用户任务
 * @author admin
 * @version: 8.0
 * @Created: 2014-11-23 10:07 参数说明:需要传入WF_NextNodeid要结束的节点,WF_NextUserList要结束的用户,WF_Remark办理意见
 */
final public class R_S003_B073 implements LinkeyRule {
    @Override
    @SuppressWarnings("unchecked")
    public String run(HashMap<String, Object> params) throws Exception {
        //params为运行本规则时所传入的参数
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        String processid = linkeywf.getProcessid();
        String docUnid = linkeywf.getDocUnid();

        //2.结束用户的审批权限
        InsUser insUser = (InsUser) BeanCtx.getBean("InsUser");
        String userList = ((HashMap<String, String>) params.get("WF_NextUserList")).get("ALL"); //获得要结束的用户列表
        String nodeid = (String) params.get("WF_NextNodeid"); //获得要结束的节点id

        insUser.endUser(processid, docUnid, nodeid, userList); //结束所有用户

        //4.获得提示语并返回
        LinkeyUser linkeyUser = (LinkeyUser) BeanCtx.getBean("LinkeyUser");
        String returnMsg = BeanCtx.getMsg("Engine", "EndUser", linkeyUser.getCnName(userList));

        //5.增加流转记录
        Remark remark = (Remark) BeanCtx.getBean("Remark");
        remark.AddRemark("EndUser", (String) params.get("WF_Remark"), "1");

        linkeywf.setRunStatus(true);//表示运行成功
        return returnMsg;
    }
}