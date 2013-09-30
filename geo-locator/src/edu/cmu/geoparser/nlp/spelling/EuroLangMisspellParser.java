package edu.cmu.geoparser.nlp.spelling;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.util.Version;

import edu.cmu.geoparser.common.CollectionSorting;
import edu.cmu.geoparser.common.StringUtil;
import edu.cmu.geoparser.io.GetReader;
import edu.cmu.geoparser.model.Tweet;
import edu.cmu.geoparser.nlp.MisspellParser;
import edu.cmu.geoparser.parser.utils.ParserUtils;

public class EuroLangMisspellParser implements MisspellParser {

	IndexSearcher is;
	StandardAnalyzer an;
	BooleanQuery eq, gq;
	Query Wordquery, TRquery;
	QueryParser wparser, trparser;
	ScoreDoc[] esd, ensd;

	public EuroLangMisspellParser(IndexSearcher is) {
		this.is = is;
		this.an = new StandardAnalyzer(Version.LUCENE_36, new HashSet<String>());
		wparser = new QueryParser(Version.LUCENE_36, "NORM-WS", an);
		trparser = new QueryParser(Version.LUCENE_36, "TRIGRAM", an);
	}

	@Override
	public String parse(String s) throws IOException {

		String orig = s.trim();
		// get text, then deaccent, and lowered.
		s = StringUtil.getDeAccentLoweredString(s);

		// it's in the dictionary, then return itself.
		if (ParserUtils.isFilterword(s))
			return s;
		// check if exact match
		eq = new BooleanQuery();
		eq.add(new TermQuery(new Term("NORM-WS", s)), Occur.MUST);
		esd = is.search(eq, 1).scoreDocs;
		eq = new BooleanQuery();
		eq.add(new TermQuery(new Term("NORM-NO-WS", s)), Occur.MUST);
		ensd = is.search(eq, 1).scoreDocs;
		// got exact match!
		if (esd.length > 0 || ensd.length > 0) {
			// System.out.println("exact");
			return (esd.length > ensd.length) ? is.doc(esd[0].doc).get("ORIGIN") : is.doc(ensd[0].doc).get("ORIGIN");
		} else
		// no exact match!
		{
			// combination candidates match?
			List<String> ntokens = new ArrayList<String>();
			ntokens.addAll(StringUtil.repeatNormalization(s));
//			System.out.println(ntokens);

			eq = new BooleanQuery();
			for (String ntoken : ntokens) {
				eq.add(new TermQuery(new Term("NORM-WS", ntoken)), Occur.SHOULD);
				eq.add(new TermQuery(new Term("NORM-NO-WS", ntoken)), Occur.SHOULD);
			}
			if (is.search(eq, 1).scoreDocs.length > 0) {
//				System.out.println("FOUND!!!!!");
				esd = is.search(eq, 1).scoreDocs;
				return (esd.length > ensd.length) ? is.doc(esd[0].doc).get("ORIGIN") : is.doc(ensd[0].doc).get("ORIGIN");
			}

			// No combination candidate match. Guess Match
			HashMap<String, Integer> matches = new HashMap<String, Integer>();
			for (String ntoken : ntokens) {
				// generate fuzzy query
				char[] cnorm = StringUtil.getDeAccentLoweredChars(ntoken);
				gq = new BooleanQuery();
				try {
					Wordquery = wparser.parse(s);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					continue;
				}
				try {
					TRquery = trparser.parse(StringUtil.factorize(StringUtil.getTrigram(cnorm)));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					continue;
				}
				gq.add(Wordquery, Occur.SHOULD);
				gq.add(TRquery, Occur.SHOULD);
				// do fuzzy search
				for (ScoreDoc sd : is.search(gq, 30).scoreDocs) {
					Document d = is.doc(sd.doc);
					String lw = d.get("NORM-WS");
					// System.out.println(lw);
					// System.out.println(ntoken);
					int ed = StringUtil.editDistance(ntoken, lw);
					double p = (double) ed / (double) s.length();
					// System.out.println(p);
					if (p < 0.25) {
						// System.out.println("Fuzzy match:");
						matches.put(d.get("ORIGIN"), ed);
					}
				}
			}
			if (matches.isEmpty())
				// if no fuzzy match? then say, it's OOV or normal word. Leave
				// it alone.
				// System.out.println("Not corrected.");
				return orig;
			else {
				Set<Entry<String, Integer>> a = matches.entrySet();
				LinkedHashMap<String, Integer> rmatches = CollectionSorting.sortHashMapByValuesD(matches);
				return rmatches.entrySet().iterator().next().getKey().toString();

			}

		}
	}

	public static void main(String argv[]) throws IOException, ParseException {
		EuroLangMisspellParser parser = new EuroLangMisspellParser(GetReader.getIndexSearcher("GazIndex"));
		double s = System.currentTimeMillis();
		// for (int i = 0; i < 300; i++)
		{
			System.out.println("Perfectly Working Cases:\n");
			
			System.out.println(parser.parse("Pittsburghhhhhh"));//light redundent
			System.out.println(parser.parse("waaaaaaashiiiiiiingggggttttoooooooonnnnn"));//heavily redundent
			System.out.println();
			System.out.println(parser.parse("califonria"));//char flip
			System.out.println(parser.parse("califooooonnnnnnnnnnria"));//char flip and redundent
			System.out.println();
			System.out.println(parser.parse("philadylphia"));//char misspelled
			System.out.println(parser.parse("philadylphiaaaaaaaaaaaa"));//char misspelled + redudent
			System.out.println();
			System.out.println(parser.parse("LosAngeles"));//words sticking together
			System.out.println(parser.parse("losangeles"));//not capitalized
			
			System.out.println();
			System.out.println("Not Working Cases:");
			System.out.println(parser.parse("Angeles Los"));// words fliped
			System.out.println(parser.parse("philadylfia"));//too much mistake in it.

		}
		double e = System.currentTimeMillis();
		System.out.println(e - s);
	}

}
