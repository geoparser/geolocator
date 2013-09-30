package edu.cmu.geoparser.nlp.lemma;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import nl.rug.eco.lucene.EnglishLemmaTokenizer;

import edu.cmu.geoparser.nlp.Lemmatizer;
import edu.cmu.geoparser.nlp.pos.ENPOSTagger;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

/**
 * Problem:
 * The stanford parser could not use arbitrary tokenizer, but only the tokenizer from itself.
 * @author indri
 *
 */
public class Deprecated_StanfordEnLemmatizer implements Lemmatizer {

	EnglishLemmaTokenizer elt;

	public Deprecated_StanfordEnLemmatizer(MaxentTagger tagger) {
		elt = new EnglishLemmaTokenizer(tagger);
	}

	public List<String> lemmatize(List<String> sent) {
		String txt = new String();
		for (String s : sent)
			txt += s + " ";
		txt = txt.trim();
		String lemmatizedSent = "";
		StringReader sr = new StringReader(txt);
		elt.start(sr);
		// elt.lemmaNext = true;
		int ij = 0;
		try {
			while (elt.incrementToken()) {
				if (ij++ % 2 == 1)
					lemmatizedSent += " " + elt.getTermAtt();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Arrays.asList(lemmatizedSent.trim().split(" "));
	}

	public static void main(String argv[]) throws IOException, ClassNotFoundException {
		ENPOSTagger tagger = new ENPOSTagger("res/en/wsj-0-18-bidirectional-distsim.tagger");
		Deprecated_StanfordEnLemmatizer l = new Deprecated_StanfordEnLemmatizer(tagger.getTagger());
		System.out.println(l.lemmatize(Arrays.asList("The crowd has been evacuated".split(" "))));
	}
}
