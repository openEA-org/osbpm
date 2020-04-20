package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.wf.ProcessEngine;
import cn.linkey.wf.InsUser;
import cn.linkey.wf.Remark;

/**
 * 本规则把当前用户标记为已阅
 * 
 * 参数说明:无需传入参数
 * 
 * @author Administrator
 */
public class R_S003_B009 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {

        ProcessEngine linkeywf = BeanCtx.getLinkeywf();

        //1.把当前用户标记为已阅
        InsUser insUser = (InsUser) BeanCtx.getBean("InsUser");
        Document insUserDoc = insUser.endCopyUser(BeanCtx.getUserid());

        //2.增加已阅记录
        Remark remark = (Remark) BeanCtx.getBean("Remark");
        remark.AddReadRemark(insUserDoc.g("Nodeid"), insUserDoc.g("NodeName"), insUserDoc.g("StartTime"));

        linkeywf.setRunStatus(true);//表示运行成功
        return BeanCtx.getMsg("Engine", "EndCopyTo");
    }

}
