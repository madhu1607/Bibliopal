/**
 * Different from the normal ListRecommenderMaster, this class only has one 
 * instance of recommender core, which builds the recommendation model 
 * using the data across the companies.
 * 
 * @author Madhuri Rudrabhatla
 */
package com.madhuri.bibliopal.recommender;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.mahout.cf.taste.recommender.RecommendedItem;

import com.madhuri.bibliopal.helper.*;

public class GenericRecommenderMaster {
	private String _folder = "data/";
	
	// the type of this recommender. content-based by default
	private RecommenderType _type = RecommenderType.CONTENT;
	private RecommenderNode _recommender;
	private Parser _parser;

	public GenericRecommenderMaster(String inFolder) {
		_folder = inFolder;
		try {
			setup();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public GenericRecommenderMaster(String inFolder, RecommenderType inType) {
		_folder = inFolder;
		_type = inType;
		try {
			setup();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setup() throws Exception {
		System.out.println("Reading input file...");
		_parser = new Parser(_folder);
		_parser.parse();
		_parser.filter();
		_parser.write();

		System.out.println("Initializing recommender...");
		_recommender = new RecommenderNode(-1, _type, _folder, _parser.getItemID2Title());
	}

	// for debug only, on terminal
	private void printRecommendationInfo(List<RecommendedItem> recs, int userID, boolean printHistory) {

		HashMap<Long, String> itemID2Title = _parser.getItemID2Title();

		System.out.println("------------------------------------------");
		System.out.println("|User ID: " + userID);
		System.out.println("|-----------------------------------------");

		if (printHistory) {
			HashMap<String, Integer> superID2Rating = _parser.getSuperID2Rating();

			System.out.println("|-----------------------------------------");
			System.out.println("|Rating History");
			System.out.println("|-----------------------------------------");

			for (String ID : superID2Rating.keySet()) {

				if (ID.split(",")[0].equals(Integer.toString(userID))) {
					long book = Long.parseLong(ID.split(",")[1]);
					System.out.println("User: " + userID + ", Book: " + itemID2Title.get(book) + ", Rating: " + superID2Rating.get(ID));
				}
			}
		}

		System.out.println("|-----------------------------------------");
		System.out.println("|Recommendations");
		System.out.println("|-----------------------------------------");

		if (recs.size() == 0) {
			System.out.println("|(Need More Data for Prediction)");
		} else {
			for (RecommendedItem rec : recs) {
				System.out.println("|Book Title: " + itemID2Title.get(rec.getItemID()) + ", Rating: " + rec.getValue());
			}
		}
		System.out.println("|-----------------------------------------");
	}

	/**
	 * This method is for testing offline via console.
	 */
	public void run() {
		Scanner input = new Scanner(System.in);

		// a live loop to answer query instantly
		while (true) {
			// read in and check userID
			// if the input is the IP address
			System.out.println("Enter the user ID or IP...");
			int userID = input.nextInt();
			if (userID == -1) {
				break;
			}
			
			try {
				// make recommendations
				List<RecommendedItem> recs = _recommender.recommend(userID);
				printRecommendationInfo(recs, userID, true);

			// the user does not exist
			} catch (Exception e) {
				//e.printStackTrace();
				System.out.println("User does not exist!");
			}
		}
		input.close();
	}

	// wrapper of the recommend function of Mahout's Recommender class. The 
	// results will be directed sent to RecommenderServlet.
	public ArrayList<ArrayList<String>> recommend(String input) {
		int userID;
		
		if (input.matches("[0-9]+")) {
			try {
				userID = Integer.parseInt(input);
			} catch (Exception e) {
				// the ID is too long
				return null;
			}
		} else {
			// the format if ID is not correct
			return null;
		}

		// obtain the recommendations from the recommender.
		List<RecommendedItem> recs;
		try {
			recs = _recommender.recommend(userID);
		} catch (Exception e) {
			System.out.println("Failed to recommend.");
			recs = null;
		}

		// prepare the results needed by servlet.
		ArrayList<String> history = new ArrayList<String>();
		ArrayList<String> historyRatings = new ArrayList<String>();
		ArrayList<String> books = new ArrayList<String>();
		ArrayList<String> bookRatings = new ArrayList<String>();

		HashMap<String, Integer> superID2Rating = _parser.getSuperID2Rating();
		HashMap<Long, String> itemID2Title = _parser.getItemID2Title();

		for (String ID : superID2Rating.keySet()) {
			if (ID.split(",")[0].equals(Integer.toString(userID))) {
				long book = Long.parseLong(ID.split(",")[1]);
				history.add(itemID2Title.get(book));
				historyRatings.add(Integer.toString(superID2Rating.get(ID)));
			}
		}

		for (RecommendedItem rec : recs) {
			long book = rec.getItemID();
			books.add(itemID2Title.get(book));
			bookRatings.add(Float.toString(rec.getValue()));
		}

		ArrayList<ArrayList<String>> ret = new ArrayList<ArrayList<String>>();
		ret.add(history);
		ret.add(historyRatings);
		ret.add(books);
		ret.add(bookRatings);
		return ret;
	}
}
