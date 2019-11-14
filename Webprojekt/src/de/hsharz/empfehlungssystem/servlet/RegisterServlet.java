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
		System.out.println("RegisterPage: " + registerPage);
		if ("1".equals(registerPage)) {

			createAndStoreUser(request);
			if (!arePasswordsEqual(request)) {
				request.setAttribute("errorString", "Passwörter stimmen nicht überein");
				RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/jsps/Register.jsp");
				dispatcher.forward(request, response);
				return;
			}

			// Benutzer zur zweiten Seite der Registrierung weiterleiten
			RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/jsps/Register2.jsp");
			dispatcher.forward(request, response);
			return;
		} else if ("2".equals(registerPage)) {
			System.out.println("Registrierung abschließen");
			User user = Session.remove(Session.ATTRIBUTE_REGISTERING_USER);
			System.out.println(user);

		} else {
			System.out.println("Keine Page erkannt");
		}
	}

	private void createAndStoreUser(HttpServletRequest request) {
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
		request.setAttribute("user", user);
		Session.store(Session.ATTRIBUTE_REGISTERING_USER, user);
	}

	private boolean arePasswordsEqual(HttpServletRequest request) {
		String password1 = request.getParameter("password");
		String password2 = request.getParameter("passwordRepeat");

		return password1 != null && password1.equals(password2);
	}

}
