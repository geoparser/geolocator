package edu.cmu.geoparser.parser;

import edu.cmu.geoparser.parser.english.EnglishMTNERParser;
import edu.cmu.geoparser.parser.english.EnglishRuleSTBDParser;
import edu.cmu.geoparser.parser.english.EnglishRuleToponymParser;

public class ParserFactory {

  private static EnglishMTNERParser emtparser;

  private static EnglishRuleSTBDParser estbdparser;

  private static EnglishRuleToponymParser etopoparser;
  
  public static EnglishMTNERParser getEnNERParser() {
    if (emtparser == null)
      return EnglishMTNERParser.getInstance();
    else
      return emtparser;
  }

  public static EnglishRuleSTBDParser getEnSTBDParser() {
    if (estbdparser == null)
      return EnglishRuleSTBDParser.getInstance();
    else
      return estbdparser;
  }
  
  public static EnglishRuleToponymParser getEnToponymParser() {
    if (etopoparser == null)
      return EnglishRuleToponymParser.getInstance();
    else
      return etopoparser;
  }

}
