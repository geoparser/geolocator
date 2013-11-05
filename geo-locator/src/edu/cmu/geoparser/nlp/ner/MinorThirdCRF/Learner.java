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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import edu.cmu.geoparser.io.GetReader;
import edu.cmu.minorthird.classify.ClassLabel;
import edu.cmu.minorthird.classify.Example;
import edu.cmu.minorthird.classify.Feature;
import edu.cmu.minorthird.classify.MutableInstance;
import edu.cmu.minorthird.classify.sequential.CMM;
import edu.cmu.minorthird.classify.sequential.CRFLearner;
import edu.cmu.minorthird.classify.sequential.CollinsPerceptronLearner;
import edu.cmu.minorthird.classify.sequential.SequenceDataset;
import edu.cmu.minorthird.util.IOUtil;

public class Learner {

	public static void main(String argv[]) throws IOException {
		String lang = "en";
		String learner = "crf";
		String featuretype
		// = "ct-only";
		 = "-3tok*.3pres.2cap.3caps.1pos.2pos.1gaz.1gazs.1cty.1ctys.-3-1prep";
		int iter = 150;	
		BufferedReader br = GetReader.getUTF8FileReader("trainingdata/" + lang + "NER/train/" + featuretype
				+ "-features.txt");
		// learn
		SequenceDataset dataset = new SequenceDataset();
		String anexample;
		ArrayList<String[]> examples = new ArrayList<String[]>();
		int sequenceid = 0;
		while ((anexample = br.readLine()) != null) {
			String[] featurestring = anexample.split("\t");
			if (featurestring.length < 2) {
				Example[] seq = new Example[examples.size()];
				int flen;
				for (int j = 0; j < examples.size(); j++) {
					String[] features = examples.get(j);
					flen = features.length - 1;
					// System.out.println(flen + "," + sequenceid +
					// " " + j);
					ClassLabel lab;
					// if(features[flen].equals("[O]"))continue;
					lab = new ClassLabel(features[flen]);
					MutableInstance inst = new MutableInstance(sequenceid + "", sequenceid + "." + j);
					// for (int i = 0; i < flen;
					// i++)System.out.println(features[i]);System.out.println();

					// rule out previous label.
					for (int i = 0; i < flen; i++)
						inst.addBinary(new Feature(features[i]));
					seq[j] = new Example(inst, lab);
					// System.out.println(seq[j].toString());
				}
				dataset.addSequence(seq);
				examples.clear();
				sequenceid++;
			} else {
				examples.add(featurestring);
			}
		}
		// initialize default properties.
		if (learner.equals("crf")) {
			CRFLearner CRF = new CRFLearner();
			CRF.setMaxIters(iter);
			CMM model = (CMM) CRF.batchTrain(dataset);
			IOUtil.saveSerialized(model, new File("trainingdata/" + lang + "NER/" + lang + "-crf" + iter + "-"
					+ featuretype + ".model"));

		} else if (learner.equals("cpl")) {
			CollinsPerceptronLearner cpl = new CollinsPerceptronLearner(3, iter);
			CMM model = (CMM) cpl.batchTrain(dataset);
			IOUtil.saveSerialized(model, new File("trainingdata/" + lang + "NER/" + lang + "-cpl" + iter + "-"
					+ featuretype + ".model"));
		}
		else
			System.out.println("Learner not defined.");
	}
}
