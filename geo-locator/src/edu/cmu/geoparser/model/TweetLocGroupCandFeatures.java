package edu.cmu.geoparser.model;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.lucene.document.Document;

import edu.cmu.geoparser.common.TweetDisambUtil;
import edu.cmu.geoparser.resource.gazindexing.CollaborativeIndex.CollaborativeIndex;

public class TweetLocGroupCandFeatures {

  ArrayList<ArrayList<GazEntryAndFeature>> featureArrays;

  TweetExample t;

  CollaborativeIndex ci;

  TweetLocGroupCandFeatures(TweetExample t, CollaborativeIndex ci) {
    this.t = t;
    this.ci = ci;
    LocEntity[] topos = t.getToponyms();

    // store all the loc entity.
    featureArrays = new ArrayList(t.getToponyms().length);

    // store feature for a loc entity.
    ArrayList<GazEntryAndFeature> aFeatureArray = new ArrayList<GazEntryAndFeature>();

    for (LocEntity topo : topos) {
      ArrayList<Document> docs = ci.getDocumentsByPhrase(topo.getStringTokens());
      int docListSize = docs.size();
      for (int i = 0; i < docs.size(); i++) {
        // doc is the doc of the candidate.
        Document doc = docs.get(i);
        // convert doc to GazEntryInfoandFeature type to store the fields in the doc.
        GazEntryAndFeature aFeature = new GazEntryAndFeature(doc);
        /**
         * set contains user geo feature
         */
        if (t.containsTweetCoord())
          aFeature.setF_containsTweetCoordinates(true);

        /**
         * set contains user location & description info
         */
        if (t.containsUserInfo())
          aFeature.setF_containsUserInfo(true);

        /**
         * set contained in timezone
         */
        Document[] timezone = TweetDisambUtil.twitterTimezone2Country(aFeature.getTimezone());
        if (TweetDisambUtil.countryInTimezone(timezone, doc))
          aFeature.setF_InTimezone(true);

        /**
         * the user info contains the document info. of the current doc.
         */
        // if ()
        // aFeature.setF_DocInfoInUserInfo();

        // add that to the candidates.
        aFeatureArray.add(aFeature);
      }

      /**
       * set population ranking
       * non-zero population greater than 1000 are set to 1.
       */
      // sort by population decreasing order.
      Collections.sort(aFeatureArray);
      // the first most population one is 1, and the rest are 0.
      // if the population is null, it's rank is 0. if not null, it's all 1.
      for (int i = 0; i < docListSize; i++) {
        if (aFeatureArray.get(i).getPopulation() > 1000)
          aFeatureArray.get(0).setF_PopRank(1);
      }

      /**
       * set feature ranking
       * use countrystate infered feature type. use the infered feature value.
       */
      Collections.sort(aFeatureArray, GazEntryAndFeature.getCountryStateComparator());
      for (int i = 0; i < docListSize / 2; i++) {
        aFeatureArray.get(i).setF_FeatureRank(aFeatureArray.get(i).getHierarcheyLevel());
      }

      /**
       * set alternate names number ranking
       * if alternativenames is bigger than 10, then set to 1.
       */
      Collections.sort(aFeatureArray, GazEntryAndFeature.getAltNamesComparator());
      for (int i = 0; i < docListSize; i++) {
        if (aFeatureArray.get(i).getAltnameCount() > 10)
          aFeatureArray.get(i).setF_AltNamesRank(1);
      }

      // add those into the feature arrays.
      featureArrays.add(aFeatureArray);
    }// end of LocEntity loop
  }

  public static void main(String argv[]) {
    // sample program, not working.
    TweetExample t = new TweetExample();
    CollaborativeIndex ci = new CollaborativeIndex();
    TweetLocGroupCandFeatures cf = new TweetLocGroupCandFeatures(t, ci).genFeatures();
  }

  private TweetLocGroupCandFeatures genFeatures() {
    return null;
  }
}
