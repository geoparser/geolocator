package edu.cmu.geoparser.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.lucene.document.Document;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.IntArrayData;

import edu.cmu.geoparser.resource.ResourceFactory;
import edu.cmu.geoparser.resource.gazindexing.CollaborativeIndex.InfoFields;
import edu.stanford.nlp.util.StringUtils;

/**
 * The data structure storing the gazetteer entry, and store the features given the context and
 * metadata. Gaz Entry is not unique. In the system we can have multiple copies of the same entry,
 * with different additional feature information.
 * 
 * The basic information inherited from the gazetteer is the same. However, When we extract features
 * and fill out the fields, we could have different features. Features are based on the context, and
 * the metadata of the tweet, if any.
 * 
 * @author Wei Zhang
 * 
 */
public class CandidateAndFeature implements Comparable<CandidateAndFeature> {

  /**
   * share the following with all the subclasses.
   * 
   */
  // original name in the text.
  String originName;

  // Loc entity
  LocEntity le;

  // id and name in the candidate;
  String id, asciiName;

  int altnameCount;

  double latitude, longitude;

  long population;

  String countryCode, country;

  String adm1Code, adm2Code, adm3Code, adm4Code;

  String adm1, adm2, adm3, adm4;

  String feature, featureClass;

  boolean isCountry;

  public boolean isCountry() {
    return isCountry;
  }

  String timezone;

  /**
   * The following variables are the feature variables. The features are generated based on the
   * context and the metadata.
   */

  /**
   * If it's a single location in the tweet.
   */

  boolean f_single;

  /**
   * This is for storing the rank of the gaz entry for a specific location. Now we can assign 1 when
   * it's the top one, and 0 for not being the top one.
   */
  double f_PopRank;

  /**
   * if contains coordinates of tweet.
   */
  boolean f_containsTweetCoordinates;

  /**
   * if contains user info in the tweet.
   */
  boolean f_containsUserLoc;

  // the value to store the overlap.
  private double f_userInfoOverlap;

  /**
   * if contains in the timezone
   */
  private boolean f_inTimezone;

  /**
   * the feature ranking. It's shared by feature_ranking( feature based) and country-state inference
   * ranking comparator.
   */
  private int f_FeatureRank;

  /**
   * alternate names number rank.
   */
  private int f_AltNamesRank;

  /**
   * timezone feature
   */
  private boolean f_containsTimezone;

  /**
   * The overlap of the current candidate with the other candidates in the context.
   */
  private int f_OtherLocOverlap;

  /**
   * The label of the whole instance, gazentryAndFeature, in the given context, of course.
   */
  private int Y;

  private int[] featureVector;

  private double f_DistanceToUserLoc;

  private int f_DistanceToUserLocRank;

  private boolean f_isCommonCountry;

  private boolean f_isCommonState;

  public CandidateAndFeature() {
    this.f_single = false;
    this.f_PopRank = -1;
    this.f_containsTweetCoordinates = false;
    this.f_containsUserLoc = false;
    this.f_AltNamesRank = -1;
    this.f_FeatureRank = -1;
    this.f_inTimezone = false;
    this.f_userInfoOverlap = -1;
    this.f_containsTimezone = false;
    this.f_OtherLocOverlap = -1;
    this.Y = 0; // not a true instance as default.
  }

  /**
   * only fill out information, does not fill out string. String is filled out by name and
   * alternatives.
   * 
   * @param d
   * @return
   */
  public CandidateAndFeature(String name, Document d, LocEntity le) {
    this();
    this.le = le;
    this.originName = name;
    this.id = d.get("ID");
    this.asciiName = d.get("ORIGINAL-NAME");
    this.altnameCount = Integer.parseInt(d.get("ALTNAME-COUNT"));
    this.longitude = Double.parseDouble(d.get("LONGTITUDE"));
    this.latitude = Double.parseDouble(d.get("LATITUDE"));
    this.population = Long.parseLong(d.get("POPULATION"));
    this.countryCode = d.get("COUNTRY-CODE");
    this.adm1Code = d.get("ADM1-CODE");
    this.adm2Code = d.get("ADM2-CODE");
    this.adm3Code = d.get("ADM3-CODE");
    this.adm4Code = d.get("ADM4-CODE");
    this.feature = d.get("FEATURE");
    this.featureClass = d.get("FEATURE-CLASS");
    this.timezone = d.get("TIMEZONE");

    // generate some indicator variables when creating the object.
    this.isCountry = (this.countryCode.length() != 0 && this.adm1Code.equals("00") && this.adm2Code
            .length() == 0) ? true : false;
  }

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * @return the asciiName
   */
  public String getAsciiName() {
    return asciiName;
  }

  /**
   * @return the altnameCount
   */
  public int getAltnameCount() {
    return altnameCount;
  }

  /**
   * @return the latitude
   */
  public double getLatitude() {
    return latitude;
  }

  /**
   * @return the longitude
   */
  public double getLongitude() {
    return longitude;
  }

  /**
   * @return the population
   */
  public long getPopulation() {
    return population;
  }

  /**
   * @return the countryCode
   */
  public String getCountryCode() {
    return countryCode;
  }

  /**
   * @return the country
   */
  public String getCountry() {
    return country;
  }

  /**
   * @return the adm1Code
   */
  public String getAdm1Code() {
    return adm1Code;
  }

  /**
   * @return the adm2Code
   */
  public String getAdm2Code() {
    return adm2Code;
  }

  /**
   * @return the adm3Code
   */
  public String getAdm3Code() {
    return adm3Code;
  }

  /**
   * @return the adm4Code
   */
  public String getAdm4Code() {
    return adm4Code;
  }

  /**
   * @return the adm1
   */
  public String getAdm1() {
    return adm1;
  }

  /**
   * @return the adm2
   */
  public String getAdm2() {
    return adm2;
  }

  /**
   * @return the adm3
   */
  public String getAdm3() {
    return adm3;
  }

  /**
   * @return the adm4
   */
  public String getAdm4() {
    return adm4;
  }

  /**
   * @return the feature
   */
  public String getFeature() {
    return feature;
  }

  /**
   * @return the featureClass
   */
  public String getFeatureClass() {
    return featureClass;
  }

  /**
   * @return the timezone
   */
  public String getTimezone() {
    return timezone;
  }

  /**
   * @param country
   *          the country to set
   */
  public CandidateAndFeature setCountry(String country) {
    this.country = country;
    return this;
  }

  /**
   * @param adm1
   *          the adm1 to set
   */
  public CandidateAndFeature setAdm1(String adm1) {
    this.adm1 = adm1;
    return this;
  }

  /**
   * @param adm2
   *          the adm2 to set
   */
  public CandidateAndFeature setAdm2(String adm2) {
    this.adm2 = adm2;
    return this;
  }

  /**
   * @param adm3
   *          the adm3 to set
   */
  public CandidateAndFeature setAdm3(String adm3) {
    this.adm3 = adm3;
    return this;
  }

  /**
   * @param adm4
   *          the adm4 to set
   */
  public CandidateAndFeature setAdm4(String adm4) {
    this.adm4 = adm4;
    return this;
  }

  /**
   * @param id
   *          the id to set
   */
  public CandidateAndFeature setId(String id) {
    this.id = id;
    return this;
  }

  /**
   * @param asciiName
   *          the asciiName to set
   */
  public CandidateAndFeature setAsciiName(String asciiName) {
    this.asciiName = asciiName;
    return this;
  }

  /**
   * @param altnameCount
   *          the altnameCount to set
   */
  public CandidateAndFeature setAltnameCount(int altnameCount) {
    this.altnameCount = altnameCount;
    return this;
  }

  /**
   * @param latitude
   *          the latitude to set
   */
  public CandidateAndFeature setLatitude(double latitude) {
    this.latitude = latitude;
    return this;
  }

  /**
   * @param longitude
   *          the longitude to set
   */
  public CandidateAndFeature setLongitude(double longitude) {
    this.longitude = longitude;
    return this;
  }

  /**
   * @param population
   *          the population to set
   */
  public CandidateAndFeature setPopulation(long population) {
    this.population = population;
    return this;
  }

  /**
   * @param countryCode
   *          the countryCode to set
   */
  public CandidateAndFeature setCountryCode(String countryCode) {
    this.countryCode = countryCode;
    return this;
  }

  /**
   * @param adm1Code
   *          the adm1Code to set
   */
  public CandidateAndFeature setAdm1Code(String adm1Code) {
    this.adm1Code = adm1Code;
    return this;
  }

  /**
   * @param adm2Code
   *          the adm2Code to set
   */
  public CandidateAndFeature setAdm2Code(String adm2Code) {
    this.adm2Code = adm2Code;
    return this;
  }

  /**
   * @param adm3Code
   *          the adm3Code to set
   */
  public CandidateAndFeature setAdm3Code(String adm3Code) {
    this.adm3Code = adm3Code;
    return this;
  }

  /**
   * @param adm4Code
   *          the adm4Code to set
   */
  public CandidateAndFeature setAdm4Code(String adm4Code) {
    this.adm4Code = adm4Code;
    return this;
  }

  /**
   * @param feature
   *          the feature to set
   */
  public CandidateAndFeature setFeature(String feature) {
    this.feature = feature;
    return this;
  }

  /**
   * @param featureClass
   *          the featureClass to set
   */
  public CandidateAndFeature setFeatureClass(String featureClass) {
    this.featureClass = featureClass;
    return this;
  }

  /**
   * @param timezone
   *          the timezone to set
   */
  public CandidateAndFeature setTimezone(String timezone) {
    this.timezone = timezone;
    return this;
  }

  /**
   * Helper classes, determining the level of hierarchy by country and adminX fields.
   * 
   */
  public int getHierarcheyLevel() {

    if (this.countryCode.length() == 0)
      return -1;// _XX_____ or ______ not a country, but bigger than that or equal.
    if (this.adm1Code.length() == 0)
      return 0; // CN_ ______ not a valid form. but check it for safety.
    if (this.adm1Code.equals("00"))
      return 0; // CN_00_ _____ a valid country form.
    if (this.adm2Code.length() == 0)
      return 1; // CN_01__ ____ a valid adm1 code.
    if (this.adm3Code.length() == 0)
      return 2; // CN_01_13_ ____ a valid adm2 code.
    if (this.adm4Code.length() == 0)
      return 3; // CN_01_13_14_ __ a valid adm3 code.
    return 4; // CN_01_13_14_39 a valid adm4 code.
  }

  /**
   * default comparator: POPULATION decreasing. There's other comparators to sort. Decreasing order.
   */
  @Override
  public int compareTo(CandidateAndFeature o) {
    if (this.population == o.population)
      return 0;
    if (this.population > o.population)
      return -1;
    return 1;
  }

  public String toString() {
    return this.f_DistanceToUserLoc + "";
  }

  /**
   * 
   * Not done. Don't use it.
   * 
   * Here use the feature class coarse category, instead of the specific category. which is ,
   * H(stream), P(city), A(country, state), S(building),
   * L(parks),R(road),T(mountain),U(undersea),V(forest), A comparator to sort the
   * GazEnrtyInfoAndFeature by feature class.
   * 
   * descreasing order.
   */
  private static Comparator featureComparator;

  public static Comparator getFeatureComparator() {
    if (featureComparator == null)
      return new Comparator() {
        public int compare(Object o1, Object o2) {
          return 0;
        }
      };
    return featureComparator;
  }

  /**
   * A comparator to sort the GazEnrtyInfoAndFeature by inference from country and state value.
   */
  private static Comparator countryStateComparator;

  public static Comparator getCountryStateComparator() {
    if (countryStateComparator == null)
      return new Comparator() {
        public int compare(Object o1, Object o2) {
          CandidateAndFeature info1 = (CandidateAndFeature) o1;
          CandidateAndFeature info2 = (CandidateAndFeature) o2;
          // descending order.
          return info2.getHierarcheyLevel() - info1.getHierarcheyLevel();
        }
      };
    return countryStateComparator;
  }

  /**
   * alternate Names comparator
   */
  private static Comparator altNamesComparator;

  public static Comparator getAltNamesComparator() {
    if (altNamesComparator == null)
      return new Comparator() {
        public int compare(Object o1, Object o2) {
          CandidateAndFeature info1 = (CandidateAndFeature) o1;
          CandidateAndFeature info2 = (CandidateAndFeature) o2;
          // descending order.
          return info2.getAltnameCount() - info1.getAltnameCount();
        }
      };
    return altNamesComparator;
  }

  /**
   * @return the f_PopRank
   */
  public double getF_PopRank() {
    return f_PopRank;
  }

  /**
   * @param f_PopRank
   *          the f_PopRank to set
   */
  public void setF_PopRank(double f_PopRank) {
    this.f_PopRank = f_PopRank;
  }

  /**
   * @return the f_containsTweetCoordinates
   */
  public boolean getF_containsTweetCoordinates() {
    return f_containsTweetCoordinates;
  }

  /**
   * @param f_containsTweetCoordinates
   *          the f_containsTweetCoordinates to set
   */
  public void setF_containsTweetCoordinates(boolean f_containsTweetCoordinates) {
    this.f_containsTweetCoordinates = f_containsTweetCoordinates;
  }

  public void setF_containsUserLoc(boolean b) {
    // TODO Auto-generated method stub
    this.f_containsUserLoc = true;
  }

  public boolean getF_containsTimezone() {
    // TODO Auto-generated method stub
    return this.f_containsTimezone;
  }

  public void setF_InTimezone(boolean b) {
    // TODO Auto-generated method stub
    this.f_inTimezone = b;
  }

  /**
   * @return the f_containsUserInfo
   */
  public boolean getF_containsUserLoc() {
    return f_containsUserLoc;
  }

  /**
   * @return the f_inTimezone
   */
  public boolean getF_inTimezone() {
    return f_inTimezone;
  }

  public void setF_FeatureRank(int i) {
    this.f_FeatureRank = i;
  }

  /**
   * @return the f_FeatureRank
   */
  public int getF_FeatureRank() {
    return f_FeatureRank;
  }

  public void setF_AltNamesRank(int i) {
    // TODO Auto-generated method stub
    this.f_AltNamesRank = i;
  }

  public void setF_userInfoOverlap(double sim) {
    // TODO Auto-generated method stub
    this.f_userInfoOverlap = sim;
  }

  public void setF_containsTimezone(boolean b) {
    // TODO Auto-generated method stub
    this.f_containsTimezone = b;
  }

  /**
   * @return the f_userInfoOverlap
   */
  public double getF_userInfoOverlap() {
    return f_userInfoOverlap;
  }

  /**
   * @return the f_single
   */
  public boolean getF_single() {
    return f_single;
  }

  /**
   * @param f_single
   *          the f_single to set
   */
  public void setF_single(boolean f_single) {
    this.f_single = f_single;
  }

  /**
   * @return the f_AltNamesRank
   */
  public int getF_AltNamesRank() {
    return f_AltNamesRank;
  }

  /**
   * @param f_inTimezone
   *          the f_inTimezone to set
   */
  public void setF_inTimezone(boolean f_inTimezone) {
    this.f_inTimezone = f_inTimezone;
  }

  public void setF_isCommonCountry(boolean b) {
    // TODO Auto-generated method stub
    this.f_isCommonCountry = b;
  }

  public boolean getF_isCommonCountry() {
    return f_isCommonCountry;
  }

  public void setF_isCommonState(boolean b) {
    // TODO Auto-generated method stub
    this.f_isCommonState = b;
  }

  public boolean getF_isCommonState() {
    return f_isCommonState;
  }

  /**
   * @return the f_OtherLocOverlap
   */
  public int getF_OtherLocOverlap() {
    return f_OtherLocOverlap;
  }

  /**
   * @return the y
   */
  public int getY() {
    return Y;
  }

  /**
   * @param y
   *          the y to set
   */
  public void setY(int y) {
    Y = y;
  }

  public String getF_featureValue() {
    // TODO Auto-generated method stub
    return this.feature;
  }

  public void setF_featureVector(String string) {
    // TODO Auto-generated method stub\
    this.featureVector = new int[ResourceFactory.getFeatureCode2Map().size()];
    if (string.equals("0") == false)
      featureVector[ResourceFactory.getFeatureCode2Map().getIndex(string)] = 1;
  }

  public int[] getF_featureVector() {
    return featureVector;
  }

  public double getF_DistanceToUserLoc() {
    return f_DistanceToUserLoc;
  }

  public void setF_DistanceToUserLoc(double f_DistanceToUserLoc) {
    this.f_DistanceToUserLoc = f_DistanceToUserLoc;
  }

  public int getF_DistanceToUserLocRank() {
    return f_DistanceToUserLocRank;
  }

  public void setF_DistanceToUserLocRank(int f_DistanceToUserLocRank) {
    this.f_DistanceToUserLocRank = f_DistanceToUserLocRank;
  }

  Comparator userlocComparator;

  public static Comparator<? super CandidateAndFeature> getDistToUserLocComparator() {
    if (altNamesComparator == null)
      return new Comparator() {
        public int compare(Object o1, Object o2) {
          CandidateAndFeature info1 = (CandidateAndFeature) o1;
          CandidateAndFeature info2 = (CandidateAndFeature) o2;
          // descending order.
          if (info2.f_DistanceToUserLoc > info1.f_DistanceToUserLoc)
            return -1;
          if (info2.f_DistanceToUserLoc == info1.f_DistanceToUserLoc)
            return 0;
          return 1;
        }
      };
    return altNamesComparator;
  }

  private int featureCodeInt;

  public void setOneHotFeatureCode(int fc) {

    this.featureCodeInt = fc;

  }

  public int getOneHotFeatureCode(int fc) {

    return this.featureCodeInt;

  }

  public double[] getOneHotFeatureVector() {
    double[] vec = new double[ResourceFactory.getFeatureCode2Map().size()];
    if (this.featureCodeInt == -1)
      return vec;
    vec[this.featureCodeInt] = 1;
    return vec;
  }

  public boolean isAbbr() throws Exception {
    if (originName == null)
      throw new Exception("empty string abbr");
    if (originName.length() > 3)
      return false;
    if (Character.isLowerCase(originName.charAt(0)))
      return false;
    for (char c : originName.toCharArray())
      if (Character.isLowerCase(c) || Character.isLetter(c) == false)
        return false;
    return true;
  }

  boolean abbr;

  public CandidateAndFeature setF_isAbbr(boolean b) {
    // TODO Auto-generated method stub
    abbr = b;
    return this;
  }

  public boolean getF_isAbbr() {
    // TODO Auto-generated method stub
    return abbr;
  }

  double stringSim;

  public void setF_strSim() {
    // TODO Auto-generated method stub
    stringSim = StringUtils.editDistance(asciiName, originName);
    stringSim = 1.0d - stringSim / (double) Math.max(asciiName.length(), originName.length());
  }

  public double getF_strSim() {
    return stringSim;
  }

  public void setF_strSim4Abbr() {
    // TODO Auto-generated method stub
    String pattern = "";
    for (char c : originName.toLowerCase().toCharArray())
      pattern += ".*" + c;
    pattern += ".*";
    stringSim = asciiName.toLowerCase().matches(pattern) ? 1.0 : 0;
  }

  boolean f_singleCandidate;

  private boolean f_userLocCountryAgree;

  private boolean f_userLocStateAgree;

  public void setF_singleCandidate(boolean b) {
    // TODO Auto-generated method stub
    this.f_singleCandidate = b;
  }

  public boolean getF_singleCandidate() {
    // TODO Auto-generated method stub
    return this.f_singleCandidate;
  }

  public boolean getF_isCountry() {
    // TODO Auto-generated method stub
    return this.isCountry();
  }

  public void setF_userLocOverlap(List<LocEntity> userLocs) {
    // TODO Auto-generated method stub
    for (LocEntity loc : userLocs){
      ArrayList<Document> docs = ResourceFactory.getClbIndex().getDocumentsByPhrase(loc.getTokenString());
      for (Document doc : docs){
        String dc = doc.get(InfoFields.countryCode);
        String ds = doc.get(InfoFields.adm1Code);
        if (this.countryCode.equals(dc))
                this.f_userLocCountryAgree = true;
        if(this.adm1Code.equals(ds))
            this.f_userLocStateAgree = true;
      }
    }
  }

  public boolean getF_userLocCountryAgree() {
    return f_userLocCountryAgree;
  }

  public boolean getF_userLocStateAgree() {
    // TODO Auto-generated method stub
    return this.f_userLocStateAgree;
  }
}
