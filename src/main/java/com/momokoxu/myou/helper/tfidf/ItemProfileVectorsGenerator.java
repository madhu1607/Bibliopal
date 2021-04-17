/**
 * This class is used to build up the "item profile" of each Book application.
 * It basically calculates the TF-IDF of each word in the book titles, and 
 * stores this information in a list of feature vectors.
 */

package com.madhuri.bibliopal.helper.tfidf;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.PrintWriter;
import java.net.URLDecoder;

import com.madhuri.bibliopal.helper.*;

import org.apache.commons.math.linear.ArrayRealVector;

public class ItemProfileVectorsGenerator {
	// _wordCount maps single words to the number of appearance across all titles
	private HashMap<String, Integer> _wordCount; 
	private HashMap<Integer, Integer> _dfHashMap;
	private ArrayList<ArrayRealVector> _featureVectors;
	private HashMap<Long, Integer> _itemIDToIndexHashMap;

	// folder that hold the files
	private String _folder = "data/";
	private String _vectorFile = "feature_vectors.txt";
	private String _stemmedFile = "stemmed_titles.csv";

	public ItemProfileVectorsGenerator() {
		_wordCount = new HashMap<String, Integer>();
		_dfHashMap = new HashMap<Integer, Integer>();
		_itemIDToIndexHashMap = new HashMap<Long, Integer>();
		_featureVectors = new ArrayList<ArrayRealVector>();
		setPaths();
	}
	
	public ItemProfileVectorsGenerator(String inFolder) {
		_folder = inFolder;
		_wordCount = new HashMap<String, Integer>();
		_dfHashMap = new HashMap<Integer, Integer>();
		_itemIDToIndexHashMap = new HashMap<Long, Integer>();
		_featureVectors = new ArrayList<ArrayRealVector>();
		setPaths();
	}

	public ItemProfileVectorsGenerator(HashMap<String, Integer> inTable) {
		_wordCount = inTable;
		_dfHashMap = new HashMap<Integer, Integer>();
		_itemIDToIndexHashMap = new HashMap<Long, Integer>();
		_featureVectors = new ArrayList<ArrayRealVector>();
	}
	
	public ItemProfileVectorsGenerator(String inFolder, 
			HashMap<String, Integer> inTable) {
		_folder = inFolder;
		_wordCount = inTable;
		_dfHashMap = new HashMap<Integer, Integer>();
		_itemIDToIndexHashMap = new HashMap<Long, Integer>();
		_featureVectors = new ArrayList<ArrayRealVector>();
		setPaths();
	}
	
	// combine folder with the file names to generate the paths
	private void setPaths() {
		_vectorFile = _folder + _vectorFile;
		_stemmedFile = _folder + _stemmedFile;
	}

	// access methods
	public void setFeatureHashMap(HashMap<String, Integer> inTable) {
		_wordCount = inTable;
	}

	public HashMap<String, Integer> getFeatureHashMap() {
		return _wordCount;
	}

	public void setItemIDToIndexHashMap(HashMap<Long, Integer> inTable) {
		_itemIDToIndexHashMap = inTable;
	}

	public HashMap<Long, Integer> getItemIDToIndexHashMap() {
		return _itemIDToIndexHashMap;
	}

	public void setFeatureVectors(ArrayList<ArrayRealVector> vecs) {
		_featureVectors = vecs;
	}

	public ArrayList<ArrayRealVector> getFeatureVectors() {
		return _featureVectors;
	}

	public void setVectorFile(String inFile) {
		_vectorFile = inFile;
	}

	public String getVector() {
		return _vectorFile;
	}

	public void setStemmedFile(String inFile) {
		_stemmedFile = inFile;
	}

	public String getStemmedFile() {
		return _stemmedFile;
	}

	// this method reads in TF vectors and generate TF-IDF vectors
	public void tfIdf() {
		// process the stemmed file, where each line corresponds to a
		// (BookID, Book title) pair
		CsvReader reader = new CsvReader(_stemmedFile);
		ArrayList<String> lines = reader.read();
		ArrayList<ArrayRealVector> vec = new ArrayList<ArrayRealVector>();

		// the dimensionality is equal to the total number of words in
		// titles
		int dim = _wordCount.size();
		
		// number of book titles
		int nBook = lines.size();
		int lineCnt = 0;

		for (String line : lines) {
			// initialize the feature vector for current line of stemmed file 
			ArrayRealVector v = new ArrayRealVector(dim);
			try {
				String[] lineTokens = line.split(",");
				_itemIDToIndexHashMap.put(Long.parseLong(lineTokens[0]), lineCnt++);

				// check if the book title is valid
				if (lineTokens.length > 1) {
					// tokenize
					String[] words = URLDecoder.decode(lineTokens[1].trim(), "UTF-8").split("\\s+");

					// calculate DF (document frequency) for each word and 
					// store it in the feature vector for future processing
					for (String w : words) {
						if (_wordCount.get(w) != null) {
							// obtain corresponding index of the word in the feature vector
							int index = _wordCount.get(w);
							v.setEntry(index, v.getEntry(index) + 1);

							// calculate the DF
							if (_dfHashMap.get(index) == null) {
								_dfHashMap.put(index, 1);
							} else {
								_dfHashMap.put(index, _dfHashMap.get(index) + 1);
							}
						}
					}
				}
				// append vector to the vector list
				vec.add(v);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// calculate TF-IDF score for each feature vector.
		for (ArrayRealVector v : vec) {
			for (int i = 0; i < dim; ++i) {
				double tf = v.getEntry(i);
				if (tf != 0) {
					double idf = nBook / (double) _dfHashMap.get(i);
					// score = TF * lg(idf)
					v.setEntry(i, 1.0 * tf * Math.log(idf));
				}
			}
			_featureVectors.add(v);
		}
	}

	// write feature vectors to the file
	public void write() {
		try {
			PrintWriter writer = new PrintWriter(_vectorFile, "UTF-8");
			for (ArrayRealVector v : _featureVectors) {
				writer.println(v);
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
