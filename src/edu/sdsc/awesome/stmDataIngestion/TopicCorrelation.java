package edu.sdsc.awesome.stmDataIngestion;

import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A class that stores the topic correlation matrix.
 * @author Haoran Sun
 * @since 03-29-2018
 */
public class TopicCorrelation {
	private JSONArray corrMatrix;
	private HashSet<String> set;
	
	/**
	 * Default constructor
	 */
	public TopicCorrelation() {
		this.corrMatrix = new JSONArray();
		this.set = new HashSet<>();
	}
	
	/**
	 * Append a row of topic correlation matrix.
	 * @param row - string representation of a row
	 */
	public void appendRow(String row) {
		String[] cols = row.split(","); //Correlation scores
		int topicId = Integer.parseInt(cols[0].replace("\"", ""));
		for(int i = 1; i < cols.length; i++) {
			/* Check duplicate edge; each edge is not directed */
			if(i == topicId || this.set.contains(i + " " + topicId)) continue;
			this.set.add(topicId + " " + i);
			
			JSONObject reln = new JSONObject();
			reln.put("First", topicId);
			reln.put("Second", i);
			reln.put("Corr", Float.parseFloat(cols[i]));
			this.corrMatrix.put(reln);
		}
	}
	
	/**
	 * Get a JSONArray representation of the correlation matrix.
	 * @return A JSONArray of correlations
	 */
	public JSONArray getCorrMatrix() {
		return this.corrMatrix;
	}
	
	/**
	 * Get the size of the matrix.
	 * @return size of the matrix
	 */
	public int size() {
		return this.corrMatrix.length();
	}
	
	/**
	 * Remove all correlations.
	 */
	public void clear() {
		this.corrMatrix = new JSONArray();
	}
}