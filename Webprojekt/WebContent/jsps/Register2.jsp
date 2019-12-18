<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
    <jsp:useBean id="genreBean" class="de.hsharz.empfehlungssystem.servlet.RegisterServlet" scope="session"/>
    
<!DOCTYPE html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>Login Page</title>
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/jsps/css/ChoosePreferences.css" title="style1" />
	</head>
	<body>	
		<form action="/Empfehlungssystem/Register" method="POST">	
			<input type="hidden" name="registerPage" value="2"/>
			<div class="container">
			
				<h3>Was magst du?</h3>
				<p>Bitte wähle bis zu 3 Elemente aus</p>
				<br>
				
				<jsp:getProperty name="genreBean" property="genres" />
				
				<br>
				
				<button type="submit">Registrierung abschließen</button>
			</div>
		</form>
	</body>
</html>