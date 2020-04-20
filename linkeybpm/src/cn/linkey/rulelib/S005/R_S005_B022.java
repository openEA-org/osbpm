package cn.linkey.rulelib.S005;

import java.util.*;
import cn.linkey.dao.*;
import cn.linkey.util.*;
import cn.linkey.doc.*;
import cn.linkey.factory.*;
import cn.linkey.wf.*;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.org.LinkeyUser;
import java.io.File;
/**
 * @RuleName:头像上去获取文件是否存在
 * @author  designer
 * @version: 8.0
 * @Created: 2016-11-11 16:11:18
 */
final public class R_S005_B022 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
//
    
     String Name []={"png","jpg","jpeg","gif"};
     List<String> list=Arrays.asList(Name);
     if(list.contains(getLastName(BeanCtx.getUserid()))){
		BeanCtx.p("{\"Judge\":\"true\",\"Userid\":\""+BeanCtx.getUserid()+"\",\"LastName\":\""+getLastName(BeanCtx.getUserid())+"\",\"url\":\"attachment/headpic/"+getLastName(BeanCtx.getUserid())+"\"}");
		}else{
		BeanCtx.p("{\"Judge\":\"false\",\"Userid\":\""+BeanCtx.getUserid()+"\",\"LastName\":\""+getLastName(BeanCtx.getUserid())+"\",\"UserName\":\"3\"}");
		}
	    
	    return "";
	}
	
/*	public static String getExtensionName(String filename) { 
        if ((filename != null) && (filename.length() > 0)) { 
            int dot = filename.lastIndexOf('.'); 
            if ((dot >-1) && (dot < (filename.length() - 1))) { 
                return filename.substring(dot + 1); 
            } 
        } 
        return filename; 
    } */
   //判断文件是否存在 
public String getLastName(String Userid){
   
    File file = new File(BeanCtx.getAppPath()+"\\attachment\\headpic\\png\\"+Userid+".png");
     if(file.exists()){
         return "png";
     }
      file = new File(BeanCtx.getAppPath()+"\\attachment\\headpic\\jpg\\"+Userid+".jpg");
     if(file.exists()){
         return "jpg";
     }
     file = new File(BeanCtx.getAppPath()+"\\attachment\\headpic\\jpeg\\"+Userid+".jpeg");
     if(file.exists()){
         return "jpeg";
     }
     file = new File(BeanCtx.getAppPath()+"\\attachment\\headpic\\gif\\"+Userid+".gif");
     if(file.exists()){
         return "gif";
     }
     
    
    return "";
	}
}