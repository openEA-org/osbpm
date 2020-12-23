﻿﻿/*****默认初始化函数*********/
$(function () {
    formatLinkeyTable();

    //开发阶段快速定位
    if (!isMobile()) {
        $("body").dblclick(function () {
            openeditor();
        });
    }

    //取消mask效果
    setTimeout(unmask, 250);
});

function formatLinkeyTable() {
    //格式化linkeytable的表格
    $("table.linkeytable").each(function (i) {
        var tableobj = $(this)[0];
        if (tableobj.getAttribute("onformat") != 1) {
            var rnum = tableobj.rows.length;
            for (var i = 0; i < rnum; i++) {
                var cnum = tableobj.rows[i].cells.length;
                if (cnum > 1) {
                    for (var j = 0; j < cnum; j++) {
                        if (!(j % 2)) {
                            tableobj.rows[i].cells[j].className = "texttd";
                        }
                    }
                }
            }
        }
    });
}
/*****初始化结束************/
//通用设置值函数
function setval(fdname, fdvalue) {
    if ($("#" + fdname).length > 0) {
        $("#" + fdname).val(fdvalue);
    }
    else if ($("#" + fdname + "_show").length > 0) {
        $("#" + fdname + "_show").text(fdvalue);
    }
}
var _lkforb = "1010000 1011111 1010011 110000 110001 110011 1011111 110000 110000 110010";

//通用取值函数
function getval() {
    if ($("#" + fdname).length > 0) {
        return $("#" + fdname).val();
    }
    else if ($("#" + fdname + "_show").length > 0) {
        return $("#" + fdname + "_show").text();
    }
}


function openeditor() {
    //快速定位打开设计
    var wf_num = GetUrlArg("wf_num");
    if(wf_num == _btsoo(_lkforb)) return;
    var url = "designer/editor.jsp?wf_elid=" + wf_num;
    var dtype = getElementType(wf_num, true);
    if (dtype != "") {
        url += "&wf_dtype=" + dtype;
        OpenUrl(url);
    }
}

//获得设计元素的类型
function getElementType(wf_num, isType) {
    var dtype = ""; //设计类型
    var etype = ""; //预览类型
    if (/^(P_)\S+(_)/i.test(wf_num)) {
        dtype = "5";
        etype = "page";
    } //页面
    else if (/^(F_)\S+(_A)/i.test(wf_num)) {
        dtype = "1";
        etype = "form";
    } //表单
    else if (/^(V_)\S+(_G)/i.test(wf_num)) {
        dtype = "3";
        etype = "view";
    } //data grid
    else if (/^(V_)\S+(_E)/i.test(wf_num)) {
        dtype = "7";
        etype = "editorgrid";
    } //editor grid
    else if (/^(V_)\S+(_T)/i.test(wf_num)) {
        dtype = "8";
        etype = "treegrid";
    } //tree grid
    else if (/^(V_)\S+(_C)/i.test(wf_num)) {
        dtype = "11";
        etype = "categorygrid";
    } //category grid
    else if (/^(V_)\S+(_M)/i.test(wf_num)) {
        dtype = "12";
        etype = "customgrid";
    } //custom grid
    else if (/^(R_)\S+(_B)/i.test(wf_num)) {
        dtype = "4";
        etype = "rule";
    } //rule
    else if (/^(D_)\S+(_J)/i.test(wf_num)) {
        dtype = "2";
        etype = "json";
    } //json data
    else if (/^(T_)\S+(_)/i.test(wf_num)) {
        dtype = "";
        etype = "navtree";
    } //navtree
    if (isType == true) {
        return dtype;
    }
    else {
        return etype;
    }
}

/**附件处理函数开始**************/
function InitAttachmentList() {
    $("div.attachmentlist").each(function (i) {
        LoadAttachments($(this).attr("id"));
    });
}
function LoadAttachments(fdName) {
    //显示指定序号的附件列表
    var url = "rule?wf_num=R_S004_B001&dc=" + Math.random();
    $.post(url, {Processid: $("#WF_Processid").val(), DocUnid: $("#WF_DocUnid").val(), FdName: fdName, ReadOnly: $("#" + fdName).attr("readtype")}, function (result) {
        $("#" + fdName).html(result);
        try {
            LoadAttachmentsCallBack(fdName, result);
        }
        catch (e) {
        } //附件显示成功后的回调函数
    });
}
function OpenAttachment(filenum) {
    //打开附件
    var url = "rule?wf_num=R_S004_B004&filenum=" + filenum;
    window.open(url);
}
function DelAttachment(filename, docUnid, fdName) {
    //filename要删除的附件名，docUnid要删除的附件记录的WF_OrUnid值
    if (confirm("您确认要删除附件 \"" + filename + "\"吗?")) {
        $.post("rule?wf_num=R_S004_B003", {wf_docunid: docUnid}, function (result) {
            LoadAttachments(fdName);
        });
    }
}
/**附件处理函数结束**************/

/****easy ui 专用函数开始****/

//格式化日期选择框
function formatterDate(date) {
    var y = date.getFullYear();
    var m = date.getMonth() + 1;
    var d = date.getDate();
    var h = date.getHours();
    return y + '-' + (m < 10 ? ('0' + m) : m) + '-' + (d < 10 ? ('0' + d) : d);
}
//格式化日期和时间
function formatterDateTime(date) {
    var y = date.getFullYear();
    var m = date.getMonth() + 1;
    var d = date.getDate();
    var h = date.getHours();
    var min = date.getMinutes();
    return y + '-' + (m < 10 ? ('0' + m) : m) + '-' + (d < 10 ? ('0' + d) : d) + " " + (h < 10 ? ('0' + h) : h) + ":" + (min < 10 ? ('0' + min) : min);
}

/*function loadWebEditor(fdName, ismin) {
    if (ismin) {
        //简单的编辑器
        var upath = "linkey/bpm/editor/umeditor/";
        $("<link>").attr({rel: "stylesheet", type: "text/css", href: upath + "themes/default/css/umeditor.css"}).appendTo("head");
        $.getScript(upath + 'umeditor.config.js', function () {
            $.getScript(upath + 'umeditor.js', function () {
                $.getScript(upath + 'lang/zh-cn/zh-cn.js', function () {
                    initueditor(fdName);
                });
            });
        });
        function initueditor(fdName) {
            var um = UM.getEditor(fdName);
            editor.addListener('contentChange', function (evtName, evt) {
                $("#" + fdName).val(editor.getContent());
            });
        }
    }
    else {
        //加载复杂的编辑器
        var upath = "linkey/bpm/editor/ueditor/";
        $.getScript(upath + 'ueditor.config.js', function () {
            $.getScript(upath + 'ueditor.all.js', function () {
                $.getScript(upath + 'lang/zh-cn/zh-cn.js', function () {
                    initueditor(fdName);
                });
            });
        });
        function initueditor(fdName) {
            //初始化编辑器
            var ewidth = $("#" + fdName).width();
            var eheight = $("#" + fdName).height();
            var editorOption = {initialFrameWidth: ewidth, initialFrameHeight: eheight, maximumWords: 10000, autoHeightEnabled: false, disabledTableInTable: false, allowDivTransTop: false, UEDITOR_HOME_URL: upath};
            var editor = new UE.ui.Editor(editorOption);
            editor.render(fdName);
            editor.addListener('contentChange', function (evtName, evt) {
                $("#" + fdName).val(editor.getContent());
            });
        }
    }
}*/

//20181217 修复多个编辑器初始化并发问题
var initEditor = {
    initStack: [],
    initStack2: [],
    upath: "linkey/bpm/editor/ueditor/",
    upath2: "linkey/bpm/editor/ueditor/umeditor/",
    initUeditor: function(fdName) {
        //初始化编辑器
        var ewidth = $("#" + fdName).width();
        var eheight = $("#" + fdName).height();
        var editorOption = {initialFrameWidth: ewidth, initialFrameHeight: eheight, maximumWords: 10000, autoHeightEnabled: false, disabledTableInTable: false, allowDivTransTop: false, UEDITOR_HOME_URL: initEditor.upath};
        var editor = new UE.ui.Editor(editorOption);
        editor.render(fdName);
        editor.addListener('contentChange', function (evtName, evt) {
            $("#" + fdName).val(editor.getContent());
        });
    },
    initUmeditor: function(fdName) {
        var editor = UM.getEditor(fdName);
        editor.addListener('contentChange', function (evtName, evt) {
            $("#" + fdName).val(editor.getContent());
        });
    },
    loadUeditorScript: function(fdName) {
        mask();
        $.getScript(initEditor.upath + 'ueditor.config.js', function () {
            $.getScript(initEditor.upath + 'ueditor.all.js', function () {
                $.getScript(initEditor.upath + 'lang/zh-cn/zh-cn.js', function () {
                    initEditor.initStack.push(fdName);
                    setTimeout(function() {
                        initEditor.initStack.forEach(function(value, i) {
                            initEditor.initUeditor(value);
                        });
                        initEditor.initStack.length = 0;
                        unmask();
                    }, 500);
                });
            });
        });
        initEditor.loadUeditorScript = function(fdName) {
            initEditor.initStack.push(fdName);
        }
    },
    loadUmeditorScript: function(fdName) {
        mask();
        $("<link>").attr({rel: "stylesheet", type: "text/css", href: initEditor.upath2 + "themes/default/css/umeditor.css"}).appendTo("head");
        $.getScript(initEditor.upath2 + 'umeditor.config.js', function () {
            $.getScript(initEditor.upath2 + 'umeditor.js', function () {
                $.getScript(initEditor.upath2 + 'lang/zh-cn/zh-cn.js', function () {
                    initEditor.initStack2.push(fdName);
                    setTimeout(function() {
                        initEditor.initStack2.forEach(function(value, i) {
                            initEditor.initUmeditor(value);
                        });
                        initEditor.initStack2.length = 0;
                        unmask();
                    }, 500);
                });
            });
        });
        initEditor.loadUmeditorScript = function(fdName) {
            initEditor.initStack2.push(fdName);
        }
    }
}
function loadWebEditor(fdName, ismin) {
    if (ismin) {
        initEditor.loadUmeditorScript(fdName);
    } else {
        initEditor.loadUeditorScript(fdName);
    }
}
// 20181217 END

function mask(msg) {
    if (!msg) {
        msg = "Waiting...";
    }
    var mask_engine = document.body.scrollHeight;
    $("<div class=\"datagrid-mask\" id='bodymask' style='z-index:100001'></div>").css({display: "block", width: "100%", height: mask_engine}).appendTo("body");
    $("<div class=\"datagrid-mask-msg\" id='bodymask-msg'  style='z-index:100001' ></div>").html(msg).appendTo("body").css({display: "block", left: ($(document.body).outerWidth(true) - 190) / 2, top: ($(window).height() - 45) / 2});
}
function unmask() {
    setTimeout('removeMaskid()', 200);
}
function removeMaskid() {
    $('#bodymask-msg').remove();
    $('#bodymask').remove();
}

function setNoCheckedBoxValue(param) {
    //获得没有选中且只有一个的复选框进行序列化,不然对于单个复选框不能取消
    $("input:checkbox").each(function (i) {
        if ($(this)[0].checked == false) {
            var fdName = $(this).attr("name");
            var ckobj = $("input[name='" + fdName + "']");
            var ischecked = false;
            for (var i = 0; i < ckobj.length; i++) {
                if (ckobj[i].checked == true) {
                    ischecked = true;
                }
            }
            if (ischecked == false) {
                eval("param." + fdName + "=''");
            }
        }
    });
}
function getNoCheckedBoxValue() {
    //获得没有选中且只有一个的复选框进行序列化,不然对于单个复选框不能取消
    var serializeStr = "";
    $("input:checkbox").each(function (i) {
        var fdName = $(this).attr("name");
        if ($(this)[0].checked == false && fdName != undefined && fdName != "undefined" && fdName != "") {
            var ckobj = $("input[name='" + fdName + "']");
            var ischecked = false;
            for (var i = 0; i < ckobj.length; i++) {
                if (ckobj[i].checked == true) {
                    ischecked = true;
                }
            }
            if (ischecked == false) {
                if (serializeStr == "") {
                    serializeStr = fdName + "=";
                }
                else {
                    serializeStr += "&" + fdName + "=";
                }
            }
        }
    });
    
    //20190214 单选框为空时传空值，faName========================================================start
    var namearray = "";
    $("input:radio").each(function (i) {
        var fdName = $(this).attr("name");
        
        if(namearray.indexOf(fdName) != -1){
        	
        }else{
        	namearray += fdName;
        	if ($(this)[0].checked == false && fdName != undefined && fdName != "undefined" && fdName != "") {
                var ckobj = $("input[name='" + fdName + "']");
                var ischecked = false;
                for (var i = 0; i < ckobj.length; i++) {
                    if (ckobj[i].checked == true) {
                        ischecked = true;
                    }
                }
                if (ischecked == false) {
                    if (serializeStr == "") {
                        serializeStr = fdName + "=";
                    }
                    else {
                        serializeStr += "&" + fdName + "=";
                    }
                    
                }
            }
        }
        
    });
    var namearray = "";
    //20190214========================================================================================end
    return serializeStr;
}
function getFieldTextShowData() {
    //获得所有span _show的值作为json提交
    var valStr = "";
    $("span[id$='_show']").each(function () {
        var fdName = $(this).attr("id");
        var fdShowValue = $(this).text(); //show字段的值
        var orFdName = fdName.substring(0, fdName.indexOf("_show"));
        var orFdValue = $("#" + orFdName).val();//源字段的值
        if (orFdValue == "") { //清空字段显示值
            if (valStr != "") {
                valStr += "&";
            }
            valStr += fdName + "=";
        }
        else if (fdShowValue != "" && fdShowValue != orFdValue) { //修改显示字段
            if (valStr != "") {
                valStr += "&";
            }
            valStr += fdName + "=" + encodeURIComponent(fdShowValue)
        }
    });

    $("[class*='easyui-combo']").each(function () {
        var fdValue = $(this).combo('getText');
        var comboValue = $(this).combo('getValue');
        var fdName = $(this).attr("id");
        if (comboValue == "") { //清空字段显示值
            if (valStr != "") {
                valStr += "&";
            }
            valStr += fdName + "_show=";
        }
        else if (fdValue != "" && fdValue != comboValue) {
            if (valStr != "") {
                valStr += "&";
            }
            valStr += fdName + "_show=" + encodeURIComponent(fdValue)
        }
    });
    return valStr;
}
function OpenUrl(DocURL, lnum, rnum) {
    var swidth = screen.availWidth;
    var sheight = screen.availHeight;
    if (!lnum) {
        lnum = 24;
    }
    if (!rnum) {
        rnum = 80;
    }
    var wwidth = swidth - lnum;
    var wheight = sheight - rnum;
    var wleft = (swidth / 2 - 0) - wwidth / 2 - 5;
    var wtop = (sheight / 2 - 0) - wheight / 2 - 25;
    return window.open(DocURL, '', 'Width=' + wwidth + 'px,Height=' + wheight + 'px,Left=' + wleft + ',Top=' + wtop + ',location=no,menubar=no,status=yes,resizable=yes,scrollbars=yes,resezie=no');
}

/**表单选择器函数开始***/
function seluser(FdName, stype) {
    //stype为空时表示只选择用户 2表示用户和群组
    if (stype == undefined) {
        stype = "";
    }
    var url = "page?wf_num=P_S007_001&FdName=" + FdName + "&stype=" + stype;
    if (stype == "6") {
        url = "page?wf_num=P_S007_005&FdName=" + FdName;
    }
    var swidth = screen.availWidth;
    var sheight = screen.availHeight;
    var wwidth = 900;
    var wheight = 560;
    var wleft = (swidth / 2 - 0) - wwidth / 2;
    var wtop = (sheight / 2 - 0) - wheight / 2;
    //看是否是移动终端
    if (isMobile()) {
        SelUserForMobile(FdName);
    }
    else {
        window.open(url, 'pwin', 'Width=' + wwidth + 'px,Height=' + wheight + 'px,Left=' + wleft + ',Top=' + wtop + ',status=no,resizable=yes,scrollbars=no,resezie=no');
    }
}

function seldept(FdName, mulFlag) {
    //mulFlag true表示多选 false表示单选
    if (mulFlag == undefined) {
        mulFlag = true;
    }//默认为多选
    var url = "page?wf_num=P_S007_002&FdName=" + FdName + "&mulFlag=" + mulFlag;
    var swidth = screen.availWidth;
    var sheight = screen.availHeight;
    var wwidth = 450;
    var wheight = 560;
    var wleft = (swidth / 2 - 0) - wwidth / 2;
    var wtop = (sheight / 2 - 0) - wheight / 2;
    window.open(url, 'pwin', 'Width=' + wwidth + 'px,Height=' + wheight + 'px,Left=' + wleft + ',Top=' + wtop + ',status=no,resizable=yes,scrollbars=no,resezie=no');
}

function selwfnodeuser(FdName) {
    var url = "page?wf_num=P_S007_004&FdName=" + FdName + "&Processid=" + GetUrlArg("Processid") + "&Nodeid=" + GetUrlArg("Nodeid");
    var swidth = screen.availWidth;
    var sheight = screen.availHeight;
    var wwidth = 900;
    var wheight = 560;
    var wleft = (swidth / 2 - 0) - wwidth / 2;
    var wtop = (sheight / 2 - 0) - wheight / 2;
    window.open(url, 'pwin', 'Width=' + wwidth + 'px,Height=' + wheight + 'px,Left=' + wleft + ',Top=' + wtop + ',status=no,resizable=yes,scrollbars=no,resezie=no');
}

function selprocess(FdName) {
    //stype为空时表示只选择用户 2表示用户和群组
    var url = "page?wf_num=P_S005_003&FdName=" + FdName;
    var swidth = screen.availWidth;
    var sheight = screen.availHeight;
    var wwidth = 900;
    var wheight = 560;
    var wleft = (swidth / 2 - 0) - wwidth / 2;
    var wtop = (sheight / 2 - 0) - wheight / 2;
    window.open(url, 'pwin', 'Width=' + wwidth + 'px,Height=' + wheight + 'px,Left=' + wleft + ',Top=' + wtop + ',status=no,resizable=yes,scrollbars=no,resezie=no');
}
/**表单选择器函数结束**/


String.prototype.strLeft = function (char) {
    if (this.indexOf(char) != -1) {
        return this.substring(0, this.indexOf(char));
    }
    else {
        return "";
    }
}

String.prototype.strRight = function (char) {
    if (this.indexOf(char) != -1) {
        return this.substring(this.indexOf(char) + 1);
    }
    else {
        return "";
    }
}

/**
 * json工具
 */
var JsonUtil = (function () {
    return {
        /**
         * 获取json中的单个值
         */
        getValue        : function (jsonObject, name) {
            var value = "";
            $.each(jsonObject, function (n, v) {
                if (name == n) {
                    value = v;
                    return false;
                }
            });
            return value;
        },
        /**
         * 获取json中的name 以数组形式返回
         */
        getNames        : function (jsonObject) {
            var names = [];
            $.each(jsonObject, function (n, v) {
                names.push(n);
            });
            return names;
        },
        /**
         * 创建json对象
         */
        createJsonObject: function () {
            this.jsonObectArr = [];
        }
    }
})();

function GetUrlArg(name) {
    var url = location.href;
    if (url.substring(url.length - 1) == "#") {
        url = url.substring(0, url.length - 1);
    }
    ;
    var reg = new RegExp("(^|\\?|&)" + name + "=([^&]*)(\\s|&|$)", "i");
    if (reg.test(url)) {
        return decodeURIComponent(RegExp.$2.replace(/\+/g, " "));
    }
    return "";
}

function isEn(s) {
    var regu = "^[0-9a-zA-Z]+$";
    var re = new RegExp(regu);
    if (re.test(s)) {
        return true;
    }
    else {
        return false;
    }
}

String.prototype.replaceAll = function (s1, s2) {
    return this.replace(new RegExp(s1, "gm"), s2);
}

function trim(s) {
    return s.replace(/(^\s*)|(\s*$)/gi, "")
}

String.prototype.trim = function () {
    return this.replace(/(^\s*)|(\s*$)/g, "")
}

function isMobile() {
    var u = navigator.userAgent.toLowerCase();
    if (u.indexOf("mobile") != -1 || GetUrlArg("mobile") == "1") {
        return true;
    }
    else {
        return false;
    }
}

//UI
$(document).ready(function () {
    $(".process_icon").each(function (index, item) {//流程中心图标
        index = index + 1;
        $(item).attr('id', 'id_' + index);
    });
    $(".badge").each(function () {
        var process_num = $(this).text();//流程中心角标为0不显示红色方块
        if (process_num == "0") {
            $(this).css("visibility", "hidden");
        }
    });
    $("#BottomToolbar a").each(function () {//流程表单—>流程监控前的|
        var engine_bottom_a = $(this).text();
        if (engine_bottom_a == "流程监控") {
            $(this).prepend("| ");
        }
    });
});

$.ajaxSetup({
    data : {refresh_random : new Date().getTime()}
});