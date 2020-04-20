<%@ page import="cn.linkey.factory.BeanCtx,cn.linkey.dao.Rdb,cn.linkey.util.Tools,cn.linkey.doc.Document,java.util.*" %>
<%@ page errorPage="../error.jsp"%>
<%@page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8"%> 
<%
String checked="";
String docunid=BeanCtx.g("wf_eldocunid");
String sqlTableName="BPM_PageList";
Document formDoc=null;
if(Tools.isBlank(docunid)){
	BeanCtx.showErrorMsg(response,"Error : wf_docunid cannot be empty! WF_OrUnid="+docunid);
	BeanCtx.close();
	return;
}else{
	String sql="select * from "+sqlTableName+" where WF_OrUnid='"+docunid+"'";
	formDoc=Rdb.getDocumentBySql(sqlTableName,sql); //得到page文档数据
	String pageNum=formDoc.g("PageNum"); //page编号
	if(formDoc.isNull()){
		BeanCtx.showErrorMsg(response,"Error : Could not find the page! WF_OrUnid="+docunid);
		BeanCtx.close();
		return;
	}
}
BeanCtx.close();
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
function formonload(){}
function serializeForm(){
	//统一的序列化表单方法 
	var winobj=document.getElementById("HTMLFRAEM_PageBody").contentWindow;
	return "PageBody="+encodeURIComponent(winobj.getContent().trim());
}
function SaveDocument(Action){
	mask();
	var winobj=document.getElementById("HTMLFRAEM_PageBody").contentWindow;
	var htmlCode=winobj.getContent();
	htmlCode=htmlCode.trim();
	$.post("../rule?wf_num=R_S001_B002",{WF_DocUnid:'<%=docunid%>',WF_TableName:'BPM_PageList',WF_KeyFdName:'pageNum',PageBody:htmlCode},function(data){
    	unmask();
    	if(data.Status=="error"){
        	$.messager.alert('Info',data.msg,'Error');
        }
  	});
}
function formatHtml(){
		var winobj=document.getElementById("HTMLFRAEM_PageBody").contentWindow;
		var PageBody=winobj.getContent();
		PageBody=PageBody.replaceAll("<tr","\n    <tr");
		PageBody=PageBody.replaceAll("</tr>","\n    </tr>");
		PageBody=PageBody.replaceAll("<td","\n      <td");
		PageBody=PageBody.replaceAll("<thead","  <thead");
		PageBody=PageBody.replaceAll("</thead>","\n    </thead>");
		PageBody=PageBody.replaceAll("<th","\n      <th");
		PageBody=PageBody.replaceAll("</tbody>","\n</tbody>");
		PageBody=PageBody.replaceAll("</table>","\n</table>\n");
		PageBody=PageBody.replaceAll("<div","\n<div");
		PageBody=PageBody.replaceAll("</div>","\n</div>\n");
		PageBody=PageBody.replaceAll("<span","\n<span");
		PageBody=PageBody.replaceAll("</span>","\n</span>\n");
		PageBody=PageBody.replaceAll("<iframe","\n<iframe");
		PageBody=PageBody.replaceAll("</ul>","\n</ul>");
		PageBody=PageBody.replaceAll("<li>","\n<li>");
		PageBody=PageBody.replaceAll("<a","\n<a");
		winobj.editor.setValue("");
        winobj.editor.insert(PageBody);
}
//预览表单
function PreviewView(){
	window.open("../page?wf_num=<%=formDoc.g("PageNum")%>");
}
</script>
<body style="margin:1px;overflow:hidden"  >
<div style="padding:2px;/*#border:1px solid #ddd;background:#f4f4f4#*/">
	| <a href="#" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-save'" onclick="SaveDocument()">保存(Ctr+s)</a>
	| <a href="#" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-cmp'" onclick="formatHtml();">格式化</a>
    | <a href="#" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-reload'" onclick="location.reload();">刷新</a>
</div>
<textarea name="PageBody" id="PageBody" style="display:none;width:100%;height:700px;overflow:auto" ><%
	String pageBody=formDoc.g("PageBody");
	pageBody=pageBody.replace("<","&lt;");
	pageBody=pageBody.replace(">","&gt;");
	pageBody=pageBody.replace("&nbsp;","&amp;nbsp;");
	out.print(pageBody);
	BeanCtx.close();
%></textarea>
<iframe src="../linkey/bpm/editor/ace/editor.html?mode=html&theme=xcode&frmname=HTMLFRAEM_PageBody&fdname=PageBody" frameborder=0 width=99.5% height="600px" id="HTMLFRAEM_PageBody" ></iframe>

</body></html>
