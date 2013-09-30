package edu.cmu.geoparser.parser.spanish;

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

public class SpanishParser {

	NERTagger ner;
	STBDParser stbd;
	TPParser tp;
	HashSet<String> match;
	public SpanishParser(String root, IndexSupportedTrie topotrie, boolean misspell){
    FeatureGenerator fgen = new FeatureGenerator("es", topotrie, "res/");
//		match = new HashSet<String>();
		ner = new SpanishMTNERParser(root +"es/esNER-crf-final.model",fgen);
		stbd = new SpanishRuleSTBDParser(fgen);
		tp = new SpanishRuleToponymParser(fgen,new EuroLangMisspellParser(fgen.getTrie().index),misspell);
	}
	public List<String> parse(Tweet t){
		
		match = new HashSet<String>();
		List<String> nerresult = ner.parse(t);
		List<String> stbdresult = stbd.parse(t);
		List<String> toporesult = tp.parse(t);
		match.addAll(nerresult);
		match.addAll(stbdresult);
//		match.addAll(toporesult);
		
		return ParserUtils.ResultReduce(new ArrayList<String>(match));
		
	}
}
