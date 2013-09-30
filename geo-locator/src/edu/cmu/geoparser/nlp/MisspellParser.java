package edu.cmu.geoparser.nlp;

import java.io.IOException;

import org.apache.lucene.queryParser.ParseException;

import edu.cmu.geoparser.model.Tweet;

public interface MisspellParser {

	String parse(String s) throws IOException, ParseException;
}
