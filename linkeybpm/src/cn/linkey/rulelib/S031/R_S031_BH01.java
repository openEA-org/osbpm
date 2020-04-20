package cn.linkey.rulelib.S031;

import java.util.HashMap;

import javax.servlet.http.Cookie;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * 
 * 获取用户主题
 *
 * @author: alibao
 *
 * Modification History:
 * Date         Author          Version            Description
 *---------------------------------------------------------*
 * 2018年9月6日     alibao           v1.0.0               修改原因
 */
final public class R_S031_BH01 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
	    
	    String themePath = this.getNowTheme();//获取当前主题并且返回  

	    BeanCtx.p("{\"Status\":\"ok\",\"themePath\":\"" + themePath +"\"}");
	    

	    return "";
	}
	
    /*
     * 返回主题存放的文件夹路径
     */
	public static String getNowTheme(){
    //从cookie中读取主题id
	    String nowthemeid = "";
	    String themePath = ""; // 主题存放文件夹路径 
      	Cookie[] cookies = BeanCtx.getRequest().getCookies();//获取cookie数组  
        for(Cookie cookie : cookies){  
            if(cookie.getName().equals("themename")){  
            	nowthemeid = cookie.getValue();  
            }  
        }
       // BeanCtx.out("cs:"+nowthemeid);
        String sql = "";
        sql = "select * from S031_ThemeList where WF_OrUnid='"+nowthemeid+"'";
	    Document themeDoc = Rdb.getDocumentBySql(sql);   //获取主题对象 
	    if(themeDoc.isNull()){
	        nowthemeid = "4e1b8bf405e4504c740b3af0eb2ca5860b96";
	        sql = "select * from S031_ThemeList where WF_OrUnid='"+nowthemeid+"'";
	        themeDoc = Rdb.getDocumentBySql(sql);   //获取主题对象 
	        
	        //后期优化：第一次初始化插入默认主题
	        
	        
	    }
	    themePath = themeDoc.g("Theme_Path");    //获取主题存放文件夹路径
	    //updateHeader(themePath);
	    
	    
	    return themePath;
    }
    
    	private static void updateHeader(String filePath){

	    String sql1="update BPM_SystemConfig set ConfigValue='<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><link rel=\"stylesheet\" id=\"bpm_theme\" type=\"text/css\" href=\""+filePath+"/devclient.css\"><script type=\"text/javascript\" src=\"linkey/bpm/easyui/newtheme/CreateCSSforDev.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/lang/{LANG}.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/easyui/jquery.min.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/easyui/jquery.easyui.min.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/sharefunction.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/json.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/viewgrid.js\"></script>'where Configid='AppGridHtmlHeader'";
	    String sql2="update BPM_SystemConfig set ConfigValue='<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><link rel=\"stylesheet\" id=\"bpm_theme\" type=\"text/css\" href=\""+filePath+"/devclient.css\"><script type=\"text/javascript\" src=\"linkey/bpm/easyui/newtheme/CreateCSSforDev.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/lang/{LANG}.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/easyui/jquery.min.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/easyui/jquery.easyui.min.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/sharefunction.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/json.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/app_openform.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/uivalidate.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/app_custom.js\"></script>'where Configid='AppFormHtmlHeader'";
	    String sql3="update BPM_SystemConfig set ConfigValue='<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><link rel=\"stylesheet\" id=\"bpm_theme\" type=\"text/css\" href=\""+filePath+"/devclient.css\"><script type=\"text/javascript\" src=\"linkey/bpm/easyui/newtheme/CreateCSSforDev.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/easyui/jquery.min.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/easyui/jquery.easyui.min.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/sharefunction.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/json.js\"></script>'where Configid='AppPageHtmlHeader'";
	    String sql4="update BPM_SystemConfig set ConfigValue='<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><link rel=\"stylesheet\" id=\"bpmu_theme\" type=\"text/css\" href=\""+filePath+"/userclient.css\"><script type=\"text/javascript\" src=\"linkey/bpm/easyui/newtheme/CreateCSSforUser.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/easyui/jquery.min.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/easyui/jquery.easyui.min.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/sharefunction.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/json.js\"></script>'where Configid='AppPageHtmlHeader_theme'";
	    String sql5="update BPM_SystemConfig set ConfigValue='<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><link rel=\"stylesheet\" id=\"bpm_theme\" type=\"text/css\" href=\""+filePath+"/devclient.css\"><script type=\"text/javascript\" src=\"linkey/bpm/easyui/newtheme/CreateCSSforDev.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/lang/{LANG}.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/easyui/jquery.min.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/easyui/jquery.easyui.min.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/json.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/sharefunction.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/engine_openform.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/uivalidate.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/viewgrid.js\"></script><script type=\"text/javascript\" src=\"linkey/bpm/jscode/engine_custom.js\"></script>'where Configid='ProcessFormHtmlHeader'";
	    String sql6="update BPM_SystemConfig set ConfigValue='<link rel=\"stylesheet\" id=\"bpm_theme\" type=\"text/css\" href=\"../"+filePath+"/devclient.css\"><script type=\"text/javascript\" src=\"../linkey/bpm/easyui/newtheme/CreateCSSforDev.js\"></script>'where Configid='DesignerHtmlHeader'";
	    
	    Rdb.execSql(sql1.toString());
	    Rdb.execSql(sql2.toString());
	    Rdb.execSql(sql3.toString());
	    Rdb.execSql(sql4.toString());
	    Rdb.execSql(sql5.toString());
	    Rdb.execSql(sql6.toString());
	    
	}
	
}