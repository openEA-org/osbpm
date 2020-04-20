
function mask(msg){
	if(!msg){msg="Waiting...";}
	$("<div class=\"datagrid-mask\" id='bodymask' style='z-index:100001'></div>").css({display:"block",width:"100%",height:$(window).height()}).appendTo("body");
	$("<div class=\"datagrid-mask-msg\" id='bodymask-msg'  style='z-index:100001' ></div>").html(msg).appendTo("body").css({display:"block",left:($(document.body).outerWidth(true) - 190) / 2,top:($(window).height() - 45) / 2});
}
function unmask(){setTimeout('removeMaskid()',200);}
function removeMaskid(){$('#bodymask-msg').remove();$('#bodymask').remove();}
function GetUrlArg(name){var url=location.href;if(url.substring(url.length-1)=="#"){url=url.substring(0,url.length-1);};var reg = new RegExp("(^|\\?|&)"+ name +"=([^&]*)(\\s|&|$)", "i");       if (reg.test(url)) return unescape(RegExp.$2.replace(/\+/g, " ")); return ""; };
function isEn(s){var regu = "^[0-9a-zA-Z]+$"; var re = new RegExp(regu); if (re.test(s)) {return true; }else{return false;} } 
String.prototype.replaceAll=function(s1,s2){return this.replace(new RegExp(s1,"gm"),s2);}
function trim(s){return s.replace(/(^\s*)|(\s*$)/gi, "")}
String.prototype.trim=function(){return this.replace(/(^\s*)|(\s*$)/g, "")}