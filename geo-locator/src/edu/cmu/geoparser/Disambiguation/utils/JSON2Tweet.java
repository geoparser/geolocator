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
package edu.cmu.geoparser.Disambiguation.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;

import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.json.DataObjectFactory;

import edu.cmu.geoparser.model.JSONTweet;
import edu.cmu.geoparser.io.GetReader;

public class JSON2Tweet {

	BufferedReader br;
	static JSONTweet jsontweet;

	public JSON2Tweet(String JSONFile) throws FileNotFoundException,
			UnsupportedEncodingException {

		br = GetReader.getUTF8FileReader(JSONFile);

	}

	public JSONTweet readLine() {
		String line;
		try {
			line = br.readLine();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		if (line == null)
			return null;

		

		Status tweet = null;
		try {
			// parse json to object type
			tweet = DataObjectFactory.createStatus(line);
		} catch (TwitterException e) {
			System.err.println("error parsing tweet object");
			return null;
		}

		jsontweet.JSON = line;
		jsontweet.id = tweet.getId();
		jsontweet.source = tweet.getSource();
		jsontweet.text = tweet.getText();
		jsontweet.createdat = tweet.getCreatedAt();
		jsontweet.tweetgeolocation = tweet.getGeoLocation();

		User user;
		if ((user = tweet.getUser()) != null) {

			jsontweet.userdescription = user.getDescription();
			jsontweet.userid = user.getId();
			jsontweet.userlanguage = user.getLang();
			jsontweet.userlocation = user.getLocation();
			jsontweet.username = user.getName();
			jsontweet.usertimezone = user.getTimeZone();
			jsontweet.usergeoenabled = user.isGeoEnabled();

			if (user.getURL() != null) {
				String url = user.getURL().toString();

				jsontweet.userurl = url;

				String addr = url.substring(7).split("/")[0];
				String[] countrysuffix = addr.split("[.]");
				String suffix = countrysuffix[countrysuffix.length - 1];

				jsontweet.userurlsuffix = suffix;

				try {
					InetAddress address = null;//InetAddress.getByName(user.getURL().getHost());

					String generate_URL
					// =
					// "http://www.geobytes.com/IpLocator.htm?GetLocation&template=php3.txt&IpAddress="
					= "http://www.geoplugin.net/php.gp?ip="
							+ address.getHostAddress();
					URL data = new URL(generate_URL);
					URLConnection yc = data.openConnection();
					BufferedReader in = new BufferedReader(
							new InputStreamReader(yc.getInputStream()));
					String inputLine;
					String temp = "";
					while ((inputLine = in.readLine()) != null) {
						temp += inputLine + "\n";
					}
					temp = temp.split("s:2:\"")[1].split("\"")[0];

					jsontweet.userurllocation = temp;

				} catch (Exception uhe) {
					//uhe.printStackTrace();
					jsontweet.userurllocation = null;
				}
			}
		}
		
		return jsontweet;
		
	}

	public static Status getStatusTweet(String JSONString){
		if (JSONString == null)
			return null;
		
		Status tweet = null;
		try {
			// parse json to object type
			tweet = DataObjectFactory.createStatus(JSONString);
		} catch (TwitterException e) {
			System.err.println("error parsing tweet object");
			return null;
		}
		return tweet;
	}
	public static JSONTweet getJSONTweet(String JSONString){
		JSONTweet jsontweet=new JSONTweet();
		if (JSONString == null)
			return null;
		
		Status tweet = null;
		try {
			// parse json to object type
			tweet = DataObjectFactory.createStatus(JSONString);
		} catch (TwitterException e) {
			System.err.println("error parsing tweet object");
			return null;
		}

		jsontweet.JSON = JSONString;
		jsontweet.id = tweet.getId();
		jsontweet.source = tweet.getSource();
		jsontweet.text = tweet.getText();
		jsontweet.createdat = tweet.getCreatedAt();
		jsontweet.tweetgeolocation = tweet.getGeoLocation();

		User user;
		if ((user = tweet.getUser()) != null) {

			jsontweet.userdescription = user.getDescription();
			jsontweet.userid = user.getId();
			jsontweet.userlanguage = user.getLang();
			jsontweet.userlocation = user.getLocation();
			jsontweet.username = user.getName();
			jsontweet.usertimezone = user.getTimeZone();
			jsontweet.usergeoenabled = user.isGeoEnabled();

			if (user.getURL() != null) {
				String url = user.getURL().toString();

				jsontweet.userurl = url;

				String addr = url.substring(7).split("/")[0];
				String[] countrysuffix = addr.split("[.]");
				String suffix = countrysuffix[countrysuffix.length - 1];

				jsontweet.userurlsuffix = suffix;

				try {
					InetAddress address = null;//InetAddress.getByName(user.getURL().getHost());

					String generate_URL
					// =
					// "http://www.geobytes.com/IpLocator.htm?GetLocation&template=php3.txt&IpAddress="
					= "http://www.geoplugin.net/php.gp?ip="
							+ address.getHostAddress();
					URL data = new URL(generate_URL);
					URLConnection yc = data.openConnection();
					BufferedReader in = new BufferedReader(
							new InputStreamReader(yc.getInputStream()));
					String inputLine;
					String temp = "";
					while ((inputLine = in.readLine()) != null) {
						temp += inputLine + "\n";
					}
					temp = temp.split("s:2:\"")[1].split("\"")[0];

					jsontweet.userurllocation = temp;

				} catch (Exception uhe) {
					//uhe.printStackTrace();
					jsontweet.userurllocation = null;
				}
			}
		}
		
		return jsontweet;

	}

	public void close() throws IOException{
		br.close();
	}
	public static void main(String argv[]) throws IOException{
		JSON2Tweet j2atr = new JSON2Tweet(
				"/Users/indri/Eclipse_workspace/Research/texasfiretweets/tweets.2011-09-06.gz.txt");
		JSONTweet jt;
		while((jt = j2atr.readLine())!= null){
			
			System.out.println(jt.username);
			System.out.println(jt.userlanguage);
			System.out.println(jt.userlocation);
		}
		j2atr.close();
	}
}
