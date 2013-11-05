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
