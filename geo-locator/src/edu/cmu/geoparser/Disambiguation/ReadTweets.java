package edu.cmu.geoparser.Disambiguation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import edu.cmu.geoparser.io.GetReader;
import edu.cmu.geoparser.io.GetWriter;
import edu.cmu.geoparser.model.LocEntity;
import edu.cmu.geoparser.model.Tweet;
import edu.cmu.geoparser.parser.english.EnglishParser;
import edu.cmu.geoparser.resource.trie.IndexSupportedTrie;

public class ReadTweets {
  BufferedReader br;

  ReadTweets(String fileName) throws FileNotFoundException, UnsupportedEncodingException {
    br = GetReader.getUTF8FileReader(fileName);
  }

  public TweetEntry readLine() throws IOException {
    String line = br.readLine();
    if (line == null)
      return null;
    TweetEntry entry = new TweetEntry();
    String[] toks = line.split("\t");
    entry.text = toks[0];
    if(toks.length<2)return entry;
    entry.toponym1K = getLocEntity(toks[1]);
    entry.toponym1N = getLocEntity(toks[2]);
    if(toks.length<5)
      return entry;
    entry.toponymTruce1 = getLocEntity(toks[3]);
    if (toks.length > 5) {
      try{
      entry.latitude = Double.parseDouble(toks[4]);
      entry.longitude = Double.parseDouble(toks[5]);
      }
      catch(NumberFormatException e){
        return entry;
      }
    }
    return entry;

  }

  private LocEntity[] getLocEntity(String string) {

    string = string.replace("\"", "").replace("tp{", "").replace("}tp", "\t");
    ArrayList<LocEntity> ar = new ArrayList<LocEntity>();
    if (string.length() == 0)
      return null;
    String[] locs = string.split("\t");
    for (String loc : locs) {
      if (locs.length == 0)
        continue;
      String[] toks = loc.replace("]", "").split("\\[");
      if (toks.length < 2)
        continue;
      String[] lonlat = toks[1].split(",");
      double lat = Double.parseDouble(lonlat[0]);
      double lon = Double.parseDouble(lonlat[1]);
      String word = toks[0];
      LocEntity le = new LocEntity(word, lat, lon);
      ar.add(le);
    }
    return ar.toArray(new LocEntity[] {});
  }

  public static void main(String argv[]) throws IOException {
    ReadTweets rt = new ReadTweets(
            "/Users/indri/Documents/Research_data/Disambiguation/1500 metadata twitter place.txt");
    TweetEntry te = null;

    ContextDisamb c = new ContextDisamb();
    IndexSupportedTrie topotrie = new IndexSupportedTrie("GeoNames/cities1000.txt", "GazIndex/",
            true, false);
    
    EnglishParser enparser = new EnglishParser("res/", topotrie, false);

    int i=0;
    BufferedWriter wr = GetWriter.getFileWriter("result.csv");
    while ((te = rt.readLine()) != null) {
      System.out.println(i++);
      if(i>302) break;
      if (te.text == null)
        continue;
      String text = te.text;
      System.out.println(text);
      List<String> parses = enparser.parse(new Tweet(text));
      wr.write(text+"\t");
      ArrayList<LocEntity> locs = new ArrayList<LocEntity>();

      if (te.toponymTruce1 != null)
        for (LocEntity le : te.toponymTruce1) {
          locs.add(le);
        }
      System.out.println(locs);
      wr.write(locs+"\t");
      wr.write(parses+"\t");
      for (LocEntity loc : locs) {
        HashSet<String> topo = new HashSet<String>();
        topo.add(loc.address);
        HashMap<String, String[]> besttopo = c.returnBestTopo(topotrie, topo);
        for (String topo1 : besttopo.keySet())
        {  System.out.println(topo1 + ": " + besttopo.get(topo1)[2] + " " + besttopo.get(topo1)[0]
                  + " " + besttopo.get(topo1)[1]);
          wr.write("tp{"+topo1+ "["+besttopo.get(topo1)[0]+","+besttopo.get(topo1)[1]+"]}tp");
        }
      }
      wr.write("\n");
    }
    
    wr.close();
  }

}
