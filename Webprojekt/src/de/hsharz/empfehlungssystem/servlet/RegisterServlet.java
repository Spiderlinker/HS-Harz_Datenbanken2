package de.hsharz.empfehlungssystem.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.hsharz.empfehlungssystem.beans.User;

/**
 * Servlet implementation class RegisterServlet
 */
@WebServlet(description = "Register", urlPatterns = { "/Register", "/RegisterPage", "/RegisterServlet", })
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String FIRST_REGISTER_PAGE = "1";
	private static final String SECOND_REGISTER_PAGE = "2";

	private static final String CHECKBOX_WITH_LABEL = "<input type=\"checkbox\" hidden=\"hidden\" id=\"%s\" name=\"%s\" /><label for=\"%s\">%s</label>";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RegisterServlet() {
		super();
	}

	public String getGenres() {

		List<String> genreNames = new ArrayList<>();
		genreNames.add("Techno");
		genreNames.add("Metal");
		genreNames.add("Pop");
		genreNames.add("Rock");
		genreNames.add("Schulungen");
//		try {
//			genreNames = DatabaseAdapter.getAllGenreNames();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}

		StringBuilder builder = new StringBuilder();
		for (String genre : genreNames) {
			builder.append(String.format(CHECKBOX_WITH_LABEL, genre, genre, genre, genre));
		}
		return builder.toString();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/jsps/Register.jsp");
		dispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String registerPage = request.getParameter("registerPage");
		System.out.println("User is at Register-Page #" + registerPage);

		// Prüfen welche Register-Page der Nutzer aktuell aufgerufen hat.
		// - Falls der Nutzer sich auf der 1. Seite befand, müssen seine Eingaben
		// geprüft werden und wird dann auf Seite 2 weitergeleitet.
		// - Falls der Nutzer sich auf der 2. Seite befand, muss seine Genre-Auswahl
		// geprüft werden und wird dann in der Datenbank registriert.
		if (FIRST_REGISTER_PAGE.equals(registerPage)) {

			// Daten aus der Webseite auslesen und Nutzer erstellen
			User user = createUser(request);
			Session.store(Session.ATTRIBUTE_REGISTERING_USER, user);
			request.setAttribute("user", user);

			StringBuilder errorStrings = new StringBuilder();
			boolean inputsValid = true;

			/*
			 * Validitätsprüfung der eingegebenen Daten
			 */
			if (!arePasswordsEqual(request)) {
				inputsValid = false;
				errorStrings.append("Die Passwörter stimmen nicht überein<br>");
			}

			if (!isStreetValid(user.getStreet())) {
				inputsValid = false;
				errorStrings.append("Die Straße entspricht nicht dem erwarteten Format<br>");
			}

			if (!isHouseNumberValid(user.getHouseNr())) {
				inputsValid = false;
				errorStrings.append("Die Hausnummer entspricht nicht dem erwarteten Format<br>");
			}

			/*
			 * Falls der Nutzer ungültige Angaben gemacht hat, wird dieser nicht auf die 2.
			 * Seite der Registrierung weitergeleitet, sondern wird erneut auf die 1. Seite
			 * geschickt. Alle bisher getätigten Eingaben bleiben erhalten, da der User als
			 * Objekt in den Request gespeichert wurde (unter dem Key 'user')
			 */
			String forwardToUrl = "/jsp/Register2.jsp";
			if (!inputsValid) {
				// Die fehlerhaften Felder sollen auf der Webseite ausgegeben werden
				request.setAttribute("errorString", errorStrings.toString());
				forwardToUrl = "/jsps/Register.jsp";
			}

			// Benutzer weiteleiten
			RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher(forwardToUrl);
			dispatcher.forward(request, response);
			return;
		} else if (SECOND_REGISTER_PAGE.equals(registerPage)) {
			System.out.println("Registrierung abschließen");
			User user = Session.remove(Session.ATTRIBUTE_REGISTERING_USER);
			System.out.println(user);

		} else {
			System.err.println("Keine Implementierung für dieses Register-Seite.");
		}
	}

	private User createUser(HttpServletRequest request) {
		User user = new User();
		user.setGender(request.getParameter("gender"));
		user.setFirstname(request.getParameter("firstname"));
		user.setLastname(request.getParameter("lastname"));
		user.setStreet(request.getParameter("street"));
		user.setHouseNr(request.getParameter("houseNumber"));
		user.setZip(Integer.parseInt(request.getParameter("zip")));
		user.setCity(request.getParameter("city"));
		user.setEmail(request.getParameter("email"));
		user.setPassword(request.getParameter("password"));
		user.setBirthday(request.getParameter("birthday"));
		return user;
	}

	private boolean arePasswordsEqual(HttpServletRequest request) {
		String password1 = request.getParameter("password");
		String password2 = request.getParameter("passwordRepeat");

		return password1 != null && password1.equals(password2);
	}

	private boolean isStreetValid(String street) {
		return false;
	}

	private boolean isHouseNumberValid(String houseNumber) {
		return false;
	}

	private boolean isZipValid(String zip) {
		return false;
	}

	private boolean isCityValid(String city) {
		return false;
	}

}
