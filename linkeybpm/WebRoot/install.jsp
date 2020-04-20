<%@ page import="cn.linkey.install.*,java.util.*" %>
<%@ page contentType="text/html; charset=utf-8" %>
<%
	String method=request.getMethod();
	String msg="";
	if(method.equals("POST")){
		String dirPath=request.getParameter("dirPath");
		boolean deleteOldData=false;
		if(request.getParameter("DeleteOldData").equals("1")){
			deleteOldData=true;
		}
		msg=InstallDataFromXml.install(dirPath,deleteOldData);
	}
%>

<!DOCTYPE html><html>
<head><title>Linkey SmartApp初始化系统数据</title>
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href="../linkey/bpm/easyui/themes/gray/easyui.css">
    <link rel="stylesheet" type="text/css" href="../linkey/bpm/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="../linkey/bpm/css/app_openform.css">
    <script type="text/javascript" src="../linkey/bpm/easyui/jquery.min.js"></script>
    <script type="text/javascript" src="../linkey/bpm/easyui/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="../linkey/bpm/jscode/sharefunction.js"></script>
    <script type="text/javascript" src="../linkey/bpm/jscode/app_openform.js"></script>
</head>
<body>
<form action="install.jsp" method="post"  >
<font color=red>本数据初始化功能只适合安装和初始化时使用，在正式环境中请删除本jsp页面</font>
<br><br>
<input value="1" name="DeleteOldData" type="checkbox" checked >删除旧的数据 &nbsp;&nbsp;
Xml数据所在目录:<input value="F:/bpmdata/" name="dirPath" id="dirPath"  size="80">
<input type="submit" value="开始初始化数据" >
<br>
<%=msg%>
</form> 
</body>
</html>
