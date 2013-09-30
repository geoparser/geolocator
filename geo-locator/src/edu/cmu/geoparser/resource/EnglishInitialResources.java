package edu.cmu.geoparser.resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import edu.cmu.geoparser.common.Util;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class EnglishInitialResources {

	private static EnglishInitialResources _instance = new EnglishInitialResources();

	private final static List<String> stops = Arrays.asList("the", "about", "to", "and", "&", "or", "i", "of", "in", "for", "about", "a", "which",
			"what", "why", "how", "when", "my", "mine", "your", "at", "on", "his", "her", "our", "their");
	private final static Set<String> stopwords = Util.getDictionary("resources.english/stopwords.txt");
	private final static List<String> webWords = Arrays.asList("http", "www", ".com", ".org", ".gov", ".net", ".ly", ".me");
	private static List<String> puncs = Arrays.asList(".", "!", "?", ",", ";", "(", ")", "[", "]", "{", "}", ":", "-", "_");
	private static Set<String> preps = Util.getFileText("resources.english/prepositions.txt");
	private static Set<String> directions = Util.getFileText("resources.english/directions.txt");
	private static Set<String> dict = Util.getDictionary("resources.english/words.filtered_SRC1000PlusCountry.txt");
	private static Set<String> commonAbbreviation = Util.getDictionary("resources.english/abbreviation.txt");
	private static Set<String> twittionary = Util.getDictionary("resources.english/twittionary.txt");
	private final static String classifierPath = "res/en/english.all.3class.distsim.crf.ser.gz";
	private final static AbstractSequenceClassifier<?> classifier = CRFClassifier.getClassifierNoExceptions(classifierPath);
	private final static String enpostaggerpath = "resources.english/wsj-0-18-bidirectional-distsim.tagger";
	private final static Set<String> streetsufs = Util.getFileText("resources.english/streetsuffixes.txt");
	private final static Set<String> saintnames = Util.getFileText("resources.english/saintnames.txt");

	private final static Set<String> buildings = Util.getFileText("resources.english/buildings.txt");
	private final static Set<String> distances = Util.getFileText("resources.english/distances.txt");

	/**
	 * Static method to get in instance of this class
	 * 
	 * @return
	 */
	public static EnglishInitialResources getInstance() {
		return _instance;
	}

	public EnglishInitialResources() {
		// public constructor
	}

	/*****************************************
	 * Utility Function
	 *****************************************/

	/**
	 * Return a hash set build from text files.
	 * 
	 * @param dictFile
	 * @return
	 */

	public static boolean isBuilding(String word) {
		return buildings.contains(word);
	}

	public static Set<String> getBuildingWords() {
		return buildings;
	}

	public static boolean isDistanceWord(String word) {
		return distances.contains(word);
	}

	public static boolean isSaintName(String word) {
		return saintnames.contains(word);
	}

	public static boolean isStreetSuffix(String word) {
		return streetsufs.contains(word);
	}

	/*****************************************
	 * Web Words
	 *****************************************/

	public static boolean isWebWords(String word) {
		return webWords.contains(word);
	}

	/**
	 * Get a list of web words.
	 * 
	 * @return
	 */
	public static List<String> getWebwords() {
		return webWords;
	}

	/*****************************************
	 * Punctuation
	 ****************************************/

	public static List<String> getPunctuations() {
		
		return puncs;
	}

	public static List<String> getPuncs() {
		return puncs;
	}

	/*****************************************
	 * Classifier
	 ****************************************/

	public static AbstractSequenceClassifier<?> getClassifier() {
		return classifier;
	}

	public static MaxentTagger getEnPOSTagger() {
		return new MaxentTagger(enpostaggerpath);
	}

	/*****************************************
	 * Stop Words
	 *****************************************/

	/**
	 * Check if a word is stop word.
	 * 
	 * @param word
	 * @return
	 */
	public static boolean isInShortStopWordList(String word) {
		return stops.contains(word);
	}
	
	public static boolean isInStopWordsFile(String word){
		return stopwords.contains(word);
	}

	/**
	 * Return stop words list.
	 * 
	 * @return
	 */
	public static List<String> getStopWords() {
		return stops;
	}

	/*****************************************
	 * Preposition words
	 *****************************************/

	public static Set<String> getPreps() {
		return preps;
	}

	public static boolean isPreposition(String word) {
		return preps.contains(word);
	}

	/*****************************************
	 * Direction Words
	 *****************************************/

	public static Set<String> getDirections() {
		return directions;
	}

	public static boolean isDirections(String word) {
		return directions.contains(word);
	}

	/*****************************************
	 * English Dictionary
	 *****************************************/
	public static Set<String> getDict() {
		return dict;
	}

	public static boolean isInDictionary(String word) {
		return dict.contains(word);
	}

	/*****************************************
	 * Common Abbreviations
	 *****************************************/
	public static Set<String> getCommonAbbreviation() {
		return commonAbbreviation;
	}

	public static boolean isCommonAbbreviation(String word) {
		return commonAbbreviation.contains(word);
	}

	/*****************************************
	 * Twittionary
	 *****************************************/

	public static Set<String> getTwittionary() {
		return twittionary;
	}

	public static boolean isTwittionaryWord(String word) {
		return twittionary.contains(word);
	}

}
