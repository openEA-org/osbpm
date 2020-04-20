<%@ page import="cn.linkey.factory.BeanCtx,cn.linkey.dao.Rdb,cn.linkey.util.Tools,cn.linkey.doc.Document,java.util.*" %>
<%@page errorPage="../error.jsp"%>
<%@page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8"%> 

<%
String docunid=BeanCtx.g("wf_eldocunid");
Document formDoc=null;
if(Tools.isBlank(docunid)){
		BeanCtx.showErrorMsg(response,"Error : Could not find the form! WF_OrUnid="+docunid);
		BeanCtx.close();
		return;
}else{
	String sql="select * from BPM_FormList where WF_OrUnid='"+docunid+"'";
	formDoc=Rdb.getDocumentBySql("BPM_FormList",sql); //得到表单文档数据
	if(formDoc.isNull()){
		BeanCtx.showErrorMsg(response,"Error : Could not find the form! WF_OrUnid="+docunid);
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
	$('#dg').datagrid('appendRow',{"btnid":"btnNew","btnName":"Button Name","btnclass":"layui-btn","btnfilter":"*","clickEvent":"SaveDocument()","hidden":"","icons":"cmp.gif","sortNum":"5"});
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

function SaveDocument(btnAction){
	stopEditRow();
	var jsonData=$("#dg").datagrid('getData');
	$("#ToolbarConfig").val(JSON.stringify(jsonData));
	mask();
	$('#WF_Action').val(btnAction);
	$('#linkeyform').form({
    	url:'../rule?wf_num=R_S001_B002',
    	onSubmit: function(param){
    		param.WF_TableName="BPM_FormList";
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
				var img=$('<a href="#" onclick="OpenUrl(\'../r?wf_num=V_S001_E016&targetName=label&WF_Appid=<%=formDoc.g("WF_Appid")%>\',600,300)";return false;>标签</a>').appendTo(container);
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
<body style="margin:1px;overflow:hidden"  >
<div id="mm" style="padding:2px;/*#border:1px solid #ddd;background:#f4f4f4#*/" >
	| <a href="#" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-add'" onclick="insertRow()" >新增按扭</a>
	| <a href="#" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-remove'" onclick="removeSelectedRow()" >删除按扭</a>
    | <a href="#" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-reload'" onclick="location.reload();">刷新</a>
</div>

<form method='post' id="linkeyform"  style="display:none">
<textarea name="ToolbarConfig" id="ToolbarConfig" ><%=formDoc.g("ToolbarConfig")%></textarea>
<input name='WF_Action' id="WF_Action" value="" ><!-- 按扭动作id-->
<input name='WF_DocUnid' id="WF_DocUnid" value="<%=docunid%>" ><!-- 表单的docunid-->
<script>
var jsonStr=$("#ToolbarConfig").val();
if(jsonStr==""){
		btnJson={total:"4","rows":[
		   {"btnid":"btnQuit","btnName":"保存退出","btnclass":"layui-btn layui-btn-sm layui-btn-normal","btnfilter":"*","clickEvent":"SaveDocument('Quit')","layuiClick":"","hidden":"","sortNum":"1"},
		   {"btnid":"btnSave","btnName":"保存","btnclass":"layui-btn layui-btn-sm layui-btn-danger","btnfilter":"*","clickEvent":"SaveDocument()","layuiClick":"","hidden":"","sortNum":"2"},
		   {"btnid":"btnEdit","btnName":"编辑","btnclass":"layui-btn","btnfilter":"","clickEvent":"EditDocument()","hidden":"NEW,EDIT,NOAUTHOR","layuiClick":"","iconCls":"&#xe642;","sortNum":"3"},
		   {"btnid":"btnBack","btnName":"返回","btnclass":"layui-btn layui-btn-sm","btntype":"reset","btnfilter":"","clickEvent":"history.back()","layuiClick":"","hidden":"","sortNum":"4"}
		]}
}else{
	btnJson=$.parseJSON(jsonStr);
}

</script>
</form>
 <table id="dg" width="100%" height="300px" class="easyui-datagrid" data-options="data:btnJson,remoteSort:false,singleSelect:false,border:false,fitColumns:true,rownumbers: true,onClickRow: editRow,onRowContextMenu:stopEditRow">
     <thead>
         <tr>
         	<th data-options="checkbox:true"></th>
            <th field="btnid" width="35"  editor='text' >按扭编号</th>
            <th field="btnName" width="90"  editor='SelectLabel' >按扭名称</th>
            <th data-options="field:'clickEvent',editor:{
                            type:'combobox',
                            options:{
                                valueField:'val',
                                textField:'txt',
                                data:[{val:'SaveDocument(\'Quit\')',txt:'保存退出SaveDocument(\'Quit\')'},{val:'SaveDocument()',txt:'保存SaveDocument()'},{val:'EditDocument()',txt:'编辑EditDocument()'},{val:'PrintAppForm()',txt:'打印PrintAppForm()'},{val:'history.back()',txt:'返回history.back()'},{val:'window.close()',txt:'关闭window.close()'}]
                            }
                        }" width="80" sortable="true" >clickEvent</th>
            <th  data-options="field:'layuiClick',width:80,editor:'text'" >点击事件(layui)</th>
            <th width="100"  data-options="field:'RoleNumber',width:90,
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
            <th  data-options="field:'hiddenfd',width:50,editor:'text'" >根据字段隐藏</th>
            <th  data-options="field:'btnclass',width:80,editor:'text'" >Class</th>
            <th  data-options="field:'btnfilter',width:50,editor:'text'" >过滤器(Filter)</th>
            <th  data-options="field:'btntype',width:50,editor:'text'" >按钮类型(type)</th>
            <th  width="70"  data-options="field:'hidden',width:100,
                        editor:{
                            type:'combobox',
                            options:{
                                valueField:'conStr',
                                textField:'txt',
                                multiple:true,
                                data:[{conStr:'NEW',txt:'新建(NEW)'},{conStr:'EDIT',txt:'编辑(EDIT)'},{conStr:'READ',txt:'阅读(READ)'},{conStr:'NOAUTHOR',txt:'作者权限(NOAUTHOR)'}]
                            }
                        }" >按状态隐藏</th>
            <th width="50"  data-options="field:'iconCls',width:100,
                        editor:{
                            type:'combobox',
                            options:{
                                valueField:'iconCls',
                                textField:'iconCls',
                                formatter: formatIcons,
                                url:'../linkey/bpm/easyui/themes/gray/icon.json'
                            }
                        }" >图标</th>
            <th data-options="field:'sortNum'" width="20" sortable="true" editor='numberbox' >排序</th>
        </tr>
    </thead>
</table>
</body></html>
<%
	BeanCtx.close();
%>
