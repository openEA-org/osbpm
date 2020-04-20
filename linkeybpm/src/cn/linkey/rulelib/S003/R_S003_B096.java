package cn.linkey.rulelib.S003;

import java.util.*;
import cn.linkey.doc.*;
import cn.linkey.factory.*;
import cn.linkey.wf.*;
import cn.linkey.rule.LinkeyRule;
/**
 * @RuleName:流程撤销
 * @author  Mr.Yun
 * @version: 8.0
 * @Created: 2019-01-15 17:10:15
 */
final public class R_S003_B096 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        
        // 1.准备归档时的撤销参数
        Document doc = linkeywf.getDocument();
        doc.s("WF_EndBusinessName", "已撤销");
        doc.s("WF_EndBusinessid", "2");
        
        // 2.增加撤销记录
        Remark remark = (Remark) BeanCtx.getBean("Remark");
        remark.AddRemark((String) params.get("WF_RunActionid"), (String) params.get("WF_Remark"), "1");
        
        // 3.设置结束环节的节点id，这样就会linkeywf.run()方法会自动运行归档程序进行归档
        BeanCtx.getLinkeywf().setEndNodeid("GoToArchived"); 
        String returnMsg = "流程已成功撤销";
        linkeywf.setRunStatus(true);//表示运行成功
        
        return returnMsg;
	}
}