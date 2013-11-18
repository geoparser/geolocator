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
package edu.cmu.geoparser.parser.english;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.geoparser.model.LocEntity;
import edu.cmu.geoparser.model.Sentence;
import edu.cmu.geoparser.model.Token;
import edu.cmu.geoparser.model.Tweet;
import edu.cmu.geoparser.nlp.NERFeatureFactory;
import edu.cmu.geoparser.nlp.ner.FeatureExtractor.FeatureGenerator;
import edu.cmu.geoparser.nlp.tokenizer.EuroLangTwokenizer;
import edu.cmu.geoparser.parser.NERTagger;
import edu.cmu.geoparser.parser.ParserFactory;
import edu.cmu.geoparser.resource.ResourceFactory;
import edu.cmu.geoparser.resource.gazindexing.CollaborativeIndex.CollaborativeIndex;
import edu.cmu.minorthird.classify.ClassLabel;
import edu.cmu.minorthird.classify.Example;
import edu.cmu.minorthird.classify.Feature;
import edu.cmu.minorthird.classify.MutableInstance;
import edu.cmu.minorthird.classify.sequential.SequenceClassifier;
import edu.cmu.minorthird.util.IOUtil;

public class EnglishMTNERParser implements NERTagger {

  SequenceClassifier model;

  FeatureGenerator fg;

  /**
   * Default protected constructor
   * 
   * @throws IOException
   * 
   */

  private static EnglishMTNERParser emtparser;

  public static EnglishMTNERParser getInstance() {
    if (emtparser == null) {
      String encrfname = "res/en/enNER-crf-final.model";
      try {
        return new EnglishMTNERParser(encrfname, NERFeatureFactory.getInstance("en"));
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return emtparser;
  }

  public EnglishMTNERParser(String modelname, FeatureGenerator featureg) {
    try {
      model = (SequenceClassifier) IOUtil.loadSerialized(new java.io.File(modelname));
      this.fg = featureg;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // Identifying location names by NER
  Example[] examples;

  Sentence tweetSentence;

  public List<LocEntity> parse(Tweet tweet) {
    tweetSentence = tweet.getSentence();
    EuroLangTwokenizer.tokenize(tweetSentence);
    examples = new Example[tweet.getSentence().tokenLength()];

    // instances that is converted to feature list. Some features are stored in the tokens.
    List<Feature[]> feature_instances = fg.extractFeature(tweetSentence);

    for (int i = 0; i < examples.length; i++) {

      ClassLabel label = new ClassLabel("NA");
      MutableInstance instance = new MutableInstance("0", Integer.toString(i));

      Feature[] features = feature_instances.get(i);

      for (int j = 0; j < features.length; j++) {
        instance.addBinary(features[j]);
      }
      examples[i] = new Example(instance, label);
      // System.out.println(examples[i].toString());
    }
    ClassLabel[] resultlabels = model.classification(examples);
    /*
     * for (int i = 0; i < resultlabels.length; i++) {
     * System.out.print(resultlabels[i].bestClassName() + " "); }
     */
    List<LocEntity> locs = new ArrayList<LocEntity>();

    /**
     * rewrite the loc-entity generation, to support positions.
     */
    int startpos = -1, endpos = -1;
    String current = "O", previous = "O";
    for (int k = 0; k < resultlabels.length; k++) {
      if (k > 0)
        previous = current;
      current = resultlabels[k].bestClassName();
      if (current.equals("O"))
        if (previous.equals("O"))
          continue;
        else {
          endpos = k - 1;
          System.out.println(startpos + " " + endpos + " " + previous);
          Token[] t = new Token[endpos - startpos + 1];
          for (int i = startpos; i <= endpos; i++) {
            t[i - startpos] = tweet.getSentence().getTokens()[i].setNE(previous);
          }
          LocEntity le = new LocEntity(startpos, endpos, previous, t);
          locs.add(le);
        }
      else if (previous.equals("O"))
        startpos = k;
      else
        endpos = k;

    }
    return locs;
  }

  public static void main(String argv[]) throws IOException {

    String s = "I have been studying at Schenley Park for two years.";
    Tweet t = new Tweet(s);
    NERTagger enner =ParserFactory.getEnNERParser();
    System.out.println(enner.parse(t));
  }
}
