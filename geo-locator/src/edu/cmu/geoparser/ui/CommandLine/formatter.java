package edu.cmu.geoparser.ui.CommandLine;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.cybozu.labs.langdetect.LangDetectException;

import edu.cmu.geoparser.Disambiguation.ContextDisamb;
import edu.cmu.geoparser.model.Tweet;
import edu.cmu.geoparser.nlp.languagedetector.LangDetector;
import edu.cmu.geoparser.nlp.ner.FeatureExtractor.FeatureGenerator;
import edu.cmu.geoparser.parser.english.EnglishParser;
import edu.cmu.geoparser.parser.spanish.SpanishParser;
import edu.cmu.geoparser.parser.utils.ParserUtils;
import edu.cmu.geoparser.resource.trie.IndexSupportedTrie;

public class formatter {

  public static void main(String argv[]) throws IOException {
    boolean misspell = argv[0].equals("-mis") ? true : false;
    String dicPath = argv[1]; // = "GeoNames/allCountries.txt";// gazetteer from geonames
    String indexPath = argv[2]; // index path
    String input = argv[3];// = "tweet.csv";//to be determined.// test file path
    String output = argv[4];// = "output2.csv"; //output file path

    IndexSupportedTrie topotrie = new IndexSupportedTrie(dicPath,indexPath, true, false);

    EnglishParser enparser = new EnglishParser("res/", topotrie, false);
    SpanishParser esparser = new SpanishParser("res/", topotrie, false);
    ContextDisamb c = new ContextDisamb();
    LangDetector lang = new LangDetector("res/langdetect.profile");

    CsvReader csvr = new CsvReader(input, ',', Charset.forName("utf-8"));
    csvr.readHeaders();
    CsvWriter writer = new CsvWriter(output, ',', Charset.forName("utf-8")); // write

    writer.writeRecord(new String[] { "SPANISH TWEETS", "STREETS", "BUILDINGS", "TOPONYMS",
        "ABBREVIATION" });

    List<String> match = null;
    List<String> reducematch;
    Tweet t = new Tweet();

    int i = 1;
    while (csvr.readRecord()) {
      if ((++i) % 100 == 0)
        System.out.print(i + "...");
      // if(i>2000) break;
      String estweet = csvr.get(csvr.getHeaders()[0]);// System.out.println(tweet);
      if (estweet.length() == 0) {
        continue;
      }

      t.setText(estweet);
      t.setMatches(null);
      String language = null;

      // Language Detection

      try {
        language = lang.detect(estweet);
      } catch (LangDetectException e) {
        System.err.println("Language Detection Error.");
        // continue;
      }
      if (language.equals("en")) {
        match = enparser.parse(t);
      } else if (language.equals("es")) {
        match = esparser.parse(t);
      } else {
        // System.err.println("Neither English nor Spanish.");
        match = enparser.parse(t);
        // continue;
      }
      // Generate Matches
      if (match == null) {
        // System.out.println("No location.");
        writer.writeRecord(new String[] { estweet, "", "", "", "" });
      } else {
        reducematch = match;// ParserUtils.ResultReduce(match);
        // no match after reducing.
        String st = " ", bd = " ", tp = " ", ab = " ";
        if (reducematch.size() != 0) {
          for (String s : reducematch) {
            if (s.startsWith("st") || s.startsWith("ST"))
              st += " " + s;
            else if (s.startsWith("bd") || s.startsWith("BD"))
              bd += " " + s;
            else if (s.startsWith("tp") || s.startsWith("TP"))
              tp += " " + s;
            else if (s.startsWith("ab") || s.startsWith("AB"))
              ab += " " + s;
          }
          writer.writeRecord(new String[] { estweet, st, bd, tp, ab });
        } else {
          writer.writeRecord(new String[] { estweet, "", "", "", "" });
        }
      }
    }
    writer.close();
    csvr.close();
  }
}
