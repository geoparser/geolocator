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
package edu.cmu.geoparser.parser.spanish;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

import edu.cmu.geoparser.common.StringUtil;
import edu.cmu.geoparser.model.Tweet;
import edu.cmu.geoparser.nlp.MisspellParser;
import edu.cmu.geoparser.nlp.ner.FeatureExtractor.FeatureGenerator;
import edu.cmu.geoparser.nlp.tokenizer.EuroLangTwokenizer;
import edu.cmu.geoparser.parser.TPParser;
import edu.cmu.geoparser.parser.utils.ParserUtils;
import edu.cmu.geoparser.resource.gazindexing.CollaborativeIndex.CollaborativeIndex;

public class SpanishRuleToponymParser implements TPParser {

  static final int GRAM = 5;

  static String[] ngram;

  static String[] posngram;

  static boolean allstopwords;

  static ArrayList<String> results;

  static String p1 = "d[an]";

  static String p2 = "[avn]";

  static String p3 = "[an][an]";

  static String p4 = "[an][an][an]";

  static String[] patterns = { p1, p2, p3, p4 };

  static Pattern gazpattern;

  FeatureGenerator fgen;

  /*
   * Gaz Matching. The parser only lookup the token array. This parser does not tokenize the input
   * string for efficiency reasons.
   */
  public SpanishRuleToponymParser(FeatureGenerator fgen) {
    this.fgen = fgen;
  }

  public List<String> parse(Tweet tweet) {
    // TODO Auto-generated method stub

    String text = tweet.getOrigText();
    List<String> tokens = EuroLangTwokenizer.tokenize(text);

    System.out.println("Tokenization : \n" + tokens.toString());

    for (int i = 0; i < tokens.size(); i++)
      if (tokens.get(i).startsWith("#"))
        tokens.set(i, tokens.get(i).substring(1));
    List<String> postags = fgen.getPostagger().tag(tokens);

    String posstr = "";
    for (int i = 0; i < postags.size(); i++)
      posstr += postags.get(i).charAt(0);

    System.out.println("POS : \n" + posstr);

    List<String> matches = new ArrayList<String>();

    // match countries without considering the part of speech.
    for (int i = 1; i < 5; i++) {
      String[] igrams = StringUtil.constructgrams(tokens.toArray(new String[] {}), i, true);
      for (String igram : igrams) {
        if (ParserUtils.isCountry(igram))
          if (!matches.contains("tp{" + igram + "}tp"))
            matches.add("tp{" + igram + "}tp");
      }
    }

    for (int j = 0; j < patterns.length; j++) {
      gazpattern = Pattern.compile(patterns[j]);
      Matcher gazmatcher = gazpattern.matcher(posstr);
      while (gazmatcher.find()) {
        // System.out.println("found");
        int n = gazmatcher.end() - gazmatcher.start();
        String[] subtoks = new String[n];
        for (int i = gazmatcher.start(); i < gazmatcher.end(); i++) {
          subtoks[i - gazmatcher.start()] = tokens.get(i);
        }
        for (int k = 1; k < n + 1; k++) {
          String[] kgrams = StringUtil.constructgrams(subtoks, k, true);
          for (String kgram : kgrams) {
            kgram = kgram.trim();

            String corkgram = null;
            corkgram = kgram;
            // match gaz entry in the chunk.
            if (fgen.getIndex().inIndex(corkgram)) {
              if (ParserUtils.isEsFilterword(corkgram)
              // &&Character.isLowerCase(kgram.charAt(0))
              ) {
                continue;
              }
              // if (ParserUtils.isEsFilterword(kgram)&&Character.isUpperCase(kgram.charAt(0))
              // &&
              // StringUtil.mostTokensUpperCased(tokens)
              // && tokens.size()>5
              // ) {
              // continue;
              // }
              // System.out.println("word to be added is: " + kgram);
              String[] grams = corkgram.split(" ");
              if (grams.length == 2) {
                if ((grams[0].equals("el") || grams[0].equals("la")))
                  if (ParserUtils.isEsFilterword(grams[1]))
                    continue;
              }
              if (!matches.contains("tp{" + corkgram.trim() + "}tp"))
                matches.add("tp{" + corkgram.trim() + "}tp");
            }
          }
        }
      }
    }
    return matches;
  }

  public static void main(String argv[]) throws IOException {
    
    System.out.println("NOT WORKING UNTILL FEATURE GENERATOR IS CREATED");

    CollaborativeIndex ci = new CollaborativeIndex().config("GazIndex/StringIndex",
            "GazIndex/InfoIndex", "mmap", "mmap").open();
    Tweet t = new Tweet();

    SpanishRuleToponymParser estopo = null;// new
    // SpanishRuleToponymParser(new
    // FeatureGenerator());
    String filename = "copied";
    // String filenames[] = new String[] { "1401-1999", "36701-36838",
    // "4701-5000", "5701-6000", "6701-7000" };

    CsvReader csvr = new CsvReader("trainingdata/translation/" + filename + ".csv", ',',
            Charset.forName("utf-8"));
    csvr.readHeaders();
    CsvWriter writer = new CsvWriter("output/" + filename + ".csv", ',', Charset.forName("utf-8")); // write
    writer.writeRecord(new String[] { "es tweet", "street", "building", "toponym", "esTP" });
    writer.endRecord();
    int i = 1;
    while (csvr.readRecord()) {
      System.out.println(i);

      String estweet = csvr.get(csvr.getHeaders()[1]);// System.out.println(tweet);
      String entweet = csvr.get(csvr.getHeaders()[2]);
      String street = csvr.get(csvr.getHeaders()[3]);
      String building = csvr.get(csvr.getHeaders()[4]);
      String toponym = csvr.get(csvr.getHeaders()[5]);
      String abbr = csvr.get(csvr.getHeaders()[6]);
      String locadj = csvr.get(csvr.getHeaders()[7]);
      // String abbrinfull = csvr.get(csvr.getHeaders()[8]);
      System.out.println(estweet);
      t.setMatches(null);

      writer.write(estweet);
      // writer.write(entweet);
      writer.write(street);
      writer.write(building);
      writer.write(toponym);
      // writer.write(abbr);
      // writer.write(locadj);
      // writer.write(abbrinfull);
      if (estweet.length() == 0 || entweet.length() == 0) {
        writer.endRecord();
        continue;
      }

      List<String> match;

      // toponym parsing
      t.setText(estweet);
      List<String> matches = estopo.parse(t);
      match = t.getMatches();
      writer.write(match.toString());
      t.setMatches(null);

      writer.endRecord();
      System.out.println(i + " ended.");
      i++;
      if (i > 200)
        break;
    }
    writer.close();
    csvr.close();
  }
}
