package edu.cmu.geoparser.resource;

import edu.cmu.geoparser.resource.Map.AdmCode2GazCandidateMap;
import edu.cmu.geoparser.resource.Map.CountryCode2COUNTRYMap;
import edu.cmu.geoparser.resource.gazindexing.CollaborativeIndex.CollaborativeIndex;

public class ResourceFactory {

  private static CollaborativeIndex collaborativeIndex = CollaborativeIndex.getInstance();

  private static AdmCode2GazCandidateMap adminCode2GazCandidateMap = AdmCode2GazCandidateMap
          .getInstance();

  private static CountryCode2COUNTRYMap countryCode2CountryMap = CountryCode2COUNTRYMap
          .getInstance();

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
  public static CountryCode2COUNTRYMap getCountryCode2CountryMap() {
    return countryCode2CountryMap;
  }
}
