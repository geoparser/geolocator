package edu.cmu.geoparser.resource;

import java.util.HashMap;


public class GazFactory {
	
	private static HashMap<String, GazResources> mapping = new HashMap<String, GazResources>();
	
	private static void build(String gazName){
		
		GazResources gazResource = new GazResources(gazName);
		
		if(gazResource != null)
			mapping.put(gazName, gazResource);
		
	}
	
	public static GazResources getGazResouce(String gazName){
		
		if(!mapping.containsKey(gazName) || mapping.get(gazName) == null)
			build(gazName);
		
		return mapping.get(gazName);
		
	}
	
	
	
}
