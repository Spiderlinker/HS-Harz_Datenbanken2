<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<jsp:useBean id="empfehlungenID" class="de.hsharz.empfehlungssystem.servlet.RecommenderServlet" scope="session" />

<!DOCTYPE html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>Deine Empfehlungen</title>
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/jsps/css/Recommender.css" title="style1" />
	</head>
	<body>
	
		<jsp:include page="_header.jsp"></jsp:include>
		
		<div class="sidebar">
			<p>Navigation</p>
			<a href="/Empfehlungssystem/Empfehlungen">Empfehlungen</a>
			<a href="/Empfehlungssystem/Bewertungen">Bewertungen</a>
			<a href="/Empfehlungssystem/Statistiken">Statistiken</a>
			<a href="/Empfehlungssystem/Logout">Logout</a>
		</div>
		
		<div class="container">
			<jsp:getProperty name="empfehlungenID" property="empfehlungen" />
		</div>
	</body>
</html>