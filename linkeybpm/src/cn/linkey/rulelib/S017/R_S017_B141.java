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
 * @RuleName:Rest_Engine查询节点已审批用户
 * @author  admin
 * @version: 8.0
 * @Created: 2014-07-10 21:52
 */
final public class R_S017_B141 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
        String docUnid = BeanCtx.g("docUnid"); //实例id
        String nodeid=BeanCtx.g("nodeId"); //节点id
        
        if(Tools.isBlank(docUnid)){return Tools.jmsg("0", "docUnid不能为空");}
        if(Tools.isBlank(nodeid)){return Tools.jmsg("0", "nodeId不能为空");}

        String sql="select WF_ProcessId from bpm_maindata where WF_OrUnid='"+docUnid+"'";
        String processid=Rdb.getValueBySql(sql);
        
        sql = "select ActionUserid from bpm_insnodelist where DocUnid='" + Rdb.formatArg(docUnid) + "' and  Processid='" + Rdb.formatArg(processid) + "' and Nodeid='" + Rdb.formatArg(nodeid) + "' and Status='End'";
        HashSet<String> userSet=Rdb.getValueSetBySql(sql);
        String userList=Tools.join(userSet, ",");
        
        return RestUtil.formartResultJson("1","", "{\"userIds\":\""+userList+"\"}");
	}
	
}