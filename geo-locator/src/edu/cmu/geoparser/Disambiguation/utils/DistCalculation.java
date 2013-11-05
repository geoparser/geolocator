/**
 * 
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
 * 
 * @author Wei Zhang,  Language Technology Institute, School of Computer Science, Carnegie-Mellon University.
 * email: wei.zhang@cs.cmu.edu
 * 
 */
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
