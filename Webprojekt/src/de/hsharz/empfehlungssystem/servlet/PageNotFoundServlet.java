package de.hsharz.empfehlungssystem.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = { "/404", "/Error" })
public class PageNotFoundServlet extends HttpServlet {

	/** */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println(getClass().getSimpleName() + ":GET: Display / Forward to Error-Page");
		this.getServletContext().getRequestDispatcher("/jsps/404.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println(getClass().getSimpleName() + ":POST: Redirecting from Error-Page to Login-Page");
		response.sendRedirect(request.getContextPath() + "/Login");
	}

}
