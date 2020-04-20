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
 * @RuleName:获得流程节点属性配置服务
 * @author  admin
 * @version: 8.0
 * @Created: 2014-07-01 22:40
 */
final public class R_S017_B136 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
	    ModNode modNode=(ModNode)BeanCtx.getBean("ModNode");
	    String processid=BeanCtx.g("processId");
	    String nodeid=BeanCtx.g("nodeId");
	    
        if(Tools.isBlank(processid)){return Tools.jmsg("0", "processId不能为空");}
        if(Tools.isBlank(nodeid)){return Tools.jmsg("0", "nodeId不能为空");}
        
	    
	    Document nodeDoc=modNode.getNodeDoc(processid,nodeid);
	    if(nodeDoc.isNull()){
	        return RestUtil.formartResultJson("0","节点不存在!","");
	    }else{
	    	return RestUtil.formartResultJson("1","",nodeDoc.toJson());
	    }
	}
}