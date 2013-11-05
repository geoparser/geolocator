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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.geoparser.model.Tweet;
import edu.cmu.geoparser.parser.NERTagger;
import edu.cmu.geoparser.parser.utils.ParserUtils;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;

public class EnglishStanfordNERParser implements NERTagger{

	
	// pattern for finding <TAG> tags
	private static Pattern pattern = Pattern.compile("<([A-Z][A-Z0-9]*)\\b[^>]*>(.*?)</\\1>");
	private  static AbstractSequenceClassifier<?> classifier;
	Set<String> entities = getEntities();

	/**
	 * Default protected constructor
	 * 
	 */
	public EnglishStanfordNERParser() {
		String classifierPath = "res/en/english.all.3class.distsim.crf.ser.gz";
		 classifier = CRFClassifier.getClassifierNoExceptions(classifierPath);
		
	}

	// Identifying location names by NER
	public List<String>  parse(Tweet tweet) {

		String origText = tweet.getOrigText();

		if (tweet.getMatches() == null) {
			tweet.setMatches(new ArrayList<String>());
		}

		String result = classifier.classifyWithInlineXML(origText);
		System.out.println(result);

		// if (LOGGER.isDebugEnabled())
		// LOGGER.debug(result);

		// parse the result
		Matcher matcher = pattern.matcher(result);

		// find entity
		while (matcher.find()) {
			if (entities.contains(matcher.group(1))) {
				// find relevant entities.

				String elem = matcher.group(2);

				if (elem.contains("RT"))
					continue; // false positive "RT" in twitter

				// contains number, dot, @, :, and #
				if (elem.contains(".") || elem.contains("@") || elem.contains(":") || elem.contains("#") || ParserUtils.hasNum(elem)) {
					continue;
				}
				boolean add = true;
				String[] words = elem.toLowerCase().split("[ ]+");

				/*
				 * for (String word : words) { if
				 * (!EnglishInitialResources.isInDictionary(word)) { add = true;
				 * break; } }
				 */
				// make sure found element doesn't contain stop word
				for (String word : words) {
		//			if (EnglishInitialResources.isInShortStopWordList(word))
					{
						add = false;
						break;
					}
				}
				
				//System.out.println(elem);
				// if add
				if (add) {
					if (!tweet.getMatches().contains("n{" + elem + "}n"))
						tweet.getMatches().add("n{" + elem + "}n");
				}
			}
		}
		return tweet.getMatches();
	}

	private static Set<String> getEntities() {
		// the types of entities we are looking for from Stanford NER
		Set<String> entities = new HashSet<String>();
		entities.add("LOCATION");
		// entities.add("ORGANIZATION");
		return entities;
	}

	public static void main(String argv[]) {
		Tweet t = new Tweet();
		EnglishStanfordNERParser snp = new EnglishStanfordNERParser();
		t.setText("Chennai is one of the major cities in the central Qitaihe. However, I live in Amberson Apartment in Pittsburgh.");
		System.out.println(t.getOrigText());
		snp.parse(t);
		List<String> m = t.getMatches();
		System.out.println(m.size());
		for (String s : m) {
			System.out.println(s);
		}
	}
}
