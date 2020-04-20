<%@ page import="cn.linkey.factory.BeanCtx,cn.linkey.dao.Rdb,cn.linkey.util.Tools,cn.linkey.doc.Document,java.util.*" %>
<%@page errorPage="../error.jsp" %>
<%@page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8" %>

<%
    String sqlTableName = "BPM_TableConfig";
    String docunid = BeanCtx.g("wf_eldocunid");
    String sql = "select * from " + sqlTableName + " where WF_OrUnid='" + docunid + "'";
    Document eldoc = Rdb.getDocumentBySql(sqlTableName, sql); //得到文档数据
    String elid = eldoc.g("Dataid"); //id编号
    docunid = eldoc.g("WF_OrUnid");//文档unid
    if (eldoc.isNull()) {
        BeanCtx.showErrorMsg(response, "Error : Could not find the table config! WF_OrUnid=" + docunid);
        BeanCtx.close();
        return;
    }
    String rdbTableName = eldoc.g("TableName"); //对应的sql中的数据库表
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
    function formonload() {
    }
    function insertRow() {
        stopEditRow();
        var i = $("#dg").datagrid('getData').length;
        $('#dg').datagrid('insertRow', {
            index: i,
            row: {"FdName": "", "FdNull": "N", "FdKey": "N", "FdType": "varchar", "FdLen": "50", "Remark": ""}
        });
    }

    function removeSelectedRow() {
        stopEditRow();
        var rows = $('#dg').datagrid('getSelections');
        if (!rows || rows.length == 0) {
            $.messager.alert('提示', '请选择要删除的数据!', 'info');
            return;
        }

        $.messager.confirm('Confirm', '您确定要删除选中字段吗?', function (r) {
            if (r) {
                var dg = $('#dg');
                $.each(rows, function (i, row) {
                    var index = dg.datagrid('getRowIndex', row);
                    dg.datagrid('deleteRow', index);
                });
            }
        });
    }

    var lastIndex;
    function editRow(rowIndex) {
        $("#dg").datagrid('unselectAll');
        $("#dg").datagrid('selectRow', rowIndex);
        if (lastIndex != rowIndex) {
            $('#dg').datagrid('endEdit', lastIndex);
            $('#dg').datagrid('beginEdit', rowIndex);
        }
        lastIndex = rowIndex;
    }

    function stopEditRow() {
        $('#dg').datagrid('endEdit', lastIndex);
        lastIndex = undefined;
    }

    var tableName = "<%=rdbTableName%>";
    if (tableName == "") {
        var tableName = parent.frames[0].$("#TableName").combobox('getValue');
    }

    //从sql表中载入数据
    function loadFromSqlTable() {
        mask();
        var url = "../rule?wf_num=R_S001_B010&DataSourceid=default&WF_TableName=" + tableName;
        var data = $("#dg").datagrid('getData');
        $.get(url, function (r) {
        	for (var i = 0; i < r.length; i++) {
                var ColumnName = r[i].ColumnName;
                var hflag = false;
                for (var j = 0; j < data.rows.length; j++) {
                    if (data.rows[j].FdName == ColumnName) {
                        hflag = true;
                    }
                }
                if (hflag == false) {
                    /* $('#dg').datagrid('appendRow', {
                        "FdName": ColumnName,
                        "FdNull": "N",
                        "FdType": "varchar",
                        "FdLen": "50",
                        "Remark": ""
                    }); */
                    
                    // 20181226 修复数据库表视图载入字段配置不全的问题---------------
                    $('#dg').datagrid('appendRow', {
                        "FdName": ColumnName, // 字段名称
                        "FdNull": r[i].FdNull, // 非空
                        "FdType": r[i].FdType, // 字段类型
                        "FdKey": r[i].FdKey, // 主键
                        "FdLen": r[i].FdLen, // 长度
                        "FdVal": r[i].FdVal, // 缺省值
                        "FdRemark": r[i].Remark // 备注
                    });
					// 20181226 END----------------------------------------
                    
                }
            }
            unmask();
        });
    }

    function clickRow(index) {
        $("#dg").datagrid('unselectAll');
        $("#dg").datagrid('selectRow', index);
        stopEditRow();
    }

    function formonsubmit() {
    }

    function serializeForm() {
        //统一的序列化表单方法
        stopEditRow();
        var rowData = $("#dg").datagrid('getData');
        var k = 0, keyStr = "";
        for (var i = 0; i < rowData.rows.length; i++) {
            if (rowData.rows[i].FdKey == "Y") {
                k++;
                keyStr += " " + rowData.rows[i].FdName;
                if (rowData.rows[i].FdNull == "N") {
                    alert("主键字段" + rowData.rows[i].FdName + "必须设置为不允许为空!");
                    return;
                }
            }
            //看长度设置是否合理
            var fdType = rowData.rows[i].FdType;
            if (fdType == "datetime" || fdType == "int" || fdType == "xml" || fdType == "text") {
                if (rowData.rows[i].FdLen != "") {
                    alert(rowData.rows[i].FdName + "字段的类型为" + fdType + "时不能设置长度");
                }
            } else if (fdType == "varchar" || fdType == "char") {
                if (rowData.rows[i].FdLen == "") {
                    alert(rowData.rows[i].FdName + "字段的类型为" + fdType + "时必须设置长度!");
                }
            }

        }
        if (k > 1) {
            alert("不能同时设置两个主键(" + keyStr + ")!");
            return;
        }
        return "FieldConfig=" + encodeURIComponent(JSON.stringify($("#dg").datagrid('getData')));
    }

</script>
</head>

<body style="margin:1px;overflow:auto" onclick="stopEditRow()">
<div class="toptoolbar" id="mm">
    | <a href="#" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-add'"
         onclick="insertRow()">新增字段</a>
    | <a href="#" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-remove'"
         onclick="removeSelectedRow()">删除选中字段</a>
    | <a href="#" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-cmp'" onclick="loadFromSqlTable()">从SQL表中载入</a>
</div>

<form method='post' id="linkeyform" style="display:none">
    <input name='WF_DocUnid' id="WF_DocUnid" value="<%=docunid%>">
<textarea name="FieldConfig" id="FieldConfig">
<%
    String jsonConfig = eldoc.g("FieldConfig");
    if (Tools.isBlank(jsonConfig)) {
        jsonConfig = "{total:1,\"rows\":[{\"FdName\":\"WF_OrUnid\",\"FdNull\":\"Y\",\"FdType\":\"varchar\",\"FdLen\":\"50\",\"FdKey\":\"Y\",\"FdRemark\":\"唯一编号\"},{\"FdName\":\"WF_DocCreated\",\"FdNull\":\"N\",\"FdType\":\"varchar\",\"FdKey\":\"N\",\"FdLen\":\"50\",\"FdRemark\":\"创建时间\"},{\"FdName\":\"WF_LastModified\",\"FdNull\":\"N\",\"FdType\":\"varchar\",\"FdKey\":\"N\",\"FdLen\":\"50\",\"FdRemark\":\"最后更新时间\"},{\"FdName\":\"WF_AddName\",\"FdNull\":\"N\",\"FdType\":\"varchar\",\"FdLen\":\"50\",\"FdKey\":\"N\",\"FdRemark\":\"创建者id\"},{\"FdName\":\"WF_AddName_CN\",\"FdNull\":\"N\",\"FdType\":\"varchar\",\"FdLen\":\"50\",\"FdKey\":\"N\",\"FdRemark\":\"创建者中文名\"},{\"FdName\":\"WF_AllReaders\",\"FdNull\":\"N\",\"FdType\":\"varchar\",\"FdLen\":\"500\",\"FdKey\":\"N\",\"FdRemark\":\"读者域\"},{\"FdName\":\"WF_AllAuthors\",\"FdNull\":\"N\",\"FdType\":\"varchar\",\"FdLen\":\"500\",\"FdKey\":\"N\",\"FdRemark\":\"作者域\"},{\"FdName\":\"XmlData\",\"FdNull\":\"N\",\"FdType\":\"xml\",\"FdLen\":\"\",\"FdKey\":\"N\",\"FdRemark\":\"XML数据\"}]}";
    }
    out.println(jsonConfig);
%>
</textarea>
</form>
<script>
    var jsonStr = $("#FieldConfig").val().trim();
    var colJson = eval('(' + jsonStr + ')');
</script>
<%
    String code = Rdb.getValueBySql("select DefaultCode from BPM_DevDefaultCode where CodeType='TableConfigFdCfg" + Rdb.getDbType() + "'");
    out.println(code);
%>
</body>
</html>
<%
    BeanCtx.close();
%>
