<%@ page import="cn.linkey.factory.BeanCtx,cn.linkey.dao.Rdb,cn.linkey.util.Tools,cn.linkey.doc.Document,java.util.*" %>
<%@ page errorPage="../error.jsp"%>
<%@page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8"%> 
<%
String docunid=BeanCtx.g("wf_eldocunid");
String tableName=BeanCtx.g("tablename"); //设计元素所在数据库表名
String tableElid=BeanCtx.g("tableelid"); //元素id的字段名
String tableElName=BeanCtx.g("tableelname"); //设计名称的字段名
String defaultCodeType=BeanCtx.g("codetype"); //默认代码类型
Document eldoc=null;
if(Tools.isBlank(docunid)){
	BeanCtx.close();
	BeanCtx.showErrorMsg(response,"Error : wf_eldocunid cannot be empty!");
	return;
}else{
	String sql="select * from "+tableName+" where WF_OrUnid='"+docunid+"'";
	eldoc=Rdb.getDocumentBySql(tableName,sql); //得到设计文档数据
	String elid=eldoc.g(tableElid); //elid编号
	if(eldoc.isNull()){
		BeanCtx.close();
		BeanCtx.showErrorMsg(response,"Error : Could not find the element!");
		return;
	}
}
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
function formonload(){
    //表单打开时执行
    
}

function formonsubmit(){
    //return false表示退出提交

}

function serializeForm(){
	//统一的序列化表单方法
	var jsHeader=HTMLFRAEM_JsHeader.getContent();
	jsHeader=encodeURIComponent(jsHeader.trim());
	jsHeader="JsHeader="+jsHeader;
	return jsHeader;
}

function SaveDocument(Action){
	mask();
	var fdName=GetUrlArg("FdName");
	if(fdName==""){fdName="JsHeader";}
	var jsStr=HTMLFRAEM_JsHeader.getContent();
	jsStr=jsStr.trim();
	$.post("../rule?wf_num=R_S001_B002",{WF_DocUnid:'<%=docunid%>',WF_TableName:'<%=tableName%>',WF_KeyFdName:'<%=tableElid%>',WF_RuleFdName:'<%=tableElName%>',JsHeader:jsStr},function(data){
    	unmask();
    	if(data.Status=="error"){
        	$.messager.alert('Info',data.msg,'Error');
        }
  	});
}
</script>
<body style="margin:1px;overflow:auto"  >
<div style="padding:2px;/*#border:1px solid #ddd;background:#f4f4f4#*/">
	| <a href="#" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-save'" onclick="SaveDocument()" title="仅保存当前JS代码">保存设计(Ctr+s)</a>
    | <a href="#" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-reload'" onclick="location.reload();" title="仅刷新当前JS代码" >刷新</a>
</div>
<form method='post' id="linkeyform" >
<textarea name="JsHeader" id="JsHeader" style="display:none" >
<% 
	String jsHeader=eldoc.g("JsHeader");
	if(Tools.isBlank(jsHeader)){
		jsHeader=Rdb.getValueBySql("select DefaultCode from BPM_DevDefaultCode where CodeType='"+defaultCodeType+"'");
	}
	jsHeader=jsHeader.replace("<","&lt;");
	jsHeader=jsHeader.replace(">","&gt;");
	out.print(jsHeader);	
	BeanCtx.close();
%></textarea>

<iframe src="../linkey/bpm/editor/ace/editor.html?mode=javascript&frmname=HTMLFRAEM_JsHeader&fdname=JsHeader" frameborder=0 width=99.5% height="600px" name="HTMLFRAEM_JsHeader" id="HTMLFRAEM_JsHeader" ></iframe>

</form></body></html>
