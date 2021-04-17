/**
 * This class is the core book recommender for the system. 
 */
package com.madhuri.bibliopal.recommender;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.HashMap;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.*;
import org.apache.mahout.cf.taste.impl.recommender.svd.*;
import org.apache.mahout.cf.taste.impl.similarity.*;
import org.apache.mahout.cf.taste.impl.recommender.AllSimilarItemsCandidateItemsStrategy;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.CandidateItemsStrategy;
import org.apache.mahout.cf.taste.recommender.MostSimilarItemsCandidateItemsStrategy;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

import com.madhuri.bibliopal.helper.tfidf.ItemProfileVectorsGenerator;
import com.madhuri.bibliopal.helper.tfidf.TitleAnalyzer;
import com.madhuri.bibliopal.recommender.similarity.*;

public class RecommenderNode {
	private int _recID;

	// denotes the recommender type
	private RecommenderType _recommenderType;

	// the path to the respective data file
	private String _folder = "data/";
	private String _dataFile = "parsed_data.csv";

	// data model to hold the input data
	private DataModel _dataModel;

	// the recommender engine
	private Recommender _recommender;
	
	// book title mapping
	private HashMap<Long, String> _bookMapping = new HashMap<Long, String>();

	// analyzer tokenizes the titles and eliminates stem words, while the latter
	// generates the feature vectors for each book.
	private static TitleAnalyzer _titleAnalyzer = null;
	private static ItemProfileVectorsGenerator _itemProfilesGenerator = null;

	public RecommenderNode(int assignedID, RecommenderType recommenderType, String inFolder, HashMap<Long, String> mapping) {
		_recID = assignedID;
		_folder = inFolder;
		_dataFile = _folder + _dataFile;
		_recommenderType = recommenderType;
		_bookMapping = mapping;
		// once the file path has been set, call setup function to build up the
		// prediction model
		try {
			setup();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Build up the recommender engine.
	 * 
	 * @throws Exception
	 */
	private void setup() throws Exception {

		System.out.println("Analyzing book titles...");
		
		if (_titleAnalyzer == null) {
			_titleAnalyzer = new TitleAnalyzer(_folder, _bookMapping);
			_titleAnalyzer.tokenize();
			_titleAnalyzer.build();
			_titleAnalyzer.write();
		}

		System.out.println("Generating book profiles...");
		if (_itemProfilesGenerator == null) {
			_itemProfilesGenerator = new ItemProfileVectorsGenerator(_folder, _titleAnalyzer.getFeatureHashMap());
			_itemProfilesGenerator.tfIdf();
			_itemProfilesGenerator.write();
		}

		System.out.println("Setting up recommender " + (_recID == -1 ? "" : _recID) + "...");

		// load data
		try {
			_dataModel = new FileDataModel(new File(_dataFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// item-based recommender
		if (_recommenderType == RecommenderType.ITEM) {
			ItemSimilarity sim = new PearsonCorrelationSimilarity(_dataModel);
			_recommender = new GenericItemBasedRecommender(_dataModel, sim);
		// user-based recommender
		} else if (_recommenderType == RecommenderType.USER) {
			UserSimilarity sim = new PearsonCorrelationSimilarity(_dataModel);
			UserNeighborhood neighborhood = new NearestNUserNeighborhood(5, sim, _dataModel);
			_recommender = new GenericUserBasedRecommender(_dataModel, neighborhood, sim);
		// single-value-decomposition-based (matrix factorization) recommender
		} else if (_recommenderType == RecommenderType.SVD) {
			_recommender = new SVDRecommender(_dataModel, new ALSWRFactorizer(_dataModel, 10, 0.05, 30));
		} else if (_recommenderType == RecommenderType.CONTENT) {
			ItemSimilarity sim = new ContentBasedItemSimilarity(_dataModel, _itemProfilesGenerator.getFeatureVectors(), _itemProfilesGenerator.getItemIDToIndexHashMap());

			CandidateItemsStrategy candidateStrategy = new AllSimilarItemsCandidateItemsStrategy(sim);
			MostSimilarItemsCandidateItemsStrategy similarCandidateStrategy = new AllSimilarItemsCandidateItemsStrategy(sim);

			_recommender = new GenericItemBasedRecommender(_dataModel, sim, candidateStrategy, similarCandidateStrategy);

		}
	}

	public synchronized List<RecommendedItem> recommend(long userID) throws Exception {
		return _recommender.recommend(userID, 5);
	}
	
	public synchronized List<RecommendedItem> recommend(long userID, int nRecs) throws Exception {
		return _recommender.recommend(userID, nRecs);
	}
}
