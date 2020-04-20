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
 * @RuleName:删除表单
 * @author  admin
 * @version: 8.0
 * @Created: 2017-09-18 09:41:28
 */
final public class R_S018_B001 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
        String docUnid =BeanCtx.g("docUnid"); //流程id多个用逗号分隔
        if(Tools.isBlank(docUnid)){return RestUtil.formartResultJson("0", "docUnid不能为空");}
   
        int i=0;
        String[] docArray=docUnid.split(",");
        for(String pid:docArray){
                String sql = "select * from BPM_FormList where WF_OrUnid='" + pid + "'";
                Document doc=Rdb.getDocumentBySql(sql);
                if(!doc.isNull()){
                	i++;
                	doc.remove(true);
                }
        }
        
	    return RestUtil.formartResultJson("1", "成功删除("+i+")个表单!","");
	    
	}
}