<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>Login Page</title>
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/jsps/css/Login.css" title="style1" />
	</head>
	<body>
		<form action="/Empfehlungssystem/Login" method="POST">
			<div class="image">
				<img src="${pageContext.request.contextPath}/jsps/img/events.jpg" alt="Events">
			</div>
	
			<p>${errorString}<p>
	
			<div class="container">
				<label for="username"><b>Benutzername:</b></label> 
				<input type="text" value="${user.username}" placeholder="Wie lautet dein Benutzername?" name="username" required>
				
				<label for="password"><b>Passwort:</b></label> 
				<input type="password" placeholder="Und das dazugehörige Passwort?" name="password" required>
	
				<button type="submit">Login</button>
			</div>
	
			<span class="register">Noch keinen Account? <a href="Register">Jetzt Registrieren</a></span>
		</form>
	</body>
</html>