package cn.linkey.rulelib.S017;

import java.util.*;

import cn.linkey.dao.*;
import cn.linkey.util.*;
import cn.linkey.doc.*;
import cn.linkey.factory.*;
import cn.linkey.form.ApprovalForm;
import cn.linkey.wf.*;
import cn.linkey.rest.RestUtil;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.org.LinkeyUser;
/**
 * @RuleName:Rest_Engine生成流程处理单
 * @author  admin
 * @version: 1.0
 */
final public class R_S017_B134 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
	    String processid=BeanCtx.g("processId");
	    String docUnid=BeanCtx.g("docUnid");
        if(Tools.isBlank(processid)){return RestUtil.formartResultJson("0", "processId不能为空");}
        if(Tools.isBlank(docUnid)){return RestUtil.formartResultJson("0", "docUnid不能为空");}
        

		//初始化引擎
		ProcessEngine linkeywf=BeanCtx.getDefaultEngine();
		if(Tools.isBlank(docUnid)){docUnid=Rdb.getNewUnid();} //如果没有传入文档unid则说明要启动一个新文档
		linkeywf.init(processid,docUnid,BeanCtx.getUserid(),""); //初始化工作流引擎
		for(String keyName:params.keySet()){
			linkeywf.getDocument().s(keyName, (String)params.get(keyName));
		}
		
		
		//输出当前节点组合好的处理单HTML代码
		String htmlCode="";
		if(Tools.isNotBlank(linkeywf.getCurrentNodeid())){
			HashMap<String,Object> nodeParams=new HashMap<String,Object>();
			ApprovalForm approvalForm=(ApprovalForm)BeanCtx.getBean("ApprovalForm");
			htmlCode=approvalForm.getWsApprovalForm(nodeParams);
		}
		
		String jsonStr=RestUtil.formartResultJson("1","", "{\"htmlCode\":\""+RestUtil.encodeJson(htmlCode)+"\"}");
		
		return jsonStr;
	}
}