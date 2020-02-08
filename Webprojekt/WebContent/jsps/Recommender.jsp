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
		
		<div class="container">
			<jsp:getProperty name="empfehlungenID" property="empfehlungen" />
		</div>
	</body>
</html>