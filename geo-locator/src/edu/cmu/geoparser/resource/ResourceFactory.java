package edu.cmu.geoparser.resource;

import edu.cmu.geoparser.resource.Map.AdmCode2GazCandidateMap;
import edu.cmu.geoparser.resource.Map.CCodeAdj2CTRYtype;
import edu.cmu.geoparser.resource.Map.FeatureCode2Map;
import edu.cmu.geoparser.resource.gazindexing.CollaborativeIndex.CollaborativeIndex;

public class ResourceFactory {

  private static CollaborativeIndex collaborativeIndex = CollaborativeIndex.getInstance();

  private static AdmCode2GazCandidateMap adminCode2GazCandidateMap = AdmCode2GazCandidateMap
          .getInstance();

  private static CCodeAdj2CTRYtype countryCode2CountryMap = CCodeAdj2CTRYtype
          .getInstance();

  private static FeatureCode2Map featurecode2map = FeatureCode2Map.getInstance();
  /**
   * @return the collaborativeIndex
   */
  public static CollaborativeIndex getClbIndex() {
    return collaborativeIndex;
  }

  /**
   * return the GazEntryAndInfo type given the admin code.
   * @return the adminCode2GazCandidateMap
   */
  public static AdmCode2GazCandidateMap getAdminCode2GazCandidateMap() {
    return adminCode2GazCandidateMap;
  }

  /**
   * return the Country type given the country code.
   * @return the countryCode2CountryMap
   * 
   */
  public static CCodeAdj2CTRYtype getCountryCode2CountryMap() {
    return countryCode2CountryMap;
  }

  public static FeatureCode2Map getFeatureCode2Map() {
    // TODO Auto-generated method stub
    return featurecode2map;
  }
}
