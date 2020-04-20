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
import java.sql.Statement;
/**
 * @RuleName:PreparedStatement插入数据到数据库
 * @author  admin
 * @version: 8.0
 * @Created: 2016-07-14 14:43:42
 */
final public class R_S012_B014 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
	   
	    Connection conn=null;
	   
	    try{
	         String sql= "insert into S001_xinze_00 (TableName) values('xieweiqiang')"; 
	        conn=Rdb.getNewConnection("mysql");
	        Statement stmt = conn.createStatement();
	        stmt.executeUpdate(sql);
	           }catch(Exception e){  
	               BeanCtx.p("写入失败");
          } 
      BeanCtx.p("保存成功");
       return "";
	}
}