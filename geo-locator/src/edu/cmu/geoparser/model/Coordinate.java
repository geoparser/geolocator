package edu.cmu.geoparser.model;

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
/**
 * 
 * @author shuguang
 * A data structure for Geo-Pair, with Longitude and Latitude, and their set and get methods. 
 */
public class Coordinate {
	
	private double latitude;
	private double longtitude;
	
	/**
	 * Construction methods
	 * @param latitude
	 * @param longtitude
	 */
	public Coordinate(double latitude, double longtitude){
		this.latitude = latitude;
		this.longtitude = longtitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongtitude() {
		return longtitude;
	}

	public void setLongtitude(double longtitude) {
		this.longtitude = longtitude;
	}
	
	

}
