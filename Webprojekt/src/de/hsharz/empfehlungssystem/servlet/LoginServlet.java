package de.hsharz.empfehlungssystem.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.hsharz.empfehlungssystem.Session;
import de.hsharz.empfehlungssystem.beans.User;
import de.hsharz.empfehlungssystem.database.DatabaseAdapter;
import de.hsharz.empfehlungssystem.utils.StringUtils;

@WebServlet(description = "Login", urlPatterns = { "/Login", "/LoginPage" })
public class LoginServlet extends HttpServlet {

	/** */
	private static final long serialVersionUID = 1L;

	public LoginServlet() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("Check if user already logged in...");

		User loggedInUser = Session.getLoggedInUser();
		System.out.println("User: " + loggedInUser);
		// Nutzer auf die Login.jsp weiterleiten, falls dieser nicht eingeloggt ist (==
		// null)
		if (loggedInUser == null) {
			// Keine GET-Operationen ausführen, nur über POST anmelden
			RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/jsps/Login.jsp");
			dispatcher.forward(request, response);
			return;
		}

		// Benutzer ist eingeloggt. Weiterleiten auf Empfehlungen
		request.setAttribute("user", loggedInUser);
		response.sendRedirect(request.getContextPath() + "/Empfehlungen");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("Processing user...");

		String username = request.getParameter("username");
		String password = request.getParameter("password");

		User user = null;
		String errorString = "";
		boolean hasError = false;

		if (StringUtils.isNullOrEmpty(username) || StringUtils.isNullOrEmpty(password)) {
			errorString = "Username and password required!";
		} else {
			try {
				user = DatabaseAdapter.getUser(username, password);

				if (user == null) {
					hasError = true;
					errorString = "Invalid username or password!";
				}
			} catch (SQLException e) {
				e.printStackTrace();
				hasError = true;
				errorString = e.getMessage();
			}

		}

		// Traten Fehler beim Einloggen auf?
		if (hasError) {
			System.out.println("Has Error...");
			user = new User();
			user.setUsername(username);

			request.setAttribute("errorString", errorString);
			request.setAttribute("user", user);

			// Benutzer mit Fehlermeldung erneut die Login-Seite darstellen mit
			// Fehlermeldung
			RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/jsps/Login.jsp");
			dispatcher.forward(request, response);
		} else {
			// User in Session abspeichern und zu Empfehlungen weiterleiten
			Session.storeLoggedInUser(user);
			response.sendRedirect(request.getContextPath() + "/Empfehlungen");
		}

	}

}
