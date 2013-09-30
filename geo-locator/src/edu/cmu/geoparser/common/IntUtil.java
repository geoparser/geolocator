package edu.cmu.geoparser.common;


public class IntUtil {
	
	public enum RANGE{TINY,SMALL,MIDDLE,LARGE};//tiny<1000, small<5000, middle<15000,Large>15000
	
	public static RANGE getPRange(int i){
		if(i<1000)
			return RANGE.TINY;
		else if (i<5000)
			return RANGE.SMALL;
		else if (i<15000)
			return RANGE.MIDDLE;
		else
			return RANGE.LARGE;
	}

	public static void main(String argv[]){
		RANGE a = IntUtil.getPRange(1490);
		System.err.print(IntUtil.getPRange(1490));
		
	}
}
