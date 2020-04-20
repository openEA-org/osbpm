package cn.linkey.rulelib.S003;

import java.util.HashMap;

import cn.linkey.factory.BeanCtx;
import cn.linkey.form.ApprovalForm;
import cn.linkey.rule.LinkeyRule;

/**
 * 本类为单例类 本类已取消
 * 
 * 流程处理单生成类，主要负责自动生成流程处理单
 * 
 * @author Administrator
 */
public class R_S003_B033 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> nodeParams) throws Exception {
        ApprovalForm approvalForm = (ApprovalForm) BeanCtx.getBean("ApprovalForm");
        return approvalForm.getEngineApprovalForm(nodeParams);
    }
}
