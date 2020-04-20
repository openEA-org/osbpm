<%@ page import="cn.linkey.factory.BeanCtx,cn.linkey.dao.Rdb,cn.linkey.util.Tools,cn.linkey.doc.Document,java.util.*" %>
<%@page errorPage="../error.jsp"%>
<%@page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8"%> 

<%
String checked="";
String gridNum="";
String docunid=BeanCtx.g("wf_eldocunid",true);
String sqlTableName="BPM_GridList";
Document eldoc=null;
if(Tools.isBlank(docunid)){
	docunid=Rdb.getNewid(sqlTableName);
	eldoc=BeanCtx.getDocumentBean(sqlTableName);
	eldoc.s("WF_OrUnid",docunid);
	eldoc.setIsNull();
}else{
	String sql="select * from "+sqlTableName+" where WF_OrUnid='"+docunid+"'";
	eldoc=Rdb.getDocumentBySql(sqlTableName,sql); //得到grid文档数据
	gridNum=eldoc.g("GridNum"); //grid编号
	if(eldoc.isNull()){
		BeanCtx.showErrorMsg(response,"Error : Could not find the grid! WF_OrUnid="+docunid);
		BeanCtx.close();
		return;
	}
}
%>
<!DOCTYPE html><html><head><title>Grid Attribute</title>
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
	if($("#GridNum").val()==""){
		$("#GridNum").val("V_"+$('#WF_Appid').val()+"_G");
	}
	if($("#DataSource").val()!=""){
		loadTableFdList($("#DataSource").val());
	}
	
})

function loadTableFdList(tableName){
	//重新载入sqltable字段列表
	$('#TreeField').combobox('reload', '../rule?wf_num=R_S001_B030&Dataid='+tableName);
	$('#IdField').combobox('reload', '../rule?wf_num=R_S001_B030&Dataid='+tableName);
	$('#SortName').combobox('reload', '../rule?wf_num=R_S001_B030&Dataid='+tableName);
}

$.extend($.fn.validatebox.defaults.rules, {
		elementid : {// 验证GridNum有没有重复
			validator : function(v) {
				var appid=$("#WF_Appid").combobox('getValue');
				if(v.indexOf("V_"+appid+"_T")==-1){
					return false;
				}else{
					var vflag=false;
					$.ajax({
						type : "post", 
						url : "../rule?wf_num=R_S001_B005",
						data : {WF_OrUnid:'<%=docunid%>',WF_TableName:'BPM_GridList',WF_TableColName:'GridNum',WF_Elid:v}, 
          				async : false, 
          				success : function(data){vflag=data.Status;} 
          			}); 
          			return vflag;
				}
			},
			message : 'Invalid format or duplicate number!'
		},
		DataSourceParams:{
			validator : function(v) {
				if(v.indexOf("'")==-1 && v.indexOf("\"")==-1){
					return true;
				}else{
					return false;
				}
			},
			message : '参数中不能包含单引号以及双引号!'
		}
});

function serializeForm(){
	//统一的序列化表单方法
	var formData=$("form").serialize();
	var checkBoxData=getNoCheckedBoxValue();
	if(checkBoxData!=""){
		formData+="&"+checkBoxData;
	}
	formData="WF_TableName=BPM_GridList&GridType=3&"+formData;
	return formData;
}

function SaveDocument(btnAction){
	mask();
	$('#linkeyform').form({
    	url:'../rule?wf_num=R_S001_B002',
    	onSubmit: function(param){
    		setNoCheckedBoxValue(param);
    		param.WF_TableName="BPM_GridList";
    		param.WF_Action=btnAction;
    		param.GridType="3";
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
    		try{
    			unmask();
        		var data = eval('(' + data + ')');
        		if(data.Status=="error"){
        			unmask();
        			alert(data.msg);
        		}
        	}catch(e){unmask();alert(data);}
    	}
	});
	$('#linkeyform').submit();
}

function PreviewView(){
	window.open("../treegrid?wf_num=<%=gridNum%>");
}
function openDevForm(){
	var formNum=$('#NewFormNum').combobox('getValue');
	if(formNum==""){alert("Please select a form!");return;}
	var url="editor.jsp?wf_dtype=1&wf_elid="+formNum;
	OpenUrl(url,100,100);
}
function NewForm(){
	$('#formwin').window({ width:600,height:260,modal:true,title:'新增表单'});
    $('#formwin').html("<iframe height='220px' width='100%' frameborder='0' src='../form?wf_num=F_S001_A005&WF_Appid="+$('#WF_Appid').val()+"'></iframe>");
}
function openDevRule(){
	var ruleNum=$('#EventRuleNum').combobox('getValue');
	if(ruleNum==""){alert("Please select a event!");return;}
	var url="editor.jsp?wf_dtype=4&wf_elid="+ruleNum;
	OpenUrl(url,50,50);
}
function NewEventRule(){
	$('#eventwin').window({ width:600,height:260,modal:true,title:'新增事件'});
    $('#eventwin').html("<iframe height='220px' width='100%' frameborder='0' src='../form?wf_num=F_S001_A009&EventType=2&WF_Appid="+$('#WF_Appid').val()+"'></iframe>");
}
function openJsonData(){
	var dataSource=$('#DataSource').combobox('getValue');
	if(dataSource==""){alert("Please select a datasource!");return;}
	var url="editor.jsp?wf_dtype=9&wf_elid="+dataSource;
	OpenUrl(url,50,50);
}
function NewJsonData(){
	$('#jsonwin').window({ width:600,height:260,modal:true,title:'新增Tree数据源'});
    $('#jsonwin').html("<iframe height='220px' width='100%' frameborder='0' src='../form?wf_num=F_S001_A019&WF_Appid="+$('#WF_Appid').val()+"'></iframe>");
}
</script>
</head>
<body style="margin:1px;overflow:hidden" >
<div id="eventwin"></div>
<div id="jsonwin"></div>
<div id="formwin"></div>
<form method='post' id="linkeyform" style="padding:5px" >
<fieldset style="padding:5px"><legend>视图基本属性</legend>
<table id="mytable" class="linkeytable" border="0" cellspacing="1" cellpadding="2" width="100%"><tbody>
<tr valign="top"><td valign="middle" width="21%" align="right">所属应用:</td><td width="79%">
<%=cn.linkey.app.AppUtil.getAppSelected(eldoc.g("WF_Appid"),false)%>
</td></tr>

<tr><td valign="middle" width="21%" align="right">Grid名称:</td>
<td width="79%"><input value="<%=eldoc.g("GridName")%>"  class="easyui-validatebox" required name="GridName" size="60"/></td>
</tr>

<tr><td valign="middle" width="21%" align="right">唯一编号:</td>
<td width="79%"><input value="<%=gridNum%>" name="GridNum" id="GridNum" class="easyui-validatebox" validType="elementid" required size="60"/></td>
</tr>

<tr valign="top"><td valign="middle" width="21%" align="right">绑定TreeData数据源:</td>
<td width="79%"><input value="<%=eldoc.g("DataSource")%>" name="DataSource" id="DataSource" required size="60" title="test" class="easyui-combobox" data-options="
                    url:'../rule?wf_num=R_S001_B029&appid=<%=eldoc.g("WF_Appid")%>',
                    method:'get',
                    valueField:'Dataid',
                    textField:'DataName',
                    onSelect:function(rc){loadTableFdList(rc.Dataid);}
            " />
            <a href="#" class="easyui-linkbutton"   onclick="openJsonData();return false;">打开</a>
            <a href="#" class="easyui-linkbutton"   onclick="NewJsonData();return false;">新建</a>
</td>
</tr>

<tr><td valign="middle" width="21%" align="right">绑定事件规则:</td>
<td width="79%">
<input value="<%=eldoc.g("EventRuleNum")%>" name="EventRuleNum" id="EventRuleNum"  size="60" class="easyui-combobox" data-options="
                    url:'../rule?wf_num=R_S001_B021&appid=<%=eldoc.g("WF_Appid")%>&EventType=2',
                    method:'get',
                    valueField:'RuleNum',
                    textField:'RuleName',
                    panelHeight:'auto',
                    formatter:function(r){if(r.RuleNum==''){return r.RuleName} return r.RuleName+'('+r.RuleNum+')';}
            "  /> 
 <a href="#" class="easyui-linkbutton"   onclick="openDevRule();return false;">打开</a>
 <a href="#" class="easyui-linkbutton"   onclick="NewEventRule();return false;">新建</a>

</td>
</tr>

<tr valign="top"><td valign="middle" width="21%" align="right">绑定数据表单:</td>
<td width="79%"><input value="<%=eldoc.g("NewFormNum")%>" name="NewFormNum" id="NewFormNum" size="60" class="easyui-combobox" data-options="
                    url:'../rule?wf_num=R_S001_B011&appid=<%=eldoc.g("WF_Appid")%>',
                    method:'get',
                    valueField:'FormNumber',
                    textField:'FormName',
                    groupField:'WF_Appid'
            " /> 
<a href="#" class="easyui-linkbutton"   onclick="openDevForm();return false;">打开</a>
<a href="#" class="easyui-linkbutton"   onclick="NewForm();return false;">新建</a>

</td>
</tr>

<tr><td valign="middle" width="21%" align="right">节点id字段(idField):</td>
<% 
String idField=eldoc.g("IdField");
if(Tools.isBlank(idField)){
	idField="id";
}
%>
<td width="79%"><input value="<%=idField%>" name="IdField" id="IdField" class="easyui-combobox" data-options="valueField:'FdName',textField:'FdName',data:{}"  size="30"/></td>
</tr>

<% 
String treeField=eldoc.g("TreeField");
if(Tools.isBlank(treeField)){
	treeField="text";
}
%>
<tr><td valign="middle" width="21%" align="right">节点名称字段(treeField):</td>
<td width="79%"><input value="<%=treeField%>" name="TreeField" id="TreeField" class="easyui-combobox" data-options="valueField:'FdName',textField:'FdName',data:{}"  size="30"/></td>
</tr>

<tr><td valign="middle" width="21%" align="right">显示时的排序字段:</td>
<td width="79%"><input value="<%=eldoc.g("SortName")%>" name="SortName" id="SortName" class="easyui-combobox" data-options="valueField:'FdName',textField:'FdName',data:{}"  size="30"/>
<% 
	if(eldoc.g("SortOrder").equals("1")){checked="checked";}else{checked="";} 
%>
<input class="lschk" name="SortOrder" value="1" <%=checked%> type="checkbox"/>DESC

<% if(eldoc.g("multiSort").equals("1")){checked="checked";}else{checked="";} %>
<input class="lschk" name="multiSort" value="1" <%=checked%> type="checkbox"/>允许多个字段排序  

</td>
</tr>

<tr><td valign="middle" width="21%" align="right">行双击事件:</td>
<% String rowDblClick=eldoc.g("RowDblClick");if(Tools.isBlank(rowDblClick)){rowDblClick="RowDblClick";}%>
<td width="79%"><input value="<%=rowDblClick%>" name="RowDblClick"  size="30"/></td>
</tr>

<tr><td valign="middle" width="21%" align="right">行单击事件:</td>
<% String rowClick=eldoc.g("RowClick");if(Tools.isBlank(rowClick)){rowClick="RowClick";} %>
<td width="79%"><input value="<%=rowClick%>" name="RowClick"  size="30"/></td>
</tr>

<tr><td valign="middle" width="21%" align="right">右键事件:</td>
<td width="79%"><input value="<%=eldoc.g("RowContextMenu")%>" name="RowContextMenu" id="RowContextMenu"  size="30"/></td>
</tr>

<tr><td width="21%">可选项:</td>
<td width="79%">
<% if(eldoc.g("RemoteSort").equals("1") || eldoc.isNewDoc()){checked="checked";}else{checked="";} %>
<input class="lschk" name="RemoteSort" value="1" <%=checked%> type="checkbox"/>远程排序

<% 	if(eldoc.g("ShowRowNumber").equals("1") || eldoc.isNewDoc()){checked="checked";}else{checked="";} %>
<input class="lschk" name="ShowRowNumber" value="1" <%=checked%> type="checkbox"/>显示行号

<% 	if(eldoc.g("ShowCheckBox").equals("1") || eldoc.isNewDoc()){checked="checked";}else{checked="";} %>
<input class="lschk" name="ShowCheckBox" value="1" <%=checked%> type="checkbox"/>显示复选框

<% 	if(eldoc.g("MutliSelect").equals("1") || eldoc.isNewDoc()){checked="checked";}else{checked="";} %>
<input class="lschk" name="MutliSelect" value="1" <%=checked%> type="checkbox"/>允许多选

<% 	if(eldoc.g("fitColumns").equals("1") || eldoc.isNewDoc() ){checked="checked";}else{checked="";} %>
<input class="lschk" name="fitColumns" value="1" <%=checked%> type="checkbox"/>平铺列

<% 	if(eldoc.g("isRollBack").equals("1") || eldoc.isNewDoc() ){checked="checked";}else{checked="";} %>
<input class="lschk" name="isRollBack" id="isRollBack" value="1" <%=checked%> type="checkbox"/>开启事务(针对视图文档的操作)

</td></tr>


<tr><td>默认传入数据源参数:</td><td>
<input value="<%=eldoc.g("DataSourceParams")%>" name="DataSourceParams" class="easyui-validatebox" validType='DataSourceParams'  title="格式为:fdName=1&fdName=2" size="60"  />
</td></tr>

<tr><td valign="middle" width="21%" align="right">data-options:</td>
<td width="79%"><input value="<%=eldoc.g("dataoptions")%>" name="dataoptions"  size="60"/></td>
</tr>

<tr valign="top"><td valign="middle" width="21%" align="right">Grid访问权限:</td>
<td width="79%"><input value="<%=eldoc.g("Roles")%>" name="Roles" size="60" class="easyui-combobox" data-options="
                    url:'../rule?wf_num=R_S006_B001&WF_Appid=<%=eldoc.g("WF_Appid")%>',
                    method:'get',
                    valueField:'RoleNumber',
                    textField:'RoleNumber',
                    multiple:true,
                    formatter: function(row){ var s = row.RoleName + '('+row.RoleNumber+')';return s;},
                    panelHeight:'auto'
            " >
</td></tr>

<tr valign="top"><td valign="middle" width="21%" align="right">版本:</td>
<%
String version=eldoc.g("WF_Version");
if(Tools.isBlank(version)){version="8.0";}
%>
<td width="79%"><input value="<%=version%>" name="WF_Version" size="20" required="true" class="easyui-validatebox" /></td>
</tr>

<tr valign="top"><td width="21%"></td>
<td width="79%">
<% 	if(eldoc.g("Status").equals("1") || eldoc.isNewDoc() ){checked="checked";}else{checked="";} %>
<input class="lschk" name="Status" id="Status" value="1" <%=checked%> type="checkbox"/>启用

<% 	if(eldoc.g("WF_CacheFlag").equals("1")){checked="checked";}else{checked="";} %>
<input class="lschk" name="WF_CacheFlag" id="WF_CacheFlag" value="1" <%=checked%> type="checkbox"/>缓存配置(针对访问量大的)

<% 	if(eldoc.g("WF_NoUpdate").equals("1")){checked="checked";}else{checked="";} %>
<input class="lschk" name="WF_NoUpdate" value="1" <%=checked%> type="checkbox"/>禁止升级本设计

</td></tr>

</tbody></table><br/>
<%
	BeanCtx.close();
%>
<!-- Hidden Field Begin--><div style='display:none'>
<input name='WF_DocUnid' id="WF_DocUnid" value="<%=docunid%>" ><!-- 表单的docunid-->
</div><!-- Hidden Field End-->
</fieldset></form></body></html>
