<%@ page import="cn.linkey.factory.BeanCtx,cn.linkey.dao.Rdb,cn.linkey.util.Tools,cn.linkey.doc.Document,java.util.*" %>
<%@page errorPage="../error.jsp"%>
<%@page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8"%> 

<%
String docunid=BeanCtx.g("wf_eldocunid");
Document eldoc=null;
String DataSource=""; //数据源id或url
if(Tools.isBlank(docunid)){
		BeanCtx.showErrorMsg(response,"Error : Could not find the grid! WF_OrUnid="+docunid);
		BeanCtx.close();
		return;
}else{
	String sql="select * from BPM_GridList where WF_OrUnid='"+docunid+"'";
	eldoc=Rdb.getDocumentBySql(sql); //得到grid文档数据
	if(eldoc.isNull()){
		BeanCtx.showErrorMsg(response,"Error : Could not find the grid! WF_OrUnid="+docunid);
		BeanCtx.close();
		return;
	}
	DataSource=eldoc.g("DataSource");
}
%>
<!DOCTYPE html><html><head><title>Form Toolbar</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<!--insert-->
<%String designerHtmlHeader=Rdb.getValueBySql("select ConfigValue from BPM_SystemConfig where Configid='DesignerHtmlHeader'"); %>
<%=designerHtmlHeader%>
<!--insert end-->
<!--#<link rel="stylesheet" type="text/css" href="../linkey/bpm/easyui/themes/gray/easyui.css">#-->
<!--#<link rel="stylesheet" type="text/css" href="../linkey/bpm/easyui/themes/icon.css">#-->
<!--#<link rel="stylesheet" type="text/css" href="../linkey/bpm/css/app_grid.css">#-->
<script type="text/javascript" src="../linkey/bpm/easyui/jquery.min.js"></script>
<script type="text/javascript" src="../linkey/bpm/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript" src="../linkey/bpm/jscode/sharefunction.js"></script>
<script type="text/javascript" src="../linkey/bpm/jscode/app_openform.js"></script>
<script>
function formonload(){}
function insertRow(){
	stopEditRow();
	$('#dg').datagrid('insertRow',{index:1,row:{"FdName":"","RowNum":"1","Remark":"","ColWidth":"100","Sort":"Y","Mobile":"N","SortNum":"11"}});
}

function removeSelectedRow(){
	stopEditRow();
    var rows = $('#dg').datagrid('getSelections');
    if (!rows || rows.length == 0) {
        $.messager.alert('提示', '请选择要删除的数据!', 'info');
        return;
    }
    
	$.messager.confirm('Confirm','您确定要删除选中字段吗?',function(r){if (r){
		var dg=$('#dg');
		$.each(rows, function (i, row) {
        	var index=dg.datagrid('getRowIndex',row);
        	dg.datagrid('deleteRow',index);
    	});
	}});
}

var lastIndex;
function editRow(rowIndex){
	$("#dg").datagrid('unselectAll');
	$("#dg").datagrid('selectRow',rowIndex);
	if (lastIndex != rowIndex){
		$('#dg').datagrid('endEdit', lastIndex);
		$('#dg').datagrid('beginEdit', rowIndex);
	}
	lastIndex = rowIndex;
}

function stopEditRow(){
	$('#dg').datagrid('endEdit', lastIndex);
	lastIndex=undefined;
}

//从数据源中载入数据源的字段列表
function loadColFromJsonData(){
	stopEditRow();
	mask();
	var dataSource=parent.window.frames[0].$('#DataSource').combobox('getValue');
	if(dataSource==""){dataSource="<%=DataSource%>";}
	if(dataSource==""){alert("Please save the grid property before you perform this operation!");unmask();return;}
	var url="../rule?wf_num=R_S001_B012&Dataid="+dataSource;
	var data=$("#dg").datagrid('getData');
	$.get(url,function(r){
		r=eval(r);
		for(var i=0;i<r.length;i++){
			var JsonName=r[i].JsonName;
			var FdName=r[i].FdName;
			if(JsonName==""){JsonName=FdName;}
			var hflag=false;
			for(var j=0;j<data.rows.length;j++){
				if(data.rows[j].FdName==JsonName){
					hflag=true;
				}
			}
			if(hflag==false && JsonName!="WF_OrUnid"){
				$('#dg').datagrid('appendRow',{"FdName":JsonName,"ColName":r[i].Remark,"ColWidth":"100","RowNum":"1","Sort":"Y","Mobile":"N","SortNum":i+20});
			}
		}
		unmask();
	});
}

function clickRow(index){
	$("#dg").datagrid('unselectAll');
	$("#dg").datagrid('selectRow',index);
	stopEditRow();
}

function formonsubmit(){}

function serializeForm(){
	//统一的序列化表单方法
	stopEditRow();
	return "ColumnConfig="+encodeURIComponent(JSON.stringify($("#dg").datagrid('getData')));
}

$(function(){
	$.extend($.fn.datagrid.defaults.editors, {
		SelectLabel: { //选择标签
			init: function(container, options){
				var obj = $('<input type="text" id="label" style="width:80%" >').appendTo(container);
				var img=$('<a href="#" onclick="OpenUrl(\'../r?wf_num=V_S001_E016&targetName=label&WF_Appid=<%=eldoc.g("WF_Appid")%>\',600,300)";return false;>标签</a>').appendTo(container);
				return obj;
			},
			destroy: function(target){
				$(target).remove();
			},
			getValue: function(target){
				return $(target).val();
			},
			setValue: function(target, value){
				$(target).val(value);
			},
			resize: function(target, width){
				//$(target)._outerWidth(width);
			}
		}
	});
});
</script>
</head>
<body style="margin:1px;overflow:auto"  >
<div id="mm" style="padding:2px;/*#border:1px solid #ddd;background:#f4f4f4#*/" >
	| <a href="#" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-add'" onclick="insertRow()" >新增列</a>
	| <a href="#" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-remove'" onclick="removeSelectedRow()" >删除选中列</a>
	| <a href="#" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-cmp'" onclick="loadColFromJsonData()" >从数据源中载入</a>
    | <a href="#" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-reload'" onclick="location.reload();">刷新</a>
</div>

<form method='post' id="linkeyform"  style="display:none">
<input name='WF_DocUnid' id="WF_DocUnid" value="<%=docunid%>" ><!-- json docunid-->
<textarea name="ColumnConfig" id="ColumnConfig" >
<%
String columnConfig=eldoc.g("ColumnConfig");
if(Tools.isBlank(columnConfig)){
	columnConfig=Rdb.getValueBySql("select DefaultCode from BPM_DevDefaultCode where CodeType='"+BeanCtx.g("codetype")+"InitVal'");
}
out.println(columnConfig);
%>
</textarea>
</form>
<script>
var jsonStr=$("#ColumnConfig").val().trim();
var colJson=eval('('+jsonStr+')');
var dataSource=parent.window.frames[0].$('#DataSource').combobox('getValue');
if(dataSource==""){dataSource="<%=DataSource%>";}
var fdListUrl="../rule?wf_num=R_S001_B012&Dataid="+dataSource;
</script>
<%
	String code=Rdb.getValueBySql("select DefaultCode from BPM_DevDefaultCode where CodeType='"+BeanCtx.g("codetype")+"'");
	out.println(code);
%>
</body></html>
<%
	BeanCtx.close();
%>
