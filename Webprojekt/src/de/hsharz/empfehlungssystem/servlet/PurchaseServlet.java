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

import de.hsharz.empfehlungssystem.Session;
import de.hsharz.empfehlungssystem.beans.Event;
import de.hsharz.empfehlungssystem.beans.TicketType;
import de.hsharz.empfehlungssystem.database.DatabaseAdapter;
import de.hsharz.empfehlungssystem.utils.SessionUtils;

@WebServlet(description = "Tickets kaufen", urlPatterns = { "/Purchase" })
public class PurchaseServlet extends HttpServlet {

	/** */
	private static final long serialVersionUID = 1L;

	public String getPurchase() {
		System.out.println("Hole Event für Kauf");

		Event event = Session.get(Session.ATTRIBUTE_EVENT_TO_PURCHASE);

		StringBuilder builder = new StringBuilder();
		if (event == null) {
			return "Fehler beim Holen des Events! Keine Datensätze vorhanden in der Datenbank.";
		}

		builder.append("<b>" + event.getTitle() + "</b>");

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
		builder.append("</b><br> Adresse:" + event.getStreet() //
				+ " " + event.getHouseNumber() //
				+ ", " + event.getZIP()//
				+ " " + event.getCity());
		builder.append("</p>");

		// Beschreibung einfuegen
		builder.append(event.getDescription());
		builder.append("<input type=\"hidden\" name=\"eventID\" value=\"" + event.getId() + "\"/>");

		// Preis einfuegen
		builder.append("<br><p style=\"text-align: center;\">Preis pro Ticket: <b>"
				+ String.format("%.2f", event.getPrice()) + "&euro; </b> </p>");

		// Veranstalter und Dauer des Events einfügen
		builder.append("Veranstalter:" + event.getOrganizer() + " &nbsp Dauer:" + event.getDuration() + " min<hr>");

		builder.append("<table><tr><td>Anzahl der Tickets</td><td>");
		// Ticketanzahl
		builder.append(" <select name=\"amountOfTickets\">");
		for (int i = 1; i <= 5; i++) {
			builder.append("<option value=\"" + i + "\">" + i + "</option>");
		}
		builder.append("</select></td></tr>");
		builder.append("<tr><td>Zahlungsmethode</td><td>");

		// Zahlungsmethode auswählen
		try {
			List<String> paymethods = DatabaseAdapter.getPaymethods();
			builder.append("<select name=\"paymethod\">");
			for (String paymethod : paymethods) {
				builder.append("<option value=\"" + paymethod + "\">" + paymethod + "</option>");
			}
			builder.append("</select></td></tr>");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		builder.append("<tr><td>Rabatt</td><td>");

		try {
			List<TicketType> ticketTypes = DatabaseAdapter.getTicketTypes();
			builder.append("<select name=\"ticketType\">");
			for (TicketType type : ticketTypes) {
				builder.append("<option value=\"" + type.getName() + "\">" + type.toString() + "</option>");
			}
			builder.append("</select></td></tr></table>");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		builder.append("<br>");
		builder.append(
				"Bitte beachten Sie, dass die Auswahl des Rabatts beim Einlass des "
				+ "Events kontrolliert wird und Sie ggf. bei Falschangaben mit Nachzahlungen rechnen müssen.");

		// Kaufen-Button (und Preis) einfuegen
		builder.append("<span style=\"float: right;\">" + "<button type=\"submit\">Kaufen</button></span>");

		return builder.toString();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("doGet Purchase");

		if (SessionUtils.redirectToLoginPageIfNotLoggedIn(request, response)) {
			// Nutzer war nicht eingeloggt und wurde auf die Login-Page weitergeleitet
			return;
		}

		String eventID = request.getParameter("eventID");
		System.out.println("Given EventID: " + eventID);
		if (eventID == null) {
			System.out.println("Keine EventID übergeben!");
			response.sendRedirect(request.getContextPath() + "/Empfehlungen");
			return;
		}

		try {
			Session.store(Session.ATTRIBUTE_EVENT_TO_PURCHASE, DatabaseAdapter.getEvent(eventID));
		} catch (SQLException e) {
			e.printStackTrace();
		}

		RequestDispatcher requestDispatcher = this.getServletContext().getRequestDispatcher("/jsps/Purchase.jsp");
		requestDispatcher.forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("doPost Purchase");

		Integer amountOfTickets = Integer.parseInt(request.getParameter("amountOfTickets"));
		String paymethod = request.getParameter("paymethod");
		String ticketTypeName = request.getParameter("ticketType");
		Event event = Session.remove(Session.ATTRIBUTE_EVENT_TO_PURCHASE);

		boolean success = false;
		String error = "";
		try {
			success = DatabaseAdapter.putPurchase(event, amountOfTickets, paymethod, ticketTypeName,
					Session.get(Session.ATTRIBUTE_LOGGED_IN_USER));
		} catch (SQLException e) {
			e.printStackTrace();
			error = e.getMessage();
		}

		if (success) {
			response.sendRedirect(request.getContextPath() + "/Empfehlungen");
		} else {
			System.out.println("Konnte Purchase nicht einfügen!");
			System.out.println(error);
		}

	}
}
