package de.hsharz.empfehlungssystem.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.hsharz.empfehlungssystem.database.DatabaseAdapter;

/**
 * Servlet implementation class RegisterServlet
 */
@WebServlet(description = "Register", urlPatterns = { "/Register", "/RegisterPage", "/RegisterServlet", "/Genre" })
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
    

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/jsps/Register.jsp");
		dispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.getParameter("");
		
	}

}
