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
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.lucene.document.Document;

import edu.cmu.geoparser.Disambiguation.ContextDisamb;
import edu.cmu.geoparser.model.LocEntity;
import edu.cmu.geoparser.model.Sentence;
import edu.cmu.geoparser.model.Tweet;
import edu.cmu.geoparser.parser.english.EnglishParser;
import edu.cmu.geoparser.resource.gazindexing.CollaborativeIndex.CollaborativeIndex;

/**
 * This demo shows the on-the-fly tagging of the sample text or JSON
 * 
 */
public class CmdInputParser {

  public static void main(String argv[]) throws IOException {

    boolean misspell = argv[0].equals("mis") ? true : false;

    /**
     * Use the collaborative index version instead of the in-memory version. Which aims to reduce
     * memory usage.
     */
    CollaborativeIndex ci = new CollaborativeIndex().config("GazIndex/StringIndex",
            "GazIndex/InfoIndex", "mmap", "mmap").open();
    /**
     * This is the main construction function for the English parser. The Spanish parser has the
     * same form.
     */
    EnglishParser enparser = new EnglishParser("res/", ci, false);

    /**
     * Initialize a context disambiguation class c.
     */
    ContextDisamb c = new ContextDisamb();

    String text = null;
    Tweet t = new Tweet();
    ArrayList<Document> cand;

    BufferedReader br = new BufferedReader(new InputStreamReader(System.in, "utf-8"));
    System.out.print(">");

    while ((text = br.readLine()) != null) {
      if (text.length() == 0)
        continue;
      t.setSentence(new Sentence().setSentenceString(text));
      List<LocEntity> match = enparser.parse(t);
      if (match == null || match.size() == 0) {
        System.out.println("No toponyms in text.");
        continue;
      }
      /**
       * if matches are found:
       */
      //System.out.println("The locations found :\n" + match);

      /**
       * The parsing output will give you parsed results in the form of tp{XXX}tp, or TP{XXX}TP. The
       * next lines are for stripping the first and last three characters.
       */      
      
      for (LocEntity s : match) {
        System.out.println("["+s+"]");
        cand = ci.getDocumentsByPhraseStrict(s.getTokenString());
        if (cand == null) {
          System.out.println("No results.");
          continue;
        }
        for (int i = 0; i < cand.size(); i++) {
          System.out.println();
          System.out.println("ID : " + cand.get(i).get("ID"));
          System.out.println("Lat : " + cand.get(i).get("LATITUDE"));
          System.out.println("Lon : " + cand.get(i).get("LONGTITUDE"));
          System.out.println("POP : " + cand.get(i).get("POPULATION"));
          System.out.println("CNTRY_STATE : " + cand.get(i).get("COUNTRYSTATE"));
          System.out.println("FEATURE : " + cand.get(i).get("FEATURE"));
          System.out.println("TIMEZONE : " + cand.get(i).get("TIMEZONE"));
        }
      }
      System.out.print(">");
    }// end of while
  }
}
