package cn.linkey.rulelib.S012;

import java.util.*;
import cn.linkey.dao.*;
import cn.linkey.util.*;
import cn.linkey.doc.*;
import cn.linkey.factory.*;
import cn.linkey.wf.*;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.org.LinkeyUser;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
/**
 * @RuleName:PreparedStatement查看数据
 * @author  admin
 * @version: 8.0
 * @Created: 2016-07-14 14:55:21
 */
final public class R_S012_B015 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
	    Connection conn=null;
	    PreparedStatement ps=null;
	    ResultSet rs=null;
	    String passwd=null;
	    try{
	        conn=Rdb.getNewConnection("mysql");
	        String sql= "Select * from BPM_ProcessMapData "; 
	        
	           ps=conn.prepareStatement(sql);
	           rs=ps.executeQuery();
	           while(rs.next()){
	               passwd=rs.getString(1);
	           }
	        
	        
	    }catch(Exception e){
	        BeanCtx.p("出错了<br>");
	    }finally{
	        Rdb.close(conn);
	    }
	    BeanCtx.p(passwd);
	    return "";
	}
}