package edu.sdsc.awesome.stmDataIngestion;

import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A class that stores stm topic information, including top terms evaluated
 * by highest probability, FREX, lift, and score.
 * @author Haoran Sun
 * @since 03-29-2018
 */
public class TopicInfo {
	static final String[] TYPE = {"Highest_prob", "FREX", "Lift", "Score"};
	private JSONArray topics;
	
	/**
	 * Default constructor
	 */
	public TopicInfo() {
		topics = new JSONArray();
	}
	
	/**
	 * Parse data for a topic from string array and store them.
	 * @param info - string array of top terms using four different methods
	 * @param exclusivity - the exclusivity score
	 * @param semCohr - the semantic coherence score
	 */
	public void ingest(String[] info, float exclusivity, float semCohr) {
		JSONObject topic = new JSONObject();
		topic.put("Exclusivity", exclusivity);
		topic.put("Semantic Coherence", semCohr);
		for(int i = 0; i < info.length; i++) {
			String ranking = info[i].substring(info[i].indexOf(':') + 1).trim().
					replace(",", ""); //Remove row description and commas
			JSONArray arr = new JSONArray();
			try(Scanner sc = new Scanner(ranking)) { //Add each word
				while(sc.hasNext())
					arr.put(sc.next());
			}
			topic.put(TYPE[i], arr);
		}
		this.topics.put(topic);
	}
	
	/**
	 * Get a JSONArray representation of topic information.
	 * @return A JSONArray of topic information
	 */
	public JSONArray getAllTopicInfo() {
		return this.topics;
	}
	
	/**
	 * Get the size of topic information vector.
	 * @return size of the vector
	 */
	public int size() {
		return this.topics.length();
	}
	
	/**
	 * Remove all topic information records.
	 */
	public void clear() {
		this.topics = new JSONArray();
	}
}