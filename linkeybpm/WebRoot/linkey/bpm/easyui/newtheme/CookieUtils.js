/************************************************************************
|    函数名称： setCookie                                                |
|    函数功能： 设置cookie函数                                            |
|    入口参数： name：cookie名称；value：cookie值                        |
*************************************************************************/
function setCookie(name, value) 
{ 
    var argv = setCookie.arguments; 
    var argc = setCookie.arguments.length; 
    var expires = (argc > 2) ? argv[2] : null; 
    if(expires!=null) 
    { 
        var LargeExpDate = new Date (); 
        LargeExpDate.setTime(LargeExpDate.getTime() + (expires*31536000000)); //365*1000*3600*24=31536000000 表示为一年     
    }else{
        var LargeExpDate = new Date (); 
        LargeExpDate.setTime(LargeExpDate.getTime() + 157680000000); //5*365*1000*3600*24=157680000000 五年
    }
    document.cookie = name + "=" + escape (value)+"; expires=" +LargeExpDate.toGMTString(); 
}
/************************************************************************
|    函数名称： getCookie                                                |
|    函数功能： 读取cookie函数                                            |
|    入口参数： Name：cookie名称                                            |
*************************************************************************/
function getCookie(Name) 
{ 
    var search = Name + "=" 
    if(document.cookie.length > 0) 
    { 
        offset = document.cookie.indexOf(search) 
        if(offset != -1) 
        { 
            offset += search.length 
            end = document.cookie.indexOf(";", offset) 
            if(end == -1) end = document.cookie.length 
            return unescape(document.cookie.substring(offset, end)) 
        } 
        else return "" 
    } 
} 

/************************************************************************
|    函数名称： deleteCookie                                            |
|    函数功能： 删除cookie函数                                            |
|    入口参数： Name：cookie名称                                        |
*************************************************************************/    
function deleteCookie(name) 
{ 
    var expdate = new Date(); 
    expdate.setTime(expdate.getTime() - (86400 * 1000 * 1)); 
    setCookie(name, "", expdate); 
} 
/********************************************************************/
function changeSheetFile(){
	var nowtheme = getCookie("themename");
	var pathName=window.document.location.pathname;
	var projectName=pathName.substring(0,pathName.substr(1).indexOf('/')+1); 

	var dthemecss = document.getElementById("bpm_theme");
	if(dthemecss != undefined && dthemecss != null){
		var theme_bule_dpath = projectName+"/"+"linkey/bpm/easyui/newtheme/bule/devclient.css";
		var theme_green_dpath = projectName+"/"+"linkey/bpm/easyui/newtheme/green/devclient.css";
		var theme_black_dpath = projectName+"/"+"linkey/bpm/easyui/newtheme/black/devclient.css";
		dthemecss.href = eval(nowtheme+"_dpath");
	}
	
	var uthemecss = document.getElementById("bpmu_theme");
	if(uthemecss != undefined && uthemecss != null){
		var theme_bule_upath = projectName+"/"+"linkey/bpm/easyui/newtheme/bule/userclient.css";
		var theme_green_upath = projectName+"/"+"linkey/bpm/easyui/newtheme/green/userclient.css";
		var theme_black_upath = projectName+"/"+"linkey/bpm/easyui/newtheme/black/userclient.css";
		uthemecss.href = eval(nowtheme+"_upath");
	}
	
/*	var fthemecss = document.getElementById("font_awesome");
	if(fthemecss != undefined && fthemecss != null){
		var theme_bule_fpath = "linkey/bpm/easyui/themes/testtheme/bule/font-awesome-4.7.0/css/font-awesome.css";
		var theme_green_fpath = "linkey/bpm/easyui/themes/testtheme/green/font-awesome-4.7.0/css/font-awesome.css";
		fthemecss.href = eval(nowtheme+"_fpath");
	}*/
	
/*	var pthemecss = document.getElementById("bpmpage_theme");
	if(pthemecss != undefined && pthemecss != null){
		var theme_bule_ppath = "linkey/bpm/easyui/themes/testtheme/bule/page.css";
		var theme_green_ppath = "linkey/bpm/easyui/themes/testtheme/green/page.css";
		pthemecss.href = eval(nowtheme+"_ppath");
	}*/
	//alert(themecss.href); 
}
//20180903
//window.onload = function (){
//  changeSheetFile();
//}
