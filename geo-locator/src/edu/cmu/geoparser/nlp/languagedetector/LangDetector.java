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
