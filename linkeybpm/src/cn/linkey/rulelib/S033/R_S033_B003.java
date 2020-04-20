package cn.linkey.rulelib.S033;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import cn.linkey.dao.*;
import cn.linkey.util.*;
import cn.linkey.doc.*;
import cn.linkey.factory.*;
import cn.linkey.wf.*;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.org.LinkeyUser;
/**
 * @RuleName:开始测试
 * @author  admin
 * @version: 8.0
 * @Created: 2016-09-21 10:52:47
 */
final public class R_S033_B003 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
	    try{
			String restUrl=BeanCtx.g("url");
			String method=BeanCtx.g("Method"); //get,post
			String inParams=BeanCtx.g("InParams");
			String headerParams=BeanCtx.g("HeaderParams");
			String connectTimeout=BeanCtx.g("ConnectTimeout");
			String readTimeout=BeanCtx.g("ReadTimeout");
			if(Tools.isBlank(connectTimeout)){connectTimeout="30000";}
			if(Tools.isBlank(readTimeout)){readTimeout="30000";}
			
			//构建输入参数
			String postStr="";
			JSONArray jsonArray=JSONArray.parseArray(inParams);
			for(int i=0;i<jsonArray.size();i++){
				JSONObject jsonObject=jsonArray.getJSONObject(i);
				String id=jsonObject.getString("id");
				String value=jsonObject.getString("value");
				if(Tools.isBlank(postStr)){
					postStr=id+"="+value;
				}else{
					postStr+="&"+id+"="+value;
				}
			}
			BeanCtx.out("method="+method);
//			BeanCtx.out("PostStr="+postStr);
			if(method.equalsIgnoreCase("GET")){
				restUrl=restUrl+"?"+postStr;
			}
			BeanCtx.out(restUrl);
			URL url = new URL(restUrl);  
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();      //打开restful链接  
			conn.setRequestMethod(method);//POST GET PUT DELETE  
			
			//设置访问提交模式，表单提交  
	//		conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");  
			
			//设置请求头的参数
			jsonArray=JSONArray.parseArray(headerParams);
			for(int i=0;i<jsonArray.size();i++){
				JSONObject jsonObject=jsonArray.getJSONObject(i);
				String id=jsonObject.getString("id");
				String value=jsonObject.getString("value");
//				BeanCtx.out(id+"="+value);
				conn.setRequestProperty(id,value);  
			}
			
			conn.setConnectTimeout(Integer.parseInt(connectTimeout));//连接超时 单位毫秒  
			conn.setReadTimeout(Integer.parseInt(readTimeout));//读取超时 单位毫秒  
			
			conn.setDoOutput(true);// 是否输入参数
			conn.setDoInput(true);
			
			if(method.equalsIgnoreCase("GET")){
				conn.connect();
			}else{
				//POST方法需要传参数
				OutputStream outStrm = conn.getOutputStream();    
				BeanCtx.out("postStr="+postStr);
				byte[] bypes=postStr.getBytes("UTF-8");
				try{
					outStrm.write(bypes);// 输入参数
					outStrm.flush();
					outStrm.close();
					BeanCtx.out("写入成功post");
				}catch(Exception e){
					BeanCtx.log(e, "E", "");
				}finally{
					outStrm.close();
				}
			}
			
			int status=conn.getResponseCode();
			
			//获得返回值
			InputStream inStream=conn.getInputStream();  
			String returnStr=Tools.streamToString(inStream, "UTF-8");
			Map<String,List<String>> map=conn.getHeaderFields();
//			for(Map<String>)
//			
//			BeanCtx.out(conn.getHeaderFields());
			BeanCtx.p("ResponseCode="+status+"\nContent-Type="+conn.getContentType()+"\n"+conn.getHeaderField("Set-Cookie")+"\n"+returnStr);
			
	    }catch(Exception e){
	    	BeanCtx.log(e, "E","");
	    	BeanCtx.p(Tools.getErrorMsgFromException(e));
	    }
	    return "";
	}
}