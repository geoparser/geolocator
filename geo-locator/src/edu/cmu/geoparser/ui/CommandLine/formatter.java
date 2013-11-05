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
package edu.cmu.geoparser.ui.CommandLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import twitter4j.TwitterException;
import twitter4j.json.DataObjectFactory;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.cybozu.labs.langdetect.LangDetectException;

import edu.cmu.geoparser.Disambiguation.ContextDisamb;
import edu.cmu.geoparser.io.GetReader;
import edu.cmu.geoparser.model.Tweet;
import edu.cmu.geoparser.nlp.languagedetector.LangDetector;
import edu.cmu.geoparser.nlp.ner.FeatureExtractor.FeatureGenerator;
import edu.cmu.geoparser.parser.english.EnglishParser;
import edu.cmu.geoparser.parser.spanish.SpanishParser;
import edu.cmu.geoparser.parser.utils.ParserUtils;
import edu.cmu.geoparser.resource.trie.IndexSupportedTrie;

public class formatter {

  public static void main(String argv[]) throws IOException {
    argv[0] ="-mis";
    argv[1] = "/Users/Indri/Eclipse_workspace/GeoNames/cities1000.txt";
    argv[2] ="/Users/Indri/Eclipse_workspace/GazIndex";
    argv[3] = "SampleInput/jsonTweets.txt";
    argv[4] = "-json";
    argv[5] = "SampleOutput/jsonTweets.out.csv";
    boolean misspell = argv[0].equals("-mis") ? true : false;
    String dicPath = argv[1]; // = "GeoNames/allCountries.txt";// gazetteer from geonames
    String indexPath = argv[2]; // index path
    String input = argv[3];// = "tweet.csv";//to be determined.// test file path
    String type = argv[4]; // -json or -text
    String output = argv[5];// = "output2.csv"; //output file path

    IndexSupportedTrie topotrie = new IndexSupportedTrie(dicPath, indexPath, true, false);

    EnglishParser enparser = new EnglishParser("res/", topotrie, false);
    ContextDisamb c = new ContextDisamb();
    LangDetector lang = new LangDetector("res/langdetect.profile");

    BufferedReader reader = GetReader.getUTF8FileReader(argv[3]);
    CsvWriter writer = new CsvWriter(output, ',', Charset.forName("utf-8")); // write

    writer.writeRecord(new String[] { "SPANISH TWEETS", "LOCATIONS" });

    String line = null;
    while ((line = reader.readLine()) != null) {
      line = line.trim();
      if (line.length() == 0)
        continue;
      Tweet t = new Tweet();
      String text = null;
      if (argv[4].equals("-text"))
        text = line;
      else
        try {
          text = (DataObjectFactory.createStatus(line.trim()).getText());
        } catch (TwitterException e) {
          // TODO Auto-generated catch block
          System.err.println("JSON format corrupted, or no content.");
          continue;
        }
      t.setText(text);
      List<String> match = enparser.parse(t);
      // Generate Matches
      if (match == null || match.size()==0) {
        /**
         * write blank result and the line itself if no match found.
         */
        writer.writeRecord(new String[] { text, "" });
        continue;
      }
      HashSet<String> reducedmatch = new HashSet<String>();
      for (String s : match)
        reducedmatch.add(s.substring(3, s.length() - 3));

      // Disambiguate topo
      HashMap<String, String[]> result = c.returnBestTopo(topotrie, reducedmatch);

      if (result == null) {
        System.out.println("No GPS for any location is found.");
      } else {
        System.out.println("The grounded location(s) are:");
        String topoStr = "";
        for (String topo : result.keySet())
          topoStr += "["+(topo + ": " + result.get(topo)[2] + " " + result.get(topo)[0] + " "
                  + result.get(topo)[1]) + "] ";
        writer.writeRecord(new String[]{text,topoStr});
      }

    }
    reader.close();
    writer.close();
  }
}
