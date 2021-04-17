/**
 * This code is for testing
 */
package com.madhuri.bibliopal.test_snippets;

import com.madhuri.bibliopal.recommender.*;

public class MasterRunner {
	public static void main(String[] args) {
		String folder = "data/";
		GenericRecommenderMaster master = new GenericRecommenderMaster(folder);
		master.run();
	}
}