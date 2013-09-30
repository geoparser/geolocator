package edu.cmu.geoparser.nlp.sentenceSplitter;

import java.util.List;
import java.text.*;
import sensegmenter.SenSegmenter;

public class testseg {

	public static void main(String argv[]) {
		SenSegmenter ss = new SenSegmenter("models/SenSegmenter");
		ss.init();
		String t = "My wife is the greatest wife of all time. She's having a boy! A boy!!!";
		List<String> res = ss.senSegment(t);
		for (String s : res) {
			System.out.println(s);
		}

		BreakIterator b = BreakIterator.getSentenceInstance();
		b.setText(t);
		int start = b.first();
		for (int end = b.next(); end != BreakIterator.DONE; start = end, end = b.next()) {
			System.out.println(t.substring(start, end));
		}
	}
}
