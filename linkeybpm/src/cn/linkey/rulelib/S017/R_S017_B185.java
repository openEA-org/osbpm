package cn.linkey.rulelib.S017;

import java.util.*;
import cn.linkey.dao.*;
import cn.linkey.util.*;
import cn.linkey.doc.*;
import cn.linkey.factory.*;
import cn.linkey.wf.*;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.org.LinkeyUser;

/**
 * @RuleName:登入登出业务系统
 * @author  admin
 * @version: 8.0
 * @Created: 2019-03-04 11:07:58
 */
final public class R_S017_B185 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
	    String action = BeanCtx.g("action");
	    String sysid = BeanCtx.g("sysid");
	    if ("login".equals(action)) {
    	    /*String sql = "select * from BPM_BusinessSystem where Systemid='" + sysid + "' and SystemPwd='" + Tools.md5(BeanCtx.g("pwd")) + "'";
    		if (!Rdb.hasRecord(sql)) {
    			BeanCtx.p(Tools.jmsg("0", "系统id或密码错误!"));
    		} else {
    		    Document d = Rdb.getDocumentBySql(sql);
    		    d.s("Status", "1");
    		    d.save();
    		    BeanCtx.p(Tools.jmsg("1", "登入成功!"));
    		}*/
    		
    		String sql = "select * from BPM_BusinessSystem where Systemid='" + sysid + "'";
    		 Document d = Rdb.getDocumentBySql(sql);
		     d.s("Status", "1");
		     d.save();
		     BeanCtx.p(Tools.jmsg("1", "登入成功!"));
	    } else if ("logout".equals(action)) {
	        String sql = "select * from BPM_BusinessSystem where Systemid='" + sysid + "'";
	        if (!Rdb.hasRecord(sql)) {
    			BeanCtx.p(Tools.jmsg("0", "不存在的业务系统!"));
    		} else {
    		    Document d = Rdb.getDocumentBySql(sql);
    		    d.s("Status", "0");
    		    d.save();
    		    BeanCtx.p(Tools.jmsg("1", "登出成功!"));
    		}
	    }
	    
	    return "";
	}
}