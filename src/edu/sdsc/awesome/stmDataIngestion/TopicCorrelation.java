package edu.sdsc.awesome.stmDataIngestion;

import org.json.JSONArray;
import org.json.JSONObject;

public class TopicCorrelation {
	private JSONArray corrMatrix;
	
	public TopicCorrelation() {
		this.corrMatrix = new JSONArray();
	}
	
	public void appendRow(String row) {
		String[] cols = row.split(",");
		int topicId = Integer.parseInt(cols[0].replace("\"", ""));
		for(int i = 1; i < cols.length; i++) {
			if(topicId == i || cols[i].equals("0")) continue;
			
			JSONObject reln = new JSONObject();
			reln.put("First", topicId);
			reln.put("Second", i);
			reln.put("Corr", Float.parseFloat(cols[i]));
			this.corrMatrix.put(reln);
		}
	}
	
	public JSONArray getCorrMatrix() {
		return this.corrMatrix;
	}
	
	public int size() {
		return this.corrMatrix.length();
	}
	
	public void clear() {
		this.corrMatrix = new JSONArray();
	}
}