<%@ page import="cn.linkey.factory.BeanCtx,cn.linkey.dao.Rdb,cn.linkey.util.Tools,cn.linkey.doc.Document,java.util.*" %>
<%@page errorPage="../error.jsp"%>
<%@page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8"%> 

<%
String sqlTableName="BPM_ProcessMapData",checked="";
String docunid=BeanCtx.g("wf_eldocunid");
String sql="select * from "+sqlTableName+" where WF_OrUnid='"+docunid+"'";
Document eldoc=Rdb.getDocumentBySql(sqlTableName,sql); //得到文档数据
String elid=eldoc.g("Dataid"); //id编号
if(eldoc.isNull()){
    BeanCtx.showErrorMsg(response,"Error : Could not find the data config! WF_OrUnid="+docunid);
    BeanCtx.close();
    return;
}
%>
<!DOCTYPE html><html><head><title>Form Attribute</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" type="text/css" href="../linkey/bpm/easyui/themes/gray/easyui.css">
<link rel="stylesheet" type="text/css" href="../linkey/bpm/easyui/themes/icon.css">
<link rel="stylesheet" type="text/css" href="../linkey/bpm/css/app_openform.css">
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

$(function(){
    if($('#SqlTableName').val()!=""){
        var url="../rule?wf_num=R_S001_B010&DataSourceid="+$("#DataSourceid").val()+"&WF_TableName="+$('#SqlTableName').val();
        $('#OrderFieldName').combobox('reload', url);
        $('#DefaultSearchField').combobox('reload', url);
    }
})

$.extend($.fn.validatebox.defaults.rules, {
        elementid : {// 验证dataid有没有重复
            validator : function(v) {
                var appid=$("#WF_Appid").combobox('getValue');$("#WF_Appid").combobox('getValue')
                if(v.indexOf("BPDATA")==-1){
                    return false;
                }else{
                    var vflag=false;
                    $.ajax({
                        type : "post",
                        url : "../rule?wf_num=R_S001_B005",
                        //20180905 写错字段名修改
                        data : {WF_OrUnid:'<%=docunid%>',WF_TableName:'BPM_ProcessMapData',WF_TableColName:'Dataid',WF_Elid:v}, 
                          async : false,
                          success : function(data){vflag=data.Status;} 
                      }); 
                      return vflag;
                }
            },
            message : 'Invalid format or duplicate number!'
        }
});

function serializeForm(){
    //统一的序列化表单方法
    var formData=$("form").serialize();
    var checkBoxData=getNoCheckedBoxValue();
    if(checkBoxData!=""){
        formData+="&"+checkBoxData;
    }
    formData="WF_TableName=BPM_ProcessMapData&"+formData;
    return formData;
}

function PreviewView(){
    alert("本配置不支持预览!");
}

function getTableList(){
    //根据数据源重新获得数据库表
    var url='../rule?wf_num=R_S001_B009&DataSourceid='+$('#DataSourceid').val();
    $('#SqlTableName').combobox('reload',url);
}

</script>
</head>
<body style="margin:2px;overflow:hidden" >
<div id="eventwin"></div>

<form method='post' id="linkeyform" style="padding:5px" >
 <fieldset style="padding:5px" ><legend>数据配置基本属性</legend>
<table id="mytable" class="linkeytable" border="0" cellspacing="1" cellpadding="2" width="100%"><tbody>
<tr valign="top"><td valign="middle" width="21%" align="right">所属应用:</td><td width="79%">
<%=cn.linkey.app.AppUtil.getAppSelected(eldoc.g("WF_Appid"),false)%>
</td></tr>

<tr><td valign="middle" width="21%" align="right">数据配置名称:</td>
<td width="79%"><input value="<%=eldoc.g("DataName")%>"  class="easyui-validatebox" required name="DataName" size="60"/></td>
</tr>

<tr><td valign="middle" width="21%" align="right">唯一编号:</td>
<td width="79%"><input value="<%=elid%>" name="Dataid" id="Dataid" class="easyui-validatebox" validType='elementid' required size="60"/></td>
</tr>

<tr valign="top"><td valign="middle" width="21%" align="right">选择要输出的流程表单:</td>
<td width="79%">
<input data-options="lines:true,cascadeCheck:false,url:'../rule?wf_num=R_S009_B001',method:'get'" id="FormNumber" class="easyui-combotree" name="FormNumber" value="<%=eldoc.g("FormNumber")%>"  size="60" exttype="combotree"/>
</td>
</tr>

<tr valign="top"><td valign="middle" width="21%" align="right">指定目标数据源:</td><td width="79%">
<select name="DataSourceid" id="DataSourceid" onchange="getTableList()" >
<%
    String selectStr="";
    Document[] dc=Rdb.getAllDocumentsBySql("select * from BPM_DataTypeConfig");
    for(Document doc:dc){
        if(doc.g("DataSourceid").equals(eldoc.g("DataSourceid"))){
            selectStr="selected";
        }else{
            selectStr="";
        }
        out.println("<option value=\""+doc.g("DataSourceid")+"\" "+selectStr+" >"+doc.g("DataSourceName")+"("+doc.g("DataSourceid")+")</option>");
    }
    if(selectStr.equals("")){
        out.println("<option value=\"default\" selected >默认数据源</option>");
    }else{
        out.println("<option value=\"default\" >默认数据源</option>");
    }
%>
</select>
</td></tr>

<tr valign="top"><td valign="middle" width="21%" align="right">目标数据所在表:</td>
<td width="79%"><input value="<%=eldoc.g("SqlTableName")%>" name="SqlTableName" id="SqlTableName" required size="60" class="easyui-combobox" data-options="
                    url:'../rule?wf_num=R_S001_B009&DataSourceid=<%=eldoc.g("DataSourceid")%>',
                    method:'get',
                    valueField:'tableName',
                    textField:'tableName'
            " />
</td>
</tr>

<tr valign="top"><td valign="middle" width="21%" align="right">目标数据关键字段(Key):</td>
<td width="79%"><input value="<%=eldoc.g("keyFdName")%>" name="keyFdName" id="keyFdName" class="easyui-validatebox" required size="60"  />
</td>
</tr>

</tbody></table><br/>
<%
    BeanCtx.close();
%>
<!-- Hidden Field Begin--><div style='display:none'>
<input name='WF_DocUnid' id="WF_DocUnid" value="<%=docunid%>" ><!-- 表单的docunid-->
</div><!-- Hidden Field End-->
</fieldset></form></body></html>
