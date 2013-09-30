package edu.cmu.geoparser.nlp;

import java.util.List;

public interface SentenceSplitter {

  List<String> split(String s);
  
}
