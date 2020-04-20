package cn.linkey.rulelib.S018;

import java.util.*;

import cn.linkey.app.AppUtil;
import cn.linkey.dao.*;
import cn.linkey.util.*;
import cn.linkey.doc.*;
import cn.linkey.factory.*;
import cn.linkey.wf.*;
import cn.linkey.rest.RestUtil;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.org.LinkeyUser;
/**
 * @RuleName:保存流程过程属性
 * @author  admin
 * @version: 1.0
 */
final public class R_S018_B013 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
		String tableName="BPM_ModProcessList";
		
		String processId=BeanCtx.g("processId",true);
		String appId=BeanCtx.g("WF_Appid",true);
		String NodeName=BeanCtx.g("NodeName");
		if(Tools.isBlank(processId)){processId=Rdb.getNewUnid();}
		
		if(Tools.isBlank(NodeName)){return RestUtil.formartResultJson("0", "NodeName不能为空");}
		if(Tools.isBlank(appId)){return RestUtil.formartResultJson("0", "WF_AppId不能为空");}
		
		
        Document eldoc = Rdb.getDocumentBySql("select * from "+tableName+" where ProcessId='"+processId+"'");
    	eldoc.appendFromRequest();
        if (eldoc.isNull()) {
        	eldoc.s("Processid", processId);
        	eldoc.s("WF_Appid", appId);
        	eldoc.s("NodeType", "Process");
        	eldoc.s("ExtNodeType", "Process");
        	eldoc.s("Status", "0");
        	eldoc.s("SortNum", "1001");
            eldoc.s("WF_OrUnid", Rdb.getNewUnid());
        }
        int i=eldoc.save();
        if(i>0){
        	return RestUtil.formartResultJson("1", "流程过程属性成功保存");
        }else{
        	return RestUtil.formartResultJson("0", "流程过程属性保存失败");
        }
	}
}