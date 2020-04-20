package cn.linkey.rulelib.S017;

import java.util.*;

import cn.linkey.dao.*;
import cn.linkey.util.*;
import cn.linkey.doc.*;
import cn.linkey.factory.*;
import cn.linkey.wf.*;
import cn.linkey.rest.RestUtil;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.org.LinkeyUser;
/**
 * @RuleName:Rest_Engine_启动一个新流程
 * @author  admin
 * @version: 8.0
 * @Created: 2017-09-02 22:00:26
 */
final public class R_S017_B101 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
		String processid=BeanCtx.g("processId"); //从url中获取流程id
		String targetNodeid=BeanCtx.g("nextNodeId");
		String targetUser=BeanCtx.g("nextUserId");
		String targetCopyUser=BeanCtx.g("copyUserId");
		String remark=BeanCtx.g("remark");
		String formData=BeanCtx.g("formData"); //要修改的表单数据
		 
		if(Tools.isBlank(processid)){return RestUtil.formartResultJson("0", "processId不能为空");}
		if(Tools.isBlank(targetNodeid)){return RestUtil.formartResultJson("0", "nextNodeId不能为空");}

		
		Document doc=BeanCtx.getDocumentBean("BPM_MainData");
        if(Tools.isNotBlank(formData)){
        	doc.appendFromJsonStr(formData);
        }
		String docUnid=Rdb.getNewUnid();
		doc.s("WF_OrUnid", docUnid);
		
	    String msg=ProcessUtil.startNewProcess(doc, processid, docUnid, targetNodeid, targetUser, targetCopyUser, remark);
	    String jsonStr="{\"WF_DocUnid\":\""+docUnid+"\"}";
	    return RestUtil.formartResultJson("1", msg,jsonStr);
	}
}