<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<jsp:useBean id="historyID" class="de.hsharz.empfehlungssystem.servlet.HistoryServlet" scope="session" />

<!DOCTYPE html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>Tickets kaufen</title>
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/jsps/css/Recommender.css" title="style1" />
	</head>
	<body>
		<jsp:include page="_header.jsp"></jsp:include>
		
		<div class="sidebar">
			<p>Navigation</p>
			<a href="Empfehlungen">Empfehlungen</a>
			<a href="Bewertungen">Bewertungen</a>
			<a href="History">Kaufhistorie</a>
			<a href="Statistiken">Statistiken</a>
			<a href="Logout">Logout</a>
		</div>
				
		<div class="container">
			<jsp:getProperty name="historyID" property="history" />
		</div>
	</body>
</html>