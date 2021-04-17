/**
 * This defines different types of recommendation metrics to be used in the engine.
 * ITEM stands for item-based approach, USER stands for user-based, CONTENT stands
 * for content-based (not yet implemented), and SVD stands for single values de-
 * composition approach.
 */

package com.madhuri.bibliopal.recommender;

public enum RecommenderType {
	ITEM, USER, CONTENT, SVD
}
