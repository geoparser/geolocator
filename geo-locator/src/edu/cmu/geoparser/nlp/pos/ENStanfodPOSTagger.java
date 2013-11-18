package edu.cmu.geoparser.nlp.pos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.geoparser.nlp.tokenizer.EuroLangTwokenizer;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;


/**
 * The Stanford tagger is 60 ms/30toks. 35ms/17toks, -> 2ms/1tok.
 */
public class ENStanfodPOSTagger {
	public MaxentTagger tagger;
	public ENStanfodPOSTagger(String f) {
		tagger = new MaxentTagger(f);
	}

	public MaxentTagger getTagger(){
		return tagger;
	}
	public List<String> tag(List<String> tokens) {
		StringBuilder tokenstring = new StringBuilder();
		for (String tok : tokens) {
			tokenstring.append(tok).append(" ");
		}
		String tagged = tagger.tagString(tokenstring.toString());
		// System.out.println(tagged);
		String[] toktags = tagged.split("[ ]");
		List<String> tags = new ArrayList<String>(toktags.length);
		for (String toktag : toktags) {
			tags.add(toktag.split("[_]")[1]);
		}
		return tags;

	}

	public static void main(String avp[]) throws IOException {
		ENStanfodPOSTagger entagger = new ENStanfodPOSTagger("resources.english/wsj-0-18-bidirectional-distsim.tagger");
		
		String sample = "40.#twitterafterdark she works at the ford plant! http://t.co/ruaip2aa #pussy #booty|org";
		List<String> tagged = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in, "utf-8"));
		String inputStr = br.readLine();
		while (inputStr != "") {
			long start = System.currentTimeMillis();
//			for (int i = 0; i < 1000; i++) 
			{
				List<String> tokens = EuroLangTwokenizer.tokenize(inputStr);
				 System.out.println(tokens.toString());
				// The tagged string
				tagged = entagger.tag(tokens);
			}
			long end = System.currentTimeMillis();
			System.out.println((end - start) + tagged.toString());
			inputStr = br.readLine();
		}
	}
}
