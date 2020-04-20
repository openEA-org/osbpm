<%@ page import="cn.linkey.factory.BeanCtx,cn.linkey.dao.Rdb,cn.linkey.util.Tools,cn.linkey.doc.Document,java.util.*" %>
<%@page errorPage="../error.jsp"%>
<%@page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8"%> 

<%
String sqlTableName="BPM_DataSourceList",checked="";
String docunid=BeanCtx.g("wf_eldocunid",true);
String sql="select * from "+sqlTableName+" where WF_OrUnid='"+docunid+"'";
Document eldoc=Rdb.getDocumentBySql(sqlTableName,sql); //得到data文档数据
String elid=eldoc.g("Dataid"); //id编号
if(eldoc.isNull()){
		BeanCtx.showErrorMsg(response,"Error : Could not find the json config! WF_OrUnid="+docunid);
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
	if($("#Jsonid").val()==""){
		$("#Jsonid").val("D_"+$('#WF_Appid').val()+"_J");
	}
	
	if($('#SqlTableName').val()!=""){
		loadTableFdList($('#SqlTableName').val())
	}
	
})

function loadTableFdList(tableName){
	//重新载入sqltable字段列表
	$('#ParentFdName').combobox('reload', '../rule?wf_num=R_S001_B010&WF_TableName='+tableName);
	$('#CurrentFdName').combobox('reload', '../rule?wf_num=R_S001_B010&WF_TableName='+tableName);
	$('#OrderFieldName').combobox('reload', '../rule?wf_num=R_S001_B010&WF_TableName='+tableName);
}

$.extend($.fn.validatebox.defaults.rules, {
		elementid : {// 验证jsonid有没有重复
			validator : function(v) {
				var appid=$("#WF_Appid").combobox('getValue');
				if(v.indexOf("D_"+appid+"_T")==-1){
					return false;
				}else{
					var vflag=false;
					$.ajax({
						type : "post", 
						url : "../rule?wf_num=R_S001_B005",
						data : {WF_OrUnid:'<%=docunid%>',WF_TableName:'BPM_DataSourceList',WF_TableColName:'Dataid',WF_Elid:v}, 
          				async : false, 
          				success : function(data){vflag=data.Status;} 
          			}); 
          			return vflag;
				}
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
	formData="WF_TableName=BPM_DataSourceList&"+formData;
	return formData;
}

function SaveDocument(btnAction){
	mask();
	$('#linkeyform').form({
    	url:'../rule?wf_num=R_S001_B002',
    	onSubmit: function(param){
    		setNoCheckedBoxValue(param);
    		param.WF_TableName="BPM_DataSourceList";
    		param.WF_Action=btnAction;
    		param.WF_KeyFdName="Dataid";
    		param.WF_DocUnid="<%=docunid%>";
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
	if($("#Dataid").val()==""){alert("Number can't be empty!");return;}
	window.open("../treedata?wf_num="+$("#Dataid").val());
}

$(function(){
	$("#UseJsonColumnFlag").click(function(e){
			$("#SelectColList").toggle();
		});
});

function openDevRule(){
	var ruleNum=$('#EventRuleNum').combobox('getValue');
	if(ruleNum==""){alert("Please select a event!");return;}
	var url="editor.jsp?wf_dtype=4&wf_elid="+ruleNum;
	OpenUrl(url,50,50);
}
function NewEventRule(){
	$('#eventwin').window({ width:600,height:260,modal:true,title:'新增事件'});
    $('#eventwin').html("<iframe height='220px' width='100%' frameborder='0' src='../form?wf_num=F_S001_A009&EventType=3&WF_Appid="+$('#WF_Appid').val()+"'></iframe>");
}
function getTableList(){
	//根据数据源重新获得数据库表 20180404
	var url = '../rule?wf_num=R_S001_B009&DataSourceid=' + $('#DataSourceid').val();
	$('#SqlTableName').combobox('reload',url);
}
</script>
</head>
<body style="margin:2px;overflow:hidden" >
<div id="eventwin"></div>

<form method='post' id="linkeyform" style="padding:5px" >
 <fieldset style="padding:5px" ><legend>数据源基本属性</legend>
<table id="mytable" class="linkeytable" border="0" cellspacing="1" cellpadding="2" width="100%"><tbody>
<tr valign="top"><td valign="middle" width="21%" align="right">所属应用:</td><td width="79%">
<%=cn.linkey.app.AppUtil.getAppSelected(eldoc.g("WF_Appid"),false)%>
</td></tr>

<tr><td valign="middle" width="21%" align="right">数据源名称:</td>
<td width="79%"><input value="<%=eldoc.g("DataName")%>"  class="easyui-validatebox" required name="DataName" size="60"/></td>
</tr>

<tr><td valign="middle" width="21%" align="right">唯一编号:</td>
<td width="79%"><input value="<%=elid%>" name="Dataid" id="Dataid" class="easyui-validatebox" validType='elementid' required size="60"/></td>
</tr>

<tr valign="top"><td valign="middle" width="21%" align="right">数据源:</td><td width="79%">
<select name="DataSourceid" id="DataSourceid" onchange="getTableList()">
<%
	String selectStr="";
	Document[] dc=Rdb.getAllDocumentsBySql("select * from BPM_DataTypeConfig");
	
	//20180404 修改读取数据时逻辑判断
	for(Document doc:dc){
		if(doc.g("DataSourceid").equals(eldoc.g("DataSourceid"))){
			selectStr="selected";
			out.println("<option value=\""+doc.g("DataSourceid")+"\" "+selectStr+" >"+doc.g("DataSourceName")+"("+doc.g("DataSourceid")+")</option>");
		}else{
			out.println("<option value=\""+doc.g("DataSourceid")+"\">"+doc.g("DataSourceName")+"("+doc.g("DataSourceid")+")</option>");
		}
	}
	if(selectStr.equals("")){
		out.println("<option value=\"default\" selected >默认数据源</option>");
	}else{
		out.println("<option value=\"default\" >默认数据源</option>");
	}
%>
</select>
</td></tr>

<tr valign="top"><td valign="middle" width="21%" align="right" nowrap >指定SQL表:</td>
<td width="79%"><input value="<%=eldoc.g("SqlTableName")%>" name="SqlTableName" id="SqlTableName" required size="60" class="easyui-combobox" data-options="
                    url:'../rule?wf_num=R_S001_B009',
                    method:'get',
                    valueField:'tableName',
                    textField:'tableName',
                    onSelect:function(rc){loadTableFdList(rc.tableName);}
            " />
</td>
</tr>

<tr><td valign="middle" width="21%" align="right">SQL Where条件:</td>
<td width="79%"><input value="<%=eldoc.g("SqlWhere")%>" name="SqlWhere" title="{Userid}可获得用户id{字段名}可获得url中传入的参数" size="90%"/>
<% if(eldoc.g("AutoRoleFlag").equals("1")){checked="checked";}else{checked="";} %>
<input type="checkbox"  <%=checked%> name="AutoRoleFlag" id="AutoRoleFlag" value="1" >自动匹配数据源权限
</td>
</tr>

<tr><td valign="middle" width="21%" align="right">指定数据列:</td>
<td width="79%">
<%
	String selectColList=eldoc.g("SelectColList");
	if(Tools.isBlank(selectColList)){
 		selectColList="*";
 	}
 	String styleStr="";
 	if(eldoc.g("UseJsonColumnFlag").equals("1") || eldoc.isNewDoc() ){
 		styleStr="display:none";
 	}
%>
<input value="<%=selectColList%>" name="SelectColList" id="SelectColList"  size="60" style="<%=styleStr%>" />

<%
	if( eldoc.g("UseJsonColumnFlag").equals("1") || eldoc.isNewDoc()){checked="checked";}else{checked="";} 
%>
<input name="UseJsonColumnFlag" id="UseJsonColumnFlag" value="1" type="checkbox" <%=checked%> >使用JSON列中的配置

</td>
</tr>

<tr><td valign="middle" width="21%" align="right">绑定事件规则:</td>
<td width="79%">
<input value="<%=eldoc.g("EventRuleNum")%>" name="EventRuleNum" id="EventRuleNum"  size="60" class="easyui-combobox" data-options="
                    url:'../rule?wf_num=R_S001_B021&appid=<%=eldoc.g("WF_Appid")%>&EventType=3',
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

<tr><td valign="middle" width="21%" align="right">上级文件夹编号对应字段名:</td>
<td width="79%">
<%
 String parentFdName=eldoc.g("ParentFdName");
 if(Tools.isBlank(parentFdName)){parentFdName="ParentFolderid";}
%>
<input value="<%=parentFdName%>" name="ParentFdName" id="ParentFdName" class="easyui-combobox" data-options="valueField:'ColumnName',textField:'ColumnName',data:{}"  size="30"/>
</tr>

<tr><td valign="middle" width="21%" align="right">当前文件夹编号对应字段名:</td>
<td width="79%">
<%
 String currentFdName=eldoc.g("CurrentFdName");
 if(Tools.isBlank(currentFdName)){currentFdName="Folderid";}
%>
<input value="<%=currentFdName%>" name="CurrentFdName" id="CurrentFdName" class="easyui-combobox" data-options="valueField:'ColumnName',textField:'ColumnName',data:{}"  size="30"/>
</tr>

<tr><td valign="middle" width="21%" align="right">默认排序字段:</td>
<td width="79%">
<input value="<%=eldoc.g("OrderFieldName")%>" name="OrderFieldName" id="OrderFieldName" class="easyui-combobox" data-options="valueField:'ColumnName',textField:'ColumnName',data:{}"  size="30"/>
 
<% if(eldoc.g("SqlDirection").equals("DESC")){checked="checked";}else{checked="";} %>
<input value="DESC" name="SqlDirection" type="checkbox" <%=checked%>  />DESC</td>
</tr>

<tr><td valign="middle" width="21%" align="right">数据过虑:</td>
<% if(eldoc.g("InReaders").equals("1")){checked="checked";}else{checked="";} %>
<td width="79%">
<input value="1" name="InReaders" type="checkbox"  <%=checked%>   />仅显示有权限的数据
</td>
</tr>

<tr><td valign="middle" width="21%" align="right">权限字段:</td>
<td width="79%">
<input value="<% if(Tools.isBlank(eldoc.g("AclFdName"))){out.println("WF_AllReaders");}else{out.println(eldoc.g("AclFdName"));}%>" name="AclFdName" id="AclFdName" size="30"   />
</td>
</tr>

<tr><td valign="middle" width="21%" align="right">选项:</td>
<td width="79%">
<% 
	if(eldoc.g("Async").equals("true")){checked="checked";}else{checked="";} 
%>
<input class="lschk" name="Async" id="Async" value="true" <%=checked%>  type="checkbox"/>异步加载
<% 
	if(eldoc.g("ShowRootFolder").equals("true")){checked="checked";}else{checked="";} 
%>
<input class="lschk" name="ShowRootFolder" id="ShowRootFolder" value="true" <%=checked%>  type="checkbox"/>默认显示根文件夹

</td>
</tr>

<tr valign="top"><td valign="middle" width="21%" align="right">绑定角色:</td>
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

<% 
	if(eldoc.g("WF_NoUpdate").equals("1")){checked="checked";}else{checked="";} 
%>
<input class="lschk" name="WF_NoUpdate" value="1" <%=checked%> type="checkbox"/>禁止升级本设计 

<% 
	if(eldoc.g("OutSql").equals("1")){checked="checked";}else{checked="";} 
%>
<input class="lschk" name="OutSql" value="1" <%=checked%> type="checkbox"/>调试输出SQL
</td></tr>


</tbody></table><br/>
<%
	BeanCtx.close();
%>
</fieldset></form></body></html>
