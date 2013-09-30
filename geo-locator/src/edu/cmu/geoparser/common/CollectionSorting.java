package edu.cmu.geoparser.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class CollectionSorting {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static ArrayList<Entry<String, Float>> rankArray(ArrayList<Entry<String, Float>> as) {
		// sort by frequency
		Collections.sort(as, new Comparator() {
			public int compare(Object o1, Object o2) {
				Map.Entry e1 = (Map.Entry) o1;
				Map.Entry e2 = (Map.Entry) o2;
				Float first = (Float) e1.getValue();
				Float second = (Float) e2.getValue();
				return second.compareTo(first);
			}
		});

		return as;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static ArrayList<Entry<String, Integer>> rankIntArray(ArrayList<Entry<String, Integer>> as) {
		// sort by frequency
		Collections.sort(as, new Comparator() {
			public int compare(Object o1, Object o2) {
				Map.Entry e1 = (Map.Entry) o1;
				Map.Entry e2 = (Map.Entry) o2;
				Integer first = (Integer) e1.getValue();
				Integer second = (Integer) e2.getValue();
				return second.compareTo(first);
			}
		});
		return as;
	}
	public static LinkedHashMap sortHashMapByValuesD(HashMap passedMap) {
		   ArrayList mapKeys = new ArrayList(passedMap.keySet());
		   ArrayList mapValues = new ArrayList(passedMap.values());
		   Collections.sort(mapValues);
		   Collections.sort(mapKeys);

		   LinkedHashMap sortedMap = 
		       new LinkedHashMap();

		   Iterator valueIt = mapValues.iterator();
		   while (valueIt.hasNext()) {
		       Object val = valueIt.next();
		    Iterator keyIt = mapKeys.iterator();

		    while (keyIt.hasNext()) {
		        Object key = keyIt.next();
		        String comp1 = passedMap.get(key).toString();
		        String comp2 = val.toString();

		        if (comp1.equals(comp2)){
		            passedMap.remove(key);
		            mapKeys.remove(key);
		            sortedMap.put((String)key, val);
		            break;
		        }

		    }

		}
		return sortedMap;
	}
}
