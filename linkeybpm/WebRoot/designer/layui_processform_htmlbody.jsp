<%@ page import="cn.linkey.factory.BeanCtx,cn.linkey.dao.Rdb,cn.linkey.util.Tools,cn.linkey.doc.Document,java.util.*,cn.linkey.form.FormDesigner" %>
<%@page errorPage="../error.jsp" %>
<%@page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8" %>
<%
    String checked = "";
    String docunid = BeanCtx.g("wf_eldocunid");
    String sqlTableName = "BPM_FormList";
    String formNumber = "";
    String uiType = "";  //20180206添加UI类型
    Document formDoc = null;
    if (Tools.isBlank(docunid)) {
        BeanCtx.showErrorMsg(response, "Error : wf_docunid cannot be empty! WF_OrUnid=" + docunid);
        BeanCtx.close();
        return;
    }
    else {
        String sql = "select * from " + sqlTableName + " where WF_OrUnid='" + docunid + "'";
        formDoc = Rdb.getDocumentBySql(sqlTableName, sql); //得到表单文档数据
        formNumber = formDoc.g("FormNumber"); //表单编号
        uiType = formDoc.g("UIType");   //UI 类型
        if (formDoc.isNull()) {
            BeanCtx.showErrorMsg(response, "Error : Could not find the form! WF_OrUnid=" + docunid);
            BeanCtx.close();
            return;
        }
    }
    if (formDoc.g("UseCodeMode").equals("1")) {
        //重定向到代码模式
        RequestDispatcher rd = request.getRequestDispatcher("form_htmlcode.jsp");
        rd.forward(request, response);
        BeanCtx.close();
        return;
    }
    FormDesigner appFormDesigner = (FormDesigner) BeanCtx.getBean("FormDesigner");
%>
<!DOCTYPE html><html><head><title>Form Attribute</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<script>
    var formNumber = "<%=formNumber%>";
    var WF_Appid = "<%=formDoc.g("WF_Appid")%>";
</script>

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
<script type="text/javascript" src="../rule?wf_num=R_S001_B042&WF_Appid=<%=formDoc.g("WF_Appid")%>&uiType=<%=uiType %>"></script>
</head>

<body style="margin:1px;overflow:hidden">
<div class="easyui-layout" id="panel" fit="true" style="width:100%;">
    <div data-options="region:'north',split:true" style="height:39px">
        <div style="padding:2px;/*#border:1px solid #ddd;background:#f4f4f4#*/">
            | <a href="#" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-edit'" onclick="gotoCodeDesigner();">切换到纯代码模式</a>
            | <a href="#" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-reload'" onclick="location.reload();"><%=BeanCtx.getMsg("Designer", "Refresh")%>
        </a>
        </div>
    </div>

    <div data-options="region:'west',split:true" style="width:150px;">
        <div class="easyui-accordion" data-options="fit:true,border:false">
            <div title="基础控件" data-options="selected:true" style="padding:5px;">
                <%=appFormDesigner.getFormControlHtmlCode("1", uiType)%>
            </div>

            <div title="流程相关控件" style="padding:5px;">
                <%=appFormDesigner.getFormControlHtmlCode("2", uiType)%>
            </div>

            <div title="自定义控件" style="padding:5px">
                <%=appFormDesigner.getFormControlHtmlCode("3", uiType)%>
            </div>

            <div title="数据表字段" style="padding:0px" class="easyui-tree" id="fdtreepanel"
                 data-options="url:'../rule?wf_num=R_S001_B013&wf_docunid=<%=docunid%>',method:'get',animate:false,dnd:false,lines:true,onDblClick:function(node){insertText(node.text);},onContextMenu: function(e,node){e.preventDefault();$(this).tree('select',node.target);$('#treefdmm').menu('show',{left: e.pageX,top: e.pageY});}">
                <div id="treefdmm" class="easyui-menu" style="width:120px;">
                    <div onclick="var node = $('#fdtreepanel').tree('getSelected');insertText(node.text)" data-options="iconCls:'icon-form-input'">插入文本框</div>
                    <div onclick="var node = $('#fdtreepanel').tree('getSelected');insertCheckbox(node.text)" data-options="iconCls:'icon-form-input'">插入复选框</div>
                    <div onclick="var node = $('#fdtreepanel').tree('getSelected');insertRadio(node.text)" data-options="iconCls:'icon-form-input'">插入单选框</div>
                    <div onclick="var node = $('#fdtreepanel').tree('getSelected');insertDateonly(node.text)" data-options="iconCls:'icon-form-input'">日期控件</div>
                    <div onclick="var node = $('#fdtreepanel').tree('getSelected');insertSelect(node.text)" data-options="iconCls:'icon-form-input'">插入列表框</div>
                    <div onclick="var node = $('#fdtreepanel').tree('getSelected');insertTextarea(node.text)" data-options="iconCls:'icon-form-input'">多行文本框</div>
                    <div onclick="var node = $('#fdtreepanel').tree('getSelected');insertxfield(node.text)" data-options="iconCls:'icon-form-input'">插入计算文本</div>
                </div>
            </div>

        </div>
    </div>

    <div region="center" border="true" fit="false">
        <div class="easyui-layout" id="centerpanel" fit="true" style="width:100%;">
            <div data-options="region:'center',split:true" id="div_mainpanel" style="overflow:hidden;">
                <form method='post' name='linkeyform' id="linkeyform">
                    <textarea id="FieldConfig" name="FieldConfig" style="display:none;width:800px;height:200px"><%=formDoc.g("FieldConfig")%></textarea>
                    <textarea id="FormBody" name="FormBody" style="width:1px;height:1px">
                    <%
                        String formBody = formDoc.g("FormBody");
                        if (Tools.isBlank(formBody)) {
                            formBody = "<table class='linkeytable'><tr><td width='15%' align='right'></td><td width='30%'></td><td width='15%' align='right'></td><td width='30%'></td></tr><tr><td align='right' ></td><td></td><td align='right'></td><td></td></tr><tr><td align='right' ></td><td></td><td align='right'></td><td></td></tr><tr><td align='right' ></td><td></td><td align='right'></td><td></td></tr></table>";
                        }
                        formBody = formBody.replace("<", "&lt;");
                        formBody = formBody.replace(">", "&gt;");
                        out.println(formBody);
                    %>
                    </textarea>
                    <input name='WF_Action' id="WF_Action" value="" type="hidden">
                    <input name='WF_DocUnid' id="WF_DocUnid" value="<%=docunid%>" type="hidden">
                </form>
            </div>

            <div data-options="region:'south',split:true" style="height:60px;overflow:hidden">
                <textarea id="htmlcode" style="height:26px;width:100%"></textarea>
                <a href="#" onclick="editEditorHtml();return false;" title="Change html"><img src="../linkey/bpm/images/icons/sok.gif" border="0"></a>
            </div>

        </div>
    </div>

    <div data-options="region:'east',split:true" title="控件属性" style="width:280px;">
        <table id="elpg" class="easyui-propertygrid" data-options="
                columns:[[{field:'name',title:'属性名',width:80,sortable:true},{field:'value',title:'属性值',width:180,sortable:true}]],
                showGroup:true,
                fit:true,
                scrollbarSize:0
            ">
        </table>
    </div>

</div>
<div id="win"></div>
</body>
</html>
<%BeanCtx.close();%>
