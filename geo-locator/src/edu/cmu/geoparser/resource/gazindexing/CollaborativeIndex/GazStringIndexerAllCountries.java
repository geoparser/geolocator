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
package edu.cmu.geoparser.resource.gazindexing.CollaborativeIndex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import edu.cmu.geoparser.common.StringUtil;
import edu.cmu.geoparser.io.GetReader;
import edu.cmu.geoparser.io.GetWriter;

/**
 * This is used for indexing the Gazetteer for misspelling checking. Usage:
 * GazStringIndexerAllCountries.java -write [geonames gaz file path] [user specified index
 * location]
 * 
 * @Input: Gaz entries
 * 
 * @Output: Index of Gaz
 * 
 *          Features used for each word: (e.g. Chilee) 1. c,h,i,l,e,e 2. ch,hi,il,le,ee 3.
 *          c_0,h_1,i_2,l_3,e_4,e_5
 */
public class GazStringIndexerAllCountries {

  static String f_unigram, f_bigram, f_trigram;

  static String getUnigram() {
    return f_unigram;
  }

  static String getBigram() {
    return f_bigram;
  }

  static String getTrigram() {
    return f_trigram;
  }
  static char[] locchars;
  static String[] bigramloc, trigramloc;
  static void getIndexFeatures(String phrase) {
    // prepare for indexing
    locchars = (phrase).toCharArray();
    bigramloc = StringUtil.getBigram(locchars);
    trigramloc = StringUtil.getTrigram(locchars);

    f_unigram = StringUtil.factorize(locchars);
    f_bigram = StringUtil.factorize(bigramloc);
    f_trigram = StringUtil.factorize(trigramloc);
  }

  // main method for indexing gazatteer into index.
  void indexGazatteer(BufferedReader br, IndexWriter iw) throws IOException, InterruptedException {

    Document d = new Document();
    StringField nfid = new StringField("ID", "",Field.Store.YES);
    StringField sforigin = new StringField("LOWERED_ORIGIN", "", Field.Store.YES);
    StringField normnws = new StringField("LOWERED-NO-WS", "", Field.Store.YES);
    TextField sfunigram = new TextField("UNIGRAM", "", Field.Store.YES);
    TextField sfbigram = new TextField("BIGRAM", "", Field.Store.YES);
    TextField sftrigram = new TextField("TRIGRAM","", Field.Store.YES);
    StringField lang = new StringField("LANG", "", Field.Store.YES);
    d.add(nfid);
    d.add(sforigin);
    d.add(normnws);
    d.add(sfunigram);
    d.add(sfbigram);
    d.add(sftrigram);

    String line;
    String[] column;
    String id, phrase,otherlang, oneOtherlang;
    int otherlangLength;
    int linen = 0;
    while ((line = br.readLine()) != null) {
      if (linen++ % 10000 == 0)
        System.out.println(linen + "\n" + line);
      column = line.trim().split("\t");

      // get other columns except for the location words
      id = column[0];
      phrase = column[1];
      otherlang = column[3];

      // To Do: set values to document d, and index it
      nfid.setStringValue(id);// 1
      // id does not change, change the strings to index.
      phrase = phrase.toLowerCase();
      sforigin.setStringValue(phrase);// 5
      normnws.setStringValue(phrase.replaceAll(" ", ""));

      getIndexFeatures(phrase);

      sfunigram.setStringValue(getUnigram());
      sfbigram.setStringValue(getBigram());
      sftrigram.setStringValue(getTrigram());

      lang.setStringValue("");
      // add this new document.
      iw.addDocument(d);

      if(otherlang.length()==0)continue;
      String otherlangs[] = otherlang.split(",");
      otherlangLength = otherlangs.length;
      for (int i = 0 ; i < otherlangLength; i++) {
        if(otherlangs[i].length()==0)continue;
        // id does not change, change the strings to index.
        oneOtherlang = otherlangs[i].toLowerCase();
        sforigin.setStringValue(oneOtherlang);// 5
        normnws.setStringValue(oneOtherlang.replaceAll(" ", ""));

        getIndexFeatures(oneOtherlang);

        sfunigram.setStringValue(getUnigram());
        sfbigram.setStringValue(getBigram());
        sftrigram.setStringValue(getTrigram());

        lang.setStringValue("");
        // add this new document.
        iw.addDocument(d);
      }
    }
  }

  public static void main(String argv[]) throws Exception {

    GazStringIndexerAllCountries gi = new GazStringIndexerAllCountries();

    argv[0] = "-write";
    String mode = argv[0];

    if (mode.equals("-write")) {
      if (argv.length != 3)
        throw new Exception("Command line argument number wrong");
      argv[1] = "GeoNames/allCountries.txt";
      argv[2] = "GazIndex/StringIndex/";

      BufferedReader br = GetReader.getUTF8FileReader(argv[1]);
      IndexWriter iw = GetWriter.getIndexWriter(argv[2],1600);
      iw.deleteAll();
      gi.indexGazatteer(br, iw);
      // iw.optimize();
      iw.close();
      br.close();
    }
    if (mode.equals("-read")) {
      System.out.println("input id. Output basic information. For debugging.");
      // query first two fields.
      argv[1] = "GazIndex/StringIndex";
      IndexSearcher is = GetReader.getIndexSearcher(argv[1],"disk");
      BufferedReader r = new BufferedReader(new InputStreamReader(System.in, "utf-8"));
      String line;
      while ((line = r.readLine()) != null) {

        long id;
        try {
          id = Long.parseLong(line);
        } catch (Exception e) {
          System.err.println("number wrong.");
          continue;
        }

        Query q = NumericRangeQuery.newLongRange("ID", id, id, true, true);

        long start = System.currentTimeMillis();
        TopDocs docs = is.search(q, 1);
        if (docs == null) {
          System.err.println("Not found.");
          continue;
        }
        if (docs.scoreDocs.length == 0) {
          System.err.println("Not found.");
          continue;
        }
        ScoreDoc sd = docs.scoreDocs[0];
        Document d = is.doc(sd.doc);
        long end = System.currentTimeMillis();
        System.out.println(d);
        System.out.println(d.get("ID"));
        System.out.println(d.get("LOWERED_ORIGIN"));
      }
    }
  }
}
