<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>Seite nicht gefunden</title>
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/jsps/css/404.css" title="style1" />
	</head>
	<body>
		<form action="/Empfehlungssystem/Error" method="POST">
			<div class="image">
				<img src="${pageContext.request.contextPath}/jsps/img/404.png" alt="404 not found">
			</div>
	
			<div class="container">
				<p>Diese Seite wurde nicht gefunden</p>
				<button type="submit">Zum Login</button>
			</div>
		</form>
	</body>
</html>