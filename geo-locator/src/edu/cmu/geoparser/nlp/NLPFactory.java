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
    if (enPosTagger==null)
      return ENTweetPOSTagger.getInstance();
    return enPosTagger;
  }
  public static Lemmatizer getEnUWStemmer(){
    if (uwStemmer ==null)
      return UWMorphaStemmer.getInstance();
    return uwStemmer;
  }
}
