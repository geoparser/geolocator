/**
 * 
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
 * 
 * @author Wei Zhang,  Language Technology Institute, School of Computer Science, Carnegie-Mellon University.
 * email: wei.zhang@cs.cmu.edu
 * 
 */
package edu.cmu.geoparser.Disambiguation;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import org.apache.lucene.document.Document;

import com.cybozu.labs.langdetect.LangDetectException;

import twitter4j.Status;
import edu.cmu.geoparser.Disambiguation.utils.JSON2Tweet;
import edu.cmu.geoparser.model.Tweet;
import edu.cmu.geoparser.nlp.languagedetector.LangDetector;
import edu.cmu.geoparser.nlp.ner.FeatureExtractor.FeatureGenerator;
import edu.cmu.geoparser.parser.english.EnglishParser;
import edu.cmu.geoparser.parser.spanish.SpanishParser;
import edu.cmu.geoparser.parser.utils.ParserUtils;
import edu.cmu.geoparser.resource.Index;
import edu.cmu.geoparser.resource.gazindexing.CollaborativeIndex.CollaborativeIndex;
import edu.cmu.geoparser.resource.trie.IndexSupportedTrie;

public class ContextDisamb {
	double W1 = 1.07411942015214; // feature1: weighted population
	double W2 = 0.133562484069893; // feature2: weighted altnames
	double W3 = 0.130046709351386; // feature3: weighted hierarchy
	double W4 = -0.071519071;// feature4: same country
	double W5 = -0.00044497005999997; // feature5: same state
	double W6 = 0.30495218957606; // feature6: same country (sum of weighted
									// population)
	double W7 = 0.449218432461177; // feature7: same state (sum of weighted
									// population)
	// timezone mapping in geonames
	static HashMap<String, Double> timezoneGeo = new HashMap<String, Double>();
	// timezone mapping in tweets
	static HashMap<String, Double> timezoneTwt = new HashMap<String, Double>();

	HashMap<Integer, Long> matchedPop = new HashMap<Integer, Long>();
	// store the <lat, lon, alt, pop, hie> of each matched candidate
	HashMap<Integer, ArrayList<Integer>> matchedMap = new HashMap<Integer, ArrayList<Integer>>();
	HashMap<Integer, ArrayList<Double>> matchedDouble = new HashMap<Integer, ArrayList<Double>>();
	// count occurance of country
	HashMap<String, Integer> countryMap = new HashMap<String, Integer>();
	double countryCount = 0.0;
	// count occurance of state
	HashMap<String, Integer> stateMap = new HashMap<String, Integer>();
	double stateCount = 0.0;
	HashMap<String, Integer> candHierMap = new HashMap<String, Integer>();
	HashMap<String, Double> candAdmin1Map = new HashMap<String, Double>();
	double candAdmin1Count = 0.0;
	HashMap<String, Double> candAdmin0Map = new HashMap<String, Double>();
	double candAdmin0Count = 0.0;
	HashMap<String, Double> candAdmin1Pop = new HashMap<String, Double>();
	double candAdmin1wCount = 0.0;
	HashMap<String, Double> candAdmin0Pop = new HashMap<String, Double>();
	double candAdmin0wCount = 0.0;
	ArrayList<topo> topoList = new ArrayList<topo>(); // topoList in the
														// paragraph

	HashSet<String> titleCountrySet = new HashSet<String>();
	HashSet<String> titleTopoSet = new HashSet<String>();

	HashMap<String, Double> timezoneMap = new HashMap<String, Double>();

	class topo {
		String n;
		String state;
		double la;
		double lo;

		public topo(String locStr, String state, double latStd, double longStd) {
			// TODO Auto-generated constructor stub
			this.n = locStr;
			this.state = state;
			this.la = latStd;
			this.lo = longStd;
		}

		public topo(String locStr, double latStd, double longStd) {
			// TODO Auto-generated constructor stub
			this.n = locStr;
			this.la = latStd;
			this.lo = longStd;
		}

	}

	// read full JSON file, return the extracted toponyms, along with its most
	// likely geo-coordinates
	public HashMap<String, String[]> extractTopoFromTweets(IndexSupportedTrie topotrie, String file, EnglishParser enparser,
			SpanishParser esparser, LangDetector lang) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String tweet = "";
		while ((tweet = br.readLine()) != null) {
			Status tt = JSON2Tweet.getStatusTweet(tweet);
			String text = null;
			String location = null;
			String time_zone = null;
			String desc = null;
			Double lat = 0.0;
			Double lon = 0.0;
			try {
				// System.out.println(tweet);
				text = tt.getText();
				location = tt.getUser().getLocation();
				time_zone = tt.getUser().getTimeZone();
				desc = tt.getUser().getDescription();
				lat = tt.getGeoLocation().getLatitude();
				lon = tt.getGeoLocation().getLongitude();
				if (text.trim().length() == 0) {
					System.out.println("Empty text");
					continue;
				}
			} catch (Exception e) {
				if (text == null || text.trim().length() == 0) {
					System.out.println("Empty text");
				} else {
					System.out.println("Information in JSON is not Enough!");
				}
				continue;
			}
		}
		return null;
	}

	// read text, return the extracted toponyms, along with its most likely
	// geo-coordinates
	public HashMap<String, String[]> extractTopoFromText(Index topotrie, String text, EnglishParser enparser,
			SpanishParser esparser, LangDetector lang) throws IOException {
		List<String> match = null;
		Tweet t = new Tweet();
		t.setText(text);
		t.setMatches(null);
		String language = null;

		try {
			language = lang.detect(text);
		} catch (LangDetectException e) {
			// if the language is not detectable, use English parser as default.
			match = enparser.parse(t);
		}

		if (language.equals("es"))
			match = esparser.parse(t);
		else
			match = enparser.parse(t);

		if (match == null) {
			System.err.println("No toponyms in text.");
			return null;
		} else {

			if (match.size() == 0) {
				System.err.println("No toponyms in text.");
				return null;
			}
			System.out.println(match);

			HashSet<String> reducedmatch = new HashSet(); // store the parsed
															// toponyms
			for (String s : match)
				reducedmatch.add(s.substring(3, s.length() - 3));

			HashMap<String, String[]> result = returnBestTopo(topotrie, reducedmatch);
			return result;
		}
	}

	// given topos that appear in the same tweet/paragraph/sentence, return
	// their most likely geo-coordinates
	public HashMap<String, String[]> returnBestTopo(Index ci, HashSet<String> topo) {
		HashSet<String> ntopo = new HashSet<String>();
		for (String s : topo)
		{
			if (s.startsWith("#"))
				ntopo.add(s.substring(1));
			else
				ntopo.add(s);
		}
		topo = ntopo;
		HashMap<String, Long> totalpopMap = new HashMap<String, Long>();
		HashMap<String, Integer> totalaltMap = new HashMap<String, Integer>();
		HashMap<String, Integer> totalhierMap = new HashMap<String, Integer>();
		HashMap<String, Integer> hierMap = new HashMap<String, Integer>();
		// assign politically more important locations a higher score for
		// hierarchy
		hierMap.put("PCLI", 5);
		hierMap.put("PCLS", 5);
		hierMap.put("ADM1", 4);
		hierMap.put("PCLIX", 4);
		hierMap.put("PPLC", 4);
		hierMap.put("PPLA", 3);
		hierMap.put("ADM2", 3);
		hierMap.put("PPLA2", 2);
		hierMap.put("ADM3", 2);
		hierMap.put("PPLA3", 1);
		hierMap.put("ADM4", 1);
		hierMap.put("PPL", 1);

		for (String str : topo) {
			ArrayList<Document> d = ci.getDocuments(str);
			if (d == null) {
				//System.out.println("no GPS found for "+str);
				continue;
			}
			HashSet<String> candAdmin0temp = new HashSet<String>();
			HashSet<String> candHiertemp = new HashSet<String>();
			HashSet<String> candAdmin1temp = new HashSet<String>();
			HashMap<String, Long> candAdmin0wtemp = new HashMap<String, Long>();
			HashMap<String, Long> candAdmin1wtemp = new HashMap<String, Long>();
			HashSet<Long> candPop = new HashSet<Long>();
			long totalPop = 0; // total population of all candidates
			int totalAlt = 0;
			int totalHier = 0;

			// find common countries and states (features 4-7)
			for (Document cand : d) {
				try {
					String[] part = cand.get("COUNTRYSTATE").split("_");
					String country = part[0].toLowerCase();
					String state = part[0].toLowerCase() + "_" + part[1].toLowerCase();
					int alt = cand.get("OTHERLANG").split(",").length;
					Long population = Long.parseLong(cand.get("POPULATION"));
					String Hier = "RGN";
					Hier = cand.get("FEATURE").split("_")[1];
					if (Hier.equals("PCLI") || Hier.equals("PCLS")) {
						if (!countryMap.containsKey(country))
							countryMap.put(country, 1);
						else
							countryMap.put(country, countryMap.get(country) + 1);
						countryCount++;
					} else if (Hier.equals("PPLC") || Hier.equals("ADM1") || Hier.equals("PCLIX")) {
						if (!stateMap.containsKey(state))
							stateMap.put(state, 1); // non-ambiguous assumption
						else
							stateMap.put(state, stateMap.get(state) + 1);
						stateCount++;
					}
					// add population counts
					if (!candHiertemp.contains(Hier))
						candHiertemp.add(Hier);
					if (!candAdmin0temp.contains(country)) {
						candAdmin0temp.add(country);
						candAdmin0wtemp.put(country, population);
					} else
						candAdmin0wtemp.put(country, Math.max(population, candAdmin0wtemp.get(country)));
					if (!candAdmin1temp.contains(state)) {
						candAdmin1temp.add(state);
						candAdmin1wtemp.put(state, population);
					} else
						candAdmin1wtemp.put(state, Math.max(population, candAdmin1wtemp.get(state)));
					if (!candPop.contains(population)) { // remove potential
															// duplicates
						candPop.add(population);
						totalPop += population;
						totalAlt += alt;
						try {
							totalHier += hierMap.get(Hier);
						} catch (Exception e) {
						}
					}
				} catch (Exception e) {
					continue;
				}

			}
			totalpopMap.put(str, totalPop);
			totalaltMap.put(str, totalAlt);
			totalhierMap.put(str, totalHier);

			System.out.println(str);
			// combine common countries and states
			for (String cand : candAdmin0temp) {
				if (!candAdmin0Map.containsKey(cand))
					candAdmin0Map.put(cand, 1.0);
				else
					candAdmin0Map.put(cand, candAdmin0Map.get(cand) + 1);
			}
			for (String cand : candAdmin1temp) {
				if (!candAdmin1Map.containsKey(cand))
					candAdmin1Map.put(cand, 1.0);
				else
					candAdmin1Map.put(cand, candAdmin1Map.get(cand) + 1);
			}
			for (Entry<String, Long> entry : candAdmin0wtemp.entrySet()) {
				if (!candAdmin0Pop.containsKey(entry.getKey())) {
					if (totalPop != 0)
						candAdmin0Pop.put(entry.getKey(), entry.getValue() / (double) totalPop);
					else
						candAdmin0Pop.put(entry.getKey(), 0.0);
				}

				else if (totalPop != 0)
					candAdmin0Pop.put(entry.getKey(), candAdmin0Pop.get(entry.getKey()) + entry.getValue() / (double) totalPop);
			}
			for (Entry<String, Long> entry : candAdmin1wtemp.entrySet()) {
				if (!candAdmin1Pop.containsKey(entry.getKey())) {
					if (totalPop != 0)
						candAdmin1Pop.put(entry.getKey(), entry.getValue() / (double) totalPop);
					else
						candAdmin1Pop.put(entry.getKey(), 0.0);
				} else if (totalPop != 0)
					candAdmin1Pop.put(entry.getKey(), candAdmin1Pop.get(entry.getKey()) + entry.getValue() / (double) totalPop);
			}
		}

		topo t = null;
		// find the best candidate

		HashMap<String, String[]> result = new HashMap<String, String[]>();
		for (String str : topo) {
			double max_score = Double.NEGATIVE_INFINITY;
			ArrayList<Document> d= ci.getDocuments(str);
			if (d == null) {
				System.out.println("No GPS for "+ str);
				continue;
			}
			Long totalPop = totalpopMap.get(str);
			int totalAlt = totalaltMap.get(str);
			int totalHier = totalhierMap.get(str);
			if (totalHier == 0) // avoid dividing 0
				totalHier = 1;
			if (totalAlt == 0)
				totalAlt = 1; // use population/(total population of all
								// candidates) as a feature
			if (totalPop == 0)
				totalPop = (long) 1;
			for (Document cand : d) {
				try {
					String id = cand.get("ID");
					String[] part = cand.get("COUNTRYSTATE").split("_");
					String country = part[0].toLowerCase();
					String state = part[0].toLowerCase() + "_" + part[1].toLowerCase();
					Long population = Long.parseLong(cand.get("POPULATION"));
					String Hier = cand.get("FEATURE").split("_")[1];
					double alt = cand.get("OTHERLANG").split(",").length;
					double lat = Float.parseFloat(cand.get("LATITUDE"));
					double lon = Float.parseFloat(cand.get("LONGTITUDE"));
					double sameCountry = 0;
					double sameState = 0;
					double samewCountry = 0;
					double samewState = 0;
					int hie = 0;
					try {
						hie = hierMap.get(Hier);
					} catch (Exception e) {
					}
					if (candAdmin0Map.containsKey(country))
						sameCountry = candAdmin0Map.get(country);
					if (candAdmin1Map.containsKey(state))
						sameState = candAdmin1Map.get(state);
					if (candAdmin0Pop.containsKey(country))
						samewCountry = candAdmin0Pop.get(country);
					if (candAdmin1Pop.containsKey(state))
						samewState = candAdmin1Pop.get(state);
					double f1 = population / (double) totalPop;
					double f2 = alt / (double) totalAlt;
					double f3 = hie / (double) totalHier;
					double f4 = sameCountry;
					double f5 = sameState;
					double f6 = samewCountry;
					double f7 = samewState;
					double score = f1 * W1 + f2 * W2 + f3 * W3 + f4 * W4 + f5 * W5 + f6 * W6 + f7 * W7;
					if (score > max_score) {
						t = new topo(str, state, lat, lon);
						max_score = score;
					}
				} catch (Exception e) {
					continue;
				}
			}
			if (t==null )continue;
			result.put(t.n, new String[] { String.valueOf(t.la), String.valueOf(t.lo), t.state });
		}
		return result;
	}

	public void loadTimezones() {
		// load the timezone mapping in geonames
		try {
			BufferedReader fin = new BufferedReader(new InputStreamReader(new FileInputStream("geoNames.com/timeZones.txt")));
			String line = "";
			fin.readLine();
			while ((line = fin.readLine()) != null) {
				String[] str = line.split("	");
				String loc = str[1];
				Double offset = Double.parseDouble(str[2]);
				timezoneGeo.put(loc, offset);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// load the timezone mapping in twitter
		try {
			BufferedReader fin = new BufferedReader(new InputStreamReader(new FileInputStream("geoNames.com/timezone.csv")));
			String line = "";
			while ((line = fin.readLine()) != null) {
				String[] str = line.split(",");
				String loc = str[0];
				Double offset = Double.parseDouble(str[1]);
				timezoneTwt.put(loc, offset);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 * @throws IOException
	 */

	public static void main(String[] args) throws IOException {

	  Index ci = new CollaborativeIndex().config("GazIndex/StringIndex",
            "GazIndex/InfoIndex", "mmap", "mmap").open();
	  EnglishParser enparser = new EnglishParser("res/", ci, false);
    SpanishParser esparser = new SpanishParser("res/",ci,false);
    ContextDisamb c = new ContextDisamb();
    LangDetector lang = new LangDetector("res/langdetect.profile");

		String text = "Coconuts falling from trees in Hawaii causes 150 deaths a year! United States";
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in, "utf-8"));

		while (true) {

			System.out.print(">");
			text = br.readLine();

			List<String> match = null;
			Tweet t = new Tweet();
			t.setText(text);
			t.setMatches(null);
			String language = null;

			try {
				language = lang.detect(text);
			} catch (LangDetectException e) {
				// if the language is not detectable, use English parser as
				// default.
				match = enparser.parse(t);
			}

			// parse the text.

			if (language.equals("es"))
				match = esparser.parse(t);
			else
				match = enparser.parse(t);

			if (match == null) {
				System.err.println("No toponyms in text.");
				continue;
			} else {

				if (match.size() == 0) {
					System.err.println("No toponyms in text.");
					continue;
				}

				// if matches are found:

				System.out.println(match);

				HashSet<String> reducedmatch = new HashSet<String>();
				for (String s : match)
					reducedmatch.add(s.substring(3, s.length() - 3));

				HashMap<String, String[]> result = c.returnBestTopo(ci, reducedmatch);

				if (result == null)
					System.out.println("No toponyms detected.");
				else {
					System.out.println(text);
					for (String topo : result.keySet())
						System.out.println(topo + ": " + result.get(topo)[2] + " " + result.get(topo)[0] + " "
								+ result.get(topo)[1]);
				}
			}
		}
	}
}
