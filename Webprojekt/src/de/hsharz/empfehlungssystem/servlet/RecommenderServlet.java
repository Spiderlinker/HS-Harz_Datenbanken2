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

@WebServlet(description = "Deine vorgeschlagenen Events", urlPatterns = { "/Empfehlungen" })
public class RecommenderServlet extends HttpServlet {

	/** */
	private static final long serialVersionUID = 1L;

	private static final int MIN_AMOUNT_RECOMMENDATION = 5;
	private static final double MIN_SIMILARITY = 0.2;
	private static final int MIN_RATING = 4;

	public String getEmpfehlungen() {
		System.out.println("Hole Empfehlungen");

		String eventsOfTitleToShow = Session.remove(Session.ATTRIBUTE_SHOW_EVENTS_OF_TITLE);
		System.out.println("Should show a specific event?: " + eventsOfTitleToShow);
		if (eventsOfTitleToShow == null) {
			return showAllEvents();
		} else {
			return showEventsOfTitle(eventsOfTitleToShow);
		}
	}

	private String showAllEvents() {
		int userID = Session.getLoggedInUser().getId();

		List<Event> eventsToRecommend = new ArrayList<>();
		addEventsViaCollaborativeFiltering(eventsToRecommend, userID);

		if (eventsToRecommend.size() < MIN_AMOUNT_RECOMMENDATION) {
			addEventsViaKnowledgeBasedAndDemographicFiltering(eventsToRecommend, userID);
		}

		if (eventsToRecommend.size() < MIN_AMOUNT_RECOMMENDATION) {
			addAllEvents(eventsToRecommend);
		}

		eventsToRecommend.sort((e1, e2) -> e1.getGenre().compareTo(e2.getGenre()));
		return createWebsiteFromEvents(eventsToRecommend);
	}

	private void addEventsViaCollaborativeFiltering(List<Event> events, int userID) {
		Set<Event> unsortedEvents = new HashSet<>();
		try {
			List<Integer> aehnlicheUser = DatabaseAdapter.getAehnlicheUser(userID, MIN_SIMILARITY);
			int index = 0;
			System.out.println("Aehnliche User: " + aehnlicheUser);
			while (aehnlicheUser.size() < MIN_AMOUNT_RECOMMENDATION && index < aehnlicheUser.size()) {
				System.out.println("Suche erneut nach aehnlichen Usern...");
				List<Integer> tempAehnliche = DatabaseAdapter.getAehnlicheUser(aehnlicheUser.get(index),
						MIN_SIMILARITY);
				tempAehnliche.removeAll(aehnlicheUser);
				if (tempAehnliche.isEmpty()) {
					System.out.println("Keine weiteren ähnlichen User gefunden");
					break;
				}
				aehnlicheUser.addAll(tempAehnliche);
				System.out.println("Aehnliche User: " + aehnlicheUser);
			}

			aehnlicheUser.remove(Integer.valueOf(userID));
			System.out.println("Final: Aehnliche User: " + aehnlicheUser);

			if(aehnlicheUser.isEmpty()) {
				System.err.println("User is grey sheep: " + userID);
				return;
			}
			
			String userList = "";
			System.out.print("Erstelle Userlist: ");
			userList = aehnlicheUser.stream().map(String::valueOf).collect(Collectors.joining(", "));
			System.out.println(userList);

			unsortedEvents
					.addAll(DatabaseAdapter.getRecommendationCollaborativeFiltering(userID, userList, MIN_RATING));
			System.out.println(unsortedEvents.size() + " Events to recommend: " + unsortedEvents);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		events.addAll(unsortedEvents);

	}

	private void addEventsViaKnowledgeBasedAndDemographicFiltering(List<Event> events, int userID) {
		try {
			events.addAll(DatabaseAdapter.getRecommendationFilterStrong(userID, MIN_RATING));
			System.out.println("Got " + events.size() + " Events from Strong-Filter");

			if (events.size() < MIN_AMOUNT_RECOMMENDATION) {
				System.out.println("Fetching Events from Medium-Filter...");
				events.addAll(DatabaseAdapter.getRecommendationFilterMedium(userID, MIN_RATING));
				System.out.println("Got " + events.size() + " Events from Medium-Filter");

			}

			if (events.size() < MIN_AMOUNT_RECOMMENDATION) {
				System.out.println("Fetching Events from Light-Filter...");
				events.addAll(DatabaseAdapter.getRecommendationFilterLight(userID, MIN_RATING));
				System.out.println("Got " + events.size() + " Events from Light-Filters");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private void addAllEvents(List<Event> events) {
		try {
			events.addAll(DatabaseAdapter.getAllEventTitleGrouped());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private String createWebsiteFromEvents(List<Event> events) {
		StringBuilder builder = new StringBuilder();
		String currentGenre = "";

		for (Event event : events) {

			// Genre-Trennung
			if (!event.getGenre().equals(currentGenre)) {
				currentGenre = event.getGenre();
				builder.append("<h2>" + event.getGenre() + "</h2>");
				builder.append("<hr><br>");
			}

			// Titel einfuegen
			builder.append(
					"<form method=\"POST\" action=\"/Empfehlungssystem/Empfehlungen\"><b>" + event.getTitle() + "</b>");

			// Kaufen-Button einfuegen
			builder.append("<br><span style=\"float: right;\">" + "<button type=\"submit\">Auswählen</button></span>");

			// Beschreibung einfuegen
			builder.append("Weitere Termine für diese Veranstaltung finden Sie nach der Auswahl");
			builder.append("<input type=\"hidden\" name=\"eventTitle\" value=\"" + event.getTitle() + "\"/>");
			builder.append("</form><br><hr><br>");
		}
		return builder.toString();
	}

	private String showEventsOfTitle(String eventTitle) {

		StringBuilder builder = new StringBuilder();
		try {

			List<Event> events = DatabaseAdapter.getAllEventsOf(eventTitle);

			for (Event event : events) {
				// Titel einfuegen
				builder.append(
						"<form method=\"GET\" action=\"/Empfehlungssystem/Purchase\"><b>" + event.getTitle() + "</b>");

				// Kaufen-Button (und Preis) einfuegen
				builder.append("<span style=\"float: right;\">" + "<button type=\"submit\">Kaufen</button>"
				// Preis einfuegen
						+ "<p style=\"text-align: center;\">" + String.format("%.2f", event.getPrice())
						+ "&euro; </p></span>");

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
				builder.append("<input type=\"hidden\" name=\"eventID\" value=\"" + event.getId() + "\"/>");
				builder.append("</form><br><hr><br>");
			}
			builder.append("<a href=\"Empfehlungen\">Zurück zu meinen Empfehlungen</a>");
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("doPost Recommender " + request.getParameter("eventTitle"));
		Session.store(Session.ATTRIBUTE_SHOW_EVENTS_OF_TITLE, request.getParameter("eventTitle"));
		response.sendRedirect(request.getContextPath() + "/Empfehlungen");
	}
}
