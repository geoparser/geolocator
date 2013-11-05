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
