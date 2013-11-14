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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.cmu.geoparser.Disambiguation.ContextDisamb;
import edu.cmu.geoparser.io.GetReader;
import edu.cmu.geoparser.model.Tweet;
import edu.cmu.geoparser.parser.english.EnglishParser;
import edu.cmu.geoparser.resource.gazindexing.CollaborativeIndex.CollaborativeIndex;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.json.DataObjectFactory;

public class TwitterJsonParser {

  public static void main(String argv[]) throws IOException {

    /**
     * Preparation: initialize the parser.
     */
    String uri = "/Users/indri/Eclipse_workspace/";
    String geonames = uri + "GeoNames/cities1000.txt";
    String gazindex = uri + "GazIndex";
    CollaborativeIndex ci = new CollaborativeIndex().config("GazIndex/StringIndex",
            "GazIndex/InfoIndex", "mmap", "mmap").open();
    EnglishParser enparser = new EnglishParser("res/", ci, false);

    ContextDisamb c = new ContextDisamb();

    /**
     * read the tweets, one Json tweet per line.
     * 
     */
    BufferedReader br = GetReader.getUTF8FileReader("SampleInput/jsonTweets.txt");
    String line = null;
    while ((line = br.readLine()) != null) {

      // parse the tweet json file into a Status object( twitter4j convention).
      Status rawTweet = null;
      try {
        rawTweet = DataObjectFactory.createStatus(line.trim());
      } catch (TwitterException e) {
        System.err.println("JSON File is corrupted. Continue");
        /**
         * Do something to handle this problem. Ignore it, or output the JSON.
         */
        continue;
      }

      /**
       *  wrap it into our tweet object. Here we only copy the text.
       */
      Tweet aTweet = new Tweet();
      aTweet.setText(rawTweet.getText());

      
      /**
       *  The next several lines are identical to CmdInputParser.java
       */

      List<String> match = enparser.parse(aTweet);

      if (match == null || match.size() == 0) {
        System.out.println("No toponyms in text. Do something, then continue.");
        continue;
      } else { // if matches are found:
        HashSet<String> reducedmatch = new HashSet<String>();
        for (String s : match)
          reducedmatch.add(s.substring(3, s.length() - 3));

        HashMap<String, String[]> result = c.returnBestTopo(ci, reducedmatch);

        if (result == null) {
          System.out.println("No GPS for any location is found.");
        } else {
          System.out.println("The grounded location(s) are: (topo: country_state, latitude, longitude.");
          for (String topo : result.keySet())
            System.out.println(topo + ": " + result.get(topo)[2] + " " + result.get(topo)[0] + " "
                    + result.get(topo)[1]);
        }

      }
    }
  }
}
