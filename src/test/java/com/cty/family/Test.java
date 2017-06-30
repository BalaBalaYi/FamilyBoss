package com.cty.family;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Test {

	public static void main(String[] args) throws Exception {
		
		ConcurrentHashMap<String, LinkedList<String>> map = new ConcurrentHashMap<String, LinkedList<String>>();
		
		LinkedList<String> temp1 = new LinkedList<String>();
//		temp1.add("1");
		
		LinkedList<String> temp2 = new LinkedList<String>();
		temp2.add("2");
		
		map.put("1", temp1);
		map.put("2", temp2);
		
		LinkedList<String> temp = map.get("2");
		temp.add("3");
		temp.add("4");
		temp.removeFirst();
		
		System.out.println(map.get("1").size());
	}

}
