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
	$('#GroupField').combobox('reload', '../rule?wf_num=R_S001_B012&Dataid='+tableName);
	$('#SortName').combobox('reload', '../rule?wf_num=R_S001_B012&Dataid='+tableName);
}

$.extend($.fn.validatebox.defaults.rules, {
		elementid : {// 验证GridNum有没有重复
			validator : function(v) {
				var appid=$("#WF_Appid").combobox('getValue');
				if(v.indexOf("V_"+appid+"_E")==-1){
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
	formData="WF_TableName=BPM_GridList&GridType=2&"+formData;
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
    		param.GridType="2";
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
	window.open("../editorgrid?wf_num=<%=gridNum%>");
}
function openDevRule(){
	var ruleNum=$('#EventRuleNum').combobox('getValue');
	if(ruleNum==""){alert("Please select a event!");return;}
	//var url="rule_code.jsp?tablename=BPM_RuleList&tableelid=RuleNum&tableelname=RuleName&codetype=Rule_Code&wf_dtype=4&rulenum="+ruleNum;
	//parent.addtab(url,"视图事件","formevent");
	var url="editor.jsp?wf_dtype=4&wf_elid="+ruleNum+"&CodeType=EditorGrid_Event";
	OpenUrl(url,50,50);
}
function NewEventRule(){
	$('#eventwin').window({ width:600,height:260,modal:true,title:'新增事件'});
    $('#eventwin').html("<iframe height='220px' width='100%' frameborder='0' src='../form?wf_num=F_S001_A009&EventType=2&WF_Appid="+$('#WF_Appid').val()+"'></iframe>");
}
function openJsonData(){
	var dataSource=$('#DataSource').combobox('getValue');
	if(dataSource==""){alert("Please select a datasource!");return;}
	var url="editor.jsp?wf_dtype=2&wf_elid="+dataSource;
	OpenUrl(url,50,50);
}
function NewJsonData(){
	$('#jsonwin').window({ width:600,height:260,modal:true,title:'新增JSON数据源'});
    $('#jsonwin').html("<iframe height='220px' width='100%' frameborder='0' src='../form?wf_num=F_S001_A002&WF_Appid="+$('#WF_Appid').val()+"'></iframe>");
}
</script>
</head>
<body style="margin:1px;overflow:auto" >
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

<tr valign="top"><td valign="middle" width="21%" align="right">绑定数据源:</td>
<td width="79%"><input value="<%=eldoc.g("DataSource")%>" name="DataSource" id="DataSource" required size="60" title="test" class="easyui-combobox" data-options="
                    url:'../rule?wf_num=R_S001_B008&appid=<%=eldoc.g("WF_Appid")%>',
                    method:'get',
                    valueField:'Dataid',
                    textField:'DataName',
                    groupField:'WF_Appid',
                    formatter:function(r){return r.DataName+'('+r.Dataid+')';},
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
                    formatter:function(r){if(r.RuleNum==''){return r.RuleName} return r.RuleName+'('+r.RuleNum+')';}
            "  /> 
 <a href="#" class="easyui-linkbutton"   onclick="openDevRule();return false;">打开</a>
 <a href="#" class="easyui-linkbutton"   onclick="NewEventRule();return false;">新建</a>

</td>
</tr>

<tr><td valign="middle" width="21%" align="right">每页显示:</td>
<%
	String pageSize=Tools.isBlank(eldoc.g("PageSize")) ? "25" : eldoc.g("PageSize");
	String pageList=Tools.isBlank(eldoc.g("PageList")) ? "[20,25,30,40,60]" : eldoc.g("PageList");
%>
<td width="79%">
<input value="<%=pageSize%>" name="PageSize"  required="true" class="easyui-numberbox" size="5"/>

</td>
</tr>
<tr><td align="right">可选分页数:</td><td><input value="<%=pageList%>" name="PageList"  required="true"  size="30"/></td></tr>

<%-- <tr><td valign="middle" width="21%" align="right">显示时用此字段分组:</td>
<td width="79%"><input value="<%=eldoc.g("GroupField")%>" name="GroupField" id="GroupField" class="easyui-combobox" data-options="valueField:'FdName',textField:'FdName',data:{}"  size="30"/></td>
</tr> --%>

<tr><td valign="middle" width="21%" align="right">显示时的排序字段:</td>
<td width="79%"><input value="<%=eldoc.g("SortName")%>" name="SortName" id="SortName" class="easyui-combobox" data-options="valueField:'FdName',textField:'FdName',data:{}"  size="30"/>
<% 
	if(eldoc.g("SortOrder").equals("1")){checked="checked";}else{checked="";} 
%>
<input class="lschk" name="SortOrder" value="1" <%=checked%> type="checkbox"/>DESC

<%-- <% if(eldoc.g("multiSort").equals("1")){checked="checked";}else{checked="";} %>
<input class="lschk" name="multiSort" value="1" <%=checked%> type="checkbox"/>允许多个字段排序  --%> 

<% if(eldoc.g("RemoteSort").equals("1")){checked="checked";}else{checked="";} %>
<input class="lschk" name="RemoteSort" value="1" <%=checked%> type="checkbox"/>远程排序(允许多字段)  

</td>
</tr>

<tr><td valign="middle" width="21%" align="right">行双击事件:</td>
<% String rowDblClick=eldoc.g("RowDblClick");if(Tools.isBlank(rowDblClick)){rowDblClick="stopEditRow";}%>
<td width="79%"><input value="<%=rowDblClick%>" name="RowDblClick"  size="30"/></td>
</tr>

<tr><td valign="middle" width="21%" align="right">行单击事件:</td>
<% String rowClick=eldoc.g("RowClick");if(Tools.isBlank(rowClick)){rowClick="RowClick";} %>
<td width="79%"><input value="<%=rowClick%>" name="RowClick"  size="30"/></td>
<tr><td width="21%" align="right">风格:</td>
<%
String layStyle=eldoc.g("layStyle");
if(Tools.isBlank(layStyle)){layStyle="";}
%>
<td width="79%">
<input value="<%=eldoc.g("layStyle")%>" name="layStyle" id="layStyle" class="easyui-combobox" data-options="valueField:'value',textField:'text',data: [{text: '默认边框风格',value: ''},{text: '行边框风格',value: 'line'},{text: '列边框风格',value: 'row'},{text: '无边框风格',value: 'nob'}]"  size="15"/>
</td>

</tr>
<tr><td width="21%" align="right">尺寸:</td>
<%
String layStyleLG=eldoc.g("layStyleLG");
if(Tools.isBlank(layStyleLG)){layStyleLG="";}
%>
<td width="79%"><input value="<%=layStyleLG%>" name="layStyleLG" class="easyui-combobox" data-options="valueField:'value',textField:'text',data: [{text: '默认大小',value: ''},{text: '大尺寸',value: 'lg'},{text: '小尺寸',value: 'sm'}]" size="15"/></td>
</tr>

<%-- <tr><td valign="middle" width="21%" align="right">右键事件:</td>
<% String onRowContextMenu=eldoc.g("RowContextMenu");if(Tools.isBlank(onRowContextMenu)){onRowContextMenu="stopEditRow";} %>
<td width="79%"><input value="<%=onRowContextMenu%>" name="RowContextMenu" id="RowContextMenu"  size="30"/></td> --%>

<tr><td width="21%" align="right">无数据时提示:</td>
<%
String notRowData=eldoc.g("notRowData");
if(Tools.isBlank(notRowData)){notRowData="亲，没有数据哦！";}
%>
<td width="79%"><input value="<%=notRowData%>" name="notRowData" size="20" class="easyui-validatebox" /></td>
</tr>
<tr><td width="21%" >分页工具条:</td>
<td width="79%">
<% 	if(eldoc.g("ShowPageBar").equals("1") || eldoc.isNewDoc() ){checked="checked";}else{checked="";} %>
<input class="lschk" name="ShowPageBar" value="1" <%=checked%> type="checkbox"/>显示分页条

<% 	if(eldoc.g("ShowRowNumberFirst").equals("1") || eldoc.isNewDoc()){checked="checked";}else{checked="";} %>
<input class="lschk" name="ShowRowNumberFirst" value="1" <%=checked%> type="checkbox"/>显示首页

<% 	if(eldoc.g("ShowRowNumberEnd").equals("1") || eldoc.isNewDoc()){checked="checked";}else{checked="";} %>
<input class="lschk" name="ShowRowNumberEnd" value="1" <%=checked%> type="checkbox"/>显示尾页  

<% 	if(eldoc.g("openSkipPage").equals("1") || eldoc.isNewDoc()){checked="checked";}else{checked="";} %>
<input class="lschk" name="openSkipPage" value="1" <%=checked%> type="checkbox"/>开启跳页

&nbsp;&nbsp;&nbsp;&nbsp;初始页：<input value="1" name="initPageNum"  class="easyui-numberbox" size="3"/> 
&nbsp;&nbsp;&nbsp;&nbsp;显示<input value="3" name="showPageNumlist"  class="easyui-numberbox" size="3"/> 个连续页码

<!-- cellMinWidth: 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增 -->
</td>
</tr>


<tr><td width="21%">可选项:</td>
<td width="79%">
<%-- <% if(eldoc.g("RemoteSort").equals("1") || eldoc.isNewDoc()){checked="checked";}else{checked="";} %>
<input class="lschk" name="RemoteSort" value="1" <%=checked%> type="checkbox"/>远程排序 --%>

<% 	if(eldoc.g("ShowRowNumber").equals("1") || eldoc.isNewDoc()){checked="checked";}else{checked="";} %>
<input class="lschk" name="ShowRowNumber" value="1" <%=checked%> type="checkbox"/>显示行号

<% 	if(eldoc.g("ShowCheckBox").equals("1") || eldoc.isNewDoc()){checked="checked";}else{checked="";} %>
<input class="lschk" name="ShowCheckBox" value="1" <%=checked%> type="checkbox"/>显示复选框

<% 	if(eldoc.g("ShowCheckBoxSelect").equals("1") || eldoc.isNewDoc()){checked="checked";}else{checked="";} %>
<input class="lschk" name="ShowCheckBoxSelect" value="1" <%=checked%> type="checkbox"/>默认全选

<%-- <% 	if(eldoc.g("MutliSelect").equals("1") || eldoc.isNewDoc()){checked="checked";}else{checked="";} %>
<input class="lschk" name="MutliSelect" value="1" <%=checked%> type="checkbox"/>允许多选 --%>

<% 	if(eldoc.g("ShowSearchBar").equals("1") || eldoc.isNewDoc() ){checked="checked";}else{checked="";} %>
<input class="lschk" name="ShowSearchBar" value="1" <%=checked%> type="checkbox"/>显示搜索条

<% 	if(eldoc.g("striped").equals("1")){checked="checked";}else{checked="";} %>
<input class="lschk" name="striped" value="1" <%=checked%> type="checkbox"/>隔行背景

<% 	if(eldoc.g("groudBtn").equals("1")){checked="checked";}else{checked="";} %>
<input class="lschk" name="groudBtn" value="1" <%=checked%> type="checkbox"/>按钮组

<%-- <% 	if(eldoc.g("fitColumns").equals("1") || eldoc.isNewDoc() ){checked="checked";}else{checked="";} %>
<input class="lschk" name="fitColumns" value="1" <%=checked%> type="checkbox"/>平铺列 --%>

<%-- <% 	if(eldoc.g("border").equals("1")){checked="checked";}else{checked="";} %>
<input class="lschk" name="border" value="1" <%=checked%> type="checkbox"/>显示边框 --%>

<%-- <% 	if(eldoc.g("fit").equals("1") ){checked="checked";}else{checked="";} %>
<input class="lschk" name="fit" value="1" <%=checked%> type="checkbox"/>不平铺窗口 --%>

<% 	if(eldoc.g("notAutoHeight").equals("1")){checked="checked";}else{checked="";} %>
<input class="lschk" name="notAutoHeight" value="1" <%=checked%> type="checkbox"/>表格高度(固定值/full-差值)
<%
String fixedHeight=eldoc.g("fixedHeight");
if(Tools.isBlank(fixedHeight)){fixedHeight="";}
%>
<input value="<%=fixedHeight%>" name="fixedHeight" size="20" class="easyui-validatebox" />

<!-- cellMinWidth: 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增 -->
</td></tr>


<tr><td>默认传入数据源参数:</td><td>
<input value="<%=eldoc.g("DataSourceParams")%>" name="DataSourceParams" class="easyui-validatebox" validType='DataSourceParams'  title="格式为:fdName=1&fdName=2" size="60"  />
</td></tr>

<tr><td valign="middle" width="21%" align="right">lay-data:</td>
<td width="79%"><input value="<%=eldoc.g("dataoptions")%>" name="dataoptions"  size="60"/></td>
</tr>

<tr valign="top"><td valign="middle" width="21%" align="right">访问权限:</td>
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
if(Tools.isBlank(version)){version="10.0";}
%>
<td width="79%"><input value="<%=version%>" name="WF_Version" size="20" required="true" class="easyui-validatebox" /></td>
</tr>

<tr valign="top"><td width="21%"></td>
<td width="79%">
<% 	if(eldoc.g("Status").equals("1") || eldoc.isNewDoc() ){checked="checked";}else{checked="";} %>
<input class="lschk" name="Status" id="Status" value="1" <%=checked%> type="checkbox"/>启用

<% 	if(eldoc.g("isRollBack").equals("1") || eldoc.isNewDoc() ){checked="checked";}else{checked="";} %>
<input class="lschk" name="isRollBack" id="isRollBack" value="1" <%=checked%> type="checkbox"/>开启事务(针对视图数据操作)


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
