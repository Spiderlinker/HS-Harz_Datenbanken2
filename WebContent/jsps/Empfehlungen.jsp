<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
    
    <jsp:useBean id="torsten" class="de.hsharz.empfehlungssystem.servlet.EmpfehlungenServlet" scope="session"/>
    
<!DOCTYPE html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>Deine Empfehlungen</title>
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/jsps/css/Login.css" title="style1" />
	</head>
	<body>
		<div class="container">
						
			<jsp:getProperty name="torsten" property="empfehlungen" />
			
		</div>
	</body>
</html>