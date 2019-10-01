package com.textura.framework.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class XmlFileBuilder {

	public static Document readXmlFile(String xmlFile) {
		
		Document document = null;
		SAXReader reader = new SAXReader();
		
		try {
			File file = new File(xmlFile);
			document = reader.read(file);
			return document;
		}
		catch (Exception e) {
		     e.printStackTrace();
		}
		return null;
	}
	
	public static void writeXmlFile(String path, Document document) {
		
		try {	
	        OutputFormat format = OutputFormat.createPrettyPrint();
	        XMLWriter writer = new XMLWriter(new FileWriter(path), format);
	        writer.write(document);
	        writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
	public static void setElement(Document document, String element, String value) {
		
		List<?> list = document.selectNodes("//" + element);
		if(value != null)
			((Element) list.get(0)).setText(value);
     }
	
	public static void setElementOrCreate(Document document, String parent, String element, String value) {
		
		List<?> list = document.selectNodes("//" + element);
		if(value != null && list.size() > 0) {
			((Element) list.get(0)).setText(value);
		}
		else if(value != null) {
			Node n = document.selectSingleNode("//" + parent);
			((Element) n).addElement(element).setText(value);
		}
     }
     
	public static void printPrettyXmlFile(Document document) {
		
		try {
	        OutputFormat format = OutputFormat.createPrettyPrint();
	        XMLWriter writer = new XMLWriter(System.out, format);
			writer.write(document);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String toString(Document document) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter writer = new XMLWriter(b, format);
			writer.write(document);
			return b.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return "null";
		}
		catch(Exception e){
			return "null";
		}
	}
	
	public static Document readXmlFromString(String xml) {
		Document document = null;
		SAXReader reader = new SAXReader();
		try {
			StringReader sr = new StringReader(xml);
			document = reader.read(sr);
			return document;
		}
		catch (Exception e) {
		     e.printStackTrace();
		}
		return null;
	}
	
	public static String getPrettyPrintXml(String xml){
		return toString(readXmlFromString(xml));
	}
	
}
