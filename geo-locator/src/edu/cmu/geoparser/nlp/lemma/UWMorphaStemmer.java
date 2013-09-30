package edu.cmu.geoparser.nlp.lemma;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import edu.cmu.geoparser.nlp.Lemmatizer;
import edu.cmu.geoparser.nlp.tokenizer.EuroLangTwokenizer;
import edu.washington.cs.knowitall.morpha.*;
public class UWMorphaStemmer implements Lemmatizer{

	private StringBuilder sb;
	private String s;
	private List<String> n;
	public UWMorphaStemmer(){
	}
	@Override
	public List<String> lemmatize(List<String> sent) {
		// TODO Auto-generated method stub
		sb = new StringBuilder();
		for ( int i = 0 ; i <sent.size(); i ++){
			sb.append(sent.get(i)).append(" ");
		}
		
		return Arrays.asList(MorphaStemmer.stem(sb.toString().trim()).split(" "));
	}
	public static void main(String argv[]){
		UWMorphaStemmer n = new UWMorphaStemmer();
		ArrayList<String> sent = new ArrayList<String>((EuroLangTwokenizer.tokenize(" books are surprising!")));
		
		System.out.println(n.lemmatize(sent));
	}

}
