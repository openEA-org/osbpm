/* (function () {


})() */


/**
 *创建输入连接线
 */
function createInputLine(line) {
	id = "R";
	//输入
	if (line == "inputLine") {
		f = "493,107 657,107";
	}
	//输出
	if (line == "outputLine") {
		f = "493,87 657,87";
	}
	//控制
	if (line == "controlLine") {
		f = "564,199 564,332";
	}
	//机制
	if (line == "mechanismLine") {
		f = "600,332 600,199";
	}
	//var f = GetPointsStr("Creat");
	console.log(f);
	var svgroot = document.getElementById("svg");
	var defs = svg.create("defs").appendTo(svgroot).node;

	var polyline = svg.create("polyline").attrNS({
		id: "polyline1111",
		fill: "none",
		"marker-end": "url(#markerEndArrow)",
		"stroke-width": "1.5",
		"stroke": "black",
		//"left": hotx,
		//"top": hoty,
		"points": f
	}).attr({
		// "Nodeid": id,
		// "SourceNode": goalStartObj.getAttribute("Nodeid"),
		// "TargetNode": goalEndObj.getAttribute("Nodeid"),
		// "LinkeyNode": g,
		// "PolyLineType": StartPointType,
		// "hotType": hotType,
		//"hotEndType":hotEndType, //20171101
		"oldpoints": f,
		// ondblclick: "SetProperty(this, 'Router')"
	}).appendTo(svgroot).node;
	//polyline.ondblclick = function() {
	//SetProperty(this, 'Router');
	//};
	var i = f.split(" ");
	//var p1 = f.substring(0, i)
	//i = f.lastIndexOf(" ")
	//var p2 = f.substring(i, f.length)

	var str1 = i[0].split(",");
	var str2 = i[1].split(",");
	var str3 = i[i.length - 2].split(",");
	var str4 = i[i.length - 1].split(",");
	var svgtext = svg.create("text").attrNS({
		id: line + "_text",
		x: (parseInt(str2[0]) + parseInt(str3[0])) / 2,
		y: (parseInt(str2[1]) + parseInt(str3[1])) / 2,
		"font-size": "9pt",
		"text-anchor": "middle",
		fill: "black"
	}).attr({
		"Nodeid": id + "_text",
		ondblclick: "SetProperty(this, 'Router')",

	}).appendTo(svgroot).node;


	/*svgtext.ondblclick = function() {
		SetProperty(this, 'Activity')
	};*/

	svgtext.textContent = "";
	// RegObj("polyline" + g);
	switch (line) {
		case "inputLine":
			$("#inputLine_text").text("输入");
			break;
		case "outputLine":
			$("#outputLine_text").text("输出");
			break;
		case "controlLine":
			$("#controlLine_text").text("控制");
			break;
		case "mechanismLine":
			$("#mechanismLine_text").text("机制");
			break;
	}
}
/**
 * 鼠标滚动事件
 * 这里不支持Firefox
 */
windowAddMouseWheel();
//缩放大小幅度
var zoom = 0.1;
//最大缩放倍数
var maximum = 10;
function windowAddMouseWheel() {
	//隐藏滚动条
	document.getElementById("svg").parentNode.parentNode.setAttribute("style","margin: 0px; background-color: rgb(248, 248, 248); zoom: 1; cursor: default;;overflow:hidden");
	//父body设置scroll为no
	//document.getElementById("svg").parentNode.parentNode.setAttribute("scroll","no");
	//当前body设置scroll为no
	//document.getElementsByName("body").setAttribute("scroll","no");
	//初始化iframe缩放大小
	window.parent.document.getElementById("flowframe").setAttribute("style","transform:scale(1)");
	var scrollFunc = function (e) {
		e = e || window.event;
		if (e.wheelDelta > 0) { //向上滚动
			Increase();
		}
		if (e.wheelDelta < 0) { //向下滚动
			Narrow();
		}
	};
	//绑定滚动事件 
	window.onmousewheel = document.onmousewheel = scrollFunc;
}
/**
 * 放大
 * 单击一次，放大0.1
* 最大放大10倍
 */
function Increase() {
	var chartObj = window.parent.document.getElementById("flowframe")
	//取style的transform值
	if (!chartObj.style.transform) {
		chartObj.style.transform = "scale(1.10)";
	} else {
		var scale = chartObj.style.transform
		var num = scale.substring(scale.indexOf("(") + 1, scale.indexOf(")"));
		if(num == maximum){
			return
		}
		num = parseFloat(num) + parseFloat(zoom);
		chartObj.style.transform = "scale(" + num + ")"
	}
}
/**
 * 缩小
 * 单击一次，缩小0.1
 * 放大缩小的是iframe
 */
function Narrow() {
	//var chartObj = document.getElementById("svg");
	var chartObj = window.parent.document.getElementById("flowframe")
	if (!chartObj.style.transform) {
		return
	} else {
		var scale = chartObj.style.transform
		var num = scale.substring(scale.indexOf("(") + 1, scale.indexOf(")"));
		if (num == 1) {
			return
		}
		num = parseFloat(num) - parseFloat(zoom);
		chartObj.style.transform = "scale(" + num + ")"
	}
}


/*  document.getElementById("svg").onmouseover = function(e) {
    if ((e.target||window.event.srcElement).tagName.toLowerCase() == 'svg') {
		document.getElementById("svg").addEventListener("click", function(ev){
			var disLeft = ev.clientX - $(this).offset().left;//最左侧到鼠标位置的坐标距离
			movelayer(disLeft)
			return false;
		});
	}
    return false;
};  */

/**
 * 鼠标停留在停留SVG图层添加点击事件，实现点击工作区域左侧左移，点击右侧右移
 * 鼠标停留在节点上则去除点击事件
 */
document.getElementById("svg").onmouseover = function(e) {
	if ((e.target||window.event.srcElement).tagName.toLowerCase() !== 'svg') {
		document.getElementById("svg").removeEventListener("click", myFunction);
	}else{
		document.getElementById("svg").addEventListener("click", myFunction);
	}
}
/**
 * 
 * @param {*} ev 
 */
function myFunction(ev) {
	var disLeft = ev.clientX - $(this).offset().left;//最左侧到鼠标位置的坐标距离
	movelayer(disLeft)
}
/**
 * 左移、右移
 * @param {*} disLeft 
 */
function movelayer(disLeft){
	var clientwidth = document.body.clientWidth;//可见区域宽
	var guide = "";
	if(clientwidth/2>=disLeft){
		guide = "left";
	}else{
		guide = "right";
	}
	checkAttr();
    MoveCenter(guide);
	ToMoveLine();
}