package de.hsharz.empfehlungssystem.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.hsharz.empfehlungssystem.Session;
import de.hsharz.empfehlungssystem.beans.Event;
import de.hsharz.empfehlungssystem.database.DatabaseAdapter;
import de.hsharz.empfehlungssystem.utils.SessionUtils;

@WebServlet(description = "Neuerscheinungen", urlPatterns = { "/New" })
public class NewReleasesServlet extends HttpServlet {

	/** */
	private static final long serialVersionUID = 1L;

	public String getNewReleases() {
		System.out.println("Hole Neuerscheinungen");

		StringBuilder builder = new StringBuilder();
		String currentGenre = "";

		try {
			List<Event> events = DatabaseAdapter.getNewEvents();

			for (Event event : events) {

				// Genre-Trennung
				if (!event.getGenre().equals(currentGenre)) {
					currentGenre = event.getGenre();
					builder.append("<h2>" + event.getGenre() + "</h2>");
					builder.append("<hr><br>");
				}

				// Titel einfuegen
				builder.append("<form method=\"POST\" action=\"/Empfehlungssystem/Empfehlungen\"><b>" + event.getTitle()
						+ "</b>");

				// Kaufen-Button einfuegen
				builder.append(
						"<br><span style=\"float: right;\">" + "<button type=\"submit\">Auswählen</button></span>");

				// Beschreibung einfuegen
				builder.append("Weitere Termine für diese Veranstaltung finden Sie nach der Auswahl");
				builder.append("<input type=\"hidden\" name=\"eventTitle\" value=\"" + event.getTitle() + "\"/>");
				builder.append("</form><br><hr><br>");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return builder.toString();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("doGet NewReleases");

		if (SessionUtils.redirectToLoginPageIfNotLoggedIn(request, response)) {
			// Nutzer war nicht eingeloggt und wurde auf die Login-Page weitergeleitet
			return;
		}

		RequestDispatcher requestDispatcher = this.getServletContext().getRequestDispatcher("/jsps/NewReleases.jsp");
		requestDispatcher.forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("doPost NewReleases to Recommender " + request.getParameter("eventTitle"));
		Session.store(Session.ATTRIBUTE_SHOW_EVENTS_OF_TITLE, request.getParameter("eventTitle"));
		response.sendRedirect(request.getContextPath() + "/Empfehlungen");
	}
}
