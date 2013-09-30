package edu.cmu.geoparser.nlp.ner.StanfordCRF;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.sequences.DocumentReaderAndWriter;
import edu.stanford.nlp.util.StringUtils;

public class CRF_Test {


	public static void main(String[] argc) throws Exception {
		//StringUtils.printErrInvocationString("CRFClassifier", args);
		String[] args=new String[2];
		args[0]="-prop";args[1]="src/edu/cmu/geoparser/nlptools/ner/StanfordCRF/test.prop";
		Properties props = StringUtils.argsToProperties(args);
		
		CRFClassifier<CoreLabel> crf = new CRFClassifier<CoreLabel>(props);
		
		String testFile = crf.flags.testFile;
		String testFiles = crf.flags.testFiles;
		String textFile = crf.flags.textFile;
		String textFiles = crf.flags.textFiles;
		String loadPath = crf.flags.loadClassifier;
		String loadTextPath = crf.flags.loadTextClassifier;
		String serializeTo = crf.flags.serializeTo;
		String serializeToText = crf.flags.serializeToText;

		if (loadPath != null) {
			crf.loadClassifierNoExceptions(loadPath, props);
		} else if (loadTextPath != null) {
			System.err
					.println("Warning: this is now only tested for Chinese Segmenter");
			System.err.println("(Sun Dec 23 00:59:39 2007) (pichuan)");
			try {
				crf.loadTextClassifier(loadTextPath, props);
				// System.err.println("DEBUG: out from crf.loadTextClassifier");
			} catch (Exception e) {
				throw new RuntimeException("error loading " + loadTextPath, e);
			}
		} else if (crf.flags.loadJarClassifier != null) {
			crf.loadJarClassifier(crf.flags.loadJarClassifier, props);
		} else if (crf.flags.trainFile != null
				|| crf.flags.trainFileList != null) {
			
			//Wei Zhang: This is where the program starts to train.
			crf.train();
			////////////
			
		} else {
			crf.loadDefaultClassifier();
		}

		// System.err.println("Using " + crf.flags.featureFactory);
		// System.err.println("Using " +
		// StringUtils.getShortClassName(crf.readerAndWriter));

		if (serializeTo != null) {
			
			//Wei Zhang: This is used.
			crf.serializeClassifier(serializeTo);
			/////////////////////////
		}

		if (serializeToText != null) {
			crf.serializeTextClassifier(serializeToText);
		}

		if (testFile != null) {
			DocumentReaderAndWriter<CoreLabel> readerAndWriter = crf
					.defaultReaderAndWriter();
			if (crf.flags.searchGraphPrefix != null) {
				crf.classifyAndWriteViterbiSearchGraph(testFile,
						crf.flags.searchGraphPrefix, crf.makeReaderAndWriter());
			} else if (crf.flags.printFirstOrderProbs) {
				crf.printFirstOrderProbs(testFile, readerAndWriter);
			} else if (crf.flags.printProbs) {
				crf.printProbs(testFile, readerAndWriter);
			} else if (crf.flags.useKBest) {
				int k = crf.flags.kBest;
				crf.classifyAndWriteAnswersKBest(testFile, k, readerAndWriter);
			} else if (crf.flags.printLabelValue) {
				crf.printLabelInformation(testFile, readerAndWriter);
			} else {
				crf.classifyAndWriteAnswers(testFile, readerAndWriter);
			}
		}

		if (testFiles != null) {
			List<File> files = new ArrayList<File>();
			for (String filename : testFiles.split(",")) {
				files.add(new File(filename));
			}
			crf.classifyAndWriteAnswers(files, crf.defaultReaderAndWriter());
		}

		if (textFile != null) {
			crf.classifyAndWriteAnswers(textFile);
		}

		if (textFiles != null) {
			List<File> files = new ArrayList<File>();
			for (String filename : textFiles.split(",")) {
				files.add(new File(filename));
			}
			crf.classifyAndWriteAnswers(files);
		}

		if (crf.flags.readStdin) {
			crf.classifyStdin();
		}
	} // end main
}
