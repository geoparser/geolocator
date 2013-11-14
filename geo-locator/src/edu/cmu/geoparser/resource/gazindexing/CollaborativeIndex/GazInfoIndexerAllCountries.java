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
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
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
 * This is used for indexing the Gazetteer for misspelling checking.
 * Usage: GazStringIndexerAllCountries.java -write [geonames gaz file path] [user specified index location]
 * 
 * @Input: Gaz entries
 * 
 * @Output: Index of Gaz
 * 
 * Features used for each word: (e.g. Chilee) 1. c,h,i,l,e,e 2. ch,hi,il,le,ee
 * 3. c_0,h_1,i_2,l_3,e_4,e_5
 */
public class GazInfoIndexerAllCountries {




	// main method for indexing gazatteer into index.
	void indexGazatteer(BufferedReader br, IndexWriter iw) throws IOException {

		Document d = new Document();
		StringField nfid = new StringField("ID", "0", Field.Store.YES);
		DoubleField nflong = new DoubleField("LONGTITUDE", 0.0, Field.Store.YES);
		DoubleField nfla = new DoubleField("LATITUDE", 0.0, Field.Store.YES);
		LongField nfpop = new LongField("POPULATION", 0, Field.Store.YES);
		StringField sfcountrystate = new StringField("COUNTRYSTATE", "0", Field.Store.YES);
		StringField sffeature = new StringField("FEATURE", "0", Field.Store.YES);
		StringField sftimezone = new StringField("TIMEZONE", "0", Field.Store.YES);
		d.add(nfid);
		d.add(nflong);
		d.add(nfla);
		d.add(nfpop);
		d.add(sfcountrystate);
		d.add(sffeature);
		d.add(sftimezone);

		String line;
		int linen = 0;
		while ((line = br.readLine()) != null) {
			if (linen++ % 10000 == 0)
				System.out.println(linen + "\n" + line);
			String[] column = line.trim().split("\t");

			// get other columns except for the location words
			String id = column[0];
			String latitude = column[4];
			String longtitude = column[5];
			double dlong, dla;
			if (latitude == null) {
				dlong = 999;
				dla = 999;
			} else {
				dlong = Double.parseDouble(longtitude);
				dla = Double.parseDouble(latitude);
			}
			String featureclass = column[6];
			String feature = column[7];
			String country = column[8];
			String state = column[10] + "_" + column[11] + "_" + column[12] + "_" + column[13];
			String population = column[14];
			long longpop;
			if (population == null)
				longpop = -1;
			longpop = Long.parseLong(population);
			String timezone = column[17];

			// To Do: set values to document d, and index it
			nfid.setStringValue(id);// 1
			nflong.setDoubleValue(dlong);
			nfla.setDoubleValue(dla);
			nfpop.setLongValue(longpop);

			sfcountrystate.setStringValue(country + "_" + state);
			sffeature.setStringValue(featureclass + "_" + feature);
			sftimezone.setStringValue(timezone);// 13

			// add this new document.
			iw.addDocument(d);
		}
	}

	public static void main(String argv[]) throws Exception {

		GazInfoIndexerAllCountries gi = new GazInfoIndexerAllCountries();

		argv[0]="-write";
		String mode = argv[0];
		
		if (mode.equals("-write")) {
			if (argv.length != 3)
				throw new Exception("Command line argument number wrong");
			argv[1] ="GeoNames/allCountries.txt";
			argv[2] = "GazIndex/InfoIndex";
			BufferedReader br = GetReader.getUTF8FileReader(argv[1]);
			IndexWriter iw = GetWriter.getIndexWriter(argv[2],1400);
			iw.deleteAll();
			gi.indexGazatteer(br, iw);
			iw.close();
			br.close();
		}
		if (mode.equals("-read")) {
			System.out.println("input id. Output basic information. For debugging.");
			// query first two fields.
			argv[1] = "/Users/Indri/Eclipse_workspace/GazIndex/";
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
				System.out.println(d.get("ORIGIN"));
				System.out.println(d.get("LONGTITUDE") + " " + d.get("LATITUDE"));
				System.out.println("lookup time: " + (end - start));
			}
		}
	}
}
