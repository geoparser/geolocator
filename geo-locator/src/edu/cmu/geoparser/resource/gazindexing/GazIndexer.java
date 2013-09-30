package edu.cmu.geoparser.resource.gazindexing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import edu.cmu.geoparser.common.StringUtil;
import edu.cmu.geoparser.io.GetReader;
import edu.cmu.geoparser.io.GetWriter;

/*
 * This is used for indexing the Gazetteer for misspelling checking.
 * 
 * @Input: Gaz entries
 * 
 * @Output: Index of Gaz
 * 
 * Features used for each word: (e.g. Chilee) 1. c,h,i,l,e,e 2. ch,hi,il,le,ee
 * 3. c_0,h_1,i_2,l_3,e_4,e_5
 */
public class GazIndexer {

	static String f_unigram, f_bigram, f_trigram, f_positionunigram;

	static String getUnigram() {
		return f_unigram;
	}

	static String getBigram() {
		return f_bigram;
	}

	static String getTrigram() {
		return f_trigram;
	}

	static String getPositionUnigram() {
		return f_positionunigram;
	}

	static void getIndexFeatures(String phrase) {
		// prepare for indexing
		char[] locchars = StringUtil.getDeAccentLoweredChars(phrase);
		String[] bigramloc = StringUtil.getBigram(locchars);
		String[] trigramloc = StringUtil.getTrigram(locchars);
		String[] positionloc = StringUtil.getPosition(locchars);

		f_unigram = StringUtil.factorize(locchars);
		f_bigram = StringUtil.factorize(bigramloc);
		f_trigram = StringUtil.factorize(trigramloc);
		f_positionunigram = StringUtil.factorize(positionloc);
	}

	// main method for indexing gazatteer into index.
	void indexGazatteer(BufferedReader br, IndexWriter iw) throws IOException, InterruptedException {

		Document d = new Document();
		NumericField nfid = new NumericField("ID", Field.Store.YES, true);
		NumericField nflong = new NumericField("LONGTITUDE", Field.Store.YES, true);
		NumericField nfla = new NumericField("LATITUDE", Field.Store.YES, true);
		NumericField nfpop = new NumericField("POPULATION", Field.Store.YES, true);
		Field sforigin = new Field("ORIGIN", false, "", Field.Store.YES, Index.ANALYZED, TermVector.NO);
		Field normws = new Field("NORM-WS", false, "", Field.Store.YES, Index.NOT_ANALYZED, TermVector.NO);
		Field normnws = new Field("NORM-NO-WS", false, "", Field.Store.YES, Index.NOT_ANALYZED, TermVector.NO);
		Field sfotherlang = new Field("OTHERLANG", false, "", Field.Store.YES, Index.NOT_ANALYZED, TermVector.NO);
		Field sfunigram = new Field("UNIGRAM", false, "", Field.Store.YES, Index.ANALYZED, TermVector.NO);
		Field sfbigram = new Field("BIGRAM", false, "", Field.Store.YES, Index.ANALYZED, TermVector.NO);
		Field sftrigram = new Field("TRIGRAM", false, "", Field.Store.YES, Index.ANALYZED, TermVector.NO);
		Field sfposition = new Field("POSITION", false, "", Field.Store.YES, Index.ANALYZED, TermVector.NO);
		Field sfcountrystate = new Field("COUNTRYSTATE", false, "", Field.Store.YES, Index.NOT_ANALYZED, TermVector.NO);
		Field sffeature = new Field("FEATURE", false, "", Field.Store.YES, Index.NOT_ANALYZED, TermVector.NO);
		Field sftimezone = new Field("TIMEZONE", false, "", Field.Store.YES, Index.NOT_ANALYZED, TermVector.NO);
		d.add(nfid);
		d.add(nflong);
		d.add(nfla);
		d.add(nfpop);
		d.add(sforigin);
		d.add(normws);
		d.add(normnws);
		d.add(sfotherlang);
		d.add(sfunigram);
		d.add(sfbigram);
		d.add(sftrigram);
		d.add(sfposition);
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
			long lid = Long.parseLong(id);
			String phrase = column[1];
			String otherlang = column[3];
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
				longpop = -1l;
			longpop = Long.parseLong(population);
			String timezone = column[17];

			// To Do: set values to document d, and index it
			nfid.setLongValue(lid);// 1
			nflong.setDoubleValue(dlong);
			nfla.setDoubleValue(dla);
			nfpop.setLongValue(longpop);
			sforigin.setValue(phrase);// 5
			normws.setValue(StringUtil.getDeAccentLoweredString(phrase));
			normnws.setValue(StringUtil.getDeAccentLoweredString(phrase).replaceAll(" ", ""));
			sfotherlang.setValue(otherlang);

			getIndexFeatures(phrase);

			sfunigram.setValue(getUnigram());
			sfbigram.setValue(getBigram());
			sftrigram.setValue(getTrigram());
			sfposition.setValue(getPositionUnigram());// 10
			sfcountrystate.setValue(country + "_" + state);
			sffeature.setValue(featureclass + "_" + feature);
			sftimezone.setValue(timezone);// 13

			// add this new document.
			iw.addDocument(d);
		}
	}

	public static void main(String argv[]) throws Exception {

		GazIndexer gi = new GazIndexer();

		String mode = argv[0];
		if (mode.equals("-write")) {
			if (argv.length != 2)
				throw new Exception("Command line argument number wrong");
			BufferedReader br = GetReader.getUTF8FileReader(argv[1]);
			IndexWriter iw = GetWriter.getIndexWriter("GazIndex/");
			iw.deleteAll();
			gi.indexGazatteer(br, iw);
			iw.optimize();
			iw.close();
			br.close();
		}
		if (mode.equals("-read")) {
			System.out.println("input id. Output basic information. For debugging.");
			// query first two fields.
			IndexSearcher is = GetReader.getIndexSearcher("GazIndex/");
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
				System.out.println(d.get("ID"));
				System.out.println(d.get("ORIGIN"));
				System.out.println(d.get("LONGTITUDE") + " " + d.get("LATITUDE"));
				System.out.println("lookup time: " + (end - start));
			}
		}
	}
}
