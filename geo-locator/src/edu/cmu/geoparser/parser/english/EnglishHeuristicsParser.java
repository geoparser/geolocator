package edu.cmu.geoparser.parser.english;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.cmu.geoparser.model.Tweet;
import edu.cmu.geoparser.parser.utils.ParserUtils;
import edu.cmu.geoparser.resource.EnglishInitialResources;

public class EnglishHeuristicsParser {
	
	/**
	 * Default protected constructor
	 */
	public EnglishHeuristicsParser() {

	}
	

	String dirID = "of";
	List<String> conjs = Arrays.asList("and", "&", "or");

	// Heuristics Analysis of Geo-Parsing
	public void parse(Tweet tweet) {


		// get original tweet text
		String tweetText = tweet.getNewText();
		// to lower case
		String lowerTweet = tweetText.toLowerCase();
		
		String[] tweetWordsArr = lowerTweet.split("[ ]+");
		List<String> tweetWords = Arrays.asList(tweetWordsArr);
		Arrays.asList(tweetWordsArr);

		int numWords = tweetWords.size();
		
		List<String> matches = tweet.getMatches();
		if(matches == null){
			matches = new ArrayList<String>();
		}
		tweet.setMatches(matches);
		
		// find something of form "x mi south of y"
		int startIndex = -1;
		int endIndex = -1;
		for (int i = 0; i < numWords; i++) {

			String word = tweetWords.get(i);
			if (EnglishInitialResources.isDirections(word)) {

				if (i < numWords - 1 && tweetWords.get(i + 1).equals(dirID)) {
					if (i > numWords - 2) {
						continue;
					}

					// south of it should be avoid
					if (EnglishInitialResources.isInShortStopWordList(tweetWords.get(i + 2))) {
						continue;
					}
					// set end boundary
					endIndex = i + 2;

					// now find start boundary
					if (i == 0) {
						startIndex = i;
					} else if (i > 1) {
						// x mi south of y
						if (EnglishInitialResources.isDistanceWord(tweetWords.get(i - 1)))
							startIndex = i - 2;
						// or more complicated x mi south of y
						else if (ParserUtils.hasNum(tweetWords.get(i - 1))) {
							String tmpword = tweetWords.get(i - 1);
							tmpword = tmpword.replaceAll("[0-9]*", "");
							if (EnglishInitialResources.isDistanceWord(tmpword))
								startIndex = i - 1;
						}
					}

					if (startIndex > 0 && endIndex >= 0) {

						StringBuilder builder = new StringBuilder();
						for (int x = startIndex; x <= endIndex; x++) {
							builder.append(tweetWords.get(x));
							if (i < endIndex)
								builder.append(" ");
						}
						matches.add("h{" + builder.toString() + "}h");
					}
				}
			}
		}

		// find something of form "x _ y buildings"
		int buildingsIndex = tweetWords.indexOf("buildings");

		if (buildingsIndex > 2) {
			if (conjs.contains(tweetWords.get(buildingsIndex - 2))) { // _ is a
																		// conjunction
				matches.add("h{"
						+ ParserUtils.getString(tweetWords, buildingsIndex - 3,
								buildingsIndex) + "}h");
			} else if (conjs.contains(tweetWords.get(buildingsIndex - 3))) {
				// find something of form "a b *conj* x y buildings"
				boolean add = true;
				String match = "";
				if (buildingsIndex > 4) {
					match = ParserUtils.getString(tweetWords, buildingsIndex - 5,
							buildingsIndex);
					for (int i = buildingsIndex - 5; i <= buildingsIndex; i++) {
						if (EnglishInitialResources.isInShortStopWordList(tweetWords.get(i))) {
							add = false;
							break;
						}
					}

				}
				if (add)
					matches.add("h{" + match + "}h");
			}
		}

		for (String building : EnglishInitialResources.getBuildingWords()) { // loop through building words

			int index = tweetWords.indexOf(building);
			if (index > 0) { // found a building term

				String match;
				boolean add = true;
				String preWord = tweetWords.get(index - 1);

				if (EnglishInitialResources.isPreposition(preWord)
						|| conjs.contains(preWord)
						|| preWord.equals("the")
						|| EnglishInitialResources.isInShortStopWordList(preWord)
						|| ParserUtils.punctuationInPrev(building,lowerTweet)) {
					// we don't want a preposition, conjunction, stopword, or
					// "the", or punctuation
					continue;
				}

				if (preWord.equals("xxx"))
					continue;
				if (index > 1) {
					match = ParserUtils.getNewString(tweetWords, index - 2, index);

					for (int i = index - 2; i <= index; i++) {
						if (EnglishInitialResources.isInShortStopWordList(tweetWords.get(i))) {
							add = false;
							break;
						}

					}
				} else {
					match = ParserUtils.getNewString(tweetWords, index - 1, index);
					for (int i = index - 1; i <= index; i++) {
						// if its stopword, dont add
						if (EnglishInitialResources.isInShortStopWordList(tweetWords.get(i))) {
							add = false;
							break;
						}
					}
				}
				if (add) {
					matches.add("h{"
							+ match.replace("  ", " ").replace("xxx", "")
									.trim() + "}h");
				}
			}
		}
		tweet.setMatches(matches);
	}

}
