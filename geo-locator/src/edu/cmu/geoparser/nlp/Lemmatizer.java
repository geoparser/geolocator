package edu.cmu.geoparser.nlp;

import edu.cmu.geoparser.model.Sentence;

public interface Lemmatizer {
	Sentence lemmatize(Sentence sent);
}
