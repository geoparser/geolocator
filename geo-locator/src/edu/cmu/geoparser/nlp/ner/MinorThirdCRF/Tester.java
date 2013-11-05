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
package edu.cmu.geoparser.nlp.ner.MinorThirdCRF;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import edu.cmu.geoparser.io.GetReader;
import edu.cmu.minorthird.classify.ClassLabel;
import edu.cmu.minorthird.classify.Example;
import edu.cmu.minorthird.classify.Feature;
import edu.cmu.minorthird.classify.MutableInstance;
import edu.cmu.minorthird.classify.sequential.CMM;
import edu.cmu.minorthird.util.IOUtil;

public class Tester {

	/**
	 * Best options:
	 * 
	 *  en CRF:iteration = 100.
	 *  es CRF: iteration =150.
	 *  
	 *  en CPL: iteration = 10 (not as good as crf)
	 *  es CPL: iteration = 5 (overfit, no recall)
	 *  
	 * @param argv
	 * @throws IOException
	 */ 
	public static void main(String argv[]) throws IOException {
		String lang = "es";
		int iteration = 150;
		String learningalgo = "crf";
		String featuretype
		="-3tok_.3pres.2cap.3caps.1pos.2pos.1gaz.1gazs.1cty.1ctys.-3-1prep";		//		= "ct-1+1";
		String crff = lang + "-" + learningalgo + iteration + "-" + featuretype + ".model";
		// = "en-crf150-ct-only.model";
		// = "en-crf100-all.model";
		// = "en-crf100-ct-only.model";
		System.out.println("Begin Loading the model: " + crff);
		CMM model = null;
		if (learningalgo.equals("cpl"))
			model = (CMM) IOUtil.loadSerialized(new java.io.File("trainingdata/" + lang + "NER/" + crff));
		if (learningalgo.equals("crf"))
			model = (CMM) IOUtil.loadSerialized(new java.io.File("trainingdata/" + lang + "NER/" + crff));
		System.out.println("Model loaded. Tagging test set and Evaluate:");

		// usage:
		// Example[] sequence;
		// model.classification(sequence);

		BufferedReader br = GetReader.getUTF8FileReader("trainingdata/" + lang + "NER/test/" + featuretype
				+ "-features.txt");
		BufferedReader csvbr = GetReader.getUTF8FileReader("trainingdata/" + lang + "NER/test/raw.csv");
		csvbr.readLine();
		// 0: O, 1:ST, 2:BD, 3:TP, 4: AB.
		double[] org = new double[5];
		double[] res = new double[5];
		double[] mth = new double[5];
		double total=0.0d;
		double fzyorg = 0, fzyres = 0, fzymth = 0;
		// test
		String anexample;
		ArrayList<String[]> examples = new ArrayList<String[]>();
		int sequenceid = 0;
		while ((anexample = br.readLine()) != null) {
			String[] featurestring = anexample.split("\t");
			if (featurestring.length < 2) {
				Example[] seq = new Example[examples.size()];
				int flen = 0;
				for (int j = 0; j < examples.size(); j++) {
					String[] features = examples.get(j);
					flen = features.length - 1;
					ClassLabel lab;
					lab = new ClassLabel(features[flen]);
					MutableInstance inst = new MutableInstance(sequenceid, sequenceid + "" + j);
					// rule out previous label.
					for (int i = 0; i < flen; i++) {
						inst.addBinary(new Feature(features[i]));
					}
					seq[j] = new Example(inst, lab);
				}
				ClassLabel[] reslabel = model.classification(seq);

				// build measurement:

				for (int i = 0; i < reslabel.length; i++) {
					total += 1;
					String tag = seq[i].getLabel().bestClassName();
					if (tag.equals("O"))
						org[0] += 1;
					else {
						fzyorg += 1;
						if (tag.equals("ST"))
							org[1] += 1;
						else if (tag.equals("BD"))
							org[2] += 1;
						else if (tag.equals("TP"))
							org[3] += 1;
						else if (tag.equals("AB"))
							org[4] += 1;
					}
					String result = reslabel[i].bestClassName();
					if (result.equals("O"))
						res[0] += 1;
					else {
						fzyres += 1;
						if (result.equals("ST"))
							res[1] += 1;
						else if (result.equals("BD"))
							res[2] += 1;
						else if (result.equals("TP"))
							res[3] += 1;
						else if (result.equals("AB"))
							res[4] += 1;
					}

					if (tag.equals(result) && tag.equals("O"))
						mth[0] += 1;
					else if (tag.equals(result) && tag.equals("ST"))
						mth[1] += 1;
					else if (tag.equals(result) && tag.equals("BD"))
						mth[2] += 1;
					else if (tag.equals(result) && tag.equals("TP"))
						mth[3] += 1;
					else if (tag.equals(result) && tag.equals("AB"))
						mth[4] += 1;

					if (!tag.equals("O") && !result.equals("O"))
						fzymth += 1;
				}
				examples.clear();
				sequenceid++;
			} else {
				examples.add(featurestring);
			}
		}
		System.out.println("tested " +sequenceid+ " "+ lang+"tweets in total.");
		String[] t = new String[] { "O", "ST", "BD", "TP", "AB" };
		for (int i = 0 ;i < t.length; i++){
			double p = (mth[i] / res[i]); double r = (mth[i] / org[i]);
			double f1 = 2.0d/(1.0d/p +1.0d/r);
			System.out.println(t[i] + ": p " + p + " r " + r + " f1 " + f1);
		}
		System.out.println("Fuzzy results:");
		double fp = (fzymth/fzyres), fr = (fzymth/fzyorg);
		double ff1= 2.0d/(1.0d/fp+1.0d/fr);
		System.out.println("p "+fp+" r "+fr+" f1 "+ff1);
	}
}
