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

import edu.cmu.geoparser.model.LocEntity;
import edu.cmu.geoparser.model.Tweet;
import edu.cmu.geoparser.nlp.ner.FeatureExtractor.FeatureGenerator;
import edu.cmu.geoparser.parser.NERTagger;
import edu.cmu.geoparser.parser.ParserFactory;
import edu.cmu.geoparser.parser.STBDParser;
import edu.cmu.geoparser.parser.TPParser;
import edu.cmu.geoparser.parser.utils.ParserUtils;
import edu.cmu.geoparser.resource.gazindexing.Index;

/**
 * The aggregation of all the parsers for English.
 * @author indri
 *
 */
public class EnglishParser {

  private NERTagger ner;
  private STBDParser stbd;
  private TPParser tp;
	
	HashSet<LocEntity> match;
	public EnglishParser(String root, Index index, boolean misspell){
		ner = ParserFactory.getEnNERParser();
		stbd = ParserFactory.getEnSTBDParser();
		tp = ParserFactory.getEnToponymParser();
	}
	public List<LocEntity> parse(Tweet t){
		match = new HashSet<LocEntity>();
		List<LocEntity> nerresult = ner.parse(t);
		List<LocEntity> stbdresult = stbd.parse(t);
		List<LocEntity> toporesult = tp.parse(t);
		match.addAll(nerresult);
		match.addAll(stbdresult);
		match.addAll(toporesult);
		return new ArrayList<LocEntity>(match);	
		
	}
}
