var EndNodeColor1="#3f6",EndNodeColor2="#38e100",CurrentNodeColor1="#ffa6a6",CurrentNodeColor2="#ff0000",OrNodeColor="#92cddc",NoStartNodeColor1="#e7e7e7",NoStartNodeColor2="#d7d7d7";
function GetUrlArg(name){var reg = new RegExp("(^|\\?|&)"+ name +"=([^&]*)(\\s|&|$)", "i");       if (reg.test(location.href)) return unescape(RegExp.$2.replace(/\+/g, " ")); return ""; }; 
function s(FieldName,FieldValue){SetFieldValue(FieldName,FieldValue);}
function SetFieldValue(FieldName,FieldValue,separator,WinObj)
{
	if(!WinObj){WinObj=window;}
	if(!separator){separator=",";}
	var FdArray=FieldName.split(",");
	for (f=0;f<FdArray.length;f++)
	{
		FieldName=FdArray[f];
		var obj=GetObj(FieldName,WinObj);
		if(obj)
		{
			   var tagName=GetTagName(FieldName,WinObj);
			   if(tagName=="textarea")
			   {
					obj.value=FieldValue.replaceAll(separator,",");
			   }
			   else if(tagName=="div"||tagName=="span")
			   {
					obj.innerHTML=FieldValue.replaceAll(separator,",");
			   }
			   else if(tagName=="input")
			   {
					 if(obj.length)  //more checkbox radio
					 {
						var tempArray=FieldValue.split(separator);
						for(j=0;j<obj.length;j++)
						{
							if(tempArray.in_array(obj[j].value)){obj[j].checked=true;}else{obj[j].checked=false;}
						}
					 }
					 else // one field
					 {
						if((obj.type.toLowerCase()=="text"||obj.type.toLowerCase()=="password")&&obj.style.display!="none")
						{
							obj.value=FieldValue.replaceAll(separator,",");
						}else if(obj.type.toLowerCase()=="hidden"||obj.style.display=="none")
						{
							obj.value=FieldValue.replaceAll(separator,",");
							try{GetObj("idShow_"+obj.name,WinObj).innerText=obj.value;}catch(e){}
						}
						else if(obj.type.toLowerCase()=="checkbox"||obj.type.toLowerCase()=="radio")
						{
							var tempArray=FieldValue.split(separator);
							if(tempArray.in_array(obj.value)){obj.checked=true;}else{obj.checked=false;}
						}
					 }
			   }else //select or select-multi
			   {
						var Num=obj.length;
						for(N=0;N<Num;N++){obj.remove(0);}
						var tempArray=FieldValue.split(separator)
						for(i=0;i<tempArray.length;i++)
						{
							var oOption=WinObj.document.createElement("OPTION");
							if(tempArray[i].indexOf("|")==-1)
							{
								oOption.text=tempArray[i];
								oOption.value=tempArray[i];
							}else
							{
								var vArray=tempArray[i].split("|");
								oOption.text=vArray[0];
								oOption.value=vArray[1];
							}
							obj.add(oOption);  
						}
			   }
		}
	}
}

function g(FdName,WinObj) //get the field value
{
	var rval="";
	var obj=GetObj(FdName,WinObj);
	if(!obj) return "";
	var TagName=GetTagName(FdName,WinObj);
	if(TagName=="textarea")
	{
		return obj.value;
	}
	else if(TagName=="select" || TagName=="option")
	{
		for(y=0;y<obj.length;y++)
		{
			if(obj.options){if(obj.options[y].selected==true){if(rval==""){rval=obj.options[y].value}else{rval+=","+obj.options[y].value;}}}
		}
		return rval;
	}
	else if(TagName=="div")
	{
		return obj.innerText;
	}else if(TagName=="span")
	{
		return obj.innerText;
	}
	else if(TagName=="input")
	{
		if(obj.type=="text"||obj.type=="hidden"||obj.type=="password"){return obj.value;}
		else{
			if(obj.length)
			{
				for(y=0;y<obj.length;y++)
				{
					if(obj[y].checked==true){if(rval==""){rval=obj[y].value}else{rval+=","+obj[y].value}}
				}
				return rval;
			}else{if(obj.checked==true){return obj.value;}else{return "";}}
		}
	}
}

function OpenUrl(DocURL,lnum,rnum)
{
 var swidth=screen.availWidth;
 var sheight=screen.availHeight;
 if(!lnum) lnum=24;
 if(!rnum) rnum=80;
 var wwidth=swidth-lnum;
 var wheight=sheight-rnum;
 var wleft=(swidth/2-0)-wwidth/2-5;
 var wtop=(sheight/2-0)-wheight/2-25;
 return window.open(DocURL,'','Width='+wwidth+'px,Height='+wheight+'px,Left='+wleft+',Top='+wtop+',location=no,menubar=no,status=yes,resizable=yes,scrollbars=auto,resezie=no');
}
