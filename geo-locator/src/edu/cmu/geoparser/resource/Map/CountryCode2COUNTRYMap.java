package edu.cmu.geoparser.resource.Map;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.apache.lucene.document.Document;

import edu.cmu.geoparser.io.GetReader;
import edu.cmu.geoparser.model.Country;
import edu.cmu.geoparser.resource.ResourceFactory;

public class CountryCode2COUNTRYMap extends Map {

  public static final String name = "GeoNames/countrymapping.txt";

  static CountryCode2COUNTRYMap c2cMap;

  HashMap<String, Country> theMap;

  @SuppressWarnings("unchecked")
  public static CountryCode2COUNTRYMap getInstance() {
    if (c2cMap == null)
      return new CountryCode2COUNTRYMap().load();
    return c2cMap;
  }

  public CountryCode2COUNTRYMap load() {
    theMap = new HashMap<String, Country>(252);
    BufferedReader br = null;
    try {
      br = GetReader.getUTF8FileReader(name);
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    String line = null;
    try {
      while ((line = br.readLine()) != null) {
        String[] toks = line.split("\t");
        Country c = (Country) new Country().setAbbr(toks[0].toLowerCase())
                .setLang(toks[6].toLowerCase()).setRace(toks[7].toLowerCase())
                .setAsciiName(toks[4].toLowerCase()).setId(toks[18]);
        theMap.put(toks[0].toLowerCase(), c);
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    try {
      br.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return this;
  }

  @Override
  public Country getValue(String code) {
    return theMap.get(code.toLowerCase());
  }

  /**
   * check if it's a country abbreviation. Case insensitive. Example : US, CN, BR. or us, cn, br.
   * 
   * @param phrase
   * @return
   */
  public boolean isCountryAbbreviation(String phrase) {
    String countryId = null;
    phrase = phrase.toLowerCase();
    if (phrase.length() != 2)
      return false;
    if (ResourceFactory.getCountryCode2CountryMap().getValue(phrase.toLowerCase()) != null)
      countryId = ResourceFactory.getCountryCode2CountryMap().getValue(phrase.toLowerCase())
              .getId();
    return (countryId == null) ? false : true;
  }

  /**
   * Get document if the string "phrase" is the country abbreviation return null if it's not a
   * abbreviation.
   * 
   * Checking criteria: form as US, CN, BR. two chars, all cap.
   * 
   * @param phrase
   * @return
   */
  public Document getCountryDoc(String phrase) {
    if (isCountryAbbreviation(phrase)) {
      String id = ResourceFactory.getCountryCode2CountryMap().getValue(phrase).getId();
      Document d = ResourceFactory.getClbIndex().getDocumentsById(id);
      return d;
    } else
      return null;
  }

  public static void main(String argv[]) {
    CountryCode2COUNTRYMap inst = ResourceFactory.getCountryCode2CountryMap();
    System.out.println(inst.getValue("us").getAsciiName());
    System.out.println(inst.getValue("us").getId());
    System.out.println(inst.getCountryDoc("CN"));
  }

}
