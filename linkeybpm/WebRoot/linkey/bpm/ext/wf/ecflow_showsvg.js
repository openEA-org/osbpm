
//==========================================================
/*
 * 201805进行大幅度修改，解决流程设计器升级后审批表单查看流程图无法使用的问题
 */
//==========================================================
var EndNodeColor1="#f8feb0";
var EndNodeColor2="#fad01e";
var EndNodeColor1="#3f6";
var EndNodeColor2="#38e100";
var CurrentNodeColor1="#ffa6a6";
var CurrentNodeColor2="#ff0000";
var OrNodeColor="#92cddc";
var NoStartNodeColor1="#e7e7e7";
var NoStartNodeColor2="#d7d7d7";
var goalClickStyle="";
var goalPrvNode=new Array();
var goalNextNode=new Array();

if ((typeof Range !== "undefined") && !Range.prototype.createContextualFragment) { 
Range.prototype.createContextualFragment = function(html) { 
var frag = document.createDocumentFragment(), div = document.createElement("div"); 
frag.appendChild(div); 
div.outerHTML = html; 
return frag; 
}; 
}

﻿var GoalStrokeWidth="";
function ClickChangeObjStyle(e,Action)
{
	//单击时改变环节的边框
	var eventobj = e.srcElement ? e.srcElement : e.target;
	var id=(eventobj.getAttribute("nodeid")!=null)?eventobj.getAttribute("nodeid"):eventobj.id;
	if(id==""){return;}
	var obj=document.getElementById(id);
	if(obj && id!="svg"){
		if(Action=="click"){
			GoalStrokeWidth=obj.getAttribute("stroke-width");
			obj.setAttribute("stroke-width","3");
		}else{
			if(GoalStrokeWidth=="3"){GoalStrokeWidth="1.5";}
			obj.setAttribute("stroke-width",GoalStrokeWidth);
		}
	}
}

function SetProperty (a,b){}
function InitNode()
{
	var allobj=document.getElementsByTagName("*");
	for (var i=0; i<allobj.length; i++) 
	{
		var obj=allobj[i];
		if(EndNodeList!=""){
			if(((","+EndNodeList+",").indexOf(","+obj.getAttribute("nodeid")+",")!=-1)||((","+EndNodeList+",").indexOf(","+obj.id+",")!=-1)){
				//20180709 修改，除去线条填充了颜色
				if(obj.tagName.toLowerCase()!="polyline"){
					obj.setAttribute("fill","#00ff00");
				}
			}
		}

		if(CurrentNodeid!=""){
			if(((","+CurrentNodeid+",").indexOf(","+obj.getAttribute("nodeid")+",")!=-1)||((","+CurrentNodeid+",").indexOf(","+obj.id+",")!=-1)){
				//20180709 修改，除去线条填充了颜色
				if(obj.tagName.toLowerCase()!="polyline"){
					obj.setAttribute("fill","#ff3300");
				}
			}
		}
	}
	
}

function CancelPrvNextNode()
{
	var tempStr="";
	var tempArray="";
	var tempObj="";
	var getObj="";
	for(i=0;i<goalPrvNode.length;i++)
	{
		cancelNodeError(goalPrvNode[i]);
	}
	for(i=0;i<goalNextNode.length;i++)
	{
		cancelNodeError(goalNextNode[i]);
	}
	function cancelNodeError(tempStr)
	{
		tempArray=tempStr.split(",");
		for(j=0;j<tempArray.length;j++)
		{
			tempObj=tempArray[j].split("$$");
			var NodeName=tempObj[0];
			var NodeObj=eval(NodeName);
			var NodeColor=tempObj[1];
			if(NodeObj.tagName=="polyline")
			{
//		        NodeObj.strokecolor=NodeColor;
				Nodeobj.setAttribute("stroke",NodeColor);
			}
			else
			{
				if(j==0){NodeObj.setAttribute("fill",NodeColor);}
			}
		}
	}
	goalPrvNode=new Array();
	goalNextNode=new Array();
}
  
  var PrvNodeObj;
  var CurNodeid="";
  var goalsNum=0; //开始点
  var EndNodeArray=new Array();//已结束节点列表
  function play(){goalsNum=0;goalendNum=1;setTimeout("PlayNode()",1000);}
  function PlayTrace()//播放动画
  {
	//获得所有已走过的节点id
	goalsNum=0;
	if(EndNodeList==""){return false;	}
	var allobj=document.getElementsByTagName("*");
	for (i=0; i<allobj; i++) 
	{
			var obj=allobj[i];
			if(obj.getAttribute("NodeType")=="StartNode" || obj.getAttribute("NodeType")=="EndNode")
			{
				EndNodeList=obj.getAttribute("nodeid")+","+EndNodeList;
			}
	}
	EndNodeArray=EndNodeList.split(",");
	CancelCurNodeid();
	PlayNode();
  }

  function CancelCurNodeid()
  {
		//取消当前节点的红色
		if(CurrentNodeid=="") return false;
		var allobj=document.getElementsByTagName("*");
		for (var i=0; i<allobj.length; i++) 
		{
			var obj=allobj[i];
			if(((","+CurrentNodeid+",").indexOf(","+obj.getAttribute("nodeid")+",")!=-1)||((","+CurrentNodeid+",").indexOf(","+obj.id+",")!=-1)){
				
				//20180709 修改，除去线条填充了颜色
				if(obj.tagName.toLowerCase()!="polyline"){
					obj.setAttribute("fill","#00ff00");
				}
			}
		}

  }
  
  function HightLightCurNodeid()
  {
		//标识活动的环节
		if(CurrentNodeid=="") return false;
		var allobj=document.getElementsByTagName("*");
		for (var i=0; i<allobj.length; i++) 
		{
			var obj=allobj[i];
			if(((","+CurrentNodeid+",").indexOf(","+obj.getAttribute("nodeid")+",")!=-1)||((","+CurrentNodeid+",").indexOf(","+obj.id+",")!=-1)){
				//20180709 修改，除去线条填充了颜色
				if(obj.tagName.toLowerCase()!="polyline"){
					obj.setAttribute("fill","#ff3300");
				}
				//obj.setAttribute("fill","#ff3300");
			}
		}
  }
  
  function PlayNode()
  {
  	//取消前面节点的色彩
  	if(PrvNodeObj)//节点
  	{
  	   //20180709 修改，除去线条填充了颜色
		if(PrvNodeObj.tagName.toLowerCase()!="polyline"){
			PrvNodeObj.setAttribute("fill","#00ff00");
		}
		
  	}
  	//读取要播放的对像id
  	if(goalsNum<EndNodeArray.length){CurNodeid=EndNodeArray[goalsNum];}//开始点
  	else{HightLightCurNodeid();return false;}//全部播放完成
  	//开始播放
  	try{
  		//改变当前节点的色彩
		var allobj=document.getElementsByTagName("*");
		for (var i=0; i<allobj.length; i++) 
		{
			var obj=allobj[i];
			if((obj.getAttribute("nodeid")==CurNodeid)||(obj.id==CurNodeid)){
				//20180709 修改，除去线条填充了颜色
				if(obj.tagName.toLowerCase()!="polyline"){
					obj.setAttribute("fill","#ff3300");
				}
				//obj.setAttribute("fill","#ff3300");
				PrvNodeObj=obj;
			}
		}
  	}catch(e){alert(e.message);}
  	goalsNum++;
  	setTimeout("PlayNode()",1000);
  }
  
  Ext.onReady(function()
{
	var ProcessUNID=parent.processid;
	var ProcessInsNum="";	
	var DocUNID=parent.docUnid;
	//Ext.get("svg").on('contextmenu',function(e)  //change 20180109
	Ext.getBody().on('contextmenu',function(e)
	{
		var thisobj = e.srcElement ? e.srcElement : e.target;
		if(thisobj.tagName=="rect"||thisobj.tagName=="path")
		{
			var menu = new Ext.menu.Menu();
			Ext.menu.initMenu(menu);
			var Nodeid=(thisobj.getAttribute("nodeid")!=null)?thisobj.getAttribute("nodeid"):thisobj.id;
			var fill=thisobj.getAttribute("fill");
			if(Nodeid==undefined){return;}
			if(thisobj.tagName=="rect")
			{
				var url="page?wf_num=P_S003_001&DocUnid="+parent.docUnid+"&Nodeid="+Nodeid;
				if(fill=="#00ff00" || fill=="#ff3300")
				{
					menu.add(new Ext.menu.Item({text: wflang.show_msg01,url:url,handler:ShowNodeRemark}));
				}else{menu.add(new Ext.menu.Item({text: wflang.show_msg01,disabled:true}))}
			}
			else{menu.add(new Ext.menu.Item({text: wflang.show_msg01,disabled:true}))}
			if(fill=="#ff3300")
			{
					if(thisobj.tagName!="path"){
						var insMenu=new Ext.menu.Item({text: wflang.show_msg02,menu: {items:[]}});
						Ext.Ajax.request({
						   url: 'rule?wf_num=R_S003_B047',
						   method:'POST',
						   success : function(response, action) 
								  {
								   var responseArray = Ext.util.JSON.decode(response.responseText);
								   var ItemArray=responseArray.item.split(",");
								   for(i=0;i<ItemArray.length;i++)
								   {
										var UserName=ItemArray[i];
										var insSubMenu=new Ext.menu.Item({text:UserName,icon:'linkey/bpm/images/icons/user_green.gif'});
										insMenu.menu.add(insSubMenu);
									}           
								  },
						   params: { Processid: ProcessUNID,DocUnid:DocUNID,Nodeid:Nodeid,Action:'Current'}
						});
						menu.add(insMenu);
					}
				}
				if((fill=="#00ff00" || fill=="#ff3300" ))
				{

					if(thisobj.tagName!="path"){
						var EndUserMenu=new Ext.menu.Item({text: wflang.show_msg03,menu: {items:[]}});
						Ext.Ajax.request({
						   url: 'rule?wf_num=R_S003_B047',
						   method:'POST',
						   success : function(response, action) 
								  {
								   var responseArray = Ext.util.JSON.decode(response.responseText);
								   var ItemArray=responseArray.item.split(",");
								   for(i=0;i<ItemArray.length;i++)
								   {
										var UserName=ItemArray[i];
										if(UserName=="") UserName=wflang.show_msg04;
										var insSubMenu=new Ext.menu.Item({text:UserName,icon:'linkey/bpm/images/icons/user_green.gif'});
										EndUserMenu.menu.add(insSubMenu);
									}           
								  },
						   params: {Processid: ProcessUNID,DocUnid:DocUNID,Nodeid:Nodeid,Action:'End'}
						});
						menu.add(EndUserMenu);
					}
				}
			
				if(thisobj.tagName=="path")
				{
					if(thisobj.fillcolor!=OrNodeColor)
					{
						var insDocMenu=new Ext.menu.Item({text: wflang.show_msg05,menu: {items:[]}});
						var url='rule?wf_num=R_S003_B063';
						Ext.Ajax.request({
						   url: url,
						   method:'GET',
						   success : function(response, action) 
								  {
								   var responseArray = Ext.util.JSON.decode(response.responseText);
								   var ItemArray=responseArray.item.split(",");
								   for(i=0;i<ItemArray.length;i++)
								   {
										var sArray=ItemArray[i].split("$");
										var Subject=sArray[0];
										var url=sArray[1];
										var insDocSubMenu=new Ext.menu.Item({text:Subject,icon:'linkey/bpm/images/icons/doclist.gif',url:url,handler: ShowSubDoc});
										if(Subject==wflang.show_msg06){insDocSubMenu.setDisabled(true);}
										insDocMenu.menu.add(insDocSubMenu);
									}           
								  },
						   params: { Processid: ProcessUNID,DocUnid:DocUNID,Nodeid:Nodeid,wf_appid:top.GetUrlArg("WF_Appid")}
						});
					}else{var insDocMenu=new Ext.menu.Item({text: wflang.show_msg05,disabled:true});}
					menu.add(insDocMenu);
				}
			
			e.preventDefault();
			menu.showAt(e.getXY());
		}
	});
	InitNode();
	setTimeout(function(){try{parent.Ext.get('loading').remove();Ext.fadeInit();parent.Ext.get('loading-mask').fadeOut({remove:true});}catch(e){}}, 150);
});

function ShowNodeRemark(item)
{
	OpenUrl(item.url,300,300);
}
function ShowSubDoc(item){OpenUrl(item.url,100,100);}


