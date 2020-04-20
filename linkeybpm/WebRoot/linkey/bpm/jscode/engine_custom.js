//请把所有需要自定义或覆盖的js函数放入本文件中
$(document).ready(function() {
	//20180910 注释
    //$("listsubform").find("span").addClass("ExpandSubForm");
    $(window).bind('beforeunload',function() {
        $.post('rule?wf_num=R_S003_B081', {WF_DocUnid: $('#WF_DocUnid').val()}, function(data, textStatus, xhr) {
        });
     });
});
