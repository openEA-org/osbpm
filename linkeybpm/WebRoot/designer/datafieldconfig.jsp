<%@ page import="cn.linkey.factory.BeanCtx,cn.linkey.dao.Rdb,cn.linkey.util.Tools,cn.linkey.doc.Document,java.util.*" %>
<%@page errorPage="../error.jsp"%>
<%@page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8"%> 

<%
String sqlTableName="BPM_DataSourceList";
String docunid=BeanCtx.g("wf_eldocunid");
String sql="select * from "+sqlTableName+" where WF_OrUnid='"+docunid+"'";
Document eldoc=Rdb.getDocumentBySql(sqlTableName,sql); //得到json文档数据
String elid=eldoc.g("Jsonid"); //id编号
docunid=eldoc.g("WF_OrUnid");//文档unid
if(eldoc.isNull()){
		BeanCtx.showErrorMsg(response,"Error : Could not find the json config! WF_OrUnid="+docunid);
		BeanCtx.close();
		return;
}
String rdbTableName=eldoc.g("SqlTableName"); //对应的sql中的数据库表
String codeType=BeanCtx.g("codetype");
if(Tools.isBlank(codeType)){
	String dataType=eldoc.g("DataType");
	if(dataType.equals("1")){
		codeType="JsonData_FieldConfig";
	}else if(dataType.equals("2")){
		codeType="TreeData_FieldConfig";
	}else if(dataType.equals("2")){
		codeType="XmlData_FieldConfig";
	}else if(dataType.equals("2")){
		codeType="CategoryData_FieldConfig";
	}
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
	$('#dg').datagrid('insertRow',{index:0,row:{"JsonName":"","FdName":"","IsString":"Y","Remark":""}});
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


//从sql表中载入数据
function loadFromSqlTable(){
	mask();
	var url="../rule?wf_num=R_S001_B010&DataSourceid="+dataSourceid+"&WF_TableName="+tableName;
	var data=$("#dg").datagrid('getData');
	$.get(url,function(r){
		for(var i=0;i<r.length;i++){
			var ColumnName=r[i].ColumnName;
			var remark=r[i].Remark;
			var hflag=false;
			for(var j=0;j<data.rows.length;j++){
				if(data.rows[j].FdName==ColumnName){
					hflag=true;
				}
			}
			if(hflag==false){
				$('#dg').datagrid('appendRow',{"JsonName":"","FdName":ColumnName,"IsString":"Y","Remark":remark});
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
	return "JsonConfig="+encodeURIComponent(JSON.stringify($("#dg").datagrid('getData')));
}

function SaveDocument(btnAction){
	stopEditRow();
	mask();
	$("#JsonConfig").val(JSON.stringify($("#dg").datagrid('getData')));
	$('#linkeyform').form({
    	url:'../rule?wf_num=R_S001_B002',
    	onSubmit: function(param){
    		param.WF_TableName="BPM_DataSourceList";
    		param.WF_Action=btnAction;
    		param.WF_KeyFdName="Dataid";
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
        	}else if(data.Status=="error"){
        		$.messager.alert('Info',data.msg,'Error');
        	}
    	}
	});
	$('#linkeyform').submit();
}
        
</script>
</head>
<body style="margin:1px;overflow:auto" onclick="stopEditRow()" >
<div class="toptoolbar" id="mm" >
	| <a href="#" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-add'" onclick="insertRow()" >新增列</a>
	| <a href="#" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-remove'" onclick="removeSelectedRow()" >删除选中字段</a>
	| <a href="#" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-cmp'" onclick="loadFromSqlTable()" >从SQL表中载入</a>
    | <a href="#" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-reload'" onclick="location.reload();">刷新</a>
</div>

<form method='post' id="linkeyform"  style="display:none">
<input name='WF_DocUnid' id="WF_DocUnid" value="<%=docunid%>" ><!-- json docunid-->
<textarea name="JsonConfig" id="JsonConfig" >
<%
String jsonConfig=eldoc.g("JsonConfig");
if(Tools.isBlank(jsonConfig)){
	jsonConfig=Rdb.getValueBySql("select DefaultCode from BPM_DevDefaultCode where CodeType='"+codeType+"InitVal'");
}
out.println(jsonConfig);
%>
</textarea>
</form>
<script>
var jsonStr=$("#JsonConfig").val().trim();
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
