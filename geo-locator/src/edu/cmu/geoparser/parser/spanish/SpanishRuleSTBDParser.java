package edu.cmu.geoparser.parser.spanish;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.csvreader.CsvReader;

import edu.cmu.geoparser.model.Tweet;
import edu.cmu.geoparser.nlp.ner.FeatureExtractor.FeatureGenerator;
import edu.cmu.geoparser.nlp.tokenizer.EuroLangTwokenizer;
import edu.cmu.geoparser.parser.STBDParser;
import edu.cmu.geoparser.parser.utils.ParserUtils;

public class SpanishRuleSTBDParser implements STBDParser {

	/**
	 * Default protected constructor
	 */
	static String streetpattern = "\\$+[an]+n";
	static String streetpattern2 = "[d]*[an]n";
	static String streetpattern3 = "[d]*[an][an]n";
	static String streetpattern4 = "[d]*[an][an][an]n";
	static String highway = "nz";
	static String av_street = "afvz";
	static String st_adj = "na";
	static String st_only = "ndn";
	static String calle = "nn[z]*|na[z]*";
	static String esquina = "ns[an]{0,2}";
	static String[] stpattern = { streetpattern, streetpattern2, streetpattern3, streetpattern4, highway, av_street,
			st_adj, st_only, calle, esquina };
	static String buildingpattern = "[d]*[an]+n";
	static String buildingpattern2 = "ns[an]+[na]";
	static String el_pattern = "[n]+dn";
	static String de_pattern = "nsn";
	static String de_sup_pattern = "nsdn";
	static String recurNS_de_pattern = "nsnsn[a]?";
	static String de_dot_pattern = "nfsn";
	static String buildingpattern3 = "[an]+";
	static String bd_adj = "na";
	static String[] bdpattern = { buildingpattern, buildingpattern2, el_pattern, recurNS_de_pattern, de_dot_pattern,
			buildingpattern3, de_pattern, bd_adj, de_sup_pattern };
	static Pattern streetpospattern;
	static Pattern buildingpospattern;

	private FeatureGenerator fgen;

	public SpanishRuleSTBDParser(FeatureGenerator fgen) {
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
		// System.out.println(tok.toString());
		List<String> poss = fgen.getPostagger().tag(tok);
		String posstr = "";
		for (int i = 0; i < poss.size(); i++)
			posstr += poss.get(i).charAt(0);
		// System.out.println(posstr);
//		for (int i = 0; i < tok.size(); i++) {
//			System.out.print(tok.get(i) + "_" + posstr.charAt(i) + ", ");
//		}
//		System.out.println();
		List<String> matches = new ArrayList<String>();

		for (int j = 0; j < stpattern.length; j++) {
			streetpospattern = Pattern.compile(stpattern[j]);
			Matcher stmatcher = streetpospattern.matcher(posstr);
			if (j == 8) {

			}
			while (stmatcher.find()) {
				if (ParserUtils.isESStreetPrefix(tok.get(stmatcher.start()))) {
					String temp = "";
					for (int i = stmatcher.start(); i < stmatcher.end(); i++)
						temp += tok.get(i) + " ";
					matches.add("st{" + temp.trim() + "}st");
//					System.out.println(posstr);
				}
			}
		}
		for (int j = 0; j < bdpattern.length; j++) {
			buildingpospattern = Pattern.compile(bdpattern[j]);
			Matcher bdmatcher = buildingpospattern.matcher(posstr);
			// System.out.println(j);
			if (j == 2)// the building prefix is at the end, which is the
					// building suffix
				while (bdmatcher.find()) {
					if (ParserUtils.isESBuildingPrefix(tok.get(bdmatcher.end() - 1))) {
						String temp = "";
						for (int i = bdmatcher.start(); i < bdmatcher.end(); i++)
							temp += tok.get(i) + " ";
						matches.add("bd{" + temp.trim() + "}bd");
//						System.out.println(posstr);

					} else if (ParserUtils.isESBuildingPrefix(tok.get(bdmatcher.start()))) {
						String temp = "";
						for (int i = bdmatcher.start(); i < bdmatcher.end(); i++)
							temp += tok.get(i) + " ";
						matches.add("bd{" + temp.trim() + "}bd");
//						System.out.println(posstr);

					}
				}
			else if (j == 3)
				while (bdmatcher.find()) {
					if (ParserUtils.isESBuildingPrefix(tok.get(bdmatcher.start() + 2))) {
						String temp = "";
						for (int i = bdmatcher.start() + 2; i < bdmatcher.end(); i++)
							temp += tok.get(i) + " ";
						if (temp.contains(" por "))
							continue;
						matches.add("bd{" + temp.trim() + "}bd");
//						System.out.println(bdmatcher.group());

					}
				}
			else
				while (bdmatcher.find()) {
					if (ParserUtils.isESBuildingPrefix(tok.get(bdmatcher.start()))) {
//						System.out.println(tok.get(bdmatcher.start()));
						String temp = "";
						for (int i = bdmatcher.start(); i < bdmatcher.end(); i++)
							temp += tok.get(i) + " ";
						if (temp.contains(" por "))
							continue;
						if (j == 4 && !tok.get(bdmatcher.start() + 1).equals("."))
							continue;
						matches.add("bd{" + temp.trim() + "}bd");
//						System.out.println(bdmatcher.group());

					}
				}
		}
		List<String> finalmatch = new ArrayList<String>();
		for (String match : matches) {
			String word = match.substring(2, match.length() - 2);
			if (word.split(" ").length == 1) {
				if (ParserUtils.isBuildingSuffix(match) || ParserUtils.isESBuildingPrefix(word)
						|| ParserUtils.isESStreetPrefix(word) || ParserUtils.isStreetSuffix(word))
					continue;
			} else if (word.contains("..") || word.contains("=") || word.contains(",") || word.contains("@")
					|| word.contains("?") || word.contains("!") || word.contains(" _") || word.contains(" rt}")
					|| word.contains("{rt ") || word.contains(" rt ")) {
				continue;
			} else if (finalmatch.contains(match))
				continue;
			else
				finalmatch.add(match);
		}
		return matches;
	}

	public static void main(String argv[]) throws IOException {
		Tweet t = new Tweet();
		CsvReader csvr = new CsvReader("trainingdata/esNER/train/raw.csv", ',', Charset.forName("utf-8"));
		csvr.readHeaders();
		String line = null;
		int i = 2;

		// THIS CODE WON'T WORK UNLESS ADDING FEATURE GENERATOR TO PARSER.
		SpanishRuleSTBDParser esstbdparser = null;// new
									// SpanishRuleSTBDParser(new
									// FeatureGenerator);
		while (csvr.readRecord()) {
			line = csvr.get(csvr.getHeaders()[1]);
			if (i < 3500 || i > 4000) {
				i++;
				continue;
			}
			System.out.println(i);
			System.out.println(line);
			t.setText(line);
			List<String> matches = esstbdparser.parse(t);
			if (t.getMatches().size() != 0) {
				System.out.println(t.getMatches());
			}
			t.setMatches(null);
			i++;
		}

	}
}
