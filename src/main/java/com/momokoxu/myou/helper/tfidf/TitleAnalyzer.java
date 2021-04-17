/**
 * This class reads book mapping and converts the book titles into
 * tokens. Stem words will also be removed. Basically it calculates
 * TF.
 */

package com.madhuri.bibliopal.helper.tfidf;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.StringReader;
import java.io.PrintWriter;
import java.net.URLDecoder;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.*;
import org.apache.lucene.util.Version;

public class TitleAnalyzer {
	private String _folder = "data/";
	private String _dictFile = "dictionary.csv";
	private String _stemmedFile = "stemmed_titles.csv";
	private ArrayList<String> _tokens;
	private HashMap<Long, String> _bookMapping;
	
	// tf
	private HashMap<String, Integer> _wordCount;

	public TitleAnalyzer() {
		_tokens = new ArrayList<String>();
		_wordCount = new HashMap<String, Integer>();
		setPaths();
	}
	
	public TitleAnalyzer(String inFolder, HashMap<Long, String> mapping) {
		_folder = inFolder;
		_tokens = new ArrayList<String>();
		_wordCount = new HashMap<String, Integer>();
		_bookMapping = mapping;
		setPaths();
	}
	
	public HashMap<String, Integer> getFeatureHashMap() {
		return _wordCount;
	}

	private void setPaths() {
		_dictFile = _folder + _dictFile;
		_stemmedFile = _folder + _stemmedFile;
	}

	public void tokenize() {
		Analyzer analyzer = new EnglishAnalyzer(Version.LUCENE_46);
		
		try {
			// initialize the printer
			PrintWriter writer = new PrintWriter(_stemmedFile, "UTF-8");
			
			for (long itemID: _bookMapping.keySet()) {
				// store the book ID, which is in the first column
				writer.print(itemID + ",");
				
				// eliminate punctuation
				String title = URLDecoder.decode(_bookMapping.get(itemID), "UTF-8").replace("-", " ").replace(".", " ");
				
				if (title.length() > 0) {
					// use Lucene to analyze the words
					TokenStream stream = analyzer.tokenStream(null, new StringReader(title));
					CharTermAttribute cta = stream.addAttribute(CharTermAttribute.class);
					stream.reset();

					while (stream.incrementToken()) {
						// Lucene automatically skip stop words and stem each word
						String w = cta.toString();
					
						writer.print(" " + w);
						// also record the word in the HashMap 
						if (!_tokens.contains(w)) {
							_tokens.add(w);
						}
					}
					stream.end();
					stream.close();
				}
				writer.println();
			}
			analyzer.close();
			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// this builds the term frequency (TF), which means how
	// many times each word has appeared across all titles.
	public void build() {
		int cnt = 0;
		for (String t : _tokens) {
			_wordCount.put(t, cnt++);
		}
	}

	// write the TF records to the file
	public void write() {
		try {
			PrintWriter writer = new PrintWriter(_dictFile, "UTF-8");
			for (String t : _wordCount.keySet()) {
				writer.println(_wordCount.get(t) + "," + t);
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		tokenize();
		build();
		write();
	}
}