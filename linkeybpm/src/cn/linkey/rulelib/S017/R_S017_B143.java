package cn.linkey.rulelib.S017;

import java.util.*;

import cn.linkey.dao.*;
import cn.linkey.util.*;
import cn.linkey.doc.*;
import cn.linkey.factory.*;
import cn.linkey.form.ApprovalForm;
import cn.linkey.form.ModForm;
import cn.linkey.wf.*;
import cn.linkey.rest.RestUtil;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.org.LinkeyUser;
/**
 * @RuleName:Rest_Form获取流程处理表单部分的HTML
 * @author  admin
 * @version: 8.0
 * @Created: 2014-07-10 21:52
 */
final public class R_S017_B143 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
        String processid =BeanCtx.g("WF_Processid"); //流程id
        String docUnid = BeanCtx.g("WF_DocUnid"); //实例id

        
        if(Tools.isBlank(processid)){return Tools.jmsg("0", "processid不能为空,必须在post中传入WF_Processid");}
        if(Tools.isBlank(docUnid)){return Tools.jmsg("0", "docUnid不能为空,必须在post中传入WF_DocUnid");}

		//初始化引擎
		ProcessEngine linkeywf=BeanCtx.getDefaultEngine();
		if(Tools.isBlank(docUnid)){docUnid=Rdb.getNewUnid();} //如果没有传入文档unid则说明要启动一个新文档
		linkeywf.init(processid,docUnid,BeanCtx.getUserid(),""); //初始化工作流引擎
		linkeywf.getDocument().appendFromRequest();

		if(Tools.isNotBlank(linkeywf.getCurrentNodeid())){
			String htmlBody = getApprovalFormHtml(processid);
			String jsonStr=RestUtil.formartResultJson("1","", "{\"htmlCode\":\""+RestUtil.encodeJson(htmlBody)+"\"}");
			return jsonStr;
		}else{
			String jsonStr=RestUtil.formartResultJson("0","您没有处理权限!","");
			return jsonStr;
		}
	}
	
	public String getApprovalFormHtml(String processid) throws Exception {
			
			Document formDoc=BeanCtx.getLinkeywf().getFormDoc();

			HashMap<String,Object> params=new HashMap<String,Object>();
	        StringBuilder formBody = new StringBuilder(10000);
	        params = new HashMap<String, Object>();
	        params.put("FormDoc", formDoc);
	        params.put("FormBody", formBody);
	        ApprovalForm approvalForm = (ApprovalForm) BeanCtx.getBean("ApprovalForm");
	        formBody.append(approvalForm.getEngineApprovalForm(params));
	        
	        formBody.trimToSize();
	        return formBody.toString();
	    }
	
}