package edu.cmu.geoparser.resource.gazindexing;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import edu.cmu.geoparser.io.GetReader;

public class AbbrMapper {

	public static HashMap<String, String> load() throws IOException {
		BufferedReader reader = GetReader.getUTF8FileReader("resources.english/state_abbr.txt");
		HashMap<String, String> map = new HashMap<String, String>(60);
		String line;
		while ((line = reader.readLine()) != null) {
			String[] toks = line.split("[\t]");
			String key = toks[0].trim().toLowerCase();
			for (int i = 0; i < toks.length; i++) {
			//	if (i != 1)
					map.put(toks[i].trim().toLowerCase(), key);
			}
		}

		return map;
	}
	public static HashMap<String, String> loadReverse() throws IOException {
		BufferedReader reader = GetReader.getUTF8FileReader("resources.english/state_abbr.txt");
		HashMap<String, String> map = new HashMap<String, String>(60);
		String line;
		while ((line = reader.readLine()) != null) {
			String[] toks = line.split("[\t]");
			String key = toks[1].trim().toLowerCase();
			for (int i = 0; i < toks.length; i++) {
				if (i != 1)
					map.put(toks[i].trim().toLowerCase(), key);
			}
		}

		return map;
	}

}
