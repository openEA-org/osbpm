<%@ page import="cn.linkey.factory.BeanCtx,cn.linkey.dao.Rdb,cn.linkey.util.*,cn.linkey.doc.Document" %>
<%@page errorPage="../error.jsp"%>
<%@page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8"%>
<%
String docunid=BeanCtx.g("wf_eldocunid"); //规则的unid
String ruleNum=BeanCtx.g("rulenum"); //规则的编号
String themeid = BeanCtx.getSystemConfig("ideTheme"); // 主题ID 202005 add by alibao

Document ruleDoc;
if(docunid!=null){
	String sql="select * from BPM_RuleList where WF_OrUnid='"+docunid+"'"; //这个docunid是设计元素的unid需要转换成为规则的unid
	ruleDoc=Rdb.getDocumentBySql(sql);
	ruleNum=ruleDoc.g("RuleNum"); //规则编号
}else{
	String sql="select * from BPM_RuleList where RuleNum='"+ruleNum+"'";
    ruleDoc=Rdb.getDocumentBySql("BPM_RuleList",sql); //得到规则文档,有可能文档是空值，因为新建的情况
    docunid=ruleDoc.g("WF_OrUnid");
}
if(ruleDoc.isNull()){BeanCtx.showErrorMsg(response,"Error : Could not find the rule!");BeanCtx.close();return;}

String appid=ruleDoc.g("WF_Appid");
String ruleCode=ruleDoc.g("RuleCode"); 
if(Tools.isBlank(ruleCode)){
	if(Tools.isNotBlank(ruleDoc.g("ClassPath")) && ruleDoc.g("CompileFlag").equals("1")){
		ruleCode="<The code is not visible...>";
	}else{
		String defaultCodeType=BeanCtx.g("CodeType"); //默认代码类型如果url中有指定则使用url中的指定值，否则根据设计元素类型来取
		if(Tools.isBlank(defaultCodeType)){
			String ruleType=ruleDoc.g("RuleType");
			if(ruleType.equals("2")){
				defaultCodeType="Rule_Code"; //业务规则
			}else if(ruleType.equals("4")){
				defaultCodeType="FieldRule_Code"; //字段规则
			}else if(ruleType.equals("5")){
				defaultCodeType="TimeRule_Code"; //定时规则
			}else if(ruleType.equals("6")){
				defaultCodeType="ChianRule_Code"; //过虑器规则
			}else if(ruleType.equals("7")){
				defaultCodeType="JavaBean_Code"; //javabean规则
			}else if(ruleType.equals("8")){
				defaultCodeType="Rule_Code"; //流程规则
			}else if(ruleType.equals("3")){ //事件
				String eventType=ruleDoc.g("EventType");
				if(eventType.equals("1")){defaultCodeType="Form_Event";}
				else if(eventType.equals("2")){defaultCodeType="Grid_Event";}
				else if(eventType.equals("3")){defaultCodeType="Json_Event";}
				else if(eventType.equals("4")){defaultCodeType="Page_Event";}
			}
		}
		ruleCode=Rdb.getValueBySql("select DefaultCode from BPM_DevDefaultCode where CodeType='"+defaultCodeType+"'");
	}
}
	ruleCode=ruleCode.replace("<","&lt;");
	ruleCode=ruleCode.replace(">","&gt;");
	ruleCode=ruleCode.replace("{ClassName}",ruleNum);
	ruleCode=ruleCode.replace("{WF_Appid}",appid);
	ruleCode=ruleCode.replace("{RuleName}",ruleDoc.g("RuleName"));
	ruleCode=ruleCode.replace("{Userid}",BeanCtx.getUserid());
	ruleCode=ruleCode.replace("{CreatedDate}",DateUtil.getNow());
	ruleCode=ruleCode.replace("{Version}",ruleDoc.g("WF_Version"));
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
	//统一的序列化表单方法
	var ruleCode=HTMLFRAEM_RuleCode.getContent();
	if(ruleCode.indexOf("class")==-1){return "";}
	ruleCode=encodeURIComponent(ruleCode.trim());
	return "RuleCode="+ruleCode;
}
function SaveDocument(Action){
	if(Action=="btnSaveToFile"){
		 if(!confirm("您确认要把代码保存到Java源文件中吗?如果文件已存在将被覆盖!")){return false;}
	}
	mask();
	var rulCode=HTMLFRAEM_RuleCode.getContent();
	ruleCode=rulCode.trim();
	if(ruleCode.indexOf("class")==-1){unmask();return;}
	$.post("../rule?wf_num=R_S001_B003",{WF_elDocUnid:'<%=docunid%>',WF_Action:Action,RuleCode:ruleCode},function(data){
		data=eval('('+data+')');
    	unmask();
    	if(data.Status=="error"){
        	$('#win').window({width:600,height:260,title:'Compile Error'});
    		$('#win').html("<div style='color:red'>"+decodeURIComponent(data.msg)+"</div>");
        }else{
        	if(data.msg!=undefined){alert(data.msg);}
        	try{$('#win').window('close');}catch(e){}
        }
  	});
}
function SyncJavaFileCode(){
	var appid=parent.window.frames[0].$('#WF_Appid').val();
	var ruleNum=parent.window.frames[0].$('#RuleNum').val();
	if(ruleNum==undefined){
		ruleNum="<%=ruleNum%>";
		appid="<%=appid%>";
	}
	mask();
	$.post("../rule?wf_num=R_S001_B022",{WF_Appid:appid,ruleNum:ruleNum},function(data){
		data=eval('('+data+')');
    	unmask();
    	if(data.Status=="ok"){
    		var winobj=document.getElementById("HTMLFRAEM_RuleCode").contentWindow;
    		var curRuleCode=winobj.getContent().trim();
    		var javaCode=data.code.trim();
    		if(curRuleCode==javaCode){
    			alert("Java源文件的内容与当前规则的代码是一至的，无需同步!");
    			return false;
    		}
        	winobj.editor.setValue("");
        	winobj.editor.insert(javaCode);
        	alert("代码已成功同步为"+ruleNum+".java源文件中的代码!");
        }else{
        	alert(data.msg);
        }
  	});
}

function CheckJavaFileCode(){
	var appid=parent.window.frames[0].$('#WF_Appid').val();
	var ruleNum=parent.window.frames[0].$('#RuleNum').val();
	if(ruleNum==undefined){
		ruleNum="<%=ruleNum%>";
		appid="<%=appid%>";
	}
	var curRuleCode=HTMLFRAEM_RuleCode.getContent().trim();
	mask();
	$.post("../rule?wf_num=R_S001_B032",{WF_Appid:appid,ruleNum:ruleNum,ruleCode:curRuleCode},function(data){
		data=eval('('+data+')');
    	unmask();
    	alert(data.msg);
  	});
}

function insertCode(node){
	var code=node.text;
	code=code.replace("&lt;","<");
	code=code.replace("&gt;",">");
	window.frames["HTMLFRAEM_RuleCode"].editor.insert(code);
}

$(function(){
	var h=$(document).height()-1;
	$('#panel').panel('resize',{width: 210,height: h});
});

function expandRoot(node,data){
    var n=$('#tree').tree('getRoot');
	$('#tree').tree('expand',n.target);
}

//右键菜单
function onContextMenu(e,node){
        e.preventDefault();
        if(node.id=="001"){return;}
        $(this).tree('select',node.target);
        $('#treemm').menu('show',{left: e.pageX,top: e.pageY});
}

function showhelpinfo(){
  var node = $('#tree').tree('getSelected');
  $('#win').window({width:700,height:300,modal:false,title:'帮助信息'});
  var url="../rule?wf_num=R_S015_B001&Folderid="+node.id;
  $('#win').window('refresh', url);
}

// 20200502 add by alibao =========================================start
// 添加自定义代码提示配置按钮
function updateCustom(){
	$('#win').window({width:1200,height:500,modal:true,title:'自定义代码提示'});
    $('#win').html("<iframe height='450px' width='100%' overflow='hidden' frameborder='0' src='../r?wf_num=V_S001_E021&notCodeType=2'></iframe>");
}
//20200502 add by alibao =========================================end

</script>
<body style="margin:0px;overflow:hidden"  >
<table width=100% height="100%" id='ptable' cellpadding=0 cellspacing=0 ><tr>
<td width='210px;' valign='top'  >
 		<div id="panel" class="easyui-panel"  title="常用类包" style="width:210px;padding:5px;">
		<ul class="easyui-tree" id="tree"  data-options="url:'../treedata?wf_num=D_S015_T001',method:'get',animate:true,lines:true,onDblClick:insertCode,onLoadSuccess:expandRoot,onContextMenu:onContextMenu" ></ul>
		</div>
		
        <div id="treemm" class="easyui-menu" style="width:120px;">
        	<div onclick="showhelpinfo()" data-options="iconCls:'icon-help'">查看帮助</div>
       </div>
		
</td>
<td width='*' valign=top >
            <div style="padding:1px;/*#border:1px solid #ddd;background:#f4f4f4#*/">
			| <a href="#" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-save'" onclick="SaveDocument('btnSaveAndCompile')" title="编译并保存当前Java代码,编译错误则不会保存">保存并编译</a>
			| <a href="#" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-redo'" onclick="SaveDocument('btnSaveToFile')">保存到Eclipse源文件中</a>
			| <a href="#" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-undo'" onclick="SyncJavaFileCode()">读取Eclipse源Java代码</a>
			| <a href="#" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-ok'" onclick="CheckJavaFileCode()">比较源文件与当前代码</a>
    		| <a href="#" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-reload'" onclick="location.reload();">刷新</a>
    		| <a href="#" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-cmp'" onclick="updateCustom();">自定义代码提示</a>
			</div>
        	<form method='post' id="linkeyform" style='margin:0px' ><textarea name="RuleCode" id="RuleCode" style="display:none" ><%=ruleCode%></textarea>
        	<iframe src="../linkey/bpm/editor/ace/editor.html?mode=java&frmname=HTMLFRAEM_RuleCode&fdname=RuleCode&theme=<%= themeid %>"  frameborder=0 width=100% height="100%" name="HTMLFRAEM_RuleCode" id="HTMLFRAEM_RuleCode" ></iframe>
			</form>
</td> 
</tr></table>
 
<%BeanCtx.close();%>
<div id="win"></div>
</body></html>
