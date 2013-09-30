package edu.cmu.geoparser.nlp;

import java.util.List;

public interface Lemmatizer {
	List<String> lemmatize(List<String> sent);
}
