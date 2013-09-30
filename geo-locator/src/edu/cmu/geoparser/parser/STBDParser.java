package edu.cmu.geoparser.parser;

import java.util.List;

import edu.cmu.geoparser.model.Tweet;

public interface STBDParser {
	List<String> parse(Tweet tweet);
}
