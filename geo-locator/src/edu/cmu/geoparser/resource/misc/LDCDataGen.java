package edu.cmu.geoparser.resource.misc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import edu.cmu.geoparser.io.GetReader;
import edu.cmu.geoparser.io.GetWriter;
import edu.cmu.geoparser.nlp.sentenceSplitter.JTextProSentSplitter;

public class LDCDataGen {

  enum STATE {
    PLACE, TEXT
  };

  public static void main(String a[]) throws IOException {

    BufferedWriter bw = GetWriter.getFileWriter("SPATIALML.csv");

    JTextProSentSplitter sentsplit = new JTextProSentSplitter();
    File d = new File("sgm/");
    File[] files = d.listFiles();
    BufferedReader br = null;
    // loop every file in the folder
    for (File f : files) {
      if (!f.getName().endsWith(".sgm"))
        continue;
      // System.out.println(f.getName());
      br = GetReader.getUTF8FileReader(f.getAbsolutePath());
      String line;
      // loop every line of the file
      int lineno = 0;
      String title = "", content = "";
      StringBuilder cont = new StringBuilder();
      StringBuilder tit = new StringBuilder();
      HashMap<String, String> places = new HashMap<String, String>();
      HashMap<String, String> tplaces = new HashMap<String, String>();

      // get content and tit.
      while ((line = br.readLine()) != null) {
        if (line.length() == 0)
          continue;
        if (lineno == 4) {
          title = line;
        }
        if (line.trim().equals("<TEXT>"))
          continue;
        if (lineno > 5) {
          if (line.startsWith("</TEXT>"))
            break;
          else {
            content += " " + line;
          }
        }
        lineno++;
      }// end of while

      // extract tags in tit and content.
      getPlace(title.replaceAll("&quot;", "\""), tplaces, tit);
      getPlace(content.replaceAll("&quot;", "\""), places, cont);

      // directly write the title and title locations to the file
      bw.write(tit.toString() + "\t");
      int i = 0;
      for (Entry<String, String> tplace : tplaces.entrySet()) {
        String s = tplace.getKey() + " " + tplace.getValue();
        if (i++ == 0)
          bw.write(s);
        else
          bw.write("," + s);
      }
      bw.write("\n");

      // SENTENCE SPLIT
      List<String> sents = sentsplit.split(cont.toString());
      // write each sentence, and write the correspondent places in the sentence.
      for (String sent : sents) {
        bw.write(sent + "\t");
        int j = 0;
        for (String k : places.keySet()) {
          if (sent.contains(k)) {
            String s = k + " " + places.get(k);
            if (j++ == 0)
              bw.write(s);
            else
              bw.write("," + s);
          }
        }
        bw.write("\n");
      }
    }// end of for

    bw.close();
  }

  private static void getPlace(String content, HashMap<String, String> place, StringBuilder context) {

    String tempplace = "";// location string
    String temphead = "";// description of the location string

    // String context is the total string
    int loccount = 0; // how many locations
    int state = -1;// outside location is 0. inside is 1.

    for (String s : content.split("[<>/>]")) {
      s = s.trim();
      if (s.trim().length() == 0) {
        context.append(" ");
        continue;
      }
      // start with context word, not location word
      if (s.startsWith("PLACE country")) {// location head
        state = 1;
        temphead = s;
      } else if (s.startsWith("PLACE")) {// location end
//        place.put((loccount++) + "_[" + tempplace + "]_", temphead);
        place.put(tempplace, temphead);
        state = 0;// set back to state 0;
      } else {// context or location word
        if (state == 0)
          context.append(" " + s);
        else if (state == -1) {
          context.append(" " + s);
          state = 0;
        } else { // state ==1
          tempplace = s;
          context.append(" " + tempplace);
        }
      }
    }// end for
  }// end getPlace
}
