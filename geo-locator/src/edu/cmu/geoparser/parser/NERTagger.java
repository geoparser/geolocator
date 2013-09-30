package edu.cmu.geoparser.parser;

import java.util.List;

import edu.cmu.geoparser.model.Tweet;

public interface NERTagger {
	
	List<String> parse(Tweet tweet);
}
