<%@ page import="cn.linkey.factory.BeanCtx,cn.linkey.dao.Rdb,cn.linkey.util.Tools,cn.linkey.doc.Document" %>
<%@page errorPage="../error.jsp"%>
<%@page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8"%>
<%
String docunid=BeanCtx.g("wf_docunid",true); //设计元素的unid
String sql="select * from BPM_DocTrash where WF_OrUnid='"+docunid+"'"; //这个docunid是设计元素的unid
Document eldoc=Rdb.getDocumentBySql(sql);
String xmlCode=eldoc.toXmlStr(true);
xmlCode=xmlCode.replace("<WFItem","\n	<WFItem");
xmlCode=xmlCode.replace("</Items>","\n</Items>");
xmlCode=xmlCode.replace("<![CDATA[function","<![CDATA[\nfunction");
xmlCode=xmlCode.replace("<","&lt;");
xmlCode=xmlCode.replace(">","&gt;");
String targetTableName=Rdb.getValueBySql("select SourceTableName from BPM_DocTrash where WF_OrUnid='"+docunid+"'");

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
function serializeForm(){
	//不允许全部保存
	return "";
}
function SaveDocument(Action){
 	if(!confirm("您确认要还原本文档吗?")){return;}
	mask();
	var xmlCode=HTMLFRAEM_XmlCode.getContent();
	xmlCode=xmlCode.trim();
	$.post("../rule?wf_num=R_S001_B025",{WF_elDocUnid:'<%=docunid%>',WF_ElTableName:'<%=targetTableName%>',XmlCode:xmlCode},function(data){
    	unmask();
        alert(data.msg);
  	});
}
</script>
<body style="margin:1px;overflow:hidden"  >
<div id="win"></div>
<div style="padding:1px;/*#border:1px solid #ddd;background:#f4f4f4#*/">
	| <a href="#" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-save'" onclick="SaveDocument('btnSave')">还原文档</a>
    | <a href="#" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-remove'" onclick="window.close();"><%=BeanCtx.getMsg("Designer","Close")%></a>
</div>
<form method='post' id="linkeyform" >
<textarea name="XmlCode" id="XmlCode" style="display:none" ><%=xmlCode%></textarea>
<iframe src="../linkey/bpm/editor/ace/editor.html?mode=xml&theme=eclipse&frmname=HTMLFRAEM_XmlCode&fdname=XmlCode" frameborder=0 width=99.5% height="100%" name="HTMLFRAEM_XmlCode" id="HTMLFRAEM_XmlCode" ></iframe>
</form></body></html>
<%BeanCtx.close();%>
