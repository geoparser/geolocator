package edu.cmu.geoparser.model;

public class Country extends CandidateAndFeature implements Comparable<CandidateAndFeature>{

  
  public Country(){
    super();
  }
  public String getAbbr() {
    return abbr;
  }
  public Country setAbbr(String abbr) {
    this.abbr = abbr;
    return this;
  }
  public String getLang() {
    return lang;
  }
  public Country setLang(String lang) {
    this.lang = lang;
    return this;
  }
  public String getRace() {
    return race;
  }
  public Country setRace(String race) {
    this.race = race;
    return this;
  }

  String abbr,lang,race;
  @Override
  public int compareTo(CandidateAndFeature arg0) {
    // TODO Auto-generated method stub
    return arg0.getId().compareTo(this.getId());
  }
    
  }
