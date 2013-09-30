package edu.cmu.geoparser.nlp.ner.FeatureExtractor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.lucene.queryParser.ParseException;

import com.csvreader.CsvReader;

import edu.cmu.geoparser.common.StringUtil;
import edu.cmu.geoparser.io.GetWriter;
import edu.cmu.geoparser.nlp.Lemmatizer;
import edu.cmu.geoparser.nlp.POSTagger;
import edu.cmu.geoparser.nlp.lemma.AnnaLemmatizer;
import edu.cmu.geoparser.nlp.lemma.UWMorphaStemmer;
import edu.cmu.geoparser.nlp.pos.ENTweetPOSTagger;
import edu.cmu.geoparser.nlp.pos.ESAnnaPOSTagger;
import edu.cmu.geoparser.nlp.tokenizer.EuroLangTwokenizer;
import edu.cmu.geoparser.parser.utils.ParserUtils;
import edu.cmu.geoparser.resource.dictionary.Dictionary;
import edu.cmu.geoparser.resource.dictionary.Dictionary.DicType;
import edu.cmu.geoparser.resource.trie.IndexSupportedTrie;
import edu.cmu.geoparser.resource.trie.Trie;
import edu.cmu.minorthird.classify.Feature;

public class FeatureGenerator {

	HashSet<String> preposition, countries;
	Dictionary prepdict, countrydict;
	EuroLangTwokenizer tokenizer;
	Lemmatizer lemmatizer;
	POSTagger postagger;
	IndexSupportedTrie trie;

	@SuppressWarnings("unchecked")
	public FeatureGenerator(String language, IndexSupportedTrie tr, String resourcepath) {
		// initialize dictionary to lookup.
		// "geoNames.com/allCountries.txt"
		trie = tr;

		if (language.equals("en") || language.equals("es"))
			tokenizer = new EuroLangTwokenizer();
		else
			System.err.println("No proper tokenizer found for this language.");

		if (language.equals("en"))
			//lemmatizer = new AnnaLemmatizer(resourcepath + language+ "/CoNLL2009-ST-English-ALL.anna-3.3.lemmatizer.model");
			lemmatizer = new UWMorphaStemmer();
		else if (language.equals("es"))
			lemmatizer = new AnnaLemmatizer(resourcepath + language
					+ "/CoNLL2009-ST-Spanish-ALL.anna-3.3.lemmatizer.model");
		if (language.equals("en"))
			postagger = new ENTweetPOSTagger(resourcepath + language + "/model.20120919");
		else if (language.equals("es"))
			postagger = new ESAnnaPOSTagger(resourcepath + language
					+ "/CoNLL2009-ST-Spanish-ALL.anna-3.3.postagger.model");
		try {
			prepdict = Dictionary.getSetFromListFile(resourcepath + language + "/prepositions.txt", true, true);
			preposition = (HashSet<String>) prepdict.getDic(DicType.SET);
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	static int statstreet = 0;
	static int statbuilding = 0;
	static int stattoponym = 0;
	static int statabbr = 0, statadj = 0;

	String tweet;

	public static void main(String argv[]) throws IOException, InterruptedException, ParseException {
//		argv[0] = "es";
//		argv[1] = "GeoNames/allCountries.txt";
		argv[2] = "res/";

		String featuretype = "-3tok*.3pres.2cap.3caps.1pos.2pos.1gaz.1gazs.1cty.1ctys.-3-1prep";
		// ="ct-only";
		// ="allbutpos";

		FeatureGenerator fgen = new FeatureGenerator(argv[0], new IndexSupportedTrie(argv[1], "GazIndex/",false, false), argv[2]);
		String traintest[] = new String[] { "train", "test" };
		for (int tt = 0; tt < 2; tt++) {
			CsvReader entries = new CsvReader("trainingdata/" + argv[0] + "NER/" + traintest[tt] + "/raw.csv", ',',
					Charset.forName("utf-8"));

			BufferedWriter fwriter = GetWriter.getFileWriter("trainingdata/" + argv[0] + "NER/" + traintest[tt] + "/"
					+ featuretype + "-features.txt");

			entries.readHeaders();

			int i = 1;
			int counttweets = 0;
			while (entries.readRecord()) {
				i++;
				if (i % 200 == 0)
					System.out.println(i + " ");

				String tag = entries.get(entries.getHeaders()[0]);

				if (tag.equals("") == false)
					continue;// filter language
				else
					counttweets++; // count spanish tweets

				String tweet = entries.get(entries.getHeaders()[1]);// System.out.println(tweet);
				tweet = tweet.replace("-", " - ");
				String street = entries.get(entries.getHeaders()[2]);
				street = street.replace("-", " - ");
				String building = entries.get(entries.getHeaders()[3]);
				building = building.replace("-", " - ");
				String toponym = entries.get(entries.getHeaders()[4]);
				toponym = toponym.replace("-", " - ");
				String abbr = entries.get(entries.getHeaders()[5]);
				abbr = abbr.replace("-", " - ");
				// String locadj = entries.get(entries.getHeaders()[6]);
				// locadj = locadj.replace("-", " - ");

				String[] t_tweet = (EuroLangTwokenizer.tokenize(tweet)).toArray(new String[] {});
				String[] t_street = street.trim().split(",");// without
												// trimming
				String[] t_building = building.trim().split(",");// without
													// trimming
				String[] t_toponym = toponym.trim().split(",");// without
												// trimming
				String[] t_abbr = abbr.trim().split(",");// without
											// trimming
				// String[] t_locadj = locadj.trim().split(",");

				if (!EmptyArray(t_street))
					statstreet += t_street.length;
				if (!EmptyArray(t_building))
					statbuilding += t_building.length;
				if (!EmptyArray(t_toponym))
					stattoponym += t_toponym.length;
				if (!EmptyArray(t_abbr))
					statabbr += t_abbr.length;
				// if (!EmptyArray(t_locadj))
				// statadj += t_locadj.length;
				if (t_tweet.length == 0)
					continue;

				// labeling
				HashMap<Integer, String> f_loc = safeTag(t_tweet, t_street, t_building, t_toponym, t_abbr);

				List<Feature[]> tweetfeatures = fgen.extractFeature(t_tweet);

				for (int j = 0; j < tweetfeatures.size(); j++) {
					initialFeatureWriter();
					for (Feature f : tweetfeatures.get(j)) {
						append(f.toString());
						// System.out.println(f.toString());
					}

					// location class.
					String loctag = "O";
					if (f_loc.containsKey(j))
						loctag = f_loc.get(j);
					// loctag = "LOC";
					append(loctag);
					fwriter.write(emit());
				}

				fwriter.write("\n");
			}
			System.out.println(statstreet);
			System.out.println(statbuilding);
			System.out.println(stattoponym);
			System.out.println(statabbr);
			// System.out.println(statadj);
			System.out.println();
			System.out.println(counttweets);

			fwriter.close();
			entries.close();
		}
	}

	/**
	 * MAIN FUNCTION FOR EXTRACTIN FEATURES
	 * 
	 * @param t_tweet
	 * @param trie
	 * @param postags
	 * @return FEATURE LISTS
	 */
	public List<Feature[]> extractFeature(String[] t_tweet) {

		List<List<Feature>> instances = new ArrayList<List<Feature>>(t_tweet.length);
		List<Feature> f = new ArrayList<Feature>();

		// normalize tweet
		String[] norm_tweet = new String[t_tweet.length];
		for (int i = 0; i < norm_tweet.length; i++)
			norm_tweet[i] = StringUtil.getDeAccentLoweredString(tokentype(t_tweet[i]));

		// lemmatize
		String[] lemmat_tweet = lemmatizer.lemmatize(Arrays.asList(norm_tweet)).toArray(new String[] {});

		// pos tagging
		List<String> postags = postagger.tag(Arrays.asList(t_tweet));
		String[] f_pos = postags.toArray(new String[] {});

		boolean[] f_gaz = gazTag(norm_tweet, trie);
		boolean[] f_country = countryTag(norm_tweet);

		for (int i = 0; i < t_tweet.length; i++) {
			// clear feature list for this loop
			f = new ArrayList<Feature>();
			// /////////////////////////////// MORPH FEATURES
			genTokenFeatures(f, lemmat_tweet, i);
			genCapFeatures(f, t_tweet, i);
			// ////////////////////////////// SEMANTIC FEATURES
			genPosFeatures(f, f_pos, i);
			// ////////////////////////////////// GAZ AND DICT LOOK UP
			genGazFeatures(f, f_gaz, i);
			// f7: STREET SUFFIX
			// f8 PREPOSITION
			genPrepFeatures(f, t_tweet, i, preposition);
			// f9: COUNTRY
			genCountryFeatures(f, f_country, i);// f10: DIRECTION
			// f10 directions
			// f11: DISTANCE
			// f12: STOPWORDS
			// f13: BUILDING
			instances.add(f);
		}

		// convert array to output format.
		ArrayList<Feature[]> newinstances = new ArrayList<Feature[]>();
		for (int i1 = 0; i1 < instances.size(); i1++) {
			newinstances.add(instances.get(i1).toArray(new Feature[] {}));
		}
		return newinstances;
	}

	// //////////////////////////////////////////////////////////////////////////////////
	// FEATURE EXTRACTORS
	// //////////////////////////////////////////////
	/**
	 * PREPOSITION OR NOT.
	 * 
	 * INPUT RAW TOKENS OUTPUT BINARY VALUE YES OR NO.
	 * 
	 * @param f
	 * @param t_tweet
	 * @param i
	 */
	// prep-2.prep-1
	private static void genPrepFeatures(List<Feature> f, String[] t_tweet, int i, HashSet<String> preposition) {
		if (i - 3 >= 0)
			addFeature(f, "-3_cont_prep_" + preposition.contains(TOKLW(t_tweet[i - 3])));
		if (i - 2 >= 0)
			addFeature(f, "-2_cont_prep_" + preposition.contains(TOKLW(t_tweet[i - 2])));
		if (i - 1 >= 0)
			addFeature(f, "-1_cont_prep_" + preposition.contains(TOKLW(t_tweet[i - 1])));
	}

	/**
	 * COUNTRY GAZ EXISTENCE
	 * 
	 * @param f
	 * @param f_country
	 * @param i
	 */
	// country.-1.+1.seq-1+1
	private static void genCountryFeatures(List<Feature> f, boolean[] f_country, int i) {
		addFeature(f, "0_cont_country_" + f_country[i]);
		String countryseq = "";
		if (i - 1 >= 0) {
			addFeature(f, "-1_cont_country_" + f_country[i - 1]);
			countryseq += f_country[i - 1] + "::";
		}
		if (i + 1 <= f_country.length - 1) {
			addFeature(f, "+1_cont_country_" + f_country[i + 1]);
			countryseq += f_country[i + 1];
		}
		addFeature(f, "-+_cont_country_seq_" + countryseq);

	}

	/**
	 * GAZ EXISTENCE
	 * 
	 * @param f
	 * @param f_gaz
	 * @param i
	 */
	// gaz.-1.+1.seq-1+1
	private static void genGazFeatures(List<Feature> f, boolean[] f_gaz, int i) {

		// CURRENT WORD
		addFeature(f, "0_cont_gaz_" + f_gaz[i]);

		String gazseq = "";
		if (i - 1 >= 0) {
			addFeature(f, "-1_cont_gaz_" + f_gaz[i - 1]);
			gazseq += f_gaz[i - 1] + "::";
		}
		if (i + 1 <= f_gaz.length - 1) {
			addFeature(f, "+1_cont_gaz_" + f_gaz[i + 1]);
			gazseq += f_gaz[i + 1];
		}
		addFeature(f, "-+_cont_gaz_seq_" + gazseq);
	}

	/**
	 * POINT POS FOR EACH SURROUNDING WORD POS SEQUENCE
	 * 
	 * @param f
	 * @param f_pos
	 * @param i
	 */
	// pos.seq-3-1.seq+1+3
	private static void genPosFeatures(List<Feature> f, String[] f_pos, int i) {
		int t_length = f_pos.length;
		// f5 PART OF SPEECH

		// CURRENT WORD
		addFeature(f, "0_pos_" + f_pos[i]);

		String posleft = "", posright = "";
		if (i - 4 >= 0) {
			// addFeature(f, "-4.pos." + f_pos[i - 4]);
			// posleft += f_pos[i - 4];
		}
		if (i - 3 >= 0) {
			// addFeature(f, "-3.pos." + f_pos[i - 3]);
			// posleft += f_pos[i - 3];
		}
		if (i - 2 >= 0) {
			// addFeature(f, "-2_pos_" + f_pos[i - 2]);
			posleft += f_pos[i - 2];
		}
		if (i - 1 >= 0) {
			addFeature(f, "-1_pos_" + f_pos[i - 1]);
			posleft += f_pos[i - 1];
		}
		if (i + 1 <= t_length - 1) {
			addFeature(f, "+1_pos_" + f_pos[i + 1]);
			posright += f_pos[i + 1];
		}
		if (i + 2 <= t_length - 1) {
			// addFeature(f, "+2_pos_" + f_pos[i + 2]);
			posright += f_pos[i + 2];
		}
		if (i + 3 <= t_length - 1) {
			// addFeature(f, "+3.pos." + f_pos[i + 3]);
			// posright += f_pos[i + 3];
		}
		if (i + 4 <= t_length - 1) {
			// addFeature(f, "+4.pos." + f_pos[i + 4]);
			// posright += f_pos[i + 4];
		}
		addFeature(f, "-pos_seq_" + posleft);
		addFeature(f, "+pos_seq_" + posright);

	}

	/**
	 * CAPITALIZATION SEQUENCE POINT CAPs OF SURROUNDING WORDS CAP SEQUENCEs
	 * 
	 * @param f
	 * @param t_tweet
	 * @param i
	 */
	// cap.seq-3-1.seq+1+3
	private static void genCapFeatures(List<Feature> f, String[] t_tweet, int i) {
		int t_length = t_tweet.length;

		// CURRENT WORD
		addFeature(f, "0_mph_cap_" + MPHCAP(t_tweet[i]));

		String left = "", right = "";
		if (i - 4 >= 0) {
			// addFeature(f, "-4_mph_cap_" + MPHCAP(t_tweet[i - 4]));
			// left += MPHCAP(t_tweet[i - 4]);
		}
		if (i - 3 >= 0) {
			addFeature(f, "-3_mph_cap_" + MPHCAP(t_tweet[i - 3]));
			// left += MPHCAP(t_tweet[i - 3]);
		}
		if (i - 2 >= 0) {
			addFeature(f, "-2_mph_cap_" + MPHCAP(t_tweet[i - 2]));
			left += MPHCAP(t_tweet[i - 2]);
		}
		if (i - 1 >= 0) {
			addFeature(f, "-1_mph_cap_" + MPHCAP(t_tweet[i - 1]));
			left += MPHCAP(t_tweet[i - 1]) + "::";
		}
		if (i + 1 <= t_length - 1) {
			addFeature(f, "+1_mph_cap_" + MPHCAP(t_tweet[i + 1]));
			right += MPHCAP(t_tweet[i + 1]);
		}
		if (i + 2 <= t_length - 1) {
			addFeature(f, "+2_mph_cap_" + MPHCAP(t_tweet[i + 2]));
			right += MPHCAP(t_tweet[i + 2]);
		}
		if (i + 3 <= t_length - 1) {
			addFeature(f, "+3_mph_cap_" + MPHCAP(t_tweet[i + 3]));
			// right += MPHCAP(t_tweet[i + 3]);
		}
		if (i + 4 <= t_length - 1) {
			// addFeature(f, "+4_mph_cap_" + MPHCAP(t_tweet[i + 4]));
			// right += MPHCAP(t_tweet[i + 4]);
		}
		addFeature(f, "-_mph_cap_seq_" + left);
		addFeature(f, "+_mph_cap_seq_" + right);
		addFeature(f, "-+_mph_cap_seq_" + left + right);

	}

	/**
	 * CONTEXT WORD (LEMMA) EXISTENCE The bag of words feature, and position
	 * appearance feature together. 1. Each lemma is added in bag of context
	 * words 2. Each position has an presence feature for determining the
	 * existence of the window position.
	 * 
	 * @param f
	 *              : Feature list
	 * @param lemmat_tweet
	 *              : lemmas of the tweet,
	 * @param i
	 *              : position of the current word
	 */
	// tok.-1.+1.pres-4+4.
	private static void genTokenFeatures(List<Feature> f, String[] lemmat_tweet, int i) {

		// CURRENT TOKEN
		addFeature(f, "0_tok_lw_" + TOKLW(lemmat_tweet[i]));
		if (i - 4 >= 0) {
			// addFeature(f, "-_tok_lw_" + TOKLW(lemmat_tweet[i - 4]));
			addFeature(f, "-4_tok_present_1");
		} else {
			addFeature(f, "-4_tok_present_0");
		}
		if (i - 3 >= 0) {
			addFeature(f, "-_tok_lw_" + TOKLW(lemmat_tweet[i - 3]));
			addFeature(f, "-3_tok_present_1");
		} else {
			addFeature(f, "-3_tok_present_0");
		}
		// this feature has changed into bag of window words feature,
		// which is less specific than just the position.
		if (i - 2 >= 0) {
			addFeature(f, "-2_tok_lw_" + TOKLW(lemmat_tweet[i - 2]));
			addFeature(f, "-2_tok_present_1");
		} else {
			addFeature(f, "-2_tok_present_0");
		}
		if (i - 1 >= 0) {
			addFeature(f, "-1_tok_lw_" + TOKLW(lemmat_tweet[i - 1]));
			addFeature(f, "-1_tok_present_1");
		} else {
			addFeature(f, "-1_tok_present_0");
		}
		if (i + 1 <= lemmat_tweet.length - 1) {
			addFeature(f, "+1_tok_lw_" + TOKLW(lemmat_tweet[i + 1]));
			addFeature(f, "+1_tok_present_1");
		} else {
			addFeature(f, "+1_tok_present_0");
		}
		if (i + 2 <= lemmat_tweet.length - 1) {
			addFeature(f, "+2_tok_lw_" + TOKLW(lemmat_tweet[i + 2]));
			addFeature(f, "+2_tok_present_1");
		} else {
			addFeature(f, "+2_tok_present_0");
		}
		if (i + 3 <= lemmat_tweet.length - 1) {
			addFeature(f, "+_tok_lw_" + TOKLW(lemmat_tweet[i + 3]));
			addFeature(f, "+3_tok_present_1");
		} else {
			addFeature(f, "+3_tok_present_0");
		}
		if (i + 4 <= lemmat_tweet.length - 1) {
			// addFeature(f, "+_tok_lw_" + TOKLW(lemmat_tweet[i + 4]));
			addFeature(f, "+4_tok_present_1");
		} else {
			addFeature(f, "+4_tok_present_0");
		}
	}

	/**
	 * CAPITALIZATION
	 * 
	 * @param string
	 * @return boolean
	 */
	private static String MPHCAP(String string) {

		boolean a = Character.isUpperCase(string.charAt(0));
		return Boolean.toString(a);
	}

	/**
	 * CONVERT TO LOWER TYPE Input the lemma, 1. Run tokentype() to convert to
	 * token 2. lowercase and deaccent the lemma.
	 * 
	 * @param lemmastring
	 * @return
	 */
	private static String TOKLW(String lemmastring) {

		lemmastring = StringUtil.getDeAccentLoweredString(tokentype(lemmastring));
		return lemmastring;
	}

	/**
	 * CONVERT TO TYPE Naively decide the tweet token type, url, or hashtag,
	 * or metion, or number. Or it's not any of them, just return it's
	 * original string.
	 * 
	 * @param token
	 * @return
	 */
	public static String tokentype(String token) {
		// lower cased word.
		String ltoken = StringUtil.getDeAccentLoweredString(token.trim());

		if (ltoken.startsWith("http:") || ltoken.startsWith("www:")) {
			ltoken = "[http]";
		} else if (ltoken.startsWith("@") || ltoken.startsWith("#")) {
			if (ltoken.length() > 1) {
				ltoken = ltoken.substring(1);
			}
		}
		try {
			Double.parseDouble(ltoken);
			ltoken = "[num]";
		} catch (NumberFormatException e) {
		}

		return ltoken;
	}

	// ////////////////////////////////////////////////////////////////////////////////////
	// GAZ FEATURE HELPER
	// //////////////////////////////////////////////////////////
	/**
	 * GAZ TAGGING BASED ON GREEDY SEARCH. FIND THE LONGEST MATCH STARTING
	 * FROM THE CURRENT WORD
	 * 
	 * @param t_tweet
	 * @param trie
	 * @return
	 */
	private static boolean[] gazTag(String[] t_tweet, Trie trie) {

		boolean[] gaztag = new boolean[t_tweet.length];
		int i = 0;
		while (i < t_tweet.length) {
			String history = "";
			for (int j = i; j < t_tweet.length; j++) {
				history += t_tweet[j];
				if (trie.search(history).startsWith("WL")) {
					for (int k = i; k < j + 1; k++)gaztag[k] = true;
//					gaztag[j]=true;
				}
			}
			i++;
		}
		return gaztag;
	}

	public static void mainpro(String argv[]) {
		String tweet = "He was born in los angeles";
		String[] t_tweet = EuroLangTwokenizer.tokenize(tweet).toArray(new String[] {});
		boolean[] tag = gazTag(t_tweet, new IndexSupportedTrie("GeoNames/SRC_cities1000.txt","GazIndex/", false, false));
		for (boolean t : tag) {
			System.out.print(t + " ");
		}
	}

	/**
	 * COUNTRY TAGGING BASED ON GREEDY SEARCH FIND THE LONGEST MATCH STARTING
	 * FROM THE CURRENT WORD
	 * 
	 * @param t_tweet
	 * @return
	 */
	private static boolean[] countryTag(String[] t_tweet) {

		boolean[] countrytag = new boolean[t_tweet.length];
		int i = 0;
		while (i < t_tweet.length) {
			String history = "";
			for (int j = i; j < t_tweet.length; j++) {
				history += " " + StringUtil.getDeAccentLoweredString(t_tweet[j]);
				// System.out.println(history);
				// System.out.println(ParserUtils.isCountry(history.trim()));
				if (ParserUtils.isCountry(history.trim())) {
					for (int k = i; k < j + 1; k++)
						countrytag[k] = true;
				}
			}
			i++;
		}
		return countrytag;
	}

	/**
	 * HELPER FOR SAFELY TAGGING THE STRINGS
	 * 
	 * @param t_tweet
	 * @param t_street
	 * @param t_building
	 * @param t_toponym
	 * @param t_abbr
	 * @param tk
	 * @return
	 */
	private static HashMap<Integer, String> safeTag(String[] t_tweet, String[] t_street, String[] t_building,
			String[] t_toponym, String[] t_abbr) {
		HashMap<Integer, String> tagresults = new HashMap<Integer, String>();
		if (!EmptyArray(t_toponym)) {
			fillinTag(t_tweet, t_toponym, tagresults, "TP");
		}
		if (!EmptyArray(t_street)) {
			fillinTag(t_tweet, t_street, tagresults, "ST");
		}
		if (!EmptyArray(t_building)) {
			fillinTag(t_tweet, t_building, tagresults, "BD");
		}
		if (!EmptyArray(t_abbr)) {
			fillinTag(t_tweet, t_abbr, tagresults, "AB");
		}
		return tagresults;
	}

	/**
	 * AUXILARY FUNCTION FOR SAFETAG. FILL THE TAG IN t_location INTO THE
	 * tagresults hashmap.
	 * 
	 * @param t_tweet
	 * @param t_street
	 * @param tagresults
	 * @param tk
	 */
	private static void fillinTag(String[] t_tweet, String[] t_location, HashMap<Integer, String> tagresults, String TAG) {

		for (String location : t_location) {

			List<String> loctokens = EuroLangTwokenizer.tokenize(location);
			// if (TAG.equals("AB")) {
			// System.out.println("the original tweet tokenized is : " +
			// Arrays.asList(t_tweet).toString());
			// System.out.println("the location tokenized is :" +
			// loctokens.toString());
			// }
			for (String token : loctokens) {
				boolean have = false;
				String ntoken = StringUtil.getDeAccentLoweredString(token);
				for (int i = 0; i < t_tweet.length; i++) {
					if (StringUtil.getDeAccentLoweredString(
							(t_tweet[i].startsWith("#") && t_tweet[i].length() > 1) ? t_tweet[i].substring(1)
									: t_tweet[i]).equals(ntoken)) {
						tagresults.put(i, TAG);
						have = true;
					}
				}
				if (have == false)
					System.out.println("Don't have the tag: " + token);
			}
		}
		// if(TAG.equals("AB"))
		// System.out.println(tagresults);
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////////////
	// TOOLS
	// //////////////////////////////////
	/**
	 * JUDGE EMPTY OF AN ARRAY.
	 * 
	 * @param array
	 * @return
	 */
	static boolean EmptyArray(String[] array) {
		if (array.length < 2)
			if (array[0].equals(""))
				return true;
		return false;
	}

	// ////////////////////////////////////////////////////////////////////////////////
	// HELPER FOR FEATURE VECTOR
	// /////////////////////////////////////////
	static StringBuilder sb = new StringBuilder();

	/**
	 * helper for building feature vector. sb stores the features on a line,
	 * and this func is used to initialize the sb, aka, clear the builder.
	 */
	private static void initialFeatureWriter() {
		sb = new StringBuilder();
	}

	private static void append(String featurestring) {

		if (sb.length() > 0)
			sb.append("\t");
		sb.append(featurestring);
	}

	static String emit() {
		return sb.append("\n").toString();
	}

	private static void addFeature(List<Feature> features, String string) {

		features.add(new Feature(string));
	}

	// ////////////////////////////////////////////////////////////////////////////////////
	// GETTER AND SETTERS /////

	public HashSet<String> getPreposition() {
		return preposition;
	}

	public void setPreposition(HashSet<String> preposition) {
		this.preposition = preposition;
	}

	public HashSet<String> getCountries() {
		return countries;
	}

	public void setCountries(HashSet<String> countries) {
		this.countries = countries;
	}

	public EuroLangTwokenizer getTokenizer() {
		return tokenizer;
	}

	public void setTokenizer(EuroLangTwokenizer tokenizer) {
		this.tokenizer = tokenizer;
	}

	public Lemmatizer getLemmatizer() {
		return lemmatizer;
	}

	public void setLemmatizer(Lemmatizer lemmatizer) {
		this.lemmatizer = lemmatizer;
	}

	public POSTagger getPostagger() {
		return postagger;
	}

	public void setPostagger(POSTagger postagger) {
		this.postagger = postagger;
	}

	public IndexSupportedTrie getTrie() {
		return trie;
	}

	public void setTrie(IndexSupportedTrie trie) {
		this.trie = trie;
	}

}
