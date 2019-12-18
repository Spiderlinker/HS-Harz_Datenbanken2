<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<jsp:useBean id="statistikServlet" class="de.hsharz.empfehlungssystem.servlet.StatistikenServlet" scope="session"/>

<html>
<head>

<jsp:getProperty property="statistik" name="statistikServlet"/>

</head>
<body>
	<div id="chartContainer" style="height: 300px; width: 100%;"></div>
	<script src="https://canvasjs.com/assets/script/canvasjs.min.js"></script>
</body>
</html>