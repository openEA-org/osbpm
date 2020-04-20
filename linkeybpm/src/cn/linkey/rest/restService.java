package cn.linkey.rest;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.spi.resource.Singleton;

import cn.linkey.app.AppUtil;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.*;
import cn.linkey.servlet.DESUserid;
import cn.linkey.util.Tools;

@Produces("text/html")
@Consumes("text/html")
@Path("ws")
@Singleton
public class restService{

	//访问路径http://localhost:8080/bpm/rest/ws/g/S033/getuser/pathparam
	
	@GET
	@Path("/g/{appid}/{restid}/{params}")
	public String getRuleNum(@PathParam("appid") String appid,@PathParam("restid") String restid,@PathParam("params") String params,@Context UriInfo ui,@Context HttpServletRequest request,@Context HttpServletResponse response ) {
		MultivaluedMap<String, String> queryParams=ui.getQueryParameters();
//		BeanCtx.out("queryParams="+queryParams);
		String errormsg="";
		HashMap<String,Object> ruleParams=new HashMap<String,Object>();
		ruleParams.put("PathParam", params); //路径参数传入到规则中去
		ruleParams.put("appid", appid);
		ruleParams.put("restid", restid);
		try{
			
			for(String keyName:queryParams.keySet()){
				ruleParams.put(keyName, queryParams.getFirst(keyName));
			}
			
		 BeanCtx.init("", request, response); //首先初始化
		 
        //1.检测wf_num是否符合要求
        if (!Tools.isString(appid) || !Tools.isString(restid) || !Tools.isString(params)) { //防止sql注入
            return "Error:illegal url!";
        }
        BeanCtx.setAppid(appid);//当前应用的编号设置到线程变量中去
        
        //获得发布接口的配置信息
        String sql="select RuleNum from BPM_RestInterfaceList where WF_Appid='"+appid+"' and Interfaceid='"+restid+"'";
        String rulenum=Rdb.getValueBySql(sql);
        if(Tools.isBlank(rulenum)){
        	return "Error:can't found the rest interface!";
        }
        
        BeanCtx.setWfnum(rulenum); //把当前设计的编号设置到线程变量中去
        
        //4.获得应用文档检测应用是否允许匿名访问
        String userid = "";
        Document appdoc = AppUtil.getDocByid("BPM_AppList", "WF_Appid", appid, true);
        if (appdoc.isNull() || appdoc.g("Status").equals("0")) {
            //应用不存在,或者应用未启用禁止访问
        	errormsg=BeanCtx.getMsg("Common", "AppNotFindOrStop", "");
            return errormsg;
        }
        else {
            userid = DESUserid.getLoginUserid(request, response);//从session中获取用户名
            if (appdoc.g("Anonymous").equals("1")) {
                //说明允许匿名访问
                if (Tools.isBlank(userid)) {
                    userid = "Anonymous";
                }
            }
            else {
                // 2.1.看用户是否已登录
                if (Tools.isBlank(userid)) { // 用户未登录
                   // return "请先调用登录接口";
                }
            }
        }

        //5.设置已登录用户的userid,并重新初始BeanCtx
        BeanCtx.init(userid, request, response);
        
        //运行规则并返回结果
        String result = BeanCtx.getExecuteEngine().run(rulenum,ruleParams); //执行规则
        return result;
        
    }
    catch (Exception e) {
        BeanCtx.log(e, "W", "");
        BeanCtx.setRollback(true); //设置为需要回滚如果没有开启事务则设置了回滚系统也不会回滚数据
        return "run error";
    }
    finally {
        BeanCtx.close(); // 这句一定要执行，要收回资源
    }
    
	}
	
	//访问路径http://localhost/bpm/rest/ws/p/rulenum
	@POST
	@Produces("text/html")
	@Consumes("application/x-www-form-urlencoded")
	@Path("/p/{appid}/{restid}/{actionid}")
	public String postRuleNum(@PathParam("appid") String appid,@PathParam("restid") String restid,@PathParam("actionid") String actionid,MultivaluedMap<String, String> formParams,@Context HttpServletRequest request,@Context HttpServletResponse response ) {
//		BeanCtx.out("formParams="+formParams);
		HashMap<String,Object> params=new HashMap<String,Object>();
		try{
			params.put("appid", appid);
			params.put("restid", restid);
			params.put("actionid", actionid);
			for(String keyName:formParams.keySet()){
				params.put(keyName, formParams.getFirst(keyName));
			}
			
			BeanCtx.init("", request, response); //首先初始化
		 
	        //1.检测是否符合要求
	        if (!Tools.isString(appid) || !Tools.isString(restid) || !Tools.isString(actionid)) { //防止sql注入
	            return "Error:illegal url!";
	        }
	        
	        BeanCtx.setAppid(appid);//当前应用的编号设置到线程变量中去
        
	        //获得发布接口的配置信息
	        String sql="select RuleNum from BPM_RestInterfaceList where WF_Appid='"+appid+"' and Interfaceid='"+restid+"'";
	        String rulenum=Rdb.getValueBySql(sql);
	        if(Tools.isBlank(rulenum)){
	        	return "Error:can't found the rest interface!";
	        }
	        
	        BeanCtx.setWfnum(rulenum); //把当前设计的编号设置到线程变量中去
	        
	        String result = BeanCtx.getExecuteEngine().run(rulenum,params); //执行规则
	        return result;
        
    }
    catch (Exception e) {
        BeanCtx.log(e, "W", "");
        BeanCtx.setRollback(true); //设置为需要回滚如果没有开启事务则设置了回滚系统也不会回滚数据
        return "run error";
    }
    finally {
        BeanCtx.close(); // 这句一定要执行，要收回资源
    }
    
	}
	
}