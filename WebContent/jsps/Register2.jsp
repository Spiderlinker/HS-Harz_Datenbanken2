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
		<div class="container">
			<p class="hello" >Hallo <b>${user.username}</b></p>
		</div>
	
		<form action="/Empfehlungssystem/Register" method="POST">	
			<div class="container">
			
				<h3>Was magst du?</h3>
				<br>
				
				<jsp:getProperty name="genreBean" property="genres" />
				
				<br>
				
				<button type="submit">Auswählen</button>
			</div>
			<input type="text" name="selectGenre" hidden="hidden"/>
		</form>
		<span class="logout"><a href="Logout">Abmelden</a></span>
	</body>
</html>