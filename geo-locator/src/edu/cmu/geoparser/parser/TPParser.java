package edu.cmu.geoparser.parser;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.queryParser.ParseException;

import edu.cmu.geoparser.model.Tweet;

public interface TPParser {

	List<String> parse(Tweet t);
}
