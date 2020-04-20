<%@ page import="cn.linkey.factory.BeanCtx,cn.linkey.dao.Rdb,cn.linkey.util.Tools,cn.linkey.doc.Document,java.util.*" %>
<%@page errorPage="../error.jsp"%>
<%@page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8"%> 

<%
String docunid=BeanCtx.g("wf_eldocunid");
String sqlTableName="BPM_FormList";
Document formDoc=null;
String appFormHtmlHeader="AppFormHtmlHeader";
if(Tools.isBlank(docunid)){
	docunid=Rdb.getNewid("BPM_FormList");
	formDoc=BeanCtx.getDocumentBean("BPM_FormList");
	formDoc.s("WF_OrUnid",docunid);
}else{
	String sql="select * from "+sqlTableName+" where WF_OrUnid='"+docunid+"'";
	formDoc=Rdb.getDocumentBySql(sqlTableName,sql); //得到表单文档数据
	String formNumber=formDoc.g("FormNumber"); //表单编号
	if(formDoc.isNull()){
		BeanCtx.showErrorMsg(response,"Error : Could not find the form! WF_OrUnid="+docunid);
		BeanCtx.close();
		return;
	}else{
		appFormHtmlHeader=formDoc.g("HeaderConfigid");
		if(Tools.isBlank(appFormHtmlHeader)){appFormHtmlHeader="AppFormHtmlHeader";}
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

function SaveDocument(btnAction){
	mask();
	$('#WF_Action').val(btnAction);
	$('#linkeyform').form({
    	url:'../rule?wf_num=R_S001_B002',
    	onSubmit: function(param){
    		param.WF_TableName="BPM_FormList";
    		var isValid = $(this).form('validate');
			if (isValid){
				var r=formonsubmit();
				if(r==false){
					unmask();
					return false;
				}
			}else{
				unmask();
				return false;
			}
    	},
    	success:function(data){
        	var data = eval('(' + data + ')');
        	if(data.Status=="ok"){
        		 unmask();
        	}else if(data.Status=="new"){
        		top.location.replace("index.jsp?wf_docunid="+g("WF_DocUnid"));
        	}else if(data.Status=="error"){
        		$.messager.alert('Info',data.msg,'Error');
        	}
    	}
	});
	$('#linkeyform').submit();
}

</script>
</head>
<body style="margin:2px;overflow:hidden" >
<form method='post' id="linkeyform" >
<fieldset style="padding:10px,margin:10px" ><legend>Html Header</legend>
<table id="mytable" class="linkeytable" border="0" cellspacing="1" cellpadding="2" width="100%"><tbody>

<tr valign="top">
<td valign="middle" width="21%" align="right">Header Config id:</td>
<td width="79%">
<input name="HeaderConfigid" id="HeaderConfigid" value="<%=appFormHtmlHeader%>" class="easyui-validatebox" required  size="60" >
</td>

<tr valign="top">
<td valign="middle" width="21%" align="right">Body Tag Config:</td>
<td width="79%">
<%
String bodyTagConfig=formDoc.g("BodyTag");
if(Tools.isBlank(bodyTagConfig)){
	 bodyTagConfig="style=\"margin:0px 5px 0px 5px;\"  scroll=\"auto\"";
}
%>
<input name="BodyTag" id="BodyTag" value='<%=bodyTagConfig%>' style="width:750px;" >
</td>

<tr valign="top">
<td valign="middle" width="21%" align="right">Header Content:</td>
<td width="79%">
<textarea name="HtmlHeader" style="width:750px;height:400px;"/><%=formDoc.g("HtmlHeader")%></textarea></td>
</tr>

</tbody></table><br/>
<%
	BeanCtx.close();
%>
</fieldset></form></body></html>
