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
package edu.cmu.geoparser.nlp.lemma;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.cmu.geoparser.model.Sentence;
import edu.cmu.geoparser.nlp.Lemmatizer;
import edu.cmu.geoparser.nlp.tokenizer.EuroLangTwokenizer;
import edu.washington.cs.knowitall.morpha.*;
public class UWMorphaStemmer implements Lemmatizer{

	private StringBuilder sb;
	private String s;
	private List<String> n;
	public UWMorphaStemmer(){
	}
	
	private static UWMorphaStemmer uwStemmer;
	public static UWMorphaStemmer getInstance(){
	  if (uwStemmer==null)
	    uwStemmer =  new UWMorphaStemmer();
	  return uwStemmer;
	}
	
	 List<String> text,tokens;
	  public Sentence lemmatize(Sentence sent){
	    text = new ArrayList<String>(sent.tokenLength());
	    for ( int i = 0 ; i < sent.tokenLength(); i ++)
	      text.add(sent.getTokens()[i].getToken());
	    tokens = lemmatize(text);
	    for ( int i = 0 ; i < sent.tokenLength(); i ++){
	      sent.getTokens()[i].setLemma(tokens.get(i));
	    }
	    return sent;
	  }

	private List<String> lemmatize(List<String> sent) {
		// TODO Auto-generated method stub
		sb = new StringBuilder();
		for ( int i = 0 ; i <sent.size(); i ++){
			sb.append(sent.get(i)).append(" ");
		}
		
		return Arrays.asList(MorphaStemmer.stem(sb.toString().trim()).split(" "));
	}
	public static void main(String argv[]){
		UWMorphaStemmer n = new UWMorphaStemmer();
		
		//old way of using lemmatizer, no sentence wrapping yet.
		ArrayList<String> sent = new ArrayList<String>((EuroLangTwokenizer.tokenize(" books are surprising!")));
		
		System.out.println(n.lemmatize(sent));
	}

}
