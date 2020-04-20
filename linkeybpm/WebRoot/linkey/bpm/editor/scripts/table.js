/***********************************************************
	Copyright ?2003, InnovaStudio.com. All rights reserved.
************************************************************/

/***************************************************
	Utils:
	- getNumOfColumns
	- getCurrentRowLayout
	- getAbsoluteCellIndex
	- getNextRowLayout
****************************************************/
function getNumOfColumns(oTable)
	{
	var numOfCols=0
	for (var i=0;i<oTable.rows.length;i++) 	
		{
		var nCount=0
		var oTR_tmp=oTable.rows[i];
		for (var j=0;j<oTR_tmp.childNodes.length;j++) 
			{
			var oTD_tmp=oTR_tmp.childNodes[j];
			nCount+=oTD_tmp.colSpan
			}
		if(nCount>numOfCols)numOfCols=nCount;
		//alert(numOfCols)
		}
	return numOfCols;
	}
	
function getCurrentRowLayout(oTable, oTR)
	{
	var numOfCols=getNumOfColumns(oTable);
	
	var sTmp="["
	for (var i=1;i<=numOfCols;i++)  sTmp+="false,";
	sTmp=sTmp.substr(0,sTmp.length-1)
	sTmp+="]"
	var arrAllCols = eval(sTmp)

	for(var i=0;i<oTR.rowIndex;i++)//tdk termasuk current row
		{
		var oTR_tmp=oTable.rows[i]
		//alert(oTR_tmp.outerHTML)
		var m=0
		for(var j=0;j<oTR_tmp.childNodes.length;j++)
			{
			var oTD_tmp=oTR_tmp.childNodes[j]
			m+=oTD_tmp.colSpan
			if(oTD_tmp.rowSpan>=oTR.rowIndex+1-i)
				{
				for(var k=0;k<oTD_tmp.colSpan;k++)
					{
					arrAllCols[m-1+k]=true;
					}
				}
			}
		}		
	return arrAllCols;
	}
	
function getAbsoluteCellIndex(oTable,oTR,oTD)//base 1
	{
	var arrAllCols=getCurrentRowLayout(oTable,oTR);
	
	var nCount=0;
	var bFinish=false
	for(var i=0;i<oTR.childNodes.length;i++)
		{
		if(bFinish==false)
			{
			nCount+=oTR.childNodes[i].colSpan
			}
		if(oTD==oTR.childNodes[i])bFinish=true;
		}
	nCount=nCount-(oTD.colSpan-1)
	//alert(nCount)
	for(var i=0;i<nCount;i++)
		{
		if(arrAllCols[i]==true)
			{//alert("OK")
			nCount++;
			}
		}
	var nCellIndex = nCount
	return nCellIndex;
	}
	
function getNextRowLayout(oTable,oTR,oTD)
	{
	var nCellIndex = getAbsoluteCellIndex(oTable,oTR,oTD);//base 1
	var numOfCols=getNumOfColumns(oTable);

	var sTmp="["
	for (i=1;i<=numOfCols;i++) sTmp+="false,";
	sTmp=sTmp.substr(0,sTmp.length-1)
	sTmp+="]"
	var arrTmp= eval(sTmp)

	var bFinish=false
	var oTR_tmp=oTR
	for(var k=0;k<oTD.rowSpan;k++) oTR_tmp=oTR_tmp.nextSibling;
	if(!oTR_tmp) return null
	//alert(oTR_tmp.outerHTML)

	//Navigate rows sblm TR3(target/next row) => TR0, TR1, TR2
	for (var i=0;i<oTR_tmp.rowIndex;i++) 
		{
		var oTR_before=oTable.rows[i]
		// alert(oTR_before.outerHTML)

		for (var j=0;j<oTR_before.childNodes.length;j++) 
			{
			var oTD_before=oTR_before.childNodes[j]; //alert(oTD_before.innerHTML)
			// alert(indx +", CONTENT: " + oTD_before.innerHTML)
						
			/*	kalo TR1 cari rowSpan>=3
				kalo TR2 cari rowSpan>=2
				i+x=oTR_tmp.rowIndex krn base0 & next)
				0+x=3 x=3
				1+x=3 x=2	
				alert(oTD_before.rowSpan + " == " + (oTR_tmp.rowIndex+1-i))//+1 spy base1 krn jumlah*/
			if(oTD_before.rowSpan>=oTR_tmp.rowIndex+1-i)
				{
				for(k=0;k<oTD_before.colSpan;k++)
					{
					// alert("OK" + k)
					var indx=getAbsoluteCellIndex(oTable,oTR_before,oTD_before)
					arrTmp[indx-1+k]=true; //indx-1 krn base indx=1
					}
				}
			}					
		}
	return arrTmp;
	}

/***************************************************
	Span Row
****************************************************/
function spanRow()
	{
	if(!dialogArguments.oUtil.obj.checkFocus()){return;}//Focus stuff
	var oEditor=dialogArguments.oUtil.oEditor;
	var oSel=oEditor.document.selection.createRange();
	var sType=oEditor.document.selection.type;

	var oTable = (oSel.parentElement != null ? GetElement(oSel.parentElement(),"TABLE") : GetElement(oSel.item(0),"TABLE"))
	if (oTable == null) return;
	var oTR = (oSel.parentElement != null ? GetElement(oSel.parentElement(),"TR") : GetElement(oSel.item(0),"TR"))
	if (oTR == null) return;
	var oTD = (oSel.parentElement != null ? GetElement(oSel.parentElement(),"TD") : GetElement(oSel.item(0),"TD"))
	if (oTD == null) return;
	
	var numOfCols=getNumOfColumns(oTable);
	
	var nCellIndex = getAbsoluteCellIndex(oTable,oTR,oTD);//base 1
	
	//Next Row
	oTR_tmp=oTR
	for(var k=0;k<oTD.rowSpan;k++) oTR_tmp=oTR_tmp.nextSibling;
	if(!oTR_tmp) return false;
	//alert(oTR_tmp.outerHTML)
	
	var arrTmp = getNextRowLayout(oTable,oTR,oTD)
	//alert(arrTmp)

	//Cek arrTmp, jumlah true
	nCount=0
	for (i=0;i<nCellIndex;i++) if(arrTmp[i]==true)nCount++;
	numOfTrue=nCount
		
	nCount=numOfTrue
	iResult=0
	bFinish=false
	for (i=0;i<oTR_tmp.childNodes.length;i++) 	
		{
		oTD_tmp=oTR_tmp.childNodes[i];
		nCount+=oTD_tmp.colSpan
		if(nCount>=nCellIndex && bFinish==false)
			{
			nCount=nCount-(oTD_tmp.colSpan-1)
			if(nCount==nCellIndex)
				{
				if(oTD_tmp.colSpan==oTD.colSpan)
					{
					nn=oTD_tmp.rowSpan
					//alert(oTD_tmp.innerHTML)
					iResult=i
					bFinish=true
					}
				else return false;
				}
			else return false;
			}
		}
	//alert(iResult)

	nTmp=oTD.rowSpan;
	oTD.rowSpan=oTD.rowSpan+nn;	
	oTR_tmp.deleteCell(iResult);
	return true;
	}	

/***************************************************
	Split Row
****************************************************/
function splitRow()
	{
	if(!dialogArguments.oUtil.obj.checkFocus()){return;}//Focus stuff
	var oEditor=dialogArguments.oUtil.oEditor;
	var oSel=oEditor.document.selection.createRange();
	var sType=oEditor.document.selection.type;

	var oTable = (oSel.parentElement != null ? GetElement(oSel.parentElement(),"TABLE") : GetElement(oSel.item(0),"TABLE"))
	if (oTable == null) return;
	var oTR = (oSel.parentElement != null ? GetElement(oSel.parentElement(),"TR") : GetElement(oSel.item(0),"TR"))
	if (oTR == null) return;
	var oTD = (oSel.parentElement != null ? GetElement(oSel.parentElement(),"TD") : GetElement(oSel.item(0),"TD"))
	if (oTD == null) return;
	
	if(oTD.rowSpan==1) return;//"TD tdk ada rowSpan
	
	var numOfCols=getNumOfColumns(oTable);
	
	var nCellIndex = getAbsoluteCellIndex(oTable,oTR,oTD);//base 1
	
	if(!oTR.nextSibling)return;

	var arrTmp=getCurrentRowLayout(oTable, oTR.nextSibling)
	//alert(arrTmp)
	//Cek arrTmp, jumlah true
	nCount=0
	for (i=0;i<nCellIndex;i++) if(arrTmp[i]==true)nCount++;
	numOfTrue=nCount

	nPoint=nCellIndex-numOfTrue//krn base=1
	//alert("nPoint= "+nPoint)
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	nCount=0
	iResult=0//
	oTR_tmp=oTR.nextSibling;
	bFinish=false
	for (i=0;i<oTR_tmp.childNodes.length;i++) 	
		{
		oTD_tmp=oTR_tmp.childNodes[i];
			
		nCount+=oTD_tmp.colSpan

		//alert(nCount + "  " + oTD_tmp.innerHTML)
		if(nCount>nPoint && bFinish==false)
			{
			//alert("    OK")
			iResult=i
			bFinish=true
			}
		else if(bFinish==false)//kalau nCount belum mencapai >nPoint
			{
			//alert("    ambil index terakhir")
			iResult=i+1;
			}			
		}
	//alert(iResult)
		
	nTmp=oTD.rowSpan;
	oTD.rowSpan=1;	
	
	var oNewCell = oTR_tmp.insertCell(iResult);
	oNewCell.innerHTML = "";
	oNewCell.rowSpan=nTmp-1	
	}

/***************************************************
	Span Column
****************************************************/
function spanCol()
	{
	if(!dialogArguments.oUtil.obj.checkFocus()){return;}//Focus stuff
	var oEditor=dialogArguments.oUtil.oEditor;
	var oSel=oEditor.document.selection.createRange();
	var sType=oEditor.document.selection.type;

	var oTable = (oSel.parentElement != null ? GetElement(oSel.parentElement(),"TABLE") : GetElement(oSel.item(0),"TABLE"))
	if (oTable == null) return;
	var oTR = (oSel.parentElement != null ? GetElement(oSel.parentElement(),"TR") : GetElement(oSel.item(0),"TR"))
	if (oTR == null) return;
	var oTD = (oSel.parentElement != null ? GetElement(oSel.parentElement(),"TD") : GetElement(oSel.item(0),"TD"))
	if (oTD == null) return;
	
	if(oTD.nextSibling)
		{
		if(oTD.rowSpan==oTD.nextSibling.rowSpan)
			{
			oTD.colSpan += oTD.nextSibling.colSpan;
			oTR.deleteCell(oTD.nextSibling.cellIndex);
			}
		}
	}

/***************************************************
	Split Column
****************************************************/
function splitCol()
	{
	if(!dialogArguments.oUtil.obj.checkFocus()){return;}//Focus stuff
	var oEditor=dialogArguments.oUtil.oEditor;
	var oSel=oEditor.document.selection.createRange();
	var sType=oEditor.document.selection.type;

	var oTable = (oSel.parentElement != null ? GetElement(oSel.parentElement(),"TABLE") : GetElement(oSel.item(0),"TABLE"))
	if (oTable == null) return;
	var oTR = (oSel.parentElement != null ? GetElement(oSel.parentElement(),"TR") : GetElement(oSel.item(0),"TR"))
	if (oTR == null) return;
	var oTD = (oSel.parentElement != null ? GetElement(oSel.parentElement(),"TD") : GetElement(oSel.item(0),"TD"))
	if (oTD == null) return;
	
	if(oTD.colSpan==1)return;
	
	//~~~~ OLD ~~~~~~
	//if(!oTD.nextSibling)return;	
	//if(oTD.rowSpan!=oTD.nextSibling.rowSpan)return;
	//~~~~ OLD ~~~~~~

	//~~~~ NEW ~~~~~~
	if(oTD.nextSibling)
		{
		if(oTD.rowSpan!=oTD.nextSibling.rowSpan)return;
		}
	//~~~~ NEW ~~~~~~

	oTD.colSpan--;

	var oNewCell = oTR.insertCell(oTD.cellIndex+1);
	oNewCell.innerHTML = "";
	}

/************************
	SIZE
************************/
function insertRow(what)
	{
	if(what=="Above") 
		rowOperation(false,true,false,false);
	else
		rowOperation(false,false,true,false);
	}
function insertCol(what)
	{
	if(what=="Left") 
		colOperation(false,true,false,false);
	else
		colOperation(false,false,true,false);
	}	
function delRow()
	{
	rowOperation(false,false,false,true)
	}
function delCol()
	{
	colOperation(false,false,false,true)
	}
	

/***************************************************
	col & row Operation
****************************************************/
function colOperation(bGetArrayCells,bDoInsertColumnLeft,bDoInsertColumnRight,bDeleteColumn)
	{
	if(!dialogArguments.oUtil.obj.checkFocus()){return;}//Focus stuff
	var oEditor=dialogArguments.oUtil.oEditor;
	var oSel=oEditor.document.selection.createRange();
	var sType=oEditor.document.selection.type;

	var oTable = (oSel.parentElement != null ? GetElement(oSel.parentElement(),"TABLE") : GetElement(oSel.item(0),"TABLE"))
	if (oTable == null) return;
	var oTR = (oSel.parentElement != null ? GetElement(oSel.parentElement(),"TR") : GetElement(oSel.item(0),"TR"))
	if (oTR == null) return;
	var oTD = (oSel.parentElement != null ? GetElement(oSel.parentElement(),"TD") : GetElement(oSel.item(0),"TD"))
	if (oTD == null) return;
	
		
	var bCannotDelete=false;
		
	sArrCols=""
	var nCellIndex = getAbsoluteCellIndex(oTable,oTR,oTD);//base 1
	nColSpan=oTD.colSpan
		
	for (var i=0;i<oTable.rows.length;i++) 	
		{
		var oTR_tmp = oTable.rows[i];
		arrTmp = getCurrentRowLayout(oTable,oTR_tmp)
		//alert(oTR_tmp.outerHTML)			
		
		if(arrTmp[nCellIndex-1]!=true)//Special case
			{				
			//Cek arrTmp, jumlah true
			nCount=0
			for (j=0;j<nCellIndex;j++) if(arrTmp[j]==true) nCount++;
			numOfTrue=nCount

			nCount=numOfTrue
			bFinish=false
			//alert(numOfTrue + "  " + arrTmp)
			for (k=0;k<oTR_tmp.childNodes.length;k++) 	
				{					
				oTD_tmp=oTR_tmp.childNodes[k];
				nCount+=oTD_tmp.colSpan
				if(nCount>=nCellIndex && bFinish==false)
					{						
					nCount=nCount-(oTD_tmp.colSpan-1)
					//alert(nCellIndex + " OK  :  " + nCount + "   " +oTD_tmp.innerHTML)
					if(nCount==nCellIndex)
						{
						//alert(oTD_tmp.innerHTML)	
												
						//~~~~~~~~~~~~~~~~~~~~~~~~~~
						if(bDoInsertColumnLeft || bDoInsertColumnRight)	
							{
							if(oTD_tmp.colSpan>1) 
								{
								//alert("colspan")	
								oTD_tmp.colSpan=oTD_tmp.colSpan+1;
								}
							else
								{
								if(bDoInsertColumnLeft)
									{
									var oNewCell = oTR_tmp.insertCell(oTD_tmp.cellIndex);
									oNewCell.style.cssText=oTD_tmp.style.cssText;
									oNewCell.innerHTML = "";
											
									oNewCell.rowSpan = oTD_tmp.rowSpan;	
									}
								else
									{
									var oNewCell = oTR_tmp.insertCell(oTD_tmp.cellIndex+1);
									oNewCell.style.cssText=oTD_tmp.style.cssText;
									oNewCell.innerHTML = "";	
											
									oNewCell.rowSpan = oTD_tmp.rowSpan;	
									}
								}
							}
						//~~~~~~~~~~~~~~~~~~~~~~~~~~							
						
						if(oTD_tmp.colSpan==nColSpan)
							{
							sArrCols += "[" + i + "," + oTD_tmp.cellIndex + "]," 
							}
						else
							{
							sArrCols += "[" + i + "," + oTD_tmp.cellIndex + "]," 
							
							if(bDeleteColumn)
								{
								alert(getTxt("Cannot delete column."));
								return;
								}						
							
							}
						}
					else
						{
						//alert(oTD_tmp.innerHTML)
						
						//~~~~~~~~~~~~~~~~~~~~~~~~~~
						if(bDoInsertColumnLeft || bDoInsertColumnRight)	
							{
							oTD_tmp.colSpan=oTD_tmp.colSpan+1;
							}
						//~~~~~~~~~~~~~~~~~~~~~~~~~~

						if(bDeleteColumn)
							{
							alert(getTxt("Cannot delete column."));
							return;
							}
						}
					bFinish=true
					}					
				}
			}			
		}
	//alert(sArrCols.substring(0,sArrCols.length-1))
		
	var arrCols = eval("["+sArrCols.substring(0,sArrCols.length-1)+"]");
	
	if(bGetArrayCells)
		{
		return arrCols;
		}
	
	if(bDeleteColumn)
		{
		for (var i=0;i<arrCols.length;i++) 	
			{
			var rowIndex=arrCols[i][0];
			var colIndex=arrCols[i][1];
			oTable.rows[rowIndex].deleteCell(colIndex);
			}
		}				
	}

function rowOperation(bGetArrayCells,bDoInsertRowAbove,bDoInsertRowBelow,bDeleteRow)
	{
	if(!dialogArguments.oUtil.obj.checkFocus()){return;}//Focus stuff
	var oEditor=dialogArguments.oUtil.oEditor;
	var oSel=oEditor.document.selection.createRange();
	var sType=oEditor.document.selection.type;

	var oTable = (oSel.parentElement != null ? GetElement(oSel.parentElement(),"TABLE") : GetElement(oSel.item(0),"TABLE"))
	if (oTable == null) return;
	var oTR = (oSel.parentElement != null ? GetElement(oSel.parentElement(),"TR") : GetElement(oSel.item(0),"TR"))
	if (oTR == null) return;
	var oTD = (oSel.parentElement != null ? GetElement(oSel.parentElement(),"TD") : GetElement(oSel.item(0),"TD"))
	if (oTD == null) return;
	
	if(bGetArrayCells)
		{
		var sTmp="[";
		for(var i=0;i<oTR.childNodes.length;i++)
			{
			sTmp += "[" + oTR.rowIndex + "," + oTR.childNodes[i].cellIndex + "],"
			}
		sTmp=sTmp.substr(0,sTmp.length-1) + "]"
		return eval(sTmp);	
		}
	
	
	var bCannotDelete=false;

	var numOfCols=getNumOfColumns(oTable);
	var sTmp="["
	for (var i=1;i<=numOfCols;i++)  sTmp+="[null,false],";
	sTmp=sTmp.substr(0,sTmp.length-1)
	sTmp+="]"
	var arrAllCols = eval(sTmp)

	//Cari yg rowSpannya mengenai current row
	for(var i=0;i<oTR.rowIndex;i++)//tdk termasuk current row
		{
		var oTR_tmp=oTable.rows[i]
		//alert(oTR_tmp.outerHTML)
		var m=0
		for(var j=0;j<oTR_tmp.childNodes.length;j++)
			{
			var oTD_tmp=oTR_tmp.childNodes[j]
			m+=oTD_tmp.colSpan
			if(oTD_tmp.rowSpan>=oTR.rowIndex+1-i)
				{
				for(var k=0;k<oTD_tmp.colSpan;k++)
					{
					//arrAllCols[m-1+k]=true;
					arrAllCols[m-1+k][0]=oTD_tmp;
					arrAllCols[m-1+k][1]=true;
					
					if(bDeleteRow)
						{
						alert(getTxt("Cannot delete row."));
						return;
						}				
					}
				}
			}
		}
		
	//Navigate current row
	nCount=0;
	for(var i=0;i<oTR.childNodes.length;i++)
		{
		var oTD_tmp=oTR.childNodes[i];
		
		while(arrAllCols[nCount][0]!=null)	nCount++
		
		if(nCount<=numOfCols)
			{
			
			//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			if(bDoInsertRowAbove)
				{
				for(var j=0;j<oTD_tmp.colSpan;j++)
					{
					arrAllCols[nCount+j][0]=oTD_tmp;
					arrAllCols[nCount+j][1]=false;				
					}
				nCount=nCount+j;	
				}
			if(bDoInsertRowBelow)
				{
				if(oTD_tmp.rowSpan>1)
					{
					for(var j=0;j<oTD_tmp.colSpan;j++)
						{
						arrAllCols[nCount+j][0]=oTD_tmp;
						arrAllCols[nCount+j][1]=true;				
						}				
					}
				else
					{
					for(var j=0;j<oTD_tmp.colSpan;j++)
						{
						arrAllCols[nCount+j][0]=oTD_tmp;
						arrAllCols[nCount+j][1]=false;				
						}				
					}			
				nCount=nCount+j;
				}
			//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			}
		}
		
		
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	if(bDoInsertRowAbove)
		{
		var oNewRow = oTable.insertRow(oTR.rowIndex);
		}
	if(bDoInsertRowBelow)
		{
		var oNewRow = oTable.insertRow(oTR.rowIndex+1);
		}
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~		
	//alert(arrAllCols)
	//Sampai di sini dapat arrAllCols, tapi blm ada pembedaan colSpan

	
	var oTD_Prev=null;
	var x=0;
	for(var i=0;i<arrAllCols.length;i++)
		{
		var oTD_tmp=arrAllCols[i][0];
		
		if(oTD_tmp!=oTD_Prev)//ada pembedaan colSpan
			{			
			//alert(oTD_tmp.innerHTML)	
			//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			if(bDoInsertRowAbove || bDoInsertRowBelow)
				{
				if(arrAllCols[i][1]==true)
					{
					//alert("span")
					oTD_tmp.rowSpan=oTD_tmp.rowSpan+1
					}
				else
					{
					oNewTD = oNewRow.insertCell();
					oNewTD.style.cssText=oTD_tmp.style.cssText;
					oNewTD.innerHTML = "";
					//if(x==0){oNewTD.className="input_data_table_td";x=1;}else{oNewTD.className="bg_white";x=0;}
					oNewTD.colSpan=oTD_tmp.colSpan;
					}
				}
			//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			}
		oTD_Prev=oTD_tmp;
		}
	

	if(bDeleteRow)
		{
		oTable.deleteRow(oTR.rowIndex);
		}
	}