<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>Login Page</title>
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/jsps/css/Register.css" title="style1" />
	</head>
	<body>
		<form action="/Empfehlungssystem/Register" method="POST">	
			<div class="container">
			
				<label><b>Persönliche Daten</b></label>
				<br>
				<select name="gender" style="width:18%;">
					<option selected="selected"> Männlich </option>
					<option> Weiblich </option>
					<option> Divers </option>
				</select>
				<input type="text" placeholder="Vorname" name="firstname" style="width:40%;" required>
				<input type="text" placeholder="Nachname" name="lastname" style="width:40%;" required>
				
				
				<input type="text" placeholder="Anschrift" name="street" style="width:74%;" required>
				<input type="text" placeholder="Hausnr." name="houseNumber" style="width:25%;" required>
				
				<input type="number" placeholder="PLZ" name="zip" style="width:30%;" min="0" max="99999" required>
				<input type="text" placeholder="Ort" name="city" style="width:69%;" required>
				
				<br>
				<br>
				
				<label for="email"><b>Welche Anmeldedaten möchtest du verwenden?</b></label> 
				<input type="text" placeholder="Wie lautet deine E-Mail-Adresse" name="email" class="fullText" required>
				<input type="password" placeholder="Welches Passwort möchtest du wählen?" name="password" class="fullText" required>
				<input type="password" placeholder="Bitte wiederhole dein eingegebenes Passwort" name="passwordRepeat" class="fullText" required>
	
				<br>
				<br>
				
				<label for="birthday"><b>Bitte gib noch dein Geburtsdatum an:</b></label> 
				<input type="date" placeholder="Geburtsdatum" name="birthday" value="01.01.2000" class="fullText" required>
				
				<br>
				<br>
				<button type="button">Nächster Schritt</button>
				<button type="submit">Registrieren</button>
			</div>
	
			<span class="login">Hast du bereits einen Account? <a href="Login">Zurück zum Login</a></span>
		</form>
	</body>
</html>