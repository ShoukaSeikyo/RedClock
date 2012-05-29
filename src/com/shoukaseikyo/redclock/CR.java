package com.shoukaseikyo.redclock;

import java.util.ArrayList;

public class CR {
	
	public static String c(String text) {
		char[] chars = text.toCharArray();
		ArrayList<Integer> ints = new ArrayList<Integer>();
		int j = 1;
		for(char c : chars) {
			ints.add((int)c+j);
		}
		String retour = "";
		for(int i : ints) {
			retour+=(i==92)? "\\" : (char)i;
		}
		
		return retour;
	}

	public static String d(String text) {
		char[] chars = text.toCharArray();
		ArrayList<Integer> ints = new ArrayList<Integer>();
		int j = 1;
		for(char c : chars) {
			ints.add((int)c-j);
		}
		String retour = "";
		for(int i : ints) {
			retour+=(i==92)? "\\" : (char)i;
		}
		
		return retour;
	}
}