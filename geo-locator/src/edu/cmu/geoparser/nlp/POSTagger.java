package edu.cmu.geoparser.nlp;

import java.util.List;

public interface POSTagger {

	List<String> tag(List<String> string);
}
