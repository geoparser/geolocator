package edu.cmu.geoparser.parser;

import edu.cmu.geoparser.parser.english.EnglishMTNERParser;
import edu.cmu.geoparser.parser.english.EnglishRuleSTBDParser;
import edu.cmu.geoparser.parser.english.EnglishRuleToponymParser;

public class ParserFactory {

  private static EnglishMTNERParser emtparser;

  private static EnglishRuleSTBDParser estbdparser;

  private static EnglishRuleToponymParser etopoparser;
  
  public static EnglishMTNERParser getEnNERParser() {
      return EnglishMTNERParser.getInstance();
  }

  public static EnglishRuleSTBDParser getEnSTBDParser() {
      return EnglishRuleSTBDParser.getInstance();
  }
  
  public static EnglishRuleToponymParser getEnToponymParser() {
      return EnglishRuleToponymParser.getInstance();
  }

}
