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

import edu.cmu.geoparser.model.Tweet;
import edu.cmu.geoparser.nlp.ner.FeatureExtractor.FeatureGenerator;
import edu.cmu.geoparser.nlp.spelling.EuroLangMisspellParser;
import edu.cmu.geoparser.parser.NERTagger;
import edu.cmu.geoparser.parser.STBDParser;
import edu.cmu.geoparser.parser.TPParser;
import edu.cmu.geoparser.parser.utils.ParserUtils;
import edu.cmu.geoparser.resource.trie.IndexSupportedTrie;

public class EnglishParser {

	NERTagger ner;
	STBDParser stbd;
	TPParser tp;
	
	HashSet<String> match;
	public EnglishParser(String root, IndexSupportedTrie topotrie, boolean misspell){
    FeatureGenerator fgen = new FeatureGenerator("en", topotrie, "res/");
//		match = new HashSet<String>();
		ner = new EnglishMTNERParser(root +"en/enNER-crf-final.model",fgen);
		stbd = new EnglishRuleSTBDParser(fgen);
		tp = new EnglishRuleToponymParser(fgen,new EuroLangMisspellParser(fgen.getTrie().index),misspell);
		
	}
	public List<String> parse(Tweet t){
		
		match = new HashSet<String>();
		List<String> nerresult = ner.parse(t);
		List<String> stbdresult = stbd.parse(t);
		List<String> toporesult = tp.parse(t);
		match.addAll(nerresult);
		match.addAll(stbdresult);
		match.addAll(toporesult);
		
		return ParserUtils.ResultReduce(new ArrayList<String>(match));
		
	}
}
