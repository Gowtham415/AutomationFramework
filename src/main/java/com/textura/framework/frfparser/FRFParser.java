package com.textura.framework.frfparser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.Node;
import com.textura.framework.utils.XmlFileBuilder;

public class FRFParser {
	
	String frfPath;
	String actualValue;
	String newLine;
	StringBuilder expectedValue;
	List<String> actualSplit;
	
	private FRFParser(String expectedPath, String actual){
		frfPath = expectedPath;
		actualValue = actual;
		newLine = "\n";
		if(actualValue.contains("\r")){
			newLine = "\r\n";
		}
		expectedValue = new StringBuilder();
		actualSplit = Arrays.asList(actualValue.split(newLine));
	}
	
	public static String getExpectedValue(String frfPath, String actualValue) {
		FRFParser parser = new FRFParser(frfPath, actualValue);
		
		//read frf file
		Document document = XmlFileBuilder.readXmlFile(frfPath);
		List<Node> nodes = document.selectNodes("./result/*"); //get immediate children
		parser.processNodes(nodes);
		return parser.expectedValue.toString().trim();
	}

	private void processNodes(List<Node> nodes){
		for(Node n : nodes){
			if(n.getName().equals("static")){
				
				String text = n.getText();
				if(!text.contains(newLine)){
					text = text.replaceAll("\n", newLine);
				}
				expectedValue.append(text);//append all contents of static text section
				List<Node> children = n.selectNodes("./*");
				processNodes(children);
				
			} // end static processing
			else if(n.getName().equals("flexiblelist")){	
				
				//read all item	s inside the flexiblelist
				List<Node> children = n.selectNodes("./flexibleitem");
				List<String> expectedChildren = sortFlexibleChildren(getTextOfChildren(children));
				children = n.selectNodes("./*");
				for(Node node : children){
					if(node.getName().equals("static")){
						expectedValue.append(node.getText());
					}
					else if(node.getName().equals("flexibleitem")){
						expectedValue.append(expectedChildren.get(0) + newLine);
						expectedChildren.remove(0);
					}
					else if(node.getName().equals("flexiblelist")){
						processNodes(node.selectNodes("./*"));
					}
				}
			} //end flexible list processing
		} // end first level nodes processing
	}
	
	private List<String> sortFlexibleChildren(List<String> children){
		ArrayList<ArrayList<Object>> indexAndText = new ArrayList<ArrayList<Object>>();
		
		//populate actual indeces
		for(String c : children){
			int actualIndex = getActualIndexOfText(c);
			indexAndText.add(new ArrayList<Object>(Arrays.asList(actualIndex, c)));
		}
		
		Collections.sort(indexAndText, new Comparator<ArrayList<Object>>() {
			@Override
			public int compare(ArrayList<Object> arg0, ArrayList<Object> arg1) {
				return ((Integer)arg0.get(0)).compareTo((Integer)arg1.get(0));
			}
		});
		
		List<String> output = new ArrayList<String>();
		
		for(List<Object> l : indexAndText){
			output.add((String) l.get(1));
		}
		return output;
	}
	
	private int getActualIndexOfText(String text){
		
		String firstRow = getFirstLine(text);
		
		for(int index = 0; index < actualSplit.size(); index++){
			String actual = actualSplit.get(index);
			if(wildCardMatch(actual, firstRow)){
				if(match(index, text)){
					return index;
				}
			}
		}
		return -1;	
	}
	
	private boolean match(int index, String text){
		int rows = text.split(newLine).length;
		int end = index + rows;
		String block = buildStringFromActual(index, end);
		if(wildCardMatch(block, text)){
			return true;
		}
		return false;
	}
	
	private String buildStringFromActual(int begin, int end){
		StringBuilder sb = new StringBuilder();
		if(begin >= 0 && begin < end && end <= actualSplit.size()){
			for(int i = begin; i < end; i++){
				sb.append(actualSplit.get(i) + newLine);
			}
		}
		return sb.toString();
	}
	
	@SuppressWarnings("unused")
	private void removeTextFromActual(String text) {
		List<String> split = Arrays.asList(text.split(newLine));
		for (String expected : split) {
			for (String actual : actualSplit) {
				if (wildCardMatch(actual, expected)) {
					actualSplit.remove(actual);
					break;
				}
			}
		}
	}
	
	
	/**
	 * returns the first line of the string
	 * @param string 
	 * @return first line of the string
	 */
	private String getFirstLine(String string){
		String s = "";
		int end = string.indexOf(newLine) - 1;
		if(end > 0){
			s = string.substring(0, end);
		}
		else{
			s = string;
		}
//		if(s.contains("*")){
//			int begin = s.indexOf("*") + 1;
//			int wildCardEnd = s.indexOf("*", begin);
//			if(wildCardEnd < 0){
//				s = s.substring(begin);
//			}
//			else{
//				s = s.substring(begin, wildCardEnd);
//			}
//		}
		return s;
	}
	
	private List<String> getTextOfChildren(List<Node> children){
		List<String> childrenText = new ArrayList<String>();
		for(Node child : children){
			String text = child.getText();
			if(!text.contains(newLine)){
				text = text.replace("\n", newLine);
			}
			childrenText.add(text);
		}
		return childrenText;
	}

	// Matches strings ignoring '*' wildcards
	private static boolean wildCardMatch(String text, String pattern) {
		if (text.length() < 1 && pattern.length() < 1) {
			return true;
		}
		if (text.length() < 1 || pattern.length() < 1) {
			return false;
		}

		// Create the cards by splitting using a RegEx. If more speed
		// is desired, a simpler character based splitting can be done.
		String[] cards = pattern.split("\\*");

		// Iterate over the cards.
		for (String card : cards) {
			int idx = text.indexOf(card);

			// Card not detected in the text.
			if (idx == -1) {
				return false;
			}

			// Move ahead, towards the right of the text.
			text = text.substring(idx + card.length());
		}

		return true;
	}
	
	
}
