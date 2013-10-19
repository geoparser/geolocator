package edu.cmu.geoparser.parser.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.lucene.document.Document;

import edu.cmu.geoparser.model.Tweet;
import edu.cmu.geoparser.resource.trie.IndexSupportedTrie;

public class PostProcessing {
	static StringBuilder sbContent;
	static StringBuilder sbGeoPair;
	static StringBuilder sbGeoCounty;

	public static String getLocationString() {
		return sbContent.toString();
	}

	public static String getGeoCode() {
		return sbGeoPair.toString();
	}

	public static String getGeoCounty() {
		return sbGeoCounty.toString();
	}

	static HashSet<String> matchedHM = new HashSet<String>();
	static ArrayList<Document> res;
	static Document d;
	static Document md;

	public static void getGeoInfo(IndexSupportedTrie trie, Tweet tweet, boolean enableGeoInfo) throws IOException {
		// TODO add POI index mapping
		if (!enableGeoInfo)
			return;
		List<String> matches = tweet.getMatches();

		if (matches != null && matches.size() >= 0) {

			sbContent = new StringBuilder();
			sbGeoPair = new StringBuilder();
			sbGeoCounty = new StringBuilder();
			matches = tweet.getMatches();
			 System.out.println("Matches before generating the geocode:"
			 + matches.toString());
			// if the length is longer than 4 words, then remove this.
			for (String match : matches) {
				if (match.split("[ ]").length > 4)
					continue;
				// System.out.println("match is:"+match);
				res = trie.search(match, true);
				if (res == null){
					System.out.println("Not in the gaz index!");
					continue;}
				sbContent.append(match).append("-:-");

				try {
					md = res.get(0);
				} catch (Exception e) {
					System.out.println("Not in the index!");
					continue;
				}
				for (int i = 0; i < res.size(); i++) {
					d = res.get(i);
					sbGeoPair.append(match).append("-:-").append(d.get("POPULATION")).append("::")
							.append(d.get("COUNTRYSTATE")).append("::").append(d.get("LONGTITUDE")).append("::")
							.append(d.get("LATITUDE")).append("\n");
					if (Long.parseLong(md.get("POPULATION")) < Long.parseLong(d.get("POPULATION")))
						md = d;
				}
				sbGeoPair.append("\nMost Population City:\n{").append(match).append(" -:- ")
						.append(md.get("POPULATION")).append("::").append(md.get("COUNTRYSTATE")).append("::")
						.append(md.get("LONGTITUDE")).append("::").append(md.get("LATITUDE")).append("}");
			}
			// Determine Whether System will break
		}
	}

	public static void fuseAndDeleteEnMatches(Tweet tweet, List<String> tokens, List<String> postags) {
		// TODO Auto-generated method stub
		List<String> matches = tweet.getMatches();
		HashSet<String> uniquematch = new HashSet<String>(3);
		for (int i = 0; i < matches.size(); i++) {
			uniquematch.add(matches.get(i).substring(2, matches.get(i).length() - 2));
		}
		int toklength = postags.size();
		if (postags.get(toklength - 1).equals("U") && postags.get(toklength - 2).equals("~"))
			uniquematch.remove(tokens.get(tokens.size() - 3));
		tweet.setMatches(new ArrayList<String>(uniquematch));
	}

	public static void fuseAndDeleteEsMatches(Tweet tweet) {
		// TODO Auto-generated method stub
		List<String> matches = tweet.getMatches();
		HashSet<String> uniquematch = new HashSet<String>(3);
		for (int i = 0; i < matches.size(); i++) {
			uniquematch.add(matches.get(i).substring(2, matches.get(i).length() - 2));
		}
	}


}
