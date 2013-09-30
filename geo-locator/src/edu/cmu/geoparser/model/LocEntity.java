package edu.cmu.geoparser.model;

public class LocEntity {
	public String address;
	public double latitude;
	public double longitude;
	
	public LocEntity(String address, double latitude, double longitude){
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	@Override
	public String toString() {
		return address + ", ["+latitude+","+longitude+"]";
	}
}
