package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
import cn.linkey.wf.InsNode;
import cn.linkey.wf.ProcessEngine;
import cn.linkey.wf.Remark;

/**
 * 结束当前环节并自动提交到下一节点
 * 
 * <pre>
 * Action=GoToNextNode 
 * 当提交的Actionid参数为GoToNextNode时，表示由系统自动定位用户当前所在的节点id
 * 系统将强制结束当前节点并自动寻找本节点的后继路由线，计算路由条件找到符合条件的节点进行启动.
 * </pre>
 * 
 * 
 * @author Administrator
 */
public class R_S003_B080 implements LinkeyRule {
    /**
     * GoTo->GoToNextNode 动作
     */
    @Override
    public String run(HashMap<String, Object> params) throws Exception {

        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        String processid = linkeywf.getProcessid();
        String currentNodeid=(String)params.get("WF_CurrentNodeid"); //指定当前节点id
//        BeanCtx.out("currentNodeid="+currentNodeid);
        if(Tools.isBlank(currentNodeid)){
        	currentNodeid = linkeywf.getCurrentNodeid();
        }
        String runNodeid=currentNodeid;
//        BeanCtx.out("runNodeid="+runNodeid);
        String docUnid=linkeywf.getDocUnid();
        
        //1.强制结束当前环节,不管当前环节是否会签，多实例，串行会签以及后置结束节点等，直接忽略掉，强制结束
        InsNode insNode = (InsNode) BeanCtx.getBean("InsNode");
        insNode.endNode(processid, docUnid, runNodeid);

        //2.推进到后继路由线
        linkeywf.goToNextNode(runNodeid, params);
        
        //4.增加流转记录
        Remark remark = (Remark) BeanCtx.getBean("Remark");
        remark.AddRemark("GoToNextNode", (String) params.get("WF_Remark"), "1");

        linkeywf.setRunStatus(true);//表示运行成功
        return linkeywf.getRunMsg(); //返回提示信息
    }

}
