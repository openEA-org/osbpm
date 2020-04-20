/***********************************************************
InnovaStudio WYSIWYG Editor 2.9
Copyright ?2003-2005, INNOVA STUDIO (www.InnovaStudio.com). All rights reserved.
************************************************************/

/*** UTILITY OBJECT ***/
var oUtil=new InnovaEditorUtil();
function InnovaEditorUtil()
	{
	/*** Localization ***/
	this.langDir="english";
	try{if(LanguageDirectory)this.langDir=LanguageDirectory;}catch(e){;}
	var oScripts=document.getElementsByTagName("script");
	for(var i=0;i<oScripts.length;i++)
		{
		var sSrc=oScripts[i].src.toLowerCase();
		if(sSrc.indexOf("scripts/editor.js")!=-1) this.scriptPath=oScripts[i].src.replace(/editor.js/ig,"");
		}
	this.scriptPathLang=this.scriptPath+"language/"+this.langDir+"/";
	if(this.langDir=="english")
		document.write("<scr"+"ipt src='"+this.scriptPathLang+"editor_lang.js'></scr"+"ipt>");
	/*** /Localization ***/

	this.oName;this.oEditor;this.obj;
	this.oSel;
	this.sType;
	this.bInside=bInside;
	this.useSelection=true;
	this.arrEditor=[];
	this.onSelectionChanged=function(){return true;};
	this.activeElement;
	}

/*** FOCUS STUFF ***/
function bInside(oElement)
	{
	while(oElement!=null)
		{
		if(oElement.contentEditable=="true")return true;
		oElement=oElement.parentElement;
		}
	return false;
	}
function checkFocus()
	{
	var oEditor=eval("idContent"+this.oName);
	var oSel=oEditor.document.selection.createRange();
	var sType=oEditor.document.selection.type;

	if(oSel.parentElement!=null)
		{
		if(!bInside(oSel.parentElement()))return false;
		}
	else
		{
		if(!bInside(oSel.item(0)))return false;
		}
	return true;
	}
function iwe_focus()
	{
	var oEditor=eval("idContent"+this.oName);
	oEditor.focus()
	}

/*** EDITOR OBJECT ***/
function InnovaEditor(oName)
	{
	this.oName=oName;
	this.RENDER=RENDER;
	this.IsSecurityRestricted=false;

	this.loadHTML=loadHTML;
	this.putHTML=putHTML;
	this.getHTMLBody=getHTMLBody;
	this.getXHTMLBody=getXHTMLBody;
	this.getHTML=getHTML;
	this.getXHTML=getXHTML;
	this.getTextBody=getTextBody;
	this.initialRefresh=false;
	this.preserveSpace=false;

	this.bInside=bInside;
	this.checkFocus=checkFocus;
	this.focus=iwe_focus;
	
	this.onKeyPress=function(){return true;};
	
	this.styleSelectionHoverBg="#acb6bf";
	this.styleSelectionHoverFg="white";

	//clean
	this.cleanEmptySpan=cleanEmptySpan;
	this.cleanFonts=cleanFonts;
	this.cleanTags=cleanTags;
	this.replaceTags=replaceTags;
	this.cleanDeprecated=cleanDeprecated;

	this.doClean=doClean;
	this.applySpanStyle=applySpanStyle;
	this.applyLine=applyLine;
	this.applyBold=applyBold;
	this.applyItalic=applyItalic;

	this.doOnPaste=doOnPaste;
	this.isAfterPaste=false;

	this.doCmd=doCmd;
	this.applyParagraph=applyParagraph;
	this.applyFontName=applyFontName;
	this.applyFontSize=applyFontSize;
	this.applyBullets=applyBullets;
	this.applyNumbering=applyNumbering;
	this.applyJustifyLeft=applyJustifyLeft;
	this.applyJustifyCenter=applyJustifyCenter;
	this.applyJustifyRight=applyJustifyRight;
	this.applyJustifyFull=applyJustifyFull;
	this.applyBlockDirLTR=applyBlockDirLTR;
	this.applyBlockDirRTL=applyBlockDirRTL;
	this.doPaste=doPaste;
	this.doPasteText=doPasteText;
	this.applySpan=applySpan;
	this.makeAbsolute=makeAbsolute;
	this.insertHTML=insertHTML;
	this.clearAll=clearAll;
	this.insertCustomTag=insertCustomTag;
	this.selectParagraph=selectParagraph;
	
	this.hide=hide;
	this.dropShow=dropShow;

	this.width="100%";
	this.height="100%";
	this.publishingPath="";//ex."http://localhost/InnovaStudio/"

	var oScripts=document.getElementsByTagName("script");
	for(var i=0;i<oScripts.length;i++)
		{
		var sSrc=oScripts[i].src.toLowerCase();
		if(sSrc.indexOf("scripts/editor.js")!=-1) this.scriptPath=oScripts[i].src.replace(/editor.js/,"");
		}

	this.iconPath="icons/";
	this.iconWidth=23;//25;
	this.iconHeight=25;//24;
	this.iconOffsetTop;//not used

	this.writeIconToggle=writeIconToggle;
	this.writeIconStandard=writeIconStandard;
	this.writeDropDown=writeDropDown;
	this.writeBreakSpace=writeBreakSpace;
	this.dropTopAdjustment=-1;
	this.dropLeftAdjustment=0;

	this.runtimeBorder=runtimeBorder;
	this.runtimeBorderOn=runtimeBorderOn;
	this.runtimeBorderOff=runtimeBorderOff;
	this.IsRuntimeBorderOn=true;
	this.runtimeStyles=runtimeStyles;

	this.applyColor=applyColor;
	this.customColors=[];//["#ff4500","#ffa500","#808000","#4682b4","#1e90ff","#9400d3","#ff1493","#a9a9a9"];
	this.oColor1 = new ColorPicker("oColor1",this.oName);//to call: oEdit1.oColor1
	this.oColor2 = new ColorPicker("oColor2",this.oName);//rendered id: ...oColor1oEdit1
	this.expandSelection=expandSelection;

	this.fullScreen=fullScreen;
	this.stateFullScreen=false;
	this.onFullScreen=function(){return true;};
	this.onNormalScreen=function(){return true;};

	this.arrElm=new Array(300);
	this.getElm=iwe_getElm;

	this.features=[];
	this.buttonMap=["ClearAll","Save","FullScreen","Preview","Print","Search","SpellCheck","|",
			"Cut","Copy","Paste","PasteWord","PasteText","|","Undo","Redo","|",
			"ForeColor","BackColor","|","Bookmark","Hyperlink",
			"Image","Flash","Media","ContentBlock","InternalLink","CustomObject","|",
			"Table","Guidelines","Absolute","|","Characters","Line",
			"Form","RemoveFormat","HTMLFullSource","HTMLSource","XHTMLFullSource",
			"XHTMLSource","BRK",
			"StyleAndFormatting","Styles","|","CustomTag","Paragraph","FontName","FontSize","|",
			"Bold","Italic","Underline","Strikethrough","Superscript","Subscript","|",
			"JustifyLeft","JustifyCenter","JustifyRight","JustifyFull","|",
			"Numbering","Bullets","|","Indent","Outdent","LTR","RTL"];//complete, default

	this.btnSave=false;this.btnPreview=true;this.btnFullScreen=false;this.btnPrint=false;this.btnSearch=true;
	this.btnSpellCheck=false;this.btnTextFormatting=true;
	this.btnListFormatting=true;this.btnBoxFormatting=true;this.btnParagraphFormatting=true;this.btnCssText=true;this.btnCssBuilder=false;
	this.btnStyles=false;this.btnParagraph=true;this.btnFontName=true;this.btnFontSize=true;
	this.btnCut=true;this.btnCopy=true;this.btnPaste=true;this.btnPasteText=false;this.btnUndo=true;this.btnRedo=true;
	this.btnBold=true;this.btnItalic=true;this.btnUnderline=true;
	this.btnStrikethrough=false;this.btnSuperscript=false;this.btnSubscript=false;
	this.btnJustifyLeft=true;this.btnJustifyCenter=true;this.btnJustifyRight=true;this.btnJustifyFull=true;
	this.btnNumbering=true;this.btnBullets=true;this.btnIndent=true;this.btnOutdent=true;
	this.btnLTR=false;this.btnRTL=false;this.btnForeColor=true;this.btnBackColor=true;
	this.btnHyperlink=true;this.btnBookmark=true;this.btnCharacters=true;this.btnCustomTag=false;
	this.btnImage=true;this.btnFlash=false;this.btnMedia=false;
	this.btnTable=true;this.btnGuidelines=true;
	this.btnAbsolute=true;this.btnPasteWord=true;this.btnLine=true;
	this.btnForm=true;this.btnRemoveFormat=true;
	this.btnHTMLFullSource=false;this.btnHTMLSource=true;
	this.btnXHTMLFullSource=false;this.btnXHTMLSource=true;
	this.btnClearAll=true;

	//*** CMS Features ***
	this.cmdAssetManager="";

	this.btnContentBlock=false;
	this.cmdContentBlock=";";//needs ;
	this.btnInternalLink=false;
	this.cmdInternalLink=";";//needs ;
	this.insertLink=insertLink;
	this.btnCustomObject=false;
	this.cmdCustomObject=";";//needs ;
	//*****

	this.css="/linkey/bpm/bpm.css";
	this.arrStyle=[];
	this.isCssLoaded=false;
	this.openStyleSelect=openStyleSelect;

	this.arrParagraph=[[getTxt("Heading 1"),"H1"],
						[getTxt("Heading 2"),"H2"],
						[getTxt("Heading 3"),"H3"],
						[getTxt("Heading 4"),"H4"],
						[getTxt("Heading 5"),"H5"],
						[getTxt("Heading 6"),"H6"],
						[getTxt("Preformatted"),"PRE"],
						[getTxt("Normal (P)"),"P"],
						[getTxt("Normal (DIV)"),"DIV"]];

	this.arrFontName=["宋体","黑体","Book Antiqua","Arial","Arial Black","Arial Narrow",
						"Bookman Old Style",
						"Century Gothic","Comic Sans MS","Courier New",
						"Franklin Gothic Medium","Garamond","Georgia",
						"Impact","Lucida Console","Lucida Sans","Lucida Unicode",
						"Modern","Monotype Corsiva","Palatino Linotype",
						"Roman","Script","Small Fonts","Symbol",
						"Tahoma","Times New Roman","Trebuchet MS"];

	this.arrFontSize=[[getTxt("Size 1"),"1"],
						[getTxt("Size 2"),"2"],
						[getTxt("Size 3"),"3"],
						[getTxt("Size 4"),"4"],
						[getTxt("Size 5"),"5"],
						[getTxt("Size 6"),"6"],
						[getTxt("Size 7"),"7"]];

	this.arrCustomTag=[];//eg.[["Full Name","{%full_name%}"],["Email","{%email%}"]];

	this.docType="";
	this.html="<html>";
	this.headContent="";
	this.preloadHTML="";

	this.onSave=function(){document.getElementById("iwe_btnSubmit"+this.oName).click()};
	this.useBR=false;
	this.useDIV=true;

	this.doUndo=doUndo;
	this.doRedo=doRedo;
	this.saveForUndo=saveForUndo;
	this.arrUndoList=[];
	this.arrRedoList=[];

	this.useTagSelector=true;
	this.TagSelectorPosition="bottom";
	this.moveTagSelector=moveTagSelector;
	this.selectElement=selectElement;
	this.removeTag=removeTag;
	this.doClick_TabCreate=doClick_TabCreate;
	this.doRefresh_TabCreate=doRefresh_TabCreate;

	this.arrCustomButtons = [["CustomName1","alert(0)","caption here","btnSave.gif"],
							["CustomName2","alert(0)","caption here","btnSave.gif"]];

	this.onSelectionChanged=function(){return true;};
	
	this.spellCheckMode="ieSpell";//NetSpell

	this.REPLACE=REPLACE;
	this.idTextArea;
	this.mode="HTMLBody";
	}

/*** UNDO/REDO ***/
function saveForUndo()
	{
	var oEditor=eval("idContent"+this.oName);
	var obj=eval(this.oName);
	if(obj.arrUndoList[0])
		if(oEditor.document.body.innerHTML==obj.arrUndoList[0][0])return;
	for(var i=20;i>1;i--)obj.arrUndoList[i-1]=obj.arrUndoList[i-2];
	obj.focus();
	var oSel=oEditor.document.selection.createRange();
	var sType=oEditor.document.selection.type;

	if(sType=="None")
		obj.arrUndoList[0]=[oEditor.document.body.innerHTML,
			oEditor.document.selection.createRange().getBookmark(),"None"];
	else if(sType=="Text")
		obj.arrUndoList[0]=[oEditor.document.body.innerHTML,
			oEditor.document.selection.createRange().getBookmark(),"Text"];
	else if(sType=="Control")
		{
		oSel.item(0).selThis="selThis";
		obj.arrUndoList[0]=[oEditor.document.body.innerHTML,null,"Control"];
		oSel.item(0).removeAttribute("selThis",0);
		}
	this.arrRedoList=[];//clear redo list

	if(this.btnUndo) makeEnableNormal(eval("document.all.btnUndo"+this.oName));
	if(this.btnRedo) makeDisabled(eval("document.all.btnRedo"+this.oName));
	}
function doUndo()
	{
	var oEditor=eval("idContent"+this.oName);
	var obj=eval(this.oName);
	if(!obj.arrUndoList[0])return;
	//~~~~
	for(var i=20;i>1;i--)obj.arrRedoList[i-1]=obj.arrRedoList[i-2];
	var oSel=oEditor.document.selection.createRange();
	var sType=oEditor.document.selection.type;
	if(sType=="None")
		this.arrRedoList[0]=[oEditor.document.body.innerHTML,
			oEditor.document.selection.createRange().getBookmark(),"None"];
	else if(sType=="Text")
		this.arrRedoList[0]=[oEditor.document.body.innerHTML,
			oEditor.document.selection.createRange().getBookmark(),"Text"];
	else if(sType=="Control")
		{
		oSel.item(0).selThis="selThis";
		this.arrRedoList[0]=[oEditor.document.body.innerHTML,null,"Control"];
		oSel.item(0).removeAttribute("selThis",0);
		}
	//~~~~
	sHTML=obj.arrUndoList[0][0];
	var arrA = String(sHTML).match(/<A[^>]*>/ig);
	if(arrA)
		for(var i=0;i<arrA.length;i++)
			{
			sTmp = arrA[i].replace(/href=/,"href_iwe=");
			sHTML=String(sHTML).replace(arrA[i],sTmp);
			}
	var arrB = String(sHTML).match(/<IMG[^>]*>/ig);
	if(arrB)
		for(var i=0;i<arrB.length;i++)
			{
			sTmp = arrB[i].replace(/src=/,"src_iwe=");
			sHTML=String(sHTML).replace(arrB[i],sTmp);
			}
	var arrC = String(sHTML).match(/<AREA[^>]*>/ig);
	if(arrC)
		for(var i=0;i<arrC.length;i++)
			{
			sTmp = arrC[i].replace(/href=/,"href_iwe=");
			sHTML=String(sHTML).replace(arrC[i],sTmp);
			}
	oEditor.document.body.innerHTML=sHTML;
	for(var i=0;i<oEditor.document.all.length;i++)
		{
		if(oEditor.document.all[i].getAttribute("href_iwe"))
			{
			oEditor.document.all[i].href=oEditor.document.all[i].getAttribute("href_iwe");
			oEditor.document.all[i].removeAttribute("href_iwe",0);
			}
		if(oEditor.document.all[i].getAttribute("src_iwe"))
			{
			oEditor.document.all[i].src=oEditor.document.all[i].getAttribute("src_iwe");
			oEditor.document.all[i].removeAttribute("src_iwe",0);
			}
		}
	//*** RUNTIME STYLES ***
	this.runtimeBorder(false);
	this.runtimeStyles();
	//***********************
	var oRange=oEditor.document.body.createTextRange();
	if(obj.arrUndoList[0][2]=="None")
		{
		oRange.moveToBookmark(obj.arrUndoList[0][1]);
		oRange.select(); //di-disable, spy tdk select all? tdk perlu utk undo
		}
	else if(obj.arrUndoList[0][2]=="Text")
		{
		oRange.moveToBookmark(obj.arrUndoList[0][1]);
		oRange.select();
		}
	else if(obj.arrUndoList[0][2]=="Control")
		{
		for(var i=0;i<oEditor.document.all.length;i++)
			{
			if(oEditor.document.all[i].selThis=="selThis")
				{
				var oSelRange=oEditor.document.body.createControlRange();
				oSelRange.add(oEditor.document.all[i]);
				oSelRange.select();
				oEditor.document.all[i].removeAttribute("selThis",0);
				}
			}
		}
	//~~~~
	for(var i=0;i<19;i++)obj.arrUndoList[i]=obj.arrUndoList[i+1];
	obj.arrUndoList[19]=null;
	realTime(this.oName);
	}
function doRedo()
	{
	var oEditor=eval("idContent"+this.oName);
	var obj=eval(this.oName);
	if(!obj.arrRedoList[0])return;
	//~~~~
	for(var i=20;i>1;i--)obj.arrUndoList[i-1]=obj.arrUndoList[i-2];
	var oSel=oEditor.document.selection.createRange();
	var sType=oEditor.document.selection.type;
	if(sType=="None")
		obj.arrUndoList[0]=[oEditor.document.body.innerHTML,
			oEditor.document.selection.createRange().getBookmark(),"None"];
	else if(sType=="Text")
		obj.arrUndoList[0]=[oEditor.document.body.innerHTML,
			oEditor.document.selection.createRange().getBookmark(),"Text"];
	else if(sType=="Control")
		{
		oSel.item(0).selThis="selThis";
		this.arrUndoList[0]=[oEditor.document.body.innerHTML,null,"Control"];
		oSel.item(0).removeAttribute("selThis",0);
		}
	//~~~~
	sHTML=obj.arrRedoList[0][0];
	var arrA = String(sHTML).match(/<A[^>]*>/ig);
	if(arrA)
		for(var i=0;i<arrA.length;i++)
			{
			sTmp = arrA[i].replace(/href=/,"href_iwe=");
			sHTML=String(sHTML).replace(arrA[i],sTmp);
			}
	var arrB = String(sHTML).match(/<IMG[^>]*>/ig);
	if(arrB)
		for(var i=0;i<arrB.length;i++)
			{
			sTmp = arrB[i].replace(/src=/,"src_iwe=");
			sHTML=String(sHTML).replace(arrB[i],sTmp);
			}
	var arrC = String(sHTML).match(/<AREA[^>]*>/ig);
	if(arrC)
		for(var i=0;i<arrC.length;i++)
			{
			sTmp = arrC[i].replace(/href=/,"href_iwe=");
			sHTML=String(sHTML).replace(arrC[i],sTmp);
			}
	oEditor.document.body.innerHTML=sHTML;
	for(var i=0;i<oEditor.document.all.length;i++)
		{
		if(oEditor.document.all[i].getAttribute("href_iwe"))
			{
			oEditor.document.all[i].href=oEditor.document.all[i].getAttribute("href_iwe");
			oEditor.document.all[i].removeAttribute("href_iwe",0);
			}
		if(oEditor.document.all[i].getAttribute("src_iwe"))
			{
			oEditor.document.all[i].src=oEditor.document.all[i].getAttribute("src_iwe");
			oEditor.document.all[i].removeAttribute("src_iwe",0);
			}
		}
	//*** RUNTIME STYLES ***
	this.runtimeBorder(false);
	this.runtimeStyles();
	//***********************
	var oRange=oEditor.document.body.createTextRange();
	if(obj.arrRedoList[0][2]=="None")
		{
		oRange.moveToBookmark(obj.arrRedoList[0][1]);
		//oRange.select(); //di-disable, sph tdk select all, utk redo perlu
		}
	else if(obj.arrRedoList[0][2]=="Text")
		{
		oRange.moveToBookmark(obj.arrRedoList[0][1]);
		oRange.select();
		}
	else if(obj.arrRedoList[0][2]=="Control")
		{
		for(var i=0;i<oEditor.document.all.length;i++)
			{
			if(oEditor.document.all[i].selThis=="selThis")
				{
				var oSelRange = oEditor.document.body.createControlRange();
				oSelRange.add(oEditor.document.all[i]);
				oSelRange.select();
				oEditor.document.all[i].removeAttribute("selThis",0);
				}
			}
		}
	//~~~~
	for(var i=0;i<19;i++)obj.arrRedoList[i]=obj.arrRedoList[i+1];
	obj.arrRedoList[19]=null;
	realTime(this.oName);
	}

/*** RENDER ***/
var bOnSubmitOriginalSaved=false;
function REPLACE(idTextArea, sMode)
	{
	this.idTextArea=idTextArea;
	var oTextArea=document.getElementById(idTextArea);
	oTextArea.style.display="none";
	var oForm=GetElement(oTextArea,"FORM");
	if(oForm)
		{
		if(!bOnSubmitOriginalSaved)
			{
			onsubmit_original=oForm.onsubmit;
			
			bOnSubmitOriginalSaved=true;	
			}
		oForm.onsubmit = new Function("return onsubmit_new()");
		}
	
	var sContent=document.getElementById(idTextArea).value;
	sContent=sContent.replace(/&/g,"&amp;");
	sContent=sContent.replace(/</g,"&lt;");
	sContent=sContent.replace(/>/g,"&gt;");
	
	this.RENDER(sContent);
	}
function onsubmit_new()
	{
	var sContent;
	for(var i=0;i<oUtil.arrEditor.length;i++)
		{
		var oEdit=eval(oUtil.arrEditor[i]);
		if(oEdit.mode=="HTMLBody")sContent=oEdit.getHTMLBody();
		if(oEdit.mode=="HTML")sContent=oEdit.getHTML();
		if(oEdit.mode=="XHTMLBody")sContent=oEdit.getXHTMLBody();
		if(oEdit.mode=="XHTML")sContent=oEdit.getXHTML();
		document.getElementById(oEdit.idTextArea).value=sContent;
		}
	if(onsubmit_original)return onsubmit_original();
	}
function onsubmit_original(){}

var iconHeight;//icons related
function RENDER(sPreloadHTML)
	{
	iconHeight=this.iconHeight;//icons related

	/*** Tetap Ada (For downgrade compatibility) ***/
	if(sPreloadHTML.substring(0,4)=="<!--" &&
		sPreloadHTML.substring(sPreloadHTML.length-3)=="-->")
		sPreloadHTML=sPreloadHTML.substring(4,sPreloadHTML.length-3);

	if(sPreloadHTML.substring(0,4)=="<!--" &&
		sPreloadHTML.substring(sPreloadHTML.length-6)=="--&gt;")
		sPreloadHTML=sPreloadHTML.substring(4,sPreloadHTML.length-6);

	/*** Converting back HTML-encoded content (kalau tdk encoded tdk masalah) ***/
	sPreloadHTML=sPreloadHTML.replace(/&lt;/g,"<");
	sPreloadHTML=sPreloadHTML.replace(/&gt;/g,">");
	sPreloadHTML=sPreloadHTML.replace(/&amp;/g,"&");

	/*** enable required buttons ***/
	if(this.cmdContentBlock!=";")this.btnContentBlock=true;
	if(this.cmdInternalLink!=";")this.btnInternalLink=true;	
	if(this.cmdCustomObject!=";")this.btnCustomObject=true;
	if(this.arrCustomTag.length>0)this.btnCustomTag=true;
	if(this.mode=="HTMLBody"){this.btnXHTMLSource=true;this.btnXHTMLFullSource=false;}
	if(this.mode=="HTML"){this.btnXHTMLFullSource=true;this.btnXHTMLSource=false;}
	if(this.mode=="XHTMLBody"){this.btnXHTMLSource=true;this.btnXHTMLFullSource=false;}
	if(this.mode=="XHTML"){this.btnXHTMLFullSource=true;this.btnXHTMLSource=false;}
	
	/*** features ***/
	var bUseFeature=false;
	if(this.features.length>0)
		{
		bUseFeature=true;
		for(var i=0;i<this.buttonMap.length;i++)
			eval(this.oName+".btn"+this.buttonMap[i]+"=true");//ex: oEdit1.btnStyleAndFormatting=true (no problem), oEdit1.btn|=true (no problem), oEdit1.btnBRK=true (no problem)

		this.btnTextFormatting=false;this.btnListFormatting=false;
		this.btnBoxFormatting=false;this.btnParagraphFormatting=false;
		this.btnCssText=false;this.btnCssBuilder=false;
		for(var j=0;j<this.features.length;j++)
			eval(this.oName+".btn"+this.features[j]+"=true");//ex: oEdit1.btnTextFormatting=true

		for(var i=0;i<this.buttonMap.length;i++)
			{
			sButtonName=this.buttonMap[i];
			bBtnExists=false;
			for(var j=0;j<this.features.length;j++)
				if(sButtonName==this.features[j])bBtnExists=true;//ada;

			if(!bBtnExists)//tdk ada; set false
				eval(this.oName+".btn"+sButtonName+"=false");//ex: oEdit1.btnBold=false, oEdit1.btn|=false (no problem), oEdit1.btnBRK=false (no problem)
			}
		//Remove:"TextFormatting","ListFormatting",dst.=>tdk perlu(krn diabaikan)
		this.buttonMap=this.features;
		}
	/*** /features ***/

	this.preloadHTML=sPreloadHTML;
	var sHTMLDropMenus="";
	var sHTMLIcons="";
	var sTmp="";

	for(var i=0;i<this.buttonMap.length;i++)
		{
		sButtonName=this.buttonMap[i];
		switch(sButtonName)
			{
			case "|":
				sHTMLIcons+=this.writeBreakSpace();
				break;
			case "BRK":
				sHTMLIcons+="</td></tr></table><table cellpadding=0 cellspacing=0><tr><td dir=ltr style='padding:0px'>";
				break;
			case "Save":
				if(this.btnSave)sHTMLIcons+=this.writeIconStandard("btnSave"+this.oName,this.oName+".onSave()","btnSave.gif",getTxt("Save"));
				break;
			case "Preview":
				if(this.btnPreview)
					{
					sHTMLIcons+=this.writeIconStandard("btnPreview"+this.oName,this.oName+".dropShow(this,dropPreview"+this.oName+")","btnPreview.gif",getTxt("Preview"));
					var arrPreviewSize=[[640,480],[800,600],[1024,768]];
					sTmp="";
					for(var j=0;j<arrPreviewSize.length;j++)
						{
						sTmp+= "<tr><td onclick=\"dropPreview"+this.oName+".style.display='none';setActiveEditor('"+this.oName+"');modalDialogShow('"+this.scriptPath+"preview.htm',"+arrPreviewSize[j][0]+","+arrPreviewSize[j][1]+");\" "+
							"style=\"padding:2px;padding-top:1px;font-family:Tahoma;font-size:11px;color:black;\" "+
							"onmouseover=\"this.style.backgroundColor='#708090';this.style.color='#FFFFFF';\" "+
							"onmouseout=\"this.style.backgroundColor='';this.style.color='#000000';\" unselectable=on>"+arrPreviewSize[j][0]+"x"+arrPreviewSize[j][1]+"</td></tr>";
						}
					sHTMLDropMenus+="<table id=dropPreview"+this.oName+" cellpadding=0 cellspacing=0 "+
						"style='z-index:1;display:none;position:absolute;border:#80788D 1px solid;"+
						"cursor:default;background-color:#fbfbfd;' unselectable=on>"+
						sTmp+"</table>";
					}
				break;
			case "FullScreen":
				if(this.btnFullScreen)sHTMLIcons+=this.writeIconStandard("btnFullScreen"+this.oName,this.oName+".fullScreen()","btnFullScreen.gif",getTxt("Full Screen"));
				break;
			case "Print":
				if(this.btnPrint)sHTMLIcons+=this.writeIconStandard("btnPrint"+this.oName,this.oName+".focus();"+this.oName+".doCmd('Print')","btnPrint.gif",getTxt("Print"));
				break;
			case "Search":
				if(this.btnSearch)sHTMLIcons+=this.writeIconStandard("btnSearch"+this.oName,this.oName+".hide();modelessDialogShow('"+this.scriptPath+"search.htm',375,163)","btnSearch.gif",getTxt("Search"));
				break;
			case "SpellCheck":
				if(this.btnSpellCheck)
					{
					if(this.spellCheckMode=="ieSpell")
						sHTMLIcons+=this.writeIconStandard("btnSpellCheck"+this.oName,this.oName+".hide();windowOpen('"+this.scriptPath+"spellcheck.htm',500,222)","btnSpellCheck.gif",getTxt("Check Spelling"));
					if(this.spellCheckMode=="NetSpell")
						sHTMLIcons+=this.writeIconStandard("btnSpellCheck"+this.oName,this.oName+".hide();windowOpen('"+this.scriptPath+"spellcheck2.htm',500,500)","btnSpellCheck.gif",getTxt("Check Spelling"));
					}
				break;
			case "StyleAndFormatting":
				sTmp="";
				if(this.btnTextFormatting)
					sTmp+="<tr><td onclick=\"modelessDialogShow('"+this.scriptPath+"text1.htm',511,465);"+
						"dropStyle"+this.oName+".style.display='none'\""+
						" style=\"padding:2px;padding-top:1px;font-family:Tahoma;font-size:11px;color:black;\" "+
						"onmouseover=\"this.style.backgroundColor='#708090';this.style.color='#FFFFFF';\" "+
						"onmouseout=\"this.style.backgroundColor='';this.style.color='#000000';\" unselectable=on>"+getTxt("Text Formatting")+"</td></tr>";
				if(this.btnParagraphFormatting)
					sTmp+="<tr><td onclick=\"modelessDialogShow('"+this.scriptPath+"paragraph.htm',440,284);"+
						"dropStyle"+this.oName+".style.display='none'\""+
						" style=\"padding:2px;padding-top:1px;font-family:Tahoma;font-size:11px;color:black;\" "+
						"onmouseover=\"this.style.backgroundColor='#708090';this.style.color='#FFFFFF';\" "+
						"onmouseout=\"this.style.backgroundColor='';this.style.color='#000000';\" unselectable=on>"+getTxt("Paragraph Formatting")+"</td></tr>";
				if(this.btnListFormatting)
					sTmp+="<tr><td onclick=\"modelessDialogShow('"+this.scriptPath+"list.htm',270,335);"+
						"dropStyle"+this.oName+".style.display='none'\""+
						" style=\"padding:2px;padding-top:1px;font-family:Tahoma;font-size:11px;color:black;\" "+
						"onmouseover=\"this.style.backgroundColor='#708090';this.style.color='#FFFFFF';\" "+
						"onmouseout=\"this.style.backgroundColor='';this.style.color='#000000';\" unselectable=on>"+getTxt("List Formatting")+"</td></tr>";
				if(this.btnBoxFormatting)
					sTmp+="<tr><td onclick=\"modelessDialogShow('"+this.scriptPath+"box.htm',438,380);"+
						"dropStyle"+this.oName+".style.display='none'\""+
						" style=\"padding:2px;padding-top:1px;font-family:Tahoma;font-size:11px;color:black;\" "+
						"onmouseover=\"this.style.backgroundColor='#708090';this.style.color='#FFFFFF';\" "+
						"onmouseout=\"this.style.backgroundColor='';this.style.color='#000000';\" unselectable=on>"+getTxt("Box Formatting")+"</td></tr>";
				if(this.btnCssText)
					sTmp+= "<tr><td onclick=\"modelessDialogShow('"+this.scriptPath+"styles_cssText.htm',360,332);"+
						"dropStyle"+this.oName+".style.display='none'\""+
						" style=\"padding:2px;padding-top:1px;font-family:Tahoma;font-size:11px;color:black;\" "+
						"onmouseover=\"this.style.backgroundColor='#708090';this.style.color='#FFFFFF';\" "+
						"onmouseout=\"this.style.backgroundColor='';this.style.color='#000000';\" unselectable=on>"+getTxt("Custom CSS")+"</td></tr>";
				if(this.btnCssBuilder) 
					sTmp+= "<tr><td onclick=\"modelessDialogShow('"+this.scriptPath+"styles_cssText2.htm',430,445);"+
						"dropStyle"+this.oName+".style.display='none'\""+
						" style=\"padding:2px;padding-top:1px;font-family:Tahoma;font-size:11px;color:black;\" "+
						"onmouseover=\"this.style.backgroundColor='#708090';this.style.color='#FFFFFF';\" "+
						"onmouseout=\"this.style.backgroundColor='';this.style.color='#000000';\" unselectable=on>"+getTxt("CSS Builder")+"</td></tr>";
				if(this.btnTextFormatting||this.btnParagraphFormatting||this.btnListFormatting||this.btnBoxFormatting||this.btnCssText||this.btnCssBuilder)
					{
					sHTMLIcons+=this.writeIconStandard("btnStyleAndFormat"+this.oName,this.oName+".dropShow(this,dropStyle"+this.oName+")","btnStyle.gif",getTxt("Styles & Formatting"));
					sHTMLDropMenus+="<table id=dropStyle"+this.oName+" cellpadding=0 cellspacing=0 "+
						"style='z-index:1;display:none;position:absolute;border:#80788D 1px solid;"+
						"cursor:default;background-color:#fbfbfd;' unselectable=on>"+
						sTmp+"</table>";
					}
				break;
			case "Styles":
				if(this.btnStyles)sHTMLIcons+=this.writeIconStandard("btnStyles"+this.oName,this.oName+".hide();"+this.oName+".openStyleSelect()","btnStyleSelect.gif",getTxt("Style Selection"));
				break;
			case "Paragraph":
				if(this.btnParagraph)
					{
					sHTMLDropMenus+="<table id=dropParagraph"+this.oName+" cellpadding=0 cellspacing=0 "+
						"style='z-index:1;display:none;position:absolute;border:#80788D 1px solid;"+
						"cursor:default;background-color:#fbfbfd;' unselectable=on>";
					for(var j=0;j<this.arrParagraph.length;j++)
						{
						sHTMLDropMenus+="<tr><td onclick=\""+this.oName+".applyParagraph('<"+this.arrParagraph[j][1]+">')\" "+
							"style=\"padding:0;padding-left:5px;padding-right:5px;font-family:tahoma;color:black;\" "+
							"onmouseover=\"this.style.backgroundColor='#708090';this.style.color='#FFFFFF';\" "+
							"onmouseout=\"this.style.backgroundColor='';this.style.color='#000000';\" unselectable=on align=center>"+
							"<"+this.arrParagraph[j][1]+" style=\"\margin-bottom:4px\"  unselectable=on> "+
							this.arrParagraph[j][0]+"</"+this.arrParagraph[j][1]+"></td></tr>";
						}
					sHTMLDropMenus+="</table>";
					sHTMLIcons+=this.writeDropDown("btnParagraph"+this.oName,this.oName+".selectParagraph();"+this.oName+".dropShow(this,dropParagraph"+this.oName+")","btnParagraph.gif",getTxt("Paragraph"),77);
					}
				break;
			case "FontName":
				if(this.btnFontName)
					{
					sHTMLDropMenus+="<table id=dropFontName"+this.oName+" cellpadding=0 cellspacing=0 "+
						"style='z-index:1;display:none;position:absolute;border:#80788D 1px solid;"+
						"cursor:default;background-color:#fbfbfd;' unselectable=on><tr><td style='padding:0px;'>";

					//~~~~ up to 120 fonts
					var numOfFonts=0;
					for(var j=0;j<this.arrFontName.length;j++)
						{
						//if(this.arrFontName[j].length==1)
						if(this.arrFontName[j].toString().indexOf(",")==-1)
							{
							if(this.arrFontName[j]!="serif" &&
								this.arrFontName[j]!="sans-serif" &&
								this.arrFontName[j]!="cursive" &&
								this.arrFontName[j]!="fantasy" &&
								this.arrFontName[j]!="monoscape")numOfFonts++;
							}
						else numOfFonts++;
						}
					sHTMLDropMenus+="<table cellpadding=0 cellspacing=0>";
					for(var j=0;j<this.arrFontName.length;j++)
						{
						//if(this.arrFontName[j].length==1)
						if(this.arrFontName[j].toString().indexOf(",")==-1)						
							{
							if(this.arrFontName[j]!="serif" &&
								this.arrFontName[j]!="sans-serif" &&
								this.arrFontName[j]!="cursive" &&
								this.arrFontName[j]!="fantasy" &&
								this.arrFontName[j]!="monoscape")
								sHTMLDropMenus+="<tr><td onclick=\""+this.oName+".applyFontName('"+this.arrFontName[j]+"')\" "+
									"style=\"padding:2px;padding-top:1px;font-family:"+ this.arrFontName[j] +";font-size:11px;color:black;\" "+
									"onmouseover=\"if(this.style.backgroundColor=='#708090')this.sel='true';this.style.backgroundColor='#708090';this.style.color='#FFFFFF';\" "+
									"onmouseout=\"if(this.sel=='true'){this.sel='false'}else{this.style.backgroundColor='';this.style.color='#000000';}\" unselectable=on>"+
									this.arrFontName[j]+" <span unselectable=on style='font-family:tahoma'>("+ this.arrFontName[j] +")</span></td></tr>";
							}
						else
							{
							sHTMLDropMenus+="<tr><td onclick=\""+this.oName+".applyFontName('"+this.arrFontName[j][0]+"')\" "+
								"style=\"padding:2px;padding-top:1px;font-family:"+ this.arrFontName[j][0] +";font-size:11px;color:black;\" "+
								"onmouseover=\"if(this.style.backgroundColor=='#708090')this.sel='true';this.style.backgroundColor='#708090';this.style.color='#FFFFFF';\" "+
								"onmouseout=\"if(this.sel=='true'){this.sel='false'}else{this.style.backgroundColor='';this.style.color='#000000';}\" unselectable=on>"+
								this.arrFontName[j][1]+" <span unselectable=on style='font-family:tahoma'>("+ this.arrFontName[j][1] +")</span></td></tr>";
							}
						if(j==14||j==29||j==44||j==59||j==74||j==89||j==104)
							{
							if(j!=numOfFonts-1)
								{
								sHTMLDropMenus+="</table>";
								sHTMLDropMenus+="</td><td valign=top style='padding:0px;border-left:#80788D 1 solid'>";//main
								sHTMLDropMenus+="<table cellpadding=0 cellspacing=0>";
								}
							}
						}
					sHTMLDropMenus+="</table>";
					//~~~~

					sHTMLDropMenus+="</td></tr></table>";
					sHTMLIcons+=this.writeDropDown("btnFontName"+this.oName,this.oName+".expandSelection();"+this.oName+".dropShow(this,dropFontName"+this.oName+");realtimeFontSelect('"+this.oName+"')","btnFontName.gif",getTxt("Font Name"),77);
					}
				break;
			case "FontSize":
				if(this.btnFontSize)
					{
					sHTMLDropMenus+="<table id=dropFontSize"+this.oName+" cellpadding=0 cellspacing=0 "+
						"style='z-index:1;display:none;position:absolute;border:#80788D 1px solid;"+
						"cursor:default;background-color:#fbfbfd;' unselectable=on>";
					for(var j=0;j<this.arrFontSize.length;j++)
						{
						sHTMLDropMenus+="<tr><td onclick=\""+this.oName+".applyFontSize('"+this.arrFontSize[j][1]+"')\" "+
							"style=\"padding:0;padding-left:5px;padding-right:5px;font-family:tahoma;color:black;\" "+
							"onmouseover=\"if(this.style.backgroundColor=='#708090')this.sel='true';this.style.backgroundColor='#708090';this.style.color='#FFFFFF';\" "+
							"onmouseout=\"if(this.sel=='true'){this.sel='false'}else{this.style.backgroundColor='';this.style.color='#000000';}\" unselectable=on align=center>"+
							"<font unselectable=on size=\""+this.arrFontSize[j][1]+"\">"+
							this.arrFontSize[j][0]+"</font></td></tr>";
						}
					sHTMLDropMenus+="</table>";
					sHTMLIcons+=this.writeDropDown("btnFontSize"+this.oName,this.oName+".expandSelection();"+this.oName+".dropShow(this,dropFontSize"+this.oName+");realtimeSizeSelect('"+this.oName+"')","btnFontSize.gif",getTxt("Font Size"),60);
					}
				break;
			case "Cut":
				if(this.btnCut)sHTMLIcons+=this.writeIconStandard("btnCut"+this.oName,this.oName+".doCmd('Cut')","btnCut.gif",getTxt("Cut"));
				break;
			case "Copy":
				if(this.btnCopy)sHTMLIcons+=this.writeIconStandard("btnCopy"+this.oName,this.oName+".doCmd('Copy')","btnCopy.gif",getTxt("Copy"));
				break;
			case "Paste":
				if(this.btnPaste)sHTMLIcons+=this.writeIconStandard("btnPaste"+this.oName,this.oName+".doPaste()","btnPaste.gif",getTxt("Paste"));
				break;
			case "PasteWord":
				if(this.btnPasteWord)sHTMLIcons+=this.writeIconStandard("btnPasteWord"+this.oName,this.oName+".hide();modelessDialogShow('"+this.scriptPath+"paste_word.htm',400,280)","btnPasteWord.gif",getTxt("Paste from Word"));
				break;
			case "PasteText":
				if(this.btnPasteText)sHTMLIcons+=this.writeIconStandard("btnPasteText"+this.oName,this.oName+".doPasteText()","btnPasteText.gif",getTxt("Paste Text"));
				break;
			case "Undo":
				if(this.btnUndo)sHTMLIcons+=this.writeIconStandard("btnUndo"+this.oName,this.oName+".doUndo()","btnUndo.gif",getTxt("Undo"));
				break;
			case "Redo":
				if(this.btnRedo)sHTMLIcons+=this.writeIconStandard("btnRedo"+this.oName,this.oName+".doRedo()","btnRedo.gif",getTxt("Redo"));
				break;
			case "Bold":
				if(this.btnBold)sHTMLIcons+=this.writeIconToggle("btnBold"+this.oName,this.oName+".applyBold()","btnBold.gif",getTxt("Bold"));
				break;
			case "Italic":
				if(this.btnItalic)sHTMLIcons+=this.writeIconToggle("btnItalic"+this.oName,this.oName+".applyItalic()","btnItalic.gif",getTxt("Italic"));
				break;
			case "Underline":
				if(this.btnUnderline)sHTMLIcons+=this.writeIconToggle("btnUnderline"+this.oName,this.oName+".applyLine('underline')","btnUnderline.gif",getTxt("Underline"));
				break;
			case "Strikethrough":			
				if(this.btnStrikethrough)sHTMLIcons+=this.writeIconToggle("btnStrikethrough"+this.oName,this.oName+".applyLine('line-through')","btnStrikethrough.gif",getTxt("Strikethrough"));
				break;
			case "Superscript":
				if(this.btnSuperscript)sHTMLIcons+=this.writeIconToggle("btnSuperscript"+this.oName,this.oName+".doCmd('Superscript')","btnSuperscript.gif",getTxt("Superscript"));
				break;
			case "Subscript":
				if(this.btnSubscript)sHTMLIcons+=this.writeIconToggle("btnSubscript"+this.oName,this.oName+".doCmd('Subscript')","btnSubscript.gif",getTxt("Subscript"));
				break;
			case "JustifyLeft":
				if(this.btnJustifyLeft)sHTMLIcons+=this.writeIconToggle("btnJustifyLeft"+this.oName,this.oName+".applyJustifyLeft()","btnLeft.gif",getTxt("Justify Left"));
				break;
			case "JustifyCenter":
				if(this.btnJustifyCenter)sHTMLIcons+=this.writeIconToggle("btnJustifyCenter"+this.oName,this.oName+".applyJustifyCenter()","btnCenter.gif",getTxt("Justify Center"));
				break;
			case "JustifyRight":
				if(this.btnJustifyRight)sHTMLIcons+=this.writeIconToggle("btnJustifyRight"+this.oName,this.oName+".applyJustifyRight()","btnRight.gif",getTxt("Justify Right"));
				break;
			case "JustifyFull":
				if(this.btnJustifyFull)sHTMLIcons+=this.writeIconToggle("btnJustifyFull"+this.oName,this.oName+".applyJustifyFull()","btnFull.gif",getTxt("Justify Full"));
				break;
			case "Numbering":
				if(this.btnNumbering)sHTMLIcons+=this.writeIconToggle("btnNumbering"+this.oName,this.oName+".applyNumbering()","btnNumber.gif",getTxt("Numbering"));
				break;
			case "Bullets":
				if(this.btnBullets)sHTMLIcons+=this.writeIconToggle("btnBullets"+this.oName,this.oName+".applyBullets()","btnList.gif",getTxt("Bullets"));
				break;
			case "Indent":
				if(this.btnIndent)sHTMLIcons+=this.writeIconStandard("btnIndent"+this.oName,this.oName+".doCmd('Indent')","btnIndent.gif",getTxt("Indent"));
				break;
			case "Outdent":
				if(this.btnOutdent)sHTMLIcons+=this.writeIconStandard("btnOutdent"+this.oName,this.oName+".doCmd('Outdent')","btnOutdent.gif",getTxt("Outdent"));
				break;
			case "LTR":
				if(this.btnLTR)sHTMLIcons+=this.writeIconToggle("btnLTR"+this.oName,this.oName+".applyBlockDirLTR()","btnLTR.gif",getTxt("Left To Right"));
				break;
			case "RTL":
				if(this.btnRTL)sHTMLIcons+=this.writeIconToggle("btnRTL"+this.oName,this.oName+".applyBlockDirRTL()","btnRTL.gif",getTxt("Right To Left"));
				break;
			case "ForeColor":
				if(this.btnForeColor)sHTMLIcons+=this.writeIconStandard("btnForeColor"+this.oName,this.oName+".oColor1.show(this)","btnForeColor.gif",getTxt("Foreground Color"));
				break;
			case "BackColor":
				if(this.btnBackColor)sHTMLIcons+=this.writeIconStandard("btnBackColor"+this.oName,this.oName+".oColor2.show(this)","btnBackColor.gif",getTxt("Background Color"));
				break;
			case "Bookmark":
				if(this.btnBookmark)sHTMLIcons+=this.writeIconStandard("btnBookmark"+this.oName,this.oName+".hide();modelessDialogShow('"+this.scriptPath+"bookmark.htm',245,216)","btnBookmark.gif",getTxt("Bookmark"));
				break;
			case "Hyperlink":
				if(this.btnHyperlink)sHTMLIcons+=this.writeIconStandard("btnHyperlink"+this.oName,this.oName+".hide();modelessDialogShow('"+this.scriptPath+"hyperlink.htm',460,260)","btnHyperlink.gif",getTxt("Hyperlink"));
				break;
			case "CustomTag":
				if(this.btnCustomTag)
					{
					sHTMLDropMenus+="<table id=dropCustomTag"+this.oName+" cellpadding=0 cellspacing=0 "+
						"style='z-index:1;display:none;position:absolute;border:#80788D 1px solid;"+
						"cursor:default;background-color:#fbfbfd;' unselectable=on><tr><td valign=top style='padding:0px;'>";

					//~~~~ up to 120 tags
					sHTMLDropMenus+="<table cellpadding=0 cellspacing=0>";
					for(var j=0;j<this.arrCustomTag.length;j++)
						{
						sHTMLDropMenus+="<tr><td onclick=\""+this.oName+".insertCustomTag("+j+")\" "+
							"style=\"padding:1px;padding-left:5px;padding-right:5px;font-family:tahoma;font-size:11px;color:black;\" "+
							"onmouseover=\"this.style.backgroundColor='#708090';this.style.color='#FFFFFF';\" "+
							"onmouseout=\"this.style.backgroundColor='';this.style.color='#000000';\" unselectable=on align=center>"+
							this.arrCustomTag[j][0]+"</td></tr>";

						if(j==14||j==29||j==44||j==59||j==74||j==89||j==104)
							{
							if(j!=this.arrCustomTag.length-1)
								{
								sHTMLDropMenus+="</table>";
								sHTMLDropMenus+="</td><td valign=top style='padding:0px;border-left:#80788D 1 solid'>";//main
								sHTMLDropMenus+="<table cellpadding=0 cellspacing=0>";
								}
							}
						}
					sHTMLDropMenus+="</table>";
					//~~~~

					sHTMLDropMenus+="</td></tr></table>";
					sHTMLIcons+=this.writeDropDown("btnCustomTag"+this.oName,this.oName+".dropShow(this,dropCustomTag"+this.oName+")","btnCustomTag.gif",getTxt("Tags"),60);
					}
				break;
			case "Image":
				if(this.btnImage)sHTMLIcons+=this.writeIconStandard("btnImage"+this.oName,this.oName+".hide();modelessDialogShow('"+this.scriptPath+"image.htm',440,351)","btnImage.gif",getTxt("Image"));
				break;
			case "Flash":
				if(this.btnFlash)sHTMLIcons+=this.writeIconStandard("btnFlash"+this.oName,this.oName+".hide();modelessDialogShow('"+this.scriptPath+"flash.htm',340,275)","btnFlash.gif",getTxt("Flash"));
				break;
			case "Media":
				if(this.btnMedia)sHTMLIcons+=this.writeIconStandard("btnMedia"+this.oName,this.oName+".hide();modelessDialogShow('"+this.scriptPath+"media.htm',340,272)","btnMedia.gif",getTxt("Media"));
				break;
			case "ContentBlock":
				if(this.btnContentBlock)sHTMLIcons+=this.writeIconStandard("btnContentBlock"+this.oName,this.oName+".hide();"+this.cmdContentBlock,"btnContentBlock.gif",getTxt("Content Block"));
				break;
			case "InternalLink":
				if(this.btnInternalLink)sHTMLIcons+=this.writeIconStandard("btnInternalLink"+this.oName,this.oName+".hide();"+this.cmdInternalLink,"btnInternalLink.gif",getTxt("Internal Link"));
				break;
			case "CustomObject":
				if(this.btnCustomObject)sHTMLIcons+=this.writeIconStandard("btnCustomObject"+this.oName,this.oName+".hide();"+this.cmdCustomObject,"btnCustomObject.gif",getTxt("Object"));
				break;
			case "Table":
				if(this.btnTable)
					{
					sHTMLDropMenus+="<table id=dropTable"+this.oName+" cellpadding=0 cellspacing=0 "+
						"style='z-index:1;display:none;position:absolute;border:#80788D 1px solid;"+
						"cursor:default;background-color:#fbfbfd;' unselectable=on>"+
						"<tr><td id=\"mnuTableSize"+this.oName+"\" onclick=\"if(this.style.color!='gray'){modelessDialogShow('"+this.scriptPath+"table_size.htm',240,262);"+
						"	dropTable"+this.oName+".style.display='none'}\""+
						"	style=\"padding:2px;padding-top:1px;font-family:Tahoma;font-size:11px;color:black\""+
						"	onmouseover=\"if(this.style.color!='gray'){this.style.backgroundColor='#708090';this.style.color='#FFFFFF';}\""+
						"	onmouseout=\"if(this.style.color!='gray'){this.style.backgroundColor='';this.style.color='#000000';}\" unselectable=on>"+getTxt("Table Size")+" </td></tr>"+
						"<tr><td id=\"mnuTableEdit"+this.oName+"\" onclick=\"if(this.style.color!='gray'){modelessDialogShow('"+this.scriptPath+"table_edit.htm',358,380);"+
						"	dropTable"+this.oName+".style.display='none'}\""+
						"	style=\"padding:2px;padding-top:1px;font-family:Tahoma;font-size:11px;color:black\""+
						"	onmouseover=\"if(this.style.color!='gray'){this.style.backgroundColor='#708090';this.style.color='#FFFFFF';}\""+
						"	onmouseout=\"if(this.style.color!='gray'){this.style.backgroundColor='';this.style.color='#000000';}\" unselectable=on>"+getTxt("Edit Table")+" </td></tr>"+
						"<tr><td id=\"mnuCellEdit"+this.oName+"\" onclick=\"if(this.style.color!='gray'){modelessDialogShow('"+this.scriptPath+"table_editCell.htm',427,440);"+
						"	dropTable"+this.oName+".style.display='none'}\""+
						"	style=\"padding:2px;padding-top:1px;font-family:Tahoma;font-size:11px;color:black\""+
						"	onmouseover=\"if(this.style.color!='gray'){this.style.backgroundColor='#708090';this.style.color='#FFFFFF';}\""+
						"	onmouseout=\"if(this.style.color!='gray'){this.style.backgroundColor='';this.style.color='#000000';}\" unselectable=on>"+getTxt("Edit Cell")+" </td></tr>"+
						"</table>";

					sHTMLDropMenus+="<table width=195 id=dropTableCreate"+this.oName+" onmouseout='doOut_TabCreate();event.cancelBubble=true' style='position:absolute;display:none;cursor:default;background:#f3f3f8;border:#8a867a 1px solid;' cellpadding=0 cellspacing=2 border=0 unselectable=on>";
					for(var m=0;m<8;m++)
						{
						sHTMLDropMenus+="<tr>";
						for(var n=0;n<8;n++)
							{
							sHTMLDropMenus+="<td onclick='"+this.oName+".doClick_TabCreate()' onmouseover='doOver_TabCreate()' style='background:#ffffff;font-size:1px;border:#8a867a 1px solid;width:20px;height:20px;' unselectable=on>&nbsp;</td>";
							}
						sHTMLDropMenus+="</tr>";
						}
					sHTMLDropMenus+="<tr><td colspan=8 onclick=\""+this.oName+".hide();modelessDialogShow('"+this.scriptPath+"table_insert.htm',300,322);\" onmouseover=\"this.innerText='"+getTxt("Advanced Table Insert")+"';this.style.border='#777777 1px solid';this.style.backgroundColor='#8d9aa7';this.style.color='#ffffff'\" onmouseout=\"this.style.border='#f3f3f8 1px solid';this.style.backgroundColor='#f3f3f8';this.style.color='#000000'\" align=center style='font-family:verdana;font-size:10px;font-color:black;border:#f3f3f8 1px solid;' unselectable=on>"+getTxt("Advanced Table Insert")+"</td></tr>";
					sHTMLDropMenus+="</table>";

					sHTMLIcons+=this.writeIconStandard("btnTable"+this.oName,this.oName+".dropShow(this,dropTableCreate"+this.oName+")","btnTable.gif",getTxt("Insert Table"));
					sHTMLIcons+=this.writeIconStandard("btnTableEdit"+this.oName,this.oName+".dropShow(this,dropTable"+this.oName+")","btnTableEdit.gif",getTxt("Edit Table/Cell"));
					}
				break;
			case "Guidelines":
				if(this.btnGuidelines)sHTMLIcons+=this.writeIconStandard("btnGuidelines"+this.oName,this.oName+".runtimeBorder(true)","btnGuideline.gif",getTxt("Show/Hide Guidelines"));
				break;
			case "Absolute":
				if(this.btnAbsolute)sHTMLIcons+=this.writeIconStandard("btnAbsolute"+this.oName,this.oName+".makeAbsolute()","btnAbsolute.gif",getTxt("Absolute"));
				break;
			case "Characters":
				if(this.btnCharacters)sHTMLIcons+=this.writeIconStandard("btnCharacters"+this.oName,this.oName+".hide();modelessDialogShow('"+this.scriptPath+"characters.htm',495,162)","btnSymbol.gif",getTxt("Special Characters"));
				break;
			case "Line":
				if(this.btnLine)sHTMLIcons+=this.writeIconStandard("btnLine"+this.oName,this.oName+".doCmd('InsertHorizontalRule')","btnLine.gif",getTxt("Line"));
				break;
			case "Form":
				if(this.btnForm)
					{
					var arrFormMenu = [[getTxt("Form"),"form_form.htm","280","177"],
						[getTxt("Text Field"),"form_text.htm","285","289"],
						[getTxt("List"),"form_list.htm","295","332"],
						[getTxt("Checkbox"),"form_check.htm","235","174"],
						[getTxt("Radio Button"),"form_radio.htm","235","177"],
						[getTxt("Hidden Field"),"form_hidden.htm","235","152"],
						[getTxt("File Field"),"form_file.htm","235","132"],
						[getTxt("Button"),"form_button.htm","235","174"]];
					sHTMLIcons+=this.writeIconStandard("btnForm"+this.oName,this.oName+".dropShow(this,dropForm"+this.oName+")","btnForm.gif",getTxt("Form Editor"));
					sHTMLDropMenus+="<table id=dropForm"+this.oName+" cellpadding=0 cellspacing=0 "+
						"style='z-index:1;display:none;position:absolute;border:#80788D 1px solid;"+
						"cursor:default;background-color:#fbfbfd;' unselectable=on>";
					for(var j=0;j<arrFormMenu.length;j++)
						{
						sHTMLDropMenus+="<tr><td onclick=\"modelessDialogShow('"+this.scriptPath + arrFormMenu[j][1]+"',"+arrFormMenu[j][2]+","+arrFormMenu[j][3]+");"+
						"dropForm"+this.oName+".style.display='none'\""+
						" style=\"padding:2px;padding-top:1px;font-family:Tahoma;font-size:11px;color:black;\" "+
						"onmouseover=\"this.style.backgroundColor='#708090';this.style.color='#FFFFFF';\" "+
						"onmouseout=\"this.style.backgroundColor='';this.style.color='#000000';\" unselectable=on>"+arrFormMenu[j][0]+"</td></tr>";
						}
					sHTMLDropMenus+="</table>";
					}
				break;
			case "RemoveFormat":
				if(this.btnRemoveFormat)sHTMLIcons+=this.writeIconStandard("btnRemoveFormat"+this.oName,this.oName+".doClean()","btnRemoveFormat.gif",getTxt("Remove Formatting"));
				break;
			case "HTMLFullSource":
				if(this.btnHTMLFullSource)sHTMLIcons+=this.writeIconStandard("btnHTMLFullSource"+this.oName,"setActiveEditor('"+this.oName+"');"+this.oName+".hide();modalDialogShow('"+this.scriptPath+"source_html_full.htm',600,450);","btnSource.gif",getTxt("View/Edit Source"));
				break;
			case "HTMLSource":
				if(this.btnHTMLSource)sHTMLIcons+=this.writeIconStandard("btnHTMLSource"+this.oName,"setActiveEditor('"+this.oName+"');"+this.oName+".hide();modalDialogShow('"+this.scriptPath+"source_html.htm',600,450);","btnSource.gif",getTxt("View/Edit HTMLSource"));
				break;
			case "XHTMLFullSource":
				if(this.btnXHTMLFullSource)sHTMLIcons+=this.writeIconStandard("btnXHTMLFullSource"+this.oName,"setActiveEditor('"+this.oName+"');"+this.oName+".hide();modalDialogShow('"+this.scriptPath+"source_xhtml_full.htm',600,450);","btnSource.gif",getTxt("View/Edit Source"));
				break;
			case "XHTMLSource":
				if(this.btnXHTMLSource)sHTMLIcons+=this.writeIconStandard("btnXHTMLSource"+this.oName,"setActiveEditor('"+this.oName+"');"+this.oName+".hide();modalDialogShow('"+this.scriptPath+"source_xhtml.htm',600,450);","btnSource.gif",getTxt("View/Edit XHTMLSource"));
				break;
			case "ClearAll":
				if(this.btnClearAll)sHTMLIcons+=this.writeIconStandard("btnClearAll"+this.oName,this.oName+".clearAll()","btnDelete.gif","清除内容");
				break;
			default:
				for(j=0;j<this.arrCustomButtons.length;j++)
					{
					if(sButtonName==this.arrCustomButtons[j][0])
						{
						sCbName=this.arrCustomButtons[j][0];
						//sCbCommand=this.arrCustomButtons[j][1];
						sCbCaption=this.arrCustomButtons[j][2];
						sCbImage=this.arrCustomButtons[j][3];
						if(this.arrCustomButtons[j][4])
							sHTMLIcons+=this.writeIconStandard("btn"+sCbName+this.oName,"eval("+this.oName+".arrCustomButtons["+j+"][1])",sCbImage,sCbCaption,this.arrCustomButtons[j][4]);
						else
							sHTMLIcons+=this.writeIconStandard("btn"+sCbName+this.oName,"eval("+this.oName+".arrCustomButtons["+j+"][1])",sCbImage,sCbCaption);
						}
					}
				break;
			}
		}

	var sHTML="";

	sHTML+="<iframe  name=idFixZIndex"+this.oName+" id=idFixZIndex"+this.oName+"  frameBorder=0 style='display:none;position:absolute;filter:progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)' src='"+this.scriptPath+"blank.gif' ></iframe>"; //src='javascript:;'
	sHTML+="<table id=idArea"+this.oName+" name=idArea"+this.oName+" border=0 "+
		"cellpadding=0 cellspacing=0 width='"+this.width+"' height='"+this.height+"'>"+
		"<tr><td colspan=2 style=\"padding:0px;padding-left:1;border:#cfcfcf 1px solid;border-bottom:0;background:url('"+this.scriptPath+"icons/bg.gif')\">"+
		"<table cellpadding=0 cellspacing=0><tr><td dir=ltr style='padding:0px'>"+
		sHTMLIcons+
		"</td></tr></table>"+
		"</td></tr>"+
		"<tr id=idTagSelTopRow"+this.oName+"><td colspan=2 id=idTagSelTop"+this.oName+" height=0 style='padding:0px'></td></tr>";

	sHTML+="<tr><td colspan=2 valign=top height=100% style='padding:0px;background:white'>";

	sHTML+="<table border=0 cellpadding=0 cellspacing=0 width=100% height=100%><tr><td width=100% height=100% style='padding:0px'>";//StyleSelect

	if(this.IsSecurityRestricted)
		sHTML+="<iframe security='restricted' style='width:100%;height:100%;' src='"+this.scriptPath+"blank.gif'"+
			" name=idContent"+ this.oName + " id=idContent"+this.oName+
			" contentEditable=true></iframe>";//prohibit running ActiveX controls
	else
		sHTML+="<iframe style='width:100%;height:100%;' src='"+this.scriptPath+"blank.gif'"+
			" name=idContent"+ this.oName + " id=idContent"+this.oName+
			" contentEditable=true ></iframe>";

	//Paste From Word
	sHTML+="<iframe style='width:1px;height:1px;overflow:auto;' src='"+this.scriptPath+"blank.gif'"+
		" name=idContentWord"+ this.oName +" id=idContentWord"+ this.oName+
		" contentEditable=true onfocus='"+this.oName+".hide()'></iframe>";

	sHTML+="</td><td id=idStyles"+this.oName+" style='padding:0px;background:#E9E8F2' valign=top></td></tr></table>"//StyleSelect

	sHTML+="</td></tr>";
	sHTML+="<tr id=idTagSelBottomRow"+this.oName+"><td colspan=2 id=idTagSelBottom"+this.oName+" style='padding:0px;'></td></tr>";
	sHTML+="</table>";

	sHTML+=sHTMLDropMenus;//dropdown
	
	sHTML+="<input type=submit name=iwe_btnSubmit"+this.oName+" id=iwe_btnSubmit"+this.oName+" value=SUBMIT style='display:none' >";//hidden submit button

	document.write(sHTML);

	//Render Color Picker (forecolor)
	this.oColor1.url=this.scriptPath+"color_picker_fg.htm";
	this.oColor1.onShow = new Function(this.oName+".hide()");
	this.oColor1.onMoreColor = new Function(this.oName+".hide()");
	this.oColor1.onPickColor = new Function(this.oName+".applyColor('ForeColor',eval('"+this.oName+"').oColor1.color)");
	this.oColor1.onRemoveColor = new Function(this.oName+".applyColor('ForeColor','')");
	this.oColor1.txtCustomColors=getTxt("Custom Colors");
	this.oColor1.txtMoreColors=getTxt("More Colors...");
	this.oColor1.RENDER();

	//Render Color Picker (backcolor)
	this.oColor2.url=this.scriptPath+"color_picker_bg.htm";
	this.oColor2.onShow = new Function(this.oName+".hide()");
	this.oColor2.onMoreColor = new Function(this.oName+".hide()");
	this.oColor2.onPickColor = new Function(this.oName+".applyColor('BackColor',eval('"+this.oName+"').oColor2.color)");
	this.oColor2.onRemoveColor = new Function(this.oName+".applyColor('BackColor','')");
	this.oColor2.txtCustomColors=getTxt("Custom Colors");
	this.oColor2.txtMoreColors=getTxt("More Colors...");
	this.oColor2.RENDER();

	if(this.useTagSelector)
		{
		if(this.TagSelectorPosition=="bottom")this.TagSelectorPosition="top";
		else this.TagSelectorPosition="bottom";
		this.moveTagSelector()
		}

	//paste from word temp storage
	/*var oWord=eval("idContentWord"+this.oName);
	oWord.document.designMode="on";
	oWord.document.open("text/html","replace");
	oWord.document.write("<html><head></head><body></body></html>");
	oWord.document.close();
	oWord.document.body.contentEditable=true;*/

	oUtil.oName=this.oName;//default active editor
	oUtil.oEditor=eval("idContent"+this.oName);
	oUtil.obj=eval(this.oName);

	oUtil.arrEditor.push(this.oName);

	if(this.btnTable)
		{
		this.arrElm[0]=this.getElm("btnTableEdit");
		this.arrElm[1]=this.getElm("mnuTableSize");
		this.arrElm[2]=this.getElm("mnuTableEdit");
		this.arrElm[3]=this.getElm("mnuCellEdit");
		}
	if(this.btnParagraph)this.arrElm[4]=this.getElm("btnParagraph");
	if(this.btnFontName)this.arrElm[5]=this.getElm("btnFontName");
	if(this.btnFontSize)this.arrElm[6]=this.getElm("btnFontSize");
	if(this.btnCut)this.arrElm[7]=this.getElm("btnCut");
	if(this.btnCopy)this.arrElm[8]=this.getElm("btnCopy");
	if(this.btnPaste)this.arrElm[9]=this.getElm("btnPaste");
	if(this.btnPasteWord)this.arrElm[10]=this.getElm("btnPasteWord");
	if(this.btnPasteText)this.arrElm[11]=this.getElm("btnPasteText");
	if(this.btnUndo)this.arrElm[12]=this.getElm("btnUndo");
	if(this.btnRedo)this.arrElm[13]=this.getElm("btnRedo");
	if(this.btnBold)this.arrElm[14]=this.getElm("btnBold");
	if(this.btnItalic)this.arrElm[15]=this.getElm("btnItalic");
	if(this.btnUnderline)this.arrElm[16]=this.getElm("btnUnderline");
	if(this.btnStrikethrough)this.arrElm[17]=this.getElm("btnStrikethrough");
	if(this.btnSuperscript)this.arrElm[18]=this.getElm("btnSuperscript");
	if(this.btnSubscript)this.arrElm[19]=this.getElm("btnSubscript");
	if(this.btnNumbering)this.arrElm[20]=this.getElm("btnNumbering");
	if(this.btnBullets)this.arrElm[21]=this.getElm("btnBullets");
	if(this.btnJustifyLeft)this.arrElm[22]=this.getElm("btnJustifyLeft");
	if(this.btnJustifyCenter)this.arrElm[23]=this.getElm("btnJustifyCenter");
	if(this.btnJustifyRight)this.arrElm[24]=this.getElm("btnJustifyRight");
	if(this.btnJustifyFull)this.arrElm[25]=this.getElm("btnJustifyFull");
	if(this.btnIndent)this.arrElm[26]=this.getElm("btnIndent");
	if(this.btnOutdent)this.arrElm[27]=this.getElm("btnOutdent");
	if(this.btnLTR)this.arrElm[28]=this.getElm("btnLTR");
	if(this.btnRTL)this.arrElm[29]=this.getElm("btnRTL");
	if(this.btnForeColor)this.arrElm[30]=this.getElm("btnForeColor");
	if(this.btnBackColor)this.arrElm[31]=this.getElm("btnBackColor");
	if(this.btnLine)this.arrElm[32]=this.getElm("btnLine");

	//Normalize button position if the editor is placed in relative positioned element
	eval("idArea"+this.oName).style.position="absolute";
	window.setTimeout("eval('idArea"+this.oName+"').style.position='';",1);

	var arrA = String(this.preloadHTML).match(/<HTML[^>]*>/ig);
	if(arrA)
		{//full html
		this.loadHTML("");
		//this.preloadHTML is required here. Can't use sPreloadHTML as in:
		//window.setTimeout(this.oName+".putHTML("+sPreloadHTML+")",0);
		window.setTimeout(this.oName+".putHTML("+this.oName+".preloadHTML)",0);
		//window.setTimeout utk fix swf loading.
		//Utk loadHTML & putHTML yg di SourceEditor tdk masalah
		}
	else
		{
		this.loadHTML(sPreloadHTML)
		}
	}

function iwe_getElm(s)
	{
	return document.getElementById(s+this.oName)
	}

/*** COLOR PICKER ***/
var arrColorPickerObjects=[];
function ColorPicker(sName,sParent)
	{
	this.oParent=sParent;
	if(sParent)
		{
		this.oName=sParent+"."+sName;
		this.oRenderName=sName+sParent;
		}
	else
		{
		this.oName=sName;
		this.oRenderName=sName;
		}
	arrColorPickerObjects.push(this.oName);

	this.url="color_picker.htm";
	this.onShow=function(){return true;};
	this.onHide=function(){return true;};
	this.onPickColor=function(){return true;};
	this.onRemoveColor=function(){return true;};
	this.onMoreColor=function(){return true;};
	this.show=showColorPicker;
	this.hide=hideColorPicker;
	this.hideAll=hideColorPickerAll;
	this.color;
	this.customColors=[];
	this.refreshCustomColor=refreshCustomColor;
	this.isActive=false;
	this.txtCustomColors="Custom Colors";
	this.txtMoreColors="More Colors...";
	this.align="left";
	this.currColor="#ffffff";//default current color
	this.RENDER=drawColorPicker;
	}
function drawColorPicker()
	{
	var arrColors=[["#800000","#8b4513","#006400","#2f4f4f","#000080","#4b0082","#800080","#000000"],
				["#ff0000","#daa520","#6b8e23","#708090","#0000cd","#483d8b","#c71585","#696969"],
				["#ff4500","#ffa500","#808000","#4682b4","#1e90ff","#9400d3","#ff1493","#a9a9a9"],
				["#ff6347","#ffd700","#32cd32","#87ceeb","#00bfff","#9370db","#ff69b4","#dcdcdc"],
				["#ffdab9","#ffffe0","#98fb98","#e0ffff","#87cefa","#e6e6fa","#dda0dd","#ffffff"]];
	var sHTMLColor="<table id=dropColor"+this.oRenderName+" style=\"z-index:1;display:none;position:absolute;border:#9b95a6 1px solid;cursor:default;background-color:#E9E8F2;padding:2px\" unselectable=on cellpadding=0 cellspacing=0 width=143 height=109><tr><td unselectable=on style='padding:0px;'>";
	sHTMLColor+="<table align=center cellpadding=0 cellspacing=0 border=0 unselectable=on>";
	for(var i=0;i<arrColors.length;i++)
		{
		sHTMLColor+="<tr>";
		for(var j=0;j<arrColors[i].length;j++)
			sHTMLColor+="<td onclick=\""+this.oName+".color='"+arrColors[i][j]+"';"+this.oName+".onPickColor();"+this.oName+".currColor='"+arrColors[i][j]+"';"+this.oName+".hideAll()\" onmouseover=\"this.style.border='#777777 1px solid'\" onmouseout=\"this.style.border='#E9E8F2 1px solid'\" style=\"cursor:default;padding:1px;border:#E9E8F2 1px solid;\" unselectable=on>"+
				"<table style='margin:0;width:13px;height:13px;background:"+arrColors[i][j]+";border:white 1px solid' cellpadding=0 cellspacing=0 unselectable=on>"+
				"<tr><td unselectable=on></td></tr>"+
				"</table></td>";
		sHTMLColor+="</tr>";
		}

	//~~~ custom colors ~~~~
	sHTMLColor+="<tr><td colspan=8 id=idCustomColor"+this.oRenderName+" style='padding:0px;'></td></tr>";

	//~~~ remove color & more colors ~~~~
	sHTMLColor+= "<tr>";
	sHTMLColor+= "<td unselectable=on style='padding:0px;'>"+
		"<table style='margin-left:1px;width:14px;height:14px;background:#E9E8F2;' cellpadding=0 cellspacing=0 unselectable=on>"+
		"<tr><td onclick=\""+this.oName+".onRemoveColor();"+this.oName+".currColor='';"+this.oName+".hideAll()\" onmouseover=\"this.style.border='#777777 1px solid'\" onmouseout=\"this.style.border='white 1px solid'\" style=\"cursor:default;padding:1px;border:white 1px solid;font-family:verdana;font-size:10px;font-color:black;line-height:9px;\" align=center valign=top unselectable=on>x</td></tr>"+
		"</table></td>";
	sHTMLColor+= "<td colspan=7 unselectable=on style='padding:0px;'>"+
		"<table style='margin:1px;width:117px;height:16px;background:#E9E8F2;border:white 1px solid' cellpadding=0 cellspacing=0 unselectable=on>"+
		"<tr><td onclick=\""+this.oName+".onMoreColor();"+this.oName+".hideAll();window.showModelessDialog('"+this.url+"',[window,'"+this.oName+"'],'dialogWidth:432px;dialogHeight:427px;edge:Raised;center:1;help:0;resizable:1;')\" onmouseover=\"this.style.border='#777777 1px solid';this.style.background='#8d9aa7';this.style.color='#ffffff'\" onmouseout=\"this.style.border='#E9E8F2 1px solid';this.style.background='#E9E8F2';this.style.color='#000000'\" style=\"cursor:default;padding:1px;border:#efefef 1px solid\" style=\"font-family:verdana;font-size:9px;font-color:black;line-height:9px;padding:1px\" align=center valign=top nowrap unselectable=on>"+this.txtMoreColors+"</td></tr>"+
		"</table></td>";
	sHTMLColor+= "</tr>";

	sHTMLColor+= "</table>";
	sHTMLColor+="</td></tr></table>";
	document.write(sHTMLColor);
	}
function refreshCustomColor()
	{
	this.customColors=eval(this.oParent).customColors;//[CUSTOM] (Get from public definition)

	if(this.customColors.length==0)
		{
		eval("idCustomColor"+this.oRenderName).innerHTML="";
		return;
		}
	sHTML="<table cellpadding=0 cellspacing=0 width=100%><tr><td colspan=8 style=\"font-family:verdana;font-size:9px;font-color:black;line-height:9px;padding:1\">"+this.txtCustomColors+":</td></tr></table>";
	sHTML+="<table cellpadding=0 cellspacing=0><tr>";
	for(var i=0;i<this.customColors.length;i++)
		{
		if(i<22)
			{
			if(i==8||i==16||i==24||i==32)sHTML+="</tr></table><table cellpadding=0 cellspacing=0><tr>";
			sHTML+="<td onclick=\""+this.oName+".color='"+this.customColors[i]+"';"+this.oName+".onPickColor()\" onmouseover=\"this.style.border='#777777 1px solid'\" onmouseout=\"this.style.border='#E9E8F2 1px solid'\" style=\"cursor:default;padding:1px;border:#E9E8F2 1px solid;\" unselectable=on>"+
				"	<table  style='margin:0;width:13;height:13;background:"+this.customColors[i]+";border:white 1px solid' cellpadding=0 cellspacing=0 unselectable=on>"+
				"	<tr><td unselectable=on></td></tr>"+
				"	</table>"+
				"</td>";
			}
		}
	sHTML+="</tr></table>";
	eval("idCustomColor"+this.oRenderName).innerHTML=sHTML;
	}
function showColorPicker(oEl)
	{
	this.onShow();
	this.hideAll();
	var box=eval("dropColor"+this.oRenderName);
	box.style.display="block";
	var nTop=0;
	var nLeft=0;

	oElTmp=oEl;
	while(oElTmp.tagName!="BODY" && oElTmp.tagName!="HTML")
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

	if(this.align=="left") box.style.left=nLeft+oUtil.obj.dropLeftAdjustment;
	else box.style.left=nLeft-143+oEl.offsetWidth+oUtil.obj.dropLeftAdjustment;

	box.style.top=nTop+1+oUtil.obj.dropTopAdjustment;//[CUSTOM]
	//box.style.top=nTop+1+oEl.offsetHeight;//[CUSTOM]

	this.isActive=true;
	this.refreshCustomColor();
	}
function hideColorPicker()
	{
	this.onHide();
	var box=eval("dropColor"+this.oRenderName);
	box.style.display="none";
	this.isActive=false;
	}
function hideColorPickerAll()
	{
	for(var i=0;i<arrColorPickerObjects.length;i++)
		{
		var box=eval("dropColor"+eval(arrColorPickerObjects[i]).oRenderName);
		box.style.display="none";
		eval(arrColorPickerObjects[i]).isActive=false;
		}
	}

/*** CONTENT ***/
function loadHTML(sHTML)//hanya utk first load.
	{
	var oEditor=eval("idContent"+this.oName);
	
	/*** Apply this.css to the content ***/
	var sStyle="";
	if(this.css!="") sStyle="<link href='"+this.css+"' rel='stylesheet' type='text/css'>"

	var oDoc=oEditor.document.open("text/html","replace");
	if(this.publishingPath!="")
		{
		var arrA = String(this.preloadHTML).match(/<base[^>]*>/ig);
		if(!arrA)
			{//if no <base> found
			sHTML=this.docType+"<HTML><HEAD><BASE HREF=\""+this.publishingPath+"\"/>"+this.headContent+sStyle+"</HEAD><BODY contentEditable=true>" + sHTML + "</BODY></HTML>";
			//kalau cuma tambah <HTML><HEAD></HEAD><BODY.. tdk apa2.
			}
		}
	else
		{
		sHTML=this.docType+"<HTML><HEAD>"+this.headContent+sStyle+"</HEAD><BODY contentEditable=true>"+sHTML+"</BODY></HTML>";
		}
	oDoc.write(sHTML);
	oDoc.close();

	oEditor.document.body.contentEditable=true;
	oEditor.document.execCommand("2D-Position", true, true);//make focus
	oEditor.document.execCommand("MultipleSelection", true, true);//make focus
	oEditor.document.execCommand("LiveResize", true, true);//make focus

	//RealTime
	//oEditor.document.body.onkeydown = new Function("editorDoc_onkeydown('"+this.oName+"')");
	oEditor.document.body.onkeyup = new Function("editorDoc_onkeyup('"+this.oName+"')");
	oEditor.document.body.onmouseup = new Function("editorDoc_onmouseup('"+this.oName+"')");
	if(location.href.indexOf("linkeyeditor.html")!=-1)
	{
		oEditor.document.body.oncontextmenu=new Function("showContextmenu('"+this.oName+"');return false;");
	}
	//<br> or <p>
	oEditor.document.body.onkeydown=new Function("doKeyPress(eval('idContent"+this.oName+"').event,'"+this.oName+"')");

	//*** RUNTIME STYLES ***
	this.runtimeBorder(false);
	this.runtimeStyles();
	//***********************
	
	//Save for Undo
	oEditor.document.body.onpaste = new Function(this.oName+".doOnPaste()");
	oEditor.document.body.oncut = new Function(this.oName+".saveForUndo()");

	//fix undisplayed content (new)
	oEditor.document.body.style.lineHeight="1.2";
	oEditor.document.body.style.lineHeight="";

	//fix undisplayed content (old)
	if(this.initialRefresh)
		{
		oEditor.document.execCommand("SelectAll");
		window.setTimeout("eval('idContentWord"+this.oName+"').document.execCommand('SelectAll');",0);
		}

	/*** Apply this.arrStyle to the content ***/
	if(this.arrStyle.length>0)
		{
		var oElement=oEditor.document.createElement("<STYLE>");
		oEditor.document.documentElement.childNodes[0].appendChild(oElement);
		for(var i=0;i<this.arrStyle.length;i++)
			{
			selector=this.arrStyle[i][0];
			style=this.arrStyle[i][3];
			oEditor.document.styleSheets(0).addRule(selector,style);
			}
		}

	this.cleanDeprecated();
	}
function putHTML(sHTML)//used by source editor
	{
	var oEditor=eval("idContent"+this.oName);

	//save doctype (if any/if not body only)
	var arrA=String(sHTML).match(/<!DOCTYPE[^>]*>/ig);
	if(arrA)
		for(var i=0;i<arrA.length;i++)
			{
			this.docType=arrA[i];
			}
	else this.docType="";//back to default value

	//save html (if any/if not body only)
	var arrB=String(sHTML).match(/<HTML[^>]*>/ig);
	if(arrB)
		for(var i=0;i<arrB.length;i++)
			{
			s=arrB[i];
			s=s.replace(/\"[^\"]*\"/ig,function(x){
						x=x.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/'/g, "&apos;").replace(/\s+/ig,"#_#");
						return x});
			s=s.replace(/<([^ >]*)/ig,function(x){return x.toLowerCase()});					
			s=s.replace(/ ([^=]+)=([^" >]+)/ig," $1=\"$2\"");
			s=s.replace(/ ([^=]+)=/ig,function(x){return x.toLowerCase()});
			s=s.replace(/#_#/ig," ");
			this.html=s;
			}
	else this.html="<html>";//back to default value

	//Dalam pengeditan kalau pakai doctype,
	//membuat mouse tdk bisa di-klik di empty area
	//sHTML=String(sHTML).replace(/<!DOCTYPE[^>]*>/ig,"");
	if(this.publishingPath!="")
		{
		var arrA = sHTML.match(/<base[^>]*>/ig);
		if(!arrA)
			{
			sHTML="<BASE HREF=\""+this.publishingPath+"\"/>"+sHTML;
			}
		}

	var oDoc=oEditor.document.open("text/html","replace");
	sHTML="<link href='"+this.css+"' rel='stylesheet' type='text/css'>"+sHTML;
	oDoc.write(sHTML);
	oDoc.close();
	oEditor.document.body.contentEditable=true;
	//oEditor.document.body.onload=new Function("eval('idContent"+this.oName+"').document.body.contentEditable=true");
	oEditor.document.execCommand("2D-Position",true,true);
	oEditor.document.execCommand("MultipleSelection",true,true);
	oEditor.document.execCommand("LiveResize",true,true);

	//RealTime
	//oEditor.document.body.onkeydown=new Function("editorDoc_onkeydown('"+this.oName+"')");
	oEditor.document.body.onkeyup=new Function("editorDoc_onkeyup('"+this.oName+"')");
	oEditor.document.body.onmouseup=new Function("editorDoc_onmouseup('"+this.oName+"')");
    oEditor.document.body.oncontextmenu=new Function("showContextmenu('"+this.oName+"');return false;");

	//<br> or <p>
	oEditor.document.body.onkeydown=new Function("doKeyPress(eval('idContent"+this.oName+"').event,'"+this.oName+"')");

	//*** RUNTIME STYLES ***
	this.runtimeBorder(false);
	this.runtimeStyles();
	//***********************
	
	/*** No need to apply this.css or this.arrStyle to the content 
	assuming that the content has stylesheet applied.
	***/

	this.cleanDeprecated();
	}
function getTextBody()
	{
	var oEditor=eval("idContent"+this.oName);
	return oEditor.document.body.innerText;
	}
function getHTML()
	{
	var oEditor=eval("idContent"+this.oName);
	this.cleanDeprecated();

	sHTML=oEditor.document.documentElement.outerHTML;
	sHTML=String(sHTML).replace(/ contentEditable=true/g,"");
	sHTML = String(sHTML).replace(/\<PARAM NAME=\"Play\" VALUE=\"0\">/ig,"<PARAM NAME=\"Play\" VALUE=\"-1\">");
	sHTML=this.docType+sHTML;//restore doctype (if any)
	return sHTML;
	}
function getHTMLBody()
	{
	var oEditor=eval("idContent"+this.oName);
	this.cleanDeprecated();

	sHTML=oEditor.document.body.innerHTML;
	sHTML=String(sHTML).replace(/ contentEditable=true/g,"");
	sHTML = String(sHTML).replace(/\<PARAM NAME=\"Play\" VALUE=\"0\">/ig,"<PARAM NAME=\"Play\" VALUE=\"-1\">");
	return sHTML;
	}
var sBaseHREF="";
function getXHTML()
	{
	var oEditor=eval("idContent"+this.oName);
	this.cleanDeprecated();

	//base handling
	sHTML=oEditor.document.documentElement.outerHTML;
	var arrTmp=sHTML.match(/<BASE([^>]*)>/ig);
	if(arrTmp!=null)sBaseHREF=arrTmp[0];
	for(var i=0;i<oEditor.document.all.length;i++)
		if(oEditor.document.all[i].tagName=="BASE")oEditor.document.all[i].removeNode();
	for(var i=0;i<oEditor.document.all.length;i++)
		if(oEditor.document.all[i].tagName=="BASE")oEditor.document.all[i].removeNode();
	//~~~~~~~~~~~~~
	sBaseHREF=sBaseHREF.replace(/<([^ >]*)/ig,function(x){return x.toLowerCase()});						
	sBaseHREF=sBaseHREF.replace(/ [^=]+="[^"]+"/ig,function(x){
				x=x.replace(/\s+/ig,"#_#");
				x=x.replace(/^#_#/," ");
				return x});
	sBaseHREF=sBaseHREF.replace(/ ([^=]+)=([^" >]+)/ig," $1=\"$2\"");
	sBaseHREF=sBaseHREF.replace(/ ([^=]+)=/ig,function(x){return x.toLowerCase()});
	sBaseHREF=sBaseHREF.replace(/#_#/ig," ");

	sBaseHREF=sBaseHREF.replace(/>$/ig," \/>").replace(/\/ \/>$/ig,"\/>");
	//~~~~~~~~~~~~~

	sHTML=recur(oEditor.document.documentElement,"");
	sHTML=this.docType+this.html+sHTML+"\n</html>";//restore doctype (if any) & html
	sHTML=sHTML.replace(/<\/title>/,"<\/title>"+sBaseHREF);//restore base href

	return sHTML;
	}
function getXHTMLBody()
	{
	var oEditor=eval("idContent"+this.oName);
	this.cleanDeprecated();

	//base handling
	sHTML=oEditor.document.documentElement.outerHTML;
	var arrTmp=sHTML.match(/<BASE([^>]*)>/ig);
	if(arrTmp!=null)sBaseHREF=arrTmp[0];
	for(var i=0;i<oEditor.document.all.length;i++)
		if(oEditor.document.all[i].tagName=="BASE")oEditor.document.all[i].removeNode();
	for(var i=0;i<oEditor.document.all.length;i++)
		if(oEditor.document.all[i].tagName=="BASE")oEditor.document.all[i].removeNode();
	//~~~~~~~~~~~~~

	sHTML=recur(oEditor.document.body,"");
	return sHTML;
	}

function ApplyExternalStyle(oName)
	{	
	var oEditor=eval("idContent"+oName);
	var sTmp="";
	for(var j=0;j<oEditor.document.styleSheets.length;j++)
		{
		var myStyle=oEditor.document.styleSheets(j);

		//In full HTML editing: this will parse linked & embedded stylesheet
		//In Body content editing: this will parse all embedded/applied css & arrStyle.
		for(var i=0;i<myStyle.rules.length;i++)
			{
			sSelector=myStyle.rules.item(i).selectorText;	
			sCssText=myStyle.rules.item(i).style.cssText.replace(/"/g,"&quot;");
			var itemCount = sSelector.split(".").length;
			if(itemCount>1) 
				{
				sCaption=sSelector.split(".")[1];
				sTmp+=",[\""+sSelector+"\",true,\""+sCaption+"\",\""+ sCssText + "\"]";
				}
			else sTmp+=",[\""+sSelector+"\",false,\"\",\""+ sCssText + "\"]";
			}
		}
	var arrStyle = eval("["+sTmp.substr(1)+"]"); 
	eval(oName).arrStyle=arrStyle;//Update arrStyle property
	}

function doApplyStyle(oName,sClassName)
	{
	//Focus stuff
	if(!eval(oName).checkFocus())return;
	
	var oEditor=eval("idContent"+oName);
	var oSel=oEditor.document.selection.createRange();

	eval(oName).saveForUndo();

	if(oUtil.activeElement)
		{
		oElement=oUtil.activeElement;
		oElement.className=sClassName;
		}
	else if (oSel.parentElement)	
		{
		if(oSel.text=="")
			{
			oElement=oSel.parentElement();
			if(oElement.tagName=="BODY")return;
			oElement.className=sClassName;
			}
		else
			{
			//var idNewSpan=eval(oName).applySpan();
			//if(idNewSpan)idNewSpan.className=sClassName;
			eval(oName).applySpanStyle([],sClassName);
			}
		}
	else 
		{
		oElement=oSel.item(0);
		oElement.className=sClassName;
		}
	realTime(oName);
	}
function openStyleSelect()
	{
	if(!this.isCssLoaded)ApplyExternalStyle(this.oName);
	this.isCssLoaded=true;//make only 1 call to ApplyExternalStyle()

	var bShowStyles=false;
	var idStyles=document.getElementById("idStyles"+this.oName);
	if(idStyles.innerHTML!="")
		{//toggle
		if(idStyles.style.display=="")
			idStyles.style.display="none";
		else
			idStyles.style.display="";
		return;
		}
	idStyles.style.display="";
	
	var h=document.getElementById("idContent"+this.oName).offsetHeight-27;
	
	var arrStyle=this.arrStyle;
	var sHTML="";
	sHTML+="<div unselectable=on style='width:200px;margin:1px;margin-top:0;margin-right:2px;' align=right>"
	sHTML+="<table style='margin-right:1px;margin-bottom:3px;width:14px;height:14px;background:#E9E8F2;' cellpadding=0 cellspacing=0 unselectable=on>"+
		"<tr><td onclick=\""+this.oName+".openStyleSelect();\" onmouseover=\"this.style.border='#708090 1px solid';this.style.color='white';this.style.backgroundColor='9FA7BB'\" onmouseout=\"this.style.border='white 1px solid';this.style.color='black';this.style.backgroundColor=''\" style=\"cursor:default;padding:1px;border:white 1px solid;font-family:verdana;font-size:10px;font-color:black;line-height:9px;\" align=center valign=top unselectable=on>x</td></tr>"+
		"</table></div>";

	var sBody="";
	for(var i=0;i<arrStyle.length;i++)
		{
		sSelector=arrStyle[i][0];
		if(sSelector=="BODY")sBody=arrStyle[i][3];
		}

	sHTML+="<div unselectable=on style='overflow:auto;width:200px;height:"+h+"px;padding-left:3px;'>";
	sHTML+="<table name='tblStyles"+this.oName+"' id='tblStyles"+this.oName+"' cellpadding=0 cellspacing=0 style='background:#fcfcfc;"+sBody+";width:100%;height:100%;margin:0;'>";

	for(var i=0;i<arrStyle.length;i++)
		{
		sSelector=arrStyle[i][0];
		isOnSelection=arrStyle[i][1];
		
		sCssText=arrStyle[i][3];
		
		//sCssText=sCssText.replace(/COLOR: rgb\(255,255,255\)/,"COLOR: #000000");
		//sCssText=sCssText.replace(/COLOR: #ffffff/,"COLOR: #000000");
		//sCssText=sCssText.replace(/COLOR: white/,"COLOR: #000000");
		
		sCaption=arrStyle[i][2];
		if(isOnSelection)
			{
			if(sSelector.split(".").length>1)//sudah pasti
				{
				bShowStyles=true;
				sHTML+="<tr style=\"cursor:default\" onmouseover=\"if(this.style.marginRight!='1px'){this.style.background='"+this.styleSelectionHoverBg+"';this.style.color='"+this.styleSelectionHoverFg+"'}\" onmouseout=\"if(this.style.marginRight!='1px'){this.style.background='';this.style.color=''}\">";
				sHTML+="<td unselectable=on onclick=\"doApplyStyle('"+this.oName+"','"+sSelector.split(".")[1]+"')\" style='padding:2px;'>";
				if(sSelector.split(".")[0]=="")
					sHTML+="<span unselectable=on style=\""+sCssText+";margin:0;\">"+sCaption+"</span>";
				else
					sHTML+="<span unselectable=on style=\""+sCssText+";margin:0;\">"+sSelector+"</span>";
				sHTML+="</td></tr>";
				}
			}
		}
	sHTML+="<tr><td height=50%>&nbsp;</td></tr></table></div>";//50% spy di style selector tdk keloar scroll (kalau ada doctype)

	if(bShowStyles)document.getElementById("idStyles"+this.oName).innerHTML=sHTML;
	else{alert("No stylesheet found.")}
	}
	
/**** REALTIME ***/
/*function editorDoc_onkeydown(oName)
	{
	realTime(oName);
	}*/
function editorDoc_onkeyup(oName)
	{
	if(eval(oName).isAfterPaste)
		{
		eval(oName).cleanDeprecated();

		//*** RUNTIME STYLES ***
		eval(oName).runtimeBorder(false);
		eval(oName).runtimeStyles();
		//***********************
		eval(oName).isAfterPaste=false;
		}
	realTime(oName);
	}
function editorDoc_onmouseup(oName)
	{
	oUtil.activeElement=null;//focus ke editor, jgn pakai selection dari tag selector
	oUtil.oName=oName;oUtil.oEditor=eval("idContent"+oName);oUtil.obj=eval(oName);eval(oName).hide();//pengganti onfocus
	realTime(oName);
	}
function setActiveEditor(oName)
	{
	//eval(oName).focus();//focus first
	oUtil.oName=oName;
	oUtil.oEditor=eval("idContent"+oName);
	oUtil.obj=eval(oName);
	}
var arrTmp=[];
function GetElement(oElement,sMatchTag)//Used in realTime() only.
	{
	while (oElement!=null&&oElement.tagName!=sMatchTag)
		{
		if(oElement.tagName=="BODY")return null;
		oElement=oElement.parentElement;
		}
	return oElement;
	}
var arrTmp2=[];//TAG SELECTOR
function realTime(oName,bTagSel)
	{
	//Focus stuff
	if(!eval(oName).checkFocus())return;

	var oEditor=eval("idContent"+oName);
	var oSel=oEditor.document.selection.createRange();
	var obj=eval(oName);

	//Enable/Disable Table Edit & Cell Edit Menu
	if(obj.btnTable)
		{
		obj.arrElm[1].style.color="gray";
		obj.arrElm[2].style.color="gray";
		obj.arrElm[3].style.color="gray";
		var oTable=(oSel.parentElement!=null?GetElement(oSel.parentElement(),"TABLE"):GetElement(oSel.item(0),"TABLE"));
		if (oTable)
			{
			obj.arrElm[1].style.color="black";
			obj.arrElm[2].style.color="black";
			obj.arrElm[3].style.color="gray";
			makeEnableNormal(obj.arrElm[0]);
			}
		else makeDisabled(obj.arrElm[0]);

		var oTD=(oSel.parentElement!=null?GetElement(oSel.parentElement(),"TD"):GetElement(oSel.item(0),"TD"));
		if (oTD)
			{
			obj.arrElm[1].style.color="black";
			obj.arrElm[2].style.color="black";
			obj.arrElm[3].style.color="black";
			}
		}

	//REALTIME BUTTONS HERE
	if(obj.btnParagraph)
		{
		if(oEditor.document.queryCommandEnabled("FormatBlock"))
			makeEnableNormal(obj.arrElm[4]);
		else makeDisabled(obj.arrElm[4]);
		}
	if(obj.btnFontName)
		{
		if(oEditor.document.queryCommandEnabled("FontName"))
			makeEnableNormal(obj.arrElm[5]);
		else makeDisabled(obj.arrElm[5]);
		}
	if(obj.btnFontSize)
		{
		if(oEditor.document.queryCommandEnabled("FontSize"))
			makeEnableNormal(obj.arrElm[6]);
		else makeDisabled(obj.arrElm[6]);
		}
	if(obj.btnCut)
		{
		if(oEditor.document.queryCommandEnabled("Cut"))
			makeEnableNormal(obj.arrElm[7]);
		else makeDisabled(obj.arrElm[7]);
		}
	if(obj.btnCopy)
		{
		if(oEditor.document.queryCommandEnabled("Copy"))
			makeEnableNormal(obj.arrElm[8]);
		else makeDisabled(obj.arrElm[8]);
		}
	if(obj.btnPaste)
		{
		if(oEditor.document.queryCommandEnabled("Paste"))
			makeEnableNormal(obj.arrElm[9]);
		else makeDisabled(obj.arrElm[9]);
		}
	if(obj.btnPasteWord)
		{
		if(oEditor.document.queryCommandEnabled("Paste"))
			makeEnableNormal(obj.arrElm[10]);
		else makeDisabled(obj.arrElm[10]);
		}
	if(obj.btnPasteText)
		{
		if(oEditor.document.queryCommandEnabled("Paste"))
			makeEnableNormal(obj.arrElm[11]);
		else makeDisabled(obj.arrElm[11]);
		}

	if(obj.btnUndo)
		{
		if(!obj.arrUndoList[0])makeDisabled(obj.arrElm[12]);
		else makeEnableNormal(obj.arrElm[12]);
		}
	if(obj.btnRedo)
		{
		if(!obj.arrRedoList[0])makeDisabled(obj.arrElm[13]);
		else makeEnableNormal(obj.arrElm[13]);
		}

	if(obj.btnBold)
		{
		if(oEditor.document.queryCommandEnabled("Bold"))
			{
			if(oEditor.document.queryCommandState("Bold"))
				makeEnablePushed(obj.arrElm[14]);
			else makeEnableNormal(obj.arrElm[14]);
			}
		else makeDisabled(obj.arrElm[14]);
		}
	if(obj.btnItalic)
		{
		if(oEditor.document.queryCommandEnabled("Italic"))
			{
			if(oEditor.document.queryCommandState("Italic"))
				makeEnablePushed(obj.arrElm[15]);
			else makeEnableNormal(obj.arrElm[15]);
			}
		else makeDisabled(obj.arrElm[15]);
		}
	if(obj.btnUnderline)
		{
		if(oEditor.document.queryCommandEnabled("Underline"))
			{
			if(oEditor.document.queryCommandState("Underline"))
				makeEnablePushed(obj.arrElm[16]);
			else makeEnableNormal(obj.arrElm[16]);
			}
		else makeDisabled(obj.arrElm[16]);
		}
	if(obj.btnStrikethrough)
		{
		if(oEditor.document.queryCommandEnabled("Strikethrough"))
			{
			if(oEditor.document.queryCommandState('Strikethrough'))
				makeEnablePushed(obj.arrElm[17]);
			else makeEnableNormal(obj.arrElm[17]);
			}
		else makeDisabled(obj.arrElm[17]);
		}
	if(obj.btnSuperscript)
		{
		if(oEditor.document.queryCommandEnabled("Superscript"))
			{
			if(oEditor.document.queryCommandState("Superscript"))
				makeEnablePushed(obj.arrElm[18]);
			else makeEnableNormal(obj.arrElm[18]);
			}
		else makeDisabled(obj.arrElm[18]);
		}
	if(obj.btnSubscript)
		{
		if(oEditor.document.queryCommandEnabled("Subscript"))
			{
			if(oEditor.document.queryCommandState("Subscript"))
				makeEnablePushed(obj.arrElm[19]);
			else makeEnableNormal(obj.arrElm[19]);
			}
		else makeDisabled(obj.arrElm[19]);
		}
	if(obj.btnNumbering)
		{
		if(oEditor.document.queryCommandEnabled("InsertOrderedList"))
			{
			if(oEditor.document.queryCommandState("InsertOrderedList"))
				makeEnablePushed(obj.arrElm[20]);
			else makeEnableNormal(obj.arrElm[20]);
			}
		else makeDisabled(obj.arrElm[20]);
		}
	if(obj.btnBullets)
		{
		if(oEditor.document.queryCommandEnabled("InsertUnorderedList"))
			{
			if(oEditor.document.queryCommandState("InsertUnorderedList"))
				makeEnablePushed(obj.arrElm[21]);
			else makeEnableNormal(obj.arrElm[21]);
			}
		else makeDisabled(obj.arrElm[21]);
		}
	if(obj.btnJustifyLeft)
		{
		if(oEditor.document.queryCommandEnabled("JustifyLeft"))
			{
			if(oEditor.document.queryCommandState("JustifyLeft"))
				makeEnablePushed(obj.arrElm[22]);
			else makeEnableNormal(obj.arrElm[22]);
			}
		else makeDisabled(obj.arrElm[22]);
		}
	if(obj.btnJustifyCenter)
		{
		if(oEditor.document.queryCommandEnabled("JustifyCenter"))
			{
			if(oEditor.document.queryCommandState("JustifyCenter"))
				makeEnablePushed(obj.arrElm[23]);
			else makeEnableNormal(obj.arrElm[23]);
			}
		else makeDisabled(obj.arrElm[23]);
		}
	if(obj.btnJustifyRight)
		{
		if(oEditor.document.queryCommandEnabled("JustifyRight"))
			{
			if(oEditor.document.queryCommandState("JustifyRight"))
				makeEnablePushed(obj.arrElm[24]);
			else makeEnableNormal(obj.arrElm[24]);
			}
		else makeDisabled(obj.arrElm[24]);
		}
	if(obj.btnJustifyFull)
		{
		if(oEditor.document.queryCommandEnabled("JustifyFull"))
			{
			if(oEditor.document.queryCommandState("JustifyFull"))
				makeEnablePushed(obj.arrElm[25]);
			else makeEnableNormal(obj.arrElm[25]);
			}
		else makeDisabled(obj.arrElm[25]);
		}

	if(obj.btnIndent)
		{
		if(oEditor.document.queryCommandEnabled("Indent"))
			makeEnableNormal(obj.arrElm[26]);
		else makeDisabled(obj.arrElm[26]);
		}
	if(obj.btnOutdent)
		{
		if(oEditor.document.queryCommandEnabled("Outdent"))
			makeEnableNormal(obj.arrElm[27]);
		else makeDisabled(obj.arrElm[27]);
		}

	if(obj.btnLTR)
		{
		if(oEditor.document.queryCommandEnabled("BlockDirLTR"))
			{
			if(oEditor.document.queryCommandState("BlockDirLTR"))
				makeEnablePushed(obj.arrElm[28]);
			else makeEnableNormal(obj.arrElm[28]);
			}
		else makeDisabled(obj.arrElm[28]);
		}
	if(obj.btnRTL)
		{
		if(oEditor.document.queryCommandEnabled("BlockDirRTL"))
			{
			if(oEditor.document.queryCommandState("BlockDirRTL"))
				makeEnablePushed(obj.arrElm[29]);
			else makeEnableNormal(obj.arrElm[29]);
			}
		else makeDisabled(obj.arrElm[29]);
		}

	if(oSel.parentElement)
		{
		if(obj.btnForeColor)makeEnableNormal(obj.arrElm[30]);
		if(obj.btnBackColor)makeEnableNormal(obj.arrElm[31]);
		if(obj.btnLine)makeEnableNormal(obj.arrElm[32]);
		}
	else
		{
		if(obj.btnForeColor)makeDisabled(obj.arrElm[30]);
		if(obj.btnBackColor)makeDisabled(obj.arrElm[31]);
		if(obj.btnLine)makeDisabled(obj.arrElm[32]);
		}

	try{oUtil.onSelectionChanged()}catch(e){;}

	try{obj.onSelectionChanged()}catch(e){;}

	//STYLE SELECTOR ~~~~~~~~~~~~~~~~~~
	var idStyles=document.getElementById("idStyles"+oName);
	if(idStyles.innerHTML!="")
		{
		var oElement;
		if(oUtil.activeElement)
			oElement=oUtil.activeElement;
		else
			{
			if (oSel.parentElement)oElement=oSel.parentElement();
			else oElement=oSel.item(0);
			}
		var sCurrClass=oElement.className;
		
		var oRows=document.getElementById("tblStyles"+oName).rows;
		for(var i=0;i<oRows.length-1;i++)
			{
			sClass=oRows[i].childNodes[0].innerText;
			if(sClass.split(".").length>1 && sClass!="")sClass=sClass.split(".")[1];
			if(sCurrClass==sClass)
				{
				oRows[i].style.marginRight="1px";
				oRows[i].style.backgroundColor=obj.styleSelectionHoverBg;
				oRows[i].style.color=obj.styleSelectionHoverFg;
				}
			else
				{
				oRows[i].style.marginRight="";
				oRows[i].style.backgroundColor="";
				oRows[i].style.color="";
				}
			}
		}

	//TAG SELECTOR ~~~~~~~~~~~~~~~~~~
	if(obj.useTagSelector && !bTagSel)
		{
		if (oSel.parentElement)	oElement=oSel.parentElement();
		else oElement=oSel.item(0);
		var sHTML="";var i=0;
		arrTmp2=[];//clear
		while (oElement!=null && oElement.tagName!="BODY")
			{
			arrTmp2[i]=oElement;
			var sTagName = oElement.tagName;
			sHTML = "&nbsp; &lt;<span id=tag"+oName+i+" unselectable=on style='text-decoration:underline;cursor:hand' onclick=\""+oName+".selectElement("+i+")\">" + sTagName + "</span>&gt;" + sHTML;
			oElement = oElement.parentElement;
			i++;
			}
		sHTML = "&nbsp;&lt;BODY&gt;" + sHTML;
		eval("idElNavigate"+oName).innerHTML = sHTML;
		eval("idElCommand"+oName).style.display="none";
		}

	if(obj.isAfterPaste)
		{
		obj.cleanDeprecated();

		//*** RUNTIME STYLES ***
		obj.runtimeBorder(false);
		obj.runtimeStyles();
		//***********************
		obj.isAfterPaste=false;
		}
	}
function realtimeFontSelect(oName)
	{
	var oEditor=eval("idContent"+oName);
	var sFontName = oEditor.document.queryCommandValue("FontName");
	var iCols = eval("dropFontName"+oName).rows[0].childNodes.length;
	for(var i=0;i<iCols;i++)
		{
		var oFontTable=eval("dropFontName"+oName).rows[0].childNodes[i].childNodes[0];
		var rowFonts = oFontTable.rows;
		for(var j=0;j<rowFonts.length;j++)
			{
			if(sFontName+")"==rowFonts[j].innerText.split("(")[1])
				{
				rowFonts[j].childNodes[0].style.backgroundColor="#708090";
				rowFonts[j].childNodes[0].style.color="#FFFFFF";
				}
			else
				{
				rowFonts[j].childNodes[0].style.backgroundColor="";
				rowFonts[j].childNodes[0].style.color="#000000";
				}
			}
		}
	}
function realtimeSizeSelect(oName)
	{
	var oEditor=eval("idContent"+oName);
	var sFontSize=oEditor.document.queryCommandValue("FontSize");
	var rowFonts=eval("dropFontSize"+oName).rows;
	for(var i=0;i<rowFonts.length;i++)
		{
		if("Size "+sFontSize==rowFonts[i].innerText)
			{
			rowFonts[i].childNodes[0].style.backgroundColor="#708090";
			rowFonts[i].childNodes[0].style.color="#FFFFFF";
			}
		else
			{
			rowFonts[i].childNodes[0].style.backgroundColor="";
			rowFonts[i].childNodes[0].style.color="#000000";
			}
		}
	}

/*** TAG SELECTOR ***/
function moveTagSelector()
	{
	var sTagSelTop="<table unselectable=on ondblclick='"+this.oName+".moveTagSelector()' width='100%' cellpadding=0 cellspacing=0><tr style='background:#e9e8f2;font-family:arial;font-size:10px;color:black;'>"+
		"<td id=idElNavigate"+ this.oName +" style='padding:1px;width:100%' valign=top>&nbsp;</td>"+
		"<td align=right valign=top nowrap>"+
		"<span id=idElCommand"+ this.oName +" unselectable=on style='display:none;text-decoration:underline;cursor:hand;padding-right:5;' onclick='"+this.oName+".removeTag()'>"+getTxt("Remove Tag")+"</span>"+
		"</td></tr></table>";

	var sTagSelBottom="<table unselectable=on ondblclick='"+this.oName+".moveTagSelector()' width='100%' cellpadding=0 cellspacing=0><tr style='background:#e4e3ed;font-family:arial;font-size:10px;color:black;'>"+
		"<td id=idElNavigate"+ this.oName +" style='padding:1px;width:100%' valign=top>&nbsp;</td>"+
		"<td align=right valign=top nowrap>"+
		"<span id=idElCommand"+ this.oName +" unselectable=on style='display:none;text-decoration:underline;cursor:hand;padding-right:5;' onclick='"+this.oName+".removeTag()'>"+getTxt("Remove Tag")+"</span>"+
		"</td></tr></table>";

	if(this.TagSelectorPosition=="top")
		{
		eval("idTagSelTop"+this.oName).innerHTML="";
		eval("idTagSelBottom"+this.oName).innerHTML=sTagSelBottom;
		eval("idTagSelTopRow"+this.oName).style.display="none";
		eval("idTagSelBottomRow"+this.oName).style.display="block";
		this.TagSelectorPosition="bottom"
		}
	else//if(this.TagSelectorPosition=="bottom")
		{
		eval("idTagSelTop"+this.oName).innerHTML=sTagSelTop;
		eval("idTagSelBottom"+this.oName).innerHTML="";
		eval("idTagSelTopRow"+this.oName).style.display="block";
		eval("idTagSelBottomRow"+this.oName).style.display="none";
		this.TagSelectorPosition="top"
		}
	}
function selectElement(i)
	{
	var oEditor=eval("idContent"+this.oName);
	var oSelRange = oEditor.document.body.createControlRange();
	var oActiveElement;
	try
		{
		oSelRange.add(arrTmp2[i]);
		oSelRange.select();
		realTime(this.oName,true);
		oActiveElement = arrTmp2[i];
		if(oActiveElement.tagName!="TD"&&
			oActiveElement.tagName!="TR"&&
			oActiveElement.tagName!="TBODY"&&
			oActiveElement.tagName!="LI")
			eval("idElCommand"+this.oName).style.display="block";	
		}
	catch(e)
		{
		try//utk multiple instance, kalau select tag tapi tdk focus atau select list & content lain di luar list lalu set color
			{
			var oSelRange = oEditor.document.body.createTextRange();
			oSelRange.moveToElementText(arrTmp2[i]);
			oSelRange.select();
			realTime(this.oName,true);
			oActiveElement = arrTmp2[i];
			if(oActiveElement.tagName!="TD"&&
				oActiveElement.tagName!="TR"&&
				oActiveElement.tagName!="TBODY"&&
				oActiveElement.tagName!="LI")
				eval("idElCommand"+this.oName).style.display="block";
			}
		catch(e){return;}
		}
	for(var j=0;j<arrTmp2.length;j++)eval("tag"+this.oName+j).style.background="";
	eval("tag"+this.oName+i).style.background="DarkGray";

	if(oActiveElement)
		oUtil.activeElement=oActiveElement;//Set active element in the Editor
	}
function removeTag()
	{
	if(!this.checkFocus())return;//Focus stuff
	eval(this.oName).saveForUndo();//Save for Undo
	var oEditor=eval("idContent"+this.oName);
	var oSel=oEditor.document.selection.createRange();
	var sType=oEditor.document.selection.type;
	if(sType=="Control")
		{
		oSel.item(0).outerHTML="";
		this.focus();
		realTime(this.oName);
		return;
		}

	var oActiveElement=oUtil.activeElement;
	var oSelRange = oEditor.document.body.createTextRange();
	oSelRange.moveToElementText(oActiveElement);
	oSel.setEndPoint("StartToStart",oSelRange);
	oSel.setEndPoint("EndToEnd",oSelRange);
	oSel.select();
	
	this.saveForUndo();

	sHTML=oActiveElement.innerHTML;
	var arrA = String(sHTML).match(/<A[^>]*>/g);
	if(arrA)
		for(var i=0;i<arrA.length;i++)
			{
			sTmp = arrA[i].replace(/href=/,"href_iwe=");
			sHTML=String(sHTML).replace(arrA[i],sTmp);
			}
	var arrB = String(sHTML).match(/<IMG[^>]*>/g);
	if(arrB)
		for(var i=0;i<arrB.length;i++)
			{
			sTmp = arrB[i].replace(/src=/,"src_iwe=");
			sHTML=String(sHTML).replace(arrB[i],sTmp);
			}

	var oTmp=oActiveElement.parentElement;
	if(oTmp.innerHTML==oActiveElement.outerHTML)//<b><u>TEXT</u><b> (<u> is selected)
		{//oTmp=<b> , oActiveElement=<u>
		oTmp.innerHTML=sHTML;

		for(var i=0;i<oEditor.document.all.length;i++)
			{
			if(oEditor.document.all[i].getAttribute("href_iwe"))
				{
				oEditor.document.all[i].href=oEditor.document.all[i].getAttribute("href_iwe");
				oEditor.document.all[i].removeAttribute("href_iwe",0);
				}
			if(oEditor.document.all[i].getAttribute("src_iwe"))
				{
				oEditor.document.all[i].src=oEditor.document.all[i].getAttribute("src_iwe");
				oEditor.document.all[i].removeAttribute("src_iwe",0);
				}
			}

		var oSelRange = oEditor.document.body.createTextRange();
		oSelRange.moveToElementText(oTmp);
		oSel.setEndPoint("StartToStart",oSelRange);
		oSel.setEndPoint("EndToEnd",oSelRange);
		oSel.select();
		realTime(this.oName);
		this.selectElement(0);
		return;
		}
	else
		{
		oActiveElement.outerHTML="";
		oSel.pasteHTML(sHTML);

		for(var i=0;i<oEditor.document.all.length;i++)
			{
			if(oEditor.document.all[i].getAttribute("href_iwe"))
				{
				oEditor.document.all[i].href=oEditor.document.all[i].getAttribute("href_iwe");
				oEditor.document.all[i].removeAttribute("href_iwe",0);
				}
			if(oEditor.document.all[i].getAttribute("src_iwe"))
				{
				oEditor.document.all[i].src=oEditor.document.all[i].getAttribute("src_iwe");
				oEditor.document.all[i].removeAttribute("src_iwe",0);
				}
			}
		this.focus();
		realTime(this.oName);
		}

	//*** RUNTIME STYLES ***
	this.runtimeBorder(false);
	this.runtimeStyles();
	//***********************
	}

/*** RUNTIME BORDERS ***/
function runtimeBorderOn()
	{
	this.runtimeBorderOff();//reset

	var oEditor=eval("idContent"+this.oName);
	var oTables=oEditor.document.getElementsByTagName("TABLE");
	for(i=0;i<oTables.length;i++)
		{
		var oTable=oTables[i];
		if(oTable.border==0)
			{
			var oCells=oTable.getElementsByTagName("TD")
			for(j=0;j<oCells.length;j++)
				{
				if(oCells[j].style.borderLeftWidth=="0px"||
					oCells[j].style.borderLeftWidth==""||
					oCells[j].style.borderLeftWidth=="medium")
						{
						oCells[j].runtimeStyle.borderLeftWidth=1;
						oCells[j].runtimeStyle.borderLeftColor="#BCBCBC";
						oCells[j].runtimeStyle.borderLeftStyle="dotted";
						}
				if(oCells[j].style.borderRightWidth=="0px"||
					oCells[j].style.borderRightWidth==""||
					oCells[j].style.borderRightWidth=="medium")
						{
						oCells[j].runtimeStyle.borderRightWidth=1;
						oCells[j].runtimeStyle.borderRightColor="#BCBCBC";
						oCells[j].runtimeStyle.borderRightStyle="dotted";
						}
				if(oCells[j].style.borderTopWidth=="0px"||
					oCells[j].style.borderTopWidth==""||
					oCells[j].style.borderTopWidth=="medium")
						{
						oCells[j].runtimeStyle.borderTopWidth=1;
						oCells[j].runtimeStyle.borderTopColor="#BCBCBC";
						oCells[j].runtimeStyle.borderTopStyle="dotted";
						}
				if(oCells[j].style.borderBottomWidth=="0px"||
					oCells[j].style.borderBottomWidth==""||
					oCells[j].style.borderBottomWidth=="medium")
						{
						oCells[j].runtimeStyle.borderBottomWidth=1;
						oCells[j].runtimeStyle.borderBottomColor="#BCBCBC";
						oCells[j].runtimeStyle.borderBottomStyle="dotted";
						}
				}
			}
		}
	}
function runtimeBorderOff()
	{
	var oEditor=eval("idContent"+this.oName);
	var oTables=oEditor.document.getElementsByTagName("TABLE");
	for(i=0;i<oTables.length;i++)
		{
		var oTable=oTables[i];
		if(oTable.border==0)
			{
			var oCells=oTable.getElementsByTagName("TD");
			for(j=0;j<oCells.length;j++)
				{
				oCells[j].runtimeStyle.borderWidth="";
				oCells[j].runtimeStyle.borderColor="";
				oCells[j].runtimeStyle.borderStyle="";
				}
			}
		}
	}
function runtimeBorder(bToggle)
	{
	if(bToggle)
		{
		if(this.IsRuntimeBorderOn)
			{
			this.runtimeBorderOff();
			this.IsRuntimeBorderOn=false;
			}
		else
			{
			this.runtimeBorderOn();
			this.IsRuntimeBorderOn=true;
			}
		}
	else
		{//refresh based on the current status
		if(this.IsRuntimeBorderOn) this.runtimeBorderOn();
		else this.runtimeBorderOff();
		}
	}

/*** RUNTIME STYLES ***/
function runtimeStyles()
	{
	var oEditor=eval("idContent"+this.oName);
	var oForms=oEditor.document.getElementsByTagName("FORM");
	for (i=0;i<oForms.length;i++) oForms[i].runtimeStyle.border="#7bd158 1px dotted";

	var oBookmarks=oEditor.document.getElementsByTagName("A");
	for (i=0;i<oBookmarks.length;i++)
		{
		var oBookmark=oBookmarks[i];
		if(oBookmark.name||oBookmark.NAME)
			{
			if(oBookmark.innerHTML=="")oBookmark.runtimeStyle.width="1px";
			oBookmark.runtimeStyle.padding="0px";
			oBookmark.runtimeStyle.paddingLeft="1px";
			oBookmark.runtimeStyle.paddingRight="1px";
			oBookmark.runtimeStyle.border="#888888 1px dotted";
			oBookmark.runtimeStyle.borderLeft="#cccccc 10px solid";
			}
		}
	}

/**************************
	NEW SPAN OPERATION 
**************************/

/*** CLEAN ***/
function cleanFonts()
	{
	var oEditor=eval("idContent"+this.oName);
	var allFonts=oEditor.document.body.getElementsByTagName("FONT");
	if(allFonts.length==0)return false;

	var f;
	while(allFonts.length>0)
		{
		f=allFonts[0];
		if(f.hasChildNodes && f.childNodes.length==1 && f.childNodes[0].nodeType==1 && f.childNodes[0].nodeName=="SPAN") 
			{
			//if font containts only span child node
			copyAttribute(f.childNodes[0],f);
			f.removeNode(false);
			}
		else
			if(f.parentElement.nodeName=="SPAN" && f.parentElement.childNodes.length==1)
				{
				//font is the only child node of span.
				copyAttribute(f.parentElement,f);
				f.removeNode(false);
				}
			else
				{
				var newSpan=oEditor.document.createElement("SPAN");
				copyAttribute(newSpan,f);
				newSpan.innerHTML=f.innerHTML;
				f.replaceNode(newSpan);
				}
		}
	return true;
	}
function cleanTags(elements,sVal)//WARNING: Dgn asumsi underline & linethrough tidak bertumpuk
	{
	var oEditor=eval("idContent"+this.oName);
	var f;
	while(elements.length>0)
		{
		f=elements[0];
		if(f.hasChildNodes && f.childNodes.length==1 && f.childNodes[0].nodeType==1 && f.childNodes[0].nodeName=="SPAN") 
			{//if font containts only span child node
			if(sVal=="bold")f.childNodes[0].style.fontWeight="bold";
			if(sVal=="italic")f.childNodes[0].style.fontStyle="italic";
			if(sVal=="line-through")f.childNodes[0].style.textDecoration="line-through";
			if(sVal=="underline")f.childNodes[0].style.textDecoration="underline";	
			f.removeNode(false);
			}
		else
			if(f.parentElement.nodeName=="SPAN" && f.parentElement.childNodes.length==1)
				{//font is the only child node of span.
				if(sVal=="bold")f.parentElement.style.fontWeight="bold";
				if(sVal=="italic")f.parentElement.style.fontStyle="italic";
				if(sVal=="line-through")f.parentElement.style.textDecoration="line-through";
				if(sVal=="underline")f.parentElement.style.textDecoration="underline";	
				f.removeNode(false);
				}
			else
				{
				var newSpan=oEditor.document.createElement("SPAN");
				if(sVal=="bold")newSpan.style.fontWeight="bold";
				if(sVal=="italic")newSpan.style.fontStyle="italic";
				if(sVal=="line-through")newSpan.style.textDecoration="line-through";
				if(sVal=="underline")newSpan.style.textDecoration="underline";
				newSpan.innerHTML=f.innerHTML;
				f.replaceNode(newSpan);
				}
		}
	}
function replaceTags(sFrom,sTo)
	{
	var oEditor=eval("idContent"+this.oName);
	var elements=oEditor.document.getElementsByTagName(sFrom);

	var newSpan;
	var count=elements.length;
	while(count > 0) 
		{
		f=elements[0];
		newSpan=oEditor.document.createElement(sTo);
		newSpan.innerHTML=f.innerHTML;
		f.replaceNode(newSpan);          
		count--;
		}
	}
/************************************
	Used in loadHTML, putHTML, 
	editorDoc_onkeyup, realTime, 
	getHTML, getXHTML, 
	getHTMLBody, getXHTMLBody 
	pasteWord.htm
*************************************/
function cleanDeprecated()
	{
	var oEditor=eval("idContent"+this.oName);

	var elements;

	elements=oEditor.document.body.getElementsByTagName("STRONG");
	this.cleanTags(elements,"bold");
	elements=oEditor.document.body.getElementsByTagName("B");
	this.cleanTags(elements,"bold");

	elements=oEditor.document.body.getElementsByTagName("I");
	this.cleanTags(elements,"italic");
	elements=oEditor.document.body.getElementsByTagName("EM");
	this.cleanTags(elements,"italic");
	
	elements=oEditor.document.body.getElementsByTagName("STRIKE");
	this.cleanTags(elements,"line-through");
	elements=oEditor.document.body.getElementsByTagName("S");
	this.cleanTags(elements,"line-through");
	
	elements=oEditor.document.body.getElementsByTagName("U");
	this.cleanTags(elements,"underline");

	this.replaceTags("DIR","DIV");
	this.replaceTags("MENU","DIV");	
	this.replaceTags("CENTER","DIV");
	this.replaceTags("XMP","PRE");
	this.replaceTags("BASEFONT","SPAN");//will be removed by cleanEmptySpan()
	
	elements=oEditor.document.body.getElementsByTagName("APPLET");
	var count=elements.length;
	while(count>0) 
		{
		f=elements[0];
		f.removeNode(false);   
		count--;
		}
	
	this.cleanFonts();
	this.cleanEmptySpan();

	return true;
	}

/*** APPLY ***/
function applyBold()
	{
	if(!this.checkFocus())return;//Focus stuff

	var oEditor=eval("idContent"+this.oName);
	var oSel=oEditor.document.selection.createRange();
	this.saveForUndo();
	
	var currState=oEditor.document.queryCommandState("Bold");

	if(oUtil.activeElement) oElement=oUtil.activeElement;
	else 
		{
		if(oSel.parentElement)
			{
			if(oSel.text=="")
				{
				oElement=oSel.parentElement();
				if(oElement.tagName=="BODY")return;
				}
			else
				{
				if(currState)
					{
					this.applySpanStyle([["fontWeight",""]]);
					this.cleanEmptySpan();
					}
				else this.applySpanStyle([["fontWeight","bold"]]);
				
				if(currState==oEditor.document.queryCommandState("Bold")&&currState==true)
					this.applySpanStyle([["fontWeight","normal"]]);
				return;
				}
			}
		else oElement=oSel.item(0);
		}

	if(currState)oElement.style.fontWeight="";
	else oElement.style.fontWeight="bold";

	if(currState==oEditor.document.queryCommandState("Bold")&&currState==true)
		oElement.style.fontWeight="normal";
	}
function applyItalic()
	{
	if(!this.checkFocus())return;//Focus stuff

	var oEditor=eval("idContent"+this.oName);
	var oSel=oEditor.document.selection.createRange();
	this.saveForUndo();
	
	var currState=oEditor.document.queryCommandState("Italic");

	if(oUtil.activeElement) oElement=oUtil.activeElement;
	else 
		{
		if(oSel.parentElement)
			{
			if(oSel.text=="")
				{
				oElement=oSel.parentElement();
				if(oElement.tagName=="BODY")return;
				}
			else
				{
				if(currState)
					{
					this.applySpanStyle([["fontStyle",""]]);
					this.cleanEmptySpan();
					}
				else this.applySpanStyle([["fontStyle","italic"]]);
				
				if(currState==oEditor.document.queryCommandState("Italic")&&currState==true)
					this.applySpanStyle([["fontStyle","normal"]]);
				return;
				}
			}
		else oElement=oSel.item(0);
		}

	if(currState)oElement.style.fontStyle="";
	else oElement.style.fontStyle="italic";

	if(currState==oEditor.document.queryCommandState("Italic")&&currState==true)
		oElement.style.fontStyle="normal";
	}
function applyLine(sCmd)
	{
	if(!this.checkFocus())return;//Focus stuff

	var oEditor=eval("idContent"+this.oName);
	var oSel=oEditor.document.selection.createRange();
	this.saveForUndo();
	
	var currState1=oEditor.document.queryCommandState("Underline");
	var currState2=oEditor.document.queryCommandState("Strikethrough");
	var sValue;
	if(sCmd=="underline")
		{
		if(currState1&&currState2)sValue="line-through";
		else if(!currState1&&currState2)sValue="underline line-through";		
		else if(currState1&&!currState2)sValue="";
		else if(!currState1&&!currState2)sValue="underline";
		}
	else//"line-through"
		{
		if(currState1&&currState2)sValue="underline";
		else if(!currState1&&currState2)sValue="";
		else if(currState1&&!currState2)sValue="underline line-through";		
		else if(!currState1&&!currState2)sValue="line-through";
		}

	if(oUtil.activeElement) oElement=oUtil.activeElement;
	else 
		{
		if(oSel.parentElement)
			{
			if(oSel.text=="")
				{
				oElement=oSel.parentElement();
				if(oElement.tagName=="BODY")return;
				}
			else
				{
				if(sValue=="")
					{
					this.applySpanStyle([["textDecoration",""]]);
					this.cleanEmptySpan();
					}
				else this.applySpanStyle([["textDecoration",sValue]]);

				/* Note: text-decoration is not inherited. */
				if((sCmd=="underline"&&currState1==oEditor.document.queryCommandState("Underline")&&currState1==true) ||
					(sCmd=="line-through"&&currState2==oEditor.document.queryCommandState("Strikethrough")&&currState2==true))
					{
					this.applySpanStyle([["textDecoration",""]]);
					this.cleanEmptySpan();
					}
				return;
				}
			}
		else oElement=oSel.item(0);
		}
		
	oElement.style.textDecoration=sValue;

	/* Note: text-decoration is not inherited. */
	if((sCmd=="underline"&&currState1==oEditor.document.queryCommandState("Underline")&&currState1==true) ||
		(sCmd=="line-through"&&currState2==oEditor.document.queryCommandState("Strikethrough")&&currState2==true))
		{
		this.applySpanStyle([["textDecoration",""]]);
		this.cleanEmptySpan();
		}
	}
function applyColor(sType,sColor)
	{
	if(!this.checkFocus())return;//Focus stuff

	var oEditor=eval("idContent"+this.oName);
	var oSel=oEditor.document.selection.createRange();
	this.saveForUndo();

	if(oUtil.activeElement)
		{
		oElement=oUtil.activeElement;
		if(sType=="ForeColor")oElement.style.color=sColor;
		else oElement.style.backgroundColor=sColor;
		}
	else if(oSel.parentElement)
		{
		if(oSel.text=="")
			{
			oElement=oSel.parentElement();
			if(oElement.tagName=="BODY")return;
			if(sType=="ForeColor")oElement.style.color=sColor;
			else oElement.style.backgroundColor=sColor;
			}
		else
			{
			if(sType=="ForeColor")this.applySpanStyle([["color",sColor]]);
			else this.applySpanStyle([["backgroundColor",sColor]]);
			}
		}

	if(sColor=="")
		{
		this.cleanEmptySpan();
		realTime(this.oName);
		}
	}
function applyFontName(val)
	{
	this.hide();

	if(!this.checkFocus())return;//Focus stuff

	var oEditor=eval("idContent"+this.oName);
	var oSel=oEditor.document.selection.createRange();
	this.hide();//ini menyebabkan text yg ter-select menjadi tdk ter-select di framed-page.
	//Solusi: oSel di select lagi
	oSel.select();
	this.saveForUndo();

	//for existing SPANs
	if(oSel.parentElement)
		{
		var tempRange=oEditor.document.body.createTextRange();
		//var allSpans=oSel.parentElement().getElementsByTagName("SPAN");//tdk sempurna utk selection across tables
		var allSpans=oEditor.document.getElementsByTagName("SPAN");//ok
		for(var i=0;i<allSpans.length;i++)
			{
			tempRange.moveToElementText(allSpans[i]);
			if(oSel.inRange(tempRange))allSpans[i].style.fontFamily=val;
			}
		}

	this.doCmd("fontname",val);
	replaceWithSpan(oEditor);
	realTime(this.oName);
	}
function applyFontSize(val)
	{
	this.hide();

	if(!this.checkFocus())return;//Focus stuff

	var oEditor=eval("idContent"+this.oName);
	var oSel=oEditor.document.selection.createRange();
	this.hide();
	oSel.select();
	this.saveForUndo();

	//for existing SPANs
	if(oSel.parentElement)
		{
		var tempRange=oEditor.document.body.createTextRange();
		//var allSpans=oSel.parentElement().getElementsByTagName("SPAN");
		var allSpans=oEditor.document.getElementsByTagName("SPAN");//ok
		for(var i=0;i<allSpans.length;i++)
			{
			tempRange.moveToElementText(allSpans[i]);
			if (oSel.inRange(tempRange))
				{//tdk perlu ada +/- (krn optionnya tdk ada)
				if(val==1)allSpans[i].style.fontSize="8pt";
				else if(val==2)allSpans[i].style.fontSize="10pt";
				else if(val==3)allSpans[i].style.fontSize="12pt";
				else if(val==4)allSpans[i].style.fontSize="14pt";
				else if(val==5)allSpans[i].style.fontSize="18pt";
				else if(val==6)allSpans[i].style.fontSize="24pt";
				else if(val=7)allSpans[i].style.fontSize="36pt";
				}
			}
		}

	this.doCmd("fontsize",val);
	replaceWithSpan(oEditor);
	realTime(this.oName);
	}
function applySpanStyle(arrStyles,sClassName)
	{
	//tdk perlu focus stuff
	var oEditor=eval("idContent"+this.oName);
	var oSel=oEditor.document.selection.createRange();
	this.hide();
	oSel.select();
	this.saveForUndo();

	//Step 1: apply to existing SPANs
	if(oSel.parentElement)
		{
		var tempRange=oEditor.document.body.createTextRange();
		var oEl=oSel.parentElement();
		//var allSpans=oSel.parentElement().getElementsByTagName("SPAN");//tdk sempurna utk selection across tables
		var allSpans=oEditor.document.getElementsByTagName("SPAN");//ok - WARNING: hrs cek semua span.
		
		for (var i=0;i<allSpans.length;i++)
			{
			tempRange.moveToElementText(allSpans[i]);
			if (oSel.inRange(tempRange))
				copyStyleClass(allSpans[i],arrStyles,sClassName);
			}
		}

	//Step 2: apply to selection
	this.doCmd("fontname","");// 2.a
	replaceWithSpan(oEditor,arrStyles,sClassName);// 2.b
	
	this.cleanEmptySpan();// 2.c => krn ada kemungkinan arrStyle & sClassName semua di set = "", ini akan meninggalkan empty span. WARNING: pengaruh ke selection? => sudah di-test no problem.
	realTime(this.oName);// 2.d
	}
function doClean()
	{
	if(!this.checkFocus())return;//Focus stuff

	var oEditor=eval("idContent"+this.oName);
	this.saveForUndo();

	this.doCmd('RemoveFormat');

	if(oUtil.activeElement)
		{
		var oActiveElement=oUtil.activeElement;
		oActiveElement.removeAttribute("className",0);
		oActiveElement.removeAttribute("style",0);

		if(oActiveElement.tagName=="H1"||oActiveElement.tagName=="H2"||
			oActiveElement.tagName=="H3"||oActiveElement.tagName=="H4"||
			oActiveElement.tagName=="H5"||oActiveElement.tagName=="H6"||
			oActiveElement.tagName=="PRE"||oActiveElement.tagName=="P"||
			oActiveElement.tagName=="DIV")
			{
			if(this.useDIV)this.doCmd('FormatBlock','<DIV>');
			else this.doCmd('FormatBlock','<P>');
			}
		}
	else
		{		
		var oSel=oEditor.document.selection.createRange();
		var sType=oEditor.document.selection.type;
		if (oSel.parentElement)
			{
			if(oSel.text=="")
				{
				oEl=oSel.parentElement();
				if(oEl.tagName=="BODY")return;
				else
					{
					oEl.removeAttribute("className",0);
					oEl.removeAttribute("style",0);
					if(oEl.tagName=="H1"||oEl.tagName=="H2"||
						oEl.tagName=="H3"||oEl.tagName=="H4"||
						oEl.tagName=="H5"||oEl.tagName=="H6"||
						oEl.tagName=="PRE"||oEl.tagName=="P"||oEl.tagName=="DIV")
						{
						if(this.useDIV)this.doCmd('FormatBlock','<DIV>');
						else this.doCmd('FormatBlock','<P>');
						}
					}
				}
			else
				{
				this.applySpanStyle([
									["backgroundColor",""],
									["color",""],
									["fontFamily",""],
									["fontSize",""],
									["fontWeight",""],
									["fontStyle",""],
									["textDecoration",""],
									["letterSpacing",""],
									["verticalAlign",""],
									["textTransform",""],
									["fontVariant",""]
									],"");
				return;
				}
			}
		else
			{
			oEl=oSel.item(0);
			oEl.removeAttribute("className",0);
			oEl.removeAttribute("style",0);
			}
		}
	this.cleanEmptySpan();
	realTime(this.oName);
	}
function cleanEmptySpan()//WARNING: blm bisa remove span yg bertumpuk dgn style sama,dst.
	{
	var bReturn=false;
	var oEditor=eval("idContent"+this.oName);
	var allSpans=oEditor.document.getElementsByTagName("SPAN");
	if(allSpans.length==0)return false;

	var emptySpans=[];
	var reg = /<\s*SPAN\s*>/gi;
	for(var i=0;i<allSpans.length;i++)
		{
		if(allSpans[i].outerHTML.search(reg)==0)
			emptySpans[emptySpans.length]=allSpans[i];
		}
	var theSpan,theParent;
	for(var i=0;i<emptySpans.length;i++)
		{
		theSpan=emptySpans[i];
		theSpan.removeNode(false);
		bReturn=true;
		}
	return bReturn;
	}

/*** COMMON ***/
function copyStyleClass(newSpan,arrStyles,sClassName)
	{
	if(arrStyles)
		for(var i=0;i<arrStyles.length;i++)
			{
			eval("newSpan.style."+arrStyles[i][0]+"=\""+arrStyles[i][1]+"\"");
			}

	//style attr. yg empty, attr.style akan ter-remove otomatis
	if(newSpan.style.fontFamily=="")
		{//Text Formatting Related
		newSpan.style.cssText=newSpan.style.cssText.replace("FONT-FAMILY: ; ","");
		newSpan.style.cssText=newSpan.style.cssText.replace("FONT-FAMILY: ","");
		}

	if(sClassName!=null)
		{
		newSpan.className=sClassName;
		if(newSpan.className=="")newSpan.removeAttribute("className",0);//WARNING: this will remove span (for empty attributes).
		//WARNING: otomatis me-remove empty span!!
		}
	//remove class attribut tdk perlu, krn tdk ada facility yg assign class="" (yg ada hanya remove class).
	}
function copyAttribute(newSpan,f)
	{
	if((f.face!=null)&&(f.face!=""))newSpan.style.fontFamily=f.face;
	if((f.size!=null)&&(f.size!=""))
		{
		var nSize="";
		if(f.size==1)nSize="8pt";
		else if(f.size==2)nSize="10pt";
		else if(f.size==3)nSize="12pt";
		else if(f.size==4)nSize="14pt";
		else if(f.size==5)nSize="18pt";
		else if(f.size==6)nSize="24pt";
		else if(f.size>=7)nSize="36pt";
		else if(f.size<=-2||f.size=="0")nSize="8pt";
		else if(f.size=="-1")nSize="10pt";
		else if(f.size==0)nSize="12pt";
		else if(f.size=="+1")nSize="14pt";
		else if(f.size=="+2")nSize="18pt";
		else if(f.size=="+3")nSize="24pt";
		else if(f.size=="+4"||f.size=="+5"||f.size=="+6")nSize="36pt";
		else nSize="";
		if(nSize!="")newSpan.style.fontSize=nSize;
		}
	if((f.style.backgroundColor!=null)&&(f.style.backgroundColor!=""))newSpan.style.backgroundColor=f.style.backgroundColor;
	if((f.color!=null)&&(f.color!=""))newSpan.style.color=f.color;
	}
function replaceWithSpan(oEditor,arrStyles,sClassName)
	{
	var oSel=oEditor.document.selection.createRange();

	/*** Save length of selected text ***/
	var oSpanStart;
	oSel.select();
	var nSelLength=oSel.text.length;
	/************************************/

	//change all font to span
	var allFonts=new Array();
	if (oSel.parentElement().nodeName=="FONT" && oSel.parentElement().innerText==oSel.text) //Y
		{
		oSel.moveToElementText(oSel.parentElement());
		allFonts[0]=oSel.parentElement();
		} 
	else 
		{//selection over paragraphs
		//allFonts=oSel.parentElement().getElementsByTagName("FONT");
		allFonts=oEditor.document.getElementsByTagName("FONT");//WARNING: asumsi tdk ada FONT tag sblmnya.
		}

	var tempRange=oEditor.document.body.createTextRange();
	var newSpan;
	var count=allFonts.length;
	while(count>0)
		{
		f=allFonts[0];
		if(f==null||f.parentElement==null){count--;continue}
		tempRange.moveToElementText(f);

		/*************************************************
		Bagian ini utk mengantisipasi kalau setelah apply font, kita apply Bold (1 x) atau Bold & Italic (berarti 2 x)
		atau Bold, Italic & Underline (berarti 3x). Supaya dalam kasus, kalau apply font lagi tdk membuat span baru:
			<span><b><i><span baru>
		tapi tetap:
			<span><b><i>
		*************************************************/

		var sTemp="f";var nLevel=0;
		while(eval(sTemp+".parentElement"))
			{
			nLevel++;
			sTemp+=".parentElement";
			}
		var bBreak=false;
		for(var j=nLevel;j>0;j--)
			{
			sTemp="f";
			for(var k=1;k<=j;k++)sTemp+=".parentElement";
			if(!bBreak)
			if (eval(sTemp).nodeName=="SPAN" && eval(sTemp).innerText==f.innerText)
				{
				newSpan=eval(sTemp);
				if(arrStyles||sClassName)copyStyleClass(newSpan,arrStyles,sClassName);
				else copyAttribute(newSpan,f);
				f.removeNode(false);
				bBreak=true;
				}
			}
		if(bBreak)
			{
			continue;
			}

		newSpan=oEditor.document.createElement("SPAN");
		if(arrStyles||sClassName)copyStyleClass(newSpan,arrStyles,sClassName);
		else copyAttribute(newSpan,f);
		newSpan.innerHTML=f.innerHTML;
		f.replaceNode(newSpan);
		count--;

		/*** get first span selected ***/
		if(!oSpanStart)oSpanStart=newSpan;
		/*******************************/
		}

	/*** Restore selection ***/
	var rng = oEditor.document.selection.createRange();
	if(oSpanStart)
		{//WARNING: Jika tdk ada span, kemungkinan selection failed?
		rng.moveToElementText(oSpanStart);
		rng.select();
		}
	rng.moveEnd("character",nSelLength-rng.text.length);
	rng.select();

	//adjustments
	rng.moveEnd("character",nSelLength-rng.text.length);
	rng.select();
	rng.moveEnd("character",nSelLength-rng.text.length);
	rng.select();
	/**************************/
	}
/******** /NEW SPAN OPERATION *********/

/*** APPLY FORMATTING ***/
function doOnPaste()
	{
	this.isAfterPaste=true;
	this.saveForUndo();
	}
function doPaste()
	{
	this.saveForUndo();
	this.doCmd("Paste");
	//*** RUNTIME BORDERS ***
	this.runtimeBorder(false);
	//***********************
	}
function doCmd(sCmd,sOption)
	{
	if(!this.checkFocus())return;//Focus stuff

	if(sCmd=="Cut"||sCmd=="Copy"||sCmd=="Superscript"||sCmd=="Subscript"||
		sCmd=="Indent"||sCmd=="Outdent"||sCmd=="InsertHorizontalRule"||
		sCmd=="BlockDirLTR"||sCmd=="BlockDirRTL")
		this.saveForUndo();

	var oEditor=eval("idContent"+this.oName);
	var oSel=oEditor.document.selection.createRange();
	var sType=oEditor.document.selection.type;
	var oTarget=(sType=="None"?oEditor.document:oSel);
	oTarget.execCommand(sCmd,false,sOption);
	realTime(this.oName);
	}
function applyParagraph(val)
	{
	this.hide();

	if(!this.checkFocus())return;//Focus stuff

	var oEditor=eval("idContent"+this.oName);
	var oSel=oEditor.document.selection.createRange();
	this.hide();
	oSel.select();
	this.saveForUndo();
	this.doCmd("FormatBlock",val);
	}
function applyBullets()
	{
	if(!this.checkFocus())return;//Focus stuff

	this.saveForUndo();
	this.doCmd("InsertUnOrderedList");
	makeEnableNormal(eval("document.all.btnNumbering"+this.oName));

	var oEditor=eval("idContent"+this.oName);
	var oSel=oEditor.document.selection.createRange();
	var oElement=oSel.parentElement();
	while (oElement!=null&&oElement.tagName!="OL"&&oElement.tagName!="UL")
		{
		if(oElement.tagName=="BODY")return;
		oElement=oElement.parentElement;
		}
	oElement.removeAttribute("type",0);
	oElement.style.listStyleImage="";
	}
function applyNumbering()
	{
	if(!this.checkFocus())return;//Focus stuff

	this.saveForUndo();
	this.doCmd("InsertOrderedList");
	makeEnableNormal(eval("document.all.btnBullets"+this.oName));

	var oEditor=eval("idContent"+this.oName);
	var oSel=oEditor.document.selection.createRange();
	var oElement=oSel.parentElement();
	while (oElement!=null&&oElement.tagName!="OL"&&oElement.tagName!="UL")
		{
		if(oElement.tagName=="BODY")return;
		oElement=oElement.parentElement;
		}
	oElement.removeAttribute("type",0);
	oElement.style.listStyleImage="";
	}
function applyJustifyLeft()
	{
	if(!this.checkFocus())return;//Focus stuff

	this.saveForUndo();	
	this.doCmd("JustifyLeft");
	if(this.btnJustifyCenter) makeEnableNormal(eval("document.all.btnJustifyCenter"+this.oName));
	if(this.btnJustifyRight) makeEnableNormal(eval("document.all.btnJustifyRight"+this.oName));
	if(this.btnJustifyFull) makeEnableNormal(eval("document.all.btnJustifyFull"+this.oName));
	}
function applyJustifyCenter()
	{
	if(!this.checkFocus())return;//Focus stuff

	this.saveForUndo();
	this.doCmd("JustifyCenter");
	if(this.btnJustifyLeft) makeEnableNormal(eval("document.all.btnJustifyLeft"+this.oName));
	if(this.btnJustifyRight) makeEnableNormal(eval("document.all.btnJustifyRight"+this.oName));
	if(this.btnJustifyFull) makeEnableNormal(eval("document.all.btnJustifyFull"+this.oName));
	}
function applyJustifyRight()
	{
	if(!this.checkFocus())return;//Focus stuff

	this.saveForUndo();
	this.doCmd("JustifyRight");
	if(this.btnJustifyLeft) makeEnableNormal(eval("document.all.btnJustifyLeft"+this.oName));
	if(this.btnJustifyCenter) makeEnableNormal(eval("document.all.btnJustifyCenter"+this.oName));
	if(this.btnJustifyFull) makeEnableNormal(eval("document.all.btnJustifyFull"+this.oName));
	}
function applyJustifyFull()
	{
	if(!this.checkFocus())return;//Focus stuff

	this.saveForUndo();
	this.doCmd("JustifyFull");
	if(this.btnJustifyLeft) makeEnableNormal(eval("document.all.btnJustifyLeft"+this.oName));
	if(this.btnJustifyCenter) makeEnableNormal(eval("document.all.btnJustifyCenter"+this.oName));
	if(this.btnJustifyRight) makeEnableNormal(eval("document.all.btnJustifyRight"+this.oName));
	}
function applyBlockDirLTR()
	{
	if(!this.checkFocus())return;//Focus stuff

	this.doCmd("BlockDirLTR");
	if(this.btnRTL) makeEnableNormal(eval("document.all.btnRTL"+this.oName));
	}
function applyBlockDirRTL()
	{
	if(!this.checkFocus())return;//Focus stuff

	this.doCmd("BlockDirRTL");
	if(this.btnLTR) makeEnableNormal(eval("document.all.btnLTR"+this.oName));
	}
function doPasteText()
	{
	if(!this.checkFocus())return;//Focus stuff

	//paste from word temp storage
	var oWord=eval("idContentWord"+this.oName);
	oWord.document.designMode="on";
	oWord.document.open("text/html","replace");
	oWord.document.write("<html><head></head><body></body></html>");
	oWord.document.close();
	oWord.document.body.contentEditable=true;

	var oEditor=eval("idContent"+this.oName);
	var oSel=oEditor.document.selection.createRange();
	this.saveForUndo();
	var oWord = eval("idContentWord"+this.oName);
	oWord.focus();
	oWord.document.execCommand("SelectAll");
	oWord.document.execCommand("Paste");
	
	var sHTML = oWord.document.body.innerHTML;
	//replace space between BR and text
	sHTML = sHTML.replace(/(<br>)/gi, "$1&lt;REPBR&gt;");
	sHTML = sHTML.replace(/(<\/tr>)/gi, "$1&lt;REPBR&gt;");
	sHTML = sHTML.replace(/(<\/div>)/gi, "$1&lt;REPBR&gt;");
	sHTML = sHTML.replace(/(<\/h1>)/gi, "$1&lt;REPBR&gt;");
	sHTML = sHTML.replace(/(<\/h2>)/gi, "$1&lt;REPBR&gt;");
	sHTML = sHTML.replace(/(<\/h3>)/gi, "$1&lt;REPBR&gt;");
	sHTML = sHTML.replace(/(<\/h4>)/gi, "$1&lt;REPBR&gt;");
	sHTML = sHTML.replace(/(<\/h5>)/gi, "$1&lt;REPBR&gt;");
	sHTML = sHTML.replace(/(<\/h6>)/gi, "$1&lt;REPBR&gt;");
	sHTML = sHTML.replace(/(<p>)/gi, "$1&lt;REPBR&gt;");
	oWord.document.body.innerHTML=sHTML;	  
	oSel.pasteHTML(oWord.document.body.innerText.replace(/<REPBR>/gi, "<br>"));
	}
function insertCustomTag(index)
	{
	this.hide();

	if(!this.checkFocus())return;//Focus stuff

	this.insertHTML(this.arrCustomTag[index][1]);
	this.hide();
	this.focus();
	}
function insertHTML(sHTML)
	{
	if(!this.checkFocus())return;//Focus stuff

	var oEditor=eval("idContent"+this.oName);
	var oSel=oEditor.document.selection.createRange();
	
	this.saveForUndo();

	var arrA = String(sHTML).match(/<A[^>]*>/ig);
	if(arrA)
		for(var i=0;i<arrA.length;i++)
			{
			sTmp = arrA[i].replace(/href=/,"href_iwe=");
			sHTML=String(sHTML).replace(arrA[i],sTmp);
			}

	var arrB = String(sHTML).match(/<IMG[^>]*>/ig);
	if(arrB)
		for(var i=0;i<arrB.length;i++)
			{
			sTmp = arrB[i].replace(/src=/,"src_iwe=");
			sHTML=String(sHTML).replace(arrB[i],sTmp);
			}

	if(oSel.parentElement)oSel.pasteHTML(sHTML);
	else oSel.item(0).outerHTML=sHTML;

	for(var i=0;i<oEditor.document.all.length;i++)
		{
		if(oEditor.document.all[i].getAttribute("href_iwe"))
			{
			oEditor.document.all[i].href=oEditor.document.all[i].getAttribute("href_iwe");
			oEditor.document.all[i].removeAttribute("href_iwe",0);
			}
		if(oEditor.document.all[i].getAttribute("src_iwe"))
			{
			oEditor.document.all[i].src=oEditor.document.all[i].getAttribute("src_iwe");
			oEditor.document.all[i].removeAttribute("src_iwe",0);
			}
		}
	}
function insertLink(url,title,target)
	{
	if(!this.checkFocus())return;//Focus stuff
	var oEditor=eval("idContent"+this.oName);
	var oSel=oEditor.document.selection.createRange();

	this.saveForUndo();

	if(oSel.parentElement)
		{
		if(oSel.text=="")
			{
			var oSelTmp=oSel.duplicate();
			if(title!="" && title!=undefined) oSel.text=title;
			else oSel.text=url;
			oSel.setEndPoint("StartToStart",oSelTmp);
			oSel.select();
			}
		}
	oSel.execCommand("CreateLink",false,url);

	if (oSel.parentElement)	oEl=GetElement(oSel.parentElement(),"A");
	else oEl=GetElement(oSel.item(0),"A");
	if(oEl)
		{
		if(target!="" && target!=undefined)oEl.target=target;
		}
	}
function clearAll()
	{
	if(confirm(getTxt("Are you sure you wish to delete all contents?"))==true)
		{
		var oEditor=eval("idContent"+this.oName);
		this.saveForUndo();
		oEditor.document.body.innerHTML="";
		}
	}
function applySpan()
	{
	if(!this.checkFocus())return;//Focus stuff

	var oEditor=eval("idContent"+this.oName);
	var oSel=oEditor.document.selection.createRange();
	var sType=oEditor.document.selection.type;
	if(sType=="Control"||sType=="None")return;

	sHTML=oSel.htmlText;

	var oParent=oSel.parentElement();
	if(oParent)
	if(oParent.innerText==oSel.text)
		{
		/*
		for(var j=0;j<arrTmp2.length;j++)
			{
			if(arrTmp2[j]==oParent)
				{
				alert(arrTmp2[j].tagName)
				}
			}*/
		if(oParent.tagName=="SPAN")
			{
			idSpan=oParent;
			return idSpan;
			}
		}

	var arrA = String(sHTML).match(/<A[^>]*>/ig);
	if(arrA)
		for(var i=0;i<arrA.length;i++)
			{
			sTmp = arrA[i].replace(/href=/,"href_iwe=");
			sHTML=String(sHTML).replace(arrA[i],sTmp);
			}

	var arrB = String(sHTML).match(/<IMG[^>]*>/ig);
	if(arrB)
		for(var i=0;i<arrB.length;i++)
			{
			sTmp = arrB[i].replace(/src=/,"src_iwe=");
			sHTML=String(sHTML).replace(arrB[i],sTmp);
			}

	oSel.pasteHTML("<span id='idSpan__abc'>"+sHTML+"</span>");
	var idSpan=oEditor.document.all.idSpan__abc;

	var oSelRange=oEditor.document.body.createTextRange();
	oSelRange.moveToElementText(idSpan);
	oSel.setEndPoint("StartToStart",oSelRange);
	oSel.setEndPoint("EndToEnd",oSelRange);
	oSel.select();

	for(var i=0;i<oEditor.document.all.length;i++)
		{
		if(oEditor.document.all[i].getAttribute("href_iwe"))
			{
			oEditor.document.all[i].href=oEditor.document.all[i].getAttribute("href_iwe");
			oEditor.document.all[i].removeAttribute("href_iwe",0);
			}
		if(oEditor.document.all[i].getAttribute("src_iwe"))
			{
			oEditor.document.all[i].src=oEditor.document.all[i].getAttribute("src_iwe");
			oEditor.document.all[i].removeAttribute("src_iwe",0);
			}
		}

	idSpan.removeAttribute("id",0);
	return idSpan;
	}
function makeAbsolute()
	{
	if(!this.checkFocus())return;//Focus stuff

	var oEditor=eval("idContent"+this.oName);
	var oSel=oEditor.document.selection.createRange();
	this.saveForUndo();

	if(oSel.parentElement)
		{
		var oElement=oSel.parentElement();
		oElement.style.position="absolute";
		}
	else
		this.doCmd("AbsolutePosition");
	}
//~~~~~~~~~~~~~
function expandSelection()
	{
	if(!this.checkFocus())return;//Focus stuff

	var oEditor=eval("idContent"+this.oName);
	var oSel=oEditor.document.selection.createRange();
	if(oSel.text!="")return;

	oSel.expand("word");
	oSel.select();
	if(oSel.text.substr(oSel.text.length*1-1,oSel.text.length)==" ")
		{
		oSel.moveEnd("character",-1);
		oSel.select();
		}
	}
function selectParagraph()
	{
	if(!this.checkFocus())return;//Focus stuff

	var oEditor=eval("idContent"+this.oName);
	var oSel=oEditor.document.selection.createRange();

	if(oSel.parentElement)
		{
		if(oSel.text=="")
			{
			var oElement=oSel.parentElement();
			while (oElement!=null&&
				oElement.tagName!="H1"&&oElement.tagName!="H2"&&
				oElement.tagName!="H3"&&oElement.tagName!="H4"&&
				oElement.tagName!="H5"&&oElement.tagName!="H6"&&
				oElement.tagName!="PRE"&&oElement.tagName!="P"&&
				oElement.tagName!="DIV")
				{
				if(oElement.tagName=="BODY")return;
				oElement=oElement.parentElement;
				}
			var oSelRange = oEditor.document.body.createControlRange();
			try
				{
				oSelRange.add(oElement);
				oSelRange.select();
				}
			catch(e)
				{
				var oSelRange = oEditor.document.body.createTextRange();
				try{oSelRange.moveToElementText(oElement);
					oSelRange.select()
					}catch(e){;}
				}
			}
		}
	}

/*** Table Insert Dropdown ***/
function doOver_TabCreate()
	{
	var oTD=event.srcElement;
	var oTable=oTD.parentElement.parentElement.parentElement;
	var nRow=oTD.parentElement.rowIndex;
	var nCol=oTD.cellIndex;
	var rows=oTable.rows;
	rows[rows.length-1].childNodes[0].innerHTML="<div align=right>"+(nRow*1+1) + " x " + (nCol*1+1) + " Table ...  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span style='text-decoration:underline'>Advanced</span>&nbsp;</div>";
	for(var i=0;i<rows.length-1;i++)
		{
		var oRow=rows[i];
		for(var j=0;j<oRow.childNodes.length;j++)
			{
			var oCol=oRow.childNodes[j];
			if(i<=nRow&&j<=nCol)oCol.style.backgroundColor="#316ac5";
			else oCol.style.backgroundColor="#ffffff";
			}
		}
	event.cancelBubble=true;
	}
function doOut_TabCreate()
	{
	var oTable=event.srcElement;
	if(oTable.tagName!="TABLE")return;
	var rows=oTable.rows;
	rows[rows.length-1].childNodes[0].innerText=getTxt("Advanced Table Insert");
	for(var i=0;i<rows.length-1;i++)
		{
		var oRow=rows[i];
		for(var j=0;j<oRow.childNodes.length;j++)
			{
			var oCol=oRow.childNodes[j];
			oCol.style.backgroundColor="#ffffff";
			}
		}
	event.cancelBubble=true;
	}
function doRefresh_TabCreate()
	{
	var oTable=eval("dropTableCreate"+this.oName);
	var rows=oTable.rows;
	rows[rows.length-1].childNodes[0].innerText=getTxt("Advanced Table Insert");
	for(var i=0;i<rows.length-1;i++)
		{
		var oRow=rows[i];
		for(var j=0;j<oRow.childNodes.length;j++)
			{
			var oCol=oRow.childNodes[j];
			oCol.style.backgroundColor="#ffffff";
			}
		}
	}
function doClick_TabCreate()
	{
	this.hide();

	if(!this.checkFocus())return;//Focus stuff

	var oEditor=eval("idContent"+this.oName);
	var oSel=oEditor.document.selection.createRange();

	var oTD=event.srcElement;
	var nRow=oTD.parentElement.rowIndex+1;
	var nCol=oTD.cellIndex+1;

	this.saveForUndo();

	var sHTML="<table style='border-collapse:collapse;width:99%;' class='linkeytable'>";
	for(var i=1;i<=nRow;i++)
		{
		sHTML+="<tr>";
		var cwidth=100/nCol;
		if(nCol==4)
		{
			sHTML+="<td align=right width=15% ></td><td width=35% ></td><td align=right width=15% ></td><td width=35% ></td>";
		}else if(nCol==6)
		{
			sHTML+="<td width=10% align=right></td> <td width=23%></td><td width=10% align=right></td><td width=23%></td><td width=10% align=right></td><td width=23%></td>";
		}
		else if(nCol==2)
		{
			sHTML+="<td align=right width=15% ></td><td width=85% ></td>";
		}else if(nCol==1)
		{
			sHTML+="<td width=100% ></td>";
		}else if(nCol>2)
		{
			for(var j=1;j<=nCol;j++)
			{
				if((j % 2) && nCol>1){sHTML+="<td align=right width="+cwidth+"% ></td>";}else{sHTML+="<td width="+cwidth+"% ></td>";}
			}
		}
		sHTML+="</tr>";
		}
	sHTML+="</table>";

	if(oSel.parentElement)
		oSel.pasteHTML(sHTML);
	else
		oSel.item(0).outerHTML = sHTML;

	realTime(this.oName);

	//*** RUNTIME STYLES ***
	this.runtimeBorder(false);
	this.runtimeStyles();
	//***********************
	}

/*** doKeyPress ***/
function doKeyPress(evt,oName)
	{
	if(!eval(oName).arrUndoList[0]){eval(oName).saveForUndo();}//pengganti saveForUndo_First

	if(evt.ctrlKey)
		{
		if(evt.keyCode==89)
			{//CTRL-Y (Redo)
			if (!evt.altKey) eval(oName).doRedo();
			}
		if(evt.keyCode==90)
			{//CTRL-Z (Undo)
			if (!evt.altKey) eval(oName).doUndo();
			}
		if(evt.keyCode==83)
			{
			try{parent.SaveForm("save");}catch(e){}
			}
		if(evt.keyCode==65)
			{//CTRL-A (Select All) => spy jalan di modal dialog
			if (!evt.altKey) eval(oName).doCmd("SelectAll");
			}
		}

	if(evt.keyCode==37||evt.keyCode==38||evt.keyCode==39||evt.keyCode==40)//Arrow
		{
		eval(oName).saveForUndo();//Save for Undo
		}
	if(evt.keyCode==13)
		{
		if(eval(oName).useDIV && !eval(oName).useBR)
			{
			var oSel=document.selection.createRange();

			if(oSel.parentElement)
				{
				eval(oName).saveForUndo();//Save for Undo

				if(GetElement(oSel.parentElement(),"FORM"))
					{
					var oSel=document.selection.createRange();
					oSel.pasteHTML('<br>');
					evt.cancelBubble=true;
					evt.returnValue=false;
					oSel.select();
					oSel.moveEnd("character", 1);
					oSel.moveStart("character", 1);
					oSel.collapse(false);
					return false;
					}
				else
					{
					var oEl = GetElement(oSel.parentElement(),"H1");
					if(!oEl) oEl = GetElement(oSel.parentElement(),"H2");
					if(!oEl) oEl = GetElement(oSel.parentElement(),"H3");
					if(!oEl) oEl = GetElement(oSel.parentElement(),"H4");
					if(!oEl) oEl = GetElement(oSel.parentElement(),"H5");
					if(!oEl) oEl = GetElement(oSel.parentElement(),"H6");
					if(!oEl) oEl = GetElement(oSel.parentElement(),"PRE");
					if(!oEl)eval(oName).doCmd("FormatBlock","<div>");
					return true;
					}
				}
			}
		if((eval(oName).useDIV && eval(oName).useBR)||
			(!eval(oName).useDIV && eval(oName).useBR))
			{
			var oSel=document.selection.createRange();
			oSel.pasteHTML('<br>');
			evt.cancelBubble=true;
			evt.returnValue=false;
			oSel.select();
			oSel.moveEnd("character", 1);
			oSel.moveStart("character", 1);
			oSel.collapse(false);
			return false;
			}
		eval(oName).saveForUndo();//Save for Undo
		}
	eval(oName).onKeyPress()
	}

/*** FullScreen **/
function fullScreen()
	{
	this.hide();

	var oEditor=eval("idContent"+this.oName);

	if(this.stateFullScreen)
		{
		this.onNormalScreen();
		this.stateFullScreen=false;
		document.body.style.overflow="";

		eval("idArea"+this.oName).style.position="";
		eval("idArea"+this.oName).style.top=0;
		eval("idArea"+this.oName).style.left=0;
		eval("idArea"+this.oName).style.width=this.width;
		eval("idArea"+this.oName).style.height=this.height;
		
		var ifrm=document.getElementById("idFixZIndex"+this.oName);
		ifrm.style.top=0;
		ifrm.style.left=0;
		ifrm.style.width=0;
		ifrm.style.height=0;
		ifrm.style.display="none";
		
		//fix undisplayed content (new)
		oEditor.document.body.style.lineHeight="1.2";
		window.setTimeout("eval('idContent"+this.oName+"').document.body.style.lineHeight='';",0);

		for(var i=0;i<oUtil.arrEditor.length;i++)
			{
			if(oUtil.arrEditor[i]!=this.oName)eval("idArea"+oUtil.arrEditor[i]).style.display="block";
			}
		}
	else
		{
		this.onFullScreen();
		this.stateFullScreen=true;
		scroll(0,0);
		document.body.style.overflow="hidden";

		eval("idArea"+this.oName).style.position="absolute";
		eval("idArea"+this.oName).style.top=0;
		eval("idArea"+this.oName).style.left=0;

		var numOfBrk=0;
		for(var j=0;j<this.buttonMap.length;j++)if(this.buttonMap[j]=="BRK")numOfBrk++;

		nToolbarHeight=(numOfBrk+1)*27;

		if (document.compatMode && document.compatMode!="BackCompat")
			{
			//using doctype
			try
				{
				var tes=dialogArguments;
				w=(document.body.offsetWidth);
				document.body.style.height="100%";
				h=document.body.offsetHeight-nToolbarHeight;
				document.body.style.height="";
				}
			catch(e)
				{
				w=(document.body.offsetWidth+20);
				document.body.style.height="100%";
				h=document.body.offsetHeight-nToolbarHeight;
				document.body.style.height="";
				}
			}
		else
			{
			if(document.body.style.overflow=="hidden")
				{
				w=document.body.offsetWidth;
				}
			else
				{
				w=document.body.offsetWidth-22;
				}
			h=document.body.offsetHeight-4;
			}

		if (document.compatMode && document.compatMode!="BackCompat")
			{
			//using doctype => need adjustment. TODO: create as properties.
			w=w-20;
			h=h-13;
			}
			
		eval("idArea"+this.oName).style.width=w;
		eval("idArea"+this.oName).style.height=h;
		
		var ifrm=document.getElementById("idFixZIndex"+this.oName);
		ifrm.style.top=0;
		ifrm.style.left=0;
		ifrm.style.width=w;
		ifrm.style.height=h;
		ifrm.style.display="";

		for(var i=0;i<oUtil.arrEditor.length;i++)
			{
			if(oUtil.arrEditor[i]!=this.oName)eval("idArea"+oUtil.arrEditor[i]).style.display="none";
			}
		
		//fix undisplayed content (new)
		oEditor.document.body.style.lineHeight="1.2";
		window.setTimeout("eval('idContent"+this.oName+"').document.body.style.lineHeight='';",0);

		oEditor.focus();
		}
	
	var idStyles=document.getElementById("idStyles"+this.oName);
	idStyles.innerHTML="";
	}

/*** Show/Hide Dropdown ***/
function dropShow(oEl,box)
	{
	this.hide();

	box.style.display="block";
	var nTop=0;
	var nLeft=0;

	oElTmp=oEl;
	while(oElTmp.tagName!="BODY" && oElTmp.tagName!="HTML")
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

	box.style.left=nLeft+this.dropLeftAdjustment;
	box.style.top=nTop+1+this.dropTopAdjustment;
	}
function hide()
	{
	if(this.btnPreview)eval("dropPreview"+this.oName).style.display="none";
	if(this.btnTextFormatting||this.btnParagraphFormatting||this.btnListFormatting||this.btnBoxFormatting||this.btnCssText||this.btnCssBuilder)eval("dropStyle"+this.oName).style.display="none";
	if(this.btnParagraph)eval("dropParagraph"+this.oName).style.display="none";
	if(this.btnFontName)eval("dropFontName"+this.oName).style.display="none";
	if(this.btnFontSize)eval("dropFontSize"+this.oName).style.display="none";
	if(this.btnTable)eval("dropTable"+this.oName).style.display="none";
	if(this.btnTable)eval("dropTableCreate"+this.oName).style.display="none";
	if(this.btnForm)eval("dropForm"+this.oName).style.display="none";
	if(this.btnCustomTag)eval("dropCustomTag"+this.oName).style.display="none";

	this.oColor1.hide();
	this.oColor2.hide();

	//additional
	if(this.btnTable)this.doRefresh_TabCreate();
	}

/*** Open Dialog ***/
function modelessDialogShow(url,width,height)
	{
	window.showModelessDialog(url,window,
		"dialogWidth:"+width+"px;dialogHeight:"+height+"px;edge:Raised;center:1;help:0;resizable:1;");
	}
function modalDialogShow(url,width,height)
	{
	window.showModalDialog(url,window,
		"dialogWidth:"+width+"px;dialogHeight:"+height+"px;edge:Raised;center:1;help:0;resizable:1;maximize:1");
	}
function windowOpen(url,width,height)
	{
	window.open(url,"","width="+width+"px,height="+height+"px;toolbar=no,menubar=no,location=no,directories=no,status=yes")
	}

/*** HTML to XHTML ***/
function lineBreak1(tag) //[0]<TAG>[1]text[2]</TAG>
	{
	arrReturn = ["\n","",""];
	if(	tag=="A"||tag=="B"||tag=="CITE"||tag=="CODE"||tag=="EM"||
		tag=="FONT"||tag=="I"||tag=="SMALL"||tag=="STRIKE"||tag=="BIG"||
		tag=="STRONG"||tag=="SUB"||tag=="SUP"||tag=="U"||tag=="SAMP"||
		tag=="S"||tag=="VAR"||tag=="BASEFONT"||tag=="KBD"||tag=="TT")
		arrReturn=["","",""];

	if(	tag=="TEXTAREA"||tag=="TABLE"||tag=="THEAD"||tag=="TBODY"||
		tag=="TR"||tag=="OL"||tag=="UL"||tag=="DIR"||tag=="MENU"||
		tag=="FORM"||tag=="SELECT"||tag=="MAP"||tag=="DL"||tag=="HEAD"||
		tag=="BODY"||tag=="HTML")
		arrReturn=["\n","","\n"];

	if(	tag=="STYLE"||tag=="SCRIPT")
		arrReturn=["\n","",""];

	if(tag=="BR"||tag=="HR")
		arrReturn=["","\n",""];

	return arrReturn;
	}
function fixAttr(s)
	{
	s = String(s).replace(/&/g, "&amp;");
	s = String(s).replace(/</g, "&lt;");
	s = String(s).replace(/"/g, "&quot;");
	return s;
	}
function fixVal(s)
	{
	s = String(s).replace(/&/g, "&amp;");
	s = String(s).replace(/</g, "&lt;");
	var x = escape(s);
	x = unescape(x.replace(/\%A0/gi, "-*REPL*-"));
	s = x.replace(/-\*REPL\*-/gi, "&nbsp;");
	return s;
	}
function recur(oEl,sTab)
	{
	var sHTML="";
	for(var i=0;i<oEl.childNodes.length;i++)
		{
		var oNode=oEl.childNodes(i);
		if(oNode.nodeType==1)//tag
			{
			var sTagName = oNode.nodeName;

			var bDoNotProcess=false;
			if(sTagName.substring(0,1)=="/")
				{
				bDoNotProcess=true;//do not process
				}
			else
				{
				/*** tabs ***/
				var sT= sTab;
				sHTML+= lineBreak1(sTagName)[0];
				if(lineBreak1(sTagName)[0] !="") sHTML+= sT;//If new line, use base Tabs
				/************/
				}

			if(bDoNotProcess)
				{
				;//do not process
				}
			else if(sTagName=="OBJECT" || sTagName=="EMBED")
				{
				s=oNode.outerHTML;

				s=s.replace(/\"[^\"]*\"/ig,function(x){
						x=x.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/'/g, "&apos;").replace(/\s+/ig,"#_#");
						return x});
				s=s.replace(/<([^ >]*)/ig,function(x){return x.toLowerCase()});		
				s=s.replace(/ ([^=]+)=([^"' >]+)/ig," $1=\"$2\"");//new
				s=s.replace(/ ([^=]+)=/ig,function(x){return x.toLowerCase()});
				s=s.replace(/#_#/ig," ");

				s=s.replace(/<param([^>]*)>/ig,"\n<param$1 />").replace(/\/ \/>$/ig," \/>");//no closing tag

				if(sTagName=="EMBED")
					if(oNode.innerHTML=="")
						s=s.replace(/>$/ig," \/>").replace(/\/ \/>$/ig,"\/>");//no closing tag

				s=s.replace(/<param name=\"Play\" value=\"0\" \/>/,"<param name=\"Play\" value=\"-1\" \/>");

				sHTML+=s;
				}
			else if(sTagName=="TITLE")
				{
				sHTML+="<title>"+oNode.innerHTML+"</title>";
				}
			else
				{
				if(sTagName=="AREA")
					{
					var sCoords=oNode.coords;
					var sShape=oNode.shape;
					}

				var oNode2=oNode.cloneNode();
				if (oNode.checked) oNode2.checked=oNode.checked;
				s=oNode2.outerHTML.replace(/<\/[^>]*>/,"");

				if(sTagName=="STYLE")
					{
					var arrTmp=s.match(/<[^>]*>/ig);
					s=arrTmp[0];
					}

				s=s.replace(/\"[^\"]*\"/ig,function(x){
						//x=x.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/'/g, "&apos;").replace(/\s+/ig,"#_#");
						//x=x.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/\s+/ig,"#_#");
						x=x.replace(/&/g, "&amp;").replace(/&amp;amp;/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/\s+/ig,"#_#");
						return x});
						//info ttg: .replace(/&amp;amp;/g, "&amp;")
						//ini karena '&' di (hanya) '&amp;' selalu di-replace lagi dgn &amp;.
						//tapi kalau &lt; , &gt; tdk (no problem) => default behaviour

				s=s.replace(/<([^ >]*)/ig,function(x){return x.toLowerCase()});		
				s=s.replace(/ ([^=]+)=([^" >]+)/ig," $1=\"$2\"");
				s=s.replace(/ ([^=]+)=/ig,function(x){return x.toLowerCase()});
				s=s.replace(/#_#/ig," ");

				//single attribute
				s=s.replace(/[<hr]?(noshade)/ig,"noshade=\"noshade\"");
				s=s.replace(/[<input]?(checked)/ig,"checked=\"checked\"");
				s=s.replace(/[<select]?(multiple)/ig,"multiple=\"multiple\"");
				s=s.replace(/[<option]?(selected)/ig,"selected=\"true\"");
				s=s.replace(/[<input]?(readonly)/ig,"readonly=\"readonly\"");
				s=s.replace(/[<input]?(disabled)/ig,"disabled=\"disabled\"");
				s=s.replace(/[<td]?(nowrap )/ig,"nowrap=\"nowrap\" ");
				s=s.replace(/[<td]?(nowrap\>)/ig,"nowrap=\"nowrap\"\>");

				s=s.replace(/ contenteditable=\"true\"/ig,"");

				if(sTagName=="AREA")
					{
					s=s.replace(/ coords=\"0,0,0,0\"/ig," coords=\""+sCoords+"\"");
					s=s.replace(/ shape=\"RECT\"/ig," shape=\""+sShape+"\"");
					}

				var bClosingTag=true;
				if(sTagName=="IMG"||sTagName=="BR"||
					sTagName=="AREA"||sTagName=="HR"||
					sTagName=="INPUT"||sTagName=="BASE"||
					sTagName=="LINK")//no closing tag
					{
					s=s.replace(/>$/ig," \/>").replace(/\/ \/>$/ig,"\/>");//no closing tag
					bClosingTag=false;	
					}

				sHTML+=s;

				/*** tabs ***/
				if(sTagName!="TEXTAREA")sHTML+= lineBreak1(sTagName)[1];
				if(sTagName!="TEXTAREA")if(lineBreak1(sTagName)[1] !="") sHTML+= sT;//If new line, use base Tabs
				/************/

				if(bClosingTag)
					{
					/*** CONTENT ***/
					s=oNode.outerHTML;					
					if(sTagName=="SCRIPT")
						{
						s = s.replace(/<script([^>]*)>[\n+\s+\t+]*/ig,"<script$1>");//clean spaces
						s = s.replace(/[\n+\s+\t+]*<\/script>/ig,"<\/script>");//clean spaces
						s = s.replace(/<script([^>]*)>\/\/<!\[CDATA\[/ig,"");
						s = s.replace(/\/\/\]\]><\/script>/ig,"");
						s = s.replace(/<script([^>]*)>/ig,"");
						s = s.replace(/<\/script>/ig,"");		
						s = s.replace(/^\s+/,'').replace(/\s+$/,'');						

						sHTML+="\n"+
							sT + "//<![CDATA[\n"+
							sT + s + "\n"+
							sT + "//]]>\n"+sT;
						}
					if(sTagName=="STYLE")
						{
						s = s.replace(/<style([^>]*)>[\n+\s+\t+]*/ig,"<style$1>");//clean spaces
						s = s.replace(/[\n+\s+\t+]*<\/style>/ig,"<\/style>");//clean spaces			
						s = s.replace(/<style([^>]*)><!--/ig,"");
						s = s.replace(/--><\/style>/ig,"");
						s = s.replace(/<style([^>]*)>/ig,"");
						s = s.replace(/<\/style>/ig,"");		
						s = s.replace(/^\s+/,"").replace(/\s+$/,"");					

						sHTML+="\n"+
							sT + "<!--\n"+
							sT + s + "\n"+
							sT + "-->\n"+sT;
						}
					if(sTagName=="DIV"||sTagName=="P")
						{
						if(oNode.innerHTML==""||oNode.innerHTML=="&nbsp;")
							{
							sHTML+="&nbsp;";
							}
						else sHTML+=recur(oNode,sT+"\t");
						}
					else
						{
						sHTML+=recur(oNode,sT+"\t");
						}

					/*** tabs ***/
					if(sTagName!="TEXTAREA")sHTML+=lineBreak1(sTagName)[2];
					if(sTagName!="TEXTAREA")if(lineBreak1(sTagName)[2] !="")sHTML+=sT;//If new line, use base Tabs
					/************/

					sHTML+="</" + sTagName.toLowerCase() + ">";
					}
				}
			}
		else if(oNode.nodeType==3)//text
			{
			sHTML+= fixVal(oNode.nodeValue);
			}
		else if(oNode.nodeType==8)
			{
			if(oNode.outerHTML.substring(0,2)=="<"+"%")
				{//server side script
				sTmp=(oNode.outerHTML.substring(2));
				sTmp=sTmp.substring(0,sTmp.length-2);
				sTmp = sTmp.replace(/^\s+/,"").replace(/\s+$/,"");

				/*** tabs ***/
				var sT= sTab;
				/************/

				sHTML+="\n" +
					sT + "<%\n"+
					sT + sTmp + "\n" +
					sT + "%>\n"+sT;
				}
			else
				{//comments
				sTmp=oNode.nodeValue;
				sTmp = sTmp.replace(/^\s+/,"").replace(/\s+$/,"");

				sHTML+="\n" +
					sT + "<!--\n"+
					sT + sTmp + "\n" +
					sT + "-->\n"+sT;
				}
			}
		else
			{
			;//Not Processed
			}
		}
	return sHTML;
	}

/*** TOOLBAR ICONS ***/
var buttonArrays=[];
var buttonArraysCount=0;

function writeIconToggle(id,command,img,title)
	{
	w=this.iconWidth;
	h=this.iconHeight;
	imgPath=this.scriptPath+this.iconPath+img;
	sHTML=""+
		"<td unselectable='on' style='padding:0px;padding-right:1px;VERTICAL-ALIGN: top;margin-left:0;margin-right:1px;margin-bottom:1px;width:"+w+"px;height:"+h+"px;'>"+
		"<span unselectable='on' style='position:absolute;clip: rect(0 "+w+"px "+h+"px 0)'>"+
		"<img name=\""+id+"\" id=\""+id+"\" btnIndex=\""+buttonArraysCount+"\" unselectable='on' src='"+imgPath+"' style='position:absolute;top:0;width:"+w+"px'"+
		"onmouseover='doOver(this)' "+
		"onmouseout='doOut(this)' "+
		"onmousedown='doDown(this)' "+
		"onmouseup=\"if(doUpToggle(this)){"+command+"}\" alt=\""+title+"\">"+
		"</span></td>";
	sHTML="<table align=left cellpadding=0 cellspacing=0 style='table-layout:fixed;'><tr>"+sHTML+"</tr></table>";
	buttonArrays.push(["inactive"]);
	buttonArraysCount++;
	return sHTML;
	}
function writeIconStandard(id,command,img,title,width)
	{
	w=this.iconWidth;
	h=this.iconHeight;
	if(width)w=width;
	imgPath=this.scriptPath+this.iconPath+img;
	sHTML=""+
		"<td unselectable='on' style='padding:0px;padding-right:1px;VERTICAL-ALIGN: top;margin-left:0;margin-right:1px;margin-bottom:1px;width:"+w+"px;height:"+h+"px;'>"+
		"<span unselectable='on' style='position:absolute;clip: rect(0 "+w+"px "+h+"px 0)'>"+
		"<img name=\""+id+"\" id=\""+id+"\" btnIndex=\""+buttonArraysCount+"\" unselectable='on' src='"+imgPath+"' style='position:absolute;top:0;width:"+w+"px'"+
		"onmouseover='doOver(this)' "+
		"onmouseout='doOut(this)' "+
		"onmousedown='doDown(this)' "+
		"onmouseup=\"if(doUp(this)){"+command+"}\" alt=\""+title+"\">"+
		"</span></td>";
	sHTML="<table align=left cellpadding=0 cellspacing=0 style='table-layout:fixed;'><tr>"+sHTML+"</tr></table>";
	buttonArrays.push(["inactive"]);
	buttonArraysCount++;
	return sHTML;
	}
function writeBreakSpace()
	{
	w=this.iconWidth;
	h=this.iconHeight;
	imgPath=this.scriptPath+this.iconPath+"brkspace.gif";
	sHTML=""+
		"<td unselectable='on' style='padding:0px;padding-left:0px;padding-right:0px;VERTICAL-ALIGN:top;margin-bottom:1px;width:5px;height:"+h+"px;'>"+
		"<img unselectable='on' src='"+imgPath+"'></td>";
	sHTML="<table align=left cellpadding=0 cellspacing=0 style='table-layout:fixed;'><tr>"+sHTML+"</tr></table>";
	return sHTML;
	}
function writeDropDown(id,command,img,title,width)
	{
	w=width;
	h=this.iconHeight;

	/*** Localization ***/
	imgPath=this.scriptPath+this.iconPath+oUtil.langDir+"/"+img;
	/*** /Localization ***/

	sHTML=""+
		"<td unselectable='on' style='padding:0px;padding-right:1px;VERTICAL-ALIGN: top;margin-left:0;margin-right:1px;margin-bottom:1px;width:"+w+"px;height:"+h+"px;'>"+
		"<span unselectable='on' style='position:absolute;clip: rect(0 "+w+"px "+h+"px 0)'>"+
		"<img name=\""+id+"\" id=\""+id+"\" btnIndex=\""+buttonArraysCount+"\" unselectable='on' src='"+imgPath+"' style='position:absolute;top:0;width:"+w+"px'"+
		"onmouseover='doOver(this)' "+
		"onmouseout='doOut(this)' "+
		"onmousedown='doDown(this)' "+
		"onmouseup=\"if(doUp(this)){"+command+"}\" alt=\""+title+"\">"+
		"</span></td>";
	sHTML="<table align=left cellpadding=0 cellspacing=0 style='table-layout:fixed;'><tr>"+sHTML+"</tr></table>";
	buttonArrays.push(["inactive"]);
	buttonArraysCount++;
	return sHTML;
	}
function doOver(btn)
	{
	btnArr=buttonArrays[btn.btnIndex];
	if(btnArr[0]=="inactive")btn.style.top=-iconHeight;//no.2
	}
function doDown(btn)
	{
	btnArr=buttonArrays[btn.btnIndex];
	if(btnArr[0]!="disabled")btn.style.top=-iconHeight*2;//no.3
	}
var bCancel=false;
function doOut(btn)
	{
	if(btn.style.top=="-"+iconHeight*2+"px")
		{//lagi pushed tapi mouseout (cancel)
		bCancel=true;
		}
	btnArr=buttonArrays[btn.btnIndex];
	if(btnArr[0]=="active")btn.style.top=-iconHeight*3;//no.4 (remain active/pushed)
	if(btnArr[0]=="inactive")btn.style.top=0;//no.1 (remain inactive)
	}
function doUpToggle(btn)
	{
	if(bCancel)
		{//lagi pushed tapi mouseout (cancel)
		bCancel=false;btn.style.top=0;
		return false;
		}
	btnArr = buttonArrays[btn.btnIndex];
	if(btnArr[0]=="inactive")
		{
		btn.style.top=-iconHeight*3;//no.4
		btnArr[0]="active";
		return true;
		}
	if(btnArr[0]=="active")
		{
		btn.style.top=-iconHeight;//no.2
		btnArr[0]="inactive";
		return true;
		}
	}
function doUp(btn)//return true/false
	{
	if(bCancel)
		{//lagi pushed tapi mouseout (cancel)
		bCancel=false;btn.style.top=0;
		return false;
		}
	btnArr=buttonArrays[btn.btnIndex];
	if(btnArr[0]=="disabled") return false;
	btn.style.top=-iconHeight;//no.2
	return true;
	}
function makeEnablePushed(btn)
	{
	btnArr=buttonArrays[btn.btnIndex];
	btnArr[0]="active";
	btn.style.top=-iconHeight*3;//no.4
	}
function makeEnableNormal(btn)
	{
	btnArr=buttonArrays[btn.btnIndex];
	btnArr[0]="inactive";
	btn.style.top=0;//no.1
	}
function makeDisabled(btn)
	{
	btnArr=buttonArrays[btn.btnIndex];
	btnArr[0]="disabled";
	btn.style.top=-iconHeight*4;//no.5
	}
	

	//Edit By Iryc
	//创建右键菜单
function showContextmenu(strName)
	{
	var oEditor=eval("idContent"+strName);
	var gobj=oEditor.event.srcElement;
	var tagName=gobj.tagName.toLowerCase();
	if(tagName=="input" || tagName=="select" || tagName=="textarea" )
	{
		//(top.grid);
		var fdName="";
		if(gobj.name){fdName=gobj.name;}else{fdName=gobj.NAME;}
		oPopupMenu = oEditor.createPopup();
		var oPopDocument=oPopupMenu.document;
		oPopDocument.open();
		oPopDocument.cssText="margin:0px 0px 0px 0px";
		//将右键的HTML写在此处
		var pHTML="<TABLE width=100% border=0 margin='0px' cellSpacing=1 cellpadding=4 >";
		
		pHTML+="<TR><TD align='left' style='cursor:default;height:22px;padding-top:5px'  onmouseover='this.style.background=\"#f1f194\"' onmouseout='this.style.background=\"#d2d8ea\"'><img src='/icons/vwicn184.gif' align=absmiddel >字段名：<input name='fdname' value='"+fdName+"' size=15 ></TD></TR>";
		pHTML+="<TR><TD height=1 bgcolor=cccccc ></td></tr>";
		pHTML+="<TR><TD align='left' style='cursor:default;height:22px' onclick='parent.parent.AddNewField(\""+fdName+"\");' onmouseover='this.style.background=\"#f1f194\"' onmouseout='this.style.background=\"#d2d8ea\"'><img src='/linkey/bpm/images/r.gif' align=absmiddel >加入到字段列表中</TD></TR>";
		pHTML+="<TR><TD align='left' style='cursor:default;height:22px' onclick='parent.parent.SetNotNull(\""+fdName+"\");' onmouseover='this.style.background=\"#f1f194\"' onmouseout='this.style.background=\"#d2d8ea\"'><img src='/linkey/bpm/images/r.gif' align=absmiddel >不允许为空</TD></TR>";
		pHTML+="<TR><TD align='left' style='cursor:default;height:22px' onclick='parent.parent.SetDicData(\""+fdName+"\");' onmouseover='this.style.background=\"#f1f194\"' onmouseout='this.style.background=\"#d2d8ea\"'><img src='/linkey/bpm/images/r.gif' align=absmiddel >匹配数据字典</TD></TR>";
		pHTML+="<TR><TD align='left' style='cursor:default;height:22px' onclick='parent.parent.SetFdSql(\""+fdName+"\");' onmouseover='this.style.background=\"#f1f194\"' onmouseout='this.style.background=\"#d2d8ea\"'><img src='/linkey/bpm/images/r.gif' align=absmiddel >绑定高级规则</TD></TR>";
		pHTML+="<TR><TD align='left' style='cursor:default;height:22px' onclick='parent.parent.FocusFieldRow(\""+fdName+"\");' onmouseover='this.style.background=\"#f1f194\"' onmouseout='this.style.background=\"#d2d8ea\"'><img src='/linkey/bpm/images/r.gif' align=absmiddel >切换至字段属性</TD></TR>";

		pHTML+="</TABLE>";
		oPopDocument.write(pHTML);
		oPopDocument.close();
		
		oPopDocument.body.scroll="no";
		oPopDocument.body.style.cssText="border:1px #B0C4DE solid;padding:0px;margin:0px;background:#d2d8ea;";
		//oPopDocument.body.oncontextmenu=new Function("return false;");
		//显示右键菜单
		var x=oEditor.event.clientX;
		var y=oEditor.event.clientY;
		var w=260;//oPopDocument.body.scrollWidth;
		var h=180;//oPopDocument.body.scrollHeight;
		oPopupMenu.show(x,y,w,h,oEditor.document.body);
		return false;
	}else{
		var fdName="";
		if(gobj.name){fdName=gobj.name;}else{fdName=gobj.NAME;}
		oPopupMenu = oEditor.createPopup();
		var oPopDocument=oPopupMenu.document;
		oPopDocument.open();
		oPopDocument.cssText="margin:0px 0px 0px 0px";
		//将右键的HTML写在此处
		var pHTML="<TABLE width=100% border=0 margin='0px' cellSpacing=1 cellpadding=4 >";
		pHTML+="<TR><TD height=1 bgcolor=cccccc ></td></tr>";
		pHTML+="<TR><TD align='left' style='cursor:default;height:22px' onclick='parent.parent.AddBlankStr(\"[F]File$1[/F]\");' onmouseover='this.style.background=\"#f1f194\"' onmouseout='this.style.background=\"#d2d8ea\"'><img src='/icons/vwicn005.gif' align=absmiddel >插入附件上载框</TD></TR>";
		pHTML+="<TR><TD align='left' style='cursor:default;height:22px' onclick='parent.parent.AddBlankStr(\"[C][/C]\");' onmouseover='this.style.background=\"#f1f194\"' onmouseout='this.style.background=\"#d2d8ea\"'><img src='/linkey/bpm/images/r.gif' align=absmiddel >插入共享代码标签</TD></TR>";
		pHTML+="<TR><TD align='left' style='cursor:default;height:22px' onclick='parent.parent.AddBlankStr(\"[X][/X]\");' onmouseover='this.style.background=\"#f1f194\"' onmouseout='this.style.background=\"#d2d8ea\"'><img src='/linkey/bpm/images/r.gif' align=absmiddel >插入计算文本标签</TD></TR>";
        pHTML+="<TR><TD align='left' style='cursor:default;height:22px' onclick='parent.parent.AddBlankStr(\"[T]All[/T]\");' onmouseover='this.style.background=\"#f1f194\"' onmouseout='this.style.background=\"#d2d8ea\"'><img src='/linkey/bpm/images/r.gif' align=absmiddel >插入意见显示标签</TD></TR>";
		pHTML+="<TR><TD align='left' style='cursor:default;height:22px' onclick='parent.parent.AddBlankStr(\"[SUBFORM]\");' onmouseover='this.style.background=\"#f1f194\"' onmouseout='this.style.background=\"#d2d8ea\"'><img src='/linkey/bpm/images/r.gif' align=absmiddel >插入子表单定位标记</TD></TR>";
		pHTML+="<TR><TD align='left' style='cursor:default;height:22px' onclick=\"parent.parent.AddWordDoc();\" onmouseover='this.style.background=\"#f1f194\"' onmouseout='this.style.background=\"#d2d8ea\"'><img src='/linkey/bpm/images/r.gif' align=absmiddel >插入Word正文</TD></TR>";
		pHTML+="<TR><TD align='left' style='cursor:default;height:22px' onclick=\"parent.parent.SelectAttachment();\" onmouseover='this.style.background=\"#f1f194\"' onmouseout='this.style.background=\"#d2d8ea\"'><img src='/linkey/bpm/images/r.gif' align=absmiddel >插入附件模板</TD></TR>";
		pHTML+="<TR><TD align='left' style='cursor:default;height:22px' onclick=\"parent.parent.AddExcelData();\" onmouseover='this.style.background=\"#f1f194\"' onmouseout='this.style.background=\"#d2d8ea\"'><img src='/linkey/bpm/images/icons/fam/xls.gif' align=absmiddle >插入Excel导入数据功能</TD></TR>";
		pHTML+="</TABLE>";
		oPopDocument.write(pHTML);
		oPopDocument.close();
		
		oPopDocument.body.scroll="no";
		oPopDocument.body.style.cssText="border:1px #B0C4DE solid;padding:0px;margin:0px;background:#d2d8ea;";
		//oPopDocument.body.oncontextmenu=new Function("return false;");
		//显示右键菜单
		var x=oEditor.event.clientX;
		var y=oEditor.event.clientY;
		var w=220;//oPopDocument.body.scrollWidth;
		var h=225;//oPopDocument.body.scrollHeight;
		oPopupMenu.show(x,y,w,h,oEditor.document.body);
		return false;
	}

}

function AddNewField(FdName,Action)
{
	var spos=FdName.lastIndexOf("_");
	var sFdName=FdName.substring(0,spos);
	var rc=top.store.getRange(0,top.store.getCount());
	for(i=0;i<rc.length;i++)
	{
		if(rc[i].get("FdName")==FdName || rc[i].get("FdName")==sFdName+"_N")
		{
			if(Action!=0){alert("字段已经在列表中");}
			return false;
		}
	}

 var p = new top.Plant({FdName: FdName,FdInfo: "",FdMsg:"",FdType:"TEXT",FdBlank:"",FdCheck:"",FdValue:"",FdAction:"",FdFun:"",FdAcl:"",FdVar:"",FdSql:"",FdDataCfg:"",FdLS:"",FdUpdate:''});
 top.store.insert(0, p);
 if(Action!=0){alert("成功加入!");}
 top.SaveStore();
}

function SetNotNull(FdName)
{
	AddNewField(FdName,0);
	var rc=top.store.getRange(0,top.store.getCount());
	for(i=0;i<rc.length;i++)
	{
		if(rc[i].get("FdName")==FdName)
		{
			rc[i].set("FdBlank","true");
		    alert("成功设置!");
			top.SaveStore();
			return false;
		}
	}
	//alert("字段不在列表中，请先加入字段列表中!");
}

function SetDicData(FdName)
{
	AddNewField(FdName,0);
	var rc=top.store.getRange(0,top.store.getCount());
	for(i=0;i<rc.length;i++)
	{
		if(rc[i].get("FdName")==FdName)
		{
			rc[i].set("FdValue","数据字典");
		    alert("成功设置!");
			top.SaveStore();
			return false;
		}
	}
	//alert("字段不在属性列表中，请先执行加入操作!");
}

function FocusFieldRow(FdName)
{
	//AddNewField(FdName,0);
	var spos=FdName.lastIndexOf("_");
	var sFdName=FdName.substring(0,spos);
	var sm=top.grid.getSelectionModel();
	var rc=top.store.getRange(0,top.store.getCount());
	for(i=0;i<rc.length;i++)
	{
		if(rc[i].get("FdName")==FdName || rc[i].get("FdName")==sFdName+"_N" )
		{
			sm.selectRow(i);
			top.tab.setActiveTab("FdTab");
			return false;
		}
	}
	alert("字段不在属性列表中，请先执行加入操作!");
}
function AddBlankStr(HtmlStr)
{
	top.myiframe.oEdit1.insertHTML(HtmlStr);
}
function AddWordDoc()
{
	top.myiframe.oEdit1.insertHTML("<a onclick='OpenWordDoc();return false;' href='#'>Word正文</a>");
}
function AddExcelData()
{
	top.myiframe.oEdit1.insertHTML("<a onclick=\"GetDataFromExcel();return false;\" href=\"#\">从Excel中导入</a>");
}
function SetFdSql(FdName)
{
    AddNewField(FdName,0);
	var url="/"+parent.folder+"/linkey_form.nsf/frmSetFdSql?openform&FdName="+FdName;
	modalDialogShow(url,700,450);
}
function SelectAttachment()
{
	var url="/"+parent.folder+"/linkey_wordtemplate.nsf/WF_AttachmentListForSel?openform";
	modalDialogShow(url,700,450);
}