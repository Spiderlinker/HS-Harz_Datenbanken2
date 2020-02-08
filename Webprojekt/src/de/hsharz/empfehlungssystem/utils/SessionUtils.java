package de.hsharz.empfehlungssystem.utils;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.hsharz.empfehlungssystem.Session;
import de.hsharz.empfehlungssystem.beans.User;

public class SessionUtils {

	private static final String LOGIN_PAGE_PATH = "/Login";

	/**
	 * Leitet den User auf die Login-Page weiter, sofern dieser nicht im System
	 * angemeldet ist. Falls der Benutzer bereits angemeldet ist, so wird der User
	 * als Attribut in den gegebenen request geschrieben.
	 * 
	 * @param request  HttpServletRequest des Users
	 * @param response HttpServletResponse für den User
	 * @return ob der Benutzer auf die Login-Page weitergeleitet wurde
	 * @throws IOException Fehler beim Weiterleiten
	 */
	public static boolean redirectToLoginPageIfNotLoggedIn(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		User loggedInUser = Session.getLoggedInUser();

		if (loggedInUser == null) {
			response.sendRedirect(request.getContextPath() + LOGIN_PAGE_PATH);
			return true;
		}

		request.setAttribute("user", loggedInUser);
		return false;
	}

}
