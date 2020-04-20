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
 * @RuleName:删除子流程节点属性
 * @author  admin
 * @version: 1.0
 */
final public class R_S018_B017 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
		String tableName="BPM_ModSubProcessList";
		
		String processId=BeanCtx.g("processId",true);
		String nodeid=BeanCtx.g("nodeid",true);
		
		if(Tools.isBlank(processId)){return RestUtil.formartResultJson("0", "processId不能为空");}
		if(Tools.isBlank(nodeid)){return RestUtil.formartResultJson("0", "nodeid不能为空");}
		
		String sql="delete from "+tableName+" where ProcessId='"+processId+"' and Nodeid='"+nodeid+"'";
		int i=Rdb.execSql(sql);
        if(i>0){
        	return RestUtil.formartResultJson("1", "节点属性成功删除");
        }else{
        	return RestUtil.formartResultJson("0", "节点属性删除失败");
        }
	}
}