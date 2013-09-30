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
import edu.cmu.geoparser.resource.trie.IndexSupportedTrie;

public class LDCTest {

  public static void main(String argv[]) throws IOException {

    IndexSupportedTrie topotrie = new IndexSupportedTrie("GeoNames/allCountries.txt", "GazIndex/",true, false);
    EnglishParser enparser = new EnglishParser("res/", topotrie, false);
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
      HashMap<String, String[]> dis = c.returnBestTopo(topotrie, topo);

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
