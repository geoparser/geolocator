package edu.cmu.geoparser.nlp.sentenceSplitter;

import java.util.List;

import sensegmenter.SenSegmenter;
import edu.cmu.geoparser.nlp.SentenceSplitter;

public class JTextProSentSplitter implements SentenceSplitter{

  private SenSegmenter ss;
  public JTextProSentSplitter(){
    ss = new SenSegmenter("models/SenSegmenter");
    ss.init();
  }

  @SuppressWarnings("unchecked")
  public  List<String> split(String s) {
    // TODO Auto-generated method stub
    return ss.senSegment(s);
  }

}
