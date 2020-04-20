function GridDeleteDoc(dg) {
    //Grid文档删除函数
    var rows = dg.datagrid('getSelections');
    if (!rows || rows.length == 0) {
        $.messager.alert(lang.vw_GridDeleteDoc_msg01, lang.vw_GridDeleteDoc_msg02, 'info');
        return;
    }

    $.messager.confirm('Confirm', lang.vw_GridDeleteDoc_msg03, function (r) {
        if (r) {
            mask();
            var WF_OrUnid = "";
            $.each(rows, function (i, row) {
                if (WF_OrUnid == "") {
                    WF_OrUnid = row.WF_OrUnid;
                }
                else {
                    WF_OrUnid += "," + row.WF_OrUnid;
                }
            });
            $.post("readgridaction", {WF_Action: 'del', WF_OrUnid: WF_OrUnid, wf_num: GridNum}, function (data) {
                unmask();
                data = eval('(' + data + ')');
                $.messager.alert('Info', data.msg, 'Error');
                dg.datagrid('reload');
            });
        }
    });
}
function GridCopyDoc(dg) {
    //Grid文档拷贝函数
    var rows = dg.datagrid('getSelections');
    if (!rows || rows.length == 0) {
        $.messager.alert(lang.vw_GridCopyDoc_msg01, lang.vw_GridCopyDoc_msg02, 'info');
        return;
    }

    $.messager.confirm('Confirm', lang.vw_GridCopyDoc_msg03, function (r) {
        if (r) {
            mask();
            var WF_OrUnid = "";
            $.each(rows, function (i, row) {
                if (WF_OrUnid == "") {
                    WF_OrUnid = row.WF_OrUnid;
                }
                else {
                    WF_OrUnid += "," + row.WF_OrUnid;
                }
            });
            $.post("readgridaction", {WF_Action: 'copy', WF_OrUnid: WF_OrUnid, wf_num: GridNum}, function (data) {
                unmask();
                data = eval('(' + data + ')');
                $.messager.alert('Info', data.msg, 'Error');
                dg.datagrid('reload');
            });

        }
    });
}

function GridDoSearch(v) {
    //默认搜索函数
    $('#dg').datagrid({queryParams: {searchStr: v}});
    $('#dg').datagrid('reload');
}

function GridBtnClick(dg, btnid) {
    //Grid按扭点击函数
    var rows = dg.datagrid('getSelections');
    if (!rows || rows.length == 0) {
        $.messager.alert(lang.vw_GridCopyDoc_msg01, lang.vw_GridBtnClick_msg01, 'info');
        return;
    }

    $.messager.confirm('Confirm', lang.vw_GridBtnClick_msg02, function (r) {
        if (r) {
            mask();
            var WF_OrUnid = "";
            $.each(rows, function (i, row) {
                if (WF_OrUnid == "") {
                    WF_OrUnid = row.WF_OrUnid;
                }
                else {
                    WF_OrUnid += "," + row.WF_OrUnid;
                }
            });
            $.post("readgridaction", {
                WF_Action: 'btnClick',
                WF_Btnid: btnid,
                WF_OrUnid: WF_OrUnid,
                wf_num: GridNum
            }, function (data) {
                unmask();
                var data = eval('(' + data + ')');
                alert(data.msg);
                dg.datagrid('reload');
            });

        }
    });
}

function EditorGridSave(dg) {
    //获得所有修改的数据,视图单独保存时使用
    var delRow = JSON.stringify(dg.datagrid('getChanges', 'deleted'));
    var newRow = JSON.stringify(dg.datagrid('getChanges', 'inserted'));
    var editRow = JSON.stringify(dg.datagrid('getChanges', 'updated'));
    var parentDocUnid = $("#WF_DocUnid").val();
    if (parentDocUnid == "" || parentDocUnid == undefined) {
        parentDocUnid = GetUrlArg("wf_docunid");
    }

    //保存修改结果
    mask();
    $.post("editoraction", {
        WF_ParentDocUnid: parentDocUnid,
        WF_Action: 'saveEditor',
        wf_num: GridNum,
        delRow: delRow,
        newRow: newRow,
        editRow: editRow
    }, function (data) {
        data = eval('(' + data + ')');
        if (data.Status == "error") {
            alert(data.msg);
        }
        else {
            dg.datagrid('reload');
        }
        unmask();
    });
}

function saveViewData() {
    //同步请求,一般嵌入到页中时和主表单一起保存时使用,保存成功返回true保存失败返回false
    var saveFlag = false;
    stopEditRow();//首先停止编辑视图
    var dg = $("#dg");
    var delRow = JSON.stringify(dg.datagrid('getChanges', 'deleted'));
    var newRow = JSON.stringify(dg.datagrid('getChanges', 'inserted'));
    var editRow = JSON.stringify(dg.datagrid('getChanges', 'updated'));
    var parentDocUnid = $("#WF_DocUnid").val();
    if (parentDocUnid == "" || parentDocUnid == undefined) {
        parentDocUnid = GetUrlArg("wf_docunid");
    }
    $.ajax({
        url: 'editoraction',
        data: {
            WF_ParentDocUnid: parentDocUnid,
            WF_Action: 'saveEditor',
            wf_num: GridNum,
            delRow: delRow,
            newRow: newRow,
            editRow: editRow
        },
        cache: false,
        async: false,
        type: "POST",
        dataType: 'json',
        success: function (data) {
            if (data.Status == "error") {
                alert(data.msg);
                saveFlag = false;
            }
            else {
                dg.datagrid('acceptChanges');
                saveFlag = true;
            }
        }
    });
    return saveFlag;
}

function getGridVal(dg, id) {
    //通用获取选中grid的列值方法
    var rows = dg.datagrid('getSelections');
    var val = "";
    $.each(rows, function (i, row) {
        if (val == "") {
            val = eval("row." + id);
        }
        else {
            val += "," + eval("row." + id);
        }
    });
    return val;
}

var fdList = "";//用来存动态表格的字段名称列表
function getEditorGridData(dg) {
    //序列化可编辑grid的数据作为表单提交时使用
    dg.datagrid('acceptChanges');
    var dgdata = dg.datagrid('getData');
    var j = 1;
    var postdata = "";
    for (var i = 0; i < dgdata.rows.length; i++) {
        var rowdata = dgdata.rows[i];
        $.each(rowdata, function (n, v) {
            if (postdata != "") {
                postdata += "&";
            }
            postdata += n + "_" + j + "=" + encodeURIComponent(v);
            if (j == 1) {
                if (fdList != "") {
                    fdList += ",";
                }
                fdList += n;
            }
        });
        j++;
    }
    if (postdata != "") {
        postdata += "&WF_DwEditorFdList=" + fdList
    }
    return postdata;
}

function excel2grid() {
    //Excel导入到grid
    $('#win').window({width: 600, height: 260, modal: true, title: lang.vw_excel2grid_msg01});
    $('#win').html("<iframe height='220px' width='100%' frameborder='0' src='form?wf_num=F_S005_A009&GridNum=" + GridNum + "'></iframe>");
}

function grid2excel() {
    //grid导出到excel
    var rows = $("#dg").datagrid('getRows');
    if (!rows || rows.length == 0) {
        $.messager.alert(lang.vw_GridDeleteDoc_msg01, lang.vw_grid2excel_msg01, 'info');
        return;
    }

    $.messager.confirm('Confirm', lang.vw_grid2excel_msg02, function (r) {
        if (r) {
            mask();
            var WF_OrUnid = "";
            $.each(rows, function (i, row) {
                if (WF_OrUnid == "") {
                    WF_OrUnid = row.WF_OrUnid;
                }
                else {
                    WF_OrUnid += "," + row.WF_OrUnid;
                }
            });
            $.post("rule?wf_num=R_S005_B014", {WF_OrUnid: WF_OrUnid, GridNum: GridNum}, function (data) {
                unmask();
                var data = eval('(' + data + ')');
                if (data.Status == "ok") {
                    var url = "rule?wf_num=R_S001_B020&filename=" + data.msg;
                    window.open(url, 'pwin', 'height=20,width=10,top=10000,left=10000');
                }
                else {
                    alert(data.msg);
                }
            });

        }
    });
}
function pkgData(WF_TableName) {
    //打包下载
    var dg = $('#dg');
    var rows = dg.datagrid('getSelections');
    if (!rows || rows.length == 0) {
        $.messager.alert('提示', '请选择要打包的文档!', 'info');
        return;
    }

    $.messager.confirm('Confirm', '您确定要打包选中文档吗?', function (r) {
        if (r) {
            mask();
            var WF_OrUnid = "";
            $.each(rows, function (i, row) {
                if (WF_OrUnid == "") {
                    WF_OrUnid = row.WF_OrUnid;
                }
                else {
                    WF_OrUnid += "," + row.WF_OrUnid;
                }
            });
            $.post("rule?wf_num=R_S001_B072", {WF_OrUnid: WF_OrUnid, WF_TableName: WF_TableName}, function (data) {
                unmask();
                if (data.Status == "ok") {
                    var url = "rule?wf_num=R_S001_B020&filename=" + data.fileName;
                    location.href = url;
                }
                else {
                    alert(data.msg);
                }
            });

        }
    });
}
//列表分页部分中文显示
$(function () {
	var dgObj = $('#dg');
    if (dgObj[0]) {
        var p = dgObj.datagrid('getPager');
        $(p).pagination({
            beforePageText: '第',
            afterPageText: '页    共 {pages} 页',
            displayMsg: '当前显示 {from} - {to} 条记录   共 {total} 条记录'
        });
    }
	
/*	// 20190213 修复视图中，点击搜索后，分页信息变英文   但视图导航树有问题，先注销
	var dgObj = $('#dg');
    if (dgObj[0]) {
        dgObj.datagrid({

        	//  onLoadSuccess: function(d) {
        	// 20190220 将onLoadSuccess 替换为 onBeforeLoad
        	onBeforeLoad: function(d) {
            	
                var p = dgObj.datagrid('getPager');
                $(p).pagination({
                    beforePageText: '第',
                    afterPageText: '页    共 {pages} 页',
                    displayMsg: '当前显示 {from} - {to} 条记录   共 {total} 条记录'
                });
            }
        });
    }
    // 20190213 END
*/    
    // 20181227 新增监听视图滚动条，表头随横向滚动条滚动
    var w1 = $('.datagrid-header')[1];
    if (w1 != undefined && !isMobile()) {  //20190226  移动端不需要加偏移 && !isMobile() 
    	w1.style.position = 'relative';
        var w2 = $('.datagrid-body')[1];
        w2.onscroll = function(e) {
            w1.style.left = -w2.scrollLeft + 'px';
        }
    }
    // 20181227 END
});