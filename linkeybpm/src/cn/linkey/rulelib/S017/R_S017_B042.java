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
 * @RuleName:Engine_流程进度查询接口
 * @author  admin
 * @version: 8.0
 * @Created: 2016-10-09 14:50:52
 */
final public class R_S017_B042 implements LinkeyRule {
	@Override
	public String run(HashMap<String, Object> params) throws Exception  {
	    //params为运行本规则时所传入的参数
        //示例参数:{"Processid":"", "DocUnid":""}
		String jsonStr="";
		Boolean wsflag=true;
        String processid = (String) params.get("Processid");
        String docUnid = (String) params.get("DocUnid");
        if(Tools.isBlank(processid)){
        	processid=BeanCtx.g("Processid");
        	wsflag=false;
        }
        if(Tools.isBlank(docUnid)){
        	docUnid=BeanCtx.g("DocUnid");
        }
        if(Tools.isBlank(processid)){
        	jsonStr="{\"msg\":\"Processid error!\"}";
        }
        
        //读取节点列表
        jsonStr=GetAllNodeList(processid,docUnid);
        
        //只有通过http方式调用时才输要输出
        if(wsflag==false){
        	BeanCtx.p(jsonStr);
        }
        
	    return jsonStr;
	}
	
	//获得节点列表
    public String GetAllNodeList(String processid,String docUnid) {
        StringBuilder jsonStr = new StringBuilder();
        String sql = "select NodeName,Nodeid from BPM_AllModNodeList where Processid='" + Rdb.formatArg(processid) + "' and NodeType<>'Process' and NodeType<>'sequenceFlow' order by Nodeid";
        Document[] dc = Rdb.getAllDocumentsBySql(sql);
        for (Document doc : dc) {
        	if (jsonStr.length() > 0) {
                jsonStr.append(",");
            }
        	String nodeid=doc.g("Nodeid");
        	Document nodeDoc=null;
        	if(getDocumentStatus(docUnid).equals("ARC")){
        		//文档已经结束并归档
        		nodeDoc=getNodeStatusForArc(processid,docUnid,nodeid);
        	}else{
        		//还在流转中
        		nodeDoc=getNodeStatus(processid,docUnid,nodeid);
        	}
        	nodeDoc.copyAllItems(doc);
        	jsonStr.append(doc.toJson());
        }
        jsonStr.insert(0, "[");
        jsonStr.append("]");
        return jsonStr.toString();
    }

    //看文档是否已经归档
    public String getDocumentStatus(String docUnid){
    	String sql="select WF_Status form BPM_AllDocument where WF_OrUnid='"+Rdb.formatArg(docUnid)+"'";
    	String status=Rdb.getValueBySql(sql);
    	return status;
    }
    
    //获得已归档的节点当前状态
    public Document getNodeStatusForArc(String processid,String docUnid,String nodeid){
    	//从流转记录中得到节点的最后处理人,如果一个节点有多次审批时只能输出最后一次的审批记录
    	String sql="select StartTime,EndTime,UserName from BPM_AllRemarkList where DocUnid='"+Rdb.formatArg(docUnid)+"' and Nodeid='"+Rdb.formatArg(nodeid)+"' order by StartTime DESC";
    	Document doc=Rdb.getDocumentBySql(sql);
    	if(!doc.isNull()){
    		doc.s("Status", "End");
    	}
    	return doc;
    }
    
    //获得还在审批中的节点的当前状态
    public Document getNodeStatus(String processid,String docUnid,String nodeid){
    	//从节点实例表中得到节点状态
    	String sql="select StartTime,EndTime,Status from BPM_InsNodeList where DocUnid='"+Rdb.formatArg(docUnid)+"' and Nodeid='"+Rdb.formatArg(nodeid)+"' order by StartTime DESC";
    	Document doc=Rdb.getDocumentBySql(sql);
    	if(!doc.isNull()){
    		String userList=approvalUser(processid,docUnid,nodeid);
    		doc.s("UserList", userList);
    	}
    	return doc;
    }
    
    //获得还在审批中的节点的处理人
    public String approvalUser(String processid,String docUnid,String nodeid){
    	String userList="";
    	String sql="select Userid,Status from BPM_InsUserList where DocUnid='"+Rdb.formatArg(docUnid)+"' and Nodeid='"+Rdb.formatArg(nodeid)+"'";
    	Document[] dc=Rdb.getAllDocumentsBySql(sql);
    	for(Document doc:dc){
    		String userName=BeanCtx.getLinkeyUser().getCnName(doc.g("Userid"));
    		if(Tools.isBlank(userList)){
    			userList=userName;
    		}else{
    			userList+=","+userName;
    		}
    	}
    	return userList;
    }
    
}