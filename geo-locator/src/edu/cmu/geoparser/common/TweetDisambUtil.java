package edu.cmu.geoparser.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;

import edu.cmu.geoparser.model.CandidateAndFeature;
import edu.cmu.geoparser.model.LocEntity;
import edu.cmu.geoparser.nlp.tokenizer.EuroLangTwokenizer;
import edu.cmu.geoparser.resource.ResourceFactory;
import edu.cmu.geoparser.resource.gazindexing.CollaborativeIndex.InfoFields;

public class TweetDisambUtil {

  public static boolean countryInTimezone(Document[] timeZone, CandidateAndFeature aFeature) {
    String candCountry = aFeature.getCountryCode();
    for (Document atz : timeZone) {
      String atzCountry = atz.get(InfoFields.countryCode);
      if (candCountry.equals(atzCountry)) {
        return true;
      }
    }
    return false;
  }

  public static Document[] twitterTimezone2Country(String ttz) {
    if (ttz.length() == 0 || ttz == null)
      return null;
    if (ttz.endsWith("(US & Canada)")) {
      String full = ResourceFactory.getCountryCode2CountryMap().getValue("us").getAsciiName();
      ArrayList<Document> us = ResourceFactory.getClbIndex().getDocumentsByPhrase(full);
      Document theUS = null;
      long pop = -1;
      for (Document eachUS : us) {
        long eachPop = Long.parseLong(eachUS.get("POPULATION"));
        if (eachPop > pop) {
          theUS = eachUS;
          pop = eachPop;
        }
      }

      ArrayList<Document> ca = ResourceFactory.getClbIndex().getDocumentsByPhrase("Canada");
      Document theCA = null;
      pop = -1;
      for (Document eachCA : ca) {
        long eachPop = Long.parseLong(eachCA.get("POPULATION"));
        if (eachPop > pop) {
          theCA = eachCA;
          pop = eachPop;
        }
      }
      return new Document[] { theUS, theCA };
    }
    if (ttz.endsWith("(Canada)")) {
      ArrayList<Document> ca = ResourceFactory.getClbIndex().getDocumentsByPhrase("Canada");
      Document theCA = null;
      long pop = -1;
      for (Document eachCA : ca) {
        long eachPop = Long.parseLong(eachCA.get("POPULATION"));
        if (eachPop > pop) {
          theCA = eachCA;
          pop = eachPop;
        }
      }
      return new Document[] { theCA };
    } else {
      ArrayList<Document> locs = ResourceFactory.getClbIndex().getDocumentsByPhrase(ttz);
      Document theloc = null;
      if (locs == null)
        return null;
      long pop = -1;
      for (Document loc : locs) {
        long eachpop = Long.parseLong(loc.get("POPULATION"));
        if (eachpop > pop) {
          theloc = loc;
          pop = eachpop;
        }
      }
      return new Document[] { theloc };
    }
  }

  public static double getDocStringOverlap(CandidateAndFeature aFeature, String userInfo) {
    // TODO Auto-generated method stub
    ArrayList<String> dTokens = new ArrayList<String>();
    String name = aFeature.getAsciiName();

//    System.out.println(doc);
    //add alternative names into it.
    String id = aFeature.getId();
    String[] altnames = ResourceFactory.getClbIndex().getAlternateNames(id);
    
    String country = "", countryCode = "";
    countryCode = aFeature.getCountryCode();
    if (ResourceFactory.getCountryCode2CountryMap().isInMap(countryCode))
      country = ResourceFactory.getCountryCode2CountryMap().getValue(countryCode).getAsciiName();
    
    List<String> userTokens = EuroLangTwokenizer.tokenize(userInfo.toLowerCase());
    
    String[] temp = new String[]{country, countryCode};
    for (String t : temp){
      if (t==null ||t.length()==0)continue;
      dTokens.add(t.toLowerCase());
    }
    for (String t : altnames){
      if (t==null ||t.length()==0)continue;
      dTokens.add(t.toLowerCase());
    }

    return StringUtil.getGramSimilarity(userTokens.toArray(new String[]{}), dTokens.toArray(new String[]{}));
  }

}
