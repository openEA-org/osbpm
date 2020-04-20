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
 * @RuleName:Rest_Form获取完整的流程表单HTML代码
 * @author  admin
 * @version: 1.0
 */
final public class R_S017_B144 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
        String processid =BeanCtx.g("processId"); //流程id
        String docUnid = BeanCtx.g("docUnid"); //实例id

        
        if(Tools.isBlank(processid)){return Tools.jmsg("0", "processid不能为空");}
        if(Tools.isBlank(docUnid)){return Tools.jmsg("0", "docUnid不能为空");}

		//初始化引擎
		ProcessEngine linkeywf=BeanCtx.getDefaultEngine();
		if(Tools.isBlank(docUnid)){docUnid=Rdb.getNewUnid();} //如果没有传入文档unid则说明要启动一个新文档
		linkeywf.init(processid,docUnid,BeanCtx.getUserid(),""); //初始化工作流引擎
		linkeywf.getDocument().appendFromRequest();

        String htmlBody = linkeywf.open();
        String jsonStr=RestUtil.formartResultJson("1","", "{\"htmlCode\":\""+RestUtil.encodeJson(htmlBody)+"\"}");
		return jsonStr;
		
	}

	
}