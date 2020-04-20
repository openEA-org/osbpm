package cn.linkey.rulelib.S018;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rest.RestUtil;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.util.Tools;
/**
 * @RuleName:获取流程图JSON数据
 * @author  admin
 * @version: 8.0
 * @Created: 2018-09-28 09:29:45
 */
final public class R_S018_B031 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    
	    String processId = BeanCtx.g("processid");
	    if(Tools.isBlank(processId)){
	    	processId = null;
	    }
		
	    StringBuffer sb = new StringBuffer();
	    sb.append("Select GraphicBody from bpm_modgraphiclist where FlowType = '2'");
	    String jsonStr = "";
	    if (processId != null) {
	    	sb.append(" and Processid = '" + processId + "'");
	    	Document doc = Rdb.getDocumentBySql(sb.toString());
		    jsonStr = doc.toJson();
		    jsonStr = RestUtil.formartResultJson("1", "查询成功", jsonStr);
	    } else {
	    	jsonStr = RestUtil.formartResultJson("0", "查询失败，没有指定processid");
	    }
        
        //BeanCtx.p(jsonStr);  //这里可以调试输出预览 JSON 字符串
        
		return jsonStr;
	}
}