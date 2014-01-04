/**
 * 
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
 * 
 * @author Wei Zhang,  Language Technology Institute, School of Computer Science, Carnegie-Mellon University.
 * email: wei.zhang@cs.cmu.edu
 * 
 */
package edu.cmu.geoparser.model;

import java.util.HashSet;

import org.apache.lucene.document.Document;

import edu.cmu.geoparser.Disambiguation.TweetContentType.CONTENTTYPE;


public class TweetExample {
  private String text;
  private LocEntity[] toponyms;
  /**
   * exact document in the gazetteer.
   * It's Existence is because we don't have the precise location. So we store the most probable place
   * given the distance.
   * 
   * if the doc is null, then just fill it with a null.
   * Because we have a document array, we can set certain place to be null.
   */

  private double latitude, longitude;

  private String userLocation, timezone, userDescription;

  public TweetExample() {
    text = null;
    toponyms = null;
    latitude = longitude = -999;
    userLocation = timezone = userDescription = null;
  }

  public String getText() {
    return text;
  }

  public TweetExample setText(String text) {
    this.text = text;
    return this;
  }

  public LocEntity[] getToponyms() {
    return toponyms;
  }

  public TweetExample setToponyms(LocEntity[] toponyms) {
    this.toponyms = toponyms;
    return this;
  }

  public double getLatitude() {
    return latitude;
  }

  public TweetExample setLatitude(double latitude) {
    this.latitude = latitude;
    return this;
  }

  public double getLongitude() {
    return longitude;
  }

  public TweetExample setLongitude(double longitude) {
    this.longitude = longitude;
    return this;
  }

  public String getUserLocation() {
    return userLocation;
  }

  public TweetExample setUserLocation(String userLocation) {
    this.userLocation = userLocation;
    return this;
  }

  public String getTimezone() {
    return timezone;
  }

  public TweetExample setTimezone(String timezone) {
    this.timezone = timezone;
    return this;
  }

  public String getUserDescription() {
    return userDescription;
  }

  public TweetExample setUserDescription(String userDescription) {
    this.userDescription = userDescription;
    return this;
  }
  
  public boolean containsTweetCoord(){
    if (this.latitude==-999 || this.longitude==-999)
      return false;
    return true;
  }

  public String toString() {
    StringBuilder s = new StringBuilder().append("text: ").append(this.text).append("\n lat: ")
            .append(this.latitude).append("\n lon: ").append(this.longitude)
            .append("\n timezone: ").append(this.timezone).append("\n userLocation: ")
            .append(this.userLocation).append("\n description : ").append(this.userDescription+"\n");
    for (LocEntity e : this.toponyms) {
      s.append(e);
    }
    return s.toString();

  }

  public boolean containsUserInfo() {
    if(this.userLocation.length()==0 && this.userDescription.length()==0)
      return false;
    else
      return true;
  }
  public String getUserInfo(){
    return userLocation +" : "+ userDescription;
  }

  public boolean containsTimezone() {
    // TODO Auto-generated method stub
    return !(timezone==null);
  }

  public String getToponymsAsText() {
    // TODO Auto-generated method stub
    StringBuilder sb = new StringBuilder();
    for (LocEntity e : this.toponyms)
      sb.append(e).append("\n");
    return sb.toString();
  }

  public HashSet<String> getAllIds() {
    // TODO Auto-generated method stub
    if (this.toponyms==null)
      return null;
    HashSet<String> ids = new HashSet<String>();
    for (LocEntity topo : this.toponyms)
    {
      if (topo.getGeonamesIds()==null)
        continue;
      ids.addAll(topo.getGeonamesIds());
    }
    return ids;
  }

}
