<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<jsp:useBean id="statistikServlet" class="de.hsharz.empfehlungssystem.servlet.StatistikenServlet" scope="session"/>

<html>
<head>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/jsps/css/Statistiken.css" title="style1" />
	<jsp:getProperty property="statistik" name="statistikServlet"/>
</head>
<body>
	
	<jsp:getProperty property="statistiken" name="statistikServlet"/>
	
	<div id="chartContainer" style="height: 700px; width: 100%;"></div>
	<script src="https://canvasjs.com/assets/script/canvasjs.min.js"></script>
</body>
</html>