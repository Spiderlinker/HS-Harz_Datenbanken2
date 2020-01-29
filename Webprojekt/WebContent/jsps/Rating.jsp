<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<jsp:useBean id="ratingID" class="de.hsharz.empfehlungssystem.servlet.RatingServlet" scope="session" />

<!DOCTYPE html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>Deine K�ufe</title>
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/jsps/css/Recommender.css" title="style1" />
	</head>
	<body>
	
		<jsp:include page="_header.jsp"></jsp:include>
		
		<form method="POST" action="/Empfehlungssystem/Bewertungen">
			<div class="container">
				<jsp:getProperty name="ratingID" property="purchases" />
			</div>
			<span style="float: right;"><button type="submit">Bewertungen abschicken</button></span>
			<br>
		</form>
	</body>
</html>