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

import java.util.Arrays;

import org.apache.lucene.document.Document;
/**
 * An entity that is extracted from text. After it's generated, 
 * geonamesID, latitude, longitude, and population needs to be determine.
 * @author indri
 *
 */
public class LocEntity {

  /**
   * Those are filled out after generating the loc entity.
   */
  private int toksStart, toksEnd;
  private Token[] tokens;

  private String NEType;

  /**
   * This is absolutely necessary.
   */
  private double latitude;
  private double longitude;

  /**
   * GeonamesID is optional, because we may not find it in gaz, but we have to attach geocode to it.
   */
  private String geonamesId;
  private Document geonamesEntry;

  public Document getGeonamesEntry() {
    return geonamesEntry;
  }

  public void setGeonamesEntry(Document geonamesEntry) {
    this.geonamesEntry = geonamesEntry;
  }

  public int getToksStart() {
    return toksStart;
  }

  public LocEntity setToksStart(int toksStart) {
    this.toksStart = toksStart;
    return this;
  }

  public int getToksEnd() {
    return toksEnd;
  }

  public LocEntity setToksEnd(int toksEnd) {
    this.toksEnd = toksEnd;
    return this;
  }

  public String getNEType() {
    return NEType;
  }

  public LocEntity setNEType(String type) {
    this.NEType = type;

    for (int i = 0; i < tokens.length; i++) {
      tokens[i].setNE(type + "_" + (i == 0 ? "B" : "I"));
    }
    return this;
  }

  public Token[] getTokens() {
    return tokens;
  }

  public LocEntity setTokens(Token[] token) {
    this.tokens = token;
    return this;
  }

  public double getLatitude() {
    return latitude;
  }

  public LocEntity setLatitude(double latitude) {
    this.latitude = latitude;
    return this;
  }

  public double getLongitude() {
    return longitude;
  }

  public LocEntity setLongitude(double longitude) {
    this.longitude = longitude;
    return this;
  }
/**
 * tokStart, toke End, NEType, tokens are necessary.
 * Geonames ID, lat and lon are not.
 * @param tokStart
 * @param tokEnd
 * @param NEType
 * @param tokens
 */
  public LocEntity(int tokStart, int tokEnd, String NEType, Token[] tokens) {
    this.NEType = NEType;
    toksStart =tokStart;
    toksEnd = tokEnd;
    this.tokens=tokens;
    
    latitude = longitude = -999;
    this.geonamesEntry=null;
  }

  @Override
  public String toString() {
    String s =  "["+NEType+"] " ;
    for ( Token t : tokens)
      s += t.getToken()+" ";
    s+= " [ " + toksStart + " " + toksEnd + "]" + (Arrays.asList(tokens)) + " ["
            + latitude + "," + longitude + "]";
    return s;
  }

  StringBuffer sb;

  public String getStringTokens() {
    sb = new StringBuffer();
    for (Token t : tokens) {
      sb.append(t.getToken()).append(" ");
    }
    return sb.toString().trim();
  }

  @Override
  public boolean equals(Object lc) {
    if (lc instanceof LocEntity) {
      LocEntity le = (LocEntity) lc;
      if (le.getStringTokens().equals(this.getStringTokens()) && le.toksStart == this.toksStart
              && le.toksEnd == this.toksEnd
              && le.NEType.equals(this.NEType))
        return true;
    }
    return false;
  }

  public void setGeonamesId(String string) {
    // TODO Auto-generated method stub
    this.geonamesId = string;
    
  }
}
