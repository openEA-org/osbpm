package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.wf.ProcessEngine;
import cn.linkey.wf.InsNode;

/**
 * 具有自动推进功能,但能根据事件执行结果进行中断 本规则主要负责启动前置事件节点 前置事件节点在通过检测规则后具有自动推进到下一节点的功能
 * 
 * @author Administrator
 *
 */
public class R_S003_B023 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        String processid = linkeywf.getProcessid();
        String runNodeid = (String) params.get("WF_RunNodeid"); //获得要运行的节点id,即前置事件环节的nodeid,如果为空表示没有画
        //BeanCtx.p("YES说明配置了前置节点="+runNodeid);

        //1.首先启动本节点
        InsNode insNode = (InsNode) BeanCtx.getBean("InsNode");
        insNode.startNode(processid, linkeywf.getDocUnid(), runNodeid);

        //2.抛出本节点状态变化事件，如果有事件返回0则表示中止并把数据驻留在本节点上，否则直接推进到userTask节点
        String eventResult = BeanCtx.getEventEngine().run(processid, runNodeid, "FrontEventAfterStarted", params);
        if (eventResult.equals("0")) {
            //说明事件阻止前置事件节点的启动,这时不能启动后继的路由线，把中间驻留数据到数据库中
            linkeywf.saveStayData(processid, runNodeid, linkeywf.getDocUnid(), params);
        }
        else {
            //事件运行通过,结束本环节,只需要运行后置节点所对应的结束规则即可
            linkeywf.runNode(processid, runNodeid, "EndRuleNum", params);
        }

        return "";
    }
}
