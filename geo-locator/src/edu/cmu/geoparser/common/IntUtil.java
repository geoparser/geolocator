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
