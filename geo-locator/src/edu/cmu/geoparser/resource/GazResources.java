package edu.cmu.geoparser.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import edu.cmu.geoparser.common.Util;
import edu.cmu.geoparser.model.Coordinate;

public class GazResources {

	
	private  ArrayList<String> gazetteer = null;
	private  HashMap<String, String> invertedIndex = null;

	private  HashMap<String, String> nonGaz = new HashMap<String, String>();
	private  HashMap<String, Coordinate> getGazGeoPair = new HashMap<String, Coordinate>();
	private  HashMap<String, String> gazCounty = new HashMap<String, String>();
	private  HashMap<Integer, String> hasToWord = new HashMap<Integer, String>();
	private  HashMap<String, HashSet<Integer>> wordStarts = new HashMap<String, HashSet<Integer>>();
	protected  String gazName = null;
	
	
	public GazResources(String gazName) {
		this.gazName = gazName;
		init();
	}

	private void init() {
		Set<String> manuals = Util.getFileText("resources.english/gaz/manuals.txt");
		gazetteer = Util.getGazEntry("resources.english/gaz/Gaz_" + this.gazName +".txt");

		nonGaz = Util.getNonGazatter();
		
		// build inverted index by Gaz_XXXii.txt;
		invertedIndex = Util.getInvertedIndex("resources.english/gaz/Gaz_"
				+ this.gazName + "_ii.txt");

		// build gazetteer to Geo Pair
		getGazGeoPair = Util.getGazGeoPair("resources.english/gaz/Gaz_"+ this.gazName + ".txt");

		gazCounty = Util.getGazCounty("resources.english/gaz/Gaz_" + this.gazName+ ".txt");

		for (Map.Entry<String, String> m : invertedIndex.entrySet()) {
			String key = m.getKey();
			int code = key.hashCode();
			this.hasToWord.put(code, key);

			char keyArray[] = key.toCharArray();

			if (keyArray.length >= 5) {
				String key_two = keyArray[0] + "" + keyArray[1];
				if (wordStarts.containsKey(key_two))
					wordStarts.get(key_two).add(code);
				else {
					HashSet<Integer> newh = new HashSet<Integer>();
					newh.add(code);
					this.wordStarts.put(key_two, newh);
				}
			}
		}

		// Build Inverted Index for Gazeteer

		// Add the manual ones
		for (String m : manuals)
			gazetteer.add(m.toLowerCase());
		
	}

	/*****************************************
	 * Getter() Methods 
	 *****************************************/
	public  ArrayList<String> getGazetteer() {
		return gazetteer;
	}

	public  HashMap<String, String> getInvertedIndex() {
		return invertedIndex;
	}

	public  HashMap<String, String> getNonGaz() {
		return nonGaz;
	}

	public  HashMap<String, Coordinate> getGetGazGeoPair() {
		return getGazGeoPair;
	}

	public  HashMap<String, String> getGazCounty() {
		return gazCounty;
	}

	public  HashMap<Integer, String> getHasToWord() {
		return hasToWord;
	}

	public  HashMap<String, HashSet<Integer>> getWordStarts() {
		return wordStarts;
	}

	public  String getGazName() {
		return gazName;
	}
	

	
	
	
	
	

}
