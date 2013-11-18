package edu.cmu.geoparser.nlp;

import edu.cmu.geoparser.model.*;
public interface POSTagger {

	Sentence tag(Sentence sent);
}
