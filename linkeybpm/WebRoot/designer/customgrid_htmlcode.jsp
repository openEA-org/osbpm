<%@ page import="cn.linkey.factory.BeanCtx,cn.linkey.dao.Rdb,cn.linkey.util.Tools,cn.linkey.doc.Document,java.util.*" %>
<%@ page errorPage="../error.jsp"%>
<%@page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8"%> 
<%
String checked="";
String docunid=BeanCtx.g("wf_eldocunid");
String sqlTableName="BPM_GridList";
Document gridDoc=null;
if(Tools.isBlank(docunid)){
	BeanCtx.showErrorMsg(response,"Error : wf_docunid cannot be empty! WF_OrUnid="+docunid);
	BeanCtx.close();
	return;
}else{
	String sql="select * from "+sqlTableName+" where WF_OrUnid='"+docunid+"'";
	gridDoc=Rdb.getDocumentBySql(sqlTableName,sql); //得到grid文档数据
	String griNum=gridDoc.g("GridNum"); //page编号
	if(gridDoc.isNull()){
		BeanCtx.showErrorMsg(response,"Error : Could not find the grid! WF_OrUnid="+docunid);
		BeanCtx.close();
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
function formonload(){}
function serializeForm(){
	//统一的序列化表单方法 20180404
	//return "GridBody="+encodeURIComponent(HTMLFRAEM_GridBody.getContent().trim()); 
	var winobj = document.getElementById("HTMLFRAEM_GridBody").contentWindow;
	return "GridBody="+encodeURIComponent(winobj.getContent().trim());
}
function SaveDocument(Action){
	mask();
	//var htmlCode=HTMLFRAEM_GridBody.getContent(); 
	var winobj = document.getElementById("HTMLFRAEM_GridBody").contentWindow;
	var htmlCode=winobj.getContent(); 
	htmlCode=htmlCode.trim();
	$.post("../rule?wf_num=R_S001_B002",{WF_DocUnid:'<%=docunid%>',WF_TableName:'BPM_GridList',WF_KeyFdName:'griNum',GridBody:htmlCode},function(data){
    	unmask();
    	if(data.Status=="error"){
        	$.messager.alert('Info',data.msg,'Error');
        }
  	});
}

function formatHtml(){
		var winobj=document.getElementById("HTMLFRAEM_GridBody").contentWindow;
		var GridBody=winobj.getContent();
		GridBody=GridBody.replaceAll("<tr","\n    <tr");
		GridBody=GridBody.replaceAll("</tr>","\n    </tr>");
		GridBody=GridBody.replaceAll("<td","\n      <td");
		GridBody=GridBody.replaceAll("<thead","  <thead");
		GridBody=GridBody.replaceAll("</thead>","\n    </thead>");
		GridBody=GridBody.replaceAll("<th","\n      <th");
		GridBody=GridBody.replaceAll("</tbody>","\n</tbody>");
		GridBody=GridBody.replaceAll("</table>","\n</table>\n");
		GridBody=GridBody.replaceAll("<div","\n<div");
		GridBody=GridBody.replaceAll("</div>","\n</div>\n");
		GridBody=GridBody.replaceAll("<span","\n<span");
		GridBody=GridBody.replaceAll("</span>","\n</span>\n");
		GridBody=GridBody.replaceAll("<iframe","\n<iframe");
		GridBody=GridBody.replaceAll("</ul>","\n</ul>");
		GridBody=GridBody.replaceAll("<li>","\n</li>");
		GridBody=GridBody.replaceAll("<a","\n<a");
		winobj.editor.setValue("");
        winobj.editor.insert(GridBody);
}
//预览表单
function PreviewView(){
	window.open("../customgrid?wf_num=<%=gridDoc.g("GridNum")%>");
}
</script>
<body style="margin:1px;overflow:hidden"  >
<div style="padding:2px;/*#border:1px solid #ddd;background:#f4f4f4#*/">
	| <a href="#" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-save'" onclick="SaveDocument()">保存(Ctr+s)</a>
	| <a href="#" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-cmp'" onclick="formatHtml();">格式化</a>
    | <a href="#" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-reload'" onclick="location.reload();">刷新</a>
</div>
<textarea name="GridBody" id="GridBody" style="display:none;width:100%;height:700px;overflow:auto" >
<% 
	String gridBody=gridDoc.g("GridBody");
	if(Tools.isBlank(gridBody)){
		gridBody=Rdb.getValueBySql("select DefaultCode from BPM_DevDefaultCode where CodeType='CustomGrid_GridBody'");
	}
	gridBody=gridBody.replace("&amp;","&").replace("&amp;","&").replace("&lt;","<").replace("&gt;",">");
	out.println(gridBody);	
	BeanCtx.close();
%>
</textarea>
<iframe src="../linkey/bpm/editor/ace/editor.html?mode=html&theme=xcode&frmname=HTMLFRAEM_GridBody&fdname=GridBody" frameborder=0 width=99.5% height="600px" id="HTMLFRAEM_GridBody" ></iframe>

</body></html>
