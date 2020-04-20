package cn.linkey.rulelib.S003;

import java.util.HashMap;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.wf.ProcessEngine;
import cn.linkey.wf.InsUser;
import cn.linkey.wf.Remark;

/**
 * 本规则为实现提交下一串行审批用户 WF_RunActionid=GoToNextSerialUser
 * 
 * 提交下一串行审批用户时环节的状态是不用变化的，只需要结束当前用户的处理权限， 然后启动后面排队的第一个用户的任务即可 参数说明:无需传入参数
 * 
 * @author Administrator
 */
public class R_S003_B044 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        String processid = linkeywf.getProcessid();
        String docUnid = linkeywf.getDocUnid();

        //1.结束当前用户的处理权限
        InsUser insUser = (InsUser) BeanCtx.getBean("InsUser");
        insUser.endUser(processid, docUnid, linkeywf.getCurrentNodeid(), BeanCtx.getUserid());

        //2找到后面排队的用户进行启动
        String userid = "";
        String sql = "select Nodeid,Userid,SerialIndexNum,Deptid from BPM_InsUserList where DocUnid='" + docUnid + "' and Nodeid='" + linkeywf.getCurrentNodeid()
                + "' and Status='Wait' order by SerialIndexNum";
        Document[] dc = Rdb.getAllDocumentsBySql("BPM_InsUserList", sql);
        if (dc.length > 0) {
            Document insUserDoc = dc[0]; //获得第一个用户的文档并启动

            HashMap<String, String> deptSet = new HashMap<String, String>();
            deptSet.put(insUserDoc.g("Userid"), insUserDoc.g("Deptid")); //用户与部门的组合map对像

            Document newInsUserDoc = insUser.startUser(processid, docUnid, insUserDoc.g("Nodeid"), insUserDoc.g("Userid"), deptSet); //启动后继串行的Wait用户
            newInsUserDoc.s("SerialIndexNum", insUserDoc.g("SerialIndexNum"));
            newInsUserDoc.save();
            userid = newInsUserDoc.g("Userid");
        }

        //3.获得当前环节还在活动的用户,提示语并返回
        String returnMsg = BeanCtx.getMsg("Engine", "GoToNextSerialUser", BeanCtx.getLinkeyUser().getCnName(userid));

        //5.增加流转记录
        Remark remark = (Remark) BeanCtx.getBean("Remark");
        remark.AddRemark("GoToNextSerialUser", (String) params.get("WF_Remark"), "1");

        linkeywf.setRunStatus(true);//表示运行成功
        return returnMsg;
    }
}
