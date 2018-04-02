package edu.sdsc.awesome.stmDataIngestion;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A class that store document-topic correlations.
 * @author Haoran Sun
 * @since 03-29-2018
 */
public class DocumentTopicProportion {
	private JSONArray theta;
	
	/**
	 * Default constructor
	 */
	public DocumentTopicProportion() {
		this.theta = new JSONArray();
	}
	
	/**
	 * Parse and append rows of matrix
	 * @param row - string representation of a row
	 * @param threshold - correlation score cutoff
	 */
	public void appendRow(String row, float threshold) {
		String[] cols = row.split(","); //Correlation scores
		/* Obtain document id */
		int docId = Integer.parseInt(cols[0].replace("\"", ""));
		for(int i = 1; i < cols.length; i++) {
			if(Float.parseFloat(cols[i]) < threshold) continue;
			
			JSONObject prop = new JSONObject();
			prop.put("Document", docId);
			prop.put("Topic", i);
			prop.put("Proportion", Float.parseFloat(cols[i]));
			this.theta.put(prop);
		}
	}
	
	/**
	 * Getter method for document-topic proportion matrix
	 * @return A JSONArray representation of the matrix
	 */
	public JSONArray getProportionMatrix() {
		return this.theta;
	}
	
	/**
	 * Number of correlations.
	 * @return size of the document-topic proportion vector.
	 */
	public int size() {
		return this.theta.length();
	}
	
	/**
	 * Clear all document-topic proportions elements.
	 */
	public void clear() {
		this.theta = new JSONArray();
	}
}