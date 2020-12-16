<%@ page import="cn.linkey.factory.BeanCtx,cn.linkey.dao.Rdb,cn.linkey.util.Tools,cn.linkey.doc.Document,java.util.*" %>
<%@page errorPage="../error.jsp"%>
<%@page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8"%> 

<%
String docunid=BeanCtx.g("wf_eldocunid");
%>
<!DOCTYPE html><html><head><title>Form Attribute</title>
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

function serializeForm(){
	//统一的序列化表单方法
	//var jsonStr=JSON.stringify($("#dg").datagrid('getData').rows);
	//jsonStr="{\"fdList\":"+jsonStr+"}";
	return "";
}

function removeSelectedRow(){
    var rows = $('#dg').datagrid('getSelections');
    if (!rows || rows.length == 0) {
       alert('请选择要删除的字段!');
       return;
    }
	if(!confirm("您确认要删除选中字段吗?")){return false;}
	var dg=$('#dg');
	$.each(rows, function (i, row) {
        	var index=dg.datagrid('getRowIndex',row);
        	dg.datagrid('deleteRow',index);
    });
	
	//更新字段属性
	var jsonStr=JSON.stringify($("#dg").datagrid('getData').rows);
	jsonStr="{\"fdList\":"+jsonStr+"}";
	parent.frames[0].$("#FieldConfig").val(jsonStr);
}

function clickRow(index){
	$("#dg").datagrid('unselectAll');
	$("#dg").datagrid('selectRow',index);
}

$(function(){
	var jsonStr=parent.frames[0].$("#FieldConfig").val();
	if(jsonStr==undefined){
		var url="../rule?wf_num=R_S001_B017&wf_docunid=<%=docunid%>";
		$.get(url,function(data){
			$("#dg").datagrid('loadData',data);
		});
	}else{
		if(jsonStr==""){jsonStr="{rows:[]}";}
		else{
			//alert(jsonStr);
			var htmlJsonData=$.parseJSON(jsonStr);
			var jsonData={rows:[]}
			jsonData.rows=htmlJsonData.fdList;
			$("#dg").datagrid('loadData',jsonData);
			//alert("NewJsonData="+JSON.stringify(jsonData));
		}
	}
});


</script>
</head>
<body style="margin:1px;overflow-x: hidden; overflow-y: auto;" >
<div id="mm" style="padding:2px;/*#border:1px solid #ddd;background:#f4f4f4#*/" >
	| <a href="#" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-remove'" onclick="removeSelectedRow()" >删除字段属性</a>
    | <a href="#" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-reload'" onclick="location.reload();">刷新</a>
</div>

 <table id="dg" width="100%" height="300px" class="easyui-datagrid" data-options="data:{rows:[]},remoteSort:false,border:false,singleSelect:false,fit:false,fitColumns:true,rownumbers: true,onClickRow:clickRow,toolbar:'#mm'">
     <thead>
         <tr>
         	<th data-options="checkbox:true"></th>
            <th field="name" width="40" sortable="true" >字段名</th>
            <th field="remark" width="60" sortable="true" >备注</th>
            <th field='required' width="30" sortable="true" >不允许为空</th>
            <th field="valimsg" width="60" sortable="true" >为空提示</th>
            <th field='validtype' width="50" sortable="true" >验证规则</th>
            <th field='selector' width="50" sortable="true" >选择器</th>
            <th field='jsevent' width="40" sortable="true" >JS前端事件</th>
            <th field='jsfun' width="50" sortable="true" >JS函数</th>
            <th field='rule' width="50" sortable="true" >绑定规则</th>
            <th field='hiddentype' width="30" sortable="true" >隐藏模式</th>
        </tr>
    </thead>
</table>
        
</body></html>
<%BeanCtx.close();%>
