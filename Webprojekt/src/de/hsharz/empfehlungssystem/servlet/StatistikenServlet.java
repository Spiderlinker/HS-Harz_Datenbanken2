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
import de.hsharz.empfehlungssystem.database.DatabaseAdapter;

@WebServlet(description = "Deine Statistiken", urlPatterns = { "/Statistiken" })
public class StatistikenServlet extends HttpServlet {

	/** */
	private static final long serialVersionUID = 1L;

	public String getStatistiken() {
		StringBuilder builder = new StringBuilder();

		try {
			List<String> analyticNames = DatabaseAdapter.getAnalyticNames();

			builder.append("<ul>");
			for (String name : analyticNames) {
				// Titel einfuegen
				builder.append("<li><form method=\"POST\" action=\"/Empfehlungssystem/Statistiken\">");

				// Kaufen-Button (und Preis) einfuegen
				builder.append("<button type=\"submit\">" + name + "</button>");

				// Beschreibung einfuegen
				builder.append("<input type=\"hidden\" name=\"analysisName\" value=\"" + name + "\"/>");
				builder.append("</form></li>");
			}
			builder.append("</ul>");

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return builder.toString();
	}

	public String getStatistik() {
		System.out.println("Hole statistik");

		String analysisName = Session.remove(Session.ATTRIBUTE_ANALYSIS_TO_SHOW);
		System.out.println("Is analysis available? " + analysisName);
		if (analysisName == null) {
			return "";
		}

		StringBuilder builder = new StringBuilder();
		try {
			builder.append("<script>");
			builder.append("window.onload = function() {");
			builder.append("var chart = new CanvasJS.Chart(\"chartContainer\", ");
			builder.append(DatabaseAdapter.getAnalysis(analysisName));
			builder.append(");");
			builder.append("chart.render();"); //
			builder.append("}"); //
			builder.append("</script>"); //
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return builder.toString();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("doGet Statistiken");
//		
//		if(SessionUtils.redirectToLoginPageIfNotLoggedIn(request, response)) {
//			// Nutzer war nicht eingeloggt und wurde auf die Login-Page weitergeleitet
//			return;
//		}

		RequestDispatcher requestDispatcher = this.getServletContext().getRequestDispatcher("/jsps/Statistiken.jsp");
		requestDispatcher.forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("doPost Statistiken");

		String analyticName = request.getParameter("analysisName");
		System.out.println("Analytic: " + analyticName);
		Session.store(Session.ATTRIBUTE_ANALYSIS_TO_SHOW, analyticName);

		RequestDispatcher requestDispatcher = this.getServletContext().getRequestDispatcher("/jsps/Statistiken.jsp");
		requestDispatcher.forward(request, response);
	}
}
