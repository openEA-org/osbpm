package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.wf.ProcessEngine;
import cn.linkey.wf.Remark;

/**
 * 引擎自动运行
 * 
 * <pre>
 * Action=AutoRun 
 * 当提交的Actionid参数为AutoRun时，表示由系统自动定位用户当前所在的节点id
 * 并自动进行处理包含(提交下一环节，返回转交者，返回回退者，提交下一会签用户，提交下一串行用户) 
 * 不需要指定Actionid参数，只需要指明用户即可，如果用户也不指定则自动从环节中进行计算
 * </pre>
 * 
 * 参数说明:无需参数
 * 
 * @author Administrator
 */
public class R_S003_B051 implements LinkeyRule {
    /**
     * GoTo->AutoRun 动作
     */
    @Override
    public String run(HashMap<String, Object> params) throws Exception {

        //long ts = System.currentTimeMillis();
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        String processid = linkeywf.getProcessid();
        String runNodeid = linkeywf.getCurrentNodeid();

        linkeywf.runNode(processid, runNodeid, "EndRuleNum", params); //运行userTask的结束规则R_S003_B020

        //4.增加流转记录
        Remark remark = (Remark) BeanCtx.getBean("Remark");
        remark.AddRemark("AutoRun", (String) params.get("WF_Remark"), "1");

        linkeywf.setRunStatus(true);//表示运行成功
        return linkeywf.getRunMsg(); //返回提示信息
    }

}
