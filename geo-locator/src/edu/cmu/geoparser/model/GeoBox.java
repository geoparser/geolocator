package edu.cmu.geoparser.model;

public class GeoBox {


		double
		llo,rlo,bla,ula;
		public GeoBox(double llo,double rlo,double bla,double ula){
			this.llo=llo;
			this.rlo=rlo;
			this.bla=bla;
			this.ula=ula;
		}
		
		public int [] getLOBound(){
			if ((int)llo==(int)rlo)
				return new int[]{(int)llo};
			else
				return new int[]{(int)llo,(int)rlo};
		}
		
		public int [] getLABound(){
			if((int)bla==(int)ula)
				return new int[]{(int)bla};
			else
				return new int[]{(int)bla,(int)ula};
		}

}
