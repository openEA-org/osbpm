package cn.linkey.rulelib.S003;

import java.util.HashMap;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.wf.InsNode;
import cn.linkey.wf.ModNode;
import cn.linkey.doc.Document;

/**
 * 本规则负责启动和结束节点
 * 
 * @author Administrator
 *
 */
public class R_S003_B025 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {

        String processid = BeanCtx.getLinkeywf().getProcessid();
        String runNodeid = (String) params.get("WF_RunNodeid"); //获得要运行的节点id
        Document nodeDoc = ((ModNode) BeanCtx.getBean("ModNode")).getNodeDoc(processid, runNodeid);

        if (nodeDoc.g("Terminate").equals("1")) {
            //说明启动了结束环节且环节要求结束整个流程
            BeanCtx.getLinkeywf().setEndNodeid(runNodeid); //设置结束环节的节点id
        }

        //直接结束本环节，不经过启动->结束过程了
        ((InsNode) BeanCtx.getBean("InsNode")).endNode(processid, BeanCtx.getLinkeywf().getDocUnid(), runNodeid);

        return "";
    }
}
