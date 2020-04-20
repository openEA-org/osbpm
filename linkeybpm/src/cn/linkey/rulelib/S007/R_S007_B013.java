package cn.linkey.rulelib.S007;

import java.util.*;
import cn.linkey.dao.*;
import cn.linkey.util.*;
import cn.linkey.doc.*;
import cn.linkey.factory.*;
import cn.linkey.wf.*;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.org.LinkeyUser;
/**
 * @RuleName:根据DeptId获取部门文书
 * @author  admin
 * @version: 8.0
 * @Created: 2016-05-12 16:59
 */
final public class R_S007_B013 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
	    String sql="select * from bpm_orgdeptlist where OrgClass='1' and Deptid='"+BeanCtx.g("DeptId")+"'";
	    Document doc=Rdb.getDocumentBySql(sql);
	    if(!doc.isNull()){
	        BeanCtx.print("{dept_clerk:'" + doc.g("dept_clerk") + "'}");
	    }
	    
	    return "";
	}
}