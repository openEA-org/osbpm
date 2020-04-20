package cn.linkey.rulelib.S017;

import java.util.*;
import cn.linkey.dao.*;
import cn.linkey.util.*;
import cn.linkey.doc.*;
import cn.linkey.factory.*;
import cn.linkey.wf.*;
import cn.linkey.rule.LinkeyRule;
import cn.linkey.org.LinkeyUser;
/**
 * @RuleName:List所有Rest服务清单
 * @author  admin
 * @version: 8.0
 * @Created: 2017-09-18 09:41:28
 */
final public class R_S017_B146 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
	    String sql="select * from BPM_RestInterfaceList order by url";
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