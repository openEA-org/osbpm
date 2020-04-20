package cn.linkey.rulelib.S003;

import java.util.HashMap;
import cn.linkey.dao.Rdb;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.wf.ProcessEngine;
import cn.linkey.wf.InsUser;
import cn.linkey.wf.Remark;

/**
 * 本规则为实现提交下一会签用户 WF_RunActionid=GoToNextParallelUser
 * 
 * 提交下一会签用户时环节的状态是不用变化的，只需要结束当前用户的处理权限即可，后面的会签者同样已经具有处理权限
 * 
 * 参数说明:调用本规则时无需传入参数，因为下一会签用户是自动计算出来的
 * 
 * @author Administrator
 */
public class R_S003_B043 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        String processid = linkeywf.getProcessid();
        String docUnid = linkeywf.getDocUnid();

        //1.结束当前用户的处理权限
        InsUser insUser = (InsUser) BeanCtx.getBean("InsUser");
        insUser.endUser(processid, docUnid, linkeywf.getCurrentNodeid(), BeanCtx.getUserid());

        //4.获得当前环节还在活动的用户,提示语并返回
        String sql = "select Userid from BPM_InsUserList where DocUnid='" + docUnid + "' and Status='Current' and Nodeid='" + linkeywf.getCurrentNodeid() + "'";
        String curUserid = Rdb.getValueBySql(sql);
        String returnMsg = BeanCtx.getMsg("Engine", "GoToNextParallelUser", BeanCtx.getLinkeyUser().getCnName(curUserid));

        //5.增加流转记录
        Remark remark = (Remark) BeanCtx.getBean("Remark");
        remark.AddRemark("GoToNextParallelUser", (String) params.get("WF_Remark"), "1");

        linkeywf.setRunStatus(true);//表示运行成功
        return returnMsg;
    }
}
