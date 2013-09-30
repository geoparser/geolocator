package edu.cmu.geoparser.resource.gazindexing;

public class GeoNameGazEntry {

	public int id;
	public String 
	name, 
	asciiname,
	alternatenames, 
	latitude, longtitude,
	featureclass, featurecode,
	country, state1, state2, state3, state4,
	population,
	timezone;
	
	public String toString(){
		return name+" "+country;
	}
}
