package cn.linkey.rulelib.S033;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import cn.linkey.dao.*;
import cn.linkey.util.*;
import cn.linkey.doc.*;
import cn.linkey.factory.*;
import cn.linkey.wf.*;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.org.LinkeyUser;
/**
 * @RuleName:发送Post数据
 * @author  admin
 * @version: 8.0
 * @Created: 2016-09-22 11:34:25
 */
final public class R_S033_B005 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
	    
	        String encoding="UTF-8";
	        String pStr="?&arg=test&pwd=123";
	        String path ="http://localhost:8080/bpm/r?wf_num=R_S033_B004";
	        byte[] data = pStr.getBytes(encoding);
	        URL url =new URL(path);
	        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
	        conn.setRequestMethod("POST");
	        conn.setUseCaches(false);  
	        conn.setDoOutput(true);
	        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	        conn.setConnectTimeout(5*1000);
	        conn.getOutputStream().write(data);   
	        conn.getOutputStream().flush();  
	        conn.getOutputStream().close();
	        System.out.println(conn.getResponseCode()); //响应代码 200表示成功
	        if(conn.getResponseCode()==200){
//	            InputStream inStream = conn.getInputStream();   
//	            String result=Tools.streamToString(inStream,"utf-8");
	            BeanCtx.p("post end");
	        }
	        conn.disconnect();
	    return "";
	}
}