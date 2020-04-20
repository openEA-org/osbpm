/***********************************************************
	Copyright © 2004, Yusuf Wiryonoputro. All rights reserved.
************************************************************/

var oUtil=new webUtil();
function webUtil()
	{
	this.arrWebDrop=[];
	}

var oActiveBox;
function dropShow(oEl,box)
	{
	if(box.style.display=="block")
		{
		box.style.display="none";
		return;
		}
	hide();

	if(eval("document.all.inp"+this.oName).value!=eval("document.all.sel"+this.oName).value)
		{
		eval("document.all.sel"+this.oName).selectedIndex=-1
		}
	for(var i=0;i<eval("document.all.sel"+this.oName).options.length;i++)
		{
		if(eval("document.all.inp"+this.oName).value==eval("document.all.sel"+this.oName).options[i].innerHTML)
			{
			eval("document.all.sel"+this.oName).selectedIndex=i;
			}
		}
	
	var nTop=0;
	var nLeft=0;

	oElTmp=oEl;
	while(oElTmp.tagName!="BODY" )
		{
		if(oElTmp.style.top!="")
			nTop+=oElTmp.style.top.substring(1,oElTmp.style.top.length-2)*1;
		else nTop+=oElTmp.offsetTop;
		oElTmp = oElTmp.offsetParent;
		}

	oElTmp=oEl;
	while(oElTmp.tagName!="BODY" && oElTmp.tagName!="HTML")
		{
		if(oElTmp.style.left!="")
			nLeft+=oElTmp.style.left.substring(1,oElTmp.style.left.length-2)*1;
		else nLeft+=oElTmp.offsetLeft;
		oElTmp=oElTmp.offsetParent;
		}

	box.style.left=nLeft-this.width;
	box.style.top=nTop+this.height;
	
	box.style.display="block";
	
	oActiveBox=box;
	}
function hide()//independent
	{
	if(oActiveBox)oActiveBox.style.display="none";
	}

function webDrop(oName)
	{
	this.oName=oName;
	this.size;
	this.width=140;//120
	this.height=17;
	this.arrValue=[];
	this.arrCaption=[];	
	//this.onChange=function(){return true;};
	this.onChange=new Function("preview()");
	this.getValue=getValue;
	this.setValue=setValue;
	this.dropShow=dropShow;
	this.render=render;
	this.focus=focus;
	this.defaultValue=0;
	this.pickValue=pickValue;
	}
function getValue()
	{
	return eval("inp"+this.oName).value;
	}
function setValue(sValue)
	{
	eval("inp"+this.oName).value=sValue;
	}
function render()
	{
	if(this.arrValue.length>0&&this.arrCaption.length==0)
		this.arrCaption=this.arrValue;
	if(this.arrValue.length>=12)this.size=12;
	else this.size=this.arrValue.length;
	
	var s="";	
	s+="<table cellpadding=0 cellspacing=0><tr><td>"+
		"<input type=text onfocus=\"this.select()\" onkeyup=\""+this.oName+".onChange();\" onblur=\""+this.oName+".onChange();\" name=inp"+this.oName+" style='border:#d4d0c8 1px solid;padding-left:3px;width:"+this.width+";height:"+this.height+"' onclick=\"hide()\" value=\""+this.defaultValue+"\">"+
		"</td><td>"+
		"<input type=button style='cursor:default;border:none;background:url(dropbtn.gif);width:13;height:"+this.height+";' onclick=\""+this.oName+".dropShow(this,div"+this.oName+")\" onblur=\"hide();\" >"+
		"<div id=div"+this.oName+" style=\"display:none;position:absolute;\"><select name=sel"+this.oName+" multiple=multiple size="+this.size+" style='border:#000000 1px solid;width:"+(this.width+13)+";' onchange=\"hide();"+this.oName+".pickValue();"+this.oName+".onChange();\" >";
	for(var i=0;i<this.arrValue.length;i++)
		{
		s+="<option value=\""+this.arrValue[i]+"\">"+this.arrCaption[i]+"</option>";
		}
	s+="</select></div></td></tr></table>";
	document.write(s);
	
	oUtil.arrWebDrop.push(this.oName)
	}
function pickValue()
	{
	if(eval("document.all.sel"+this.oName).value=="<length>")
		{
		eval("inp"+this.oName).value=modalDialogShow("length.htm",186,123)
		}
	else if(eval("document.all.sel"+this.oName).value=="<percentage>")
		{
		eval("inp"+this.oName).value=modalDialogShow("percent.htm",186,123)
		}
	else if(eval("document.all.sel"+this.oName).value=="<color>")
		{
		if(eval("document.all.inp"+this.oName).name=="inpoColor")
			oColor1.show(eval("document.all.inp"+this.oName));
		if(eval("document.all.inp"+this.oName).name=="inpoBackgroundColor")
			oColor2.show(eval("document.all.inp"+this.oName));
		if(eval("document.all.inp"+this.oName).name=="inpoBorderColor")
			oColor3.show(eval("document.all.inp"+this.oName));
		if(eval("document.all.inp"+this.oName).name=="inpoBorderTopColor")
			oColor4.show(eval("document.all.inp"+this.oName));
		if(eval("document.all.inp"+this.oName).name=="inpoBorderBottomColor")
			oColor5.show(eval("document.all.inp"+this.oName));
		if(eval("document.all.inp"+this.oName).name=="inpoBorderLeftColor")
			oColor6.show(eval("document.all.inp"+this.oName));
		if(eval("document.all.inp"+this.oName).name=="inpoBorderRightColor")
			oColor7.show(eval("document.all.inp"+this.oName));
		}
	else if(eval("document.all.sel"+this.oName).value=="<url>")
		{
		eval("inp"+this.oName).value=modalDialogShow2("url.htm",250,120)
		}
	else 
		{
		eval("document.all.inp"+this.oName).value=eval("document.all.sel"+this.oName).value;
		}
	}
function modalDialogShow(url,width,height)
	{
	var vRetVal=window.showModalDialog(url,window,
		"dialogWidth:"+width+"px;dialogHeight:"+height+"px;edge:Raised;center:1;help:0;resizable:1;maximize:1");
	if(vRetVal)return vRetVal;
	else return "";
	}
function modalDialogShow2(url,width,height)
	{
	var vRetVal=window.showModalDialog(url,dialogArguments,
		"dialogWidth:"+width+"px;dialogHeight:"+height+"px;edge:Raised;center:1;help:0;resizable:1;maximize:1");
	if(vRetVal)return vRetVal;
	else return "";
	}
function focus()
	{
	eval("document.all.inp"+this.oName).focus();
	}