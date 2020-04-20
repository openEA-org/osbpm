package cn.linkey.rulelib.S017;

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
 * @RuleName:Rest_Engine输出SVG流程图
 * @author  admin
 * @version: 8.0
 * @Created: 2015-12-02 20:45
 */
final public class R_S017_B125 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
	     //示例参数:{"Processid":"流程id","DocUnid":"流程实列id"}
		 String processid =BeanCtx.g("processId",true); //流程id
		 String type=BeanCtx.g("type",true);
		 if(Tools.isBlank(type)){type="vml";}
	    String sql="select GraphicBody from BPM_ModGraphicList where Processid='"+processid+"'";
	    if(Tools.isBlank(processid)){return RestUtil.formartResultJson("0", "processid不能为空");}
	    
	    String xmlBody=Rdb.getValueBySql(sql);
	    xmlBody=Rdb.deCode(xmlBody, false);
	    if(type.equalsIgnoreCase("svg")){
	    	xmlBody=Vml2Svg.getSvgXml(xmlBody); //把vml转为svg
	    }else{
	    	 String htmlcode="<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:v=\"urn:schemas-microsoft-com:vml\">\n";
	         htmlcode+="<head>\n";
	         htmlcode+="<META http-equiv=Content-Type content=\"text/html; Charset=UTF-8\">\n";
	         htmlcode+="<meta http-equiv=\"X-UA-Compatible\" content=\"IE=EmulateIE7\" />\n";
	         htmlcode+="<title>VML</title>\n";
	         htmlcode+="<style>\n";
	         htmlcode+="v\\:* {behavior:url(#default#VML);}\n";
	         htmlcode+="o\\:* {behavior:url(#default#VML);}\n";
	         htmlcode+="w\\:* {behavior:url(#default#VML);}\n";
	         htmlcode+=".shape {behavior:url(#default#VML);}\n";
	         htmlcode+="</style>\n";
	         htmlcode+="<body style=\"margin:0px;background-color:#f8f8f8;\" scroll=\"auto\" >\n";
	         htmlcode+=xmlBody+"\n";
	         htmlcode+="</body></html>";
	         xmlBody=htmlcode;
	    }
	    
	    String jsonStr=RestUtil.formartResultJson("1","", "{\"XmlBody\":\""+RestUtil.encodeJson(xmlBody)+"\"}");
		return jsonStr;
		
	}
}