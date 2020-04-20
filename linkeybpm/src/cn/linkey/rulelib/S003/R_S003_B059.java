package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.wf.InsNode;

/**
 * 结束子流程节点
 * 
 * @author Administrator
 *
 */
public class R_S003_B059 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        String processid = BeanCtx.getLinkeywf().getProcessid();
        String runNodeid = (String) params.get("WF_RunNodeid"); //获得要运行的节点id

        //1.看本节点是否还有活跃的子流程文档,如果还有就不能结束,这里注意节点判断当有多个子流程节点启动时需要区分
        String sql = "select WF_OrUnid from BPM_MainData where WF_MainDocUnid='" + BeanCtx.getLinkeywf().getDocUnid() + "' and WF_MainNodeid='" + runNodeid + "' and WF_Status='Current'";
        //说明还有子流程文档处于活动状态
        if (Rdb.hasRecord(sql)) {
            return "0";
        }

        //2.结束当前节点
        InsNode insNode = (InsNode) BeanCtx.getBean("InsNode");
        insNode.endNode(processid, BeanCtx.getLinkeywf().getDocUnid(), runNodeid);

        if (BeanCtx.getLinkeywf().isDebug()) {
            BeanCtx.out("成功结束子流程节点=" + runNodeid + ",准备运行到本节点的后继节点");
        }

        //3.本环节结束，继续推进到本节点的后继节点
        BeanCtx.getLinkeywf().goToNextNode(runNodeid, params);

        return "1";

    }
}
