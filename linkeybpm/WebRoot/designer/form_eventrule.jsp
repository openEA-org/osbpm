<%@ page import="cn.linkey.factory.BeanCtx,cn.linkey.dao.Rdb,cn.linkey.util.Tools,cn.linkey.doc.Document" %>
<%@page errorPage="../error.jsp"%>
<%@page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8"%>
<%
String docunid=BeanCtx.g("wf_docunid",true);
String sqlTableName="BPM_FormList";
String sql="select * from "+sqlTableName+" where WF_OrUnid='"+docunid+"'";
Document formDoc=Rdb.getDocumentBySql(sqlTableName,sql); //得到表单文档数据
String formNumber=formDoc.g("FormNumber"); //表单编号
if(formDoc.isNull()){
	BeanCtx.showErrorMsg(response,"Error : Could not find the form! WF_OrUnid="+docunid);
	BeanCtx.close();
	return;
}
sql="select RuleCode from BPM_RuleList where RuleNum='"+formNumber+"'"; //寻找规则中的代码
String ruleCode=Rdb.getValueBySql(sql); 
%>

<!DOCTYPE html><html><head><title>Form Attribute</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<!--insert-->
<%String designerHtmlHeader=Rdb.getValueBySql("select ConfigValue from BPM_SystemConfig where Configid='DesignerHtmlHeader'"); %>
<%=designerHtmlHeader%>
<!--insert end-->
<!--#<link rel="stylesheet" type="text/css" href="../linkey/bpm/easyui/themes/gray/easyui.css">#-->
<!--#<link rel="stylesheet" type="text/css" href="../linkey/bpm/easyui/themes/icon.css">#-->
<!--#<link rel="stylesheet" type="text/css" href="../linkey/bpm/css/app_openform.css">#-->
<script type="text/javascript" src="../linkey/bpm/easyui/jquery.min.js"></script>
<script type="text/javascript" src="../linkey/bpm/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript" src="../linkey/bpm/jscode/sharefunction.js"></script>
<script type="text/javascript" src="../linkey/bpm/jscode/app_openform.js"></script>
<script>
 function formonload(){}
function serializeForm(){
	//统一的序列化表单方法
	var ruleCode=HTMLFRAEM_RuleCode.getContent();
	ruleCode=encodeURIComponent(ruleCode.trim());
	var oRuleCode=encodeURIComponent($("#RuleCode").val().trim());
	if(ruleCode==oRuleCode){
		return "";
	}else{
		return "WF_KeyFdName=FormNumber&WF_RuleFdName=FormName&RuleCode="+ruleCode;
	}
}
function SaveDocument(Action){
	mask();
	var rulCode=HTMLFRAEM_RuleCode.getContent();
	ruleCode=rulCode.trim();
	$.post("../rule?wf_num=R009",{WF_DocUnid:'<%=docunid%>',WF_Action:Action,RuleCode:ruleCode},function(data){
    	unmask();
    	var data = eval('(' + data + ')');
    	if(data.Status=="error"){
        	$('#win').window({width:600,height:260,title:'Compile Error'});
    		$('#win').html("<div style='color:red'>"+decodeURIComponent(data.msg)+"</div>");
        }
  	});
}
function clearEvent(){
	mask();
	var rulCode=HTMLFRAEM_RuleCode.getContent();
	ruleCode=rulCode.trim();
	$.post("../rule?wf_num=R028",{WF_DocUnid:'<%=docunid%>',WF_TableName:'BPM_FormList',WF_Elid:'<%=formNumber%>'},function(data){
    	unmask();
    	var data = eval('(' + data + ')');
    	if(data.Status=="ok"){
        	alert(data.msg);
        	location.reload();
        }
  	});
}
</script>
<body  style="margin:1px;overflow:hidden"  >
<div id="win"></div>
<div style="padding:2px;/*#border:1px solid #ddd;background:#f4f4f4#*/">
	| <a href="#" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-save'" onclick="SaveDocument('btnSave')"><%=BeanCtx.getMsg("Designer","Save")%></a>
	| <a href="#" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-save'" onclick="SaveDocument('btnSaveAndCompile')"><%=BeanCtx.getMsg("Designer","SaveEventAndCompile")%></a>
	| <a href="#" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-remove'" onclick="clearEvent()"><%=BeanCtx.getMsg("Designer","Clear")%></a>
    | <a href="#" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-reload'" onclick="location.reload();"><%=BeanCtx.getMsg("Common","refresh")%></a>
</div>
<form method='post' id="linkeyform" >
<textarea name="RuleCode" id="RuleCode" style="display:none" >
<% 
	if(Tools.isBlank(ruleCode)){
		ruleCode=Rdb.getValueBySql("select DefaultCode from BPM_DevDefaultCode where CodeType='Form_Event'");
		ruleCode=ruleCode.replace("{ClassName}",formNumber);
	}
	ruleCode=ruleCode.replace("<","&lt;");
	ruleCode=ruleCode.replace(">","&gt;");
	out.println(ruleCode);	
%>
</textarea>
<iframe src="../linkey/bpm/editor/ace/editor.html?mode=java&frmname=HTMLFRAEM_RuleCode&fdname=RuleCode" frameborder=0 width=99.5% height="100%" name="HTMLFRAEM_RuleCode" id="HTMLFRAEM_RuleCode" ></iframe>

<%BeanCtx.close();%>


</form></body></html>
