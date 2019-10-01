package com.textura.framework.tools.fileupdater;import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.textura.framework.utils.ExcelUtils;
import com.textura.framework.utils.JavaHelpers;

/**
 * 
 * This call will handle conversions from xls to csv, csv to json, json to csv
 *
 */
public class FileConverter {
	
	public enum FileType{
		xls("xls"),
		csv("csv"),
		json("json");
		
		String extension;
		private FileType(String extention){
			this.extension = extention;
		}
	}
	
	/**
	 * Converts file in filePath to a TargetType format. Does not support conversion to xls
	 * @param filePath
	 * @param targetType
	 * @param columns Only used if converting json to csv to specify columns to be included and in what order, leave blank otherwise 
	 * @return contents of converted file
	 */
	public static String convertFileTo(String filePath, FileType targetType, String... columns) throws JSONException{
		if(targetType.equals(FileType.xls)){
			throw new UnsupportedOperationException("Unable to convert to xls!");
		}
		String extension = getFileExtension(filePath);
		String contents = null;
		FileType type = null;
		try{
			type = FileType.valueOf(extension);
		}
		catch(IllegalArgumentException e){
			System.out.println("Cant convert file type: " + filePath);
			return JavaHelpers.readFileAsString(filePath);
		}
		
		switch(type){
		case csv:
			switch(targetType){
			case csv:
				contents = JavaHelpers.readFileAsString(filePath);
				break;
			case json:
				String csv = JavaHelpers.readFileAsString(filePath);
				contents = convertCsvStringToJson(csv);
				break;
			default:
				break;
			}
			break;
		case json:
			try{
				switch(targetType){
				case csv:
					contents = convertJsonArrayToCsv(columns, new JSONArray(JavaHelpers.readFileAsString(filePath)));
				case json:
					String csv = convertJsonArrayToCsv(columns, new JSONArray(JavaHelpers.readFileAsString(filePath)));
					contents = convertCsvStringToJson(csv);
				default:
					break;
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
			break;
		case xls:
			String csv = convertXlsToCsv(filePath);
			switch(targetType){
			case csv:
				contents = csv;
				break;
			case json:
				contents = convertCsvStringToJson(csv);
				break;
			default:
				break;
			}
		}
		return contents;
	}
	
	public static String getFileExtension(String filePath){
		int extensionBegin = filePath.lastIndexOf(".") + 1;
		return filePath.substring(extensionBegin);
	}
	
	/**
	 * 
	 * @param filePath path to file to be converted
	 * @return contents of the file in csv format
	 */
	private static String convertXlsToCsv(String filePath){
		return ExcelUtils.readXLSSheetAsString(filePath);
	}
	
	/**
	 * Converts csv file to json
	 * @param filePath
	 * @return contents of new json file
	 */
	@SuppressWarnings("unused")
	private String convertCSVFileToString(String filePath) throws JSONException{
		String contents = JavaHelpers.readFileAsString(filePath);
		return convertCsvStringToJson(contents);
	}
	
	/**
	 * Converts csv string to json.
	 * Assumes first line is the header and will use those columns as keys
	 * @param contents csv string to be converted to json
	 * @return contents of the file in json format
	 */
	private static String convertCsvStringToJson(String contents) throws JSONException{
		String newLine = contents.contains("\r") ? "\r\n" : "\n";
		String delimeter = ",";
		contents = contents.trim();
		int headerEnd = contents.indexOf(newLine);
		String header = contents.substring(0, headerEnd);
		String columns[] = header.split(delimeter);
		
		contents = contents.substring(headerEnd).trim();
		String lines[] = contents.split(newLine, -1);
		
		JSONArray jsonLines = new JSONArray();
		
		for(String line: lines){
			JSONObject jsonLine = new JSONObject();
			String values[] = line.split(delimeter, -1);
			for(int i = 0; i < values.length && i < columns.length; i++){
				String column = columns[i].trim();
				String value = values[i].trim();
				jsonLine.put(column, value);
			}
			jsonLines.put(jsonLine);
		}
		
		String newContents = jsonLines.toString(3);
		return newContents;
	}
	
	
	/**
	 * 
	 * @param columns columns to be put in csv. Order is preserved. if json has keys not present in columns, they will be omited
	 * @param json 
	 * @return
	 */
	public static String convertJsonArrayToCsv(String columns[], JSONArray json) throws JSONException{
		String DELIMETER = ",";
		String SPACING = "";
		String NEW_LINE = System.lineSeparator();
				
		StringBuilder sb = new StringBuilder();
		
		//create header line
		for(int i = 0; i < columns.length; i++){
			sb.append(columns[i]);
			if(i != columns.length - 1){
				sb.append(DELIMETER + SPACING);
			}
		}
		sb.append(NEW_LINE);
				
		for(int i = 0; i < json.length(); i++){
			JSONObject line = json.getJSONObject(i);
			for(int j = 0; j < columns.length; j++){
				String key = columns[j];
				String value = line.has(key) ? line.getString(key) : "";				
				sb.append(value);
				if(j != columns.length - 1){
					sb.append(DELIMETER + SPACING);
				}
			}
			sb.append(NEW_LINE);
		}		
		return sb.toString();
	}

}
