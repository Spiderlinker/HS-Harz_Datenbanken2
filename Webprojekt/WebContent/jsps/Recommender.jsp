<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<jsp:useBean id="purchasesID" class="de.hsharz.empfehlungssystem.servlet.PurchasesServlet" scope="session" />

<!DOCTYPE html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>Deine Käufe</title>
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/jsps/css/Purchases.css" title="style1" />
	</head>
	<body>
	
		<jsp:include page="_header.jsp"></jsp:include>
		
		<form>
			<div class="container">
				<jsp:getProperty name="purchasesID" property="purchases" />
			</div>
		</form>
	</body>
</html>