package edu.cmu.geoparser.Disambiguation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

import org.apache.lucene.document.Document;

import twitter4j.GeoLocation;
import twitter4j.Status;

import com.csvreader.CsvWriter;

import edu.cmu.geoparser.Disambiguation.utils.JSON2Tweet;
import edu.cmu.geoparser.Disambiguation.utils.TimeWindow4Tweets;
import edu.cmu.geoparser.common.CollectionSorting;
import edu.cmu.geoparser.model.Tweet;
import edu.cmu.geoparser.parser.english.EnglishStanfordNERParser;
import edu.cmu.geoparser.parser.english.EnglishRuleSTBDParser;
import edu.cmu.geoparser.parser.english.EnglishRuleToponymParser;
import edu.cmu.geoparser.resource.dictionary.Dictionary;
import edu.cmu.geoparser.resource.dictionary.Dictionary.DicType;
import edu.cmu.geoparser.resource.gazindexing.AbbrMapper;
import edu.cmu.geoparser.resource.trie.IndexSupportedTrie;

public class TopicalDisambiguator {

	static boolean addoriginalstring = false;
	static boolean savespace = true;
	static IndexSupportedTrie ist;
	static Dictionary filter;
	static HashMap<String, String> statemapper;
	static HashMap<String, String> reversestatemapper;
	static HashSet<String> filterset;
	static ArrayList<Document> locationcandidates;
	// static GazResources gazResources = new GazResources("US");
	static EnglishStanfordNERParser enp;
	static EnglishRuleSTBDParser esp;
	static EnglishRuleToponymParser etp;

	static {
		System.out.println("Static method is running!");
		// have to store the original form of locations, for quick check
		HashSet<String> countrycodes = new HashSet<String>();
		countrycodes.add("US");
		ist = new IndexSupportedTrie("geoNames.com/allCountries.txt", savespace, addoriginalstring, countrycodes);

		try {
			// LOAD THE ABBREVIATION MAPPER.
			statemapper = AbbrMapper.load();
			reversestatemapper = AbbrMapper.loadReverse();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.err.println("abbrmapper load failed.");
		}
		try {
			filter = Dictionary.getSetFromListFile("resources.english/filter.txt", true, false);
			filterset = (HashSet<String>) filter.getDic(DicType.SET);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//eap = new EnglishAbbreviationParser();
		enp = new EnglishStanfordNERParser();
		esp = new EnglishRuleSTBDParser(null);
		etp = new EnglishRuleToponymParser(null,null,false);
		// emp = new EnglishMisSpellParser();
		System.out.println("Static method is finished!");
	}

	// if the word is ambiguous, it should return 1
	// if it's not ambiguous, it should return -1;
	// if the word is not found, it should return 0;
	public static int isStrictAmbiguous(String locationphrase) throws IOException {
		return isAmbiguousAndGetCandidates(locationphrase, false);
	}

	public static int isFuzzyAmbiguous(String locationphrase) throws IOException {
		return isAmbiguousAndGetCandidates(locationphrase, true);
	}

	public static int isAmbiguousAndGetCandidates(String locationphrase, boolean fuzzy) throws IOException {
		if (addoriginalstring) {
			String r = ist.searchTrie(locationphrase, fuzzy);
			System.out.println("Determining ambiguity for :" + locationphrase + " " + r);
			if (r == null)
				return 0;
			if (r.split("_").length > 2) {
				locationcandidates = ist.search(locationphrase, fuzzy);
				return 1;
			} else {
				locationcandidates = ist.search(locationphrase, fuzzy);
				return -1;
			}
		} else {
			locationcandidates = ist.search(locationphrase, fuzzy);
			if (locationcandidates == null ||locationcandidates.size()==0)
				return 0;
			else if (locationcandidates.size() > 1)
				return 1;
			else if ( locationcandidates.size()==1)
				return -1;
			else{
				System.err.println("lcationcandidate size error.");
				return 0;
			}
		}
	}

	public static ArrayList<Document> getBasicInformation() {
		return locationcandidates;
	}

	public static String parseEnglishTweet(Tweet tweet) {

		//eap.parse(tweet);
		enp.parse(tweet);
		esp.parse(tweet);
		//etp.parse(ist,  tweet,filterset);
		// emp.parse(tweet, gazResources);
		//PostProcessing.process(tweet);
		return null;
	}

	public static HashSet<String> getParsingResults(Tweet tweet) {
		return new HashSet<String>(tweet.getMatches());
	}

	public static void main(String argv[]) throws IOException {

		// TopicalDisambiguator dis = new TopicalDisambiguator();
		// index path is 2011-09-05.
		// 15 minutes, 10000 max.
		TimeWindow4Tweets tw4t = new TimeWindow4Tweets("/Users/indri/Eclipse_workspace/Research/texasfiretweets/index/2011-09-05", 24 * 60, 50000);
		// use the language detection model to filter the languages
		HashSet<String> langs = new HashSet<String>();
		langs.add("en");
		HashSet<String> keywords = new HashSet<String>();

		keywords.add("wildfire");
		keywords.add("fire");
		tw4t.setSeed(keywords);

		// write to csv file.
		CsvWriter cw = new CsvWriter("output/disambiguator/texaswildfire.txt");
		cw.write("tweet");
		cw.write("locations");
		cw.write("DISAMBIGUATED COUNTRY and STATE");
		cw.write("DISAMBIGUATED LONG.LAT.");
		cw.endRecord();

		Document currentseed;
		Status statustweet;
		String text, userlang, userloc, userdescription, username, usertimezone, urlsuffix;
		GeoLocation tweetgeo;
		Tweet t;
		HashSet<String> parseresults;

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in, "utf-8"));
		
		//varibles in the for loop:
		String[] a = { "burn", "flame", "smoke", "evacuation", "rescue", "insurance", "die", "destroy", "destruction", "evacuation",
				"forest", "damage", "weather", "fighter", "donation", "victim", "emergency", "evacuee", "kill", "shelter", "help",
				"centraltxfires", "txfire", "txfires","wildfire","fire" };
		HashMap<String, Integer> userlocation = new HashMap<String, Integer>();
		int i = 0;
		
		
		while (true) {

			//br.readLine();
			System.err.println("[SEEDING]:");

			currentseed = tw4t.getSearchNextSeed(langs);

			// CREATE CURRENT TWEET FROM STRING.
			statustweet = JSON2Tweet.getStatusTweet(currentseed.get("JSON"));
			if (statustweet == null) {
				System.err.println("Not Available tweet Format. Reseed.");
				continue;
			}
			System.err.println("******[SEED ] " + statustweet.getText());

			// /////////////////
			// PARSING THE CURRENT TWEET TO GET LOCATIONS.
			t = new Tweet();
			t.setText(statustweet.getText());
			//t.setNewText(StringUtil.getNewText(statustweet.getText()));
			TopicalDisambiguator.parseEnglishTweet(t);
			parseresults = TopicalDisambiguator.getParsingResults(t);
			System.err.println("[TWEET TEXT PARSING RESULTS]:" + parseresults);

			if (parseresults == null || parseresults.size() == 0) {
				System.err.println("No LOCATIONS IN THE SEED. RESEED. ");
				// reseed, find the next available tweet.
				continue;
			}
			ArrayList<Document> loccandidates;
			LinkedList<String> timewindow;
			ArrayList<String> contexts = new ArrayList<String>();
			
			for (String location : parseresults) {
				// DETERMINE AMBIGUITY. (Generate candidates by
				// isStricAmbiguous() function.)
				int amb = TopicalDisambiguator.isStrictAmbiguous(location);
				
				// NOT AMBIGUOUS, CONTNINUE TO NEXT ONE.				
				if (amb == -1) {
					System.err.println("This is a Gaz Entry without Ambiguity.");
					Document loc = TopicalDisambiguator.getBasicInformation().get(0);
					// write unambiguous record.
					cw.write(statustweet.getText());
					cw.write(location);
					cw.write(loc.get("COUNTRYSTATE"));
					cw.write(loc.get("LATITUDE") + ":" + loc.get("LONGTITUDE"));
					cw.endRecord();
					System.err.println("RESOLVED GEOLOCATION FOR {" + location + "} :" + loc.get("LATITUDE") + ":" + loc.get("LONGTITUDE"));
					continue;
				} else if (amb == 0) {// IF NOT IN THE GAZ, CONTINUE TO NEXT  LOCATION.

					System.err.println(location + " is not matched in Gaz. No GeoInfo Availalbe.");
					System.err.println("Trying to guess the location using information from the tweet:");

					// AND THE WORDS THAT ARE TOPIC RELATED ARE IMPORTANT!
					for (String as : a)
						keywords.add(as);
					tw4t.generateTimeWindow(langs, keywords, location);
					keywords.clear();

					timewindow = tw4t.getTimeWindow();
					contexts.clear();
					
					userlocation.clear();
					
					int windowiter = 0;
					for (String tweet : timewindow) {
						Status tt = JSON2Tweet.getStatusTweet(tweet);
						contexts.add(tt.getText());
						System.out.println("--------------------window item " + (windowiter++) + "----------------------");
						// System.out.println(tweet);
						System.out.println("[TEXT]	" + tt.getText());
						System.out.println("[GEOLOCATION]	" + tt.getGeoLocation());
						System.out.println("[PLACE]	" + tt.getPlace());
						System.out.println("[USER:LOCATION]	" + tt.getUser().getLocation());
						System.out.println("[USER:TIMEZONE]	" + tt.getUser().getTimeZone());
						System.out.println("[USER:LANGUAGE]	" + tt.getUser().getLang());
						System.out.println("[USER:UTCOFFSET]	" + tt.getUser().getUtcOffset());
						System.out.println("[USER:DESCRIPTION]	" + tt.getUser().getDescription());
						System.out.println("[USER:URL]	" + tt.getUser().getURL());
						// map the word to its original string.
						if(tt.getUser()== null || tt.getUser().getLocation()==null)continue;
						String[] loc = tt.getUser().getLocation().toLowerCase().split("[ ,]");
						if (loc.length == 0)
							continue;
						for (String lword : loc) {
							lword = lword.trim();
							if(lword.length()==0)continue;
							String mappedword = reversestatemapper.containsKey(lword) ? reversestatemapper.get(lword): lword;
							if (userlocation.containsKey(mappedword) == false)
								userlocation.put(mappedword, 1);
							else
								userlocation.put(mappedword, userlocation.get(mappedword) + 1);
						}
					}
					// find the location word that appears most, and look it
					// up in the candidates.
					ArrayList<Entry<String, Integer>> rankedlocation = CollectionSorting.rankIntArray(new ArrayList(userlocation.entrySet()));
					System.err.println("The ranked user location is:\n" + rankedlocation);
					Iterator<Entry<String, Integer>> iterator = rankedlocation.iterator();
					boolean flag = false;
					for (Entry e : rankedlocation) {
						String k = (String) e.getKey();
						
						int judge = TopicalDisambiguator.isStrictAmbiguous(k);
						ArrayList<Document> kinfo = TopicalDisambiguator.getBasicInformation();
						if(kinfo==null)continue;
						for (Document info : kinfo) {
							if (! info.get("POPULATION").equals("0")) {
								System.out.println("Infered place is :"+ info.get("COUNTRYSTATE"));
								cw.write(statustweet.getText());
								cw.write(location);
								cw.write(info.get("COUNTRYSTATE"));
								cw.write(info.get("LATITUDE") + " " + info.get("LONGTITUDE"));
								cw.endRecord();
								flag =true;
								break;
							}
						}
						if(flag ==true)break;
					}
					if (flag == false) {
						System.err.println("No GeoResult for this location.");
						cw.write(statustweet.getText());
						cw.write(location);
						cw.write("");
						cw.write("");
						cw.endRecord();
					}
				} else if (amb == 1) {
					HashMap<String, Document> stateindex = null;

					System.err.println("{" + location + "} IS AMBIGUOUS. RESOLVING:");

					// GET AMBIGUOUS ENTRIES.
					loccandidates = TopicalDisambiguator.getBasicInformation();
					stateindex = new HashMap<String, Document>();
					for (Document cand : loccandidates) {
						System.out.println("CANDIDATE: {" + cand.get("ORIGIN") + "} :" + cand.get("COUNTRYSTATE") + " " + cand.get("TIMEZONE") + " "
								+ cand.get("POPULATION") + "	" + cand.get("LATITUDE") + "	" + cand.get("LONGTITUDE"));
						stateindex.put(cand.get("COUNTRYSTATE").toLowerCase().split("[_]")[1], cand);
					}
					System.out.println("--------------------TWEET FIELD----------------------");
					System.out.println("[TEXT]	" + statustweet.getText());
					System.out.println("[GEOLOCATION]	" + statustweet.getGeoLocation());
					System.out.println("[PLACE]	" + statustweet.getPlace());
					System.out.println("[USER:LOCATION]	" + statustweet.getUser().getLocation());
					System.out.println("[USER:TIMEZONE]	" + statustweet.getUser().getTimeZone());
					System.out.println("[USER:LANGUAGE]	" + statustweet.getUser().getLang());
					System.out.println("[USER:UTCOFFSET]	" + statustweet.getUser().getUtcOffset());
					System.out.println("[USER:DESCRIPTION]	" + statustweet.getUser().getDescription());
					System.out.println("[USER:URL]	" + statustweet.getUser().getURL());
					System.out.println("-----------------TWEET FIELD ENDS-------------------");

					System.err.println(" CREATING TIMEWINDOW FRO THIS LOCATION {" + location + "}, IN THE TIMEFRAME: ");

					// IN THOSE TWEETS, THE TOPIC WORD IS NECESSARY.
					
					// AND THE WORDS THAT ARE TOPIC RELATED ARE IMPORTANT!
					for (String as : a)
						keywords.add(as);
					tw4t.generateTimeWindow(langs, keywords, location);
					keywords.clear();

					timewindow = tw4t.getTimeWindow();
					contexts.clear();
					int windowiter = 0;
					
					userlocation.clear();
					
					for (String tweet : timewindow) {
						Status tt = JSON2Tweet.getStatusTweet(tweet);
						contexts.add(tt.getText());
						System.out.println("--------------------window item " + (windowiter++) + "----------------------");
						// System.out.println(tweet);
						System.out.println("[TEXT]	" + tt.getText());
						System.out.println("[GEOLOCATION]	" + tt.getGeoLocation());
						System.out.println("[PLACE]	" + tt.getPlace());
						System.out.println("[USER:LOCATION]	" + tt.getUser().getLocation());
						System.out.println("[USER:TIMEZONE]	" + tt.getUser().getTimeZone());
						System.out.println("[USER:LANGUAGE]	" + tt.getUser().getLang());
						System.out.println("[USER:UTCOFFSET]	" + tt.getUser().getUtcOffset());
						System.out.println("[USER:DESCRIPTION]	" + tt.getUser().getDescription());
						System.out.println("[USER:URL]	" + tt.getUser().getURL());
						// map the word to its original string.
						if (tt.getUser() == null)continue;
						if (tt.getUser().getLocation()==null)continue;
						String[] loc = tt.getUser().getLocation().toLowerCase().split("[ ,]");
						if (loc.length == 0 || loc ==null)
							continue;
						for (String lword : loc) {
							lword = lword.trim();
							String mappedword = statemapper.containsKey(lword) ? statemapper.get(lword) : lword;
							if (userlocation.containsKey(mappedword) == false)
								userlocation.put(mappedword, 1);
							else
								userlocation.put(mappedword, userlocation.get(mappedword) + 1);
						}
					}
					// find the location word that appears most, and look it
					// up in the candidates.
					ArrayList<Entry<String, Integer>> rankedlocation = CollectionSorting.rankIntArray(new ArrayList(userlocation.entrySet()));
					Iterator<Entry<String, Integer>> iterator = rankedlocation.iterator();
					System.err.println("The ranked user location is:\n" + rankedlocation);

					boolean flag = false;
					for (Entry e : rankedlocation) {
						String k = (String) e.getKey();
						if (stateindex.containsKey(k)) {
							Document geo = stateindex.get(k);
							System.err.println("THE INFERRED PLACE IS:" + geo.get("COUNTRYSTATE"));
							
							cw.write(statustweet.getText());
							cw.write(location);
							cw.write(geo.get("COUNTRYSTATE"));
							cw.write(geo.get("LONGTITUDE")+" "+geo.get("LATITUDE"));
							cw.endRecord();
							flag = true;
							
							break;
						}
					}
					if (flag == false) {
						System.err.println("No GeoResult for this location.");
						//select the city with the most population.
						Document mostpop = null; long temppop=0;
						for (Document cand : loccandidates) {
							if(! (Long.parseLong(cand.get("POPULATION"))<temppop))
								mostpop = cand;
						}
						cw.write(statustweet.getText());
						cw.write(location);
						cw.write(mostpop.get("COUNTRYSTATE"));
						cw.write(mostpop.get("LATITUDE")+ " "+mostpop.get("LONGTITUDE"));
						cw.endRecord();
					}
				}
			}
			//String inputStr = br.readLine();
		}
	}
}
