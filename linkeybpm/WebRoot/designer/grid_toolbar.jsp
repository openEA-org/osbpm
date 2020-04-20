<%@ page import="cn.linkey.factory.BeanCtx,cn.linkey.dao.Rdb,cn.linkey.util.Tools,cn.linkey.doc.Document,java.util.*" %>
<%@page errorPage="../error.jsp"%>
<%@page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8"%> 

<%
String docunid=BeanCtx.g("wf_eldocunid");
Document eldoc=null;
if(Tools.isBlank(docunid)){
		BeanCtx.showErrorMsg(response,"Error : Could not find the grid! WF_OrUnid="+docunid);
		BeanCtx.close();
		return;
}else{
	String sql="select * from BPM_GridList where WF_OrUnid='"+docunid+"'";
	eldoc=Rdb.getDocumentBySql("BPM_GridList",sql); //得到grid文档数据
	if(eldoc.isNull()){
		BeanCtx.showErrorMsg(response,"Error : Could not find the grid! WF_OrUnid="+docunid);
		BeanCtx.close();
		return;
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
	$('#dg').datagrid('appendRow',{"btnid":"btn01","btnName":"Button Name","clickEvent":"","icons":"cmp.gif","sortNum":"5"});
}

function removeSelectedRow(){
    var rows = $('#dg').datagrid('getSelections');
    if (!rows || rows.length == 0) {
        $.messager.alert('提示', '请选择要删除的数据!', 'info');
        return;
    }
    
	$.messager.confirm('Confirm','您确定要删除选中按扭吗?',function(r){if (r){
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

function clickRow(index){
	$("#dg").datagrid('unselectAll');
	$("#dg").datagrid('selectRow',index);
	stopEditRow();
}

function formonsubmit(){}

function serializeForm(){
	//统一的序列化表单方法
	stopEditRow();
	return "ToolbarConfig="+encodeURIComponent(JSON.stringify($("#dg").datagrid('getData')));
}

function copyBtn(){
	 var json=JSON.stringify($("#dg").datagrid('getData'));
	 $.copy(json);
}

function SaveDocument(btnAction){
	stopEditRow();
	var jsonData=$("#dg").datagrid('getData');
	$("#ToolbarConfig").val(JSON.stringify(jsonData));
	mask();
	$('#WF_Action').val(btnAction);
	$('#linkeyform').form({
    	url:'../rule?wf_num=R_S001_B002',
    	onSubmit: function(param){
    		param.WF_TableName="BPM_GridList";
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

function formatRoleName(row){ var s = row.RoleName + '('+row.RoleNumber+')';return s;}

function formatIcons(row){
          var s ="<img src='../linkey/bpm/easyui/themes/"+row.image+"' style='vertical-align:middle;' > "+row.iconCls;
          return s;
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
<body style="margin:1px;overflow:hidden" onclick="stopEditRow()" >
<div id="mm" style="padding:2px;/*#border:1px solid #ddd;background:#f4f4f4#*/" >
	| <a href="#" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-add'" onclick="insertRow()" >新增按扭</a>
	| <a href="#" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-remove'" onclick="removeSelectedRow()" >删除按扭</a>
    | <a href="#" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-reload'" onclick="location.reload();">刷新</a>
</div>

<form method='post' id="linkeyform"  style="display:none">
<textarea name="ToolbarConfig" id="ToolbarConfig" ><%=eldoc.g("ToolbarConfig")%></textarea>
<input name='WF_Action' id="WF_Action" value="" ><!-- 按扭动作id-->
<input name='WF_DocUnid' id="WF_DocUnid" value="<%=docunid%>" ><!-- 表单的docunid-->
<script>
var jsonStr=$("#ToolbarConfig").val();
var btnJson;
if(jsonStr==""){
		<%
			String defaultCodeType=BeanCtx.g("codetype"); //默认代码类型
			if(Tools.isBlank(defaultCodeType)){defaultCodeType="Grid_ToolBar";}
			String defaultCode=Rdb.getValueBySql("select DefaultCode from BPM_DevDefaultCode where CodeType='"+defaultCodeType+"'");
		%>
		btnJson=<%=defaultCode.trim()%>
}else{
	btnJson=$.parseJSON(jsonStr);
}
</script>
</form>
 <table id="dg" width="100%" height="300px" class="easyui-datagrid" data-options="data:btnJson,remoteSort:false,singleSelect:false,border:false,fitColumns:true,rownumbers: true,onClickRow: editRow,onRowContextMenu:stopEditRow">
     <thead>
         <tr>
         	<th data-options="checkbox:true"></th>
            <th field="btnid" width="40"  editor='text' >按扭编号</th>
            <th field="btnName" width="80"  editor='SelectLabel' >按扭名称</th>
            <th data-options="field:'clickEvent',editor:{type:'combobox',options:{
                                									valueField:'val',
                                									textField:'text',
                                									data:[{val:'NewDoc()',text:'新增文档'},{val:'DeleteDoc()',text:'删除文档'},{val:'CopyDoc()',text:'拷贝文档'},{val:'grid2excel()',text:'导出到Excel'},{val:'excel2grid()',text:'从Excel导入'},{val:'btnClick(this.id)',text:'执行视图事件'},{val:'history.back()',text:'返回'},{val:'window.close()',text:'关闭'}]
                                								}}"  width="80" sortable="true" >点击事件</th>
            <th data-options="field:'RoleNumber',width:100,
                        editor:{
                            type:'combobox',
                            options:{
                                valueField:'RoleNumber',
                                textField:'RoleName',
                                groupField:'WF_Appid',
                                multiple:true,
                                formatter: function(row){ var s = row.RoleName + '('+row.RoleNumber+')';return s;},
                                url:'../rule?wf_num=R_S006_B001'
                            }
                        }" >绑定角色</th>
            <th  data-options="field:'hiddenfd',width:100,editor:'text'" >根据字段隐藏(填字段名)</th>
            <th  data-options="field:'iconCls',width:80,
                        editor:{
                            type:'combobox',
                            options:{
                                valueField:'iconCls',
                                textField:'iconCls',
                                formatter: formatIcons,
                                url:'../linkey/bpm/easyui/themes/gray/icon.json'
                            }
                        }" >图标</th>
            <th data-options="field:'sortNum'" width="25" sortable="true" editor='numberbox' >排序</th>
        </tr>
    </thead>
</table>
</body></html>
<%
	BeanCtx.close();
%>
