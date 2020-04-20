/***********************************************************
	Copyright © 2004, Yusuf Wiryonoputro. All rights reserved.
************************************************************/

function ListEditor(oName)
	{
	this.items
	this.oName = oName;
	
	this.listClick1 = listClick1;
	this.listClick2 = listClick2;
	
	this.RUN = RUN;
	this.add = add;
	this.remove = remove;

	this.row1
	this.row2
	
	this.onEdit = function(){return true;};
	
	this.getResultValues = getResultValues;
	this.getResultDisplayed = getResultDisplayed;
	}
function getResultValues()
	{
	oTable = eval("inpList2"+this.oName);
	sArr = ""
	for (var i=0;i<oTable.rows.length;i++)
		{
		if(oTable.rows[i].childNodes[0].innerText!="")
			{
			val = oTable.rows[i].childNodes[0].childNodes[0].innerText	
			sArr += "\"" + val + "\","
			}
		}
	sArr = sArr.substring(0,sArr.length-1)
	return eval("[" + sArr + "]")
	}
function getResultDisplayed()
	{
	oTable = eval("inpList2"+this.oName);
	sArr = ""
	for (var i=0;i<oTable.rows.length;i++)
		{
		
		if(oTable.rows[i].childNodes[0].innerText!="")
			{
			display = oTable.rows[i].childNodes[0].childNodes[1].innerText
			sArr += "\"" + display + "\","
			}
		}
	sArr = sArr.substring(0,sArr.length-1)
	return eval("[" + sArr + "]")		
	}
function RUN()
	{
	sHTML = "<input type=hidden name=inpObjName value='"+this.oName+"'>" +
			"<table width=100% cellpadding=0 cellspacing=0><tr><td>"
	
	sHTML+= "<div style='border:1 #7F9DB9 solid;padding:1'>" +
			"<div style='overflow:auto;width:120;height:80;border:1 #716F64 solid;border-bottom:1 #F1EFE2 solid;border-right:none;'>" +
			"<table id=inpList1"+this.oName+" style='border-collapse:collapse;width:100;height:80;background:#ffffff' cellpadding=1 cellspacing=0>"
	for(var i=0;i<this.items.length;i++)
		{
		var arrItem = this.items[i]
		if(arrItem[0]=="-" && arrItem[1]=="-")
			{
			sHTML+= "<tr><td style='height:2;border-bottom:1 silver dotted;'>" +
					"	<span style='display:none'>-</span><span style='display:none'>-</span>" +
					"</td></tr>"
			}
		else
			{
			sHTML+= "<tr><td onclick=\""+this.oName+".listClick1(this,['"+arrItem[0]+"','"+arrItem[1]+"','"+arrItem[2]+"'])\" style='cursor:default'>" +
					"	<span style='display:none'>"+arrItem[0]+"</span><span style='"+ arrItem[2] +"'>" + arrItem[1] + "</span>" +
					"</td></tr>"
			}
		}
	sHTML+= "<tr><td height=100%>" +
			"	<span style='display:none'>-</span><span style='display:none'>-</span>" +
			"</td></tr>" +
			"</table></div></div>"
	
	sHTML+= "</td><td style='padding-left:3;padding-right:3;' width=100% align=center>" +
			"	<input type=button style='width:100%' class=inpBtn value=' >> ' onclick='"+this.oName+".add()'><div style='padding:2'></div>" +
			"	<input type=button style='width:100%' class=inpBtn value=' << ' onclick='"+this.oName+".remove()'>" +
			"</td><td valign=top>" +
			"<div style='border:1 #7F9DB9 solid;padding:1'>" +
			"<div style='overflow:auto;width:120;height:80;border:1 #716F64 solid;border-bottom:1 #F1EFE2 solid;border-right:none;'>" +
			"	<table id=inpList2"+this.oName+" style='border-collapse:collapse;width:100;height:80;background:#ffffff' cellpadding=1 cellspacing=0>" +
			"	<tr><td height=100%></td></tr></table></div></div>" +
			"</td></tr></table>"

	return(sHTML)
	}
function listClick1(e,arrItem)
	{
	e.style.background = "#316AC5"
	e.style.color = "white"
	oTable = eval("inpList1"+this.oName);
	for (var i=0;i<oTable.rows.length;i++)
		{
		if(e!=oTable.rows[i].childNodes[0])	
			{
			oTable.rows[i].childNodes[0].style.background = "";
			oTable.rows[i].childNodes[0].style.color = "";
			}
		}
	
	this.row1 = e.parentElement //store selected row
	}
function listClick2()
	{
	oName = eval(inpObjName.value).oName //this.oName //=> fail
	oTD = event.srcElement.parentElement
	if(oTD.tagName=="TR")
		{
		oTD = oTD.childNodes[0]
		}
	oTD.style.background = "#316AC5"
	oTD.style.color = "white"
	oTable = eval("inpList2"+oName);
	for (var i=0;i<oTable.rows.length;i++)
		{
		if(oTD==oTable.rows[i].childNodes[0])	
			{
			eval(oName).row2 = oTable.rows[i]
			}
		else
			{
			oTable.rows[i].childNodes[0].style.background = "";
			oTable.rows[i].childNodes[0].style.color = "";			
			}
		}
	}
function add()
	{
	if(this.row1.style.display=="none") return;
	
	val = this.row1.childNodes[0].childNodes[0].innerText
	display = this.row1.childNodes[0].childNodes[1].innerText
	style = this.row1.childNodes[0].childNodes[1].style.cssText

	var eRow = eval("inpList2"+this.oName).insertRow(eval("inpList2"+this.oName).rows.length-1);
	var eCell = eRow.insertCell();
	eCell.innerHTML = "<span style='display:none'>"+val+"</span><span style='"+style+"'>"+display+"</span>";
	eCell.style.cursor = "default"
	eCell.onclick = this.listClick2
	
	this.row1.childNodes[0].style.background = "";
	this.row1.childNodes[0].style.color = "";
	this.row1.style.display = "none"
	
	this.onEdit()
	}
function remove()
	{
	if(this.row2==null) return;
	
	val2 = this.row2.childNodes[0].childNodes[0].innerText
	display2 = this.row2.childNodes[0].childNodes[1].innerText

	oTable = eval("inpList1"+this.oName);
	for (var i=0;i<oTable.rows.length;i++)
		{
		val = oTable.rows[i].childNodes[0].childNodes[0].innerText
		display = oTable.rows[i].childNodes[0].childNodes[1].innerText
		if(val == val2 && display == display2)
			{
			oTable.rows[i].style.display = "block"
			}			
		}

	oTable = eval("inpList2"+this.oName);
	for (var i=0;i<oTable.rows.length;i++)
		{
		if(oTable.rows[i]==this.row2)idx=i;
		}
	oTable.deleteRow(idx)
	
	this.row2=null
	
	this.onEdit()
	}