package edu.cmu.geoparser.nlp.pos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cmu.arktweetnlp.Tagger;
import cmu.arktweetnlp.Tagger.TaggedToken;
import cmu.arktweetnlp.impl.Model;
import cmu.arktweetnlp.impl.ModelSentence;
import cmu.arktweetnlp.impl.Sentence;
import cmu.arktweetnlp.impl.features.FeatureExtractor;
import edu.cmu.geoparser.nlp.POSTagger;
import edu.cmu.geoparser.nlp.tokenizer.EuroLangTwokenizer;

public class ENTweetPOSTagger implements POSTagger{

	/*
	 * English Tokenizer Utilize the tagger from Noah's Ark. Tagging speed is
	 * proportional to the length of the sentence. 0.08ms/token.
	 */
	public static Model model;
	public static FeatureExtractor featureExtractor;

	/**
	 * Loads a model from a file. The tagger should be ready to tag after
	 * calling this.
	 * 
	 * @param modelFilename
	 * @throws IOException
	 */
	private ENTweetPOSTagger(String f) {
		try {
			System.err.println("loading: "+f);
			model = Model.loadModelFromText(f);
			featureExtractor = new FeatureExtractor(model, false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("ARK TWEET POS TAGGER IS LOADED.");
	}
	private static ENTweetPOSTagger entweetPosTagger;
	
	public static ENTweetPOSTagger getInstance(){
	  if (entweetPosTagger==null)
	    return new ENTweetPOSTagger("res/en/model.20120919");
	  return entweetPosTagger;
	}
	/**
	 * Run the tokenizer and tagger on one tweet's text.
	 **/
	Sentence sentence= new Sentence();
	TaggedToken tt=new TaggedToken();
	ModelSentence ms;

  List<String> poss,tokens;
  public edu.cmu.geoparser.model.Sentence tag(edu.cmu.geoparser.model.Sentence sent){
    tokens = new ArrayList<String>(sent.tokenLength());
    for (int i = 0 ; i < sent.tokenLength(); i++){
      tokens.add(sent.getTokens()[i].getToken());
    }
    poss = tag(tokens);
    for ( int i = 0 ; i < sent.tokenLength(); i ++){
      sent.getTokens()[i].setPOS(poss.get(i));
    }
    return sent;
  }
  
	public List<String> tag(List<String> tokens) {

		sentence = new Sentence();
		tt = new TaggedToken();
		sentence.tokens = tokens;
		ms = new ModelSentence(sentence.T());
		featureExtractor.computeFeatures(sentence, ms);
		model.greedyDecode(ms, false);

		ArrayList<String> taggedTokens = new ArrayList<String>(sentence.T());

		for (int t = 0; t < sentence.T(); t++) {
			taggedTokens.add(model.labelVocab.name(ms.labels[t]));
		}

		return taggedTokens;
	}

	public static void main(String argv[]) throws IOException {

		ENTweetPOSTagger postagger = new ENTweetPOSTagger("res/en/model.20120919");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in, "utf-8"));
		System.out.print("> ");
		// Read user input
		String inputStr = br.readLine();
		while (!inputStr.equals("")) {

			long start = System.currentTimeMillis();
			// for(int i=0;i<1000;i++)
			{
				List<String> toks = EuroLangTwokenizer.tokenize(inputStr);
				// System.out.println("Tokenizing");
				List<String> tags = postagger.tag(toks);
				// System.out.println("Tagging");
				System.out.println(tags.toString());
			}
			long end = System.currentTimeMillis();
			System.out.print((end - start) + "\n> ");
			inputStr = br.readLine();
		}
	}


}
