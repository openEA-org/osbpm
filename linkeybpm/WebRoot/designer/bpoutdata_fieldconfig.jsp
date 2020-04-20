<%@ page import="cn.linkey.factory.BeanCtx,cn.linkey.dao.Rdb,cn.linkey.util.Tools,cn.linkey.doc.Document,java.util.*" %>
<%@page errorPage="../error.jsp"%>
<%@page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8"%> 

<%
String sqlTableName="BPM_ProcessMapData";
String docunid=BeanCtx.g("wf_eldocunid");
String sql="select * from "+sqlTableName+" where WF_OrUnid='"+docunid+"'";
Document eldoc=Rdb.getDocumentBySql(sqlTableName,sql); //得到文档数据
String elid=eldoc.g("Dataid"); //id编号
docunid=eldoc.g("WF_OrUnid");//文档unid
if(eldoc.isNull()){
		BeanCtx.showErrorMsg(response,"Error : Could not find the data config! WF_OrUnid="+docunid);
		BeanCtx.close();
		return;
}
String rdbTableName=eldoc.g("SqlTableName"); //对应的sql中的数据库表
String codeType="ProcessOutData_FieldConfig";
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
	$('#dg').datagrid('insertRow',{index:0,row:{"DestFdName":"","SrcFdName":"","FdType":"varchar","Remark":""}});
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

var tableName="<%=rdbTableName%>";
if(tableName==""){
	var tableName=parent.frames[0].$("#SqlTableName").combobox('getValue');
}

var dataSourceid="<%=eldoc.g("DataSourceid")%>";
if(dataSourceid==""){
	dataSourceid=parent.frames[0].$("#DataSourceid").val();
}

var formNumber="<%=eldoc.g("FormNumber")%>";
if(formNumber==""){
	formNumber=parent.frames[0].$("#FormNumber").val();
}


//从sql表中载入数据
function loadFromSqlTable(){
	mask();
	var url="../rule?wf_num=R_S001_B010&DataSourceid="+dataSourceid+"&WF_TableName="+tableName;
	var data=$("#dg").datagrid('getData');
	$.get(url,function(r){
		for(var i=0;i<r.length;i++){
			var ColumnName=r[i].ColumnName;
			var hflag=false;
			for(var j=0;j<data.rows.length;j++){
				if(data.rows[j].DestFdName==ColumnName){
					hflag=true;
				}
			}
			if(hflag==false){
				$('#dg').datagrid('appendRow',{"DestFdName":ColumnName,"SrcFdName":"","FdType":"varchar","Remark":""});
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
	return "FieldConfig="+encodeURIComponent(JSON.stringify($("#dg").datagrid('getData')));
}
        
</script>
</head>
<body style="margin:1px;overflow:auto" onclick="stopEditRow()" >
<div class="toptoolbar" id="mm" >
	| <a href="#" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-add'" onclick="insertRow()" >新增字段</a>
	| <a href="#" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-remove'" onclick="removeSelectedRow()" >删除选中字段</a>
	| <a href="#" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-cmp'" onclick="loadFromSqlTable()" >从SQL表中载入</a>
</div>

<form method='post' id="linkeyform"  style="display:none">
<input name='WF_DocUnid' id="WF_DocUnid" value="<%=docunid%>" >
<textarea name="FieldConfig" id="FieldConfig" >
<%
String jsonConfig=eldoc.g("FieldConfig");
if(Tools.isBlank(jsonConfig)){
	jsonConfig="{total:1,\"rows\":[]}";
}
out.println(jsonConfig);
%>
</textarea>
</form>
<script>
var jsonStr=$("#FieldConfig").val().trim();
var colJson=eval('('+jsonStr+')');
</script>
<%
	String code=Rdb.getValueBySql("select DefaultCode from BPM_DevDefaultCode where CodeType='"+codeType+"'");
	out.println(code);
%>
</body></html>
<%
	BeanCtx.close();
%>
