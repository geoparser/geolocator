package edu.cmu.geoparser.resource.gazindexing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import edu.cmu.geoparser.io.GetReader;

public class GeoNamesReader {

	BufferedReader reader;
	int population;
	String[] col;
	String line;
	GeoNameGazEntry e = new GeoNameGazEntry();
	
	public GeoNamesReader(String filename) throws FileNotFoundException,
			UnsupportedEncodingException {
		reader = GetReader.getUTF8FileReader(filename);
	}

	public GeoNameGazEntry readLine() throws IOException {

		line = reader.readLine();
		try{
		col = line.split("\t");
		}
		catch(NullPointerException npe){
			return null;
		}
		try{
		e.id = Integer.parseInt(col[0].trim());
		}catch(NumberFormatException nfe){
			e.id=-1;
		}
		e.name = col[1];
		e.asciiname = col[2];
		e.alternatenames = col[3];
		e.latitude = col[4];
		e.longtitude = col[5];
		e.featureclass = col[6];
		e.featurecode = col[7];
		e.country = col[8];
		e.state1 = col[10];
		e.state2 = col[11];
		e.state3 = col[12];
		e.state4 = col[13];
		e.timezone = col[17];
		
		e.population = (col[14]);
		return e;

	}

	public void close() throws IOException {
		reader.close();
	}

	public static void main(String args[]) {

	}

}
