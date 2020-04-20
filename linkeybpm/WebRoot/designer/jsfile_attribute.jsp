<%@ page import="cn.linkey.factory.BeanCtx,cn.linkey.dao.Rdb,cn.linkey.util.Tools,cn.linkey.doc.Document,java.util.*" %>
<%@page errorPage="../error.jsp"%>
<%@page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8"%> 

<%
String checked="";
String docunid=BeanCtx.g("wf_eldocunid");
String sqlTableName="BPM_JavaScript";
Document fileDoc=null;
if(Tools.isBlank(docunid)){
	docunid=Rdb.getNewid(sqlTableName);
	fileDoc=BeanCtx.getDocumentBean(sqlTableName);
	fileDoc.s("WF_OrUnid",docunid);
}else{
	String sql="select * from "+sqlTableName+" where WF_OrUnid='"+docunid+"'";
	fileDoc=Rdb.getDocumentBySql(sqlTableName,sql); //得到page文档数据
	String fileName=fileDoc.g("FileName"); //文件名
	if(fileDoc.isNull()){
		BeanCtx.showErrorMsg(response,"Error : Could not find the page! WF_OrUnid="+docunid);
		BeanCtx.close();
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
    //页面打开时执行
    
}
function PreviewView(){
 alert("JS文件不支持预览!");
}
function formonsubmit(){
    //return false表示退出提交

}

function serializeForm(){
	//统一的序列化表单方法
	var formData=$("form").serialize();
	var checkBoxData=getNoCheckedBoxValue();
	if(checkBoxData!=""){
		formData+="&"+checkBoxData;
	}
	return formData;
}

</script>
</head>
<body style="margin:1px;overflow:hidden" >
<div id="eventwin"></div>
<form method='post' id="linkeyform" style="padding:5px" >
 <fieldset style="padding:10px,margin:10px" ><legend>基本属性</legend>
<table id="mytable" class="linkeytable" border="0" cellspacing="1" cellpadding="2" width="100%"><tbody>
<tr valign="top"><td valign="middle" width="21%" align="right">所属应用:</td><td width="79%">
<%=cn.linkey.app.AppUtil.getAppSelected(fileDoc.g("WF_Appid"),false)%>
</td></tr>
<tr valign="top"><td valign="middle" width="21%" align="right">文件说明:</td>
<td width="79%"><input value="<%=fileDoc.g("Subject")%>" name="Subject" id="Subject" size="60" class="easyui-validatebox" data-options="required:true" /></td></tr>
<tr valign="top"><td valign="middle" width="21%" align="right">文件名:</td>
<td width="79%"><input class="easyui-validatebox" required value="<%=fileDoc.g("FileName")%>" name="FileName" id="FileName" size="60"/></td></tr>
<tr valign="top"><td valign="middle" width="21%" align="right">路径:</td>
<td width="79%"><%
String filePath=BeanCtx.getAppPath() + "linkey/bpm/jscode/"+fileDoc.g("FileName");
filePath=filePath.replace("\\","/");
out.println(filePath);
%></td>
</tr>


<tr valign="top"><td valign="middle" width="21%" align="right">版本:</td>
<%
String version=fileDoc.g("WF_Version");
if(Tools.isBlank(version)){version="8.0";}
%>
<td width="79%"><input value="<%=version%>" name="WF_Version" size="20" required="true" class="easyui-validatebox" /></td>
</tr>

</tbody></table><br/>
<%
	BeanCtx.close();
%>
<!-- Hidden Field Begin--><div style='display:none'>
<input name='WF_DocUnid' id="WF_DocUnid" value="<%=docunid%>" ><!-- 页面的docunid-->
</div><!-- Hidden Field End-->
</fieldset></form></body></html>
