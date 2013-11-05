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
import java.util.ArrayList;
import java.util.HashSet;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import edu.cmu.geoparser.io.GetReader;

/**
 * The trie tree is used along with the gaz index. The connection is built by the ids.
 * we search the trie tree for id of the string, and then use the id to search the index
 * to get the information in it.
 * 
 * @author indri
 *
 */
public class IndexSupportedTrie extends Trie {

	public IndexSearcher index;

	public IndexSupportedTrie(String filename, boolean savespace, boolean addoriginalstring,
			HashSet<String> countrycode) {
		super(filename, savespace, addoriginalstring, countrycode);
		// get the index searcher
		try {
			index = GetReader.getIndexSearcher("GazIndex/");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("IndexSupportedTrie is successfuly built.");
	}
/**
 * Trie that is built to work with the gazindex.
 * @param filename
 * @param savespace
 * @param addoriginalstring
 */
	public IndexSupportedTrie(String filename, String gazPath,boolean savespace, boolean addoriginalstring) {
		super(filename, savespace, addoriginalstring);
		// get the index searcher
		try {
			index = GetReader.getIndexSearcher(gazPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("IndexSupportedTrie is successfuly built.");
	}

	void close() {
		try {
			index.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// fuzzy controls the accented and deaccented search.
	/*
	 * fuzzy = true: sanjose = sanjosé fuzzy = false: sanjose <> sanjosé
	 */
	String rs;
	StringBuilder sb;
	String loweredstring;
	String[] stringid;
	String str;

	/**
	 * Search the Trie, return the ids as String.
	 * It is used for the IndexSupportedTrie to retrieve documents by ids.
	 * @param s
	 * @param fuzzy
	 * @return
	 */
	public String searchTrie(String s, boolean fuzzy) {
		rs = super.search(s);
		// System.out.println(rs);
		// System.out.println(addoriginalstring);
		if (this.addoriginalstring){
			if (fuzzy) {
				if (rs.startsWith("WL"))
					return rs;
				else
					return null;
			} else {
				if (rs.startsWith("WLAD")) { // select to output
					sb = new StringBuilder("WLAD");
					loweredstring = s.toLowerCase();
					if (!savespace)
						loweredstring = loweredstring.replace(" ", "");

					stringid = rs.split("_");
					for (int i = 1; i < stringid.length; i++) {
						str = stringid[i].split("::")[0];
						if (loweredstring.equals(str))
							sb.append("_").append(stringid[i]);
					}
					return sb.toString();

				} else if (rs.startsWith("WLA")) {
					return rs;
				} else if (rs.startsWith("WLD")) {
					return null;
				} else if (rs.startsWith("WL")) {
					return rs;
				} else
					return null;
			}
		}
		else {
			if (rs.startsWith("WL"))
				return rs;
			else
				return null;
		}
	}

	// if add to string is true, then, generate the ids in trie tree search.
	// if add to string is false, then generate the ids in the index search
	// phase.

	String ids;
	// been defined for other function searchtrie.
	// String[] stringid;
	long longid;
	String[] stringidmap;

	Query q;
	ScoreDoc sd;
	Document gazinfo;
	ArrayList<Document> d = new ArrayList<Document>();

	public ArrayList<Document> search(String s, boolean fuzzy) throws IOException {

		ids = searchTrie(s, fuzzy);
//		 System.out.println(ids);
		if (ids == null) {
			System.err.println("This word is not in the memory trie.");
			return null;
		}
		// clear the result list.
		d.clear();
		if (addoriginalstring) {
			stringidmap = ids.split("_");
			for (int i = 1; i < stringidmap.length; i++) {
				stringid = stringidmap[i].split("::");
				longid = Long.parseLong(stringid[1].trim());
				q = NumericRangeQuery.newLongRange("ID", longid, longid, true, true);

				sd = index.search(q, 1).scoreDocs[0];
				d.add(index.doc(sd.doc));
			}
			return d;
		} else {
			// if the trie tree didn't store the information, look up
			// index.
			// So, we should perform strict accent filtering by comparing
			// index
			// original string with the query.

			//sring id map only has id, no map.
			stringidmap = ids.split("_");
			//first is the flag of match. WLAD, for example.
			for (int i = 1; i < stringidmap.length; i++) {
				longid = Long.parseLong(stringidmap[i].trim());
				q = NumericRangeQuery.newLongRange("ID", longid, longid, true, true);
				// System.out.println("q is:"+q);

				// get original string and normalize
				ScoreDoc[] sds = index.search(q, 1).scoreDocs;

				// It's weird that allCountries.txt does not contain all
				// the locations that cities1000 has.
				// So this is to say, that if it's not in the
				// index(built by allCountries.txt), then it's not a
				// city name.
				if (sds.length == 0) {
					System.err.println("This word is not in the WorldGaz, but in the trie, which is wierd.");
					System.err.println("Used to halt the system. Now just ignore the unmatch. continue.");
					continue;
					//return null;
				}
				sd = sds[0];
				gazinfo = index.doc(sd.doc);

				d.add(gazinfo);
				// System.out.println(d.toString());
			}
			return d;
		}

	}

	public static void main(String argv[]) throws IOException {

		// HashSet<String> countrycodes = new
		// HashSet<String>(Arrays.asList(new
		// String[]{"US"}));//,"UK","CA","CL","AR","BR","UY","VZ","CN","JP"}));
		IndexSupportedTrie ist = new IndexSupportedTrie("GeoNames/cities1000.txt", "GazIndex/",Trie.NoSaveSpace, Trie.AddOrigStr);
		// "geoNames.com/allCountries.txt", false, false);
		// WL_3701195_1688161_1688162_1688164_1688169_1688170_1688171_1688172_1688175_4727022_5490263
		// WL_santa fé::3701195_santa fe::1688161_santa fe::1688162_santa
		// fe::1688164_santa fe::1688169_santa fe::1688170_santa
		// fe::1688171_santa fe::1688172_santa fe::1688175_santa
		// fe::4727022_santa fe::5490263
		// WL_3836278_3836279_8353260_3904870_3904871_3904872_3904873_3904874_3904875_3904876_3904877_3904878_3389633_3389634_3389635_3389636_3389637_3389638_3389639_3389640_3389641_3389642_3389643_3389644_3389646_3410387_3450236_3450237_3450238_3450239_3450240_3450241_3450242_3450243_3450244_3450245_3450246_3662493_3924810_6318238_6318239_6319121_7692676_3871572_3871573_3871574_3871575_3871576_3871577_3668756_3668757_3668758_3668759_3668760_3668761_3668762_3668763_3668764_3668765_3668766_3668767_3668768_3668769_3668770_3731365_3731399_3731504_3731864_3732643_3760015_3769163_3769419_3770338_3775732_3779676_3780215_3783236_3793553_6194809_6195380_6195544_6195867_6195963_6196842_6197482_6197913_6198076_6978954_7504627_7523144_7552560_7857863_8015999_8139336_8182165_8336338_3537736_3537737_3537738_3537739_3537740_3537741_3537742_3537743_3537744_3537745_3537746_3537747_3537748_3537749_3537750_3537751_3537752_3537753_3651430_3651431_2511160_3109867_6357777_3589351_3759498_3376132_3601593_3601594_3601595_3601596_3601597_3601598_3601599_3601600_3601601_3601602_1555414_3482926_3483712_3483874_3517464_3517465_3517466_3517467_3517468_3517469_3517470_3517471_3517472_3517473_3517474_3517475_3517476_3791901_3792119_3792120_3814097_3814946_3815556_3816955_3823639_3823964_3824107_3824289_3970673_3970877_3971366_3972410_3972523_3972845_3974581_3974582_3977504_3977856_3977908_3984350_3984351_3984352_3984353_3984354_3984355_3984356_3984357_3984358_3984359_3984360_3984361_3984362_3984363_3984364_3984365_3984367_3984368_3984369_3984370_3984371_4020647_4021292_4021307_4022233_4023025_4023362_4025692_4027575_4027678_4027960_6690611_1096441_3616492_3616493_3616494_3616495_3616496_3616497_3616498_8017050_3701193_3701194_3701195_3701196_3701197_3701198_3701199_8299084_3692190_3692191_3736674_3929209_3963168_6389170_6412603_6667349_1688161_1688162_1688163_1688164_1688165_1688166_1688167_1688168_1688169_1688170_1688171_1688172_1688173_1688174_1688175_1688176_1688177_1688178_1688179_1688180_7443304_3583268_4172075_4264460_4307632_4407603_4550874_4655930_4727022_4926137_5170753_5490263_3627372_3627373_3627374_3627375_3627376_3627377_3627378_3627379_3627380_3740707_3742446_3750993_3758818_3765648_3768498_3768499_3774181_3784566_3784567_3790759_3790760_3809453

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in, "utf-8"));
		System.out.print("> ");
		// Read user input
		String inputStr = br.readLine();
		while (!inputStr.equals("")) {

			long start = System.currentTimeMillis();
			// for (int i = 0; i < 100; i++)
			{
				// inputStr = "santa fé";
				// This is how to use the search function. false means
				// accent
				// fuzzy
				// = false, which is exact match of accent.
				ArrayList<Document> d = ist.search(inputStr, true);
//				String rs = ist.searchTrie(inputStr, true);
				if (d == null) {
					System.out.print("null!\n> ");
					inputStr = br.readLine();
					continue;
				}
//				System.out.println(rs);
				for (Document doc : d)
				System.out.println(doc.toString());
				/*
				 * System.out.println(d.size()); System.out.println(rs);
				 * for (Document doc : d) { System.out.println("[Name]"
				 * + doc.get("ORIGIN")); System.out.println("[ID]" +
				 * doc.get("ID")); System.out.println("[Country]" +
				 * doc.get("COUNTRYSTATE"));
				 * System.out.println("[GeoCode]" + doc.get("LATITUDE")
				 * + doc.get("LONGTITUDE"));
				 * System.out.println("[POPULATION]" +
				 * doc.get("POPULATION") + "\n"); }
				 */
			}
			long end = System.currentTimeMillis();
			System.out.println(end - start);
			System.out.print("\n> ");
			inputStr = br.readLine();
		}
		br.close();

	}
}
