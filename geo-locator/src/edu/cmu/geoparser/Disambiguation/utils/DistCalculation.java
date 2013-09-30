package edu.cmu.geoparser.Disambiguation.utils;

import edu.cmu.geoparser.model.GeoBox;

public class DistCalculation {
	static double R= 16.5;
	public static double getDeltaLongtitude(double la){
		double d = Math.cos(Math.toRadians(la));
		return  (R/(double)111)/d;
	}
	
	public static double getDeltaLatitude(){
		return R/111d;
	}
	
	public static GeoBox getBox(double lo, double la){
		double dlo = getDeltaLongtitude(la);
		double dla = getDeltaLatitude();
		
		double leftlo = lo - dlo; double rightlo = lo+dlo;
		double upperla = la +dla; double belowla = la-dla;
		System.out.println(leftlo+" "+rightlo+" "+belowla+ " " + upperla);
		
		return new GeoBox(leftlo,rightlo,belowla,upperla);
	}
	public static void main(String argv[]){
		DistCalculation.getBox(25.61955, 56.27291);
	}
}
