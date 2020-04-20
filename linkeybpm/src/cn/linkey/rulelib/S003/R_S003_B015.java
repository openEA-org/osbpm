package cn.linkey.rulelib.S003;

import java.util.HashMap;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.wf.ProcessEngine;
import cn.linkey.wf.InsNode;

/**
 * 启动后置事件
 * 
 * @author Administrator
 *
 */
public class R_S003_B015 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        String processid = linkeywf.getProcessid();
        String docUnid = linkeywf.getDocUnid();
        String runNodeid = (String) params.get("WF_RunNodeid"); //获得要运行的本后置节点的nodeid

        //1.首先启动本节点
        InsNode insNode = (InsNode) BeanCtx.getBean("InsNode");
        insNode.startNode(processid, docUnid, runNodeid);

        //2.抛出本节点状态变化事件，如果有事件返回0则表示中止并把数据驻留在本节点上，否则结束上一Task节点并推进到本后置事件的路由线上去
        String eventResult = BeanCtx.getEventEngine().run(processid, runNodeid, "RearEventAfterStarted", params);
        //		BeanCtx.out(runNodeid+"配置了RearEventAfterStarted事件,执行结果为="+eventResult);
        if (eventResult.equals("0")) {
            //说明事件阻止后置环节的结束，存中间驻留数据到数据库中
            linkeywf.saveStayData(processid, runNodeid, docUnid, params);
        }
        else {
            //事件运行通过,结束本环节,只需要运行后置节点所对应的结束规则即可
            linkeywf.runNode(processid, runNodeid, "EndRuleNum", params);
        }

        return "";
    }
}
