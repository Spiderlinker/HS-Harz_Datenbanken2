<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<jsp:useBean id="ratingID" class="de.hsharz.empfehlungssystem.servlet.RatingServlet" scope="session" />

<!DOCTYPE html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>Deine Käufe</title>
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
		
		<form method="POST" action="/Empfehlungssystem/Bewertungen">
			<div class="container">
				<jsp:getProperty name="ratingID" property="purchases" />
				<span style="float: right;"><button type="submit">Bewertungen abschicken</button></span>
				<br>
			</div>
		</form>
	</body>
</html>