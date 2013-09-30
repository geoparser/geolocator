package edu.cmu.geoparser.model;

import java.util.Date;

import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.Place;

public class JSONTweet  {

	public long id;

	public String source,text;
	
	public Date createdat;

	public GeoLocation tweetgeolocation;

	public HashtagEntity[] hashtagentites;

	public String userdescription;

	public long userid;

	public String userlanguage;

	public String userlocation;

	public String username;

	public String usertimezone;

	public boolean usergeoenabled;

	public String userurl;

	public String userurlsuffix;

	public String userurllocation;

	public Place places;
	
	public String JSON;

}
