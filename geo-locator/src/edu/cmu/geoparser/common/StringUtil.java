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
package edu.cmu.geoparser.common;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StringUtil {

	static Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

	/**
	 * check each word in the given string array is capitalized or not Input:
	 * array of strings Output: U U L L U L U L L L U L....
	 */
	public static String[] capitalizedArray(String[] sstr) {
		String[] cap = new String[sstr.length];
		int i = 0;
		for (String s : sstr) {
			if (Character.isUpperCase(s.charAt(0)))
				cap[i] = "U";
			else
				cap[i] = "L";
			i++;
		}
		return cap;
	}

	/**
	 * Input: array of strings, and a target array Output: the first appearance
	 * position
	 */
	public static int getFirst(String[] target, String[] pattern) {

		for (int i = 0; i < target.length; i++)
			target[i] = target[i].trim().toLowerCase();
		for (int i = 0; i < pattern.length; i++)
			pattern[i] = pattern[i].trim().toLowerCase();

		int tlen = target.length;
		int plen = pattern.length;

		int itarget = 0, ipattern = 0;

		while (itarget != tlen) {

			if ((itarget + plen) >= tlen)
				return -1;
			while (ipattern < plen) {
				if (pattern[ipattern].equals(target[itarget + ipattern])) {
					if (ipattern == (plen - 1))
						return itarget;
					else {
						ipattern++;
					}
				} else
					break;
			}
			itarget++;
			ipattern = 0;
		}
		return -1;
	}

	/**
	 * Input: array of strings, and a target array Output: the array of all the
	 * appearance
	 */

	public static ArrayList<Integer> getAllMatches(String[] target, String[] pattern) {

		ArrayList<Integer> result = new ArrayList<Integer>();
		int start = 0;
		while (start < target.length) {
			String[] subArray = new String[target.length - start];
			for (int iter = 0; iter < subArray.length; iter++) {
				subArray[iter] = target[iter + start];
				// System.out.print(subArray[iter]+" ");
			}
			// System.out.println();
			int index = getFirst(subArray, pattern);
			if (index != -1) {
				result.add(start + index);
				start = start + index + pattern.length;
			} else
				return result;
		}
		return result;
	}

	/**
	 * Strip the accent of the Spanish characters.
	 */

	public static String deAccent(String str) {
		String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
		return pattern.matcher(nfdNormalizedString).replaceAll("");
	}

	public static String getCapitalizedString(String str) {
		if (str.length() == 0)
			return "";
		char[] chars = str.toCharArray();
		chars[0] = Character.toUpperCase(chars[0]);
		return new String(chars);
	}

	public static String getCapitalizedPhrase(String[] phrase) {

		StringBuilder allUpperedloc = new StringBuilder();
		for (String word : phrase)
			allUpperedloc.append(getCapitalizedString(word) + " ");
		return allUpperedloc.toString();

	}

	public static char[] getDeAccentLoweredChars(String word) {
		char[] chars = deAccent(word.toLowerCase()).toCharArray();
		return chars;
	}

	public static String getDeAccentLoweredString(String word) {
		String chars = deAccent(word.toLowerCase());
		return chars;
	}

	public static String[] getDeAccentLoweredString(String[] words) {
		int len = words.length;
		for (int i = 0; i < len; i++)
			words[i] = deAccent(words[i].toLowerCase());
		return words;
	}

	public static String[] getBigram(char[] chars) {
		int length = chars.length;
		if (length == 0)
			return null;
		if (length == 1) {
			String str = chars[0] + "";
			String[] bigram = { str };
			return bigram;
		}

		String[] bigram = new String[length - 1];
		for (int i = 0; i < length - 1; i++) {
			bigram[i] = chars[i] + "" + chars[i + 1];
		}
		return bigram;
	}

	public static String[] getTrigram(char[] locchars) {

		// TODO Auto-generated method stub
		if (locchars.length == 0)
			return null;
		if (locchars.length < 3) {
			String[] s = new String[1];
			s[0] = new String(locchars);
			// System.out.println(s.toString());
			return s;
		} else {
			String[] s = new String[locchars.length - 2];
			for (int i = 2; i < locchars.length; i++) {
				String str = locchars[i - 2] + "" + locchars[i - 1] + "" + locchars[i];
				s[i - 2] = str;
			}
			// System.out.println(s.toString());
			return s;
		}
	}

	public static String[] getPosition(char[] getDeAccentedchars) {
		int length = getDeAccentedchars.length;
		String[] poschars = new String[length];
		for (int i = 0; i < length; i++) {
			poschars[i] = getDeAccentedchars[i] + "_" + i;
		}
		return poschars;
	}

	/**
	 *  convert char array to string, which is factorization for indexing
	 * @param chars
	 * @return
	 */
	public static String factorize(char[] chars) {
		if (chars == null)
			return null;
		StringBuilder sb = new StringBuilder();
		for (char c : chars)
			sb.append(c + " ");
		return sb.toString();
	}

	/**
	 *  convert char array to string, which is factorization for indexingfeatures.
	 * @param chars
	 * @return
	 */
	public static String factorize(String[] chars) {
		if (chars == null)
			return null;
		StringBuilder sb = new StringBuilder();
		for (String c : chars)
			sb.append(c + " ");
		return sb.toString();
	}

	/**
	 *  if string is empty, then return null;
	 *  if string is not, but no repetition, then return candidate.
	 * @param s
	 * @return
	 */
	public static ArrayList<String> repeatNormalization(String s) {

		if (s == null)
			return null;
		if (s.length() == 0)
			return null;

		Pattern p = Pattern.compile("([a-z])\\1{1,}");
		Matcher m = p.matcher(s);

		ArrayList<String> candidates = new ArrayList<String>();

		int i = 0, j = s.length();
		// store all the start and end positions.
		while (m.find() == true) {
			int start = m.start();
			int end = m.end();

			if (candidates.size() == 0) {
				candidates.add(s.replace(s.subSequence(start, end), "" + s.charAt(start)));
				candidates.add(s.replace(s.subSequence(start, end), s.charAt(start) + "" + s.charAt(start)));

			} else {
				Object[] ncand = candidates.toArray();
				candidates = new ArrayList<String>();
				for (Object s3 : ncand) {
					String s2 = (String) s3;
					candidates.add(s2.replace(s.subSequence(start, end), "" + s.charAt(start)));
					candidates.add(s2.replace(s.subSequence(start, end), s.charAt(start) + "" + s.charAt(start)));
				}
			}
		}

		if (candidates.size() == 0)
			candidates.add(s);
		return candidates;
	}

	String replace() {
		return null;
	}

	// edit distance
	public static int editDistance(String s, String t) {
		int m = s.length();
		int n = t.length();
		int[][] d = new int[m + 1][n + 1];
		for (int i = 0; i <= m; i++) {
			d[i][0] = i;
		}
		for (int j = 0; j <= n; j++) {
			d[0][j] = j;
		}
		for (int j = 1; j <= n; j++) {
			for (int i = 1; i <= m; i++) {
				if (s.charAt(i - 1) == t.charAt(j - 1)) {
					d[i][j] = d[i - 1][j - 1];
				} else {
					d[i][j] = min((d[i - 1][j] + 1), (d[i][j - 1] + 1), (d[i - 1][j - 1] + 1));
				}
			}
		}
		return (d[m][n]);
	}

	// edit distance
	public static int min(int a, int b, int c) {
		return (Math.min(Math.min(a, b), c));
	}

	public static boolean isEmpty(String[] features) {
		// TODO Auto-generated method stub
		if (features.length == 1)
			if (features[0].length() == 0)
				return true;
		return false;
	}

	public static String[] constructgrams(String[] tokens, int ngram, boolean keepspace) {
		// TODO Auto-generated method stub
		if (tokens.length - ngram + 1 < 1)
			return null;
		String[] n = new String[tokens.length - (ngram - 1)];
		for (int i = 0; i < tokens.length - (ngram - 1); i++) {
			n[i] = "";
			for (int j = i; j < i + ngram; j++) {
				if (keepspace)
					n[i] += " ";
				n[i] += tokens[j];
			}
			n[i] = n[i].trim();
		}
		return n;
	}

	public static String[] constructNoPuncgrams(String[] tokens, int ngram,
			boolean keepspace) {
		// TODO Auto-generated method stub
		if (tokens.length - ngram + 1 < 1)
			return null;
		String[] n = new String[tokens.length - (ngram - 1)];
		for (int i = 0; i < tokens.length - (ngram - 1); i++) {
			n[i]="";
			for (int j = i; j < i + ngram; j++) {
				if (keepspace)
					n[i] += " ";
				n[i] += tokens[j];
			}
			n[i]=n[i].trim();
		}
		return n;
	}

	public static boolean isPunctuation(char c) {
		return c == ',' || c == '.' || c == '!' || c == '?' || c == ':' || c == ';' || c == '$' || c == '%' || c == '^' || c == '&' || c == '*'
				|| c == '(' || c == ')' || c == '-' || c == '_' || c == '+' || c == '=' || c == '@' || c == '#' || c == '~' || c == '`' || c == '{'
				|| c == '}' || c == '[' || c == ']' || c == '\\' || c == '|' || c == '\"' || c == '\'' || c == '<' || c == '>' || c == '/'
				|| c == '¡' || c == '¿' || c == '«'|| c == '»';
	}

	public static String[] replaceAll(String[] toks, String string, String string2) {
		// TODO Auto-generated method stub
		for (int i = 0; i < toks.length; i++) {
			toks[i] = toks[i].replaceAll(string, string2).trim();
		}
		return toks;
	}

	public static boolean isCapitalized(String phrase) {
		// TODO Auto-generated method stub

		return Character.isUpperCase(phrase.charAt(0));
	}
	public static int commonLengthfromHead(String a,String b){
		if (a.length()==0 || b.length()==0 || a ==null || b == null) return 0;
		int l = a.length() < b.length() ? a.length():b.length();
		int i = 0;
		while( a.charAt(i)==b.charAt(i)){
			i++;
			if(i==l)
				return l;
		}
		return i;
	}

	// test function
	public static void main(String argv[]) {

		System.out.println();
		String[] ngram = { "He", "is", "not", "a", "jurk", "is", "he", "?" };
		System.out.println(Arrays.asList(StringUtil.constructgrams(ngram, 1, true)).toString());
		System.out.println(StringUtil.repeatNormalization("chile"));

		for (String s : argv) {
			System.out.println(s);
		}
		Class c = new HashMap<String, Integer>().getClass();
		System.out.println(c.getSimpleName().equals("HashMap"));
		/*
		 * / //testing editing distance
		 * System.out.println(StringUtil.editDistance("Pennsylvania",
		 * "Pennsyl vania"));
		 * 
		 * // for testing capitalizedArray() String[] a = { "this", "IS", "The",
		 * "Capitalized", "string" }; String[] f = capitalizedArray(a); for
		 * (String s : f) System.out.println(s);
		 * 
		 * // for testing getFirst() and getAllMathces() String[] target = {
		 * "do", "you", "think", "this", "is", "cool", "?", "this", "is",
		 * "cool", "I", "think", "so" }; String[] pattern = { "this", "is",
		 * "cool" }; System.out.println(getFirst(target, pattern));
		 * System.out.println(getAllMatches(target, pattern).toString());
		 * 
		 * //for testing trigram char[] c = {'a'}; String[] s =
		 * StringUtil.getTrigram(c); if (s == null) System.out.println("null");
		 * else for (String w : s) System.out.println(w);
		 * 
		 * //
		 */
	}
	/**
	 * The distance definition is : common(q,c) / q.length
	 * @param q_gram
	 * @param c_gram
	 * @return double value, based on the definition.
	 */
	public static double getGramSimilarity(String[] q_gram, String[] c_gram) {
		// TODO Auto-generated method stub
		if (q_gram.length==0 || c_gram.length==0)
			return 0;
		
		HashSet<String> cgram = new HashSet<String>(Arrays.asList(c_gram));
		
		int common = 0;
		for ( String q : q_gram)
			if (!cgram.contains(q)) common++;
		
		return (double) common / (double) q_gram.length;
	}

	public static double PhraseCommonHeadRatio(String query, String candidate) {
		if (query.length()==0|| candidate.length()==0) return 0;
		// TODO Auto-generated method stub
		String[] qtoks = query.trim().split(" ");
		double qscore[] = new double[qtoks.length]; int i = 0;
		String[] ctoks = candidate.trim().split(" ");
		int effective = 0;
		for (String q : qtoks){
			if (q.length()==0) continue;
			effective ++;
			double maxcommon = 0;
			for ( String c: ctoks){
				int temp = StringUtil.commonLengthfromHead(q, c);
				if (temp>maxcommon)
				{
					maxcommon = temp;
				}
			}
			qscore[i++] = maxcommon/(double)q.length();
		}
		double d = 0;
		for ( double score : qscore) d+=score;
		return d/(double)(effective);
	}
}
