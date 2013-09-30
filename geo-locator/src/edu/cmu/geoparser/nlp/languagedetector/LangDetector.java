package edu.cmu.geoparser.nlp.languagedetector;

import java.util.ArrayList;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.cybozu.labs.langdetect.Language;

public class LangDetector {

	Detector detector;

	public LangDetector(String f) {
		try {

			DetectorFactory.loadProfile(f);
			System.err.println("Language detector contructor is run.");
		} catch (LangDetectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String detect(String text) throws LangDetectException {
		detector = DetectorFactory.create();
		detector.append(text);
		String s = detector.detect();
		return s;
	}

	public ArrayList<Language> detectLangs(String text) throws LangDetectException {
		detector = DetectorFactory.create();
		detector.append(text);
		return detector.getProbabilities();
	}

	public static void main(String argv[]) throws LangDetectException {
		 LangDetector a = new LangDetector("langdetect.profile");
		System.out.println(a.detect("xcj sdfjisdfjis sdiospa swerc90e "));
		System.out.println(a.detect("this is english. ok? "));
	}
}
