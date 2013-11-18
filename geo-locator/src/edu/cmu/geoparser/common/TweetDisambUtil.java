package edu.cmu.geoparser.common;

import java.util.ArrayList;

import org.apache.lucene.document.Document;

import edu.cmu.geoparser.resource.ResourceFactory;

public class TweetDisambUtil {

  public static boolean countryInTimezone(Document[] timeZone, Document candidate){
    String candCountry = candidate.get("COUNTRY-CODE");
    for( Document atz : timeZone){
      String atzCountry = atz.get("COUNTRY-CODE");
      if(candCountry.equals(atzCountry)){
        return true;
      }
    }
    return false;
  }
  public static Document[] twitterTimezone2Country(String ttz) {
    if (ttz.length()==0 || ttz==null)
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

      ArrayList<Document> ca = ResourceFactory.getClbIndex().getDocumentsByPhrase(
              "Canada");
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
      ArrayList<Document> ca = ResourceFactory.getClbIndex().getDocumentsByPhrase(
              "Canada");
      Document theCA = null;
      long pop = -1;
      for (Document eachCA : ca) {
        long eachPop = Long.parseLong(eachCA.get("POPULATION"));
        if (eachPop > pop) {
          theCA = eachCA;
          pop = eachPop;
        }
      }
      return new Document[]{theCA};
    }
    else{
      ArrayList<Document> locs = ResourceFactory.getClbIndex().getDocumentsByPhrase(
              ttz);
      Document theloc = null;
      if(locs==null)
        return null;
      long pop=-1;
      for (Document loc : locs){
        long eachpop =Long.parseLong(loc.get("POPULATION"));
        if (eachpop > pop){
          theloc = loc;
          pop = eachpop;
        }
      }
      return new Document[]{theloc};
    }
  }

  
}
