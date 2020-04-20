package cn.linkey.rulelib.S018;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rest.RestUtil;
import cn.linkey.rule.LinkeyRule;

public class R_S018_B029 implements LinkeyRule {
	
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    String sql="select * from BPM_RestInterfaceList where WF_AppId='S018' order by url";
	    Document[] dc=Rdb.getAllDocumentsBySql(sql);
	    String htmlCode="<link rel=\"stylesheet\" type=\"text/css\" href=\"linkey/bpm/css/app_openform.css\">"
	    		+ "<table width='100%' class='linkeytable' border='1px' style='border-collapse:collapse;'><tr bgcolor='#cccccc'><td></td><td>Method</td><td>接口名称</td><td>url</td><td>输入参数</td><td>输出参数</td></tr>";
	    int i=0;
	    for(Document doc:dc){
	    	i++;
	    	String inParams=doc.g("inParams").replace("\n","<br>");
	    	htmlCode+="<tr height=\"30px\"  ><td width='20px'>"+i+"</td><td>"+doc.g("Method")+"</td><td nowrap >"+doc.g("InterfaceName")+"</td><td nowrap >"+doc.g("url")+"</td><td>"+inParams+"</td><td>"+doc.g("outParams")+"</td></tr>";
	    }
	    htmlCode="<html><title>Rest List</title><style>td{font-size:9pt}</style><body>"+htmlCode+"</table></body></html>";
	    BeanCtx.p(htmlCode);
	    return "";
	}
	
}
