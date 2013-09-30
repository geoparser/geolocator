package edu.cmu.geoparser.common;

/***************************************
 Copyright 2012 Judith Gelernter, Language Technologies Institute, School of Computer Science, Carnegie Mellon University.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 *****************************************/

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


import edu.cmu.geoparser.io.GetReader;
import edu.cmu.geoparser.model.Coordinate;

/**
 * 
 * @author shuguang some basic functions and their immpelementation. For
 *         instance, loading inverted list files, and gazatter entry files.
 */
public class Util {


	// Get each line from File
	public static HashSet<String> getFileText(String file) {
		HashSet<String> lines = new HashSet<String>();
		try {
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader Inputreader = new InputStreamReader(fis, "UTF-8");
			BufferedReader br = new BufferedReader(Inputreader);
			String line = new String();
			while ((line = br.readLine()) != null) {
				line = line.trim().toLowerCase();
				lines.add(line);
			}
			br.close();
			Inputreader.close();
			fis.close();
		} catch (IOException ioe) {
			System.out.println("File not Found");

		}

		return lines;
	}

	// Get gazetteer entry from file
	public static ArrayList<String> getGazEntry(String file) {

		ArrayList<String> lines = new ArrayList<String>();
		try {
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader Inputreader = new InputStreamReader(fis, "utf-8");
			BufferedReader br = new BufferedReader(Inputreader);
			String line = new String();
			while ((line = br.readLine()) != null) {
				line = line.split("\t")[0].toLowerCase().trim();
				lines.add(line);
			}
			br.close();
			Inputreader.close();
			fis.close();
		} catch (IOException ioe) {
			System.out.println("File not Found: " + file);
		}

		return lines;
	}

	// Get Gazatter entry and its geo-pair from files
	public static HashMap<String, Coordinate> getGazGeoPair(String file) {
		HashMap<String, Coordinate> lines = new HashMap<String, Coordinate>();
		try {
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader Inputreader = new InputStreamReader(fis, "utf-8");
			BufferedReader br = new BufferedReader(Inputreader);
			String line = new String();
			while ((line = br.readLine()) != null) {
				String lineArray[] = line.split("\t");
				String gazatterName = lineArray[0];
				double latitude = Double.parseDouble(lineArray[1]);
				double longtitude = Double.parseDouble(lineArray[2]);
				Coordinate gp = new Coordinate(latitude, longtitude);
				lines.put(gazatterName, gp);
			}
			br.close();
			Inputreader.close();
			fis.close();
		} catch (IOException ioe) {
			System.out.println("File not Found: " + file);
		}

		return lines;
	}

	// Get county of each gazatter
	public static HashMap<String, String> getGazCounty(String file) {
		HashMap<String, String> lines = new HashMap<String, String>();
		try {
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader Inputreader = new InputStreamReader(fis, "utf-8");
			BufferedReader br = new BufferedReader(Inputreader);
			String line = new String();
			while ((line = br.readLine()) != null) {
				String lineArray[] = line.split("\t");
				String gazatterName = lineArray[0];
				String county = "";
				if (lineArray.length >= 4)
					county = lineArray[3];

				lines.put(gazatterName, county);
			}
			br.close();
			Inputreader.close();
			fis.close();
		} catch (IOException ioe) {
			System.out.println("File not Found: " + file);
		}

		return lines;
	}

	// Load filter list
	public static HashMap<String, String> getNonGazatter() {

		String[] filterFiles = { "resources.english/filter.txt", "resources.english/stopwords.txt", "resources.english/words.txt" };
		HashMap<String, String> nonGaz = new HashMap<String, String>();
		FileInputStream fis = null;
		InputStreamReader inputReader = null;
		BufferedReader br = null;

		for (String filterFileName : filterFiles) {
			try {
				fis = new FileInputStream(filterFileName);
			} catch (FileNotFoundException e) {
				System.out.println("File " + filterFileName + " not found");
				throw new RuntimeException("Error: File " + filterFileName + " not found");
			}

			inputReader = new InputStreamReader(fis, Charset.forName("UTF-8"));

			br = new BufferedReader(inputReader);
			String line = new String();

			int count = 0;

			try {
				while ((line = br.readLine()) != null) {
					count++;
					line = line.trim().toLowerCase();
					nonGaz.put(line, "");
				}
			} catch (IOException e) {
				System.out.println("Error in reading line " + count);
			}

			// clean up
			try {
				br.close();
				inputReader.close();
				fis.close();
			} catch (IOException e) {
				System.out.println("Error in closing resource.");
			}

		}

		return nonGaz;
	}

	// Load abbreviation list
	public static HashMap<String, String> getAbbreviation() {
		HashMap<String, String> abb = new HashMap<String, String>();
		FileInputStream fis = null;
		String fileName = "resources/US_Abbreiviation.txt";
		try {
			fis = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			System.out.println("File not found " + fileName);
		}

		InputStreamReader inputReader = null;
		inputReader = new InputStreamReader(fis, Charset.forName("UTF-8"));

		BufferedReader br = new BufferedReader(inputReader);
		String line = new String();

		int lineCount = 0;
		try {
			while ((line = br.readLine()) != null) {
				lineCount++;
				line = line.trim().toLowerCase();
				abb.put(line.split("\t")[0], line.split("\t")[1]);
			}
		} catch (IOException e1) {
			System.out.println("Error in reading line " + lineCount);
		}

		// clean up
		try {
			br.close();
			inputReader.close();
			fis.close();
		} catch (IOException e) {
			System.out.println("Error in closing resource.");
		}
		return abb;
	}

	// Get inverted list
	public static HashMap<String, String> getInvertedIndex(String file) {
		// result;
		HashMap<String, String> invertedIndex = new HashMap<String, String>();

		FileInputStream fis = null;
		String fileName = file;

		try {
			fis = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			System.out.println("File not found " + fileName);
		}

		InputStreamReader inputReader = null;
		inputReader = new InputStreamReader(fis, Charset.forName("UTF-8"));

		BufferedReader br = new BufferedReader(inputReader);

		String line = new String();

		try {
			while ((line = br.readLine()) != null) {

				line = line.trim();
				String lineArray[] = line.split("\t");
				if (lineArray.length >= 2) {
					String word = lineArray[0];
					invertedIndex.put(word, lineArray[1]);
				}
			}
		} catch (IOException e) {
			System.out.println("Error in reading line ");
		}

		// clean up
		try {
			br.close();
			inputReader.close();
			fis.close();
		} catch (IOException e) {
			System.out.println("Error in closing resource.");
		}
		return invertedIndex;
	}

	public static HashSet<String> getDictionary(String dictFile) {
		HashSet<String> lines = new HashSet<String>();

		BufferedReader br = null;
		try {
			br = GetReader.getUTF8FileReader(dictFile);
		} catch (FileNotFoundException e1) {
			System.out.println("File not found.");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String line;
		try {
			while ((line = br.readLine()) != null) {
			if (line.length() > 1 && line.length() <7 )
				{
					line = line.toLowerCase().trim();
					lines.add(line);
				} 
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
		return lines;
	}

	// write to files
	public static void write(String fileName, String content) {

		try {
			FileOutputStream fos = new FileOutputStream(fileName, true);
			OutputStreamWriter osr = new OutputStreamWriter(fos, "utf-8");
			osr.write(content);
			osr.close();
			fos.close();
		} catch (IOException ioe) {

		}
	}

}
