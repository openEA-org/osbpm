//==========================================================
/*
 * 201805进行大幅度修改，解决流程设计器升级后流程监控无法使用的问题
 * 
 */
//==========================================================

var EndNodeColor1 = "#3f6";
var EndNodeColor2 = "#38e100";
var CurrentNodeColor1 = "#ffa6a6";
var CurrentNodeColor2 = "#ff0000";
var OrNodeColor = "#92cddc";
var NoStartNodeColor1 = "#e7e7e7";
var NoStartNodeColor2 = "#d7d7d7";
var goalClickStyle = "";
var goalPrvNode = new Array();
var goalNextNode = new Array();
//function div1.onmousedown()
div1.onmousedown = function() {
	CancelPrvNextNode();
	//	if(event.srcElement.parentElement.id=="div1"||event.srcElement.parentElement.parentElement.id=="div1")
	//  {
	//      	if(event.srcElement.parentElement.id=="div1"){var obj=event.srcElement;}
	//      	else{var obj=event.srcElement.parentElement;}
	//      	ChangeObjStyle(obj);
	//  }
	if(event.srcElement.parentNode.id == "svg" || event.srcElement.parentNode.parentNode.id == "svg") {
		if(event.srcElement.parentNode.id == "svg") {
			var obj = event.srcElement;
		} else {
			var obj = event.srcElement.parentElement;
		}
		ChangeObjStyle(obj);
	}
}
//function div1.onmouseup()
div1.onmouseup = function() {
	ChangeObjStyle();
}

function SetProperty(a, b) {}

//function ChangeObjStyle(obj) {
//	if(obj) {
//		if(obj.NodeType == "YNode" || obj.NodeType == "XNode" || obj.NodeType == "YNode_text" || obj.NodeType == "XNode_text") {
//			goalClickStyle = "";
//			return;
//		}
//		if(goalClickStyle != obj.id) {
//			obj.strokeweight += 1;
//			obj.style.zIndex = "1200";
//			goalClickStyle = obj.id;
//		}
//	} else {
//		if(goalClickStyle != "") {
//			try {
//				var oldobj = document.getElementById(goalClickStyle);
//				oldobj.strokeweight -= 1;
//				if(oldobj.tagName == "polyline") {
//					oldobj.style.zIndex = "1";
//				} else {
//					oldobj.style.zIndex = "10";
//				}
//			} catch(e) {}
//			goalClickStyle = "";
//		}
//	}
//}

//20180515 修改点击时样式
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

function InitNode() {
	var AllObjNum = document.all.length;
	var ObjArray = new Array();
	var j = 0;
	for(i = 0; i < AllObjNum; i++) {
		var obj = document.all(i);
		if(obj.tagName == "polyline") {
			ObjArray[j] = obj;
			j = j + 1;
		}
	}
	for(i = 0; i < ObjArray.length; i++) {
		var obj = ObjArray[i];
		if(obj.getAttribute("oldpoints") != "") { //20180515
			//obj.points.value = obj.oldpoints;
			obj.setAttribute("points", obj.getAttribute("oldpoints"));
		}
	}
	if(document.all.div1.style.display == "none") {
		setTimeout(function() {
			document.all.div1.style.display = "";
		}, 200);
	}

	for(var i = 0; i < document.all.length; i++) {
		var obj = document.all(i);
		if(obj && obj.tagName == "text" && unionNode(null, obj)) { //20180515
			obj = unionNode(null, obj);
		}
		var gettagName = obj.tagName;
		if(obj.getAttribute("NodeType") == "StartNode") {
			//obj.fillcolor = EndNodeColor1;
			//20180711 修改，除去线条填充了颜色
			if(obj.tagName.toLowerCase() != "polyline"){
				obj.setAttribute("fill",EndNodeColor1);
			}
			//obj.setAttribute("fill", EndNodeColor1);
			//obj.firstChild.color2 = EndNodeColor2;
		}
		if(EndNodeList != "") {
			if(("," + EndNodeList + ",").indexOf("," + obj.getAttribute("Nodeid") + ",") != -1) {
				//20180711 修改，除去线条填充了颜色
				if(obj.tagName.toLowerCase() != "polyline"){
					obj.setAttribute("fill",EndNodeColor1);
				}
				//obj.setAttribute("fill", EndNodeColor1);
				//obj.fillcolor = EndNodeColor1;
				//obj.firstChild.color2 = EndNodeColor2;
			}
		}
		if(CurrentNodeid != "") {
			if(("," + CurrentNodeid + ",").indexOf("," + obj.getAttribute("Nodeid") + ",") != -1) {
				
				//20180711 修改，除去线条填充了颜色
				if(obj.tagName.toLowerCase() != "polyline"){
					obj.setAttribute("fill",CurrentNodeColor1);
				}
				//obj.setAttribute("fill", CurrentNodeColor1);
				obj.setAttribute("stroke-width", 1.5);
				//				obj.fillcolor = CurrentNodeColor1;
				//				obj.firstChild.color2 = CurrentNodeColor2;
				//				obj.strokeweight = 1.50;
			}
		}

		if((obj.getAttribute("NodeType") == "SubProcess" || obj.getAttribute("NodeType") == "Activity" || obj.getAttribute("NodeType") == "AutoActivity") && obj.getAttribute("fill") == OrNodeColor) {
			
			//20180711 修改，除去线条填充了颜色
			if(obj.tagName.toLowerCase() != "polyline"){
				obj.setAttribute("fill",CurrentNodeColor1);
			}
			//obj.setAttribute("fill", CurrentNodeColor1);
			//obj.fillcolor = NoStartNodeColor1;
			//obj.firstChild.color2 = NoStartNodeColor2;
		}
	}

}

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
			goalNextNode[goalNextNode.length] = "Node" + f + "$$" + g.getAttribute("fill") + "," + g.id;
			g.setAttribute("fill", "#f8feb0");
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
			g.setAttribute("fill", "#f8feb0");
			//g.firstChild.color2 = "#fad01e"
		}
	}
}

function CancelPrvNextNode() {
	var tempStr = "";
	var tempArray = "";
	var tempObj = "";
	var getObj = "";
	for(i = 0; i < goalPrvNode.length; i++) {
		cancelNodeError(goalPrvNode[i]);
	}
	for(i = 0; i < goalNextNode.length; i++) {
		cancelNodeError(goalNextNode[i]);
	}

	//20180515 修改
	function cancelNodeError(tempStr) {
		tempArray = tempStr.split(",");
		for(j = 0; j < tempArray.length; j++) {
			tempObj = tempArray[j].split("$$");
			var NodeName = tempObj[0];
			var NodeObj = eval(NodeName);
			var NodeColor = tempObj[1];
			if(NodeObj.tagName == "polyline") {
				//NodeObj.strokecolor = NodeColor;
				NodeObj.setAttribute("stroke-width", NodeColor);
			} else {
				if(j == 0) {
					//NodeObj.fillcolor = NodeColor;
					NodeObj.setAttribute("fill", NodeColor);
				}
				//				else {
				//					NodeObj.firstChild.color2 = NodeColor;
				//				}
			}
		}
	}
	goalPrvNode = new Array();
	goalNextNode = new Array();
}

var PrvNodeObj;
var CurNodeid = "";
var goalsNum = 0; //开始点
var EndNodeArray = new Array(); //已结束节点列表
function play() {
	goalsNum = 0;
	goalendNum = 1;
	setTimeout("PlayNode()", 1000);
}

function PlayTrace() //播放动画
{
	//获得所有已走过的节点id
	goalsNum = 0;
	if(EndNodeList == "") {
		return false;
	}
	for(i = 0; i < document.all.length; i++) {
		var obj = document.all(i);
		var gettagName = obj.tagName;
		if(gettagName == "circle" && obj.getAttribute("NodeType") == "StartNode") {
			EndNodeList = obj.getAttribute("Nodeid") + "," + EndNodeList;
		}
	}
	EndNodeArray = EndNodeList.split(",");
	CancelCurNodeid();
	PlayNode();
}

function CancelCurNodeid() {
	//取消当前节点的红色
	if(CurrentNodeid == "") return false;
	for(var i = 0; i < document.all.length; i++) {
		var obj = document.all(i);
		if(("," + CurrentNodeid + ",").indexOf("," + obj.getAttribute("Nodeid") + ",") != -1) {
			//20180711 修改，除去线条填充了颜色
			if(obj.tagName.toLowerCase() != "polyline"){
				obj.setAttribute("fill",EndNodeColor1);
			}
			//obj.setAttribute("fill", EndNodeColor1); //201805
			obj.setAttribute("stroke-width", 1.3);
			//obj.fillcolor = EndNodeColor1;
			//obj.firstChild.color2 = EndNodeColor2;
			//obj.strokeweight = 1.30;
		}
	}

}

function HightLightCurNodeid() {
	//标识活动的环节
	if(CurrentNodeid == "") return false;
	for(var i = 0; i < document.all.length; i++) {
		var obj = document.all(i);
		if(("," + CurrentNodeid + ",").indexOf("," + obj.getAttribute("Nodeid") + ",") != -1) {
			
			//20180711 修改，除去线条填充了颜色
			if(obj.tagName.toLowerCase() != "polyline"){
				obj.setAttribute("fill",CurrentNodeColor1);
			}
			//obj.setAttribute("fill", CurrentNodeColor1); //201805
			obj.setAttribute("stroke-width", 1.3);
			//			obj.fillcolor = CurrentNodeColor1;
			//			obj.firstChild.color2 = CurrentNodeColor2;
			//			obj.strokeweight = 1.30;
		}
	}
}

function PlayNode() {
	//取消前面节点的色彩
	if(PrvNodeObj) //节点
	{
		//PrvNodeObj.fillcolor = EndNodeColor1;
		//PrvNodeObj.firstChild.color2 = EndNodeColor2;
		//PrvNodeObj.strokeweight = 1.2;
		//20180711 修改，除去线条填充了颜色
		if(obj.tagName.toLowerCase() != "polyline"){
			obj.setAttribute("fill",EndNodeColor1);
		}
		//obj.setAttribute("fill", EndNodeColor1); //201805
		obj.setAttribute("stroke-width", 1.2);
		if(PrvNodeObj.tagName == "polyline") {
			obj.setAttribute("fill", black); //201805
			//obj.setAttribute("stroke-width",1.2);
			//			PrvNodeObj.strokecolor = "black";
			//			PrvNodeObj.strokeweight = ".9pt";
		}
	}
	//读取要播放的对像id
	if(goalsNum < EndNodeArray.length) {
		CurNodeid = EndNodeArray[goalsNum];
	} //开始点
	else {
		HightLightCurNodeid();
		return false;
	} //全部播放完成
	//开始播放
	try {
		//改变当前节点的色彩
		for(var i = 0; i < document.all.length; i++) {
			var obj = document.all(i);
			if(obj.getAttribute("Nodeid") == CurNodeid) {
				
				//20180711 修改，除去线条填充了颜色
				if(obj.tagName.toLowerCase() != "polyline"){
					obj.setAttribute("fill",CurrentNodeColor1);
				}
				//obj.setAttribute("fill", CurrentNodeColor1); //201805
				obj.setAttribute("stroke-width", 1.5);
				//				obj.fillcolor = CurrentNodeColor1;
				//				obj.firstChild.color2 = CurrentNodeColor2;
				//				obj.strokeweight = 1.50;
				PrvNodeObj = obj;
				if(obj.tagName == "polyline") {
					obj.setAttribute("fill", red);
					//obj.strokecolor = "red";
				}
			}
		}
	} catch(e) {
		alert(e.message);
	}
	goalsNum++;
	setTimeout("PlayNode()", 1000);
}

Ext.onReady(function() {
	var ProcessUNID = parent.processid;
	var ProcessInsNum = "";
	var DocUNID = parent.docUnid;
	//Ext.get("div1").on('contextmenu', function(e) { //change 20180515
	Ext.getBody().on('contextmenu', function(e) {
		if((event.srcElement.parentNode.id == "svg" || event.srcElement.parentNode.parentNode.id == "svg")) {
			var thisobj;
			if(event.srcElement.parentNode.id == "svg") {
				thisobj = event.srcElement;
			} else {
				thisobj = event.srcElement.parentElement;
			}
			
			//20180711 修改，添加文字节点转换
			if(thisobj && thisobj.tagName == "text" && unionNode(null, thisobj)) {
				thisobj = unionNode(null, thisobj);
			}
			
			var Nodeid = thisobj.getAttribute("Nodeid");
			if(Nodeid == undefined) {
				return;
			}
			var menu = new Ext.menu.Menu();
			if(thisobj.getAttribute("NodeType") == "Activity") {
				var url = "page?wf_num=P_S003_001&DocUnid=" + parent.docUnid + "&Nodeid=" + Nodeid;
				if(thisobj.getAttribute("fill") != OrNodeColor) {
					menu.add(new Ext.menu.Item({
						text: wflang.show_msg01,
						url: url,
						handler: ShowNodeRemark
					}));
				} else {
					menu.add(new Ext.menu.Item({
						text: wflang.show_msg01,
						disabled: true
					}))
				}
			} else {
				menu.add(new Ext.menu.Item({
					text: wflang.show_msg01,
					disabled: true
				}))
			}
			menu.add(new Ext.menu.Item({
				text: wflang.show_msg07,
				handler: function() {
					ShowPrvNode(thisobj)
				}
			}));
			menu.add(new Ext.menu.Item({
				text: wflang.show_msg08,
				handler: function() {
					ShowNextNode(thisobj)
				}
			}));
			if(thisobj.getAttribute("fill") == CurrentNodeColor1) {
				var insMenu = new Ext.menu.Item({
					text: wflang.show_msg02,
					menu: {
						items: []
					}
				});
				Ext.Ajax.request({
					url: 'rule?wf_num=R_S003_B047',
					method: 'POST',
					success: function(response, action) {
						var responseArray = Ext.util.JSON.decode(response.responseText);
						var ItemArray = responseArray.item.split(",");
						for(i = 0; i < ItemArray.length; i++) {
							var UserName = ItemArray[i];
							var insSubMenu = new Ext.menu.Item({
								text: UserName,
								icon: 'linkey/bpm/images/icons/user_green.gif'
							});
							insMenu.menu.add(insSubMenu);
						}
					},
					params: {
						Processid: ProcessUNID,
						DocUnid: DocUNID,
						Nodeid: Nodeid,
						Action: 'Current'
					}
				});
				menu.add(insMenu);
			}
			if(thisobj.getAttribute("fill") == CurrentNodeColor1 || thisobj.getAttribute("fill") == EndNodeColor1) {
				var EndUserMenu = new Ext.menu.Item({
					text: wflang.show_msg03,
					menu: {
						items: []
					}
				});
				Ext.Ajax.request({
					url: 'rule?wf_num=R_S003_B047',
					method: 'POST',
					success: function(response, action) {
						var responseArray = Ext.util.JSON.decode(response.responseText);
						var ItemArray = responseArray.item.split(",");
						for(i = 0; i < ItemArray.length; i++) {
							var UserName = ItemArray[i];
							if(UserName == "") UserName = wflang.show_msg04;
							var insSubMenu = new Ext.menu.Item({
								text: UserName,
								icon: 'linkey/bpm/images/icons/user_green.gif'
							});
							EndUserMenu.menu.add(insSubMenu);
						}
					},
					params: {
						Processid: ProcessUNID,
						DocUnid: DocUNID,
						Nodeid: Nodeid,
						Action: 'End'
					}
				});
				menu.add(EndUserMenu);
			}

			if(thisobj.getAttribute("NodeType") == "Activity") {
				if(thisobj.getAttribute("fill") == CurrentNodeColor1) {
					//活动的环节
					var url = "";
					menu.add(new Ext.menu.Item({
						text: wflang.monShow_msg01,
						Nodeid: thisobj.getAttribute("Nodeid"),
						handler: ShowApproveUser
					}));
					menu.add(new Ext.menu.Item({
						text: wflang.monShow_msg02,
						Nodeid: thisobj.getAttribute("Nodeid"),
						Action: 'End',
						handler: StartEndNode
					}));
				} else {
					//其他的环节
					menu.add(new Ext.menu.Item({
						text: wflang.monShow_msg03,
						Nodeid: thisobj.getAttribute("Nodeid"),
						Action: 'Start',
						handler: StartEndNode
					}));
				}
			}

			if(thisobj.getAttribute("NodeType") == "SubProcess") {
				if(thisobj.getAttribute("fill") != OrNodeColor) {
					var insDocMenu = new Ext.menu.Item({
						text: wflang.show_msg05,
						menu: {
							items: []
						}
					});
					var url = 'rule?wf_num=R_S003_B063';
					Ext.Ajax.request({
						url: url,
						method: 'POST',
						success: function(response, action) {
							var responseArray = Ext.util.JSON.decode(response.responseText);
							var ItemArray = responseArray.item.split(",");
							for(i = 0; i < ItemArray.length; i++) {
								var sArray = ItemArray[i].split("$");
								var Subject = sArray[0];
								var url = sArray[1];
								var insDocSubMenu = new Ext.menu.Item({
									text: Subject,
									icon: 'linkey/bpm/images/icons/doclist.gif',
									url: url,
									handler: ShowSubDoc
								});
								if(Subject == wflang.show_msg06) {
									insDocSubMenu.setDisabled(true);
								}
								insDocMenu.menu.add(insDocSubMenu);
							}
						},
						params: {
							Processid: ProcessUNID,
							DocUnid: DocUNID,
							Nodeid: Nodeid
						}
					});
				} else {
					var insDocMenu = new Ext.menu.Item({
						text: wflang.show_msg05,
						disabled: true
					});
				}
				menu.add(insDocMenu);
			}

			menu.add(new Ext.menu.Item({
				text: wflang.monShow_msg06,
				handler: function() {
					location.reload();
				}
			}));
			e.preventDefault();
			menu.showAt(e.getXY());
		}

	});
	InitNode();
	setTimeout(function() {
		try {
			parent.Ext.get('loading').remove();
			parent.Ext.get('loading-mask').fadeOut({
				remove: true
			});
		} catch(e) {}
	}, 300);
});

var subnum = 0;

function ShowSubFlow(item) {
	subnum += 10;
	var subwin = new Ext.Window({
		html: "<iframe src='" + item.url + "' frameborder=0 width=100% height=100% ></iframe>",
		width: document.body.clientWidth * 0.9,
		height: document.body.clientHeight * 0.9,
		autoScroll: true,
		closeAction: 'hide',
		shim: false,
		title: wflang.monShow_msg04 + item.ProcessName,
		iconCls: 'subwin',
		collapsible: true,
		maximizable: true
	});
	subwin.show();
}

function ShowForm(item) {
	subnum += 10;
	var subwin = new Ext.Window({
		html: "<iframe src='" + item.url + "' frameborder=0 width=100% height=100% ></iframe>",
		width: document.body.clientWidth * 0.9,
		height: document.body.clientHeight * 0.9,
		autoScroll: true,
		closeAction: 'hide',
		shim: false,
		title: wflang.monShow_msg05 + item.UserName,
		iconCls: 'subform',
		collapsible: true,
		maximizable: true
	});
	subwin.show();
}

function ShowNodeRemark(item) {
	OpenUrl(item.url, 300, 300);
}

function ShowSubDoc(item) {
	OpenUrl(item.url, 100, 100);
}

function ShowApproveUser(item) {
	var url = "form?wf_num=F_S014_A002&WF_Action=edit&Nodeid=" + item.Nodeid + "&WF_DocUnid=" + parent.docUnid;
	parent.$('#win').html("<iframe height='200px' width='100%' frameborder='0' src='" + url + "'></iframe>");
	parent.$('#win').window({
		width: 600,
		height: 260,
		modal: true,
		title: '用户管理'
	});
}

function StartEndNode(item) {
	var url = "rule?wf_num=R_S014_B001";
	Ext.Ajax.request({
		url: url,
		method: 'POST',
		success: function(response, action) {
			alert(response.responseText);
			location.reload();
		},
		params: {
			WF_Processid: parent.processid,
			WF_DocUnid: parent.docUnid,
			WF_Nodeid: item.Nodeid,
			WF_Action: item.Action
		}
	});
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

//20180713  解决保存后H5自动转换为小写问题
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