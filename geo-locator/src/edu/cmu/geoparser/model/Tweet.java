package edu.cmu.geoparser.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.stanford.nlp.util.CoreMap;

public class Tweet {
	private Long id;
	private String text;
	private HashMap<String, int[]> locaiton;
	private ArrayList<LocEntity> alternatives;
	private String formatedLocation;
	private Coordinate geoLoc;
	private boolean nativeGPS;
	private String dateString;
	private String userLocation;

	private List<String> matches;

	public List<String> getMatches() {
		return matches;
	}

	public void setMatches(List<String> matches) {
		this.matches = matches;
	}

	public String getUserLocation() {
		return userLocation;
	}

	public void setUserLocation(String userLocation) {
		this.userLocation = userLocation;
	}

	public String getDateString() {
		return dateString;
	}

	public void setDateString(String dateString) {
		this.dateString = dateString;
	}

	public boolean isNativeGPS() {
		return nativeGPS;
	}

	public void setNativeGPS(boolean nativeGPS) {
		this.nativeGPS = nativeGPS;
	}

  public Tweet() {
    this.locaiton = new HashMap<String, int[]>();
    this.alternatives = new ArrayList<LocEntity>();
    this.matches=null;
  }
  public Tweet(String s) {
    this.locaiton = new HashMap<String, int[]>();
    this.alternatives = new ArrayList<LocEntity>();
    this.matches=null;
    this.setText(s);
  }

	public Coordinate getGeoLoc() {
		return geoLoc;
	}

	public void setGeoLoc(Coordinate geoLoc) {
		this.geoLoc = geoLoc;
	}

	public void setFormatedLocation(String formatedLocation) {
		this.formatedLocation = formatedLocation;
	}

	public String getFormatedLocation() {
		return formatedLocation;
	}

	public Long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getOrigText() {
		return text;
	}

	public void setText(String origText) {
		this.text = origText;
	}

	public HashMap<String, int[]> getLocaiton() {
		return locaiton;
	}

	public void setLocaiton(HashMap<String, int[]> locaiton) {
		this.locaiton = locaiton;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return id + ": " + text + "\n" + text;
	}

	public String toResultString() {
		return String.format("%s\t%s\t%s\t%f\t%f\t", id, text,
				formatedLocation, geoLoc.getLatitude(), geoLoc.getLongtitude());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return id.hashCode();
	}

	public ArrayList<LocEntity> getAlternatives() {
		return alternatives;
	}

	public void setAlternative(ArrayList<LocEntity> alternative) {
		this.alternatives = alternative;
	}

	public void addAlternateive(String address, double latitude,
			double longitude) {
		this.alternatives.add(new LocEntity(address, latitude, longitude));
	}

}
