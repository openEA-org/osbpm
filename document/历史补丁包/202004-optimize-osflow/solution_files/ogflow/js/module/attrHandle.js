/**
 * 属性面板录入相关
 */
var attrHandle;

define([], function () {

	(function () {
		  windowAddMouseWheel(); 

	})()

	return attrHandle;
});

/**
 * 鼠标滚动事件
 * 这里不支持Firefox
 * zoom 缩放大小幅度
 * maximum 最大缩放倍数
 * minimum 最小縮放倍數
 */
var zoom = 0.1;
var maximum = 10;
var minimum = 1;

function windowAddMouseWheel() {
	//隐藏滚动条
	//document.getElementById("svg").parentNode.parentNode.style.overflow = "hidden";
	//初始化iframe缩放大小
	window.parent.document.getElementById("flowframe").setAttribute("style", "transform:scale(1)");
	var mouseleft = "";
	var mousetop = "";
	//移动鼠标,获得在DIV中的坐标
	document.getElementById("svg").onmousemove = function (e) {
		var e = e || window.event;
		mouseleft = e.offsetX ? e.offsetX : e.originalEvent.layerX;
		mousetop = e.offsetY ? e.offsetY : e.originalEvent.layerY;
	}
	var clientwidth = document.body.clientWidth; //可见区域宽
	var clientheight = document.body.clientHeight; //可见区域高
	document.getElementById("svg").onmouseover = function (e) {
		var targetObj; //鼠标停留对象
		var targetid; //鼠标停留元素id
		if ((e.target || window.event.srcElement).tagName.toLowerCase() == "tspan") {
			targetObj = (e.target || window.event.srcElement).parentNode;
		} else {
			targetid = e.target.getAttribute("id") || window.event.srcElement.getAttribute("id");
			targetObj = document.getElementById(targetid);
		}
		var scrollFunc = function (e) {
			e = e || window.event;
			var b = true;
			if (e.wheelDelta) {
				b = e.wheelDelta > 0 ? true : false;
			}
			//鼠标向上滚动且ctrl键按下
			if (b && e.ctrlKey) {
				if (document.getElementById("svg").childNodes.length == 2) {
					if (e.preventDefault) {
						e.preventDefault();
					}
					return false;
				}
				var chartObj = window.parent.document.getElementById("flowframe")
				//取style的transform值
				if (!chartObj.style.transform) {
					chartObj.style.transform = "scale(1.1)";
				} else {
					var scale = chartObj.style.transform
					var num = scale.substring(scale.indexOf("(") + 1, scale.indexOf(")"));
					if (num == maximum) {
						if (e.preventDefault) {
							e.preventDefault();
						}
						return false;
					}
					num = parseFloat(num) + parseFloat(zoom);
					//transform-origin 设置缩放中心点
					//position:absolute 固定位置
					chartObj.setAttribute("style", "transform:scale(" + num + ");transform-origin:" + Number((mouseleft / clientwidth) * 100).toFixed(3) + "% " + Number((mousetop / clientheight) * 100).toFixed(3) + "%;position:absolute;")
				}
			}
			//鼠标向下滚动且ctrl键按下
			if (!b && e.ctrlKey) { //向下滚动
				if (document.getElementById("svg").childNodes.length == 2) {
					if (e.preventDefault) {
						e.preventDefault();
					}
					return false;
				}
				var chartObj = window.parent.document.getElementById("flowframe")
				if (!chartObj.style.transform) {
					if (e.preventDefault) {
						e.preventDefault();
					}
					return false;
				} else {
					var scale = chartObj.style.transform
					var num = scale.substring(scale.indexOf("(") + 1, scale.indexOf(")"));
					if (num == minimum) {
						if (e.preventDefault) {
							e.preventDefault();
						}
						return false;
					}
					num = parseFloat(num) - parseFloat(zoom);
					chartObj.setAttribute("style", "transform:scale(" + num + ");transform-origin:" + Number((mouseleft / clientwidth) * 100).toFixed(3) + "% " + Number((mousetop / clientheight) * 100).toFixed(3) + "%;position:absolute;")
				}
			}
			//屏蔽默認事件
			if (e.preventDefault) {
				e.preventDefault();
			}
			return false;
		};
		//绑定滚动事件
		targetObj.onmousewheel = scrollFunc
	}
}
/**
 * 放大
 * 单击一次，放大0.1
 * 最大放大10倍
 */
function Increase() {
	if (document.getElementById("svg").childNodes.length == 2) {
		return
	}
	var chartObj = window.parent.document.getElementById("flowframe")
	if (!chartObj.style.transform) {
		chartObj.style.transform = "scale(1.10)";
	} else {
		var scale = chartObj.style.transform
		var num = scale.substring(scale.indexOf("(") + 1, scale.indexOf(")"));
		if (num == maximum) {
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
	if (document.getElementById("svg").childNodes.length == 2) {
		return
	}
	var chartObj = window.parent.document.getElementById("flowframe")
	if (!chartObj.style.transform) {
		return
	} else {
		var scale = chartObj.style.transform
		var num = scale.substring(scale.indexOf("(") + 1, scale.indexOf(")"));
		if (num == minimum) {
			return
		}
		num = parseFloat(num) - parseFloat(zoom);
		chartObj.style.transform = "scale(" + num + ")"
	}
}
/**
 * 移动svg图层
 * 调用全部选择方法
 * 标志symbol等于flag
 * @param {*} flag  传过来的参数为true
 */
var symbol;

function movelayer(flag) {
	symbol = flag;
	var svgObj = document.getElementById("svg");
	for (i = 0; i < svgObj.childNodes.length; i++) {
		var children = svgObj.childNodes[i];
		if (children) {
			if (children.tagName == "text" && unionNode(null, children)) {
				continue;
			}
			if ((children.tagName == "circle" || children.tagName == "rect" || children.tagName == "path" || children.tagName == "polygon" || children.tagName == "polygon") && !isXYNode(children)) {
				MulSel_AddNode(children.id)
			}
		}
	}
	//MulSel_MoveNodeObj1()
	//ToMoveLine(null, tempx, tempy);
}
/**
 * 鼠标松开，调用取消选择方法MulSel_Start
 * 标志symbol设为false
 */
document.onmouseup = function (event) {
	if (symbol) {
		MulSel_Start();
		symbol = false;
	}
}
/**
 *  鼠标按下右键，获取线的类型
 *  linetype  箭头名称，右键打开箭头属性页面时，提供默认选中依据
 *  boundaryLine 边界箭头
 */
var linetype
//var boundaryLine
document.onmousedown = function (event) {
	var event = event || window.event
	if (event.button == "2") {
		var lineobj = (event.target || window.event.srcElement)
		if (lineobj.tagName == "text") {
			linetype = document.getElementById(lineobj.id).innerHTML;
			//boundaryLine = document.getElementById(lineobj.id.substring(0, lineobj.id.lastIndexOf("_"))).getAttribute("PolyLineType")
		}
		if (lineobj.tagName == "polyline") {
			linetype = document.getElementById(lineobj.id + "_text").innerHTML;
			//boundaryLine = lineobj.getAttribute("PolyLineType")
		}
		if (lineobj.getAttribute("PolyLineType")) {
			return;
		}
	}
}
/**
 * 计算两条线的交点
 * @param {*} aLineObj 
 * @param {*} bLineObj 
 */
function segmentsIntr(aLineObj, bLineObj) {
	var aPointsArr = aLineObj.getLinePointsArr()
	var bPointsArr = bLineObj.getLinePointsArr();
	for (var i = 0; i < Object.keys(aPointsArr).length - 1; i++) {
		for (var j = 0; j < Object.keys(bPointsArr).length - 1; j++) {
			var p1 = {
				x: aPointsArr[i].x,
				y: aPointsArr[i].y
			}
			var p2 = {
				x: aPointsArr[i + 1].x,
				y: aPointsArr[i + 1].y
			}
			var p3 = {
				x: bPointsArr[j].x,
				y: bPointsArr[j].y
			}
			var p4 = {
				x: bPointsArr[j + 1].x,
				y: bPointsArr[j + 1].y
			}
			var rp = calInter(p1, p2, p3, p4)
			if (rp) {
				console.log("他们的交点为: (" + rp.x + "," + rp.y + ")");
				return {
					x: rp.x,
					y: rp.y
				};
			} else {
				console.log("不相交");
			}
		}
	}
}
/**
 * 参考---https://blog.csdn.net/u012260672/article/details/51941262
 * 计算交点坐标
 * 判断每一条线段的两个端点是否都在另一条线段的两侧,
 * 是则求出两条线段所在直线的交点, 否则不相交.
 * @param {*} a  a点坐标
 * @param {*} b  b点坐标
 * @param {*} c  c点坐标
 * @param {*} d  d点坐标
 */
function calInter(a, b, c, d) {
	// 三角形abc 面积的2倍  
	var area_abc = (a.x - c.x) * (b.y - c.y) - (a.y - c.y) * (b.x - c.x);
	// 三角形abd 面积的2倍  
	var area_abd = (a.x - d.x) * (b.y - d.y) - (a.y - d.y) * (b.x - d.x);
	// 面积符号相同则两点在线段同侧,不相交 (对点在线段上的情况,本例当作不相交处理);  
	if (area_abc * area_abd >= 0) {
		return false;
	}
	// 三角形cda 面积的2倍  
	var area_cda = (c.x - a.x) * (d.y - a.y) - (c.y - a.y) * (d.x - a.x);
	// 三角形cdb 面积的2倍  
	// 注意: 这里有一个小优化.不需要再用公式计算面积,而是通过已知的三个面积加减得出.  
	var area_cdb = area_cda + area_abc - area_abd;
	if (area_cda * area_cdb >= 0) {
		return false;
	}
	//计算交点坐标  
	var t = area_cda / (area_abd - area_abc);
	var dx = t * (b.x - a.x),
		dy = t * (b.y - a.y);
	return {
		x: parseFloat(a.x) + parseFloat(dx),
		y: parseFloat(a.y) + parseFloat(dy)
	};
}
/**
 * 框图转取
 * @param {*} mark up:向上回溯  down:向下撰取
 * @param {*} nodeid 框图节点id
 */
function openDataFlow(mark, nodeid) {
	$.post("rule?wf_num=R_DFD_B008", {
		"processid": parent.processid,
		"mark": mark,
		"Nodeid": nodeid
	}, function (data) {
		var result = JSON.parse(data);
		var processid = result.processid;
		if (processid) {
			var url = "rule?wf_num=R_S002_B001&flow=flow&Processid=" + processid;
			//window.open(url);
			OpenUrl(url);
		} else if (mark == "up") {
			alert("没有对应父图！");
		} else if (mark == "down") {
			alert("没有对应子图！");
		}
	});
}
/**
 * 打开图表属性页面
 * @param {*} processid 流程id
 */
function openChartProperty(processid) {
	$.post("rule?wf_num=R_DFD_B009", {
		"processid": processid
	}, function (data) {
		var result = JSON.parse(data);
		var wf_docunid = result.wf_docunid;
		var url = "r?wf_num=F_DFD_A007&Processid=" + processid + "&wf_action=edit&wf_docunid=" + wf_docunid;
		OpenUrl(url);
	})

}