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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.geoparser.common.StringUtil;
import edu.cmu.geoparser.model.LocEntity;
import edu.cmu.geoparser.model.Sentence;
import edu.cmu.geoparser.model.Token;
import edu.cmu.geoparser.model.Tweet;
import edu.cmu.geoparser.nlp.NERFeatureFactory;
import edu.cmu.geoparser.nlp.ner.FeatureExtractor.FeatureGenerator;
import edu.cmu.geoparser.nlp.tokenizer.EuroLangTwokenizer;
import edu.cmu.geoparser.parser.ParserFactory;
import edu.cmu.geoparser.parser.TPParser;
import edu.cmu.geoparser.parser.utils.ParserUtils;
import edu.cmu.geoparser.resource.ResourceFactory;
import edu.cmu.geoparser.resource.gazindexing.CollaborativeIndex.CollaborativeIndex;

public class EnglishRuleToponymParser implements TPParser {

  static final int GRAM = 5;

  static String[] ngram;

  static String[] posngram;

  static boolean allstopwords;

  static ArrayList<String> results;

  static Pattern gazpattern = Pattern.compile("[AN\\^G]*[N\\^G]");

  /**
   * Gaz Matching. The parser only lookup the token array. This parser does not tokenize the input
   * string for efficiency reasons.
   */

  FeatureGenerator fgen;

  public EnglishRuleToponymParser(FeatureGenerator fgen) {
    this.fgen = fgen;
  }

  List<LocEntity> les;

  public List<LocEntity> parse(Tweet tweet) {
    les = new ArrayList<LocEntity>();

    Sentence tweetSent = tweet.getSentence();
    EuroLangTwokenizer.tokenize(tweetSent);
    fgen.getPostagger().tag(tweetSent);
    Token[] tokens = tweetSent.getTokens();

    List<Token> temptokens = new ArrayList<Token>();
    for (Token t : tokens) {
      if (t.getToken().length() > 1 && t.getToken().startsWith("#")
              && t.getToken().charAt(1) != '#')
        temptokens.add(t.clone().setToken(t.getToken().substring(1)));
      else
        temptokens.add(t);
    }
    tokens = temptokens.toArray(new Token[] {});

    String posstr = "";
    for (int i = 0; i < tweetSent.tokenLength(); i++)
      posstr += tweetSent.getTokens()[i].getPOS();

    System.out.println(" POS Tagging: " + posstr);

    List<String> matches = new ArrayList<String>();

    // convert Tokens to Strings
    String[] toks = new String[tokens.length];
    for (int i = 0; i < toks.length; i++)
      toks[i] = tokens[i].getToken();

    Token[] countryToks, topoToks;// store the token array variable inside.

    // match countries without considering the part of speech.
    for (int i = 1; i < 5; i++) {
      String[] igrams = StringUtil.constructgrams(toks, i, true);
      if (igrams == null)
        continue;

      // current ngram starting position is j.
      // length is i, cause it's i-gram.
      for (int j = 0; j < igrams.length; j++) {
        if (ParserUtils.isCountry(igrams[j])) {
          String[] str = igrams[j].split(" ");
          int min = i;// minimal dimension for the igram
          if (str.length != i) {
            System.out.println("dimension not agree when unwrapping ngram in enTopoParser.");
            System.out.println("Proceed anyway. Discard the rest part in ngram.");
            min = Math.min(i, str.length);// if demension not agree, choose smaller one.
          }
          countryToks = new Token[min];
          for (int k = 0; k < min; k++) {
            countryToks[k] = new Token(str[k], tweet.getId(), j);
          }

          les.add(new LocEntity(j, j + min - 1, "tp", countryToks));

        }
      }
    }

    /**
     * match the noun phrases, and look up in the dictionary.
     */

    Matcher gazmatcher = gazpattern.matcher(posstr);
    while (gazmatcher.find()) {
      int n = gazmatcher.end() - gazmatcher.start();
      String[] subtoks = new String[n];
      // System.out.print("Found ");

      // record the starting point of the noun phrase, for adjusting the ngram starting point.
      int _offset = gazmatcher.start();

      for (int i = gazmatcher.start(); i < gazmatcher.end(); i++) {
        subtoks[i - gazmatcher.start()] = tokens[i].getToken();
        // System.out.print(tokens.get(i));
      }

      for (int i = 1; i < n + 1; i++) {
        // i gram. length of gram is i.
        String[] igrams = StringUtil.constructgrams(subtoks, i, true);
        // j-th igram. j is the starting point in the subtokens.
        // new starting point = offset + j
        for (int j = 0; j < igrams.length; j++) {
          if (fgen.getIndex().inIndex(igrams[j])) {
            if (ParserUtils.isFilterword(igrams[j]) || ParserUtils.isEsFilterword(igrams[j])) {
              continue;
            }
            String[] str = igrams[j].split(" ");
            int min = i;
            if (str.length != i) {
              System.out.println("dimension not agree when unwrapping ngram in enTopoParser.");
              System.out.println("Proceed anyway. Discard the rest part in ngram.");
              Math.min(i, str.length);// if demension not agree, choose smaller one.
            }
            topoToks = new Token[min];
            for (int k = 0; k < min; k++) {
              topoToks[k] = new Token(str[k], tweet.getId(), _offset + j);
            }

            les.add(new LocEntity(_offset + j, _offset + j + i - 1, "tp", topoToks));

          }
          // else
          // System.out.println(corkgram+" is not in the trie");
        }
      }
    }
    return les;
  }

  private static EnglishRuleToponymParser etpparser;

  public static EnglishRuleToponymParser getInstance() {
    if (etpparser == null)
      try {
        return new EnglishRuleToponymParser(NERFeatureFactory.getInstance("en"));
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    return etpparser;

  }

  public static void main(String argv[]) throws IOException {
    
    Tweet t = new Tweet();
    BufferedReader s = new BufferedReader(new InputStreamReader(System.in, "utf-8"));
    System.out.println(">");
    while (true) {
      String ss = s.readLine();
      if (ss.length() == 0)
        continue;
      t.setSentence(ss);
      double stime = System.currentTimeMillis();
      List<LocEntity> matches = ParserFactory.getEnToponymParser().parse(t);
      double etime = System.currentTimeMillis();
      System.out.println(matches);
      System.out.println(etime - stime + "\n>");
    }
  }
}
