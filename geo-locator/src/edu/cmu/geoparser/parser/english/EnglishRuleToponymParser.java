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
package edu.cmu.geoparser.parser.english;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.queryParser.ParseException;

import edu.cmu.geoparser.common.StringUtil;
import edu.cmu.geoparser.model.Tweet;
import edu.cmu.geoparser.nlp.MisspellParser;
import edu.cmu.geoparser.nlp.ner.FeatureExtractor.FeatureGenerator;
import edu.cmu.geoparser.nlp.spelling.EuroLangMisspellParser;
import edu.cmu.geoparser.nlp.tokenizer.EuroLangTwokenizer;
import edu.cmu.geoparser.parser.TPParser;
import edu.cmu.geoparser.parser.utils.ParserUtils;
import edu.cmu.geoparser.resource.trie.IndexSupportedTrie;

public class EnglishRuleToponymParser implements TPParser {

	static final int GRAM = 5;
	static String[] ngram;
	static String[] posngram;
	static boolean allstopwords;
	static ArrayList<String> results;

	static Pattern gazpattern = Pattern.compile("[AN\\^G]*[N\\^G]");

	/**
	 * Gaz Matching. The parser only lookup the token array. This parser does
	 * not tokenize the input string for efficiency reasons.
	 */

	FeatureGenerator fgen;
	MisspellParser mparser;
	boolean misspell;

	public EnglishRuleToponymParser(FeatureGenerator fgen, MisspellParser mparser, boolean misspell) {
		this.fgen = fgen;
		this.mparser = mparser;
		this.misspell = misspell;
	}

	public List<String> parse(Tweet tweet) {
		// TODO Auto-generated method stub

		String text = tweet.getOrigText();
		List<String> tokens = EuroLangTwokenizer.tokenize(text);
		
		System.out.println(" Tokenization:\n" + tokens.toString());
		
		List<String> temptokens = new ArrayList<String>();
		for (String t : tokens) {
			if (t.startsWith("#") && t.length() > 1)
				temptokens.add(t.substring(1));
			else
				temptokens.add(t);
		}
		tokens = temptokens;
		List<String> postags = fgen.getPostagger().tag(tokens);

		String posstr = "";
		for (int i = 0; i < postags.size(); i++)
			posstr += postags.get(i);

		System.out.println(" POS Tagging: "+ posstr);
		
		List<String> matches = new ArrayList<String>();

		// match countries without considering the part of speech.
		for (int i = 1; i < 5; i++) {
			String[] igrams = StringUtil.constructgrams(tokens.toArray(new String[] {}), i, true);
			if (igrams==null)
				continue;
			for (String igram : igrams) {
				// System.out.println("igram is :["+igram+"]");
				if (ParserUtils.isCountry(igram))
					if (!matches.contains("tp{" + igram + "}tp")) {
						// System.out.println("contries found.");
						matches.add("tp{" + igram + "}tp");
					}
			}
		}

		Matcher gazmatcher = gazpattern.matcher(posstr);
		while (gazmatcher.find()) {
			int n = gazmatcher.end() - gazmatcher.start();
			String[] subtoks = new String[n];
			// System.out.print("Found ");
			for (int i = gazmatcher.start(); i < gazmatcher.end(); i++) {
				subtoks[i - gazmatcher.start()] = tokens.get(i);
				// System.out.print(tokens.get(i));
			}

			for (int k = 1; k < n + 1; k++) {
				String[] kgrams = StringUtil.constructgrams(subtoks, k, true);
				for (String kgram : kgrams) {
					kgram = kgram.trim();

					String corkgram = null;
					
					if (misspell)
						try {
							corkgram = mparser.parse(kgram);
							System.out.println("The String ["+ kgram +"] has been misspelled. Corrected to	["+ corkgram+"]");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					else
						corkgram = kgram;
//					System.out.println("The String to look up Trie is : [" + corkgram + "]");

					if (fgen.getTrie().searchTrie(corkgram, true) != null) {
						if (ParserUtils.isFilterword(corkgram) || ParserUtils.isEsFilterword(corkgram)
								) {
							continue;
						}
						if (!matches.contains("tp{" + corkgram.trim() + "}tp"))
							matches.add("tp{" + corkgram.trim() + "}tp");
					}
					// else
					// System.out.println(corkgram+" is not in the trie");
				}
			}
		}
		return matches;
	}

	public static void main(String argv[]) throws IOException, ParseException {
		argv[0] = "GeoNames/cities1000.txt";
		IndexSupportedTrie ist = new IndexSupportedTrie(argv[0],"GazIndex/", true, false);
		FeatureGenerator feature = new FeatureGenerator("en", ist, "res/");
		MisspellParser mparser = new EuroLangMisspellParser(ist.index);

		EnglishRuleToponymParser etp = new EnglishRuleToponymParser(feature, mparser, true);

		Tweet t = new Tweet();
		// //////
		BufferedReader s = new BufferedReader(new InputStreamReader(System.in, "utf-8"));
		while (true) {
			t.setText(s.readLine());
			t.setMatches(null);
			if (t.getOrigText().length() == 0)
				continue;
			double stime = System.currentTimeMillis();
			List<String> matches = etp.parse(t);
			double etime = System.currentTimeMillis();
			System.out.println(matches);
			System.out.println(etime - stime);
		}
	}
}
