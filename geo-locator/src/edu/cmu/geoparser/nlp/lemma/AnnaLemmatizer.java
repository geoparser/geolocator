package edu.cmu.geoparser.nlp.lemma;

import is2.data.SentenceData09;
import is2.lemmatizer.Lemmatizer;
import is2.tools.Tool;

import java.util.ArrayList;
import java.util.List;
import edu.cmu.geoparser.nlp.tokenizer.EuroLangTwokenizer;

/**
 * The tokenizer from Mate-tools, an online google project The taggers are trained on CONLL-09
 * dataset.
 * 
 * @author indri
 * 
 */
public class AnnaLemmatizer implements edu.cmu.geoparser.nlp.Lemmatizer {

  Tool lemmatizer;

  SentenceData09 i;

  public AnnaLemmatizer(String f) {
    System.err.println("Loading: " + f);
    lemmatizer = new Lemmatizer(f);
    i = new SentenceData09();
  }

  // shows how to parse a sentences and call the tools
  public List<String> lemmatize(List<String> text) {

    // Create a data container for a sentence

    String[] s = new String[text.size() + 1];
    s[0] = "root";
    for (int j = 0; j < text.size(); j++)
      s[j + 1] = text.get(j);
    i.init(s);
    lemmatizer.apply(i);

    List<String> tags = new ArrayList<String>(i.plemmas.length - 1);
    for (int j = 1; j < i.plemmas.length; j++)
      tags.add(i.plemmas[j]);
    return tags;

  }

  public static void main(String argv[]) {
    //String s = "What a Sunday! Miss anything? We've got you covered in @Windows Week 2 Review (it's not too soon to talk playoffs) È http://bit.ly/18BpV3z ";
    String s     ="A U.S. official said Mr. Alexis joined the Navy in 2007, and was kicked out of the military in 2011 as a result of a gun arrest in Fort Worth, Texas. ";
    List<String> text = EuroLangTwokenizer.tokenize(s);
    System.out.println(text);
    AnnaLemmatizer en = new AnnaLemmatizer(
            "res/en/CoNLL2009-ST-English-ALL.anna-3.3.lemmatizer.model");
    int i = 0;
    List<String> tags = null;
    double start = System.currentTimeMillis();
    while (i++ < 100)
      tags = en.lemmatize(text);
    // System.out.println(tags);
    double end = System.currentTimeMillis();
    System.out.println(end-start);
    System.out.println(tags);
  }
}
