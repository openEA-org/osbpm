<%@ page import="cn.linkey.factory.BeanCtx,java.io.*" %>
<%@page errorPage="error.jsp"%>
<%@ page contentType="text/html; charset=utf-8" isErrorPage="true" %>
<%
	BeanCtx.close();
%>
<title>ERROR！</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
</head>
<body>
Error<br>
<font color=red><hr>
getMessage():<br>
getLocalizedMessage():<br>
PrintStatckTrace():<br>
<br>
</font></body>
</html>