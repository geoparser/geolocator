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
package edu.cmu.geoparser.ui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import edu.cmu.geoparser.Disambiguation.ContextDisamb;
import edu.cmu.geoparser.io.GetReader;
import edu.cmu.geoparser.io.GetWriter;
import edu.cmu.geoparser.model.Tweet;
import edu.cmu.geoparser.parser.english.EnglishParser;
import edu.cmu.geoparser.resource.gazindexing.Index;
import edu.cmu.geoparser.resource.gazindexing.CollaborativeIndex.CollaborativeIndex;
public class LDCTest {

  public static void main(String argv[]) throws IOException {

   Index ci =  new CollaborativeIndex().config("GazIndex/StringIndex",
            "GazIndex/InfoIndex", "mmap", "mmap").open();
    EnglishParser enparser = new EnglishParser("res/", ci, false);
    ContextDisamb c = new ContextDisamb();
    String s = null, content = null, tags = null;
    BufferedReader br = GetReader.getUTF8FileReader("PURGE.csv");
    BufferedWriter bw = GetWriter.getFileWriter("compare.csv");
    List<String> matches = null;
    while ((s = br.readLine()) != null) {
      String[] toks = s.split("\t");

      // store the content and tag
      content = toks[0];
      if (toks.length < 2)
        continue;
      tags = toks[1];

      // get parsed result
      matches = enparser.parse(new Tweet(content));
      HashSet<String> topo = new HashSet<String>();
      for (String match : matches)
        topo.add(match.substring(3, match.length() - 3));
      HashMap<String, String[]> dis = c.returnBestTopo(ci, topo);

      // write into file
      bw.write(content + "\t");
      bw.write(tags + "\t");
      if (dis != null)
        for (Entry<String, String[]> d : dis.entrySet())
          bw.write(d.getKey() + " " + d.getValue()[0] + " " + d.getValue()[1] + " "
                  + d.getValue()[2] + ",");
      bw.write("\n");
    }// end while
    bw.close();
  }// end main

}// end class
