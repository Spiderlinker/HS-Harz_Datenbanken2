<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<jsp:useBean id="purchaseID" class="de.hsharz.empfehlungssystem.servlet.PurchaseServlet" scope="session" />

<!DOCTYPE html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>Tickets kaufen</title>
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/jsps/css/Purchase.css" title="style1" />
	</head>
	<body>
		<jsp:include page="_header.jsp"></jsp:include>
			
		<form action="/Empfehlungssystem/Purchase" method="POST">
			<div class="container">
				<jsp:getProperty name="purchaseID" property="purchase" />
			</div>
		</form>
	</body>
</html>