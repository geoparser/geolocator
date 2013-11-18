package edu.cmu.geoparser.model;

import java.util.Comparator;

import org.apache.lucene.document.Document;

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
public class GazEntryAndFeature implements Comparable<GazEntryAndFeature> {

  /**
   * share the following with all the subclasses.
   * 
   */
  String id, asciiName;

  int altnameCount;

  double latitude, longitude;

  long population;

  String countryCode, country;

  String adm1Code, adm2Code, adm3Code, adm4Code;

  String adm1, adm2, adm3, adm4;

  String feature, featureClass;

  String timezone;

  /**
   * The following variables are the feature variables. The features are generated based on the
   * context and the metadata.
   */
  /**
   * This is for storing the rank of the gaz entry for a specific location. Now we can assign 1 when
   * it's the top one, and 0 for not being the top one.
   */
  int f_PopRank;

  /**
   * if contains coordinates of tweet.
   */
  boolean f_containsTweetCoordinates;

  /**
   * if contains user info in the tweet.
   */
  boolean f_containsUserInfo;

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

  public GazEntryAndFeature() {
    f_PopRank = 0;
    f_containsTweetCoordinates = false;
    f_containsUserInfo = false;
  }

  /**
   * only fill out information, does not fill out string. String is filled out by name and
   * alternatives.
   * 
   * @param d
   * @return
   */
  public GazEntryAndFeature(Document d) {
    this();
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
  public GazEntryAndFeature setCountry(String country) {
    this.country = country;
    return this;
  }

  /**
   * @param adm1
   *          the adm1 to set
   */
  public GazEntryAndFeature setAdm1(String adm1) {
    this.adm1 = adm1;
    return this;
  }

  /**
   * @param adm2
   *          the adm2 to set
   */
  public GazEntryAndFeature setAdm2(String adm2) {
    this.adm2 = adm2;
    return this;
  }

  /**
   * @param adm3
   *          the adm3 to set
   */
  public GazEntryAndFeature setAdm3(String adm3) {
    this.adm3 = adm3;
    return this;
  }

  /**
   * @param adm4
   *          the adm4 to set
   */
  public GazEntryAndFeature setAdm4(String adm4) {
    this.adm4 = adm4;
    return this;
  }

  /**
   * @param id
   *          the id to set
   */
  public GazEntryAndFeature setId(String id) {
    this.id = id;
    return this;
  }

  /**
   * @param asciiName
   *          the asciiName to set
   */
  public GazEntryAndFeature setAsciiName(String asciiName) {
    this.asciiName = asciiName;
    return this;
  }

  /**
   * @param altnameCount
   *          the altnameCount to set
   */
  public GazEntryAndFeature setAltnameCount(int altnameCount) {
    this.altnameCount = altnameCount;
    return this;
  }

  /**
   * @param latitude
   *          the latitude to set
   */
  public GazEntryAndFeature setLatitude(double latitude) {
    this.latitude = latitude;
    return this;
  }

  /**
   * @param longitude
   *          the longitude to set
   */
  public GazEntryAndFeature setLongitude(double longitude) {
    this.longitude = longitude;
    return this;
  }

  /**
   * @param population
   *          the population to set
   */
  public GazEntryAndFeature setPopulation(long population) {
    this.population = population;
    return this;
  }

  /**
   * @param countryCode
   *          the countryCode to set
   */
  public GazEntryAndFeature setCountryCode(String countryCode) {
    this.countryCode = countryCode;
    return this;
  }

  /**
   * @param adm1Code
   *          the adm1Code to set
   */
  public GazEntryAndFeature setAdm1Code(String adm1Code) {
    this.adm1Code = adm1Code;
    return this;
  }

  /**
   * @param adm2Code
   *          the adm2Code to set
   */
  public GazEntryAndFeature setAdm2Code(String adm2Code) {
    this.adm2Code = adm2Code;
    return this;
  }

  /**
   * @param adm3Code
   *          the adm3Code to set
   */
  public GazEntryAndFeature setAdm3Code(String adm3Code) {
    this.adm3Code = adm3Code;
    return this;
  }

  /**
   * @param adm4Code
   *          the adm4Code to set
   */
  public GazEntryAndFeature setAdm4Code(String adm4Code) {
    this.adm4Code = adm4Code;
    return this;
  }

  /**
   * @param feature
   *          the feature to set
   */
  public GazEntryAndFeature setFeature(String feature) {
    this.feature = feature;
    return this;
  }

  /**
   * @param featureClass
   *          the featureClass to set
   */
  public GazEntryAndFeature setFeatureClass(String featureClass) {
    this.featureClass = featureClass;
    return this;
  }

  /**
   * @param timezone
   *          the timezone to set
   */
  public GazEntryAndFeature setTimezone(String timezone) {
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
  public int compareTo(GazEntryAndFeature o) {
    if (this.population == o.population)
      return 0;
    if (this.population > o.population)
      return -1;
    return 1;
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
          GazEntryAndFeature info1 = (GazEntryAndFeature) o1;
          GazEntryAndFeature info2 = (GazEntryAndFeature) o2;
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
          GazEntryAndFeature info1 = (GazEntryAndFeature) o1;
          GazEntryAndFeature info2 = (GazEntryAndFeature) o2;
          // descending order.
          return info2.getAltnameCount() - info1.getAltnameCount();
        }
      };
    return altNamesComparator;
  }

  /**
   * @return the f_PopRank
   */
  public int getF_PopRank() {
    return f_PopRank;
  }

  /**
   * @param f_PopRank
   *          the f_PopRank to set
   */
  public void setF_PopRank(int f_PopRank) {
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

  public void setF_containsUserInfo(boolean b) {
    // TODO Auto-generated method stub
    this.f_containsUserInfo = true;
  }

  public void setF_InTimezone(boolean b) {
    // TODO Auto-generated method stub
    this.f_inTimezone = b;
  }

  /**
   * @return the f_containsUserInfo
   */
  public boolean getF_containsUserInfo() {
    return f_containsUserInfo;
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

}
