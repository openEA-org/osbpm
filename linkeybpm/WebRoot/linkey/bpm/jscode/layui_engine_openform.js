layui.use(['table','form','element'] , function(){
  var table = layui.table;
  var form = layui.form;
  var $ = layui.jquery;
  var element = layui.element;
		form.on('submit(formValidata)', function(data){
			switch(this.id)
			{
			case "BU1001":GoToNextNode();break;
			case "BU1002":GoToReassignment();break;
			case "BU1003":GoToPrvUser();break;
			case "BU1004":GoToPrvNode();break;
			case "BU1005":GoToFirstNode();break;
			case "BU1006":ReturnToPrevUser();break;
			case "BU1007":BackToReturnUser();break;
			case "BU1008":ReturnToAnyNode();break;
			case "BU1009":GoToNextSerialUser();break;
			case "BU1010":GoToNextParallelUser('Y');break;
			case "BU1011":GoToNextParallelUser('N');break;
			case "BU1012":GoToNextParallelUser('Y');break;
			case "BU1022":
				GoToSaveDoc();break;
			case "BU1024":CopyToDraftDoc();break;
			case "BU1025":CopyToDraftDoc();break;
			case "BU1026":GoToArchived();break;
			default:;
			}
			return false;
});
		
		form.on('checkbox(switchHand)', function(data){
//			  console.log(data.elem); //得到checkbox原始DOM对象
//			  console.log(data.elem.checked); //是否被选中，true或者false
//			  console.log(data.value); //复选框value值，也可以通过data.elem.value得到
//			  console.log(data.othis); //得到美化后的DOM对象
			var isChecked= data.elem.checked;
			if(isMobile()){  //手机端处理
				if(isChecked){
					$('#WF_SelectRemark_td').css('display','none'); 
					$('#signature_TmpRemark').css('display',''); 
					
					//20180427
				    $('#signName').canvasSignature({
						fillStyle:'transparent',	//生成图片背景色，默认为透明
						lineWidth:4,	//笔画粗细（尺寸），默认为四像素粗细
						strokeStyle:'red'	//笔画颜色，默认为黑色
					});
					
//					$('#win').window({width:"380",height:"500",modal:true,title:'手写签名'});
//				    $('#win').html("<iframe height='380' width='500' overflow='hidden' frameborder='0' src='page?wf_num=P_Signature_002'></iframe>");
					//window.location.href="page?wf_num=P_Signature_002";
				}else {
					$('#signature_TmpRemark').css('display','none'); 
					$('#WF_SelectRemark_td').css('display',''); 
				}
				
			}else{ //PC端处理
				if(isChecked){
					var signName = $('#signName')
					$('#WF_SelectRemark_td').css('display','none'); 
					$('#signature_TmpRemark').css('display',''); 
					//20180427
				    $('#signName').canvasSignature({
						fillStyle:'transparent',	//生成图片背景色，默认为透明
						lineWidth:4,	//笔画粗细（尺寸），默认为四像素粗细
						strokeStyle:'red'	//笔画颜色，默认为黑色
					});
				}else {
					$('#signature_TmpRemark').css('display','none'); 
					$('#WF_SelectRemark_td').css('display',''); 
				}
			}
			});  

		
});
$(function() {
    //表单初始化时执行
    if ($("#WF_CurrentNodeid").val() != "") {
        InitUserAndNode(); //初始化流程的处理单
    }
    InitAttachmentList(); //获得附件列表
    formonload(); //调用自定义的表单onload事件

    //解锁文档
    var url = "r?wf_num=R_S003_B053&DocUnid=" + $("#WF_DocUnid").val();
    $(window).unload(function() {
        $.get(url, function(data) {})
    });
    
    //清除重写20180502
	$('#clearCanvas').on('click',function(){
		//清除重写调用方式
		$('#signName').clearSignature();
	});
	
	//返回 
	$('#sureCanvas').on('click',function(){
		//$("#win").close();
		//window.history.back();
	});

});

//WF_Action为引擎动作id,WF_Params为post时的附加参数,msg为提示消息
function SaveDocument(WF_Action, msg, WF_Params) {
    if (WF_Action == undefined || WF_Action == "") {
        WF_Action = "EndUserTask";
    }
    if (GetRemarkStr() == false) {
        return;
    }

    var formData = "";
    var checkBoxData = getNoCheckedBoxValue();
    if (checkBoxData != "") {
        formData = checkBoxData;
    } //单选复选框提交
    var textShowStr = getFieldTextShowData();
    if (textShowStr != "") {
        formData += "&" + textShowStr;
    } //_show字段的内容
    if (WF_Action) {
        formData += "&WF_Action=" + WF_Action;
    } //要提交的动作，默认为EndUserTask办理完成
    var copyUserList = $("#WF_SelCopyUser").val();
    if (copyUserList != "" && copyUserList != undefined) {
        formData += "&WF_CopyUserList=" + encodeURIComponent($("#WF_SelCopyUser").val()); //要抄送的用户列表
    }
    if ($("#WF_SelSendSms:checked")[0] != null) {
        formData += "&WF_SendSms=" + $("#WF_SelSendSms").val(); //1表示手机短信发送标记
    }
    if (WF_Params != undefined && WF_Params != "") {
        formData += "&" + WF_Params; //传入的附加post参数,传入的WF_Params要进行utf-8编码
    }

    //开始表单验证
    //var isValid = $("#linkeyform").form('validate');
    var isValid = true; //写死了，这里不做验证了
    if (isValid) {
        var r = formonsubmit();
        if (typeof(r) == "boolean" && r == false) {
            return;
        }
        else if (typeof(r) == "string") {
            formData += "&" + r;
        } //如果返回的时字符串则作为提交的附加数据
        //r = validReadFieldIsNull();
        r == true; //写死了，这里不做验证了
        if (r == false) {
            return false;
        } //检测只读的字段是否有必填选项
    }
    else {
        alert(lang.eo_SaveDocument_msg);
        return;
    }

    //序列号表单字段
    formData += "&" + $("#linkeyform").serialize();

    //显示提示消息
    if (msg != "" && msg != undefined) {
        if (!confirm(msg)) {
            return false;
        }
    }

    //开始提交
    mask();
    var url = "r?wf_num=R_S003_B035";
    $.post(url, formData, function(data) {
        unmask();
        saveSignature(); //保存手写签名
        var rs = eval('(' + data + ')');
        if (isMobile()) {
            alert(rs.msg);
            location.replace("r?wf_num=P_S023_004");
        }
        else {
            SaveDocumentCallBack(WF_Action, rs);
        }
    });

}


/**
 * 保存手写签名 20180503
 * @returns
 */
function saveSignature(){
	 //20180427 添加手写签名
    if ($("#switchBox").is(":checked")) {
    	$("#WF_DocUnid").val();
    	var signName = $('#signName').createSignature('png');
    	var WF_ImageEncode = "&WF_ImageEncode=" + signName.replace(/\+/g, '%2B'); 
    	var signData = "isSignature=1&actionid=post&Processid=" + $("#WF_Processid").val() + "&DocUnid=" + $("#WF_DocUnid").val() + "&WF_CurrentNodeid=" + $("#WF_CurrentNodeid").val() + WF_ImageEncode;
    	var url = "r?wf_num=R_Signature_B001";
        $.post(url, signData, function(data) {
//            var rs = eval('(' + data + ')');
//            if (isMobile()) {
//                alert(rs.msg);
//                location.replace("r?wf_num=P_S023_004");
//            }
        });
    }
}

function GetRemarkStr() {
    //保存办理意见
    var RemarkStr = "";
    var obj = $("#WF_TmpRemark");
    if (obj.length > 0) {
        RemarkStr = obj.val();
        if (obj.attr("IsNull") == "2" && RemarkStr == "") {
            alert(lang.eo_GetRemarkStr_msg);
            return false;
        }
    }
    RemarkStr = RemarkStr.replace(/(\r\n)/gi, "<br>");
    $("#WF_Remark").val(RemarkStr);
}

function GoToNextNode() {
    //提交到下一环节
    var NodeList = ""; //获得选中的节点
    $('input[name="WF_NextNodeSelect"]:checked').each(function() {
        if (NodeList == "") {
            NodeList = $(this).val();
        }
        else {
            NodeList += "," + $(this).val();
        }
    });

    if ($('input[name="WF_NextNodeSelect"]').length > 0) { //看是否可以选择环节
        if (NodeList == "") {
            alert(lang.eo_GoToNextNode_msg01);
            return false;
        }
        //获得审批用户列表
        var UserList = "";
        var NodeArray = NodeList.split(",");
        for (var i = 0; i < NodeArray.length; i++) {
            var Nodeid = NodeArray[i];
            var obj = $("#WF_" + Nodeid);
            if (obj.length > 0) {
                var NodeName = obj.attr("NodeName");
                var MaxUserNum = obj.attr("MaxUserNum") - 0;
                var MinUserNum = obj.attr("MinUserNum") - 0;
                var userList = obj.val() + "";
                var UserArray = userList.split(",");
                if (UserArray.length > MaxUserNum && MaxUserNum != 0) {
                    alert(lang.eo_GoToNextNode_msg02.replace('{0}', NodeName));
                    return false;
                }
                if ((userList == "null" || userList == "") && MinUserNum != 0) {
                    alert(lang.eo_GoToNextNode_msg03.replace("{0}", NodeName));
                    return false;
                }
                if (UserArray.length < MinUserNum) {
                    alert(lang.eo_GoToNextNode_msg04.replace("{0}", NodeName));
                    return false;
                }
                for (var j = 0; j < UserArray.length; j++) {
                    if (UserArray[j] != "") {
                        if (UserList == "") {
                            UserList = UserArray[j] + "$" + Nodeid;
                        }
                        else {
                            UserList += "," + UserArray[j] + "$" + Nodeid;
                        }
                    }
                }
            }
        }
        $("#WF_NextNodeid").val(NodeList);
        $("#WF_NextUserList").val(UserList);
    }
    SaveDocument("EndUserTask", lang.eo_GoToNextNode_msg05);
}

function ShowSendToOtherUser(CheckBoxObj) //显示转他人处理的相关字段
{
    //控制按扭和人员选择的显示和权限
    if ($("#SpanSendToOtherUser").css("display") != "none") {
        $("#SpanSendToOtherUser").css("display", "none");
        $("#BU1002").linkbutton('disable'); //转他人处理按扭
        $("#BU1001").linkbutton('enable'); //提交下一环节按扭
        $("#BU1003").linkbutton('enable'); //回退上一用户
        $("#BU1004").linkbutton('enable'); //回退上一环节按扭
        $("#BU1005").linkbutton('enable'); //回退首环节按扭
        $("#BU1008").linkbutton('enable'); //回退任意环节
    }
    else {
        $("#SpanSendToOtherUser").css("display", "");
        $("#BU1002").linkbutton('enable'); //转他人处理按扭
        $("#BU1001").linkbutton('disable'); //提交下一环节按扭
        $("#BU1004").linkbutton('disable'); //回退上一环节按扭
        $("#BU1003").linkbutton('disable'); //回退上一用户
        $("#BU1005").linkbutton('disable'); //回退首环节按扭
        $("#BU1008").linkbutton('disable'); //回退任意环节
    }
    //隐藏和显示路由选择以及人员选择
    var TableObj = $("#ApprovalTable");
    if (CheckBoxObj.checked) {
        for (var i = 0; i < TableObj[0].rows.length; i++) {
            if (TableObj[0].rows[i].id.indexOf("UserTr_") != -1) {
                TableObj[0].rows[i].style.display = "none";
            }
        }
    }
    else {
        TableObj[0].rows[0].style.display = "";
        ShowRouterUser();
    }

}


function InitUserAndNode() {
    HiddenButton();
    ShowRouterUser();
}

function HiddenButton() {
    //隐藏相应的按扭
    if ($("#WF_IsFirstNodeFlag").val() == "1") {
        try {
            $("#BU1005").css("display", "none");
        } catch (e) {} //如果已经是首环节则隐藏
    }
    if ($("#TrOtherUser").length == 0) {
        try {
            $("#BU1002").css("display", "none");
        } catch (e) {
            alert(e.message);
        } //如果没有转他人处理功能，则隐藏
    }
}

function ShowRouterUser() {
    //显示路由用户选择
    var Nodeid = "",
        x;
    var obj = $("[name='WF_NextNodeSelect']");
    if (obj.length == 0) return;
    //有多个路由选项
    if (obj.length > 1) {
        for (var i = 0; i < obj.length; i++) {
            Nodeid = obj[i].value;
            var tmpArray = Nodeid.split(",");
            for (x = 0; x < tmpArray.length; x++) {
                var UserObj = $("#UserTr_" + tmpArray[x]);
                if (UserObj.length > 0) {
                    if (obj[i].checked == true) {
                        UserObj.css("display", "");
                    }
                    else {
                        UserObj.css("display", "none");
                    }
                }
            }
        }
    }
    else { //只有一个路由选项
        obj[0].checked = true; //只有一个路由选项时默认选中
        Nodeid = obj.val();
        var tmpArray = Nodeid.split(",");
        for (x = 0; x < tmpArray.length; x++) {
            var UserObj = $("#UserTr_" + tmpArray[x]);
            if (UserObj.length > 0) {
                if (obj.is(':checked')) {
                    UserObj.css("display", "");
                }
                else {
                    UserObj.css("display", "none");
                }
            }
        }
    }
}

function GoToArchived() {
    if ($("#WF_TmpRemark").val().trim() == "") {
        alert(lang.eo_GoToArchived_msg01);
        return false;
    }
    SaveDocument("GoToArchived", lang.eo_GoToArchived_msg01);
}

function GoToPrvNode() {
    if ($("#WF_TmpRemark").val().trim() == "") {
        alert(eo_GoToPrvNode_msg01);
        return false;
    }
    SaveDocument("GoToPrevNode", lang.eo_GoToPrvNode_msg02);
}

function GoToPrvUser() {
    if ($("#WF_TmpRemark").val().trim() == "") {
        alert(lang.eo_GoToPrvUser_msg01);
        return false;
    }
    SaveDocument("GoToPrevUser", lang.eo_GoToPrvUser_msg02);
}

function GoToNextParallelUser(IsAgree) { //提交给下一会签用户
    if (IsAgree == undefined || IsAgree == "") {
        IsAgree = "Y";
    }
    SaveDocument("GoToNextParallelUser", lang.eo_GoToNextNode_msg05, "WF_IsAgree=" + IsAgree);
}

function GoToNextSerialUser() { //提交给下一串行审批用户
    SaveDocument("GoToNextSerialUser", lang.eo_GoToNextNode_msg05);
}

function GoToFirstNode() {
    if ($("#WF_TmpRemark").val().trim() == "") {
        alert(lang.eo_GoToFirstNode_msg01);
        return false;
    }
    $('#win').dialog({
        title: lang.eo_GoToFirstNode_msg02,
        width: 350,
        height: 150,
        closed: false,
        cache: false,
        modal: true,
        href: 'r?wf_num=R_S003_B040&WF_Action=GoToFirstNode',
        buttons: [{
            text: lang.eo_submit_msg,
            iconCls: 'icon-ok',
            handler: function() {
                var v = $('input[name="IsBackFlag"]:checked').val();
                SaveDocument("GoToFirstNode", "", "WF_IsBackFlag=" + v);
            }
        }, {
            text: lang.eo_cancel_msg,
            iconCls: 'icon-remove',
            handler: function() {
                $('#win').dialog('close');
            }
        }]
    });
}

function ReturnToAnyNode() {
    //回退任意环节
    if ($("#WF_TmpRemark").val().trim() == "") {
        alert(lang.eo_ReturnToAnyNode_msg01);
        return false;
    }
    $('#win').dialog({
        title: lang.eo_back_msg,
        width: 350,
        height: 210,
        closed: false,
        cache: false,
        modal: true,
        href: 'r?wf_num=R_S003_B040&WF_Action=ReturnToAnyNode&WF_CurrentNodeid=' + $("#WF_CurrentNodeid").val() + '&WF_DocUnid=' + $("#WF_DocUnid").val(),
        buttons: [{
            text: lang.eo_submit_msg,
            iconCls: 'icon-ok',
            handler: function() {
                var returnNodeid = $("#WF_ReturnNodeid").val();
                if (returnNodeid == "") {
                    alert(lang.eo_ReturnToAnyNode_msg02);
                    return;
                }
                var returnUserid = $("#WF_ReturnUserid").val();
                if (returnUserid == "") {
                    alert(lang.eo_ReturnToAnyNode_msg03);
                    return;
                }
                $("#WF_NextNodeid").val(returnNodeid);
                $("#WF_NextUserList").val(returnUserid);
                var v = $('input[name="IsBackFlag"]:checked').val();
                SaveDocument("ReturnToAnyNode", "", "WF_IsBackFlag=" + v);
            }
        }, {
            text: lang.eo_cancel_msg,
            iconCls: 'icon-remove',
            handler: function() {
                $('#win').dialog('close');
            }
        }]
    });
}

function getReturnAnyNodeUser() {
    //根据选择回退环节获得可回退的用户列表
    var userid = $("#WF_ReturnNodeid option:selected").attr("userid");
    var userName = $("#WF_ReturnNodeid option:selected").attr("userName");
    $("#WF_ReturnUserid").val(userid);
    $("#WF_ReturnUserid_show").text(userName);
}

function GoToSaveDoc() {
    $("#WF_Remark").val($("#WF_TmpRemark").val());
    var formData = $("form").serialize();
    var checkBoxData = getNoCheckedBoxValue();
    if (checkBoxData != "") {
        formData += "&" + checkBoxData;
    }
    var textShowStr = getFieldTextShowData();
    if (textShowStr != "") {
        formData += "&" + textShowStr;
    } //_show字段的内容
    var r = formonsubmit();
    if (typeof(r) == "boolean" && r == false) {
        return;
    }
    else if (typeof(r) == "string") {
        formData += "&" + r;
    } //如果返回的时字符串则作为提交的附加数据
    var url = "r?wf_num=R_S003_B031";
    if (!confirm(lang.eo_GoToSaveDoc_msg)) {
        return false;
    }
    mask();
    $.post(url, formData, function(data) {
        unmask();
    });
}

function CopyToDraftDoc() {
    var formData = $("form").serialize();
    var checkBoxData = getNoCheckedBoxValue();
    if (checkBoxData != "") {
        formData += "&" + checkBoxData;
    }
    var textShowStr = getFieldTextShowData();
    if (textShowStr != "") {
        formData += "&" + textShowStr;
    } //_show字段的内容
    var r = formonsubmit();
    if (typeof(r) == "boolean" && r == false) {
        return;
    }
    else if (typeof(r) == "string") {
        formData += "&" + r;
    } //如果返回的时字符串则作为提交的附加数据
    var url = "r?wf_num=R_S003_B069";
    if (!confirm(lang.eo_CopyToDraftDoc_msg)) {
        return false;
    }
    mask();
    $.post(url, formData, function(data) {
        unmask();
    });
}

function GoToReassignment() {
    //转交给其他用户
    var UserList = $("#WF_OtherUserList").val();
    if (UserList == "" || UserList == null) {
        alert(lang.eo_GoToReassignment_msg01);
        return false;
    }
    $("#WF_NextUserList").val(UserList);
    var ReassignmentFlag = "1"; //转发后不需要返回
    var backobj = $("#WF_SendToOtherUserAndBack");
    if (backobj.length > 0) {
        if (backobj[0].checked == true) {
            ReassignmentFlag = "2";
        }
    } //转发后需要返回
    SaveDocument("GoToOthers", lang.eo_GoToReassignment_msg02, "WF_ReassignmentFlag=" + ReassignmentFlag);
}

function ReturnToPrevUser() {
    //转交给其他用户
    SaveDocument("BackToDeliver", lang.eo_ReturnToPrevUser_msg);
}

function BackToReturnUser() {
    //返回给回退者
    SaveDocument("BackToReturnUser", lang.eo_BackToReturnUser_msg);
}

function SelectRemark(obj) {
    //常用意见选择
    $("#WF_TmpRemark").val(obj.value);
}

function AddToMyRemark() {
    //加入常用办理意见中
    var url = "rule?wf_num=R_S003_B037";
    if ($("#WF_TmpRemark").val() == "") {
        alert(lang.eo_AddToMyRemark_msg);
        return;
    }
    $.post(url, {
        Remark: $("#WF_TmpRemark").val()
    }, function(data) {
        data = eval('(' + data + ')');
        alert(data.msg);
    });
}

function ShowWorkflow(processid, docUnid) {
    var url = "rule?wf_num=R_S003_B066&Processid=" + processid + "&DocUnid=" + docUnid;
    OpenUrl(url);
}

function ShowRemark(processid, docUnid) {
    var url = "page?wf_num=P_S003_001&Processid=" + processid + "&DocUnid=" + docUnid;
    if (isMobile()) {
        url = "page?wf_num=P_S003_002&Processid=" + processid + "&DocUnid=" + docUnid;
    }
    OpenUrl(url, 200, 200);
}

function WF_OpenAttachmentLog() {
    $('#win').window({
        width: 750,
        height: 450,
        modal: true,
        title: lang.eo_OpenAttachmentLog_msg
    });
    $('#win').html("<iframe height='400px' width='100%' frameborder='0' src='view?wf_num=V_S003_G002&DocUnid=" + $("#WF_DocUnid").val() + "'></iframe>");
}

function WF_SystemTools() {
    var url = "page?wf_num=P_S014_002&DocUnid=" + $("#WF_DocUnid").val() + "&Processid=" + $("#WF_Processid").val();
    OpenUrl(url, 100, 100);
}

function AddToFavorites() {
    mask();
    var url = "rule?wf_num=R_S003_B056&DocUnid=" + $("#WF_DocUnid").val();
    $.get(url, function(data) {
        data = eval('(' + data + ')');
        alert(data.msg);
        unmask();
    })
}

//折叠指定子表单13-04-08
function ExpandSubForm(spanobj) {
    if (spanobj.className == "CollapseSubForm") {
        spanobj.className = "ExpandSubForm";
    }
    else {
        spanobj.className = "CollapseSubForm";
    }
    //隐藏或显示内容
    var subformObj = $("#SUBFORM_" + spanobj.id)[0];
    if (subformObj.style.display == "none") {
        subformObj.style.display = "";
    }
    else {
        subformObj.style.display = "none";
    }
}

//折叠或显示子表单的内容
function ExpandSubFormBody(spanobj) {
    if (spanobj.className == "CollapseSubForm") {
        spanobj.className = "ExpandSubForm";
    }
    else {
        spanobj.className = "CollapseSubForm";
    }
    //隐藏或显示内容
    var subformObj = $("#SUBFORM_" + spanobj.id)[0];
    if (subformObj.innerHTML == "") {
        subformObj.innerHTML = "<iframe id='subframe_" + spanobj.id + "' src='rule?wf_num=R_S003_B061&OrUnid=" + spanobj.id + "' width='100%' height='100px' frameborder='0' ></iframe>";
    }
    if (spanobj.className == "ExpandSubForm") {
        subformObj.style.display = "";
    }
    else {
        subformObj.style.display = "none";
    }

}

//折叠处理单
function ExpandApprovalForm(spanobj) {
    if (spanobj.className == "CollapseSubForm") {
        spanobj.className = "ExpandSubForm";
    }
    else {
        spanobj.className = "CollapseSubForm";
    }
    var formObj = $("#ApprovalTable")[0];
    if (formObj.style.display == "none") {
        formObj.style.display = "";
    }
    else {
        formObj.style.display = "none";
    }
}

//打印处理单
function PrintForm() {
    var url = "rule?wf_num=R_S005_B004&wf_action=read&DocUnid=" + $("#WF_DocUnid").val();
    OpenUrl(url);
}

//Word正文
function OpenWordDoc(fileName) {
    if (!fileName) {
        fileName = "正文.doc";
    }
    var url = "form?wf_num=F_S024_A004&Processid=" + $("#WF_Processid").val() + "&DocUnid=" + $("#WF_DocUnid").val() + "&Nodeid=" + $("#WF_CurrentNodeid").val() + "&IsFirstNodeFlag=" + $("#WF_IsFirstNodeFlag").val() + "&IsNewDocFlag=" + $("#WF_NewDocFlag").val() + "&FileName=" + encodeURIComponent(fileName);
    OpenUrl(url);
}
//选择文号
function SelWenHao(obj) {
    $('#win').window({
        width: 600,
        height: 300,
        modal: true,
        title: lang.eo_SelWenHao_msg
    });
    $('#win').html("<iframe height='200px' width='100%' frameborder='0' src='form?wf_num=F_S013_A007&obj=" + obj.id + "'></iframe>");
}
//附件模板
function OpenAttTemplate(fileName, docUnid) {
    if (docUnid == "" || docUnid == undefined) {
        alert(lang.eo_OpenAttTemplate_msg);
        return false;
    }
    var url = "form?wf_num=F_S024_A005&Processid=" + $("#WF_Processid").val() + "&DocUnid=" + $("#WF_DocUnid").val() + "&Nodeid=" + $("#WF_CurrentNodeid").val() + "&IsNewDocFlag=" + $("#WF_NewDocFlag").val() + "&FileName=" + encodeURIComponent(fileName) + "&TemplateDocUnid=" + docUnid;
    OpenUrl(url);
}

/*移动终端函数 start */
function MobileDeleteNodeUser(nodeid, userid) {
    $("#U_" + nodeid + "_" + userid).html("");
    var userList = $("#WF_" + nodeid).val();
    var userArray = userList.split(",");
    var newUserList = "";
    for (var i = 0; i < userArray.length; i++) {
        if (userArray[i] != userid) {
            if (newUserList == "") {
                newUserList = userArray[i];
            }
            else {
                newUserList += "," + userArray[i];
            }
        }
    }
    $("#WF_" + nodeid).val(newUserList);
}
/*移动终端函数 end */
/*UI*/
$(document).ready(function() {
    $("#ToolbarTr td:first-child").css('display', 'none');
    $("#ToolbarTr").find("td").attr("colspan", "2"); //流程表单按钮框
    var height_engine = $(window).height(); //流程表单设置最小高度
    $("form").css("min-height", height_engine); //流程表单设置最小高度
    $(window).resize(function() { //改变窗口大小时，流程子表单宽度自适应
        $(".panel-header").css('width', $("#ApprovalForm").width() - 12);
        $(".easyui-panel").css('width', $("#ApprovalForm").width() - 1);
    });
});