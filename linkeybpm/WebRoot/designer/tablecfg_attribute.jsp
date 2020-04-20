<%@ page import="cn.linkey.factory.BeanCtx,cn.linkey.dao.Rdb,cn.linkey.util.Tools,cn.linkey.doc.Document,java.util.*" %>
<%@page errorPage="../error.jsp"%>
<%@page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8"%> 

<%
String sqlTableName="BPM_TableConfig",checked="";
String docunid=BeanCtx.g("wf_eldocunid");
String sql="select * from "+sqlTableName+" where WF_OrUnid='"+docunid+"'";
Document eldoc=Rdb.getDocumentBySql(sqlTableName,sql); //得到文档数据
String elid=eldoc.g("TableName"); //id编号
if(eldoc.isNull()){
		BeanCtx.showErrorMsg(response,"Error : Could not find the table config! WF_OrUnid="+docunid);
		BeanCtx.close();
		return;
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

$(function(){	
	if($('#SqlTableName').val()!=""){
	    var url="../rule?wf_num=R_S001_B010&DataSourceid="+$("#DataSourceid").val()+"&WF_TableName="+$('#SqlTableName').val();
		$('#OrderFieldName').combobox('reload', url);
		$('#DefaultSearchField').combobox('reload', url);
	}
})

$.extend($.fn.validatebox.defaults.rules, {
		elementid : {// 验证dataid有没有重复
			validator : function(v) {
					var vflag=false;
					 if(!/^[a-zA-Z0-9_]*$/g.test(v)){
                        return false;
                    }
					$.ajax({
						type : "post", 
						url : "../rule?wf_num=R_S001_B005",
						data : {WF_OrUnid:'<%=docunid%>',WF_TableName:'BPM_TableConfig',WF_TableColName:'TableName',WF_Elid:v}, 
          				async : false, 
          				success : function(data){vflag=data.Status;} 
          			}); 
          			return vflag;
			},
			message : 'Invalid format or duplicate number!'
		}
});

function serializeForm(){
	//统一的序列化表单方法
	var formData=$("form").serialize();
	var checkBoxData=getNoCheckedBoxValue();
	if(checkBoxData!=""){
		formData+="&"+checkBoxData;
	}
	formData="WF_TableName=BPM_TableConfig&"+formData;
	return formData;
}

function PreviewView(){
	alert("本配置不支持预览!");
}

</script>
</head>
<body style="margin:2px;overflow:hidden" >
<div id="eventwin"></div>

<form method='post' id="linkeyform" style="padding:5px" >
 <fieldset style="padding:5px" ><legend>数据库表基本属性</legend>
<table id="mytable" class="linkeytable" border="0" cellspacing="1" cellpadding="2" width="100%"><tbody>
<tr valign="top"><td valign="middle" width="21%" align="right">所属应用:</td><td width="79%">
<%=cn.linkey.app.AppUtil.getAppSelected(eldoc.g("WF_Appid"),false)%>
</td></tr>

<tr><td valign="middle" width="21%" align="right">数据库表说明:</td>
<td width="79%"><input value="<%=eldoc.g("TableRemark")%>"  class="easyui-validatebox" required name="TableRemark" id="TableRemark" size="60"/></td>
</tr>

<tr><td valign="middle" width="21%" align="right">数据库表名:</td>
<td width="79%"><input value="<%=elid%>" name="TableName" id="TableName" class="easyui-validatebox" validType='elementid' required size="60"/></td>
</tr>

<tr valign="top"><td valign="middle" width="21%" align="right">类型:</td>
<td width="79%">
<%
	if(eldoc.g("TableType").equals("1")){
		out.println("<input name='TableType' id='TableType' value='1' type='radio' checked>表");
		out.println("<input name='TableType' id='TableType' value='2' type='radio'  >视图");
	}else{
		out.println("<input name='TableType' id='TableType' value='1' type='radio' >表");
		out.println("<input name='TableType' id='TableType' value='2' type='radio'  checked>视图");
	}
%>
</td>
</tr>

<tr valign="top"><td width="21%"></td>
<td width="79%">
<% 	
	if(eldoc.g("InitData").equals("1")){checked="checked";}else{checked="";}
 %>
<input class="lschk" name="InitData" value="1" <%=checked%> type="checkbox"/>应用打包时含数据
<% 	
	if(eldoc.g("CreateRdbTable").equals("1")){checked="checked";}else{checked="";}
 %>
<input class="lschk" name="CreateRdbTable" value="1" <%=checked%> type="checkbox"/>应用安装时创建实体表(注意:如果实体表不存在时不能勾选打包时含数据)
</td></tr>

<tr valign="top"><td valign="middle" width="21%" align="right">版本:</td>
<td width="79%"><input value="<%=eldoc.g("WF_Version")%>" name="WF_Version" id="WF_Version" class="easyui-validatebox" required size="10"  />
</td>
</tr>

</tbody></table><br/>
<%
	BeanCtx.close();
%>
<!-- Hidden Field Begin--><div style='display:none'>
<input name='WF_DocUnid' id="WF_DocUnid" value="<%=docunid%>" ><!-- 表单的docunid-->
</div><!-- Hidden Field End-->
</fieldset></form></body></html>
