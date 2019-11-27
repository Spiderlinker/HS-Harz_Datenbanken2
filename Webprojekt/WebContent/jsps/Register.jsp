<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<jsp:useBean id="country" class="de.hsharz.empfehlungssystem.servlet.RegisterServlet" scope="session" />
    
<!DOCTYPE html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>Login Page</title>
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/jsps/css/Register.css" title="style1" />
	</head>
	<body>
		<form action="/Empfehlungssystem/Register" method="POST">	
			<input type="hidden" name="registerPage" value="1" />
			
			<p>${errorString}<p>
			
			<div class="container">
			
				<label><b>Pers�nliche Daten</b></label>
				<br>
				<select name="gender" style="width:20%;">
					<option selected> M�nnlich </option>
					<option ${user.gender == 'Weiblich' ? 'selected' : ''}> Weiblich </option>
					<option ${user.gender == 'Divers' ? 'selected' : ''}> Divers </option>
				</select>
				<input type="text" placeholder="Vorname" name="firstname" value="${user.firstname}" style="width:39%;" required>
				<input type="text" placeholder="Nachname" name="lastname" value="${user.lastname}" style="width:39%;" required>
				
				<input type="text" placeholder="Anschrift" name="street" value="${user.street}" style="width:74%;" required>
				<input type="text" placeholder="Hausnr." name="houseNumber" value="${user.houseNr}" style="width:25%;" required>
				
				<input type="text" placeholder="PLZ" name="zip" value="${user.zip}" style="width:28%;" required>
				<input type="text" placeholder="Ort" name="city" value="${user.city}" style="width:55%;" required>
				<select name="country" style="width:15%;">
					<jsp:getProperty property="countries" name="country"/>
				</select>
				
				<br>
				<br>
				
				<label for="email"><b>Welche Anmeldedaten m�chtest du verwenden?</b></label> 
				<input type="text" placeholder="Wie lautet deine E-Mail-Adresse" name="email" value="${user.email}" 
					   pattern="[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,10}" class="fullText" required>
					   
				<!-- \s matched Whitespace, \S matched alles was nicht Whitespace ist -->
				<input type="password" placeholder="Welches Passwort m�chtest du w�hlen? (mind. 4 Zeichen)" name="password" value="${user.password}" 
					   pattern="[\s\S]{4,}" class="fullText" required>
				<input type="password" placeholder="Bitte wiederhole dein eingegebenes Passwort" name="passwordRepeat" class="fullText" required>
	
				<br>
				<br>
				
				<label for="birthday"><b>Bitte gib noch dein Geburtsdatum an:</b></label> 
				<input type="date" placeholder="Geburtsdatum" name="birthday" value="${user.birthday}" class="fullText" required>
				
				<br>
				<br>
				<button type="submit">N�chster Schritt</button>
			</div>
	
			<span class="login">Hast du bereits einen Account? <a href="Login">Zur�ck zum Login</a></span>
		</form>
	</body>
</html>