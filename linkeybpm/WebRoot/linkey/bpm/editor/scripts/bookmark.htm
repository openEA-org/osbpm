<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="style/editor.css" rel="stylesheet" type="text/css">
<script>
	var sLangDir=dialogArguments.oUtil.langDir;
	document.write("<scr"+"ipt src='language/"+sLangDir+"/bookmark.js'></scr"+"ipt>");
</script>
<script>writeTitle()</script>
<script>
/*** COMMON ***/
function GetElement(oElement,sMatchTag)
	{
	while (oElement!=null&&oElement.tagName!=sMatchTag)
		{
		if(oElement.tagName=="BODY")return null;
		oElement=oElement.parentElement;
		}
	return oElement;
	}
/*** REALTIME + PREVIEW ***/
function doWindowFocus()
	{
	dialogArguments.oUtil.onSelectionChanged=new Function("realTime()");
	updateList();
	}
function bodyOnLoad()
	{	
	window.onfocus=doWindowFocus;	
	dialogArguments.oUtil.onSelectionChanged=new Function("realTime()");
	updateList();
	realTime();
	}
function updateList()
	{
	var oEditor=dialogArguments.oUtil.oEditor;
	
	while(lstBookmark.options.length!=0) 
		{
		lstBookmark.options.remove(lstBookmark.options(0))
		}
	for(var i=0;i<oEditor.document.anchors.length;i++)
		{
		var op = document.createElement("OPTION");
		op.text=oEditor.document.anchors[i].name;
		op.value=oEditor.document.anchors[i].name;
		lstBookmark.options.add(op);		
		}
	}
function selectBookmark(sName)
	{
	if(!dialogArguments.oUtil.obj.checkFocus()){return;}//Focus stuff
	var oEditor=dialogArguments.oUtil.oEditor;
	var oSel=oEditor.document.selection.createRange();
	var sType=oEditor.document.selection.type;

	oSel.execCommand("Unselect");
	
	for(var i=0;i<oEditor.document.anchors.length;i++)
		{
		if(oEditor.document.anchors[i].name==sName)
			{
			if(oSel.parentElement)
				{
				var oSelRange = oEditor.document.body.createTextRange()
				oSelRange.moveToElementText(oEditor.document.anchors[i])
				oSel.setEndPoint("StartToStart",oSelRange);
				oSel.setEndPoint("EndToEnd",oSelRange);
				oSel.select();
				}
			}
		}
	realTime();
	dialogArguments.realTime(dialogArguments.oUtil.oName);
	dialogArguments.oUtil.obj.selectElement(0);
	}
function realTime()
	{
	if(!dialogArguments.oUtil.obj.checkFocus())return;//Focus stuff
	var oEditor=dialogArguments.oUtil.oEditor;
	var oSel=oEditor.document.selection.createRange();
	var sType=oEditor.document.selection.type;

	//If text or control is selected, Get A element if any
	if (oSel.parentElement)	oEl=GetElement(oSel.parentElement(),"A");
	else oEl=GetElement(oSel.item(0),"A");

	if (oEl)
		{
		btnInsert.style.display="none";
		btnApply.style.display="block";
		btnOk.style.display="block";
		
		if(sType!="Control")
			{
			var oEditor=dialogArguments.oUtil.oEditor;
			var oSelRange = oEditor.document.body.createTextRange()
			oSelRange.moveToElementText(oEl)
			oSel.setEndPoint("StartToStart",oSelRange);
			oSel.setEndPoint("EndToEnd",oSelRange);
			oSel.select();
			}		
		
		if(oEl.NAME)inpName.value=oEl.NAME;	
		if(oEl.name)inpName.value=oEl.name;	
		}
	else
		{
		btnInsert.style.display="block";
		btnApply.style.display="none";
		btnOk.style.display="none";

		inpName.value="";	
		}			
	}

function doApply()
	{
	if(!dialogArguments.oUtil.obj.checkFocus())return;//Focus stuff
	var oEditor=dialogArguments.oUtil.oEditor;
	var oSel=oEditor.document.selection.createRange();
	var sType=oEditor.document.selection.type;
	
	dialogArguments.oUtil.obj.saveForUndo();

	if(inpName.value!="")
		oSel.execCommand("CreateBookmark",false,inpName.value);
	else
		oSel.execCommand("UnBookmark");
	
	updateList();
	realTime();
	
	//~~~~~~~~ Focus stuff
	var oEditor=dialogArguments.oUtil.oEditor;
	oEditor.focus()
	
	//*** RUNTIME STYLES ***
	var obj=dialogArguments.oUtil.obj;
	obj.runtimeStyles();
	//***********************	
	
	dialogArguments.realTime(dialogArguments.oUtil.oName);
	dialogArguments.oUtil.obj.selectElement(0);
	}				
</script>
</head>
<body onload="loadTxt();bodyOnLoad()" style="overflow:hidden;">

<table width=100% height=100% align=center cellpadding=0 cellspacing=0>
<tr>
<td valign=top style="padding:5;height:100%">

	<table width=100% border="0">
	<tr>
	<td width="100%">
	<select size=5 name="lstBookmark" style="width:100%" id="lstBookmark" onchange="selectBookmark(this.value);selectBookmark(this.value)" class="inpSel">
	</select>
	<td>
	</tr>
	</table><br>
	
	<table width=100%>
	<tr>
		<td nowrap><span id="txtLang" name="txtLang">Name</span>:</td>
		<td style="width:100%;padding-right:5"><INPUT type="text" ID="inpName" NAME="inpName" style="width:100%" class="inpTxt"></td>
	</tr>
	</table>

</td>
</tr>
<tr>
<td class="dialogFooter" style="padding:6;" align="right">
	<table cellpadding=1 cellspacing=0>
	<tr>
	<td>
	<input type=button name=btnCancel id=btnCancel value="cancel" onclick="self.close()" class="inpBtn" onmouseover="this.className='inpBtnOver';" onmouseout="this.className='inpBtnOut'">
	</td>
	<td>
	<input type=button name=btnInsert id=btnInsert value="insert" onclick="doApply()" class="inpBtn" onmouseover="this.className='inpBtnOver';" onmouseout="this.className='inpBtnOut'">
	</td>
	<td>
	<input type=button name=btnApply id=btnApply value="apply" style="display:none" onclick="doApply()" class="inpBtn" onmouseover="this.className='inpBtnOver';" onmouseout="this.className='inpBtnOut'">
	</td>
	<td>
	<input type=button name=btnOk id=btnOk value=" ok " style="display:none;" onclick="doApply();self.close()" class="inpBtn" onmouseover="this.className='inpBtnOver';" onmouseout="this.className='inpBtnOut'">
	</td>
	</tr>
	</table>
</td>
</tr>
</table>

</body>
</html>