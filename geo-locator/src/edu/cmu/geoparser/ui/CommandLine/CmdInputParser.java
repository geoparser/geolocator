package edu.cmu.geoparser.ui.CommandLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.cybozu.labs.langdetect.LangDetectException;

import edu.cmu.geoparser.Disambiguation.ContextDisamb;
import edu.cmu.geoparser.io.GetReader;
import edu.cmu.geoparser.model.Tweet;
import edu.cmu.geoparser.nlp.languagedetector.LangDetector;
import edu.cmu.geoparser.nlp.ner.FeatureExtractor.FeatureGenerator;
import edu.cmu.geoparser.parser.english.EnglishParser;
import edu.cmu.geoparser.parser.spanish.SpanishParser;
import edu.cmu.geoparser.resource.trie.IndexSupportedTrie;

/**
 * This demo shows the on-the-fly tagging of the sample text or JSON
 * 
 */
public class CmdInputParser {

  public static void main(String argv[]) throws IOException {

    IndexSupportedTrie topotrie = new IndexSupportedTrie("GeoNames/allCountries.txt","GazIndex/", true, false);

    EnglishParser enparser = new EnglishParser("res/", topotrie, false);
    // SpanishParser esparser = new SpanishParser("res/", topotrie, false);

    ContextDisamb c = new ContextDisamb();
    LangDetector lang = new LangDetector("res/langdetect.profile");

    String text = null;
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in, "utf-8"));
    System.out.print(">");

    while ((text = br.readLine()) != null) {

      if (text.length() == 0)
        continue;

      List<String> match = null;
      Tweet t = new Tweet(text);

      // Parse topo
      match = enparser.parse(t);

      if (match == null) {
        System.out.println("No toponyms in text.");
        continue;
      } else if (match.size() == 0) {
        System.out.println("No toponyms in text.");
        continue;
      } else { // if matches are found:
        System.out.println("The locations found :");
        System.out.println(match);

        HashSet<String> reducedmatch = new HashSet<String>();
        for (String s : match)
          reducedmatch.add(s.substring(3, s.length() - 3));

        // Disambiguate topo
        HashMap<String, String[]> result = c.returnBestTopo(topotrie, reducedmatch);

        if (result == null) {
          System.out.println("No GPS for any location is found.");
        } else {
          System.out.println("The grounded location(s) are:");
          for (String topo : result.keySet())
            System.out.println(topo + ": " + result.get(topo)[2] + " " + result.get(topo)[0] + " "
                    + result.get(topo)[1]);
        }
      }
      System.out.print(">");
    }// end of while

  }
}
