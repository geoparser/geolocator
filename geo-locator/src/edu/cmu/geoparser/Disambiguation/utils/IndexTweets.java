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
package edu.cmu.geoparser.Disambiguation.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;

import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.json.DataObjectFactory;

import edu.cmu.geoparser.io.GetReader;
import edu.cmu.geoparser.io.GetWriter;

public class IndexTweets {

	BufferedReader br;
	public IndexTweets(String filename) throws FileNotFoundException,
			UnsupportedEncodingException {
		br=GetReader.getUTF8FileReader(filename);
	}

	void close() throws IOException{
		br.close();
	}
	public void start(IndexWriter iw) throws CorruptIndexException, IOException {

		Document d = new Document();
		NumericField nftime = new NumericField("CREATEDAT", Field.Store.YES,
				true);
		Field sfjson = new Field("JSON", false, "", Field.Store.YES,
				Index.ANALYZED, TermVector.NO);
		d.add(nftime);
		d.add(sfjson);

		String line;
		Status tweet;
		long time;
		while ((line = br.readLine()) != null) {
			try {
				// parse json to object type
				tweet = DataObjectFactory.createStatus(line);
			} catch (TwitterException e) {
				System.err.println("error parsing tweet object");
				continue;
			}
			time = (tweet.getCreatedAt()!=null) ?tweet.getCreatedAt().getTime():0l;
			nftime.setLongValue(time);
			//sfjson.setValue(jsontweet.JSON);
			sfjson.setValue(line);
			//System.out.println(d);
			iw.addDocument(d);
		}
	}

	public static void main(String argv[]) throws IOException {

		IndexTweets it = new IndexTweets(
				"/Users/indri/Eclipse_workspace/Research/texasfiretweets/tweets.2011-09-05.gz.txt");
		IndexWriter iw = GetWriter
				.getIndexWriter("/Users/indri/Eclipse_workspace/Research/texasfiretweets/index/2011-09-05");
		iw.deleteAll();
		it.start(iw);

		iw.close(); //
		it.close();

	}
}
