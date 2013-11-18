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
package edu.cmu.geoparser.nlp.ner.FeatureExtractor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.csvreader.CsvReader;

import edu.cmu.geoparser.common.StringUtil;
import edu.cmu.geoparser.io.GetWriter;
import edu.cmu.geoparser.model.Sentence;
import edu.cmu.geoparser.nlp.Lemmatizer;
import edu.cmu.geoparser.nlp.NLPFactory;
import edu.cmu.geoparser.nlp.POSTagger;
import edu.cmu.geoparser.nlp.lemma.AnnaLemmatizer;
import edu.cmu.geoparser.nlp.pos.ESAnnaPOSTagger;
import edu.cmu.geoparser.nlp.tokenizer.EuroLangTwokenizer;
import edu.cmu.geoparser.parser.utils.ParserUtils;
import edu.cmu.geoparser.resource.ResourceFactory;
import edu.cmu.geoparser.resource.dictionary.Dictionary;
import edu.cmu.geoparser.resource.dictionary.Dictionary.DicType;
import edu.cmu.geoparser.resource.gazindexing.Index;
import edu.cmu.minorthird.classify.Feature;

public class FeatureGenerator {

  HashSet<String> preposition, countries;

  Dictionary prepdict, countrydict;

  EuroLangTwokenizer tokenizer;

  Lemmatizer lemmatizer;

  POSTagger postagger;

  Index index;

  public Index getIndex() {
    return index;
  }

  @SuppressWarnings("unchecked")
  public FeatureGenerator(String language, Index index, String resourcepath) {
    // initialize dictionary to lookup.
    // "geoNames.com/allCountries.txt"
    this.index = index;

    if (language.equals("en") || language.equals("es"))
      tokenizer = new EuroLangTwokenizer();
    else
      System.err.println("No proper tokenizer found for this language.");

    if (language.equals("en"))
      // lemmatizer = new AnnaLemmatizer(resourcepath + language+
      // "/CoNLL2009-ST-English-ALL.anna-3.3.lemmatizer.model");
      lemmatizer = NLPFactory.getEnUWStemmer();
    else if (language.equals("es"))
      lemmatizer = new AnnaLemmatizer(resourcepath + language
              + "/CoNLL2009-ST-Spanish-ALL.anna-3.3.lemmatizer.model");
    if (language.equals("en"))
      postagger = NLPFactory.getEnPosTagger();
    else if (language.equals("es"))
      postagger = new ESAnnaPOSTagger(resourcepath + language
              + "/CoNLL2009-ST-Spanish-ALL.anna-3.3.postagger.model");
    try {
      prepdict = Dictionary.getSetFromListFile(resourcepath + language + "/prepositions.txt", true,
              true);
      preposition = (HashSet<String>) prepdict.getDic(DicType.SET);
    } catch (IOException e) {

      e.printStackTrace();
    }
  }

  static int statstreet = 0;

  static int statbuilding = 0;

  static int stattoponym = 0;

  static int statabbr = 0, statadj = 0;

  String tweet;

  public static void main(String argv[]) throws IOException, InterruptedException {
    // argv[0] = "es";
    // argv[1] = "GeoNames/allCountries.txt";
    argv[2] = "res/";

    String featuretype = "-3tok*.3pres.2cap.3caps.1pos.2pos.1gaz.1gazs.1cty.1ctys.-3-1prep";
    // ="ct-only";
    // ="allbutpos";
    FeatureGenerator fgen = new FeatureGenerator(argv[0], ResourceFactory.getClbIndex(),
            argv[2]);
    String traintest[] = new String[] { "train", "test" };
    for (int tt = 0; tt < 2; tt++) {
      CsvReader entries = new CsvReader("trainingdata/" + argv[0] + "NER/" + traintest[tt]
              + "/raw.csv", ',', Charset.forName("utf-8"));

      BufferedWriter fwriter = GetWriter.getFileWriter("trainingdata/" + argv[0] + "NER/"
              + traintest[tt] + "/" + featuretype + "-features.txt");

      entries.readHeaders();

      int i = 1;
      int counttweets = 0;
      while (entries.readRecord()) {
        i++;
        if (i % 200 == 0)
          System.out.println(i + " ");

        String tag = entries.get(entries.getHeaders()[0]);

        if (tag.equals("") == false)
          continue;// filter language
        else
          counttweets++; // count spanish tweets

        String tweet = entries.get(entries.getHeaders()[1]);// System.out.println(tweet);
        tweet = tweet.replace("-", " - ");
        String street = entries.get(entries.getHeaders()[2]);
        street = street.replace("-", " - ");
        String building = entries.get(entries.getHeaders()[3]);
        building = building.replace("-", " - ");
        String toponym = entries.get(entries.getHeaders()[4]);
        toponym = toponym.replace("-", " - ");
        String abbr = entries.get(entries.getHeaders()[5]);
        abbr = abbr.replace("-", " - ");
        // String locadj = entries.get(entries.getHeaders()[6]);
        // locadj = locadj.replace("-", " - ");

        String[] t_tweet = (EuroLangTwokenizer.tokenize(tweet)).toArray(new String[] {});
        String[] t_street = street.trim().split(",");// without
        // trimming
        String[] t_building = building.trim().split(",");// without
        // trimming
        String[] t_toponym = toponym.trim().split(",");// without
        // trimming
        String[] t_abbr = abbr.trim().split(",");// without
        // trimming
        // String[] t_locadj = locadj.trim().split(",");

        if (!EmptyArray(t_street))
          statstreet += t_street.length;
        if (!EmptyArray(t_building))
          statbuilding += t_building.length;
        if (!EmptyArray(t_toponym))
          stattoponym += t_toponym.length;
        if (!EmptyArray(t_abbr))
          statabbr += t_abbr.length;
        // if (!EmptyArray(t_locadj))
        // statadj += t_locadj.length;
        if (t_tweet.length == 0)
          continue;

        // labeling
        HashMap<Integer, String> f_loc = safeTag(t_tweet, t_street, t_building, t_toponym, t_abbr);

        Sentence sent = new Sentence(tweet);
        List<Feature[]> tweetfeatures = fgen.extractFeature(sent);

        for (int j = 0; j < tweetfeatures.size(); j++) {
          initialFeatureWriter();
          for (Feature f : tweetfeatures.get(j)) {
            append(f.toString());
            // System.out.println(f.toString());
          }

          // location class.
          String loctag = "O";
          if (f_loc.containsKey(j))
            loctag = f_loc.get(j);
          // loctag = "LOC";
          append(loctag);
          fwriter.write(emit());
        }

        fwriter.write("\n");
      }
      System.out.println(statstreet);
      System.out.println(statbuilding);
      System.out.println(stattoponym);
      System.out.println(statabbr);
      // System.out.println(statadj);
      System.out.println();
      System.out.println(counttweets);

      fwriter.close();
      entries.close();
    }
  }

  /**
   * MAIN FUNCTION FOR EXTRACTIN FEATURES
   * 
   * @param t_tweet
   * @param trie
   * @param postags
   * @return FEATURE LISTS
   */
  public List<Feature[]> extractFeature(Sentence tweetSentence) {
    // String[] t_tweet; //original input.
    int len = tweetSentence.tokenLength();
    List<List<Feature>> instances = new ArrayList<List<Feature>>(len);
    List<Feature> f = new ArrayList<Feature>();

    // normalize tweet norm_tweet
    for (int i = 0; i < len; i++)
      tweetSentence.getTokens()[i].setNorm(StringUtil
              .getDeAccentLoweredString(tokentype(tweetSentence.getTokens()[i].getToken())));
    // lemmatize norm field. store in lemma field.// originally lemmat_tweet;
    // stored in lemma field
    lemmatizer.lemmatize(tweetSentence);

    // pos tagging, originally postags. input is t_tweet
    // stored in pos field
    postagger.tag(tweetSentence);

    // String[] f_pos = postags.toArray(new String[] {});

    // f_gaz originally. filled in inGaz Field in token. check norm_tweet field.
    // boolean[] f_gaz =
    gazTag(tweetSentence, this.index);

    // use norm_tweet field to tag countries. don't remove f_country, because it's not a type in
    // token.
    boolean[] f_country = countryTag(tweetSentence);

    for (int i = 0; i < len; i++) {
      // clear feature list for this loop
      f = new ArrayList<Feature>();
      // /////////////////////////////// MORPH FEATURES
      // use lemma_tweet to get token features.
      genTokenFeatures(f, tweetSentence, i);
      // ////////////////////////////// SEMANTIC FEATURES
      genPosFeatures(f, tweetSentence, i);
      // ////////////////////////////////// GAZ AND DICT LOOK UP
      genGazFeatures(f, tweetSentence, i);
      // f7: STREET SUFFIX
      // f8 PREPOSITION

      genCountryFeatures(f, f_country, i);// f10: DIRECTION
      // f10 directions

      // FEATURES are not stored in tweetsentence in advance. Those are generated in those features.
      // use t_tweet to get cap.
      genCapFeatures(f, tweetSentence, i);

      // use t_tweet to generate preposition tags.
      genPrepFeatures(f, tweetSentence, i, preposition);

      // f9: COUNTRY
      // f11: DISTANCE
      // f12: STOPWORDS
      // f13: BUILDING
      instances.add(f);
    }

    // convert array to output format.
    ArrayList<Feature[]> newinstances = new ArrayList<Feature[]>();
    for (int i1 = 0; i1 < instances.size(); i1++) {
      newinstances.add(instances.get(i1).toArray(new Feature[] {}));
    }
    return newinstances;
  }

  // //////////////////////////////////////////////////////////////////////////////////
  // FEATURE EXTRACTORS
  // //////////////////////////////////////////////
  /**
   * PREPOSITION OR NOT.
   * 
   * INPUT RAW TOKENS OUTPUT BINARY VALUE YES OR NO.
   * 
   * @param f
   * @param t_tweet
   * @param i
   */
  // prep-2.prep-1
  private static void genPrepFeatures(List<Feature> f, Sentence sent, int i,
          HashSet<String> preposition) {
    // String[] t_tweet;

    if (i - 3 >= 0)
      addFeature(f,
              "-3_cont_prep_" + preposition.contains(TOKLW(sent.getTokens()[i - 3].getToken())));
    if (i - 2 >= 0)
      addFeature(f,
              "-2_cont_prep_" + preposition.contains(TOKLW(sent.getTokens()[i - 2].getToken())));
    if (i - 1 >= 0)
      addFeature(f,
              "-1_cont_prep_" + preposition.contains(TOKLW(sent.getTokens()[i - 1].getToken())));
  }

  /**
   * COUNTRY GAZ EXISTENCE
   * 
   * @param f
   * @param f_country
   * @param i
   */
  // country.-1.+1.seq-1+1
  private static void genCountryFeatures(List<Feature> f, boolean[] f_country, int i) {
    addFeature(f, "0_cont_country_" + f_country[i]);
    String countryseq = "";
    if (i - 1 >= 0) {
      addFeature(f, "-1_cont_country_" + f_country[i - 1]);
      countryseq += f_country[i - 1] + "::";
    }
    if (i + 1 <= f_country.length - 1) {
      addFeature(f, "+1_cont_country_" + f_country[i + 1]);
      countryseq += f_country[i + 1];
    }
    addFeature(f, "-+_cont_country_seq_" + countryseq);

  }

  /**
   * GAZ EXISTENCE
   * 
   * @param f
   * @param f_gaz
   * @param i
   */
  // gaz.-1.+1.seq-1+1
  private static void genGazFeatures(List<Feature> f, Sentence sent, int i) {
    // boolean[] f_gaz;
    // CURRENT WORD
    addFeature(f, "0_cont_gaz_" + sent.getTokens()[i].isInLocationGazetteer());

    String gazseq = "";
    if (i - 1 >= 0) {
      addFeature(f, "-1_cont_gaz_" + sent.getTokens()[i - 1].isInLocationGazetteer());
      gazseq += sent.getTokens()[i - 1].isInLocationGazetteer() + "::";
    }
    if (i + 1 <= sent.tokenLength() - 1) {
      addFeature(f, "+1_cont_gaz_" + sent.getTokens()[i + 1].isInLocationGazetteer());
      gazseq += sent.getTokens()[i + 1].isInLocationGazetteer();
    }
    addFeature(f, "-+_cont_gaz_seq_" + gazseq);
  }

  /**
   * POINT POS FOR EACH SURROUNDING WORD POS SEQUENCE
   * 
   * @param f
   * @param f_pos
   * @param i
   */
  // pos.seq-3-1.seq+1+3
  private static void genPosFeatures(List<Feature> f, Sentence twSent, int i) {
    // String[] f_pos;
    int t_length = twSent.tokenLength();
    // f5 PART OF SPEECH

    // CURRENT WORD
    addFeature(f, "0_pos_" + twSent.getTokens()[i].getPOS());

    String posleft = "", posright = "";
    if (i - 4 >= 0) {
      // addFeature(f, "-4.pos." + f_pos[i - 4]);
      // posleft += f_pos[i - 4];
    }
    if (i - 3 >= 0) {
      // addFeature(f, "-3.pos." + f_pos[i - 3]);
      // posleft += f_pos[i - 3];
    }
    if (i - 2 >= 0) {
      // addFeature(f, "-2_pos_" + f_pos[i - 2]);
      posleft += twSent.getTokens()[i - 2].getPOS();
    }
    if (i - 1 >= 0) {
      addFeature(f, "-1_pos_" + twSent.getTokens()[i - 1].getPOS());
      posleft += twSent.getTokens()[i - 1].getPOS();
    }
    if (i + 1 <= t_length - 1) {
      addFeature(f, "+1_pos_" + twSent.getTokens()[i + 1].getPOS());
      posright += twSent.getTokens()[i + 1].getPOS();
    }
    if (i + 2 <= t_length - 1) {
      // addFeature(f, "+2_pos_" + f_pos[i + 2]);
      posright += twSent.getTokens()[i + 2].getPOS();
    }
    if (i + 3 <= t_length - 1) {
      // addFeature(f, "+3.pos." + f_pos[i + 3]);
      // posright += f_pos[i + 3];
    }
    if (i + 4 <= t_length - 1) {
      // addFeature(f, "+4.pos." + f_pos[i + 4]);
      // posright += f_pos[i + 4];
    }
    addFeature(f, "-pos_seq_" + posleft);
    addFeature(f, "+pos_seq_" + posright);

  }

  /**
   * CAPITALIZATION SEQUENCE POINT CAPs OF SURROUNDING WORDS CAP SEQUENCEs
   * 
   * @param f
   * @param t_tweet
   * @param i
   */
  // cap.seq-3-1.seq+1+3
  private static void genCapFeatures(List<Feature> f, Sentence sent, int i) {
    // String[] t_tweet;
    int t_length = sent.tokenLength();

    // CURRENT WORD
    addFeature(f, "0_mph_cap_" + MPHCAP(sent.getTokens()[i].getToken()));

    String left = "", right = "";
    if (i - 4 >= 0) {
      // addFeature(f, "-4_mph_cap_" + MPHCAP(t_tweet[i - 4]));
      // left += MPHCAP(t_tweet[i - 4]);
    }
    if (i - 3 >= 0) {
      addFeature(f, "-3_mph_cap_" + MPHCAP(sent.getTokens()[i - 3].getToken()));
      // left += MPHCAP(t_tweet[i - 3]);
    }
    if (i - 2 >= 0) {
      addFeature(f, "-2_mph_cap_" + MPHCAP(sent.getTokens()[i - 2].getToken()));
      left += MPHCAP(sent.getTokens()[i - 2].getToken());
    }
    if (i - 1 >= 0) {
      addFeature(f, "-1_mph_cap_" + MPHCAP(sent.getTokens()[i - 1].getToken()));
      left += MPHCAP(sent.getTokens()[i - 1].getToken()) + "::";
    }
    if (i + 1 <= t_length - 1) {
      addFeature(f, "+1_mph_cap_" + MPHCAP(sent.getTokens()[i + 1].getToken()));
      right += MPHCAP(sent.getTokens()[i + 1].getToken());
    }
    if (i + 2 <= t_length - 1) {
      addFeature(f, "+2_mph_cap_" + MPHCAP(sent.getTokens()[i + 2].getToken()));
      right += MPHCAP(sent.getTokens()[i + 2].getToken());
    }
    if (i + 3 <= t_length - 1) {
      addFeature(f, "+3_mph_cap_" + MPHCAP(sent.getTokens()[i + 3].getToken()));
      // right += MPHCAP(t_tweet[i + 3]);
    }
    if (i + 4 <= t_length - 1) {
      // addFeature(f, "+4_mph_cap_" + MPHCAP(t_tweet[i + 4]));
      // right += MPHCAP(t_tweet[i + 4]);
    }
    addFeature(f, "-_mph_cap_seq_" + left);
    addFeature(f, "+_mph_cap_seq_" + right);
    addFeature(f, "-+_mph_cap_seq_" + left + right);

  }

  /**
   * CONTEXT WORD (LEMMA) EXISTENCE The bag of words feature, and position appearance feature
   * together. 1. Each lemma is added in bag of context words 2. Each position has an presence
   * feature for determining the existence of the window position.
   * 
   * @param f
   *          : Feature list
   * @param lemmat_tweet
   *          : lemmas of the tweet,
   * @param i
   *          : position of the current word
   */
  // tok.-1.+1.pres-4+4.
  private static void genTokenFeatures(List<Feature> f, Sentence sent, int i) {
    // String[] lemmat_tweet;
    // CURRENT TOKEN
    addFeature(f, "0_tok_lw_" + TOKLW(sent.getTokens()[i].getLemma()));
    if (i - 4 >= 0) {
      // addFeature(f, "-_tok_lw_" + TOKLW(lemmat_tweet[i - 4]));
      addFeature(f, "-4_tok_present_1");
    } else {
      addFeature(f, "-4_tok_present_0");
    }
    if (i - 3 >= 0) {
      addFeature(f, "-_tok_lw_" + TOKLW(sent.getTokens()[i - 3].getLemma()));
      addFeature(f, "-3_tok_present_1");
    } else {
      addFeature(f, "-3_tok_present_0");
    }
    // this feature has changed into bag of window words feature,
    // which is less specific than just the position.
    if (i - 2 >= 0) {
      addFeature(f, "-2_tok_lw_" + TOKLW(sent.getTokens()[i - 2].getLemma()));
      addFeature(f, "-2_tok_present_1");
    } else {
      addFeature(f, "-2_tok_present_0");
    }
    if (i - 1 >= 0) {
      addFeature(f, "-1_tok_lw_" + TOKLW(sent.getTokens()[i - 1].getLemma()));
      addFeature(f, "-1_tok_present_1");
    } else {
      addFeature(f, "-1_tok_present_0");
    }
    if (i + 1 <= sent.tokenLength() - 1) {
      addFeature(f, "+1_tok_lw_" + TOKLW(sent.getTokens()[i + 1].getLemma()));
      addFeature(f, "+1_tok_present_1");
    } else {
      addFeature(f, "+1_tok_present_0");
    }
    if (i + 2 <= sent.tokenLength() - 1) {
      addFeature(f, "+2_tok_lw_" + TOKLW(sent.getTokens()[i + 2].getLemma()));
      addFeature(f, "+2_tok_present_1");
    } else {
      addFeature(f, "+2_tok_present_0");
    }
    if (i + 3 <= sent.tokenLength() - 1) {
      addFeature(f, "+_tok_lw_" + TOKLW(sent.getTokens()[i + 3].getLemma()));
      addFeature(f, "+3_tok_present_1");
    } else {
      addFeature(f, "+3_tok_present_0");
    }
    if (i + 4 <= sent.tokenLength() - 1) {
      // addFeature(f, "+_tok_lw_" + TOKLW(sent.getTokens()[i+4].getLemma()));
      addFeature(f, "+4_tok_present_1");
    } else {
      addFeature(f, "+4_tok_present_0");
    }
  }

  /**
   * CAPITALIZATION
   * 
   * @param string
   * @return boolean
   */
  private static String MPHCAP(String string) {

    boolean a = Character.isUpperCase(string.charAt(0));
    return Boolean.toString(a);
  }

  /**
   * CONVERT TO LOWER TYPE Input the lemma, 1. Run tokentype() to convert to token 2. lowercase and
   * deaccent the lemma.
   * 
   * @param lemmastring
   * @return
   */
  private static String TOKLW(String lemmastring) {

    lemmastring = StringUtil.getDeAccentLoweredString(tokentype(lemmastring));
    return lemmastring;
  }

  /**
   * CONVERT TO TYPE Naively decide the tweet token type, url, or hashtag, or metion, or number. Or
   * it's not any of them, just return it's original string.
   * 
   * @param token
   * @return
   */
  public static String tokentype(String token) {
    // lower cased word.
    String ltoken = StringUtil.getDeAccentLoweredString(token.trim());

    if (ltoken.startsWith("http:") || ltoken.startsWith("www:")) {
      ltoken = "[http]";
    } else if (ltoken.startsWith("@") || ltoken.startsWith("#")) {
      if (ltoken.length() > 1) {
        ltoken = ltoken.substring(1);
      }
    }
    try {
      Double.parseDouble(ltoken);
      ltoken = "[num]";
    } catch (NumberFormatException e) {
    }

    return ltoken;
  }

  // ////////////////////////////////////////////////////////////////////////////////////
  // GAZ FEATURE HELPER
  // //////////////////////////////////////////////////////////
  /**
   * GAZ TAGGING BASED ON GREEDY SEARCH. FIND THE LONGEST MATCH STARTING FROM THE CURRENT WORD
   * 
   * @param t_tweet
   * @param trie
   * @return
   */
  private static Sentence gazTag(Sentence twSent, Index index) {
    int len = twSent.tokenLength();
    boolean[] gaztag = new boolean[twSent.tokenLength()];
    int i = 0;
    while (i < len) {
      String history = "";
      for (int j = i; j < len; j++) {
        history += twSent.getTokens()[j].getNorm();
        if (index.inIndex(history)) {
          for (int k = i; k < j + 1; k++)
            gaztag[k] = true;
          // gaztag[j]=true;
        }
      }
      i++;
    }

    for (i = 0; i < gaztag.length; i++) {
      twSent.getTokens()[i].setInLocationGazetteer(gaztag[i]);
    }
    return twSent;
  }

  /**
   * COUNTRY TAGGING BASED ON GREEDY SEARCH FIND THE LONGEST MATCH STARTING FROM THE CURRENT WORD
   * 
   * @param t_tweet
   * @return
   */
  private static boolean[] countryTag(Sentence sent) {
    // String[] t_tweet;
    boolean[] countrytag = new boolean[sent.tokenLength()];
    int i = 0;
    while (i < sent.tokenLength()) {
      String history = "";
      for (int j = i; j < sent.tokenLength(); j++) {
        history += " " + StringUtil.getDeAccentLoweredString(sent.getTokens()[j].getNorm());
        // System.out.println(history);
        // System.out.println(ParserUtils.isCountry(history.trim()));
        if (ParserUtils.isCountry(history.trim())) {
          for (int k = i; k < j + 1; k++)
            countrytag[k] = true;
        }
      }
      i++;
    }
    return countrytag;
  }

  /**
   * HELPER FOR SAFELY TAGGING THE STRINGS
   * 
   * @param t_tweet
   * @param t_street
   * @param t_building
   * @param t_toponym
   * @param t_abbr
   * @param tk
   * @return
   */
  private static HashMap<Integer, String> safeTag(String[] t_tweet, String[] t_street,
          String[] t_building, String[] t_toponym, String[] t_abbr) {
    HashMap<Integer, String> tagresults = new HashMap<Integer, String>();
    if (!EmptyArray(t_toponym)) {
      fillinTag(t_tweet, t_toponym, tagresults, "TP");
    }
    if (!EmptyArray(t_street)) {
      fillinTag(t_tweet, t_street, tagresults, "ST");
    }
    if (!EmptyArray(t_building)) {
      fillinTag(t_tweet, t_building, tagresults, "BD");
    }
    if (!EmptyArray(t_abbr)) {
      fillinTag(t_tweet, t_abbr, tagresults, "AB");
    }
    return tagresults;
  }

  /**
   * AUXILARY FUNCTION FOR SAFETAG. FILL THE TAG IN t_location INTO THE tagresults hashmap.
   * 
   * @param t_tweet
   * @param t_street
   * @param tagresults
   * @param tk
   */
  private static void fillinTag(String[] t_tweet, String[] t_location,
          HashMap<Integer, String> tagresults, String TAG) {

    for (String location : t_location) {

      List<String> loctokens = EuroLangTwokenizer.tokenize(location);
      // if (TAG.equals("AB")) {
      // System.out.println("the original tweet tokenized is : " +
      // Arrays.asList(t_tweet).toString());
      // System.out.println("the location tokenized is :" +
      // loctokens.toString());
      // }
      for (String token : loctokens) {
        boolean have = false;
        String ntoken = StringUtil.getDeAccentLoweredString(token);
        for (int i = 0; i < t_tweet.length; i++) {
          if (StringUtil.getDeAccentLoweredString(
                  (t_tweet[i].startsWith("#") && t_tweet[i].length() > 1) ? t_tweet[i].substring(1)
                          : t_tweet[i]).equals(ntoken)) {
            tagresults.put(i, TAG);
            have = true;
          }
        }
        if (have == false)
          System.out.println("Don't have the tag: " + token);
      }
    }
    // if(TAG.equals("AB"))
    // System.out.println(tagresults);
  }

  // ///////////////////////////////////////////////////////////////////////////////////////////////////////
  // TOOLS
  // //////////////////////////////////
  /**
   * JUDGE EMPTY OF AN ARRAY.
   * 
   * @param array
   * @return
   */
  static boolean EmptyArray(String[] array) {
    if (array.length < 2)
      if (array[0].equals(""))
        return true;
    return false;
  }

  // ////////////////////////////////////////////////////////////////////////////////
  // HELPER FOR FEATURE VECTOR
  // /////////////////////////////////////////
  static StringBuilder sb = new StringBuilder();

  /**
   * helper for building feature vector. sb stores the features on a line, and this func is used to
   * initialize the sb, aka, clear the builder.
   */
  private static void initialFeatureWriter() {
    sb = new StringBuilder();
  }

  private static void append(String featurestring) {

    if (sb.length() > 0)
      sb.append("\t");
    sb.append(featurestring);
  }

  static String emit() {
    return sb.append("\n").toString();
  }

  private static void addFeature(List<Feature> features, String string) {

    features.add(new Feature(string));
  }

  // ////////////////////////////////////////////////////////////////////////////////////
  // GETTER AND SETTERS /////

  public HashSet<String> getPreposition() {
    return preposition;
  }

  public void setPreposition(HashSet<String> preposition) {
    this.preposition = preposition;
  }

  public HashSet<String> getCountries() {
    return countries;
  }

  public void setCountries(HashSet<String> countries) {
    this.countries = countries;
  }

  public EuroLangTwokenizer getTokenizer() {
    return tokenizer;
  }

  public void setTokenizer(EuroLangTwokenizer tokenizer) {
    this.tokenizer = tokenizer;
  }

  public Lemmatizer getLemmatizer() {
    return lemmatizer;
  }

  public void setLemmatizer(Lemmatizer lemmatizer) {
    this.lemmatizer = lemmatizer;
  }

  public POSTagger getPostagger() {
    return postagger;
  }

  public void setPostagger(POSTagger postagger) {
    this.postagger = postagger;
  }

}
