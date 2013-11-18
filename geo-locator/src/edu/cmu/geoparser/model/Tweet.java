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

import java.util.List;

import twitter4j.Status;

public class Tweet {
  String id;

  Status status;

  Sentence sentence;

  List<LocEntity> geoLocations;

  public Tweet(){
  }
  public Tweet(String tweetStr){
    this.sentence = new Sentence(tweetStr);
  }
  public String getId() {
    return id;
  }

  public Tweet setId(String id) {
    this.id = id;
    return this;
  }

  public Status getStatus() {
    return status;
  }

  public Tweet setStatus(Status status) {
    this.status = status;
    return this;
  }

  public Sentence getSentence() {
    return sentence;
  }

  public Tweet setSentence(Sentence sentence) {
    this.sentence = sentence;
    return this;
  }

  public Tweet setSentence(String sent){
    if (sentence==null)
      sentence = new Sentence(sent);
    sentence.setSentenceString(sent);
    return this;
  }
  public List<LocEntity> getGeoLocations() {
    return geoLocations;
  }

  public Tweet setGeoLocations(List<LocEntity> geoLocations) {
    this.geoLocations = geoLocations;
    return this;
  }

  @Override
  public String toString() {

    return "tweet id: " + id + ", text : " + sentence;
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

}
