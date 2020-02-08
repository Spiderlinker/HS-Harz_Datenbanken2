package de.hsharz.empfehlungssystem.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.hsharz.empfehlungssystem.beans.User;
import de.hsharz.empfehlungssystem.database.DatabaseAdapter;

/**
 * Servlet implementation class RegisterServlet
 */
@WebServlet(description = "Register", urlPatterns = { "/Register", "/RegisterPage", "/RegisterServlet", })
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String FIRST_REGISTER_PAGE = "1";
	private static final String SECOND_REGISTER_PAGE = "2";

	private static final String DEFAULT_COUNTRY = "DE";

	private static final String CHECKBOX_WITH_LABEL = "<input type=\"checkbox\" hidden=\"hidden\" id=\"%s\" name=\"%s\" /><label for=\"%s\">%s</label>";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RegisterServlet() {
		super();
	}

	public String getGenres() {

		List<String> genreNames = new ArrayList<>();
		try {
			genreNames = DatabaseAdapter.getAllGenreNames();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		StringBuilder builder = new StringBuilder();
		for (String genre : genreNames) {
			builder.append(String.format(CHECKBOX_WITH_LABEL, genre, genre, genre, genre));
		}
		return builder.toString();
	}

	public String getCountries() {
		String option = "<option %s>%s</option>";
		String selectedCountryCode = DEFAULT_COUNTRY;

		// Falls der Benutzer bei der Registrierung war und noch Eingaben korrigieren
		// muss,
		// So soll der zuvor ausgewählte CountryCode wieder ausgewählt sein.
		User registeringUser = Session.get(Session.ATTRIBUTE_REGISTERING_USER);
		if (registeringUser != null) {
			selectedCountryCode = registeringUser.getCountryShort();
		}

		// CountryCodes aus der Datenbank holen
		List<String> countries = new ArrayList<>();
		try {
			countries = DatabaseAdapter.getCountryCodes();
		} catch (SQLException e) {
			e.printStackTrace();
			return "Fehler: " + e.getMessage();
		}

		// Alle CountryCodes in das <select> einfügen und in die HTML-Seite einspeisen
		StringBuilder countriesBuilder = new StringBuilder();
		for (String country : countries) {
			// select default country
			String isSelected = selectedCountryCode.equals(country) ? "selected" : "";
			countriesBuilder.append(String.format(option, isSelected, country));
		}

		return countriesBuilder.toString();
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
			if (user.getPassword().length() < 4 || !arePasswordsEqual(request)) {
				inputsValid = false;
				errorStrings.append("Die Passwörter stimmen nicht überein<br>");
			}

			if (!isEmailValid(user.getEmail())) {
				inputsValid = false;
				errorStrings.append("Die E-Mail-Adresse entspricht nicht dem erwarteten Format<br>");
			}

			/*
			 * Falls der Nutzer ungültige Angaben gemacht hat, wird dieser nicht auf die 2.
			 * Seite der Registrierung weitergeleitet, sondern wird erneut auf die 1. Seite
			 * geschickt. Alle bisher getätigten Eingaben bleiben erhalten, da der User als
			 * Objekt in den Request gespeichert wurde (unter dem Key 'user')
			 */
			String forwardToUrl = "/jsps/Register2.jsp";
			if (!inputsValid) {
				// Die fehlerhaften Felder sollen auf der Webseite ausgegeben werden
				request.setAttribute("errorString", errorStrings.toString());
				forwardToUrl = "/jsps/Register.jsp";
			}

			System.out.println("Redirect user from Register1 to " + forwardToUrl);

			// Benutzer weiteleiten
			RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher(forwardToUrl);
			dispatcher.forward(request, response);
			return;
		} else if (SECOND_REGISTER_PAGE.equals(registerPage)) {
			System.out.println("Registrierung abschließen");
			User user = Session.remove(Session.ATTRIBUTE_REGISTERING_USER);

			System.out.println(user);
			insertPreferencesIntoUser(user, request.getParameterNames());

			try {
				System.out.println("Registering User to database");
				boolean registerUser = DatabaseAdapter.registerUser(user);
				System.out.println("User registered: " + registerUser);
			} catch (SQLException e) {
				e.printStackTrace();
			}

			response.sendRedirect(request.getContextPath() + "/Empfehlungen");
			return;
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
		user.setZip(request.getParameter("zip"));
		user.setCity(request.getParameter("city"));
		user.setCountryShort(request.getParameter("country"));
		user.setEmail(request.getParameter("email"));
		user.setPassword(request.getParameter("password"));

		LocalDate birthdate = LocalDate.parse(request.getParameter("birthday"));
		String dateAsString = birthdate.getDayOfMonth() + "." + birthdate.getMonthValue() + "." + birthdate.getYear();
		user.setBirthday(dateAsString);
		return user;
	}

	private boolean arePasswordsEqual(HttpServletRequest request) {
		String password1 = request.getParameter("password");
		String password2 = request.getParameter("passwordRepeat");

		return password1 != null && password1.equals(password2);
	}

	private boolean isEmailValid(String email) {
		return Pattern.matches("[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,10}", email);
	}

	private void insertPreferencesIntoUser(User user, Enumeration<String> preferences) {
		while (preferences.hasMoreElements()) {
			String attribute = preferences.nextElement();
			if (!attribute.equals("registerPage")) {
				if (user.getPreference1() == null) {
					user.setPreference1(attribute);
				} else if (user.getPreference2() == null) {
					user.setPreference2(attribute);
				} else if (user.getPreference3() == null) {
					user.setPreference3(attribute);
				} else {
					break;
				}
			}
		}
	}

}
