package edu.cmu.geoparser.parser.english;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

import edu.cmu.geoparser.model.Tweet;
import edu.cmu.geoparser.nlp.ner.FeatureExtractor.FeatureGenerator;
import edu.cmu.geoparser.nlp.spelling.EuroLangMisspellParser;
import edu.cmu.geoparser.nlp.tokenizer.EuroLangTwokenizer;
import edu.cmu.geoparser.parser.NERTagger;
import edu.cmu.geoparser.parser.STBDParser;
import edu.cmu.geoparser.parser.TPParser;
import edu.cmu.geoparser.parser.spanish.SpanishMTNERParser;
import edu.cmu.geoparser.parser.spanish.SpanishRuleSTBDParser;
import edu.cmu.geoparser.parser.spanish.SpanishRuleToponymParser;
import edu.cmu.geoparser.resource.trie.IndexSupportedTrie;
import edu.cmu.minorthird.classify.ClassLabel;
import edu.cmu.minorthird.classify.Example;
import edu.cmu.minorthird.classify.Feature;
import edu.cmu.minorthird.classify.MutableInstance;
import edu.cmu.minorthird.classify.sequential.SequenceClassifier;
import edu.cmu.minorthird.util.IOUtil;

public class EnglishMTNERParser implements NERTagger{

	SequenceClassifier model;
	FeatureGenerator fg;

	/**
	 * Default protected constructor
	 * 
	 * @throws IOException
	 * 
	 */

	public EnglishMTNERParser(String modelname, FeatureGenerator featureg) {
		try {
			model = (SequenceClassifier) IOUtil.loadSerialized(new java.io.File(modelname));
			this.fg = featureg;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Identifying location names by NER
	Example[] examples;

	public List<String> parse(Tweet tweet) {

		String[] t_tweet = (EuroLangTwokenizer.tokenize(tweet.getOrigText())).toArray(new String[] {});
		examples = new Example[t_tweet.length];

		// instances that is converted to feature list.
		List<Feature[]> feature_instances = fg.extractFeature(t_tweet);

		for (int i = 0; i < examples.length; i++) {

			ClassLabel label = new ClassLabel("NA");
			MutableInstance instance = new MutableInstance("0", Integer.toString(i));

			Feature[] features = feature_instances.get(i);

			for (int j = 0; j < features.length; j++) {
				instance.addBinary(features[j]);
			}
			examples[i] = new Example(instance, label);
			// System.out.println(examples[i].toString());
		}
		ClassLabel[] resultlabels = model.classification(examples);
		/*
		 * for (int i = 0; i < resultlabels.length; i++) {
		 * System.out.print(resultlabels[i].bestClassName() + " "); }
		 */
		List<String> matches = tweet.getMatches();
		if (matches == null)
			matches = new ArrayList<String>();

		/**
		 * tag the result with the defined format.
		 */
		String buffer = "";
		String previous = "", current = "";
		for (int k = 0; k < resultlabels.length; k++) {
			current = resultlabels[k].bestClassName();
			if (current.equals("O")) {
				if (previous.length() == 0)
					previous = current;
				else if (previous.equals("O")) {
					continue;
				} else {
					buffer = previous + "{" + buffer.trim() + "}" + previous;
					if (!matches.contains(buffer))
						matches.add(buffer);
					buffer = "";
					previous = current;
				}
			} else {
				if (previous.length() == 0 || previous.equals("O")) {
					previous = current;
					buffer += " " + t_tweet[k];
				} else {
					if (previous.equals(current))
						buffer += " " + t_tweet[k];
					else {
						buffer = previous + "{" + buffer.trim() + "}" + previous;
						if (!matches.contains(buffer))
							matches.add(buffer);
						previous = current;
					}
				}
			}
		}

		tweet.setMatches(matches);
		return matches;
	}

	public static void main(String argv[]) throws IOException {
		String encrfname = "res/en/enNER-crf-final.model";

		IndexSupportedTrie topotrie = new IndexSupportedTrie("geoNames.com/allCountries.txt", "GazIndex/",false, false);// ,

		FeatureGenerator enfgen = new FeatureGenerator("en", topotrie, "res/");

		NERTagger enstanfordner = new EnglishStanfordNERParser();
		NERTagger enner = new EnglishMTNERParser(encrfname, enfgen);

		
	}
}
