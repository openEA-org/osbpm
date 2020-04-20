package cn.linkey.rulelib.S003;

import java.util.HashMap;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.wf.ProcessEngine;
import cn.linkey.wf.ModNode;
import cn.linkey.wf.Remark;

/**
 * 本规则为回退给回退者 WF_RunActionid=BackToReturnUser
 * 
 * 根据SourceOrUnid找到回退者的userid,nodeid然后进行启动即可
 * 
 * @author Administrator 参数说明:无需参数
 */
public class R_S003_B041 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {

        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        String processid = linkeywf.getProcessid();
        String runNodeid = BeanCtx.getLinkeywf().getCurrentNodeid();
        ModNode insModNode = (ModNode) BeanCtx.getBean("ModNode");

        //1.看本环节是否有后置事件节点来决定本环节结束后在什么位置停止推进
        String rearEventNodeid = insModNode.getRearEventNodeid(processid, linkeywf.getCurrentNodeid());
        if (Tools.isNotBlank(rearEventNodeid)) {
            params.put("WF_StopNodeType", "rearEvent");//说明有后置事件节点,运行到后置事件节点时停止推进
        }
        else {
            params.put("WF_StopNodeType", "userTask");//没有配置后置事件,直接运行userTask类型的结束规则就停止推进
        }

        //2.结束本环节，运行本环节的结束规则即可
        linkeywf.runNode(processid, runNodeid, "EndRuleNum", params);

        //3.找到本用户所在环节的上一环节的nodeid
        String sql = "select Nodeid,Userid,SourceOrUnid from BPM_InsUserList where WF_OrUnid='" + linkeywf.getCurrentInsUserDoc().g("SourceOrUnid") + "'";
        Document userdoc = Rdb.getDocumentBySql(sql);
        String targetNodeid = userdoc.g("Nodeid");

        //找到回退给本环节的用户id
        HashMap<String, String> nextUserMap = new HashMap<String, String>();
        nextUserMap.put(targetNodeid, userdoc.g("Userid"));
        params.put("WF_UsePostOwner", "1"); //设定强制使用WF_NextUserList为启动者标记
        params.put("WF_NextUserList", nextUserMap); //设定WF_NextUserList参数

        //返回给回退者时，是否需要进行前置事件节点的检测，还有待考虑，暂时使用检测功能
        String frontNodeid = insModNode.getFrontEventNodeid(processid, targetNodeid);
        if (Tools.isBlank(frontNodeid)) {
            //没有配置前置事件节点,直接启动目标环节即可
            linkeywf.runNode(processid, targetNodeid, "StartRuleNum", params);
        }
        else {
            //有配置前置事件节点,运行前置事件节点即可,前置事件节点会自动推进入到目标节点
            linkeywf.runNode(processid, frontNodeid, "StartRuleNum", params);
        }

        //5.增加流转记录
        Remark remark = (Remark) BeanCtx.getBean("Remark");
        remark.AddRemark("BackToReturnUser", (String) params.get("WF_Remark"), "1");
        linkeywf.setRunStatus(true);//表示运行成功
        return linkeywf.getRunMsg(); //返回提示信息
    }

}
