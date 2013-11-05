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
