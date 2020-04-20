<%@ page import="cn.linkey.factory.BeanCtx,cn.linkey.dao.Rdb,cn.linkey.util.Tools,cn.linkey.doc.Document,java.util.*" %>
<%@ page errorPage="../error.jsp"%>
<%@ page contentType="text/html; charset=utf-8" %>
<%
    String dType = BeanCtx.g("wf_dtype", true);
    String docunid = Rdb.formatArg(BeanCtx.g("wf_docunid", true));
    String elid = Rdb.formatArg(BeanCtx.g("wf_elid"));
    Document eldoc = null;
    String sql = "select * from BPM_DevTypeConfig where  DesignerType='" + dType + "'";
    Document devdoc = Rdb.getDocumentBySql(sql);
    if (devdoc.isNull()) {
        BeanCtx.showErrorMsg("Error:Design type (wf_dtype = " + dType + ") does not exist");
        BeanCtx.close();
        return;
    }
    String title = "[" + devdoc.g("DesignerName") + "]";
    String sqlTableName = "";
    if (Tools.isNotBlank(docunid)) {
        //通过文档unid查找设计元素
        sqlTableName = devdoc.g("ElementTableName"); //设计元素所在表名
        sql = "select * from " + sqlTableName + " where WF_OrUnid='" + docunid + "'";
    }
    else if (Tools.isNotBlank(elid)) {
        //通过编号查找设计元素
        sqlTableName = devdoc.g("ElementTableName"); //设计元素所在表名
        sql = "select * from " + sqlTableName + " where " + devdoc.g("ElementidName") + "='" + elid + "'";
    }
    else {
        BeanCtx.showErrorMsg("Error:Design element does not exist");
        BeanCtx.close();
        return;
    }
    eldoc = Rdb.getDocumentBySql(sql); //得到设计元素文档数据    
    eldoc.s("ElementTableName", devdoc.g("ElementTableName"));
    eldoc.s("ElementName", devdoc.g("ElementName"));
    eldoc.s("ElementidName", devdoc.g("ElementidName"));
    elid = eldoc.g(devdoc.g("ElementidName"));
    String elName = eldoc.g(devdoc.g("ElementName"));
    title = elName;
    if (Tools.isBlank(docunid)) {
        docunid = eldoc.g("WF_OrUnid");
    }
%>
<!DOCTYPE html><html><head><title>开发中:<%=title %></title>
    <meta charset="UTF-8">

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
    <script type="text/javascript" src="../linkey/bpm/jscode/json.js"></script>
    <script>
    function formonload(){}
    
    //保存全部属性
    function SaveDocument(btnAction) {
        var postData = "WF_Action=" + btnAction;
        $("iframe").each(function(i) {
            if($(this).attr("src") != "about:blank") {
                var id = $(this).attr("id");
                if(id != undefined) { //动态增加的tab不能计算在内，addtab()函数中的iframe不能给定id
                    var winobj = document.getElementById(id).contentWindow;
                    if(postData == "") {
                        postData = winobj.serializeForm();
                    }
                    else {
                        postData += "&" + winobj.serializeForm();
                    }
                }
            }
        });
        if(postData.indexOf("&WF_DocUnid") == -1) {
            postData = "WF_DocUnid=<%=docunid%>&" + postData;
        }
        if(postData.indexOf("&WF_TableName") == -1){
            postData = "WF_TableName=<%=sqlTableName%>&" + postData;
        }
        if(postData.indexOf("&WF_RuleFdName") == -1){
            postData = "WF_RuleFdName=<%=eldoc.g("ElementName")%>&" + postData;
        }
        if(postData.indexOf("&WF_KeyFdName") == -1) {
            postData = "WF_KeyFdName=<%=eldoc.g("ElementidName")%>&" + postData;
        }
        mask();
        $.post("../rule?wf_num=R_S001_B002", postData, function(data){
            unmask();
            data = eval('(' + data + ')');
            if(data.Status == "error") {
                $.messager.alert('Info', data.msg, 'Error');
            }
        });
      
    }
    
    function PreviewView() {
        window.frames[0].PreviewView();
    }

    $(function() {
        $("#tabs").tabs({onSelect:loadUrl});
        loadUrl();
    });
    
    function loadUrl() {
        var tab = $('#tabs').tabs('getSelected');
        var url = tab.attr("url");
        var id = tab.attr("id");
        if($("#iframe_" + id).attr("src") == "about:blank") {
            $("#iframe_" + id).attr("src",url);
        }
    }
    
    function closeWin() {
        if(window.opener == undefined) {
            parent.$("#win").window('close');
        }
        else {
            window.close();
        }
    }
    
    //增加一个tab
    function addtab(url,text,id,icon) {
        if(icon == undefined) {
        	icon = "icon-event";
        }
        var tabItem = $('#tabs').tabs('getTab',text);
        if(tabItem == null) {
            $('#tabs').tabs('add', {
                title: text,
                id: id,
                iconCls: icon,
                content: '<iframe frameborder="0" src="'+url+'" width="100%" height="99%"  name="iframe_'+id+'" ></iframe>',
                closable: true
            });
        }
        else {
            $('#tabs').tabs('select',text);
        }
     }
        
    </script>
</head>
<body style="margin:0px;overflow:hidden">
<div class="easyui-layout" id="panel" fit="true" style="width:100%;">
    <div data-options="region:'north',split:false" border="false" style="height:31px;overflow:hidden">
        <div class="toptoolbar" id="toptoolbar" >
            | <a href="#" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-save'" onclick="SaveDocument()">保存全部属性</a>
            | <a href="#" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-plugin'" onclick="PreviewView()"><%=BeanCtx.getMsg("Designer","Preview")%></a>
            | <a href="#" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-reload'" onclick="location.reload();"><%=BeanCtx.getMsg("Designer","Refresh")%></a>
            | <a href="#" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-remove'" onclick="window.close();"><%=BeanCtx.getMsg("Designer","Close")%></a>
             <div style='float:right;padding-right:20px;padding-top:5px'><img src='../linkey/bpm/images/icons/page-next.gif' align='absmiddle'>当前位置:<%=elName+"("+elid+")" %></div>
        </div>
    </div>
    <div region="center" border="false" fit="false" >
        <div id="tabs" class="easyui-tabs" fit="true" tabPosition='<%=devdoc.g("tabPosition")%>' data-options="showHeader:true,tabHeight:30" >
        <%
            //获得设计插
            if (Rdb.getDbType().equals("MSSQL")) {
                sql = "select * from BPM_DevPluginConfig where status='1' and (','+DesignerType+',' like '%," + dType + ",%' or DesignerType='*') order by SortNum";
            }
            else if (Rdb.getDbType().equals("MYSQL")) {
                sql = "select * from BPM_DevPluginConfig where status='1' and (concat(',',DesignerType,',') like '%," + dType + ",%' or DesignerType='*') order by SortNum";
            }
            else {
                sql = "select * from BPM_DevPluginConfig where status='1' and (','||DesignerType||',' like '%," + dType + ",%' or DesignerType='*') order by SortNum";
            }
            Document[] dc = Rdb.getAllDocumentsBySql(sql);
            int i = 0;
            for (Document doc : dc) {
                String pluginid = doc.g("Pluginid");
                String pluginName = doc.g("PluginName");
                String pluginUrl = doc.g("url");
                if (pluginUrl.indexOf("?") == -1) {
                    pluginUrl += "?";
                }
                pluginUrl += "&" + request.getQueryString();
                pluginUrl = Tools.parserStrByDocument(eldoc, pluginUrl);
                if (pluginUrl.indexOf("wf_eldocunid") == -1) {
                    pluginUrl += "&wf_eldocunid=" + docunid;
                }
                out.println("<div title=\"" + pluginName + "\" url=\"" + pluginUrl + "\" id=\"" + pluginid + "\"  style=\"padding:0px;overflow:hidden\"><iframe src=\"about:blank\" id=\"iframe_" + pluginid + "\" frameborder='0' style=\"width:100%;height:100%\"></iframe></div>");
            }
            BeanCtx.close();
        %>
        </div>
    </div>
</div>
</body>
</html>
