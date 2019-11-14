package de.hsharz.empfehlungssystem.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(displayName = "Logout", urlPatterns = { "/Logout", "/LogoutServlet" })
public class LogoutServlet extends HttpServlet {

	/** */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Session.removeLoggedInUser();
		response.sendRedirect(request.getContextPath() + "/Login");
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Session.removeLoggedInUser();
		response.sendRedirect(request.getContextPath() + "/Login");
	}

}
