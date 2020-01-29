package de.hsharz.empfehlungssystem.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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

@WebServlet(description = "Deine vorgeschlagenen Events", urlPatterns = { "/Empfehlungen" })
public class RecommenderServlet extends HttpServlet {

	/** */
	private static final long serialVersionUID = 1L;

	public String getEmpfehlungen() {
		System.out.println("Hole Empfehlungen");

		StringBuilder builder = new StringBuilder();
		try {

			List<Event> events = DatabaseAdapter.getAllEvents();

			for (Event event : events) {
				// Titel einfuegen
				builder.append("<form method=\"GET\" action=\"/Empfehlungssystem/Purchase\"><b>" + event.getTitle() + "</b>");

				// Kaufen-Button (und Preis) einfuegen
				builder.append("<span style=\"float: right;\">" + "<button type=\"submit\">Kaufen</button>"
				// Preis einfuegen
						+ "<p style=\"text-align: center;\">" + String.format("%.2f", event.getPrice())
						+ "&euro; </p></span>");

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
				builder.append("<input type=\"hidden\" name=\"eventID\" value=\"" + event.getId() + "\"/>");
				builder.append("</form><br><hr><br>");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return builder.toString();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("doGet Empfehlungen");

		if (SessionUtils.redirectToLoginPageIfNotLoggedIn(request, response)) {
			// Nutzer war nicht eingeloggt und wurde auf die Login-Page weitergeleitet
			return;
		}

		RequestDispatcher requestDispatcher = this.getServletContext().getRequestDispatcher("/jsps/Recommender.jsp");
		requestDispatcher.forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendRedirect(request.getContextPath() + "/Purchase");
	}
}
