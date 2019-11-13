package de.hsharz.empfehlungssystem.utils;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.hsharz.empfehlungssystem.beans.User;
import de.hsharz.empfehlungssystem.servlet.Session;

public class SessionUtils {

	public static boolean redirectToLoginPageIfNotLoggedIn(HttpServlet servlet, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		User loggedInUser = Session.getLoggedInUser();

		if (loggedInUser == null) {
			response.sendRedirect(request.getContextPath() + "/Login");
			return true;
		}
		request.setAttribute("user", loggedInUser);
		return false;
	}

}
