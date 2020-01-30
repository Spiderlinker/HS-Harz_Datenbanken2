package de.hsharz.empfehlungssystem.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
		System.out.println("Hole Käufe");

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

				// Zeit und Ort einfügen
				builder.append("<p style=\"font-size: 10pt;\">Datum: <b>");
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd. MMMM yyyy");
				LocalDate date = LocalDate.parse(event.getDate().toString());
				builder.append(formatter.format(date));
				// Uhrzeit einfügen
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

			if (purchasedEvents.isEmpty()) {
				builder.append("Keine weiteren Käufe zum Bewerten");
			}

		} catch (SQLException e) {
			e.printStackTrace();
			builder.append("Error: " + e.getMessage());
		}

		return builder.toString();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("doGet Käufe");

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
		System.out.println("doPost Käufe");

		// Alle EventIDs holen und in eine Map speichern für späteren Verarbeitung
		Enumeration<String> parameterNames = request.getParameterNames();
		Map<String, Integer> ratings = new HashMap<>();
		while (parameterNames.hasMoreElements()) {
			// EventID mit Rating in Map speichern
			String eventID = parameterNames.nextElement();
			ratings.put(eventID, Integer.parseInt(request.getParameter(eventID)));
		}

		new Thread(() -> { // Ratings in neuem Thread in Datenbank schreiben
			System.out.println("Trage Bewertungen in Datenbank ein...");
			for (Entry<String, Integer> e : ratings.entrySet()) {

				// Rating nur in die Datenbank schreiben, falls Rating > 0
				if (e.getValue() != null && e.getValue() > 0) {
					System.out.println("Event: " + e.getKey() + " - Rating: " + e.getValue());
					try {
						// Rating mit EventID in Datenbank schreiben
						DatabaseAdapter.insertRating(Session.getLoggedInUser(), e.getKey(), e.getValue());
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}
			}
		}).start();

		response.sendRedirect(request.getContextPath() + "/Bewertungen");
	}
}
