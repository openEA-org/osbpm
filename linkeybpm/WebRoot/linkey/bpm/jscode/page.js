/**
 * Created by Administrator on 2016/8/25 0025.
 */
$(document).ready(function(){
  $(".office").each(function(){
    var lwidth = $(this).parent().width();
    var date = $(".notice .date").width();
    var editor = $(this).next().next().next().width();
    var width = $(this).width();//officeµÄ¿í¶È
    var content = lwidth-date-editor-width-20;
    $(this).next().css("width",content);
  });
  $(".office").mouseover(function(){
	  $(this).css("text-decoration","underline");
	  $(this).next().css("text-decoration","underline");
  });
  $(".office").mouseout(function(){
	  $(this).css("text-decoration","none");
	  $(this).next().css("text-decoration","none");
  });
  $(".content").mouseover(function(){
	  $(this).css("text-decoration","underline");
	  $(this).prev().css("text-decoration","underline");
  });
  $(".content").mouseout(function(){
	  $(this).css("text-decoration","none");
	  $(this).prev().css("text-decoration","none");
  });
  $(".fdate").each(function(){
    var date = $(this).width();
    var width_li = $(this).parent().width();
    var fcontent = width_li-date-10;
    $(this).prev().css("width",fcontent);
  });
});