/**
 * This class is used to parse original dataset.
 */

package com.madhuri.bibliopal.helper;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Parser {
	private int MAX_LINE = 100000;
	// IO file paths
	private String _folder = "data/";
	private String _outputFile = "parsed_data.csv";
	private String _ratingFile = "ratings.csv";
	private String _bookMappingFile = "books.csv";
	
	// hash table to record mapping
	private HashMap<String, Integer> _superID2Rating = new HashMap<String, Integer>();
	private HashMap<Long, String> _itemID2Title = new HashMap<Long, String>();
	// assisting filter()
	private HashMap<Long, Integer> _itemID2Appearance = new HashMap<Long, Integer>();
	private HashMap<Integer, Integer> _userID2Appearance = new HashMap<Integer, Integer>();

	// begin of constructors
	public Parser() {
		setPaths();
	}
	
	public Parser(String inFolder) {
		_folder = inFolder;
		setPaths();
	}
	
	private void setPaths() {
		_ratingFile = _folder + _ratingFile;
		_outputFile = _folder + _outputFile;
		_bookMappingFile = _folder + _bookMappingFile;
	}

	/**
	 * This is the backbone of the parsing procedure. It reads each line of the
	 * input CSV file with CsvReader. Then it parses each fields of the entry 
	 * and store corresponding information into the HashMaps. 
	 * 
	 * @throws Exception
	 */
	public void parse() throws Exception {
		// read each line of the CSV file and save the lines into an array list
		ArrayList<String> lines = new CsvReader(_ratingFile).read();

		System.out.println("Parsing rating file...");

		int lineCnt = 0;
		// read in each row of the input
		for (String row : lines) {
			if (lineCnt++ == 0) continue;
			if (lineCnt > MAX_LINE) break;

			// split each row of CSV file based on colon
			String[] tokens = row.split(";");

			// retrieve fields of the row
			String userID = tokens[0].substring(1, tokens[0].length() - 1).trim();
			String isbn = tokens[1].substring(1, tokens[1].length() - 1).trim();
			String superID = userID + "," + isbn.hashCode();
			int rating = Integer.parseInt(tokens[2].substring(1, tokens[2].length() - 1).trim());
			
			// record appearance for filtering
			if (_superID2Rating.get(superID) == null) {
				if (_userID2Appearance.get(Integer.parseInt(userID)) == null) {
					_userID2Appearance.put(Integer.parseInt(userID), 1);
				} else {
					_userID2Appearance.put(Integer.parseInt(userID), _userID2Appearance.get(Integer.parseInt(userID)) + 1);
				}

				if (_itemID2Appearance.get((long)isbn.hashCode()) == null) {
					_itemID2Appearance.put((long)isbn.hashCode(), 1);
				} else {
					_itemID2Appearance.put((long)isbn.hashCode(), _itemID2Appearance.get((long)isbn.hashCode()) + 1);
				}
			}
			
			_superID2Rating.put(superID, rating);
		}
		
		System.out.println("Parsing book file...");
		
		lines = new CsvReader(_bookMappingFile).read();
		lineCnt = 0;
		// read in each row of the input
		for (String row : lines) {
			if (lineCnt++ == 0) continue;
			// split each row of CSV file based on colon
			String[] tokens = row.split(";");
			String bookID = tokens[0].substring(1, tokens[0].length() - 1).trim();
			String title = tokens[1].substring(1, tokens[1].length() - 1).trim().replace("%", " ");
			
			_itemID2Title.put((long)bookID.hashCode(), title);
		}
	}
	
	/**
	 * This function is used by the data filter to filter invalid users
	 * WRT the parameters. 
	 * 
	 * Appearance means how many books the user has been "interacted" with.
	 * The user gets filtered if the value is too high or too low.
	 * 
	 * @param userID - the user ID to validate
	 * @return whether it is valid
	 */
	private boolean checkUserToRemove(int userID) {
		if (_userID2Appearance.get(userID) < 5) {
			return true;
		} else {
			return false;
		}
	}
	

	private boolean checkItemToRemove(long itemID) {
		if (_itemID2Appearance.get(itemID) < 10) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * @throws Exception
	 */
	public void filter() throws Exception {
		System.out.println("Filtering...");
		ArrayList<Integer> usersToRemove = new ArrayList<Integer>();
		ArrayList<Long> booksToRemove = new ArrayList<Long>();
		ArrayList<String> superIDsToRemove = new ArrayList<String>();
		
		int cnt = 0;
		for (int userID: _userID2Appearance.keySet()) {
			if (checkUserToRemove(userID)) {
				usersToRemove.add(userID);
				++cnt;
			}
		}
		System.out.println("Filtered " + cnt + " users..");
		
		cnt = 0;
		for (long book: _itemID2Appearance.keySet()) {
			if (checkItemToRemove(book)) {
				booksToRemove.add(book);
				++cnt;
			}
		}
		System.out.println("Filtered " + cnt + " books...");
		
		cnt = 0;
		for (String ID: _superID2Rating.keySet()) {
			int userID = Integer.parseInt(ID.split(",")[0]);
			long bookID = Long.parseLong(ID.split(",")[1]);
			if (checkUserToRemove(userID) || checkItemToRemove(bookID)) {
				++cnt;
				superIDsToRemove.add(ID);
			}
		}

		for (String superID: superIDsToRemove) {
			_superID2Rating.remove(superID);
		}
		
		HashSet<Long> remaining = new HashSet<Long>();
		HashMap<String, Integer> updatedSuperIDMapping = new HashMap<String, Integer>();
		for (String superID: _superID2Rating.keySet()) {
			long bookID = Long.parseLong(superID.split(",")[1]);
			if (_itemID2Title.containsKey(bookID)) {
				remaining.add(bookID);
				updatedSuperIDMapping.put(superID, _superID2Rating.get(superID));
			}
		}
		
		HashMap<Long, String> updatedBookMapping = new HashMap<Long, String>();
		for (long bookID: _itemID2Title.keySet()) {
			if (remaining.contains(bookID)) {
				updatedBookMapping.put(bookID, _itemID2Title.get(bookID));
			}
		}
		_itemID2Title = updatedBookMapping;
		_superID2Rating = updatedSuperIDMapping;

		System.out.println("In total, filtered out " + cnt + " data points...");
		System.out.println("Remaining " + _superID2Rating.size() + " and " + _itemID2Title.size() + " books...");
	}
	
	public void write() throws Exception {
		System.out.println("Writing parsed results...");
		PrintWriter outputWriter = new PrintWriter(_outputFile);
		for (String superID: _superID2Rating.keySet()) {
			outputWriter.write(superID + "," + _superID2Rating.get(superID) + "\n");
		}
		outputWriter.close();
	}
	
	public HashMap<Long, String> getItemID2Title() {
		return _itemID2Title;
	}
	
	public HashMap<String, Integer> getSuperID2Rating() {
		return _superID2Rating;
	}
} // end of class