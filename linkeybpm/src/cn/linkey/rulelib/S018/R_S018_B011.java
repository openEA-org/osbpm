package cn.linkey.rulelib.S018;

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
 * @RuleName:复制规则
 * @author  admin
 * @version: 1.0
 */
final public class R_S018_B011 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
        String docUnid =BeanCtx.g("docUnid"); //流程id多个用逗号分隔
        if(Tools.isBlank(docUnid)){return RestUtil.formartResultJson("0", "docUnid不能为空");}
   
        int i=0;
        String[] docArray=docUnid.split(",");
        for(String pid:docArray){
                String sql = "select * from BPM_RuleList where WF_OrUnid='" + pid + "'";
                Document doc=Rdb.getDocumentBySql(sql);
                if(!doc.isNull()){
                	doc.s("WF_OrUnid", Rdb.getNewUnid());
                	doc.save();
                	i++;
                }
        }
        
	    return RestUtil.formartResultJson("1", "成功复制("+i+")个规则!","");
	}
}