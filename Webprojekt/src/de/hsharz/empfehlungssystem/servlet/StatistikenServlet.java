package de.hsharz.empfehlungssystem.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.hsharz.empfehlungssystem.beans.User;
import de.hsharz.empfehlungssystem.database.DatabaseAdapter;
import de.hsharz.empfehlungssystem.utils.SessionUtils;

@WebServlet(description = "Deine Statistiken", urlPatterns = { "/Statistiken" })
public class StatistikenServlet extends HttpServlet {

	/** */
	private static final long serialVersionUID = 1L;

	public String getStatistik() {
		System.out.println("Hole statistik");

		try {
			System.out.println("Returning statistics");
			return "<script>" //
					+ "window.onload = function() {" //
					+ "var chart = new CanvasJS.Chart(\"chartContainer\", " //
					+ DatabaseAdapter.getStatistik()
					+ ");" //
					+ "chart.render();" //
					+ "}" //
					+ "</script>";
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "Not found";
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
//		System.out.println("doGet Empfehlungen");
//		
//		if(SessionUtils.redirectToLoginPageIfNotLoggedIn(request, response)) {
//			// Nutzer war nicht eingeloggt und wurde auf die Login-Page weitergeleitet
//			return;
//		}

		RequestDispatcher requestDispatcher = this.getServletContext().getRequestDispatcher("/jsps/Statistiken.jsp");
		requestDispatcher.forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	}
}
