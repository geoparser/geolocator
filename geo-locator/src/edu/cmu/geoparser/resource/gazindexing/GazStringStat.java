package edu.cmu.geoparser.resource.gazindexing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.Map.Entry;

import edu.cmu.geoparser.common.CollectionSorting;
import edu.cmu.geoparser.io.GetReader;

/**
 * This function is to generate the most ambiguous K gazetteer entries, according to
 * allCOuntries.txt other language field, and alternativenames field.
 * 
 * Method: simply count the number of same word accurance. 1. lowercase 2. count. 3. output K top
 * words.
 * 
 * @author indri
 * 
 */
public class GazStringStat {

  public static void main(String argv[]) throws IOException {

    HashMap<String, Long> locations = new HashMap<String, Long>(30000000);
    Scanner s = new Scanner(new File("GeoNames/allCountries.txt"));
    String asciiStr = "", temp;
    s.useDelimiter("\t");
    int count = 0;
    int total = 0;
    while (s.hasNext()) {
      total++;
      if (total % 18000000 == 0)
        System.out.println(total / 18);
      temp = s.next();
      if (count == 1) {
        // System.out.println(asciiStr);
        asciiStr = temp.toLowerCase();
      }
      if (count == 14) { 
        // This is for storing the balanced or population prefered.
        if (true)
          if (locations.containsKey(asciiStr))
            locations.put(asciiStr, locations.get(asciiStr) + Long.parseLong(temp));
          else
            locations.put(asciiStr, Long.parseLong(temp));
        //This is for storing the ambiguity prefered locations.
        if(false)
        if (locations.containsKey(asciiStr))
          locations.put(asciiStr, locations.get(asciiStr)+1);
        else
          locations.put(asciiStr, 1L);
      }
      count = count % 18;
      count++;
    }
    ArrayList<Entry<String, Long>> as = new ArrayList<Entry<String, Long>>(locations.entrySet());
    ArrayList<Entry<String, Long>> sortedLocations = CollectionSorting.rankIntArray(as);

    int i = 0;
    while (i++ < 10000)
      System.out.println(sortedLocations.get(i));
  }
}
