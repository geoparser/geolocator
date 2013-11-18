package edu.cmu.geoparser.nlp;

import edu.cmu.geoparser.nlp.ner.FeatureExtractor.FeatureGenerator;
import edu.cmu.geoparser.resource.ResourceFactory;

public class NERFeatureFactory {

  private static FeatureGenerator enFeatureGenerator;
  private static FeatureGenerator esFeatureGenerator;

  public static FeatureGenerator getInstance(String langCode) throws Exception{
    if (langCode.equalsIgnoreCase("en")){
      if (enFeatureGenerator==null)
        return  new FeatureGenerator("en", ResourceFactory.getClbIndex(), "res/");
      return enFeatureGenerator;
    }
    if(langCode.equalsIgnoreCase("es")){
      if(esFeatureGenerator==null)
        return new FeatureGenerator("es", ResourceFactory.getClbIndex(), "res/");
      return esFeatureGenerator;
    }
    
    throw new Exception("Language not compatible with NER FeatureGenerator.");
  }
}
