<%@ page import="cn.linkey.factory.BeanCtx,cn.linkey.dao.Rdb,cn.linkey.util.Tools,cn.linkey.doc.Document,java.util.*" %>
<%@page errorPage="../error.jsp"%>
<%@page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8"%> 

<%
String checked="";
String docunid=BeanCtx.g("wf_eldocunid",true);
String sqlTableName="BPM_GridControlList";
Document eldoc=null;
if(Tools.isBlank(docunid)){
	docunid=Rdb.getNewid(sqlTableName);
	eldoc=BeanCtx.getDocumentBean(sqlTableName);
	eldoc.s("WF_OrUnid",docunid);
}else{
	String sql="select * from "+sqlTableName+" where WF_OrUnid='"+docunid+"'";
	eldoc=Rdb.getDocumentBySql(sqlTableName,sql); //得到page文档数据
	String controlid=eldoc.g("Controlid"); //page编号
	if(eldoc.isNull()){
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
    alert("编辑控件不支持预览功能!");
}

function serializeForm(){
	//统一的序列化页面方法
	var formData=$("form").serialize();
	var checkBoxData=getNoCheckedBoxValue();
	if(checkBoxData!=""){
		formData+="&"+checkBoxData;
	}
	return formData;
}

$(function(){
	if($("#Controlid").val()==""){
		$("#Controlid").val("P_"+$('#WF_Appid').val()+"_0");
	}
})

$.extend($.fn.validatebox.defaults.rules, {
		elementid : {// 验证controlid有没有重复
			validator : function(v) {
				var appid=$("#WF_Appid").combobox('getValue');
				if(v.indexOf("V_"+appid+"_C")==-1){
					return false;
				}else{
					var vflag=false;
					$.ajax({
						type : "post", 
						url : "../rule?wf_num=R_S001_B005",
						data : {WF_OrUnid:'<%=docunid%>',WF_TableName:'BPM_GridControlList',WF_TableColName:'Controlid',WF_Elid:v}, 
          				async : false, 
          				success : function(data){vflag=data.Status;} 
          			}); 
          			return vflag;
				}
			},
			message : '无效的格式或编号已存在!'
		}
});

function serializeForm(){
	//统一的序列化页面方法
	var formData=$("form").serialize();
	var checkBoxData=getNoCheckedBoxValue();
	if(checkBoxData!=""){
		formData+="&"+checkBoxData;
	}
	return formData;
}

function SaveDocument(btnAction){
	$('#linkeyform').form({
    	url:'../rule?wf_num=R_S001_B002',
    	onSubmit: function(param){
    		setNoCheckedBoxValue(param);
    		param.WF_TableName="BPM_GridControlList";
    		param.WF_Action=btnAction;
    		var isValid = $(this).form('validate');
			if (isValid){
				var r=formonsubmit();
				if(r==false){
					return false;
				}else{
					mask();
				}
			}else{
				return false;
			}
    	},
    	success:function(data){
    		unmask();
        	var data = eval('(' + data + ')');
        	if(data.Status=="new"){
        		$("#dg",opener).datagrid('reload');
        		top.close();
        	}else if(data.Status=="error"){
        		$.messager.alert('Info',data.msg,'Error');
        	}
    	}
	});
	$('#linkeyform').submit();
}

</script>
</head>
<body style="margin:1px;overflow:hidden" >
<div id="eventwin"></div>
<form method='post' id="linkeyform" style="padding:5px" >
 <fieldset style="padding:10px,margin:10px" ><legend>基本属性</legend>
<table id="mytable" class="linkeytable" border="0" cellspacing="1" cellpadding="2" width="100%"><tbody>
<tr valign="top"><td valign="middle" width="15%" align="right">所属应用:</td><td width="85%">
<%=cn.linkey.app.AppUtil.getAppSelected(eldoc.g("WF_Appid"),false)%>
</td></tr>

<tr valign="top"><td valign="middle"  align="right">控件名称:</td>
<td ><input class="easyui-validatebox" required value="<%=eldoc.g("ControlName")%>" name="ControlName" size="60"/></td></tr>
<tr valign="top"><td valign="middle"  align="right">控件编号:</td>
<td ><input class="easyui-validatebox" value="<%=eldoc.g("Controlid")%>" name="Controlid" id="Controlid" required validType='elementid' size="60"/></td></tr>

<tr><td>
控件配置参数:
</td>
<td>
<%
String editorStr=eldoc.g("ControlConfig");
if(Tools.isBlank(editorStr)){
	editorStr="editor:'text'";
}
%>
<textarea style="width:90%;height:300px;" name="ControlConfig" id="ControlConfig"><%=editorStr%></textarea>
</td></tr>

<tr valign="top"><td valign="middle"  align="right">版本:</td>
<%
String version=eldoc.g("WF_Version");
if(Tools.isBlank(version)){version="8.0";}
%>
<td ><input value="<%=version%>" name="WF_Version" size="20" required="true" class="easyui-validatebox" /></td>
</tr>
<tr><td >选项:</td>
<td>
<% if(eldoc.g("UseJsCode").equals("1")){checked="checked";}else{checked="";} %>
<input class="lschk" name="UseJsCode" value="1" <%=checked%> type="checkbox"/>启用JS自定义控件

<% if(eldoc.g("WF_NoUpdate").equals("1")){checked="checked";}else{checked="";} %>
<input class="lschk" name="WF_NoUpdate" value="1" <%=checked%> type="checkbox"/>禁止升级本设计
</td>
</tr>

</tbody></table><br/>
<%
	BeanCtx.close();
%>
<!-- Hidden Field Begin--><div style='display:none'>
<input name='WF_DocUnid' id="WF_DocUnid" value="<%=docunid%>" ><!-- 页面的docunid-->
</div><!-- Hidden Field End-->
</fieldset></form></body></html>
