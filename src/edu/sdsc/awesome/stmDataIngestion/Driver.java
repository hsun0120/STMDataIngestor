package edu.sdsc.awesome.stmDataIngestion;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class Driver {
	public static JSONArray getTopicInfo(String stmFile, String exclCohrFile) {
		TopicInfo topicInfo = new TopicInfo();
		ArrayList<Float> excl = new ArrayList<>();
		ArrayList<Float> cohr = new ArrayList<>();

		try(Scanner sc = new Scanner(new FileReader(exclCohrFile))) {
			int count = 0;
			ArrayList<Float> storage = excl;
			while(sc.hasNextLine()) {
				String line = sc.nextLine().trim();
				if(!line.startsWith("[")) continue;
				if(line.startsWith("[1]"))
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
		
		try(Scanner sc = new Scanner(new FileReader(stmFile))) {
			int topicNum = 1;
			boolean located = false;
			while(sc.hasNextLine()) {
				String line = sc.nextLine();
				if(line.startsWith("A topic model with")) {
					located = true;
					continue;
				}
				if(located) {
					String[] textblock = new String[4];
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
	
	public static JSONArray getTopicCorrelation(String filename) {
		TopicCorrelation corr = new TopicCorrelation();
		try(Scanner sc = new Scanner(new FileReader(filename))) {
			sc.nextLine();
			while(sc.hasNextLine())
				corr.appendRow(sc.nextLine());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return corr.getCorrMatrix();
	}
	
	public static void getAndInsertDocTopicProp(String filename,
			JSONArray topicInfo, ArrayList<String> mappings, float threshold) {
		DocumentTopicProportion dtp = new DocumentTopicProportion();
		try(Scanner sc = new Scanner(new FileReader(filename))) {
			sc.nextLine();
			while(sc.hasNextLine())
				dtp.appendRow(sc.nextLine(), threshold);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		JSONArray theta = dtp.getProportionMatrix();
		for(int i = 0; i < theta.length(); i++) {
			JSONObject prop = theta.getJSONObject(i);
			int topicNum = prop.getInt("Topic");
			if(!topicInfo.getJSONObject(topicNum - 1).has("DocTopicProportion"))
				topicInfo.getJSONObject(topicNum - 1).put("DocTopicProportion", 
						new JSONArray());
			JSONObject tmp = new JSONObject();
			tmp.put("Document", Integer.parseInt(mappings.get(prop.getInt("Document")
					- 1)));
			tmp.put("Proportion", prop.getFloat("Proportion"));
			topicInfo.getJSONObject(topicNum - 1).getJSONArray("DocTopicProportion")
			.put(tmp);
		}
	}
	
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
		Driver.getAndInsertDocTopicProp(args[3], topicInfo, mappings, 0.1f);
		JSONObject stmInfo = new JSONObject();
		stmInfo.put("DocID", 1);
		stmInfo.put("Predicate", "Tillerson");
		stmInfo.put("NumTopics", topicInfo.length());
		stmInfo.put("Topics", topicInfo);
		stmInfo.put("TopicCorr", topicCorr);
		stmInfo.put("TopicNumAuto", true);
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(args[5]))) {
			writer.write(stmInfo.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}