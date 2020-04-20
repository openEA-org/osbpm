package cn.linkey.rulelib.S003;

import java.util.HashMap;
import java.util.HashSet;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.wf.ProcessEngine;
import cn.linkey.wf.ModNode;
import cn.linkey.wf.Remark;

/**
 * 用户任务结束动作 WF_RunActionid=EndUserTask
 * 
 * 当用户能够点击办理完成执行EndUserTask时， 说明此用户已经可以结束本节点了，无需再进行串行
 * 返回转交者的检测 用户能不能点击办理完成按扭交由R_S003_B033处理单显示规则中去控制. 
 * 除非是全自动运行(处理单上只有一个办理完成按扭时)才需要检测。
 * 
 * 参数说明:需要传入WF_NextNodeid,WF_NextUserList参数
 * 
 * @author Administrator
 *
 */
public class R_S003_B002 implements LinkeyRule {
    /**
     * GoTo->EndUserTask 动作
     */
    @Override
    @SuppressWarnings("unchecked")
    public String run(HashMap<String, Object> params) throws Exception {

        //long ts = System.currentTimeMillis();
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        String processid = linkeywf.getProcessid();
        String runNodeid = linkeywf.getCurrentNodeid();
        ModNode modNode = (ModNode) BeanCtx.getBean("ModNode");

        //3.如果没有串行和返回转交的情况下就直接运行userTask类型的结束规则
        //if(runNodeFlag){
        //看是否有WF_NextNodeid参数传入，如果有则要看是否已经指定了目标Task节点的路径,如果已经指定了则获得与目标环节的后面第一条路由线段的集合id
        HashSet<String> nextNodeidSet = (HashSet<String>) params.get("WF_NextNodeid");
        if (nextNodeidSet != null && nextNodeidSet.size() != 0) {
            //计算与每个目标节点之间的第一条路由线段的id
            HashSet<String> routerPathSet = new HashSet<String>();
            for (String targetNodeid : nextNodeidSet) {
                routerPathSet.addAll(modNode.getBetweenNodeid(processid, runNodeid, targetNodeid, null)); //这个方法的算法还存在一点问题，复杂的情况下计算会有点问题
                if (linkeywf.isDebug()) {
                    BeanCtx.out("Debug:获得流程(" + processid + ")原节点(" + runNodeid + ")与目标节点(" + targetNodeid + ")之间的路径=" + routerPathSet.toString());
                }
            }
            params.put("WF_AllRouterPath", routerPathSet); //加入传入参数中,传入到Engine.goToNextNode()方法中可以指定要运行的路由线段
        }

        if (linkeywf.isDebug()) {
            BeanCtx.out("Debug:已知到所有目标节点的全部路径集合=" + params.get("WF_AllRouterPath"));
        }

        linkeywf.runNode(processid, runNodeid, "EndRuleNum", params); //运行userTask的结束规则R_S003_B020

        //4.增加流转记录
        Remark remark = (Remark) BeanCtx.getBean("Remark");
        remark.AddRemark("EndUserTask", (String) params.get("WF_Remark"), "1");

        linkeywf.setRunStatus(true);//表示运行成功
        return linkeywf.getRunMsg(); //返回提示信息
    }

}
