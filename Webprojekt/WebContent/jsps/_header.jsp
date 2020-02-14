<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<div class="header-container">
	<form action="Logout" style="display: inline; border: none;">
		<button type="submit" style="width: 20%;">Logout</button>
	</form>
	<span class="header-span">Hallo, <b>${user.firstname}</b></span>
</div>

<div class="sidebar">
	<p>Navigation</p>
	<a href="Empfehlungen">Empfehlungen</a><br>
	<a href="New">Neuerscheinungen</a><br>
	<a href="Bewertungen">Bewertungen</a><br>
	<a href="History">Kaufhistorie</a><br>
	<a href="Statistiken">Statistiken</a><br>
	<a href="Logout">Logout</a>
</div>
