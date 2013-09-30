package edu.cmu.geoparser.resource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import edu.cmu.geoparser.common.Util;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;

public class SpanishInitialResources {
	private static SpanishInitialResources _instance = new SpanishInitialResources();

	private final static Set<String> stops = Util
			.getFileText("resources.spanish/stopwords.txt");

	private final static List<String> webWords = Arrays.asList("http", "www",
			".com", ".org", ".gov", ".net", ".ly", ".me");

	private static List<String> puncs = Arrays.asList(".", "!", "?", ",", ";",
			"(", ")", "[", "]", "{", "}", ":");

	/* spanish prepostion */
	private static Set<String> preps = Util
			.getFileText("resources.spanish/prepositions.txt");
	private static Set<String> directions = Util
			.getFileText("resources.spanish/directions.txt");
	private static Set<String> dict = getDictionary("resources.spanish/dict.txt");
	
	private final static String classifierPath = "mldata/model/NER/es/Stanford-ner-model-es.ser.gz";
	private final static AbstractSequenceClassifier<?> classifier = CRFClassifier.getClassifierNoExceptions(classifierPath);

	private final static Set<String> streetsufs = Util
			.getFileText("resources.spanish/streetsufixes.txt");

	private final static Set<String> buildings = Util
			.getFileText("resources.spanish/buildings.txt");
	private final static Set<String> distances = Util
			.getFileText("resources.spanish/distances.txt");

	/**
	 * Static method to get in instance of this class
	 * 
	 * @return
	 */
	public static SpanishInitialResources getInstance() {
		return _instance;
	}

	public SpanishInitialResources() {
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
	private static HashSet<String> getDictionary(String dictFile) {
		HashSet<String> lines = new HashSet<String>();

		Scanner scanner = null;
		try {
			scanner = new Scanner(new FileInputStream(dictFile));
		} catch (FileNotFoundException e1) {
			System.out.println("File not found.");
		}

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.length() > 1 && line.length() < 6) {
				line = line.toLowerCase().trim();

				lines.add(line);
			} else
				continue;
		}
		scanner.close();
		return lines;
	}

	public static boolean isBuilding(String word) {
		return buildings.contains(word);
	}

	public static Set<String> getBuildingWords() {
		return buildings;
	}

	public static boolean isDistanceWord(String word) {
		return distances.contains(word);
	}

//	public static boolean isSaintName(String word) {
//		return saintnames.contains(word);
//	}

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

	/*****************************************
	 * Stop Words
	 *****************************************/

	/**
	 * Check if a word is stop word.
	 * 
	 * @param word
	 * @return
	 */
	public static boolean isStopWords(String word) {
		return stops.contains(word);
	}

	/**
	 * Return stop words list.
	 * 
	 * @return
	 */
	public static Set<String> getStopWords() {
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
	 * Spanish Dictionary
	 *****************************************/
	public static Set<String> getDict() {
		return dict;
	}

	public static boolean isInDictionary(String word) {
		return dict.contains(word);
	}

//	/*****************************************
//	 * Common Abbreviations
//	 *****************************************/
//	public static Set<String> getCommonAbbreviation() {
//		return commonAbbreviation;
//	}
//
//	public static boolean isCommonAbbreviation(String word) {
//		return commonAbbreviation.contains(word);
//	}
//
//	/*****************************************
//	 * Twittionary
//	 *****************************************/
//
//	public static Set<String> getTwittionary() {
//		return twittionary;
//	}
//
//	public static boolean isTwittionaryWord(String word) {
//		return twittionary.contains(word);
//	}

}
