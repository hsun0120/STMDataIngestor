package edu.sdsc.awesome.stmDataIngestion;

import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONObject;

public class TopicCorrelation {
	private JSONArray corrMatrix;
	private HashSet<String> set;
	
	public TopicCorrelation() {
		this.corrMatrix = new JSONArray();
		this.set = new HashSet<>();
	}
	
	public void appendRow(String row) {
		String[] cols = row.split(",");
		int topicId = Integer.parseInt(cols[0].replace("\"", ""));
		for(int i = 1; i < cols.length; i++) {
			if(i == topicId || this.set.contains(i + " " + topicId)) continue;
			this.set.add(topicId + " " + i);
			
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