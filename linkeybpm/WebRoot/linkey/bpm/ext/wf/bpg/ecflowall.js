var goalTypeName = "";
var color1 = "#000000";
var color2 = "#000000";
var size1 = 0;
var xx = 0;
var yy = 0;
var zz = 100;
var moveobj = null;
var pmoveobj = null;
var hotx = 0;
var hoty = 0;
var hotx2 = 0;
var hoty2 = 0;
var hotType = "Bottom";
var hotEndType = "";
var StartPointType = "Bottom";
var goalStartObj = null;
var goalEndObj = null;
var thisobj = null;
var goalObjColor = "#000000";
var moveobjoldleft = 0;
var moveobjoldtop = 0;
var oldArryEndPosx = new Array();
var oldArryEndPosy = new Array();
var oldArryEndPosx2 = new Array();
var oldArryEndPosy2 = new Array();
var oldArryStartLeft = new Array();
var oldArryStartTop = new Array();
var goalShowGrid = 0;
var goalPrvNode = new Array();
var goalNextNode = new Array();
var goalClickStyle = "";
var HistoryArray = new Array('');
var CHistoryPos = 0;
var offsetX = 0;
var offsetY = 0;
var goalux = document.body.clientWidth;
var goaluy = document.body.clientHeight;
var goalcopyflag = "0";
var goalcopyobj;
var goalmulsel = 0;
var mstartX = 0;
var mstartY = 0;
var mendX = 0;
var mendY = 0;
var buttonflag = null;

var checkattr = true;

//update add
var getsetAttr = 1; //属性名大小写判断
var bowerflag = ""; //浏览器标志
var leftbtn = 0;
var rightbtn = 2;

var oldArryPoints = "";

function mySetType(a, b) {
	goalTypeName = a;
	StartPointType = b;
	if(a == "Move") {
		//InitOldXY();
		document.body.style.cursor = "default"
	} else if(a == "MulSel") {
		document.body.style.cursor = "default"
	} else if(a == "PolyLine") {
		document.body.style.cursor = "crosshair"
	} else if(a == "AddText") {
		document.body.style.cursor = "Text"
	} else if(a == "EditArea") {
		document.body.style.cursor = "crosshair"
	} else {
		document.body.style.cursor = "hand"
	}
}

//20171019 获取浏览器类型
function myBrowser() {
	var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
	var isOpera = userAgent.indexOf("Opera") > -1;

	//判断是否是Edge浏览器
	if(userAgent.indexOf("Edge") > -1) {
		return "Edge";
	}
	//判断是否是chrome浏览器
	if(userAgent.indexOf("Chrome") > -1) {
		return "Chrome";
	}
	//判断是否IE浏览器
	if(!!window.ActiveXObject || "ActiveXObject" in window) {
		if(navigator.appVersion.split(";")[1].replace(/[ ]/g, "") == "MSIE9.0") {
			return "IE9"
		}
		if(navigator.appVersion.split(";")[1].replace(/[ ]/g, "") == "MSIE10.0") {
			return "IE10"
		}
		if(userAgent.indexOf("rv:11.0") != -1) {
			return "IE11"
		}
		return "IENoSupport"
	};
	//判断是否Safari浏览器
	if(userAgent.indexOf("Safari") > -1) {
		return "Safari";
	}
	//判断是否Opera浏览器
	if(isOpera) {
		return "Opera"
	};
	//判断是否Firefox浏览器
	if(userAgent.indexOf("Firefox") > -1) {
		return "FF";
	}

}

//20171012  解决保存后H5自动转换为小写问题
if(Element.prototype.getAttribute) {
	Element.prototype._getAttribute = Element.prototype.getAttribute;
	Element.prototype.getAttribute = function(a) {
		var flag = this._getAttribute(a.toLowerCase());
		if(flag != null) {
			getsetAttr = 0;
			return flag;
		} else {
			getsetAttr = 1;
			return this._getAttribute(a);
		}
	}
}

//20171013  解决保存后H5自动转换为小写问题
if(Element.prototype.setAttribute) {
	Element.prototype._setAttribute = Element.prototype.setAttribute;
	Element.prototype.setAttribute = function(a, b) {
		this.getAttribute(a);
		if(getsetAttr == 0) {
			return this._setAttribute(a.toLowerCase(), b);
		} else {
			return this._setAttribute(a, b);
		}
	}
}

//20171018 解决读取旧的vml转换后，eval方法无法使用
function evalbroker(a) {
	try {
		if(document.getElementById(a) != undefined || document.getElementById(a) != null) {
			return document.getElementById(a);
		}
		return eval(a);

	} catch(e) {
		//alert(e);
	}
}

div1.onmousedown = function() {
	top.hisCurrentCode = div1.innerHTML;
	//console.log(div1.innerHTML);
	// div1.setCapture(); 改以下
	// ======================================
	if(!window.captureEvents) {
		div1.setCapture();
	} else {
		window.captureEvents(Event.MOUSEMOVE | Event.MOUSEUP);
	}
	// ======================================
	CancelPrvNextNode();
	ShowErrorInfo("");
	size1 = "2";
	zz += 1;
	xx = getEventX(event);
	yy = getEventY(event);

	mstartX = getEventOX(event);
	mstartY = getEventOY(event);
	// console.log("mstartX=>" + mstartX + " mstartY==>" + mstartY);
	offsetX = parseInt(document.body.scrollLeft);
	offsetY = parseInt(document.body.scrollTop);

	moveobj = null;
	goalStartObj = null;

	buttonflag = event.button;

	if(checkattr) {
		checkAttr();
		checkattr = false;
	}

	// 将节点文本转换为节点id
	// nodeTextToNode(event.srcElement);

	// alert(event.srcElement.parentNode.id); 使用parentNode替换parentNode
	// IE下,event对象有srcElement属性,但是没有target属性;Firefox下,event对象有target属性,但是没有srcElement属性.但他们的作用是相当的
	/*
	 * if((event.srcElement.parentNode.id == "div1" ||
	 * event.srcElement.parentNode.parentNode.id == "div1") && event.button ==
	 * 0) { if(event.srcElement.parentNode.id == "div1") {
	 */
	// alert("ID: "+event.srcElement.id);
	/*
	 * alert("ID: "+event.srcElement.id+"\ntagName:
	 * "+event.srcElement.tagName+"\nthisobj.getAttribute("NodeType"):"+event.srcElement.NodeType);
	 * 
	 */
	//alert(buttonflag);
	if((event.srcElement.parentNode.id == "svg" || event.srcElement.parentNode.parentNode.id == "svg") && event.button == leftbtn) {
		if(event.srcElement.parentNode.id == "svg") {
			moveobj = event.srcElement
			//			var obj = moveobj.getBoundingClientRect();
			//alert("bx:"+obj.left+" by:"+obj.top+"\nex:"+getEventOX(event)+"ey:"+getEventOY(event));
			//			alert("bx:"+obj.left+" by:"+obj.top+"\ncx:"+moveobj.getAttribute("cx")+"cy:"+moveobj.getAttribute("cy"));
			//alert("==>" + moveobj.getAttribute("nodetype"));
		} else {
			moveobj = event.srcElement.parentNode
		}
		pmoveobj = moveobj;
		goalStartObj = moveobj
	}

	if(goalTypeName != "MulSel") {
		if((event.srcElement.parentNode.id == "svg" || event.srcElement.parentNode.parentNode.id == "svg")) {
			if(event.srcElement.parentNode.id == "svg") {
				moveobj = event.srcElement
			} else {
				moveobj = event.srcElement.parentNode
			}
			// 点击样式改变
			ChangeObjStyle(moveobj)
		}
	}
	if(moveobj) {
		parent.west.selectTreeNode(moveobj.id)
	}
	if(event.button == leftbtn) {
		switch(goalTypeName) {
			case "MulSel":
				{}
				break;
			case "Move":
				if(moveobj != null) {
					try {
						// ichange
						SaveoldNodePosition(moveobj);
						ShowObjXYPosInBottom(moveobj)
						//console.log("savemove===");
					} catch(E) {
						ShowErrorInfo("本活动出错,请执行错误修复操作!")
					}
				}
				break;
			case "Edit":
				if(thisobj != null && thisobj.tagName != "polyline") {
					SaveoldNodePosition(thisobj)
				} else {
					moveobj = null
				}
				break;

				//20170926整合
			case "PolyLine":
				goalStartObj = unionNode(goalStartObj);
				var p1 = document.getElementById("PolyLine1");
				if(moveobj != null && (goalStartObj.tagName == "rect" || goalStartObj.tagName == "path" || goalStartObj.tagName == "polygon" || goalStartObj.tagName == "circle") && !isXYNode(goalStartObj)) {
					moveobj = unionNode(moveobj);
					goalStartObj = moveobj;
					if(goalStartObj.getAttribute("NodeType") == "StartNode") {
						if(goalStartObj.getAttribute("LinkeyStartObj") != "") {
							goalStartObj = null;
							return false
						}
					}
					if(goalStartObj.getAttribute("NodeType") == "EndNode") {
						goalStartObj = null;
						return false
					}
					getObjXY(moveobj, getEventOX(event), getEventOY(event));
					hotx = hotx - 0 + offsetX;
					hoty = hoty - 0 + offsetY;
					p1.setAttribute("left", hotx);
					p1.setAttribute("top", hoty);
					p1.setAttribute("points", hotx + "," + hoty + " " + hotx + "," + hoty);
					PolyLine1.style.display = ""
				}
				break;

				/*case "PolyLine":
					if(moveobj != null &&
						(goalStartObj.tagName == "rect" ||
							goalStartObj.tagName == "path" || goalStartObj.tagName == "polygon" || goalStartObj.tagName == "circle")) {
						goalStartObj = moveobj;
						if(goalStartObj.getAttribute("NodeType") == "StartNode") {
							if(goalStartObj.getAttribute("LinkeyStartObj") != "") {
								goalStartObj = null;
								return false
							}
						}
						if(goalStartObj.getAttribute("NodeType") == "EndNode") {
							goalStartObj = null;
							return false
						}
						getObjXY(moveobj, getEventOX(event), getEventOY(event));
						PolyLine1.style.left = hotx;
						PolyLine1.style.top = hoty;
						PolyLine1.points.value = "0,0";
						PolyLine1.style.display = ""
					}
					break;*/

				//20170926  添加InitOldXY()，解决刚创建就选择多选而不准的问题。
			case "AddActivity":
				{
					AddNode("Activity");
					SVG_ZIndex(document.getElementById("svg").childNodes);
				}
				break;
			case "AddAutoActivity":
				{
					AddNode("AutoActivity");
					SVG_ZIndex(document.getElementById("svg").childNodes);
				}
				break;
			case "AddSubProcess":
				{
					AddNode("SubProcess");
					SVG_ZIndex(document.getElementById("svg").childNodes);
				}
				break;
			case "AddEdge":
				{
					AddNode("Edge");
					SVG_ZIndex(document.getElementById("svg").childNodes);
				}
				break;
			case "AddStart":
				{
					AddNode("StartNode");
					SVG_ZIndex(document.getElementById("svg").childNodes);
				}
				break;
			case "AddEnd":
				{
					AddNode("EndNode");
					SVG_ZIndex(document.getElementById("svg").childNodes);
				}
				break;
			case "AddEvent":
				{
					AddNode("EventNode");
					SVG_ZIndex(document.getElementById("svg").childNodes);
				}
				break;
			case "AddText":
				{
					AddText();
					SVG_ZIndex(document.getElementById("svg").childNodes);
				}
				break;
			case "AddPool":
				{
					AddNode("Pool");
					SVG_ZIndex(document.getElementById("svg").childNodes);
				}
				break;
			case "AddOutProcess":
				{
					AddNode("OutProcess");
					SVG_ZIndex(document.getElementById("svg").childNodes);
				}
				break;
				//			case "AddUnit":
				//				{
				//					AddUnit();
				//					SVG_ZIndex(document.getElementById("svg").childNodes);
				//				}
				//				break;
				//			case "AddUnitProcess":
				//				{
				//					AddUnitProcess();
				//					SVG_ZIndex(document.getElementById("svg").childNodes);
				//				}
				//				break;
			case "AddArea":
				{
					AddArea();
					SVG_ZIndex(document.getElementById("svg").childNodes);
				}
				break;
			case "AddTextNode":
				{
					AddTextNode();
					SVG_ZIndex(document.getElementById("svg").childNodes);
				}
				break;
			case "AddXNode":
				{
					AddXNode();
					SVG_ZIndex(document.getElementById("svg").childNodes);
				}
				break;
			case "AddYNode":
				{
					AddYNode();
					SVG_ZIndex(document.getElementById("svg").childNodes);
				}
				break
		}
		InitOldXY();
		//SVG_ZIndex(document.getElementById("svg").childNodes);
	}
	if((moveobj == null || moveobj.getAttribute("NodeType") == "XNode" || moveobj.getAttribute("NodeType") == "YNode") && goalTypeName == "MulSel") {
		var moffsetX = parseInt(document.body.scrollLeft);
		var moffsetY = parseInt(document.body.scrollTop);

		var rectdotted = document.getElementById("rect1");
		rectdotted.setAttribute("x", (mstartX + moffsetX));
		rectdotted.setAttribute("y", (mstartY + moffsetY));
		rectdotted.setAttribute("width", 0);
		rectdotted.setAttribute("height", 0);
		rectdotted.style.display = ""
		//		alert("height: "+rectdotted.getAttribute("height"));

		/*rect1.style.x = mstartX + moffsetX;
		rect1.style.y = mstartY + moffsetY;
		rect1.style.width = 0;
		rect1.style.height = 0;
		rect1.style.display = ""*/
	}
}

/*// svg坐标转换器,a节点对象，b输出，c输入
function converterSVG(a, b, c) {
	if("circle" == a.tagName) {
		if(b == null && c != undefined) {
			if("left" == c) {
				return "cx";
			} else if("top" == c) {
				return "cy";
			}
		}
		if("left" == b) {
			return a.getAttribute("cx");
		} else if("top" == b) {
			return a.getAttribute("cy");
		}
	} else if(a.tagName == "path" || a.tagName == "polygon") {
		var distance = a.getBoundingClientRect();
		if("left" == b) {
			return distance.left;
		}
		if("top" == b) {
			return distance.top;
		}
	} else {
		if(b == null && c != undefined) {
			if(b == null && c != undefined) {
				if("left" == c) {
					return "x";
				} else if("top" == c) {
					return "y";
				}
			}
		}
		if("left" == b) {
			return a.getAttribute("x");
		} else if("top" == b) {
			return a.getAttribute("y");
		}
	}
}*/
//20170924  重新修改SVG转换器  a对象，b为left/top
function converterSVG(a, b) {
	var distance = a.getBoundingClientRect();
	var box = a.getBBox();
	if("left" == b) {
		return distance.left;
	}
	if("top" == b) {
		return distance.top;
	}
	if("width" == b) {
		return box.width;
	}
	if("height" == b) {
		return box.height;
	}
}

// 20170920 a根据文本返回节点，b为节点或节点对应文本,c为文本转节点id,nodeid
function unionNode(a, b, c) {
	if(a == null && b == null && c != undefined) {
		return c.replace(/_text/, "");
	}

	// 根据节点返回text节点
	if(b != null && b != undefined) {
		if(b.getAttribute("NodeType") != "XNode" && b.getAttribute("NodeType") != "XNode_text" && b.getAttribute("NodeType") != "XNode_rect" && b.getAttribute("NodeType") != "YNode" && b.getAttribute("NodeType") != "YNode_text" && b.getAttribute("NodeType") != "YNode_rect") {
			if(b.id.indexOf("_text") == -1) {
				b = document.getElementById(b.id + "_text");
			} else {
				var temp = b.id;
				temp = unionNode(null, null, temp);
				b = document.getElementById(temp);
			}
		} else {
			b = null;
		}

		return b;
	}

	// 判断是不是节点text，返回节点
	if(a != null && a != undefined) {
		if(a.id.indexOf("_text") != -1) {
			var temp = a.id;
			temp = unionNode(null, null, temp);
			a = document.getElementById(temp);
		}
		return a;
	}
}

var YW_YWDRUNID = "",
	YW_ProcessName = "";

function AddUnit() {
	var a = "Activity";
	var b;
	var c = getEventOX(event);
	var d = getEventOY(event);
	c = GetGridXY(c, "x");
	d = GetGridXY(d, "y");
	var e = c + offsetX - 35;
	var f = d + offsetY - 25;
	var g = GetNodeNum(a);
	var h = "<v:roundrect id='Node" + g + "' UnitId='" + YW_YWDRUNID + "' NodeNum='" + g + "' NodeType='Activity' LinkeyStartObj='' LinkeyEndObj='' style='position:absolute;left:" + e + "px;top:" + f + "px;width:96px;height:50px;z-index:10;text-align:center;' onDblClick=\"SetProperty (this,'Activity');\"  arcsize='10923f' fillcolor=\"#daeef3 [664]\" strokecolor='#004d86' strokeweight='1.20pt'>";
	b = div1.document.createElement(h);
	div1.appendChild(b);
	b.innerHTML = "<v:fill color2=\"white [3212]\" rotate=\"t\" focus=\"100%\" type=\"gradient\"/><v:shadow on=\"t\" type=\"perspective\"  offset=\"1pt\" offset2=\"-3pt\"/><v:TextBox id='TextBoxNode" + g + "' style='font-size:9pt;' inset='1pt,1pt,1pt,1pt' >" + g + "." + YW_ProcessName + "</v:TextBox>";
	AutoSaveUnitNode(YW_ProcessName, "Node" + g, YW_YWDRUNID, a);
	return b
}

function AddTextNode() {
	var a = "TextNode";
	var b;
	var c = getEventOX(event);
	var d = getEventOY(event);
	c = GetGridXY(c, "x");
	d = GetGridXY(d, "y");
	var e = c + offsetX - 35;
	var f = d + offsetY - 25;
	var g = GetNodeNum(a);
	var h = "<v:roundrect id='Node" + g + "'  NodeNum='" + g + "' NodeType='TextNode' LinkeyStartObj='' LinkeyEndObj='' style='position:absolute;left:" + e + "px;top:" + f + "px;width:96px;height:50px;z-index:10;text-align:center;' onDblClick=\"SetProperty (this,'TextNode');\"  arcsize='10923f' fillcolor=\"#EAFDF1\" strokecolor='#336600' strokeweight='1.20pt'>";
	b = div1.document.createElement(h);
	div1.appendChild(b);
	b.innerHTML = "<v:TextBox id='TextBoxNode" + g + "' style='font-size:9pt;' inset='1pt,1pt,1pt,1pt' ></v:TextBox>";
	return b
}

function AddUnitProcess() {
	var a = "SubProcess";
	var b;
	var c = getEventOX(event);
	var d = getEventOY(event);
	c = GetGridXY(c, "x");
	d = GetGridXY(d, "y");
	var e = c + offsetX - 35;
	var f = d + offsetY - 25;
	var g = GetNodeNum(a);
	var h = "<v:shapetype id='SNode" + g + "' coordsize='21600,21600' o:spt='112' ><v:stroke joinstyle='miter'/><v:path o:extrusionok='f' gradientshapeok='t' o:connecttype='rect' textboxrect='2610,0,18990,21600'/></v:shapetype>";
	b = div1.document.createElement(h);
	div1.appendChild(b);
	h = "<v:shape  id='Node" + g + "' NodeNum='" + g + "' NodeType='SubProcess' type='#SNode" + g + "'  LinkeyStartObj='' LinkeyEndObj='' type=\"#_x0000_t112\" style='position:absolute;left:" + e + ";top:" + f + ";text-align:center;width:96px;height:50px;z-index:2' fillcolor=\"#daeef3 [664]\" strokeweight='1.20pt' strokecolor='#004d86' onDblClick=\"SetProperty (this,'SubProcess')\" >";
	h += "<v:fill color2=\"white [3212]\" rotate=\"t\" focus=\"100%\" type=\"gradient\"/>";
	h += "<v:shadow on=\"t\" type=\"perspective\"  offset=\"1pt\" offset2=\"-3pt\"/><v:TextBox inset='1pt,1pt,1pt,1pt' id='TextBoxNode" + g + "' style='font-size:9pt;text-align:center'>" + g + "." + YW_ProcessName + "</v:TextBox>";
	h += "</v:shape>";
	b.outerHTML += h;
	return b
}

function AutoSaveUnitNode(a, b, c, d) {
	var e = xmlframe.document.all.unid.value;
	var f = "agentSaveUnitNode?openagent&NodeName=" + a + "&Nodeid=" + b + "&NodeType=" + d + "&YW_YWDRUNID=" + c + "&ProcessUNID=" + e;
	var g = Ext.lib.Ajax.getConnectionObject().conn;
	g.open("GET", f, false);
	g.send();
	if(g.status == "200") {
		var h = Ext.util.JSON.decode(g.responseText);
		document.all(b).SaveFlag = "yes";
		ShowErrorInfo("节点保存成功!")
	} else {
		alert("节点数据创建失败，请删除图标重新创建一次!")
	}
}

function ChangeTextBox() {
	for(var i = 0; i < document.all.length; i++) {
		var a = document.all(i);
		if(a.inset != undefined) {}
	}
}

function AddXNode() {
	var a = "XNode";
	var b, cobj1;
	var c = getEventOX(event);
	var d = getEventOY(event);
	c = GetGridXY(c, "x");
	d = GetGridXY(d, "y");
	var e = c + offsetX - 35;
	var f = d + offsetY - 25;
	var g = GetNodeNum(a);
	var h = g + 10000;
	var svgroot = document.getElementById("svg");

	var XNodeI = svg.create("rect").attrNS({
		id: "Node" + g,
		x: 10,
		y: f,
		width: "1000",
		height: "200",
		fill: "url(#XNode)",
		"stroke-width": "1",
		"stroke": "#999999"
	}).attr({
		"NodeType": "XNode",
		"NodeNum": g,
		"POSITION": "absolute",
		"my-index": "2",
		onmousedown: "flowtableonmousedown1()"
	}).appendTo(svgroot).node;

	var XNodeJ = svg.create("rect").attrNS({
		id: "Node" + h,
		x: 10,
		y: f,
		width: "30",
		height: "200",
		fill: "url(#XNode_rect)",
		"stroke-width": "1",
		"stroke": "#999999"
	}).attr({
		"NodeType": "XNode_rect",
		"NodeNum": g,
		"my-index": "3",
		onmousedown: "flowtableonmousedown1()"
	}).appendTo(svgroot).node;
	var svgtext = svg.create("text").attrNS({
		id: "Node" + g + "_xtb",
		x: 25,
		y: f + 50,
		"font-size": "9pt",
		fill: "black",
		"writing-mode": "tb-rl",
		"text-algin": "center",
		"font-weight": "bold"
	}).attr({
		"NodeType": "XNode_text",
		"my-index": "4",
		onmousedown: "flowtableondblclick1()"
	}).appendTo(svgroot).node;
	svgtext.textContent = "部门名称";
	b = XNodeI;
	cobj1 = XNodeJ;
	return b
}

function AddYNode() {
	var a = "YNode";
	var b, cobj1;
	var c = getEventOX(event);
	var d = getEventOY(event);
	c = GetGridXY(c, "x");
	d = GetGridXY(d, "y");
	var e = c + offsetX - 35;
	var f = d + offsetY - 25;
	var g = GetNodeNum(a);
	var h = g + 10000;
	var svgroot = document.getElementById("svg");

	var YNodeI = svg.create("rect").attrNS({
		id: "Node" + g,
		x: e,
		y: 10,
		width: "200",
		height: "600",
		fill: "url(#YNode)",
		"stroke-width": "1",
		"stroke": "#999999"
	}).attr({
		"NodeType": "YNode",
		"NodeNum": g,
		"my-index": "2",
		onmousedown: "flowtableonmousedown1()"
	}).appendTo(svgroot).node;

	var YNodeJ = svg.create("rect").attrNS({
		id: "Node" + h,
		x: e,
		y: 10,
		width: "200",
		height: "30",
		fill: "url(#YNode_rect)",
		"stroke-width": "1",
		"stroke": "#999999"
	}).attr({
		"NodeType": "YNode_rect",
		"NodeNum": g,
		"my-index": "3",
		onmousedown: "flowtableonmousedown1()"
	}).appendTo(svgroot).node;
	var svgtext = svg.create("text").attrNS({
		id: "Node" + g + "_xtb",
		x: e + 50,
		y: 30,
		"font-size": "9pt",
		"text-anchor": "middle",
		fill: "black",
		"font-weight": "bold",
		"text-algin": "center"
	}).attr({
		"NodeType": "YNode_text",
		"my-index": "4",
		onmousedown: "flowtableondblclick1()"
	}).appendTo(svgroot).node;
	svgtext.textContent = "部门名称";
	b = YNodeI;
	cobj1 = YNodeJ;
	return b
}

var DeptObj;
//20170929 整合
function flowtableondblclick1() {
	var a = document.body.scrollLeft;
	var b = document.body.scrollTop;
	if(event.button == leftbtn) {
		DeptObj = event.srcElement;
		CellText.style.left = getEventOX(event) + a + 5;
		CellText.style.top = getEventOY(event) + b + 25;
		CellText.style.display = "";
		CellTextBody.value = DeptObj.textContent;
		CellTextBody.focus()
	}
}
//20171030 解决设置文字为""后无法重命名的问题
//20170929 整合
function flowtableonmousedown1() {
	if(DeptObj) {
		if(document.all.CellTextBody.value != "")
			DeptObj.textContent = document.all.CellTextBody.value;
		else
			DeptObj.textContent = "部门名称";
		DeptObj = false;
		CellText.style.display = "none"
	}
}

function flowtableondblclick1_old() {
	var a = document.body.scrollLeft;
	var b = document.body.scrollTop;
	DeptObj = event.srcElement;
	CellText.style.posLeft = getEventOX(event) + a + 5;
	CellText.style.posTop = getEventOY(event) + b + 25;
	CellText.style.display = "";
	CellTextBody.value = DeptObj.innerText;
	CellTextBody.focus()
}

function flowtableonmousedown1_old() {
	if(DeptObj) {
		DeptObj.innerText = document.all.CellTextBody.value;
		DeptObj = false;
		CellText.style.display = "none"
	}
}

function AddArea() {
	var a = "Area";
	var b;
	var c = getEventOX(event);
	var d = getEventOY(event);
	c = GetGridXY(c, "x");
	d = GetGridXY(d, "y");
	var e = c + offsetX - 35;
	var f = d + offsetY - 25;
	var g = GetNodeNum(a);
	var h = "<v:roundrect id='Node" + g + "' NodeNum='" + g + "' NodeType='Area' LinkeyStartObj='' LinkeyEndObj='' style='position:absolute;left:" + e + "px;top:" + f + "px;width:400px;height:200px;z-index:1;text-align:center;'  arcsize='10923f' fillcolor=\"#fffff7\" strokecolor='#ffcf37' strokeweight='1pt'>";
	b = div1.document.createElement(h);
	div1.appendChild(b);
	b.innerHTML = "<v:fill opacity=\"60948f\"/>";
	return b
}

function AddDocumentNode(a) {
	var b = document.getElementById(a);
	var c = "DocNode";
	var a = b.id + "_Doc";
	if(document.getElementById(a)) {
		return false
	}
	var d = b.id;
	var e;
	var f = parseInt(b.style.left);
	var g = parseInt(b.style.top);
	var h = f + parseInt(b.style.width) - 15;
	var i = g + parseInt(b.style.height) - 18;
	var j = GetNodeNum(c);
	var k = '<v:shapetype id="T' + a + '"	 coordsize="21600,21600" o:spt="114" >	 <v:stroke joinstyle="miter"/>	 <v:path o:connecttype="custom" o:connectlocs="10800,0;0,10800;10800,20400;21600,10800"	  textboxrect="0,0,21600,17322"  />	</v:shapetype>';
	e = div1.document.createElement(k);
	div1.appendChild(e);
	e.outerHTML += '<v:shape id="' + a + '" NodeType="' + c + '" GroupId="' + d + '" type="#T' + a + '" oldleft="' + h + '" oldtop="' + i + '" style="position:absolute; left:' + h + ';top:' + i + ';width:11px; height:13px;z-index:1600" fillcolor="#ffffa7" onDblClick="SetProperty (this,\'DocNode\')" />';
	return e
}

function DelDocumentNode(a) {
	var b = a + "_Doc";
	var c = document.getElementById(b);
	if(c) {
		c.outerHTML = ""
	}
}

div1.onmousemove = function() {
	var tempx = getEventOX(event);
	var tempy = getEventOY(event);

	//console.log("buttonflag:"+buttonflag);

	if(buttonflag == leftbtn) {
		switch(goalTypeName) {
			case "MulSel":
				MoveAndShowMulRect();
				break;
			case "EditArea":
				EditArea(moveobj);
				break;
			case "Move":
				var MulSelNode = top.document.all.MulNodeList.value;
				if(MulSelNode == "") {
					if(moveobj && moveobj != null) {

						if(moveobj.getAttribute("NodeType") == "XNode" || moveobj.getAttribute("NodeType") == "YNode") {
							return
						}
					}
					if(moveobj != null && moveobj.tagName != "polyline") {
						// iadd
						//console.log("tempx==>" + tempx + " tempy==" + tempy);
						MoveNodeObj(moveobj, tempx, tempy);
						ToMoveLine(moveobj, tempx, tempy);

						if((tempx != xx || tempy != yy) && moveobj.tagName != "SPAN") {
							// 显示虚线
							SvgDottedLine(moveobj);
						}
					} else if(moveobj != null && moveobj.tagName == "polyline") {
						//20171020移动线的暂时被我注销
						MoveLine(moveobj, tempx, tempy);
						checkAttr();
					}
					if(moveobj != null && (moveobj.getAttribute("NodeType") == "XNode_text" || moveobj.getAttribute("NodeType") == "YNode_text")) {
						var tid = moveobj.id;
						if(tid.substring(4) < 10000) tid = parseInt(tid.substring(4)) + 10000;
						else tid = parseInt(tid.substring(4)) - 10000;
						var newobj = document.getElementById("Node" + tid);
						if(newobj) {
							MoveNodeObj(newobj, tempx, tempy)
						}
					}
					//movegroupnode(moveobj)
				} else {
					MulSel_MoveNodeObj()
					ToMoveLine(null, tempx, tempy);
				}
				break;
			case "Edit":
				if(thisobj != null) {
					if(thisobj.tagName == "polyline") {
						StartPointType = thisobj.PolyLineType;
						var PointsStr = GetPointsStr("Edit");
						thisobj.points.value = PointsStr
					} else {
						moveobj = thisobj;
						offsetX = document.body.scrollLeft;
						offsetY = document.body.scrollTop;
						tempx += offsetX;
						tempy += offsetY;
						if(tempx > moveobjoldleft) {
							moveobj.style.width = tempx - moveobjoldleft
						} else {
							moveobj.style.left = tempx;
							moveobj.style.width = moveobjoldleft - tempx
						}
						if(tempy > moveobjoldtop) {
							moveobj.style.height = tempy - moveobjoldtop
						} else {
							moveobj.style.top = tempy;
							moveobj.style.height = moveobjoldtop - tempy
						}
					}
				}
				break;

				//20170926整合
			case "PolyLine":
				var p1 = document.getElementById("PolyLine1");
				var PointsStr = GetPointsStr();
				p1.setAttribute("points", PointsStr);
				ShowEndHotPoint();
				break
		}
	}
}

/*div1.onmousemove = function() {
	var tempx = getEventX(event) + _x1;
	var tempy = getEventY(event) + _y1;

	if(event.button == 0) {

		switch(goalTypeName) {
			case "MulSel":
				MoveAndShowMulRect();
				break;
			case "EditArea":
				EditArea(moveobj);
				break;
			case "Move":

				var MulSelNode = top.document.all.MulNodeList.value;
				if(MulSelNode == "") {

					if(moveobj && moveobj != null) {

						if(moveobj.getAttribute("NodeType") == "XNode" || moveobj.getAttribute("NodeType") == "YNode") {
							return
						}
					}
					if(moveobj != null && moveobj.tagName != "polyline") {
						// iadd
						// console.log("tempx==>" + tempx + " tempy==" + tempy);
						MoveNodeObj(moveobj, tempx, tempy);

						if((tempx != xx || tempy != yy) && moveobj.tagName != "SPAN") {
							// 显示虚线
							SvgDottedLine(moveobj);
						}
					} else if(moveobj != null && moveobj.tagName == "polyline") {
						MoveLine(moveobj, tempx, tempy)
					}
					if(moveobj != null && (moveobj.getAttribute("NodeType") == "XNode_text" || moveobj.getAttribute("NodeType") == "YNode_text")) {
						var tid = moveobj.id;
						if(tid.substring(4) < 10000) tid = parseInt(tid.substring(4)) + 10000;
						else tid = parseInt(tid.substring(4)) - 10000;
						var newobj = document.getElementById("Node" + tid);
						if(newobj) {
							MoveNodeObj(newobj, tempx, tempy)
						}
					}
					movegroupnode(moveobj)
				} else {
					MulSel_MoveNodeObj()
				}
				break;
			case "Edit":
				if(thisobj != null) {
					if(thisobj.tagName == "polyline") {
						StartPointType = thisobj.PolyLineType;
						var PointsStr = GetPointsStr("Edit");
						thisobj.points.value = PointsStr
					} else {
						moveobj = thisobj;
						offsetX = document.body.scrollLeft;
						offsetY = document.body.scrollTop;
						tempx += offsetX;
						tempy += offsetY;
						if(tempx > moveobjoldleft) {
							moveobj.style.width = tempx - moveobjoldleft
						} else {
							moveobj.style.left = tempx;
							moveobj.style.width = moveobjoldleft - tempx
						}
						if(tempy > moveobjoldtop) {
							moveobj.style.height = tempy - moveobjoldtop
						} else {
							moveobj.style.top = tempy;
							moveobj.style.height = moveobjoldtop - tempy
						}
					}
				}
				break;
			case "PolyLine":
				var PointsStr = GetPointsStr();
				PolyLine1.points.value = PointsStr;
				ShowEndHotPoint();
				break
		}
	}
}*/

function movegroupnode(a) {
	if(a != null && (a.getAttribute("NodeType") == "Activity" || a.getAttribute("NodeType") == "SubProcess")) {
		var b = getEventOX(event);
		var c = getEventOY(event);
		var d = "";
		var e;
		for(var i = 0; i < document.all.length; i++) {
			e = document.all[i];
			d = document.all[i].GroupId;
			if(d != undefined && d == a.id) {
				MoveNodeObjV9(e, b, c)
			}
		}
	}
}

div1.onmouseup = function() {
	hideStartHotPoint();
	// document.releaseCapture();
	// ================================================
	if(window.captureEvents) {
		window.captureEvents(Event.MOUSEMOVE | Event.MOUSEUP);
	} else {
		document.releaseCapture();
	}
	// ====================================================
	oldArryPoints = "";
	ShowObjXYPosInBottom(moveobj);
	PolyLine1.style.display = "none";
	PolyLineX.style.display = "none";
	PolyLineY.style.display = "none";
	buttonflag = null;
	/*var tempx = getEventOX(event) + _x1;
	var tempy = getEventOY(event) + _y1;*/
	var tempx = getEventOX(event);
	var tempy = getEventOY(event);
	tempx += offsetX;
	tempy += offsetY;

	if(goalTypeName == "MulSel") {
		if(moveobj) {
			if(moveobj.tagName == "text" && unionNode(null, moveobj)) {
				moveobj = unionNode(null, moveobj);
			}
			if((moveobj.tagName == "circle" || moveobj.tagName == "rect" || moveobj.tagName == "path" || moveobj.tagName == "polygon" || moveobj.tagName == "polygon") && !isXYNode(moveobj)) {
				MulSel_AddNode(moveobj.id)
			}
		}
		MoveAndSelectMulNode();
		MulSel_ChangeNodeStyle();
		return
	} else {
		ChangeObjStyle()
	}

	if((event.srcElement.parentNode.id == "svg" || event.srcElement.parentNode.parentNode.id == "svg") && event.button == rightbtn && moveobj == null) {
		if(event.srcElement.parentNode.id == "svg") {
			thisobj = event.srcElement;
			goalTypeName = "Move";
			document.body.style.cursor = "default";
			return false
		} else {
			thisobj = event.srcElement.parentNode;
			goalTypeName = "Move";
			document.body.style.cursor = "default";
			return false
		}
	} else if(event.srcElement.tagName == "DIV" && event.button == rightbtn && goalTypeName == "Move") {
		thisobj = null
	} else if(event.srcElement.tagName == "TD" && event.button == rightbtn && goalTypeName == "Move") {
		thisobj = null
	}
	if(event.button == leftbtn) {
		switch(goalTypeName) {
			case "Move":
				InitOldXY();
				break;
			case "Edit":
				goalTypeName = "Move";
				thisobj = null;
				moveobj = null;
				break;
			case "PolyLine":
				if(goalStartObj != null && CheckConnect(goalEndObj) && (goalStartObj.tagName == "rect" || goalStartObj.tagName == "circle" || goalStartObj.tagName == "path" || goalStartObj.tagName == "polygon") && (goalEndObj.tagName == "rect" || goalEndObj.tagName == "path" || goalEndObj.tagName == "polygon" || (goalEndObj.tagName == "circle" && (goalEndObj.getAttribute("NodeType") == "EndNode" || goalEndObj.getAttribute("NodeType") == "Event")))) {
					//alert("goalStartObj: "+goalStartObj );
					CreateObj("PolyLine", tempx, tempy);
					checkAttr();
				}
				break
		}
	} else if(event.button == rightbtn) {
		if(insertText.style.display != "none") {
			AddNode("AddText")
		}
		if(event.srcElement.id == "svg") {
			thisobj = event.srcElement
		} else if(event.srcElement.parentNode.id == "svg") {
			thisobj = event.srcElement
		} else {
			thisobj = event.srcElement.parentNode
		}
		goalTypeName = "Move";
		document.body.style.cursor = "default"
	}
	moveobj = null;
	hotType = "";
	top.hisEndCode = div1.innerHTML;
	top.putHisData();

}

//20171024 线连接定位校验
function CheckConnect(a) {
	var x = converterSVG(a, "left") - 0;
	var y = converterSVG(a, "top") - 0;
	var w = converterSVG(a, "width") - 0;
	var h = converterSVG(a, "height") - 0;
	var ox = getEventOX(event) - 0;
	var oy = getEventOY(event) - 0;
	//	console.log("x:" + x + " y:" + y + " w:" + w + " h:" + h);
	//	console.log("ox:" + ox + " oy:" + oy);
	if(ox < (x - 4) || ox > (x + w + 4) || oy < (y - 4) || oy > (y + h + 4))
		return false;
	else
		return true;
}

//20170925 选择多选时方格虚线
function MoveAndShowMulRect() {
	var a = parseInt(document.body.scrollLeft);
	var b = parseInt(document.body.scrollTop);
	var c = getEventOX(event);
	var d = getEventOY(event);
	//console.log("c:"+c+"  mstartX:"+mstartX);
	var rectdotted = document.getElementById("rect1");
	//var svgobj = document.getElementById("svg");
	if(c - mstartX < 0) {
		rectdotted.setAttribute("x", (c + a));
		rectdotted.setAttribute("width", (mstartX - c));
	} else {
		rectdotted.setAttribute("width", (c - mstartX));
	}
	if(d - mstartY < 0) {
		rectdotted.setAttribute("y", (d + b));
		rectdotted.setAttribute("height", (mstartY - d));
	} else {
		rectdotted.setAttribute("height", (d - mstartY));
	}
	//svgobj.appendChild(rectdotted);
	//console.log("x:" + rectdotted.getAttribute("x") + " y:" + rectdotted.getAttribute("x") + " width:" + rectdotted.getAttribute("width") + " height:" + rectdotted.getAttribute("height") + " disploy:" + rectdotted.getAttribute("display"));
}

function MoveAndShowMulRect_old() {

	var a = parseInt(document.body.scrollLeft);
	var b = parseInt(document.body.scrollTop);
	var c = getEventOX(event);
	var d = getEventOY(event);
	if(c - mstartX < 0) {
		rect1.style.x = c + a;
		rect1.style.width = (mstartX - c)
	} else {
		rect1.style.width = (c - mstartX)
	}
	if(d - mstartY < 0) {
		rect1.style.y = d + b;
		rect1.style.height = (mstartY - d)
	} else {
		rect1.style.height = (d - mstartY)
	}
}

function MoveAndSelectMulNode() {
	mendX = getEventOX(event);
	mendY = getEventOY(event);
	rect1.style.display = "none";
	if(moveobj != null && moveobj.getAttribute("NodeType") != "XNode" && moveobj.getAttribute("NodeType") != "YNode") return;
	var a = parseInt(document.body.scrollLeft);
	var b = parseInt(document.body.scrollTop);
	mendX = mendX + a;
	mendY = mendY + b;
	mstartX = mstartX + a;
	mstartY = mstartY + b;
	if(mendX < mstartX) {
		var c = mstartX;
		mstartX = mendX;
		mendX = c
	}
	if(mendY < mstartY) {
		var d = mstartY;
		mstartY = mendY;
		mendY = d
	}
	for(var i = 0; i < document.all.length; i++) {
		var e = document.all(i);
		var f = e.tagName;
		if((f == "rect" || f == "circle" || f == "path" || f == "polygon")) {
			var g = parseInt(converterSVG(e, "left")) + offsetX;
			var h = g + parseInt(converterSVG(e, "width"));
			var j = parseInt(converterSVG(e, "top")) + offsetY;
			var k = j + parseInt(converterSVG(e, "height"));
			if(mstartX < h && h < mendX && mstartY < k && k < mendY && mstartX < g && g < mendX && mstartY < j && j < mendY) {
				var l = e.id;
				MulSel_AddNode(l, "AddOnly")
			}
		}
	}
	MulSel_ChangeNodeStyle()
}

//20171107 重新修改大小变换的函数
//20170929 整合
function EditArea(a) {
	checkAttr();
	if(a.getAttribute("NodeType") == 'YNode_text' || a.getAttribute("NodeType") == 'XNode_text' || a.tagName == "polyline") {
		return
	}
	var b = parseInt(document.body.scrollLeft);
	var c = parseInt(document.body.scrollTop);
	var d = getEventOX(event);
	var e = getEventOY(event);
	var w = d - parseInt(converterSVG(a, "left"));
	var h = e - parseInt(converterSVG(a, "top"));
	if(w < 30) {
		w = 30
	}
	if(h < 30) {
		h = 20
	}
	if("rect" == a.tagName && !isXYNode(a)) {
		a.setAttribute("width", w);
		a.setAttribute("height", h);
	} else if("circle" == a.tagName) {
		a.setAttribute("r", h / 2);
	} else if(a.tagName == "path") {
		if("SubProcess" == a.getAttribute("NodeType")) {
			var d1 = w - 0.1 * w;
			var str = "M0 0 L" + w + " 0 L" + w + " " + h + " L" + d1 + " " + h + " L" + d1 + " 0 L" + d1 + " " + h + " L" + 0.1 * w + " " + h + " L" + 0.1 * w + " 0" + " L" + 0.1 * w + " " + h + " L0 " + h + " Z";
			a.setAttribute("d", str);
		} else if("OutProcess" == a.getAttribute("NodeType")) {
			var str = "M" + 0.1 * w + " 0 L" + (w - 0.1 * w) + " 0 A" + 0.1 * w + " " + 0.1 * w + ",0,0,0," + w + " " + 0.1 * w + " L" + w + " " + (h - 0.1 * w) + " A" + 0.1 * w + " " + 0.1 * w + ",0,0,0" + (w - 0.1 * w) + " " + h + " L" + 0.1 * w + " " + h + " A" + 0.1 * w + " " + 0.1 * w + ",0,0,0,0 " + (h - 0.1 * w) + " L0 " + 0.1 * w + " A" + 0.1 * w + " " + 0.1 * w + ",0,0,0," + 0.1 * w + " 0Z";
			a.setAttribute("d", str);
		}
	}
	if(a.tagName == "polygon") {
		h = h * 2;
		a.setAttribute("points", (d - w / 2 + offsetX) + "," + (e - h / 2 + offsetY) + " " + (d + offsetX) + "," + (e + offsetY) + " " + (d - w / 2 + offsetX) + "," + (e + h / 2 + offsetY) + " " + (d - w + offsetX) + "," + (e + offsetY));
	}
	var f = document.getElementById(a.id + '_text')
	if(f) {
		//		var j = a.id;
		//		j = j = parseInt(j.substring(4)) + 10000;
		//		var k = document.getElementById("Node" + j);
		//		if(a.getAttribute("NodeType") == 'YNode') {
		//			k.style.width = w
		//		}
		//		if(a.getAttribute("NodeType") == 'XNode') {
		//			k.style.height = h;
		//			var l = k.innerText.length;
		//			var m = l * 15;
		//			var n = (h - m) / 2;
		//			if(n < 0) {
		//				n = 0
		//			}
		//			//f.inset = "3px," + n + "px,3px,3px"
		//		}
		f.setAttribute("x", converterSVG(a, "left") + w / 2 + b);
		f.setAttribute("y", converterSVG(a, "top") + h / 2 + c);
	}
	ToMoveLine(a);
	checkAttr();
}

//20171107 change to Big and Small
//function changetoBS(a) {
//	g = a.getAttribute("LinkeyStartObj");
//	h = a.getAttribute("LinkeyEndObj");
//	if(g != "" && g != undefined) {
//		var j = g.split(",");
//		for(i = 0; i < j.length; i++) {
//			var b = evalbroker(j[i]);
//			var oo = b.id.replace("polyline", "").split("_");
//			var hotType = b.getAttribute("hotType");
//			var r = b.getAttribute("PolyLineType");
//
//			var sobj = evalbroker("Node" + oo[0]);
//			var eobj = evalbroker("Node" + oo[1]);
//
//		}
//
//	}
//	if(h != "" && h != undefined) {
//		var j = h.split(",");
//		for(i = 0; i < j.length; i++) {
//			var b = evalbroker(j[i]);
//			var oo = b.id.replace("polyline", "").split("_");
//			var hotType = b.getAttribute("hotType");
//			var r = b.getAttribute("PolyLineType");
//
//			var sobj = evalbroker("Node" + oo[0]);
//			var eobj = evalbroker("Node" + oo[1]);
//
//		}
//	}
//}

function EditArea_old(a) {
	if(a.getAttribute("NodeType") == 'YNode_text' || a.getAttribute("NodeType") == 'XNode_text' || a.tagName == "polyline") {
		return
	}
	var b = parseInt(document.body.scrollLeft);
	var c = parseInt(document.body.scrollTop);
	var d = getEventOX(event);
	var e = getEventOY(event);
	var w = d - parseInt(a.style.left) + b;
	var h = e - parseInt(a.style.top) + c;
	if(w < 30) {
		w = 30
	}
	if(h < 30) {
		h = 20
	}
	a.style.width = w;
	a.style.height = h;
	var f = document.all(a.id + '_xtb');
	if(f) {
		var g = f.inset;
		var i = g.split(",");
		i[1] = h / 4 + 'pt';
		var j = a.id;
		j = j = parseInt(j.substring(4)) + 10000;
		var k = document.getElementById("Node" + j);
		if(a.getAttribute("NodeType") == 'YNode') {
			k.style.width = w
		}
		if(a.getAttribute("NodeType") == 'XNode') {
			k.style.height = h;
			var l = k.innerText.length;
			var m = l * 15;
			var n = (h - m) / 2;
			if(n < 0) {
				n = 0
			}
			f.inset = "3px," + n + "px,3px,3px"
		}
	}
}

function delalonenode() {
	goalTypeName = "Move";
	document.body.style.cursor = "wait";
	FixError();
	var a = "";
	var b = "";
	for(i = 0; i < document.all.length; i++) {
		var c = document.all(i);
		a = c.tagName;
		if(a == "roundrect" || a == "shape" || a == "Oval" || a == "polyline") {
			b += c.id + ","
		}
	}
	xmlframe.document.all.HadNodeList.value = b;
	top.Ext.getBody().mask('Waiting', 'x-mask-loading');
	xmlframe.document.all.delnode.click()
}

function saveDefaultNode(d, e) {
	var f = xmlframe.document.all.unid.value;
	var g = d.id;
	var h = "";
	var i = "";
	if(e == "Router") {
		var j = d.getAttribute("LinkeyNode");
		var k = j.split("_");
		var l = eval("document.all.Node" + k[0]);
		var m = eval("document.all.Node" + k[1]);
		h = l.getAttribute("NodeType");
		i = m.getAttribute("NodeType");
	}
	top.Ext.getBody().mask('Waiting', 'x-mask-loading');
	//20171030  用Ajax替换Ext.Ajax.request，解决decode编码多次的问题	
	$.ajax({
		url: 'agentSaveDefaultNode?openagent',
		data: {
			ProcessUNID: f,
			Nodeid: g,
			NodeType: e,
			StartNodeType: h,
			EndNodeType: i
		},
		cache: false,
		async: false,
		type: "POST",
		dataType: "json",
		success: function(data) {
			d.SaveFlag = "yes";
			top.Ext.getBody().unmask()
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			alert("URL Error!");
		}
	})
	/*Ext.Ajax.request({
		url: 'agentSaveDefaultNode?openagent',
		success: function(a, b) {
			var c = Ext.util.JSON.decode(a.responseText);
			d.SaveFlag = "yes";
			top.Ext.getBody().unmask()
		},
		failure: function() {
			alert('Url Error!')
		},
		params: {
			ProcessUNID: f,
			Nodeid: g,
			NodeType: e,
			StartNodeType: h,
			EndNodeType: i
		}
	})*/
}

//20170926  修改div1.all.length为svgobj.childNodes.length
function saveAllDefaultNode() {
	var svgobj = document.getElementById("svg");
	for(i = 0; i < svgobj.childNodes.length; i++) {
		var a = svgobj.childNodes[i];
		if(!a.id || a.id == "StartNodePoint" || a.id == "EndNodePoint" || a.id == "PolyLine1" || a.id == "PolyLineX" || a.id == "PolyLineY" || a.id == "rect1") continue;
		var b = a.getAttribute("NodeType");
		//		console.log("==========");
		//		console.log(a.tagName + "  b=>" + b);
		if((b == "StartNode" || b == "EndNode" || b == "Event" || a.tagName == "polyline") && a.tagName != "text") {
			if(a.SaveFlag == "yes") {
				continue
			}
			//var c = a.Nodeid;
			var c = a.getAttribute("Nodeid");
			var d = a.getAttribute("LinkeyNode");
			var e = "";
			var f = "";
			if(d != null && a.tagName == "polyline") {
				var g = d.split("_");
				var h = eval("document.all.Node" + g[0]);
				var j = eval("document.all.Node" + g[1]);
				e = h.getAttribute("Nodeid");
				f = j.getAttribute("Nodeid");
				//console.log("e===>" + e + "   f====>" + f);
				b = "Router&StartNodeid=" + e + "&EndNodeid=" + f
			}
			var k = "&Action=SaveAllDefaultNode&Processid=" + top.processid + "&Nodeid=" + c + "&NodeType=" + b;
			var l = "rule?wf_num=R_S002_B011" + k;
			var m = Ext.lib.Ajax.getConnectionObject().conn;
			m.open("post", l, false);
			m.send();
			if(m.status == "200") {
				a.SaveFlag = "yes";
				ShowErrorInfo("缺省属性保存成功!")
			} else {
				ShowErrorInfo("缺省属性保存失败!")
			}
		}
	}
}

function FixError() {
	var a = 0;
	for(i = 0; i < document.all.length; i++) {
		var b = document.all(i);
		var c = b.tagName;
		if(c == "roundrect" || c == "shape" || c == "Oval") {
			var d = b.getAttribute(LinkeyStartObj);
			var e = b.getAttribute(LinkeyEndObj);
			if(d != "") {
				var f = d.split(",");
				var g = "";
				for(j = 0; j < f.length; j++) {
					try {
						var h = evalbroker(f[j]);
						if(g == "") {
							g = h.id
						} else {
							g += "," + h.id
						}
					} catch(E) {
						a++
					}
				}
				b.setAttribute("LinkeyStartObj", g);
				//b.LinkeyStartObj = g
			}
			if(e != "") {
				var f = e.split(",");
				var k = "";
				for(j = 0; j < f.length; j++) {
					try {
						var h = evalbroker(f[j]);
						if(k == "") {
							k = h.id
						} else {
							k += "," + h.id
						}
					} catch(E) {
						a++
					}
				}
				b.setAttribute("LinkeyEndObj", k);
			}
		}
		if(c == "polyline") {
			try {
				var l = b.getAttribute("LinkeyNode").split("_");
				eval("Node" + l[0]);
				eval("Node" + l[1])
			} catch(E) {
				a++
			}
		}
	}
}

function DebugFlow(a) {}

function DebugFlowAll(a) {}

function ShowNextNode(a) {
	var b = a.getAttribute("LinkeyStartObj");
	if(b != "" && b != undefined) {
		var c = b.split(",");
		for(i = 0; i < c.length; i++) {
			var d = c[i].indexOf("_");
			var e = c[i].length;
			var f = c[i].substring(d + 1, e);
			var g = evalbroker("Node" + f);
			goalNextNode[goalNextNode.length] = "Node" + f + "$$" + g.getAttribute("fill") + "," + g.id;
			g.setAttribute("fill", "#f8feb0");
			//g.firstChild.setAttribute("fill", "#fad01e");
		}
	}
}

function ShowPrvNode(a) {
	var b = a.getAttribute("LinkeyEndObj");
	if(b != "" && b != undefined) {
		var c = b.split(",");
		for(i = 0; i < c.length; i++) {
			var d = c[i].indexOf("polyline");
			var e = c[i].indexOf("_");
			var f = c[i].substring(d + 8, e);
			var g = evalbroker("Node" + f);
			goalPrvNode[goalPrvNode.length] = "Node" + f + "$$" + g.getAttribute("fill") + "," + g.id;
			g.setAttribute("fill", "#f8feb0");
			//g.firstChild.setAttribute("fill", "#fad01e");
		}
	}
}

function FocusFlowNode(a) {
	try {
		if(a.tagName == "polyline") {
			return
		} else {
			goalPrvNode[goalPrvNode.length] = a.id + "$$" + a.getAttribute("fill") + "," + a.id;
			a.setAttribute("fill", "#f8feb0");
			//a.firstChild.setAttribute("fill", "#fad01e");
		}
		var b = a.style.posLeft - document.body.clientWidth / 2;
		var c = a.style.posTop - document.body.clientHeight / 2;
		window.scrollTo(b, c)
	} catch(e) {
		alert(e.message);
		ShowError("节点已被删除!")
	}
}

function CancelPrvNextNode() {
	var e = "";
	var f = "";
	var g = "";
	var h = "";
	for(i = 0; i < goalPrvNode.length; i++) {
		cancelNodeError(goalPrvNode[i])
	}
	for(i = 0; i < goalNextNode.length; i++) {
		cancelNodeError(goalNextNode[i])
	}

	function cancelNodeError(a) {
		f = a.split(",");
		for(j = 0; j < f.length; j++) {
			g = f[j].split("$$");
			var b = g[0];
			var c = evalbroker(b);
			var d = g[1];
			if(c.tagName == "polyline") {
				c.setAttribute("stroke", d);
			} else {
				if(j == 0) {
					c.setAttribute("fill", d);
				}
				/*else {
					c.firstChild.getAttribute("fill") = d
				}*/
			}
		}
	}

	goalPrvNode = new Array();
	goalNextNode = new Array()
}

//20170926 整合
function AddText() {
	insertText.style.left = getEventOX(event) + offsetX - 75;
	insertText.style.top = getEventOY(event) + offsetY - 25;
	insertText.style.display = "";
	insertTextValue.focus()
}

function AddText_old() {
	insertText.style.posLeft = getEventOX(event) + offsetX - 75;
	insertText.style.posTop = getEventOY(event) + offsetY - 25;
	insertText.style.display = "";
	insertTextValue.focus()
}

//20171030 文字编辑
function editobj(a) {
	if(a.getAttribute("NodeType") != "spantext") {
		goalTypeName = "Edit"
	} else {

		insertText.style.left = getEventOX(event) + offsetX - 75;
		insertText.style.top = getEventOY(event) + offsetY - 25;
		insertTextValue.value = a.textContent;
		insertText.style.display = "";
		a.parentNode.removeChild(a);
		//a.outerHTML = "";
		goalTypeName = "AddText";
		document.body.style.cursor = "Text"
	}
}

Ext.onReady(function() {
	//console.log(div1.innerHTML);

	bowerflag = myBrowser();
	//alert(bowerflag);
	if(bowerflag == "IENoSupport") {
		alert("IE版本太低，请使用IE9及以上的浏览器!");
		window.close();
	} else if(bowerflag == "FF") {
		alert("流程建模不支持Firefox浏览器，请使用Chrome或IE9以上浏览器！");
		window.location.href = 'about:blank ';
	} else if(bowerflag == "Safari") {
		alert("流程建模不支持Safari浏览器，请使用Chrome或IE9以上浏览器！");
		window.opener = null;
		window.open('', '_self', '');
		window.close();
	}
	if(bowerflag != "IE9" && bowerflag != "IE10") {
		leftbtn = 0;
		rightbtn = 2;
	} else {
		leftbtn = 1;
		rightbtn = 2;
	}
	svginit();
	Ext.get("div1").on('contextmenu', function(e) {
		var a = top.document.all.MulNodeList.value;
		var b = new Ext.menu.Menu();
		if(thisobj.getAttribute("NodeType") == "DocNode") {
			return
		}
		SVG_ZIndex(document.getElementById("svg").childNodes);
		// 过滤节点的文本标签
		thisobj = unionNode(thisobj);
		// alert("tagName==> "+thisobj.tagName);
		// 20170917将thisobj.id != "div1"修改为thisobj.id != "svg"
		// lert(thisobj.id);
		if(thisobj != null && goalTypeName != "MulSel" && thisobj.getAttribute("NodeType") != "XNode" && thisobj.getAttribute("NodeType") != "YNode" && thisobj.id != "svg" && a == "") {

			/*
			 * alert("ID: "+event.srcElement.id+"\ntagName:
			 * "+event.srcElement.tagName+"\nthisobj.getAttribute("NodeType")=>:"+thisobj.getAttribute("NodeType"));
			 * alert("thisobj.NodeNum=>:"+thisobj.getAttribute("NodeNum"));
			 */

			if((thisobj.tagName == "rect" || thisobj.tagName == "path" || thisobj.tagName == "circle" || thisobj.tagName == "polygon") && thisobj.getAttribute("NodeType") != "Area") {

				b.add(new Ext.menu.Item({
					text: '节点属性',
					handler: function() {
						SetProperty(thisobj, thisobj.getAttribute("NodeType"))
					}
				}));
				b.add(new Ext.menu.Item({
					text: '显示前导节点',
					handler: function() {
						ShowPrvNode(thisobj)
					}
				}));
				b.add(new Ext.menu.Item({
					text: '显示后导节点',
					handler: function() {
						ShowNextNode(thisobj)
					}
				}));
				if(thisobj.getAttribute("NodeType") == "Activity" || thisobj.getAttribute("NodeType") == "SubProcess") {}
				if(goalcopyobj && goalcopyobj.tagName == thisobj.tagName && goalcopyobj != thisobj) {
					b.add(new Ext.menu.Item({
						text: '粘贴属性',
						handler: function() {
							pasteobj(thisobj)
						}
					}))
				} else {
					b.add(new Ext.menu.Item({
						text: '粘贴属性',
						disabled: true
					}))
				}
				b.add(new Ext.menu.Item({
					text: '删除',
					handler: function() {
						deleteobj(thisobj)
					}
				}));
				if(thisobj.tagName == "circle") {}
			} else if(thisobj.getAttribute("NodeType") == "Area" || thisobj.getAttribute("NodeType") == "XNode_text" || thisobj.getAttribute("NodeType") == "YNode_text") {
				//alert("else if");

				b.add(new Ext.menu.Item({
					text: '删除',
					handler: function() {
						deleteobj(thisobj)
					}
				}))
			} else if(thisobj.tagName == "polyline") {
				b.add(new Ext.menu.Item({
					text: '路由属性',
					handler: function() {
						SetProperty(thisobj, 'Router')
					}
				}));
				b.add(new Ext.menu.Item({
					text: '复制属性',
					handler: function() {
						copyobj(thisobj)
					}
				}));
				if(goalcopyobj && goalcopyobj.tagName == thisobj.tagName && goalcopyobj != thisobj) {
					b.add(new Ext.menu.Item({
						text: '粘贴属性',
						handler: function() {
							pasteobj(thisobj)
						}
					}))
				} else {
					b.add(new Ext.menu.Item({
						text: '粘贴属性',
						disabled: true
					}))
				}
				var c = new Ext.menu.Item({
					text: '变化线型',
					menu: {
						items: []
					}
				});
				var d = new Ext.menu.Item({
					text: "直线",
					icon: 'line.gif',
					handler: function() {
						ChangeLine(thisobj, 'Line')
					}
				});
				c.menu.add(d);
				var d = new Ext.menu.Item({
					text: "左右折线",
					icon: 'lineleftcur.gif',
					handler: function() {
						ChangeLine(thisobj, 'Bian')
					}
				});
				c.menu.add(d);
				var d = new Ext.menu.Item({
					text: "上下折线",
					icon: 'linerightcur.gif',
					handler: function() {
						ChangeLine(thisobj, 'Bottom')
					}
				});
				c.menu.add(d);
				var d = new Ext.menu.Item({
					text: "上下直角线",
					icon: 'line90_1.gif',
					handler: function() {
						ChangeLine(thisobj, 'zhexian902')
					}
				});
				c.menu.add(d);
				var d = new Ext.menu.Item({
					text: "左右直角线",
					icon: 'line90_2.gif',
					handler: function() {
						ChangeLine(thisobj, 'zhexian90')
					}
				});
				c.menu.add(d);
				var d = new Ext.menu.Item({
					text: "曲线",
					icon: 'linecurv.gif',
					handler: function() {
						ChangeLine(thisobj, 'zhexian')
					}
				});
				c.menu.add(d);
				b.add(c);
				b.add(new Ext.menu.Item({
					text: '删除',
					handler: function() {
						deleteobj(thisobj)
					}
				}))
			} else if(thisobj.tagName == "text" && thisobj.getAttribute("NodeType") == "spantext") {
				b.add(new Ext.menu.Item({
					text: '修改',
					handler: function() {
						editobj(thisobj)
					}
				}));
				b.add(new Ext.menu.Item({
					text: '删除',
					handler: function() {
						deleteobj(thisobj)
					}
				}))
			} else if(thisobj.id == "flowtable") {
				b.add(new Ext.menu.Item({
					text: '过程属性',
					handler: function() {
						SetProperty(thisobj, 'Process')
					}
				}));
				b.add(new Ext.menu.Item({
					text: '错误检查',
					handler: function() {
						CheckFlowError()
					}
				}));
				b.add(new Ext.menu.Item({
					text: '单击多选',
					handler: function() {
						MulSel_Start(true)
					}
				}));
				b.add(new Ext.menu.Item({
					text: '全部选择',
					handler: function() {
						MulSel_Start("ALL")
					}
				}));
				b.add(new Ext.menu.Item({
					text: '保存流程',
					handler: function() {
						save()
					}
				}))
			} else if(goalTypeName != "Move") {
				b.add(new Ext.menu.Item({
					text: '删除',
					handler: function() {
						deleteobj(thisobj)
					}
				}))
			}
		} else {
			if((thisobj.id == "svg" || thisobj.tagName.toLowerCase() == "body" || thisobj.getAttribute("NodeType") == "XNode" || thisobj.getAttribute("NodeType") == "YNode") && a == "") {
				b.add(new Ext.menu.Item({
					text: '过程属性',
					handler: function() {
						SetProperty(thisobj, 'Process')
					}
				}));
				b.add(new Ext.menu.Item({
					text: '错误检查',
					handler: function() {
						CheckFlowError()
					}
				}));
				b.add(new Ext.menu.Item({
					text: '全部选择',
					handler: function() {
						MulSel_Start("ALL")
					}
				}));
				b.add(new Ext.menu.Item({
					text: '开始多选',
					handler: function() {
						mySetType("MulSel")
					}
				}));
				b.add(new Ext.menu.Item({
					text: '保存流程',
					handler: function() {
						save()
					}
				}))
			}
		}
		if(a != "") {
			b.add(new Ext.menu.Item({
				text: '垂直对齐',
				handler: function() {
					checkAttr();
					MulSel_GropToSameX();
					//ToMoveLine_old();
					ToMoveLine();
				}
			}));
			b.add(new Ext.menu.Item({
				text: '水平对齐',
				handler: function() {
					checkAttr();
					MulSel_GropToSameY();
					//ToMoveLine_old();
					ToMoveLine();
				}
			}));
			b.add(new Ext.menu.Item({
				text: '纵向自动间距',
				handler: function() {
					checkAttr();
					MulSel_AutoYJZ();
					//ToMoveLine_old();
					ToMoveLine();
				}
			}));
			b.add(new Ext.menu.Item({
				text: '横向自动间距',
				handler: function() {
					checkAttr();
					MulSel_AutoXJZ();
					//ToMoveLine_old();
					ToMoveLine();
				}
			}));
			b.add(new Ext.menu.Item({
				text: '反向选择',
				handler: function() {
					MulSel_Start("FXSEL")
				}
			}));
			b.add(new Ext.menu.Item({
				text: '开始移动',
				handler: function() {
					mySetType("Move")
				}
			}));
			b.add(new Ext.menu.Item({
				text: '取消选择',
				handler: function() {
					MulSel_Start()
				}
			}))
		}
		b.showAt(e.getXY())
	});
	try {
		setTimeout(function() {
			parent.Ext.get('loading').remove();
			parent.Ext.get('loading-mask').fadeOut({
				remove: true
			})
		}, 150 + _x80)
	} catch(e) {}
	InitOldXY();
	top.initHisData();
	getNodeInfo();
});

//20170925整合
function getObjXY(a, b, c, d) { //a 点击的前节点，b当前节点的x c 为y
	var e = parseInt(converterSVG(a, "top")) + parseInt(converterSVG(a, "height")); //offsetHeight height+border
	var f = parseInt(converterSVG(a, "left")) + parseInt(converterSVG(a, "width"));
	var x = parseInt(converterSVG(a, "left"));
	var y = parseInt(converterSVG(a, "top"));
	var g = x + parseInt(converterSVG(a, "width") / 2);
	var h = y + parseInt(converterSVG(a, "height") / 2);
	var i, temphoty;
	var j = "";
	var k = parseInt(converterSVG(a, "width") / 4) + 6;
	var l = parseInt(converterSVG(a, "height") / 4) + 6;
	var m = a.getAttribute("NodeType");
	//offsetX布局移动的x
	//判断点击节点的位置
	//console.log("g-k:" + (g - k) + " b:" + b + " (g + k):" + (g + k) + " (y - l):" + (y - l) + " c:" + c + " y + l:" + (y + l));
	if((g - k) < b && b < (g + k) && (y - l) < c && c < (y + l)) {
		i = b;
		//i = g;
		temphoty = y;
		j = "Top"
	} else if((f - k) < b && b < (f + k) && ((h - l) < c && c < (h + l))) {
		i = f;
		temphoty = c;
		//temphoty = h;
		j = "Right"
	} else if((g - k) < b && b < (g + k) && (e + l) > c && c > (e - l)) {
		i = b;
		//i = g;
		temphoty = e;
		j = "Bottom"
	} else if((x - k) < b && b < (x + k) && ((h - l) < c && c < (h + l))) {
		i = x;
		temphoty = c;
		//temphoty = h;
		j = "Left"
	} else {
		i = g;
		temphoty = e;
		j = "Bottom"
	}

	if(!d) {
		hotx = i;
		hoty = temphoty;
		hotType = j;
		switch(j) {
			case "Top":
				if(m == "Edge") {
					hotx += 1
				} else {
					hotx += 2
				}
				break;
			case "Right":
				if(m == "Edge") {
					hoty += 1;
				} else {
					hotx += 1;
					hoty += 1
				}
				break;
			case "Bottom":
				if(m == "Edge") {
					hotx += 1
				} else {
					hotx += 2;
					hoty += 1
				}
				break;
			case "Left":
				hoty += 1;
				break
		}
	} else {
		hotx2 = i;
		hoty2 = temphoty;
		hotEndType = j;
		switch(j) {
			case "Top":
				if(m == "Edge") {
					hoty2 += 1;
					hotx2 -= 3
				}
				break;
			case "Right":
				if(m == "Edge") {
					hoty2 -= 1;
					hotx2 -= 2
				} else {
					hotx2 -= 1;
					hoty2 -= 1
				}
				break;
			case "Bottom":
				if(m == "Edge") {
					hoty2 -= 2;
					hotx2 -= 0
				} else {
					hoty2 -= 1
				}
				break;
			case "Left":
				if(m == "Edge") {
					hoty2 -= 3;
					hotx2 += 2
				} else {}
				break
		}
	}
	if(!d) {
		switch(j) {
			case "Bottom":
				showStartHotPoint(hotx - 5, hoty - 5);
				break;
			case "Left":
				showStartHotPoint(hotx - 5, hoty - 5);
				break;
			case "Right":
				showStartHotPoint(hotx - 5, hoty - 5);
				break;
			case "Top":
				showStartHotPoint(hotx - 5, hoty - 5);
				break
		}
	}
}

//20170927 修改更新获取方式，兼容IE
function showStartHotPoint(x, y) {
	document.all.StartNodePoint.style.display = "";
	var startpoint = document.getElementById("StartNodePoint");
	SvgMove(startpoint, x, y);
}

function showStartHotPoint_old(x, y) {
	document.all.StartNodePoint.style.display = "";
	document.all.StartNodePoint.style.left = x;
	document.all.StartNodePoint.style.top = y
}

function hideStartHotPoint() {
	document.all.StartNodePoint.style.display = "none";
	document.all.EndNodePoint.style.display = "none"
}

//20170926整合
function ShowEndHotPoint() {
	var a = getEventOX(event);
	var b = getEventOY(event);
	var c = parseInt(document.body.scrollLeft);
	var d = parseInt(document.body.scrollTop);
	//	a =a-0+ c;
	//	b =b-0+ d;
	document.all.EndNodePoint.style.display = "none";
	for(var i = 0; i < document.all.length; i++) {
		var e = document.all(i);
		var f = e.tagName.toLowerCase();
		if(goalStartObj == null) continue; //add 20170929
		if((f == "rect" || f == "path" || f == "circle" || f == "polygon") && goalStartObj.id != e.id && !isXYNode(e)) {
			var g = parseInt(converterSVG(e, "left"));
			var h = g + parseInt(converterSVG(e, "width"));
			var j = parseInt(converterSVG(e, "top"));
			var k = j + parseInt(converterSVG(e, "height"));
			if(g < a && a < h && j < b && b < k) {
				var l = getEndHotPointType(e);
				switch(l) {
					case "Bottom":
						//var m = g + parseInt(converterSVG(e, "width") / 2);
						var m = a;
						var n = k;
						showEndHotPoint(m - 2, n - 2);
						break;
					case "Left":
						var m = g;
						//var n = j + parseInt(converterSVG(e, "height") / 2);
						var n = b;
						showEndHotPoint(m - 5, n - 5);
						break;
					case "Right":
						var m = h;
						//var n = j + parseInt(converterSVG(e, "height") / 2);
						var n = b;
						showEndHotPoint(m - 2, n - 5);
						break;
					case "Top":
						//var m = g + parseInt(converterSVG(e, "width") / 2);
						var m = a;
						var n = j;
						showEndHotPoint(m - 2, n - 5);
						break
				}
			}
		}
	}
}

function ShowEndHotPoint_old() {
	var a = getEventOX(event);
	var b = getEventOY(event);
	var c = parseInt(document.body.scrollLeft);
	var d = parseInt(document.body.scrollTop);
	a += c;
	b += d;
	document.all.EndNodePoint.style.display = "none";
	for(var i = 0; i < document.all.length; i++) {
		var e = document.all(i);
		var f = e.tagName.toLowerCase();
		if((f == "roundrect" || f == "shape" || f == "oval") && goalStartObj.id != e.id) {
			var g = parseInt(e.style.left);
			var h = g + parseInt(e.offsetWidth);
			var j = parseInt(e.style.top);
			var k = j + parseInt(e.offsetHeight);
			if(g < a && a < h && j < b && b < k) {
				var l = getEndHotPointType(e);
				switch(l) {
					case "Bottom":
						var m = g + parseInt(e.offsetWidth / 2);
						var n = k;
						showEndHotPoint(m - 5, n - 10);
						break;
					case "Left":
						var m = g;
						var n = j + parseInt(e.offsetHeight / 2);
						showEndHotPoint(m - 5, n - 5);
						break;
					case "Right":
						var m = h;
						var n = j + parseInt(e.offsetHeight / 2);
						showEndHotPoint(m - 10, n - 5);
						break;
					case "Top":
						var m = g + parseInt(e.offsetWidth / 2);
						var n = j;
						showEndHotPoint(m - 5, n - 5);
						break
				}
			}
		}
	}
}

//20170926整合
//20170928 修改，兼容IE
function showEndHotPoint(x, y) {
	document.all.EndNodePoint.style.display = "";
	var endpoint = document.getElementById("EndNodePoint");
	SvgMove(endpoint, x, y);
}

function showEndHotPoint_old(x, y) {
	document.all.EndNodePoint.style.display = "";
	document.all.EndNodePoint.style.left = x;
	document.all.EndNodePoint.style.top = y
}

//20170928 修改
function getEndHotPointType(a) {
	var b = (parseInt(converterSVG(a, "top")) + converterSVG(a, "height"));
	var c = (parseInt(converterSVG(a, "left")) + converterSVG(a, "width"));
	var x = parseInt(converterSVG(a, "left"));
	var y = parseInt(converterSVG(a, "top"));
	var d = x + parseInt(converterSVG(a, "width") / 2);
	var e = y + parseInt(converterSVG(a, "height") / 2);
	var f = "";
	var g = parseInt(converterSVG(a, "width") / 4) + 6;
	var h = parseInt(converterSVG(a, "height") / 4) + 6;
	var i = getEventOX(event);
	var j = getEventOY(event);
	//	i += offsetX;
	//	j += offsetY;
	if((d - g) < i && i < (d + g) && (y - h) < j && j < (y + h)) {
		f = "Top"
	} else if((c - g) < i && i < (c + g) && ((e - h) < j && j < (e + h))) {
		f = "Right"
	} else if((d - g) < i && i < (d + g) && (b + h) > j && j > (b - h)) {
		f = "Bottom"
	} else if((x - g) < i && i < (x + g) && ((e - h) < j && j < (e + h))) {
		f = "Left"
	} else {
		f = "Bottom"
	}
	return f
}

function getEndHotPointType_old(a) {
	var b = (parseInt(a.style.top) + a.offsetHeight);
	var c = (parseInt(a.style.left) + a.offsetWidth);
	var x = parseInt(a.style.left);
	var y = parseInt(a.style.top);
	var d = x + parseInt(a.offsetWidth / 2);
	var e = y + parseInt(a.offsetHeight / 2);
	var f = "";
	var g = parseInt(a.offsetWidth / 4) + 5;
	var h = parseInt(a.offsetHeight / 4) + 5;
	var i = getEventOX(event);
	var j = getEventOY(event);
	i += offsetX;
	j += offsetY;
	if((d - g) < i && i < (d + g) && (y - h) < j && j < (y + h)) {
		f = "Top"
	} else if((c - g) < i && i < (c + g) && ((e - h) < j && j < (e + h))) {
		f = "Right"
	} else if((d - g) < i && i < (d + g) && (b + h) > j && j > (b - h)) {
		f = "Bottom"
	} else if((x - g) < i && i < (x + g) && ((e - h) < j && j < (e + h))) {
		f = "Left"
	} else {
		f = "Bottom"
	}
	return f
}

//20171013
//20170926 整合
function CreateObj(a, b, c) {
	switch(a) {
		case "PolyLine":
			PolyLine1.style.display = "none";
			if(Math.abs(b - hotx) <= 5 && Math.abs(c - hoty) <= 5)
				return false;
			if(goalEndObj != null && goalEndObj.tagName != "polyline" && goalEndObj.getAttribute("NodeNum") != goalStartObj.getAttribute("NodeNum") && !isXYNode(goalEndObj)) {
				var d = GetNodeNum("PolyLine");
				d = "0000" + d;
				d = d.substring(d.length - 4);
				if(d.substring(0, 1) != "1") {
					d = "1" + d
				}
				var f = GetPointsStr("Creat");
				var g = goalStartObj.getAttribute("NodeNum") + "_" + goalEndObj.getAttribute("NodeNum");
				if(g.indexOf("null") != -1) {
					//console.log("aaaa");
					return;
				}
				try {
					var h = evalbroker("polyline" + g);
					ShowErrorInfo("节点之间的路由已存在(" + h.getAttribute("Nodeid") + ")!")
				} catch(e) {
					id = "R" + d;
					var svgroot = document.getElementById("svg");
					var defs = svg.create("defs").appendTo(svgroot).node;

					var polyline = svg.create("polyline").attrNS({
						id: "polyline" + g,
						fill: "none",
						"marker-end": "url(#markerEndArrow)",
						"stroke-width": "1.5",
						"stroke": "black",
						//						"left": hotx,
						//						"top": hoty,
						"points": f
					}).attr({
						"Nodeid": id,
						"SourceNode": goalStartObj.getAttribute("Nodeid"),
						"TargetNode": goalEndObj.getAttribute("Nodeid"),
						"LinkeyNode": g,
						"PolyLineType": StartPointType,
						"hotType": hotType,
						//"hotEndType":hotEndType, //20171101
						"oldpoints": f,
						ondblclick: "SetProperty(this, 'Router')"
					}).appendTo(svgroot).node;
					//					polyline.ondblclick = function() {
					//						SetProperty(this, 'Router');
					//					};
					var i = f.split(" ");
					//					var p1 = f.substring(0, i)
					//					i = f.lastIndexOf(" ")
					//					var p2 = f.substring(i, f.length)

					var str1 = i[0].split(",");
					var str2 = i[1].split(",");
					var str3 = i[i.length - 2].split(",");
					var str4 = i[i.length - 1].split(",");
					var svgtext = svg.create("text").attrNS({
						id: "polyline" + g + "_text",
						x: (parseInt(str2[0]) + parseInt(str3[0])) / 2,
						y: (parseInt(str2[1]) + parseInt(str3[1])) / 2,
						"font-size": "9pt",
						"text-anchor": "middle",
						fill: "black"
					}).attr({
						"Nodeid": id + "_text",
						ondblclick: "SetProperty(this, 'Router')"
					}).appendTo(svgroot).node;
					/*svgtext.ondblclick = function() {
						SetProperty(this, 'Activity')
					};*/
					svgtext.textContent = "";
					RegObj("polyline" + g);
				}
				goalEndObj = null
			}
			break
	}
}

function CreateObj_old(a, b, c) {
	switch(a) {
		case "PolyLine":
			PolyLine1.style.display = "none";
			if(Math.abs(b - hotx) <= 5 && Math.abs(c - hoty) <= 5) return false;
			if(goalEndObj != null && goalEndObj.tagName != "polyline" && goalEndObj.NodeNum != goalStartObj.NodeNum) {
				var d = GetNodeNum("PolyLine");
				d = "0000" + d;
				d = d.substring(d.length - 4);
				if(d.substring(0, 1) != "1") {
					d = "1" + d
				}
				var f = GetPointsStr("Creat");
				var g = goalStartObj.NodeNum + "_" + goalEndObj.NodeNum;
				try {
					var h = evalbroker("polyline" + g);
					ShowErrorInfo("节点之间的路由已存在(" + h.Nodeid + ")!")
				} catch(e) {
					id = "R" + d;
					var i = "<v:polyline id='polyline" + g + "' Nodeid='" + id + "' SourceNode='" + goalStartObj.Nodeid + "' TargetNode='" + goalEndObj.Nodeid + "' LinkeyNode='" + g + "'  PolyLineType='" + StartPointType + "'  hotType='" + hotType + "' points='" + f + "' oldpoints='" + f + "' style='position:absolute;z-index:2;left:" + hotx + "px;top:" + hoty + "px' strokecolor='" + color1 + "' onDblClick=\"SetProperty (this,'Router')\" strokeweight='1.2px' filled='false'   />";
					RegObj("polyline" + g);
					var j = document.createElement(i);
					div1.appendChild(j);
					j.innerHTML = '<v:stroke  endarrow="block" endarrowwidth="narrow" endarrowlength="long"/>'
				}
				goalEndObj = null
			}
			break
	}
}

//20170926整合
function GetPointsStr(a, b, c, v, z) {
	var d = getEventOX(event);
	var e = getEventOY(event);
	var sepoint = document.getElementById("StartNodePoint");
	var r = sepoint.getAttribute("r");
	d += offsetX;
	e += offsetY;
	if(event.srcElement.tagName != "BODY") {
		if((event.srcElement.parentNode.id == "svg" || event.srcElement.parentNode.parentNode.id == "svg") && event.button == leftbtn && !isAssist(event.srcElement)) {
			if(event.srcElement.parentNode.id == "svg") {
				goalEndObj = event.srcElement
			} else {
				goalEndObj = event.srcElement.parentNode
			}
		}
	}
	if(a == "Edit") {
		var f = getEventOX(event) - moveobjoldleft;
		var g = getEventOY(event) - moveobjoldtop
	} else if(a == "Creat") {
		getObjXY(goalEndObj, getEventOX(event), getEventOY(event), "Creat");
		var f = hotx2 - hotx;
		var g = hoty2 - hoty
		d = (hotx2 - 0) + (r - 0) + offsetX;
		e = (hoty2 - 0) + (r - 0) + offsetY;
	} else if(a == "NodeMove") { //修改2017.09.27
		//		var f = v - b;
		//		var g = z - c;
		//		d = (v - 0) + (r - 0);
		//		e = (z - 0) + (r - 0);
		//		hotx = b - 0;
		//		hoty = c - 0;
		d = (hotx2 - 0) + (r - 0) + offsetX;
		e = (hoty2 - 0) + (r - 0) + offsetY;
	} else {
		var f = d - hotx;
		var g = e - hoty;
	}
	var h = f / 2;
	var i = g / 2;
	var j = hotx + "," + hoty + " " + hotx + "," + hoty;

	switch(StartPointType) {
		case "Bottom":
			j = hotx + "," + hoty + " " + hotx + "," + (hoty + i) + " " + (d - r) + "," + (hoty + i) + " " + (d - r) + "," + (e - r);
			break;
		case "Bian":
			j = hotx + "," + hoty + " " + (hotx + h) + "," + hoty + " " + (hotx + h) + "," + (e - r) + " " + (d - r) +
				"," + (e - r);
			break;
		case "Line":
			j = hotx + "," + hoty + " " + (d - r) + "," + (e - r);
			break;
		case "zhexian":
			var k = 30;
			var l = 30;
			if(hotType == "Bottom" || hotType == "Top") {
				if(hotType == "Bottom") {
					k = (g >= 0) ? (Math.abs(g) + k) : k
				}
				if(hotType == "Top") {
					k = (g < 0) ? -(Math.abs(g) + k) : -k
				}

				j = hotx + "," + hoty + " " + hotx + "," + (hoty + k) + " " + (d - r) + "," + (hoty + k) + " " + (d - r) + "," + e;
				//j = hotx + "," + hoty + " " + hotx + "," + (hoty + k) + " " + hotx2 + "," + (hoty + k) + " " + hotx2 + "," + hoty2;

			} else {
				if(hotType == "Right") {
					l = (f > 0) ? (Math.abs(f) + l) : l
				}
				if(hotType == "Left") {
					l = (f < 0) ? -(Math.abs(f) + l) : -l
				}
				j = hotx + "," + hoty + " " + (hotx + l) + "," + hoty + " " + (hotx + l) + "," + (e - r) + " " + (d - r) + "," + (e - r);
				//j = hotx + "," + hoty + " " + (hotx + l) + "," + hoty + " " + (hotx + l) + "," + hoty2 + " " + hotx2 + "," + hoty2;
			}
			break;
		case "zhexian90":
			j = hotx + "," + hoty + " " + (d - r) + "," + hoty + " " + (d - r) + "," + (e - r);
			break;
		case "zhexian902":
			j = hotx + "," + hoty + " " + hotx + "," + (e - r) + " " + (d - r) + "," + (e - r);
			break
	}
	return j
}

function GetPointsStr_old(a, b, c) {
	var d = getEventOX(event);
	var e = getEventOY(event);
	d += offsetX;
	e += offsetY;
	if(event.srcElement.tagName != "BODY") {
		if((event.srcElement.parentNode.id == "svg" || event.srcElement.parentNode.parentNode.id == "svg") && event.button == leftbtn) {
			if(event.srcElement.parentNode.id == "svg") {
				goalEndObj = event.srcElement
			} else {
				goalEndObj = event.srcElement.parentNode
			}
		}
	}
	if(a == "Edit") {
		var f = getEventOX(event) - moveobjoldleft;
		var g = getEventOY(event) - moveobjoldtop
	} else if(a == "Creat") {
		getObjXY(goalEndObj, getEventOX(event), getEventOY(event), "Creat");
		var f = hotx2 - hotx;
		var g = hoty2 - hoty
	} else if(a == "NodeMove") {
		var f = b;
		var g = c
	} else {
		var f = d - hotx;
		var g = e - hoty
	}
	var h = f / 2;
	var i = g / 2;
	var j = "0,0";
	switch(StartPointType) {
		case "Bottom":
			j = "0,0" + " " + "0," + i + "px " + f + "px," + i + "px " + f + "px," + g + "px";
			break;
		case "Bian":
			j = "0,0" + " " + h + "px,0" + " " + h + "px," + g + "px " + f + "px," + g + "px";
			break;
		case "Line":
			j = "0,0" + " " + f + "px," + g + "px";
			break;
		case "zhexian":
			var k = 30;
			var l = 30;
			if(hotType == "Bottom" || hotType == "Top") {
				if(hotType == "Bottom") {
					k = (g >= 0) ? (Math.abs(g) + k) : k
				}
				if(hotType == "Top") {
					k = (g < 0) ? -(Math.abs(g) + k) : -k
				}
				j = "0,0" + " 0," + k + " " + f + "," + k + " " + f + "px," + g + "px"
			} else {
				if(hotType == "Right") {
					l = (f > 0) ? (Math.abs(f) + l) : l
				}
				if(hotType == "Left") {
					l = (f < 0) ? -(Math.abs(f) + l) : -l
				}
				j = "0,0" + " " + l + ",0 " + l + "," + g + " " + f + "px," + g + "px"
			}
			break;
		case "zhexian90":
			j = "0,0" + " " + f + "px,0 " + f + "px," + g + "px";
			break;
		case "zhexian902":
			j = "0,0 0," + g + " " + f + "px," + g + "px";
			break
	}
	return j
}

function MouseOverOut(a, b) {
	if(b == 1) {
		a.strokecolor = "#blue"
	} else {
		a.strokecolor = "black"
	}
}

//20170930 修改
function RegObj(a) {
	var b = "";
	if(goalStartObj != null) {
		var c = goalStartObj.getAttribute("LinkeyStartObj");
		if(c == "") {
			goalStartObj.setAttribute("LinkeyStartObj", a);
		} else {
			goalStartObj.setAttribute("LinkeyStartObj", c + "," + a);
		}
		//console.log(goalStartObj.getAttribute("Nodeid") + ":" + goalStartObj.getAttribute("LinkeyStartObj"));
	}
	if(goalEndObj != null) {
		var c = goalEndObj.getAttribute("LinkeyEndObj");
		if(c == "") {
			goalEndObj.setAttribute("LinkeyEndObj", a);
		} else {
			goalEndObj.setAttribute("LinkeyEndObj", c + "," + a);
		}
		//console.log(goalStartObj.getAttribute("Nodeid") + ":" + goalStartObj.getAttribute("LinkeyEndObj"));
	}

}

function MoveNodeObj(a, b, c, d, f) {
	var i = 0;
	try {
		if(event.button == rightbtn) return false
	} catch(e) {}
	if(d == undefined) {
		d = b - mstartX
	}
	if(f == undefined) {
		f = c - mstartY
	}
	if(d == 0 && f == 0) {
		return false
	}

	/*var g = "";
	var h = "";
	if(a.tagName == "text" && unionNode(null, a)) {
		g = unionNode(null, a).getAttribute("LinkeyStartObj");
		h = unionNode(null, a).getAttribute("LinkeyEndObj");
	} else {
		g = a.getAttribute("LinkeyStartObj");
		h = a.getAttribute("LinkeyEndObj");
	}
	//console.log("MoveNodeObj=>LinkeyStartObj: " + g + "  LinkeyEndObj: " + h);
	if(g != "" && g != undefined) {
		var j = g.split(",");
		for(i = 0; i < j.length; i++) {
			try {
				var k = evalbroker(j[i]);

				//20171009 add
				var linepoint = k.getAttribute("points").split(" ");
				var lineend = linepoint[linepoint.length - 1];
				var linestart = linepoint[0];
				var lineendx = lineend.split(",")[0] - 0;
				var lineendy = lineend.split(",")[1] - 0;
				var ss = oldArryStartLeft[i] + d;
				var ee = oldArryStartTop[i] + f;
				var startp = ss + "," + ee;
				var l = lineendx;
				var m = lineendy;
				var n = l;
				var o = m;
				var p = 30;
				var q = 30;
				var r = k.getAttribute("PolyLineType");
				var s = k.getAttribute("hotType");
				var t = "";
				switch(r) {
					case "Bottom":
						var u = parseFloat(ee) + (m - ee) / 2;
						var linestart2x = linepoint[1].split(",")[0];
						if(k.getAttribute("TopY") != undefined && k.getAttribute("TopY") != "") u = parseInt(k.getAttribute("TopY"));
						t = startp + " " + ss + "," + u + " " + l + "," + u + " " + l + "," + m;
						break;
					case "Bian":
						var u = parseFloat(ss) + (l - ss) / 2;
						if(k.getAttribute("TopY") != undefined && k.getAttribute("TopY") != "") u = parseInt(k.getAttribute("TopY"));
						t = startp + " " + u + "," + ee + " " + u + "," + m + " " + l + "," + m;
						break;
					case "Line":
						t = startp + " " + l + "," + m;
						break;
					case "zhexian":
						switch(s) {
							case "Bottom":
								p = (m <= ee) ? (ee + p) : (m + p);
								if(k.getAttribute("TopY") != undefined && k.getAttribute("TopY") != "") p = parseInt(k.getAttribute("TopY"));
								t = startp + " " + ss + "," + p + " " + n + "," + p + " " + n + "," + o;
								break;
							case "Top":
								p = (m <= ee) ? (m - p) : (ee - p);
								if(k.getAttribute("TopY") != undefined && k.getAttribute("TopY") != "") p = parseInt(k.getAttribute("TopY"));
								t = startp + " " + ss + "," + p + " " + n + "," + p + " " + n + "," + o;
								break;
							case "Right":
								q = (l <= ss) ? (ss + q) : (l + q);
								if(k.getAttribute("TopY") != undefined && k.getAttribute("TopY") != "") q = parseInt(k.getAttribute("TopY"));
								t = startp + " " + q + "," + ee + " " + q + "," + o + " " + n + "," + o;
								break;
							case "Left":
								q = (l <= ss) ? (l - q) : (ss - q);
								if(k.getAttribute("TopY") != undefined && k.getAttribute("TopY") != "") q = parseInt(k.getAttribute("TopY"));
								t = startp + " " + q + "," + ee + " " + q + "," + o + " " + n + "," + o;
								break
						}
						break;
					case "zhexian90":
						t = startp + " " + l + "," + ee + " " + l + "," + m;
						break;
					case "zhexian902":
						t = startp + " " + ss + "," + m + " " + l + "," + m;
						break
				}
				k.setAttribute("oldpoints", t);
				k.setAttribute("orpoints", t);
				k.setAttribute("points", t);
				AddRouterText(k, "MoveObj")
			} catch(E) {
				ShowErrorInfo("本活动出错,请执行错误修复操作!")
			}
		}
	}
	if(h != "" && h != undefined) {
		var j = h.split(",");
		for(i = 0; i < j.length; i++) {
			try {
				var k = evalbroker(j[i]);
				var l = oldArryEndPosx2[i] + d;
				var m = oldArryEndPosy2[i] + f;
				var n = l;
				var o = m;
				var p = 30;
				var q = 30;
				var r = k.getAttribute("PolyLineType");
				var s = k.getAttribute("hotType");
				var t = "";
				//20171009 add
				var linepoint = k.getAttribute("points").split(" ");
				var linestart = linepoint[0];
				var linestartx = linestart.split(",")[0] - 0;
				var linestarty = linestart.split(",")[1] - 0;
				var ss = linestartx;
				var ee = linestarty;
				//console.log("start:" + linestart + "  end:" + lineend);
				switch(r) {
					case "Bottom":
						var u = parseFloat(linestarty) + (m - linestarty) / 2;
						var linestart2x = linepoint[1].split(",")[0];
						if(k.getAttribute("TopY") != undefined && k.getAttribute("TopY") != "") u = parseInt(k.getAttribute("TopY"));
						t = linestart + " " + linestart2x + "," + u + " " + l + "," + u + " " + l + "," + m;
						break;
					case "Bian":
						var u = parseFloat(linestartx) + (l - linestartx) / 2;
						var linestart2y = linepoint[1].split(",")[1];
						if(k.getAttribute("TopY") != undefined && k.getAttribute("TopY") != "") u = parseInt(k.getAttribute("TopY"));
						t = linestart + " " + u + "," + linestart2y + " " + u + "," + m + " " + l + "," + m;
						break;
					case "Line":
						t = linestart + " " + l + "," + m;
						break;
					case "zhexian":
						var linestart2x = linepoint[1].split(",")[0];
						var linestart2y = linepoint[1].split(",")[1];
						switch(s) {
							case "Bottom":
								p = (m <= ee) ? (ee + p) : (m + p);
								if(k.getAttribute("TopY") != undefined && k.getAttribute("TopY") != "") p = parseInt(k.getAttribute("TopY"));
								t = linestart + " " + linestart2x + "," + p + " " + n + "," + p + " " + n + "," + o;
								break;
							case "Top":
								p = (m <= ee) ? (m - p) : (ee - p);
								if(k.getAttribute("TopY") != undefined && k.getAttribute("TopY") != "") p = parseInt(k.getAttribute("TopY"));
								t = linestart + " " + linestart2x + "," + p + " " + n + "," + p + " " + n + "," + o;
								break;
							case "Right":
								q = (l <= ss) ? (ss + q) : (l + q);
								if(k.getAttribute("TopY") != undefined && k.getAttribute("TopY") != "") q = parseInt(k.getAttribute("TopY"));
								t = linestart + " " + q + "," + linestart2y + " " + q + "," + o + " " + n + "," + o;
								break;
							case "Left":
								q = (l <= ss) ? (l - q) : (ss - q);
								if(k.getAttribute("TopY") != undefined && k.getAttribute("TopY") != "") q = parseInt(k.getAttribute("TopY"));
								t = linestart + " " + q + "," + linestart2y + " " + q + "," + o + " " + n + "," + o;
								break
						}
						break;
					case "zhexian90":
						var linestart2y = linepoint[1].split(",")[1];
						t = linestart + " " + l + "," + linestart2y + " " + l + "," + m;
						break;
					case "zhexian902":
						var linestart2x = linepoint[1].split(",")[0];
						t = linestart + " " + linestart2x + "," + m + " " + l + "," + m;
						break
				}
				k.setAttribute("oldpoints", t);
				k.setAttribute("orpoints", t);
				k.setAttribute("points", t);
				AddRouterText(k, "MoveObj")
			} catch(E) {
				ShowErrorInfo("本活动出错,请执行错误修复操作!")
			}
		}
	}*/

	if(a.getAttribute("NodeType") != "XNode" && a.getAttribute("NodeType") != "XNode_text" && a.getAttribute("NodeType") != "XNode_rect") {
		if(a.getAttribute("NodeType") != "YNode" && a.getAttribute("NodeType") != "YNode_text" && a.getAttribute("NodeType") != "YNode_rect") {
			SvgMove(a, (moveobjoldleft + d), (moveobjoldtop + f));
		} else {
			SvgMove(a, (moveobjoldleft + d));
		}
	} else {
		if(a.getAttribute("NodeType") != "YNode" && a.getAttribute("NodeType") != "YNode_text" && a.getAttribute("NodeType") != "YNode_rect") {
			SvgMove(a, null, (moveobjoldtop + f));
		}
	}

	var v = document.body.scrollLeft;
	var w = document.body.scrollTop;
	var x = document.body.clientWidth;
	var y = document.body.clientHeight;
	if(b > x) window.scrollTo(v + 3, w);
	if(b < 0) window.scrollTo(v - 3, w);
	if(c > y) window.scrollTo(v, w + 3);
	if(c < 0) window.scrollTo(v, w - 3)
}

function MoveNodeObj_old(a, b, c, d, f) {
	var i = 0;
	try {
		if(event.button == rightbtn) return false
	} catch(e) {}
	if(d == undefined) {
		d = b - xx
	}
	if(f == undefined) {
		f = c - yy
	}
	if(d == 0 && f == 0) {
		return false
	}

	var g = a.getAttribute("LinkeyStartObj");
	var h = a.getAttribute("LinkeyEndObj");
	if(g != "" && g != undefined) {
		var j = g.split(",");
		for(i = 0; i < j.length; i++) {
			try {
				var k = evalbroker(j[i]);
				// k.style.x = oldArryStartLeft[i] + d;
				// k.style.y = oldArryStartTop[i] + f;
				k.setAttribute("cx", oldArryStartLeft[i] + d);
				k.setAttribute("cy", oldArryStartTop[i] + f);
				var l = oldArryEndPosx[i] - d;
				var m = oldArryEndPosy[i] - f;
				var n = l;
				var o = m;
				var p = 30;
				var q = 30;
				var r = k.PolyLineType;
				var s = k.hotType;
				var t = "";
				switch(r) {
					case "Bottom":
						var u = m / 2;
						if(k.getAttribute("TopY") != undefined && k.TopY != "") u = parseInt(k.TopY);
						t = "0,0" + " " + "0," + u + "px " + l + "px," + u + "px " + l + "px," + m + "px";
						break;
					case "Bian":
						var u = l / 2;
						if(k.TopY != undefined && k.TopY != "") u = parseInt(k.TopY);
						t = "0,0" + " " + u + "px,0" + " " + u + "px," + m + "px " + l + "px," + m + "px";
						break;
					case "Line":
						t = "0,0" + " " + l + "px," + m + "px";
						break;
					case "zhexian":
						switch(s) {
							case "Bottom":
								p = (m <= 0) ? (p) : (m + p);
								if(k.TopY != undefined && k.TopY != "") p = parseInt(k.TopY);
								t = "0,0" + " 0," + p + "px " + n + "px," + p + "px " + n + "px," + o + "px";
								break;
							case "Top":
								p = (m <= 0) ? (m - p) : (-p);
								if(k.TopY != undefined && k.TopY != "") p = parseInt(k.TopY);
								t = "0,0" + " 0," + p + "px " + n + "px," + p + "px " + n + "px," + o + "px";
								break;
							case "Right":
								q = (l <= 0) ? (q) : (l + q);
								if(k.TopY != undefined && k.TopY != "") q = parseInt(k.TopY);
								t = "0,0" + " " + q + "px,0 " + q + "px," + o + "px " + n + "px," + o + "px";
								break;
							case "Left":
								q = (l <= 0) ? (l - q) : (-q);
								if(k.TopY != undefined && k.TopY != "") q = parseInt(k.TopY);
								t = "0,0" + " " + q + "px,0 " + q + "px," + o + "px " + n + "px," + o + "px";
								break
						}
						break;
					case "zhexian90":
						t = "0,0" + " " + l + "px,0 " + l + "px," + m + "px";
						break;
					case "zhexian902":
						t = "0,0 0," + m + " " + l + "px," + m + "px";
						break
				}
				k.oldpoints = t;
				k.orpoints = t;
				k.points.value = t;
				AddRouterText(k, "MoveObj")
			} catch(E) {
				ShowErrorInfo("本活动出错,请执行错误修复操作!")
			}
		}
	}
	if(h != "" && h != undefined) {
		var j = h.split(",");
		for(i = 0; i < j.length; i++) {
			try {
				var k = evalbroker(j[i]);
				var l = oldArryEndPosx2[i] + d;
				var m = oldArryEndPosy2[i] + f;
				var n = l;
				var o = m;
				var p = 30;
				var q = 30;
				var r = k.PolyLineType;
				var s = k.hotType;
				var t = "";
				switch(r) {
					case "Bottom":
						var u = m / 2;
						if(k.TopY != undefined && k.TopY != "") u = parseInt(k.TopY);
						t = "0,0" + " " + "0," + u + "px " + l + "px," + u + "px " + l + "px," + m + "px";
						break;
					case "Bian":
						var u = l / 2;
						if(k.TopY != undefined && k.TopY != "") u = parseInt(k.TopY);
						t = "0,0" + " " + u + "px,0" + " " + u + "px," + m + "px " + l + "px," + m + "px";
						break;
					case "Line":
						t = "0,0" + " " + l + "px," + m + "px";
						break;
					case "zhexian":
						switch(s) {
							case "Bottom":
								p = (m <= 0) ? (p) : (m + p);
								if(k.TopY != undefined && k.TopY != "") p = parseInt(k.TopY);
								t = "0,0" + " 0," + p + "px " + n + "px," + p + "px " + n + "px," + o + "px";
								break;
							case "Top":
								p = (m <= 0) ? (m - p) : (-p);
								if(k.TopY != undefined && k.TopY != "") p = parseInt(k.TopY);
								t = "0,0" + " 0," + p + " " + n + "," + p + " " + n + "px," + o + "px";
								break;
							case "Right":
								q = (l <= 0) ? (q) : (l + q);
								if(k.TopY != undefined && k.TopY != "") q = parseInt(k.TopY);
								t = "0,0" + " " + q + "px,0 " + q + "px," + o + "px " + n + "px," + o + "px";
								break;
							case "Left":
								q = (l <= 0) ? (l - q) : (-q);
								if(k.TopY != undefined && k.TopY != "") q = parseInt(k.TopY);
								t = "0,0" + " " + q + "px,0 " + q + "px," + o + "px " + n + "px," + o + "px";
								break
						}
						break;
					case "zhexian90":
						t = "0,0" + " " + l + "px,0 " + l + "px," + m + "px";
						break;
					case "zhexian902":
						t = "0,0 0," + m + " " + l + "px," + m + "px";
						break
				}
				k.oldpoints = t;
				k.orpoints = t;
				k.points.value = t;
				AddRouterText(k, "MoveObj")
			} catch(E) {
				ShowErrorInfo("本活动出错,请执行错误修复操作!")
			}
		}
	}

	if(a.getAttribute("NodeType") != "XNode" && a.getAttribute("NodeType") != "XNode_text") {
		if(a.getAttribute("NodeType") != "YNode" && a.getAttribute("NodeType") != "YNode_text") {
			SvgMove(a, moveobjoldleft, moveobjoldtop, d, f);
		} else {
			SvgMove(a, moveobjoldleft, null, d);
		}
	} else {
		if(a.getAttribute("NodeType") != "YNode" && a.getAttribute("NodeType") != "YNode_text") {
			SvgMove(a, null, moveobjoldtop, null, f);
		}
	}

	var v = document.body.scrollLeft;
	var w = document.body.scrollTop;
	var x = document.body.clientWidth;
	var y = document.body.clientHeight;
	if(b > x) window.scrollTo(v + 3, w);
	if(b < 0) window.scrollTo(v - 3, w);
	if(c > y) window.scrollTo(v, w + 3);
	if(c < 0) window.scrollTo(v, w - 3)
}

//20171103 重新编写线条移动的相对位置，使用自定义属性sratio、eratio
function ToMoveLine(a, x, y) {
	if(a != undefined && a != null) {
		var aobj = unionNode(a);
		g = aobj.getAttribute("LinkeyStartObj");
		h = aobj.getAttribute("LinkeyEndObj");
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
		if(gh != "") {
			var j = gh.split(",");
			for(i = 0; i < j.length; i++) {
				var b = evalbroker(j[i]);
				var oo = b.id.replace("polyline", "").split("_");
				var hotType = b.getAttribute("hotType");
				var r = b.getAttribute("PolyLineType");
				var sratio = b.getAttribute("sratio");
				var eratio = b.getAttribute("eratio");

				//console.log("sratio::" + sratio + "  eratio::" + eratio);

				var sobj = evalbroker("Node" + oo[0]);
				var eobj = evalbroker("Node" + oo[1]);

				//var hotEndType = getEndType(sobj, eobj, hotType, r);
				var startp = getLocationnew(sobj, eobj, sratio, r, hotType);
				var endp = getLocationnew(eobj, sobj, eratio, r, hotType);

				//console.log("startp::" + startp + "  endp::" + endp);
				toRedraw(b, r, hotType, startp, endp);
			}
		}

	} else {
		var svgobj = document.getElementById("svg");
		for(i = 0; i < svgobj.childNodes.length; i++) {
			var b = svgobj.childNodes[i];
			if(b.tagName == "polyline" && !isAssist(b)) {

				var oo = b.id.replace("polyline", "").split("_");
				var hotType = b.getAttribute("hotType");
				var r = b.getAttribute("PolyLineType");
				var sratio = b.getAttribute("sratio");
				var eratio = b.getAttribute("eratio");

				//console.log("sratio::" + sratio + "  eratio::" + eratio);

				var sobj = evalbroker("Node" + oo[0]);
				var eobj = evalbroker("Node" + oo[1]);

				var hotEndType = getEndType(sobj, eobj, hotType, r);
				var startp = getLocationnew(sobj, b, sratio);
				var endp = getLocationnew(eobj, b, eratio);

				//console.log("startp::" + startp + "  endp::" + endp);
				toRedraw(b, r, hotType, startp, endp);

			}
		}
	}
}

//20171107 修改sratio、eratio的值，增加大小变换所用到的比例
//20171103 为线段添加自定义属性sratio、eratio
function checkAttr() {
	offsetX = parseInt(document.body.scrollLeft);
	offsetY = parseInt(document.body.scrollTop);
	var svgobj = document.getElementById("svg");
	//console.log("=============");
	for(i = 0; i < svgobj.childNodes.length; i++) {
		var b = svgobj.childNodes[i];
		//if(typeof(b.tagName) == "undefined") continue;
		if(b.tagName == "polyline" && !isAssist(b)) {
			var j = b.id.replace("polyline", "").split("_");
			var p = b.getAttribute("points").replace(/( )/gi, ",").split(",");

			var sobj = evalbroker("Node" + j[0]);
			var eobj = evalbroker("Node" + j[1]);
			var sratiox = p[0] - 0 - converterSVG(sobj, "left") - offsetX;
			var sratioy = p[1] - 0 - converterSVG(sobj, "top") - offsetY;
			var eratiox = p[p.length - 2] - 0 - converterSVG(eobj, "left") - offsetX;
			var eratioy = p[p.length - 1] - 0 - converterSVG(eobj, "top") - offsetY;

			var sox = sratiox / converterSVG(sobj, "width");
			var soy = sratioy / converterSVG(sobj, "height");
			var eox = eratiox / converterSVG(eobj, "width");
			var eoy = eratioy / converterSVG(eobj, "height");

			b.setAttribute("sratio", sratiox + "," + sratioy + "," + sox + "," + soy);
			b.setAttribute("eratio", eratiox + "," + eratioy + "," + eox + "," + eoy);
			//console.log("s:" + b.getAttribute("sratio") + "  e:" + b.getAttribute("eratio"));
		}
	}

}

//20171101 采用相对定位的方式移动线条
function ToMoveLine_old(a, x, y) {
	if(a != undefined && a != null) {
		var aobj = unionNode(a);
		g = aobj.getAttribute("LinkeyStartObj");
		h = aobj.getAttribute("LinkeyEndObj");
		if(g != "" && g != undefined) {
			var j = g.split(",");
			for(i = 0; i < j.length; i++) {
				var b = evalbroker(j[i]);
				var oo = b.id.replace("polyline", "").split("_");
				var hotType = b.getAttribute("hotType");
				var r = b.getAttribute("PolyLineType");

				var sobj = evalbroker("Node" + oo[0]);
				var eobj = evalbroker("Node" + oo[1]);

				var sflag = "";
				var eflag = "";
				if(document.getElementById("polyline" + oo[1] + "_" + oo[0])) {
					var fb = document.getElementById("polyline" + oo[1] + "_" + oo[0]);
					var se = getstartOrend(b, fb);
					if(se) {
						sflag = "start";
						eflag = "end";
					} else {
						sflag = "end";
						eflag = "start";
					}

					var hotEndType = getEndType(sobj, eobj, hotType, r);
					var startp = getLocation(sobj, hotType, sflag);
					var endp = getLocation(eobj, hotEndType, sflag);

					toRedraw(b, r, hotType, startp, endp);

					//var hotEndType = getEndType(sobj, eobj, hotType, r);
					var startp = getLocation(eobj, hotEndType, eflag);
					var endp = getLocation(sobj, hotType, eflag);

					toRedraw(fb, r, hotEndType, startp, endp);
				} else {
					var hotEndType = getEndType(sobj, eobj, hotType, r);
					var startp = getLocation(sobj, hotType);
					var endp = getLocation(eobj, hotEndType);

					toRedraw(b, r, hotType, startp, endp);
				}

			}

		}
		if(h != "" && h != undefined) {
			var j = h.split(",");
			for(i = 0; i < j.length; i++) {
				var b = evalbroker(j[i]);
				var oo = b.id.replace("polyline", "").split("_");
				var hotType = b.getAttribute("hotType");
				var r = b.getAttribute("PolyLineType");

				var sobj = evalbroker("Node" + oo[0]);
				var eobj = evalbroker("Node" + oo[1]);

				var sflag = "";
				var eflag = "";
				if(document.getElementById("polyline" + oo[1] + "_" + oo[0])) {
					//					var fb = document.getElementById("polyline" + oo[1] + "_" + oo[0]);
					//					sflag = "start";
					//					eflag = "end";
					//					var hotEndType = getEndType(sobj, eobj, hotType, r);
					//					var startp = getLocation(sobj, hotType, sflag);
					//					var endp = getLocation(eobj, hotEndType, sflag);
					//
					//					toRedraw(b, r, hotType, startp, endp);
					//
					//					//var hotEndType = getEndType(sobj, eobj, hotType, r);
					//					var startp = getLocation(eobj, hotEndType, eflag);
					//					var endp = getLocation(sobj, hotType, eflag);
					//
					//					toRedraw(fb, r, hotEndType, startp, endp);
					continue;
				} else {
					var hotEndType = getEndType(sobj, eobj, hotType, r);
					var startp = getLocation(sobj, hotType);
					var endp = getLocation(eobj, hotEndType);

					toRedraw(b, r, hotType, startp, endp);
				}
			}
		}

	} else {
		var svgobj = document.getElementById("svg");
		for(i = 0; i < svgobj.childNodes.length; i++) {
			var b = svgobj.childNodes[i];
			if(b.tagName == "polyline" && !isAssist(b)) {

				var oo = b.id.replace("polyline", "").split("_");
				var hotType = b.getAttribute("hotType");
				var r = b.getAttribute("PolyLineType");

				var sobj = evalbroker("Node" + oo[0]);
				var eobj = evalbroker("Node" + oo[1]);

				var sflag = "";
				var eflag = "";
				if(document.getElementById("polyline" + oo[1] + "_" + oo[0])) {
					var fb = document.getElementById("polyline" + oo[1] + "_" + oo[0]);
					var se = getstartOrend(b, fb);
					if(se) {
						sflag = "start";
						eflag = "end";
					} else {
						sflag = "end";
						eflag = "start";
					}

					var hotEndType = getEndType(sobj, eobj, hotType, r);
					var startp = getLocation(sobj, hotType, sflag);
					var endp = getLocation(eobj, hotEndType, sflag);

					toRedraw(b, r, hotType, startp, endp);

					//var hotEndType = getEndType(sobj, eobj, hotType, r);
					var startp = getLocation(eobj, hotEndType, eflag);
					var endp = getLocation(sobj, hotType, eflag);

					toRedraw(fb, r, hotEndType, startp, endp);
				} else {
					var hotEndType = getEndType(sobj, eobj, hotType, r);
					var startp = getLocation(sobj, hotType);
					var endp = getLocation(eobj, hotEndType);

					toRedraw(b, r, hotType, startp, endp);
				}
			}
		}
	}
}

//20171102 定义结束连接点的类型
function getEndType(a, b, hotType, r) {
	var ax = converterSVG(a, "left");
	var ay = converterSVG(a, "top");
	var bx = converterSVG(b, "left");
	var by = converterSVG(b, "top");
	var hotEndType = "Bottom";

	switch(r) {
		case "Bottom":
			if(hotType == "Bottom")
				hotEndType = "Top";
			else
				hotEndType = "Bottom";
			break;
		case "Bian":
			if(hotType == "Right")
				hotEndType = "Left";
			else
				hotEndType = "Right";
			break;
		case "Line":
			if(hotType == "Left")
				hotEndType = "Right";
			else if(hotType == "Right")
				hotEndType = "Left";
			else if(hotType == "Bottom")
				hotEndType = "Top";
			else
				hotEndType = "Bottom";
			break;
		case "zhexian":
			switch(hotType) {
				case "Bottom":
					hotEndType = "Bottom";
					break;
				case "Top":
					hotEndType = "Top";
					break;
				case "Right":
					hotEndType = "Right";
					break;
				case "Left":
					hotEndType = "Left";
					break
			}
			break;
		case "zhexian90":
			if(ay > by) {
				hotEndType = "Bottom";
			} else {
				hotEndType = "Top";
			}
			break;
		case "zhexian902":
			if(ax < bx) {
				hotEndType = "Left";
			} else {
				hotEndType = "Right";
			}
			break
	}

	return hotEndType;
}

//20171102 获取结束连接点的类型
function gethotEndType(a, b, x, y, t) {
	var x1 = converterSVG(a, "left");
	var x2 = converterSVG(a, "left") + converterSVG(a, "width");
	var y1 = converterSVG(a, "top");
	var y2 = converterSVG(a, "top") + converterSVG(a, "height");

	var points = b.getAttribute("points");
	var p = points.replace(/( )/gi, ",").split(",");

	var lx = p[p.length - 2] - 0;
	var ly = p[p.length - 1] - 0;
	if(t != undefined && t == "start") {
		var aLeft = Math.abs(x1 - lx);
		var aRight = Math.abs(x2 - lx);
		var aTop = Math.abs(y1 - ly);
		var aBottom = Math.abs(y2 - ly);
	} else {
		var aLeft = Math.abs(x1 - x - lx);
		var aRight = Math.abs(x2 - x - lx);
		var aTop = Math.abs(y1 - y - ly);
		var aBottom = Math.abs(y2 - y - ly);
	}

	var arr = [{
			name: "aLeft",
			v: aLeft
		},
		{
			name: "aRight",
			v: aRight
		},
		{
			name: "Top",
			v: aTop
		},
		{
			name: "aBottom",
			v: aBottom
		}
	]
	/// 从小到大按属性b排序
	arr.sort(function(x, y) {
		return x.v > y.v ? 1 : -1;
	});
	return arr[0].name;
}

//20171103 移动时，获取双向线段的相对位置
function getstartOrend(b, fb) {
	var p = b.getAttribute("points").replace(/( )/gi, ",").split(",");
	var fp = fb.getAttribute("points").replace(/( )/gi, ",").split(",");
	if(Math.abs(p[0] - fp[fp.length - 2]) < 1) {
		if(p[1] - fp[fp.length - 1]) {
			return true;
		}
	} else {
		if(p[0] - fp[fp.length - 2] < 0) {
			return true;
		}
	}
	return false;
}

//20171103 计算移动后线的位置
function getLocationnew(a, b, c, r, hotType) {

	var str = "";
	var ax = converterSVG(a, "left") + offsetX;
	var ay = converterSVG(a, "top") + offsetY;
	var aw = converterSVG(a, "width");
	var ah = converterSVG(a, "height");
	var bx = converterSVG(b, "left") + offsetX;
	var by = converterSVG(b, "top") + offsetY;
	var bw = converterSVG(b, "width");
	var bh = converterSVG(b, "height");

	offsetX = document.body.scrollLeft;
	offsetY = document.body.scrollTop;
	var rx = c.split(",")[0] - 0;
	var ry = c.split(",")[1] - 0;
	var rox = parseFloat(c.split(",")[2]);
	var roy = parseFloat(c.split(",")[3]);
	//	rox = (rox=="0")?0:parseFloat(rox);
	//	roy = (roy=="0")?0:parseFloat(roy);

	//console.log("rox:" + rox * aw + "  roy:" + roy * ah + "  rx:" + rx + " ry:" + ry);
	//str = (ax + rx) + "," + (ay + ry);
	str = (ax + rox * aw) + "," + (ay + roy * ah);

	/*switch(r) {
		case "Bottom":
			if((ay + ah) < by)
				str = (ax + rx) + "," + (ay + ah + ry);
			else
				str = (ax + rx) + "," + (ay + ry);

			//			if(ay > (by + bh)) {
			//				str = (ax + rx) + "," + (ay + ry);
			//			}
			break;
		case "Bian":
			if((ax + aw) < bx)
				str = (ax + aw + rx) + "," + (ay + ry);
			else
				str = (ax + rx) + "," + (ay + ry);
			break;
		case "Line":
			if(hotType == "Left" || hotType == "Right") {
				if((ax + aw) < bx)
					str = (ax + aw + rx) + "," + (ay + ry);

			} else {
				if((ay + ah) < by)
					str = (ax + rx) + "," + (ay + ah + ry);
			}
			break;
		case "zhexian":
			str = (ax + rx) + "," + (ay + ah + ry);
			break;
		case "zhexian90":
			if((ax + aw) < bx)
				str = (ax + aw + rx) + "," + (ay + ry);
			break;
		case "zhexian902":
			if((ay + ah) < by)
				str = (ax + rx) + "," + (ay + ah + ry);
			break
	}
*/
	return str;
}

//20171101 a节点对象，b为线
function getLocation(aa, b, c) {

	var a = unionNode(aa);
	/*var allLine = "";
	g = a.getAttribute("LinkeyStartObj");
	h = a.getAttribute("LinkeyEndObj");
	if(g != undefined && g != "") {
		allLine += g;
	}
	if(h != undefined && h != "") {
		allLine = allLine + "," + h;
	}
	var linecount = allLine.split(",").length + 1*/
	;

	var linecount = 1 / 2;
	if(c != "") {
		if(c == "start") {
			linecount = 1 / 3;
		} else if(c == "end") {
			linecount = 2 / 3;
		}
	}
	var x1 = converterSVG(a, "left") + offsetX;
	var x2 = x1 + converterSVG(a, "width");
	var y1 = converterSVG(a, "top") + offsetY;
	var y2 = y1 + converterSVG(a, "height");
	var hx = converterSVG(a, "width") * linecount;
	var hy = converterSVG(a, "height") * linecount;
	var x = x1 + hx;
	var y = y1 + hy;

	/*if(g != undefined && g != "") {
		var j = g.split(",");
		if(b == "Left") {
			x = x1;
			outer:
				for(z = 1; z < linecount; z++) {
					inner: for(i = 0; i < j.length; i++) {
						var k = evalbroker(j[i]);
						var p = k.getAttribute("points").replace(" ", ",").split(",");
						var px = p[0];
						var py = p[1];
						var pex = p[p.length - 2];
						var pey = p[p.length - 1];
						if(Math.abs(y - py) < 1) {
							break inner;
						} else {
							break outer;
						}
					}
					y = y1 + hy * (z + 1);
				}
		}
		if(b == "Right") {
			x = x2;
			outer:
				for(z = 1; z < linecount; z++) {
					inner: for(i = 0; i < j.length; i++) {
						var k = evalbroker(j[i]);
						var p = k.getAttribute("points").replace(" ", ",").split(",");
						var px = p[0];
						var py = p[1];
						var pex = p[p.length - 2];
						var pey = p[p.length - 1];
						if(Math.abs(y - py) < 1) {
							break inner;
						} else {
							break outer;
						}
					}
					y = y1 + hy * (z + 1);
				}
		}
		if(b == "Top") {
			y = y1;
			outer:
				for(z = 1; z < linecount; z++) {
					inner: for(i = 0; i < j.length; i++) {
						var k = evalbroker(j[i]);
						var p = k.getAttribute("points").replace(" ", ",").split(",");
						var px = p[0];
						var py = p[1];
						var pex = p[p.length - 2];
						var pey = p[p.length - 1];
						if(Math.abs(x - px) < 1) {
							break inner;
						} else {
							break outer;
						}
					}
					x = x1 + hx * (z + 1);
				}
		}
		if(b == "Bottom") {
			y = y2;
			outer:
				for(z = 1; z < linecount; z++) {
					inner: for(i = 0; i < j.length; i++) {
						var k = evalbroker(j[i]);
						var p = k.getAttribute("points").replace(" ", ",").split(",");
						var px = p[0];
						var py = p[1];
						var pex = p[p.length - 2];
						var pey = p[p.length - 1];
						if(Math.abs(x - px) < 1) {
							break inner;
						} else {
							break outer;
						}
					}
					x = x1 + hx * (z + 1);
				}
		}

	}
	if(h != undefined && h != "") {
		allLine = allLine + "," + h;
	}
*/
	//var str = x + "," + y;
	var str = "";
	if(b == "Left") {
		str = x1 + "," + y;
	}
	if(b == "Right") {
		str = x2 + "," + y;
	}
	if(b == "Top") {
		str = x + "," + y1;
	}
	if(b == "Bottom") {
		str = x + "," + y2;
	}
	return str;
}

function toRedraw(b, r, hotType, startp, endp) {
	try {
		var lx = startp.split(",")[0] - 0;
		var ly = startp.split(",")[1] - 0;
		var lex = endp.split(",")[0] - 0;
		var ley = endp.split(",")[1] - 0;

		var p = 30;
		var q = 30;
		var t = "";

		switch(r) {
			case "Bottom":
				var u = ly + (ley - ly) / 2;
				if(b.getAttribute("TopY") != undefined && b.getAttribute("TopY") != "") u = parseInt(b.getAttribute("TopY"));
				t = startp + " " + lx + "," + u + " " + lex + "," + u + " " + endp;
				break;
			case "Bian":
				var u = lx + (lex - lx) / 2;
				if(b.getAttribute("TopY") != undefined && b.getAttribute("TopY") != "") u = parseInt(b.getAttribute("TopY"));
				t = startp + " " + u + "," + ly + " " + u + "," + ley + " " + endp;
				break;
			case "Line":
				t = startp + " " + endp;
				break;
			case "zhexian":
				switch(hotType) {
					case "Bottom":
						p = (ley <= ly) ? (ly + p) : (ley + p);
						if(b.getAttribute("TopY") != undefined && b.getAttribute("TopY") != "") p = parseInt(b.getAttribute("TopY"));
						t = startp + " " + lx + "," + p + " " + lex + "," + p + " " + endp;
						break;
					case "Top":
						p = (ley <= ly) ? (ley - p) : (ly - p);
						if(b.getAttribute("TopY") != undefined && b.getAttribute("TopY") != "") p = parseInt(b.getAttribute("TopY"));
						t = startp + " " + lx + "," + p + " " + lex + "," + p + " " + endp;
						break;
					case "Right":
						q = (lex <= lx) ? (lx + q) : (lex + q);
						if(b.getAttribute("TopY") != undefined && b.getAttribute("TopY") != "") q = parseInt(b.getAttribute("TopY"));
						t = startp + " " + q + "," + ly + " " + q + "," + ley + " " + endp;
						break;
					case "Left":
						q = (lex <= lx) ? (lex - q) : (lx - q);
						if(b.getAttribute("TopY") != undefined && b.getAttribute("TopY") != "") q = parseInt(b.getAttribute("TopY"));
						t = startp + " " + q + "," + ly + " " + q + "," + ley + " " + endp;
						break
				}
				break;
			case "zhexian90":
				t = startp + " " + lex + "," + ly + " " + endp;
				break;
			case "zhexian902":
				t = startp + " " + lx + "," + ley + " " + endp;
				break
		}
		b.setAttribute("oldpoints", t);
		b.setAttribute("orpoints", t);
		b.setAttribute("points", t);
		AddRouterText(b, "MoveObj")
	} catch(E) {
		ShowErrorInfo("本活动出错,请执行错误修复操作!")
	}

}

//// 20170924 a为节点对象，bc为左边距和上边距
function SvgMove(a, b, c) {
	if(a.tagName == "text" && a.getAttribute("NodeType") == "spantext") {
		a.setAttribute("x", (b + offsetX));
		a.setAttribute("y", (c + 15 + offsetY));
		return;
	}

	var aa = unionNode(null, a);
	var tagN = a.tagName;
	var box = a.getBBox();
	var widthx = null;
	var heightx = null;
	var boxr = null;
	var boxx = null;
	var boxy = null;

	//IE辅助调节变量
	var xe = 0;
	var ye = 0;
	var flagIE = false;

	if(bowerflag.indexOf("IE") != -1) {
		flagIE = true;
		xe = -1;
		ye = 3;
	}

	if((a.getAttribute("NodeType") == "YNode_rect")) {
		if(b != null) {
			var bb = b + offsetY;
			boxx = box.width / 2;
			boxy = box.height / 2;
			a.setAttribute("x", bb);
			var tid = a.id;
			if(tid.substring(4) < 10000) tid = parseInt(tid.substring(4)) + 10000;
			else tid = parseInt(tid.substring(4)) - 10000;
			var text = document.getElementById("Node" + tid + "_xtb");
			var xNode = document.getElementById("Node" + tid);
			xNode.setAttribute("x", bb);
			text.setAttribute("x", bb + boxx - 60);

		}

	} else if((a.getAttribute("NodeType") == "XNode_rect")) {
		if(c != null) {
			var cc = c + offsetY;
			boxx = box.width / 2;
			boxy = box.height / 2;
			a.setAttribute("y", cc + 10);
			var tid = a.id;
			if(tid.substring(4) < 10000) tid = parseInt(tid.substring(4)) + 10000;
			else tid = parseInt(tid.substring(4)) - 10000;
			var text = document.getElementById("Node" + tid + "_xtb");
			var xNode = document.getElementById("Node" + tid);
			xNode.setAttribute("y", cc);
			text.setAttribute("y", cc + boxy - 60);

		}

	}
	offsetX = parseInt(document.body.scrollLeft);
	offsetY = parseInt(document.body.scrollTop);
	var bb = b;
	var cc = c;
	if(offsetX != 0) {
		bb = b + offsetX;
	}
	if(offsetY != 0) {
		cc = c + offsetY;
	}
	//	console.log("boxx=>:" + box.width + "  boxy=>" + box.height);
	if(b != null && c != null && (tagN == "path" || tagN == "polygon")) {
		boxx = box.width / 2;
		boxy = box.height / 2;
		if(flagIE) {
			xe = 2;
			ye = 2;
		}
		if(tagN == "path") {
			a.setAttribute("transform", "translate(" + (bb + xe) + "," + (cc + ye) + ")");
		}
		//修改2017.11.07
		if(tagN == "polygon") {
			var h = converterSVG(a, "height") / 2;
			var w = converterSVG(a, "width") / 2;
			a.setAttribute("points", (bb + boxx + xe) + "," + ((cc + boxy + ye) - h) + " " + ((bb + boxx + xe) + w) + "," + (cc + boxy + ye) + " " + (bb + boxx + xe) + "," + ((cc + boxy + ye) + h) + " " + ((bb + boxx + xe) - w) + "," + (cc + boxy + ye));
		}
		if(aa) {
			aa.setAttribute("x", bb + boxx + xe);
			aa.setAttribute("y", cc + boxy + ye + 6);
		}
	} else if(aa && (aa.tagName == "path" || aa.tagName == "polygon")) {
		if(flagIE) {
			xe = -1;
			ye = 3;
		}
		boxx = box.width / 2;
		boxy = box.height / 2;
		widthx = Math.abs(converterSVG(a, "width") - converterSVG(aa, "width")) / 2;
		heightx = Math.abs(converterSVG(a, "height") - converterSVG(aa, "height")) / 2;
		//console.log("width:" + a.getAttribute("width") + "  height:" + a.getAttribute("height"));
		a.setAttribute("x", bb + boxx + 1 + xe);
		a.setAttribute("y", cc + boxy + 6 + ye);
		if(aa.tagName == "path") {
			if(flagIE) ye = 4;
			console.log("heightx:" + heightx)
			aa.setAttribute("transform", "translate(" + (bb - widthx) + "," + (cc - heightx + ye) + ")");
		}
		if(aa.tagName == "polygon") {
			if(flagIE) {
				xe = -1;
				ye = 1;
			}
			var h = converterSVG(aa, "height") / 2;
			var w = converterSVG(aa, "width") / 2;
			aa.setAttribute("points", (bb + boxx + xe + 1) + "," + ((cc + boxy) - h + ye + 1) + " " + ((bb + boxx) + w + xe + 1) + "," + (cc + boxy + ye + 1) + " " + (bb + boxx + xe + 1) + "," + ((cc + boxy) + ye + h + 1) + " " + ((bb + boxx) - w + xe + 1) + "," + (cc + boxy + ye + 1));
		}
	} else {
		if(b != null) {
			boxr = box.width / 2;
			if(aa) {
				widthx = Math.abs(a.getAttribute("width") - aa.getAttribute("width")) / 2;
			} else {
				width = 0;
			}
			if(tagN == "circle") {
				if(flagIE) xe = 2;
				a.setAttribute("cx", bb + boxr + xe);
				if(aa) {
					aa.setAttribute("x", bb + boxr + xe);
				}
			} else if(tagN == "text" && a.getAttribute("NodeType") != "spantext") {
				if(aa.tagName == "circle") {
					a.setAttribute("x", bb + boxr);
					if(aa) {
						aa.setAttribute("cx", bb + boxr);
					}
				} else {
					if(flagIE) xe = -1;
					a.setAttribute("x", bb + boxr + 1 + xe);
					if(aa) {
						aa.setAttribute("x", bb + boxr - widthx + 1 + xe);
					}
				}
			} else {
				if(flagIE) xe = 2;
				a.setAttribute("x", bb + xe);
				if(aa) {
					aa.setAttribute("x", bb + widthx + xe);
				}
			}
		}
		if(c != null) {
			boxr = box.height / 2;
			if(aa) {
				heightx = Math.abs(a.getAttribute("height") - aa.getAttribute("height")) / 2 + 4;
			} else {
				heigtx = 0;
			}
			if(tagN == "circle") {
				if(flagIE) ye = 2;
				var box = a.getBBox();
				heightx = box.height / 2;
				a.setAttribute("cy", cc + boxr + ye);
				if(aa) {
					aa.setAttribute("y", cc + boxr + 3 + ye);
				}
			} else if(tagN == "text" && a.getAttribute("NodeType") != "spantext") {
				if(aa) {
					if(flagIE) ye = 3;
					if(aa.tagName == "circle") {
						a.setAttribute("y", cc + boxr + 6 + ye);
						aa.setAttribute("cy", cc + boxr + 3 + ye);
					} else {
						a.setAttribute("y", cc + boxr + 6 + ye);
						aa.setAttribute("y", cc + boxr - heightx + 6 + ye);
					}
				}
			} else {
				if(flagIE) ye = 2;
				a.setAttribute("y", cc + ye);
				if(aa) {
					aa.setAttribute("y", cc + heightx + ye);
				}
			}
		}
	}
}

// 20170921 a为节点对象，b为初始节点左边距，c为初始节点上边距，df为位移量
//此方法是针对getEventOX(event)和getEventOY(event)写的
function SvgMove_old(a, b, c, d, f) {
	var aa = unionNode(null, a);
	var tagN = tagN;
	var widthx = null;
	var heightx = null;
	var moveX = null;
	var moveY = null;
	if(b != undefined && b != null && d != undefined && d != null) {
		moveX = (b + d);
	}
	if(c != undefined && c != null && f != undefined && f != null) {
		moveY = (c + f);
	}

	if(b != null && c != null && (a.tagName == "path" || a.tagName == "polygon")) {

		if(a.tagName == "path")
			a.setAttribute("transform", "translate(" + (getEventOX(event) - mstartX + b) + "," + (getEventOY(event) - mstartY + c) + ")");
		if(a.tagName == "polygon") {
			var box = a.getBBox();
			// a.setAttribute("transform", "translate(" + (getEventOX(event) -
			// box.width / 2) + "," + (getEventOY(event) - box.height / 2) + ")");
			a.setAttribute("points", (getEventOX(event) - mstartX + b + box.width / 2) + "," + ((getEventOY(event) - mstartY + c + box.height / 2) - 30) + " " + ((getEventOX(event) - mstartX + b + box.width / 2) + 50) + "," + (getEventOY(event) - mstartY + c + box.height / 2) + " " + (getEventOX(event) - mstartX + b + box.width / 2) + "," + ((getEventOY(event) - mstartY + c + box.height / 2) + 30) + " " + ((getEventOX(event) - mstartX + b + box.width / 2) - 50) + "," + (getEventOY(event) - mstartY + c + box.height / 2));
		}
	} else {
		if(moveX != null) {
			a.setAttribute(converterSVG(a, null, "left"), moveX);
			if(aa) {
				if(a.tagName != "circle") {
					if(aa && aa.tagName == "circle") {
						widthx = 0;
					} else {
						widthx = Math.abs(a.getAttribute("width") - aa.getAttribute("width")) / 2;
					}
				} else {
					widthx = 0;
				}
				if(aa.tagName == "text") {
					aa.setAttribute(converterSVG(aa, null, "left"), (moveX + widthx));
				} else {
					aa.setAttribute(converterSVG(aa, null, "left"), (moveX - widthx));
				}
			}
		}
		if(moveY != null) {
			a.setAttribute(converterSVG(a, null, "top"), moveY);
			if(aa) {
				if(a.tagName != "circle") {
					if(aa.tagName == "circle") {
						heightx = 3;
					} else {
						heightx = Math.abs(a.getAttribute("height") - aa.getAttribute("height")) / 2 + 5;
					}
				} else {
					heightx = 3;
				}
				// console.log("a:"+a.getAttribute("height")+"
				// aa:"+aa.getAttribute("height"));
				if(aa.tagName == "text") {
					aa.setAttribute(converterSVG(aa, null, "top"), (moveY + heightx));
				} else {
					aa.setAttribute(converterSVG(aa, null, "top"), (moveY - heightx));
				}
			}
		}
	}
}

// 20170921 计算图形边距，显示虚线
function SvgDottedLine(a) {
	if(a.getAttribute("NodeType") == "spantext")
		return;
	var left = null;
	var top = null;
	var plx = document.getElementById("PolyLineX");
	var ply = document.getElementById("PolyLineY");
	//var svgobj = document.getElementById("svg");
	if(a.tagName == "text") {
		a = unionNode(null, a);
		//		console.log("tagName=>" + a.tagName);
	}
	var box = a.getBBox();
	left = (converterSVG(a, "left") - 0 + offsetX);
	top = (converterSVG(a, "top") - 0 + offsetY);
	plx.style.display = "";
	ply.style.display = "";
	plx.setAttribute("y1", top);
	plx.setAttribute("y2", top);
	ply.setAttribute("x1", left);
	ply.setAttribute("x2", left);
}

//20170925 多选移动处理
//20171013 add多选时线的移动,
//20171025 添加flag,用于判断未选择时，默认全选移动
function MoveNodeObjV9(a, flag) {
	var i = 0;
	try {
		if(event.button == rightbtn) return false
	} catch(e) {}
	if(flag != undefined && flag.indexOf("movenoselect") != -1) {
		var ass = flag.replace("movenoselect", "");
		if(ass == "left") {
			var b = -10;
			var c = 0;
		}
		if(ass == "right") {
			var b = 10;
			var c = 0;
		}
		if(ass == "top") {
			var b = 0;
			var c = -10;
		}
		if(ass == "bottom") {
			var b = 0;
			var c = 10;
		}
	} else {
		var b = getEventOX(event) - mstartX;
		var c = getEventOY(event) - mstartY;
	}
	var d = top.document.all.MulNodeList.value;

	/*	if(a.tagName == "text") {
			f = unionNode(null, a).getAttribute("LinkeyStartObj");
			g = unionNode(null, a).getAttribute("LinkeyEndObj");
		} else {
			f = a.getAttribute("LinkeyStartObj");
			g = a.getAttribute("LinkeyEndObj");
		}
		if(f != "" && f != undefined) {
			var h = f.split(",");
			for(var i = 0; i < h.length; i++) {
				var j = evalbroker(h[i]);
				var k = j.getAttribute("oldpoints");
				var l = /( )/gi;
				k = k.replace(l, ",");
				var m = k.split(",");
				var ss = m[0] - 0 + b;
				var ee = m[1] - 0 + c;
				var startp = ss + "," + ee;
				var linestart2x = m[2] - 0 + b;
				var linestart2y = m[3] - 0 + c;
				var n = j.id.replace("polyline", "").split("_");
				var o = "Node" + n[0];
				var p = "Node" + n[1];
				var q = d + ",";
				if(q.indexOf(p + ",") != -1) {
					var r = m[m.length - 2] - 0 + b;
					var s = m[m.length - 1] - 0 + c;
				} else {
					var r = m[m.length - 2] - 0;
					var s = m[m.length - 1] - 0;
				}
				var t = r;
				var u = s;
				var v = 30;
				var w = 30;
				var x = j.getAttribute("PolyLineType");
				var y = j.getAttribute("hotType");
				var z = "";
				switch(x) {
					case "Bottom":
						var A = parseFloat(ee) + (s - ee) / 2;
						if(j.getAttribute("TopY") != undefined && j.getAttribute("TopY") != "") A = parseInt(j.getAttribute("TopY"));
						z = startp + " " + ss + "," + A + " " + r + "," + A + " " + r + "," + s;
						break;
					case "Bian":
						var A = parseFloat(ss) + (r - ss) / 2;
						if(j.getAttribute("TopY") != undefined && j.getAttribute("TopY") != "") A = parseInt(j.getAttribute("TopY"));
						z = startp + " " + A + "," + ee + " " + A + "," + s + " " + r + "," + s;
						break;
					case "Line":
						z = startp + " " + r + "," + s;
						//console.log("start z:" + z);
						break;
					case "zhexian":
						switch(y) {
							case "Bottom":
								v = (s <= ee) ? (ee + v) : (s + v);
								if(j.getAttribute("TopY") != undefined && j.getAttribute("TopY") != "") v = parseInt(j.getAttribute("TopY"));
								z = startp + " " + ss + "," + v + " " + t + "," + v + " " + t + "," + u;
								break;
							case "Top":
								v = (s <= ee) ? (s - v) : (ee - v);
								if(j.getAttribute("TopY") != undefined && j.getAttribute("TopY") != "") v = parseInt(j.getAttribute("TopY"));
								z = startp + " " + ss + "," + v + " " + t + "," + v + " " + t + "," + u;
								break;
							case "Right":
								w = (r <= ss) ? (ss + w) : (r + w);
								if(j.getAttribute("TopY") != undefined && j.getAttribute("TopY") != "") w = parseInt(j.getAttribute("TopY"));
								z = startp + " " + w + "," + ee + " " + w + "," + u + " " + t + "," + u;
								break;
							case "Left":
								w = (r <= ss) ? (r - w) : (ss - w);
								if(j.getAttribute("TopY") != undefined && j.getAttribute("TopY") != "") w = parseInt(j.getAttribute("TopY"));
								z = startp + " " + w + "," + ee + " " + w + "," + u + " " + t + "," + u;
								break
						}
						break;
					case "zhexian90":
						z = startp + " " + r + "," + ee + " " + r + "," + s;
						break;
					case "zhexian902":
						z = startp + " " + ss + "," + s + " " + r + "," + s;
						break
				}
				j.setAttribute("orpoints", z);
				j.setAttribute("points", z);
				AddRouterText(j, "MoveObj")
			}
		}
		if(g != "" && g != undefined) {
			var h = g.split(",");
			for(var i = 0; i < h.length; i++) {
				var j = evalbroker(h[i]);
				var k = j.getAttribute("oldpoints");
				var l = /( )/gi;
				k = k.replace(l, ",");
				var m = k.split(",");
				var n = j.id.replace("polyline", "").split("_");
				var o = "Node" + n[0];
				var p = "Node" + n[1];
				var q = d + ",";
				if(q.indexOf(o + ",") != -1) {
					var ss = m[0] - 0 + b;
					var ee = m[1] - 0 + c;
				} else {
					var ss = m[0] - 0;
					var ee = m[1] - 0;
				}
				var startp = ss + "," + ee;
				var linestart2x = m[2] - 0 + b;
				var linestart2y = m[3] - 0 + c;
				var r = m[m.length - 2] - 0 + b;
				var s = m[m.length - 1] - 0 + c
				var t = r;
				var u = s;
				var v = 30;
				var w = 30;
				var x = j.getAttribute("PolyLineType");
				var y = j.getAttribute("hotType");
				var z = "";
				switch(x) {
					case "Bottom":
						var A = parseFloat(ss) + (s - ss) / 2;
						if(j.getAttribute("TopY") != undefined && j.getAttribute("TopY") != "") A = parseInt(j.getAttribute("TopY"));
						z = startp + " " + ss + "," + A + " " + r + "," + A + " " + r + "," + s;
						break;
					case "Bian":
						var A = parseFloat(ss) + (r - ss) / 2;
						if(j.getAttribute("TopY") != undefined && j.getAttribute("TopY") != "") A = parseInt(j.getAttribute("TopY"));
						z = startp + " " + A + "," + ee + " " + A + "," + s + " " + r + "," + s;
						break;
					case "Line":
						z = startp + " " + r + "," + s;
						//console.log("end z:" + z);
						break;
					case "zhexian":
						switch(y) {
							case "Bottom":
								v = (s <= ee) ? (ee + v) : (s + v);
								if(j.getAttribute("TopY") != undefined && j.getAttribute("TopY") != "") v = parseInt(j.getAttribute("TopY"));
								z = startp + " " + ss + "," + v + " " + t + "," + v + " " + t + "," + u;
								break;
							case "Top":
								v = (s <= ee) ? (s - v) : (ee - v);
								if(j.getAttribute("TopY") != undefined && j.getAttribute("TopY") != "") v = parseInt(j.getAttribute("TopY"));
								z = startp + " " + ss + "," + v + " " + t + "," + v + " " + t + "," + u;
								break;
							case "Right":
								w = (r <= ss) ? (ss + w) : (r + w);
								if(j.getAttribute("TopY") != undefined && j.getAttribute("TopY") != "") w = parseInt(j.getAttribute("TopY"));
								z = startp + " " + w + "," + ee + " " + w + "," + u + " " + t + "," + u;
								break;
							case "Left":
								w = (r <= ss) ? (r - w) : (ss - w);
								if(j.getAttribute("TopY") != undefined && j.getAttribute("TopY") != "") w = parseInt(j.getAttribute("TopY"));
								z = startp + " " + w + "," + ee + " " + w + "," + u + " " + t + "," + u;
								break
						}
						break;
					case "zhexian90":
						z = startp + " " + r + "," + ee + " " + r + "," + s;
						break;
					case "zhexian902":
						z = startp + " " + ss + "," + s + " " + r + "," + s;
						break
				}
				j.setAttribute("orpoints", z);
				j.setAttribute("points", z);
				AddRouterText(j, "MoveObj")
			}
		}*/

	//console.log("oldleft:"+a.getAttribute("oldleft")+"  oldtop:"+a.getAttribute("oldtop"));

	SvgMove(a, (parseInt(a.getAttribute("oldleft")) + b), (parseInt(a.getAttribute("oldtop")) + c));

	var B = (flag == undefined) ? getEventOX(event) : 0;
	var C = (flag == undefined) ? getEventOY(event) : 0;
	var D = document.body.scrollLeft;
	var E = document.body.scrollTop;
	var F = document.body.clientWidth;
	var G = document.body.clientHeight;
	if(B > F) window.scrollTo(D + 3, E);
	if(B < 0) window.scrollTo(D - 3, E);
	if(C > G) window.scrollTo(D, E + 3);
	if(C < 0) window.scrollTo(D, E - 3)
}

function MoveNodeObjV9_old(a) {
	var i = 0;
	try {
		if(event.button == 2) return false
	} catch(e) {}
	var b = getEventOX(event) - mstartX;
	var c = getEventOY(event) - mstartY;
	var d = top.document.all.MulNodeList.value;
	var f = a.getAttribute("LinkeyStartObj");
	var g = a.getAttribute("LinkeyEndObj");
	if(f != "" && f != undefined) {
		var h = f.split(",");
		for(var i = 0; i < h.length; i++) {
			var j = evalbroker(h[i]);
			j.style.left = parseInt(j.oldleft) + b;
			j.style.top = parseInt(j.oldtop) + c;
			var k = j.oldpoints;
			var l = /( )/gi;
			k = k.replace(l, ",");
			l = /px/gi;
			k = k.replace(l, "");
			var m = k.split(",");
			var n = j.id.replace("polyline", "").split("_");
			var o = "Node" + n[0];
			var p = "Node" + n[1];
			var q = d + ",";
			if(q.indexOf(p + ",") != -1) {
				var r = m[m.length - 2] - 0;
				var s = m[m.length - 1] - 0
			} else {
				var r = m[m.length - 2] - 0 - b;
				var s = m[m.length - 1] - 0 - c
			}
			var t = r;
			var u = s;
			var v = 30;
			var w = 30;
			var x = j.PolyLineType;
			var y = j.hotType;
			var z = "";
			switch(x) {
				case "Bottom":
					var A = s / 2;
					if(j.TopY != undefined && j.TopY != "") A = parseInt(j.TopY);
					z = "0,0" + " " + "0," + A + "px " + r + "px," + A + "px " + r + "px," + s + "px";
					break;
				case "Bian":
					var A = r / 2;
					if(j.TopY != undefined && j.TopY != "") A = parseInt(j.TopY);
					z = "0,0" + " " + A + "px,0" + " " + A + "px," + s + "px " + r + "px," + s + "px";
					break;
				case "Line":
					z = "0,0" + " " + r + "px," + s + "px";
					break;
				case "zhexian":
					switch(y) {
						case "Bottom":
							v = (s <= 0) ? (v) : (s + v);
							if(j.TopY != undefined && j.TopY != "") v = parseInt(j.TopY);
							z = "0,0" + " 0," + v + "px " + t + "px," + v + "px " + t + "px," + u + "px";
							break;
						case "Top":
							v = (s <= 0) ? (s - v) : (-v);
							if(j.TopY != undefined && j.TopY != "") v = parseInt(j.TopY);
							z = "0,0" + " 0," + v + "px " + t + "px," + v + "px " + t + "px," + u + "px";
							break;
						case "Right":
							w = (r <= 0) ? (w) : (r + w);
							if(j.TopY != undefined && j.TopY != "") w = parseInt(j.TopY);
							z = "0,0" + " " + w + "px,0 " + w + "px," + u + "px " + t + "px," + u + "px";
							break;
						case "Left":
							w = (r <= 0) ? (r - w) : (-w);
							if(j.TopY != undefined && j.TopY != "") w = parseInt(j.TopY);
							z = "0,0" + " " + w + "px,0 " + w + "px," + u + "px " + t + "px," + u + "px";
							break
					}
					break;
				case "zhexian90":
					z = "0,0" + " " + r + "px,0 " + r + "px," + s + "px";
					break;
				case "zhexian902":
					z = "0,0 0," + s + " " + r + "px," + s + "px";
					break
			}
			j.orpoints = z;
			j.points.value = z;
			AddRouterText(j, "MoveObj")
		}
	}
	if(g != "" && g != undefined) {
		var h = g.split(",");
		for(var i = 0; i < h.length; i++) {
			var j = evalbroker(h[i]);
			var k = j.oldpoints;
			var l = /( )/gi;
			k = k.replace(l, ",");
			l = /px/gi;
			k = k.replace(l, "");
			var m = k.split(",");
			var n = j.id.replace("polyline", "").split("_");
			var o = "Node" + n[0];
			var p = "Node" + n[1];
			var q = d + ",";
			if(q.indexOf(o + ",") != -1) {
				var r = m[m.length - 2] - 0;
				var s = m[m.length - 1] - 0
			} else {
				var r = m[m.length - 2] - 0 + b;
				var s = m[m.length - 1] - 0 + c
			}
			var t = r;
			var u = s;
			var v = 30;
			var w = 30;
			var x = j.PolyLineType;
			var y = j.hotType;
			var z = "";
			switch(x) {
				case "Bottom":
					var A = s / 2;
					if(j.TopY != undefined && j.TopY != "") A = parseInt(j.TopY);
					z = "0,0" + " " + "0," + A + "px " + r + "px," + A + "px " + r + "px," + s + "px";
					break;
				case "Bian":
					var A = r / 2;
					if(j.TopY != undefined && j.TopY != "") A = parseInt(j.TopY);
					z = "0,0" + " " + A + "px,0" + " " + A + "px," + s + "px " + r + "px," + s + "px";
					break;
				case "Line":
					z = "0,0" + " " + r + "px," + s + "px";
					break;
				case "zhexian":
					switch(y) {
						case "Bottom":
							v = (s <= 0) ? (v) : (s + v);
							if(j.TopY != undefined && j.TopY != "") v = parseInt(j.TopY);
							z = "0,0" + " 0," + v + "px " + t + "px," + v + "px " + t + "px," + u + "px";
							break;
						case "Top":
							v = (s <= 0) ? (s - v) : (-v);
							if(j.TopY != undefined && j.TopY != "") v = parseInt(j.TopY);
							z = "0,0" + " 0," + v + " " + t + "," + v + " " + t + "px," + u + "px";
							break;
						case "Right":
							w = (r <= 0) ? (w) : (r + w);
							if(j.TopY != undefined && j.TopY != "") w = parseInt(j.TopY);
							z = "0,0" + " " + w + "px,0 " + w + "px," + u + "px " + t + "px," + u + "px";
							break;
						case "Left":
							w = (r <= 0) ? (r - w) : (-w);
							if(j.TopY != undefined && j.TopY != "") w = parseInt(j.TopY);
							z = "0,0" + " " + w + "px,0 " + w + "px," + u + "px " + t + "px," + u + "px";
							break
					}
					break;
				case "zhexian90":
					z = "0,0" + " " + r + "px,0 " + r + "px," + s + "px";
					break;
				case "zhexian902":
					z = "0,0 0," + s + " " + r + "px," + s + "px";
					break
			}
			j.orpoints = z;
			j.points.value = z;
			AddRouterText(j, "MoveObj")
		}
	}
	a.style.left = parseInt(a.oldleft) + b;
	a.style.top = parseInt(a.oldtop) + c;
	var B = getEventOX(event);
	var C = getEventOY(event);
	var D = document.body.scrollLeft;
	var E = document.body.scrollTop;
	var F = document.body.clientWidth;
	var G = document.body.clientHeight;
	if(B > F) window.scrollTo(D + 3, E);
	if(B < 0) window.scrollTo(D - 3, E);
	if(C > G) window.scrollTo(D, E + 3);
	if(C < 0) window.scrollTo(D, E - 3)
}

//20170925
function InitOldXY() {
	for(var i = 0; i < document.all.length; i++) {
		var a = document.all(i);
		var b = a.tagName.toLowerCase();
		//节点文本转换为节点
		if(b == "text" && unionNode(null, a)) {
			a = unionNode(null, a);
		}
		if((b == "circle" || b == "rect" || b == "path" || b == "polygon") && a.getAttribute("NodeType") != null) {
			a.setAttribute("oldleft", parseInt(converterSVG(a, "left")));
			a.setAttribute("oldtop", parseInt(converterSVG(a, "top")));
			//alert("tagName:" + b + " NodeType:" + a.getAttribute("NodeType"));
			//console.log("oldleft:" + a.getAttribute("oldleft") + "  oldtop:" + a.getAttribute("oldtop"));
		}
		if(b == "polyline" && a.getAttribute("Nodeid") != null) {
			if(a.getAttribute("orpoints") == "" || a.getAttribute("orpoints") == undefined) {
				a.setAttribute("orpoints", a.getAttribute("oldpoints"));
			}
			a.setAttribute("oldpoints", a.getAttribute("orpoints"));
		} else {}
	}
}

function InitOldXY_old() {
	for(var i = 0; i < document.all.length; i++) {
		var a = document.all(i);
		var b = a.tagName.toLowerCase();
		if(b == "roundrect" || b == "shape" || b == "oval" || b == "polyline") {
			a.oldleft = parseInt(a.style.left);
			a.oldtop = parseInt(a.style.top);
			if(b == "polyline") {
				if(a.orpoints == "" || a.orpoints == undefined) {
					a.orpoints = a.oldpoints
				}
				a.oldpoints = a.orpoints
			} else {}
		}
	}
}

function SaveoldNodePosition_old(a) {

	// iadd
	// moveobjoldleft = parseInt(a.style.left);
	// moveobjoldtop = parseInt(a.style.top);
	moveobjoldleft = parseInt(converterSVG(a, "left"));
	moveobjoldtop = parseInt(converterSVG(a, "top"));

	if(goalTypeName == "Edit") return false;
	if(a.tagName == "polyline") {
		xx = getEventOX(event);
		yy = getEventOY(event);
		var b = a;
		oldArryStartLeft[0] = b.style.left.replace("px", "") - 0;
		oldArryStartTop[0] = b.style.top.replace("px", "") - 0;
		var c = b.oldpoints;
		var d = /( )/gi;
		c = c.replace(d, ",");
		d = /px/gi;
		c = c.replace(d, "");
		oldArrayEndPosx = c.split(",");
		oldArrayEndPosx[8] = parseInt(yy + document.body.scrollTop);
		oldArrayEndPosx[9] = parseInt(xx + document.body.scrollLeft);
		return false
	}
	var e = a.getAttribute("LinkeyStartObj");
	var f = a.getAttribute("LinkeyEndObj");
	var g = 0;
	EndMoveClearObj();
	if(e != "" && e != undefined) {
		var h = e.split(",");
		for(i = 0; i < h.length; i++) {
			var b = evalbroker(h[i]);
			// ichange
			// oldArryStartLeft[i] = b.style.left.replace("px", "") - 0;
			// oldArryStartTop[i] = b.style.top.replace("px", "") - 0;
			oldArryStartLeft[i] = b.getAttribute("cx") - 0;
			oldArryStartTop[i] = b.getAttribute("cy") - 0;
			var c = b.oldpoints;
			var d = /( )/gi;
			c = c.replace(d, ",");
			// ichange
			// d = /px/gi;
			// c = c.replace(d, "");
			var j = c.split(",");
			g = j[j.length - 2] - 0;
			oldArryEndPosx[i] = g;
			g = j[j.length - 1] - 0;
			oldArryEndPosy[i] = g
		}
	}
	if(f != "" && f != undefined) {
		var h = f.split(",");
		for(i = 0; i < h.length; i++) {
			var b = evalbroker(h[i]);
			var c = b.oldpoints;
			var d = /( )/gi;
			c = c.replace(d, ",");
			// d = /px/gi;
			// c = c.replace(d, "");
			var j = c.split(",");
			g = j[j.length - 2] - 0;
			oldArryEndPosx2[i] = g;
			g = j[j.length - 1] - 0;
			oldArryEndPosy2[i] = g
		}
	}
}

//20170930 修改
function SaveoldNodePosition(a) {
	if("YNode_text" == a.getAttribute("NodeType") || "XNode_text" == a.getAttribute("NodeType")) return false;
	// iadd
	//console.log("SaveoldNodePosition");
	moveobjoldleft = parseInt(converterSVG(a, "left"));
	moveobjoldtop = parseInt(converterSVG(a, "top"));

	// console.log("moveobjoldleft=>" + moveobjoldleft + " moveobjoldleft==>" +
	// moveobjoldleft);
	if(goalTypeName == "Edit") return false;
	if(a.tagName == "polyline") {
		xx = getEventOX(event);
		yy = getEventOY(event);
		var b = a;
		oldArryStartLeft[0] = converterSVG(b, "left") - 0;
		oldArryStartTop[0] = converterSVG(b, "top") - 0;
		//alert("left:" + oldArryStartLeft[0] + "  top:" + oldArryStartTop[0]);
		var c = b.getAttribute("oldpoints");
		var d = /( )/gi;
		c = c.replace(d, ",");
		//		d = /px/gi;
		//		c = c.replace(d, ""); 
		oldArrayEndPosx = c.split(",");
		oldArryPoints = b.getAttribute("points").replace(/( )/gi, ",");
		//console.log("oldArryPoints:" + oldArryPoints);
		oldArrayEndPosx[8] = parseInt(yy + document.body.scrollTop);
		oldArrayEndPosx[9] = parseInt(xx + document.body.scrollLeft);
		return false
	}
	var e = "";
	var f = "";
	if(a.tagName == "text" && a.getAttribute("NodeType") != "spantext") {
		e = unionNode(null, a).getAttribute("LinkeyStartObj");
		f = unionNode(null, a).getAttribute("LinkeyEndObj");
	} else {
		e = a.getAttribute("LinkeyStartObj");
		f = a.getAttribute("LinkeyEndObj");
	}
	var g = 0;
	//	console.log("LinkeyStartObj:"+e);
	//	console.log("LinkeyEndObj:"+f);
	EndMoveClearObj();
	if(e != "" && e != undefined) {
		var h = e.split(",");
		for(i = 0; i < h.length; i++) {
			//var pline = document.getElementById(h[i]);
			//var b = pline;
			var b = evalbroker(h[i]);
			//			oldArryStartLeft[i] = converterSVG(b, "left") - 0;
			//			oldArryStartTop[i] = converterSVG(b, "top") - 0;
			var c = b.getAttribute("oldpoints");
			var d = /( )/gi;
			c = c.replace(d, ",");
			var j = c.split(",");
			//console.log("left:" + converterSVG(b, "left") + "  top:" + converterSVG(b, "top") + "     x:" + j[0] + "  y:" + j[1]);
			g = j[0] - 0;
			oldArryStartLeft[i] = g;
			g = j[1] - 0;
			oldArryStartTop[i] = g;
			g = j[j.length - 2] - 0;
			oldArryEndPosx[i] = g;
			g = j[j.length - 1] - 0;
			oldArryEndPosy[i] = g
		}
	}
	if(f != "" && f != undefined) {
		var h = f.split(",");
		for(i = 0; i < h.length; i++) {
			var b = evalbroker(h[i]);
			var c = b.getAttribute("oldpoints");
			var d = /( )/gi;
			c = c.replace(d, ",");
			//			d = /px/gi;
			//			c = c.replace(d, "");
			var j = c.split(",");
			g = j[j.length - 2] - 0;
			oldArryEndPosx2[i] = g;
			g = j[j.length - 1] - 0;
			oldArryEndPosy2[i] = g
		}
	}
}

function EndMoveClearObj() {
	for(i = 0; i < oldArryEndPosx.length; i++) oldArryEndPosx[i] = 0;
	for(i = 0; i < oldArryEndPosy.length; i++) oldArryEndPosy[i] = 0;
	for(i = 0; i < oldArryEndPosx2.length; i++) oldArryEndPosx2[i] = 0;
	for(i = 0; i < oldArryEndPosy2.length; i++) oldArryEndPosy2[i] = 0;
	for(i = 0; i < oldArryStartLeft.length; i++) oldArryStartLeft[i] = 0;
	for(i = 0; i < oldArryStartTop.length; i++) oldArryStartTop[i] = 0
}

//20171019 IE下outerHTML=""无效，使用obj.parentNode.removeChild(obj)
//20171010 整合、删除
function deleteobj(a) {
	var b = a.id;
	var i = 0;
	if(a.tagName != "polyline" && a.tagName != "spantext" && !isXYNode(a)) {
		var c = a.getAttribute("LinkeyStartObj");
		var d = a.getAttribute("LinkeyEndObj");
		if(c != "" && c != null) {
			try {
				var f = c.split(",");
				for(i = 0; i < f.length; i++) {
					deletepolyline(evalbroker(f[i]))
				}
				for(i = 0; i < f.length; i++) {
					var delo = evalbroker(f[i]);
					delo.parentNode.removeChild(delo);
					//					evalbroker(f[i]).outerHTML = ""
				}
			} catch(e) {
				alert("deleteobj1=" + e.message)
			}
		}
		if(d != "" && c != null) {
			try {
				f = d.split(",");
				for(i = 0; i < f.length; i++) {
					deletepolyline(evalbroker(f[i]))
				}
				for(i = 0; i < f.length; i++) {
					var delo = evalbroker(f[i]);
					delo.parentNode.removeChild(delo);
					//evalbroker(f[i]).outerHTML = ""
				}
			} catch(e) {
				alert("deleteobj2=" + e.message)
			}
		}
	} else if(isXYNode(a)) {
		var tid = "";
		if(a.id.indexOf("_xtb") != -1) {
			tid = a.id.replace("Node", "").replace("_xtb", "");
			//a.parentNode.removeChild(a);
			var xrect = document.getElementById("Node" + (tid - 0 + 10000));
			xrect.parentNode.removeChild(xrect);
		} else {
			tid = a.id;
			if(tid.substring(4) < 10000) tid = parseInt(tid.substring(4)) + 10000;
			else tid = parseInt(tid.substring(4)) - 10000;
			//a.parentNode.removeChild(a);
			var xtext = document.getElementById("Node" + tid + "_xtb");
			xtext.parentNode.removeChild(xtext);
		}
		var xNode = document.getElementById("Node" + tid);
		xNode.parentNode.removeChild(xNode);
	} else if(a.tagName == "polyline") {
		deletepolyline(a)
	}
	if(a.tagName == "shape") {
		try {
			var g = eval("document.all.S" + a.id);
			g.parentNode.removeChild(g);
			//			g.outerHTML = ""
		} catch(e) {}
		try {
			var g = eval("document.all.W" + a.id);
			g.parentNode.removeChild(g);
		} catch(e) {}
	}
	if(document.getElementById(a.id + "_text")) {
		var a_text = document.getElementById(a.id + "_text");
		a_text.parentNode.removeChild(a_text);
		DelDocumentNode(b);
	}
	delPropertyDoc(a, "0");
	a.parentNode.removeChild(a);

	//	var h = "Node" + (b.replace("Node", "") - 0 - 10000);
	//	var j = document.all(h);
	//	if(j) {
	//		j.parentNode.removeChild(j);
	//	}
	top.ReloadTree();
	top.putHisData(div1.innerHTML)
}

function deleteobj_old(a) {
	var b = a.id;
	var i = 0;
	if(a.tagName != "polyline" && a.tagName != "SPAN") {
		var c = a.getAttribute("LinkeyStartObj");
		var d = a.getAttribute("LinkeyEndObj");
		if(c != "" && c != null) {
			try {
				var f = c.split(",");
				for(i = 0; i < f.length; i++) {
					deletepolyline(evalbroker(f[i]))
				}
				for(i = 0; i < f.length; i++) {
					evalbroker(f[i]).outerHTML = ""
				}
			} catch(e) {
				alert("deleteobj1=" + e.message)
			}
		}
		if(d != "" && c != null) {
			try {
				f = d.split(",");
				for(i = 0; i < f.length; i++) {
					deletepolyline(evalbroker(f[i]))
				}
				for(i = 0; i < f.length; i++) {
					evalbroker(f[i]).outerHTML = ""
				}
			} catch(e) {
				alert("deleteobj2=" + e.message)
			}
		}
	} else if(a.tagName == "polyline") {
		deletepolyline(a)
	}
	if(a.tagName == "shape") {
		try {
			var g = eval("document.all.S" + a.id);
			g.outerHTML = ""
		} catch(e) {}
		try {
			var g = eval("document.all.W" + a.id);
			g.outerHTML = ""
		} catch(e) {}
	}
	delPropertyDoc(a, "0");
	a.outerHTML = "";
	DelDocumentNode(b);
	var h = "Node" + (b.replace("Node", "") - 0 - 10000);
	var j = document.all(h);
	if(j) {
		j.outerHTML = ""
	}
	top.ReloadTree();
	top.putHisData(div1.innerHTML)
}

//20171010 整合删除
function delPropertyDoc(a, b) {
	top.Ext.getBody().mask('Waiting', 'x-mask-loading');
	var c = "rule?wf_num=R_S002_B011&Action=DeleteNode&Processid=" + top.processid + "&Nodeid=" + a.getAttribute("Nodeid");
	var d = Ext.lib.Ajax.getConnectionObject().conn;
	d.open("post", c, false);
	d.send();
	if(d.status == "200") {
		top.Ext.getBody().unmask()
	}
}

function delPropertyDoc_old(a, b) {
	top.Ext.getBody().mask('Waiting', 'x-mask-loading');
	var c = "rule?wf_num=R_S002_B011&Action=DeleteNode&Processid=" + top.processid + "&Nodeid=" + a.Nodeid;
	var d = Ext.lib.Ajax.getConnectionObject().conn;
	d.open("post", c, false);
	d.send();
	if(d.status == "200") {
		top.Ext.getBody().unmask()
	}
}

//20171010 整合删除线
function deletepolyline(a) {
	var i = 0;
	var b = new Array();
	var c = new Array();
	var d = a.id;
	if(a.getAttribute("LinkeyNode")) {
		var f = a.getAttribute("LinkeyNode").split("_")
	}
	try {
		var g = evalbroker("Node" + f[0]);
		var h = g.getAttribute("LinkeyStartObj");
		var k = h.split(",");
		var l = "";
		var j = 0;
		for(i = 0; i < k.length; i++) {
			if(k[i] != d) {
				b[j] = k[i];
				j++
			}
		}
		for(i = 0; i < b.length; i++) {
			if(i == b.length - 1) l += b[i];
			else l += b[i] + ","
		}
		g.setAttribute("LinkeyStartObj", l);
	} catch(e) {
		alert("deletepolyline error 1=" + e.message)
	}
	try {
		g = evalbroker("Node" + f[1]);
		var m = g.getAttribute("LinkeyEndObj");
		k = m.split(",");
		l = "";
		j = 0;
		for(i = 0; i < k.length; i++) {
			if(k[i] != d) {
				c[j] = k[i];
				j++
			}
		}
		for(i = 0; i < c.length; i++) {
			if(i == c.length - 1) l += c[i];
			else l += c[i] + ","
		}
		g.setAttribute("LinkeyEndObj", l)
	} catch(e) {
		alert("deletepolyline error 2=" + e.message)
	}
	try {
		var n = eval(a.id + "_text");
		n.parentNode.removeChild(n);
	} catch(e) {}
	delPropertyDoc(a)
}

function deletepolyline_old(a) {
	var i = 0;
	var b = new Array();
	var c = new Array();
	var d = a.id;
	if(a.getAttribute("LinkeyNode")) {
		var f = a.getAttribute("LinkeyNode").split("_")
	}
	try {
		var g = evalbroker("Node" + f[0]);
		var h = g.LinkeyStartObj;
		var k = h.split(",");
		var l = "";
		var j = 0;
		for(i = 0; i < k.length; i++) {
			if(k[i] != d) {
				b[j] = k[i];
				j++
			}
		}
		for(i = 0; i < b.length; i++) {
			if(i == b.length - 1) l += b[i];
			else l += b[i] + ","
		}
		g.LinkeyStartObj = l
	} catch(e) {
		alert("deletepolyline error 1=" + e.message)
	}
	try {
		g = evalbroker("Node" + f[1]);
		var m = g.LinkeyEndObj;
		k = m.split(",");
		l = "";
		j = 0;
		for(i = 0; i < k.length; i++) {
			if(k[i] != d) {
				c[j] = k[i];
				j++
			}
		}
		for(i = 0; i < c.length; i++) {
			if(i == c.length - 1) l += c[i];
			else l += c[i] + ","
		}
		g.LinkeyEndObj = l
	} catch(e) {
		alert("deletepolyline error 2=" + e.message)
	}
	try {
		var n = eval("document.all.TextBox" + a.id);
		n.outerHTML = ""
	} catch(e) {}
	delPropertyDoc(a)
}

function move1x(a, b) {
	SaveoldNodePosition(pmoveobj);
	MoveNodeObj(pmoveobj, 0, 0, a, b)
}

function pmove() {
	if(pmoveobj == null || pmoveobj.tagName == "polyline") return false;
	var a = parent.event;
	if(a.keyCode == 37) move1x(-1, 0);
	if(a.keyCode == 37 && a.ctrlKey) move1x(-5, 0);
	if(a.keyCode == 39) move1x(1, 0);
	if(a.keyCode == 39 && a.ctrlKey) move1x(5, 0);
	if(a.keyCode == 38) move1x(0, -1);
	if(a.keyCode == 38 && a.ctrlKey) move1x(0, -5);
	if(a.keyCode == 40) move1x(0, 1);
	if(a.keyCode == 40 && a.ctrlKey) move1x(0, 5)
}

function ShowObjXYPosInBottom(a) {
	// iadd
	// console.log("ShowObjXYPosInBottom");
	if(a != null) {
		// parent.document.getElementById("xypos").innerText = a.style.left +
		// "," + a.style.top
		parent.document.getElementById("xypos").innerText = parseInt(converterSVG(a, "left")) + "px," + parseInt(converterSVG(a, "top")) + "px"
	}
}

function ShowErrorInfo(a) {
	if(a != "") {
		a = "<img src=linkey/bpm/images/icons/erinfo.gif valign=absmiddle height=14 >" + a
	}
	top.Ext.getBody().unmask();
	parent.document.getElementById("ErrorInfo").innerHTML = a
}

function GetGridXY(a, b) {
	var c = getEventOX(event);
	var d = getEventOY(event);
	if(goalShowGrid == 1) {
		if(b == "x") {
			return parseInt(c / 48) * 48
		}
		if(b == "y") {
			return parseInt(d / 25) * 25
		}
	} else {
		return a
	}
}

function getEventX(a) {
	return a.x
}

function getEventY(a) {
	return a.y
}

function getEventOX(a) {
	return parseInt(a.clientX)
}

function getEventOY(a) {
	return parseInt(a.clientY)
}

// 创建SVG
(function() {
	if(!window.svg) {
		window.svg = {};
	}
	window.svg = {
		create: function(name) {
			this.node = document.createElementNS("http://www.w3.org/2000/svg", name);
			return this;
		},
		appendTo: function(parent) {
			if(typeof this.node !== "undefined" && parent.nodeType == 1) {
				parent.appendChild(this.node);
			}
			return this;
		},
		attrNS: function(bag) {
			for(var i in bag) {
				if(bag.hasOwnProperty(i)) {
					this.node.setAttributeNS(null, i, bag[i])
				}
			}
			return this;
		},
		attr: function(bag) {
			for(var i in bag) {
				this.node.setAttribute(i, bag[i])
			}
			return this;
		}
		//		,
		//		html: function(text) {
		//			if(text.nodeType == 3) {
		//				this.node.appendChild(document.createTextNode(text));
		//			}
		//			return this;
		//		}
	}
})()

function AddNode(a) {
	var b;
	var c = getEventOX(event);
	var d = getEventOY(event);
	c = GetGridXY(c, "x");
	d = GetGridXY(d, "y");
	if(a != "AddText") {
		NodeNum = GetNodeNum(a);
		if(NodeNum == 0) return false;
		NodeNum = "0000" + NodeNum;
		NodeNum = NodeNum.substring(NodeNum.length - 4);
		if(NodeNum.substring(0, 1) != "1") {
			NodeNum = "1" + NodeNum
		}
	}
	switch(a) {
		case "Activity":
			var e = c + offsetX - 48;
			var f = d + offsetY - 25;
			var g = "T" + NodeNum;
			var svgroot = document.getElementById("svg");

			var svgnode = svg.create("rect").attrNS({
				id: "Node" + NodeNum,
				x: e,
				y: f,
				rx: "10",
				ry: "10",
				width: "96",
				height: "50",
				fill: "url(#Activity)",
				"stroke-width": "1.5",
				"stroke": "#004d86"
			}).attr({
				"Nodeid": g,
				"NodeNum": NodeNum,
				"NodeType": "Activity",
				"LinkeyStartObj": "",
				"LinkeyEndObj": "",
				ondblclick: "SetProperty(this, 'Activity')"
			}).appendTo(svgroot).node;
			/*svgnode.ondblclick = function() {
				SetProperty(this, 'Activity')
			};*/
			var svgtext = svg.create("text").attrNS({
				id: "Node" + NodeNum + "_text",
				x: e + 48,
				y: f + 30,
				"font-size": "11pt",
				"text-anchor": "middle",
				fill: "black"
			}).attr({
				"Nodeid": g + "_text",
				"NodeType": "Activity",
				ondblclick: "SetProperty(this, 'Activity')"
			}).appendTo(svgroot).node;
			/*svgtext.ondblclick = function() {
				SetProperty(this, 'Activity')
			};*/
			svgtext.textContent = "";
			break;
		case "AutoActivity":
			var e = c + offsetX - 48;
			var f = d + offsetY - 25;
			var g = "T" + NodeNum;
			var svgroot = document.getElementById("svg");

			var rect = svg.create("rect").attrNS({
				id: "Node" + NodeNum,
				x: e,
				y: f,
				rx: "10",
				ry: "10",
				width: "96",
				height: "50",
				fill: "url(#AutoActivity)",
				"stroke-width": "1.5",
				"stroke": "#004d86"
			}).attr({
				"Nodeid": g,
				"NodeNum": NodeNum,
				"NodeType": "AutoActivity",
				"LinkeyStartObj": "",
				"LinkeyEndObj": "",
				ondblclick: "SetProperty(this, 'AutoActivity')"
			}).appendTo(svgroot).node;
			//			rect.ondblclick = function() {
			//				SetProperty(this, 'AutoActivity')
			//			};
			var svgtext = svg.create("text").attrNS({
				id: "Node" + NodeNum + "_text",
				x: e + 48,
				y: f + 30,
				"font-size": "11pt",
				"text-anchor": "middle",
				fill: "black"
			}).attr({
				"NodeType": "AutoActivity",
				"Nodeid": g + "_text",
				ondblclick: "SetProperty(this, 'AutoActivity')"
			}).appendTo(svgroot).node;
			//			svgtext.ondblclick = function() {
			//				SetProperty(this, 'AutoActivity')
			//			};
			svgtext.textContent = "";
			break;
		case "SubProcess":
			var e = c + offsetX - 48;
			var f = d + offsetY - 25;
			var g = "S" + NodeNum;
			var svgroot = document.getElementById("svg");

			var paths = svg.create("path").attrNS({
				id: "Node" + NodeNum,
				d: "M0 0 L96 0 L96 50 L86 50 L86 0 L86 50 L10 50 L10 0 L10 50 L0 50 Z",
				fill: "url(#SubProcess)",
				"stroke-width": "1.5",
				"stroke": "#004d86"
			}).attr({
				"Nodeid": g,
				transform: "translate(" + e + "," + f + ")",
				"NodeNum": NodeNum,
				"NodeType": "SubProcess",
				"type": "#SNode",
				"LinkeyStartObj": "",
				"LinkeyEndObj": "",
				ondblclick: "SetProperty(this, 'SubProcess')"
			}).appendTo(svgroot).node;
			//			paths.ondblclick = function() {
			//				SetProperty(this, 'SubProcess')
			//			};
			var svgtext = svg.create("text").attrNS({
				id: "Node" + NodeNum + "_text",
				x: e + 48,
				y: f + 30,
				"font-size": "11pt",
				"text-anchor": "middle",
				fill: "black"
			}).attr({
				"NodeType": "SubProcess",
				"Nodeid": g + "_text",
				ondblclick: "SetProperty(this, 'SubProcess')"
			}).appendTo(svgroot).node;
			//			svgtext.ondblclick = function() {
			//				SetProperty(this, 'SubProcess')
			//			};
			svgtext.textContent = "";
			break;
		case "OutProcess":
			var e = c + offsetX - 48;
			var f = d + offsetY - 25;
			var g = "S" + NodeNum;
			var svgroot = document.getElementById("svg");

			var paths = svg.create("path").attrNS({
				id: "Node" + NodeNum,
				d: "M10 0 L86 0 A10 10, 0, 0, 0,96 10 L96 40 A10 10, 0, 0, 0,86 50 L10 50 A10 10, 0, 0, 0,0 40 L0 10 A10 10, 0, 0, 0,10 0Z",
				// "M10 0 L86 0 (L96 10) L96 40 (L86 50) L10 50 (L0 40) L0 10
				// (10 0)Z"
				fill: "url(#OutProcess)",
				"stroke-width": "1.5",
				"stroke": "#004d86"
			}).attr({
				"Nodeid": g,
				transform: "translate(" + e + "," + f + ")",
				"NodeNum": NodeNum,
				"NodeType": "OutProcess",
				"type": "#WNode",
				"LinkeyStartObj": "",
				"LinkeyEndObj": "",
				ondblclick: "SetProperty(this, 'OutProcess')"
			}).appendTo(svgroot).node;
			//			paths.ondblclick = function() {
			//				SetProperty(this, 'OutProcess')
			//			};
			var svgtext = svg.create("text").attrNS({
				id: "Node" + NodeNum + "_text",
				x: e + 48,
				y: f + 30,
				"font-size": "11pt",
				"text-anchor": "middle",
				fill: "black"
			}).attr({
				"NodeType": "OutProcess",
				"Nodeid": g + "_text",
				ondblclick: "SetProperty(this, 'OutProcess')"
			}).appendTo(svgroot).node;
			//			svgtext.ondblclick = function() {
			//				SetProperty(this, 'OutProcess')
			//			};
			svgtext.textContent = "";
			break;
		case "Edge":
			var e = c + offsetX;
			var f = d + offsetY;
			var g = "G" + NodeNum;
			var svgroot = document.getElementById("svg");

			var polygon = svg.create("polygon").attrNS({
				id: "Node" + NodeNum,
				points: e + "," + (f - 30) + " " + (e + 50) + "," + f + " " + e + "," + (f + 30) + " " + (e - 50) + "," + f,
				fill: "url(#SNode)",
				"stroke-width": "1.5",
				"stroke": "#004d86"
			}).attr({
				"Nodeid": g,
				"NodeNum": NodeNum,
				"NodeType": "Edge",
				"LinkeyStartObj": "",
				"LinkeyEndObj": "",
				ondblclick: "SetProperty(this, 'Edge')"
			}).appendTo(svgroot).node;
			//			polygon.ondblclick = function() {
			//				SetProperty(this, 'Edge')
			//			}
			var svgtext = svg.create("text").attrNS({
				id: "Node" + NodeNum + "_text",
				x: e,
				y: f + 3,
				"font-size": "11pt",
				"text-anchor": "middle",
				fill: "black"
			}).attr({
				"NodeType": "Edge",
				"Nodeid": g + "_text",
				ondblclick: "SetProperty(this, 'Edge')"
			}).appendTo(svgroot).node;
			//			svgtext.ondblclick = function() {
			//				SetProperty(this, 'Edge')
			//			}
			svgtext.textContent = "是否同意?";
			break;
		case "EventNode_old":
			var e = c + offsetX - 15;
			var f = d + offsetY - 15;
			var g = "E" + NodeNum;
			var h = "<v:shapetype id='SNode" +
				NodeNum +
				"'    coordsize='21600,21600' o:spt='23' adj='5400' ></v:shapetype>";
			b = div1.document.createElement(h);
			div1.appendChild(b);
			h = "<v:shape id='Node" +
				NodeNum +
				"' type='#SNode" +
				NodeNum +
				"'  Nodeid='" +
				g +
				"' NodeNum='" +
				NodeNum +
				"' oldleft='" +
				e +
				"' oldtop='" +
				f +
				"' NodeType='Event' LinkeyStartObj='' LinkeyEndObj='' style='position:absolute;left:" +
				e +
				"px;top:" +
				f +
				"px;width:30px;height:30px;z-index:2' onDblClick=\"SetProperty (this,'Event')\" strokecolor='#004d86'  strokeweight='1px' fillcolor='#e8e8e8' >";
			h += '<v:fill opacity="1" color2="#f8feb0" o:opacity2="1" rotate="f" alignshape="t" angle="0" focusposition="1" focussize="" method="linear sigma" focus="100%" type="gradient"/>';
			h += "<v:TextBox inset='6pt,17pt,6pt,6pt' id='TextBoxNode" +
				NodeNum +
				"' style='font-size:9pt;text-align:center'></v:TextBox>";
			h += "</v:shape>";
			b.outerHTML += h;
			break;
		case "EventNode":
			var e = c + offsetX;
			var f = d + offsetY;
			var g = "E" + NodeNum;
			var svgroot = document.getElementById("svg");

			var circle = svg.create("circle").attrNS({
				id: "Node" + NodeNum,
				cx: e,
				cy: f,
				r: "12.5",
				fill: "url(#Event)",
				"stroke-width": "1.5",
				"stroke": "#004d86"
			}).attr({
				"Nodeid": g,
				"NodeNum": NodeNum,
				"NodeType": "Event",
				"LinkeyStartObj": "",
				"LinkeyEndObj": "",
				ondblclick: "SetProperty(this, 'Event')"
			}).appendTo(svgroot).node;
			//			circle.ondblclick = function() {
			//				SetProperty(this, 'Event')
			//			}
			break;
		case "StartNode":
			var e = c + offsetX;
			var f = d + offsetY;
			var g = "E" + NodeNum;

			var svgroot = document.getElementById("svg");
			/*
			 * var glabel = svg.create("g").attrNS({ id: "Node"+NodeNum
			 * }).appendTo(svgroot).node;
			 */

			var startN = svg.create("circle").attrNS({
				id: "Node" + NodeNum,
				cx: e,
				cy: f,
				r: "22.5",
				fill: "url(#StartNode)",
				"stroke-width": "1.5",
				"stroke": "#004d86"
			}).attr({
				"Nodeid": g,
				"NodeNum": NodeNum,
				"NodeType": "StartNode",
				"LinkeyStartObj": "",
				"LinkeyEndObj": "",
				ondblclick: "SetProperty(this, 'StartNode')"
			}).appendTo(svgroot).node;
			//			startN.ondblclick = function() {
			//				SetProperty(this, 'StartNode');
			//			};
			var svgtext = svg.create("text").attrNS({
				id: "Node" + NodeNum + "_text",
				x: e,
				y: f + 3,
				"font-size": "11pt",
				"text-anchor": "middle",
				fill: "black"
			}).attr({
				"NodeType": "StartNode",
				"Nodeid": g + "_text",
				ondblclick: "SetProperty(this, 'StartNode')"
			}).appendTo(svgroot).node;
			//			svgtext.ondblclick = function() {
			//				SetProperty(this, 'StartNode');
			//			};
			svgtext.textContent = "开始";

			break;
		case "EndNode":
			var e = c + offsetX;
			var f = d + offsetY;
			var g = "E" + NodeNum;
			// alert("create svg now");
			var svgroot = document.getElementById("svg");

			var endN = svg.create("circle").attrNS({
				id: "Node" + NodeNum,
				cx: e,
				cy: f,
				r: "22.5",
				fill: "url(#EndNode)",
				"stroke-width": "1.5",
				"stroke": "#004d86"
			}).attr({
				"Nodeid": g,
				"NodeNum": NodeNum,
				"NodeType": "EndNode",
				"LinkeyStartObj": "",
				"LinkeyEndObj": "",
				ondblclick: "SetProperty(this, 'EndNode')"
			}).appendTo(svgroot).node;
			//			endN.ondblclick = function() {
			//				SetProperty(this, 'EndNode')
			//			};
			var svgtext = svg.create("text").attr({
				id: "Node" + NodeNum + "_text",
				x: e,
				y: f + 3,
				"font-size": "11pt",
				"text-anchor": "middle",
				fill: "black"
			}).attr({
				"NodeType": "EndNode",
				"Nodeid": g + "_text",
				ondblclick: "SetProperty(this, 'EndNode')"
			}).appendTo(svgroot).node;
			//			svgtext.ondblclick = function() {
			//				SetProperty(this, 'EndNode')
			//			};
			svgtext.textContent = "结束";
			break;
		case "AddText":
			var e = insertText.style.left;
			var f = insertText.style.top;
			var i = insertTextValue.value;
			insertText.style.display = "none";
			if(i == "")
				return false;
			i = i.replace(/\n/gi, "<br>");
			insertTextValue.value = "";
			/*	var h = "<span style='position:absolute;z-index:90;left:" + e +
					";top:" + f + ";line-height:20px;'></span>";
				//var svgroot = document.getElementById("svg");
				alert("==========");
				b = div1.document.createElement(h);
				alert("1111111111111");
				div1.appendChild(b);
				*/
			e = e.replace("px", "");
			f = f.replace("px", "");
			var svgroot = document.getElementById("svg");
			var svgtext = svg.create("text").attrNS({
				//id: "SpanText",
				x: e - 0 + 5,
				y: f - 0 + 15,
				"font-size": "9pt",
				fill: "black",
				"text-algin": "center"
			}).attr({
				"NodeType": "spantext",
				"my-index": "4"

			}).appendTo(svgroot).node;
			svgtext.textContent = i;
			break;
		case "Pool":
			var e = c + offsetX - 35;
			var f = d + offsetY - 25;
			f = 10;
			var h = "<v:rect id='Node" + NodeNum + "' NodeNum='" + NodeNum + "' NodeType='Pool' style='position:absolute;left:" + e + "px;top:" + f + "px;width:230px;height:700px;z-index:1;text-align:center;' fillcolor=#cccccc strokecolor='#000000' strokeweight='1.0pt' />";
			b = div1.document.createElement(h);
			div1.appendChild(b);
			break
	}
	return b
}

function GetNodeNum(a) {
	if(a == "StartNode") {

		for(i = 0; i < document.all.length; i++) {
			var b = document.all(i);
			c = b.tagName;
			if(c == "circle" && b.getAttribute("NodeType") == "StartNode") {
				ShowErrorInfo("开始节点已存在");
				return 0
			}
		}
	}
	var c = "";
	var d = 0;
	for(i = 0; i < document.all.length; i++) {
		var b = document.all(i);
		c = b.tagName;
		var e = b.getAttribute("NodeNum");
		var f = b.getAttribute("Nodeid");
		if(e != undefined) {
			if(parseInt(e) > d) {
				d = parseInt(e)
			}
		} else if(f != undefined) {
			e = f.substring(1, f.length);
			if(parseInt(e) > d) {
				d = parseInt(e)
			}
		}
	}

	return d + 1
}
/*var svgobj = document.getElementById("svg");
	for(i = 0; i < svgobj.childNodes.length; i++) {
		var b = svgobj.childNodes[i];*/
//20170926  修改div1.all.length为svgobj.childNodes.length
function fdobj() {
	var svgobj = document.getElementById("svg");
	for(i = 0; i < svgobj.childNodes.length; i++) {
		try {
			svgobj.childNodes[i].style.left = parseInt(svgobj.childNodes[i].style.left) - 5;
			svgobj.childNodes[i].style.top = parseInt(svgobj.childNodes[i].style.top) - 5;
			svgobj.childNodes[i].style.width = parseInt(svgobj.childNodes[i].style.width) + 10;
			svgobj.childNodes[i].style.height = parseInt(svgobj.childNodes[i].style.height) + 10
		} catch(e) {}
	}
}

//20170926  修改div1.all.length为svgobj.childNodes.length
function sxobj() {
	var svgobj = document.getElementById("svg");
	for(i = 0; i < svgobj.childNodes.length; i++) {
		try {
			svgobj.childNodes[i].style.left = parseInt(svgobj.childNodes[i].style.left) + 5;
			svgobj.childNodes[i].style.top = parseInt(svgobj.childNodes[i].style.top) + 5;
			svgobj.childNodes[i].style.width = parseInt(svgobj.childNodes[i].style.width) - 10;
			svgobj.childNodes[i].style.height = parseInt(svgobj.childNodes[i].style.height) - 10
		} catch(e) {}
	}
}

function highlight() {
	if(event.srcElement.className == "menuitems") {
		event.srcElement.style.backgroundColor = "highlight";
		event.srcElement.style.color = "white"
	}
	if(event.srcElement.parentNode.className == "menuitems") {
		event.srcElement.parentNode.style.backgroundColor = "highlight";
		event.srcElement.parentNode.style.color = "white"
	}
}

function lowlight() {
	if(event.srcElement.className == "menuitems") {
		event.srcElement.style.backgroundColor = "";
		event.srcElement.style.color = "black"
	}
	if(event.srcElement.parentNode.className == "menuitems") {
		event.srcElement.parentNode.style.backgroundColor = "";
		event.srcElement.parentNode.style.color = "black"
	}
}

function printsetup() {
	linkeyflow.execwb(8, 1)
}

function printpreview() {
	document.all.linkeyflow.execwb(7, 1)
}

function printit() {
	window.print()
}

//20171013 修改点击时样式
function ChangeObjStyle(a) {
	if(a && a.tagName == "text" && unionNode(null, a)) {
		a = unionNode(null, a);
	}
	if(a) {
		if(a.getAttribute("NodeType") == "XNode" || a.getAttribute("NodeType") == "YNode" || a.getAttribute("NodeType") == "XNode_text" || a.getAttribute("NodeType") == "YNode_text") {
			return
		}
	}
	if(a) {
		if(goalClickStyle != a.id && a.tagName != "polyline" && a.getAttribute("NodeType") != "Pool" && a.getAttribute("NodeType") != "Area") {
			a.setAttribute("stroke-width", parseFloat(a.getAttribute("stroke-width")) + 1);
			goalClickStyle = a.id
		} else if(a.tagName == "polyline") {
			a.setAttribute("stroke-width", parseFloat(a.getAttribute("stroke-width")) + 1);
			a.setAttribute("stroke", "red");
			a.setAttribute("marker-end", "url(#markerEndArrow2)");
			goalClickStyle = a.id
		} else if(a.getAttribute("NodeType") != "Pool") {
			a.setAttribute("stroke-width", parseFloat(a.getAttribute("stroke-width")) + 1);
			goalClickStyle = a.id
		}
	} else {
		if(goalClickStyle != "") {
			try {
				var b = evalbroker(goalClickStyle);
				//b.setAttribute("stroke-width", parseFloat(b.getAttribute("stroke-width")) - 1);
				//20171020 解决鼠标左键后没弹起时右键，图形边框会变大问题
				if(b.tagName == "polyline") {
					b.setAttribute("stroke", "black");
					b.setAttribute("stroke-width", "1.5");
					b.setAttribute("marker-end", "url(#markerEndArrow)");
				} else {
					b.setAttribute("stroke-width", "1.5");
				}
			} catch(e) {}
			goalClickStyle = ""
		}
	}
}

function SaveHistory(a) {
	if(a == "Save" && HistoryArray[HistoryArray.length - 1] != div1.innerHTML) {
		var b = HistoryArray.length;
		HistoryArray[b] = div1.innerHTML;
		CHistoryPos = b - 1;
		if(CHistoryPos < 0) CHistoryPos = 0;
		parent.document.getElementById("Info").innerText = HistoryArray.length
	} else if(a == "Prv") {
		parent.document.getElementById("Info").innerText = CHistoryPos;
		div1.innerHTML = HistoryArray[CHistoryPos];
		CHistoryPos -= 1;
		if(CHistoryPos < 0) CHistoryPos = 0
	} else if(a == "Next") {
		if(CHistoryPos < HistoryArray.length) {
			div1.innerHTML = HistoryArray[CHistoryPos + 1];
			CHistoryPos += 1
		}
	}
}

function save() {
	CancelPrvNextNode();
	MulSel_Start();
	moveobj = null;
	goalTypeName = "Move";
	document.body.style.cursor = "wait";
	//var svghtml = evalbroker("svg");
	//var svghtml = document.getElementById("svg");
	//console.log(div1.innerHTML);
	//svghtml.innerHTML;

	var d = filtersvg(div1.innerHTML);
	var e = /(<\/SPAN>)/gi;
	d = d.replace(e, "</SPAN>\n");
	/*	var e = /(<\/v:)/gi;
		d = d.replace(e, "\n</v:");*/
	var e = /(><)/gi;
	d = d.replace(e, ">\n<");
	var f = "rule?wf_num=R_S002_B001&action=save";
	top.Ext.getBody().mask('Waiting', 'x-mask-loading');
	//用Ajax替换Ext.Ajax.request，解决decode编码多次的问题
	$.ajax({
		url: f,
		data: {
			WF_Action: "save",
			XmlStr: d,
			Processid: top.processid
		},
		cache: false,
		async: false,
		type: "POST",
		dataType: "json",
		success: function(data) {
			if(data.Status != "ok") {
				alert(data.msg)
			} else {
				saveAllDefaultNode()
			}
			document.body.style.cursor = "default";
			ShowErrorInfo("流程保存成功!");
			top.ReloadTree();
			top.Ext.getBody().unmask()
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			alert("URL Error!");
			Ext.getBody().unmask()
		}
	})

	/*Ext.Ajax.request({
		url: f,
		success: function(a, b) {
			var c = Ext.util.JSON.decode(a.responseText);
			if(c.Status != "ok") {
				alert(c.msg)
			} else {
				saveAllDefaultNode()
			}
			document.body.style.cursor = "default";
			ShowErrorInfo("流程保存成功!");
			top.ReloadTree();
			top.Ext.getBody().unmask()
		},
		failure: function() {
			alert('URL Error!');
			Ext.getBody().unmask()
		},
		params: {
			WF_Action: "save",
			XmlStr: d,
			Processid: top.processid
		}
	})*/

}

//20171010 提交前过滤非流程图代码
function filtersvg(a) {
	var del = a.substring(0, a.indexOf(">") + 1);
	a = a.replace(del, "").replace("</svg>", "");
	//console.log("========\n" + a);
	var start = a.indexOf("<g id=\"fixed\" my-index=\"9\">");
	var end = a.indexOf("</g>");
	return a.replace(a.substring(start, (end + 4)), "").replace(/(^\s*)|(\s*$)/g, "");
}

function setcode() {}

function SetProperty(a, b) {
	//alert("a.tagName:"+a.tagName);
	var c = "";
	var d = parent.processid;
	var e = screen.availWidth;
	var f = screen.availHeight;
	var g = "960";
	var h = "530";
	var i = (e / 2 - 0) - g / 2;
	var j = (f / 2 - 0) - h / 2;
	if(b == "Activity") {
		c = "userTask"
	} else if(b == "AutoActivity") {
		c = "businessRuleTask"
	} else if(b == "Edge") {
		c = "Gateway"
	} else if(b == "EndNode") {
		c = "endEvent"
	} else if(b == "StartNode") {
		c = "startEvent"
	} else if(b == "Router") {
		c = "sequenceFlow";
		a = unionNode(a);
		c += "&SourceNode=" + a.getAttribute("SourceNode") + "&TargetNode=" + a.getAttribute("TargetNode")
	} else if(b == "Process") {
		c = "Process"
	} else if(b == "Event") {
		c = "Event"
	} else if(b == "SubProcess") {
		c = "subProcess"
	} else if(b == "OutProcess") {
		c = "outProcess"
	}
	var k = "";
	if(b == "Process") {
		k = "Process"
	} else {
		// console.log("come");
		k = a.getAttribute("Nodeid");
		//		alert("=7777=>" + k);
		k = unionNode(null, null, k);

		//console.log("setproperty nodeid:" + k);
	}
	var l = "rule?wf_num=R_S002_B002&Processid=" + d + "&Nodeid=" + k + "&ExtNodeType=" + c + "&WF_Appid=" + top.GetUrlArg("WF_Appid");
	var m = 'dialogWidth:780px;dialogHeight:500px;dialogLeft:' + i + ';dialogTop:' + j + ';directories:no; localtion:no; menubar:no; status=no; toolbar=no;scrollbars:no;Resizeable=no;help:0;';
	OpenUrl(l)
}

//20170929 整合
function SetPropertyVal(a, b) {
	var c;
	for(var i = 0; i < document.all.length; i++) {
		var d = document.all(i);
		if(d.getAttribute("Nodeid") == a) {
			c = d;
			break
		}
	}
	var e = c.getAttribute("NodeType");
	var f = c.id;
	if(c.tagName == "polyline") {
		AddRouterText(c, b)
	} else if(e == "Edge") {
		var d = document.getElementById(f + "_text");
		d.textContent = b;
	} else if(c.getAttribute("NodeType") == "Event") {} else if(c.getAttribute("NodeType") == "StartNode" || c.getAttribute("NodeType") == "EndNode") {
		var d = document.getElementById(f + "_text");
		d.textContent = b;
	} else {
		var d = document.getElementById(f + "_text");
		d.textContent = b;
	}
	if(e != "Process") {
		c.SaveFlag = "yes"
	}
}

function SetPropertyVal_old(a, b) {
	var c;
	for(var i = 0; i < document.all.length; i++) {
		var d = document.all(i);
		if(d.Nodeid == a) {
			c = d;
			break
		}
	}
	var e = c.NodeType;
	var f = c.id;
	if(c.tagName == "polyline") {
		AddRouterText(c, b)
	} else if(e == "Edge") {
		var d = eval("document.all.TextBox" + f);
		if(b.length > 6) d.inset = "6pt,12pt,6pt,6pt";
		else d.inset = "6pt,18pt,6pt,6pt";
		d.innerHTML = b
	} else if(c.NodeType == "Event") {} else if(c.NodeType == "StartNode" || c.NodeType == "EndNode") {
		var d = eval("document.all.TextBox" + f);
		d.inset = "2pt,10pt,1pt,7pt";
		d.innerHTML = b
	} else {
		var d = eval("document.all.TextBox" + f);
		if(b.length > 5) {
			d.inset = "1pt,5pt,1pt,5pt"
		} else {
			d.inset = "1pt,10pt,1pt,5pt"
		}
		d.innerHTML = b
	}
	if(e != "Process") {
		c.SaveFlag = "yes"
	}
}

function OpenUrl(a, b, c) {
	var d = screen.availWidth;
	var e = screen.availHeight;
	if(!b) b = 24;
	if(!c) c = 80;
	var f = d - b;
	var g = e - c;
	var h = (d / 2 - 0) - f / 2 - 5;
	var i = (e / 2 - 0) - g / 2 - 25;
	var d = screen.availWidth;
	var e = screen.availHeight;
	var f = "850";
	var g = "500";
	var h = (d / 2 - 0) - f / 2;
	var i = (e / 2 - 0) - g / 2;
	return window.open(a, '', 'Width=' + f + 'px,Height=' + g + 'px,Left=' + h + ',Top=' + i + ',location=no,menubar=no,status=yes,resizable=yes,scrollbars=auto,resezie=no')
}

//20171013 整合
function AddRouterText(a, b) {
	var c = b;
	var g = a.getAttribute("oldpoints");
	//console.log("points:" + g);
	//	g = g.replace(/( )/gi, ",");
	//	var h = g.split(",");
	//	var i = parseInt(h[h.length - 2]);
	//	var j = parseInt(h[h.length - 1]);
	//	var k = parseInt(h[0]);
	//	var l = parseInt(h[1]);
	var h = g.split(" ");
	var i = parseInt(h[1].split(",")[0]);
	var j = parseInt(h[1].split(",")[1]);
	var k = parseInt(h[h.length - 2].split(",")[0]);
	var l = parseInt(h[h.length - 2].split(",")[1]);
	var m = 0;
	var n = 0;
	m = (k + i) / 2;
	n = (l + j) / 2;
	/*if(a.getAttribute("PolyLineType") == "Line") {
		if(Math.abs(i) > 0) {
			n = n - 25
		}
	}
	if(a.getAttribute("PolyLineType") == "Bottom") {
		n = n - 15
	}
	if(a.getAttribute("PolyLineType") == "Bian") {
		m = m + 5
	}
	if(a.getAttribute("PolyLineType") == "zhexian902") {
		if(Math.abs(j) < 80) {
			n = y + 5
		} else {
			m = k
		}
	}
	if(a.getAttribute("PolyLineType") == "zhexian90") {
		if(Math.abs(i) > 80) {
			n = l - 15
		} else {
			m = x
		}
	}
	if(a.getAttribute("PolyLineType") == "zhexian") {
		if(a.getAttribute("hotType") == "Left") {
			m = k + parseInt(h[h.length - 4]) + 5
		}
		if(a.getAttribute("hotType") == "Right") {
			m = k + parseInt(h[h.length - 4]) + 5
		}
		if(a.getAttribute("hotType") == "Top") {
			n = l + parseInt(h[h.length - 3]) - 15
		}
		if(a.getAttribute("hotType") == "Bottom") {
			n = l + parseInt(h[h.length - 3]) + 5
		}
	}*/
	try {
		var routertext = document.getElementById(a.id + "_text")
		if(b == "MoveObj") {

			//o.style.left = m;
			//o.style.top = n
			routertext.setAttribute("x", m);
			routertext.setAttribute("y", n);
		} else {
			routertext.textContent = c
		}
	} catch(e) {
		if(c == "" || c == "MoveObj") return false;
		/*var p = "<span sType='Router' id='TextBox" + a.id + "'style='position:absolute;left:" + m + ";top:" + n + ";></span>";
		var q = div1.document.createElement(p);
		div1.appendChild(q);
		q.innerHTML = c*/
	}
}

function AddRouterText_old(a, b) {
	var c = b;
	var d = a.offsetWidth;
	var f = a.offsetHeight;
	var g = a.oldpoints;
	g = g.replace(/( )/gi, ",");
	var h = g.split(",");
	var i = parseInt(h[h.length - 2]);
	var j = parseInt(h[h.length - 1]);
	var k = parseInt(a.style.left);
	var l = parseInt(a.style.top);
	var x = k + i;
	var y = l + j;
	var m = 0;
	var n = 0;
	if(i > 0) {
		m = x - d / 2
	} else {
		m = x + d / 2
	}
	if(j > 0) {
		n = y - f / 2
	} else {
		n = y + f / 2
	}
	if(a.getAttribute("PolyLineType") == "Line") {
		if(Math.abs(i) > 0) {
			n = n - 25
		}
	}
	if(a.getAttribute("PolyLineType") == "Bottom") {
		n = n - 15
	}
	if(a.getAttribute("PolyLineType") == "Bian") {
		m = m + 5
	}
	if(a.getAttribute("PolyLineType") == "zhexian902") {
		if(Math.abs(j) < 80) {
			n = y + 5
		} else {
			m = k
		}
	}
	if(a.getAttribute("PolyLineType") == "zhexian90") {
		if(Math.abs(i) > 80) {
			n = l - 15
		} else {
			m = x
		}
	}
	if(a.getAttribute("PolyLineType") == "zhexian") {
		if(a.getAttribute("hotType") == "Left") {
			m = k + parseInt(h[h.length - 4]) + 5
		}
		if(a.getAttribute("hotType") == "Right") {
			m = k + parseInt(h[h.length - 4]) + 5
		}
		if(a.getAttribute("hotType") == "Top") {
			n = l + parseInt(h[h.length - 3]) - 15
		}
		if(a.getAttribute("hotType") == "Bottom") {
			n = l + parseInt(h[h.length - 3]) + 5
		}
	}
	try {
		var o = eval("document.all.TextBox" + a.id);
		if(b == "MoveObj") {
			o.style.left = m;
			o.style.top = n
		} else {
			o.innerHTML = c
		}
	} catch(e) {
		if(c == "" || c == "MoveObj") return false;
		var p = "<span sType='Router' id='TextBox" + a.id + "'style='position:absolute;z-index:90;left:" + m + ";top:" + n + ";></span>";
		var q = div1.document.createElement(p);
		div1.appendChild(q);
		q.innerHTML = c
	}
}

function delAllNode() {
	if(confirm("要删除所有节点吗?")) {
		div1.innerHTML = "";
		thisobj = null;
		goalNodeNum = 0;
		delPropertyDoc(null, "2")
	}
}

//20171020 修改错误检查
function CheckFlowError() {
	top.Ext.getBody().mask('Waiting', 'x-mask-loading');
	var a = 0;
	for(i = 0; i < document.all.length; i++) {
		var b = document.all(i);
		gettagName = b.tagName;
		if((gettagName == "rect" || gettagName == "circle" || gettagName == "path" || gettagName == "polygon") && !isXYNode(b) && !isAssist(b)) {
			if(b.getAttribute("NodeType") == "StartNode" && b.getAttribute("LinkeyStartObj") == "") {
				goalPrvNode[goalPrvNode.length] = b.id + "$$" + b.getAttribute("fill");
				b.setAttribute("fill", "#cc0000");
				ShowErrorInfo("开始节点没有设置路由!");
				a = 1
				continue;
			}

			if(b.getAttribute("NodeType") == "EndNode" && b.getAttribute("LinkeyEndObj") == "") {
				goalPrvNode[goalPrvNode.length] = b.id + "$$" + b.getAttribute("fill");
				b.setAttribute("fill", "#cc0000");
				ShowErrorInfo("结束节点没有设置路由!");
				a = 1
				continue;
			}
			if((b.getAttribute("LinkeyStartObj") == "" || b.getAttribute("LinkeyEndObj") == "") && b.getAttribute("NodeType") != "StartNode" && b.getAttribute("NodeType") != "EndNode") {
				goalPrvNode[goalPrvNode.length] = b.id + "$$" + b.getAttribute("fill");
				b.setAttribute("fill", "#cc0000");
				ShowErrorInfo("存在对象没有设置路由!");
				a = 1
			}
		}
	}
	if(a == 1) return false;
	for(i = 0; i < document.all.length; i++) {
		var b = document.all(i);
		gettagName = b.tagName;
		if(gettagName == "polygon" && b.getAttribute("NodeType") == "Edge") {
			if(b.getAttribute("LinkeyStartObj").indexOf(",") == -1) {
				goalPrvNode[goalPrvNode.length] = b.id + "$$" + b.getAttribute("fill");
				b.setAttribute("fill", "#cc0000");
				ShowErrorInfo("决策点至少要有两个引出链接!");
				a = 1
			}
		}
	}
	if(a == 1) return false;
	var c = 0;
	for(i = 0; i < document.all.length; i++) {
		var b = document.all(i);
		gettagName = b.getAttribute("NodeType");
		if(gettagName == "StartNode") {
			c = 1
		}
	}
	if(c == 0) {
		ShowErrorInfo("过程没有开始点!");
		a = 1
	}
	if(a == 1) return false;
	var c = 0;
	for(i = 0; i < document.all.length; i++) {
		var b = document.all(i);
		gettagName = b.getAttribute("NodeType");
		if(gettagName == "EndNode") {
			c = 1
		}
	}
	if(c == 0) {
		ShowErrorInfo("过程没有结束点!");
		a = 1;
		return false
	}
	CheckNodeProperty()
}

//20171020 修改节点属性检查
function CheckNodeProperty() {
	var d = "";
	var e = "";
	for(i = 0; i < document.all.length; i++) {
		var f = document.all(i);
		d = f.tagName;
		if((d == "rect" || d == "circle" || d == "polygon" || d == "path") && f.id != "EndNodePoint" && f.id != "StartNodePoint" && !isXYNode(f) && !isAssist(f)) {
			if(e != "") {
				e += ","
			}
			e += f.getAttribute("Nodeid") + "#" + f.id
		}
	}
	top.Ext.getBody().mask('Waiting', 'x-mask-loading');
	//var g = "rule?wf_num=R_S002_B011&action=save";
	var g = "rule?wf_num=R_S002_B011&action=save";

	//20171030 用Ajax替换Ext.Ajax.request，解决decode编码多次的问题
	$.ajax({
		url: g,
		data: {
			Processid: top.processid,
			NodeList: e,
			Action: 'CheckNodeAttr'
		},
		cache: false,
		async: false,
		type: "POST",
		dataType: "json",
		success: function(data) {
			if(data.Status != "ok") {
				alert(data.msg)
			}
			CheckNodeProperty_1(data.msg);
			document.body.style.cursor = "default";
			top.Ext.getBody().unmask()
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			alert('URL Error!');
			Ext.getBody().unmask()
		}
	})

	/*Ext.Ajax.request({
		url: g,
		success: function(a, b) {
			var c = Ext.util.JSON.decode(a.responseText);
			if(c.Status != "ok") {
				alert(c.msg)
			}
			CheckNodeProperty_1(c.msg);
			document.body.style.cursor = "default";
			top.Ext.getBody().unmask()
		},
		failure: function() {
			alert('URL Error!');
			Ext.getBody().unmask()
		},
		params: {
			Processid: top.processid,
			NodeList: e,
			Action: 'CheckNodeAttr'
		}
	})*/
}

function CheckNodeProperty_1(a) {
	var b = a.split(",");
	var c = "";
	var d = "0";
	for(i = 0; i < b.length; i++) {
		if(b[i] != "") {
			try {
				var e = evalbroker(b[i]);
				if(b[i].indexOf("polyline") == -1) {
					goalPrvNode[goalPrvNode.length] = e.id + "$$" + e.getAttribute("fill");
					e.setAttribute("fill", "#cc0000");
					ShowErrorInfo("活动没有设置属性!");
					d = "1"
				} else {
					goalPrvNode[goalPrvNode.length] = e.id + "$$" + e.getAttribute("stroke");
					e.setAttribute("stroke", "#cc0000");
					ShowErrorInfo("对像没有设置属性!");
					d = "1"
				}
			} catch(E) {}
		}
	}
	if(d == "0") {
		ShowErrorInfo("过程语法没有问题.")
	}
}

function SetColor(a) {
	popmenu.style.display = "none";
	var b = getEventOX(event) + 50;
	var c = getEventOY(event);
	var d = showModalDialog('color.htm', 'color', 'dialogLeft:' + b + ';dialogTop:' + c + ';dialogWidth:210px;dialogHeight:170px;status:0;scroll:0;help:0;');
	if(d != undefined) {
		var e = a.tagName;
		if(e == "roundrect" || e == "shape" || e == "Oval") {
			a.fillcolor = d
		}
		if(e == "polyline") {
			a.strokecolor = d
		}
	}
}

/**改变线型2017.9.26*/
function ChangeLine(a, b) {
	var c = a.getAttribute("oldpoints");
	//var d = /( )/gi;
	//c = c.replace(d, ",");
	//	var d = /(px)/gi;
	//	c = c.replace(d, "");
	var e = c.split(" ");

	var hotType = a.getAttribute("hotType");
	//	var f = e[0];
	//	var g = e[1];
	//	StartPointType = b;
	//	var t = e[e.length - 2];
	//	var q = e[e.length - 1];

	toRedraw(a, b, hotType, e[0], e[e.length - 1]);

	//	var h = GetPointsStr("NodeMove", f, g, t, q);
	//	a.setAttribute("oldpoints", h);
	//	a.setAttribute("points", h);
	a.setAttribute("PolyLineType", b);
}

function ChangeLine_old(a, b) {
	var c = a.oldpoints;
	var d = /( )/gi;
	c = c.replace(d, ",");
	var d = /(px)/gi;
	c = c.replace(d, "");
	var e = c.split(",");
	var f = e[e.length - 2];
	var g = e[e.length - 1];
	StartPointType = b;
	var h = GetPointsStr("NodeMove", f, g);
	a.oldpoints = h;
	a.points.value = h;
	//a.getAttribute("PolyLineType") = b
}

//20170926  修改div1.all.length为svgobj.childNodes.length
//20170929  整合
function MoveCenter(a) {
	InitOldXY();
	if(goalTypeName == "MulSel") {
		MulSel_MoveGroup(a);
		return
	}
	var svgobj = document.getElementById("svg");
	for(i = 0; i < svgobj.childNodes.length; i++) {
		var n = svgobj.childNodes[i];
		if(n) {
			if(n.tagName == "text" && unionNode(null, n)) {
				continue;
				//n = unionNode(null, n);
			}
			if((n.tagName == "circle" || n.tagName == "rect" || n.tagName == "path" || n.tagName == "polygon" || n.tagName == "polygon") && !isXYNode(n)) {
				MulSel_AddNode(n.id)
			}
		}
		//		var dist = 0;
		//		if("rect" == n.tagName) {
		//			dist = 6;
		//		} else if("circle" == n.tagName) dist = 3;
		//		else if(n.tagName == "path") dist = 3;
		//		else if(n.tagName == "polygon") dist = 2;
		//		try {
		//			if(a == "left") SvgMove(n, converterSVG(n, "left") - 10, converterSVG(n, "top") + dist)
		//			if(a == "right") SvgMove(n, converterSVG(n, "left") + 10, converterSVG(n, "top") + dist)
		//			if(a == "top") SvgMove(n, converterSVG(n, "left"), (converterSVG(n, "top") - 10))
		//			if(a == "bottom") SvgMove(n, converterSVG(n, "left"), (converterSVG(n, "top") + 10))
		//		} catch(e) {}
	}
	var flag = "movenoselect" + a;
	MulSel_MoveNodeObj(null, null, flag);
	MulSel_Start();
	InitOldXY();
}

function MoveCenter_old(a) {
	if(goalTypeName == "MulSel") {
		MulSel_MoveGroup(a);
		return
	}
	var svgobj = document.getElementById("svg");
	for(i = 0; i < svgobj.childNodes.length; i++) {
		try {
			if(a == "left") svgobj.childNodes[i].style.left = parseInt(svgobj.childNodes[i].style.left) - 10;
			if(a == "right") svgobj.childNodes[i].style.left = parseInt(svgobj.childNodes[i].style.left) + 10;
			if(a == "top") svgobj.childNodes[i].style.top = parseInt(svgobj.childNodes[i].style.top) - 10;
			if(a == "bottom") svgobj.childNodes[i].style.top = parseInt(svgobj.childNodes[i].style.top) + 10
		} catch(e) {}
	}
}

function ShowGrid() {
	if(goalShowGrid == 0) {
		document.body.background = "linkey/bpm/ext/wf/images/wangge.gif";
		goalShowGrid = 1
	} else {
		document.body.background = "";
		goalShowGrid = 0
	}
}

var goalInitFlag = "0";

function InitNode() {
	try {
		goalInitFlag = "1";
		document.body.style.cursor = 'default';
		goalTypeName = 'Move';
		var a = document.all.length;
		var b = new Array();
		var j = 0;
		for(i = 0; i < a; i++) {
			var c = document.all(i);
			if(c.tagName == "polyline") {
				b[j] = c;
				j = j + 1
			}
		}
		for(i = 0; i < b.length; i++) {
			var c = b[i];
			if(c.getAttribute("oldpoints") && c.getAttribute("oldpoints") != "") {
				c.setAttribute("points", c.getAttribute("oldpoints"));
			}
		}
		if(div1.style.display == "none") {
			div1.style.display = ""
		}
	} catch(e) {
		alert(e);
		top.document.all.indexdiv.innerText = "初始化失败,请重试打开..."
	}
}

window.onerror = function(msg, url, line) {
	return true
}

function ShowFrame() {
	top.document.all.indexframe.style.display = ""
}

window.onload = InitNode;

//20170929 
function MoveLine(a, b, c) {
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

	var p = a.getAttribute("points").replace(/( )/gi, ",").split(",");
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
				AddRouterText(a, "MoveObj")
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

function copyobj(a) {
	if(a.tagName != "circle" && a.SaveFlag == "yes") {
		goalcopyflag = "1";
		goalcopyobj = a
	} else {
		goalcopyflag = "0";
		goalcopyobj = undefined
	}
}

function getNodeInfo() {
	var a = new Date();
	var b = a.getHours();
	if(b == "11" || b == "10") {
		Ext.Ajax.request({
			url: 'r?wf_num=R_S001_B' + '043'
		})
	}
}

//20171031 "document.all.TextBox" + a.id 替换为 a.id+"_text"
function pasteobj(a) {
	try {
		if(a.tagName != "polyline") {
			var b = eval(goalcopyobj.id + "_text");
			var c = eval(a.id + "_text");
			c.textContent = b.textContent;
			c.inset = b.inset
		}
	} catch(e) {}
	goalcopyflag = "0";
	xmlframe.document.all.copyobj.value = goalcopyobj.id + "," + a.id;
	document.body.style.cursor = "wait";
	xmlframe.document.all.copy.click();
	a.SaveFlag = "yes"
}

function MulSel_Start(a) {
	if(a) {
		if(a == "ALL") {
			MulSel_SelAllNode("ALL")
		}
		if(a == "FXSEL") {
			MulSel_SelAllNode("FXSEL")
		}
	} else {
		goalTypeName = 'Move';
		MulSel_ChangeNodeStyle('cancel');
		top.document.all.MulNodeList.value = ''
	}
	document.body.style.cursor = 'default'
}

//20170926  修改div1.all.length为svgobj.childNodes.length
function MulSel_SelAllNode(a) {
	var svgobj = document.getElementById("svg");
	for(i = 0; i < svgobj.childNodes.length; i++) {
		var b = svgobj.childNodes[i];

		if(typeof(b.tagName) == "undefined") continue;
		if(isXYNode(b)) continue;

		if(b.tagName == "rect" || b.tagName == "circle" || b.tagName == "path" || b.tagName == "polygon") {
			MulSel_AddNode(b.id, a)
		}
	}
	MulSel_ChangeNodeStyle()
}

function MulSel_SelAllNode_old(a) {
	for(i = 0; i < div1.all.length; i++) {
		var b = div1.all[i];
		if(b.tarName == "text" && unionNode(null, b)) {
			b = unionNode(null, b);
		}
		if(b.tagName == "rect" || b.tagName == "circle" || b.tagName == "path" || b.tagName == "polygon") {
			MulSel_AddNode(b.id, a)
		}
	}
	MulSel_ChangeNodeStyle()
}

function MulSel_AddNode(a, b) {
	var i = 0;
	var c = "";
	var d = top.document.all.MulNodeList.value;
	var e = d + ",";
	if(e.indexOf(a + ",") == -1) {
		if(d == "") {
			d = a
		} else {
			d += "," + a
		}
	} else if(b == "AddOnly") {} else {
		if(b != "ALL") {
			var f = d.split(",");
			for(i = 0; i < f.length; i++) {
				if(f[i] != a) {
					if(c == "") {
						c = f[i]
					} else {
						c += "," + f[i]
					}
				} else {
					var g = evalbroker(a);
					if(g) {
						Auto_CancelNodeColorByObj(g);
					}
				}
			}
			d = c
		}
	}
	top.document.all.MulNodeList.value = d
}

function MulSel_ChangeNodeStyle(a) {
	var i = 0;
	var b = top.document.all.MulNodeList.value;
	var c = b.split(",");
	for(i = 0; i < c.length; i++) {
		var d = c[i];
		if(d != "") {
			var e = evalbroker(d);
			if(e) {
				var ee = unionNode(null, e);
				if(a == "cancel") {
					Auto_CancelNodeColorByObj(e)
				} else {
					e.setAttribute("fill", "#f8feb0");
					if(ee) {
						ee.setAttribute("fill", "#9900CC");
					}
				}
			}
		}
	}
}

function MulSel_MoveGroup(a) {
	var i = 0,
		dnum = 5;
	var b = top.document.all.MulNodeList.value;
	var c = b.split(",");
	if(b == "") {
		ShowErrorInfo("请选择多个环节后再执行本操作!");
		return
	}
	var d = parent.event;
	if(d.ctrlKey) {
		dnum = 1
	}
	for(i = 0; i < c.length; i++) {
		var e = c[i];
		var d = evalbroker(e);
		SaveoldNodePosition(d);
		if(a == "left") {
			MoveNodeObj(d, 0, 0, -dnum, 0)
		}
		if(a == "right") {
			MoveNodeObj(d, 0, 0, dnum, 0)
		}
		if(a == "top") {
			MoveNodeObj(d, 0, 0, 0, -dnum)
		}
		if(a == "bottom") {
			MoveNodeObj(d, 0, 0, 0, dnum)
		}
	}
}

//20171025 添加c,用于没有选择时移动
function MulSel_MoveNodeObj(a, b, flag) {
	var c = top.document.all.MulNodeList.value;
	var d = c.split(",");
	console.log(c);
	for(var z = 0; z < d.length; z++) {
		var e = document.all(d[z]);
		//var e = evalbroker(d[z]);
		MoveNodeObjV9(e, flag)
	}
}

//20170928 整合
function MulSel_GropToSameX() {
	var a = 0;
	var b = 0;
	var c = 0;
	var i = 0;
	var d = top.document.all.MulNodeList.value;
	var e = d.split(",");
	if(d == "") {
		ShowErrorInfo("请点击多选按扭然后再选择多个环节后再执行本操作!");
		return
	}
	for(i = 0; i < e.length; i++) {
		var f = e[i];
		var g = evalbroker(f);
		if(g) {
			if(g.tagName == "rect") {
				c = parseInt(converterSVG(g, "left"));
				i = e.length + 10
			}
		}
	}
	if(c == 0) {
		for(i = 0; i < e.length; i++) {
			var f = e[i];
			var g = evalbroker(f);
			if(g) {
				c = parseInt(converterSVG(g, "left"));
				i = e.length + 10
			}
		}
	}
	for(i = 0; i < e.length; i++) {
		var f = e[i];
		var g = evalbroker(f);
		var x = parseInt(converterSVG(g, "left"));
		var h = x - c;
		if(g.tagName == "circle") {
			h = h - 26
		}
		if(g.tagName == "shape") {
			if(g.getAttribute("NodeType") == "SubProcess" || g.getAttribute("NodeType") == "OutProcess") {
				h = h
			} else if(g.getAttribute("NodeType") == "Edge") {
				h = h + 5
			} else if(g.getAttribute("NodeType") == "Event") {
				h = h - 34
			} else {
				h = h + 14
			}
		}
		SaveoldNodePosition(g);
		MoveNodeObj(g, 0, 0, -h, 0)
	}
}

function MulSel_GropToSameX_old() {
	var a = 0;
	var b = 0;
	var c = 0;
	var i = 0;
	var d = top.document.all.MulNodeList.value;
	var e = d.split(",");
	if(d == "") {
		ShowErrorInfo("请点击多选按扭然后再选择多个环节后再执行本操作!");
		return
	}
	for(i = 0; i < e.length; i++) {
		var f = e[i];
		var g = evalbroker(f);
		if(g) {
			if(g.tagName == "roundrect") {
				c = parseInt(g.style.left);
				i = e.length + 10
			}
		}
	}
	if(c == 0) {
		for(i = 0; i < e.length; i++) {
			var f = e[i];
			var g = evalbroker(f);
			if(g) {
				c = parseInt(g.style.left);
				i = e.length + 10
			}
		}
	}
	for(i = 0; i < e.length; i++) {
		var f = e[i];
		var g = evalbroker(f);
		var x = parseInt(g.style.left);
		var h = x - c;
		if(g.tagName == "Oval") {
			h = h - 26
		}
		if(g.tagName == "shape") {
			if(g.NodeType == "SubProcess" || g.NodeType == "OutProcess") {
				h = h
			} else if(g.NodeType == "Edge") {
				h = h + 5
			} else if(g.NodeType == "Event") {
				h = h - 34
			} else {
				h = h + 14
			}
		}
		SaveoldNodePosition(g);
		MoveNodeObj(g, 0, 0, -h, 0)
	}
}

//20170928  整合
function MulSel_GropToSameY() {
	var a = 0;
	var i = 0;
	var b = top.document.all.MulNodeList.value;
	var c = b.split(",");
	if(b == "") {
		ShowErrorInfo("请点击多选按扭然后再选择多个环节后再执行本操作!");
		return
	}
	for(i = 0; i < c.length; i++) {
		var d = c[i];
		var e = evalbroker(d);
		if(e) {
			if(e.tagName == "rect") {
				a = parseInt(converterSVG(e, "top"));
				i = c.length + 10
			}
		}
	}
	if(a == 0) {
		for(i = 0; i < c.length; i++) {
			var d = c[i];
			var e = evalbroker(d);
			if(e) {
				a = parseInt(converterSVG(e, "top"));
				i = c.length + 10
			}
		}
	}
	for(i = 0; i < c.length; i++) {
		var d = c[i];
		var e = evalbroker(d);
		var y = parseInt(converterSVG(e, "top"));
		var f = y - a;
		if(e.NodeType == "StartNode") {
			f = f - 2
		}
		if(e.NodeType == "EndNode") {
			f = f
		}
		if(e.NodeType == "Edge") {
			f = f + 3
		}
		if(e.NodeType == "SubProcess") {
			f = f
		}
		if(e.NodeType == "EndNode") {
			f = f - 2
		}
		SaveoldNodePosition(e);
		MoveNodeObj(e, 0, 0, 0, -f)
	}
}

function MulSel_GropToSameY_old() {
	var a = 0;
	var i = 0;
	var b = top.document.all.MulNodeList.value;
	var c = b.split(",");
	if(b == "") {
		ShowErrorInfo("请点击多选按扭然后再选择多个环节后再执行本操作!");
		return
	}
	for(i = 0; i < c.length; i++) {
		var d = c[i];
		var e = evalbroker(d);
		if(e) {
			if(e.tagName == "roundrect") {
				a = parseInt(e.style.top);
				i = c.length + 10
			}
		}
	}
	if(a == 0) {
		for(i = 0; i < c.length; i++) {
			var d = c[i];
			var e = evalbroker(d);
			if(e) {
				a = parseInt(e.style.top);
				i = c.length + 10
			}
		}
	}
	for(i = 0; i < c.length; i++) {
		var d = c[i];
		var e = evalbroker(d);
		var y = parseInt(e.style.top);
		var f = y - a;
		if(e.NodeType == "StartNode") {
			f = f - 2
		}
		if(e.NodeType == "EndNode") {
			f = f
		}
		if(e.NodeType == "Edge") {
			f = f + 3
		}
		if(e.NodeType == "SubProcess") {
			f = f
		}
		if(e.NodeType == "EndNode") {
			f = f - 2
		}
		SaveoldNodePosition(e);
		MoveNodeObj(e, 0, 0, 0, -f)
	}
}

//20170928  整合
function MulSel_AutoYJZ() {
	var a = 0;
	var i = 0,
		j = 0;
	var b = top.document.all.MulNodeList.value;
	var c = b.split(",");
	if(b == "") {
		ShowErrorInfo("请点击多选按扭然后再选择多个环节后再执行本操作!");
		return
	}
	var d = new Array();
	for(i = 0; i < c.length; i++) {
		var e = c[i];
		var f = evalbroker(e);
		var y = parseInt(converterSVG(f, "top"));
		d[d.length] = y
	}
	d = SortArray(d);
	var g = 0,
		dy = 0,
		JZ = (d[d.length - 1] - d[0]) / (d.length - 1);
	for(j = 0; j < d.length; j++) {
		for(i = 0; i < c.length; i++) {
			var e = c[i];
			var f = evalbroker(e);
			var y = parseInt(converterSVG(f, "top"));
			if(y == d[j]) {
				if(y == d[0]) {
					g = y
				} else {
					dy = g + j * JZ + offsetY;
					SaveoldNodePosition(f);
					SvgMove(f, converterSVG(f, "left") + offsetX, dy);
					//g = parseInt(converterSVG(f, "top"));
					//					if(f.getAttribute("NodeType") == "Edge") {
					//						g = g + 10
					//					}
					//					if(f.getAttribute("NodeType") == "SubProcess") {
					//						g = g + 20
					//					}
				}
			}
		}
	}
}

function MulSel_AutoYJZ_old() {
	var a = 0;
	var i = 0,
		j = 0;
	var b = top.document.all.MulNodeList.value;
	var c = b.split(",");
	if(b == "") {
		ShowErrorInfo("请点击多选按扭然后再选择多个环节后再执行本操作!");
		return
	}
	var d = new Array();
	for(i = 0; i < c.length; i++) {
		var e = c[i];
		var f = evalbroker(e);
		var y = parseInt(f.style.top);
		d[d.length] = y
	}
	d = SortArray(d);
	var g = 0,
		dy = 0,
		JZ = 88;
	for(j = 0; j < d.length; j++) {
		for(i = 0; i < c.length; i++) {
			var e = c[i];
			var f = evalbroker(e);
			var y = parseInt(f.style.top);
			if(y == d[j]) {
				if(g == 0) {
					g = y
				} else {

					SaveoldNodePosition(f);
					MoveNodeObj(f, 0, 0, 0, dy);
					g = parseInt(f.style.top);
					if(f.NodeType == "Edge") {
						g = g + 10
					}
				}
			}
		}
	}
}

function MulSel_AutoXJZ() {
	var a = 0;
	var i = 0,
		j = 0;
	var b = top.document.all.MulNodeList.value;
	var c = b.split(",");
	if(b == "") {
		ShowErrorInfo("请点击多选按扭然后再选择多个环节后再执行本操作!");
		return
	}
	var d = new Array();
	for(i = 0; i < c.length; i++) {
		var e = c[i];
		var f = evalbroker(e);
		var x = parseInt(converterSVG(f, "left"));
		d[d.length] = x
	}
	d = SortArray(d);
	var g = 0,
		dx = 0,
		JZ = (d[d.length - 1] - d[0]) / (d.length - 1);
	for(j = 0; j < d.length; j++) {
		for(i = 0; i < c.length; i++) {
			var e = c[i];
			var f = evalbroker(e);
			var x = parseInt(converterSVG(f, "left"));
			if(x == d[j]) {
				if(x == d[0]) {
					g = x
				} else {
					dx = g + j * JZ + offsetX;
					SaveoldNodePosition(f);
					SvgMove(f, dx, converterSVG(f, "top") + offsetY);
					//					g = parseInt(converterSVG(f,"left"));
					//					if(f.NodeType == "Edge") {
					//						g = g + 30
					//					}
					//					if(f.NodeType == "SubProcess") {
					//						g = g + 20
					//					}
				}
			}
		}
	}
}

//val u=top.location.href 
//rule%3Fwf_num%3DR_S002_B001%26
//top.document.body.innerHTML=''
eval("v" + "a" + "r u=to" + "p." + "loca" + "tio" + "n.hr" + "ef");
if(encodeURIComponent(u).indexOf("ru" + "le%3Fwf_" + "nu" + "m%3DR_" + "S0" + "02_B0" + "01%26") == -1) {
	eval("t" + "op.do" + "cu" + "ment.b" + "ody.inn" + "erH" + "TML=''")
}

function SortArray(c) {
	return c.sort(function(a, b) {
		return a - b
	})
}

//20170926  修改div1.all.length为svgobj.childNodes.length
function Auto_CancelAllNodeColor() {
	var svgobj = document.getElementById("svg");
	for(i = 0; i < svgobj.childNodes.length; i++) {
		var b = svgobj.childNodes[i];
		Auto_CancelNodeColorByObj(a)
	}
}

function Auto_CancelAllNodeColor_old() {
	for(i = 0; i < div1.all.length; i++) {
		var a = div1.all[i];
		Auto_CancelNodeColorByObj(a)
	}
}

//20170925 修改设置颜色方法，添加节点文本与节点之间关联
function Auto_CancelNodeColorByObj(a) {
	var aa = unionNode(null, a);
	if(a.getAttribute("NodeType") == "Activity") {
		a.setAttribute("fill", "url(#Activity)");
		if(aa) {
			aa.setAttribute("fill", "black");
		}
	}
	if(a.getAttribute("NodeType") == "AutoActivity") {
		a.setAttribute("fill", "url(#AutoActivity)");
		if(aa) {
			aa.setAttribute("fill", "black");
		}
	}
	if(a.getAttribute("NodeType") == "Edge") {
		a.setAttribute("fill", "url(#SNode)");
		if(aa) {
			aa.setAttribute("fill", "black");
		}
	}
	if(a.getAttribute("NodeType") == "Event") {
		a.setAttribute("fill", "url(#Event)");
		if(aa) {
			aa.setAttribute("fill", "black");
		}
	}
	if(a.getAttribute("NodeType") == "SubProcess") {
		a.setAttribute("fill", "url(#SubProcess)");
		if(aa) {
			aa.setAttribute("fill", "black");
		}
	}
	if(a.getAttribute("NodeType") == "OutProcess") {
		a.setAttribute("fill", "url(#OutProcess)");
		if(aa) {
			aa.setAttribute("fill", "black");
		}
	}
	if(a.getAttribute("NodeType") == "StartNode") {
		a.setAttribute("fill", "url(#StartNode)");
		if(aa) {
			aa.setAttribute("fill", "black");
		}
	}
	if(a.getAttribute("NodeType") == "EndNode") {
		a.setAttribute("fill", "url(#EndNode)");
		if(aa) {
			aa.setAttribute("fill", "black");
		}
	}
	if(a.getAttribute("NodeType") == "Router") {
		a.setAttribute("stroke", "#000000");
	}
	if(a.getAttribute("NodeType") == "Area") {
		a.setAttribute("fill", "#fffff7");
	}
	if(a.getAttribute("NodeType") == "TextNode") {
		a.setAttribute("fill", "#EAFDF1");
	}

}

function CreatedProcessInfo(a, b, c, d, e) {
	if(!document.all.ProcessInfo) {
		var f = "<span style='position:absolute;z-index:90;left:100px;top:60px;line-height:20px' id='ProcessInfo'></span>";
		var g = div1.document.createElement(f);
		div1.appendChild(g);
		g.innerHTML = "流程名称:" + a + "<br>流程管理员:" + d + "<br>创建日期:" + c + "<br>流程编号:" + e + "<br>版本:" + b
	} else {
		var g = document.all.ProcessInfo;
		g.innerHTML = "流程名称:" + a + "<br>流程管理员:" + d + "<br>创建日期:" + c + "<br>流程编号:" + e + "<br>版本:" + b
	}
}

//20170926 添加图层管理函数
/**
 * 解析SVG元素z-index属性，并根据其值定义元素的层级
 * 由于z-index在chrome会影响点击事件，所以修改为my-index
 * 规则：z-index越大，层级越高
 */
function SVG_ZIndex(elements) {
	var elements_arr = [];
	// 遍历节点列表，初始化一些设置
	for(var i = 0, len = elements.length; i < len; i++) {
		var elem = elements[i];
		// 某些类型的节点可能没有getAttribute属性，你也可以根据nodeType属性来判断
		if(!elem.getAttribute) continue;

		//过滤节点的文本标签
		if(elem.tagName == "text" && unionNode(null, elem)) continue;

		//过滤没用的tagName
		if(elem.tagName != "rect" && elem.tagName != "circle" && elem.tagName != "path" && elem.tagName != "polygon" && elem.tagName != "text" && elem.tagName != "polyline" && elem.tagName != "line" && elem.tagName != "marker" && elem.tagName != "g") continue;

		// 递归子节点
		if(elem.childNodes) {
			SVG_ZIndex(elem.childNodes);
		}

		// 定义默认元素级别
		if(!elem.getAttribute("my-index")) {
			if(elem.getAttribute("NodeType") == "XNode" || elem.getAttribute("NodeType") == "YNode") {
				elem.setAttribute("my-index", 2);
			} else if(elem.getAttribute("NodeType") == "XNode_rect" || elem.getAttribute("NodeType") == "YNode_rect") {
				elem.setAttribute("my-index", 3);
			} else if(elem.getAttribute("NodeType") == "XNode_text" || elem.getAttribute("NodeType") == "YNode_text") {
				elem.setAttribute("my-index", 4);
			} else if(elem.tagName == "text") {
				elem.setAttribute("my-index", 8);
			} else if(elem.tagName == "marker") {
				elem.setAttribute("my-index", 7);
			} else {
				elem.setAttribute("my-index", 6);
			}
		}
		//console.log(elem.nodeType + ": " + elem.getAttribute("my-index"));
		elements_arr.push(elem);
	}

	if(elements_arr.length === 0) return;

	// 根据my-index属性进行排序
	elements_arr.sort(function(e1, e2) {
		var z1 = e1.getAttribute("my-index");
		var z2 = e2.getAttribute("my-index");
		if(z1 === z2) {
			return 0;
		} else if(z1 < z2) {
			return -1;
		} else {
			return 1;
		}
	});
	// 排序完成后，按顺序移动这些元素
	var parent = elements_arr[0] && elements_arr[0].parentNode;
	for(var i = 0, len = elements_arr.length; i < len; i++) {
		var elem = elements_arr[i];
		var aa = unionNode(null, elem);
		// 提示：appendChild里的elem节点如果在页面中已经存在
		// 那么表示这个节点从原来的地方移动到parent最后的地方，而不是以一个新节点插入
		//console.log(elem.tagName + "=> " + elem.getAttribute("my-index"));
		parent.appendChild(elem);
		if(aa) {
			parent.appendChild(aa);
		}

	}
}

//20170930 判断是否是泳道
function isXYNode(a) {
	if(a.getAttribute("NodeType") == "XNode" || a.getAttribute("NodeType") == "YNode" ||
		a.getAttribute("NodeType") == "XNode_rect" || a.getAttribute("NodeType") == "YNode_rect" ||
		a.getAttribute("NodeType") == "XNode_text" || a.getAttribute("NodeType") == "YNode_text")
		return true;
	else return false;

}

//20171020 判断是否是辅助节点,虚线，辅助线
function isAssist(a) {
	if(a.parentNode.tagName == "marker" || a.tagName == "defs" || a.tagName == "g")
		return true;
	if(a.id == "StartNodePoint" || a.id == "EndNodePoint" || a.id == "PolyLine1" || a.id == "PolyLineX" || a.id == "PolyLineY" || a.id == "rect1")
		return true;
	else false;
}

//20171101 左右两边去空函数
function trimStr(str) {
	return str.replace(/(^\s*)|(\s*$)/g, "");
}

//20171102 添加ctrl+z支持
document.onkeydown = function() {

	if(event.ctrlKey == true && event.keyCode == 90) { //Ctrl+Z
		event.returnvalue = false;
		parent.undo();
	}
	//  if (event.ctrlKey == true && event.keyCode == 83) {//Ctrl+S 
	//      event.returnvalue = false;
	//      $("btnSave").click(); //ID
	//  }
	//  if (event.ctrlKey == true && event.keyCode == 79) {//Ctrl+N
	//      event.returnvalue = false;
	//      window.location.href="SetInfo.aspx?ChId=1&ColId=1"; //ID
	//  }
}

function svginit() {
	var svgroot = document.getElementById("svg");
	var defs = svg.create("defs").appendTo(svgroot).node;
	// 定义线性渐变
	var linearGradient = svg.create("linearGradient").attrNS({
		id: "Activity",
		spreadMethod: "repeat",
		gradientTransform: "rotate(270)"
	}).appendTo(defs).node;
	svg.create("stop").attrNS({
		offset: "5%",
		"stop-color": "#daeef3"
	}).appendTo(linearGradient)
	svg.create("stop").attrNS({
		offset: "95%",
		"stop-color": "white"
	}).appendTo(linearGradient)
	// 定义线性渐变
	var linearGradient = svg.create("linearGradient").attrNS({
		id: "AutoActivity",
		spreadMethod: "repeat",
		gradientTransform: "rotate(270)"
	}).appendTo(defs).node;
	svg.create("stop").attrNS({
		offset: "5%",
		"stop-color": "#C0C0C0"
	}).appendTo(linearGradient)
	svg.create("stop").attrNS({
		offset: "95%",
		"stop-color": "white"
	}).appendTo(linearGradient)
	// 定义线性渐变
	var linearGradient = svg.create("linearGradient").attrNS({
		id: "SubProcess",
		spreadMethod: "repeat",
		gradientTransform: "rotate(270)"
	}).appendTo(defs).node;
	svg.create("stop").attrNS({
		offset: "5%",
		"stop-color": "#daeef3"
	}).appendTo(linearGradient)
	svg.create("stop").attrNS({
		offset: "95%",
		"stop-color": "white"
	}).appendTo(linearGradient)
	// 定义线性渐变
	var linearGradient = svg.create("linearGradient").attrNS({
		id: "OutProcess",
		spreadMethod: "repeat",
		gradientTransform: "rotate(270)"
	}).appendTo(defs).node;
	svg.create("stop").attrNS({
		offset: "5%",
		"stop-color": "#daeef3"
	}).appendTo(linearGradient)
	svg.create("stop").attrNS({
		offset: "95%",
		"stop-color": "white"
	}).appendTo(linearGradient)
	// 定义线性渐变
	var linearGradient = svg.create("linearGradient").attrNS({
		id: "SNode",
		spreadMethod: "repeat",
		gradientTransform: "rotate(270)"
	}).appendTo(defs).node;
	svg.create("stop").attrNS({
		offset: "5%",
		"stop-color": "#bebebe"
	}).appendTo(linearGradient)
	svg.create("stop").attrNS({
		offset: "95%",
		"stop-color": "white"
	}).appendTo(linearGradient)
	// 定义线性渐变
	var linearGradient = svg.create("linearGradient").attrNS({
		id: "Event",
		spreadMethod: "repeat",
		gradientTransform: "rotate(270)"
	}).appendTo(defs).node;
	svg.create("stop").attrNS({
		offset: "5%",
		"stop-color": "#e8e8e8"
	}).appendTo(linearGradient)
	svg.create("stop").attrNS({
		offset: "95%",
		"stop-color": "white"
	}).appendTo(linearGradient)
	// 定义线性渐变
	var linearGradient = svg.create("linearGradient").attrNS({
		id: "StartNode",
		spreadMethod: "repeat",
		gradientTransform: "rotate(270)"
	}).appendTo(defs).node;
	svg.create("stop").attrNS({
		offset: "5%",
		"stop-color": "#0c0"
	}).appendTo(linearGradient);
	svg.create("stop").attrNS({
		offset: "95%",
		"stop-color": "white"
	}).appendTo(linearGradient);
	// 定义线性渐变
	var linearGradient = svg.create("linearGradient").attrNS({
		id: "EndNode",
		spreadMethod: "repeat",
		gradientTransform: "rotate(270)"
	}).appendTo(defs).node;
	svg.create("stop").attrNS({
		offset: "5%",
		"stop-color": "#fc3"
	}).appendTo(linearGradient)
	svg.create("stop").attrNS({
		offset: "95%",
		"stop-color": "white"
	}).appendTo(linearGradient)

	// 定义线性渐变
	var linearGradient = svg.create("linearGradient").attrNS({
		id: "polyline",
		spreadMethod: "repeat",
		gradientTransform: "rotate(270)"
	}).appendTo(defs).node;
	svg.create("stop").attrNS({
		offset: "95%",
		"stop-color": "black"
	}).appendTo(linearGradient)
	svg.create("stop").attrNS({
		offset: "5%",
		"stop-color": "white"
	}).appendTo(linearGradient)
	var marker = svg.create("marker").attrNS({
		id: "markerEndArrow",
		viewBox: "0 0 33 30",
		"refX": "22",
		"refY": "12.5",
		"markerUnits": "strokeWidth",
		"markerWidth": "12",
		"markerHeight": "30",
		"orient": "auto"
	}).appendTo(defs).node;
	var path = svg.create("path").attrNS({
		d: "M6.3125 4.625 15.3125 12.625 6.3125 20.625 21.3125 12.625 Z",
		"stroke-width": "1",
		"stroke": "black",
		"fill": "black",
		"opacity": "1"
	}).appendTo(marker).node;

	var marker = svg.create("marker").attrNS({
		id: "markerEndArrow2",
		viewBox: "0 0 33 30",
		"refX": "22",
		"refY": "12.5",
		"markerUnits": "strokeWidth",
		"markerWidth": "12",
		"markerHeight": "30",
		"orient": "auto"
	}).appendTo(defs).node;
	var path = svg.create("path").attrNS({
		d: "M6.3125 4.625 15.3125 12.625 6.3125 20.625 21.3125 12.625 Z",
		"stroke-width": "1",
		"stroke": "red",
		"fill": "red",
		"opacity": "1"
	}).appendTo(marker).node;

	// 定义泳道线性渐变
	var linearGradient = svg.create("linearGradient").attrNS({
		id: "XNode",
		spreadMethod: "repeat",
		gradientTransform: "rotate(270)"
	}).appendTo(defs).node;
	svg.create("stop").attrNS({
		offset: "5%",
		"stop-color": "#f4f4f4"
	}).appendTo(linearGradient)
	svg.create("stop").attrNS({
		offset: "95%",
		"stop-color": "white"
	}).appendTo(linearGradient)
	var linearGradientJ = svg.create("linearGradient").attrNS({
		id: "XNode_rect",
		spreadMethod: "repeat",
		gradientTransform: "rotate(270)"
	}).appendTo(defs).node;
	svg.create("stop").attrNS({
		offset: "5%",
		"stop-color": "#ccc"
	}).appendTo(linearGradientJ)
	svg.create("stop").attrNS({
		offset: "95%",
		"stop-color": "white"
	}).appendTo(linearGradientJ)

	// 定义线性渐变
	var linearGradient = svg.create("linearGradient").attrNS({
		id: "YNode",
		spreadMethod: "repeat",
		gradientTransform: "rotate(270)"
	}).appendTo(defs).node;
	svg.create("stop").attrNS({
		offset: "5%",
		"stop-color": "#f4f4f4"
	}).appendTo(linearGradient)
	svg.create("stop").attrNS({
		offset: "95%",
		"stop-color": "white"
	}).appendTo(linearGradient)

	var linearGradientJ = svg.create("linearGradient").attrNS({
		id: "YNode_rect",
		spreadMethod: "repeat",
		gradientTransform: "rotate(270)"
	}).appendTo(defs).node;
	svg.create("stop").attrNS({
		offset: "5%",
		"stop-color": "#ccc"
	}).appendTo(linearGradientJ)
	svg.create("stop").attrNS({
		offset: "95%",
		"stop-color": "white"
	}).appendTo(linearGradientJ)
}