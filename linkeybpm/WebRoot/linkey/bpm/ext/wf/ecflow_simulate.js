
//==========================================================
/*
 * 201805进行大幅度修改，解决流程设计器升级后仿真策略无法使用的问题
 */
//==========================================================
var goalClickStyle = "";
var thisobj;
var goalPrvNode = new Array();
var goalNextNode = new Array();
var folder = "",
	UIUserDbName = "";

div1.onmousedown = function() {
	CancelPrvNextNode();
	if(event.srcElement.parentNode.id == "svg" || event.srcElement.parentNode.parentNode.id == "svg") {
		if(event.srcElement.parentNode.id == "svg") {
			thisobj = event.srcElement
		} else {
			thisobj = event.srcElement.parentNode
		}
		ChangeObjStyle(thisobj)
	} else {
		thisobj = event.srcElement
	}
	
	//20180711 修改，添加文字节点转换
	if(thisobj && thisobj.tagName == "text" && unionNode(null, thisobj)) {
		thisobj = unionNode(null, thisobj);
	}
}

div1.onmouseup = function() {
	ChangeObjStyle()
}

function SetProperty(a, b) {}


//20180509 修改点击时样式
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

// 20180509 a根据文本返回节点，b为节点或节点对应文本,c为文本转节点id,nodeid
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
}

//20180509 解决读取旧的vml转换后，eval方法无法使用
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

//function ChangeObjStyle(a) {
//	if(a) {
//		if(goalClickStyle != a.id) {
//			a.strokeweight += 1;
//			a.style.zIndex = "1200";
//			goalClickStyle = a.id
//		}
//	} else {
//		if(goalClickStyle != "") {
//			try {
//				var b = document.getElementById(goalClickStyle);
//				b.strokeweight -= 1;
//				if(b.tagName == "polyline") {
//					b.style.zIndex = "1"
//				} else {
//					b.style.zIndex = "10"
//				}
//			} catch(e) {}
//			goalClickStyle = ""
//		}
//	}
//}

function ShowNextNode(a) {
	
	//20180509 添加
	if(a && a.tagName == "text" && unionNode(null, a)) {
		a = unionNode(null, a);
	}
	
	var b = a.getAttribute("LinkeyStartObj");
	if(b != "" && b != undefined) {
		var c = b.split(",");
		for(i = 0; i < c.length; i++) {
			var d = c[i].indexOf("_");
			var e = c[i].length;
			var f = c[i].substring(d + 1, e);
			var g = eval("Node" + f);
			//goalNextNode[goalNextNode.length] = "Node" + f + "$$" + g.fillcolor + "," + g.id + "$$" + g.firstChild.color2;
			goalNextNode[goalNextNode.length] = "Node" + f + "$$" + g.getAttribute("fill") + "," + g.id ;
			g.setAttribute("fill","#F9E26F");
			//g.fillcolor = "#f8feb0";
			//g.firstChild.color2 = "#fad01e"
		}
	}
}

function ShowPrvNode(a) {
	
	//20180509 添加
	if(a && a.tagName == "text" && unionNode(null, a)) {
		a = unionNode(null, a);
	}
	
	var b = a.getAttribute("LinkeyEndObj");
	if(b != "" && b != undefined) {
		var c = b.split(",");
		for(i = 0; i < c.length; i++) {
			var d = c[i].indexOf("polyline");
			var e = c[i].indexOf("_");
			var f = c[i].substring(d + 8, e);
			var g = eval("Node" + f);
			goalPrvNode[goalPrvNode.length] = "Node" + f + "$$" + g.getAttribute("fill") + "," + g.id;
			g.setAttribute("fill","#F9E26F");
			//g.firstChild.color2 = "#fad01e"
		}
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

//20180509 change
	function cancelNodeError(a) {
		f = a.split(",");
		for(j = 0; j < f.length; j++) {
			g = f[j].split("$$");
			var b = g[0];
			var c = eval(b);
			var d = g[1];
			if(c.tagName == "polyline") {
				c.setAttribute("stroke-width", d);
				//c.strokecolor = d
			} else {
				if(j == 0) {
					c.setAttribute("fill",d);
					//c.fillcolor = d
				} 
//				else {
//					c.firstChild.color2 = d
//				}
			}
		}
	}
	goalPrvNode = new Array();
	goalNextNode = new Array()
}
var SimProcessUnid = GetUrlArg("SimProcessUnid");
var Processid = GetUrlArg("Processid");
var DataDocUnid = GetUrlArg("DataDocUnid");
Ext.onReady(function() {
	svginit(); //20180514
//	Ext.get("svg").on('contextmenu', function(e) {  //change 20180514
	Ext.getBody().on('contextmenu',function(e) {
		var a = new Ext.menu.Menu();
		if(thisobj != null) {
			var b = thisobj.id;
			if(thisobj.getAttribute("nodetype") == "Activity") {
				a.add(new Ext.menu.Item({
					text: '指定处理用户',
					handler: function() {
						SetNodeUser(thisobj)
					}
				}));
				a.add(new Ext.menu.Item({
					text: '显示前导节点',
					handler: function() {
						ShowPrvNode(thisobj)
					}
				}));
				a.add(new Ext.menu.Item({
					text: '显示后继节点',
					handler: function() {
						ShowNextNode(thisobj)
					}
				}));
				a.add(new Ext.menu.Item({
					text: '取消设置',
					handler: function() {
						DelNodeUser(thisobj)
					}
				}))
			}
			if(thisobj.getAttribute("nodetype") == "SubProcess") {
				a.add(new Ext.menu.Item({
					text: '指定子流程仿真策略',
					handler: function() {
						SetSubProcess(thisobj)
					}
				}));
				a.add(new Ext.menu.Item({
					text: '显示前导节点',
					handler: function() {
						ShowPrvNode(thisobj)
					}
				}));
				a.add(new Ext.menu.Item({
					text: '显示后继节点',
					handler: function() {
						ShowNextNode(thisobj)
					}
				}));
				a.add(new Ext.menu.Item({
					text: '取消设置',
					handler: function() {
						DelNodeUser(thisobj)
					}
				}))
			}
			if(thisobj.getAttribute("nodetype") == "Edge" || thisobj.getAttribute("nodetype") == "StartNode" || thisobj.getAttribute("nodetype") == "EndNode" || thisobj.getAttribute("nodetype") == "AutoActivity") {
				a.add(new Ext.menu.Item({
					text: '显示前导节点',
					handler: function() {
						ShowPrvNode(thisobj)
					}
				}));
				a.add(new Ext.menu.Item({
					text: '显示后继节点',
					handler: function() {
						ShowNextNode(thisobj)
					}
				}))
			}
			if(thisobj.tagName == "polyline") {
				a.add(new Ext.menu.Item({
					text: '强制路由',
					handler: function() {
						SetRouter("2", thisobj)
					}
				}));
				a.add(new Ext.menu.Item({
					text: '禁止路由',
					handler: function() {
						SetRouter("3", thisobj)
					}
				}));
				a.add(new Ext.menu.Item({
					text: '取消设置',
					handler: function() {
						SetRouter("4", thisobj)
					}
				}))
			}
			if(thisobj.id == "svg") {
				a.add(new Ext.menu.Item({
					text: '自动运行',
					handler: function() {
						AutoRun(1)
					}
				}));
				a.add(new Ext.menu.Item({
					text: '单步运行',
					handler: function() {
						AutoRun(0)
					}
				}));
				a.add(new Ext.menu.Item({
					id: 'NextRunMenu',
					text: '继续运行',
					handler: function() {
						AutoRun(0)
					}
				}));
				a.add(new Ext.menu.Item({
					id: 'DelDataMenu',
					text: '清除运行数据',
					handler: function() {
						top.location.reload()
					}
				}));
				if(PauseDocUnid == "") {
					Ext.getCmp("NextRunMenu").setDisabled(true);
					Ext.getCmp("DelDataMenu").setDisabled(true)
				}
			}
			e.preventDefault();
			a.showAt(e.getXY())
		}
	});
	InitNode()
});

function SetSubProcess(a) {
	var b = "r?wf_num=F_S026_A005&Nodeid=" + a.getAttribute("nodeid") + "&Processid=" + Processid + "&SimProcessUnid=" + SimProcessUnid + "&DataDocUnid=" + DataDocUnid;
	OpenUrl(b, 800, 600)
}

function SetRouter(d, e) {
	Ext.getBody().mask('Waiting...', 'x-mask-loading');
	Ext.Ajax.request({
		url: 'r?wf_num=R_S026_B004',
		success: function(a, b) {
			var c = Ext.util.JSON.decode(a.responseText);
			if(c.Status == "ok") {
				if(d == "2") {
					e.setAttribute("stroke-width",1.2);
					//20180711 修改，除去线条填充了颜色
					if(e.tagName.toLowerCase() != "polyline"){
						e.setAttribute("fill","#FF0000");
					}
					//e.setAttribute("fill","#FF0000");
//					e.strokeweight = 1.2;
//					e.strokecolor = "#FF0000"
				} else if(d == "3") {
					e.setAttribute("stroke-width",1.2);
					//20180711 修改，除去线条填充了颜色
					if(e.tagName.toLowerCase() != "polyline"){
						e.setAttribute("fill","#CCCCCC");
					}
					//e.setAttribute("fill","#CCCCCC");
//					e.strokeweight = 1.2;
//					e.strokecolor = "#CCCCCC"
				} else if(d == "4") {
					e.setAttribute("stroke-width",1.0);
					e.setAttribute("fill","#000000");
//					e.strokeweight = 1.0;
//					e.strokecolor = "#000000"
				}
			} else {
				alert("设置失败!")
			}
			Ext.getBody().unmask()
		},
		failure: function() {
			alert('URL Error!')
		},
		params: {
			Nodeid: e.getAttribute("nodeid"),
			RouterRule: d,
			Processid: Processid,
			DataDocUnid: DataDocUnid,
			SimProcessUnid: SimProcessUnid
		}
	})
}

function SetNodeUser(a) {
	var b = "r?wf_num=F_S026_A004&Nodeid=" + a.getAttribute("nodeid") + "&Processid=" + Processid + "&SimProcessUnid=" + SimProcessUnid + "&DataDocUnid=" + DataDocUnid;
	var c = new Ext.Window({
		html: "<iframe src='" + b + "' frameborder=0 width=100% height=100% ></iframe>",
		width: 450,
		height: 150,
		autoScroll: true,
		closeAction: 'hide',
		shim: false,
		title: "指定审批用户",
		iconCls: 'subform',
		collapsible: true,
		maximizable: true
	});
	c.show()
}

function DelNodeUser(d) {
	Ext.getBody().mask('Waiting...', 'x-mask-loading');
	Ext.Ajax.request({
		url: 'r?wf_num=R_S026_B005',
		success: function(a, b) {
			var c = Ext.util.JSON.decode(a.responseText);
			alert("成功取消");
			Ext.getBody().unmask()
		},
		failure: function() {
			alert('URL Error!')
		},
		params: {
			Nodeid: d.getAttribute("nodeid"),
			SimProcessUnid: SimProcessUnid
		}
	})
}

function InitNode() {
	var g = document.all.length;
	var h = new Array();
	var j = 0;
	for(i = 0; i < g; i++) {
		var k = document.all(i);
		if(k.tagName == "polyline") {
			h[j] = k;
			j = j + 1
		}
	}
	for(i = 0; i < h.length; i++) {
		var k = h[i];
		if(k.getAttribute("oldpoints") != "") {
			k.setAttribute("points", k.getAttribute("oldpoints")); //20180510
		}
	}
	if(document.all.div1.style.display == "none") {
		setTimeout(function() {
			document.all.div1.style.display = ""
		}, 200)
	}
	Ext.Ajax.request({
		url: 'r?wf_num=R_S026_B006',
		success: function(a, b) {
			var c = Ext.util.JSON.decode(a.responseText);
			var d = c.Nodeid.split(",");
			for(var i = 0; i < d.length; i++) {
				var f = d[i];
				if(f.substring(0, 1) == "R") {
					SetRouterColor(f)
				} else {
					SetNodeColor(f)
				}
			}
			setTimeout(function() {
				try {
					Ext.get('loading').remove();
					Ext.get('loading-mask').fadeOut({
						remove: true
					})
				} catch(e) {}
			}, 100)
		},
		failure: function() {
			alert('URL Error!')
		},
		params: {
			SimProcessUnid: SimProcessUnid
		}
	})
}

function SetNodeColor(a) {
	if(a.indexOf("#") != -1) {
		var b = a.split("#");
		nodeid = b[0]
	}
//	for(var i = 0; i < document.all.length; i++) {
//		try {
//			var c = document.all(i);
//			if(c.Nodeid == nodeid) {
//				var d = parseInt(c.style.left) + 5;
//				var f = parseInt(c.style.top) + 5;
//				var g = "<v:rect id=\"User_" + a + "\" GroupId='" + c.id + "' style='position:absolute;left:" + d + ";text-align:left;margin-top:" + f + ";width:16px;height:16px;z-index:251658240' stroked=\"f\"></v:rect>";
//				var h = div1.document.createElement(g);
//				div1.appendChild(h);
//				h.innerHTML += '<v:fill src="linkey/bpm/images/icons/user.gif" o:title="user" recolor="t" rotate="t" type="frame"/>'
//			}
//		} catch(e) {}
//	}
}

function SetRouterColor(a) {
	var b = a.split("#");
	var c = b[0];
	var d = b[1];
	for(var i = 0; i < document.all.length; i++) {
		try {
			var f = document.all(i);
			if(f.getAttribute("nodeid") == c) {
				if(d == "1") {//20180510
					f.setAttribute("stroke-width",1.2);
					//20180711 修改，除去线条填充了颜色
					if(f.tagName.toLowerCase() != "polyline"){
						f.setAttribute("fill","#FF0000");
					}
					//f.setAttribute("fill","#FF0000");
//					f.strokeweight = 1.2;
//					f.strokecolor = "#FF0000"
				} else {
					f.setAttribute("stroke-width",1.2);
					//20180711 修改，除去线条填充了颜色
					if(f.tagName.toLowerCase() != "polyline"){
						f.setAttribute("fill","#CCCCCC");
					}
					//f.setAttribute("fill","#CCCCCC");
//					f.strokeweight = 1.2;
//					f.strokecolor = "#CCCCCC"
				}
			}
		} catch(e) {}
	}
}
var PauseProcessid = Processid;
var PauseDocUnid = "";

function AutoRun(d) {
	Ext.getBody().mask('Waiting...', 'x-mask-loading');
	Ext.Ajax.request({
		url: 'r?wf_num=R_S026_B007',
		method: 'GET',
		success: function(a, b) {
			Ext.getBody().unmask();
			var c = Ext.util.JSON.decode(a.responseText);
			if(c.Status == "Error") {
				alert(c.msg);
				return false
			}
			if(c.Status == "End") {
				cancelCurentNodeid();
				alert(c.msg);
				return false
			}
			try {
				if(c.Processid != "") PauseProcessid = c.Processid;
				if(c.DocUnid != "") {
					PauseDocUnid = c.DocUnid
				}
				if(c.EndNodeList != "") {
					SetEndNodeColor(c.EndNodeList)
				}
				if(c.CurrentNodeList != "") {
					SetCurrentNodeColor(c.CurrentNodeList)
				}
				if(d == 0) {
					if(confirm("成功运行至(" + c.msg + ")\n点击确定运行下一步!点击取消暂停运行!")) {
						AutoRun(0)
					}
				} else if(d == 1) {
					AutoRun(1)
				}
			} catch(e) {
				alert(e.message);
				alert("Error!(" + a.responseText + ")")
			}
		},
		failure: function() {
			alert('URL Error!')
		},
		params: {
			SimProcessUnid: SimProcessUnid,
			Processid: PauseProcessid,
			DocUnid: PauseDocUnid
		}
	})
}
var CurrentNodeColor1 = "#ffa6a6";
var CurrentNodeColor2 = "#ff0000";

function SetCurrentNodeColor(a) {
	if(a == "" || PauseProcessid != Processid) return false;
	var b = a.split(",");
	for(var x = 0; x < b.length; x++) {
		if(b[x].substring(0, 1) != "R") {
			for(var i = 0; i < document.all.length; i++) {
				var c = document.all(i);
				if(b[x] == c.getAttribute("nodeid")) {
					//c.fillcolor = CurrentNodeColor1;
					c.setAttribute("fill",CurrentNodeColor1);
					//c.firstChild.color2 = CurrentNodeColor2
				}
			}
		}
	}
}
var EndNodeColor1 = "#3f6";
var EndNodeColor2 = "#38e100";

function SetEndNodeColor(a) {
	if(a == "" || PauseProcessid != Processid) return false;
	var b = a.split(",");
	for(var x = 0; x < b.length; x++) {
		if(b[x].substring(0, 1) != "R") {
			for(var i = 0; i < document.all.length; i++) {
				var c = document.all(i);
				if(b[x] == c.getAttribute("nodeid")) {
					c.setAttribute("fill",EndNodeColor1)
					//c.fillcolor = EndNodeColor1;
					//c.firstChild.color2 = EndNodeColor2
				}
			}
		}
	}
}

function cancelCurentNodeid() {
	for(var i = 0; i < document.all.length; i++) {
		var a = document.all(i);
		if(a.getAttribute("fill") == CurrentNodeColor1) {
			a.setAttribute("fill",EndNodeColor1)
			//a.fillcolor = EndNodeColor1;
			//a.firstChild.color2 = EndNodeColor2
		}
	}
}

//20180509  解决保存后H5自动转换为小写问题
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


//20180514 svg图形初始化的描述
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