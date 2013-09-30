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
