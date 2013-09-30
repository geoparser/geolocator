package edu.cmu.geoparser.nlp.pos;

import is2.data.SentenceData09;
import is2.io.CONLLWriter09;
import is2.lemmatizer.Lemmatizer;

import is2.parser.Parser;
import is2.tag.Tagger;
import is2.tools.Tool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import edu.cmu.geoparser.nlp.POSTagger;
import edu.cmu.geoparser.nlp.tokenizer.EuroLangTwokenizer;

public class ESAnnaPOSTagger implements POSTagger{

	Tool tagger;
	SentenceData09 i;
	public ESAnnaPOSTagger(String f) {
		System.out.println("Initializing Spanish POSTagger");
		tagger = new Tagger(f);
		i = new SentenceData09();
	}

	// shows how to parse a sentences and call the tools
	public List<String> tag(List<String> text) {

		// Create a data container for a sentence

		String[] s = new String[text.size() + 1];
		s[0] = "root";
		for (int j = 0; j < text.size(); j++)
			s[j + 1] = text.get(j);
		i.init(s);
		// System.out.println(EuroLangTwokenizer.tokenize(text));

		// lemmatizing

		// System.out.println("\nReading the model of the lemmatizer");
		// Tool lemmatizer = new
		// Lemmatizer("resources.spanish/CoNLL2009-ST-Spanish-ALL.anna-3.3.lemmatizer.model");
		// // create a lemmatizer

		// System.out.println("Applying the lemmatizer");
		// lemmatizer.apply(i);

		// System.out.print(i.toString());
		// System.out.print("Lemmata: "); for (String l : i.plemmas)
		// System.out.print(l+" "); System.out.println();

		// morphologic tagging

		// System.out.println("\nReading the model of the morphologic tagger");
		// is2.mtag.Tagger morphTagger = new
		// is2.mtag.Tagger("resources.spanish/CoNLL2009-ST-Spanish-ALL.anna-3.3.morphtagger.model");

		// System.out.println("\nApplying the morpholoigc tagger");
		// morphTagger.apply(i);

		// System.out.print(i.toString());
		// System.out.print("Morph: "); for (String f : i.pfeats)
		// System.out.print(f+" "); System.out.println();

		// part-of-speech tagging

		// System.out.println("\nReading the model of the part-of-speech tagger");

//		System.out.println("Applying the part-of-speech tagger");
		tagger.apply(i);
		List<String> tags = new ArrayList<String>(i.ppos.length - 1);
		for (int j = 1; j < i.ppos.length; j++)
			tags.add(i.ppos[j]);
		return tags;
		// System.out.println("Part-of-Speech tags: ");
		// for (String p : i.ppos)
		// System.out.print(p + " ");
		// System.out.println();

		// parsing

		// System.out.println("\nReading the model of the dependency parser");
		// Tool parser = new Parser("models/prs-spa.model");

		// System.out.println("\nApplying the parser");
		// parser.apply(i);

		// System.out.println(i.toString());

		// write the result to a file

		// CONLLWriter09 writer = new
		// is2.io.CONLLWriter09("example-out.txt");

		// writer.write(i, CONLLWriter09.NO_ROOT);
		// writer.finishWriting();

	}

	public static void main(String argv[]) {
		String s = "RT @charlymaiz: Sismo de 5.3 en Neuquén http://bit.ly/cXoteZ via @Cynega #terremoto";
		List<String> text = EuroLangTwokenizer.tokenize(s);
		System.out.println(text);
		POSTagger postagger = new ESAnnaPOSTagger("resources.spanish/CoNLL2009-ST-Spanish-ALL.anna-3.3.postagger.model");
		List<String> tags = postagger.tag(text);
		System.out.println(tags);
	}

}
