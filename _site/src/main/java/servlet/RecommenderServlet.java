/**
 * This is the servlet used to respond to the requests from the clients.
 */
package com.madhuri.bibliopal.servlet;

import java.io.*;
import java.util.ArrayList;
import javax.servlet.*;
import javax.servlet.http.*;

import com.madhuri.bibliopal.recommender.*;

public class RecommenderServlet extends HttpServlet {
	private static final long serialVersionUID = 2314132412481L;

	// a recommender will be running in the back end
	private static GenericRecommenderMaster _recommender = null;

	// constructor
	public RecommenderServlet() {
		super();
	}

	// method to respond GET requests
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter writer = response.getWriter();

		// if Build Model button is pressed
		if (request.getParameter("run") != null) {

			// the model has not been built yet
			if (_recommender == null) {
				// initialize a new recommender, thread-safe
				synchronized (this) {
					_recommender = new GenericRecommenderMaster("data/");
				}
				// prompt
				writer.println("<p class=\"lead\">Finished Preprocessing!</p>");
			} else {
				writer.println("<p class=\"lead\">Model Already Generated!</p>");
			}

			// if the Recommend button is pressed
		} else if (request.getParameter("userID") != null) {

			// if the model has been generated
			if (_recommender != null) {
				// obtain userID from the request, regardless of proceeding
				// and succeeding spaces.
				String userID = request.getParameter("userID").trim();

				// get recommender results
				ArrayList<ArrayList<String>> res = _recommender.recommend(userID);

				// if user ID does not exist, write information to the webpage
				if (res == null) {
					writer.println("<p class=\"lead\">Invalid user ID!</p>");
				} else if (res.size() == 0) {
					writer.println("<p class=\"lead\">User does not exist!</p>");
				} else {
					// read different fields from the recommendation results
					ArrayList<String> history = res.get(0);
					ArrayList<String> historyRatings = res.get(1);
					ArrayList<String> recommendations = res.get(2);
					ArrayList<String> recommendationRatings = res.get(3);

					// the first table contains the book recommendation to the user
					writer.println("<table class=\"table table-bordered table-striped\" style=\"width:600px; table-layout:fixed;\">");
					writer.println("<thead><tr><th style=\"width:70%\">Recommendation</th><th style=\"width:15%;\">Rating</th></tr></thead><tbody>");

					// if the recommender is not able to generate results
					if (recommendations.size() == 0) {
						writer.println("<tr><td>(Need more data for prediction)</td><td>N/A</td><td style=\"overflow:hidden;\">N/A</td></tr>");
					} else {

						// write each row of the table, which consists of the recommendation

						int len = recommendations.size();
						for (int i = 0; i < len; ++i) {
							writer.println("<tr><td>" + recommendations.get(i) + "</td><td>" + recommendationRatings.get(i) + "</tr>");
						}
					}
					writer.println("</tbody></table>");

					// the second table is the book reading history of the user
					writer.println("<table class=\"table table-bordered table-striped\" style=\"width:600px; table-layout:fixed;\">");
					writer.println("<thead><tr><th style=\"width:70%\">Reading History</th><th style=\"width:15%;\">Rating</th></tr></thead><tbody>");


					int len = history.size();
					
					// length = 0 means the user has viewed some books before 
					if (len != 0) {
						for (int i = 0; i < len; ++i) {
							writer.println("<tr><td>" + history.get(i) + "</td><td>" + historyRatings.get(i) + "</tr>");
						}
					
					// this means the user is either a) anonymous user, or b) a user who
					// doesn't have viewed enough books
					} else {
						writer.println("<tr><td>(View more books to have better recommendations)</td><td>N/A</td><td style=\"overflow:hidden;\">N/A</td></tr>");
					}
					writer.println("</tbody></table>");
				}
			} else {
				writer.println("<p class=\"lead\">Please build model first!</p>");
			}
		}
	}

	// method to respond POST requests
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	}
}
