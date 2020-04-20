package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.org.LinkeyUser;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.wf.InsUser;
import cn.linkey.wf.ProcessEngine;
import cn.linkey.wf.Remark;

/**
 * 本规则为实现返回给转交者 WF_Action=BackToDeliver
 * 
 * 返回转交者时，环节的状态是不用变化的，只需要结束当前用户的处理权限然后再启动新的用户即可
 * 
 * 参数说明:无需传入参数
 * 
 * @author Administrator
 */
public class R_S003_B039 implements LinkeyRule {

    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        String processid = linkeywf.getProcessid();
        String docUnid = linkeywf.getDocUnid();

        //1.首先结束当前用户的处理权限
        InsUser insUser = (InsUser) BeanCtx.getBean("InsUser");
        insUser.endUser(processid, docUnid, linkeywf.getCurrentNodeid(), BeanCtx.getUserid());

        //2.启动要转交者,获得原转交者的文档copy一份并把状态改为current即可
        String sql = "select Userid,SourceOrUnid,ReassignmentOrUnid,Deptid from BPM_InsUserList where WF_OrUnid='" + linkeywf.getCurrentInsUserDoc().g("ReassignmentOrUnid") + "'";
        Document oldUserDoc = Rdb.getDocumentBySql(sql); //原转交者的旧文档对像

        //3修改几个变量用来记录旧的文档信息
        HashMap<String, String> deptSet = new HashMap<String, String>();
        deptSet.put(oldUserDoc.g("Userid"), oldUserDoc.g("Deptid")); //用户与部门id的组合map

        Document newUserDoc = insUser.startUser(processid, docUnid, linkeywf.getCurrentNodeid(), oldUserDoc.g("Userid"), deptSet); //启动转交者
        newUserDoc.s("SourceOrUnid", oldUserDoc.g("SourceOrUnid"));
        newUserDoc.s("ReassignmentOrUnid", oldUserDoc.g("ReassignmentOrUnid"));
        newUserDoc.save();

        //4.获得提示语并返回
        LinkeyUser linkeyUser = (LinkeyUser) BeanCtx.getBean("LinkeyUser");
        String returnMsg = BeanCtx.getMsg("Engine", "ReturnToPrevUser", linkeyUser.getCnName(oldUserDoc.g("Userid")));

        //5.增加流转记录
        Remark remark = (Remark) BeanCtx.getBean("Remark");
        remark.AddRemark("ReturnToPrevUser", (String) params.get("WF_Remark"), "1");

        linkeywf.setRunStatus(true);//表示运行成功
        return returnMsg;
    }
}
