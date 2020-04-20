/*
 * 此文件给通用页面调用的JS
 */

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

//JS添加CSS链接
function loadStyle(url){
var link = document.createElement('link');
    link.type = 'text/css';
    link.rel = 'stylesheet';
    link.href = url;
    var head = document.getElementsByTagName('head')[0];
    head.prepend(link);
   // head.appendChild(link);
}
window.onload = function (){
	//loadStyle2();
	
}


//JS修改CSS链接
function loadStyle2(){
	var filePath = "";  //存放主题路径目录
	var link = document.getElementById("bpm_theme");
	var pathName=window.document.location.pathname;
    var projectName=pathName.substring(0,pathName.substr(1).indexOf('/')+1);
    if(projectName=="/designer"){
    	projectName = ""
    }
    var myResource=new Array("devclient.css");
/*	$.post("rule?wf_num=R_S031_BH01",function(data){
    	data=eval('('+data+')');
        filePath = data.themePath;
        for(var i=0;i<myResource.length;i++){
	    	var filePathall = projectName+"/"+filePath+"/"+myResource[i];
    		loadStyle(filePathall);
    	}
	    });*/
    
    /*var jqueryJS=document.createElement("script"); 
    jqueryJS.setAttribute("type","text/javascript"); 
    jqueryJS.setAttribute("src","linkey/bpm/easyui/jquery.min.js");// 在这里引入了a.js 
    var head = document.getElementsByTagName('head')[0];
    head.appendChild(jqueryJS);
    console.log(head.outerHTML);*/
    
    
    
	$.ajax({  
		async: false,
		type: "POST",  
        url: projectName+"/rule?wf_num=R_S031_BH01",  
        //data: { name: "John", location: "Boston" }
        success: function(data){
        	data=eval('('+data+')');
            filePath = data.themePath;
            for(var i=0;i<myResource.length;i++){
    	    	var filePathall = projectName+"/"+filePath+"/"+myResource[i];
    	    	//loadStyle(filePathall); 使用创建CSS
    	    	//loadStyle2(filePathall); // 使用修改
    	    	link.href = filePathall;
    	    	//alert(link.href);
    	       
        	}
        }
    });  

}

