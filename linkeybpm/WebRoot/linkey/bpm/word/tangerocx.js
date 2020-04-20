//通用的js语句和函数

var TANGER_OCX_bDocOpen = false;
var TANGER_OCX_strOp; //标识当前操作。1:新建；2:打开编辑；3:打开阅读
var TANGER_OCX_attachName; //标识已经存在的在线编辑文档附件的名称
var TANGER_OCX_attachURL; //在线编辑文档附件的URL
var TANGER_OCX_actionURL; //表单提交到的URL
var TANGER_OCX_OBJ; //控件对象
var TANGER_OCX_key=""; //加密签章
var TANGER_OCX_Username="匿名用户";


//此函数在网页装载时被调用。用来获取控件对象并保存到TANGER_OCX_OBJ
//同时，可以设置初始的菜单状况，打开初始文档等等。
function TANGER_OCX_Init(initdocurl)
{
	TANGER_OCX_OBJ = document.all.item("TANGER_OCX");	
	TANGER_OCX_EnableFileNewMenu(false);
	TANGER_OCX_EnableFileCloseMenu(false);
	TANGER_OCX_EnableFileOpenMenu(false);
	TANGER_OCX_EnableFileSaveMenu(false);
	TANGER_OCX_EnableFileSaveAsMenu(false);
	try
	{
		//保存该表单的提交url，将来传递给控件的SaveToURL函数
		TANGER_OCX_actionURL = "rule?wf_num=R_S024_B003"; //document.forms[0].action;
		//获取当前操作代码
		TANGER_OCX_strOp = document.all.item("TANGER_OCX_op").innerHTML;
		//获取已经存在的附件名称	
		TANGER_OCX_attachName = document.all.item("TANGER_OCX_attachName").innerHTML;
		//获取已经存在的附件URL
		TANGER_OCX_attachURL = document.all.item("TANGER_OCX_attachURL").innerHTML;
		try{
		   TANGER_OCX_key = document.all.item("TANGER_OCX_key").innerHTML;
		}catch(err){}finally{};
		switch(TANGER_OCX_strOp)
		{
			
			case "1":
				if(initdocurl!="")
				{
					TANGER_OCX_OBJ.OpenFromURL(initdocurl,false);
				}
				break;
			case "2":
				if(TANGER_OCX_attachURL)
				{
					TANGER_OCX_OBJ.OpenFromURL(TANGER_OCX_attachURL,false);
				}
				else
				{
					if(initdocurl!="")
					TANGER_OCX_OBJ.OpenFromURL(initdocurl,false);
				}
				break;
			case "3":
				if(TANGER_OCX_attachURL)
				{
					TANGER_OCX_OBJ.OpenFromURL(TANGER_OCX_attachURL,true);
				}				
				break;
			default: //去要打开指定的模板文件，此时，TANGER_OCX_strOp指定的是url
				TANGER_OCX_OBJ.OpenFromURL(TANGER_OCX_strOp,false);
				break;
		}		
	}
	catch(err){
		alert("错误：" + err.number + ":" + err.description);
	}
	finally{
	}
}

function ShowTitleBar(bShow)
{
	TANGER_OCX_OBJ.Titlebar = bShow;
}
function ShowMenubar(bShow)
{
	TANGER_OCX_OBJ.Menubar = bShow;
}
function ShowToolMenu(bShow)
{
	TANGER_OCX_OBJ.IsShowToolMenu = bShow;
}
//从本地增加图片到文档指定位置
function AddPictureFromLocal()
{
	if(TANGER_OCX_bDocOpen)
	{	
    TANGER_OCX_OBJ.AddPicFromLocal(
	"", //路径
	true,//是否提示选择文件
	true,//是否浮动图片
	100,//如果是浮动图片，相对于左边的Left 单位磅
	100); //如果是浮动图片，相对于当前段落Top
	};	
}

//从URL增加图片到文档指定位置
function AddPictureFromURL(URL)
{
	if(TANGER_OCX_bDocOpen)
	{
    TANGER_OCX_OBJ.AddPicFromURL(
	URL,//URL 注意；URL必须返回Word支持的图片类型。
	true,//是否浮动图片
	0,//如果是浮动图片，相对于左边的Left 单位磅
	0);//如果是浮动图片，相对于当前段落Top
	};
}

//从本地增加印章文档指定位置
function AddSignFromLocal()
{
//   alert(TANGER_OCX_key);
   if(TANGER_OCX_bDocOpen)
   {
      TANGER_OCX_OBJ.AddSignFromLocal(
	TANGER_OCX_Username,//当前登陆用户
	"",//缺省文件
	true,//提示选择
	0,//left
	0,
	TANGER_OCX_key)  //top
   }
}

//从URL增加印章文档指定位置
function AddSignFromURL(URL)
{
   //alert(TANGER_OCX_key);
   if(TANGER_OCX_bDocOpen)
   {
        TANGER_OCX_OBJ.AddSignFromURL
        (
        	TANGER_OCX_Username,//当前登陆用户
        	URL,//URL
        	0,//left
        	0,
        	TANGER_OCX_key,
        	1,
        	100,
        	0
        )  //top
   }
}

//开始手写签名

function DoHandSign()
{
//	alert(TANGER_OCX_key);
    if(TANGER_OCX_bDocOpen)
    {	
    	TANGER_OCX_OBJ.DoHandSign2
    	(
        	TANGER_OCX_Username,//当前登陆用户 必须
        	TANGER_OCX_key
    	); 
    }  
}

//开始手工绘图，可用于手工批示
function DoHandDraw()
{
	if(TANGER_OCX_bDocOpen)
	{	
	TANGER_OCX_OBJ.DoHandDraw(
	0,//笔型0－实线 0－4 //可选参数
	0x00ff0000,//颜色 0x00RRGGBB//可选参数
	3,//笔宽//可选参数
	200,//left//可选参数
	50);//top//可选参数
	}
}
//检查签名结果
function DoCheckSign()
{
   //alert(TANGER_OCX_key);
   if(TANGER_OCX_bDocOpen)
   {		
	var ret = TANGER_OCX_OBJ.DoCheckSign
	(
	false,/*可选参数 IsSilent 缺省为FAlSE，表示弹出验证对话框,否则，只是返回验证结果到返回值*/
	TANGER_OCX_key
	);//返回值，验证结果字符串
	//alert(ret);
   }	
}
//以下为以前版本的函数和实用函数


//如果原先的表单定义了OnSubmit事件，保存文档时首先会调用原先的事件。
function TANGER_OCX_doFormOnSubmit()
{
	var form = document.forms[0];
  	if (form.onsubmit)
	{
    	var retVal = form.onsubmit();
     	if (typeof retVal == "boolean" && retVal == false)
       	return false;
	}
	return true;
}


//允许或禁止用户从控件拷贝数据
function TANGER_OCX_SetNoCopy(boolvalue)
{
	TANGER_OCX_OBJ.IsNoCopy = boolvalue;
}

//允许或禁止文件－>新建菜单
function TANGER_OCX_EnableFileNewMenu(boolvalue)
{
	TANGER_OCX_OBJ.EnableFileCommand(0) = boolvalue;
}
//允许或禁止文件－>打开菜单
function TANGER_OCX_EnableFileOpenMenu(boolvalue)
{
	TANGER_OCX_OBJ.EnableFileCommand(1) = boolvalue;
}
//允许或禁止文件－>关闭菜单
function TANGER_OCX_EnableFileCloseMenu(boolvalue)
{
	TANGER_OCX_OBJ.EnableFileCommand(2) = boolvalue;
}
//允许或禁止文件－>保存菜单
function TANGER_OCX_EnableFileSaveMenu(boolvalue)
{
	TANGER_OCX_OBJ.EnableFileCommand(3) = boolvalue;
}
//允许或禁止文件－>另存为菜单
function TANGER_OCX_EnableFileSaveAsMenu(boolvalue)
{
	TANGER_OCX_OBJ.EnableFileCommand(4) = boolvalue;
}
//允许或禁止文件－>打印菜单
function TANGER_OCX_EnableFilePrintMenu(boolvalue)
{
	TANGER_OCX_OBJ.EnableFileCommand(5) = boolvalue;
}
//允许或禁止文件－>打印预览菜单
function TANGER_OCX_EnableFilePrintPreviewMenu(boolvalue)
{
	TANGER_OCX_OBJ.EnableFileCommand(6) = boolvalue;
}

//设置用户名
function TANGER_OCX_SetDocUser(cuser)
{
	with(TANGER_OCX_OBJ.ActiveDocument.Application)
	{
		UserName = cuser;
		TANGER_OCX_Username = cuser;
	}	
}

//设置页面布局
function TANGER_OCX_ChgLayout()
{
 	try
	{
		TANGER_OCX_OBJ.showdialog(5); //设置页面布局
	}
	catch(err){
		alert("错误：" + err.number + ":" + err.description);
	}
	finally{
	}
}

//打印文档
function TANGER_OCX_PrintDoc(isBackground)
{
	var oldOption;	
	try
	{
		var objOptions =  TANGER_OCX_OBJ.ActiveDocument.Application.Options;
		oldOption = objOptions.PrintBackground;
		objOptions.PrintBackground = isBackground;
	}
	catch(err){};

	TANGER_OCX_OBJ.printout(true);

	try
	{
		var objOptions =  TANGER_OCX_OBJ.ActiveDocument.Application.Options;
		objOptions.PrintBackground = oldOption;
	}
	catch(err){};	
}

//此函数在文档关闭时被调用。
function TANGER_OCX_OnDocumentClosed()
{
	TANGER_OCX_bDocOpen = false;
}
//此函数用来保存当前文档。主要使用了控件的SaveToURL函数。
//有关此函数的详细用法，请参阅编程手册。
function TANGER_OCX_SaveDoc(fileName)
{
	var retStr=new String;
	var newwin,newdoc;
	if(fileName==""){alert("请指定附件名称!");return;}
	try
	{
	 	if(!TANGER_OCX_doFormOnSubmit())return;
		if(!TANGER_OCX_bDocOpen){alert("没有打开的文档。");return;}

		//在编辑状态下需要删除的附件名称
		var deleteFile = "";
		//设置要保存的附件文件名		
		document.all.item("TANGER_OCX_filename").value = fileName;
		switch(TANGER_OCX_strOp)
		{			
			case "3":
				alert("文档处于阅读状态，您不能保存到服务器。");
				break;	
			case "2": //需要首先删除以前的文档附件
				deleteFile = (TANGER_OCX_attachName=="")?"":"%%Detach="+TANGER_OCX_attachName;
			case "1": 	
				//新建文档
			default:
				retStr = TANGER_OCX_OBJ.SaveToURL(TANGER_OCX_actionURL,
				"file1",//文件上载控件的名称
				deleteFile,
				fileName,
				0 //同时提交forms[0]的信息
				);
				alert("保存成功!");
				break;
		}
	}
	catch(err){
		alert("不能保存到URL：" + err.number + ":" + err.description);
	}
}

function TANGER_OCX_SaveDoc_fd(fileName)
{
	var retStr=new String;
	var newwin,newdoc;
	if(fileName==""){alert("请指定附件名称!");return;}
	var spos=fileName.lastIndexOf(".");
	fileName=fileName.substring(0,spos)+"(副本)"+fileName.substring(spos);
	try
	{
	 	if(!TANGER_OCX_doFormOnSubmit())return;
		if(!TANGER_OCX_bDocOpen){alert("没有打开的文档。");return;}

		//在编辑状态下需要删除的附件名称
		var deleteFile = "";
		//设置要保存的附件文件名		
		document.all.item("TANGER_OCX_filename").value = fileName;
		switch(TANGER_OCX_strOp)
		{			
			case "3":
				alert("文档处于阅读状态，您不能保存到服务器。");
				break;	
			case "2": //需要首先删除以前的文档附件
				deleteFile = (TANGER_OCX_attachName=="")?"":"%%Detach="+TANGER_OCX_attachName;
			case "1": 	
				//新建文档
			default:
				retStr = TANGER_OCX_OBJ.SaveToURL(TANGER_OCX_actionURL,
				"file1",//文件上载控件的名称
				deleteFile,
				fileName,
				0 //同时提交forms[0]的信息
				);
				alert("副本保存成功!");
				break;
		}
	}
	catch(err){
		alert("不能保存到URL：" + err.number + ":" + err.description);
	}
}


//此函数在文档打开时被调用。
function TANGER_OCX_OnDocumentOpened(str, obj)
{
	try
	{
		TANGER_OCX_bDocOpen = true;	
		//设置用户名
		TANGER_OCX_SetDocUser(TANGER_OCX_Username);		
		if(obj)
		{
			switch(TANGER_OCX_strOp)
			{
				case "1":
				case "2":
					//TANGER_OCX_SetReadOnly(false);
					break;
				case "3":
					//TANGER_OCX_SetReadOnly(true);
					break;
				default:
					break;
			}
		}	
	}
	catch(err){
		
	}
	finally{
	}
}