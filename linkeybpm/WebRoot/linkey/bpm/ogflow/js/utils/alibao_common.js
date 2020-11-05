/**
 * alibao 全局工具类
 * 依赖 jQuery-2.1.4
 */

require.config({
	paths: {
		//相关JS引入
		"jquery": "../js/lib/jquery-2.1.4.js"
	}

});

var Tools;

define(["jquery"], function($) {

	/** 
	 * 简易ajax，方便调用 
	 * @param {Object} url
	 * @param {String} type   get/post
	 * @param {Stirng} data   数据参数
	 * @param {Function} fun    回调函数
	 * @param {Object} params   回调函数可选参数
	 * @param {String} async  true/false
	 */
	function ajaxUtil(url, type, data, fun, params, async) {

		let returnData;

		//默认为异步
		if(isBlank(async)) {
			async = true;
		}

		var ajaxConfig = {
			url: url,
			type: type,
			data: data,
			async: async,
			beforeSend: function(xhr) {
				//				setHeader(xhr);
			},
			success: function(res, status, xhr) {
				//如果 回调函数为空，则直接返回数据

				//linkeyData 默认返回接口格式{Status:"",msg:"",linkeyData:{}}
				if(res.Status != undefined && res.Status == "0") {
					console.log("接口出错：" + res.msg + "  " + url);
				}

				if(isBlank(fun)) {
					returnData = res;
					return;
				}
				fun(res, params);
			},
			error: function() {
				console.log("ajax 访问错误：" + url);
			}
		}
		$.ajax(ajaxConfig);

		return returnData;
	}

	/**
	 * 获得URL里的参数值 
	 */
	function GetUrlArg(name) {
		let url = location.href;
		if(url.substring(url.length - 1) == "#") {
			url = url.substring(0, url.length - 1);
		};
		let reg = new RegExp("(^|\\?|&)" + name + "=([^&]*)(\\s|&|$)", "i");
		if(reg.test(url)) {
			return decodeURIComponent(RegExp.$2.replace(/\+/g, " "));
		}
		return "";
	}

	/**
	 * 全部替换方法
	 */
	function replaceAll(item, oldStr, newStr) {
		let reg = new RegExp(oldStr, "g")
		return item.replace(reg, newStr);
	}

	/** 判断是否不为空 */
	function isNotBlank(obj) {
		return !isBlank(obj);
	}

	/**
	 * 判断是否为空
	 */
	function isBlank(obj) {
		if(typeof obj == "undefined" || obj == null || obj.toString().trim() == "") {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 动态插入JS
	 * @param {String} code  JS 代码
	 * @param {String} position  指定插入位置，body、head等 默认head
	 */
	function loadScriptString(code, position) {

		if(position == undefined) {
			position = "head"
		}

		var script = document.createElement("script");
		script.type = "text/javascript";
		try {
			script.appendChild(document.createTextNode(code));
		} catch(ex) {
			script.text = code;
		}
		let parentObj = "document." + position;
		parentObj = eval('(' + parentObj + ')');
		parentObj.appendChild(script);
	}

	/**
	 * 字符串转换成DOM对象[只返回第一个]
	 * @param {String} arg 要转换DOM的字符串
	 */
	function parseDom(arg) {
		var objE = parseDomNodeList(arg);

		return objE.childNodes[0];
	}

	/**
	 * 字符串转换成DOM 含父节点div
	 * @param {String} arg 要转换DOM的字符串
	 */
	function parseDomNodeList(arg) {
		var objE = document.createElement("div");
		objE.innerHTML = arg;

		return objE;
	}

	/**
	 * 全部替换方法
	 */
	function replaceAll(item, oldStr, newStr) {
		let reg = new RegExp(oldStr, "g")
		return item.replace(reg, newStr);
	}

	/**
	 * 计算两点之间距离
	 * @param {Object} p1
	 * @param {Object} p2
	 */
	function getDistance(p1, p2) {
		var dx = Math.abs(p2.x - p1.x);         
		var dy = Math.abs(p2.y - p1.y); 
		return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
	}

	/**
	 * 判断相对于{x1,y1}点,{x2,y2}点是水平还是垂直方向
	 * @param {Object} x1
	 * @param {Object} y1
	 * @param {Object} x2
	 * @param {Object} y2
	 */
	function verdictXY(x1, y1, x2, y2) {

		return Math.abs(y2 - y1) / Math.abs(x2 - x1);
	}

	/**
	 * 获取鼠标移动xy,含滚动长度
	 * @param {Object} event
	 */
	function getEventXY(e) {

		var evex = event.clientX; //相对客户端X轴坐标 de
		var evey = event.clientY;
		var offsetX = parseInt(document.body.scrollLeft); //滚动条高度，在鼠标按下的时候初始化
		var offsetY = parseInt(document.body.scrollTop);
		evex = (evex - 0 + offsetX);
		evey = (evey - 0 + offsetY);

		var eve = {
			x: evex,
			y: evey
		}
		
		return eve
	}

	Tools = {

		// 判断是否为空
		isBlank: isBlank,
		isNotBlank: isNotBlank,

		// 简易ajax，方便调用 
		ajaxUtil: ajaxUtil,

		// 获得URL里的参数值 
		GetUrlArg: GetUrlArg,

		// 全部替换方法
		replaceAll: replaceAll,

		// 动态插入JS
		loadScriptString: loadScriptString,

		// 字符串转换成DOM[只返回第一个]
		parseDom: parseDom,

		// 字符串转换成DOM 含父节点div
		parseDomNodeList: parseDomNodeList,

		// 替换全部方法
		replaceAll: replaceAll,

		// 计算两点之间距离
		getDistance: getDistance,

		// 判断相对于{x1,y1}点,{x2,y2}点是水平还是垂直方向
		verdictXY: verdictXY,

		// 获取鼠标移动xy,含滚动长度
		getEventXY: getEventXY

	}

	return Tools;
})