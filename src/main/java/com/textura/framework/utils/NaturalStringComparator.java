/**
 * Compares strings with numbers so that they are ordered naturally instead of lexicographically
 * such that the order is 1, 2, 10, instead of 1, 10, 2
 */

package com.textura.framework.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class NaturalStringComparator implements Comparator<String>{

	@Override
	public int compare(String left, String right) {
		if(left.equals(right))
			return 0;
		
		List<String> leftList = dissectString(left);
		List<String> rightList = dissectString(right);
		
		int limit = Math.min(leftList.size(), rightList.size());
		for(int i = 0; i<limit; i++){
			boolean leftSection = isNumber(leftList.get(i));
			boolean rightSection = isNumber(rightList.get(i));
			
			//Case 1: neither is a number section
			if(leftSection || rightSection == false)
				return leftList.get(i).compareTo(rightList.get(i));
			
			//Case 2: both are numbers
			if(leftSection && rightSection){
				int leftNumber = Integer.parseInt(leftList.get(i));
				int rightNumber = Integer.parseInt(rightList.get(i));
				
				if(leftNumber < rightNumber)
					return leftNumber-rightNumber;
				if(rightNumber > leftNumber)
					return rightNumber-leftNumber;
			}
			//Case 3: one is a number, one is not
			else{
				if(leftSection)
					return -1;
				else
					return 1;
			}	
		}
		
		//The only difference is length, shorter string is less than longer string
		if(left.length() < right.length())
			return -1;
		return 1;
}

	/**
	 * 
	 * @param s string to be dissected
	 * @return a list of strings that is split into sections of characters and numbers
	 */
	private List<String> dissectString(String s) {
		List<String> sections = new ArrayList<String>();
		int limit = s.length();
		
		if(limit <= 0)
			return sections;
		
		boolean isDigitSection = Character.isDigit(s.charAt(0));
		String current = "";
		for(int i = 0; i<limit; i++){
			if(Character.isDigit(s.charAt(i)) != isDigitSection){
				isDigitSection = !isDigitSection;
				sections.add(current);
				current = "";
			}				
			current = current.concat(s.substring(i, i+1));
		}
		if(sections.isEmpty()){
			sections.add(current);
		}
		return sections;
	}
	
	private boolean isNumber(String s){
		if(s.length() <= 0)
			return false;
		return Character.isDigit(s.charAt(0));
	}
}
