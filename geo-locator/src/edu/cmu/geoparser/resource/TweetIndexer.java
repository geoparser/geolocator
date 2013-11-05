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
package edu.cmu.geoparser.resource;

import java.io.*;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.json.DataObjectFactory;

import edu.cmu.geoparser.io.GetReader;
import edu.cmu.geoparser.io.GetWriter;
/**
 * This class is used to index JSON tweets. 
 * INDEXED FIELDS: CONTENT, JSON, CREATED TIME
 * 
 */
public class TweetIndexer {

	public String path;
	public String indexfile;
	public IndexWriter Iwriter;
	public IndexSearcher Isearcher;

	TweetIndexer(String file, String indexfile) {
		this.path = file;
		this.indexfile = indexfile;
		try {
			Iwriter = GetWriter.getIndexWriter(indexfile);
			Iwriter.deleteAll();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

	IndexSearcher getSearcher() throws IOException {
		return GetReader.getIndexSearcher(indexfile);

	}

	public static void main(String argv[]) throws IOException {
		TweetIndexer ti = new TweetIndexer("tweets/", "tweetIndex/");
		ti.index();
		IndexSearcher searcher = ti.getSearcher();
		TermQuery q = new TermQuery(new Term("CONTENT", "Okay new task... get all this food safely into my vehichle... *yikes*"));
		BooleanQuery bq = new BooleanQuery();
		bq.add(q, Occur.MUST);
		TopDocs docs = searcher.search(bq, 100);
		if (docs == null) {
			System.err.println("Not found.");
			return;
		}
		if (docs.scoreDocs.length == 0) {
			System.err.println("Not found.");
			return;
		}
		ScoreDoc sd = docs.scoreDocs[0];
		Document d = searcher.doc(sd.doc);
		System.out.println(d.get("JSON"));
	}

	public void index() throws IOException {

		long t = (System.currentTimeMillis());
		File[] listOfFiles = readFolder();

		Document d = new Document();
		Field fcontent = new Field("CONTENT", false, "", Field.Store.YES, Index.NOT_ANALYZED, TermVector.NO);
//		Field fjson = new Field("JSON", false, "", Field.Store.YES, Index.NOT_ANALYZED, TermVector.NO);
		NumericField ftime = new NumericField("TIME", Field.Store.YES, true);
		Field fuserdesc =new Field("DESC",false,"",Field.Store.YES,Index.NOT_ANALYZED,TermVector.NO);
		Field fuserlocation = new Field("USERLOC",false,"",Field.Store.YES,Index.NOT_ANALYZED,TermVector.NO);
		Field ftimezone = new Field("TIMEZONE",false,"",Field.Store.YES,Index.NOT_ANALYZED,TermVector.NO);
		d.add(fcontent);
//		d.add(fjson);
		d.add(ftime);
		d.add(fuserdesc);
		d.add(fuserlocation);
		d.add(ftimezone);
		
		Status statustweet = null;
		BufferedReader TR = null;
		String content = null;
		String text = null;
		long longtime = 0;
		for (File file : listOfFiles) {

			// Read the file, get the buffered reader.
			if (file.isFile()) {
				System.err.println(file.getName());
				try {
					TR = GetReader.getUTF8FileReader(path + file.getName());
				} catch (FileNotFoundException e) {
					System.err.println("NO such a file" + file.getName());
				} catch (UnsupportedEncodingException e) {
					System.err.println("Encoding Not Recognized for" + file.getName());
					e.printStackTrace();
				}
			}

			// read each line of the tweet file
			int i = 0;
			while ((text = TR.readLine()) != null) {
				if ((i++)%10000 ==0) System.err.print(i+"...");
				// parse json to status
				try {
					statustweet = DataObjectFactory.createStatus(text);
				} catch (TwitterException e) {
					System.err.println("tweet parsing error");
					continue;
				} catch (NullPointerException e) {
					System.err.println("Nothing in the content");
					continue;
				}

				// get the content of the tweet
				content = statustweet.getText();
				if (content == null) {
					System.err.println(statustweet.getId() + " is deleted tweet.");
					continue;
				} else {
					// get the time field of the tweet
					fcontent.setValue(content);
//					fjson.setValue(text);
					ftime.setLongValue(statustweet.getCreatedAt().getTime());
//					fuserdesc.setValue(statustweet.);
					Iwriter.addDocument(d);
//					System.out.println(d);
				}
			}
		}
		Iwriter.close();
		System.out.println(System.currentTimeMillis() - t);
	}

	File[] readFolder() {
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		return listOfFiles;
	}
}
