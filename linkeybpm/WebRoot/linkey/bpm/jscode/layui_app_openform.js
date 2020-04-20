$(function() {
    //表单初始化时执行
    try {
        InitAttachmentList(); //获得附件列表
        formonload(); //调用自定义的表单onload事件
    } catch (e) {}

});

function historyback(){
	console.log("historyback");
	history.back();
	return false;
}

function SaveAppDocument(btnAction) {
	
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
