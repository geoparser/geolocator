/**
 * 
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
 * 
 * @author Wei Zhang,  Language Technology Institute, School of Computer Science, Carnegie-Mellon University.
 * email: wei.zhang@cs.cmu.edu
 * 
 */
package edu.cmu.geoparser.resource.trie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

import edu.cmu.geoparser.common.StringUtil;
import edu.cmu.geoparser.io.GetReader;
import edu.cmu.geoparser.resource.gazindexing.GeoNameGazEntry;
import edu.cmu.geoparser.resource.gazindexing.GeoNamesReader;

public class Trie {

	public Node ROOT;

	// if save space is true, then 
	public boolean savespace, addoriginalstring;

	public static final boolean SaveSpace = true;
	public static final boolean NoSaveSpace = false;
	public static final boolean AddOrigStr = true;
	public static final boolean NoAddOrigStr = false;
	StringBuilder rs;

	public Trie(String filename, boolean svespace, boolean addoriginalstring, HashSet<String> countrycode) {
		ROOT = new Node((char) 0);
		this.savespace = svespace;
		this.addoriginalstring = addoriginalstring;
		try {
			buildTrieFromGeoNamesForIndex(filename, countrycode);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Trie(String filename, boolean savespace, boolean addoriginalstring) {
		ROOT = new Node((char) 0);
		this.savespace = savespace;
		this.addoriginalstring = addoriginalstring;
		try {
			System.err.println("Loading the whole world gazetteer for quick lookup.");
			buildTrieFromGeoNamesForIndex(filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	String normword;
	char[] chars;
	Node father;
	Node child;
	int index;

	/**
	 *  if savespace  is true, then in the trie path for the entry, we count space as a node.
	 *  If not savespace, then no space in it.
	 *  Add original string adds the lowercased original string. if not add orignial string, 
	 *  the trie is smaller to store in the trie.
	 * @param Origword
	 * @param id
	 * @return
	 */
	
	public boolean addWord2Trie(String Origword, long id) {

		Origword = Origword.toLowerCase();
		// San José -> san jose // Indexing char sequence.
		normword = StringUtil.deAccent(Origword);

		// san jose -> sanjose ? or leave it alone.
		if (!savespace)
			normword = normword.replaceAll("[ ]", "");

		// use norm word to build the tree. The decision of stripping the
		// spaces
		// is made by the caller of this function.
		chars = normword.toCharArray();
		father = this.ROOT;
		index = 0;
		for (char c : chars) {
			index++;
			child = null;

			if (!father.containsChild(c)) {
				child = new Node(c);
				father.addChildren(child);
			} else
				child = father.getChild(c);

			if (index == chars.length) {
				// add original word and it's id to the leaf node.
				if (addoriginalstring)
					child.addValue(id, Origword);
				else
					child.addValue(id);
				break;
			}
			father = child;
		}
		return true;
	}

	/**
	 * Tree build rule: (This didn't add Haibo's other language field and
	 * country, adjs)
	 * 
	 * Original String has accent, and all converted to lowecase. Leave space
	 * alone according to save space flag.
	 * 
	 * Ex: San José -> san josé or sanjosé.
	 * 
	 * Index String could choose to be space removed or not. But, All the chars
	 * are deaccented and lowered.
	 * 
	 * Ex: San José -> san jose or sanjose.
	 */
	public void buildTrieFromGeoNamesForIndex(String filename, HashSet<String> countrycode) throws IOException {
		GeoNamesReader namesreader = new GeoNamesReader(filename);
		double start = System.currentTimeMillis();

		int i = 0;
		GeoNameGazEntry e;
		while ((e = namesreader.readLine()) != null) {
			System.out.print((++i % 100000 == 0) ? i + "\n" : "");
			if (countrycode.contains(e.country))
				addWord2Trie(e.name, e.id);
		}
		double end = System.currentTimeMillis();

		System.out.println("Trie construction time is :" + (end - start));

	}

	/**
	 * This version added haibo's other language and Country, adjective strings.
	 * 
	 * @param filename
	 * @throws IOException
	 */
	public void buildTrieFromGeoNamesForIndex(String filename) throws IOException {
		BufferedReader namesreader = GetReader.getUTF8FileReader(filename);
		double start = System.currentTimeMillis();

		int i = 0;
		String e;
		String s[];
		while ((e = namesreader.readLine()) != null) {
			s = e.split("	");
			addWord2Trie(s[1], Long.parseLong(s[0]));


			System.err.print((++i % 1000000 == 0) ? i + " has been loaded. 8,000,000 in total.\n" : "");

			/**
			 * added by Haibo
			 */
			if (s[7].equals("PCLI") || s[7].equals("PCLS") || s[7].equals("PPLC") || s[7].equals("PPLA") || s[7].equals("PCLIX")
					|| s[7].equals("ADM1") || s[7].equals("ADM2")) {
				String[] alts = s[3].split(",");
				for (String alt : alts) {

					addWord2Trie(alt, Long.parseLong(s[0]));
					System.err.print((++i % 1000000 == 0) ? i + " has been loaded. 8,000,000 in total.\n" : "");
				}
			}
		}

		// add adj mapping
		BufferedReader fin = GetReader.getUTF8FileReader("GeoNames/countrymapping.txt");
		String line = "";
		while ((line = fin.readLine()) != null) {
			String[] str = line.split("	");
			if (str[18].length() == 0) {
				continue;
			}

			if (str[5].indexOf(",") == -1) {
				str[5] = str[5].toLowerCase();
				str[5] = StringUtil.deAccent(str[5]);
				if (!savespace)
					str[5] = str[5].replaceAll("[ ]", "");
				addWord2Trie(str[5], Long.parseLong(str[18])); // add adj
																// mapping
			} else
				for (String sub : str[5].split(",")) {
					sub = sub.toLowerCase();
					sub = StringUtil.deAccent(sub);
					if (!savespace)
						sub = sub.replaceAll("[ ]", "");
					addWord2Trie(sub, Long.parseLong(str[18])); // add adj
																// mapping
				}
			if (str[6].indexOf(",") == -1) {
				str[6] = str[6].toLowerCase();
				str[6] = StringUtil.deAccent(str[6]);
				if (!savespace)
					str[6] = str[6].replaceAll("[ ]", "");
				addWord2Trie(str[6], Long.parseLong(str[18])); // add citizen
																// mapping
			} else
				for (String sub : str[6].split(",")) {
					sub = sub.toLowerCase();
					sub = StringUtil.deAccent(sub);
					if (!savespace)
						sub = sub.replaceAll("[ ]", "");
					addWord2Trie(sub, Long.parseLong(str[18])); // add citizen
																// mapping
				}
		}

		// add US-states mapping
		fin = GetReader.getUTF8FileReader("GeoNames/USstates.txt");
		line = "";
		while ((line = fin.readLine()) != null) {
			String[] str = line.split("	");
			String state = str[0].split("\\.")[1];
			String id = str[3];
			addWord2Trie(state, Long.parseLong(id));
		}
		double end = System.currentTimeMillis();

		System.out.println("Trie construction time is :" + (end - start));

	}

	String Aphrase, Dphrase;
	// have been defined for addwordtotrie();
	// char[] chars; Node father; Node child;
	int i;
	long[] ids;
	String[] string;
	boolean A, D;

	/**
	 * ignore case. Accent sensitive. WLD: deaccented version match.
	 * WLA:accented version match.
	 * 
	 * Pittsburgh: WLAD pittsburgh: WLAD Pitts burgh: WLAD (depends on flag
	 * removespace)
	 * 
	 * Sanjosé: WLA (depends on flag removespace)
	 * 
	 * output format: without original string stored: WLAD_XXXX_XXXX_XXXX_XXXX....
	 * 			with original string stord: WLAD_AAA::XXX_AAA::XXX_....
	 */
	public String search(String s) {
		if (s == null) {
			return "EMPTY";
		}
		if (s.length() == 0)
			return "EMPTY";

		if (!savespace)
			s = s.replaceAll(" ", "");
		Aphrase = s.toLowerCase(); // accented lowercased phrase
		Dphrase = StringUtil.deAccent(Aphrase); // deaccented lowercased
		// phrase
		chars = Dphrase.toCharArray();
		i = 0;//current char in phrase
		father = ROOT;// current father for the current char. root is the father of the first char.

		//father is the father of the current char in the tree we want to match with the current char in the query.
		//while father node is not leaf
		while (father.isChildrenEmpty() == false) {
			if (father.containsChild(chars[i])) {//if qi in the children set, matched.
				if (i == chars.length - 1) // if the match is the last character, then the whole thing is matched.
					if (father.getChild(chars[i]).isLocation()) { // check if the whole match is a location or not.
						// "it's a whole match, and it's a location!";
						rs = new StringBuilder();
						rs.append("WL");
						if (addoriginalstring) {
							// IDs are mapped with the strings, so
							// that we could
							ids = father.getChild(chars[i]).getIDValue();
							string = father.getChild(chars[i]).getStringValue();
							// whole word and accent match!!!
							A = false;
							D = false;
							for (int k = 0; k < ids.length; k++) {
								if (k == 0) {
									if (string[k].equals(Aphrase) && A == false) {
										rs.append("A");
										A = true;
									}
									if (string[k].equals(Dphrase) && D == false) {
										rs.append("D");
										D = true;
									}
									rs.append("_").append(string[k]).append("::").append(ids[k]);
								} else
									rs.append("_").append(string[k]).append("::").append(ids[k]);

							}
							return rs.toString();
						} else {
							for (long j : father.getChild(chars[i]).getIDValue()) {
								rs.append("_").append(j);
							}
							return rs.toString();
						}
					} else {
						// " it's a whole match, but it's not a location!";
						return "WN";
					}

				father = father.getChild(chars[i++]);
				continue;
			} else {
				if (father.isLocation()) {
					// "partial match, but the matched part a location:" + i;
					return "PL" + i;
				} else
					// partial match, and the matched part is not a location.
					return "PN" + i;
			}
		}

		// the word is too long, which can not be matched with the branch
		// fully.
		return "PM:" + i;

	}

	public static void main(String args[]) throws IOException {

		// HashSet<String> countrycodes = new
		// HashSet<String>();countrycodes.add("US");
		Trie trie = new Trie("GeoNames/" + "cities1000.txt", Trie.SaveSpace, Trie.NoAddOrigStr);

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in, "utf-8"));
		System.out.print("> ");
		// Read user input
		String inputStr = br.readLine();
		int i = 0;
		long start = System.currentTimeMillis();
		while (i++ < 1000000) {

			System.out.print(trie.search(inputStr) + "\n");
			System.out.println();
			System.out.print("\n> ");

			inputStr = br.readLine();
		}
		long end = System.currentTimeMillis();
		System.out.print(end - start);

		br.close();
		trie = null;
	}

}
