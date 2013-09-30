package edu.cmu.geoparser.Disambiguation.utils;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

import twitter4j.Status;
import com.cybozu.labs.langdetect.LangDetectException;
import edu.cmu.geoparser.nlp.languagedetector.LangDetector;

public class TimeWindow4Tweets {

	private static long timespan = 15l;// default 15 minutes.
	private static long sizeinmilliseconds;
	private static int maxsize = 5000; // default 5000 maxsize;

	IndexReader reader;
	private IndexSearcher ir;
	private QueryParser qp;
	Query seedq;
	ScoreDoc[] sd;

	private LinkedList<String> timewindow;

	private Document seed;
	private int seedcount;
	private String indexpath;
	LangDetector langDetector;
	public TimeWindow4Tweets(String indexpath, long minutes, int maxsize) throws IOException {

		langDetector = new LangDetector("langdetect.profile");
		this.timespan = minutes;
		this.sizeinmilliseconds = timespan * 60l * 1000l;
		this.maxsize = maxsize;
		timewindow = new LinkedList<String>();

		this.indexpath = indexpath;
		this.seedcount = 0;
		SimpleFSDirectory index = new SimpleFSDirectory(new File(this.indexpath));

		reader = IndexReader.open(index);
		ir = new IndexSearcher(reader);

		qp = new QueryParser(Version.LUCENE_36, indexpath, new StandardAnalyzer(Version.LUCENE_36, new HashSet<String>()));
	}

	public LinkedList<String> getTimeWindow() {
		return timewindow;
	}

	public void setSeed(Set<String> keywords) {
		String querystring = "";
		int i = 0;
		for (String k : keywords) {
			if (i++ == 0)
				querystring += "JSON: " + k + "*";
			else
				querystring += " OR JSON: " + k + "*";
		}
		try {
			seedq = qp.parse(querystring);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.err.println("Could not parse the seed search query.");
		}

		try {
			sd = ir.search(seedq, 1000000).scoreDocs;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.out.println("Could not search the index for seed query.");
		}
		if (sd.length == 0) {
			System.err.println("No seed generated. Please change the keywords.");
			return ;
		}

	}

	public Document getSearchNextSeed(Set<String> langs) {

		for (int count = seedcount; count < sd.length; count++) {
			try {
				seed = ir.doc(sd[count].doc);
			} catch (CorruptIndexException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.err.println("Could not find the doc in the index for seed.");
			}
			String lang;
			try {
				lang = langDetector.detect(JSON2Tweet.getStatusTweet(seed.get("JSON")).getText());
				System.out.println(lang + " : ");
			} catch (LangDetectException e) {
				// TODO Auto-generated catch block
				System.err.println("Lang not Detectable for seed. Continue to find next doc for seed.");
				continue;
			}
			if (langs.contains(lang)) {
				seedcount++;
				return seed;
			}
		}
		System.out.println("No seed generated At the end. Please change the keywords");
		return null;
	}

	public void generateTimeWindow(Set<String> lang, Set<String> topic, String location) throws IOException {
		timewindow.clear();

		long current = Long.parseLong(seed.get("CREATEDAT")) - sizeinmilliseconds;
		long later = current + sizeinmilliseconds;
		System.out.println("CURRENT DATE IS: " + new Date(current));

		BooleanQuery bq = new BooleanQuery();
		String querystring = "";
		int i = 0;
		for (String t : topic) {
			if (i++ == 0) {
				querystring += "JSON: " + t; // + "*";
				// querystring += " OR JSON: " + t + "*";
			} else {
				querystring += " OR JSON: " + t; // + "*";
				// querystring += " OR JSON: #" + t + "*";
			}
		}
		if (querystring.length() != 0)
			try {
				bq.add(qp.parse(querystring), Occur.MUST);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		try {
			bq.add(qp.parse("JSON:" + location), Occur.MUST);
			bq.add(NumericRangeQuery.newLongRange("CREATEDAT", current, later, false, false), Occur.MUST);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("QUERY FOR GEN. TIME WINDOW : " + bq);
		ScoreDoc[] sd = ir.search(bq, maxsize).scoreDocs;
		System.out.println(sd.length);

		Document document;
		String json;
		Status statustweet;
		boolean flag;
		for (ScoreDoc d : sd) {
			// System.out.println(d);
			// System.out.println(document);
			json = ir.doc(d.doc).get("JSON");
			/*
			 * if ((statustweet = JSON2Tweet.getStatusTweet(json)) == null) {
			 * System.err.println("Json tweet could not be parsed into status");
			 * continue; } /*try { if
			 * (!lang.contains(LangDetector.detect(statustweet.getText())))
			 * continue; } catch (LangDetectException e) { // TODO
			 * Auto-generated catch block
			 * System.err.println("Language Not Detectable."); continue; }
			 */
			flag = true;
			Status tweet = JSON2Tweet.getStatusTweet(json);
			if (tweet.getText().toLowerCase().contains(" " + location.toLowerCase())
					|| tweet.getText().toLowerCase().contains(location.toLowerCase() + " ")
					|| tweet.getText().toLowerCase().contains(location.toLowerCase())) {
				if (tweet.getText().toLowerCase().contains("AustinMahone".toLowerCase()))
					continue;
				if (tweet.getText().toLowerCase().contains("austin powers".toLowerCase()))
					continue;
				if (tweet.getText().toLowerCase().contains("AustinKeller".toLowerCase()))
					continue;
				timewindow.add(json);
			}
		}
	}

	public static void main(String arg[]) throws IOException {
		TimeWindow4Tweets tw4jt = new TimeWindow4Tweets("/Users/indri/Eclipse_workspace/Research/texasfiretweets/index/2011-09-05", 24 * 60, 50000);
		// "/Users/indri/Eclipse_workspace/Research/tweet2weeks/tweets.2010-03-19.gz.txt");

		Set<String> langs = new HashSet<String>();
		Set<String> keywords = new HashSet<String>();

		langs.add("en");
		keywords.add("wildfire");
		keywords.add("fire");

		tw4jt.setSeed(keywords);
		tw4jt.getSearchNextSeed(langs);

		String[] a = { "burn", "flame", "smoke", "evacuation", "rescue", "insurance", "die", "destroy", "destruction", "evacuation", "forest",
				"damage", "weather", "fighter", "donation", "victim", "emergency", "evacuee", "kill", "shelter", "help", "centraltxfires", "txfire",
				"txfires" };
		for (String as : a)
			keywords.add(as);

		tw4jt.generateTimeWindow(langs, keywords, "austin");
		LinkedList<String> window = tw4jt.getTimeWindow();
		Status tweet;
		for (String w : window) {
			tweet = JSON2Tweet.getStatusTweet(w);
			// System.out.println("_--------------");
			// System.out.println(tweet.getId());
			System.out.println(tweet.getText());
			// System.out.println(tweet.getCreatedAt());
		}
		System.out.println("Result size is: " + window.size());

	}
}