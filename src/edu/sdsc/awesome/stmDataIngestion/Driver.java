package edu.sdsc.awesome.stmDataIngestion;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Driver for stm data ingestor
 * @author Haoran Sun
 * @since 03-29-2018
 */
public class Driver {
	/**
	 * Parse topic information from stm output.
	 * @param stmFile - stm output that contains topic scores and top 10 terms
	 * @param exclCohrFile - stm output that contains exclusivity and semantic
	 * coherence scores.
	 * @return - A JSONArray representation of topics
	 */
	public static JSONArray getTopicInfo(String stmFile, String exclCohrFile) {
		TopicInfo topicInfo = new TopicInfo();
		ArrayList<Float> excl = new ArrayList<>();
		ArrayList<Float> cohr = new ArrayList<>();

		/* Read and parse exclusivity and semantic coherence socres from file */
		try(Scanner sc = new Scanner(new FileReader(exclCohrFile))) {
			int count = 0;
			ArrayList<Float> storage = excl;
			while(sc.hasNextLine()) {
				String line = sc.nextLine().trim();
				if(!line.startsWith("[")) continue; //Validate lines
				if(line.startsWith("[1]")) //Check boundary of two kinds of scores
					count++;
				if(count > 1)
					storage = cohr;
				String[] cols = line.split(" +");
				for(int i = 1; i < cols.length; i++)
					storage.add(Float.parseFloat(cols[i]));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		/* Read and parse stm topic output from file */
		try(Scanner sc = new Scanner(new FileInputStream(stmFile), 
				StandardCharsets.UTF_8.toString())) {
			int topicNum = 1;
			boolean located = false;
			while(sc.hasNextLine()) { //Locate starting point
				String line = sc.nextLine();
				if(line.startsWith("A topic model with")) {
					located = true;
					continue;
				}
				if(located) {
					String[] textblock = new String[4];
					/* Parse output of four different scoring methods */
					for(int i = 0; i < textblock.length; i++)
						textblock[i] = sc.nextLine();
					topicInfo.ingest(textblock, excl.get(topicNum - 1), 
							cohr.get(topicNum - 1));
					topicNum++;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return topicInfo.getAllTopicInfo();
	}
	
	/**
	 * Read and parse topic correlation matrix.
	 * @param filename - topic correlation csv file
	 * @return - A JSONArray representation of topic correlation matrix.
	 */
	public static JSONArray getTopicCorrelation(String filename) {
		TopicCorrelation corr = new TopicCorrelation();
		/* Read and parse topic correlation matrix line by line */
		try(Scanner sc = new Scanner(new FileReader(filename))) {
			sc.nextLine(); //Skip header
			while(sc.hasNextLine())
				corr.appendRow(sc.nextLine());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return corr.getCorrMatrix();
	}
	
	/**
	 * Read and parse document-topic proportion matrix; replace stm document id
	 * with actual file id.
	 * @param filename - a file containing document-topic proportion matrix
	 * @param topicInfo - a JSONArray representation of topics
	 * @param mappings - mappings from stm document id to file id
	 * @param threshold - cutoff for topic proportion
	 */
	public static void getAndInsertDocTopicProp(String filename,
			JSONArray topicInfo, ArrayList<String> mappings, float threshold) {
		DocumentTopicProportion dtp = new DocumentTopicProportion();
		/* Read and parse document-topic correlation matrix line by line */
		try(Scanner sc = new Scanner(new FileReader(filename))) {
			sc.nextLine();
			while(sc.hasNextLine())
				dtp.appendRow(sc.nextLine(), threshold);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		JSONArray theta = dtp.getProportionMatrix(); //Parsed proportion matrix
		for(int i = 0; i < theta.length(); i++) { //Insert to topic info JSONArray
			JSONObject prop = theta.getJSONObject(i);
			int topicNum = prop.getInt("Topic");
			if(!topicInfo.getJSONObject(topicNum - 1).has("DocTopicProportion"))
				topicInfo.getJSONObject(topicNum - 1).put("DocTopicProportion", 
						new JSONArray());
			JSONObject tmp = new JSONObject();
			tmp.put("Doc", mappings.get(prop.getInt("Document") - 1)); //Replace id
			tmp.put("Prop", prop.getFloat("Proportion"));
			topicInfo.getJSONObject(topicNum - 1).getJSONArray("DocTopicProportion")
			.put(tmp);
		}
	}
	
	/**
	 * Main method for the driver.
	 * @param args: arg0: stm topics output file
	 *              arg1: stm exclusivity and semantic coherence score output
	 *              file
	 *              arg2: topic correlation matrix file
	 *              arg3: document-topic proportion matrix file
	 *              arg4: mappings file that contains mappings from stm id to
	 *              actual file id.
	 */
	public static void main(String[] args) {
		ArrayList<String> mappings = new ArrayList<>();
		try(Scanner sc = new Scanner(new FileReader(args[4]))) {
			while(sc.hasNextLine())
				mappings.add(sc.nextLine());
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		JSONArray topicInfo = Driver.getTopicInfo(args[0], args[1]);
		JSONArray topicCorr = Driver.getTopicCorrelation(args[2]);
		Driver.getAndInsertDocTopicProp(args[3], topicInfo, mappings, Float.parseFloat(args[5]));
		JSONObject stmInfo = new JSONObject();
		//stmInfo.put("DocID", 3);
		//stmInfo.put("Predicate", "Date: 2013-11-01 to 2016-11-01");
		stmInfo.put("NumTopics", topicInfo.length());
		stmInfo.put("Topics", topicInfo);
		stmInfo.put("TopicCorr", topicCorr);
		//stmInfo.put("TopicNumAuto", true);
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(args[6]))) {
			writer.write(stmInfo.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}