function showsg(idName,DocUNID,UserName)
{
	if(!idName){idName="SendOut";}
	var WebHtmlSign='<div id="tmpElement"><OBJECT name="'+idName+'" classid="clsid:2294689C-9EDF-40BC-86AE-0438112CA439"  codebase="linkey/bpm/word/iWebRevision.cab#version=6,0,0,0" width="100%" height="100%" >';
	WebHtmlSign+='<param name="WebUrl" value="/servlet/iWebServer">    <!-- WebUrl:系统服务器路径，与服务器交互操作，如打开签章信息 -->   ';
	WebHtmlSign+='<param name="EXTPARAM" value="'+folder+'\\linkey_wordtemplate.nsf">    <!-- EXTPARAM:扩展全局变量参数，指定为数据库名称，如果Demo.nsf数据库放在 DB目录下，则为DBDemo.snf -->  ';
	WebHtmlSign+='<param name="RecordID" value="'+DocUNID+'">    <!-- RecordID:本文档记录编号 -->  ';
	WebHtmlSign+='<param name="FieldName" value="'+idName+'">        <!-- FieldName:签章窗体可以根据实际情况再增加，只需要修改控件属性 FieldName 的值就可以 -->   ';
	WebHtmlSign+='<param name="UserName" value="'+UserName+'">  <!-- UserName:签名用户名称 -->  ';
	WebHtmlSign+='<param name="Enabled" value="">  <!-- Enabled:是否允许修改，0:不允许 1:允许  默认值:1  -->  ';
	WebHtmlSign+='<param name="PenColor" value="#000000">   <!-- PenColor:笔的颜色，采用网页色彩值  默认值:#000000  -->  ';
	WebHtmlSign+='<param name="BorderStyle" value="0">    <!-- BorderStyle:边框，0:无边框 1:有边框  默认值:1  -->  ';
	WebHtmlSign+='<param name="EditType" value="0">     <!-- EditType:默认签章类型，0:签名 1:文字  默认值:0  --> ';
	WebHtmlSign+='<param name="ShowPage" value="1">    <!-- ShowPage:设置默认显示页面，0:电子印章,1:手写签名,2:文字批注  默认值:0  --> ';
	WebHtmlSign+='<param name="InputText" value="">    <!-- InputText:设置署名信息，  为空字符串则默认信息[用户名+时间]内容  -->';
	WebHtmlSign+='<param name="PenWidth" value="2">     <!-- PenWidth:笔的宽度，值:1 2 3 4 5   默认值:2  -->   ';
	WebHtmlSign+='<param name="FontSize" value="11">    <!-- FontSize:文字大小，默认值:11 -->   ';
	WebHtmlSign+='<param name="SignatureType" value="0"> ';
	WebHtmlSign+='</OBJECT></div>';
	document.write(WebHtmlSign);
}

//调入签名
function loadsg(idName)
{
  if(!idName){idName="SendOut";}
  document.all(idName).LoadSignature();
}

//保存签章信息
function savesg(idName)
{
  if(!idName){idName="SendOut";}
  if ( document.all(idName).Modify){
    if (! document.all(idName).SaveSignature()){
       return false;
    }
  }
}
