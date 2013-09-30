package edu.cmu.geoparser.Disambiguation;

import edu.cmu.geoparser.model.LocEntity;

public class TweetEntry {

  public String text;
  public LocEntity[] toponym1K,toponym1N,toponymTruce1;
  public LocEntity[] toponym2K,toponym2N,toponymTruce2;
  public double latitude,longitude;
  public String userLocation,timezone,userDescription;
  
}
