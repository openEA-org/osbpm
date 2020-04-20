<%@ page import="cn.linkey.factory.BeanCtx,cn.linkey.dao.Rdb,cn.linkey.util.Tools,cn.linkey.doc.Document" %>
<%@page errorPage="../error.jsp"%>
<%@page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8"%>
<%
String tableName=BeanCtx.g("tablename"); //设计元素所在数据库表名
String docunid=BeanCtx.g("wf_eldocunid"); //设计元素的unid
String sql="select * from "+tableName+" where WF_OrUnid='"+docunid+"'"; //这个docunid是设计元素的unid
Document eldoc=Rdb.getDocumentBySql(sql);
if(eldoc.isNull()){
		BeanCtx.showErrorMsg(response,"Error : Could not find the element!");
		BeanCtx.close();
		return;
}
String xmlCode=eldoc.toXmlStr(true);
xmlCode=xmlCode.replace("<WFItem","\n	<WFItem");
xmlCode=xmlCode.replace("</Items>","\n</Items>");
xmlCode=xmlCode.replace("<![CDATA[function","<![CDATA[\nfunction");
xmlCode=xmlCode.replace("<","&lt;");
xmlCode=xmlCode.replace(">","&gt;");

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
	mask();
	var xmlCode=HTMLFRAEM_XmlCode.getContent();
	xmlCode=xmlCode.trim();
	$.post("../rule?wf_num=R_S001_B025",{WF_elDocUnid:'<%=docunid%>',WF_ElTableName:'<%=tableName%>',XmlCode:xmlCode},function(data){
    	unmask();
    	if(data.Status=="error"){
        	alert(data.msg);
        }else{
        	alert("XML数据已更新,请刷新整个页面的数据!");
        }
  	});
}
</script>
<body style="margin:1px;overflow:hidden"  >
<div id="win"></div>
<div style="padding:1px;/*#border:1px solid #ddd;background:#f4f4f4#*/">
	| <a href="#" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-save'" onclick="SaveDocument('btnSave')" title="更新整个设计的XML代码">保存并更新XML数据</a>
    | <a href="#" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-reload'" onclick="location.reload();" title="仅刷新当前页">刷新</a>
</div>
<form method='post' id="linkeyform" >
<textarea name="XmlCode" id="XmlCode" style="display:none" ><%=xmlCode%></textarea>
<iframe src="../linkey/bpm/editor/ace/editor.html?mode=xml&theme=eclipse&frmname=HTMLFRAEM_XmlCode&fdname=XmlCode" frameborder=0 width=99.5% height="100%" name="HTMLFRAEM_XmlCode" id="HTMLFRAEM_XmlCode" ></iframe>

<%BeanCtx.close();%>

</form></body></html>
