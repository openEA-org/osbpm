package cn.linkey.rulelib.S018;

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
 * @RuleName:监控正在审批中的流程
 * @author  admin
 * @version: 1.0
 */
final public class R_S018_B024 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
	    String pageSize=BeanCtx.g("pageSize");
	    String pageNo=BeanCtx.g("pageNo");
	    String appid=BeanCtx.g("appId");
	    String keyWord=BeanCtx.g("keyWord");
	    String processId=BeanCtx.g("processId");
	    String processName=BeanCtx.g("processName");
	    
//	    if(Tools.isBlank(processId)){return RestUtil.formartResultJson("0", "processId不能为空");}
	    
	    if(Tools.isBlank(pageSize)){pageSize="20";}
	    if(Tools.isBlank(pageNo)){pageNo="1";}
	    
	    //只查询自已有权限的文档
	    String sqlWhere="WF_Status<>'Draft'";
	    String sql="select WF_DocNumber,Subject,WF_Author_CN,WF_AddName_CN,WF_DocCreated,WF_ProcessName,WF_CurrentNodeName,WF_OrUnid,WF_Processid from BPM_MainData where "+sqlWhere;
	    if(Tools.isNotBlank(processId)){
	        sql+=" and WF_Processid='"+appid+"'";
	    }
	    
	    if(Tools.isNotBlank(appid)){
	        sql+=" and WF_Appid='"+appid+"'";
	    }
	    //增加搜索条件WF_ProcessId
	    if(Tools.isNotBlank(processId)){
	        sql+=" and WF_ProcessId like '%"+processId+"%'";
	    }
	    //增加搜索条件WF_ProcessName
	    if(Tools.isNotBlank(processName)){
	        sql+=" and WF_ProcessName like '%"+processName+"%'";
	    }
	    //增加搜索条件subject
	    if(Tools.isNotBlank(keyWord)){
	        sql+=" and subject like '%"+keyWord+"%'";
	    }
	    //增加排序功能
	    sql+=" order by WF_DocCreated desc";
//	    BeanCtx.out(sql);
	    String totalNum=String.valueOf(Rdb.getCountBySql(sql));
	    Document[] dc=Rdb.getAllDocumentsBySql("BPM_MainData",sql,Integer.valueOf(pageNo),Integer.valueOf(pageSize));
	    //格式化总耗时
	    for(Document doc:dc){
	        String difTime=DateUtil.getAllDifTime(doc.g("WF_DocCreated"), DateUtil.getNow());
    		int min=Integer.valueOf(difTime);
    		if(min>60){
    			difTime=String.valueOf(min/60)+"(小时)";
    		}else{
    			difTime=min+"(分钟)";
    		}
    		doc.s("TotalTime",difTime);
	    }
	    
	    String jsonStr="{\"total\":"+totalNum+",\"rows\":"+Documents.dc2json(dc,"")+"}";
	    jsonStr=RestUtil.formartResultJson("1", "", jsonStr);
	    
	    return jsonStr;
	    
	}
}