$(function() {
    //表单初始化时执行
    try {
        InitAttachmentList(); //获得附件列表
        formonload(); //调用自定义的表单onload事件
    } catch (e) {}

    var dgObj = $('#dg');
    if (dgObj[0]) {
        var p = dgObj.datagrid('getPager');
        $(p).pagination({
            beforePageText: '第',
            afterPageText: '页    共 {pages} 页',
            displayMsg: '当前显示 {from} - {to} 条记录   共 {total} 条记录'
        });
    }
});

function SaveAppDocument(btnAction) {
    //表单验证
    var isValid = $("form").form('validate');
    if (isValid) {
        var r = formonsubmit();
        if (typeof(r) == "boolean" && r == false) {
            return;
        }
        else if (typeof(r) == "string") {
            postData += "&" + r;
        }
        //如果返回的时字符串则作为提交的附加数据
        r = validReadFieldIsNull();
        if (r == false) {
            return false;
        }
        //检测只读的字段是否有必填选项
    }
    else {
        alert(lang.ao_SaveAppDocument_msg02);
        return false;
    }

    //附加默认提交数据
    if (btnAction == undefined) {
        btnAction = "save";
    }
    var postData = "WF_Action=" + btnAction + "&wf_num=" + $("#WF_FormNumber").val();
    var chkStr = getNoCheckedBoxValue();
    if (chkStr != "") {
        postData += "&" + chkStr;
    }
    var textShowStr = getFieldTextShowData();
    if (textShowStr != "") {
        postData += "&" + textShowStr;
    }

    //序列化表单字段
    postData += "&" + $("form").serialize();

    //开始提交
    mask();
    $.post("r", postData, function(data) {
        unmask();
        var rs = eval('(' + data + ')');
        SaveDocumentCallBack(btnAction, rs);
    });
}

function EditDocument() {
    mask();
    var url = "r?wf_num=" + GetUrlArg("wf_num") + "&wf_docunid=" + GetUrlArg("wf_docunid") + "&wf_action=edit";
    location.href = url;
}

function PrintAppForm() {
    //打印表单
    var url = "r?wf_num=F_S024_A002&FormNumber=" + $("#WF_FormNumber").val() + "&DocUnid=" + WF_DocUnid;
    OpenUrl(url);
}
