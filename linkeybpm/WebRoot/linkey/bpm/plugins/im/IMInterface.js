function LoginIM(userid){
	if(userid==undefined){userid="test1";}
	if (IMInterfaceISLogined(userid,'即时通')){
		alert('您已经登过录了!');
		return;
	}
    var url="r?wf_num=R_SL01_B001";
    $.get(url,function(data){
        if (IMInterfaceLoginByToken(data.token,'即时通')==0){
            //alert('登录成功')
        }else{
            alert('登录失败')
        }
    },"json")
}

var userAgent = navigator.userAgent, 
rMsie = /(msie\s|trident.*rv:)([\w.]+)/, 
rFirefox = /(firefox)\/([\w.]+)/, 
rOpera = /(opera).+version\/([\w.]+)/, 
rChrome = /(chrome)\/([\w.]+)/, 
rSafari = /version\/([\w.]+).*(safari)/;
var browser;
var version;
var ua = userAgent.toLowerCase();
function uaMatch(ua) {
	var match = rMsie.exec(ua);
	if (match != null) {
		return { browser : "IE", version : match[2] || "0" };
	}
	var match = rFirefox.exec(ua);
	if (match != null) {
		return { browser : match[1] || "", version : match[2] || "0" };
	}
	var match = rOpera.exec(ua);
	if (match != null) {
		return { browser : match[1] || "", version : match[2] || "0" };
	}
	var match = rChrome.exec(ua);
	if (match != null) {
		return { browser : match[1] || "", version : match[2] || "0" };
	}
	var match = rSafari.exec(ua);
	if (match != null) {
		return { browser : match[2] || "", version : match[1] || "0" };
	}
	if (match != null) {
		return { browser : "", version : "0" };
	}
}
var browserMatch = uaMatch(userAgent.toLowerCase());
if (browserMatch.browser) {
	browser = browserMatch.browser;
	version = browserMatch.version;
}

var IMInterfaceisie = (browser=="IE"?1:0);//0/*@cc_on+1@*/;

var IMInterfaceobjWebOcx = false;

//创建插件
function IMInterfaceCreateplugin() {

	if (IMInterfaceisie) {
		try {
			if (!IMInterfaceobjWebOcx) {
				IMInterfaceobjWebOcx = new ActiveXObject("WebRun.IMWebOcx"); // 创建控件
			}
			return IMInterfaceobjWebOcx;
		} catch (O) {
			return false;
		}
	} else {

		var IMInterfacePlugin = navigator.mimeTypes["application/im-im-plugin"];
		if (IMInterfacePlugin) {
			var IMInterfacePluginObject = document
					.getElementById("IMInterfacePluginObjectID");
			if (IMInterfacePluginObject==null) {
				IMInterfacePluginObject = document.createElement("EMBED");
				IMInterfacePluginObject.setAttribute("type",
						"application/im-im-plugin");
				IMInterfacePluginObject.setAttribute("id",
						"IMInterfacePluginObjectID");
				IMInterfacePluginObject.setAttribute("width", "0");
				IMInterfacePluginObject.setAttribute("height", "0");
				document.getElementsByTagName('body')[0]
						.appendChild(IMInterfacePluginObject);
			}
			return IMInterfacePluginObject;

		}
	}
	return false;
}

//判断是否安装  true 表示已经安装  false 表示没有安装
//Oem软件Oem名称 
function IMInterfaceISInstalled(oem) {

	var IMInterfaceobj = IMInterfaceCreateplugin();

	if (IMInterfaceobj) {
		return IMInterfaceobj.ISInstalled(oem);
	} else {
		return false;
	}

}
//判断是否已经登录    true 表示已经登录  false 表示没有登录
//loginName 用户名
//Oem软件Oem名称 
function IMInterfaceISLogined(loginName, oem) {
	var IMInterfaceobj = IMInterfaceCreateplugin();
	if (IMInterfaceobj) {
		return IMInterfaceobj.ISLogined(loginName,oem);
	} else {
		return false;
	}
}

//登录  0 表示登录 成功   1 表示没有安装
//loginName 用户名
//password 密码
//Oem软件Oem名称 
function IMInterfaceLogin(LoginName, Password, Oem) {
	var IMInterfaceobj = IMInterfaceCreateplugin();
	if (IMInterfaceobj) {
		return IMInterfaceobj.Login(LoginName, Password, Oem);
	} else {
		return 1;
	}
}

//登录  0 表示登录 成功   1 表示没有安装
//token 令牌
//Oem软件Oem名称 
function IMInterfaceLoginByToken(token, Oem) {
	var IMInterfaceobj = IMInterfaceCreateplugin();
	if (IMInterfaceobj) {
		return IMInterfaceobj.LoginByToken(token, Oem);
	} else {
		return 1;
	}
}

//登录  0 表示登录 成功   1 表示没有安装
//loginName 用户名
//password 密码
//Oem软件Oem名称 
//toChatLoginName 登录成功打开与某人的聊天窗体
function IMInterfaceLogin2(loginName, password, oem, toChatLoginName) {
	var IMInterfaceobj = IMInterfaceCreateplugin();
	if (IMInterfaceobj) {
		return IMInterfaceobj.Login2(loginName, password, oem, toChatLoginName);
	} else {
		return 1;
	}
}
//使用令牌登录   0 表示登录 成功   1 表示没有安装
//token 令牌
//Oem软件Oem名称 
//toChatLoginName 登录成功打开与某人的聊天窗体
function IMInterfaceLoginByToken2(token, oem, toChatLoginName) {
	var IMInterfaceobj = IMInterfaceCreateplugin();
	if (IMInterfaceobj) {
		return IMInterfaceobj.LoginByToken2(token, oem, toChatLoginName);
	} else {
		return 1;
	}
}

//通过用户名打开聊天窗体    0 表示登录 成功   1 表示没有安装
//loginName用户名
//Oem软件Oem名称 
function IMInterfaceWebChatToByLoginName(loginName, oem) {
	var IMInterfaceobj = IMInterfaceCreateplugin();
	if (IMInterfaceobj) {
		return IMInterfaceobj.WebChatToByLoginName(loginName, oem);
	} else {
		return 1;
	}
}

//通过群ID打开群聊天窗体    0 表示登录 成功   1 表示没有安装
//GroupID群ID
//Oem软件Oem名称 
//UserID 本地登录用户ID，-1表示不指定
function IMInterfaceWebGroupChatTo(GroupID, Oem, UserID) {
	var IMInterfaceobj = IMInterfaceCreateplugin();
	if (IMInterfaceobj) {
		return IMInterfaceobj.WebGroupChatTo(GroupID, Oem, UserID);
	} else {
		return 1;
	}
}

//通过用户ID打开聊天窗体    0 表示登录 成功   1 表示没有安装
//ID用户ID
//Oem软件Oem名称 
function IMInterfaceWebChatTo(ID, Oem) {
	var IMInterfaceobj = IMInterfaceCreateplugin();
	if (IMInterfaceobj) {
		return IMInterfaceobj.WebChatTo(ID, Oem);
	} else {
		return 1;
	}
}

//通过用户ID打开聊天窗体    0 表示登录 成功   1 表示没有安装
//ID用户ID
//Oem软件Oem名称 
//UserID 本地登录用户ID，-1表示不指定
function IMInterfaceWebChatToEx(ID, Oem, UserID) {
	var IMInterfaceobj = IMInterfaceCreateplugin();
	if (IMInterfaceobj) {
		return IMInterfaceobj.WebChatToEx(ID, Oem, UserID);
	} else {
		return 1;
	}
}


//获取统一登录令牌
function IMInterfaceGetLoginToken(Oem){
var IMInterfaceobj = IMInterfaceCreateplugin();
	if (IMInterfaceobj) {
		return IMInterfaceobj.GetLoginToken(Oem);
	} else {
		return "";
	}
}

//通过用户名打开聊天窗体    0 表示登录 成功   1 表示没有安装
//LoginName用户名
//Oem软件Oem名称 
//UserID 本地登录用户ID，-1表示不指定
//AdditionalValueMe自己聊天窗体右侧打开的URL
//AdditionalValueFriend对方聊天窗体打开的URL地址
function IMInterfaceWebChatToByLoginName2(LoginName,UserID, Oem, AdditionalValueMe,AdditionalValueFriend) {
	var IMInterfaceobj = IMInterfaceCreateplugin();
	if (IMInterfaceobj) {
		return IMInterfaceobj.WebChatToByLoginName2(LoginName,UserID, Oem, AdditionalValueMe,AdditionalValueFriend);
	} else {
		return 1;
	}
}

//通过用户ID打开聊天窗体    0 表示登录 成功   1 表示没有安装
//ID用户ID
//Oem软件Oem名称 
//UserID 本地登录用户ID，-1表示不指定
//AdditionalValueMe自己聊天窗体右侧打开的URL
//AdditionalValueFriend对方聊天窗体打开的URL地址
function IMInterfaceWebChatTo2(ID,Oem,UserID,  AdditionalValueMe,AdditionalValueFriend) {
	var IMInterfaceobj = IMInterfaceCreateplugin();
	if (IMInterfaceobj) {
		return IMInterfaceobj.WebChatTo2(ID,Oem,UserID,  AdditionalValueMe,AdditionalValueFriend);
	} else {
		return 1;
	}
}

//显示主界面
//Oem名称
//loginName 用户名  不填表示所有
function IMInterfaceShowMainForm(Oem,loginName){
var IMInterfaceobj = IMInterfaceCreateplugin();
	if (IMInterfaceobj) {
		return IMInterfaceobj.ShowMainForm(Oem,loginName);
	} else {
		return 1;
	}
}
