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
package edu.cmu.geoparser.Disambiguation;

import edu.cmu.geoparser.common.StringUtil;
import edu.cmu.geoparser.io.GetReader;
import edu.cmu.geoparser.model.GazEntryAndFeature;
import edu.cmu.geoparser.model.LocEntity;
import edu.cmu.geoparser.model.Sentence;
import edu.cmu.geoparser.model.Tweet;
import edu.cmu.geoparser.model.TweetExample;
import edu.cmu.geoparser.nlp.ner.FeatureExtractor.FeatureGenerator;
import edu.cmu.geoparser.nlp.tokenizer.EuroLangTwokenizer;
import edu.cmu.geoparser.resource.ResourceFactory;
import edu.cmu.geoparser.resource.gazindexing.Index;
import edu.cmu.geoparser.resource.gazindexing.CollaborativeIndex.CollaborativeIndex;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import libsvm.svm_node;

import org.apache.lucene.document.Document;

/**
 * In this class, strings are operated on character level, not string level.
 * 
 * @author indri
 * 
 */
public class Tweetdisamb {

  public Tweetdisamb() {
  }

  public static void main(String argv[]) throws IOException {
    Tweetdisamb td = new Tweetdisamb();
    TweetExample[] examples = td
            .loadExamples("/Users/Indri/Documents/Research_data/Disambiguation/tagging/1500removed.pure.csv");
    LocEntity[] topos;
    for (int e = 0; e < examples.length; e++) {
      System.out.println(e);
      topos = examples[e].getToponyms();
      for (int i = 0; i < topos.length; i++) {
        // System.out.println(i);
        ArrayList<Document> candidates = td.getCandidates(topos[i]);
        if (candidates == null)
          continue;
        double minDist = 99999, tempdist;
        Document tempcorrectChoice = null;
        for (int j = 0; j < candidates.size(); j++) {
          double clat = Double.parseDouble(candidates.get(j).get("LATITUDE"));
          double clon = Double.parseDouble(candidates.get(j).get("LONGTITUDE"));
          double entitylat = topos[i].getLatitude();
          double entitylon = topos[i].getLongitude();
          tempdist = (Math.pow(clat - entitylat, 2) + Math.pow(clon - entitylon, 2));
          if (tempdist < minDist) {
            tempcorrectChoice = candidates.get(j);
            minDist = tempdist;
          }
        }
        if (minDist < 10) {
          topos[i].setGeonamesEntry(tempcorrectChoice);
          topos[i].setGeonamesId(tempcorrectChoice.get("ID"));
        }
        if (minDist > 10) {
          System.out.println(topos[i]);
          System.out.println(minDist);
          System.out.println(tempcorrectChoice);
        }
      }
    }
  }

  public TweetExample[] loadExamples(String filename) throws IOException {
    ArrayList<TweetExample> tes = new ArrayList<TweetExample>();
    BufferedReader br = GetReader.getUTF8FileReader(filename);
    String line = null;
    while ((line = br.readLine()) != null) {
      line = line.toLowerCase();
      String[] part = line.split("\t");
      String tweet = part[0];
      Tweet t = new Tweet(tweet);
      EuroLangTwokenizer.tokenize(t.getSentence());
      String locs = part[1];
      String lat = part[2];
      String lon = part[3];
      String userLoc = part[4].trim();
      String timezone = part[5].trim();
      String userDsc = part[6].trim();
      TweetExample te = new TweetExample().setText(tweet).setLatitude(Double.parseDouble(lat))
              .setLongitude(Double.parseDouble(lon)).setUserLocation(userLoc)
              .setUserDescription(userDsc);
      // handle locs
      locs = locs.substring(3, locs.length() - 3);
      String[] loc = locs.split("\\}tptp\\{");
      ArrayList<LocEntity> topos = new ArrayList<LocEntity>();
      ArrayList<Document> topodocs = new ArrayList<Document>();
      for (String l : loc) {
        String[] e = l.split("\\[");
        String str = e[0];
        Sentence phrase = new Sentence(str);
        EuroLangTwokenizer.tokenize(phrase);
        targetTagPhraseInSentence(t.getSentence(), phrase);
        String[] geo = e[1].substring(0, e[1].length() - 1).split(",");
        String elat = geo[0];
        String elon = geo[1];
        LocEntity le = new LocEntity(phrase.getTokens()[0].getPosition(),
                phrase.getTokens()[0].getPosition() + phrase.tokenLength() - 1, "tp",
                phrase.getTokens()).setLatitude(Double.parseDouble(elat)).setLongitude(
                Double.parseDouble(elon));
        topos.add(le);
      }
      te.setToponyms(topos.toArray(new LocEntity[topos.size()]));
      tes.add(te);
      System.out.println(te + "\n");
    }

    return tes.toArray(new TweetExample[] {});
  }

  /**
   * Because our tag is flawed, we have to find the target in the sentence, to generate the token
   * start and end position for each one of them.
   * 
   * @param sentence
   * @param phrase
   */
  private void targetTagPhraseInSentence(Sentence sentence, Sentence phrase) {
    // for each token in phrase
    // i-th phrase token
    for (int i = 0; i < phrase.tokenLength(); i++) {
      String lowPraseToken = phrase.getTokens()[i].getToken().toLowerCase();
      // j-th sentence token
      for (int j = 0; j < sentence.tokenLength(); j++) {
        String lowSentToken = sentence.getTokens()[j].getToken().toLowerCase();
        if (lowSentToken.equals(lowPraseToken)) {
          phrase.getTokens()[i].setPosition(j);
        }
      }
    }
  }

  /**
   * get the candidates of the locEntity as documents.
   * 
   * @param t
   * @return
   */
  public ArrayList<Document> getCandidates(LocEntity t) {
    String tok = t.getStringTokens();
    if (StringUtil.isTwitterHashTag(tok))
      return ResourceFactory.getClbIndex().getDocumentsByPhrase(tok.substring(1));
    else
      return ResourceFactory.getClbIndex().getDocumentsByPhrase(tok);
  }

  public svm_node[] featEx(int docIndex4loc, int locEntityIndexInExample, TweetExample tweetExample) {

    return null;
  }

  public void train(TweetExample[] examples) {
    LocEntity[] topos;
    for (int e = 0; e < examples.length; e++) {
      System.out.println(e);
      topos = examples[e].getToponyms();
      for (int i = 0; i < topos.length; i++) {
        
        // among all canidates, only one is correct, the others are wrong.
        ArrayList<Document> candidates = ResourceFactory.getClbIndex().getDocumentsByPhrase(
                topos[i].getStringTokens().startsWith("#") ? topos[i].getStringTokens()
                        .substring(1) : topos[i].getStringTokens());
        if (candidates == null)
          continue;
        double minDist = 99999, tempdist;
        Document tempcorrectChoice = null;
        for (int j = 0; j < candidates.size(); j++) {
          double clat = Double.parseDouble(candidates.get(j).get("LATITUDE"));
          double clon = Double.parseDouble(candidates.get(j).get("LONGTITUDE"));
          double entitylat = topos[i].getLatitude();
          double entitylon = topos[i].getLongitude();
          tempdist = (Math.pow(clat - entitylat, 2) + Math.pow(clon - entitylon, 2));
          if (tempdist < minDist) {
            tempcorrectChoice = candidates.get(j);
            minDist = tempdist;
          }
        }
        // This is the positive instance of the training data.
        if (minDist < 10) {
          topos[i].setGeonamesEntry(tempcorrectChoice);
          topos[i].setGeonamesId(tempcorrectChoice.get("ID"));
        }
      }
    }

  }

  public void test() {

  }

  // Helper
  private int targetLocationStringHelper(String text, LocEntity le) {
    // I dare use it here because the locentity only contains one token for training data.
    String loc = le.getStringTokens();
    return text.indexOf(loc);
  }

}
