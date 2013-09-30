package edu.cmu.geoparser.resource.dictionary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import edu.cmu.geoparser.common.CollectionSorting;
import edu.cmu.geoparser.common.StringUtil;
import edu.cmu.geoparser.io.GetReader;
import edu.cmu.geoparser.io.GetWriter;
import edu.cmu.geoparser.nlp.tokenizer.EuroLangTwokenizer;
import edu.cmu.geoparser.parser.utils.ParserUtils;
import edu.cmu.geoparser.resource.dictionary.Dictionary.DicType;
import edu.cmu.geoparser.resource.trie.IndexSupportedTrie;

public class SpanishDictionaryGenerator {

	static HashMap<String, Integer> types;

	public static void main(String arg[]) throws IOException {
		types = new HashMap<String, Integer>();
		String suffix = ".warc_o_content.txt", filename = "";
		IndexSupportedTrie ist = new IndexSupportedTrie("geoNames.com/SRC_cities1000.txt", "GazIndex/",false, false);
		HashSet<String> endict = (HashSet<String>) Dictionary.getSetFromListFile(
				"resources.english/words.filtered_SRC1000PlusCountry.txt", true, true).getDic(DicType.SET);
		int i = 0;
		while (i < 2) {
			filename = "resources.spanish/es_dictionary/" + (i++) + suffix;
			BufferedReader r = GetReader.getUTF8FileReader(filename);
			System.out.println(filename);
			String line;
			while ((line = r.readLine()) != null) {
				List<String> tokens = EuroLangTwokenizer.tokenize(line);
				for (String token : tokens) {
					token = StringUtil.getDeAccentLoweredString(token);
					// System.out.println(token);
					String t = ist.searchTrie(token, true);
					if (t != null && t.startsWith("WL"))
						continue;
					if (ParserUtils.isCountry(token))
						continue;
					if (endict.contains(token))
						continue;
					if (types.containsKey(token))
						types.put(token, types.get(token) + 1);
					else
						types.put(token, 1);
				}
			}
		}
		ArrayList<Entry<String, Integer>> as = new ArrayList(types.entrySet());
		CollectionSorting.rankIntArray(as);
		BufferedWriter w = GetWriter.getFileWriter("resources.spanish/esdict.txt");
		for (Entry e : as) {
			w.write((String) e.getKey()+"\n");
		}
		w.close();
	}
}
