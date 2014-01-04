package edu.cmu.geoparser.nlp;

import edu.cmu.geoparser.nlp.lemma.UWMorphaStemmer;
import edu.cmu.geoparser.nlp.pos.ENTweetPOSTagger;

public class NLPFactory {
  private static POSTagger enPosTagger ;
  private static Lemmatizer uwStemmer;

  /**
   * @return the enPosTagger
   */
  public static POSTagger getEnPosTagger() {
      return ENTweetPOSTagger.getInstance();
  }
  public static Lemmatizer getEnUWStemmer(){
      return UWMorphaStemmer.getInstance();
  }
}
