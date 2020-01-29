package de.hsharz.empfehlungssystem.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.hsharz.empfehlungssystem.beans.Event;
import de.hsharz.empfehlungssystem.database.DatabaseAdapter;
import de.hsharz.empfehlungssystem.utils.SessionUtils;

@WebServlet(description = "Deine gekauften Events", urlPatterns = { "/Bewertungen" })
public class RatingServlet extends HttpServlet {

	/** */
	private static final long serialVersionUID = 1L;

	public String getPurchases() {
		System.out.println("Hole K�ufe");

		StringBuilder builder = new StringBuilder();
		try {

			List<Event> purchasedEvents = DatabaseAdapter.getPurchasesOfUser(Session.getLoggedInUser());

			for (Event event : purchasedEvents) {
				// Titel einfuegen
				builder.append("<b>" + event.getTitle() + "</b>");

				// Kaufen-Button (und Preis) einfuegen
				builder.append("<span style=\"float: right;\">");
				builder.append(" <select name=\"" + event.getId() + "\">");
				for (int i = 0; i <= 5; i++) {
					builder.append("<option value=\"" + i + "\">" + i + "</option>");
				}
				builder.append("</select></span>");

				// Zeit und Ort einf�gen
				builder.append("<p style=\"font-size: 10pt;\">Datum: <b>");
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd. MMMM yyyy");
				LocalDate date = LocalDate.parse(event.getDate().toString());
				builder.append(formatter.format(date));
				// Uhrzeit einf�gen
				builder.append("</b> &nbsp; Uhrzeit: <b>");
				DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
				LocalTime time = LocalTime.parse(event.getTime().toString());
				builder.append(timeFormatter.format(time));
				// Ort einfuegen
				builder.append("</b> &nbsp; Ort: <b>" + event.getCity());
				builder.append("</b></p>");

				// Beschreibung einfuegen
				builder.append(event.getDescription());
				builder.append("<br><hr><br>");

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return builder.toString();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("doGet K�ufe");

		if (SessionUtils.redirectToLoginPageIfNotLoggedIn(request, response)) {
			// Nutzer war nicht eingeloggt und wurde auf die Login-Page weitergeleitet
			return;
		}

		RequestDispatcher requestDispatcher = this.getServletContext().getRequestDispatcher("/jsps/Rating.jsp");
		requestDispatcher.forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("doPost K�ufe");

		// Alle EventIDs holen
		Enumeration<String> parameterNames = request.getParameterNames();
		new Thread(() -> { // Ratings in neuem Thread in Datenbank schreiben
			System.out.println("Trage Bewertungen in Datenbank ein...");
			while (parameterNames.hasMoreElements()) {
				// EventID mit Rating holen
				String eventID = parameterNames.nextElement();
				Integer rating = Integer.parseInt(request.getParameter(eventID));

				// Rating nur in die Datenbank schreiben, falls Rating > 0
				if (rating != null && rating > 0) {
					System.out.println("Event: " + eventID + " - Rating: " + rating);
					try {
						// Rating mit EventID in Datenbank schreiben
						DatabaseAdapter.insertRating(Session.getLoggedInUser(), eventID, rating);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();

		response.sendRedirect(request.getContextPath() + "/Bewertungen");
	}
}
