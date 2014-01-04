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
package edu.cmu.geoparser.Disambiguation;

import edu.cmu.geoparser.io.GetReader;
import edu.cmu.geoparser.model.CandidateAndFeature;
import edu.cmu.geoparser.model.LocEntity;
import edu.cmu.geoparser.model.Sentence;
import edu.cmu.geoparser.model.Tweet;
import edu.cmu.geoparser.model.TweetExample;
import edu.cmu.geoparser.model.LocGroupFeatures;
import edu.cmu.geoparser.nlp.tokenizer.EuroLangTwokenizer;
import edu.cmu.geoparser.resource.ResourceFactory;
import edu.cmu.geoparser.resource.gazindexing.CollaborativeIndex.InfoFields;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

import org.apache.lucene.document.Document;

/**
 * In this class, strings are operated on character level, not string level.
 * 
 * @author indri
 * 
 */
public class Tweetdisamb {

  public Tweetdisamb() {
  }

  static TweetExample[] examples;

  double totalValidTopos;

  public static void main(String argv[]) throws Exception {
    Tweetdisamb td = new Tweetdisamb();
    examples = td
            .loadExamples("/Users/Indri/Documents/Research_data/Disambiguation/tagging/400run.csv");
    // td.tagCorrectLabel();
    int totalexamples, train, test;
    totalexamples = examples.length;
    train = (int) (((double) totalexamples) * 0.6);
    test = totalexamples - train;

    ArrayList<TweetExample> trainingdata = new ArrayList<TweetExample>();
    ArrayList<TweetExample> testdata = new ArrayList<TweetExample>();
    Random r = new Random();
    int testa=101, testb=200; 
    int traina1=101, trainb1= 100;
    int traina2=301, trainb2=400;
    for (int i = 0; i < totalexamples; i++) {
      // if (r.nextDouble() > 0.25)
      if (testb >= i && i >=testa)
        testdata.add(examples[i]);
      if ( i >= traina1&& i <=trainb1 || i>= traina2 && i<= trainb2 )
        trainingdata.add(examples[i]);
    }    
    int k = trainb2-traina2 + trainb1-traina1;
    String str = "cord : test:["+ testa +" to " + testb + "] train: "+ k;
    System.out.println(str);
    td.train(trainingdata);
    double correct = 0, total = 0, trueCorrect = 0, falseCorrect = 0, trueTotal = 0, falseTotal = 0, predtrue = 0, goldtrue = 0, truepos = 0;
    double adjTrueFound = 0, adjTrueTotal = 0;
    int i = trainingdata.size();
    for (TweetExample te : testdata) {
      System.out.println("///////////////////////////  Testing " + (i++) +"/////////////////////////////////////");
      System.out.println(te.getText());
      if (te.getToponyms().length == 0) {
        System.out.println("The tweet does not have a valid toponym to disambiguate. Continue;");
        continue;
      }
      td.testPerLocation(te);
      correct += td.correct;      total += td.total;
      trueCorrect += td.trueCorrect;      falseCorrect += td.falseCorrect;
      trueTotal += td.trueTotal;      falseTotal += td.falseTotal;
      predtrue += td.predictedTrue;      goldtrue += td.goldTrue;
      truepos += td.truePositive;      adjTrueFound += td.toposFound4Recall;
      adjTrueTotal += td.goldTrue4Recall;
    }
    System.out.println(str+k);
    System.out.println(" precision:" + truepos / predtrue);
    System.out.println(" Recall:" + truepos / goldtrue);
    System.out.println("adjusted Recall is: " + adjTrueFound / adjTrueTotal);

    System.out.println("gold true : " + goldtrue);
    System.out.println("predicted true: " + predtrue);
    System.out.println(" true pos : " + truepos);
  }

  HashSet<String> FeatureSet;

  public TweetExample[] loadExamples(String filename) throws IOException {
    ArrayList<TweetExample> tes = new ArrayList<TweetExample>();
    BufferedReader br = GetReader.getUTF8FileReader(filename);
    String line = null;
    int count = 0;
    int _totalLocs = 0;
    int _totalIds = 0;
    HashSet<String> _uniqueTopos = new HashSet<String>();
    HashSet<String> _uniqueIds = new HashSet<String>();
    while ((line = br.readLine()) != null) {
      line = line.toLowerCase();
      String[] part = line.split("\t");
      String tweet = part[0];
      Tweet t = new Tweet(tweet);
      EuroLangTwokenizer.tokenize(t.getSentence());
      String locs = part[1];
      String lat = part[2];
      String lon = part[3];
      String userLoc = part[4].trim();
      String timezone = part[5].trim();
      String userDsc = part[6].trim();
      TweetExample te = new TweetExample().setText(tweet).setLatitude(Double.parseDouble(lat))
              .setLongitude(Double.parseDouble(lon)).setUserLocation(userLoc)
              .setUserDescription(userDsc);
      // handle locs
      locs = locs.substring(3, locs.length() - 3);
      String[] loc = locs.split("\\}tptp\\{");

      _totalLocs += loc.length;

      ArrayList<LocEntity> topos = new ArrayList<LocEntity>();
      ArrayList<Document> topodocs = new ArrayList<Document>();
      for (String l : loc) {
        String[] e = l.split("\\[");
        String str = e[0];
        _uniqueTopos.add(str);
        if (e[1].endsWith("]"))
          // it is not a in-gazetteer entry.
          continue;
        Sentence phrase = new Sentence(str);
        EuroLangTwokenizer.tokenize(phrase);
        targetTagPhraseInSentence(t.getSentence(), phrase);

        String[] geo_id = e[1].split("\\]");
        String[] geo = geo_id[0].split(",");
        String elat = geo[0];
        String elon = geo[1];
        String[] ids = geo_id[1].split(",");
        _totalIds += ids.length;
        for (String id : ids)
          _uniqueIds.add(id);
        LocEntity le = new LocEntity(phrase.getTokens()[0].getPosition(),
                phrase.getTokens()[0].getPosition() + phrase.tokenLength() - 1, "tp",
                phrase.getTokens()).setLatitude(Double.parseDouble(elat))
                .setLongitude(Double.parseDouble(elon))
                .setGeoNamesId(new ArrayList<String>(Arrays.asList(ids)));
        topos.add(le);
      }
      te.setToponyms(topos.toArray(new LocEntity[topos.size()]));
      tes.add(te);
      count += topos.size();
      // System.out.println(te + "\n");
    }
    System.out.println("total toponyms are :" + count);
    System.out.println("total ids are: " + _totalIds);
    System.out.println("unique ids are: " + _uniqueIds.size());
    System.out.println("unique topos are: " + _uniqueTopos.size());
    System.out.println("Load Done.");
    return tes.toArray(new TweetExample[] {});
  }

  /**
   * Because our tag is flawed, we have to find the target in the sentence, to generate the token
   * start and end position for each one of them.
   * 
   * @param sentence
   * @param phrase
   */
  private void targetTagPhraseInSentence(Sentence sentence, Sentence phrase) {
    // for each token in phrase
    // i-th phrase token
    for (int i = 0; i < phrase.tokenLength(); i++) {
      String lowPraseToken = phrase.getTokens()[i].getToken().toLowerCase();
      // j-th sentence token
      for (int j = 0; j < sentence.tokenLength(); j++) {
        String lowSentToken = sentence.getTokens()[j].getToken().toLowerCase();
        if (lowSentToken.equals(lowPraseToken)) {
          phrase.getTokens()[i].setPosition(j);
        }
      }
    }
  }

  svm_parameter p;

  svm_model model;

  void setDefaultSVMParam() {
    p = new svm_parameter();
    p.svm_type = svm_parameter.C_SVC;
    p.kernel_type = svm_parameter.RBF;
    p.degree = 3;
    p.gamma = 0.5; // 1/k
    p.coef0 = 0;
    p.nu = 0.5;
    p.cache_size = 40;
    p.C = 1;
    p.eps = 1e-3;
    p.p = 0.1;
    p.shrinking = 1;
    p.nr_weight = 0;
    p.weight_label = new int[0];
    p.weight = new double[3];
    p.probability = 1;
  }

  public void train(ArrayList<TweetExample> examples) throws Exception {
    System.out.println("prepare for training...");
    svm_problem problem = new svm_problem();

    ArrayList<svm_node[]> nodelist = new ArrayList<svm_node[]>();
    ArrayList<Double> labels = new ArrayList<Double>();
    for (int i = 0; i < examples.size(); i++) {
      System.out.println("example number " + i);
      System.out.println("Text : " + examples.get(i).getText());

      if (examples.get(i).getToponyms().length == 0) // converted from arraylist, so no null value.
      {
        System.out.println("No topos in example. Continue.");
        continue;
      }
      System.out.println("The toponyms are : " + examples.get(i).getToponymsAsText());
      LocGroupFeatures feature = new LocGroupFeatures(examples.get(i)).toFeatures();
      ArrayList<svm_node[]> fvec = feature.getFeatureVector();
      ArrayList<Double> flabel = feature.getLabels();
      for (int j = 0; j < fvec.size(); j++) {
        nodelist.add(fvec.get(j));
        labels.add(flabel.get(j));
      }
    }
    problem.l = nodelist.size();
    problem.x = new svm_node[problem.l][];
    problem.y = new double[problem.l];
    for (int i = 0; i < problem.l; i++) {
      problem.x[i] = nodelist.get(i);
      problem.y[i] = labels.get(i);
    }
    System.out.println("training...");
    setDefaultSVMParam();
    model = svm.svm_train(problem, p);
    try {
      svm.svm_save_model("res/geocoder-test.mdl", model);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    System.out.println("training done.");
  }

  int correct, total;

  int trueCorrect, trueTotal;

  int falseCorrect, falseTotal;

  public void test(TweetExample example) throws Exception {
    this.correct = 0;
    this.total = 0;
    this.trueCorrect = 0;
    this.falseCorrect = 0;
    this.trueTotal = 0;
    this.falseTotal = 0;
    svm_problem problem = new svm_problem();

    ArrayList<svm_node[]> nodelist;
    ArrayList<Double> labels;

    LocGroupFeatures feature = new LocGroupFeatures(example).toFeatures();
    ArrayList<ArrayList<CandidateAndFeature>> farrays = feature.getFeatureArrays();
    ArrayList<CandidateAndFeature> fs = new ArrayList<CandidateAndFeature>();

    for (ArrayList<CandidateAndFeature> a : farrays)
      for (CandidateAndFeature b : a)
        fs.add(b);
    nodelist = feature.getFeatureVector();
    labels = feature.getLabels();

    this.total = nodelist.size();

    problem.l = this.total;
    problem.x = new svm_node[problem.l][];
    problem.y = new double[problem.l];
    for (int i = 0; i < problem.l; i++) {
      System.out.println(fs.get(i).getId() + " " + fs.get(i).getAsciiName() + " "
              + fs.get(i).getAltnameCount() + " " + fs.get(i).getCountryCode() + " "
              + fs.get(i).getAdm1Code() + " " + fs.get(i).getAdm2Code() + " "
              + fs.get(i).getAdm3Code() + " " + fs.get(i).getLatitude() + " "
              + fs.get(i).getLongitude() + " " + fs.get(i).getY());
      problem.x[i] = nodelist.get(i);
      problem.y[i] = labels.get(i);
      int l = (int) svm.svm_predict(model, problem.x[i]);
      System.out.println("The prediction is " + l);
      if (l == 1)
        trueTotal += 1;
      else {
        falseTotal += 1;
      }
      if (l == (int) problem.y[i]) {
        this.correct += 1;
        if (l == 1)
          trueCorrect += 1;
        else
          falseCorrect += 1;
      } else {
        System.out.println("for the " + i + " th toponym, This instance should be " + problem.y[i]);
        System.out.println("The instance is false for toponym : ");

      }
    }
  }

  // precision
  double predictedTrue;

  double goldTrue;

  double truePositive;

  double goldTrue4Recall, toposFound4Recall;

  public void testPerLocation(TweetExample example) throws Exception {

    this.predictedTrue = 0;
    this.goldTrue = 0;
    this.truePositive = 0;
    this.goldTrue4Recall = example.getToponyms().length;
    this.toposFound4Recall = 0.0;
    svm_problem problem = new svm_problem();

    ArrayList<svm_node[]> nodelist;
    ArrayList<Double> labels;
    ArrayList<String> ids;
    HashSet<String> predictedIds = new HashSet<String>();

    LocGroupFeatures feature = new LocGroupFeatures(example).toFeatures();
    ArrayList<ArrayList<CandidateAndFeature>> farrays = feature.getFeatureArrays();
    ArrayList<CandidateAndFeature> fs = new ArrayList<CandidateAndFeature>();

    for (ArrayList<CandidateAndFeature> a : farrays)
      for (CandidateAndFeature b : a)
        fs.add(b);
    nodelist = feature.getFeatureVector();
    labels = feature.getLabels();
    ids = feature.getIds();
    this.total = nodelist.size();

    problem.l = this.total;
    problem.x = new svm_node[problem.l][];
    problem.y = new double[problem.l];
    for (int i = 0; i < problem.l; i++) {
      problem.x[i] = nodelist.get(i);
      problem.y[i] = labels.get(i);
      double[] prob_estimates = new double[2];
      // int l = (int) svm.svm_predict_probability(model, problem.x[i],prob_estimates);
      int l = (int) svm.svm_predict(model, problem.x[i]);

      if (l == 1) {
        this.predictedTrue += 1;
        predictedIds.add(ids.get(i));
      }
      if ((int) problem.y[i] == 1) {
        this.goldTrue += 1;
        System.out.println("True Label: " + fs.get(i).getId() + " " + fs.get(i).getAsciiName()
                + " " + fs.get(i).getPopulation() + " " + fs.get(i).getAltnameCount() + " "
                + fs.get(i).getCountryCode() + " " + fs.get(i).getAdm1Code() + " "
                + fs.get(i).getAdm2Code() + " " + fs.get(i).getAdm3Code() + " "
                + fs.get(i).getLatitude() + " " + fs.get(i).getLongitude() + " "
                + fs.get(i).getFeature() + " label:" + fs.get(i).getY());
      }
      if (l == (int) problem.y[i]) {
        if (l == 1)
          this.truePositive += 1;
      } else {
        if (l == 0)
          System.out.println("miss:");
        else
          System.out.println("false positive:");

        System.out.println(fs.get(i).getId() + " " + fs.get(i).getAsciiName() + " "
                + fs.get(i).getPopulation() + " " + fs.get(i).getAltnameCount() + " "
                + fs.get(i).getCountryCode() + " " + fs.get(i).getAdm1Code() + " "
                + fs.get(i).getAdm2Code() + " " + fs.get(i).getAdm3Code() + " "
                + fs.get(i).getLatitude() + " " + fs.get(i).getLongitude() + " "
                + fs.get(i).getFeature() + " label:" + fs.get(i).getY());
      }
    }
    this.toposFound4Recall = getToposFound4Recall(example, predictedIds);
    System.out.println(example.getToponymsAsText());
    System.out.println(example.getAllIds());
    System.out.println(" No. of topos found : " + this.toposFound4Recall);
    System.out.println(predictedIds);
  }

  private double getToposFound4Recall(TweetExample example, HashSet<String> predictedIds) {
    LocEntity[] topos = example.getToponyms();
    if (topos == null || topos.length == 0)
      return 0;
    int toposfound = 0;
    for (LocEntity topo : topos) {
      ArrayList<String> ids = topo.getGeonamesIds();
      if (ids == null || ids.size() == 0)
        continue;
      boolean found = false;
      for (String id : ids) {
        if (predictedIds.contains(id)) {
          found = true;
          break;
        }
      }
      if (found == true)
        toposfound += 1;
    }
    return toposfound;
  }

  // Helper
  private int targetLocationStringHelper(String text, LocEntity le) {
    // I dare use it here because the locentity only contains one token for training data.
    String loc = le.getTokenString();
    return text.indexOf(loc);
  }

}
