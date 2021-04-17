/**
 * This class is used to read CSV file and return it as an ArraryList<String>. 
 * Each element of the list is corresponding to each line of the file.
 */

package com.madhuri.bibliopal.helper;

import java.io.*;
import java.util.ArrayList;

public class CsvReader {
	// path to the CSV file
	private String _path = "";
	
	// this is used to control the max number of lines to be read
	private int _maxLineToRead = 5000000;
	
	public CsvReader() {};
	public CsvReader(String path) {
		_path = path;
	}
	public CsvReader(String path, int inMaxLineToRead) {
		_path = path;
		_maxLineToRead = inMaxLineToRead;
	}
	
	// read line by line, and append each line to an ArrayList
	public ArrayList<String> read() {
		
		// this is the array list to be returned
		ArrayList<String> csvLines = new ArrayList<String>();
		
		BufferedReader br = null;
		String line = "";
		int lineCnt = 0;
		
		try {
			br = new BufferedReader(new FileReader(_path));
			// read each line of the file
			while ((line = br.readLine()) != null) {
				// exceeds the threshold
				if (lineCnt++ > _maxLineToRead) break;
				
				csvLines.add(line);
			} 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// close the file for processing
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return csvLines;
	}
}
