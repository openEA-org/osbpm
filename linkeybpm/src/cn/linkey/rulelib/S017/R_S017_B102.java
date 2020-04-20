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
 * @RuleName:收回文档
 * @author  admin
 * @version: 1.0
 */
final public class R_S017_B102 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	        String docUnid = BeanCtx.g("docUnid");
	        if(Tools.isBlank(docUnid)){return RestUtil.formartResultJson("0", "docUnid不能为空");}
	        
	        String sql="select WF_processId from bpm_maindata where WF_OrUnid='"+docUnid+"'";
	        String processid=Rdb.getValueBySql(sql);
	        
	        String actionid = "Undo";
	        params.put("WF_Processid",processid);
	        params.put("WF_DocUnid",docUnid);
	        params.put("WF_Action",actionid);
	        ProcessEngine linkeywf=new ProcessEngine();
	        BeanCtx.setLinkeywf(linkeywf);
	        linkeywf.init(processid,docUnid,BeanCtx.getUserid(),"");
	        
	        String msg=BeanCtx.getLinkeywf().run(actionid,params);
	        
	        return RestUtil.formartResultJson("1", msg);
	}
}