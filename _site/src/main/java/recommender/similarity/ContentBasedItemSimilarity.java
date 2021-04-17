/**
 * This class implements the core of content-based recommendation. It calculates 
 * the cosine distance of two feature vectors in order to denote the similarity 
 * between two book titles.
 */

package com.madhuri.bibliopal.recommender.similarity;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.math.linear.ArrayRealVector;
import org.apache.mahout.cf.taste.impl.similarity.*;
import org.apache.mahout.cf.taste.model.DataModel;

// AbstractItemSimilarity is the interface provided by Mahout for users
// to implement customized similarity
public class ContentBasedItemSimilarity extends AbstractItemSimilarity {
	private ArrayList<ArrayRealVector> _featureVectors;
	private HashMap<Long, Integer> _itemIDToIndexHashMap;
	
	public ContentBasedItemSimilarity(DataModel model, ArrayList<ArrayRealVector> inVec, HashMap<Long, Integer> inTable) {
		super(model);
		_featureVectors = inVec;
		_itemIDToIndexHashMap = inTable;
	}
	
	private ArrayRealVector getFeatureVector(long bookID) {
		return _featureVectors.get(_itemIDToIndexHashMap.get(bookID));
	}
	
	// calculate the cosine similarity
	public double itemSimilarity(long bookID1, long bookID2) {
		ArrayRealVector v1 = getFeatureVector(bookID1);
		ArrayRealVector v2 = getFeatureVector(bookID2);
		return v1.dotProduct(v2) / (v1.getNorm() * v2.getNorm());
	}
	
	public double[] itemSimilarities(long bookID1, long[] bookIDs) {
		int len = bookIDs.length;
		double[] res = new double[len];

		for (int i = 0; i < len; ++i) {
			res[i] = itemSimilarity(bookID1, bookIDs[i]) + 1.0;
		}
		return res;
	}
}
