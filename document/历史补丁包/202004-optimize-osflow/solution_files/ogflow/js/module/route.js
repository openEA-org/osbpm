/**
 * 路由相关处理
 */
//var route;

var freeRulingStatus = false; // 自由划线状态
var freeLineTemp = null; // 自由划线临时保存变量

var cacheConvergeLine = ""; // 汇聚后临时保存变量，用于汇聚后拆分
var sortEqual = false; // true表示即使相等也排序

define([], function() {

	/**
	 * 对节点某一条边进行排序
	 * @param {Object} nodeObj  节点对象
	 * @param {Object} pointType  边的类型 Left、Right、Top、Bottom
	 */
	function sortSide(nodeObj, pointType, linkeyObj) {

		// return;

		// 202003 add by alibao ===========start
		// 添加对开始节点和结束节点的过滤
		if(nodeObj.tagName == "circle" || nodeObj.tagName == "polygon"){
			return false;
		}
		// 202003 add by alibao ===========end



		linkeyObj = Tools.isNotBlank(linkeyObj) ? linkeyObj : "LinkeyStartObj";

		var lineArr = new Array();
		var LinkeyStartObj = nodeObj.getAttribute(linkeyObj);
		if(Tools.isNotBlank(LinkeyStartObj)) {
			var startLines = LinkeyStartObj.trim().split(",");
			for(var i = 0; i < startLines.length; i++) {

				var lineObj = evalbroker(startLines[i]);
				var oo = lineObj.id.replace("polyline", "").split("_");

				if(linkeyObj == "LinkeyStartObj") {
					var fistPoint = lineObj.getFirstPoint();
					var linePointType = getPointType(fistPoint.x, fistPoint.y, nodeObj);
					if(pointType == linePointType) {
						lineArr.push(fistPoint);
					}
				} else {
					var endPoint = lineObj.getLastPoint();
					var linePointType = getPointType(endPoint.x, endPoint.y, nodeObj);
					if(pointType == linePointType) {
						lineArr.push(endPoint);
					}
				}

			}
		}

		var xType = (pointType == "Left" || pointType == "Right");

		var count = 0;

		lineArr.sort(function(a, b) {

			if(xType) {
				if(a.y == b.y) count++;
				return a.y - b.y
			} else {
				if(a.x == b.x) count++;
				return a.x - b.x
			}

		})

		if(sortEqual) count = 0;

		var overLong = xType ? CONFIG.ACTIVITY.height : CONFIG.ACTIVITY.width;
		var lineSize = lineArr.length + 1 - count;
		var average = overLong / lineSize;

		var nodeTop = parseInt(converterSVG(nodeObj, "top") - 0 + offsetY);
		var nodeLeft = parseInt(converterSVG(nodeObj, "left") - 0 + offsetX);

		for(var i = 0, j = 0; i < lineArr.length; i++) {
			var lineObj = document.getElementById(lineArr[i].id);

			if(i < lineArr.length - 1) {
				if(xType) {
					if(!sortEqual && lineArr[i + 1].y == lineArr[i].y) {
						lineArr[i].y = nodeTop + average * (j + 1);
					} else {
						lineArr[i].y = nodeTop + average * (j + 1);
						j++;
					}
				} else {
					if(!sortEqual && lineArr[i + 1].x == lineArr[i].x) {
						lineArr[i].x = nodeLeft + average * (j + 1);
					} else {
						lineArr[i].x = nodeLeft + average * (j + 1);
						j++;
					}
				}
			} else {
				if(xType) {
					lineArr[i].y = nodeTop + average * (j + 1);
				} else {
					lineArr[i].x = nodeLeft + average * (j + 1);
				}
				j++;
			}

			if(linkeyObj == "LinkeyStartObj") {

				var PolyLineType = lineObj.getAttribute("PolyLineType");
				if(PolyLineType == "boundaryLine") {
					if(xType) {
						lineObj.setLastPoint({
							x: lineObj.getLastPoint().x,
							y: lineArr[i].y
						})
					} else {
						lineObj.setLastPoint({
							x: lineArr[i].x,
							y: lineObj.getLastPoint().y
						})
					}
				}

				lineObj.setFirstPoint(lineArr[i]);
			} else {

				var PolyLineType = lineObj.getAttribute("PolyLineType");
				if(PolyLineType == "boundaryLine") {
					if(xType) {
						lineObj.setFirstPoint({
							x: lineObj.getFirstPoint().x,
							y: lineArr[i].y
						})
					} else {
						lineObj.setFirstPoint({
							x: lineArr[i].x,
							y: lineObj.getFirstPoint().y
						})
					}
				}

				lineObj.setLastPoint(lineArr[i]);
			}

		}

		// 重新计算线段与节点 关系
		checkAttr();

		// 重绘一下箭头
		if(lineArr.length > 0) {
			var lineId = lineArr[0].id;
			var oo = lineId.replace("polyline", "").split("_");
			var sobj = evalbroker("Node" + oo[0]);
			var eobj = evalbroker("Node" + oo[1])
			lkwf.toMoveLine(sobj);
			lkwf.toMoveLine(eobj);
		}

	}

	/**
	 * 箭头拐角是直角拐角处理
	 * @param {Object} j 线段Points
	 */
	function rightCorner(j) {

		// 是否开启直角拐角
		if(!CONFIG.OGFLOW_CONF.isCurvel) {
			return j;
		}

		// 直角拐角长度
		var cornerL = CONFIG.LINE.cornerL;
		var crate = 2 * cornerL; // 用于比较参数

		points = j.split(" ");
		// 点小于3，则是直线，无需对拐角处处理
		if(points.length < 3) {
			return j;
		}
		//console.log("原： " + points);
		var pointsNew = points.concat();

		var xs = points[0].split(",")[0] - 0;
		var ys = points[0].split(",")[1] - 0;
		var xe = points[points.length - 1].split(",")[0] - 0;
		var ye = points[points.length - 1].split(",")[1] - 0;

		for(var i = 0; i < points.length - 2; i++) {

			var p1 = points[i];
			var p2 = points[i + 1];
			var p3 = points[i + 2];

			var p1x = p1.split(",")[0] - 0;
			var p1y = p1.split(",")[1] - 0;
			var p2x = p2.split(",")[0] - 0;
			var p2y = p2.split(",")[1] - 0;
			var p3x = p3.split(",")[0] - 0;
			var p3y = p3.split(",")[1] - 0;

			if(p1x == p2x && p3y == p2y) {

				if((p2y - p1y) > crate && (p2x - p3x) > crate) { // 向下左拐
					pointsNew[i + 1] = p2x + "," + (p2y - cornerL) + " " + (p2x - cornerL) + "," + p2y;
				} else if((p2y - p1y) > crate && (p3x - p2x) > crate) { // 向下右拐
					pointsNew[i + 1] = p2x + "," + (p2y - cornerL) + " " + (p2x + cornerL) + "," + p2y;
				} else if((p1y - p2y) > crate && (p2x - p3x) > crate) { // 向上左拐
					pointsNew[i + 1] = p2x + "," + (p2y + cornerL) + " " + (p2x - cornerL) + "," + p2y;
				} else if((p1y - p2y) > crate && (p3x - p2x) > crate) { //向上右拐
					pointsNew[i + 1] = p2x + "," + (p2y + cornerL) + " " + (p2x + cornerL) + "," + p2y;
				} else {
					return j;
				}

			} else if(p1y == p2y && p3x == p2x) {

				if((p1x - p2x) > crate && (p2y - p3y) > crate) { // 向左上拐
					pointsNew[i + 1] = (p2x + cornerL) + "," + p2y + " " + p2x + "," + (p2y - cornerL);
				} else if((p1x - p2x) > crate && (p3y - p2y) > crate) { // 向左下拐
					pointsNew[i + 1] = (p2x + cornerL) + "," + p2y + " " + p2x + "," + (p2y + cornerL);
				} else if((p2x - p1x) > crate && (p2y - p3y) > crate) { // 向右上拐
					pointsNew[i + 1] = (p2x - cornerL) + "," + p2y + " " + p2x + "," + (p2y - cornerL);
				} else if((p2x - p1x) > crate && (p3y - p2y) > crate) { //向右下拐
					pointsNew[i + 1] = (p2x - cornerL) + "," + p2y + " " + p2x + "," + (p2y + cornerL);
				} else {
					return j;
				}

			}

		}

		// 转换成字符串points
		var pointStr = "";
		for(var i = 0; i < pointsNew.length; i++) {
			pointStr += pointsNew[i] + " ";
		}

		pointStr = pointStr.trim();
//		console.log("现在： " + pointStr);
		return pointStr;
	}

	/**
	 * 获得一个节点边上所在的所有线
	 * @param {Object} nodeObj 一个节点
	 */
	function getOneNodeWithLines(nodeObj) {

		var g = nodeObj.getAttribute("LinkeyStartObj");
		var h = nodeObj.getAttribute("LinkeyEndObj");

		var gh = "";
		if(g != "" && g != undefined) {
			gh += g;
		}

		if(h != "" && h != undefined) {
			if(gh == "") {
				gh += h;
			} else {
				gh = gh + "," + h
			}
		}

		return gh;
	}

	/**
	 * 获取线所在节点的边  Left、Right、Top、Bottom
	 * @param {Object} x 线上开始或结束节点的x
	 * @param {Object} y
	 * @param {Object} nodeObj  节点对象
	 */
	function getPointType(x, y, nodeObj) {

		// 节点最右边x，最下边y
		var buttomy = parseInt(converterSVG(nodeObj, "top")) + parseInt(converterSVG(nodeObj, "height")) - 0 + offsetY;
		var rightx = parseInt(converterSVG(nodeObj, "left")) + parseInt(converterSVG(nodeObj, "width")) - 0 + offsetX;

		// 节点起始x,y
		var leftx = parseInt(converterSVG(nodeObj, "left")) - 0 + offsetX;
		var topy = parseInt(converterSVG(nodeObj, "top")) - 0 + offsetY;

		if(Math.abs(x - leftx) <= 1 && Math.abs(y - topy) > 1 && Math.abs(y - buttomy) > 1) {
			pointType = "Left";
		} else if(Math.abs(x - rightx) <= 1 && Math.abs(y - topy) > 1 && Math.abs(y - buttomy) > 1) {
			pointType = "Right";
		} else if(Math.abs(y - topy) <= 1) {
			pointType = "Top";
		} else {
			pointType = "Bottom";
		}

		return pointType;
	}

	/**
	 * 移动线段 内部箭头、边界箭头处理
	 * @param {Object} lineObj
	 * @param {Object} x
	 * @param {Object} y
	 */
	function moveSmartLinePart(lineObj) {

		var dl = CONFIG.LINE.cornerL * 3; // 使用箭头长度作为灵敏长度

		var lineArr = lineObj.getLinePointsArr();
		var lineType = lineObj.getAttribute("PolyLineType");

		var x = Tools.getEventXY().x;
		var y = Tools.getEventXY().y;

		var se = lineObj.id.replace("polyline", "").split("_");
		var sobj = evalbroker("Node" + se[0]);
		var eobj = evalbroker("Node" + se[1]);

		try {
			if(event.button == rightbtn)
				return false
		} catch(e) {}

		var cornerL = CONFIG.LINE.cornerL;
		//			if(lineArr.length < 3) return lineArr;
		var minLong = Math.sqrt(Math.pow(cornerL, 2) * 2) - (-0.5);

		var ps = lineArr[0];
		var ps1 = lineArr[1];
		var pe = lineArr[lineArr.length - 1];
		var pe1 = lineArr[lineArr.length - 2];

		for(var i = 0; i < lineArr.length - 1; i++) {

			var p1 = lineArr[i];
			var p2 = lineArr[i + 1];

			var directionType = getDirectionType(p1, p2);

			if(directionType == -1) continue;

			// 水平方向
			if(directionType == 0 && ((x > (p1.x - 0 + dl) && x < (p2.x - dl)) || (x < (p1.x - dl) && x > (p2.x - 0 + dl))) && Math.abs(p1.y - y) < cornerL * 2) {

				if(Tools.getDistance(p1, p2) < minLong) {
					return;
				}

				if(i == 0 && Tools.isNotBlank(sobj)) { // 第一段水平
					var sy = parseInt(converterSVG(sobj, "top") + offsetY);
					var sh = parseInt(converterSVG(sobj, "height"));

					if(y <= sy || y >= (sy + sh)) {
						return;
					}
				} else if(i == (lineArr.length - 2) && Tools.isNotBlank(eobj)) { // 最后一段水平

					var ey = parseInt(converterSVG(eobj, "top") + offsetY);
					var eh = parseInt(converterSVG(eobj, "height"));

					if(y <= ey || y >= (ey + eh)) {
						return;
					}
				}

				// 第一个点的相邻点
				if(i > 0 && Tools.getDistance(lineArr[i - 1], p1) < minLong) {
					lineArr[i - 1].y = Math.round(lineArr[i - 1].y / p1.y * y);
				}

				// 第二个点的相邻点
				if(i <= lineArr.length - 3 && Tools.getDistance(lineArr[i + 2], p2) < minLong) {
					lineArr[i + 2].y = Math.round(lineArr[i + 2].y / p2.y * y);
				}

				lineArr[i].y = y;
				lineArr[i + 1].y = y;

				break;
			}

			// 垂直方向
			if(directionType == 1 && ((y > (p1.y - 0 + dl) && y < (p2.y - dl)) || (y < (p1.y - dl) && y > (p2.y - 0 + dl))) && Math.abs(p1.x - x) < cornerL * 2) {

				if(Tools.getDistance(p1, p2) < minLong) {
					return;
				}

				// 第一段垂直
				if(i == 0 && Tools.isNotBlank(sobj)) {

					var sx = parseInt(converterSVG(sobj, "left") + offsetX);
					var sw = parseInt(converterSVG(sobj, "width"));

					if(x <= sx || x >= (sx + sw)) {
						return;
					}
				} else if(i == (lineArr.length - 2) && Tools.isNotBlank(eobj)) { // 最后一段垂直

					var ex = parseInt(converterSVG(eobj, "left") + offsetX);
					var ew = parseInt(converterSVG(eobj, "width"));

					if(x <= ex || x >= (ex + ew)) {
						return;
					}
				}

				// 第一个点的相邻点
				if(i > 0 && Tools.getDistance(lineArr[i - 1], p1) < minLong) {
					lineArr[i - 1].x = Math.round(lineArr[i - 1].x / p1.x * x);
				}

				// 第二个点的相邻点
				if(i <= lineArr.length - 3 && Tools.getDistance(lineArr[i + 2], p2) < minLong) {
					lineArr[i + 2].x = Math.round(lineArr[i + 2].x / p2.x * x);
				}

				lineArr[i].x = x;
				lineArr[i + 1].x = x;

				break;
			}
		}

		var points = "";
		for(var i = 0; i < lineArr.length; i++) {
			points = points + " " + lineArr[i].x + "," + lineArr[i].y
		}

		//		console.log(points);

		points = points.trim();
		lineObj.setAttribute("oldpoints", points);
		lineObj.setAttribute("orpoints", points);
		lineObj.setAttribute("points", points);
		AddRouterText(lineObj, "MoveObj")

	}

	/**
	 * 计算任意两点走向，水平 0，垂直 1 ，都否则 -1
	 * @param {Object} p1  x,y
	 * @param {Object} p2
	 */
	function getDirectionType(p1, p2) {

		var derectionType = -1;

		if(p1.y == p2.y) {
			derectionType = 0;
		} else if(p1.x == p2.x) {
			derectionType = 1;
		} else {
			derectionType = -1;
		}

		return derectionType;
	}

	/**
	 * 左键点击线，移动线某一段[原生路由]
	 * @param {Object} a  moveobj  点击对象
	 * @param {Object} b           x坐标
	 * @param {Object} c           y坐标
	 */
	function moveLinePart(a, b, c) {

		offsetX = parseInt(document.body.scrollLeft);
		offsetY = parseInt(document.body.scrollTop);
		var bb = event.clientX + offsetX;
		var cc = event.clientY + offsetY;

		try {
			if(event.button == rightbtn) return false
		} catch(e) {}

		var d = bb - mstartX;
		var f = cc - mstartY;
		if(d == 0 && f == 0) return false;
		var g = a.getAttribute("hotType");
		var h = a.getAttribute("PolyLineType");

		var p = a.getAttribute("points").trim().replace(/( )/gi, ",").split(",");
		var x = parseInt(p[0]);
		var y = parseInt(p[1]);
		var x2 = parseInt(p[2]);
		var y2 = parseInt(p[3]);
		var x3 = parseInt(p[p.length - 4]);
		var y3 = parseInt(p[p.length - 3]);
		var x4 = parseInt(p[p.length - 2]);
		var y4 = parseInt(p[p.length - 1]);

		//console.log("x:" + x + "," + y + " " + x2 + "," + y2 + " " + x3 + "," + y3 + " " + x4 + "," + y4);

		var se = a.id.replace("polyline", "").split("_");
		var sobj = evalbroker("Node" + se[0]);
		var eobj = evalbroker("Node" + se[1]);

		var sx = parseInt(converterSVG(sobj, "left")) + offsetX;
		var sy = parseInt(converterSVG(sobj, "top")) + offsetY;
		var sw = parseInt(converterSVG(sobj, "width"));
		var sh = parseInt(converterSVG(sobj, "height"));
		var ex = parseInt(converterSVG(eobj, "left")) + offsetX;
		var ey = parseInt(converterSVG(eobj, "top")) + offsetY;
		var ew = parseInt(converterSVG(eobj, "width"));
		var eh = parseInt(converterSVG(eobj, "height"));

		var startp = x + "," + y;
		var endp = x4 + "," + y4;
		var t = startp + " " + x2 + "," + y2 + " " + x3 + "," + y3 + " " + endp;
		switch(h) {
			case "Bottom":
				var n = "0";
				if(g == "Bottom") {
					if(cc < y2 && (Math.abs(bb - x2) < 5)) {
						n = "1"
					} else if(cc > y2 && (Math.abs(bb - x3) < 5)) {
						n = "2"
					}
				} else {
					if(cc > y2 && (Math.abs(bb - x2) < 5)) {
						n = "1"
					} else if(cc < y2 && (Math.abs(bb - x3) < 5)) {
						n = "2"
					}
				}
				if(Math.abs(cc - y2) < 5) {
					y2 = cc;
					t = startp + " " + x2 + "," + cc + " " + x3 + "," + cc + " " + endp;
					a.setAttribute("oldpoints", t);
					a.setAttribute("orpoints", t);
					a.setAttribute("points", t);
					//				AddRouterText(a, "MoveObj")
				} else if(n == "1") {
					try {
						if(sobj.getAttribute("NodeType") == "Edge") {
							return false;
						}
						if(bb > sx && bb < (sx + sw)) {
							t = bb + "," + y + " " + bb + "," + y2 + " " + x3 + "," + y3 + " " + endp;
							a.setAttribute("oldpoints", t);
							a.setAttribute("orpoints", t);
							a.setAttribute("points", t);
							AddRouterText(a, "MoveObj")
						}
					} catch(e) {}

				} else if(n == "2") {
					try {
						if(eobj.getAttribute("NodeType") == "Edge") {
							return false;
						}
						if(bb > ex && bb < (ex + ew)) {
							t = startp + " " + x2 + "," + y2 + " " + bb + "," + y3 + " " + bb + "," + y4;
							a.setAttribute("oldpoints", t);
							a.setAttribute("orpoints", t);
							a.setAttribute("points", t);
							AddRouterText(a, "MoveObj")
						}
					} catch(e) {}

				}
				break;
			case "Bian":
				var n = "0";
				if(g == "Right") {
					if(bb < x2 && (Math.abs(cc - y2) < 5)) {
						n = "1"
					} else if(bb > x2 && (Math.abs(cc - y3) < 5)) {
						n = "2"
					}
				} else {
					if(bb > x2 && (Math.abs(cc - y2) < 5)) {
						n = "1"
					} else if(bb < x2 && (Math.abs(cc - y3) < 5)) {
						n = "2"
					}
				}
				if(Math.abs(bb - x2) < 5) {
					t = startp + " " + bb + "," + y2 + " " + bb + "," + y3 + " " + endp;
					a.setAttribute("oldpoints", t);
					a.setAttribute("orpoints", t);
					a.setAttribute("points", t);
					AddRouterText(a, "MoveObj")
				} else if(n == "1") {
					try {
						if(sobj.getAttribute("NodeType") == "Edge") {
							return false;
						}
						if(cc > sy && cc < (sy + sh)) {
							t = x + "," + cc + " " + x2 + "," + cc + " " + x3 + "," + y3 + " " + endp;
							a.setAttribute("oldpoints", t);
							a.setAttribute("orpoints", t);
							a.setAttribute("points", t);
							AddRouterText(a, "MoveObj")
						}
					} catch(e) {}

				} else if(n == "2") {
					try {
						if(eobj.getAttribute("NodeType") == "Edge") {
							return false;
						}
						if(cc > ey && cc < (ey + eh)) {
							t = startp + " " + x2 + "," + y2 + " " + x3 + "," + cc + " " + x4 + "," + cc;
							a.setAttribute("oldpoints", t);
							a.setAttribute("orpoints", t);
							a.setAttribute("points", t);
							AddRouterText(a, "MoveObj")
						}
					} catch(e) {}

				}
				break;
			case "zhexian":
				var n = "0";
				if(g == "Bottom" || g == "Top") {
					if(Math.abs(bb - x2) < 5) {
						n = "1"
					} else if(Math.abs(bb - x3) < 5) {
						n = "2"
					}

					if(Math.abs(cc - y2) < 5) {
						t = startp + " " + x2 + "," + cc + " " + x3 + "," + cc + " " + endp;
						a.setAttribute("oldpoints", t);
						a.setAttribute("orpoints", t);
						a.setAttribute("points", t);
						AddRouterText(a, "MoveObj")
					} else if(n == "1") {
						if(sobj.getAttribute("NodeType") == "Edge") {
							return false;
						}
						if(bb > sx && bb < (sx + sw)) {
							t = bb + "," + y + " " + bb + "," + y2 + " " + x3 + "," + y3 + " " + endp;
							a.setAttribute("oldpoints", t);
							a.setAttribute("orpoints", t);
							a.setAttribute("points", t);
							AddRouterText(a, "MoveObj")
						}
					} else if(n == "2") {
						if(eobj.getAttribute("NodeType") == "Edge") {
							return false;
						}
						if(bb > ex && bb < (ex + ew)) {
							t = startp + " " + x2 + "," + y2 + " " + bb + "," + y3 + " " + bb + "," + y4;
							a.setAttribute("oldpoints", t);
							a.setAttribute("orpoints", t);
							a.setAttribute("points", t);
							AddRouterText(a, "MoveObj")
						}
					}

				} else {
					if(Math.abs(cc - y2) < 5) {
						n = "1"
					} else if(Math.abs(cc - y3) < 5) {
						n = "2"
					}

					if(Math.abs(bb - x2) < 5) {
						t = startp + " " + bb + "," + y2 + " " + bb + "," + y3 + " " + endp;
						a.setAttribute("oldpoints", t);
						a.setAttribute("orpoints", t);
						a.setAttribute("points", t);
						AddRouterText(a, "MoveObj")
					} else if(n == "1") {
						if(sobj.getAttribute("NodeType") == "Edge") {
							return false;
						}
						if(cc > sy && cc < (sy + sh)) {
							t = x + "," + cc + " " + x2 + "," + cc + " " + x3 + "," + y3 + " " + endp;
							a.setAttribute("oldpoints", t);
							a.setAttribute("orpoints", t);
							a.setAttribute("points", t);
							AddRouterText(a, "MoveObj")
						}
					} else if(n == "2") {
						if(eobj.getAttribute("NodeType") == "Edge") {
							return false;
						}
						if(cc > ey && cc < (ey + eh)) {
							t = startp + " " + x2 + "," + y2 + " " + x3 + "," + cc + " " + x4 + "," + cc;
							a.setAttribute("oldpoints", t);
							a.setAttribute("orpoints", t);
							a.setAttribute("points", t);
							AddRouterText(a, "MoveObj")
						}
					}
				}
				break;
			case "zhexian90":

				if(Math.abs(cc - y2) < 5) {
					n = "1"
				} else if(Math.abs(bb - x2) < 5) {
					n = "2"
				}
				if(n == "1") {
					if(sobj.getAttribute("NodeType") == "Edge") {
						return false;
					}
					if(cc > sy && cc < (sy + sh)) {
						t = x + "," + cc + " " + x2 + "," + cc + " " + endp;
						a.setAttribute("oldpoints", t);
						a.setAttribute("orpoints", t);
						a.setAttribute("points", t);
						AddRouterText(a, "MoveObj")
					}
				} else if(n == "2") {
					if(eobj.getAttribute("NodeType") == "Edge") {
						return false;
					}
					if(bb > ex && bb < (ex + ew)) {
						t = startp + " " + bb + "," + y2 + " " + bb + "," + y4;
						a.setAttribute("oldpoints", t);
						a.setAttribute("orpoints", t);
						a.setAttribute("points", t);
						AddRouterText(a, "MoveObj")
					}
				}

				break;
			case "zhexian902":
				if(Math.abs(bb - x2) < 5) {
					n = "1"
				} else if(Math.abs(cc - y2) < 5) {
					n = "2"
				}
				if(n == "1") {
					if(sobj.getAttribute("NodeType") == "Edge") {
						return false;
					}
					if(bb > sx && bb < (sx + sw)) {
						t = bb + "," + y + " " + bb + "," + y2 + " " + endp;
						a.setAttribute("oldpoints", t);
						a.setAttribute("orpoints", t);
						a.setAttribute("points", t);
						AddRouterText(a, "MoveObj")
					}
				} else if(n == "2") {
					if(eobj.getAttribute("NodeType") == "Edge") {
						return false;
					}
					if(cc > ey && cc < (ey + eh)) {
						t = startp + " " + x2 + "," + cc + " " + x4 + "," + cc;
						a.setAttribute("oldpoints", t);
						a.setAttribute("orpoints", t);
						a.setAttribute("points", t);
						AddRouterText(a, "MoveObj")
					}
				}
				break;
			case "Line":
				var op = oldArryPoints.split(",");
				//			var op0 = op[0] - 0;
				//			var op1 = op[1] - 0;
				//			var op2 = op[2] - 0;
				//			var op3 = op[3] - 0;
				var op0 = op[0] - 0 - offsetX;
				var op1 = op[1] - 0 - offsetY;
				var op2 = op[2] - 0 - offsetX;
				var op3 = op[3] - 0 - offsetY;
				var ty = Math.abs((y2 - y)) / 4;
				var tx = Math.abs((x2 - x)) / 4;
				if(g == "Bottom") {
					if(Math.abs(bb - x) < 10 && cc - y - ty < 0) {
						n = "1"
					} else if(Math.abs(bb - x2) < 15 && cc - y - (ty * 3) > 0) {
						n = "2"
					} else if(cc - y - ty > 0 && cc - y - (ty * 3) < 0) {
						n = "3"
					}
					if(n == "1") {
						if(sobj.getAttribute("NodeType") == "Edge") {
							return false;
						}
						if(bb > sx && bb < (sx + sw)) {
							t = bb + "," + y + " " + x2 + "," + y2;
							a.setAttribute("oldpoints", t);
							a.setAttribute("orpoints", t);
							a.setAttribute("points", t);
							AddRouterText(a, "MoveObj")
						}
					} else if(n == "2") {
						if(eobj.getAttribute("NodeType") == "Edge") {
							return false;
						}
						if(bb > ex && bb < (ex + ew)) {
							t = x + "," + y + " " + bb + "," + y2;
							a.setAttribute("oldpoints", t);
							a.setAttribute("orpoints", t);
							a.setAttribute("points", t);
							AddRouterText(a, "MoveObj")
						}
					} else if(n == "3") {
						if(sobj.getAttribute("NodeType") == "Edge" || eobj.getAttribute("NodeType") == "Edge") {
							return false;
						}
						if((op0 + d) > sx && (op0 + d) < (sx + sw) && (op2 + d) > ex && (op2 + d) < (ex + ew)) {
							t = (op0 + d) + "," + y + " " + (op2 + d) + "," + y2;
							a.setAttribute("oldpoints", t);
							a.setAttribute("orpoints", t);
							a.setAttribute("points", t);
							AddRouterText(a, "MoveObj")
						}
					}
				} else if(g == "Top") {
					if(Math.abs(bb - x) < 10 && cc - y + ty > 0) {
						n = "1"
					} else if(Math.abs(bb - x2) < 15 && cc - y + ty * 3 < 0) {
						n = "2"
					} else if(cc - y + ty < 0 && cc - y + ty * 3 > 0) {
						n = "3"
					}
					if(n == "1") {
						if(sobj.getAttribute("NodeType") == "Edge") {
							return false;
						}
						if(bb > sx && bb < (sx + sw)) {
							t = bb + "," + y + " " + x2 + "," + y2;
							a.setAttribute("oldpoints", t);
							a.setAttribute("orpoints", t);
							a.setAttribute("points", t);
							AddRouterText(a, "MoveObj")
						}
					} else if(n == "2") {
						if(eobj.getAttribute("NodeType") == "Edge") {
							return false;
						}
						if(bb > ex && bb < (ex + ew)) {
							t = x + "," + y + " " + bb + "," + y2;
							a.setAttribute("oldpoints", t);
							a.setAttribute("orpoints", t);
							a.setAttribute("points", t);
							AddRouterText(a, "MoveObj")
						}
					} else if(n == "3") {
						if(sobj.getAttribute("NodeType") == "Edge" || eobj.getAttribute("NodeType") == "Edge") {
							return false;
						}
						if((op0 + d) > sx && (op0 + d) < (sx + sw) && (op2 + d) > ex && (op2 + d) < (ex + ew)) {
							t = (op0 + d) + "," + y + " " + (op2 + d) + "," + y2;
							a.setAttribute("oldpoints", t);
							a.setAttribute("orpoints", t);
							a.setAttribute("points", t);
							AddRouterText(a, "MoveObj")
						}
					}
				} else if(g == "Right") {
					if(Math.abs(cc - y) < 10 && bb - x - tx < 0) {
						n = "1"
					} else if(Math.abs(cc - y2) < 15 && bb - x - (tx * 3) > 0) {
						n = "2"
					} else if(bb - x - tx > 0 && bb - x - (tx * 3) < 0) {
						n = "3"
					}
					if(n == "1") {
						if(sobj.getAttribute("NodeType") == "Edge") {
							return false;
						}
						if(cc > sy && cc < (sy + sh)) {
							t = x + "," + cc + " " + x2 + "," + y2;
							a.setAttribute("oldpoints", t);
							a.setAttribute("orpoints", t);
							a.setAttribute("points", t);
							AddRouterText(a, "MoveObj")
						}
					} else if(n == "2") {
						if(eobj.getAttribute("NodeType") == "Edge") {
							return false;
						}
						if(cc > ey && cc < (ey + eh)) {
							t = x + "," + y + " " + x2 + "," + cc;
							a.setAttribute("oldpoints", t);
							a.setAttribute("orpoints", t);
							a.setAttribute("points", t);
							AddRouterText(a, "MoveObj")
						}
					} else if(n == "3") {
						if(sobj.getAttribute("NodeType") == "Edge" || eobj.getAttribute("NodeType") == "Edge") {
							return false;
						}
						if((op1 + f) > sy && (op1 + f) < (sy + sh) && (op3 + f) > ey && (op3 + f) < (ey + eh)) {
							t = x + "," + (op1 + f) + " " + x2 + "," + (op3 + f);
							a.setAttribute("oldpoints", t);
							a.setAttribute("orpoints", t);
							a.setAttribute("points", t);
							AddRouterText(a, "MoveObj")
						}
					}
				} else {
					if(Math.abs(cc - y) < 10 && bb - x + tx > 0) {
						n = "1"
					} else if(Math.abs(cc - y2) < 15 && bb - x + (tx * 3) < 0) {
						n = "2"
					} else if(bb - x + tx < 0 && bb - x + (tx * 3) > 0) {
						n = "3"
					}
					if(n == "1") {
						if(sobj.getAttribute("NodeType") == "Edge") {
							return false;
						}
						if(cc > sy && cc < (sy + sh)) {
							t = x + "," + cc + " " + x2 + "," + y2;
							a.setAttribute("oldpoints", t);
							a.setAttribute("orpoints", t);
							a.setAttribute("points", t);
							AddRouterText(a, "MoveObj")
						}
					} else if(n == "2") {
						if(eobj.getAttribute("NodeType") == "Edge") {
							return false;
						}
						if(cc > ey && cc < (ey + eh)) {
							t = x + "," + y + " " + x2 + "," + cc;
							a.setAttribute("oldpoints", t);
							a.setAttribute("orpoints", t);
							a.setAttribute("points", t);
							AddRouterText(a, "MoveObj")
						}
					} else if(n == "3") {
						if(sobj.getAttribute("NodeType") == "Edge" || eobj.getAttribute("NodeType") == "Edge") {
							return false;
						}
						if((op1 + f) > sy && (op1 + f) < (sy + sh) && (op3 + f) > ey && (op3 + f) < (ey + eh)) {
							t = x + "," + (op1 + f) + " " + x2 + "," + (op3 + f);
							a.setAttribute("oldpoints", t);
							a.setAttribute("orpoints", t);
							a.setAttribute("points", t);
							AddRouterText(a, "MoveObj")
						}
					}
				}
				break

		}

		ShowObjXYPosInBottom(a);
		var E = document.body.scrollLeft;
		var F = document.body.scrollTop;
		var G = document.body.clientWidth;
		var H = document.body.clientHeight;
		if(b > G) window.scrollTo(E + 3, F);
		if(b < 0) window.scrollTo(E - 3, F);
		if(c > H) window.scrollTo(E, F + 3);
		if(c < 0) window.scrollTo(E, F - 3)
	}

	route = {
		getOneNodeWithLines: getOneNodeWithLines, //获得一个节点边上所在的所有线
		getPointType: getPointType, // 获取线所在节点的边  Left、Right、Top、Bottom
		rightCorner: rightCorner, // 直角拐角处理
		sortSide: sortSide, // 对节点某一条边上输出线进行排序
		moveSmartLinePart: moveSmartLinePart, // 移动部分线段，边界箭头/内部箭头处理
		moveLinePart: moveLinePart // 移动部分线段，原生路由线段移动时处理
	}

	return route;
});