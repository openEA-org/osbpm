<%@ page import="cn.linkey.factory.BeanCtx,cn.linkey.dao.Rdb,cn.linkey.util.Tools,cn.linkey.doc.Document,java.util.*" %>
<%@page errorPage="../error.jsp"%>
<%@page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8"%> 

<%
String checked="";
String docunid=BeanCtx.g("wf_eldocunid");
String sqlTableName="BPM_FormList";
Document formDoc=null;
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
	var formData=$("form").serialize();
	var checkBoxData=getNoCheckedBoxValue();
	if(checkBoxData!=""){
		formData+="&"+checkBoxData;
	}
	return formData;
}

$(function(){
	if($("#FormNumber").val()==""){
		$("#FormNumber").val("F_"+$('#WF_Appid').val()+"_0");
	}
})

$.extend($.fn.validatebox.defaults.rules, {
		elementid : {// 验证formNumber有没有重复
			validator : function(v) {
				var appid=$("#WF_Appid").combobox('getValue');
				if(v.indexOf("F_"+appid+"_")==-1){
					return false;
				}else{
					var vflag=false;
					$.ajax({
						type : "post", 
						url : "../rule?wf_num=R_S001_B005",
						data : {WF_OrUnid:'<%=docunid%>',WF_TableName:'BPM_FormList',WF_TableColName:'FormNumber',WF_Elid:v}, 
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
	//统一的序列化表单方法
	var formData=$("form").serialize();
	var checkBoxData=getNoCheckedBoxValue();
	if(checkBoxData!=""){
		formData+="&"+checkBoxData;
	}
	return formData;
}

function SaveDocument(btnAction){
	$('#WF_Action').val(btnAction);
	$('#linkeyform').form({
    	url:'../rule?wf_num=R_S001_B002',
    	onSubmit: function(param){
    		setNoCheckedBoxValue(param);
    		param.WF_TableName="BPM_FormList";
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

function openDevRule(){
	var ruleNum=$('#EventRuleNum').combobox('getValue');
	if(ruleNum==""){alert("Please select a event!");return;}
	var url="editor.jsp?wf_dtype=4&wf_elid="+ruleNum;
	OpenUrl(url,50,50);
}
function NewEventRule(){
	$('#eventwin').window({ width:600,height:260,modal:true,title:'新增事件'});
    $('#eventwin').html("<iframe height='220px' width='100%' frameborder='0' src='../form?wf_num=F_S001_A009&WF_Appid="+$('#WF_Appid').val()+"'></iframe>");
}

</script>
</head>
<body style="margin:1px;overflow:hidden" >
<div id="eventwin"></div>
<form method='post' id="linkeyform" style="padding:5px" >
 <fieldset style="padding:10px,margin:10px" ><legend>表单基本属性</legend>
<table id="mytable" class="linkeytable" border="0" cellspacing="1" cellpadding="2" width="100%"><tbody>
<tr valign="top"><td valign="middle" width="21%" align="right">所属应用:</td><td width="79%">
<%=cn.linkey.app.AppUtil.getAppSelected(formDoc.g("WF_Appid"),false)%>
</td></tr>

<tr><td valign="middle" width="21%" align="right">所属分类:</td>
<td width="79%">
<input value="<%=formDoc.g("Folderid")%>" name="Folderid" id="Folderid"  size="60" class="easyui-combotree" data-options="
                    url:'../navtree?wf_num=T_S009_001',
                    method:'get',
                    valueField:'id',
                    textField:'text'
            "  />
</td>
</tr>

<tr valign="top"><td valign="middle" width="21%" align="right">表单名称:</td>
<td width="79%"><input class="easyui-validatebox" required value="<%=formDoc.g("FormName")%>" name="FormName" size="60"/></td></tr>
<tr valign="top"><td valign="middle" width="21%" align="right">表单编号:</td>
<td width="79%"><input class="easyui-validatebox" value="<%=formDoc.g("FormNumber")%>" name="FormNumber" id="FormNumber" required validType='elementid' size="60"/></td></tr>

<tr><td valign="middle" width="21%" align="right">绑定事件规则:</td>
<td width="79%">
<input value="<%=formDoc.g("EventRuleNum")%>" name="EventRuleNum" id="EventRuleNum"  size="60" class="easyui-combobox" data-options="
                    url:'../rule?wf_num=R_S001_B021&appid=<%=formDoc.g("WF_Appid")%>&EventType=1',
                    method:'get',
                    valueField:'RuleNum',
                    textField:'RuleName',
                    formatter:function(r){if(r.RuleNum==''){return r.RuleName} return r.RuleName+'('+r.RuleNum+')';}
            "  /> <a href="#" class="easyui-linkbutton"   onclick="openDevRule();return false;">打开</a>
            <a href="#" class="easyui-linkbutton"   onclick="NewEventRule();return false;">新建</a>

</td>
</tr>

<tr valign="top"><td valign="middle" width="21%" align="right">流程表头样式id:</td>
<%
String processformid=formDoc.g("EngineFormTopBar");
if(Tools.isBlank(processformid)){processformid="EngineFormTopBar";}
%>
<td width="79%"><input value="<%=processformid%>" name="EngineFormTopBar" size="60" required="true" class="easyui-validatebox" /></td>
</tr>

<tr valign="top"><td width="21%" align="right"></td><td width="79%">
<% if(formDoc.g("ShowMask").equals("1")){checked="checked";}else{checked="";} %>
<input class="lschk" name="ShowMask" value="1"  <%=checked%>  type="checkbox"/>打开时使用Mask效果

<% if(formDoc.g("NoEnCode").equals("1")){checked="checked";}else{checked="";} %>
<input <%=checked%>  class="lschk" name="NoEnCode" value="1" type="checkbox"/>存盘时不对数据进行编码
</td></tr>



<tr valign="top"><td valign="middle" width="21%" align="right">版本:</td>
<%
String version=formDoc.g("WF_Version");
if(Tools.isBlank(version)){version="8.0";}
%>
<td width="79%"><input value="<%=version%>" name="WF_Version" size="20" required="true" class="easyui-validatebox" /></td>
</tr>
<tr><td width="21%">状态:</td>
<td>
<% 	if(formDoc.g("Status").equals("1") || formDoc.isNewDoc()){checked="checked";}else{checked="";} %>
<input class="lschk" name="Status" value="1" <%=checked%> type="checkbox"/>启用
<% if(formDoc.g("WF_NoUpdate").equals("1")){checked="checked";}else{checked="";} %>
<input class="lschk" name="WF_NoUpdate" value="1" <%=checked%> type="checkbox"/>禁止升级本设计
<% if(formDoc.g("UseCodeMode").equals("1")){checked="checked";}else{checked="";} %>
<input class="lschk" name="UseCodeMode" value="1" <%=checked%> type="checkbox"/>默认启用纯代码模式
</td>
</tr>

</tbody></table><br/>
<%
	BeanCtx.close();
%>
<!-- Hidden Field Begin--><div style='display:none'>
<input name='WF_Action' id="WF_Action" value="" ><!-- 按扭动作id-->
<input name='WF_DocUnid' id="WF_DocUnid" value="<%=docunid%>" ><!-- 表单的docunid-->
</div><!-- Hidden Field End-->
</fieldset></form></body></html>
