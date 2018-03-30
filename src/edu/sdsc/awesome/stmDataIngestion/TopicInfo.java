package edu.sdsc.awesome.stmDataIngestion;

import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class TopicInfo {
	static final String[] TYPE = {"Highest_prob", "FREX", "Lift", "Score"};
	private JSONArray topics;
	
	public TopicInfo() {
		topics = new JSONArray();
	}
	
	public void ingest(String[] info, float exclusivity, float semCohr) {
		JSONObject topic = new JSONObject();
		topic.put("Exclusivity", exclusivity);
		topic.put("Semantic Coherence", semCohr);
		for(int i = 0; i < info.length; i++) {
			String ranking = info[i].substring(info[i].indexOf(':') + 1).trim().
					replace(",", "");
			JSONArray arr = new JSONArray();
			try(Scanner sc = new Scanner(ranking)) {
				while(sc.hasNext())
					arr.put(sc.next());
			}
			topic.put(TYPE[i], arr);
		}
		this.topics.put(topic);
	}
	
	public JSONArray getAllTopicInfo() {
		return this.topics;
	}
	
	public int size() {
		return this.topics.length();
	}
	
	public void clear() {
		this.topics = new JSONArray();
	}
}