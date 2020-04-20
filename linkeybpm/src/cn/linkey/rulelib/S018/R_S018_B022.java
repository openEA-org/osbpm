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
 * @RuleName:保存事件节点属性
 * @author  admin
 * @version: 1.0
 */
final public class R_S018_B022 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
		String tableName="BPM_ModEventList";
		
		String processId=BeanCtx.g("processId",true);
		String nodeid=BeanCtx.g("Nodeid",true);
		String NodeName=BeanCtx.g("NodeName");
		String ExtNodeType=BeanCtx.g("ExtNodeType");
		
		if(Tools.isBlank(processId)){return RestUtil.formartResultJson("0", "processId不能为空");}
		if(Tools.isBlank(NodeName)){return RestUtil.formartResultJson("0", "NodeName不能为空");}
		if(Tools.isBlank(nodeid)){return RestUtil.formartResultJson("0", "Nodeid不能为空");}
		if(Tools.isBlank(ExtNodeType)){return RestUtil.formartResultJson("0", "ExtNodeType不能为空");}
		
        Document eldoc = Rdb.getDocumentBySql("select * from "+tableName+" where ProcessId='"+processId+"' and Nodeid='"+nodeid+"'");
        eldoc.appendFromRequest();
        if (eldoc.isNull()) {
        	eldoc.s("Processid", processId);
        	eldoc.s("Nodeid", nodeid);
        	eldoc.s("NodeType", "Event");
        	eldoc.s("ExtNodeType", ExtNodeType);
            eldoc.s("WF_OrUnid", Rdb.getNewUnid());
        }
        int i=eldoc.save();
        if(i>0){
        	return RestUtil.formartResultJson("1", "节点属性成功保存");
        }else{
        	return RestUtil.formartResultJson("0", "节点属性保存失败");
        }
	}
}