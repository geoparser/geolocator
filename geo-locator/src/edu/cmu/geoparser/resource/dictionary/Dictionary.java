package edu.cmu.geoparser.resource.dictionary;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.cmu.geoparser.common.StringUtil;
import edu.cmu.geoparser.io.GetReader;
import edu.cmu.geoparser.resource.gazindexing.GeoNameGazEntry;

//This gazetteer is just for quick search.

public class Dictionary {

	public static enum DicType {
		SET, MAP, GAZ
	};

	public static enum MapValueType {
		POPULATION, COUNTRY, GEONAMEGAZENTRY
	};

	HashSet<String> set;
	HashMap<String, String> map;
	HashMap<String, List<GeoNameGazEntry>> gaz;

	public Object getDic(DicType t) {
		if (t == DicType.SET)
			return set;
		else if (t == DicType.MAP)
			return map;
		else if (t == DicType.GAZ)
			return gaz;
		else
			return null;

	}

	// type is to determine if the file is from geonames or from dictionary.
	Dictionary(DicType type, int size) {
		if (type == DicType.SET)
			set = new HashSet<String>(size);
		else if (type == DicType.MAP)
			map = new HashMap<String, String>(size);
		else if (type == DicType.GAZ)
			gaz = new HashMap<String, List<GeoNameGazEntry>>(size);
		else
			System.err
					.println("Dictionary Type is wrong. Sould be dictionary(HashSet) or map(HashMap)");
	}

	// read file from dictionary file
	// keepspace to add to the dictionary if true.
	public static Dictionary getSetFromListFile(String filename,
			 boolean normalized, boolean keepspace)
			throws IOException {
		Dictionary d = new Dictionary(DicType.SET, 20);
		BufferedReader reader = GetReader.getUTF8FileReader(filename);
		String e;
//		int count = 0;
		if (keepspace) {

			while ((e = reader.readLine()) != null) {
				if (e.startsWith("##"))
					continue;
				if (normalized)
					e = StringUtil.getDeAccentLoweredString(e.trim());
				if(e.endsWith("'s"))
					d.set.add(StringUtil.getDeAccentLoweredString(e.substring(0,e.length()-2)));
				d.set.add(StringUtil.getDeAccentLoweredString(e));
			}

		} else {
			while ((e = reader.readLine()) != null) {
				if (e.startsWith("##"))
					continue;
				e = e.replace(" ", "");
				if (normalized)
					e = StringUtil.getDeAccentLoweredString(e.trim());
				if(e.endsWith("'s"))
					d.set.add(StringUtil.getDeAccentLoweredString(e.substring(0,e.length()-2)));
				
				d.set.add(StringUtil.getDeAccentLoweredString(e));
			}
		}
		reader.close();
		return d;
	}

	public static Dictionary getSetFromListFile(String filename, int size,
			 boolean normalized, boolean keepspace)
			throws IOException {
		Dictionary d = new Dictionary(DicType.SET, 20);
		BufferedReader reader = GetReader.getUTF8FileReader(filename);
		String e;
		int count = 0;
		if (keepspace) {

			while ((e = reader.readLine()) != null) {
				if(count++<size)break;
				if (e.startsWith("##"))
					continue;
				if (normalized)
					e = StringUtil.getDeAccentLoweredString(e.trim());
				if(e.endsWith("'s"))
					d.set.add(StringUtil.getDeAccentLoweredString(e.substring(0,e.length()-2)));
				
				d.set.add(StringUtil.getDeAccentLoweredString(e));
			}

		} else {
			while ((e = reader.readLine()) != null) {
				if (e.startsWith("##"))
					continue;
				e = e.replace(" ", "");
				if (normalized)
					e = StringUtil.getDeAccentLoweredString(e.trim());
				if(e.endsWith("'s"))
					d.set.add(StringUtil.getDeAccentLoweredString(e.substring(0,e.length()-2)));
				
				d.set.add(StringUtil.getDeAccentLoweredString(e));
			}
		}
		reader.close();
		return d;
	}


	public static void main(String argv[]) throws Exception {

	}
}
