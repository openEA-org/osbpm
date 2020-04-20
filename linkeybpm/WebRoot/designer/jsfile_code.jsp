<%@ page import="cn.linkey.factory.BeanCtx,cn.linkey.dao.Rdb,cn.linkey.util.Tools,cn.linkey.doc.Document,java.util.*" %>
<%@ page errorPage="../error.jsp"%>
<%@page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8"%> 
<%
String docunid=BeanCtx.g("wf_eldocunid");
String tableName=BeanCtx.g("tablename"); //设计元素所在数据库表名
String tableElid=BeanCtx.g("tableelid"); //元素id的字段名
String tableElName=BeanCtx.g("tableelname"); //设计名称的字段名
String defaultCodeType=BeanCtx.g("codetype"); //默认代码类型
Document eldoc=null;
if(Tools.isBlank(docunid)){
	BeanCtx.close();
	BeanCtx.showErrorMsg(response,"Error : wf_eldocunid cannot be empty!");
	return;
}else{
	String sql="select * from "+tableName+" where WF_OrUnid='"+docunid+"'";
	eldoc=Rdb.getDocumentBySql(tableName,sql); //得到设计文档数据
	String elid=eldoc.g(tableElid); //elid编号
	if(eldoc.isNull()){
		BeanCtx.close();
		BeanCtx.showErrorMsg(response,"Error : Could not find the element!");
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

function SaveDocument(Action){
	mask();
	var fdName=GetUrlArg("FdName");
	if(fdName==""){fdName="JsHeader";}
	var jsStr=HTMLFRAEM_JsHeader.getContent();
	jsStr=jsStr.trim();
	$.post("../rule?wf_num=R_S001_B002",{WF_DocUnid:'<%=docunid%>',WF_TableName:'<%=tableName%>',WF_KeyFdName:'<%=tableElid%>',WF_RuleFdName:'<%=tableElName%>',JsHeader:jsStr},function(data){
    	unmask();
    	if(data.Status=="error"){
        	$.messager.alert('Info',data.msg,'Error');
        }
  	});
}

function serializeForm(){
	//统一的序列化表单方法
	var jsHeader=HTMLFRAEM_JsHeader.getContent();
	jsHeader=encodeURIComponent(jsHeader.trim());
	jsHeader="JsHeader="+jsHeader;
	return jsHeader;
}

function SaveToDiskFile(Action){
	if(!confirm("您确认要把代码保存到JS文件中去吗?如果文件已存在将被覆盖!")){return false;}
	mask();
	var fileName=parent.window.frames[0].$('#FileName').val();
	var jsCode=HTMLFRAEM_JsHeader.getContent();
	jsCode=jsCode.trim();
	$.post("../rule?wf_num=R_S001_B045",{FileName:fileName,JsCode:jsCode},function(data){
		data=eval('('+data+')');
    	unmask();
    	alert(data.msg);
  	});
}

function ReadJsFileCode(){
	
	var fileName=parent.window.frames[0].$('#FileName').val();
	mask();
	$.post("../rule?wf_num=R_S001_B044",{FileName:fileName},function(data){
		data=eval('('+data+')');
    	unmask();
    	if(data.Status=="ok"){
    		var winobj=document.getElementById("HTMLFRAEM_JsHeader").contentWindow;
    		var curJsCode=winobj.getContent().trim();
    		var javaCode=data.code.trim();
    		if(curJsCode==javaCode){
    			alert("Java源文件的内容与当前规则的代码是一至的，无需同步!");
    			return false;
    		}
        	winobj.editor.setValue("");
        	winobj.editor.insert(javaCode);
        	alert("代码已成功同步为"+fileName+".js源文件中的代码!");
        }else{
        	alert(data.msg);
        }
  	});
}

function CheckJsFileCode(){
	var jsCode=HTMLFRAEM_JsHeader.getContent().trim();
	mask();
	$.post("../rule?wf_num=R_S001_B046",{DocUnid:"<%=docunid%>",JsCode:jsCode},function(data){
		data=eval('('+data+')');
    	unmask();
    	alert(data.msg);
  	});
}

</script>
<body style="margin:1px;overflow:hidden"  >
<div style="padding:2px;/*#border:1px solid #ddd;background:#f4f4f4#*/">
	| <a href="#" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-save'" onclick="SaveDocument()">保存设计(Ctr+s)</a>
	| <a href="#" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-redo'" onclick="SaveToDiskFile()">更新硬盘中的JS文件</a>
	| <a href="#" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-undo'" onclick="ReadJsFileCode()">读取硬盘JS文件代码</a>
	| <a href="#" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-ok'" onclick="CheckJsFileCode()">比较JS文件</a>
    | <a href="#" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-reload'" onclick="location.reload();">刷新</a>
</div>
<form method='post' id="linkeyform" >
<textarea name="JsHeader" id="JsHeader" style="display:none" >
<% 
	String jsHeader=eldoc.g("JsHeader");
	jsHeader=jsHeader.replace("<","&lt;");
	jsHeader=jsHeader.replace(">","&gt;");
	out.println(jsHeader);	
	BeanCtx.close();
%>
</textarea>

<iframe src="../linkey/bpm/editor/ace/editor.html?mode=javascript&frmname=HTMLFRAEM_JsHeader&fdname=JsHeader" frameborder=0 width=99.5% height="600px" name="HTMLFRAEM_JsHeader" id="HTMLFRAEM_JsHeader" ></iframe>

</form></body></html>
