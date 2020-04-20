package cn.linkey.rulelib.S018;

import java.util.HashMap;

import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.rest.RestUtil;
import cn.linkey.rule.LinkeyRule;

public class R_S018_B028 implements LinkeyRule {
	
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    String processsql = "select distinct WF_ProcessName from BPM_ArchivedData";
	    String countsql = "";
	    String count = "[";
	    Document[] document = Rdb.getAllDocumentsBySql(processsql);
	    for (Document doc : document) {
	        countsql = "select WF_ProcessName from BPM_ArchivedData where WF_ProcessName='" + doc.g("WF_ProcessName") + "'";
	        count = count + "{value:" + Rdb.getCountBySql(countsql) + ",name:'" + doc.g("WF_Processname") + "'},";
	    }
	    count = count.substring(0, count.length() - 1);
	    count = count + "]";
	    
	    String jsonStr=RestUtil.formartResultJson("1", "", count);
	    BeanCtx.p(jsonStr);
	    return jsonStr;
	}
	
}
