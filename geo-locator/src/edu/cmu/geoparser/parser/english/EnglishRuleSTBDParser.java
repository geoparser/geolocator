package edu.cmu.geoparser.parser.english;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.geoparser.model.Tweet;
import edu.cmu.geoparser.nlp.ner.FeatureExtractor.FeatureGenerator;
import edu.cmu.geoparser.nlp.tokenizer.EuroLangTwokenizer;
import edu.cmu.geoparser.parser.STBDParser;
import edu.cmu.geoparser.parser.utils.ParserUtils;
import edu.cmu.geoparser.resource.trie.IndexSupportedTrie;

public class EnglishRuleSTBDParser implements STBDParser {

	/**
	 * Default protected constructor
	 */
	static String streetpattern = "\\$+[AN\\^G]+[N\\^G]";
	static String streetpattern2 = "[D]*[AN\\^G][N\\^G]";
	static String streetpattern3 = "[D]*[AN\\^G][AN\\^G][N\\^G]";
	static String streetpattern4 = "[D]*[AN\\^G][AN\\^G][AN\\^G][N\\^G]";

	static String[] stpattern = { streetpattern, streetpattern2, streetpattern3, streetpattern4 };
	static String buildingpattern = "([D]*[AN\\^G]+[N\\^G])";

	static Pattern streetpospattern;
	static Pattern buildingpospattern = Pattern.compile(buildingpattern);

	FeatureGenerator fgen;

	public EnglishRuleSTBDParser(FeatureGenerator fgen) {
		this.fgen = fgen;
	}

	/**
	 * Find streets by street suffixes
	 * 
	 * @param tweetMatches
	 * @param tweet
	 */
	public List<String> parse(Tweet tweet) {
		String text = tweet.getOrigText();
		List<String> tok = EuroLangTwokenizer.tokenize(text);

		List<String> poss = fgen.getPostagger().tag(tok);
		String posstr = "";
		for (int i = 0; i < poss.size(); i++)
			posstr += poss.get(i);

		List<String> matches = new ArrayList<String>();

		for (int j = 0; j < stpattern.length; j++) {
			streetpospattern = Pattern.compile(stpattern[j]);
			Matcher stmatcher = streetpospattern.matcher(posstr);
			while (stmatcher.find()) {
				if (ParserUtils.isStreetSuffix(tok.get(stmatcher.end() - 1))) {
					String temp = "";
					for (int i = stmatcher.start(); i < stmatcher.end(); i++)
						temp += tok.get(i) + " ";
					matches.add("st{" + temp.trim() + "}st");
				}
			}
		}
		Matcher bdmatcher = buildingpospattern.matcher(posstr);
		while (bdmatcher.find()) {
			if (ParserUtils.isBuildingSuffix(tok.get(bdmatcher.end() - 1))) {
				String temp = "";
				for (int i = bdmatcher.start(); i < bdmatcher.end(); i++)
					temp += tok.get(i) + " ";
				matches.add("bd{" + temp.trim() + "}bd");
			}
		}
		return matches;
	}

	public static void main(String argv[]) {
		Tweet t = new Tweet();
		String s = "RT @JenellaHerring: #centraltxfires Big Dog Rescue is evac. THEY NEED TRAILERS, TRUCKS & CRATES!!! shelter is located 589 Cool Water Dri ...";
		s = "Neal St. Park on fire damn man we breathing ashes";
		s = "I'm at Harrah's Louisiana Downs (8000 E Texas St, at I-220, Bossier City) http://t.co/EPvhlnd";
		s = "I'm at Trigon Bus Stop (Texas A&M University, College Station) http://t.co/xLlvSH9";
		s = "RT @ksatweather: Mandatory evacuations ordered for The Abbey Apts & Canyon Ridge Estates in the Stone Oak area. #TxWildfire";
		// s =
		// "MT @roxxsfisher: law officers asking spectators to please leave canyon ridge elem school and stone oak park for their safety. #satxwildfires";
		// s =
		// "Am worried about the #centraltxfires in Cedar Creek bc the Capitol of Texas Zoo is there. Lots of rescues & endangered animals. #pawcircle";
		t.setText(s);
		EnglishRuleSTBDParser stparser = new EnglishRuleSTBDParser(new FeatureGenerator("en", new IndexSupportedTrie(
				 "res/GeoNames/allCountries.txt", "GazIndex/",false, false), "res/"));
		stparser.parse(t);
		System.out.println(t.getMatches());

	}
}
