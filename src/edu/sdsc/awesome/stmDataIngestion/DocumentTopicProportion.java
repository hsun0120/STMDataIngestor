package edu.sdsc.awesome.stmDataIngestion;

import org.json.JSONArray;
import org.json.JSONObject;

public class DocumentTopicProportion {
	private JSONArray theta;
	
	public DocumentTopicProportion() {
		this.theta = new JSONArray();
	}
	
	public void appendRow(String row, float threshold) {
		String[] cols = row.split(",");
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
	
	public JSONArray getProportionMatrix() {
		return this.theta;
	}
	
	public int size() {
		return this.theta.length();
	}
	
	public void clear() {
		this.theta = new JSONArray();
	}
}