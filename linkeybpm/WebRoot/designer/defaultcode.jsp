<%@ page import="cn.linkey.factory.BeanCtx,cn.linkey.dao.Rdb,cn.linkey.util.Tools,cn.linkey.doc.Document,java.util.*" %>
<%@ page errorPage="../error.jsp"%>
<%@page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8"%> 
<% 
String checked="";
String docunid=BeanCtx.g("wf_docunid",true);
String sqlTableName="BPM_DevDefaultCode";
Document eldoc=null;
if(Tools.isBlank(docunid)){
	docunid=Rdb.getNewid(sqlTableName);
	eldoc=BeanCtx.getDocumentBean(sqlTableName);
	eldoc.s("WF_OrUnid",docunid);
}else{
	String sql="select * from "+sqlTableName+" where WF_OrUnid='"+docunid+"'";
	eldoc=Rdb.getDocumentBySql(sqlTableName,sql); //得到config文档数据
	if(eldoc.isNull()){
		BeanCtx.showErrorMsg(response,"Error : Could not find the config document! WF_OrUnid="+docunid);
		BeanCtx.close();
		return;
	}
}
%>
<!DOCTYPE html><html><head><title>Default Code Config</title>
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
function SaveDocument(Action){
	mask();
	var defaultCode=HTMLFRAEM_JsHeader.getContent();
	defaultCode=defaultCode.trim();
	$.post("../rule?wf_num=R_S001_B002",{WF_KeyFdName:'CodeType',WF_KeyFdName:'CodeType',WF_DocUnid:'<%=docunid%>',WF_Appid:'S001',WF_Version:$('#WF_Version').val(),WF_TableName:'BPM_DevDefaultCode',DefaultCode:defaultCode,Remark:$("#Remark").val(),CodeType:$("#CodeType").val(),Country:$("#Country").val()},function(data){
    	unmask();
    	if(data.Status=="error"){
        	$.messager.alert('Info',data.msg,'Error');
        }
  	});
}
</script>
<body  style="margin:1px;overflow:hidden"  >
<div style="padding:2px;/*#border:1px solid #ddd;background:#f4f4f4#*/">
	| <a href="#" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-save'" onclick="SaveDocument()"><%=BeanCtx.getMsg("Designer","Save")%></a>
    | <a href="#" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-reload'" onclick="location.reload();"><%=BeanCtx.getMsg("Designer","Refresh")%></a>
    | <a href="#" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-remove'" onclick="window.close();"><%=BeanCtx.getMsg("Designer","Close")%></a>
</div>
<textarea name="JsHeader" id="JsHeader" style="display:none" ><%
String htmlCode=eldoc.g("DefaultCode");
htmlCode=htmlCode.replace("<","&lt;");
htmlCode=htmlCode.replace(">","&gt;");
out.println(htmlCode);

String country=eldoc.g("Country");
if(Tools.isBlank(country)){
	country="CN";
}
String version=eldoc.g("WF_Version");
if(Tools.isBlank(version)){
	version="8.0";
}
%></textarea>
<table class="linkeytable" >
<tr>
<td>唯一id:</td><td><input name="CodeType" id="CodeType" value="<%=eldoc.g("CodeType")%>" size='40' ></td>
<td>说明:</td><td><input name="Remark" id="Remark" value="<%=eldoc.g("Remark")%>" size='40' ></td>
<td>国家:</td><td><input name="Country" id="Country" value="<%=country%>"></td>
<td>版本:</td><td><input name="WF_Version" id="WF_Version" value="<%=version%>"></td>
</tr>
</table>
<iframe src="../linkey/bpm/editor/ace/editor.html?mode=javascript&frmname=HTMLFRAEM_JsHeader&fdname=JsHeader" frameborder=0 width=99.5% height="600px" name="HTMLFRAEM_JsHeader" id="HTMLFRAEM_JsHeader" ></iframe>

</form></body></html>
<%BeanCtx.close();%>
