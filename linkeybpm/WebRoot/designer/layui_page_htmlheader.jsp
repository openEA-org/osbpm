<%@ page import="cn.linkey.factory.BeanCtx,cn.linkey.dao.Rdb,cn.linkey.util.Tools,cn.linkey.doc.Document,java.util.*" %>
<%@page errorPage="../error.jsp"%>
<%@page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8"%> 

<%
String docunid=BeanCtx.g("wf_eldocunid");
String sqlTableName="BPM_PageList";
Document formDoc=null;
String appFormHtmlHeader="AppPageHtmlHeader";
if(Tools.isBlank(docunid)){
	docunid=Rdb.getNewid("BPM_PageList");
	formDoc=BeanCtx.getDocumentBean("BPM_PageList");
	formDoc.s("WF_OrUnid",docunid);
}else{
	String sql="select * from "+sqlTableName+" where WF_OrUnid='"+docunid+"'";
	formDoc=Rdb.getDocumentBySql(sqlTableName,sql); //得到表单文档数据
	String pageNum=formDoc.g("PageNum"); //表单编号
	if(formDoc.isNull()){
		BeanCtx.showErrorMsg(response,"Error : Could not find the form! WF_OrUnid="+docunid);
		BeanCtx.close();
		return;
	}else{
		appFormHtmlHeader=formDoc.g("HeaderConfigid");
		if(Tools.isBlank(appFormHtmlHeader)){appFormHtmlHeader="AppPageHtmlHeader";}
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
$.extend($.fn.validatebox.defaults.rules, {
    minLength: {
        validator: function(value, param){ return value.length >= param[0]; },
         message: 'Please enter at least {0} characters.'
    }
});

function serializeForm(){
	//统一的序列化表单方法
	var formData=$("form").serialize();
	return formData;
}

</script>
</head>
<body style="margin:2px;overflow:hidden" >
<form method='post' id="linkeyform" >
<fieldset style="padding:10px,margin:10px" ><legend>Html Header</legend>
<table id="mytable" class="linkeytable" border="0" cellspacing="1" cellpadding="2" width="100%"><tbody>

<tr valign="top">
<td valign="middle" width="15%" align="right">Header Config id:</td>
<td width="85%">
<input name="HeaderConfigid" id="HeaderConfigid" value="<%=appFormHtmlHeader%>" class="easyui-validatebox" required  size="60" >
</td>

<tr valign="top">
<td valign="middle" width="15%" align="right" nowrap >Body Tag Config:</td>
<td width="85%">
<%
String bodyTagConfig=formDoc.g("BodyTag");
if(Tools.isBlank(bodyTagConfig)){
	 bodyTagConfig="class=\"layui-layout-body\" style=\"margin:0px;\"  scroll=\"auto\"";
}
bodyTagConfig=bodyTagConfig.replace("'","\"");
%>
<input name="BodyTag" id="BodyTag" value='<%=bodyTagConfig%>' style="width:90%;" >
</td>

<tr valign="top">
<td valign="middle" width="15%" align="right">Header Content:</td>
<td width="85%">
<textarea name="HtmlHeader" style="width:90%;height:400px;"/><%=formDoc.g("HtmlHeader")%></textarea></td>
</tr>

</tbody></table><br/>
<%
	BeanCtx.close();
%>
</fieldset></form></body></html>
