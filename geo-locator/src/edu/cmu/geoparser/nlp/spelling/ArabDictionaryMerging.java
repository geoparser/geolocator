package edu.cmu.geoparser.nlp.spelling;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.util.Version;

import edu.cmu.geoparser.common.CollectionSorting;
import edu.cmu.geoparser.common.StringUtil;
import edu.cmu.geoparser.io.GetReader;
import edu.cmu.geoparser.io.GetWriter;

public class ArabDictionaryMerging {

	IndexSearcher indexSearcher;
	Analyzer analyzer;
	QueryParser parser;
	String[] fields = { "ORIGIN", "UNIGRAM", "NORM-WS", "NORM-NO-WS", "BIGRAM", "TRIGRAM", "POSITION" };

	public ArabDictionaryMerging(IndexSearcher indexSearcher) {

		this.indexSearcher = indexSearcher;
		this.analyzer = new StandardAnalyzer(Version.LUCENE_36, new HashSet<String>());

	}

	public void closeSearcher() throws IOException {
		indexSearcher.close();
	}

	Object[] normalization(String phrase) throws IOException, ParseException {

		// System.out.println("before Normalization : " + phrase);
		// step 1: deaccent, and lowercase
		// For arabic, we don't do this step.
		String normphrase = StringUtil.deAccent(phrase).toLowerCase();
		//System.out.println(normphrase);
		// System.out.println("After Normalization : " + normphrase);
		// step 2: repetition reduction
		ArrayList<String> powersetphrase = StringUtil.repeatNormalization(normphrase);
		if (powersetphrase == null)
			return null;

		return (Object[]) powersetphrase.toArray();
	}

	/**
	 * Readin lowercased word, with or without whitespace. exact match string
	 * with or without space. Either one is OK.
	 * 
	 * @param phrase
	 * @return Null if no gaz match found. Or return the country-state that is contained in the phrase.
	 * @throws ParseException
	 * @throws CorruptIndexException
	 * @throws IOException
	 */

	public String gazMatch(String phrase, double dlat, double dlon) throws ParseException, CorruptIndexException,
			IOException {
		IndexSearcher is = indexSearcher;

		// int wordnumber = phrase.split(" ").length;

		BooleanQuery bquery = new BooleanQuery();
		// exact match
		bquery.add(new TermQuery(new Term("NORM-WS", phrase)), Occur.SHOULD);
		Query lonrange = NumericRangeQuery.newDoubleRange("LONGTITUDE", dlon - 0.5, dlon + 0.5, true, true);
		Query latrange = NumericRangeQuery.newDoubleRange("LATITUDE", dlat - 0.5, dlat + 0.5, true, true);

		bquery.add(lonrange, Occur.MUST);
		bquery.add(latrange, Occur.MUST);
		// no space exact match
		String nospace = phrase.replace(" ", "").trim();
		 bquery.add(new TermQuery(new Term("NORM-NO-WS", nospace)),Occur.SHOULD);

		ScoreDoc[] sd = is.search(bquery, 1).scoreDocs;
		if (sd.length > 0) {
			for (ScoreDoc d : sd) {
				if (
						is.doc(d.doc).get("ORIGIN").equals(phrase) ||
						is.doc(d.doc).get("NORM-WS").equals(phrase)||
						is.doc(d.doc).get("NORM-NO-WS").equals(phrase)
						){
					System.out.println(is.doc(d.doc).get("ORIGIN") + " ORIGIN matched.");
					return is.doc(d.doc).get("ORIGIN")+"	"+is.doc(d.doc).get("LATITUDE")+","+is.doc(d.doc).get("LONGTITUDE")+"	"+is.doc(d.doc).get("COUNTRYSTATE");
				}
				else{
					continue;
				}
			}
			
		} else {
			System.out.println(phrase + " No exact match,or lowercased match, or lowercased non-whitepace match!");
			return null;
		}
		return null;// this is meaningless. It's just a loop hole of the program
	}
	/**
	 * 
	 * @param phrase
	 * @param dlat
	 * @param dlon
	 * @return Null if no exact match. Else return gazMatch() result.
	 * @throws IOException
	 * @throws ParseException
	 */
	public String exactMatch(String phrase, double dlat, double dlon) throws IOException, ParseException {
		
		Object[] norms = normalization(phrase);
		// System.out.println((String)norms[0]);
		for (Object norm : norms) {
			String r =gazMatch((String)norm, dlat, dlon);
			if (r!=null)
				return r;
		}
		return null;
	}

	private HashMap<String, Float> getGuessCandidates(String phrase, double dlat, double dlon) throws ParseException,
			CorruptIndexException, IOException {
		// TODO Auto-generated method stub

		IndexSearcher is = indexSearcher;
		Analyzer an = analyzer;

		BooleanQuery bquery = new BooleanQuery();
		String nphrase = StringUtil.getDeAccentLoweredString(phrase);
		char[] norm = StringUtil.getDeAccentLoweredChars(phrase);

		// exact total string match
		Query Nquery = new TermQuery(new Term("NORM-WS", nphrase));

		// Exact range
		Query lonrange = NumericRangeQuery.newDoubleRange("LONGTITUDE", dlon - 0.5, dlon + 0.5, true, true);
		Query latrange = NumericRangeQuery.newDoubleRange("LATITUDE", dlat - 0.5, dlat + 0.5, true, true);

		// query features
		Query Wordquery = new QueryParser(Version.LUCENE_36, "ORIGIN", an).parse(phrase);
		// Query Uniquery = new QueryParser(Version.LUCENE_36, "UINGRAM",
		// an).parse(StringUtil.factorize(norm));

		Query Biquery = new QueryParser(Version.LUCENE_36, "BIGRAM", an).parse(StringUtil.factorize(StringUtil
				.getBigram(norm)));
		Biquery.setBoost(6);

		Query TRquery = new QueryParser(Version.LUCENE_36, "TRIGRAM", an).parse(StringUtil.factorize(StringUtil
				.getTrigram(norm)));
		TRquery.setBoost(0.16f);

		bquery.add(Nquery, Occur.SHOULD);
		bquery.add(lonrange, Occur.MUST);
		bquery.add(latrange, Occur.MUST);
		bquery.add(Wordquery, Occur.SHOULD);
		// bquery.add(Uniquery,Occur.SHOULD);
		// bquery.add(Biquery,Occur.SHOULD);
		bquery.add(TRquery, Occur.SHOULD);
		// System.out.println(bquery);
		int u = 0;
		HashMap<String, Float> map = new HashMap<String, Float>();
		for (ScoreDoc sd : is.search(bquery, 30).scoreDocs) {
			Document d = is.doc(sd.doc);
			// if (d.get("LW").split(" ").length != wordnumber)continue;
			String lw = d.get("ORIGIN");
			String lon = d.get("LONGTITUDE");
			String lat = d.get("LATITUDE");
			String ctryst = d.get("COUNTRYSTATE");
			// System.out.println(lw);
			if (!map.containsKey(lw))
				map.put(lw + "	" + lat + "," + lon+"	"+ctryst, 1.0f);
			else
				map.put(lw + "	" + lat + "," + lon+"	"+ctryst, map.get(lw) + 1.0f);
		}
		// System.out.println(map);
		return map;
	}

	ArrayList<Entry<String, Float>> getGuessMatch(String phrase, double dlat, double dlon) throws CorruptIndexException,
			ParseException, IOException {
		HashMap<String, Float> map = getGuessCandidates(phrase, dlat, dlon);
		Iterator<Entry<String, Float>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Float> e = iter.next();
			String[] toks = e.getKey().split("	");
			phrase = phrase.trim();
			toks[0] = toks[0].trim();
			float distance = StringUtil.editDistance(phrase, toks[0]);
			//float commonlength =StringUtil.commonLengthfromHead(phrase, toks[0]);
			// map.put(e.getKey(), (float) (e.getValue()/distance));
			// map.put(e.getKey(), (float)
			// (Math.log(1+e.getValue())/Math.log(0.01+distance)));
			// map.put(e.getKey(), (float)
			// (Math.log(1+e.getValue())/distance));
//			map.put(e.getKey(), 0.8f*(1.0f - (float) distance / (float) phrase.length())+ 0.2f*((float)commonlength/(float)phrase.length()));
			map.put(e.getKey(), (1.0f - (float) distance / (float) phrase.length()));
		}
		ArrayList<Entry<String, Float>> r = CollectionSorting.rankArray(new ArrayList<Entry<String, Float>>(map
				.entrySet()));
		return r;	
	}

	public static void main(String argv[]) throws IOException, ParseException {
		System.out.println("TODO: Add dictionary. reBuild Index without space. ");
		ArabDictionaryMerging mc = new ArabDictionaryMerging(GetReader.getIndexSearcher("EG"));
		BufferedReader br = GetReader.getUTF8FileReader("/Users/indri/Documents/Research_data/misspell/arabic/ArabicAll.csv");
		BufferedWriter bw = GetWriter.getFileWriter("/Users/indri/Documents/Research_data/misspell/arabic/matches-ArabicAll.csv");
		// System.out.print("> ");
		// Read user input
		String phrase = br.readLine();
		int i = 0;
		while (!phrase.equals("")) {
			System.out.println("iteration " + (i++));
			String[] toks = phrase.split("	");
			System.out.println(Arrays.asList(toks).toString());
			phrase = toks[0].trim().replace("(", " ").replace(")"," ")
					.replace(":", " ").replace("*", " ").replace("-", " ")
					.replace("]", " ");
			String lat = toks[toks.length-2];
			Double dlat = Double.parseDouble(lat);
			String lon = toks[toks.length-1];
			Double dlon = Double.parseDouble(lon);
			
			String res;
			if ((res = mc.exactMatch(phrase, dlat, dlon)) == null) {
				System.out.println("The word is not matched. Now guessing:");
				ArrayList<Entry<String, Float>> guesses = mc.getGuessMatch(phrase, dlat, dlon);
				System.out.println("Guessed is: " + guesses);
				int count = 0;
				for (Entry<String, Float> e : guesses) {
					if (++count > 5)
						break;
					bw.write(phrase + "	" + lat + "	" + lon + "	" + count + "	" + e.getKey() + "	" + e.getValue());
					if (e.getValue() > 0.75 && e.getValue() < 0.9)
						bw.write("	[Similar]\n");
					else if (e.getValue() >= 0.9)
						bw.write("	[Guess Match]\n");
					else {
						String cand = e.getKey().split("	")[0].trim();
						if (cand.contains(phrase) || phrase.contains(cand))
							bw.write("	[Containment]\n");
						else
							bw.write("\n");
					}
				}
			} else{
				System.out.println("The location is matched.");
				bw.write(phrase + "	" + lat + "	" + lon + "		" + res + "	" + 1+"	[Matched]\n");
				
			}
			// System.out.print("\n> ");
			phrase = br.readLine();
			if (phrase == null)
				break;
		}
		br.close();
		bw.close();
	}

	private String removePuncs(String phrase) {
		// TODO Auto-generated method stub
		char[] newchars = new char[phrase.length()];
		for (int j = 0; j < newchars.length; j++) {
			char i = phrase.charAt(j);
			if (i > 122 || i < 65 || (i > 90 && i < 97))
				newchars[j] = ' ';
			else
				newchars[j] = i;
		}
		return new String(newchars);
	}

}
