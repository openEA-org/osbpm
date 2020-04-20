package cn.linkey.rulelib.S003;

import java.util.HashMap;
import cn.linkey.dao.Rdb;
import cn.linkey.factory.BeanCtx;
import cn.linkey.org.LinkeyUser;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.wf.ProcessEngine;
import cn.linkey.wf.InsUser;
import cn.linkey.wf.Remark;

/**
 * 本规则为实现转他人处理功能 WF_RunActionid=GoToOthers
 * 
 * 转他人处理时，环节的状态是不用变化的，只需要结束当前用户的处理权限然后再启动新的用户即可 参数说明:需要传入WF_NextUserList参数,WF_ReassignmentFlag转交返回标识参数,WF_Remark办理意见
 * 
 * @author Administrator
 */
public class R_S003_B008 implements LinkeyRule {
    @SuppressWarnings({ "unchecked" })
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        String processid = linkeywf.getProcessid();
        String docUnid = linkeywf.getDocUnid();
        String ReassignmentFlag = (String) params.get("WF_ReassignmentFlag"); //转交时是否需要转交者返回的标记1表示不需要2表示需要
        if (Tools.isBlank(ReassignmentFlag)) {
            ReassignmentFlag = "1";
        }

        //1.首先结束当前用户的处理权限
        InsUser insUser = (InsUser) BeanCtx.getBean("InsUser");
        insUser.endUser(processid, docUnid, linkeywf.getCurrentNodeid(), BeanCtx.getUserid());

        //2.启动要转交的用户,只需要copy当前用户的实例文档即可
        String otherUserList = ((HashMap<String, String>) params.get("WF_NextUserList")).get("ALL"); //获得要转交的用户列表
        HashMap<String, String> deptSet = BeanCtx.getDeptidByMulStr(otherUserList);//转交时允许指定部门id
        otherUserList = BeanCtx.getUseridByMulStr(otherUserList);

        insUser.startUser(processid, docUnid, linkeywf.getCurrentNodeid(), otherUserList, deptSet); //启动所有转交的用户

        //3.修改刚转交的用户实例的一些参数,如果转交后要收回可以通过ReassignmentOrUnid得到转交记录
        //这样修改后转交的用户也可以进行回退上一环节操作，回退上一用户也会回退给原转交人的上一用户
        //修改SerialIndexNum后串行审批时也支持转交操作，实现插队的功能
        String sql = "update BPM_InsUserList set ReassignmentOrUnid='" + linkeywf.getCurrentInsUserDoc().g("WF_OrUnid") + "',ReassignmentFlag='" + ReassignmentFlag + "',SourceOrUnid='"
                + linkeywf.getCurrentInsUserDoc().g("SourceOrUnid") + "',SerialIndexNum='" + linkeywf.getCurrentInsUserDoc().g("SerialIndexNum") + "' where DocUnid='" + docUnid + "' and ActionNum='"
                + linkeywf.getActionNum() + "'";
        Rdb.execSql(sql);

        //4.获得提示语并返回
        LinkeyUser linkeyUser = (LinkeyUser) BeanCtx.getBean("LinkeyUser");
        String returnMsg = BeanCtx.getMsg("Engine", "GoToOthers", linkeyUser.getCnName(otherUserList));

        //5.增加流转记录
        Remark remark = (Remark) BeanCtx.getBean("Remark");
        remark.AddRemark("GoToOthers", (String) params.get("WF_Remark"), "1");

        linkeywf.setRunStatus(true);//表示运行成功
        return returnMsg;
    }
}
